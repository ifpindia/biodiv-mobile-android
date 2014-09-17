package com.mobisys.android.ibp.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
@DatabaseTable(tableName="observations")
public class ObservationInstance implements Parcelable{

	public static final String OI = "oi";
	public static final String ObsInstance = "obs_instance";
	
	public enum StatusType {
	    SUCCESS,
	    PENDING,
	    FAILURE,
	    INCOMPLETE,
	    PROCESSING;
	}
	
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
			String speciesId=null;
			if(!node.isNull()&& node.get("sciNameReco")!=null){
				sciName=node.get("sciNameReco").get("name").asText();
				if(node.get("sciNameReco").get("speciesId")!=null)
					speciesId=node.get("sciNameReco").get("speciesId").asText();
			}	
			
			NameRecord nr=new NameRecord();
			nr.setCommonName(commnName);
			nr.setScientificName(sciName);
			nr.setSpeciesIdForSciRecord(speciesId);
			return nr;
		}
	}
	
	@DatabaseField(generatedId=true)
	private long server_id;
	@DatabaseField
	private long id;
	@DatabaseField
	private String placeName;
	private String topology;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Category group;
	private Habitat habitat;
	@DatabaseField
	private Date fromDate;
	private Date toDate;
	private Date createdOn;
	private Date lastRevised;
	private Author author;
	private String thumbnail;
	@DatabaseField
	private String notes;
	private String summary;
	private int rating;
	@JsonDeserialize(using = NameRecordDeserializer.class)
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private NameRecord maxVotedReco;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private ArrayList<Resource> resource;
	@DatabaseField
	private long group_id;
	@DatabaseField
	private long habitat_id;
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
	
	public ObservationInstance(){}
	
	public ObservationInstance(Parcel in){
		readFromParcel(in);
	}
	
	public ObservationInstance(long server_id, long id, String placeName, String topology,Category group,
			Habitat habitat, Date fromDate, Date toDate, Date createdOn,
			Date lastRevised, Author author, String thumbnail, String notes,
			String summary, int rating, NameRecord maxVotedReco, ArrayList<Resource> resource, long group_id, long habitat_id, String areas,
			String commonName,String recoName, String resources, String image_type, StatusType status, String message) {
		super();
		this.server_id = server_id;
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
		this.group_id = group_id;
		this.habitat_id = habitat_id;
		this.areas = areas;
		this.commonName = commonName;
		this.recoName = recoName;
		this.resources = resources;
		this.image_type = image_type;
		this.status = status;
		this.message = message;
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

	public long getServer_id() {
		return server_id;
	}

	public void setServer_id(long server_id) {
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

	private void readFromParcel(Parcel in) {
		server_id=in.readLong();
		id=in.readLong();
		placeName=in.readString();
		topology=in.readString();
		group=in.readParcelable(Category.class.getClassLoader());
		habitat=in.readParcelable(Habitat.class.getClassLoader());
		fromDate= (Date) in.readSerializable();
		toDate=(Date) in.readSerializable();
		createdOn=(Date) in.readSerializable();
		lastRevised=(Date) in.readSerializable();
		author=in.readParcelable(Author.class.getClassLoader());
		thumbnail=in.readString();
		notes=in.readString();
		summary=in.readString();
		rating=in.readInt();
		maxVotedReco=in.readParcelable(NameRecord.class.getClassLoader());
		resource=new ArrayList<Resource>();
		in.readList(resource, Resource.class.getClassLoader());
		group_id=in.readLong();
		habitat_id=in.readLong();
		areas=in.readString();
		commonName=in.readString();
		recoName=in.readString();
		resources=in.readString();
		image_type=in.readString();
		status=StatusType.valueOf(in.readString());
		message=in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(server_id);
		dest.writeLong(id);
		dest.writeString(placeName);
		dest.writeString(topology);
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
		dest.writeList(resource);
		dest.writeLong(group_id);
		dest.writeLong(habitat_id);
		dest.writeString(areas);
		dest.writeString(commonName);
		dest.writeString(recoName);
		dest.writeString(resources);
		dest.writeString(image_type);
		if(status!=null) dest.writeString(status!=null?status.name():"");
		dest.writeString(message);
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
