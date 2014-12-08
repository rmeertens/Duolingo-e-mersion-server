/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.crawlers.music;

import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class LyricsDownloader {

  
    
    HashMap<String, TrackInformation> tracksDatabase;
    private final String apiKey;
    public LyricsDownloader() throws FileNotFoundException, IOException {
        // Read the key
        BufferedReader readFile = new BufferedReader(new FileReader("database/key.txt"));
        apiKey  = readFile.readLine();
        readFile.close();
        
        // Load the database
        tracksDatabase = loadDatabase();
        System.out.println("Now our database contains " + tracksDatabase.size() + " songs");
        

    }

    public ArrayList<TrackInformation> getLyrics(ArrayList<TrackInformation> tracks) throws FileNotFoundException, UnsupportedEncodingException, IOException, JSONException, MusixMatchException {

        ArrayList<TrackInformation> allLyrics = new ArrayList<TrackInformation>();
        MusixMatch musixMatch = new MusixMatch(apiKey);

        BufferedReader readFile = new BufferedReader(new FileReader("database/AmountOfLyricsDownloaded.txt"));
        // Get the integer value from the String.
        int amountDownloaded = Integer.parseInt(readFile.readLine());
        readFile.close();
        //  int amountDownloaded = 0;
        System.out.println("We already downloaded " + amountDownloaded + " files");

        for (TrackInformation track : tracks) {
            String keyInDatabase = track.getArtistNameOwnDatabase()+ "-" + track.getTrackNameOwnDatabase();
            if (tracksDatabase.containsKey(keyInDatabase)) {
                System.out.println("Skipping " + keyInDatabase);
                continue;
            }
            if (amountDownloaded > 500) {
                System.err.println("ERROR! Downloaded " + amountDownloaded + " files!");
                break;
            }
            
            TrackInformation lyrics = getLyrics(musixMatch, track);
            allLyrics.add(lyrics);              
            tracksDatabase.put(keyInDatabase, track);
            amountDownloaded += 1;
            saveAmountOfFilesDownloaded(amountDownloaded);

            
        }
        saveTracksDatabase(tracksDatabase);
        return allLyrics;
    }

    private TrackInformation getLyrics(MusixMatch musixMatch, TrackInformation firstInfo) {
        try {
            // Track Search [ Fuzzy ]
            System.out.println("Seaching " + firstInfo.getTrackNameOwnDatabase() + " by " + firstInfo.getArtistNameOwnDatabase());
            
            
            Track track = musixMatch.getMatchingTrack(firstInfo.getTrackNameOwnDatabase(), firstInfo.getArtistNameOwnDatabase());
            
            TrackData data = track.getTrack();
      
            System.out.println("Track share URL " + data.getTrackShareURL());
            System.out.println("AlbumID : " + data.getAlbumId());
            System.out.println("Album Name : " + data.getAlbumName());
            System.out.println("Artist ID : " + data.getArtistId());
            System.out.println("Album Name : " + data.getArtistName());
            System.out.println("Track ID : " + data.getTrackId());
            
            
            int trackID = data.getTrackId();
            firstInfo.setMusicxmatchInfo(data);

            if(data.getHas_lyrics()==1)
            {
                Lyrics lyrics = musixMatch.getLyrics(trackID);
                
                System.out.println("Lyrics ID       : " + lyrics.getLyricsId());
                System.out.println("Lyrics Language : " + lyrics.getLyricsLang());
                System.out.println("Lyrics Body     : " + lyrics.getLyricsBody());
                System.out.println("Lyrics Copyright : " + lyrics.getLyricsCopyright());

                firstInfo.setLyrics(lyrics.getLyricsBody(), lyrics.getLyricsLang(), lyrics.getLyricsCopyright());
            }
            else
            {
                firstInfo.setLyrics("sno slyrics sfound", "sno slyrics sfound", "sno slyrics sfound");
            }
            

            return firstInfo;
        } 
        catch (MusixMatchException e) {
            e.printStackTrace();
            return firstInfo;
        }
        catch (JsonSyntaxException e) {
            e.printStackTrace();
            return firstInfo;
        }
    }

    private void saveAmountOfFilesDownloaded(int amountDownloaded) {

        try {
            FileWriter saveFile = new FileWriter("database/AmountOfLyricsDownloaded.txt");
            saveFile.write(amountDownloaded + "\n");
            saveFile.flush();
            saveFile.close();
        } catch (IOException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveTracksDatabase(HashMap<String, TrackInformation> tracksDatabase) throws FileNotFoundException, IOException, JSONException {
        File file = new File("database/ourMusicDatabase.txt");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(tracksDatabase);
        s.close();

        FileWriter saveFile = new FileWriter("database/ourMusicDatabaseJSON.txt");

        JSONArray array = new JSONArray();
        Iterator it = tracksDatabase.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            JSONObject trackToAdd = new JSONObject();
            
            
            trackToAdd.put("trackId", ((TrackInformation) pairs.getValue()).getTrackId());
            trackToAdd.put("trackMbid", ((TrackInformation) pairs.getValue()).getTrackMbid());
            trackToAdd.put("track_spotify_id", ((TrackInformation) pairs.getValue()).getTrack_spotify_id());

            trackToAdd.put("track_soundcloud_id", ((TrackInformation) pairs.getValue()).getTrack_soundcloud_id());
            trackToAdd.put("track_rating", ((TrackInformation) pairs.getValue()).getTrack_rating());
            trackToAdd.put("track_length", ((TrackInformation) pairs.getValue()).getTrack_length());
            trackToAdd.put("commontrack_id", ((TrackInformation) pairs.getValue()).getCommontrack_id());
            trackToAdd.put("explicit", ((TrackInformation) pairs.getValue()).getExplicit());
            trackToAdd.put("has_lyrics", ((TrackInformation) pairs.getValue()).getHas_lyrics());
            trackToAdd.put("has_subtitles", ((TrackInformation) pairs.getValue()).getHas_subtitles());
            trackToAdd.put("num_favourite", ((TrackInformation) pairs.getValue()).getNum_favourite());
            trackToAdd.put("album_coverart_100x100", ((TrackInformation) pairs.getValue()).getAlbum_coverart_100x100());
            trackToAdd.put("album_coverart_350x350", ((TrackInformation) pairs.getValue()).getAlbum_coverart_350x350());
            trackToAdd.put("album_coverart_500x500", ((TrackInformation) pairs.getValue()).getAlbum_coverart_500x500());
            trackToAdd.put("album_coverart_800x800", ((TrackInformation) pairs.getValue()).getAlbum_coverart_800x800());
            trackToAdd.put("track_edit_url", ((TrackInformation) pairs.getValue()).getTrack_edit_url());
            trackToAdd.put("updated_time", ((TrackInformation) pairs.getValue()).getUpdated_time());
            trackToAdd.put("albumId", ((TrackInformation) pairs.getValue()).getAlbumId());
            trackToAdd.put("albumName", ((TrackInformation) pairs.getValue()).getAlbumName());
            trackToAdd.put("artistId", ((TrackInformation) pairs.getValue()).getArtistId());
            trackToAdd.put("artistMbid", ((TrackInformation) pairs.getValue()).getArtistMbid());
            trackToAdd.put("artistNameMusixMatch", ((TrackInformation) pairs.getValue()).getArtistNameMusixMatch());
            trackToAdd.put("artistNameOwnDatabase", ((TrackInformation) pairs.getValue()).getArtistNameOwnDatabase());
            
            trackToAdd.put("track_share_url", ((TrackInformation) pairs.getValue()).getTrack_share_url());
            trackToAdd.put("instrumental", ((TrackInformation) pairs.getValue()).getInstrumental());
            trackToAdd.put("lyricsId", ((TrackInformation) pairs.getValue()).getLyricsId());
            trackToAdd.put("lyricsLength", ((TrackInformation) pairs.getValue()).getLyricsLength());
            trackToAdd.put("subtitleId", ((TrackInformation) pairs.getValue()).getSubtitleId());
            trackToAdd.put("trackNameMusixMatch", ((TrackInformation) pairs.getValue()).getTrackNameMusixMatch());
            trackToAdd.put("trackNameOwnDatabase", ((TrackInformation) pairs.getValue()).getTrackNameOwnDatabase());
            
             //trackToAdd.put("restricted", ((TrackInformation) pairs.getValue()).getRestricted());
             trackToAdd.put("lyrics_body", ((TrackInformation) pairs.getValue()).getLyrics_body());
             trackToAdd.put("lyrics_language", ((TrackInformation) pairs.getValue()).getLyrics_language());
             trackToAdd.put("lyrics_copyright", ((TrackInformation) pairs.getValue()).getLyrics_copyright());

     

            array.put(trackToAdd);
        }
        saveFile.write(array.toString() + "\n");
        saveFile.flush();
        saveFile.close();
    }

    private HashMap<String, TrackInformation> loadDatabase() {
        FileInputStream f = null;
        System.out.println("Started loading database");
        try {
            File file = new File("database/ourMusicDatabase.txt");
            f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            HashMap<String, TrackInformation> fileObj2 = (HashMap<String, TrackInformation>) s.readObject();

            s.close();
            f.close();
            return fileObj2;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);

        }
        System.err.println("Generating new database");
        return new HashMap<String, TrackInformation>();
    }
}
