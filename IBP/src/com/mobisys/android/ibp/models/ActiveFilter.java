package com.mobisys.android.ibp.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class ActiveFilter implements Parcelable{

	private double lat;
	@JsonProperty("long")
	private double lng;
	private int maxRadius;
	
	public ActiveFilter(){}
	
	public ActiveFilter(Parcel in){
		readFromParcel(in);
	}
	
	public ActiveFilter(double lat,double lng, int maxRadius) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.maxRadius = maxRadius;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getMaxRadius() {
		return maxRadius;
	}

	public void setMaxRadius(int maxRadius) {
		this.maxRadius = maxRadius;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + maxRadius;
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
		ActiveFilter other = (ActiveFilter) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		if (maxRadius != other.maxRadius)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ActiveFilter [lat=" + lat + ", lng=" + lng + ", maxRadius="
				+ maxRadius + "]";
	}

	private void readFromParcel(Parcel in) {
		lat=in.readDouble();
		lng=in.readDouble();
		maxRadius=in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeInt(maxRadius);
	}

	public static final Creator<ActiveFilter> CREATOR = new Creator<ActiveFilter>() {
		
		public ActiveFilter createFromParcel(Parcel source) {
			return new ActiveFilter(source);
		}
		
		public ActiveFilter[] newArray(int size) {
			return new ActiveFilter[size];
		}
	};
}
