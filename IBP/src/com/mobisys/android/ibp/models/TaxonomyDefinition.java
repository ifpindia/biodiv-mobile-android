package com.mobisys.android.ibp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaxonomyDefinition implements Parcelable{

	private long id;
	private String name;
	private String canonicalForm;
	private String italicisedForm;
	private String rank;
	
	public TaxonomyDefinition(){}
	
	public TaxonomyDefinition(Parcel in){
		readFromParcel(in);
	}
	
	public TaxonomyDefinition(long id, String name, String canonicalForm,
			String italicisedForm, String rank) {
		super();
		this.id = id;
		this.name = name;
		this.canonicalForm = canonicalForm;
		this.italicisedForm = italicisedForm;
		this.rank = rank;
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

	public String getCanonicalForm() {
		return canonicalForm;
	}

	public void setCanonicalForm(String canonicalForm) {
		this.canonicalForm = canonicalForm;
	}

	public String getItalicisedForm() {
		return italicisedForm;
	}

	public void setItalicisedForm(String italicisedForm) {
		this.italicisedForm = italicisedForm;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((canonicalForm == null) ? 0 : canonicalForm.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((italicisedForm == null) ? 0 : italicisedForm.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
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
		TaxonomyDefinition other = (TaxonomyDefinition) obj;
		if (canonicalForm == null) {
			if (other.canonicalForm != null)
				return false;
		} else if (!canonicalForm.equals(other.canonicalForm))
			return false;
		if (id != other.id)
			return false;
		if (italicisedForm == null) {
			if (other.italicisedForm != null)
				return false;
		} else if (!italicisedForm.equals(other.italicisedForm))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaxonomyDefinition [id=" + id + ", name=" + name
				+ ", canonicalForm=" + canonicalForm + ", italicisedForm="
				+ italicisedForm + ", rank=" + rank + "]";
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		name=in.readString();
		canonicalForm=in.readString();
		italicisedForm=in.readString();
		rank=in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(canonicalForm);
		dest.writeString(italicisedForm);
		dest.writeString(rank);
	}

	public static final Creator<TaxonomyDefinition> CREATOR = new Creator<TaxonomyDefinition>() {
		
		public TaxonomyDefinition createFromParcel(Parcel source) {
			return new TaxonomyDefinition(source);
		}
		
		public TaxonomyDefinition[] newArray(int size) {
			return new TaxonomyDefinition[size];
		}
	};
}
