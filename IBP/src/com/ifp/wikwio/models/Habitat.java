package com.ifp.wikwio.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Habitat implements Parcelable{

	private long id;
	private String name;
	private int habitatOrder;
	
	public Habitat(){}
	
	public Habitat(Parcel in){
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

	public int getHabitatOrder() {
		return habitatOrder;
	}

	public void setHabitatOrder(int habitatOrder) {
		this.habitatOrder = habitatOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + habitatOrder;
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
		Habitat other = (Habitat) obj;
		if (habitatOrder != other.habitatOrder)
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
		return "Habitat [id=" + id + ", name=" + name + ", habitatOrder="
				+ habitatOrder + "]";
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		name = in.readString();
		habitatOrder = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(habitatOrder);
	}

	public static final Creator<Habitat> CREATOR = new Creator<Habitat>() {
		
		public Habitat createFromParcel(Parcel source) {
			return new Habitat(source);
		}
		
		public Habitat[] newArray(int size) {
			return new Habitat[size];
		}
	};
}
