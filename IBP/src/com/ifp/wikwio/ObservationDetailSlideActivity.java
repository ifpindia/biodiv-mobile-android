package com.ifp.wikwio;

import java.util.ArrayList;

import com.ifp.wikwio.R;
import com.ifp.wikwio.models.ObservationInstance;
import com.ifp.wikwio.models.Resource;
import com.ifp.wikwio.utils.AppUtil;
import com.ifp.wikwio.widget.MImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ObservationDetailSlideActivity extends ActionBarActivity{
	
	//private ObservationInstance mObv;
	//private String DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ss'Z'";
	private String DATE_FORMAT1="MMM dd, yyyy";
	private ArrayList<ObservationInstance> mList;
	private int mObvIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obv_detail_slide);
		getSupportActionBar().hide();
		mList=getIntent().getParcelableArrayListExtra(ObservationInstance.ObsInstanceList);
		//mObv=getIntent().getParcelableExtra(ObservationInstance.ObsInstance);
		mObvIndex=getIntent().getIntExtra("index", 0);
		
		initScreen();
	}

	private void initScreen() {
		//findViewById(R.id.btn_edit).setVisibility(View.GONE);
		
		ObservationAdapter adapter = new ObservationAdapter(this, mList);
	    final ViewPager myPager = (ViewPager) findViewById(R.id.slide_pager);
	    myPager.setAdapter(adapter);
	    myPager.setCurrentItem(mObvIndex);
	}
	
	public class ObservationAdapter extends PagerAdapter {

		private Context context;
		private ArrayList<ObservationInstance> list;
		
		public ObservationAdapter(Context context, ArrayList<ObservationInstance> list) {
		    this.list = list;
		    this.context = context;
		}

		public int getCount() {
		    return list.size();
		}

		public Object instantiateItem(View collection,final int position) {
		    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.obv_detail_view_pager_item, null);   
		    ObservationInstance obvInstance=list.get(position);
		    
		    fillViews(obvInstance, layout);
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

	public void fillViews(final ObservationInstance mObv, View layout) {
		if(mObv.getMaxVotedReco()!=null){
			if(mObv.getMaxVotedReco().getCommonName().length()>0){
				layout.findViewById(R.id.common_name_layout).setVisibility(View.VISIBLE);
				((TextView)layout.findViewById(R.id.title)).setText(mObv.getMaxVotedReco().getCommonName());
				((TextView)layout.findViewById(R.id.common_name)).setText(mObv.getMaxVotedReco().getCommonName());
			}	
			else{ 
				if(mObv.getMaxVotedReco().getScientificName().length()==0){ //common name and sci name empty, 
					layout.findViewById(R.id.common_name_layout).setVisibility(View.VISIBLE);
					layout.findViewById(R.id.sci_name_layout).setVisibility(View.GONE);
					((TextView)layout.findViewById(R.id.common_name)).setText(R.string.unknown);
					((TextView)layout.findViewById(R.id.title)).setText(R.string.unknown);
				}
				else{  // common name empty but sci name present
					layout.findViewById(R.id.common_name_layout).setVisibility(View.GONE);
					((TextView)layout.findViewById(R.id.title)).setText(mObv.getMaxVotedReco().getScientificName());
				}
			}
			
			if(mObv.getMaxVotedReco().getSpeciesIdForSciRecord()!=null){
				layout.findViewById(R.id.sci_name_layout).setVisibility(View.VISIBLE);
				((TextView)layout.findViewById(R.id.sci_name)).setText(mObv.getMaxVotedReco().getScientificName());
				((TextView)layout.findViewById(R.id.more_info)).setText(getString(R.string.more_info));
				layout.findViewById(R.id.sci_name_layout).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent i=new Intent(ObservationDetailSlideActivity.this, SpeciesInfoWebViewActivity.class);
						i.putExtra(Constants.SPECIES_ID, mObv.getMaxVotedReco().getSpeciesIdForSciRecord());
						i.putExtra(Constants.SCI_NAME, mObv.getMaxVotedReco().getScientificName());
						startActivity(i);
					}
				});
			}	
			else{
				if(mObv.getMaxVotedReco().getScientificName()!=null && mObv.getMaxVotedReco().getScientificName().length()>0){
					layout.findViewById(R.id.sci_name_layout).setVisibility(View.VISIBLE);
					((TextView)layout.findViewById(R.id.more_info)).setText("");
					((TextView)layout.findViewById(R.id.sci_name)).setText(mObv.getMaxVotedReco().getScientificName());
				}
				else{
					layout.findViewById(R.id.sci_name_layout).setVisibility(View.GONE);
				}
			}	
			
			((TextView)layout.findViewById(R.id.place)).setText(mObv.getPlaceName());
			if(mObv.getNotes()!=null && mObv.getNotes().length()>0)
				((TextView)layout.findViewById(R.id.notes)).setText(Html.fromHtml(mObv.getNotes()));
			else{ 
				((TextView)layout.findViewById(R.id.label_notes)).setVisibility(View.GONE);
				((TextView)layout.findViewById(R.id.notes)).setVisibility(View.GONE);
			}
			if(mObv.getAuthor()!=null)
				((TextView)layout.findViewById(R.id.submitted_by)).setText(mObv.getAuthor().getName());
			
			String observed=AppUtil.getStringFromDate(mObv.getFromDate(), DATE_FORMAT1);
			String submitted=AppUtil.getStringFromDate(mObv.getCreatedOn()!=null?mObv.getCreatedOn():mObv.getFromDate(), DATE_FORMAT1);
			String updated=AppUtil.getStringFromDate(mObv.getLastRevised()!=null?mObv.getLastRevised():mObv.getFromDate(), DATE_FORMAT1);
			
			((TextView)layout.findViewById(R.id.observed_on)).setText(observed);
			((TextView)layout.findViewById(R.id.submitted)).setText(submitted);
			((TextView)layout.findViewById(R.id.updated)).setText(updated);
			
			CirclePageIndicator indicator = (CirclePageIndicator)layout.findViewById(R.id.indicator);
			ImagePagerAdapter adapter = new ImagePagerAdapter(ObservationDetailSlideActivity.this, mObv.getResource(), indicator);
		    final ViewPager myPager = (ViewPager)layout.findViewById(R.id.pager);
		    myPager.setAdapter(adapter);
		    myPager.setCurrentItem(0);
		    indicator.setViewPager(myPager);
		    
		    if(mObv.getResource().size()==1) indicator.setVisibility(View.GONE);
		}
	}	
	
	private class ImagePagerAdapter extends PagerAdapter {

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
		    if(resource.get(position).getIcon()!=null )
		    	MImageLoader.displayImage(context, getImageUrl(resource.get(position).getIcon()), (ImageView)layout.findViewById(R.id.myimage), R.drawable.user_stub);
		    else
		    	AppUtil.setUriBitmap((ImageView)layout.findViewById(R.id.myimage), Uri.parse(resource.get(position).getUri()), ObservationDetailSlideActivity.this, 300);
		    
		    ((ImageView) layout.findViewById(R.id.myimage)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(resource.get(position).getIcon()!=null )
						showImageDialog(context, resource.get(position));
					else
						showImageDialog(context, resource.get(position));
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

	private void showImageDialog(Context context, Resource resource) {
		Dialog settingsDialog = new Dialog(ObservationDetailSlideActivity.this);
		settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
		
		if(resource.getIcon()!=null)
			MImageLoader.displayImage(context, resource.getUrl(), ((ImageView)settingsDialog.findViewById(R.id.image)), R.drawable.user_stub);
		else
			AppUtil.setUriBitmap((ImageView)settingsDialog.findViewById(R.id.image), Uri.parse(resource.getUri()), ObservationDetailSlideActivity.this,600);
		
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
