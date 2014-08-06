package com.mobisys.android.ibp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable{

	private String url;
	private String icon;
	
	public Resource(){}
	
	public Resource(String url, String icon) {
		super();
		this.url = url;
		this.icon = icon;
	}

	public Resource(Parcel in) {
		readFromParcel(in);
	}
	
	private void readFromParcel(Parcel in) {
		url=in.readString();
		icon=in.readString();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(url);
		dest.writeString(icon);
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
