package com.ifp.wikwio.utils;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.ifp.wikwio.R;

public class ReveseGeoCodeUtil {
	public static interface ReveseGeoCodeListener {
		public void onReveseGeoCodeSuccess(boolean success, double lat, double lng, String address);
	}
	
	@SuppressWarnings("unchecked")
	public static void doReverseGeoCoding(Context ctx, double lat, double lng,final HttpRetriever httpRetriver, final ReveseGeoCodeListener listener){
		//((TextView)findViewById(R.id.address)).setText(getResources().getString(R.string.loading));
    	Hashtable<String, String> hash=new Hashtable<String, String>();
    	hash.put("lat", String.valueOf(lat));
    	hash.put("lng", String.valueOf(lng));
    	
        (new ReverseGeocodingTask(httpRetriver, listener, ctx)).execute(hash);
	}
	
	public static class ReverseGeocodingTask extends AsyncTask<Hashtable<String,String>, Void, Void> {
		private HttpRetriever httpRetriver; 
		private ReveseGeoCodeListener listener;
		private Context ctx;
	    public ReverseGeocodingTask(HttpRetriever httpRetriver, ReveseGeoCodeListener listener, Context ctx) {
	        super();
	        this.httpRetriver=httpRetriver;
	        this.listener=listener;
	        this.ctx=ctx;
	    }

	    @Override
	    protected Void doInBackground(Hashtable<String, String>... params) {
	       final Hashtable<String, String> ht=params[0];
	    	
           String addressText = AppUtil.getAddressFromGPSData(Double.valueOf(ht.get("lat")), Double.valueOf(ht.get("lng")), httpRetriver);
           final String strAddress = addressText==null?"":addressText;
           final double lat = Double.valueOf(ht.get("lat"));
           final double lng = Double.valueOf(ht.get("lng"));
            ((Activity)ctx).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(strAddress.length()>0)
						listener.onReveseGeoCodeSuccess(true, lat, lng, strAddress);
	            	else {
	            		String defaultAddress = String.format(ctx.getResources().getString(R.string.reverse_lookup_error_address), lat, lng);
	            		listener.onReveseGeoCodeSuccess(false, lat, lng, defaultAddress);
	            	}
				}
			});
	        return null;
	    }
	}
}
