/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.main;

import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.server.DuolingoEmersionServer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.jmusixmatch.MusixMatchException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class MainClass {

    public static void main(String[] args) throws MusixMatchException, IOException, JSONException, Exception {
        LyricsDownloader lyricsDownloader = new LyricsDownloader();
        ArrayList<TrackInformation> popularTracks = getPopularTracks();
        ArrayList<TrackInformation> lyrics = lyricsDownloader.getLyrics(popularTracks);

        DuolingoEmersionServer server = new DuolingoEmersionServer();
    }

    static public ArrayList<TrackInformation> getPopularTracks() throws IOException {
        ArrayList<TrackInformation> popularTracks = new ArrayList<TrackInformation>();
        String rootFolder = "database/";
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "top2000stemlijst_2012.csv"));
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "spanish-music-database-2-version-13.csv"));
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "aliensamaditop300artistschart.csv"));
        System.out.println("now knows " + popularTracks.size());

        System.out.println("now knows " + popularTracks.size());

        return popularTracks;
    }

    private static ArrayList<TrackInformation> getTrackInformationFromFile(String csvFile) throws IOException {
        ArrayList<TrackInformation> popularTracks = new ArrayList<TrackInformation>();
        File csvData = new File(csvFile);
        CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);

        for (CSVRecord csvRecord : parser) {
            popularTracks.add(new TrackInformation(csvRecord.get(0), csvRecord.get(1)));
        }
        return popularTracks;
    }

}
