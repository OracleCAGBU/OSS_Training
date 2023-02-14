package com.orcl.activation.cisco.isp.x1_0.helper;
/**
 * This class is for creating custom exception
 *
 */
public class AuthTokenNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public AuthTokenNotFoundException(){
		
	}
	/**
	 * This method is for setting exception
	 * @param msg
	 */
	public AuthTokenNotFoundException(String msg){
		super(msg);
	}
}