package com.mobisys.android.ibp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kbeanie.imagechooser.api.ImageChooserActivity;
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
import com.mobisys.android.ibp.widget.MImageLoader;

public class NewObservationActivity extends BaseSlidingActivity implements ConnectionCallbacks, OnConnectionFailedListener{

	private static final int IMAGE_CHOOSER = 100;
	
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private int mSelectedImagePos;
	private ImageView mSelectedImageView;
	private ArrayList<Resource> mResourceList;
	private double mLat=0, mLng=0;
	private HttpRetriever mHttpRetriever;
	private Category mSelectedCategory=null;
	private String mSelectedDateStr=null;
	private Date mSelectedDate=null;
	private String mAddress;
	private com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView mCommNameAutoText, mSciNameAutoText;
	private String DATE_FORMAT1="yyyy:MM:dd hh:mm:ss";
	private ObservationInstance mObv;
	private Dialog mPG;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_observation);
		mHttpRetriever = new HttpRetriever();
		mObv=getIntent().getParcelableExtra(ObservationInstance.ObsInstance);
		buildGoogleApiClient();
		initActionTitle(getString(R.string.new_observation));
		initScreen();
		if(mObv!=null){
			mResourceList = mObv.getResource();
			initEditScreen();
		} else {
			mResourceList = new ArrayList<Resource>();
		}
	}

	 @Override
	 protected void onStart() {
       super.onStart();
       mGoogleApiClient.connect();
	 }
	 
	 @Override
	 protected void onStop() {
       super.onStop();
       if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
	 }
	 
	@SuppressLint("SimpleDateFormat")
	private void initScreen() {
		mCommNameAutoText = (com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.edit_common_name);
		mCommNameAutoText.setAutoCompleteUrl(Request.AUTO_COMPLETE_URL);
		mCommNameAutoText.setParser(new AutoCompleteResponseParserInterface() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String response) {
				return AppUtil.parseAutocompleteResponse(response);
			}
		});

		mSciNameAutoText = (com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.edit_sci_name);
		mSciNameAutoText.setAutoCompleteUrl(Request.AUTO_COMPLETE_URL);
		mSciNameAutoText.setParser(new AutoCompleteResponseParserInterface() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String response) {
				return AppUtil.parseAutocompleteResponse(response);
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
				showImageChooserActivity();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_2)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 1;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_2);
				showImageChooserActivity();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_3)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 2;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_3);
				showImageChooserActivity();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_4)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 3;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_4);
				showImageChooserActivity();
			}
		});
		
		((Button)findViewById(R.id.btn_add_photo_5)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelectedImagePos = 4;
				mSelectedImageView=(ImageView)findViewById(R.id.species_image_5);
				showImageChooserActivity();
			}
		});
		
		((Button)findViewById(R.id.btn_gps)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEditLocationOptions();
			}
		});
		findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				enableDisableLocationProgress(false);
				stopLocationUpdates();
			}
		});
		
		//if(mObv==null || isFromStatusScreen){
		if(mObv==null) {
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
				validateParams();
			}
		});

		Location location = AppUtil.getCurrentLocation(this);
		if(mObv==null){
			mLat=location.getLatitude();
			mLng=location.getLongitude();
			reverseGeocodeLocation(mLat, mLng);
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
						AppUtil.setUriBitmap((ImageView)findViewById(R.id.species_image_1), Uri.parse(uri), NewObservationActivity.this, 100);
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
						AppUtil.setUriBitmap((ImageView)findViewById(R.id.species_image_2), Uri.parse(uri), NewObservationActivity.this, 100);
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
						AppUtil.setUriBitmap((ImageView)findViewById(R.id.species_image_3), Uri.parse(uri), NewObservationActivity.this,100);
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
						AppUtil.setUriBitmap((ImageView)findViewById(R.id.species_image_4), Uri.parse(uri), NewObservationActivity.this,100);
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
						AppUtil.setUriBitmap((ImageView)findViewById(R.id.species_image_5), Uri.parse(uri), NewObservationActivity.this,100);
						((Button)findViewById(R.id.btn_add_photo_5)).setText(getString(R.string.edit_photo));
					}
					else ((Button)findViewById(R.id.btn_add_photo_5)).setText(getString(R.string.add_photo));
				}
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
			if(mPG!=null && mPG.isShowing()) mPG.dismiss();
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
		createSaveParamObject();
	}

	private void createSaveParamObject() {
		ObservationInstance sp=new ObservationInstance();
		if(mObv!=null){
			sp.setId(mObv.getId());
			if(mObv.getServer_id()!=0)
				sp.setServer_id(mObv.getServer_id());
		}	
		else
			sp.setId(-1);
		
		sp.setGroup(mSelectedCategory);  //set object
		sp.setHabitatId(Constants.HABITATE_ID_STAGING);
		sp.setFromDate(mSelectedDate);
		if(!Preferences.LOCATION_DEBUG){
			sp.setPlaceName(mAddress);
		}
		else sp.setPlaceName(Constants.DEFAULT_ADDRESS);
		
		if(Preferences.LOCATION_DEBUG){ 
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
		sp.setNotes(notes);
		sp.setResource(mResourceList);  // image list
		
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
		
		ObservationRequestQueue.getInstance().executeAllSubmitRequests(NewObservationActivity.this);
		if(AppUtil.isNetworkAvailable(NewObservationActivity.this)) Toast.makeText(NewObservationActivity.this, R.string.observation_msg, Toast.LENGTH_SHORT).show();
		else Toast.makeText(NewObservationActivity.this, R.string.observation_msg_without_internet, Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(NewObservationActivity.this, HomeActivity.class);
		startActivity(i);
		finish();
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
	
	private void showImageChooserActivity() {
		Intent intent = new Intent(this, ImageChooserActivity.class);
		startActivityForResult(intent,IMAGE_CHOOSER);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == IMAGE_CHOOSER){
    		if(resultCode == RESULT_OK) {
        		String path = data.getStringExtra(ImageChooserActivity.INTENT_IMAGE_PATH);
        		Uri selectedImage = Uri.fromFile(new File(path));

        		onImageSelected(selectedImage);
        		MImageLoader.displayImage(this, selectedImage.toString(), mSelectedImageView, R.drawable.user_stub);
    		} else {
        		if(data!=null){
            		String reason = data.getStringExtra(ImageChooserActivity.INTENT_ERROR_MESSAGE);
            		AppUtil.showErrorDialog(reason, this);
        		}
    		}
    	}
		else if(resultCode == RESULT_OK && requestCode == Constants.LOCATION_ADDRESS){
			String address=data.getStringExtra(Constants.ADDRESS);
            mLat=data.getDoubleExtra(Constants.LAT, 0);
            mLng=data.getDoubleExtra(Constants.LNG, 0);
            mAddress=address;
            Log.d("NewObservation", "Address: "+mAddress+" Latitude: "+mLat+" Longitude:"+mLng);
            ((TextView)findViewById(R.id.address)).setText(address);
		}
    }
	
	private void onImageSelected(Uri imageUri){
		//Put Uri into arraylist
		if(mSelectedImagePos<mResourceList.size()){
    		mResourceList.get(mSelectedImagePos).setDirty(true);
    		mResourceList.get(mSelectedImagePos).setUri(imageUri.toString());
		}
    	else{
			Resource r=new Resource();
			r.setDirty(true);
			r.setUri(imageUri.toString());
			mResourceList.add(r);
		}
		
		showImportMetaDataDialog(imageUri);
	}
	
	private void showEditLocationOptions(){
		String[] options = {getString(R.string.get_current_location), getString(R.string.edit_location)};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewObservationActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, options);
		AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
        builder.setTitle(R.string.edit_location);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					enableDisableLocationProgress(true);
					getCurrentLocation();
				} else if(which == 1){
					Intent i=new Intent(NewObservationActivity.this, SelectLocationActivity.class);
					i.putExtra(Constants.LAT, mLat);
					i.putExtra(Constants.LNG, mLng);
					startActivityForResult(i, Constants.LOCATION_ADDRESS);
				}
			}
		});
	
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void getCurrentLocation(){
		startLocationUpdates();
	}
	
	private void showImportMetaDataDialog(final Uri imageUri){
		AlertDialog.Builder builder = new AlertDialog.Builder(NewObservationActivity.this);
        builder.setTitle(R.string.import_metadata);
        builder.setMessage(getString(R.string.msg_import_metadata));
	
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				displayExifData(imageUri);
			}
		});
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void enableDisableLocationProgress(boolean enable) {
		if(enable){
			findViewById(R.id.btn_gps).setVisibility(View.GONE);
			findViewById(R.id.search_location_progress).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.btn_gps).setVisibility(View.VISIBLE);
			findViewById(R.id.search_location_progress).setVisibility(View.GONE);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void displayExifData(Uri uri) {
		try{
			ExifInterface exif = new ExifInterface(AppUtil.getRealPathFromURI(uri, getApplicationContext()));
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
			 
			reverseGeocodeLocation(latitude, longitude);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void reverseGeocodeLocation(double latitude, double longitude){
		if(latitude!=0.0 && longitude!=0.0){
			//mLat = latitude;
			//mLng = longitude;
			String defaultAddress = String.format(NewObservationActivity.this.getResources().getString(R.string.reverse_lookup_error_address), latitude, longitude);
			((TextView)findViewById(R.id.address)).setText(defaultAddress);
			ReveseGeoCodeUtil.doReverseGeoCoding(NewObservationActivity.this, latitude,longitude, mHttpRetriever, new ReveseGeoCodeListener() {
				
				@Override
				public void onReveseGeoCodeSuccess(boolean success, double lat, double lng, String address) {
					enableDisableLocationProgress(false);
					((TextView)findViewById(R.id.address)).setText(address);
					mLat=lat;
					mLng=lng;
					mAddress=address;
				}
			});
		} 
	}

	private void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	private void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
	}
	
	private void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
	}
	
	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			stopLocationUpdates();
			AppUtil.saveCurrentLocation(NewObservationActivity.this, location);
			if(location!=null) reverseGeocodeLocation(location.getLatitude(), location.getLongitude());
			else{
				enableDisableLocationProgress(false);
				Toast.makeText(NewObservationActivity.this, getString(R.string.cannot_get_current_location), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		createLocationRequest();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}
}