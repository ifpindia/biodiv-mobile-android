package com.mobisys.android.ibp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable{

	private String url;
	private String icon;
	private Uri uri;
	private boolean isDirty;
	
	public Resource(){}
	
	public Resource(String url, String icon, Uri uri, boolean isDirty) {
		super();
		this.url = url;
		this.icon = icon;
		this.uri=uri;
		this.isDirty=isDirty;
	}

	public Resource(Parcel in) {
		readFromParcel(in);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		url=in.readString();
		icon=in.readString();
		uri = in.readParcelable(Uri.class.getClassLoader());
		isDirty = in.readInt()==1;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeString(icon);
		dest.writeParcelable(uri, flags);
		dest.writeInt(isDirty?1:0);
	}

	public static final Creator<Resource> CREATOR = new Creator<Resource>() {
		
		public Resource createFromParcel(Parcel source) {
			return new Resource(source);
		}
		
		public Resource[] newArray(int size) {
			return new Resource[size];
		}
	};
}
