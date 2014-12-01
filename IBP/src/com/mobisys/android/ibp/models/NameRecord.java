package com.mobisys.android.ibp.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable(tableName="namerecord_table")
public class NameRecord implements Parcelable, Serializable{
	private static final long serialVersionUID = -7298899393767031107L;
	
	@DatabaseField
	private String common_name;
	@DatabaseField
	private String scientific_name;
	@DatabaseField
	private String speciesId; //sciRec species id
	
	public NameRecord(){}
	
	public NameRecord(Parcel in){
		readFromParcel(in);
	}

	public NameRecord(String common_name, String scientific_name,String speciesId) {
		super();
		this.common_name = common_name;
		this.scientific_name = scientific_name;
		this.speciesId = speciesId;
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

	public String getSpeciesIdForSciRecord() {
		return speciesId;
	}

	public void setSpeciesIdForSciRecord(String speciesId) {
		this.speciesId = speciesId;
	}

	private void readFromParcel(Parcel in) {
		common_name=in.readString();
		scientific_name=in.readString();
		speciesId=in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(common_name);
		dest.writeString(scientific_name);
		dest.writeString(speciesId);
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
