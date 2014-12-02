/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.DuolingoApi;
import com.pinchofintelligence.duolingoemersion.duolingo.LanguageWithWords;
import com.pinchofintelligence.duolingoemersion.duolingo.UserRepresentation;
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

        DuolingoEmersionServer parent;
        JTextArea textArea;
        HashMap<String, TrackInformation> tracksDatabase;
        DuolingoApi duolingoApi;

        public SongsBasedOnUsernameHandler(DuolingoEmersionServer parent, JTextArea are, HashMap<String, TrackInformation> tracksDatabase) {
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
                
                UserRepresentation ourUser = new UserRepresentation();
                ourUser.username = username;

                try {
                    ourUser = duolingoApi.getWordsForUser(ourUser);
                } catch (JSONException ex) {
                    Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.textArea.append(username + " asking for songs in the language " + ourUser.languageLearning + "\n");
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
            
            byte[] bytes = response.getBytes(Charset.forName("UTF-8"));
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            //os.write(response.getBytes());
            os.write(bytes);

            os.close();
        }

        public JSONArray best10Matches(ArrayList<String> knownWords, String language) {
            language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);
            Iterator it = this.tracksDatabase.entrySet().iterator();
            List<DuolingoEmersionServer.LyricWithScore> scores = new ArrayList<DuolingoEmersionServer.LyricWithScore>();
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
                        scores.add(new DuolingoEmersionServer.LyricWithScore(score / wordsTested, lyric));
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
