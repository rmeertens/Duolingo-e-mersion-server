/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.crawlers.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Roland
 */
public class LyricsDownloader {

  
    
    HashMap<String, TrackInformation> tracksDatabase;
    private String apiKey;
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
            if (tracksDatabase.containsKey(track.nameSong + "-" + track.nameArtist)) {
                System.out.println("Skipping " + track.nameSong + " from " + track.nameArtist);
                continue;
            }
            if (amountDownloaded > 500) {
                System.err.println("ERROR! Downloaded " + amountDownloaded + " files!");
                break;
            }
            try {

                  TrackInformation lyrics = getLyrics(musixMatch, track);
                //TrackInformation lyrics = getLyricsChartLyrics(track);

                allLyrics.add(lyrics);
                System.out.println("Just added something to the database " + tracksDatabase.size());
                tracksDatabase.put(track.nameSong + "-" + track.nameArtist, track);
                System.out.println("2 Just added something to the database" + tracksDatabase.size());

                System.out.println("Downloaded " + track.nameArtist + " ---- " + track.nameSong);
                amountDownloaded += 1;
                saveAmountOfFilesDownloaded(amountDownloaded);
                saveTracksDatabase(tracksDatabase);
            } catch (MalformedURLException ex) {
                Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return allLyrics;
    }

    private TrackInformation getLyrics(MusixMatch musixMatch, TrackInformation firstInfo) throws MusixMatchException {
        try {
            // Track Search [ Fuzzy ]
            System.out.println("Name song:-" + firstInfo.nameSong+"-");
            System.out.println("Name artist:-" + firstInfo.nameArtist+"-");
            Track track = musixMatch.getMatchingTrack(firstInfo.nameSong, firstInfo.nameArtist);
            
            TrackData data = track.getTrack();
            
            
            System.out.println("AlbumID : " + data.getAlbumId());
            System.out.println("Album Name : " + data.getAlbumName());
            System.out.println("Artist ID : " + data.getArtistId());
            System.out.println("Album Name : " + data.getArtistName());
            System.out.println("Track ID : " + data.getTrackId());
            int trackID = data.getTrackId();
            

            Lyrics lyrics = musixMatch.getLyrics(trackID);
            
            System.out.println("Lyrics ID       : " + lyrics.getLyricsId());
            System.out.println("Lyrics Language : " + lyrics.getLyricsLang());
            System.out.println("Lyrics Body     : " + lyrics.getLyricsBody());
            System.out.println("Script-Tracking-URL : " + lyrics.getScriptTrackingURL());
            System.out.println("Pixel-Tracking-URL : " + lyrics.getPixelTrackingURL());
            System.out.println("Lyrics Copyright : " + lyrics.getLyricsCopyright());

            firstInfo.setLyrics(lyrics.getLyricsBody(), lyrics.getLyricsLang(), lyrics.getLyricsId());
            firstInfo.setMusicxmatchInfo(lyrics.getScriptTrackingURL(), lyrics.getPixelTrackingURL(), lyrics.getLyricsCopyright());

            return firstInfo;
        } catch (Exception e) {
            e.printStackTrace();
            firstInfo.setLyrics("sno slyrics", "sunknown", -1);
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
            trackToAdd.put("nameArtist", ((TrackInformation) pairs.getValue()).nameArtist);
            trackToAdd.put("nameSong", ((TrackInformation) pairs.getValue()).nameSong);
            trackToAdd.put("songtext", ((TrackInformation) pairs.getValue()).lyricsBody);
            trackToAdd.put("lyricsID", ((TrackInformation) pairs.getValue()).LyricsID);
            trackToAdd.put("language", ((TrackInformation) pairs.getValue()).language);
            trackToAdd.put("scriptTrackingUrl", ((TrackInformation) pairs.getValue()).ScriptTrackingURL);
            trackToAdd.put("pixelTrackingUrl", ((TrackInformation) pairs.getValue()).PixelTrackingURL);
            trackToAdd.put("copyright", ((TrackInformation) pairs.getValue()).LyricsCopyright);

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

    private TrackInformation getLyricsChartLyrics(TrackInformation track) throws MalformedURLException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {

        String urlString = "http://api.chartlyrics.com/apiv1.asmx/SearchLyric?artist=michael%20jackson&song=bad";
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream in = conn.getInputStream();
        Document doc = builder.parse(in);
        in.close();
        
        
        NodeList nodes = doc.getElementsByTagName("SearchLyricResult");
        
        System.out.println("There are " + nodes.getLength() + "  elements.");
        
        for (int i = 0; i < nodes.getLength(); i++) {
           Element element = (Element) nodes.item(i);

           NodeList name = element.getElementsByTagName("LyricChecksum");
           Element line = (Element) name.item(0);

           System.out.println("TrackChecksum: " + line.getFirstChild().getTextContent());

           System.out.println("TrackId: " + line.getAttribute("LyricId"));
           getSongtext(line.getAttribute("LyricId"), line.getFirstChild().getTextContent());
         //  http://api.chartlyrics.com/apiv1.asmx/GetLyric?lyricId=1710&lyricCheckSum=adc1973bc2401c48d9e088e93cda83c7
/*
           NodeList age = element.getElementsByTagName("age");
           line = (Element) age.item(0);
           System.out.println("Age: " + line.getFirstChild().getTextContent());

           NodeList hobby = element.getElementsByTagName("hobby");
           for(int j=0;j<hobby.getLength();j++)
           {
              line = (Element) hobby.item(j);
              System.out.println("Hobby: " + line.getFirstChild().getTextContent());
           }
*/
           System.out.println();
           break;
        }
        
        
            
        TransformerFactory factory2 = TransformerFactory.newInstance();
        Transformer xform = factory2.newTransformer();

// that’s the default xform; use a stylesheet to get a real one
        xform.transform(new DOMSource(doc), new StreamResult(System.out));
        return track;
    }
    
    
     private String getSongtext(String lyricID, String lyricChecksum) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {

        String urlString = "http://api.chartlyrics.com/apiv1.asmx/GetLyric?lyricId="+lyricID+"&lyricCheckSum="+lyricChecksum;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream in = conn.getInputStream();
        Document doc = builder.parse(in);
        in.close();
        NodeList nodes = doc.getElementsByTagName("SearchLyricResult");
        System.out.println("There are " + nodes.getLength() + "  elements.");
        
        for (int i = 0; i < nodes.getLength(); i++) {
           Element element = (Element) nodes.item(i);

           NodeList name = element.getElementsByTagName("Lyric");
           Element line = (Element) name.item(0);

           System.out.println("Lyric: " + line.getFirstChild().getTextContent());
           return line.getFirstChild().getTextContent();
//           System.out.println("TrackId: " + line.getAttribute("LyricId"));
           
   //        http://api.chartlyrics.com/apiv1.asmx/GetLyric?lyricId=1710&lyricCheckSum=adc1973bc2401c48d9e088e93cda83c7
/*
           NodeList age = element.getElementsByTagName("age");
           line = (Element) age.item(0);
           System.out.println("Age: " + line.getFirstChild().getTextContent());

           NodeList hobby = element.getElementsByTagName("hobby");
           for(int j=0;j<hobby.getLength();j++)
           {
              line = (Element) hobby.item(j);
              System.out.println("Hobby: " + line.getFirstChild().getTextContent());
           }
*/
           
        }
        return "sno slyrics sfound";
    }
}
