package com.oracle.activation.eric.mobile.x2_0;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Helper {

	/**
	 * Mandatory parameters check
	 * 
	 * @param val
	 * @param param
	 * @throws MandatoryAttributeNotSetException
	 */
	public void isNotNull(String val, String param) throws MandatoryAttributeNotSetException {
		if (val != null && !val.trim().equals("")) {
			// do nothing
		} else
			throw new MandatoryAttributeNotSetException("Cannot Find Mandatory Parameter " + param);

	}
	
	
	/**
	 * This method checks for exception
	 * 
	 * @param Exception
	 * @return String
	 * 
	 */
	public static String getExceptionResponseCode(Exception ex){
		
		String responseCode = null;
		
		if(ex instanceof MandatoryAttributeNotSetException)
			responseCode = MobConstants.PARAM_MISSING;
		else if(ex instanceof UnknownHostException)
			responseCode = MobConstants.UNKNOWN_HOST_EXCEPTION_CODE;
		else if(ex instanceof ConnectException)
			responseCode = MobConstants.CONN_EXCEPTION_CODE;
		else if(ex instanceof SocketTimeoutException)
			responseCode = MobConstants.SOCKET_TIMEOUT_EXCEPTION;		
		else 
			responseCode = MobConstants.PROV_EXCEPTION_CODE;
		
		return responseCode;
	}		

}
