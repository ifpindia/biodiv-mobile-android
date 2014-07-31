package com.mobisys.android.ibp;

import java.sql.SQLException;

import com.mobisys.android.ibp.database.ObservationParamsTable;

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
	        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
	        	if(Preferences.DEBUG) Log.d("NetWork Receiver","*****There's no network connectivity");
	            try {
	            	if(Preferences.DEBUG) Log.d("NetWork Receiver", "No. of records in database"+ObservationParamsTable.getNoOfRowsInTable(context));
	    		} catch (SQLException e1) {
	    			e1.printStackTrace();
	    		}
	        }
	   }
	}	
}