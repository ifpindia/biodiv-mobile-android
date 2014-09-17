package com.mobisys.android.ibp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.AutoCompleteResponseParserInterface;
import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.DisplayStringInterface;
import com.mobisys.android.ibp.database.CategoriesTable;
import com.mobisys.android.ibp.database.ObservationInstanceTable;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.Category;
import com.mobisys.android.ibp.models.NameRecord;
import com.mobisys.android.ibp.models.ObservationInstance;
import com.mobisys.android.ibp.models.ObservationInstance.StatusType;
import com.mobisys.android.ibp.models.Resource;

import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.AppUtil.DateListener;
import com.mobisys.android.ibp.utils.HttpRetriever;
import com.mobisys.android.ibp.utils.ProgressDialog;
import com.mobisys.android.ibp.utils.ReveseGeoCodeUtil;
import com.mobisys.android.ibp.utils.ReveseGeoCodeUtil.ReveseGeoCodeListener;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;
import com.mobisys.android.ibp.widget.MImageLoader;

public class NewObservationActivity extends BaseSlidingActivity{

	private int mSelectedImagePos;
	private ImageView mSelectedImageView;
	private Button mSelectedButton;
	private Uri mFileUri;
	private ArrayList<Resource> mResourceList;
	private double mLat=0, mLng=0;
	private HttpRetriever mHttpRetriever;
	private Category mSelectedCategory=null;
	private String mSelectedDateStr=null;
	private Date mSelectedDate=null;
	private String mAddress;
	private com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView mCommNameAutoText, mSciNameAutoText;
	private String DATE_FORMAT1="yyyy:MM:dd hh:mm:ss";
	//private String DATE_FORMAT_FROM_SERVER="yyyy-MM-dd'T'HH:mm:ss'Z'";
	private ObservationInstance mObv;
	private Dialog mPG;
	private boolean isFromStatusScreen=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_sighting);
		mHttpRetriever = new HttpRetriever();
		mObv=getIntent().getParcelableExtra(ObservationInstance.ObsInstance);
		isFromStatusScreen=getIntent().getBooleanExtra(Constants.FROM_STATUS_SCREEN, false);
		initActionTitle(getString(R.string.new_observation));
		initScreen();
		if(mObv!=null){
			mResourceList = mObv.getResource();
			initEditScreen();
		} else {
			mResourceList = new ArrayList<Resource>();
		}
	}

	private void initEditScreen() {
		initActionTitle(getString(R.string.edit_observation));
		
		final ArrayList<String> categoryListStr=new ArrayList<String>();
		final ArrayList<Category> categoryList=(ArrayList<Category>) CategoriesTable.getAllCategories(NewObservationActivity.this);
		if(categoryList!=null && categoryList.size()>0){
			categoryList.remove(0);
			for(int i=0;i<categoryList.size();i++){
				categoryListStr.add(categoryList.get(i).getName());
			}
		}
		for(int i=0;i<categoryListStr.size();i++){
			
			if(mObv.getGroup().getName().equals(categoryListStr.get(i))){
				mSelectedCategory=categoryList.get(i);
				((TextView)findViewById(R.id.category)).setText(""+categoryListStr.get(i));
				break;
			}
		}
		
		mAddress=mObv.getPlaceName();
		((TextView)findViewById(R.id.address)).setText(mAddress);
		if(mObv.getCreatedOn()!=null){
			mSelectedDateStr=AppUtil.getStringFromDate(mObv.getCreatedOn(), Constants.DATE_FORMAT);
			mSelectedDate=mObv.getCreatedOn();
		}
		else{
			mSelectedDateStr=AppUtil.getStringFromDate(mObv.getFromDate(), Constants.DATE_FORMAT);
			mSelectedDate=mObv.getFromDate();
		}
		((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
		
		if(mObv.getMaxVotedReco()!=null){
			if(mObv.getMaxVotedReco().getCommonName()!=null && mObv.getMaxVotedReco().getCommonName().length()>0){
				mCommNameAutoText.setText(mObv.getMaxVotedReco().getCommonName());
			}
			if(mObv.getMaxVotedReco().getScientificName()!=null && mObv.getMaxVotedReco().getScientificName().length()>0){
				mSciNameAutoText.setText(mObv.getMaxVotedReco().getScientificName());
			}
		}
		
		if(mObv.getNotes()!=null && mObv.getNotes().length()>0)
			((EditText)findViewById(R.id.edit_add_notes)).setText(mObv.getNotes());
		
		displayImage();
	}

	private void displayImage() {
		if(mObv.getResource()!=null && mObv.getResource().size()>0){
			for(int i=0;i<mObv.getResource().size();i++){
				String url=mObv.getResource().get(i).getIcon();
				String uri=mObv.getResource().get(i).getUri();
				if(i==0){
					if(url!=null){
						MImageLoader.displayImage(NewObservationActivity.this, url, (ImageView)findViewById(R.id.species_image_1), R.drawable.user_stub);
						((Button)findViewById(R.id.btn_add_photo_1)).setText(getString(R.string.edit_photo));
					}
					else if(uri!=null){
						((ImageView)findViewById(R.id.species_image_1)).setImageURI(Uri.parse(uri));
						((Button)findViewById(R.id.btn_add_photo_1)).setText(getString(R.string.edit_photo));
					}	
					else ((Button)findViewById(R.id.btn_add_photo_1)).setText(getString(R.string.add_photo));
				}
				else if(i==1){
					if(url!=null){
						MImageLoader.displayImage(NewObservationActivity.this, url, (ImageView)findViewById(R.id.species_image_2), R.drawable.user_stub);
						((Button)findViewById(R.id.btn_add_photo_2)).setText(getString(R.string.edit_photo));
					}
					else if(uri!=null){
						((ImageView)findViewById(R.id.species_image_2)).setImageURI(Uri.parse(uri));
						((Button)findViewById(R.id.btn_add_photo_2)).setText(getString(R.string.edit_photo));
					}
					else ((Button)findViewById(R.id.btn_add_photo_2)).setText(getString(R.string.add_photo));
				}
				else if(i==2){
					if(url!=null){
						MImageLoader.displayImage(NewObservationActivity.this, url, (ImageView)findViewById(R.id.species_image_3), R.drawable.user_stub);
						((Button)findViewById(R.id.btn_add_photo_3)).setText(getString(R.string.edit_photo));
					}
					else if(uri!=null){
						((ImageView)findViewById(R.id.species_image_3)).setImageURI(Uri.parse(uri));
						((Button)findViewById(R.id.btn_add_photo_3)).setText(getString(R.string.edit_photo));
					}
					else ((Button)findViewById(R.id.btn_add_photo_3)).setText(getString(R.string.add_photo));
				}
				else if(i==3){
					if(url!=null){
						MImageLoader.displayImage(NewObservationActivity.this, url, (ImageView)findViewById(R.id.species_image_4), R.drawable.user_stub);
						((Button)findViewById(R.id.btn_add_photo_4)).setText(getString(R.string.edit_photo));
					}
					else if(uri!=null){
						((ImageView)findViewById(R.id.species_image_4)).setImageURI(Uri.parse(uri));
						((Button)findViewById(R.id.btn_add_photo_4)).setText(getString(R.string.edit_photo));
					}
					else ((Button)findViewById(R.id.btn_add_photo_4)).setText(getString(R.string.add_photo));
				}
				else if(i==4){
					if(url!=null){
						MImageLoader.displayImage(NewObservationActivity.this, url, (ImageView)findViewById(R.id.species_image_5), R.drawable.user_stub);
						((Button)findViewById(R.id.btn_add_photo_5)).setText(getString(R.string.edit_photo));
					}
					else if(uri!=null){
						((ImageView)findViewById(R.id.species_image_5)).setImageURI(Uri.parse(uri));
						((Button)findViewById(R.id.btn_add_photo_5)).setText(getString(R.string.edit_photo));
					}
					else ((Button)findViewById(R.id.btn_add_photo_5)).setText(getString(R.string.add_photo));
				}
				
			}
		}
	}

	private void initScreen() {
		mCommNameAutoText = (com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.edit_common_name);
		mCommNameAutoText.setParser(new AutoCompleteResponseParserInterface() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String response) {
				return getStringArray(response);
			}
		});

		mSciNameAutoText = (com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.edit_sci_name);
		mSciNameAutoText.setParser(new AutoCompleteResponseParserInterface() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String response) {
				return getStringArray(response);
			}
		});
		
		((CheckBox)findViewById(R.id.chk_help_identify)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
			    if(!isChecked){
			    	findViewById(R.id.edit_common_name).setVisibility(View.VISIBLE);
			    	findViewById(R.id.edit_sci_name).setVisibility(View.VISIBLE);
			    	findViewById(R.id.rule03).setVisibility(View.VISIBLE);
			    	findViewById(R.id.rule05).setVisibility(View.VISIBLE);
			    }else{
			    	findViewById(R.id.rule03).setVisibility(View.GONE);
			    	findViewById(R.id.rule05).setVisibility(View.GONE);
			    	findViewById(R.id.edit_common_name).setVisibility(View.GONE);
			    	findViewById(R.id.edit_sci_name).setVisibility(View.GONE);
			    }
		}});
		
		findViewById(R.id.category_layout).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCategoryDialogNew();
			}
		});
		
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
		mSelectedDateStr=sdf.format(new Date());
		mSelectedDate=new Date();
		((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
		
		findViewById(R.id.date_layout).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDate();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_1)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 0;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_1);
				mSelectedButton=(Button)v;
				showSelectDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_2)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 1;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_2);
				mSelectedButton=(Button)v;
				showSelectDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_3)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 2;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_3);
				mSelectedButton=(Button)v;
				showSelectDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_4)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 3;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_4);
				mSelectedButton=(Button)v;
				showSelectDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_5)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 4;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_5);
				mSelectedButton=(Button)v;
				showSelectDialog();
			}
		});
		
		((RelativeLayout)findViewById(R.id.address_layout)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(NewObservationActivity.this, SelectLocationActivity.class);
				i.putExtra(Constants.LAT, mLat);
				i.putExtra(Constants.LNG, mLng);
				startActivityForResult(i, Constants.LOCATION_ADDRESS);
			}
		});
		
		if(mObv==null || isFromStatusScreen){
			LinearLayout btn_layout=((LinearLayout)findViewById(R.id.btn_layout));
			btn_layout.setWeightSum(1);
			
			Button btn_edit=(Button)findViewById(R.id.btn_submit);
			LinearLayout.LayoutParams p0 = new LinearLayout.LayoutParams(AppUtil.getDipValue(0,NewObservationActivity.this), AppUtil.getDipValue(40,NewObservationActivity.this));
			p0.weight = 1.0f;
			btn_edit.setLayoutParams(p0);
			
			View view=(View)findViewById(R.id.view00);
			LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(AppUtil.getDipValue(0,NewObservationActivity.this), LinearLayout.LayoutParams.MATCH_PARENT);
			p1.weight = 0.0f;
			view.setLayoutParams(p1);
			
			Button btn=(Button)findViewById(R.id.btn_delete);
			LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(AppUtil.getDipValue(0,NewObservationActivity.this), LinearLayout.LayoutParams.MATCH_PARENT);
			p2.weight = 0.0f;
			btn.setLayoutParams(p2);
		}
		else
			((Button)findViewById(R.id.btn_submit)).setText(getString(R.string.edit));
		
		((Button)findViewById(R.id.btn_delete)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showConfirmDeleteObservationDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAddress.equals(getResources().getString(R.string.label_reverse_lookup_error))) 
					showLocationAlertDialog();
				else
					validateParams();
			}
		});

		if(mObv==null){
			mLat=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(NewObservationActivity.this, Constants.LAT, String.valueOf(0)));
			mLng=Double.valueOf(SharedPreferencesUtil.getSharedPreferencesString(NewObservationActivity.this, Constants.LNG, String.valueOf(0)));
			if(mLat!=0.0 && mLng!=0.0){
				((TextView)findViewById(R.id.address)).setText(getResources().getString(R.string.loading));
				ReveseGeoCodeUtil.doReverseGeoCoding(NewObservationActivity.this, mLat,mLng, mHttpRetriever, new ReveseGeoCodeListener() {
					
					@Override
					public void onReveseGeoCodeSuccess(double lat, double lng, String address) {
						((TextView)findViewById(R.id.address)).setText(address);
						mLat=lat;
						mLng=lng;
						mAddress=address;
					}
				});
			}
		}	
		else{
			//split lat lng from topology string
			if(mObv.getTopology()!=null){
				String topology=mObv.getTopology();
				String t1=topology.replace("POINT (", "");
				String arr[]=t1.split(" ");
				mLng=Double.valueOf(arr[0]);
				mLat=Double.valueOf(arr[1].replace(")", ""));
			}
			else{
				mLng=0.0;
				mLat=0.0;
			}
		}
	}
	
	private void showConfirmDeleteObservationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
		builder.setTitle(getString(R.string.alert));
		builder.setMessage(getString(R.string.msg_confirm_delete));
		
		builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				deleteObservation();
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	private void deleteObservation() {
		mPG= ProgressDialog.show(NewObservationActivity.this,getString(R.string.loading));
		Bundle b=new Bundle();
		b.putString(Request.OBV_ID, String.valueOf(mObv.getId()));
		
		WebService.sendRequest(NewObservationActivity.this, Request.METHOD_POST, Request.PATH_DELETE_OBSERVATION, b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseDeleteObservationResponse(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPG!=null && mPG.isShowing()) mPG.dismiss();
				AppUtil.showErrorDialog(content, NewObservationActivity.this);
			}
		});
	}

	private void parseDeleteObservationResponse(String response) {
		try {
			JSONObject jObj=new JSONObject(response);
			boolean success=jObj.optBoolean("success");
			if(success){
				Log.d("NewObservationActivity", ""+jObj.optString("msg"));
				Intent i=new Intent();
				setResult(RESULT_OK, i);
				finish();
			}
			else{
				Toast.makeText(NewObservationActivity.this, jObj.optString("msg"), Toast.LENGTH_SHORT).show();
				Log.d("NewObservationActivity", ""+jObj.optString("msg"));
			}
		} catch (JSONException e) {
			if(mPG!=null && mPG.isShowing()) mPG.dismiss();
			e.printStackTrace();
		}
	}

	private void showLocationAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
		builder.setTitle(getString(R.string.alert));
		builder.setMessage(getString(R.string.alert_internet));
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				validateParams();
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	private ArrayList<DisplayStringInterface> getStringArray(String response) {
		ArrayList<DisplayStringInterface> array=new ArrayList<DisplayStringInterface>();
		try {
			JSONArray jsonArray=new JSONArray(response);
			if(jsonArray!=null){
				for(int i=0;i<jsonArray.length();i++){
					final String str=jsonArray.getJSONObject(i).optString("label");
					array.add(new DisplayStringInterface() {
						
						@Override
						public String getDisplayString() {
							
							return str;
						}
					});
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}

	protected void validateParams() {

		if((mResourceList==null||mResourceList.size()==0) && mObv==null){
			AppUtil.showDialog("You must submit atleast one photo.", NewObservationActivity.this);
			return;
		}
		if(mSelectedCategory==null){ 
			AppUtil.showDialog("Please Select Category", NewObservationActivity.this);
			return;
		}
		if(mSelectedDateStr==null){ 
			AppUtil.showDialog("Please select sighting date", NewObservationActivity.this);
			return;
		}
		
		if(!((CheckBox)findViewById(R.id.chk_help_identify)).isChecked()){
			String common_name=mCommNameAutoText.getText().toString();
			String sci_name=mSciNameAutoText.getText().toString();
			if(common_name.length()==0 && sci_name.length()==0){
				AppUtil.showDialog("Please enter all fields", NewObservationActivity.this);
				return;
			} 
		} 
		resizeImagesAndStoreDatatoDB();
	}

	private void resizeImagesAndStoreDatatoDB() {
		/*final ArrayList<String> imageStringPath=new ArrayList<String>();
		final ArrayList<String> mImageType=new ArrayList<String>();
			
		if(mResourceList!=null && mResourceList.size()>0){
			for(int i=0;i<mResourceList.size();i++){
				if(mResourceList.get(i).getUri()!=null && mResourceList.get(i).isDirty()){ //while edit add uri and url to imagepath
					String imagepath=AppUtil.getRealPathFromURI(Uri.parse(mResourceList.get(i).getUri()), NewObservationActivity.this);
					if(Preferences.DEBUG) Log.d("NewSightingActivity", "***image path for server "+imagepath);
		    		imageStringPath.add(imagepath);
		    		
		    		String imageType=AppUtil.GetMimeType(NewObservationActivity.this, Uri.parse(mResourceList.get(i).getUri()));
		    		if(Preferences.DEBUG) Log.d("AddPlacesActivity", "***image type for server "+imageType);
		    		mImageType.add(imageType);
				}
				else {
					if(mResourceList.get(i).getUrl()!=null){
						imageStringPath.add(mResourceList.get(i).getUrl());
						mImageType.add("null");
					}
				}	
			}
		}*/
		createSaveParamObject(/*imageStringPath, mImageType*/);
	}

	private void createSaveParamObject(/*ArrayList<String> imageStringPath, ArrayList<String> imageType*/) {
		ObservationInstance sp=new ObservationInstance();
		if(mObv!=null && mObv.getId()!=-1)
			sp.setId(mObv.getId());
		else
			sp.setId(-1);
		sp.setGroup(mSelectedCategory);  //set object
		sp.setGroupId(mSelectedCategory.getId());
		sp.setHabitatId(Constants.HABITATE_ID_STAGING);
		//sp.setFromDate(mSelectedDateStr);
		sp.setFromDate(mSelectedDate);
		if(!Preferences.NEW_DEBUG){
			/*if(mAddress.equals(getResources().getString(R.string.label_reverse_lookup_error))) sp.setPlaceName(null);
			else*/ 
			sp.setPlaceName(mAddress);
		}
		else sp.setPlaceName(Constants.DEFAULT_ADDRESS);
		
		if(Preferences.NEW_DEBUG){ 
			sp.setAreas("Point("+Constants.DEFAULT_LNG+" "+Constants.DEFAULT_LAT+")");
			sp.setStatus(StatusType.PENDING);
		}
		else{
			if(mLng!=0.0 && mLat!=0.0)
				sp.setStatus(StatusType.PENDING);
			else
				sp.setStatus(StatusType.INCOMPLETE);
			
			sp.setAreas("Point("+mLng+" "+mLat+")");
		}	
		
		String common_name=mCommNameAutoText.getText().toString();
		String sci_name=mSciNameAutoText.getText().toString();
		if(mObv!=null && mObv.getMaxVotedReco()!=null)
			sp.setMaxVotedReco(new NameRecord(common_name, sci_name, mObv.getMaxVotedReco().getSpeciesIdForSciRecord()));
		else
			sp.setMaxVotedReco(new NameRecord(common_name, sci_name, null));
		String notes=((EditText)findViewById(R.id.edit_add_notes)).getText().toString();
		sp.setCommonName(common_name);
		sp.setRecoName(sci_name);
		sp.setNotes(notes);
		sp.setResource(mResourceList);  // image list
		
		/*if(imageStringPath!=null && imageStringPath.size()>0){
			String resources = imageStringPath.toString().replace("[", "").replace("]", "").replace(", ", ",");
			sp.setResources(resources);
		}
		if(imageType!=null && imageType.size()>0){
			String imageType2=imageType.toString().replace("[", "").replace("]", "").replace(", ", ",");
			sp.setImageType(imageType2);
		}*/
		
		if(mObv!=null){
			ArrayList<ObservationInstance> obvList= (ArrayList<ObservationInstance>) ObservationInstanceTable.getAllRecords(NewObservationActivity.this);
			if(obvList!=null && obvList.size()>0){
				if(ObservationInstanceTable.isRecordAvailable(NewObservationActivity.this,sp))
					ObservationInstanceTable.updateRowFromTable2(NewObservationActivity.this, sp);
				else
					ObservationInstanceTable.createEntryInTable(NewObservationActivity.this, sp);
			}
			else
				ObservationInstanceTable.createEntryInTable(NewObservationActivity.this, sp);
		}	
		else
			ObservationInstanceTable.createEntryInTable(NewObservationActivity.this, sp);
		
		/*ArrayList<ObservationInstance> spList=(ArrayList<ObservationInstance>) ObservationInstanceTable.getAllRecords(NewObservationActivity.this);
		Log.d("Total","Db record: "+spList.size());		*/
		ObservationRequestQueue.getInstance().executeAllSubmitRequests(NewObservationActivity.this);
		if(AppUtil.isNetworkAvailable(NewObservationActivity.this)) Toast.makeText(NewObservationActivity.this, R.string.observation_msg, Toast.LENGTH_SHORT).show();
		else Toast.makeText(NewObservationActivity.this, R.string.observation_msg_without_internet, Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(NewObservationActivity.this, HomeActivity.class);
		startActivity(i);
		finish();
	}

	private void showSelectDialog() {
		final CharSequence[] methods = {getString(R.string.gallery), getString(R.string.camera)};
		 
        AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
        builder.setTitle(getString(R.string.select_from));
        builder.setItems(methods, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                case 0://Gallery
                	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            		photoPickerIntent.setType("image/*");
            		startActivityForResult(photoPickerIntent, Constants.GALLERY_PHOTO);
                	break;
                case 1://Camera
                	startCameraActivity();
                	break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void startCameraActivity() {
		File dir = new File(AppUtil.getDir());
		if(!dir.exists()) dir.mkdirs();
		String filename=String.valueOf(System.currentTimeMillis());
		
		File file = new File(AppUtil.getImagePath(filename));
		mFileUri = Uri.fromFile(file);
		Log.d("On camera click","*** Uri: "+mFileUri.toString());
		MyApplication myApplication = (MyApplication)getApplication();
		myApplication.setPictUri(mFileUri);
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
		startActivityForResult(intent, Constants.CAMERA_PHOTO);
	}

	private void showDate() {
		AppUtil.getDate(NewObservationActivity.this, new DateListener() {
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onSelectedDate(Date date) {
				Date currentDate=Calendar.getInstance().getTime();
				if(date.after(currentDate)){
					AppUtil.showDialog(getString(R.string.valid_date), NewObservationActivity.this);
					((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
				}
				else{
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
					mSelectedDateStr=sdf.format(date);
					mSelectedDate=date;
					((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
				}
			}

			@Override
			public void onCancelClicked() {
			}
			
		});
	}
	
	private void showCategoryDialogNew() {
		final ArrayList<String> categoryListStr=new ArrayList<String>();
		final ArrayList<Category> categoryList=(ArrayList<Category>) CategoriesTable.getAllCategories(NewObservationActivity.this);
		if(categoryList!=null && categoryList.size()>0){
			categoryList.remove(0);
			for(int i=0;i<categoryList.size();i++){
				categoryListStr.add(categoryList.get(i).getName());
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewObservationActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, categoryListStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
        builder.setTitle(R.string.select_category);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSelectedCategory=categoryList.get(which);
				Log.d("NewSighingActivity", "Category selected: "+categoryListStr.get(which));
				((TextView)findViewById(R.id.category)).setText(""+categoryListStr.get(which));
			}
		});
	
        AlertDialog alert = builder.create();
        alert.show();  
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == Constants.CAMERA_PHOTO){
			MyApplication myApplication = (MyApplication)getApplication();
            mFileUri=myApplication.getPicUri();
            Log.d("OnActivityResult","*** Uri: "+mFileUri.toString());
			Bitmap bmp = convertUriToBitmap(mFileUri, 800, 800);
        	mFileUri = getImageUri(this, bmp);
        	
        	displayExifData(mFileUri); //display date and location from location
        	
    		
        	if(mSelectedImagePos<mResourceList.size()){
        		mResourceList.get(mSelectedImagePos).setDirty(true);
        		mResourceList.get(mSelectedImagePos).setUri(mFileUri.toString());
			}
        	else{
				Resource r=new Resource();
				r.setDirty(true);
				r.setUri(mFileUri.toString());
				mResourceList.add(r);
			}
        	
        	//mImageUrls.add(mFileUri);
    		String path = AppUtil.getRealPathFromURI(mFileUri, NewObservationActivity.this);
    		//This is just for display image in ImageView
    		File imageFile = new File(path);
    		//Rotate if necessary
    		int rotate=AppUtil.getCameraPhotoOrientation(NewObservationActivity.this, mFileUri, path);
    		Matrix matrix = new Matrix();
    		matrix.postRotate(rotate);
    		
    		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mFileUri));
    		
    		//handle Out of memory error
    		Bitmap bmpDisplay=AppUtil.decodeFile(imageFile,100,100);
    		
    		//Rotate BMP
    		Bitmap rotatedBitmap = Bitmap.createBitmap(bmpDisplay, 0, 0, bmpDisplay.getWidth(), bmpDisplay.getHeight(), matrix, true);
    		mSelectedImageView.setImageBitmap(rotatedBitmap);
    		mSelectedButton.setText(getString(R.string.add_photo));
        }
		else if(resultCode == RESULT_OK && requestCode == Constants.GALLERY_PHOTO){
			Uri selectedImage = data.getData();
			
			displayExifData(selectedImage); //display date and location from location
						
    		//Put Uri into arraylist
    		
			if(mSelectedImagePos<mResourceList.size()){
        		mResourceList.get(mSelectedImagePos).setDirty(true);
        		mResourceList.get(mSelectedImagePos).setUri(selectedImage.toString());
			}
        	else{
				Resource r=new Resource();
				r.setDirty(true);
				r.setUri(selectedImage.toString());
				mResourceList.add(r);
			}
			
			
    		File imageFile = new File(AppUtil.getRealPathFromURI(selectedImage, getApplicationContext()));
    		//Rotate if necessary
    		int rotate=AppUtil.getCameraPhotoOrientation(NewObservationActivity.this, selectedImage, AppUtil.getRealPathFromURI(selectedImage,getApplicationContext()));
    		Matrix matrix = new Matrix();
    		matrix.postRotate(rotate);
    		
    		//handle Out of memory error
    		Bitmap bmp=AppUtil.decodeFile(imageFile,100,100);
    		
    		//Rotate BMP
    		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    		//mSelectedImageView.setBackgroundDrawable(new ColorDrawable(0));
            mSelectedImageView.setImageBitmap(rotatedBitmap);      
		}
		else if(resultCode == RESULT_OK && requestCode == Constants.LOCATION_ADDRESS){
			String address=data.getStringExtra(Constants.ADDRESS);
            mLat=data.getDoubleExtra(Constants.LAT, 0);
            mLng=data.getDoubleExtra(Constants.LNG, 0);
            mAddress=address;
            ((TextView)findViewById(R.id.address)).setText(address);
		}
    }
	
	@SuppressLint("SimpleDateFormat")
	private void displayExifData(Uri uri) {
		try{
			ExifInterface exif = new ExifInterface(AppUtil.getRealPathFromURI(uri, getApplicationContext()));
			Log.d("New Sigting","*****image date: "+exif.getAttribute(ExifInterface.TAG_DATETIME));
			String exif_date=exif.getAttribute(ExifInterface.TAG_DATETIME);
			if(exif_date!=null){
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
				mSelectedDateStr=sdf.format(AppUtil.getDateFromString(exif_date, DATE_FORMAT1));
				mSelectedDate=AppUtil.getDateFromString(exif_date, DATE_FORMAT1);
				((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
			}
			else{
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
				mSelectedDateStr=sdf.format(new Date());
				mSelectedDate=new Date();
				((TextView)findViewById(R.id.date_sighted)).setText(mSelectedDateStr);
			}
			
			 String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			 String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			 String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			 String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			
			 Log.d("New Sigting","*****latitude: "+LATITUDE);
			 Log.d("New Sigting","*****latitude ref: "+LATITUDE_REF);
			 Log.d("New Sigting","*****lng: "+LONGITUDE);
			 Log.d("New Sigting","*****lng ref: "+LONGITUDE_REF);
			 
			 double latitude=0.0,longitude=0.0; 
			 if((LATITUDE !=null) && (LATITUDE_REF !=null)&& (LONGITUDE != null)&& (LONGITUDE_REF !=null)){			 
				  if(LATITUDE_REF.equals("N")){
					  latitude = AppUtil.convertToDegree(LATITUDE);
				  }
				  else{
					  latitude = 0 - AppUtil.convertToDegree(LATITUDE);
				  }
	
				  if(LONGITUDE_REF.equals("E")){
					  longitude = AppUtil.convertToDegree(LONGITUDE);
				  }
				  else{
					  longitude = 0 - AppUtil.convertToDegree(LONGITUDE);
				  }
			}
			if(latitude!=0.0 && longitude!=0.0){
				((TextView)findViewById(R.id.address)).setText(getResources().getString(R.string.loading));
				ReveseGeoCodeUtil.doReverseGeoCoding(NewObservationActivity.this, latitude,longitude, mHttpRetriever, new ReveseGeoCodeListener() {
					
					@Override
					public void onReveseGeoCodeSuccess(double lat, double lng, String address) {
						((TextView)findViewById(R.id.address)).setText(address);
						mLat=lat;
						mLng=lng;
						mAddress=address;
					}
				});
			} 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	//Bitmap to uri
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()), null);
		  return Uri.parse(path);
	}
	
	public Bitmap convertUriToBitmap(Uri selectedImage,int width, int height){
		File imageFile = new File(AppUtil.getRealPathFromURI(selectedImage, getApplicationContext()));
		//Rotate if necessary
		int rotate=AppUtil.getCameraPhotoOrientation(NewObservationActivity.this, selectedImage, AppUtil.getRealPathFromURI(selectedImage,getApplicationContext()));
		Matrix matrix = new Matrix();
		matrix.postRotate(rotate);
		
		//handle Out of memory error
		Bitmap bmp=AppUtil.decodeFile(imageFile, width, height);
		
		//Rotate BMP
		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		return rotatedBitmap;
	}

	/*@Override
	public void onSelectedLocation(String selectedLocation) {
		
	}

	@Override
	public void onFetchLatLngForSelectedLoc(double lat, double lng) {
		
	}*/

}	