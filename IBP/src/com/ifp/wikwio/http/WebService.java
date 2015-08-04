package com.ifp.wikwio.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ifp.wikwio.Constants;
import com.ifp.wikwio.R;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.SharedPreferencesUtil;

public class WebService {
	private static final String TAG = "WebService";
	
	public interface ResponseHandler {
		public void onSuccess(String response);
		public void onFailure(Throwable e, String content);
	}
	
	public static void sendRequest(final Context context, String methodString, String actionString, Bundle paramsBundle, final ResponseHandler responseHandler) {
		
		AsyncHttpResponseHandler asyncHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response, Bundle headers) {
				
				try {
					JSONObject jsonObj = new JSONObject(response);
					response = jsonObj.getJSONObject("model").toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				appendLog(response);

				Log.d(TAG, "Response: "+response);
				if (response != null && !("null".equals(response))) {
					responseHandler.onSuccess(response);			
				}
				else {
					responseHandler.onFailure(new RuntimeException(), context.getResources().getString(R.string.error_uncaught));
				}
			}

			@Override
			public void onFailure(Throwable e, String content) {
				Log.d(TAG, "Error-Content: "+content);
				String msg = AppUtil.parseErrorResponse(context, content, e);
				responseHandler.onFailure(e, msg);
			}
		};
			
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader(Request.HEADER_APP_KEY, Constants.APP_KEY);
		client.addHeader(Request.HEADER_ACCEPT, Constants.APPLICATION_JSON);
		String user_key = SharedPreferencesUtil.getSharedPreferencesString(context, Request.HEADER_AUTH_KEY, null);
		if(user_key!=null) client.addHeader(Request.HEADER_AUTH_KEY, user_key);

