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
        if("dn".equals(language))
        {
            return "nl";
        }
        return language;
    }
}
