package com.ifp.wikwio.widget;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation {
    //Timer timer1;
    private LocationManager lm;
    private boolean gps_enabled=false;
    private boolean network_enabled=false;
    private Context mContext;
    private long distance_in_meters = 0;
    private long interval_in_milis = 0;
    //Intent mBroadcast;
    //LocalBroadcastManager mBroadcastManager;
    private LocationListener mLocationListener;
    //public static final String GET_LOCATION = "get_location";
    //public static final String LOCATION = "location";
    
    public MyLocation(Context context, LocationListener locationListener){
    	mContext=context;
        if(lm==null)
            lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
        mLocationListener = locationListener;
    }

    public boolean isLocationEnabled(){
        return gps_enabled || network_enabled;
    }

    public void setMinimumDisplacement(long distanceInMeters){
        distance_in_meters = distanceInMeters;
    }

    public void setInterval(long intervalInMilis){
        interval_in_milis = intervalInMilis;
    }

    public boolean startLocationUpdates(){
        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval_in_milis, distance_in_meters, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval_in_milis, distance_in_meters, locationListenerNetwork);

        return !gps_enabled && !network_enabled;
    }

    public void stopLocationUpdates(){
        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);
    }
    /*public boolean getLocation()
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
        //timer1=new Timer();
        //timer1.schedule(new UpdateLocation(), 30000);
        return true;
    }*/

    android.location.LocationListener locationListenerGps = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            //timer1.cancel();
            //lm.removeUpdates(locationListenerGps);
            //lm.removeUpdates(locationListenerNetwork);
            if(mLocationListener !=null) mLocationListener.onLocationChanged(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            //timer1.cancel();
            //lm.removeUpdates(locationListenerNetwork);
            //lm.removeUpdates(locationListenerGps);
            if(mLocationListener !=null) mLocationListener.onLocationChanged(location);
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
    
    /*class UpdateLocation extends TimerTask {
        @Override
        public void run() {
        	Activity activity = (Activity) mContext;
        	activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
		            lm.removeUpdates(locationListenerNetwork);
		            lm.removeUpdates(locationListenerGps);
					Location location=getLastKnownLocation();
					if(mLocationResult!=null) mLocationResult.onLocationChanged(location);
				}
			});	
        }
    }*/

    public static interface LocationListener {
        public void onLocationChanged(Location location);
    }
}