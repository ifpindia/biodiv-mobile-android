package com.ifp.wikwio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserGroup implements Parcelable{

	private long id;
	private String name;
	private String description;
	private String domainName;
	private String webaddress;
	private String foundedOn;
	private String icon;
	private boolean isJoined;
	
	public UserGroup(){}
	
	public UserGroup(Parcel in){
		readFromParecel(in);
	}
	
	public UserGroup(long id, String name, String description,
			String domainName, String webaddress, String foundedOn, String icon, boolean isJoined) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.domainName = domainName;
		this.webaddress = webaddress;
		this.foundedOn = foundedOn;
		this.icon = icon;
		this.isJoined = isJoined;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getWebaddress() {
		return webaddress;
	}

	public void setWebaddress(String webaddress) {
		this.webaddress = webaddress;
	}

	public String getFoundedOn() {
		return foundedOn;
	}

	public void setFoundedOn(String foundedOn) {
		this.foundedOn = foundedOn;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public boolean isJoined() {
		return isJoined;
	}

	public void setJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}

	private void readFromParecel(Parcel in) {
		id=in.readLong();
		name=in.readString();
		description=in.readString();
		domainName=in.readString();
		webaddress=in.readString();
		foundedOn=in.readString();
		icon=in.readString();
		isJoined = in.readInt()==1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(domainName);
		dest.writeString(webaddress);
		dest.writeString(foundedOn);
		dest.writeString(icon);
		dest.writeInt(isJoined?1:0);
	}

	public static final Creator<UserGroup> CREATOR = new Creator<UserGroup>() {
		
		public UserGroup createFromParcel(Parcel source) {
			return new UserGroup(source);
		}
		
		public UserGroup[] newArray(int size) {
			return new UserGroup[size];
		}
	};
}
