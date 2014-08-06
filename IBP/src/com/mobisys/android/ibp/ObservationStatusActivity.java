package com.mobisys.android.ibp;

import java.io.File;
import java.util.ArrayList;

import com.mobisys.android.ibp.database.ObservationParamsTable;
import com.mobisys.android.ibp.models.ObservationParams;
import com.mobisys.android.ibp.models.ObservationParams.StatusType;
import com.mobisys.android.ibp.utils.AppUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ObservationStatusActivity extends BaseSlidingActivity{
	private ObservationListAdapter mAdapter;
	private ListView mList;
	private ArrayList<ObservationParams> oParams;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_status);
		((TextView)findViewById(R.id.title)).setText(getString(R.string.observation_status));
		((TextView)findViewById(R.id.title)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		oParams=(ArrayList<ObservationParams>) ObservationParamsTable.getAllRecords(ObservationStatusActivity.this);
		initScreen();
	}

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
				Intent i=new Intent(ObservationStatusActivity.this, NewSightingActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	private class ObservationListAdapter extends ArrayAdapter<ObservationParams>{
 		
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
 
 		public ObservationListAdapter(Context context, int textViewResourceId,ArrayList<ObservationParams> objects) {
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
 			}
 			else{
 				holder = (ViewHolder)row.getTag();
 			}
 			
 			if(getItem(position).getCommonName()!=null){
 				if(getItem(position).getCommonName().length()>0)
 					holder.common_name.setText(getItem(position).getCommonName());
 				else 
 					holder.common_name.setText(R.string.unknown);
 			}
 			
			holder.status.setText(getItem(position).getStatus().toString());
 			if(getItem(position).getStatus().equals(StatusType.FAILURE)){
				holder.message.setText(getItem(position).getMessage().toString());
 			}
 			else holder.message.setText("");
 			
 			ArrayList<String> resources=new ArrayList<String>();
 		    String[] items = getItem(position).getResources().split(",");
 		    for (String item : items){
 		        resources.add(item);
 		    }
 		   showImage(holder.image, resources.get(0)); 
 			
 		   return row;
 		}
	}

	public void showImage(final ImageView image,final String uriStr) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri uri=Uri.fromFile(new File(uriStr));
				String path = AppUtil.getRealPathFromURI(uri, ObservationStatusActivity.this);
				
				File imageFile = new File(path);
				//Rotate if necessary
				int rotate=AppUtil.getCameraPhotoOrientation(ObservationStatusActivity.this, uri, path);
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
