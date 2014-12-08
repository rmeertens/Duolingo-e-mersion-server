/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.LyricWithScore;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class JSONArrayGenerator {

    private static JSONObject getTrackInformation(TrackInformation track) throws JSONException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Artist", track.getArtistNameMusixMatch());
        jsonResponse.put("NameSong", track.getTrackNameMusixMatch());
        jsonResponse.put("TrackShareURL", track.getTrack_share_url());
        jsonResponse.put("SpotifyID", track.getTrack_spotify_id());
        return jsonResponse;
    }

    public static JSONArray getJSONArrayWithScore(List<LyricWithScore> scores) {
        try {
            JSONArray matches = new JSONArray();
            for (int x = scores.size() - 1; x > 0; x--) {
                JSONObject jsonResponse = getTrackInformation(scores.get(x).trackInformation);
                jsonResponse.put("Score", scores.get(x).score);
                matches.put(jsonResponse);
            }
            return matches;
        } catch (JSONException ex) {
            Logger.getLogger(DuolingoEmersionServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static JSONArray getJSONArrayWithTrackInformations(List<TrackInformation> tracksWithCorrectLanguage) {
        JSONArray matches = new JSONArray();
        for (int x = tracksWithCorrectLanguage.size() - 1; x > 0; x--) {
            JSONObject jsonResponse;
            try {
                jsonResponse = getTrackInformation(tracksWithCorrectLanguage.get(x));
                matches.put(jsonResponse);
            } catch (JSONException ex) {
                Logger.getLogger(JSONArrayGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return matches;
        
    }
}
