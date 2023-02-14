package com.broadband.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.broadband.ci.BaseDesigner;
import com.broadband.utils.Constants;

import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.platform.util.Utils;


public class Util {
	
	private static final Log log = LogFactory.getLog(BaseDesigner.class);
	
	/**
	 * This method prints the debug statement
	 * @param objects	 
	 * @return
	 * @throws 
	 */
	private static void debug(Object ...objects) {
		try {
			if (log.isDebugEnabled()) {
				StringBuilder sb=new StringBuilder();
				for(Object obj:objects) {
					sb.append(obj);
				}
				log.debug("",sb.toString());
			}
		} catch (Exception e) {
			log.debug("", "Error Message :"+e.getMessage());
			log.debug("", "Exception :"+e);
		}
	}
	
 	/**
 	 * This method returns the Key based on the given value from the map.
 	 * @param map
 	 * @param value
 	 * @return <V>
 	 */
 	public static <K, V> K getKey(Map<K, V> map, V value) {
	    for (Entry<K, V> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
 	
	 /**
	  * This method returns the canonical host name of the server. 
	  * @return String
	  * @throws ValidationException
	  */
 	public static String getCanonicalHostName() throws ValidationException {
		String hostName = "";
		try {
			debug("LocalHost:" + InetAddress.getLocalHost());
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {			
			debug("Error Meassge :", e.getMessage());
			throw new ValidationException(e);
		}
		return hostName;
 	}
 	
 	/**
	  * This method will check the given value is Double or not 
	  * @param  strNum
	  * @return boolean
	  * @throws 
	  */
 	public static boolean isNumeric(String strNum) {
 	    try {
 	        Double.parseDouble(strNum);
 	    } catch (NumberFormatException | NullPointerException nfe) {
 	    	debug("EXception :", nfe);
 	        return false;
 	    }
 	    return true;
 	}
 	
 	/**
	  * This method will check the given value is Integer or not 
	  * @param strNum
	  * @return boolean
	  * @throws 
	  */
 	public static boolean isInteger(String strNum) {
 	    try {
 	        Integer.parseInt(strNum);
 	    } catch (NumberFormatException | NullPointerException nfe) {
 	    	debug("EXception :", nfe);
 	        return false;
 	    }
 	    return true;
 	}
 	
 	/**
	  * This method will return the Current Date and Time  
	  * @param 
	  * @return String
	  * @throws 
	  */
	public static String getSystemDateTime() {
		debug("getSystemDateTime - START");
		Format formatter = new SimpleDateFormat(Constants.DATETIME_FORMAT);
		String currentDateTime = formatter.format(new Date());
		debug("getSystemDateTime - END, currentDateTime:" + currentDateTime);
		return currentDateTime;
	}
	
	/**
	  * This method will convert the Input string into the date
	  * @param dateTimeString
	  * @return String
	  * @throws 
	  */
	public static Date getDateFromString(String dateTimeString) {
		debug("getDateFromString - START, dateTimeString: " + dateTimeString);
		SimpleDateFormat format = new SimpleDateFormat(Constants.DATETIME_FORMAT);
		Date date = null;
		try {
			date = format.parse(dateTimeString);
			debug("date: " + date.toString());
		} catch(ParseException pe) {			
			debug("Error Message :"+pe.getMessage());	
			debug("Exception :"+pe);
		}
		debug("getDateFromString - END");
		return date;
	}
	
	/**
	 * This method will add given no. of days on a given date. 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days){
		debug("addDays - START, Current date is " + date + " + " + days + "days");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); //minus number would decrement the days
		debug("addDays - END, Extended date is " + cal.getTime());
		return cal.getTime();
	}
	

	/**
	  * This method will add/delete the Services depend upon the ServiceNumber and Action value
	  * @param existingServices
	  * @param serviceNumber
	  * @param action
	  * @return String
	  * @throws 
	  */
	public static String addRemoveItemFromCSS(String existingServices, String serviceNumber, String action) {
		debug("addRemoveItemFromCSS - START");
		
		debug("existingServices: " + existingServices);
		debug("serviceNumber: " + serviceNumber);
		debug("action: " + action);
		
		String returnValue = Constants.EMPTY;
		if(!Utils.isEmpty(existingServices)){
			String[] servicesArray = existingServices.split(",");
			List<String> servicesList = new ArrayList<>(Arrays.asList(servicesArray));
			
			if(Constants.ADD.equalsIgnoreCase(action)){
				if(!servicesList.contains(serviceNumber)){
					servicesList.add(serviceNumber);
				} else {
					debug("ServiceNumber already set on port, so no need to add again.");
				}
			} else if(Constants.DELETE.equalsIgnoreCase(action)){
				servicesList.remove(serviceNumber);
			}
			returnValue = String.join(Constants.COMMA, servicesList);
		} else {
			if(Constants.ADD.equalsIgnoreCase(action)){
				returnValue = serviceNumber;
			} else if(Constants.DELETE.equalsIgnoreCase(action)){
				debug("ServiceNumber not found on port, so can't be cleared.");
			}
		}
		debug("addRemoveItemFromCSS - END");
		return returnValue;
	}
	
	/**
	 * This method generates random 10 char password.
	 *  �	The password must contain upper-case letter. 
		�	The password must contain lower-case letter. 
		�	The password must contain digits. 
		�	The password must contain special characters. Accepted special characters are `~!@#$%^&*()-_=+\|[{}];:�",<.>/? . 
		�	Avoid using three or more than three consecutive letters or digits, for example, ABC or 123. 
		�	Avoid using account or the account in reverse order as the password. 
		�	Avoid using three or more than three same characters, for example, AAA
	 * @return
	 */
	public static String generatePassword() {
		debug("generatePassword - START");
		
	    SecureRandom random = new SecureRandom();

	    // different dictionaries used
	    String alphaCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String numeric = "0123456789";
	    String alpha = "abcdefghijklmnopqrstuvwxyz";
	    String specialCaps = "`~!@#$%^&*()-_=+\\|[{}];:�\",<.>/ .";//Removed the question mark because OSM Manual task is displaying it as single quote. Its a workaround.
	    
	    List<String> entries = new ArrayList<>();
	    entries.add(alphaCaps);
	    entries.add(numeric);
	    entries.add(alpha);
	    entries.add(specialCaps);
	    entries.add(alphaCaps);
	    entries.add(numeric);
	    entries.add(alpha);
	    entries.add(specialCaps);
	    entries.add(alphaCaps);
	    entries.add(numeric);
	    
	    int len = 10;
	    debug("Password length to be generated: " + len);	    
	    
	    
	    StringBuilder result=new StringBuilder();
	    for (int i = 0; i < 10; i++) {
	    	String entry = entries.get(i);
	        int index = random.nextInt(entry.length());	        
	        result.append(entry.charAt(index));
	    }
	    
	    debug("generatePassword - END, " + result.toString());
	    return result.toString();
	}
}
