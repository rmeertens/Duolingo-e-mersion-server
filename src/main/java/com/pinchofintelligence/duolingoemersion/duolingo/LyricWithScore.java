/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.duolingo;

import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;

/**
 *
 * @author Roland
 */
public class LyricWithScore implements Comparable<LyricWithScore> {

        public double score;
        public TrackInformation trackInformation;

        public LyricWithScore(double score, TrackInformation name) {
            this.score = score;
            this.trackInformation = name;
        }

        @Override
        public int compareTo(LyricWithScore o) {
            return score < o.score ? -1 : score > o.score ? 1 : 0;
        }
    }
