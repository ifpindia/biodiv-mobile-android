package com.mobisys.android.ibp.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mobisys.android.ibp.Constants;
import com.mobisys.android.ibp.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyLocation {
	public static final String GET_LOCATION = "get_location";
	public static final String LOCATION = "location";
	
    Timer timer1;
    LocationManager lm;
    boolean gps_enabled=false;
    boolean network_enabled=false;
    Context mContext;
    Intent mBroadcast;
    LocalBroadcastManager mBroadcastManager;
    LocationResult mLocationResult;
    
    public MyLocation(Context context, LocationResult locationResult){
    	mContext=context;
    	mBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    	mBroadcast = new Intent();
    	mBroadcast.setAction(MyLocation.GET_LOCATION);
    	mLocationResult=locationResult;
    }
    
    public boolean getLocation(long time_milis)
    {
        if(lm==null)
            lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1=new Timer();
        timer1.schedule(new UpdateLocation(), time_milis);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            if(location!=null){
            	if(Preferences.DEBUG)Log.d("Mylocation","**********lat: "+location.getLatitude()+" lng:"+location.getLongitude());
            	SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LAT, String.valueOf(location.getLatitude()));
        		SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LNG, String.valueOf(location.getLongitude()));
            }
            else{
            	location = getLastKnownLocation();
            	if(location==null) location=getStoredLocation(mContext);
            }
            if(mLocationResult!=null) mLocationResult.gotLocation(location);
            //sendBroadcast(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            lm.removeUpdates(locationListenerNetwork);
            lm.removeUpdates(locationListenerGps);
            
            if(location!=null){
            	if(Preferences.DEBUG)Log.d("Mylocation","**********lat: "+location.getLatitude()+" lng:"+location.getLongitude());
            	SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LAT, String.valueOf(location.getLatitude()));
        		SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LNG, String.valueOf(location.getLongitude()));
            }
            else{
            	location = getLastKnownLocation();
            	if(location==null) location=getStoredLocation(mContext);
            }
            if(mLocationResult!=null) mLocationResult.gotLocation(location);
            //sendBroadcast(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public Location getLastKnownLocation(){
        List<String> providers = lm.getProviders(false);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        
        if(providers!=null){
            for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
            }
        }
        
        return l;
    }
    
    class UpdateLocation extends TimerTask {
        @Override
        public void run() {
        	Activity activity = (Activity) mContext;
        	activity.runOnUiThread(new Runnable(){
				public void run() {
		            lm.removeUpdates(locationListenerNetwork);
		            lm.removeUpdates(locationListenerGps);
					Location location=getLastKnownLocation();
					
					if(location!=null){
		            	if(Preferences.DEBUG)Log.d("Mylocation","**********lat: "+location.getLatitude()+" lng:"+location.getLongitude());
		            	SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LAT, String.valueOf(location.getLatitude()));
		        		SharedPreferencesUtil.putSharedPreferencesString(mContext, Constants.LNG, String.valueOf(location.getLongitude()));
		            }
					else{
						location=getStoredLocation(mContext);
					}
					if(mLocationResult!=null) mLocationResult.gotLocation(location);
					//sendBroadcast(location);
				}
			});	
        }
    }

    public static Location getStoredLocation(Context context){
    	Location location = new Location("");
    	double lat = Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LAT, "0.0"));
    	double lng = Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LNG, "0.0"));
    	location.setLatitude(lat);
    	location.setLongitude(lng);
    	return location;
    }
   /* private void sendBroadcast(Location location){
    	if(location!=null)
    		mBroadcast.putExtra(MyLocation.LOCATION, location);
        mBroadcastManager.sendBroadcast(mBroadcast);
    }*/
    
    public static interface LocationResult{
        public void gotLocation(Location location);
    }
}
