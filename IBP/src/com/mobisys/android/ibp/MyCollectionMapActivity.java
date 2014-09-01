package com.mobisys.android.ibp;

import java.util.ArrayList;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.InfoWindowAdapter;
import com.androidmapsextensions.GoogleMap.OnInfoWindowClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.mobisys.android.ibp.models.ObservationInstance;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCollectionMapActivity extends ActionBarActivity{
	
	private ArrayList<ObservationInstance> mObsList;
	private GoogleMap mMap;
	private CameraPosition mCameraPos;
	boolean mISRefreshingInfoWindow=false;
	private View mRefreshedInfoView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collection_map);
		getSupportActionBar().hide();
		initScreen();
	}

	private void initScreen() {
		mObsList=getIntent().getParcelableArrayListExtra(ObservationInstance.OI);
		setUpMapIfNeeded(MyCollectionMapActivity.this);
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
		double lat=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(MyCollectionMapActivity.this, Constants.LAT, "0"));
		double lng=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(MyCollectionMapActivity.this, Constants.LNG, "0"));
		LatLng lat_lng ;
		if(Preferences.NEW_DEBUG){
			lat_lng = new LatLng(Double.valueOf(Constants.DEFAULT_LAT),Double.valueOf(Constants.DEFAULT_LNG));
		}
		else{
			lat_lng = new LatLng(lat,lng);
		}
		
		mCameraPos = (new CameraPosition.Builder()).target(lat_lng).zoom(13).build();
		if(mCameraPos!=null){
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPos));
		}
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				if(mISRefreshingInfoWindow){
					marker.hideInfoWindow();
					mISRefreshingInfoWindow=false;
					return mRefreshedInfoView;
				}
				else{
					View infoView = null;
					LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					infoView = inflater.inflate(R.layout.marker_info_dialog, null);
					showObservationInfoWindow(infoView, (ObservationInstance)marker.getData());
					return infoView;
				}
			}

		});
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent i=new Intent(MyCollectionMapActivity.this, ObservationDetailActivity.class);
				i.putExtra(ObservationInstance.ObsInstance, (ObservationInstance)marker.getData());
				startActivity(i);
			}
		});
		
		addMarkersToMap();
	}
	
	private void showObservationInfoWindow(final View infoView,final ObservationInstance oi) {
		if(oi!=null){
			//Log.d("Image", "*****Image url"+sighting.getImages().get(0).getThumb());						
		    //MImageLoader.displayImage(CollectionMapActivity.this,"http://"+HttpUtils.HOST+":"+HttpUtils.PORT+sighting.getImages().get(0).getThumb(),((ImageView)infoView.findViewById(R.id.sighting_image)),R.drawable.user_stub);
		    Picasso.with(MyCollectionMapActivity.this).load(oi.getThumbnail()).into((ImageView)infoView.findViewById(R.id.image), new Callback() {
				
				@Override
				public void onSuccess() {
					Log.d("PICASSO","Image loaded");
					Marker selectedMarker=mMap.getMarkerShowingInfoWindow();
					if(selectedMarker!=null&&selectedMarker.isInfoWindowShown()){
						selectedMarker.hideInfoWindow();
						mISRefreshingInfoWindow=true;
						LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						mRefreshedInfoView = inflater.inflate(R.layout.marker_info_dialog, null);
						showObservationInfoWindow(mRefreshedInfoView, oi);
						selectedMarker.showInfoWindow();
					}
				}
				
				@Override
				public void onError() {
					((ImageView)infoView.findViewById(R.id.image)).setImageResource(R.drawable.user_stub);
					Marker marker=mMap.getMarkerShowingInfoWindow();
					if(marker!=null&&marker.isInfoWindowShown()){
						marker.hideInfoWindow();
						mISRefreshingInfoWindow=true;
						mRefreshedInfoView=infoView;
						marker.showInfoWindow();
					}
				}
			});
		}
		if(oi.getMaxVotedReco()!=null){
				if(oi.getMaxVotedReco().getCommonName().length()>0)
					((TextView)infoView.findViewById(R.id.common_name)).setText(oi.getMaxVotedReco().getCommonName());
				else 
					((TextView)infoView.findViewById(R.id.common_name)).setText(R.string.unknown);
				((TextView)infoView.findViewById(R.id.sci_name)).setVisibility(View.VISIBLE);
				((TextView)infoView.findViewById(R.id.sci_name)).setText(oi.getMaxVotedReco().getScientificName());
			}
	}

	private void addMarkersToMap() {
		//BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_red);
		if(mObsList!=null){
			for(int i=0;i<mObsList.size();i++){//POINT (73.46 18.73)
				String point=mObsList.get(i).getTopology().substring(7, mObsList.get(i).getTopology().length()-1);
				String[] parts = point.split(" ");
				String part1 = parts[0]; 
				String part2 = parts[1];
				double lat=Double.valueOf(part2);
				double lng=Double.valueOf(part1);
				LatLng lat_lng = new LatLng(lat, lng);
				mMap.addMarker(new MarkerOptions().position(lat_lng).icon(icon).data(mObsList.get(i)));
			}
		}
	}
}
