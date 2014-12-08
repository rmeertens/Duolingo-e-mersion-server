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
import com.pinchofintelligence.duolingoemersion.server.ScoreMetricSong;
import static com.pinchofintelligence.duolingoemersion.server.DatabaseAdapter.getTracksWithCorrectLanguage;
import com.pinchofintelligence.duolingoemersion.server.tools.ServerTools;
import static com.pinchofintelligence.duolingoemersion.server.tools.ServerTools.queryToMap;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class SongsBasedOnUsernameHandler implements HttpHandler {
    private static final Logger log = Logger.getLogger( SongsBasedOnUsernameHandler.class.getName() );
    
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
            try {
                UserRepresentation ourUser = new UserRepresentation(parms.get("username"));
                System.out.println("Contains the username");
                try {
                    ourUser = duolingoApi.getWordsForUser(ourUser);
                    System.out.println("Done constructing our user");
                } catch (JSONException ex) {
                    try {
                        Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
                        JSONObject responseJSON = new JSONObject();
                        responseJSON.put("message", "NOTFOUNDUSER");
                        ServerTools.writeResponse(responseJSON.toString(),exchangeObject);
                        return;
                    } catch (JSONException ex1) {
                        Logger.getLogger(SongsBasedOnUsernameHandler.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                
                boolean getRandom = parms.containsKey("random") && "true".equals(parms.get("random"));
                
                this.serverLog.addString(ourUser.getUsername() + " asking for songs in the language " + ourUser.getLanguageLearning() + " random: " + getRandom + " ip source: " + exchangeObject.getRemoteAddress().toString());
                LanguageWithWords lang = ourUser.knownLanguagesWithWords.get(0);
                JSONArray JSONBestMatches;
                
                if (!getRandom) {
                    List<LyricWithScore> languageResponse = getBestMatches(lang.wordsForLanguage, ourUser.getLanguageLearning(), DuolingoEmersionServer.AMOUNT_OF_SONGS_RETURNING);
                    JSONBestMatches = JSONArrayGenerator.getJSONArrayWithScore(languageResponse);
                } else {
                    List<LyricWithScore> languageResponse = getRandomMatches(lang.wordsForLanguage, ourUser.getLanguageLearning(), DuolingoEmersionServer.AMOUNT_OF_SONGS_RETURNING);
                    JSONBestMatches = JSONArrayGenerator.getJSONArrayWithScore(languageResponse);
                }
                JSONObject responseJSON = new JSONObject();
                responseJSON.put("array", JSONBestMatches);
                responseJSON.put("userLearningLanguage", ourUser.getLanguageLearning());
                response = responseJSON.toString();
                System.out.println(response);
                
            } catch (JSONException ex) {
                Logger.getLogger(SongsBasedOnUsernameHandler.class.getName()).log(Level.SEVERE, null, ex);
                response = "somethgsigsgsg";
            }

        } else {
            response = "Use /randomSong?username=yourname&foo=unused to see how to handle url parameters";
            //192.168.2.17:8001/marioserver?option=pressButtons&name=Roland&command=left
            System.out.println("Something else");
            serverLog.addString("Recieved something strange");

        }

        
        ServerTools.writeResponse(response,exchangeObject);
    }
    
    

    /**
     * Returns the songs with the highest amount of words known. 
     * @param knownWords
     * @param language
     * @param amount
     * @return 
     */
    public List<LyricWithScore> getBestMatches(ArrayList<String> knownWords, String language, int amount) {
        language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);

        List<TrackInformation> tracksWithCorrectLanguage = getTracksWithCorrectLanguage(this.tracksDatabase, language);
        List<LyricWithScore> scores = new ArrayList<>();

        for (TrackInformation track : tracksWithCorrectLanguage) {
            int score = ScoreMetricSong.getScore(track, knownWords);
            scores.add(new LyricWithScore(score, track));
        }

        Collections.sort(scores);

        List<LyricWithScore> bestMatches = new ArrayList<>(scores.subList(Math.max(0, scores.size() - amount), scores.size()));
        return bestMatches;
    }

    /**
     * Returns random songs including a score indicating how well a user is able to listen to this song. 
     * @param knownWords
     * @param language
     * @param amount
     * @return 
     */
    public List<LyricWithScore> getRandomMatches(ArrayList<String> knownWords, String language, int amount) {
        language = DuolingoLanguageNameAdapter.getMusixMatchLanguageFromDuoLingo(language);

        // Get a selection of random songs with a specific language
        List<TrackInformation> tracksWithCorrectLanguage = getTracksWithCorrectLanguage(this.tracksDatabase, language);
        Collections.shuffle(tracksWithCorrectLanguage);
        List<TrackInformation> chosenSongs = new ArrayList<>(tracksWithCorrectLanguage.subList(0,Math.min(amount, tracksWithCorrectLanguage.size())));
        
        // Give each lyric a score
        List<LyricWithScore> scores = new ArrayList<>();
        for (TrackInformation track : chosenSongs) {
            int score = ScoreMetricSong.getScore(track, knownWords);
            scores.add(new LyricWithScore(score, track));
        }

        return scores;
    }

    
}
