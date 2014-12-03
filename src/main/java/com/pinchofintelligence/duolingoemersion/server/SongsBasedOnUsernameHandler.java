/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.server.tools.ServerLog;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.DuolingoApi;
import com.pinchofintelligence.duolingoemersion.duolingo.LanguageWithWords;
import com.pinchofintelligence.duolingoemersion.duolingo.LyricWithScore;
import com.pinchofintelligence.duolingoemersion.duolingo.UserRepresentation;
import static com.pinchofintelligence.duolingoemersion.server.tools.ServerTools.addResponseHeaders;
import static com.pinchofintelligence.duolingoemersion.server.tools.ServerTools.queryToMap;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
 class SongsBasedOnUsernameHandler implements HttpHandler {
        
        
        ServerLog serverLog;
        HashMap<String, TrackInformation> tracksDatabase;
        DuolingoApi duolingoApi;

        public SongsBasedOnUsernameHandler(ServerLog serverLog, HashMap<String, TrackInformation> tracksDatabase) {
            this.serverLog = serverLog;
            this.tracksDatabase = tracksDatabase;
            duolingoApi = new DuolingoApi();

        }

        @Override
        public void handle(HttpExchange exchangeObject) throws IOException {
            Map<String, String> parms = queryToMap(exchangeObject);
            String response;
            if (parms.containsKey("username")) {
                UserRepresentation ourUser = new UserRepresentation(parms.get("username"));
                System.out.println("Contains the username");
                try {
                    ourUser = duolingoApi.getWordsForUser(ourUser);
                    System.out.println("Done constructing our user");
                } catch (JSONException ex) {
                    Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Now goibng to test stuff");
                //this.serverLog.append(ourUser.getUsername() + " asking for songs in the language " + ourUser.getLanguageLearning() + "\n");
                
                LanguageWithWords lang = ourUser.knownLanguagesWithWords.get(0);
                JSONArray languageResponse = getBestMatches(lang.wordsForLanguage, ourUser.getLanguageLearning(),DuolingoEmersionServer.AMOUNT_OF_SONGS_RETURNING);
                response = languageResponse.toString();
                System.out.println(response);

            } else {
                response = "Use /randomSong?username=yourname&foo=unused to see how to handle url parameters";
                //192.168.2.17:8001/marioserver?option=pressButtons&name=Roland&command=left
                System.out.println("Something else");
                this.serverLog.append("Received something strange\n");

            }

            exchangeObject = addResponseHeaders(exchangeObject);
            
            byte[] bytes = response.getBytes(Charset.forName("UTF-8"));
            exchangeObject.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchangeObject.getResponseBody();
            os.write(bytes);
            os.close();
        }

        public JSONArray getBestMatches(ArrayList<String> knownWords, String language, int amount) {
            language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);
            Iterator it = this.tracksDatabase.entrySet().iterator();
            List<LyricWithScore> scores = new ArrayList<>();
            System.out.println("Getting the best matches");
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
                            if (knownWords.contains(word.toLowerCase())) {
                                score++;
                            }
                        }
                        int percentage = (int) ((score/wordsTested)*100.0);
                        scores.add(new LyricWithScore(percentage, lyric));
                    }
                }
            }
            Collections.sort(scores);
            System.out.println("Now has sorted scores");
            try {

                JSONArray best10Matches = new JSONArray();

                for (int x = scores.size() - 1; x > scores.size() - amount && x > 0; x--) {
                    JSONObject jsonResponse = new JSONObject();

                    jsonResponse.put("Artist", scores.get(x).trackInformation.nameArtist);
                    jsonResponse.put("NameSong", scores.get(x).trackInformation.nameSong);
                    jsonResponse.put("Score", scores.get(x).score);

                    best10Matches.put(jsonResponse);
                }
                return best10Matches;
            } catch (JSONException ex) {
                Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
