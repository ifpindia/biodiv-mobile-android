package com.mobisys.android.ibp.http;

public class Request {

	public static final String HEADER_AUTH_KEY = "X-Auth-Token";
	
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
	public static final String PATH_LOGOUT = "/api/logout";
	public static final String PATH_SPECIES_CATEGORIES = "/speciesGroup/list";
	public static final String PATH_NEARBY_OBSERVATIONS = "/observation/listJSON";
	
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
}
