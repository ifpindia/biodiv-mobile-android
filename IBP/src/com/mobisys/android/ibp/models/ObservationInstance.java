package com.mobisys.android.ibp.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class ObservationInstance implements Parcelable{

	public static final String OI = "oi";
	public static final String ObsInstance = "obs_instance";
	
	public static class NameRecordDeserializer extends JsonDeserializer<NameRecord> {
		@Override
	    public NameRecord deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			ObjectCodec oc = parser.getCodec();
			JsonNode node = oc.readTree(parser);
			Iterator<JsonNode> iter;
			StringBuilder b=new StringBuilder();
			if(!node.isNull() && node.get("commonNamesRecoList")!=null){
				iter = node.get("commonNamesRecoList").getElements();
				while(iter.hasNext()){
					JsonNode n = iter.next();
					b.append(n.get("name").asText());
					if(iter.hasNext()) b.append(", ");
					Log.d("Observation", "Name: "+n.get("name").asText());
				}
			}
			String commnName=b.toString();
			String sciName="";
			if(!node.isNull()&& node.get("sciNameReco")!=null)
				sciName=node.get("sciNameReco").get("name").asText();
			
			NameRecord nr=new NameRecord();
			nr.setCommonName(commnName);
			nr.setScientificName(sciName);
			return nr;
		}
	}
	
	private long id;
	private String placeName;
	private String topology;
	private Category group;
	private Habitat habitat;
	private String fromDate;
	private String toDate;
	private String createdOn;
	private String lastRevised;
	private Author author;
	private String thumbnail;
	private String notes;
	private String summary;
	private int rating;
	@JsonDeserialize(using = NameRecordDeserializer.class)
	private NameRecord maxVotedReco;
	private ArrayList<Resource> resource;
	
	public ObservationInstance(){}
	
	public ObservationInstance(Parcel in){
		readFromParcel(in);
	}
	
	public ObservationInstance(long id, String placeName, String topology,Category group,
			Habitat habitat, String fromDate, String toDate, String createdOn,
			String lastRevised, Author author, String thumbnail, String notes,
			String summary, int rating, NameRecord maxVotedReco, ArrayList<Resource> resource) {
		super();
		this.id = id;
		this.placeName = placeName;
		this.topology=topology;
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
		this.resource = resource;
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

	public String getTopology() {
		return topology;
	}

	public void setTopology(String topology) {
		this.topology = topology;
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getLastRevised() {
		return lastRevised;
	}

	public void setLastRevised(String lastRevised) {
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

	public NameRecord getMaxVotedReco() {
		return maxVotedReco;
	}

	public void setMaxVotedReco(NameRecord maxVotedReco) {
		this.maxVotedReco = maxVotedReco;
	}

	public ArrayList<Resource> getResource() {
		return resource;
	}

	public void setResource(ArrayList<Resource> resource) {
		this.resource = resource;
	}

	private void readFromParcel(Parcel in) {
		id=in.readLong();
		placeName=in.readString();
		topology=in.readString();
		group=in.readParcelable(Category.class.getClassLoader());
		habitat=in.readParcelable(Habitat.class.getClassLoader());
		fromDate= in.readString();
		toDate= in.readString();
		createdOn= in.readString();
		lastRevised= in.readString();
		author=in.readParcelable(Author.class.getClassLoader());
		thumbnail=in.readString();
		notes=in.readString();
		summary=in.readString();
		rating=in.readInt();
		maxVotedReco=in.readParcelable(NameRecord.class.getClassLoader());
		resource=new ArrayList<Resource>();
		in.readList(resource, Resource.class.getClassLoader());
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(placeName);
		dest.writeString(topology);
		dest.writeParcelable(group, flags);
		dest.writeParcelable(habitat, flags);
		dest.writeString(fromDate);
		dest.writeString(toDate);
		dest.writeString(createdOn);
		dest.writeString(lastRevised);
		dest.writeParcelable(author, flags);
		dest.writeString(thumbnail);
		dest.writeString(notes);
		dest.writeString(summary);
		dest.writeInt(rating);
		dest.writeParcelable(maxVotedReco, flags);
		dest.writeList(resource);
	}

	public static final Creator<ObservationInstance> CREATOR = new Creator<ObservationInstance>() {
		
		public ObservationInstance createFromParcel(Parcel source) {
			return new ObservationInstance(source);
		}
		
		public ObservationInstance[] newArray(int size) {
			return new ObservationInstance[size];
		}
	};
	
}
