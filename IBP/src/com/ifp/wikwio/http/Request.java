package com.ifp.wikwio.http;

public class Request {

	public static final String HEADER_AUTH_KEY = "X-Auth-Token";
	public static final String HEADER_APP_KEY = "X-AppKey";
	public static final String HEADER_ACCEPT = "Accept";
	
	//Method - get, post or put
	public static final String METHOD = "method";
	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	public static final String METHOD_PUT = "put";
	public static final String METHOD_DELETE = "delete";
	
	//PATH
	public static final String PATH_REGISTER = "/api/register/user";
	public static final String PATH_RESEND_EMAIL = "/api/register/resend";
	public static final String PATH_LOGIN = "/api/login";
	public static final String PATH_FORGOT_PASSWORD = "/api/register/forgotPassword";
	public static final String PATH_FB_LOGIN="/api/oauth/callback/facebook";
	public static final String PATH_GOOGLE_LOGIN = "/api/oauth/callback/google";
	public static final String PATH_LOGOUT = "/api/logout";
	public static final String PATH_SPECIES_CATEGORIES = "/speciesGroup";
	public static final String PATH_GET_OBSERVATIONS = "/observation";
	//public static final String PATH_SAVE_OBSERVATION = "/api/observation/save";
	public static final String PATH_SAVE_OBSERVATION = "/api/observation";
	public static final String PATH_UPLOAD_RESOURCE="/api/observation/upload_resource";
	public static final String PATH_GET_OBSERVTAION_DETAIL = "/api/observation/show/";
	public static final String PATH_SHOW_SPECIES_DETAIL = "/species/show/";
	public static final String PATH_UPDATE_OBSERVATION = "/api/observation/%d";
	//public static final String PATH_DELETE_OBSERVATION = "/api/observation/flagDeleted";
	public static final String PATH_DELETE_OBSERVATION = "/api/observation/%d/flagDeleted";
	public static final String PATH_AUTO_COMPLETE = "/api/recommendation/suggest";
	public static final String PATH_ALL_USER_GROUPS = "/api/group";
	public static final String PATH_GET_USER_GROUPS="/api/userGroup/getUserUserGroups";
	public static final String PATH_JOIN_GROUP="/api/group/%s/joinUs";
	
	//PARAMS
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_PASSWORD2 = "password2";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_LOCATION = "location";
	public static final String PARAM_USERNAME = "username";

	public static final String PARAM_FB_ACCESS_TOKEN = "access_token";
	public static final String NEARBY_TYPE = "type";
	public static final String MAXRADIUS = "maxRadius";
	public static final String GROUP_ID = "sGroup";
	public static final String PARAM_OFFSET = "offset";

	public static final String SPECIES_GROUP_ID = "group_id";
	public static final String HABITAT_ID = "habitat_id";
	public static final String FROM_DATE = "fromDate";
	public static final String PLACE_NAME = "placeName";
	public static final String AREAS = "areas";
	public static final String COMMON_NAME = "commonName";
	public static final String SCI_NAME = "recoName";
	public static final String RESOURCE_TYPE = "resType";
	public static final String RESOURCE_LIST_TYPE = "resourceListType";
	public static final String AGREE_TERMS = "agreeTerms";
	public static final String USER = "user";
	public static final String NOTES = "notes";
	public static final String OBV_ID = "id";
	public static final String PARAM_ID = "id";
	
	public static final String AUTO_COMPLETE_URL = "http://"+HttpUtils.stageOrProdBaseURL()+PATH_AUTO_COMPLETE;
	public static final String USER_GROUP_LIST = "userGroupsList";
	public static final String MAX = "max";
	public static final String LIMIT = "limit";
	
}
