/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.duolingo;

import java.util.ArrayList;

/**
 *
 * @author Roland
 */
public class UserRepresentation {
    private String username;
    public ArrayList<LanguageWithWords> knownLanguagesWithWords ;
    private String languageLearning;
    public UserRepresentation()
    {
        knownLanguagesWithWords = new ArrayList<LanguageWithWords>();
    }

    public UserRepresentation(String username) {
        this.username = username;
    }
    public String getUsername()
    {
        return username;
    }
    public String getLanguageLearning()
    {
        return languageLearning;
    }

    void setLanguageLearning(String nameLanguage) {
        this.languageLearning = nameLanguage;
    }
    
}
