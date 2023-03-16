package com.oracle.activation.eric.mobile.x2_0;

public class MobConstants {

	public static final String CSDL_CMD = "CSDL_CMD";
	
	public static final String ADD= "add";
	public static final String REMOVE= "remove";
	public static final String SUSPEND= "suspend";
	public static final String RESUME= "resume";
	public static final String MOVE= "move";
	public static final String MODIFY= "modify";
	
	public static final String HOST = "Host";
	public static final String URLSUFFIX = "URL_Suffix";
	
 	public static final String SUCCESS = "SUCCESS";
	public static final int MAX_LENGTH = 1024;
    public static final String DEFAULT_SUCCESS_CODE = "0";
    public static final String PROV_EXCEPTION_CODE = "77777";  
    public static final String CONN_EXCEPTION_CODE = "99999"; 
    public static final String PARAM_MISSING = "88888";
    public static final String UNKNOWN_HOST_EXCEPTION_CODE = "66666"; 
    public static final String SOCKET_TIMEOUT_EXCEPTION = "55555";
	
	public static final String MSISDN = "msisdn";
	public static final String EMAIL = "email";
	public static final String ROAMING = "roamingAllowed";
	public static final String SERVICE = "service";
	public static final String IMSI = "imsi";
	public static final String ICCID = "iccid";
	public static final String NAME = "name";
	public static final String SERVICEPLAN = "servicePlan";
	public static final String PAYTYPE = "payType";
	public static final String ENABLEMMS = "enableMMS";
	public static final String OLD_MSISDN = "old_msisdn";
	
	public static final String req_msisdn = "&msisdn=";
	public static final String req_email = "&email=";
	public static final String req_roaming = "&roamingAllowed=";
	public static final String req_service = "&service=";
	public static final String req_imsi = "&imsi=";
	public static final String req_iccid = "&iccid=";
	public static final String req_name = "&name=";
	public static final String req_servicePlan = "&servicePlan=";
	public static final String req_enableMMS = "&enableMMS=";
	public static final String req_old_msisdn = "&old_msisdn=";
	public static final String req_payType = "&payType=";
	
	public static final String URL_KEY ="?userid=";
	public static final String URL_VALUE="&password=";
	
	public static final String ADD_APP_NAME= "&add_skey[0][application_name]=add_subscriber";
	public static final String REM_APP_NAME= "&remove_skey[0][application_name]=remove_subscriber";
	public static final String SUS_APP_NAME= "&suspend_skey[0][application_name]=suspend_subscriber";
	public static final String RES_APP_NAME= "&resume_skey[0][application_name]=resume_subscriber";
	public static final String MOD_APP_NAME= "&modify_skey[0][application_name]=change_subscriber";

	public static final String USERID = "Username";
	public static final String LOG_ENABLED = "Log_Enabled";
	public static final String MOB_PSWD = "Mob_password";



}
