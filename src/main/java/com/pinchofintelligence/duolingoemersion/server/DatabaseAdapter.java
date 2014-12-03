/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Roland
 */
public class DatabaseAdapter {
    public static List<TrackInformation> getTracksWithCorrectLanguage(HashMap<String, TrackInformation> tracksDatabase, String language) {
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
