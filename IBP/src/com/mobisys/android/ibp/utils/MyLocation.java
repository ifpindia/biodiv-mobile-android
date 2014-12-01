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
	
	private static final int ONE_MINUTE = 60 * 1000;
    
	private Timer timer1;
    private LocationManager lm;
    private boolean gps_enabled=false;
    private boolean network_enabled=false;
    private Context mContext;
    private LocationResult mLocationResult;
    private long mLocationRequestedAt;
    private Location mCurrentLocation;
    private boolean mLocationFound = false;
    
    public MyLocation(Context context, LocationResult locationResult){
    	mContext=context;
    	mLocationResult=locationResult;
    	mCurrentLocation = getSavedLocation(mContext);
    }
    
    public boolean getLocation(long time_milis)
    {
        if(lm==null)
            lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if(!gps_enabled && !network_enabled)
            return false;

        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        
        mLocationRequestedAt = System.currentTimeMillis();
        timer1=new Timer();
        timer1.schedule(new UpdateLocation(), time_milis);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
        	handleNewLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
        	handleNewLocation(location);
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
        
        if(l==null) l=getSavedLocation(mContext);
        return l;
    }
    
    public void stopGetLocation(){
    	timer1.cancel();
        lm.removeUpdates(locationListenerNetwork);
        lm.removeUpdates(locationListenerGps);
    }
    
    private void handleNewLocation(Location location){
    	if(location == null){
        	location = getLastKnownLocation();
    	}
    	
    	if(isBetterLocation(location, mCurrentLocation)){
    		if(Preferences.DEBUG)Log.d("Mylocation","Found location ==> lat: "+location.getLatitude()+" lng:"+location.getLongitude());
    		mLocationFound = true;
    		mCurrentLocation=location;
    		saveLocation(mContext, location);
    		if(mLocationResult!=null) mLocationResult.gotLocation(location);
    	}
    	
    	if (locationIsGood(mCurrentLocation)) {
    		stopGetLocation();
        }

        if (locationRequestIsOld() && locationIsGoodEnough(mCurrentLocation)) {
        	stopGetLocation();
        }
    }

    class UpdateLocation extends TimerTask {
        @Override
        public void run() {
        	Activity activity = (Activity) mContext;
        	activity.runOnUiThread(new Runnable(){
				public void run() {
					stopGetLocation();
					if(!mLocationFound){
						Location location=getLastKnownLocation();
						
						if(location!=null){
			            	if(Preferences.DEBUG)Log.d("Mylocation","Time out ==> lat: "+location.getLatitude()+" lng:"+location.getLongitude());
			            	saveLocation(mContext, location);
			            }
						
						if(mLocationResult!=null) mLocationResult.gotLocation(location);
					}
				}
			});	
        }
    }

	public static void saveLocation(Context context, Location location) {
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.ALTITUDE, String.valueOf(location.getAltitude()));
		SharedPreferencesUtil.putSharedPreferencesLong(context, Constants.TIME, location.getTime());
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.PROVIDER, location.getProvider());
		SharedPreferencesUtil.putSharedPreferencesFloat(context, Constants.BEARING, location.getBearing());
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LAT, String.valueOf(location.getLatitude()));
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LNG, String.valueOf(location.getLongitude()));
		SharedPreferencesUtil.putSharedPreferencesFloat(context, Constants.ACCURACY, location.getAccuracy());
		SharedPreferencesUtil.putSharedPreferencesFloat(context, Constants.SPEED, location.getSpeed());
	}
	
	public static Location getSavedLocation(Context context){
		String provider = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.PROVIDER, null);
		if(provider!=null){
			Location location = new Location(provider);
			double altitude = Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.ALTITUDE, null));
			double lat = Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LAT, null));
			double lng = Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LNG, null));
			long time = SharedPreferencesUtil.getSharedPreferencesLong(context, Constants.TIME, 0);
			float bearing = SharedPreferencesUtil.getSharedPreferencesFloat(context, Constants.BEARING, 0);
			float accuracy = SharedPreferencesUtil.getSharedPreferencesFloat(context, Constants.ACCURACY, 0);
			float speed = SharedPreferencesUtil.getSharedPreferencesFloat(context, Constants.SPEED, 0);
			location.setAltitude(altitude);
			location.setLatitude(lat);
			location.setLongitude(lng);
			location.setBearing(bearing);
			location.setAccuracy(accuracy);
			location.setTime(time);
			location.setSpeed(speed);
			return location;
		}
		
		return null;
	}
    
    public static interface LocationResult{
        public void gotLocation(Location location);
    }
    
	public static double getLatitude(Context context){
    	return Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LAT, "0.0"));
	}
	
	public static void saveLatitude(Context context, double latitude){
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LAT, String.valueOf(latitude));
	}
	
	public static double getLongitude(Context context){
		return Double.parseDouble(SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LNG, "0.0"));
	}
	
	public static void saveLongitude(Context context, double longitude){
		SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LNG, String.valueOf(longitude));
	}

	private boolean locationRequestIsOld() {
        long delta = System.currentTimeMillis() - mLocationRequestedAt;
        return delta > ONE_MINUTE;
    }

    private boolean isBetterLocation(Location newLocation, Location currentLocation) {
        if (currentLocation == null) {
            return true;
        }
        if (newLocation.hasAccuracy() && !currentLocation.hasAccuracy()) {
            return true;
        }
        if (!newLocation.hasAccuracy() && currentLocation.hasAccuracy()) {
            return false;
        }
        return newLocation.getAccuracy() < currentLocation.getAccuracy();
    }

    private boolean locationIsGood(Location location) {
        if (!locationIsGoodEnough(location)) { return false; }
        if (location.getAccuracy() <= 10) {
            return true;
        }
        return false;
    }

    private boolean locationIsGoodEnough(Location location) {
        if (location == null || !location.hasAccuracy()) { return false; }
        if (location.getAccuracy() <= 500) { return true; }
        return false;
    }
}
