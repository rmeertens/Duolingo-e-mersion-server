/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server.tools;

import com.sun.net.httpserver.HttpExchange;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Roland
 */
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
}
