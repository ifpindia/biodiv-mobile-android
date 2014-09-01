package com.mobisys.android.ibp;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobisys.android.ibp.models.ObservationInstance;
import com.mobisys.android.ibp.models.Resource;
import com.mobisys.android.ibp.utils.AppUtil;
import com.mobisys.android.ibp.widget.MImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

public class ObservationDetailActivity extends ActionBarActivity{

	private ObservationInstance mObv;
	//private Dialog mPG;
	private String DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ss'Z'";
	private String DATE_FORMAT1="MMM dd, yyyy";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obv_detail);
		getSupportActionBar().hide();
		mObv=getIntent().getParcelableExtra(ObservationInstance.ObsInstance);
		initScreen();
		//getObvDetail();
	}

	/*private void getObvDetail() {
		Bundle b = new Bundle();
		mPG= ProgressDialog.show(ObservationDetailActivity.this,getString(R.string.loading));
		WebService.sendRequest(ObservationDetailActivity.this, Request.METHOD_GET,Request.PATH_GET_OBSERVTAION_DETAIL+mObv.getId(), b, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				parseResponse(response);
			}
			
			@Override
			public void onFailure(Throwable e, String content) {
				if(mPG!=null && mPG.isShowing()) mPG.dismiss();
				AppUtil.showErrorDialog(content, ObservationDetailActivity.this);
			}
		});
	}*/

	/*private void parseResponse(String response) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			final ObservationInstance oi=mapper.readValue(response, ObservationInstance.class);
			if(mPG!=null && mPG.isShowing()) mPG.dismiss();
			initScreen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private void initScreen() {
		if(mObv.getMaxVotedReco()!=null){
			if(mObv.getMaxVotedReco().getCommonName().length()>0){
				((TextView)findViewById(R.id.title)).setText(mObv.getMaxVotedReco().getCommonName());
				((TextView)findViewById(R.id.common_name)).setText(mObv.getMaxVotedReco().getCommonName());
			}	
			else{ 
				((TextView)findViewById(R.id.common_name)).setText(R.string.unknown);
				((TextView)findViewById(R.id.title)).setText(R.string.unknown);
			}
			if(mObv.getMaxVotedReco().getSpeciesIdForSciRecord()!=null){
				((TextView)findViewById(R.id.sci_name)).setText(Html.fromHtml("<u>"+mObv.getMaxVotedReco().getScientificName()+"</u>"));
				((TextView)findViewById(R.id.sci_name)).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent i=new Intent(ObservationDetailActivity.this, SpeciesInfoWebViewActivity.class);
						i.putExtra(Constants.SPECIES_ID, mObv.getMaxVotedReco().getSpeciesIdForSciRecord());
						i.putExtra(Constants.SCI_NAME, mObv.getMaxVotedReco().getScientificName());
						startActivity(i);
					}
				});
			}	
			else
				((TextView)findViewById(R.id.sci_name)).setText(mObv.getMaxVotedReco().getScientificName());
		}
		((TextView)findViewById(R.id.place)).setText(mObv.getPlaceName());
		if(mObv.getNotes()!=null && mObv.getNotes().length()>0)
			((TextView)findViewById(R.id.notes)).setText(Html.fromHtml(mObv.getNotes()));
		else{ 
			((TextView)findViewById(R.id.label_notes)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.notes)).setVisibility(View.GONE);
		}
		((TextView)findViewById(R.id.submitted_by)).setText(mObv.getAuthor().getName());
		
		String observed=AppUtil.getStringFromDate(AppUtil.getDateFromString(mObv.getFromDate(), DATE_FORMAT), DATE_FORMAT1);
		String submitted=AppUtil.getStringFromDate(AppUtil.getDateFromString(mObv.getCreatedOn(), DATE_FORMAT), DATE_FORMAT1);
		String updated=AppUtil.getStringFromDate(AppUtil.getDateFromString(mObv.getLastRevised(), DATE_FORMAT), DATE_FORMAT1);
		
		((TextView)findViewById(R.id.observed_on)).setText(observed);
		((TextView)findViewById(R.id.submitted)).setText(submitted);
		((TextView)findViewById(R.id.updated)).setText(updated);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		ImagePagerAdapter adapter = new ImagePagerAdapter(this, mObv.getResource(), indicator);
	    final ViewPager myPager = (ViewPager) findViewById(R.id.pager);
	    myPager.setAdapter(adapter);
	    myPager.setCurrentItem(0);
	    indicator.setViewPager(myPager);
	    
	    if(mObv.getResource().size()==1) indicator.setVisibility(View.GONE);
	}
	
	public class ImagePagerAdapter extends PagerAdapter {

		private Context context;
		private ArrayList<Resource> resource;
		
		public ImagePagerAdapter(Context context, ArrayList<Resource> resource, CirclePageIndicator indicator) {
		    this.resource = resource;
		    this.context = context;
		}

		public int getCount() {
		    return resource.size();
		}

		public Object instantiateItem(View collection,final int position) {
		    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.custom_pager, null);   
		    
		    MImageLoader.displayImage(context, getImageUrl(resource.get(position).getIcon()), (ImageView)layout.findViewById(R.id.myimage), R.drawable.user_stub);
		    
		    ((ImageView) layout.findViewById(R.id.myimage)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showImageDialog(context, getImageUrl(resource.get(position).getIcon()));
				}
			});
		    
		    ((ViewPager) collection).addView(layout, 0);
		       return layout; 
		}	

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
		    ((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
		    return arg0 == ((View) arg1);
		}

		@Override
		public Parcelable saveState() {
		    return null;
		}
	}	

	private void showImageDialog(Context context, String url) {
		Dialog settingsDialog = new Dialog(ObservationDetailActivity.this);
		settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
		
		MImageLoader.displayImage(context, url, ((ImageView)settingsDialog.findViewById(R.id.image)), R.drawable.user_stub);
		
		settingsDialog.show();
	}

	public String getImageUrl(String url) {
		if(url!=null){
	    	if(url.contains("img.youtube.com"))
	    		url=url.replace("default.jpg", "hqdefault.jpg");
	    	else 
	    		url=url.replace("_th1.jpg", "_gall.jpg");
	    }
		return url;
	}
}
