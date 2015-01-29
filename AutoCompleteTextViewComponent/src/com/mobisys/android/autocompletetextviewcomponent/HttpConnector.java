package com.mobisys.android.autocompletetextviewcomponent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import android.os.Bundle;
import android.util.Log;

public class HttpConnector {

	public static String getResponse(String urlString, Bundle headers){
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {

	        URL url = new URL(urlString);
	        conn = (HttpURLConnection) url.openConnection();
	        Set<String> keys = headers.keySet();
	        for(String key:keys){
	        	String value = headers.getString(key);
	        	Log.d("Autocomplete URL ==>", "Key: "+key+" Value: "+value);
	        	conn.setRequestProperty(key, value);
	        }
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	        
	        
	    } catch (MalformedURLException e) {
	        Log.e("AppUtil", "Error processing Autocomplete API URL", e);
	    } catch (IOException e) {
	        Log.e("AppUtil", "Error connecting to Autocomplete API", e);
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }
	    return jsonResults.toString();
	}
}
