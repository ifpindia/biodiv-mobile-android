package com.mobisys.android.ibp;

import java.util.Timer;
import java.util.TimerTask;

import com.mobisys.android.ibp.http.Request;
import com.mobisys.android.ibp.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity{
	private Animation mFadeIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	setContentView(R.layout.splash_screen);
    	
		ImageView app_banner = (ImageView) findViewById(R.id.image_banner);
		mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		app_banner.startAnimation(mFadeIn);
		mFadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {

				final Handler handler = new Handler();

				Timer t = new Timer();
				t.schedule(new TimerTask() {
					public void run() {
						handler.post(new Runnable() {
							public void run() {
								startApp();
								finish();
							}
						});
					}
				}, 1600);

			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationStart(Animation arg0) {
			}
		});
	}

	protected void startApp() {
	    String token=SharedPreferencesUtil.getSharedPreferencesString(SplashScreen.this, Request.HEADER_AUTH_KEY, null);
		if(token==null){
			Intent i=new Intent(SplashScreen.this, LoginActivity.class);
			startActivity(i);
		}
		else{
			showHomeActivity();
		}
	}
	
	private void showHomeActivity() {
		Intent i=new Intent(SplashScreen.this, HomeActivity.class);
		startActivity(i);
	}
}
