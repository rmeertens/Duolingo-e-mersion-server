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
import java.util.Random;
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
 class RandomSongsBasedOnCountryHandler implements HttpHandler {
        ServerLog serverLog;
        HashMap<String, TrackInformation> tracksDatabase;
        DuolingoApi duolingoApi;

        public RandomSongsBasedOnCountryHandler(ServerLog serverLog, HashMap<String, TrackInformation> tracksDatabase) {
            this.serverLog = serverLog;
            this.tracksDatabase = tracksDatabase;
            this.duolingoApi = new DuolingoApi();
        }

        @Override
        public void handle(HttpExchange exchangeObject) throws IOException {
            // Get the parameters from the query
            Map<String, String> parms = queryToMap(exchangeObject);
            String response = "";
            
                
               
            if (parms.containsKey("language")) {
                response = getSongsInLanguage(parms.get("language"));
                

            } else {
                response = "Use /duolingorecommendation?username=yourname&foo=unused to see how to handle url parameters";
                //192.168.2.17:8001/marioserver?option=pressButtons&name=Roland&command=left
                System.out.println("Something else");
                this.serverLog.append("Received something strange\n");

            }

            exchangeObject = addResponseHeaders(exchangeObject);
           
            
            byte[] bytes = response.getBytes(Charset.forName("UTF-8"));
            exchangeObject.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchangeObject.getResponseBody();
            //os.write(response.getBytes());
            os.write(bytes);

            os.close();
        }

        public JSONArray randomSongs(String language) {
            language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);
            
            
            List<TrackInformation> tracksWithCorrectLanguage = getTracksWithCorrectLanguage(this.tracksDatabase, language);
            Collections.shuffle(tracksWithCorrectLanguage);
            
            return getFirstTracksFromCollection(tracksWithCorrectLanguage);
        }

    private String getSongsInLanguage(String language) throws IOException {
                

        this.serverLog.append("random asking for random songs in the language " + language + "\n");
        JSONArray languageResponse = randomSongs(language);
        return languageResponse.toString();
        
    }

    private JSONArray getFirstTracksFromCollection(List<TrackInformation> tracksWithCorrectLanguage) {
        try {
                JSONArray randomSongsReturning = new JSONArray();
                for (int x = tracksWithCorrectLanguage.size() - 1; x > tracksWithCorrectLanguage.size() - 10 && x > 0; x--) {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("Artist", tracksWithCorrectLanguage.get(x).nameArtist);
                    jsonResponse.put("NameSong", tracksWithCorrectLanguage.get(x).nameSong);
                    randomSongsReturning.put(jsonResponse);
                }
                return randomSongsReturning;
            } catch (JSONException ex) {
                Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
    }

    private List<TrackInformation> getTracksWithCorrectLanguage(HashMap<String, TrackInformation> tracksDatabase, String language) {
        Iterator it = tracksDatabase.entrySet().iterator();
        List<TrackInformation> tracksWithCorrectLanguage = new ArrayList<>();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                TrackInformation lyric = (TrackInformation) pairs.getValue();
                if(lyric.lyricsBody != null){
                    if(lyric.language.equals(language)){
                        tracksWithCorrectLanguage.add(lyric);
                    }
                }
            }          
            return tracksWithCorrectLanguage;
    }

    
    }
