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
import java.io.FileReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Roland
 */
public class MainClass {

    public static void main(String[] args) throws IOException, Exception {
        
        LyricsDownloader lyricsDownloader = new LyricsDownloader();
        ArrayList<TrackInformation> popularTracks = getPopularTracks();
        ArrayList<TrackInformation> lyrics = lyricsDownloader.getLyrics(popularTracks);

        DuolingoEmersionServer server = new DuolingoEmersionServer();
    }

    static public ArrayList<TrackInformation> getPopularTracks() throws IOException {
        ArrayList<TrackInformation> popularTracks = new ArrayList<TrackInformation>();
        String rootFolder = "database/";
       
        BufferedReader readFile = new BufferedReader(new FileReader("database/knownPlaylists"));
        String line = readFile.readLine();
        List<String> spotifyPlaylists = new ArrayList<>();
        while ((line = readFile.readLine()) != null) {            
            popularTracks.addAll(getTrackInformationFromFile(rootFolder + line));
        }
           
        readFile.close();
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "top2000stemlijst_2012.csv"));
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "zweedsvankim.csv"));
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "swedish-music.csv"));
        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "spanish-music-database-2-version-13.csv"));

        popularTracks.addAll(getTrackInformationFromFile(rootFolder + "aliensamaditop300artistschart.csv"));
        System.out.println("Amount of tracks we want to download: " + popularTracks.size());


        return popularTracks;
    }

    private static ArrayList<TrackInformation> getTrackInformationFromFile(String csvFile) throws IOException {
        ArrayList<TrackInformation> popularTracks = new ArrayList<TrackInformation>();
        File csvData = new File(csvFile);
        CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.RFC4180);
        
        for (CSVRecord csvRecord : parser) {
            System.out.println(csvRecord.toString());
            popularTracks.add(new TrackInformation(csvRecord.get(0), csvRecord.get(1)));
        }
        return popularTracks;
    }

}
