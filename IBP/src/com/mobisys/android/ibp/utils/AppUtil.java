package com.mobisys.android.ibp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.ibp.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

public class AppUtil {

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
	
}