		if(Request.METHOD_GET.equals(methodString)){
			String url = HttpUtils.getUri(actionString, paramsBundle).toString();
			Log.d(TAG, "URL: "+url);
			
			client.get(url, asyncHandler);
		}
		else if(Request.METHOD_POST.equals(methodString)){
			String url = HttpUtils.getUri(actionString, (Bundle) null).toString();
			Log.d(TAG, url);
			if(paramsBundle!=null) Log.d(TAG, "Params: "+paramsBundle.toString());
			client.post(url, HttpUtils.getRequestParams(paramsBundle), asyncHandler);
		}
		else if(Request.METHOD_PUT.equals(methodString)){
			String url = HttpUtils.getUri(actionString, paramsBundle).toString();
			Log.d(TAG, url);
			if(paramsBundle!=null) Log.d(TAG, "Params: "+paramsBundle.toString());
			client.put(url, HttpUtils.getRequestParams(paramsBundle), asyncHandler);
		}
		else if(Request.METHOD_DELETE.equals(methodString)){
			String url = HttpUtils.getUri(actionString, (Bundle) null).toString();
			Log.d(TAG, url);
			if(paramsBundle!=null) Log.d(TAG, "Params: "+paramsBundle.toString());
			client.delete(url, asyncHandler);
		}
	}

	public static void appendLog(String text)
		{
		    File logFile = new File("sdcard/log.txt");
		    if (!logFile.exists())
		    {
		        try
		        {
		            logFile.createNewFile();
		        } catch (IOException e)
		        {
		            e.printStackTrace();
		        }
		    }
		    try
		    {
		        // BufferedWriter for performance, true to set append to file flag
		        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
		        buf.append(text);
		        buf.newLine();
		        buf.close();
		    } catch (IOException e)
		    {
		        e.printStackTrace();
		    }
		}

	public static void sendMultiPartRequest(final Context context, String methodString, String actionString, final ResponseHandler responseHandler, MultipartEntity reqEntity) {
		
		AsyncHttpResponseHandler asyncHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response, Bundle headers) {
				Log.d(TAG, "Response: "+response);

				if (response != null && !("null".equals(response))) {
					responseHandler.onSuccess(response);			
				}
				else {
					responseHandler.onFailure(new RuntimeException(), context.getResources().getString(R.string.error_uncaught));
				}
			}

			@Override
			public void onFailure(Throwable e, String content) {
				Log.d(TAG, "Error-Content: "+content);/*.substring(0, 9683));
				String content1 = content.substring(0, 9683);
				String content2 = content.substring(9684, 19367);
				String content3 = content.substring(19368, 29050);
				String content4 = content.substring(29051);*/
				String msg = AppUtil.parseErrorResponse(context, content, e);
				responseHandler.onFailure(e, msg);
			}
		};
		
		AsyncHttpClient client = new AsyncHttpClient();
		String user_key = SharedPreferencesUtil.getSharedPreferencesString(context, Request.HEADER_AUTH_KEY, null);
		if(user_key!=null) client.addHeader(Request.HEADER_AUTH_KEY, user_key);
		client.addHeader(Request.HEADER_APP_KEY, Constants.APP_KEY);
		client.addHeader(Request.HEADER_ACCEPT, Constants.APPLICATION_JSON);
		
		if(Request.METHOD_GET.equals(methodString)){
			String url = HttpUtils.getUri(actionString, null).toString();
			Log.d(TAG, url);
			client.get(url, 
					asyncHandler);
		}
		else if(Request.METHOD_POST.equals(methodString)){
			String token = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.APP_TOKEN, null);
			if(token!=null) client.addHeader(Request.HEADER_AUTH_KEY, token);
			String url = HttpUtils.getUri(actionString, null).toString();
			Log.d(TAG, url);
			client.post(context, url, (HttpEntity)reqEntity, null, asyncHandler);
		
		}
		else if(Request.METHOD_PUT.equals(methodString)){
			String url = HttpUtils.getUri(actionString, null).toString();
			Log.d(TAG, url);
			client.put(context, url, (HttpEntity)reqEntity, null, asyncHandler);
		
		}
	}

	public static void sendRequestWithBaseUrl(final Context context, final String base_url, String methodString, String actionString, final int port, Bundle paramsBundle, final ResponseHandler responseHandler) {

        AsyncHttpResponseHandler asyncHandler = new AsyncHttpResponseHandler() {
        	@Override
			public void onSuccess(String response, Bundle headers) {
        		Log.d(TAG, "Response: "+response);

                if (response != null && !("null".equals(response))) {
                    responseHandler.onSuccess(response);
                }
                else {
                    responseHandler.onFailure(new RuntimeException(), context.getResources().getString(R.string.error_unknown_error));
                }
			}

			@Override
			public void onFailure(Throwable e, String content) {
				Log.d(TAG, "Error-Content: "+content);
                String msg = null;
                if(e instanceof ConnectException || e instanceof UnknownHostException)
                	context.getResources().getString(R.string.not_connected_text);

                	responseHandler.onFailure(e, msg);
			}
        };

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(Request.HEADER_APP_KEY, Constants.APP_KEY);
        client.addHeader(Request.HEADER_ACCEPT, Constants.APPLICATION_JSON);
		
        if(Request.METHOD_GET.equals(methodString)){
            String url = HttpUtils.getUri(base_url, actionString, port, paramsBundle, false).toString().replaceAll("%25", "%");

            Log.d(TAG,url);
            client.get(url, asyncHandler);
        }
        else if(Request.METHOD_POST.equals(methodString)){
        	String url = HttpUtils.getUri(base_url, actionString, port,(Bundle) null, false).toString().replaceAll("%25", "%");
        	Log.d(TAG,url);
            client.post(url,
                    HttpUtils.getRequestParams(paramsBundle),
                    asyncHandler);
        }
        else if(Request.METHOD_PUT.equals(methodString)){
        	String url = HttpUtils.getUri(base_url, actionString, port,(Bundle) null, false).toString().replaceAll("%25", "%");
            client.put(url,
                    HttpUtils.getRequestParams(paramsBundle),
                    asyncHandler);
        }
    }

	
}
