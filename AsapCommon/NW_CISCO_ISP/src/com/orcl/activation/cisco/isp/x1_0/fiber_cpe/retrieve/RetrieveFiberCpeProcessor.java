/**
 * TODO Copyright Notice
 */
package com.orcl.activation.cisco.isp.x1_0.fiber_cpe.retrieve;

import com.orcl.activation.cisco.isp.x1_0.IspConnectionHandler;
import com.orcl.activation.cisco.isp.x1_0.fiber_cpe.retrieve.generated.*;
import com.orcl.activation.cisco.isp.x1_0.helper.ISPConstants;
import com.orcl.activation.cisco.isp.x1_0.helper.ISPHelper;
import com.orcl.activation.cisco.isp.x1_0.request.QueryRequest;
import com.orcl.activation.cisco.isp.x1_0.request.QueryRequest.Credential;
import com.orcl.activation.cisco.isp.x1_0.response.QueryResponse;
import com.orcl.activation.cisco.isp.x1_0.response.QueryResponse.Service;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.mslv.activation.jinterpreter.JProcessor;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;
import com.mslv.studio.activation.implementation.IExitType;

public class RetrieveFiberCpeProcessor implements RetrieveFiberCpeProcessorInterface {

	/*
	 * The logger to use for all log information generated by this processor.
	 */
	private ILogger logger = null;
	private String responseCode = null;
	private String responseDesc = null;
	private JProcessor jproc=null;
	private String mac = null;
	private String serialNumber = null;
	private String state = null;
	private String IPAddress = null;
	private String CPEType = null;
	private String Vendor = null;
	private String Model = null;
	private String DownloadSpeed = null;
	private String UploadSpeed = null;

	public void execute(RetrieveFiberCpeInput parms, RetrieveFiberCpeOutput output, IConnectionHandler connection,
			IExitType exitType, ISystemParameters systemParameters) throws Exception {

		ISPHelper ispHelper = new ISPHelper(); 
		try {

			logger.logDebug("RetrieveMacProcessor: execute() - Completed step A of execution.");
			logger.logDebug("JPROC CSDL_CMD "+jproc.getParam(ISPConstants.CSDL_CMD));

			IspConnectionHandler connHdlr = (IspConnectionHandler) connection;

			// Read input parameters
			String macAddress = parms.getMACAddress();

			// Check if parameter is not null or have no value
			ispHelper.isNotNull(macAddress, ISPConstants.MACAddress);

			// Construct Provision Request
			QueryRequest queryReq = new QueryRequest();
			queryReq.setAction(ISPConstants.ACTION_QUERY);
			queryReq.setMACAddress(macAddress);

			Credential cred = new Credential();
			cred.setUsername(connHdlr.getUserId());
			cred.setPassword(connHdlr.getPassword());

			String queryReqStr = ispHelper.getRequestString(queryReq);
			logger.logDebug("Query Request - " + queryReqStr);

			//Not connected with Network so no need of checking loopback condition
			//if (!systemParameters.isLoopback()) {

			String provResStr = connHdlr.sendRequest(queryReqStr);
			
			Unmarshaller unmarshaller = JAXBContext.newInstance(QueryResponse.class).createUnmarshaller();
			// unmarshaller.setProperty(Unmarshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			QueryResponse queryRes = (QueryResponse) unmarshaller.unmarshal(new StringReader(queryReqStr));

			int Result = queryRes.getResult();

			if (Result == 0) {

				mac=queryRes.getMACAddress();
				serialNumber=queryRes.getSerialNumber();
				state=queryRes.getState();
				List<Service> response = queryRes.getService();
				for (Service res : response)
				{
					IPAddress = res.getIPAddress();
					CPEType = res.getCPEType();
					Vendor = res.getVendor();
					Model = res.getModel();
					DownloadSpeed = res.getDownloadSpeed();
					UploadSpeed = res.getUploadSpeed();

				}
				logger.logDebug("MACAddress - " + mac +"\nserialNumber - "+serialNumber+"\nstate - "+state);
				logger.logDebug("Service::" + "\nIPAddress - " + IPAddress +"\nCPEType - "+CPEType+"\nVendor - "+Vendor+"\nModel - "+Model
						+"\nDownloadSpeed - "+DownloadSpeed +"\nUploadSpeed - "+UploadSpeed);
			} else
			{
				responseCode = Integer.toString(Result);
				responseDesc = queryRes.getDesc();
			}
		}
		catch(Exception ex)
		{
			responseCode = ispHelper.getExceptionResponseCode(ex);
			responseDesc = ex.getMessage()+" in "+jproc.getParam(ISPConstants.CSDL_CMD);
			logger.logError("Exception occurred: "+ex.toString());
		}

		finally
		{
			String exitValue = exitType.setTypeByMatch(responseCode, 
					responseDesc, IExitType.FAIL);

			if ((exitValue != null) && (exitValue.equals(IExitType.FAIL))) {
				output.addActionParameter(PARAMETER_ERROR, responseDesc);
				output.addActionParameter(PARAMETER_VENDOR, VENDOR);
				output.addActionParameter(PARAMETER_TECHNOLOGY, TECHNOLOGY);
				output.addActionParameter(PARAMETER_SOFTWARE_LOAD, SOFTWARE_LOAD);
			}
		}
	}

	/**
	 * The init method is called prior to the execute method.  Additional initialization logic may be added as necessary.
	 *
	 * @param processor a reference to the underlying proxy processor.  Use of this parameter is strongly discouraged.
	 * @param logger a logging instance to be used for all processor logging.
	 */
	public void init(Object processor, ILogger logger) {
		this.logger = logger;
		//If connected to network set Loopback response
		//responseCode = ISPConstants.DEFAULT_SUCCESS_CODE;
		//responseDesc = ISPConstants.SUCCESS;

		if (processor instanceof JProcessor) {
			jproc = (JProcessor) processor;
		}	
	}
}