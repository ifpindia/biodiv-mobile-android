package com.mobisys.android.ibp;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

public class LoginActivity extends ActionBarActivity {
	private Dialog mPg;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        getSupportActionBar().hide();
        initScreen();
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
    	String email=((EditText)findViewById(R.id.email)).getText().toString();
		String password=((EditText)findViewById(R.id.password)).getText().toString();
		if(!AppUtil.emailValidator(email)){
			Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
			return;
		}
		login(email, password);
	}

	private void login(String email, String password) {
		Bundle b=new Bundle();
		b.putString(Request.PARAM_EMAIL, email);
		b.putString(Request.PARAM_PASSWORD, password);
		
		
		mPg= ProgressDialog.show(LoginActivity.this,getString(R.string.loading));
		
		WebService.sendRequestWithBaseUrl(LoginActivity.this, Request.BASE_URL, Request.METHOD_POST, Request.PATH_LOGIN,-1,b, new ResponseHandler() {
			
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
				String username=jobj.optString("username");
				
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
