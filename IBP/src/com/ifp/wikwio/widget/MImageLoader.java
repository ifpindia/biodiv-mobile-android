package com.ifp.wikwio.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

public class MImageLoader {

	private static final String UNIVERSAL_IMAGE_LOADER = "universal-image-loader";
	private static final String PICASSO_IMAGE_LOADER = "picasso";
	
	public static final String IMAGE_LOADER_TYPE = UNIVERSAL_IMAGE_LOADER;
	
	public static ImageLoader mImageLoader;
	
	public static void initImageLoader(Context context){
		if(mImageLoader==null) {
			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
	}
	
	public static void destroyImageLoader(){
		if(mImageLoader!=null){
			mImageLoader.destroy();
			mImageLoader=null;
		}
	}
	
	public static void stopImageLoader(){
		if(mImageLoader!=null){
			mImageLoader.stop();
			mImageLoader=null;
		}
	}
	public static DisplayImageOptions defaulDisplayImageOptions(int place_holder_res){
		return 
				new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.showImageForEmptyUri(place_holder_res)
			.showImageOnFail(place_holder_res)
			.showStubImage(place_holder_res)
			.resetViewBeforeLoading(true)
			.delayBeforeLoading(200)
			.imageScaleType(ImageScaleType.EXACTLY)
			.displayer(new MyFadeInBitmapDisplayer(300))
			.build();
	}
	
	public static void displayImage(Context context, String url, ImageView imageView, int place_holder_res){
		if(UNIVERSAL_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
			initImageLoader(context);
			mImageLoader.displayImage(url, imageView, defaulDisplayImageOptions(place_holder_res));
		}
		else if(PICASSO_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
			Picasso.with(context).load(url)
				.placeholder(place_holder_res)
				.error(place_holder_res)
				.into(imageView);
		}
	}
	
	public static void displayImage(Context context, String url, ImageView imageView, int place_holder_res, ImageLoadingListener listener){
		if(UNIVERSAL_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
			initImageLoader(context);
			mImageLoader.displayImage(url, imageView, defaulDisplayImageOptions(place_holder_res), listener);
		}
	}
	
	public static class MyFadeInBitmapDisplayer extends FadeInBitmapDisplayer {

        public MyFadeInBitmapDisplayer(int durationMillis) {
			super(durationMillis);
		}

		@Override
		public Bitmap display(Bitmap bitmap, ImageView imageView,
				LoadedFrom loadedFrom) {
			try {
				return super.display(bitmap, imageView, loadedFrom);
            } catch (IllegalStateException e) {
                // This is the relevant code for the app won't crash
                Log.e("debug", e.getMessage(), e);
                return bitmap;
            }
        }
	}
}
