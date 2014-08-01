package com.mobisys.android.ibp;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LoginActivity extends ActionBarActivity {
	private Dialog mPg;
	private UiLifecycleHelper uiHelper;
	private boolean mFBSessionOpening = false;
	private com.facebook.widget.LoginButton mFbButton;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();
        
        uiHelper = new UiLifecycleHelper(LoginActivity.this, fbCallback);
		uiHelper.onCreate(savedInstanceState);
		mFbButton= (com.facebook.widget.LoginButton) findViewById(R.id.btn_fb);
		//mFbButton.setReadPermissions(Arrays.asList("basic_info", "email"));
		//generateHashCode();
        initScreen();
    }
    
   /* private void generateHashCode(){
    	try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }*/

    
    @Override
	protected void onResume() {
		super.onResume();
		
		Session session = Session.getActiveSession();
		
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		else
			mFbButton.setReadPermissions(Arrays.asList("basic_info", "email"));
		
		uiHelper.onResume();
	}

    private Session.StatusCallback fbCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,Exception exception) {
			onSessionStateChange(session, state, exception);
		}

	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.equals(SessionState.OPENING)) {
			mFBSessionOpening = true;
		}
		else if (mFBSessionOpening && (state.equals(SessionState.OPENED))) {
			mFBSessionOpening=false;
			onFBLogin(session);

		} else {
			mFBSessionOpening = false;
		}
		Log.d("LoginProfileFragment", "Session state: "+state.toString()+" AuthToken: "+session.getAccessToken());
	}	
    
	private void onFBLogin(Session session){
		SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Constants.FB_ACCESS_TOKEN, session.getAccessToken());
		//enableProgressDialog(getString(R.string.loading));
    	if(mHandler!=null){
        	Message msg = Message.obtain();
			mHandler.sendMessage(msg);
    	}
	}
	
	private final Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	loginWithFb();
	    }
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
    protected void loginWithFb() {
    	mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
    	Bundle b=new Bundle();
    	String fb_access_token=SharedPreferencesUtil.getSharedPreferencesString(LoginActivity.this, Constants.FB_ACCESS_TOKEN, null);
		b.putString(Request.PARAM_FB_ACCESS_TOKEN, fb_access_token);
		WebService.sendRequest(LoginActivity.this, Request.METHOD_GET, Request.PATH_FB_LOGIN, b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseLoginDetail(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, LoginActivity.this);
			}
		});
	}

	/*protected void parseFbDetails(String response) {
		if(response.contains("token")){
			JSONObject jobj;
			try {
				jobj = new JSONObject(response);
				String token=jobj.optString("token");
				String id=jobj.optString("id");
				SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Constants.USER_ID, id);
				
				SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Constants.APP_TOKEN, token);
				showHomeActivity();
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(response.contains("error")){
			JSONObject jobj;
			try {
				jobj = new JSONObject(response);
				String error=jobj.optString("error");
				String message=jobj.optString("message");
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(message, LoginActivity.this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
*/
	private void showHomeActivity() {
		Intent i=new Intent(LoginActivity.this, HomeActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onPause(){
		super.onPause();
		uiHelper.onPause();
	}
	
	public void onDestroy(){
		super.onDestroy();
		uiHelper.onDestroy();
	}
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		uiHelper.onActivityResult(requestCode, resultCode, data);
    }

	
	private void initScreen(){
    	findViewById(R.id.new_user).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showRegisterActivity();
			}
		});
    	
    	findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				validateLogin();
			}
		});
    	
    	findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				forgotPasswordDialog();
			}
		});
    	
    }
    
    private void validateLogin() {
    	String email=((EditText)findViewById(R.id.edit_username)).getText().toString();
		String password=((EditText)findViewById(R.id.edit_password)).getText().toString();
		if(!AppUtil.emailValidator(email)){
			Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
			return;
		}
		login(email, password);
	}

	private void login(String email, String password) {
		Bundle b=new Bundle();
		b.putString(Request.PARAM_USERNAME, email);
		b.putString(Request.PARAM_PASSWORD, password);
		
		
		mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(LoginActivity.this, Request.METHOD_POST, Request.PATH_LOGIN,b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseLoginDetail(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, LoginActivity.this);
			}
		});
	}

	protected void parseLoginDetail(String response) {
		if(response.contains("token")){
			JSONObject jobj;
			try {
				jobj = new JSONObject(response);
				String token=jobj.optString("token");
				String id=jobj.optString("id");
				SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Constants.USER_ID, id);
				SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Constants.APP_TOKEN, token);
				showHomeActivity();
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(response.contains("error")){
			JSONObject jobj;
			try {
				jobj = new JSONObject(response);
				String error=jobj.optString("error");
				String message=jobj.optString("message");
				AppUtil.showErrorDialog(message, LoginActivity.this);
				
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void forgotPasswordDialog() {
		final Dialog dialog = new Dialog(LoginActivity.this);
		dialog.setTitle(R.string.forgot_password);
		dialog.setContentView(R.layout.dialog_email);
		
		dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//final String password=SharedPreferencesUtil.getSharedPreferencesString(MainActivity.this, Constants.PARAM_USER_PASSWORD, null);
				String email=((EditText)dialog.findViewById(R.id.edit_email)).getText().toString();
				dialog.dismiss();
				
				if(AppUtil.emailValidator(email)) forgotPassword(email);
				else Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
			}
		});
		
		dialog.show();
	}

	
	protected void forgotPassword(String email) {
		mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
		Bundle b=new Bundle();
		b.putString(Request.PARAM_EMAIL, email);
		mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(LoginActivity.this, Request.METHOD_POST, Request.PATH_FORGOT_PASSWORD, b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseForgotPasswordDetail(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, LoginActivity.this);
			}
		});
	}

	protected void parseForgotPasswordDetail(String response) {
		try {
			JSONObject jobj=new JSONObject(response);
			boolean success=jobj.optBoolean("success");
			String message=jobj.optString("msg");
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			
			if(success) AppUtil.showDialog(message, LoginActivity.this);
			else AppUtil.showErrorDialog(message, LoginActivity.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showRegisterActivity(){
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    }
}
