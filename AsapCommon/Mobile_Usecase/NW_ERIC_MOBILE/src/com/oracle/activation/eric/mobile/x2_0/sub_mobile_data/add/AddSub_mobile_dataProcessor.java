/**
 * TODO Copyright Notice
 */
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.add;

import com.oracle.activation.eric.mobile.x2_0.ConnectionHandler;
import com.oracle.activation.eric.mobile.x2_0.Helper;
import com.oracle.activation.eric.mobile.x2_0.MobConstants;
import com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.add.generated.*;

import java.util.HashMap;

import com.mslv.activation.jinterpreter.JProcessor;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;
import com.mslv.studio.activation.implementation.IExitType;


public class AddSub_mobile_dataProcessor implements AddSub_mobile_dataProcessorInterface {

	/*
	 * The logger to use for all log information generated by this processor.
	 */
	private ILogger logger = null;
	private String responseDesc = null;
	private JProcessor jproc=null;
	private String responseCode = null;

	public void execute(AddSub_mobile_dataInput parms, AddSub_mobile_dataOutput output, IConnectionHandler connection,
			IExitType exitType, ISystemParameters systemParameters) throws Exception {

		// TODO  Log intermediate steps of process execution. 
		logger.logDebug("execute() - Completed step A of execution.");

		try {

			logger.logDebug("AddSub_mobile_dataProcessor: execute() - Completed step A of execution.");
			logger.logDebug("JPROC CSDL_CMD "+jproc.getParam(MobConstants.CSDL_CMD));

			ConnectionHandler connHdlr = (ConnectionHandler) connection;

			// Read input parameters
			String mcli = parms.getMCLI();
			String iccid = parms.getICCID();
			String serviceplan = parms.getServicePlan();
			String name = parms.getName();
			String imsi = parms.getIMSI();
			String msisdn = parms.getMSISDN();
			String email = parms.getEmail();
			String roamingallowed = parms.getRoamingAllowed();

			HashMap<String, String> hm = new HashMap<>();

			hm.put(MobConstants.SERVICE, "data");
			hm.put(MobConstants.MSISDN, msisdn);
			hm.put(MobConstants.IMSI, imsi);
			hm.put(MobConstants.ICCID, iccid);
			hm.put(MobConstants.NAME, name);

			if(roamingallowed != null)
			{
				hm.put(MobConstants.ROAMING, roamingallowed);

			}
			if(email != null)
			{
				hm.put(MobConstants.EMAIL, email);
			}

			hm.put(MobConstants.SERVICEPLAN, serviceplan);	

			// Construct Provision Request
			logger.logDebug("ISLOOPBACK_SYSTEMPARAMETERS "+systemParameters.isLoopback());
			if (!systemParameters.isLoopback()) {
				responseCode = connHdlr.sendRequest(hm,MobConstants.ADD,true);
			}
			else
			{
				responseCode = connHdlr.sendRequest(hm,MobConstants.ADD, false);
			}
		}

		catch(Exception ex)
		{
			responseCode = Helper.getExceptionResponseCode(ex);
			responseDesc = ex.getMessage()+" in "+jproc.getParam(MobConstants.CSDL_CMD);
			logger.logError("Exception occurred: "+ex.toString());
		}


		finally
		{

			logger.logDebug("responseMessage "+responseDesc);

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

		if (processor instanceof JProcessor) {
			jproc = (JProcessor) processor;
		}	
	}

}