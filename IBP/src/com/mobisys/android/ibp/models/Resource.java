package com.mobisys.android.ibp.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable(tableName="resource_table")
public class Resource implements Parcelable, Serializable{

	private static final long serialVersionUID = -7037426423175078702L;
	@DatabaseField
	private String url;
	@DatabaseField
	private String icon;
	@DatabaseField
	private String uri;
	@DatabaseField
	private boolean isDirty;
	
	public Resource(){}
	
	public Resource(String url, String icon, String uri, boolean isDirty) {
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
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
		//uri = in.readParcelable(Uri.class.getClassLoader());
		uri = in.readString();
		isDirty = in.readInt()==1;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeString(icon);
		dest.writeString(uri);
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
