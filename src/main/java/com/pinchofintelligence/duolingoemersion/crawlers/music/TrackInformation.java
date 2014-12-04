/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.crawlers.music;

import org.jmusixmatch.entity.track.TrackData;

/**
 *
 * @author Roland
 */
public class TrackInformation implements java.io.Serializable {
    
    private int trackId;
    private String trackMbid;
    private String track_spotify_id;

    private int track_soundcloud_id;
    private int track_rating;
    private int track_length;
    private int commontrack_id;
    private int explicit;
    private int has_lyrics;
    private int has_subtitles;
    private int num_favourite;
    private String album_coverart_100x100;
    private String album_coverart_350x350;
    private String album_coverart_500x500;
    private String album_coverart_800x800;
    private String track_edit_url;
    private String updated_time;
    private int albumId;
    private String albumName;
    private String artistId;
    private String artistMbid;
    private String artistName;
    private String track_share_url;
    private int instrumental;
    private int lyricsId;
    private int lyricsLength;
    private int subtitleId;
    private String trackName;
    
    
     
     //private int restricted;
     private String lyrics_body;
     private String lyrics_language;
     private String lyrics_copyright;
        
    public TrackInformation(String nameArtist, String nameSong) {
        this.artistName = nameArtist;
        this.trackName = nameSong;
    }
    public void setLyrics(String lyricsBody, String lyricsLanguage, String LyricsCopyright)
    {
        
        this.lyrics_body = lyricsBody; 
        this.lyrics_language = lyricsLanguage;
        this.lyrics_copyright = LyricsCopyright;
    }
    public void setMusicxmatchInfo(TrackData info)
    {
         this.trackId = info.getTrackId();
        this.trackMbid = info.getTrackMbid();
        this.track_spotify_id = info.getTrack_spotify_id();
        this.track_soundcloud_id = info.getTrack_soundcloud_id();
        this.track_rating = info.getTrack_rating();
        this.track_length = info.getTrack_length();
        this.commontrack_id = info.getCommontrack_id();
        this.explicit = info.getExplicit();
        this.has_lyrics = info.getHas_lyrics();
        this.has_subtitles = info.getHas_subtitles();
        this.num_favourite = info.getNum_favourite();
        this.album_coverart_100x100 = info.getAlbum_coverart_100x100();
        this.album_coverart_350x350 = info.getAlbum_coverart_350x350();
        this.album_coverart_500x500 = info.getAlbum_coverart_500x500();
        this.album_coverart_800x800 = info.getAlbum_coverart_800x800();
        this.track_edit_url = info.getTrack_edit_url();
        this.updated_time = info.getUpdated_time();
        this.albumId = info.getAlbumId();
        this.albumName = info.getAlbumName();
        this.artistId = info.getArtistId();
        this.artistMbid = info.getArtistMbid();
        //this.artistName = info.getArtistName(); //WARNING: DO NOT SET THE ARTIST NAME FROM HERE!
        
        this.track_share_url = info.getTrackShareURL();
        this.instrumental = info.getInstrumental();
        this.lyricsId = info.getLyricsId();
        this.lyricsLength = info.getLyricsLength();
        this.subtitleId = info.getSubtitleId();
        //this.trackName = info.getTrackName(); //WARNING: DO NOT SET THE TRACK NAME FROM HERE!
    }
    
    
    
    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getTrackMbid() {
        return trackMbid;
    }

    public void setTrackMbid(String trackMbid) {
        this.trackMbid = trackMbid;
    }

    public String getTrack_spotify_id() {
        return track_spotify_id;
    }

    public void setTrack_spotify_id(String track_spotify_id) {
        this.track_spotify_id = track_spotify_id;
    }

    public int getTrack_soundcloud_id() {
        return track_soundcloud_id;
    }

    public void setTrack_soundcloud_id(int track_soundcloud_id) {
        this.track_soundcloud_id = track_soundcloud_id;
    }

    public int getTrack_rating() {
        return track_rating;
    }

    public void setTrack_rating(int track_rating) {
        this.track_rating = track_rating;
    }

    public int getTrack_length() {
        return track_length;
    }

    public void setTrack_length(int track_length) {
        this.track_length = track_length;
    }

    public int getCommontrack_id() {
        return commontrack_id;
    }

    public void setCommontrack_id(int commontrack_id) {
        this.commontrack_id = commontrack_id;
    }

    public int getExplicit() {
        return explicit;
    }

    public void setExplicit(int explicit) {
        this.explicit = explicit;
    }

    public int getHas_lyrics() {
        return has_lyrics;
    }

    public void setHas_lyrics(int has_lyrics) {
        this.has_lyrics = has_lyrics;
    }

    public int getHas_subtitles() {
        return has_subtitles;
    }

    public void setHas_subtitles(int has_subtitles) {
        this.has_subtitles = has_subtitles;
    }

    public int getNum_favourite() {
        return num_favourite;
    }

    public void setNum_favourite(int num_favourite) {
        this.num_favourite = num_favourite;
    }

    public String getAlbum_coverart_100x100() {
        return album_coverart_100x100;
    }

    public void setAlbum_coverart_100x100(String album_coverart_100x100) {
        this.album_coverart_100x100 = album_coverart_100x100;
    }

    public String getAlbum_coverart_350x350() {
        return album_coverart_350x350;
    }

    public void setAlbum_coverart_350x350(String album_coverart_350x350) {
        this.album_coverart_350x350 = album_coverart_350x350;
    }

    public String getAlbum_coverart_500x500() {
        return album_coverart_500x500;
    }

    public void setAlbum_coverart_500x500(String album_coverart_500x500) {
        this.album_coverart_500x500 = album_coverart_500x500;
    }

    public String getAlbum_coverart_800x800() {
        return album_coverart_800x800;
    }

    public void setAlbum_coverart_800x800(String album_coverart_800x800) {
        this.album_coverart_800x800 = album_coverart_800x800;
    }

    public String getTrack_edit_url() {
        return track_edit_url;
    }

    public void setTrack_edit_url(String track_edit_url) {
        this.track_edit_url = track_edit_url;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }


    public String getTrack_share_url() {
        return track_share_url;
    }

    public void setTrack_share_url(String track_share_url) {
        this.track_share_url = track_share_url;
    }

    public int getInstrumental() {
        return instrumental;
    }

    public void setInstrumental(int instrumental) {
        this.instrumental = instrumental;
    }

    public int getLyricsId() {
        return lyricsId;
    }

    public void setLyricsId(int lyricsId) {
        this.lyricsId = lyricsId;
    }

    public int getLyricsLength() {
        return lyricsLength;
    }

    public void setLyricsLength(int lyricsLength) {
        this.lyricsLength = lyricsLength;
    }

    public int getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(int subtitleId) {
        this.subtitleId = subtitleId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }


    public String getLyrics_body() {
        return lyrics_body;
    }

    public void setLyrics_body(String lyrics_body) {
        this.lyrics_body = lyrics_body;
    }

    public String getLyrics_language() {
        return lyrics_language;
    }

    public void setLyrics_language(String lyrics_language) {
        this.lyrics_language = lyrics_language;
    }

    public String getLyrics_copyright() {
        return lyrics_copyright;
    }

    public void setLyrics_copyright(String lyrics_copyright) {
        this.lyrics_copyright = lyrics_copyright;
    }
}
