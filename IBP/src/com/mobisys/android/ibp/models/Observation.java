package com.mobisys.android.ibp.models;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Observation implements Parcelable{

	private ArrayList<ObservationInstance> observationInstanceList;
	private ActiveFilter activeFilters;
	
	public Observation(){}
	
	public Observation(Parcel in){
		readFromParcel(in);
	}
	
	public Observation(ArrayList<ObservationInstance> observationInstanceList, ActiveFilter activeFilters) {
		super();
		this.observationInstanceList = observationInstanceList;
		this.activeFilters = activeFilters;
	}

	public ArrayList<ObservationInstance> getObservationInstanceList() {
		return observationInstanceList;
	}

	public void setObservationInstanceList(
			ArrayList<ObservationInstance> observationInstanceList) {
		this.observationInstanceList = observationInstanceList;
	}

	public ActiveFilter getActiveFilters() {
		return activeFilters;
	}

	public void setActiveFilters(ActiveFilter activeFilters) {
		this.activeFilters = activeFilters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeFilters == null) ? 0 : activeFilters.hashCode());
		result = prime
				* result
				+ ((observationInstanceList == null) ? 0
						: observationInstanceList.hashCode());
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
		Observation other = (Observation) obj;
		if (activeFilters == null) {
			if (other.activeFilters != null)
				return false;
		} else if (!activeFilters.equals(other.activeFilters))
			return false;
		if (observationInstanceList == null) {
			if (other.observationInstanceList != null)
				return false;
		} else if (!observationInstanceList
				.equals(other.observationInstanceList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Observation [observationInstanceList="
				+ observationInstanceList + ", activeFilters=" + activeFilters
				+ "]";
	}

	private void readFromParcel(Parcel in) {
		observationInstanceList=new ArrayList<ObservationInstance>();
		in.readList(observationInstanceList, ObservationInstance.class.getClassLoader());
		activeFilters=in.readParcelable(ActiveFilter.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(observationInstanceList);
		dest.writeParcelable(activeFilters, flags);
	}

	public static final Creator<Observation> CREATOR = new Creator<Observation>() {
		
		public Observation createFromParcel(Parcel source) {
			return new Observation(source);
		}
		
		public Observation[] newArray(int size) {
			return new Observation[size];
		}
	};
}
