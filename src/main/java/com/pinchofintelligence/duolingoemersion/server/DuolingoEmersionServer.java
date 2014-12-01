/**
 * Rolands stuff!
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.main.MainClass;
import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.DuolingoApi;
import com.pinchofintelligence.duolingoemersion.duolingo.LanguageWithWords;
import com.pinchofintelligence.duolingoemersion.duolingo.UserRepresentation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class DuolingoEmersionServer {

    JTextArea textArea;
    private final MyHandler theHandler;

    public DuolingoEmersionServer() throws Exception {
        HashMap<String, TrackInformation> tracksDatabase = loadDatabase();

        JFrame frame = new JFrame("Duolingo recommender panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(15, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        frame.getContentPane().add(scrollPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        System.out.println("Starting HTTP Duolingo Emersion server");
        HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
        this.theHandler = new MyHandler(this, textArea, tracksDatabase);
        server.createContext("/duolingorecommendation", theHandler);
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    static class MyHandler implements HttpHandler {

        DuolingoEmersionServer parent;
        JTextArea textArea;
        HashMap<String, TrackInformation> tracksDatabase;
        DuolingoApi duolingoApi;

        public MyHandler(DuolingoEmersionServer parent, JTextArea are, HashMap<String, TrackInformation> tracksDatabase) {
            this.parent = parent;
            this.textArea = are;
            this.tracksDatabase = tracksDatabase;
            duolingoApi = new DuolingoApi();

        }

        @Override
        public void handle(HttpExchange t) throws IOException {

            Map<String, String> parms = DuolingoEmersionServer.queryToMap(t
                    .getRequestURI().getQuery());

            String response = "";

            if (parms.containsKey("username")) {
                String username = parms.get("username");
                this.textArea.append(username + " asking for his stuff + \n");
                UserRepresentation ourUser = new UserRepresentation();

                try {
                    ourUser = duolingoApi.getWordsForUser("rmeertens");
                } catch (JSONException ex) {
                    Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("now we know our user");
                LanguageWithWords lang = ourUser.knownLanguagesWithWords.get(0);
                System.out.println("now we have his words");
                JSONArray languageResponse = best10Matches(lang.wordsForLanguage, ourUser.languageLearning);
                System.out.println("now we know the response");
                /*
                try {
                    toReturn.put(lang.language, languageResponse);
                } catch (JSONException ex) {
                    Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                   */
                response = languageResponse.toString();
                System.out.println(response);

            } else {
                response = "Use /duolingorecommendation?username=yourname&foo=unused to see how to handle url parameters";
                //192.168.2.17:8001/marioserver?option=pressButtons&name=Roland&command=left
                System.out.println("Something else");
                this.textArea.append("Received something strange\n");

            }

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
            t.getResponseHeaders().add("Access-Control-Max-Age", "1728000");

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        public JSONArray best10Matches(ArrayList<String> knownWords, String language) {
            language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);
            Iterator it = this.tracksDatabase.entrySet().iterator();
            List<LyricWithScore> scores = new ArrayList<LyricWithScore>();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                TrackInformation lyric = (TrackInformation) pairs.getValue();
                if(lyric.lyricsBody != null){
                    if(lyric.language.equals(language)){
                        System.out.println(lyric.language);
                        double score = 0.0;
                        double wordsTested = 0.0;
                        String[] wordsInLyric = lyric.lyricsBody.split(" ");

                        for (String word : wordsInLyric) {

                            wordsTested++;
                            //   System.out.println("Testing if " + word + " is known");
                            if (knownWords.contains(word.toLowerCase())) {
                                score++;
                            }
                        }
                        // System.out.println("The score is: " + score + "and tested : " + wordsTested);
                        System.out.println("This lyric is known " + score / wordsTested);
                        scores.add(new LyricWithScore(score / wordsTested, lyric));
                        System.out.println("Done lyric known");   
                    }
                }
            }
            System.out.println("Starting to sort");
            Collections.sort(scores);
            System.out.println("now we have an ordered list");

            try {

                JSONArray best10Matches = new JSONArray();

                for (int x = scores.size() - 1; x > scores.size() - 10 && x > 0; x--) {
                    JSONObject jsonResponse = new JSONObject();

                    jsonResponse.put("Artist", scores.get(x).trackInformation.nameArtist);
                    jsonResponse.put("NameSong", scores.get(x).trackInformation.nameSong);
                    jsonResponse.put("Score", scores.get(x).score);

                    best10Matches.put(jsonResponse);
                }
                         //   jsonResponse.put("Best", scores.get(0).trackInformation.nameSong + " by " + scores.get(0).trackInformation.nameArtist + " because " + scores.get(0).score);
                // JSONObject jsonResponse2 = new JSONObject();
                //best10Matches.to
                return best10Matches;
            } catch (JSONException ex) {
                Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    /**
     * returns the url parameters in a map
     *
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private HashMap<String, TrackInformation> loadDatabase() {
        FileInputStream f = null;
        try {
            String rootFolder = "database";
            File file = new File(rootFolder+"/ourMusicDatabase.txt");
            f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            HashMap<String, TrackInformation> fileObj2 = (HashMap<String, TrackInformation>) s.readObject();
            s.close();
            f.close();
            return fileObj2;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);

        }
        System.err.println("Generating new database");
        return new HashMap<String, TrackInformation>();
    }

    static class LyricWithScore implements Comparable<LyricWithScore> {

        double score;
        TrackInformation trackInformation;

        public LyricWithScore(double score, TrackInformation name) {
            this.score = score;
            this.trackInformation = name;
        }

        @Override
        public int compareTo(LyricWithScore o) {
            return score < o.score ? -1 : score > o.score ? 1 : 0;
        }
    }
}
