package com.ifp.wikwio;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import com.ifp.wikwio.R;
import com.ifp.wikwio.http.Request;
import com.ifp.wikwio.http.WebService;
import com.ifp.wikwio.http.WebService.ResponseHandler;
import com.ifp.wikwio.models.Observation;
import com.ifp.wikwio.models.ObservationInstance;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.ProgressDialog;
import com.ifp.wikwio.utils.SharedPreferencesUtil;
import com.ifp.wikwio.widget.MImageLoader;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ObservationActivity extends BaseSlidingActivity implements OnScrollListener{

	private long selected_group_id=-1;
	private Dialog mPG;
	private ObservationListAdapter mAdapter;
	private ListView mList;
	private ArrayList<ObservationInstance> mObsList=new ArrayList<ObservationInstance>();
	private int mOffset=0;
	private boolean mNoMoreItems = false, mMoreLoading = false;
	private boolean userScrolled=false;
	protected static final int DELETE_OBSERVATION = 200;
	private boolean mShowAllObersvations;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_list);
		selected_group_id=getIntent().getLongExtra(Constants.GROUP_ID, -1);
		mShowAllObersvations=getIntent().getBooleanExtra(Constants.SHOW_ALL, false);
		
		mList=(ListView)findViewById(R.id.list);
		getNearByObservation();
	}

	private void getNearByObservation() {
		Bundle b=new Bundle();
		isMyCollection=getIntent().getBooleanExtra(Constants.IS_MY_COLLECTION, false);
		if(!isMyCollection){
			initActionTitle(getString(R.string.observations));
			Location location = AppUtil.getCurrentLocation(this);
			if(!mShowAllObersvations){
				if(Preferences.LOCATION_DEBUG){
					b.putString(Constants.LAT, Constants.LAT);
					b.putString(Constants.LNG, Constants.LNG);
				} else {
								Log.d("Parsing Observation: ",String.valueOf(location.getLatitude()) +","+ String.valueOf(location.getLongitude()));

					b.putString(Constants.LAT, String.valueOf(location.getLatitude()));
					b.putString(Constants.LNG, String.valueOf(location.getLongitude()));
				}
			}
			b.putString(Request.NEARBY_TYPE, Constants.NEARBY);
			b.putString(Request.MAXRADIUS, String.valueOf(50000));
			if(selected_group_id!=-1) b.putString(Request.GROUP_ID, String.valueOf(selected_group_id));
			b.putString(Request.PARAM_OFFSET, String.valueOf(mOffset*24));
		} else {
			initActionTitle(getString(R.string.my_collection));
			String id=SharedPreferencesUtil.getSharedPreferencesString(ObservationActivity.this, Constants.USER_ID,null);
			b.putString(Request.USER, String.valueOf(id));
			b.putString(Request.PARAM_OFFSET, String.valueOf(mOffset*24));
		}
		
		if(!mMoreLoading)
			mPG= ProgressDialog.show(ObservationActivity.this,getString(R.string.loading));
		else
			findViewById(R.id.more_loading_layout).setVisibility(View.VISIBLE);
		
		WebService.sendRequest(ObservationActivity.this, Request.METHOD_GET, Request.PATH_GET_OBSERVATIONS, b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseObservationDetails(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPG!=null && mPG.isShowing()) mPG.dismiss();
				AppUtil.showErrorDialog(content, ObservationActivity.this);
			}
		});
	}

	private void parseObservationDetails(final String response) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				try {
					final Observation obs=mapper.readValue(response, Observation.class);
					Log.d("Parse Observation: ","no of observations: "+obs.getObservationInstanceList().size());
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(!mMoreLoading){
								if(mPG!=null && mPG.isShowing()) mPG.dismiss();
								if(obs.getObservationInstanceList()!=null && !obs.getObservationInstanceList().isEmpty()) {
									mObsList=obs.getObservationInstanceList();
									mAdapter=new ObservationListAdapter(ObservationActivity.this,0,mObsList);
									mList.setAdapter(mAdapter);
									mList.setOnScrollListener(ObservationActivity.this);
								}
								else {
									Log.d("obs parse", "***error");
									mMoreLoading = false;
								}	
							}
							else if(mMoreLoading){ //Scrolling up to load more items
								if(obs.getObservationInstanceList()==null||obs.getObservationInstanceList().size()==0){ 
									mNoMoreItems=true;
									mMoreLoading = false;
									userScrolled=true;
									findViewById(R.id.more_loading_layout).setVisibility(View.GONE);
									return;
								}
								if(obs.getObservationInstanceList()!=null && obs.getObservationInstanceList().size()>0){
									for(int i=0;i<obs.getObservationInstanceList().size();i++){
										mObsList.add(obs.getObservationInstanceList().get(i));
									}
									mAdapter.notifyDataSetChanged();
								}
								findViewById(R.id.more_loading_layout).setVisibility(View.GONE);
								mMoreLoading = false;
							}
							initScreen();
							Log.d("ObservationActivity","No of observation after parse: "+mObsList.size());
						}

					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	 
	
	private void initScreen() {
		//if(isMyCollection){
		if(mObsList!=null && mObsList.size()>0){
			mList.setVisibility(View.VISIBLE);
			findViewById(R.id.btn_show_map).setVisibility(View.VISIBLE);
			findViewById(R.id.view01).setVisibility(View.VISIBLE);
			findViewById(R.id.error_layout).setVisibility(View.GONE);
			findViewById(R.id.btn_show_map).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i=new Intent(ObservationActivity.this, MyCollectionMapActivity.class);
					i.putParcelableArrayListExtra(ObservationInstance.OI, mObsList);
					startActivity(i);
				}
			});
		} else {
			mList.setVisibility(View.GONE);
			findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_show_map).setVisibility(View.GONE);
			findViewById(R.id.view01).setVisibility(View.GONE);
			findViewById(R.id.btn_new_obs).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i=new Intent(ObservationActivity.this, NewObservationActivity.class);
					startActivity(i);
					finish();
				}
			});
		}
	}
	
	@SuppressLint("InflateParams")
	private class ObservationListAdapter extends ArrayAdapter<ObservationInstance>{
 		
		private LayoutInflater mInflater;
 		private class ViewHolder {
 			public TextView common_name, latin_name;
 			public ImageView image;
 			
 			public ViewHolder(View row){
 				common_name = (TextView)row.findViewById(R.id.species_common_name);
 				latin_name = (TextView)row.findViewById(R.id.species_latin_name);
 				image = (ImageView)row.findViewById(R.id.image);
 			}
 		}
 
 		public ObservationListAdapter(Context context, int textViewResourceId,ArrayList<ObservationInstance> objects) {
 			super(context, textViewResourceId, objects);
 			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		}
 
 		@SuppressWarnings("deprecation")
		@Override
 		public View getView(final int position, View convertView, ViewGroup parent) {
 			View row = convertView;
 			ViewHolder holder = null;
 			if(row == null){
 				row = mInflater.inflate(R.layout.row_observation_list_item, null);
 				holder = new ViewHolder(row);
 				row.setTag(holder);
 				Drawable selector=AppUtil.getPressedStateDrawable(ObservationActivity.this);
 				row.setBackgroundDrawable(selector);
 				//Drawable selector=AppUtil.getListSelectorNew(ObservationActivity.this);
				//row.setBackgroundDrawable(selector);
 			}
 			else{
 				holder = (ViewHolder)row.getTag();
 			}
 			
 			if(getItem(position).getMaxVotedReco()!=null){
 				if(getItem(position).getMaxVotedReco().getCommonName().length()>0){
 					holder.common_name.setVisibility(View.VISIBLE);
 					holder.common_name.setText(getItem(position).getMaxVotedReco().getCommonName());
 				}	
 				else{
 					if(getItem(position).getMaxVotedReco().getScientificName().length()==0){ //common name and sci name empty, 
 						holder.common_name.setVisibility(View.VISIBLE);
 						holder.common_name.setText(R.string.unknown);
 					}
 					else{  // common name empty but sci name present
 						holder.common_name.setVisibility(View.GONE);
 					} 
 				}	
 				holder.latin_name.setVisibility(View.VISIBLE);
 				holder.latin_name.setText(getItem(position).getMaxVotedReco().getScientificName());
 			}
 			
 			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isMyCollection){
						Intent i=new Intent(ObservationActivity.this, ObservationDetailActivity.class);
						i.putExtra(ObservationInstance.ObsInstance, getItem(position));
						i.putExtra(Constants.IS_MY_COLLECTION, isMyCollection);
						startActivityForResult(i, DELETE_OBSERVATION);
					}
					else{
						Intent i=new Intent(ObservationActivity.this, ObservationDetailSlideActivity.class);
						i.putParcelableArrayListExtra(ObservationInstance.ObsInstanceList, mObsList);
						i.putExtra(ObservationInstance.ObsInstance, getItem(position));
						i.putExtra("index", position);
						startActivity(i);
					}
				}
			});
 			
 			MImageLoader.displayImage(ObservationActivity.this, getItem(position).getThumbnail(), holder.image, R.drawable.user_stub);
 			return row;
 		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
	    if(userScrolled && loadMore && !mMoreLoading && !mNoMoreItems){
	    	mMoreLoading=true;
	    	mOffset=mOffset+1;
	    	getNearByObservation();
	    }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            userScrolled = true;
        }   
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == DELETE_OBSERVATION) {
            if(data!=null){
            	mObsList.clear();
            	mOffset=0;
            	getNearByObservation();
            }
        }
    }
}
