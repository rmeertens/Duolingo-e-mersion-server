/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.crawlers.music;

/**
 *
 * @author Roland
 */
public class TrackInformation implements java.io.Serializable {
    public String nameArtist;
    public String nameSong;
    public String lyricsBody;
    public String language;
    public int LyricsID;
    
    public String ScriptTrackingURL;
    public String PixelTrackingURL;
    public String LyricsCopyright;
    
    public TrackInformation(String nameArtist, String nameSong) {
        this.nameArtist = nameArtist;
        this.nameSong = nameSong;
    }
    public void setLyrics(String newLyricsBody, String lyricsLang, int newLyricsID)
    {
        this.language = lyricsLang;
        this.lyricsBody = newLyricsBody;
        this.LyricsID = newLyricsID;
    }
    public void setMusicxmatchInfo(String newScriptTrackingUrl, String newPixelTrackingUrl, String newLyricsCopyright)
    {
        this.ScriptTrackingURL = newScriptTrackingUrl;
        this.PixelTrackingURL = newPixelTrackingUrl;
        this.LyricsCopyright = newLyricsCopyright;
    }
}
