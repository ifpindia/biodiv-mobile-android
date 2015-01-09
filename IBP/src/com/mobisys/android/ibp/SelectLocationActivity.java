package com.mobisys.android.ibp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.HttpRetriever;
import com.mobisys.android.ibp.utils.ReveseGeoCodeUtil;
import com.mobisys.android.ibp.utils.ReveseGeoCodeUtil.ReveseGeoCodeListener;

@SuppressLint("HandlerLeak")
public class SelectLocationActivity extends ActionBarActivity{

	private double mLng,mLat;
	private boolean mIsReverseGeocodingSuccess;
	private GoogleMap mMap;
	private CameraPosition mCameraPos;
	private TextView mAddress;
	
	private HttpRetriever mHttpRetriever;
	private String mStrAddress;
	private static final int REQUEST_ADDRESS = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sighting_map_screen);
		getSupportActionBar().hide();
		Location location = AppUtil.getCurrentLocation(this);
		mLat = location.getLatitude();
		mLng = location.getLongitude();
		initScreen();
	}

	private void initScreen() {
		mAddress = (TextView)findViewById(R.id.address);
		mHttpRetriever = new HttpRetriever();
		setUpMapIfNeeded(this);
		
		((Button)findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStrAddress.contains("India") || !mIsReverseGeocodingSuccess){
					Log.d("SelectLocation", "Address: "+mStrAddress+" Latitude: "+mLat+" Longitude:"+mLng);
					Intent resultdata = new Intent();
					resultdata.putExtra(Constants.ADDRESS, mStrAddress);
					resultdata.putExtra(Constants.LNG, mLng);
					resultdata.putExtra(Constants.LAT, mLat);
					setResult(RESULT_OK,resultdata);
					finish();
				}
				else{
					AppUtil.showDialog(getString(R.string.bound_error), SelectLocationActivity.this);
				}
			}
		});
	}

	private void setUpMapIfNeeded(Context ctx) {
		if (mMap == null) {
	        // Try to obtain the map from the SupportMapFragment.
	        mMap = ((com.androidmapsextensions.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getExtendedMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            setUpMap(ctx);
	        }
	    }
	}
	
	private void setUpMap(final Context ctx) {
       if(mLat!=0 && mLng!=0){
			LatLng lat_lng = new LatLng(mLat,mLng);
			mCameraPos = (new CameraPosition.Builder()).target(lat_lng).zoom(13).build();	
       } else {
   		   LatLng lat_lng = new LatLng(mLat,mLng);
   		   mCameraPos = (new CameraPosition.Builder()).target(lat_lng).zoom(13).build();
       }
       
       if(mCameraPos!=null){
			mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
				
				@Override
				public void onCameraChange(CameraPosition cameraPosition) {
					mMap.setOnCameraChangeListener(mRequestListener);
					if(Preferences.DEBUG) Log.d("SightingMapActivity", "Lat: "+mCameraPos.target.latitude+" Lng: "+mCameraPos.target.longitude);
					//mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPos));
					mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraPos.target.latitude,mCameraPos.target.longitude) , 13));
				}
			});
		
		}
	}

	private GoogleMap.OnCameraChangeListener mRequestListener=new GoogleMap.OnCameraChangeListener() {

		@Override
		public void onCameraChange(CameraPosition cameraPos) {
			if(Preferences.DEBUG) Log.d("QuestaMapActivity", "********changing Location to "+"Lat: "+cameraPos.target.latitude+" Lng: "+cameraPos.target.longitude);
			Message msg = Message.obtain(mRequestHandler, REQUEST_ADDRESS);
			mRequestHandler.removeMessages(REQUEST_ADDRESS);
			mRequestHandler.sendMessageDelayed(msg, 1000);
		}
	};
	
	Handler mRequestHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==REQUEST_ADDRESS){
        		mCameraPos=mMap.getCameraPosition();
        		mAddress.setText(getResources().getString(R.string.loading));
        		doReverseGeocoding(mCameraPos.target.latitude, mCameraPos.target.longitude);
            }
        }
    };
    
    private void doReverseGeocoding(double latitude, double longitude) {
        ReveseGeoCodeUtil.doReverseGeoCoding(SelectLocationActivity.this, latitude, longitude, mHttpRetriever, new ReveseGeoCodeListener() {
			
			@Override
			public void onReveseGeoCodeSuccess(boolean success, double lat, double lng, String address) {
				mIsReverseGeocodingSuccess=success;
				mLat=lat;
				mLng=lng;
				mStrAddress=address;
				mAddress.setText(address);
			}
		});
    }

}
