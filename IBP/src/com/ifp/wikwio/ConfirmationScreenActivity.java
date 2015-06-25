package com.ifp.wikwio;

import org.json.JSONException;
import org.json.JSONObject;

import com.ifp.wikwio.R;
import com.ifp.wikwio.http.Request;
import com.ifp.wikwio.http.WebService;
import com.ifp.wikwio.http.WebService.ResponseHandler;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.utils.ProgressDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmationScreenActivity extends ActionBarActivity{

	private Dialog mPg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmtion_screen);
		initActionBar();
		initScreen();
	}

	private void initActionBar(){
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.app_actionbar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		((TextView)findViewById(R.id.title)).setText("Resend Verification Email");
		findViewById(R.id.btn_menu).setVisibility(View.GONE);
	}

	private void initScreen() {
		
		((TextView)findViewById(R.id.label_resend_verification)).setText(""+getIntent().getStringExtra(Constants.PARAM_MESSAGE));
		
		((Button)findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(ConfirmationScreenActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		((Button)findViewById(R.id.btn_resend)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				resendEmailDialog();
			}
		});
	}

	private void resendEmailDialog() {
		final Dialog dialog = new Dialog(ConfirmationScreenActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
				
				if(AppUtil.emailValidator(email)) resend(email);
				else Toast.makeText(ConfirmationScreenActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		dialog.show();
	}

	protected void resend(String email) {
		Bundle b=new Bundle();
		b.putString(Request.PARAM_EMAIL, email);
		mPg= ProgressDialog.show(ConfirmationScreenActivity.this,getString(R.string.loading));
		
		WebService.sendRequest(ConfirmationScreenActivity.this, Request.METHOD_GET, Request.PATH_RESEND_EMAIL, b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseResendMail(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPg!=null && mPg.isShowing()) mPg.dismiss();
				AppUtil.showErrorDialog(content, ConfirmationScreenActivity.this);
			}
		});
	}

	protected void parseResendMail(String response) {
		try {
			JSONObject jobj=new JSONObject(response);
			boolean success=jobj.optBoolean("success");
			String message=jobj.optString("msg");
			if(mPg!=null && mPg.isShowing()) mPg.dismiss();
			
			if(success) showDialog(message, ConfirmationScreenActivity.this);
			else AppUtil.showErrorDialog(message, ConfirmationScreenActivity.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void showDialog(String message,final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.alert);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				finish();
			}
		});
		builder.show();
	}
}
