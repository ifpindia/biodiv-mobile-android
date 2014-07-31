package com.mobisys.android.ibp.http;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobisys.android.ibp.Constants;
import com.mobisys.android.ibp.R;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;

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
		
		if(Request.METHOD_GET.equals(methodString)){
			/*String user_key = SharedPreferencesUtil.getSharedPreferencesString(context, Request.HEADER_AUTH_KEY, null);
			if(user_key!=null) client.addHeader(Request.HEADER_AUTH_KEY, user_key);*/
			String url = HttpUtils.getUri(actionString, paramsBundle).toString();
			Log.d(TAG, "URL: "+url);
			
			client.get(url, asyncHandler);
		}
		else if(Request.METHOD_POST.equals(methodString)){
			String token = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.APP_TOKEN, null);
			if(token!=null) client.addHeader(Request.HEADER_AUTH_KEY, token);
			String url = HttpUtils.getUri(actionString, (Bundle) null).toString();
			Log.d(TAG, url);
			if(paramsBundle!=null) Log.d(TAG, "Params: "+paramsBundle.toString());
			client.post(url, HttpUtils.getRequestParams(paramsBundle), asyncHandler);
		}
		else if(Request.METHOD_PUT.equals(methodString)){
			/*if(!actionString.contains(Request.PATH_LOGIN)){
				String user_key = SharedPreferencesUtil.getSharedPreferencesString(context, Request.HEADER_AUTH_KEY, null);
				if(user_key!=null) client.addHeader(Request.HEADER_AUTH_KEY, user_key);
			}*/
			String url = HttpUtils.getUri(actionString, (Bundle) null).toString();
			Log.d(TAG, url);
			if(paramsBundle!=null) Log.d(TAG, "Params: "+paramsBundle.toString());
			client.put(url, HttpUtils.getRequestParams(paramsBundle), asyncHandler);
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
