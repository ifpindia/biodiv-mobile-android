package com.mobisys.android.ibp;

public class Constants {

	public static final String PARAM_MESSAGE = "message";
	public static final String FB_ACCESS_TOKEN = "fb_access_token";
	public static final String APP_TOKEN = "token";
	public static final String LAT = "lat";
	public static final String LNG = "long";
	public static final String ACCURACY = "accuracy";
	public static final String ALTITUDE = "altitude";
	public static final String BEARING = "bearing";
	public static final String PROVIDER = "provider";
	public static final String SPEED = "speed";
	public static final String TIME = "time";
	public static final String DEFAULT_LAT ="18.4638392";
	public static final String DEFAULT_LNG ="73.86471";
	public static final String DEFAULT_ADDRESS="B-406, Swami Vivekanand Rd, Padmavati, Upper Indira Nagar Pune, Maharashtra 411037";
	public static final String APP_KEY = "c9c91329-fb27-4b0d-94ca-ee939da0c91d";
	public static final String APPLICATION_JSON = "application/json, text/json";
	
	public static final String NEARBY = "nearBy";
	protected static final String GROUP_ID = "species_group_id";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	public static final int GALLERY_PHOTO = 100;
	public static final int CAMERA_PHOTO= 200;
	public static final int LOCATION_ADDRESS = 300;
	
	public static final String ADDRESS = "address";
	public static final String IMAGE = "IMAGE";
	public static final String RESOURCE_LIST_TYPE = "ofObv";
	public static final String AGREE_TERMS_VALUE = "on";
	public static final String IS_MY_COLLECTION = "isMyCollection";
	public static final String USER_ID = "user_id";
	public static final String SPECIES_ID = "species_id";
	public static final String SCI_NAME = "sci_name";
	
	public static final long HABITATE_ID_STAGING = 267838;
	public static final long HABITATE_ID_PRODUCTION = 267835;
	public static final String FROM_STATUS_SCREEN = "from_status_screen";
	public static final String LOCATION_NOT_FETCHED = "location_not_fetched";
	
	public static final long stagingOrProdHabitatId(){
		if(Preferences.IS_STAGING) return HABITATE_ID_STAGING;
		else return HABITATE_ID_PRODUCTION;
	}
}
