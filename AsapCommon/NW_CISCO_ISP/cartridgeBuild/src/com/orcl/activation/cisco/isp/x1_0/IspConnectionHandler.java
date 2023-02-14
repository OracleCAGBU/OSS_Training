/**
 * TODO Copyright Notice
 */
package com.orcl.activation.cisco.isp.x1_0;

import com.mslv.activation.jinterpreter.NEConnection;
import com.mslv.activation.server.ASCAppl;
import com.mslv.activation.server.Security;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.SOAPException;

import com.mslv.activation.jinterpreter.ConnectionException;
import com.mslv.activation.jinterpreter.DisconnectException;
import com.mslv.activation.jinterpreter.EventType;
import com.mslv.studio.activation.implementation.DiagnosisLogger;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;
import com.orcl.activation.cisco.isp.x1_0.helper.ISPConstants;


public class IspConnectionHandler extends NEConnection implements IConnectionHandler {

	private ILogger logger = new DiagnosisLogger();
	private URL urlObj = null;
	private boolean logEnabled = false;
	private String password = null;
	private String userId = null;

	protected void connect() throws ConnectionException {
		try {

			String hostName = getCommParam(ISPConstants.HOST);
			String port = getCommParam(ISPConstants.PORT);
			String hostUrlSuffix = getCommParam(ISPConstants.HOST_URL_SUFFIX);
			userId = getCommParam(ISPConstants.USER);
			
			Security sec = ASCAppl.getSecurity();
			password = sec.getSecureData(ISPConstants.PASSWORD, 1);
			if(password == null)
				throw new ConnectionException("Error occurred while retrieving Security.getProperty");

			// Host URL
			String hostUrl = "http://"+hostName+":"+port+hostUrlSuffix;
			logger.logDebug(hostUrl);

			urlObj = new URL(hostUrl);

		}
		catch (Exception ex) {
			logger.logError("Connection Exception: " + ex.toString());
			throw new ConnectionException(ex.toString());
		}

	}

	protected void disconnect() throws DisconnectException {
		// nothing to disconnect
				logger.logDebug("Inside SMSConnectionHandler.disconnect()");

	}

	public String sendRequest(String requestXml) throws SOAPException, IOException {

		if (logEnabled) 
			logNeConversation(requestXml, EventType.NE_CMD_EVENT);

		HttpURLConnection httpUrlConnObj = (HttpURLConnection) urlObj.openConnection();
		httpUrlConnObj.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
		httpUrlConnObj.setDoOutput(true);
		httpUrlConnObj.setDoInput(true);

		// Send XML request over HTTP
		DataOutputStream wr = new DataOutputStream(httpUrlConnObj.getOutputStream());
		wr.writeBytes(requestXml);
		wr.flush();
		wr.close();

		String responseStatus = httpUrlConnObj.getResponseMessage();
		logger.logDebug("Response Status: " + responseStatus);

		// Read responseBuilder
		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnObj.getInputStream()));
		String inputLine;
		
		StringBuilder responseBuilder = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			responseBuilder.append(inputLine);
		}
		in.close();
		httpUrlConnObj.disconnect();

		String responseXml = responseBuilder.toString();
		logger.logDebug("Response Message: " +responseXml);
		
		if (logEnabled) 
			logNeConversation(responseXml, EventType.NE_RESP_EVENT);


		return responseXml;
	}

	public String getParameter(String arg0) {
		return this.getCommParam(arg0);
	}

	/*
	 * Standard log management methods
	 */
	
	public void logSoapMessageInOCA(String soapMsg) 
	 {
		if (logEnabled) 
			logNeConversation(soapMsg, EventType.NE_CMD_EVENT);
		 
	 }
	/**
	 * log in table for OCA
	 * @param msg
	 * @param eventType
	 */
	public void logNeConversation(String msg, String eventType) {
		
		while(msg.length()>ISPConstants.MAX_LENGTH)
		{
			log(msg.substring(0, ISPConstants.MAX_LENGTH), eventType);
			msg=msg.substring(ISPConstants.MAX_LENGTH);
		}
		
		// last string which could be less that 255 chars also need to be logged
		log(msg, eventType);
	}

	public void disableLog() {
		setLogging(false);
	}

	public void enableLog() {
		setLogging(true);
	}

	public void setLogging(boolean arg0) {
		logEnabled = arg0;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}