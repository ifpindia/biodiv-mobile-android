package com.mobisys.android.ibp;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;

public class LoginActivity extends ActionBarActivity {

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
    }
    
    private void showRegisterActivity(){
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    }
}
