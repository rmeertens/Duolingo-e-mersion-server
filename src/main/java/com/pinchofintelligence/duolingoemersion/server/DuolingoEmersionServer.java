/**
 * Rolands stuff!
 */
package com.pinchofintelligence.duolingoemersion.server;

import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.pinchofintelligence.duolingoemersion.server.tools.ServerLog;
import com.pinchofintelligence.duolingoemersion.crawlers.music.LyricsDownloader;
import com.pinchofintelligence.duolingoemersion.crawlers.music.TrackInformation;
import com.pinchofintelligence.duolingoemersion.server.handlers.RandomSongsBasedOnCountryHandler;
import com.pinchofintelligence.duolingoemersion.server.handlers.SongsBasedOnUsernameHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
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
       /*
        int port = 9999;
        try
        {
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress (port);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create ( address, 0 );
            SSLContext sslContext = SSLContext.getInstance ( "TLS" );
            System.out.println("We have a SSLContext");
            
            // initialise the keystore
            char[] password = "roland".toCharArray ();
            KeyStore ks = KeyStore.getInstance ( "JKS" );
            FileInputStream fis = new FileInputStream ( "keystore.jks" );
            ks.load ( fis, password );

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
            kmf.init ( ks, password );
            System.out.println("Done with init");
            
            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
            tmf.init ( ks );

            // setup the HTTPS context and parameters
            sslContext.init ( kmf.getKeyManagers (), tmf.getTrustManagers (), null );
            System.out.println("Starting to configure SSL");
            httpsServer.setHttpsConfigurator ( new HttpsConfigurator( sslContext )
            {
                @Override
                public void configure ( HttpsParameters params )
                {
                    System.out.println("Configure ");
                    try
                    {
                        System.out.println("Configure 2");
                        // initialise the SSL context
                        SSLContext c = SSLContext.getDefault ();
                        System.out.println("Configure 3");
                        SSLEngine engine = c.createSSLEngine ();
                        System.out.println("Configure 4");
                        params.setNeedClientAuth ( false );
                        System.out.println("Configure 5");
                        params.setCipherSuites ( engine.getEnabledCipherSuites () );
                        System.out.println("Configure 6");
                        params.setProtocols ( engine.getEnabledProtocols () );
                        System.out.println("Configure 7");

                        // get the default parameters
                        SSLParameters defaultSSLParameters = c.getDefaultSSLParameters ();
                        System.out.println("Configure 8");
                        params.setSSLParameters ( defaultSSLParameters );
                        System.out.println("Done with SSL");
                    }
                    catch ( Exception ex )
                    {
                        System.out.println("ERROR HERE!");
                    }
                }
            } );
            
            System.out.println("Starting to add handlers");
            addHandlers(httpsServer, serverLogger, tracksDatabase);
            
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start(); 
            
            System.out.println("Done adding handlers");
            //LigServer server = new LigServer ( httpsServer );
            //joinableThreadList.add ( server.getJoinableThread () );
        }
        catch ( Exception exception )
        {
           System.out.println("ERROR THERE!");
        }
*/
        
          HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
        addHandlers(server, serverLogger, tracksDatabase);
        server.setExecutor(null); // creates a default executor
            server.start(); 
        
        
 
    }
    private void addHandlers(HttpServer server, ServerLog serverLogger, HashMap<String, TrackInformation> tracksDatabase) 
    {
           
        server.createContext("/duolingorecommendation",new SongsBasedOnUsernameHandler(serverLogger, tracksDatabase));
        server.createContext("/randomSong", new RandomSongsBasedOnCountryHandler(serverLogger,tracksDatabase));
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
