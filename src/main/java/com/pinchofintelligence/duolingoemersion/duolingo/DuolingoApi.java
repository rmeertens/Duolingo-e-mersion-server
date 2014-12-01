/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.duolingo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class DuolingoApi {

    public DuolingoApi() {

    }

    /**
     * Returns the words a user knows in a certain language
     *
     * @param username Username on Duolingo
     * @param language The language we want to know
     * @return An arraylist with the words this person knows
     * @throws IOException
     * @throws JSONException
     */
    public UserRepresentation getWordsForUser(UserRepresentation user) throws IOException, JSONException {
        // Example URL:  http://www.duolingo.com//users/rmeertens
        JSONObject json = readJsonFromUrl("http://www.duolingo.com//users/" + user.username);

        /* When wanting to extract more information:
         System.out.println(json.toString());
         System.out.println(json.get("num_following"));
         System.out.println(json.getJSONArray("languages"));
         */
        // Get all languages the user knows
        JSONArray languages = json.getJSONArray("languages");

        for (int i = 0; i < languages.length(); i++) {
            if (languages.getJSONObject(i).getBoolean("learning") && languages.getJSONObject(i).getBoolean("current_learning")) {
                String nameLanguage = languages.getJSONObject(i).get("language").toString();
                System.out.println("This user is learning " + nameLanguage);
                user.languageLearning = nameLanguage;
                // If so: find the known words
                ArrayList<String> knownWords = getWordsKnownForLanguage(nameLanguage, json);
                LanguageWithWords wordThisLanguage = new LanguageWithWords();
                wordThisLanguage.language = nameLanguage;
                wordThisLanguage.wordsForLanguage = knownWords;
                System.out.println("Found the wanted words");
                user.knownLanguagesWithWords.add(wordThisLanguage);
                System.out.println("Added  the wanted words");
            }
        }
        return user;
    }

    /**
     * Given a JSON object specifying what languages the user knows, and the
     * language we want to learn, return a list with words.
     *
     * @param nameLanguage The name of the language the user requested
     * @param json The json object
     * @return A list with words the user knows
     * @throws JSONException
     */
    private ArrayList<String> getWordsKnownForLanguage(String nameLanguage, JSONObject json) throws JSONException {
        ArrayList<String> knownWords = new ArrayList<String>();

        JSONObject allLanguages = json.getJSONObject("language_data");
        JSONObject chosenLanguage = allLanguages.getJSONObject(nameLanguage);
        JSONArray skillsChosenLanguage = chosenLanguage.getJSONArray("skills");

        // Add all learned skills
        for (int index = 0; index < skillsChosenLanguage.length(); index++) {
            if (skillsChosenLanguage.getJSONObject(index).getBoolean("learned")) {
                JSONArray wordsInSkill = skillsChosenLanguage.getJSONObject(index).getJSONArray("words");
                for (int wordIndex = 0; wordIndex < wordsInSkill.length(); wordIndex++) {
                    knownWords.add(wordsInSkill.getString(wordIndex));
                }
            }
        }
        return knownWords;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
