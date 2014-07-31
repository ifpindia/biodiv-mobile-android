package com.mobisys.android.ibp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NameRecord implements Parcelable{

	private String common_name;
	private String scientific_name;
	
	public NameRecord(){}
	
	public NameRecord(Parcel in){
		readFromParcel(in);
	}
	
	public NameRecord(String common_name, String scientific_name) {
		super();
		this.common_name = common_name;
		this.scientific_name = scientific_name;
	}

	public String getCommonName() {
		return common_name;
	}

	public void setCommonName(String common_name) {
		this.common_name = common_name;
	}

	public String getScientificName() {
		return scientific_name;
	}

	public void setScientificName(String scientific_name) {
		this.scientific_name = scientific_name;
	}

	private void readFromParcel(Parcel in) {
		common_name=in.readString();
		scientific_name=in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(common_name);
		dest.writeString(scientific_name);
	}
	
	public static final Creator<NameRecord> CREATOR = new Creator<NameRecord>() {
		
		public NameRecord createFromParcel(Parcel source) {
			return new NameRecord(source);
		}
		
		public NameRecord[] newArray(int size) {
			return new NameRecord[size];
		}
	};
}
