package com.mobisys.android.ibp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MaxVotedRecord implements Parcelable{

	private long id;
	private String name;
	private TaxonomyDefinition taxonomyDefinition;
	private long speciesId;
	
	public MaxVotedRecord(){}
	
	public MaxVotedRecord(Parcel in){
		readFromParcel(in);
	}
	
	public MaxVotedRecord(long id, String name,
			TaxonomyDefinition taxonomyDefinition, long speciesId) {
		super();
		this.id = id;
		this.name = name;
		this.taxonomyDefinition = taxonomyDefinition;
		this.speciesId = speciesId;
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

	public TaxonomyDefinition getTaxonomyDefinition() {
		return taxonomyDefinition;
	}

	public void setTaxonomyDefinition(TaxonomyDefinition taxonomyDefinition) {
		this.taxonomyDefinition = taxonomyDefinition;
	}

	public long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(long speciesId) {
		this.speciesId = speciesId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (speciesId ^ (speciesId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaxVotedRecord other = (MaxVotedRecord) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (speciesId != other.speciesId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaxVotedRecord [id=" + id + ", name=" + name + ", speciesId="
				+ speciesId + "]";
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		name=in.readString();
		taxonomyDefinition=in.readParcelable(TaxonomyDefinition.class.getClassLoader());
		speciesId=in.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeParcelable(taxonomyDefinition, flags);
		dest.writeLong(speciesId);
	}

	public static final Creator<MaxVotedRecord> CREATOR = new Creator<MaxVotedRecord>() {
		
		public MaxVotedRecord createFromParcel(Parcel source) {
			return new MaxVotedRecord(source);
		}
		
		public MaxVotedRecord[] newArray(int size) {
			return new MaxVotedRecord[size];
		}
	};
}
