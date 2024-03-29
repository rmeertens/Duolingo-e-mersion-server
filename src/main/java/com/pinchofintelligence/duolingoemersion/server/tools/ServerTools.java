/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinchofintelligence.duolingoemersion.server.tools;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Roland
 */
public class ServerTools {
    
    public static void writeResponse(String response, HttpExchange exchangeObject) throws IOException {
               
        exchangeObject = addResponseHeaders(exchangeObject);
        byte[] bytes = response.getBytes(Charset.forName("UTF-8"));
        exchangeObject.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchangeObject.getResponseBody();
        os.write(bytes);
        os.close();
    }
    private static HttpExchange addResponseHeaders(HttpExchange exchangeObject) {
        exchangeObject.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchangeObject.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        exchangeObject.getResponseHeaders().add("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        exchangeObject.getResponseHeaders().add("Access-Control-Max-Age", "1728000");
        return exchangeObject;
    }

    /**
     * returns the url parameters in a map
     *
     * @param exchangeObject 
     * @return map
     */
    public static Map<String, String> queryToMap(HttpExchange exchangeObject) {
        String query = exchangeObject.getRequestURI().getQuery();
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
}
