package com.ifp.wikwio.http;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.ifp.wikwio.Constants;
import com.ifp.wikwio.Preferences;
import com.ifp.wikwio.utils.SharedPreferencesUtil;
import com.loopj.android.http.RequestParams;

/**
 * @author mahavir (vir.jain)
 *
 */
public class HttpUtils {
	private static final String TAG = "HttpUtils";

	public static final String HTTP = "http";
	public static final String HTTPS = "https";

	public static final int PORT = -1;
	public static final String STAGE_HOST = "pamba.strandls.com";
	public static final String PROD_HOST = "portal.wikwio.org"; //local host  
	
	public static String stageOrProdBaseURL(){
		if(Preferences.IS_STAGING) return STAGE_HOST;
		else return PROD_HOST;
	}
	
	public static Bundle getHeaderBundle(Context context){
		Bundle bundle = new Bundle();
		bundle.putString(Request.HEADER_ACCEPT, Constants.APPLICATION_JSON);
		bundle.putString(Request.HEADER_APP_KEY, Constants.APP_KEY);
		String user_key = SharedPreferencesUtil.getSharedPreferencesString(context, Request.HEADER_AUTH_KEY, null);
		bundle.putString(Request.HEADER_AUTH_KEY, user_key);
		return bundle;
	}
	
	public static URI getUri(String host, String path, String query, int port, boolean https) {
		if (Preferences.DEBUG) Log.d(TAG, "Parameters: "+query);
		URI uri = null;

		try {
			// Construct the URI
			if (query.length() > 0) {
				
				uri = new URI(
						https?HTTPS:HTTP, null, host, port, path, query, null);	
				
				if (Preferences.DEBUG) Log.d(TAG, "URI: "+uri.toString());
			}
			
		} catch (Exception e) {
			if (Preferences.DEBUG) Log.e(TAG, "getUri()", e);
		}
		return uri;
	}
	
	public static URI getUri(String host, String path, int port, Bundle params, boolean https) {
		URI uri = null;

		try {
			// Combine the params in the bundle
			String query = bundleToQuery(params);
			if (Preferences.DEBUG) Log.d(TAG, "Encoded Parameters: "+query);

			// Construct the URI
			if (query.length() > 0) {
				
				uri = new URI(https?HTTPS:HTTP, null, host, port, path, query, null);	
				
				if (Preferences.DEBUG) Log.d(TAG, "URI:"+uri.toString());
			}
			else{
				uri = new URI(
						https?HTTPS:HTTP, null, host, port, path, null, null);	
				
				if (Preferences.DEBUG) Log.d(TAG, "URI: "+uri.toString());
			}

		} catch (Exception e) {
			if (Preferences.DEBUG) Log.e(TAG, "getUri()", e);
		}
		return uri;
	}

	public static URI getUri(String path, Bundle params) {
		URI uri = null;

		try {
			// Combine the params in the bundle
			String query = bundleToQuery(params);
			if (Preferences.DEBUG) Log.d(TAG, "Encoded Parameters: "+query);

			// Construct the URI
			if (query.length() > 0) {
				uri = new URI(
						HTTP, null, stageOrProdBaseURL(), PORT, path, query, null);	
				
				if (Preferences.DEBUG) Log.d(TAG, "URI: "+uri.toString());
			}
			else{
				uri = new URI(
						HTTP, null, stageOrProdBaseURL(), PORT, path, null, null);	
				
				if (Preferences.DEBUG) Log.d(TAG, "URI: "+uri.toString());
			}

		} catch (Exception e) {
			if (Preferences.DEBUG) Log.e(TAG, "getUri()", e);
		}
		return uri;
	}
	
	public static RequestParams getRequestParams(Bundle params){
		if (params == null) {
			return null;
		}
		
		RequestParams parameters=new RequestParams();
		for (String key : params.keySet()) {
			String value = params.getString(key);
			
			parameters.put(key, value);
		}
		
		return parameters;
	}
	
	public static String bundleToQuery(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) first = false; else sb.append("&");
			String value = parameters.getString(key);
			if (null != value) {
				sb.append(key + "=" + value);
			} else {
				long valueInt = parameters.getLong(key);
				sb.append(key + "=" + String.valueOf(valueInt));
			}
		}
		return sb.toString();
	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
}
