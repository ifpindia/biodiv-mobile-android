package com.mobisys.android.ibp;

import android.os.Bundle;

public class HomeActivity extends BaseSlidingActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
	}
	
	/*private void initActionBar(){
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.app_actionbar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		((TextView)findViewById(R.id.title)).setText(getString(R.string.home));
		
		findViewById(R.id.btn_menu).setVisibility(View.VISIBLE);
		
	}*/
}
