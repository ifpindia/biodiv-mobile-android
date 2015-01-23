package com.mobisys.android.ibp;


import java.io.IOException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;

public class LoginActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener{
	private Dialog mPg;
	private UiLifecycleHelper uiHelper;
	private boolean mFBSessionOpening = false;
	private com.facebook.widget.LoginButton mFbButton;
	private GoogleApiClient mGoogleApiClient;
	
	private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
//    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;
    
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
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
        initScreen();
    }
    
/*    private void generateHashCode(){
    	try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("*********Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
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
			mFbButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
		
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
	    	String fb_access_token=SharedPreferencesUtil.getSharedPreferencesString(LoginActivity.this, Constants.FB_ACCESS_TOKEN, null);
	    	loginWithFb(true, fb_access_token);
	    }
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
    protected void loginWithFb(boolean isFbLogin, String access_token) {
    	mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
    	Bundle b=new Bundle();
    	
		b.putString(Request.PARAM_FB_ACCESS_TOKEN, access_token);
		
		String path;
		if(isFbLogin)
			path=Request.PATH_FB_LOGIN;
		else{
			path=Request.PATH_GOOGLE_LOGIN;
			b.putString("token_type", "Bearer");
			b.putString("expires_in", String.valueOf(3600));
		}	
		WebService.sendRequest(LoginActivity.this, Request.METHOD_GET, path, b, new ResponseHandler() {
			
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
		 if (requestCode == RC_SIGN_IN) {
	            if (resultCode != RESULT_OK) {
	                mSignInClicked = false;
	            }
	 
	            mIntentInProgress = false;
	 
	            if (!mGoogleApiClient.isConnecting()) {
	                mGoogleApiClient.connect();
	            }
	        }
		else
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
    	
    	findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signInWithGplus();
			}
		});
    }
    
    protected void signInWithGplus() {
    	if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
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
				SharedPreferencesUtil.putSharedPreferencesString(LoginActivity.this, Request.HEADER_AUTH_KEY, token);
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
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.dialog_forgot_password, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.forgot_password));
		builder.setView(v);
		builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String email=((EditText)v.findViewById(R.id.edit_email)).getText().toString();
				dialog.dismiss();
				
				if(AppUtil.emailValidator(email)) forgotPassword(email);
				else Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	
	protected void forgotPassword(String email) {
		mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
		Bundle b=new Bundle();
		b.putString(Request.PARAM_EMAIL, email);
		
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

	protected void onStart() {
	    super.onStart();
	    //mGoogleApiClient.connect();
	  }

	  protected void onStop() {
	    super.onStop();

	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	  }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
 
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
 
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
	}

	public void onConnectionSuspended(int cause) {
		  mGoogleApiClient.connect();
		}
	
	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					/*final String accessToken=GoogleAuthUtil.getToken(LoginActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient),
					        "oauth2:" + Scopes.PLUS_LOGIN);*/
					
					final String accessToken = GoogleAuthUtil.getToken(LoginActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient) + "", "oauth2:" + Scopes.PLUS_LOGIN + 
                            " https://www.googleapis.com/auth/userinfo.email");
					Log.d("", "G+ login successful");
					Log.d("", "Access token:"+accessToken);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//Toast.makeText(LoginActivity.this, "Google!", Toast.LENGTH_LONG).show();
							loginWithFb(false, accessToken);
						}
					});
				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), 50);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void resolveSignInError() {
		if(mConnectionResult!=null){
	        if (mConnectionResult.hasResolution()) {
	            try {
	                mIntentInProgress = true;
	                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
	            } catch (SendIntentException e) {
	                mIntentInProgress = false;
	                mGoogleApiClient.connect();
	            }
	        }
		}
		else{
			mIntentInProgress = false;
            mGoogleApiClient.connect();
		}
    }
	
}
