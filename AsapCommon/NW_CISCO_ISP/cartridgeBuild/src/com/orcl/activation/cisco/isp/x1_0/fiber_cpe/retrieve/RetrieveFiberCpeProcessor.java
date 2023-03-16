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
			cred.setUser(connHdlr.getUserId());
			cred.setAuthentication(connHdlr.getPassword());

			queryReq.setCredential(cred);

			String queryReqStr = ispHelper.getRequestString(queryReq);
			logger.logDebug("Query Request - " + queryReqStr);

			QueryResponse queryRes = null ;
			Unmarshaller unmarshaller = null;

			if (!systemParameters.isLoopback()) {

				String queryResStr = connHdlr.sendRequest(queryReqStr);

				unmarshaller = JAXBContext.newInstance(QueryResponse.class).createUnmarshaller();

				queryRes = (QueryResponse) unmarshaller.unmarshal(new StringReader(queryResStr));
			}
			else {

				QueryResponse resp=new QueryResponse();
				resp.setDesc("MAC NOT FOUND");
				resp.setResult(3);

				//If MAC ALREADY PRESENT

				/*resp.setResult(0);
				resp.setDesc("MAC FOUND");
				resp.setSerialNumber("6473765735");
				resp.setMACAddress("6482CBDC0452");
				resp.setState("Active");
				Service respService=new Service();
				respService.setCPEType("4G");
				respService.setDownloadSpeed("200Mbps");
				respService.setIPAddress("131.245.453.35");
				respService.setModel("JIO LOT 3-DP 1392");
				respService.setUploadSpeed("150Mbps");
				respService.setUsername("Sherlock");
				respService.setVendor("VI");
				*/

				String respstr = ispHelper.getQueryResponse(resp);
				logger.logDebug("Query response - " + respstr);

				unmarshaller = JAXBContext.newInstance(QueryResponse.class).createUnmarshaller();
				queryRes =(QueryResponse) unmarshaller.unmarshal(new StringReader(respstr));
			}

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
				
				responseCode = Integer.toString(Result);
				responseDesc = queryRes.getDesc();
				
				output.addActionParameter("MAC_EXIST", "TRUE");
				
			} else
			{
				responseCode = Integer.toString(Result);
				responseDesc = queryRes.getDesc();
				
				output.addActionParameter("MAC_EXIST", "FALSE");
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