package com.ifp.wikwio;

import java.sql.SQLException;

import com.ifp.wikwio.database.ObservationInstanceTable;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.SharedPreferencesUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	public boolean iswificonnection=false;
	NetworkInfo ni;
	//String state="";
	//String state1="1";
	@SuppressWarnings("deprecation")
	public void onReceive(Context context, Intent intent) {
		Log.d("app","Network connectivity change");
		Log.d("NetWork Receiver33",""+intent.getExtras());
		if(intent.getExtras()!=null) {
			 ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
			iswificonnection = SharedPreferencesUtil.getSharedPreferencesBoolean(context, Constants.WIFI_SUBMIT, false);
			Boolean button = SharedPreferencesUtil.getSharedPreferencesBoolean(context, Constants.M_UPLOAD, false);
			//state = intent.getExtras().getString("button");
			Log.d("NetWork Receiver33",""+button);
			Log.d("NetWork Receiver33",""+iswificonnection);
			 if(button == false && iswificonnection==true){
				if(AppUtil.isWIFINetworkAvailable(context)){
					Log.d("NetWork Receiver33","ft");
					methodToExecute(context,intent);
				}
			}else if(button == false && iswificonnection==false){
				Log.d("NetWork Receiver33","ff");
				methodToExecute(context,intent);
				
			}else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
						if(Preferences.DEBUG) Log.d("NetWork Receiver","*****There's no network connectivity");
						try {
							if(Preferences.DEBUG) Log.d("NetWork Receiver", "No. of records in database"+ObservationInstanceTable.getNoOfRowsInTable(context));
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
		}
	}



	private void methodToExecute(Context context,Intent intent){

		if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
						if(Preferences.DEBUG) Log.d("NetWork Receiver","*****Network connected");
						ObservationRequestQueue queue = ObservationRequestQueue.getInstance();
						queue.executeAllSubmitRequests(context);

					
					} 

	}	
}