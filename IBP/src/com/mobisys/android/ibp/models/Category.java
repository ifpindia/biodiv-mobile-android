package com.mobisys.android.ibp.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import com.j256.ormlite.field.DatabaseField;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Category implements Parcelable{
	
	@DatabaseField(id=true)
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private int groupOrder;
	
	public Category(){}
	
	public Category(Parcel in){
		readFromParcel(in);
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

	public int getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(int groupOrder) {
		this.groupOrder = groupOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupOrder;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Category other = (Category) obj;
		if (groupOrder != other.groupOrder)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", groupOrder="
				+ groupOrder + "]";
	}

	public Category(long id, String name, int groupOrder) {
		super();
		this.id = id;
		this.name = name;
		this.groupOrder = groupOrder;
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		name=in.readString();
		groupOrder=in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(groupOrder);
	}
	
	public static final Creator<Category> CREATOR = new Creator<Category>() {
		
		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}
		
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}
