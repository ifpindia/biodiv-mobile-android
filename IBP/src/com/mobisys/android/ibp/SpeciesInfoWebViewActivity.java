package com.mobisys.android.ibp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobisys.android.ibp.utils.AppUtil;

public class SpeciesInfoWebViewActivity extends ActionBarActivity{

	private String mSpeciesId; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.species_info_webview);
		((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra(Constants.SCI_NAME));
		getSupportActionBar().hide();
		initScreen();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initScreen() {
		mSpeciesId=getIntent().getStringExtra(Constants.SPECIES_ID);
		
		WebView webView = (WebView)findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
		webView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
		    	progressBar.setVisibility(View.VISIBLE);
		    	progressBar.setProgress(progress);
		    	if(progress==100) progressBar.setVisibility(View.INVISIBLE);
		    }
		});
		webView.setWebViewClient(new WebViewClient() {
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        view.loadUrl(url);
		        return true;
		    }

		    public void onPageFinished(WebView view, String url) {
		    }

		    public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
		    }
		});
		webView.loadUrl(AppUtil.getSpeciesUrl(mSpeciesId));
	}
}
