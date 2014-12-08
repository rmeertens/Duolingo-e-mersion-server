/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import java.util.ArrayList;

/**
 *
 * @author Roland
 */
public class ScoreMetricSong {
    /**
     * Get the score of a song, currently only bases on what words a user knows
     * and the lyrics of a song
     *
     * @param lyric
     * @param knownWords
     * @return
     */
    public static int getScore(TrackInformation lyric, ArrayList<String> knownWords) {

        double score = 0.0;
        double wordsTested = 0.0;
        String[] wordsInLyric = lyric.getLyrics_body().split(" ");

        for (String word : wordsInLyric) {
            wordsTested++;
            if (knownWords.contains(word.toLowerCase())) {
                score++;
            }
        }

        return (int) ((score / wordsTested) * 100.0);
    }
}
