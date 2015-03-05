package com.mobisys.android.ibp;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mobisys.android.ibp.database.CategoriesTable;
import com.mobisys.android.ibp.database.ObservationInstanceTable;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.Category;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends BaseSlidingActivity implements ConnectionCallbacks, OnConnectionFailedListener{

	boolean mLocationFetched=false;
	private ArrayList<Category> mCategoryList;
	private Dialog mPg;
	private LocationRequest mLocationRequest;
	private GoogleApiClient mGoogleApiClient;
	private boolean mBrowseObservationPressed = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		initActionTitle(getString(R.string.home));
		initScreen();
		buildGoogleApiClient();
	}
	
	 @Override
	 protected void onResume(){
		 registerReceiver(mBroadcastReceiver, new IntentFilter("com.mobisys.android.ibp.check_incomplete_obs"));
		 checkInCompleteObservations();
	     super.onResume();
	 }
	 
	 @Override
	 protected void onPause(){
		 unregisterReceiver(mBroadcastReceiver);
	     super.onPause();
	 }
	
	 @Override
	 public void onDestroy(){
		 super.onDestroy();
	 }
	 
	 @Override
	 protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
	 }
	 
	 @Override
	 protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
	 }
	 public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				checkInCompleteObservations();
			}	
	 };
	 
	private void initScreen() {
		mCategoryList=(ArrayList<Category>) CategoriesTable.getAllCategories(HomeActivity.this);
		Log.d("HomeActivity", "DB No. of Categories: at start"+mCategoryList.size());
		if(AppUtil.isNetworkAvailable(HomeActivity.this)){
			if(mCategoryList==null || mCategoryList.size()==0)
				mPg= ProgressDialog.show(HomeActivity.this,getString(R.string.loading_species_group));
			getSpeciesCategories();
		}
		findViewById(R.id.btn_browse_observation).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(!mLocationFetched) shallWeShowLastLocationDialog();
				else showCategoryDialog();*/
				
				Intent i=new Intent(HomeActivity.this, ObservationActivity.class);
				i.putExtra(Constants.GROUP_ID, Long.valueOf(-1));//mCategoryList.get(which).getId());
				i.putExtra(Constants.SHOW_ALL, true);
				startActivity(i);
				
				/*if(!mLocationFetching) showCategoryDialog();
				else if(mLocationFetching){
					//Toast.makeText(getApplicationContext(), getString(R.string.wait_fetch_location), Toast.LENGTH_SHORT).show();
					final ArrayList<String> categoryListStr=new ArrayList<String>();
					if(mCategoryList!=null && mCategoryList.size()>0){
						for(int i=0;i<mCategoryList.size();i++){
							categoryListStr.add(mCategoryList.get(i).getName());
						}
					}
					Intent i=new Intent(HomeActivity.this, ObservationActivity.class);
					i.putExtra(Constants.GROUP_ID, mCategoryList.get(0).getId());
					i.putExtra(Constants.LOCATION_NOT_FETCHED, true);
					startActivity(i);
				}*/
			}
		});
		
		findViewById(R.id.btn_new_observation).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, NewObservationActivity.class);
				startActivity(i);
			}
		});
		
		findViewById(R.id.btn_incomplete_observations).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, ObservationStatusActivity.class);
				startActivity(i);
			}
		});
	}

	private void checkInCompleteObservations() {
		long incomplete_observations=ObservationInstanceTable.getNoOfIncompleteObservations(HomeActivity.this);
		((TextView)findViewById(R.id.msg_incomplete_obs)).setText(String.format(getString(R.string.msg_incomplete_obs), incomplete_observations));
		
		if(incomplete_observations>0){
			findViewById(R.id.msg_incomplete_obs).setVisibility(View.VISIBLE);
			findViewById(R.id.view01).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_incomplete_observations).setVisibility(View.VISIBLE);
		}
		else{
			findViewById(R.id.msg_incomplete_obs).setVisibility(View.GONE);
			findViewById(R.id.view01).setVisibility(View.GONE);
			findViewById(R.id.btn_incomplete_observations).setVisibility(View.GONE);
		}
	}

	protected void showCategoryDialog() {
		final ArrayList<String> categoryListStr=new ArrayList<String>();
		if(mCategoryList!=null && mCategoryList.size()>0){
			for(int i=0;i<mCategoryList.size();i++){
				categoryListStr.add(mCategoryList.get(i).getName());
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, categoryListStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.obeservations_near_me);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i=new Intent(HomeActivity.this, ObservationActivity.class);
				i.putExtra(Constants.GROUP_ID, mCategoryList.get(which).getId());
				i.putExtra(Constants.SHOW_ALL, true);
				startActivity(i);
			}
		});
	
        AlertDialog alert = builder.create();
        alert.show();  
	}

	private void getSpeciesCategories() {
		WebService.sendRequest(HomeActivity.this, Request.METHOD_GET, Request.PATH_SPECIES_CATEGORIES, null, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseSpeciesCategories(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				Log.e("HomeActivity", e.toString());
			}
		});
	}
	
	protected void parseSpeciesCategories(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			response = jsonObject.getJSONArray("instanceList").toString();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mCategoryList=mapper.readValue(response, new TypeReference<ArrayList<Category>>(){});
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			if(mCategoryList!=null && mCategoryList.size()>0){
				for(int i=0;i<mCategoryList.size();i++)
					CategoriesTable.createOrUpdateCategory(HomeActivity.this, mCategoryList.get(i));
			}			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
	}
	
	protected void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
	}

	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			mLocationFetched=true;
			stopLocationUpdates();
			AppUtil.saveCurrentLocation(HomeActivity.this, location);
			if(mBrowseObservationPressed) showCategoryDialog();
		}
	};
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(location!=null) AppUtil.saveCurrentLocation(this, location);
		createLocationRequest();
		startLocationUpdates();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(!mLocationFetched){
					mLocationFetched=true;
					Toast.makeText(getApplicationContext(), getString(R.string.cannot_get_current_location), Toast.LENGTH_SHORT).show();
				}
			}
		}, 30*1000);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		
	}

	private void shallWeShowLastLocationDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setMessage("Please wait while we fetch current location. Otherwise, you can view observations near your last known location?");
		alertDialog.setTitle("Location not fetched");
		alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				showCategoryDialog();
			}
		});
		alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				mBrowseObservationPressed = true;
			}
		});
		alertDialog.show();
	}
}
