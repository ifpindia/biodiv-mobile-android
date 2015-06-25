package com.ifp.wikwio;

import java.sql.SQLException;

import com.ifp.wikwio.database.ObservationInstanceTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	
    @SuppressWarnings("deprecation")
	public void onReceive(Context context, Intent intent) {
	     Log.d("app","Network connectivity change");
	     if(intent.getExtras()!=null) {
	        NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	        if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
	            if(Preferences.DEBUG) Log.d("NetWork Receiver","*****Network connected");
	            ObservationRequestQueue queue = ObservationRequestQueue.getInstance();
	            queue.executeAllSubmitRequests(context);

	            /*Log.d("NetWork Receiver1",""+ObservationRequestQueue.notificationStatus);
	            if(ObservationRequestQueue.notificationStatus == true){
	            	NewObservationActivity ne =new NewObservationActivity();
	            	Log.d("NetWork Receiver2","*****Network connected");
	            	ne.StatusBarNotification();
	            }*/
	        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
	        	if(Preferences.DEBUG) Log.d("NetWork Receiver","*****There's no network connectivity");
	            try {
	            	if(Preferences.DEBUG) Log.d("NetWork Receiver", "No. of records in database"+ObservationInstanceTable.getNoOfRowsInTable(context));
	    		} catch (SQLException e1) {
	    			e1.printStackTrace();
	    		}
	        }
	   }
	}	
}