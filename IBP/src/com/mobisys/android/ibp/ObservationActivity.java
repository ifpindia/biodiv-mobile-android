package com.mobisys.android.ibp;


import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.Observation;
import com.mobisys.android.ibp.models.ObservationInstanceList;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;
import com.mobisys.android.ibp.widget.MImageLoader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ObservationActivity extends BaseSlidingActivity{

	private long selected_group_id=-1;
	private Dialog mPG;
	private ObservationListAdapter mAdapter;
	private ListView mList;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_list);
		initActionTitle(getString(R.string.observations));
		selected_group_id=getIntent().getLongExtra(Constants.GROUP_ID, -1);
		mList=(ListView)findViewById(R.id.list);
		getNearByObservation();
	}

	private void getNearByObservation() {
		Bundle b=new Bundle();
		double lat=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(ObservationActivity.this,Constants.LAT, "0.0"));
		double lng=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(ObservationActivity.this,Constants.LNG, "0.0"));
		b.putString(Constants.LAT, String.valueOf(lat));
		b.putString(Constants.LNG, String.valueOf(lng));
		b.putString(Request.NEARBY_TYPE, Constants.NEARBY);
		b.putString(Request.MAXRADIUS, String.valueOf(1000));
		if(selected_group_id!=-1) b.putString(Request.GROUP_ID, String.valueOf(selected_group_id));
		
		mPG= ProgressDialog.show(ObservationActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(ObservationActivity.this, Request.METHOD_GET, Request.PATH_NEARBY_OBSERVATIONS, b, new ResponseHandler() {
			
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
							if(mPG!=null && mPG.isShowing()) mPG.dismiss();
							mAdapter=new ObservationListAdapter(ObservationActivity.this,0,obs.getObservationInstanceList());
							mList.setAdapter(mAdapter);
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private class ObservationListAdapter extends ArrayAdapter<ObservationInstanceList>{
 		
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
 
 		public ObservationListAdapter(Context context, int textViewResourceId,ArrayList<ObservationInstanceList> objects) {
 			super(context, textViewResourceId, objects);
 			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		}
 
 		@Override
 		public View getView(int position, View convertView, ViewGroup parent) {
 			View row = convertView;
 			ViewHolder holder = null;
 			if(row == null){
 				row = mInflater.inflate(R.layout.row_observation_list_item, null);
 				holder = new ViewHolder(row);
 				row.setTag(holder);
 				Drawable selector=AppUtil.getPressedStateDrawable(ObservationActivity.this);
 				row.setBackgroundDrawable(selector);
 			}
 			else{
 				holder = (ViewHolder)row.getTag();
 			}
 			if(getItem(position).getMaxVotedReco()!=null){
 				holder.common_name.setText(getItem(position).getMaxVotedReco().getName());
 				if(getItem(position).getMaxVotedReco().getTaxonomyDefinition()!=null){
 					holder.latin_name.setVisibility(View.VISIBLE);
 					holder.latin_name.setText(getItem(position).getMaxVotedReco().getTaxonomyDefinition().getName());
 				}
 				else holder.latin_name.setVisibility(View.GONE);
 			}
 			else{ 
 				holder.common_name.setText(R.string.unknown);
 				holder.latin_name.setVisibility(View.GONE);
 			}
 			
 			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(ObservationActivity.this, "To DO", Toast.LENGTH_SHORT).show();
				}
			});
 			
 			MImageLoader.displayImage(ObservationActivity.this, getItem(position).getThumbnail(), holder.image, R.drawable.user_stub);
 			return row;
 		}
	}
	
}
