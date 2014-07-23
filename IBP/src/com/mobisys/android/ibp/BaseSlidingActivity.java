package com.mobisys.android.ibp;


import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.mobisys.android.ibp.utils.AppUtil;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

public class BaseSlidingActivity extends SlidingActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.menu_layout);
		initActionBar();
		initSlidingMenu();
	}
	private void initActionBar() {
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.app_actionbar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		((TextView)findViewById(R.id.title)).setText(getString(R.string.home));
		
		findViewById(R.id.btn_menu).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSlidingMenu().toggle();
			}
		});
	}

	private void initSlidingMenu() {
		getSlidingMenu().setBehindOffset(AppUtil.getDipValue(50, this));
	}
}
