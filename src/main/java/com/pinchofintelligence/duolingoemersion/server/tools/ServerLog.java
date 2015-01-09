/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server.tools;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Roland
 */

/*
public class ServerLog {

    JTextArea textArea;
    
    public ServerLog() {
        JFrame frame = new JFrame();
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        frame.getContentPane().add(scrollPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void addString(String string)
    {
        textArea.append(string+"\n");
    }

    public void addEvent(Object source, HttpExchange exchangeObject, Map<String, String> parms) {
        textArea.append(source.getClass().getName() + ": ");
        for (Map.Entry pairs : parms.entrySet()) {
            textArea.append("--> " + pairs.getKey() + " = " + pairs.getValue());
        }
        textArea.append(" ip source: " + exchangeObject.getRemoteAddress().toString());   
    }
}*/

public class ServerLog {
    private Logger logger ;
    public ServerLog() {
    logger = Logger.getLogger("MyLog");  
    FileHandler fh;  

    try {  

        // This block configure the logger with handler and formatter  
        fh = new FileHandler("MyLogFile.log");  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  

        // the following statement is used to log any messages  
        logger.info("My first log");  

    } catch (SecurityException e) {  
        e.printStackTrace();  
    
    }   catch (IOException ex) {  
            Logger.getLogger(ServerLog.class.getName()).log(Level.SEVERE, null, ex);
        }  

    logger.info("Hi How r u?");  

    }
    
    public void addString(String string)
    {
        logger.info(string);
    }

    public void addEvent(Object source, HttpExchange exchangeObject, Map<String, String> parms) {
        String totalArguments = "";
        for (Map.Entry pairs : parms.entrySet()) {
            totalArguments += "--> " + pairs.getKey() + " = " + pairs.getValue();
        }
        totalArguments += " ip source: " + exchangeObject.getRemoteAddress().toString();   
        logger.info(source.getClass().getName() + ": " + totalArguments);
    }
}