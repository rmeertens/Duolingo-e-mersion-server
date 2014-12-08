/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server.handlers;

import com.pinchofintelligence.duolingoemersion.server.tools.ServerLog;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.DuolingoApi;
import com.pinchofintelligence.duolingoemersion.duolingo.LanguageWithWords;
import com.pinchofintelligence.duolingoemersion.duolingo.LyricWithScore;
import com.pinchofintelligence.duolingoemersion.duolingo.UserRepresentation;
import com.pinchofintelligence.duolingoemersion.server.DuolingoEmersionServer;
import com.pinchofintelligence.duolingoemersion.server.DuolingoLanguageNameAdapter;
import com.pinchofintelligence.duolingoemersion.server.JSONArrayGenerator;
import com.pinchofintelligence.duolingoemersion.server.tools.ServerTools;
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
public class RandomSongsBasedOnCountryHandler implements HttpHandler {

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
        
        this.serverLog.addEvent(this,exchangeObject, parms);
        String response = "";

        if (parms.containsKey("language")) {
            try {
                response = getSongsInLanguage(parms.get("language"));
            } catch (JSONException ex) {
                Logger.getLogger(RandomSongsBasedOnCountryHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            response = "Use /duolingorecommendation?username=yourname&foo=unused to see how to handle url parameters";
            System.out.println("Something else");
            serverLog.addString("Received something strange");

        }

        
        ServerTools.writeResponse(response, exchangeObject);
    }

    public JSONArray randomSongs(String language) {
        language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);

        List<TrackInformation> tracksWithCorrectLanguage = getTracksWithCorrectLanguage(this.tracksDatabase, language);
        Collections.shuffle(tracksWithCorrectLanguage);
        List<TrackInformation> chosenSongs = new ArrayList<>(tracksWithCorrectLanguage.subList(0, Math.min(DuolingoEmersionServer.AMOUNT_OF_SONGS_RETURNING, tracksWithCorrectLanguage.size())));
        return JSONArrayGenerator.getJSONArrayWithTrackInformations(chosenSongs);
    }

    private String getSongsInLanguage(String language) throws IOException, JSONException {
        JSONArray languageResponse = randomSongs(language);
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("array", languageResponse);
        responseJSON.put("userLearningLanguage", language);
        return responseJSON.toString();
        

    }

    private List<TrackInformation> getTracksWithCorrectLanguage(HashMap<String, TrackInformation> tracksDatabase, String language) {
        Iterator it = tracksDatabase.entrySet().iterator();
        List<TrackInformation> tracksWithCorrectLanguage = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            TrackInformation lyric = (TrackInformation) pairs.getValue();
            if (lyric.getLyrics_body() != null) {
                if (lyric.getLyrics_language().equals(language)) {
                    tracksWithCorrectLanguage.add(lyric);
                }
            }
        }
        return tracksWithCorrectLanguage;
    }

}
