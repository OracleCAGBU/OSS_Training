package com.oracle.activation.eric.mobile.x2_0;
/**
 * This class is for creating custom exception
 *
 */
public class MandatoryAttributeNotSetException extends Exception {
	private static final long serialVersionUID = 1L;

	public MandatoryAttributeNotSetException(){
		
	}
	/**
	 * This method is for setting exception
	 * @param msg
	 */
	public MandatoryAttributeNotSetException(String msg){
		super(msg);
	}
}