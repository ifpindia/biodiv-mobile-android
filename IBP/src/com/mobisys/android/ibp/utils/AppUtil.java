package com.mobisys.android.ibp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.ibp.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.widget.DatePicker;

public class AppUtil {

	public static int mYear;
    public static int mMonth;
    public static int mDay;
    
	
	public static interface DateListener {
		public void onSelectedDate(Date date);
		public void onCancelClicked();
	}
	
	public static int getDipValue(int param, Context context){
    	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, param, context.getResources().getDisplayMetrics());
    }
	
	public static String parseErrorResponse(Context context, String content, Throwable e){
		try {
			JSONObject jobj=new JSONObject(content);
			//String error=jobj.optString("error");
			String message=jobj.optString("message");
			return message;
		} catch (JSONException e1) {
			e1.printStackTrace();
			return "Error occured ";
		}
	}
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static boolean emailValidator(String email){
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
	
	public static void showErrorDialog(String msg, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.error);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public static void showDialog(String message , Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.alert);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public static Drawable getPressedStateDrawable(Context context){
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
				context.getResources().getDrawable(R.color.orange_color));
		states.addState(new int[] {android.R.attr.state_focused},
				context.getResources().getDrawable(R.color.orange_color));
		states.addState(new int[] { },new ColorDrawable(0));

		return states;
	}

	public static void getDate(final Context context,final DateListener listener) {
		final Calendar c = Calendar.getInstance();
    	//mListener = listener;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        
       final  DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    mYear = selectedYear;
                    mMonth = selectedMonth;
                    mDay = selectedDay;
                    Calendar cal = Calendar.getInstance();
                    cal.set(mYear, mMonth, mDay);
                    listener.onSelectedDate(cal.getTime());
                    Log.d("AppUtil","selected date: "+mDay+"/"+(mMonth+1)+"/"+mYear);
            }
        };
        
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, datePickerListener,mYear, mMonth, mDay);
        
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.cancel();
                            listener.onCancelClicked();
                        }
                    }
                });

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,context.getResources().getString(R.string.pick_date), new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(DialogInterface dialog,
                    int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                   // isOkayClicked = true;
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    datePickerListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                	dialog.cancel();
                }
            }
        });
        datePickerDialog.setCancelable(true);
        datePickerDialog.show();
	}
	
	public static String getDir(){
		return Environment.getExternalStorageDirectory()+File.separator+"IBP";
	}
    
    public static String getImagePath(String prefix){
		return Environment.getExternalStorageDirectory()+File.separator+"IBP"+File.separator+"ibp_"+prefix+".jpg";
	}
	
    public static Bitmap decodeFile(File f, int width, int height){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        //final int REQUIRED_SIZE=100;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=width && o.outHeight/scale/2>=height)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
 
    public static String getRealPathFromURI(Uri contentURI, Context mContext) {
	    Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { 
	        return contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        return cursor.getString(idx); 
	    }
	}
    
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
	     int rotate = 0;
	     try {
	         //context.getContentResolver().notifyChange(imageUri, null);
	         File imageFile = new File(imagePath);
	         ExifInterface exif = new ExifInterface(
	                 imageFile.getAbsolutePath());
	         int orientation = exif.getAttributeInt(
	                 ExifInterface.TAG_ORIENTATION,
	                 ExifInterface.ORIENTATION_NORMAL);

	         switch (orientation) {
	         case ExifInterface.ORIENTATION_ROTATE_270:
	             rotate = 270;
	             break;
	         case ExifInterface.ORIENTATION_ROTATE_180:
	             rotate = 180;
	             break;
	         case ExifInterface.ORIENTATION_ROTATE_90:
	             rotate = 90;
	             break;
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	    return rotate;
	 }
}