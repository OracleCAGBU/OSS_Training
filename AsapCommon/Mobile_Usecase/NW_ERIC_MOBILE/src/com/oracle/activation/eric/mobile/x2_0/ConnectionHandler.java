/**
 * TODO Copyright Notice
 */
package com.oracle.activation.eric.mobile.x2_0;

import com.mslv.activation.jinterpreter.NEConnection;
import com.mslv.activation.server.ASCAppl;
import com.mslv.activation.server.Security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.mslv.activation.jinterpreter.ConnectionException;
import com.mslv.activation.jinterpreter.DisconnectException;
import com.mslv.activation.jinterpreter.EventType;
import com.mslv.studio.activation.implementation.DiagnosisLogger;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;

public class ConnectionHandler extends NEConnection implements IConnectionHandler {

	private ILogger logger = new DiagnosisLogger();
	private boolean logEnabled = false;
	private String hostName = null;
	private String urlSuffix = null;
	private String keyValue = null;
	private String userId = null;

	protected void connect() throws ConnectionException {
		try
		{
			hostName = getCommParam(MobConstants.HOST);
			urlSuffix = getCommParam(MobConstants.URLSUFFIX);
			userId = getCommParam(MobConstants.USERID);
			logEnabled = Boolean.valueOf(getCommParam(MobConstants.LOG_ENABLED));
			Security sec = ASCAppl.getSecurity();
			keyValue = sec.getSecureData(MobConstants.MOB_PSWD, 1);
			if(keyValue == null)
				throw new ConnectionException("Error occurred while retrieving Security.getProperty");


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

	public String sendRequest(Map<String,String> hm,String type,Boolean islive) throws Exception {

		URL urlObj = null;
		String url = generateUrl(hm,type);		
		String resultMessage = null;
		String resultCode = null;
		logger.logDebug("Provision Request - " + url);

		if(Boolean.TRUE.equals(islive))
		{
			urlObj = new URL(url);
			if (logEnabled) 
				logNeConversation(url, EventType.NE_CMD_EVENT);

			// set connection time out
			URLConnection con = urlObj.openConnection();
			con.setConnectTimeout(5000);
			con.setDoOutput(true);
			//con.setReadTimeout(readTimeout);

			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader dis = new BufferedReader(isr);
			String s;
			logger.logDebug("Response -");

			try
			{
				StringBuilder response = new StringBuilder();
				while ((s = dis.readLine()) != null)
				{
					response.append(s);

					if(s.contains("<return_message>"))
					{
						int startInd = s.indexOf("<return_message>");
						int endInd = s.indexOf("</return_message>");
						resultMessage = s.substring(startInd+16,endInd);		
					}
				}

				logger.logDebug(response.toString());
				logger.logDebug("ResultMessage "+resultMessage);

				if (logEnabled) 
					logNeConversation(response.toString(), EventType.NE_RESP_EVENT);

			}
			catch (MalformedURLException mue)
			{
				logger.logError("Ouch - a MalformedURLException happened.");
				throw new ConnectionException(mue.toString());
			}
			catch (IOException ioe)
			{
				logger.logError("An IOException happened.");
				throw new ConnectionException(ioe.toString());
			}
			finally
			{	      
				dis.close();
				isr.close();
				is.close();
			}
		}
		else {
			String response="<response>  <return_code>0</return_code>  <return_message>Command Succeed</return_message></response>";
			logger.logDebug(response);
			if(response.contains("<return_code>"))
			{
				
				int startInd = response.indexOf("<return_code>");
				int endInd = response.indexOf("</return_code>");
				resultCode = response.substring(startInd+13,endInd);		
				
				int startresp = response.indexOf("<return_message>");
				int endresp = response.indexOf("</return_message>");
				resultMessage = response.substring(startresp+16,endresp);		
			}
		}
		logger.logDebug("Response Code: "+resultCode+"   Response Message:"+resultMessage);
		return resultCode;
	}

	public String getParameter(String arg0) {
		return this.getCommParam(arg0);
	}

	/*
	 * Standard log management methods
	 */

	public void disableLog() {
		setLogging(false);
	}

	public void enableLog() {
		setLogging(true);
	}

	public void setLogging(boolean arg0) {
		logEnabled = arg0;
	}

	/**
	 * log in table for OCA
	 * @param msg
	 * @param eventType
	 */
	public void logNeConversation(String msg, String eventType) {

		while(msg.length()>MobConstants.MAX_LENGTH)
		{
			log(msg.substring(0, MobConstants.MAX_LENGTH), eventType);
			msg=msg.substring(MobConstants.MAX_LENGTH);
		}

		// last string which could be less that 255 chars also need to be logged
		log(msg, eventType);
	}

	private String generateUrl(Map<String,String> hm,String type)
	{
		String url="";
		String msisdn = hm.get(MobConstants.MSISDN);
		String imsi = hm.get(MobConstants.IMSI);
		String iccid = hm.get(MobConstants.ICCID);
		String name = hm.get(MobConstants.NAME);
		String email = hm.get(MobConstants.EMAIL);
		String servicePlan = hm.get(MobConstants.SERVICEPLAN);
		String service = hm.get(MobConstants.SERVICE);
		String roaming = hm.get(MobConstants.ROAMING);
		String enableMMS = hm.get(MobConstants.ENABLEMMS);
		String old_msisdn = hm.get(MobConstants.OLD_MSISDN);
		String payType = hm.get(MobConstants.PAYTYPE);

		String urlPrefix = MobConstants.URL_KEY+userId+MobConstants.URL_VALUE+keyValue;
		if(type.equalsIgnoreCase(MobConstants.ADD))
		{
			url = hostName+urlSuffix+type+urlPrefix+MobConstants.ADD_APP_NAME+
					MobConstants.req_service+service+
					MobConstants.req_msisdn+msisdn+
					MobConstants.req_imsi+imsi+
					MobConstants.req_iccid+iccid+
					MobConstants.req_name+name+
					MobConstants.req_roaming+roaming+
					MobConstants.req_email+email;

			if(service.equalsIgnoreCase("data")) {
				url=url+MobConstants.req_servicePlan+servicePlan;
			}
			if(service.equalsIgnoreCase("voice")) {
				url=url+MobConstants.req_enableMMS+enableMMS+
						MobConstants.req_payType+payType;
			}

		} else if (type.equalsIgnoreCase(MobConstants.REMOVE))
		{
			url = hostName+urlSuffix+type+urlPrefix+MobConstants.REM_APP_NAME+
					MobConstants.req_service+service+
					MobConstants.req_msisdn+msisdn+
					MobConstants.req_imsi+imsi;
		} else if (type.equalsIgnoreCase(MobConstants.SUSPEND))
		{
			url = hostName+urlSuffix+type+urlPrefix+MobConstants.SUS_APP_NAME+
					MobConstants.req_service+service+
					MobConstants.req_msisdn+msisdn+
					MobConstants.req_imsi+imsi;
		} else if (type.equalsIgnoreCase(MobConstants.RESUME))
		{
			url = hostName+urlSuffix+type+urlPrefix+MobConstants.RES_APP_NAME+
					MobConstants.req_service+service+
					MobConstants.req_msisdn+msisdn+
					MobConstants.req_imsi+imsi;
		} else if (type.equalsIgnoreCase(MobConstants.MOVE))
		{
			url = hostName+urlSuffix+type+urlPrefix+MobConstants.MOD_APP_NAME+
					MobConstants.req_service+service+
					MobConstants.req_msisdn+msisdn;

			if(service.equalsIgnoreCase("update_payType")) {
				if(old_msisdn!=null) {
					url=url+MobConstants.req_old_msisdn+old_msisdn;	
				}
				if(servicePlan!=null) {
					url=url+MobConstants.req_servicePlan+servicePlan+
						MobConstants.req_payType+payType;
				}
			}
			if(service.equalsIgnoreCase("update_msisdn")) {

				url=url+MobConstants.req_old_msisdn+old_msisdn+MobConstants.req_imsi+imsi;
			}
		}
		return url;
	}

	/**
	 * log string message in OCA
	 * @param soapMsg
	 */
	public void logSoapMessageInOCA(String soapMsg) 
	{
		if (logEnabled) 
			logNeConversation(soapMsg, EventType.NE_CMD_EVENT);
	}		
	/**
	 * Convert SOAPMessage to String
	 * @param soapMsg
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 */
	public String soapMessageToString(SOAPMessage soapMsg) throws SOAPException, IOException  
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		soapMsg.writeTo(baos);
		//log in JNEP
		logger.logDebug(baos.toString());
		return baos.toString();
	}

	@Override
	public String sendRequest(String message) throws Exception {
		return null;
	}
}

