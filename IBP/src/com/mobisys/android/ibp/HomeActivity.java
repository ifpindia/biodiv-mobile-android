package com.mobisys.android.ibp;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.ibp.database.CategoriesTable;
import com.mobisys.android.ibp.database.ObservationInstanceTable;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.Category;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;
import com.mobisys.android.ibp.widget.MyLocation;


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

public class HomeActivity extends BaseSlidingActivity /*implements ConnectionCallbacks, OnConnectionFailedListener*/{

	boolean mLocationFetched=false;
	private ArrayList<Category> mCategoryList;
	private Dialog mPg;
//	private boolean mBrowseObservationPressed = false;
	private MyLocation mLocation;
	private Location mCurrentLocation;
	private double mLat=0.0d, mLng=0.0d;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		initActionTitle(getString(R.string.home));
		initScreen();
		setUpLocation();
		//buildGoogleApiClient();
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
		 if(mLocation!=null)mLocation.stopLocationUpdates();
	 }
	 
	 @Override
	 protected void onStart() {
        super.onStart();
       // mGoogleApiClient.connect();
	 }
	 
	 @Override
	 protected void onStop() {
        super.onStop();
        //stopLocationUpdates();
        //if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
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
			getUserJoinedGroups();
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

	private void getUserJoinedGroups() {
		String id=SharedPreferencesUtil.getSharedPreferencesString(HomeActivity.this, Constants.USER_ID, "-1");
		Bundle b=new Bundle();
		b.putString(Request.PARAM_ID, id);
		b.putString(Request.LIMIT, String.valueOf(50));
		WebService.sendRequest(HomeActivity.this, Request.METHOD_GET, Request.PATH_GET_USER_GROUPS,b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseUserResponse(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				AppUtil.showErrorDialog(content, HomeActivity.this);
			}
		});
	}

	protected void parseUserResponse(String response) {
		try {
			JSONObject jobj=new JSONObject(response);
			String groupList=jobj.getJSONArray("observations").toString();
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.JOINED_GROUPS_JSON, groupList);
			//ArrayList<MyUserGroup> myList=mapper.readValue(groupList, new TypeReference<ArrayList<MyUserGroup>>(){});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/*catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}
	
	/*private void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}*/
	
	/*protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
	}
	
	protected void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
	}*/

	/*protected synchronized void buildGoogleApiClient() {
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
		
	}*/

	private void setUpLocation(){

        mLocation = new MyLocation(HomeActivity.this, new MyLocation.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(AppUtil.isNetworkAvailable(HomeActivity.this) && (mPg!=null && mPg.isShowing()))mPg.dismiss();
                if(location!=null){
                    mCurrentLocation = location;
                    mLat=mCurrentLocation.getLatitude();
                    mLng=mCurrentLocation.getLongitude();

                    /*if(AppUtil.isNetworkAvailable(HomeActivity.this) && (location!=null && !mIsInitScreen)){
                        getDashboardDetail();
                        mIsInitScreen=true;
                    }*/
                    SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LAT, String.valueOf(mCurrentLocation.getLatitude()));
                    SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LNG, String.valueOf(mCurrentLocation.getLongitude()));
                    //BusProvider.getBusInstance().post(mCurrentLocation);
                }
            }
        });

        mLocation.setMinimumDisplacement(100);
        if(mLocation.isLocationEnabled()){
            //if(AppUtil.isNetworkAvailable(this)&&mLat==0.0) mPg = ProgressDialog.show(this, "Fetching Location");
            mLocation.startLocationUpdates();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mPg!=null && mPg.isShowing()) mPg.dismiss();
                    if(mCurrentLocation==null) mCurrentLocation = AppUtil.getCurrentLocation(HomeActivity.this);

                    if (mCurrentLocation!=null){
                        mLat = mCurrentLocation.getLatitude();
                        mLng = mCurrentLocation.getLongitude();
                       // BusProvider.getBusInstance().post(mCurrentLocation);
                        SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LAT, String.valueOf(mLat));
                        SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LNG, String.valueOf(mLng));
                    } else {
                        SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LAT, Constants.DEFAULT_LAT);
                        SharedPreferencesUtil.putSharedPreferencesString(HomeActivity.this, Constants.LNG, Constants.DEFAULT_LAT);
                    }

                    /*if(AppUtil.isNetworkAvailable(DashboardActivity.this) && !mIsInitScreen){
                        getDashboardDetail();
                        mIsInitScreen=true;
                    }*/
                }
            }, 30000);
        }
        else {
        	alertForGPSNotEnabled();
        }
    }

	private void alertForGPSNotEnabled() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setMessage("Please enable location settings.");
        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	finish();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	finish();
            }
        });
        dialog.show();
	}
	/*private void shallWeShowLastLocationDialog(){
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
	}*/
}
