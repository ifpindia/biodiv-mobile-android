package com.mobisys.android.ibp.http;

public class Request {

	public static final String HEADER_AUTH_KEY = "header_auth_key";
	public static final String BASE_URL = "indiabiodiversity.saturn.strandls.com";
	
	//Method - get, post or put
	public static final String METHOD = "method";
	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	public static final String METHOD_PUT = "put";
	public static final String METHOD_DELETE = "delete";
	
	//PATH
	public static final String PATH_REGISTER = "/register/user";
	public static final String PATH_RESEND_EMAIL = "/register/resend";
	public static final String PATH_LOGIN = "/api/login";
	public static final String PATH_FORGOT_PASSWORD = "/api/register/forgotPassword";
	
	
	//PARAMS
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_PASSWORD2 = "password2";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_LOCATION = "location";
	
}
