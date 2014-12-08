/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifyfunctions;

import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.TracksRequest;
import com.wrapper.spotify.methods.UserRequest;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roland
 */
public class DownloadFromSpotify {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Create an API instance. The default instance connects to https://api.spotify.com/.
        //Api api = Api.DEFAULT_API;

        BufferedReader readFile = new BufferedReader(new FileReader("database/spotifyCredentials.txt"));
        String clientID = readFile.readLine();
        String clientSecret = readFile.readLine();
        String redirectURI = readFile.readLine();
        String oAuthToken = readFile.readLine();
        readFile.close();
        
 
        BufferedReader spotifyPlaylistsFile = new BufferedReader(new FileReader("database/spotifyPlaylistIDs.txt"));
        String line = null;
        List<String> spotifyPlaylists = new ArrayList<>();
        while ((line = spotifyPlaylistsFile.readLine()) != null) {
            spotifyPlaylists.add(line);
        }
        readFile.close();

        Api api = Api.builder()
                .clientId(clientID)
                .clientSecret(clientSecret)
                .redirectURI(redirectURI)
                .accessToken(oAuthToken)
                .build();

        
        getPlaylists(api, spotifyPlaylists);
    }

   

    public static void getPlaylists(Api api, List<String> spotifyPlaylists) {
        try {
            FileWriter saveFilePlaylists = new FileWriter("database/knownPlaylists");

            for (String playlistID : spotifyPlaylists) {

                final PlaylistRequest request = api.getPlaylist("duomusico", playlistID).build();

                try {
                    final Playlist playlist = request.get();

                    System.out.println("Retrieved playlist " + playlist.getName());
                    System.out.println(playlist.getDescription());
                    System.out.println("It contains " + playlist.getTracks().getTotal() + " tracks");

                    try {
                        //. You can then wrap this in an OutputStreamWriter
                        FileOutputStream saveFile3 = new FileOutputStream("database/" + playlist.getName());
                        OutputStreamWriter saveFile2 = new OutputStreamWriter(saveFile3,"UTF-8");
                        Writer saveFile = new BufferedWriter(saveFile2);
                        
                        saveFilePlaylists.write(playlist.getName()+"\n");
                        for (PlaylistTrack x : playlist.getTracks().getItems()) {
                            String artists = "";
                            for (SimpleArtist artist : x.getTrack().getArtists()) {
                                artists += artist.getName() + " ";
                            }
                            System.out.println("It contains " + x.getTrack().getName() + " by " + artists);
                            saveFile.write(artists + "," + x.getTrack().getName() + "\n");
                        }

                        saveFile.flush();
                        saveFile.close();
                    } catch (IOException ex) {
                        Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (Exception e) {
                    System.out.println("Something went wrong!" + e.getMessage());
                }

            }
            saveFilePlaylists.close();
        } catch (IOException ex) {
            Logger.getLogger(LyricsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
