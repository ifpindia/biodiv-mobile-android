package com.ifp.wikwio.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.DisplayStringInterface;
import com.ifp.wikwio.Constants;
import com.ifp.wikwio.R;
import com.ifp.wikwio.http.HttpUtils;
import com.ifp.wikwio.http.Request;

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
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.widget.DatePicker;
import android.widget.ImageView;

public class AppUtil {

	public static int mYear;
    public static int mMonth;
    public static int mDay;
    public static final String GOOGLE_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";

	public static interface DateListener {
		public void onSelectedDate(Date date);
		public void onCancelClicked();
	}

	public static String getAddressFromGPSData(double lat, double longi, HttpRetriever agent) {
		String request = GOOGLE_GEOCODER + lat + ","
				+ longi + "&sensor=true";
		// Log.d("GeoCoder", request);
		String response = agent.retrieve(request);
		String formattedAddress = "";
		if (response != null) {
			Log.d("GeoCoder", response);
			try {
				JSONObject parentObject = new JSONObject(response);
				JSONArray arrayOfAddressResults = parentObject
						.getJSONArray("results");
				JSONObject addressItem = arrayOfAddressResults.getJSONObject(0);
				formattedAddress = addressItem.getString("formatted_address");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return formattedAddress;
	}

	public static int getDipValue(int param, Context context){
    	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, param, context.getResources().getDisplayMetrics());
    }

	public static String parseErrorResponse(Context context, String content, Throwable e){
		if(content!=null && content.length()>0){
			try {

				Log.d("GeoCoder111111111111111111111111111111111111111111111111111 ", content);				
				JSONObject jobj=new JSONObject(content);
				//String error=jobj.optString("error");
				String message=jobj.optString("message");
				return message;
			} catch (JSONException e1) {
				e1.printStackTrace();
				return "Error occured ";
			}
		}
		else
			return "Error occured ";
	}

	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static boolean emailValidator(String email){
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
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

    public static void setUriBitmap(ImageView iv, Uri uri, Context context,int size){
		Bitmap bmp=null;
		try {
			bmp = AppUtil.getBitmapFromUri(uri, context, size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bmp!=null) iv.setImageBitmap(bmp);
		else iv.setImageResource(R.drawable.user_stub);
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

	public static String GetMimeType(Context context, Uri uriImage){
	    String strMimeType = null;
	    Cursor cursor = context.getContentResolver().query(uriImage,
	                        new String[] { MediaStore.MediaColumns.MIME_TYPE },
	                        null, null, null);

	    if (cursor != null && cursor.moveToNext()){
	        strMimeType = cursor.getString(0);
	    }

	    return strMimeType;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(String str, String date_format){
		SimpleDateFormat  format = new SimpleDateFormat(date_format);
		try {
		    Date date = format.parse(str);
		    System.out.println(date);
		    return date;
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getStringFromDate(Date date, String date_format){
		SimpleDateFormat  dateformat = new SimpleDateFormat(date_format);
		String datetime = dateformat.format(date);
		return datetime;
	}

	public static Drawable getListSelectorNew(Context context){
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
				context.getResources().getDrawable(R.drawable.list_item_bg_o));
		states.addState(new int[] {android.R.attr.state_focused},
				context.getResources().getDrawable(R.drawable.list_item_bg_o));
		states.addState(new int[] { },
				context.getResources().getDrawable(R.drawable.list_item_bg));

		return states;
	}

	public static String getSpeciesUrl(String speciesId) {
		return "http://"+HttpUtils.stageOrProdBaseURL()+Request.PATH_SHOW_SPECIES_DETAIL+speciesId;
	}

	@SuppressLint("UseValueOf")
	public static Float convertToDegree(String stringDMS){
		 Float result = null;
		 String[] DMS = stringDMS.split(",", 3);

		 String[] stringD = DMS[0].split("/", 2);
		    Double D0 = new Double(stringD[0]);
		    Double D1 = new Double(stringD[1]);
		    Double FloatD = D0/D1;

		 String[] stringM = DMS[1].split("/", 2);
		 Double M0 = new Double(stringM[0]);
		 Double M1 = new Double(stringM[1]);
		 Double FloatM = M0/M1;

		 String[] stringS = DMS[2].split("/", 2);
		 Double S0 = new Double(stringS[0]);
		 Double S1 = new Double(stringS[1]);
		 Double FloatS = S0/S1;

		    result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

		 return result;
	};

	public static Bitmap getBitmapFromUri(Uri uri, Context context, int size) throws FileNotFoundException, IOException{
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
		
	private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
		
	public static ArrayList<DisplayStringInterface> parseAutocompleteResponse(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			response = jsonObj.getJSONObject("model").getJSONArray("instanceList").toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
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
	
	public static void saveCurrentLocation(Context context, Location location){
		if(location!=null){
			SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LAT, String.valueOf(location.getLatitude()));
			SharedPreferencesUtil.putSharedPreferencesString(context, Constants.LNG, String.valueOf(location.getLongitude()));
		}
	}
	
	public static Location getCurrentLocation(Context context){
		String latStr = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LAT, null);
		String lngStr = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LNG, null);
		Location location = new Location("");
		if(latStr!=null && lngStr!=null){
			location.setLatitude(Double.parseDouble(latStr));
			location.setLongitude(Double.parseDouble(lngStr));
		} else {
			location.setLatitude(Double.parseDouble(Constants.DEFAULT_LAT));
			location.setLongitude(Double.parseDouble(Constants.DEFAULT_LNG));
		}
		
		return location;
	}
	
	/*public static Location getAppLastKnownLocationLocation(Context context){
        String lat = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LAT, null);
        String lng = SharedPreferencesUtil.getSharedPreferencesString(context, Constants.LNG, null);
        if(lat!=null && lng!=null){
            Location location = new Location("");
            location.setLatitude(Double.parseDouble(lat));
            location.setLongitude(Double.parseDouble(lng));
            return location;
        } else {
            Location location = new Location("");
            location.setLatitude(Double.parseDouble(Constants.DEFAULT_LAT));
			location.setLongitude(Double.parseDouble(Constants.DEFAULT_LNG));
            return location;
        }
    }*/
}
