package com.ifp.wikwio;

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

import com.ifp.wikwio.database.ObservationInstanceTable;
import com.ifp.wikwio.http.HttpUtils;
import com.ifp.wikwio.http.Request;
import com.ifp.wikwio.http.WebService;
import com.ifp.wikwio.http.WebService.ResponseHandler;
import com.ifp.wikwio.models.ObservationInstance;
import com.ifp.wikwio.models.Resource;
import com.ifp.wikwio.models.ObservationInstance.StatusType;
import com.ifp.wikwio.utils.AppUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
			ObservationInstance sp = ObservationInstanceTable.getFirstRecord(context);
			observationMethods(false, sp, context);
		}
	}

	/*public void executeSingleCheckoutRequests(Context context, Order order){
		//TODO: Get checkoutParams from CheckoutParamsTable where order_id=order.getId()
		CheckoutParams cp = CheckoutParamsTable.getRecord(context, order);
		checkoutMethods(true, cp, context);
	}*/
	
	private void observationMethods(boolean single, ObservationInstance sp, Context context) {
		if(sp == null){
			mIsRunning=false;
			return;
		}
		
		mIsRunning = true;
		submitObservation(single, sp, context);
	}

	
	private void submitObservation(final boolean single, final ObservationInstance sp, final Context context) {
		Bundle b=new Bundle();
		if(sp.getId()!=-1)
			b.putString(Request.OBV_ID, String.valueOf(sp.getId()));
		
		b.putString(Request.SPECIES_GROUP_ID, String.valueOf(sp.getGroup().getId()));
		b.putString(Request.HABITAT_ID, String.valueOf(sp.getHabitatId()));
		String date=AppUtil.getStringFromDate(sp.getFromDate(), Constants.DATE_FORMAT);
		b.putString(Request.FROM_DATE, date);
		b.putString(Request.PLACE_NAME, sp.getPlaceName());
		b.putString(Request.AREAS, sp.getAreas());
		b.putString(Request.NOTES, sp.getNotes());
		if(sp.getMaxVotedReco().getCommonName().length()>0) b.putString(Request.COMMON_NAME, sp.getMaxVotedReco().getCommonName());
		if(sp.getMaxVotedReco().getScientificName().length()>0) b.putString(Request.SCI_NAME, sp.getMaxVotedReco().getScientificName());
		b.putString(Request.RESOURCE_LIST_TYPE, Constants.RESOURCE_LIST_TYPE);
		b.putString(Request.AGREE_TERMS, Constants.AGREE_TERMS_VALUE);
		
		if(sp.getUserGroupsList()!=null && sp.getUserGroupsList().length()>0)
			b.putString(Request.USER_GROUP_LIST, sp.getUserGroupsList());
		
		ArrayList<String> imageStringPath=new ArrayList<String>();
		ArrayList<String> mImageType=new ArrayList<String>();
		ArrayList<Resource> mResourceList=new ArrayList<Resource>();
		mResourceList.addAll(sp.getResource());
		
		if(mResourceList!=null && mResourceList.size()>0){
			for(int i=0;i<mResourceList.size();i++){
				if(mResourceList.get(i).getUri()!=null && mResourceList.get(i).isDirty()){ //while edit add uri and url to imagepath
					String imagepath=AppUtil.getRealPathFromURI(Uri.parse(mResourceList.get(i).getUri()), context);
					if(Preferences.DEBUG) Log.d("ObsRequestQ", "***image path:"+imagepath);
		    		imageStringPath.add(imagepath);
		    		
		    		String imageType=AppUtil.GetMimeType(context, Uri.parse(mResourceList.get(i).getUri()));
		    		if(Preferences.DEBUG) Log.d("ObsRequestQ", "***image type: "+imageType);
		    		mImageType.add(imageType);
				}
				else {
					if(mResourceList.get(i).getUrl()!=null){
						imageStringPath.add(mResourceList.get(i).getUrl());
						mImageType.add("null");
					}
				}	
			}
		}
		uploadImage(single, b, context, imageStringPath, mImageType, sp);
	}

	private void uploadImage(final boolean single,final Bundle b,final Context context,final ArrayList<String> imageStringPath, ArrayList<String> imageType,final ObservationInstance sp) {
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		if(b!=null) Log.d("ObservationRequestQueue", "Params: "+b.toString());
		int countUri=0;
		for(int i=0;i<imageStringPath.size();i++){
			if(!imageStringPath.get(i).contains("http://")){
				FileBody bab;
				if(imageType.get(i)!=null)
					bab= new FileBody(new File(imageStringPath.get(i)),imageType.get(i)); // image path and image type
				else
					bab= new FileBody(new File(imageStringPath.get(i)),"image/jpeg"); // image p	
				reqEntity.addPart("resources", bab);
				
				++countUri;
			}
		}
		
		// if imagestring path has no Image url's.
		if(countUri!=0){
			try {
				reqEntity.addPart(Request.RESOURCE_TYPE, new StringBody("species.participation.Observation"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			WebService.sendMultiPartRequest(context, Request.METHOD_POST, Request.PATH_UPLOAD_RESOURCE, new ResponseHandler() {
				
				@Override
				public void onSuccess(String response) {
					sp.setStatus(StatusType.PROCESSING);
					ObservationInstanceTable.updateRowFromTable(context, sp);
					parseUploadResourceDetail(response,single, b, context, sp, imageStringPath);
				}
				
				@Override
				public void onFailure(Throwable e, String content) {
					Log.d("NetWorkState", content);
					if(e instanceof UnknownHostException || e instanceof ConnectException){
						mIsRunning = false;
						return;
					}
					sp.setStatus(StatusType.FAILURE);
					sp.setMessage(content);
					ObservationInstanceTable.updateRowFromTable(context, sp);
					//ObservationParamsTable.deleteRowFromTable(context, sp);
					if(!single){
						ObservationInstance sp_new = ObservationInstanceTable.getFirstRecord(context);
						observationMethods(single, sp_new, context);
					}
				}
			}, reqEntity);
		
		}
		else{ // if all are url's
			for(int i=0;i<imageStringPath.size();i++){
				b.putString("file_"+(i+1), imageStringPath.get(i).replace("http://"+HttpUtils.stageOrProdBaseURL()+"/biodiv/observations/", ""));
				b.putString("type_"+(i+1), Constants.IMAGE);
				b.putString("license_"+(i+1), "CC_BY");
			}
			submitObservationRequestFinally(single, b, context, sp);
		}	
	}

	protected void parseUploadResourceDetail(String response, boolean single, Bundle b, Context context, ObservationInstance sp, ArrayList<String> imageStringPath) {
		try {
			ArrayList<String> newImageStr=new ArrayList<String>();
			
			if(imageStringPath!=null && imageStringPath.size()>0){
				for(int i=0;i<imageStringPath.size();i++){
					if(imageStringPath.get(i).contains("http://")){
						String path=imageStringPath.get(i).replace("http://"+HttpUtils.stageOrProdBaseURL()+"/biodiv/observations/", "");
						newImageStr.add(path);
					}
				}
			}

			JSONObject jObj=new JSONObject(response);
			jObj = jObj.getJSONObject("model");
			JSONArray jArray=jObj.getJSONObject("observations").getJSONArray("resources");
			if(jArray!=null && jArray.length()>0){
				for(int i=0;i<jArray.length();i++){
					newImageStr.add(jArray.getJSONObject(i).optString("fileName"));
				}
			}
			
			for(int i=0;i<newImageStr.size();i++){
				b.putString("file_"+(i+1), newImageStr.get(i));
				b.putString("type_"+(i+1), Constants.IMAGE);
				b.putString("license_"+(i+1), "CC_BY");
			}
			
			//if(Preferences.DEBUG) Log.d("Checkout Params to b send", "****Bundle: "+b);
			submitObservationRequestFinally(single, b, context, sp);
		} catch (JSONException e) {
			sp.setStatus(StatusType.FAILURE);
			sp.setMessage("Unknown error occured..");
			ObservationInstanceTable.updateRowFromTable(context, sp);
			e.printStackTrace();
		}
	}

	protected void submitObservationRequestFinally(final boolean single,final Bundle b,final Context context,final ObservationInstance sp) {
		String path;
		if(sp.getId()==-1)
			path=Request.PATH_SAVE_OBSERVATION;
		else
			path=String.format(Request.PATH_UPDATE_OBSERVATION, sp.getId());
		
		sp.setStatus(StatusType.PROCESSING);
		ObservationInstanceTable.updateRowFromTable(context, sp);
		String method = sp.getId()==-1?Request.METHOD_POST:Request.METHOD_PUT;
		WebService.sendRequest(context, method, path, b, new WebService.ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				//ObservationParamsTable.deleteRowFromTable(context, sp);
				try {
					JSONObject jObj=new JSONObject(response);
					boolean success=jObj.optBoolean("success");
					if(success){
						NewObservationActivity ne = NewObservationActivity.getInstance();
						ne.StatusBarNotification();
						//notificationStatus = true;
						sp.setStatus(StatusType.SUCCESS);
						sp.setId(jObj.getJSONObject("instance").optLong("id"));
						ObservationInstanceTable.updateRowFromTable(context, sp);
						if(Preferences.DEBUG) Log.d("ObsRequestQueue", "******Broadcast send from ObsRequestQueue....");
						
						//send broadcast to HomeActivity and ObsStatusActivity to change status or view
						Intent i=new Intent("com.mobisys.android.ibp.check_incomplete_obs"); 
				    	context.sendBroadcast(i);
					}
					else{
						sp.setStatus(StatusType.FAILURE);
						String fail=jObj.optString("msg");
						JSONArray jarray=jObj.getJSONArray("errors");
						if(jarray!=null && jarray.length()>0){
							sp.setMessage(jarray.getJSONObject(0).optString("message"));
						}
						else sp.setMessage(fail);
						
						ObservationInstanceTable.updateRowFromTable(context, sp);
					}
					
				} catch (JSONException e) {
					sp.setStatus(StatusType.FAILURE);
					sp.setMessage("Unknown error occured..");
					ObservationInstanceTable.updateRowFromTable(context, sp);
					e.printStackTrace();
				}
				
				if(!single){
					ObservationInstance cp_new = ObservationInstanceTable.getFirstRecord(context);
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
				sp.setMessage(content);
				ObservationInstanceTable.updateRowFromTable(context, sp);
				if(!single){
					ObservationInstance cp_new = ObservationInstanceTable.getFirstRecord(context);
					observationMethods(single, cp_new, context);
				}
			}

			

		});
	}
	
}
