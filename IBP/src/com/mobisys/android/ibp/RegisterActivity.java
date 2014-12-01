package com.mobisys.android.ibp;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.http.WebService;
import com.mobisys.android.ibp.http.WebService.ResponseHandler;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.utils.ProgressDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {
	private Dialog mPg;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		setContentView(R.layout.register);
		initActionBar();
		initScreen();
	}
	
	private void initActionBar(){
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.app_actionbar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		((TextView)findViewById(R.id.title)).setText(getString(R.string.register));
		
		findViewById(R.id.btn_menu).setVisibility(View.GONE);
	}
	
	private void initScreen(){
		/*((TextView)findViewById(R.id.msg_agree)).setText(Html.fromHtml(getString(R.string.msg_term_conditions)));
		((TextView)findViewById(R.id.msg_agree)).setMovementMethod(LinkMovementMethod.getInstance());
		*/
		((Button)findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				validateRegisterDetails();
				//showConfirmationScreen("Welcome user Devaki. A verification link has been sent to devaki.anugu@gmail.com. Please click on the verification link in the email to activate your account.");
			}
		});
	}

	private void validateRegisterDetails() {
		String email=((EditText)findViewById(R.id.email)).getText().toString();
		String password=((EditText)findViewById(R.id.password)).getText().toString();
		//String confirm_password=((EditText)findViewById(R.id.confirm_password)).getText().toString();
		String name=((EditText)findViewById(R.id.name)).getText().toString();
		//String location=((EditText)findViewById(R.id.location)).getText().toString();
		
		if((name.length()==0)) {
			Toast.makeText(RegisterActivity.this, "Name can not be empty", Toast.LENGTH_SHORT).show();
			return;
        }
		
		if(!AppUtil.emailValidator(email)){
			Toast.makeText(RegisterActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if((password.length()<5) ) {
			Toast.makeText(RegisterActivity.this, "Password length has to be 5 characters", Toast.LENGTH_SHORT).show();
			return;
        }
		
		/*if(!password.equals(confirm_password)){
			Toast.makeText(RegisterActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();
			return;
        }*/
		
		register(email, password/*, confirm_password*/, name/*, location*/);
	}

	private void register(String email, String password/*, String confirm_password*/, String name/*, String location*/) {
		Bundle b=new Bundle();
		b.putString(Request.PARAM_EMAIL, email);
		b.putString(Request.PARAM_PASSWORD, password);
		b.putString(Request.PARAM_PASSWORD2, password);
		b.putString(Request.PARAM_NAME, name);
		//if(location!=null && location.length()>0) b.putString(Request.PARAM_LOCATION, location);
		
		mPg= ProgressDialog.show(RegisterActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(RegisterActivity.this, Request.METHOD_POST, Request.PATH_REGISTER,b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseRegeisterDetail(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, RegisterActivity.this);
			}
		});
	}

	private void parseRegeisterDetail(String response) {
		try {
			JSONObject jobj=new JSONObject(response);
			boolean success=jobj.optBoolean("success");
			String message=jobj.optString("msg");
			
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			
			if(success) showConfirmationScreen(message);
			else AppUtil.showErrorDialog(message, RegisterActivity.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showConfirmationScreen(String message) {
		Intent i=new Intent(this, ConfirmationScreenActivity.class);
		i.putExtra(Constants.PARAM_MESSAGE, message);
		startActivity(i);
		finish();
	}
}
