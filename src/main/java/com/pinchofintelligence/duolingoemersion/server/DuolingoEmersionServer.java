/**
 * Rolands stuff!
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.server.tools.ServerLog;
import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.server.handlers.RandomSongsBasedOnCountryHandler;
import com.pinchofintelligence.duolingoemersion.server.handlers.SongsBasedOnUsernameHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Roland
 */
public class DuolingoEmersionServer {
    public static final int AMOUNT_OF_SONGS_RETURNING = 10;

   

    public DuolingoEmersionServer() throws Exception {
        HashMap<String, TrackInformation> tracksDatabase = loadDatabase();
        System.out.println("Loaded tracksdatabase, this total database contains " + tracksDatabase.size() + " songs");
    
        ServerLog serverLogger = new ServerLog();
       
        HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
        addHandlers(server, serverLogger, tracksDatabase);
 
    }
    private void addHandlers(HttpServer server, ServerLog serverLogger, HashMap<String, TrackInformation> tracksDatabase) 
    {
           
        server.createContext("/duolingorecommendation",new SongsBasedOnUsernameHandler(serverLogger, tracksDatabase));
        server.createContext("/randomSong", new RandomSongsBasedOnCountryHandler(serverLogger,tracksDatabase));
        server.setExecutor(null); // creates a default executor
        server.start(); 
     }

    private HashMap<String, TrackInformation> loadDatabase() {
        FileInputStream f = null;
        try {
            String rootFolder = "database";
            File file = new File(rootFolder+"/ourMusicDatabase.txt");
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
