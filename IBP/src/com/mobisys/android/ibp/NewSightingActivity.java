package com.mobisys.android.ibp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.mobisys.android.ibp.database.CategoriesTable;
import com.mobisys.android.ibp.models.Category;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.AppUtil.DateListener;

public class NewSightingActivity extends BaseSlidingActivity{

	private String mCategorySelected=null;
	private int mSelectedImagePos;
	private ImageView mSelectedImageView;
	private Button mSelectedButton;
	private Uri mFileUri;
	private ArrayList<Uri> mImageUrls;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_sighting);
		mImageUrls = new ArrayList<Uri>();
		initActionTitle(getString(R.string.new_sighting));
		initScreen();
	}

	private void initScreen() {
		((CheckBox)findViewById(R.id.chk_help_identify)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
			    if(!isChecked){
			    	findViewById(R.id.edit_common_name).setVisibility(View.VISIBLE);
			    	findViewById(R.id.edit_sci_name).setVisibility(View.VISIBLE);

			    }else{
			    	findViewById(R.id.edit_common_name).setVisibility(View.GONE);
			    	findViewById(R.id.edit_sci_name).setVisibility(View.GONE);
			    }
		}});
		
		((Button)findViewById(R.id.btn_category)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCategoryDialog();
			}
		});
		
		((Button)findViewById(R.id.btn_date_sighted)).setOnClickListener(new View.OnClickListener() {
			
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
	
	}
	
	private void showSelectDialog() {
		final CharSequence[] methods = {getString(R.string.gallery), getString(R.string.camera)};
		 
        AlertDialog.Builder builder = new AlertDialog.Builder(NewSightingActivity.this);
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
	
	protected void startCameraActivity() {
		File dir = new File(AppUtil.getDir());
		if(!dir.exists()) dir.mkdirs();
		String filename=String.valueOf(System.currentTimeMillis());
		
		File file = new File(AppUtil.getImagePath(filename));
		mFileUri = Uri.fromFile(file);
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
		startActivityForResult(intent, Constants.CAMERA_PHOTO);
	}

	protected void showDate() {
		AppUtil.getDate(NewSightingActivity.this, new DateListener() {
			
			@Override
			public void onSelectedDate(Date date) {
				//mSelectedDate=date;
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
				((Button)findViewById(R.id.btn_date_sighted)).setText(sdf.format(date));
			}

			@Override
			public void onCancelClicked() {
			}
			
		});
	}
	
	protected void showCategoryDialog() {
		final ArrayList<String> categoryListStr=new ArrayList<String>();
		final ArrayList<Category> categoryList=(ArrayList<Category>) CategoriesTable.getAllCategories(NewSightingActivity.this);
		if(categoryList!=null && categoryList.size()>0){
			for(int i=0;i<categoryList.size();i++){
				categoryListStr.add(categoryList.get(i).getName());
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewSightingActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, categoryListStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(NewSightingActivity.this);
        builder.setTitle(R.string.select_category);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCategorySelected=categoryListStr.get(which);
				Log.d("NewSighingActivity", "Category selected: "+mCategorySelected);
				((Button)findViewById(R.id.btn_category)).setText(""+mCategorySelected);
			}
		});
	
        AlertDialog alert = builder.create();
        alert.show();  
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == Constants.CAMERA_PHOTO){
        	Bitmap bmp = convertUriToBitmap(mFileUri, 800, 800);
        	mFileUri = getImageUri(this, bmp);
        	
    		if(mImageUrls.size()==0) mImageUrls.add(mFileUri);
		    else{ 
		    	try {
    		    	if(mImageUrls.get(mSelectedImagePos)!=null)
    		    		mImageUrls.set(mSelectedImagePos,mFileUri);
		    	}
		    	catch ( IndexOutOfBoundsException e ) {
		    		mImageUrls.add(mSelectedImagePos, mFileUri);
		    	}
		    }
    		
        	//mImageUrls.add(mFileUri);
    		String path = AppUtil.getRealPathFromURI(mFileUri, NewSightingActivity.this);
    		//This is just for display image in ImageView
    		File imageFile = new File(path);
    		//Rotate if necessary
    		int rotate=AppUtil.getCameraPhotoOrientation(NewSightingActivity.this, mFileUri, path);
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
    		
    		//Put Uri into arraylist
    		if(mImageUrls.size()==0)
    			mImageUrls.add(selectedImage);
		    else{ 
		    	try {
    		    	if(mImageUrls.get(mSelectedImagePos)!=null)
    		    		mImageUrls.set(mSelectedImagePos,selectedImage);
		    	}
		    	catch ( IndexOutOfBoundsException e ) {
		    		mImageUrls.add(mSelectedImagePos, selectedImage);
		    	}
		    }
    		
    		File imageFile = new File(AppUtil.getRealPathFromURI(selectedImage, getApplicationContext()));
    		//Rotate if necessary
    		int rotate=AppUtil.getCameraPhotoOrientation(NewSightingActivity.this, selectedImage, AppUtil.getRealPathFromURI(selectedImage,getApplicationContext()));
    		Matrix matrix = new Matrix();
    		matrix.postRotate(rotate);
    		
    		//handle Out of memory error
    		Bitmap bmp=AppUtil.decodeFile(imageFile,100,100);
    		
    		//Rotate BMP
    		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    		//mSelectedImageView.setBackgroundDrawable(new ColorDrawable(0));
            mSelectedImageView.setImageBitmap(rotatedBitmap);      
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
		int rotate=AppUtil.getCameraPhotoOrientation(NewSightingActivity.this, selectedImage, AppUtil.getRealPathFromURI(selectedImage,getApplicationContext()));
		Matrix matrix = new Matrix();
		matrix.postRotate(rotate);
		
		//handle Out of memory error
		Bitmap bmp=AppUtil.decodeFile(imageFile, width, height);
		
		//Rotate BMP
		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		return rotatedBitmap;
	}
}	