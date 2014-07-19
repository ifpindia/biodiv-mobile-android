package com.mobisys.android.ibp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class RegisterActivity extends ActionBarActivity {

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
		findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//getSlidingMenu().toggle();
			}
		});
		findViewById(R.id.btn_menu).setVisibility(View.GONE);
	}
	
	private void initScreen(){
		((TextView)findViewById(R.id.msg_agree)).setText(Html.fromHtml(getString(R.string.msg_term_conditions)));
		((TextView)findViewById(R.id.msg_agree)).setMovementMethod(LinkMovementMethod.getInstance());
	}
}
