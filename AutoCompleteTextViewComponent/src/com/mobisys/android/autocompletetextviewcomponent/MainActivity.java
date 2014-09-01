package com.mobisys.android.autocompletetextviewcomponent;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements SelectedLocationListener{
	
	private com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView mAutoText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		mAutoText=(com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.auto_text);
		mAutoText.setSelectionListener(this);
		/*mAutoText.setParser(new AutoCompleteResponseParserInterface() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String response) {
				
				return null;
			}
		});*/
	}

	@Override
	public void onSelectedLocation(String selectedLocation) {
		Toast.makeText(MainActivity.this, "Selected Location: "+selectedLocation, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFetchLatLngForSelectedLoc(double lat, double lng) {
		Toast.makeText(MainActivity.this, "lat lng: "+lat+" "+lng, Toast.LENGTH_SHORT).show();
	}

}
