package com.ifp.wikwio;


import java.util.ArrayList;

import com.facebook.Session;
import com.ifp.wikwio.R;
import com.ifp.wikwio.database.CategoriesTable;
import com.ifp.wikwio.database.ObservationInstanceTable;
import com.ifp.wikwio.http.Request;
import com.ifp.wikwio.http.WebService;
import com.ifp.wikwio.http.WebService.ResponseHandler;
import com.ifp.wikwio.models.Category;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.ProgressDialog;
import com.ifp.wikwio.utils.SharedPreferencesUtil;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BaseSlidingActivity extends SlidingActivity{
	private Dialog mPg;
	public boolean isMyCollection=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.menu_layout);
		initActionBar();
		initSlidingMenu();
	}
	
	public void initActionTitle(String str){
		((TextView)findViewById(R.id.title)).setText(str);
	}
	
	private void initActionBar() {
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.app_actionbar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		findViewById(R.id.btn_menu).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSlidingMenu().toggle();
			}
		});
	}

	private void initSlidingMenu() {
		getSlidingMenu().setBehindOffset(AppUtil.getDipValue(50, this));
		initMenuIndicators();
		
		getSlidingMenu().findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!(BaseSlidingActivity.this instanceof HomeActivity)){
					Intent intent = new Intent(BaseSlidingActivity.this,HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else{
					getSlidingMenu().toggle();
				}
			}
		});
		
		getSlidingMenu().findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(AppUtil.isNetworkAvailable(BaseSlidingActivity.this)) logout();
				else AppUtil.showErrorDialog(getString(R.string.no_connection), BaseSlidingActivity.this);
			}
		});
		
		getSlidingMenu().findViewById(R.id.nearme).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(AppUtil.isNetworkAvailable(BaseSlidingActivity.this)){ 
					showCategoryDialog();
				}
				else AppUtil.showErrorDialog(getString(R.string.no_connection), BaseSlidingActivity.this);
			}
		});
		
		getSlidingMenu().findViewById(R.id.status).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!(BaseSlidingActivity.this instanceof ObservationStatusActivity)){
					Intent intent = new Intent(BaseSlidingActivity.this,ObservationStatusActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else{
					getSlidingMenu().toggle();
				}
			}
		});
		
		getSlidingMenu().findViewById(R.id.observation).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!(BaseSlidingActivity.this instanceof NewObservationActivity)){
					Intent intent = new Intent(BaseSlidingActivity.this,NewObservationActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else{
					getSlidingMenu().toggle();
				}
			}
		});
		
		getSlidingMenu().findViewById(R.id.joined_groups).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!(BaseSlidingActivity.this instanceof JoinedGroupsActivity)){
					Intent intent = new Intent(BaseSlidingActivity.this,JoinedGroupsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else{
					getSlidingMenu().toggle();
				}
			}
		});
		
		getSlidingMenu().findViewById(R.id.my_collection).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(AppUtil.isNetworkAvailable(BaseSlidingActivity.this)){
					isMyCollection=true;
					SharedPreferencesUtil.putSharedPreferencesBoolean(BaseSlidingActivity.this, Constants.IS_MY_COLLECTION,isMyCollection);
					Intent intent = new Intent(BaseSlidingActivity.this,ObservationActivity.class);
					intent.putExtra(Constants.IS_MY_COLLECTION, isMyCollection);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				else AppUtil.showErrorDialog(getString(R.string.no_connection), BaseSlidingActivity.this);
			}
		});
	}
	
	
	private void initMenuIndicators(){
		if(BaseSlidingActivity.this instanceof HomeActivity){
			getSlidingMenu().findViewById(R.id.indicator_home).setVisibility(View.VISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_observation).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_settings).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_status).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_joined).setVisibility(View.INVISIBLE);
		}
		if(BaseSlidingActivity.this instanceof ObservationActivity){
			getSlidingMenu().findViewById(R.id.indicator_home).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_observation).setVisibility(View.INVISIBLE);
			//getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.VISIBLE);
			//getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			isMyCollection=SharedPreferencesUtil.getSharedPreferencesBoolean(BaseSlidingActivity.this, Constants.IS_MY_COLLECTION, false);
			if(!isMyCollection){
				getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.VISIBLE);
				getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			}	
			else{
				getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.INVISIBLE);
				getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.VISIBLE);
			}	
			getSlidingMenu().findViewById(R.id.indicator_settings).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_status).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_joined).setVisibility(View.INVISIBLE);
		}
		if(BaseSlidingActivity.this instanceof NewObservationActivity){
			getSlidingMenu().findViewById(R.id.indicator_home).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_observation).setVisibility(View.VISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_settings).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_status).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_joined).setVisibility(View.INVISIBLE);
		}
		if(BaseSlidingActivity.this instanceof ObservationStatusActivity){
			getSlidingMenu().findViewById(R.id.indicator_home).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_observation).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_settings).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_status).setVisibility(View.VISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_joined).setVisibility(View.INVISIBLE);
		}
		if(BaseSlidingActivity.this instanceof JoinedGroupsActivity){
			getSlidingMenu().findViewById(R.id.indicator_home).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_observation).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_nearme).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_my_collection).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_settings).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_status).setVisibility(View.INVISIBLE);
			getSlidingMenu().findViewById(R.id.indicator_joined).setVisibility(View.VISIBLE);
		}
	}
	
	protected void showCategoryDialog() {
		final ArrayList<String> categoryListStr=new ArrayList<String>();
		final ArrayList<Category> categoryList=(ArrayList<Category>) CategoriesTable.getAllCategories(BaseSlidingActivity.this);
		if(categoryList!=null && categoryList.size()>0){
			for(int i=0;i<categoryList.size();i++){
				categoryListStr.add(categoryList.get(i).getName());
			}
		}
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(BaseSlidingActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, categoryListStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseSlidingActivity.this);
        builder.setTitle(R.string.obeservations_near_me);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {*/
				isMyCollection=false;
				SharedPreferencesUtil.putSharedPreferencesBoolean(BaseSlidingActivity.this, Constants.IS_MY_COLLECTION,isMyCollection);
				Log.d("HomeActivity", "category selected: "+categoryListStr.get(0));//which for all=0,plants=9
				Intent i=new Intent(BaseSlidingActivity.this, ObservationActivity.class);
				i.putExtra(Constants.GROUP_ID, categoryList.get(0).getId());
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			/*}
		});
	
        AlertDialog alert = builder.create();
        alert.show();  */
	}
	
	protected void logout() {
		mPg=ProgressDialog.show(BaseSlidingActivity.this,getString(R.string.logging_out));
		WebService.sendRequest(BaseSlidingActivity.this, Request.METHOD_POST, Request.PATH_LOGOUT, null, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				if(mPg!=null&&mPg.isShowing()) mPg.dismiss();
				SharedPreferencesUtil.putSharedPreferencesString(BaseSlidingActivity.this, Constants.APP_TOKEN, null);
				SharedPreferencesUtil.putSharedPreferencesString(BaseSlidingActivity.this, Constants.USER_ID, null);
				SharedPreferencesUtil.putSharedPreferencesBoolean(BaseSlidingActivity.this, Constants.IS_MY_COLLECTION, false);
				ObservationInstanceTable.deleteAllOrders(BaseSlidingActivity.this);
				Session session = Session.getActiveSession();
			    if (session != null) {

			        if (!session.isClosed()) {
			            session.closeAndClearTokenInformation();
			            //clear your preferences if saved
			        }
			    } else {

			        session = new Session(BaseSlidingActivity.this);
			        Session.setActiveSession(session);

			        session.closeAndClearTokenInformation();
			            //clear your preferences if saved

			    }
				showLoginActivity();
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null&&mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, BaseSlidingActivity.this);
			}
		});
	}
	
	protected void showLoginActivity() {
		Intent i=new Intent(BaseSlidingActivity.this, LoginActivity.class);
		startActivity(i);
		finish();
	}
	
	
}
