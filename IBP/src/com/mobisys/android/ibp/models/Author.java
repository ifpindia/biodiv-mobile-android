package com.mobisys.android.ibp.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import android.os.Parcel;
import android.os.Parcelable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Author implements Parcelable{

	private long id;
	private String name;
	private String email;
	private String icon;
	
	public Author(){}
	
	public Author(Parcel in){
		readFromParcel(in);
	}
	
	public Author(long id, String name, String email, String icon) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.icon = icon;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
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
		Author other = (Author) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
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
		return "Author [id=" + id + ", name=" + name + ", email=" + email
				+ ", icon=" + icon + "]";
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		name=in.readString();
		email=in.readString();
		icon=in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(icon);
	}

	public static final Creator<Author> CREATOR = new Creator<Author>() {
		
		public Author createFromParcel(Parcel source) {
			return new Author(source);
		}
		
		public Author[] newArray(int size) {
			return new Author[size];
		}
	};
}
