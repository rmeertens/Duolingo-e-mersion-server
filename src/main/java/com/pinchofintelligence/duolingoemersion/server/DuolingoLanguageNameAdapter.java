/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server;

/**
 *
 * @author Roland
 */
public class DuolingoLanguageNameAdapter {
    public static String getMusixMatchLanguageFromDuoLingo(String language)
    {
        // English
        //  Duolingo: en
        //  Our site: en
        //  Musixmatch: en
        
        // Dutch
        //  Duolingo: dn
        //  Our site: nl
        //  Musixmatch: nl
        
        // German
        //  Duolingo: de
        //  Our site: de
        //  Musixmatch: de
        
        // Italian
        //  Duolingo: it
        //  Our site: it
        //  Musixmatch: it
        
        // Spanish
        //  Duolingo: de
        //  Our site: de
        //  Musixmatch: de
        
        if("dn".equals(language))
        {
            return "nl";
        }
        return language;
    }
}
