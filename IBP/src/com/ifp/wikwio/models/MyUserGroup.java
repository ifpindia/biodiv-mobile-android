package com.ifp.wikwio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MyUserGroup implements Parcelable{

	private long id;
	private String notes;
	private String url;
	private String title;
	private String type;
	private boolean isSelected;
	
	public MyUserGroup(){}
	
	public MyUserGroup(Parcel in){
		readFromParcel(in);
	}
	
	public MyUserGroup(long id, String notes, String url, String title,
			String type, boolean isSelected) {
		super();
		this.id = id;
		this.notes = notes;
		this.url = url;
		this.title = title;
		this.type = type;
		this.isSelected = isSelected;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		notes=in.readString();
		url=in.readString();
		title=in.readString();
		type=in.readString();
		isSelected = (in.readInt()==1);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(notes);
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(type);
		dest.writeInt(isSelected?1:0);
	}

	public static final Creator<MyUserGroup> CREATOR = new Creator<MyUserGroup>() {
		
		public MyUserGroup createFromParcel(Parcel source) {
			return new MyUserGroup(source);
		}
		
		public MyUserGroup[] newArray(int size) {
			return new MyUserGroup[size];
		}
	};

}
