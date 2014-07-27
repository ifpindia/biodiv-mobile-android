package com.mobisys.android.ibp;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.mobisys.android.ibp.database.CategoriesTable;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.Category;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.MyLocation;
import com.mobisys.android.ibp.utils.ProgressDialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class HomeActivity extends BaseSlidingActivity{

	boolean mLocationFetching=false;
	private double mLat, mLng;
	ArrayList<Category> mCategoryList;
	private Dialog mPg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		initActionTitle(getString(R.string.home));
		initScreen();
	}

	private void initScreen() {
		getCurrentLocation();
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
				if(!mLocationFetching) showCategoryDialog();
				else if(mLocationFetching){
					Toast.makeText(getApplicationContext(), getString(R.string.wait_fetch_location), Toast.LENGTH_SHORT).show();
				}
			}
		});
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
				Log.d("HomeActivity", "category selected: "+categoryListStr.get(which));
				Intent i=new Intent(HomeActivity.this, ObservationActivity.class);
				i.putExtra(Constants.GROUP_ID, mCategoryList.get(which).getId());
				startActivity(i);
			}
		});
	
        AlertDialog alert = builder.create();
        alert.show();  
	}

	private void getCurrentLocation(){
		mLocationFetching = true;
		new MyLocation(this, new MyLocation.LocationResult() {
			
			@Override
			public void gotLocation(Location location) {
				if(location!=null){
					mLocationFetching=false;
					mLat = location.getLatitude();
					mLng = location.getLongitude();
					
				}			
			}
		}).getLocation(10000);
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
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mCategoryList=mapper.readValue(response, new TypeReference<ArrayList<Category>>(){});
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			Log.d("HomeActivity", "After Parse: No. of Categories: "+mCategoryList.size());
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
		}
	}

}
