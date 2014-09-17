package com.mobisys.android.ibp;

import java.io.File;
import java.util.ArrayList;

import com.mobisys.android.ibp.database.ObservationInstanceTable;
import com.mobisys.android.ibp.models.ObservationInstance;
import com.mobisys.android.ibp.models.ObservationInstance.StatusType;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.widget.MImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ObservationStatusActivity extends BaseSlidingActivity{
	private ObservationListAdapter mAdapter;
	private ListView mList;
	private ArrayList<ObservationInstance> oParams;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_status);
		((TextView)findViewById(R.id.title)).setText(getString(R.string.observation_status));
		((TextView)findViewById(R.id.title)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
	}

	@Override
	 protected void onResume(){
		 registerReceiver(mBroadcastReceiver, new IntentFilter("com.mobisys.android.ibp.check_incomplete_obs"));
		 oParams=(ArrayList<ObservationInstance>) ObservationInstanceTable.getAllRecords(ObservationStatusActivity.this);
		 initScreen();
	     super.onResume();
	 }
	 
	 @Override
	 protected void onPause(){
		 unregisterReceiver(mBroadcastReceiver);
	     super.onPause();
	 }
	
	 public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(Preferences.DEBUG) Log.d("ObsActivityActivity", "******Broadcast received in ObsStatusActivity....");
				oParams.clear();
				oParams.addAll((ArrayList<ObservationInstance>) ObservationInstanceTable.getAllRecords(ObservationStatusActivity.this));
				mAdapter.notifyDataSetChanged();
			}	
	 };
	
	private void initScreen() {
		if(oParams!=null && oParams.size()>0){
			findViewById(R.id.error_layout).setVisibility(View.GONE);
			mList=(ListView)findViewById(R.id.list);
			mAdapter=new ObservationListAdapter(ObservationStatusActivity.this,0,oParams);
			mList.setAdapter(mAdapter);
		}
		else findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
		
		findViewById(R.id.btn_new_obs).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(ObservationStatusActivity.this, NewObservationActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	private class ObservationListAdapter extends ArrayAdapter<ObservationInstance>{
 		
 		private LayoutInflater mInflater;
 		private class ViewHolder {
 			public TextView common_name, status, message;
 			public ImageView image;
 			
 			public ViewHolder(View row){
 				common_name = (TextView)row.findViewById(R.id.species_common_name);
 				status = (TextView)row.findViewById(R.id.status);
 				message = (TextView)row.findViewById(R.id.error_message);
 				image = (ImageView)row.findViewById(R.id.image);
 			}
 		}
 
 		public ObservationListAdapter(Context context, int textViewResourceId,ArrayList<ObservationInstance> objects) {
 			super(context, textViewResourceId, objects);
 			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		}
 
 		@Override
 		public View getView(final int position, View convertView, ViewGroup parent) {
 			View row = convertView;
 			ViewHolder holder = null;
 			if(row == null){
 				row = mInflater.inflate(R.layout.row_observation_status_list_item, parent,false);
 				holder = new ViewHolder(row);
 				row.setTag(holder);
 				Drawable selector=AppUtil.getListSelectorNew(ObservationStatusActivity.this);
				row.setBackgroundDrawable(selector);
 			}
 			else{
 				holder = (ViewHolder)row.getTag();
 			}
 			
 			if(getItem(position).getMaxVotedReco().getCommonName()!=null){
 				if(getItem(position).getMaxVotedReco().getCommonName().length()>0)
 					holder.common_name.setText(getItem(position).getMaxVotedReco().getCommonName());
 				else 
 					holder.common_name.setText(R.string.unknown);
 			}
 			
			holder.status.setText(getItem(position).getStatus().toString());
 			if(getItem(position).getStatus().equals(StatusType.FAILURE)){
				holder.message.setText(getItem(position).getMessage().toString());
 			}
 			else holder.message.setText("");
 			
 			//ArrayList<String> resources=new ArrayList<String>();
 			if(getItem(position).getResource()!=null && getItem(position).getResource().size()>0){
 				if(getItem(position).getResource().get(0).getUrl()!=null){
 						//if(getItem(position).getResource().get(0).getUrl().contains("http://")) 
	 			   MImageLoader.displayImage(ObservationStatusActivity.this, getItem(position).getResource().get(0).getUrl().replace(".jpg", "_th1.jpg"), holder.image, R.drawable.user_stub);
 				}  
 				else
	 			  showImage(holder.image, getItem(position).getResource().get(0).getUri());
 			}
 			else 
 				holder.image.setBackgroundResource(R.drawable.user_stub);
 			
 			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i=new Intent(ObservationStatusActivity.this, ObservationDetailActivity.class);
					i.putExtra(ObservationInstance.ObsInstance, getItem(position));
					i.putExtra(Constants.FROM_STATUS_SCREEN, true);
					startActivity(i);
				}
			});

 		   return row;
 		}
	}

	public void showImage(final ImageView image,final String uriStr) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//Uri uri=Uri.fromFile(new File(uriStr));
				//String path = AppUtil.getRealPathFromURI(uri, ObservationStatusActivity.this);
				String imagepath=AppUtil.getRealPathFromURI(Uri.parse(uriStr), ObservationStatusActivity.this);
				
				File imageFile = new File(imagepath);
				//Rotate if necessary
				int rotate=AppUtil.getCameraPhotoOrientation(ObservationStatusActivity.this, Uri.parse(uriStr), imagepath);
				final Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				
				//handle Out of memory error
				final Bitmap bmpDisplay=AppUtil.decodeFile(imageFile,100,100);
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//Rotate BMP
						if(bmpDisplay!=null){
							Bitmap rotatedBitmap = Bitmap.createBitmap(bmpDisplay, 0, 0, bmpDisplay.getWidth(), bmpDisplay.getHeight(), matrix, true);
							image.setImageBitmap(rotatedBitmap);
						}
					}
				});
			}
		}).start();
		
	}
}
