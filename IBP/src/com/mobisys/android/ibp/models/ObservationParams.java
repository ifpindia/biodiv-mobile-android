package com.mobisys.android.ibp.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable(tableName="observations")
public class ObservationParams implements Parcelable{
	
	public enum StatusType {
	    SUCCESS,
	    PENDING,
	    FAILURE;
	}
	
	@DatabaseField(generatedId=true)
	private long server_id;
	@DatabaseField
	private long group_id;
	@DatabaseField
	private long habitat_id;
	@DatabaseField
	private String fromDate;
	@DatabaseField
	private String placeName;
	@DatabaseField
	private String areas;
	@DatabaseField
	private String commonName;
	@DatabaseField
	private String recoName;
	@DatabaseField
	private String resources;
	@DatabaseField
	private String image_type;
	@DatabaseField (unknownEnumName = "SUCCESS")
	private StatusType status;
	@DatabaseField
	private String message;
	@DatabaseField
	private String notes;
	
	public ObservationParams(){}
	
	public ObservationParams(Parcel in){
		readFromParcel(in);
	}
	
	public ObservationParams(long server_id, long group_id, long habitat_id,
			String fromDate, String placeName, String areas, String commonName,
			String recoName, String resources, String image_type, StatusType status, String message, String notes) {
		super();
		this.server_id = server_id;
		this.group_id = group_id;
		this.habitat_id = habitat_id;
		this.fromDate = fromDate;
		this.placeName = placeName;
		this.areas = areas;
		this.commonName = commonName;
		this.recoName = recoName;
		this.resources = resources;
		this.image_type = image_type;
		this.status = status;
		this.message = message;
		this.notes = notes;
	}

	public long getServerId() {
		return server_id;
	}

	public void setServerId(long server_id) {
		this.server_id = server_id;
	}

	public long getGroupId() {
		return group_id;
	}

	public void setGroupId(long group_id) {
		this.group_id = group_id;
	}

	public long getHabitatId() {
		return habitat_id;
	}

	public void setHabitatId(long habitat_id) {
		this.habitat_id = habitat_id;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getRecoName() {
		return recoName;
	}

	public void setRecoName(String recoName) {
		this.recoName = recoName;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}
	
	public String getImageType() {
		return image_type;
	}

	public void setImageType(String image_type) {
		this.image_type = image_type;
	}

	
	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	private void readFromParcel(Parcel in) {
		server_id=in.readLong();
		group_id=in.readLong();
		habitat_id=in.readLong();
		fromDate=in.readString();
		placeName=in.readString();
		areas=in.readString();
		commonName=in.readString();
		recoName=in.readString();
		resources=in.readString();
		image_type=in.readString();
		status=StatusType.valueOf(in.readString());
		message=in.readString();
		notes=in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(server_id);
		dest.writeLong(group_id);
		dest.writeLong(habitat_id);
		dest.writeString(fromDate);
		dest.writeString(placeName);
		dest.writeString(areas);
		dest.writeString(commonName);
		dest.writeString(recoName);
		dest.writeString(resources);
		dest.writeString(image_type);
		dest.writeString(status.name());
		dest.writeString(message);
		dest.writeString(notes);
	}

	public static final Creator<ObservationParams> CREATOR = new Creator<ObservationParams>() {
		
		public ObservationParams createFromParcel(Parcel source) {
			return new ObservationParams(source);
		}
		
		public ObservationParams[] newArray(int size) {
			return new ObservationParams[size];
		}
	};
}
