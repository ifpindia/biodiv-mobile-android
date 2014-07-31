package com.mobisys.android.ibp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.mobisys.android.ibp.database.ObservationParamsTable;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.models.ObservationParams;
import com.mobisys.android.ibp.models.ObservationParams.StatusType;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ObservationRequestQueue {

	private static ObservationRequestQueue mSightingRequestQueue = null;
	
	public static ObservationRequestQueue getInstance(){
		if(mSightingRequestQueue==null){
			mSightingRequestQueue = new ObservationRequestQueue();
		}
		
		return mSightingRequestQueue;
	}

	private boolean mIsRunning;
	
	private ObservationRequestQueue(){
	}
	
	public void executeAllSubmitRequests(Context context) {
		if(!mIsRunning){
			ObservationParams sp = ObservationParamsTable.getFirstRecord(context);
			observationMethods(false, sp, context);
		}
	}

	/*public void executeSingleCheckoutRequests(Context context, Order order){
		//TODO: Get checkoutParams from CheckoutParamsTable where order_id=order.getId()
		CheckoutParams cp = CheckoutParamsTable.getRecord(context, order);
		checkoutMethods(true, cp, context);
	}*/
	
	private void observationMethods(boolean single, ObservationParams sp, Context context) {
		if(sp == null){
			mIsRunning=false;
			return;
		}
		
		mIsRunning = true;
		submitObservation(single, sp, context);
	}

	
	private void submitObservation(final boolean single, final ObservationParams sp, final Context context) {
		Bundle b=new Bundle();	
		b.putString(Request.SPECIES_GROUP_ID, String.valueOf(sp.getGroupId()));
		b.putString(Request.HABITAT_ID, String.valueOf(sp.getHabitatId()));
		b.putString(Request.FROM_DATE, sp.getFromDate());
		b.putString(Request.PLACE_NAME, sp.getPlaceName());
		b.putString(Request.AREAS, sp.getAreas());
		if(sp.getCommonName().length()>0) b.putString(Request.COMMON_NAME, sp.getCommonName());
		if(sp.getRecoName().length()>0) b.putString(Request.SCI_NAME, sp.getRecoName());
		b.putString(Request.RESOURCE_LIST_TYPE, Constants.RESOURCE_LIST_TYPE);
		b.putString(Request.AGREE_TERMS, Constants.AGREE_TERMS_VALUE);
		ArrayList<String> resources=new ArrayList<String>();
	    String[] items = sp.getResources().split(",");
	    for (String item : items){
	        resources.add(item);
	    }
		
		ArrayList<String> imageType=new ArrayList<String>();
		String[] imageT = sp.getImageType().split(",");
	    for (String item : imageT){
	    	imageType.add(item);
	    }
	    
		uploadImage(single,b, context, resources, imageType, sp);
	}

	private void uploadImage(final boolean single,final Bundle b,final Context context, ArrayList<String> imageStringPath, ArrayList<String> imageType,final ObservationParams sp) {
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		if(imageStringPath!=null && imageStringPath.size()>0){
			for(int i=0;i<imageStringPath.size();i++){
				FileBody bab;
				if(imageType.get(i)!=null)
					bab= new FileBody(new File(imageStringPath.get(i)),imageType.get(i)); // image path and image type
				else
					bab= new FileBody(new File(imageStringPath.get(i)),"image/jpeg"); // image p	
				reqEntity.addPart("resources", bab);
			}
		}
	
		try {
			reqEntity.addPart(Request.RESOURCE_TYPE, new StringBody("species.participation.Observation"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		WebService.sendMultiPartRequest(context, Request.METHOD_POST, Request.PATH_UPLOAD_RESOURCE, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseUploadResourceDetail(response,single, b, context, sp);
				//ObservationParamsTable.deleteRowFromTable(context, sp);
				//submitObservation(single, b, context, sp);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				Log.d("NetWorkState", content);
				if(e instanceof UnknownHostException || e instanceof ConnectException){
					mIsRunning = false;
					return;
				}
				sp.setStatus(StatusType.FAILURE);
				ObservationParamsTable.updateRowFromTable(context, sp);
				//ObservationParamsTable.deleteRowFromTable(context, sp);
				if(!single){
					ObservationParams sp_new = ObservationParamsTable.getFirstRecord(context);
					observationMethods(single, sp_new, context);
				}
			}
		}, reqEntity);
	}

	protected void parseUploadResourceDetail(String response, boolean single, Bundle b, Context context, ObservationParams sp) {
		try {
			JSONObject jObj=new JSONObject(response);
			JSONArray jArray=jObj.getJSONObject("observations").getJSONArray("resources");
			if(jArray!=null && jArray.length()>0){
				for(int i=0;i<jArray.length();i++){
					b.putString("file_"+(i+1), jArray.getJSONObject(i).optString("fileName"));
					b.putString("type_"+(i+1), Constants.IMAGE);
					b.putString("license_"+(i+1), "CC_BY");
				}
			}
			//if(Preferences.DEBUG) Log.d("Checkout Params to b send", "****Bundle: "+b);
			submitObservationRequestFinally(single, b, context, sp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void submitObservationRequestFinally(final boolean single,final Bundle b,final Context context,final ObservationParams sp) {
		WebService.sendRequest(context, Request.METHOD_POST, Request.PATH_SAVE_OBSERVATION, b, new WebService.ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				//ObservationParamsTable.deleteRowFromTable(context, sp);
				sp.setStatus(StatusType.SUCCESS);
				ObservationParamsTable.updateRowFromTable(context, sp);
				if(!single){
					ObservationParams cp_new = ObservationParamsTable.getFirstRecord(context);
					observationMethods(single, cp_new, context);
				}
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				Log.d("NetWorkState", content);
				if(e instanceof UnknownHostException || e instanceof ConnectException){
					mIsRunning = false;
					return;
				}
				sp.setStatus(StatusType.FAILURE);
				ObservationParamsTable.updateRowFromTable(context, sp);
				if(!single){
					ObservationParams cp_new = ObservationParamsTable.getFirstRecord(context);
					observationMethods(single, cp_new, context);
				}
			}
		});
	}
	
}
