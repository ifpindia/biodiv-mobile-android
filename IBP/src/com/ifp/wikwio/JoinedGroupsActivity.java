package com.ifp.wikwio;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import com.ifp.wikwio.R;
import com.ifp.wikwio.http.Request;
import com.ifp.wikwio.http.WebService;
import com.ifp.wikwio.http.WebService.ResponseHandler;
import com.ifp.wikwio.models.MyUserGroup;
import com.ifp.wikwio.models.UserGroup;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.ProgressDialog;
import com.ifp.wikwio.utils.SharedPreferencesUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class JoinedGroupsActivity extends BaseSlidingActivity{
	private Dialog mPg;
	private ArrayList<UserGroup> mGroupList;
	private GroupAdapter mAdapter;
	private ListView mList;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joined_group);
		((TextView)findViewById(R.id.title)).setText(getString(R.string.joined_groups));
		mList=(ListView)findViewById(R.id.list);
		getAllGroups();
	}

	private void getAllGroups() {
		Bundle b=new Bundle();
		b.putString(Request.MAX, String.valueOf(50));
		mPg= ProgressDialog.show(JoinedGroupsActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(JoinedGroupsActivity.this, Request.METHOD_GET, Request.PATH_ALL_USER_GROUPS,b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseResponse(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, JoinedGroupsActivity.this);
			}
		});
	}

	protected void parseResponse(String response) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			JSONObject jsonObject=new JSONObject(response);
			String userList=jsonObject.getJSONArray("userGroupInstanceList").toString();
			mGroupList=mapper.readValue(userList, new TypeReference<ArrayList<UserGroup>>(){});
			if(mGroupList!=null && mGroupList.size()>0)
				getUserJoinedGroups();
		} catch (JSONException e) {
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			e.printStackTrace();
		}
		catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getUserJoinedGroups() {
		String id=SharedPreferencesUtil.getSharedPreferencesString(JoinedGroupsActivity.this, Constants.USER_ID, "-1");
		Bundle b=new Bundle();
		b.putString(Request.PARAM_ID, id);
		b.putString(Request.LIMIT, String.valueOf(50));
		WebService.sendRequest(JoinedGroupsActivity.this, Request.METHOD_GET, Request.PATH_GET_USER_GROUPS,b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseUserResponse(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, JoinedGroupsActivity.this);
			}
		});
	}

	protected void parseUserResponse(String response) {
		try {
			JSONObject jobj=new JSONObject(response);
			String groupList=jobj.getJSONArray("observations").toString();
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			SharedPreferencesUtil.putSharedPreferencesString(JoinedGroupsActivity.this, Constants.JOINED_GROUPS_JSON, groupList);
			
			ArrayList<MyUserGroup> myList=mapper.readValue(groupList, new TypeReference<ArrayList<MyUserGroup>>(){});
			
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			updateList(myList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void updateList(ArrayList<MyUserGroup> myList) {
		
		if(myList!=null && myList.size()>0){
			for(int i=0;i<mGroupList.size();i++){
				for(int j=0;j<myList.size();j++){
					if(myList.get(j).getId()==mGroupList.get(i).getId()){
						mGroupList.get(i).setJoined(true);
						break;
					}
				}
			}
			mAdapter=new GroupAdapter(JoinedGroupsActivity.this,0,mGroupList);
			mList.setAdapter(mAdapter);
		}
		else{
			mAdapter=new GroupAdapter(JoinedGroupsActivity.this,0,mGroupList);
			mList.setAdapter(mAdapter);
		}
	}

	private class GroupAdapter extends ArrayAdapter<UserGroup>{
 		
 		private LayoutInflater mInflater;
 		private class ViewHolder {
 			public TextView group_name;
 			public Button btn_join_group;
 			public ImageView btn_joined;
 						
 			public ViewHolder(View row){
 				group_name = (TextView)row.findViewById(R.id.group_name);
 				btn_join_group = (Button)row.findViewById(R.id.btn_join_group);
 				btn_joined= (ImageView)row.findViewById(R.id.btn_joined);
 			}
 		}
 
 		public GroupAdapter(Context context, int textViewResourceId,ArrayList<UserGroup> objects) {
 			super(context, textViewResourceId, objects);
 			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		}
 
 		@Override
 		public View getView(final int position, View convertView, ViewGroup parent) {
 			View row = convertView;
 			ViewHolder holder = null;
 			if(row == null){
 				row = mInflater.inflate(R.layout.row_group_item, null);
 				holder = new ViewHolder(row);
 				row.setTag(holder);
 				//Drawable selector=AppUtil.getPressedStateDrawable(mContext);
 				//row.setBackgroundDrawable(selector);
 			}
 			else{
 				holder = (ViewHolder)row.getTag();
 			}
 			
 			holder.group_name.setText(getItem(position).getName());
 			
 			if(getItem(position).isJoined()){
 				holder.btn_join_group.setVisibility(View.GONE);
 				holder.btn_joined.setVisibility(View.VISIBLE);
 			}
 			else{
 				holder.btn_join_group.setVisibility(View.VISIBLE);
 				holder.btn_joined.setVisibility(View.GONE);
 			}
 			holder.btn_join_group.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showAlert(getItem(position));
				}
			});
 			return row;
 		}
	}

	protected void showAlert(final UserGroup item) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(JoinedGroupsActivity.this);
		 myAlertDialog.setTitle("Alert");
		 myAlertDialog.setMessage("Do you really want to join this group?");
		 myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

		  public void onClick(DialogInterface dialog, int arg1) {
			  joinGroup(item);
			  dialog.dismiss();
		  }});
		 
		 myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		       
		  public void onClick(DialogInterface dialog, int arg1) {
			  dialog.dismiss();
		  }});
		 myAlertDialog.show();
		}
	
	private void joinGroup(final UserGroup userGroup) {
		//Bundle b=new Bundle();
		String path=String.format(Request.PATH_JOIN_GROUP, String.valueOf(userGroup.getId()));
		mPg= ProgressDialog.show(JoinedGroupsActivity.this,getString(R.string.loading));
		WebService.sendRequest(JoinedGroupsActivity.this, Request.METHOD_GET, path,null, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseJoinGroupResponse(response, userGroup);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, JoinedGroupsActivity.this);
			}
		});

	}

	protected void parseJoinGroupResponse(String response, UserGroup userGroup) {
		try {
			JSONObject jobj=new JSONObject(response);
			boolean success=jobj.optBoolean("success");
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			if(success){
				userGroup.setJoined(true);
				AppUtil.showDialog(jobj.optString("msg"), JoinedGroupsActivity.this);
				mAdapter.notifyDataSetChanged();
			}
			else{
				AppUtil.showErrorDialog(jobj.optString("msg"), JoinedGroupsActivity.this);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
