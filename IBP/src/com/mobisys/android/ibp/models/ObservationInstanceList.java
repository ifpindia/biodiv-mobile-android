package com.mobisys.android.ibp.models;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class ObservationInstanceList implements Parcelable{

	private long id;
	private String placeName;
	private Category group;
	private Habitat habitat;
	private Date fromDate;
	private Date toDate;
	private Date createdOn;
	private Date lastRevised;
	private Author author;
	private String thumbnail;
	private String notes;
	private String summary;
	private int rating;
	private MaxVotedRecord maxVotedReco;
	
	public ObservationInstanceList(){}
	
	public ObservationInstanceList(Parcel in){
		readFromParcel(in);
	}
	
	public ObservationInstanceList(long id, String placeName, Category group,
			Habitat habitat, Date fromDate, Date toDate, Date createdOn,
			Date lastRevised, Author author, String thumbnail, String notes,
			String summary, int rating, MaxVotedRecord maxVotedReco) {
		super();
		this.id = id;
		this.placeName = placeName;
		this.group = group;
		this.habitat = habitat;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.createdOn = createdOn;
		this.lastRevised = lastRevised;
		this.author = author;
		this.thumbnail = thumbnail;
		this.notes = notes;
		this.summary = summary;
		this.rating = rating;
		this.maxVotedReco=maxVotedReco;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public Category getGroup() {
		return group;
	}

	public void setGroup(Category group) {
		this.group = group;
	}

	public Habitat getHabitat() {
		return habitat;
	}

	public void setHabitat(Habitat habitat) {
		this.habitat = habitat;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastRevised() {
		return lastRevised;
	}

	public void setLastRevised(Date lastRevised) {
		this.lastRevised = lastRevised;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public MaxVotedRecord getMaxVotedReco() {
		return maxVotedReco;
	}

	public void setMaxVotedReco(MaxVotedRecord maxVotedReco) {
		this.maxVotedReco = maxVotedReco;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result
				+ ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((habitat == null) ? 0 : habitat.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((lastRevised == null) ? 0 : lastRevised.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((placeName == null) ? 0 : placeName.hashCode());
		result = prime * result + rating;
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result
				+ ((thumbnail == null) ? 0 : thumbnail.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
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
		ObservationInstanceList other = (ObservationInstanceList) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (habitat == null) {
			if (other.habitat != null)
				return false;
		} else if (!habitat.equals(other.habitat))
			return false;
		if (id != other.id)
			return false;
		if (lastRevised == null) {
			if (other.lastRevised != null)
				return false;
		} else if (!lastRevised.equals(other.lastRevised))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (placeName == null) {
			if (other.placeName != null)
				return false;
		} else if (!placeName.equals(other.placeName))
			return false;
		if (rating != other.rating)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (thumbnail == null) {
			if (other.thumbnail != null)
				return false;
		} else if (!thumbnail.equals(other.thumbnail))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObservationInstanceList [id=" + id + ", placeName=" + placeName
				+ ", group=" + group + ", habitat=" + habitat + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", createdOn=" + createdOn
				+ ", lastRevised=" + lastRevised + ", author=" + author
				+ ", thumbnail=" + thumbnail + ", notes=" + notes
				+ ", summary=" + summary + ", rating=" + rating + "]";
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		placeName=in.readString();
		group=in.readParcelable(Category.class.getClassLoader());
		habitat=in.readParcelable(Habitat.class.getClassLoader());
		fromDate= (Date) in.readSerializable();
		toDate= (Date) in.readSerializable();
		createdOn= (Date) in.readSerializable();
		lastRevised= (Date) in.readSerializable();
		author=in.readParcelable(Author.class.getClassLoader());
		thumbnail=in.readString();
		notes=in.readString();
		summary=in.readString();
		rating=in.readInt();
		maxVotedReco=in.readParcelable(MaxVotedRecord.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(placeName);
		dest.writeParcelable(group, flags);
		dest.writeParcelable(habitat, flags);
		dest.writeSerializable(fromDate);
		dest.writeSerializable(toDate);
		dest.writeSerializable(createdOn);
		dest.writeSerializable(lastRevised);
		dest.writeParcelable(author, flags);
		dest.writeString(thumbnail);
		dest.writeString(notes);
		dest.writeString(summary);
		dest.writeInt(rating);
		dest.writeParcelable(maxVotedReco, flags);
	}

	public static final Creator<ObservationInstanceList> CREATOR = new Creator<ObservationInstanceList>() {
		
		public ObservationInstanceList createFromParcel(Parcel source) {
			return new ObservationInstanceList(source);
		}
		
		public ObservationInstanceList[] newArray(int size) {
			return new ObservationInstanceList[size];
		}
	};
}
