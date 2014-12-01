package com.pinchofintelligence.duolingoemersion.crawlers.books;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Roland
 */
public class GutenbergCrawler {
   
    public static void main(String[] args) throws MalformedURLException
    {
        for(int x = 0; x < 100; x++){
            getData("http://www.gutenberg.org/files/"+x+"/"+x+".txt");
        }
    }
    public static void getData(String address) throws MalformedURLException {
       
        URL page = new URL(address);
        StringBuffer text = new StringBuffer();
        try {
            HttpURLConnection conn = (HttpURLConnection)
                page.openConnection();
            conn.connect();
            InputStreamReader in = new InputStreamReader((InputStream) conn.getContent());
            BufferedReader buff = new BufferedReader(in);
            
            String line;
            do {
                line = buff.readLine();
                System.out.println(line);
                text.append(line + "\n");
            } while (line != null);

        } catch (IOException ioe) {
            System.out.println("IO Error:" + ioe.getMessage());
        }
    }
}
