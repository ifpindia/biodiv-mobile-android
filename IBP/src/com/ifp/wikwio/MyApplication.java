package com.ifp.wikwio;

import android.app.Application;
import android.net.Uri;

public class MyApplication extends Application{
	private Uri picUri;
	   public Uri getPicUri(){
	     return picUri;
	   }
	   
	   public void setPictUri(Uri auri){
	     picUri = auri;
	   } 
}
