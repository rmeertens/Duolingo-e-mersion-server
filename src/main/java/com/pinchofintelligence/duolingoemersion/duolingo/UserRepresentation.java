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
    public String username;
    public ArrayList<LanguageWithWords> knownLanguagesWithWords ;
    public String languageLearning;
    public UserRepresentation()
    {
        knownLanguagesWithWords = new ArrayList<LanguageWithWords>();
    }
    
}
