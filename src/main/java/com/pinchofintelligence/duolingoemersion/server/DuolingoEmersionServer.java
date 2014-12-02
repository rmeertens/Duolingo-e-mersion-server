/**
 * Rolands stuff!
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.pinchofintelligence.duolingoemersion.main.MainClass;
import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.duolingo.DuolingoApi;
import com.pinchofintelligence.duolingoemersion.duolingo.LanguageWithWords;
import com.pinchofintelligence.duolingoemersion.duolingo.UserRepresentation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class DuolingoEmersionServer {

    JTextArea textArea;
    private final SongsBasedOnUsernameHandler songsBasedOnUsernameHandler;

    public DuolingoEmersionServer() throws Exception {
        HashMap<String, TrackInformation> tracksDatabase = loadDatabase();

        JFrame frame = new JFrame("Duolingo recommender panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(15, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        frame.getContentPane().add(scrollPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        System.out.println("Starting HTTP Duolingo Emersion server");
        
        /*
        
         char[] passphrase = "passphrase".toCharArray();
   KeyStore ks = KeyStore.getInstance("JKS");
   ks.load(new FileInputStream("testkeys"), passphrase);

   KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
   kmf.init(ks, passphrase);

   TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
   tmf.init(ks);

   SSLContext ssl = SSLContext.getInstance("TLS");
   ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
   
        
        
        
        */
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
        /*
        HttpsConfigurator a = new HttpsConfigurator(ssl) {
            @Override
            public void configure (HttpsParameters params) {

            // get the remote address if needed
            InetSocketAddress remote = params.getClientAddress();

            SSLContext c = getSSLContext();

            // get the default parameters
            SSLParameters sslparams = c.getDefaultSSLParameters();
            //if (remote.equals (...) ) {
                // modify the default set for client x
            //}

            params.setSSLParameters(sslparams);
            // statement above could throw IAE if any params invalid.
            // eg. if app has a UI and parameters supplied by a user.

            }
        };
        */
       // server.setHttpsConfigurator (a);
        
        this.songsBasedOnUsernameHandler = new SongsBasedOnUsernameHandler(this, textArea, tracksDatabase);
        server.createContext("/duolingorecommendation", songsBasedOnUsernameHandler);
        
        server.setExecutor(null); // creates a default executor
        server.start();

    }

   
    /**
     * returns the url parameters in a map
     *
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
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

    static class LyricWithScore implements Comparable<LyricWithScore> {

        double score;
        TrackInformation trackInformation;

        public LyricWithScore(double score, TrackInformation name) {
            this.score = score;
            this.trackInformation = name;
        }

        @Override
        public int compareTo(LyricWithScore o) {
            return score < o.score ? -1 : score > o.score ? 1 : 0;
        }
    }
}
