/**
 * TODO Copyright Notice
 */
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_voice.add;

import com.oracle.activation.eric.mobile.x2_0.ConnectionHandler;
import com.oracle.activation.eric.mobile.x2_0.Helper;
import com.oracle.activation.eric.mobile.x2_0.MobConstants;
import com.oracle.activation.eric.mobile.x2_0.sub_mobile_voice.add.generated.*;

import java.util.HashMap;

import com.mslv.activation.jinterpreter.JProcessor;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;
import com.mslv.studio.activation.implementation.IExitType;

public class AddSub_mobile_voiceProcessor implements AddSub_mobile_voiceProcessorInterface {

	/*
	 * The logger to use for all log information generated by this processor.
	 */
	private ILogger logger = null;
	private String responseDesc = null;
	private JProcessor jproc=null;
	private String responseCode = null;


	public void execute(AddSub_mobile_voiceInput parms, AddSub_mobile_voiceOutput output, IConnectionHandler connection,
			IExitType exitType, ISystemParameters systemParameters) throws Exception {

		// TODO  Log intermediate steps of process execution. 
		logger.logDebug("execute() - Completed step A of execution.");
		try {

			logger.logDebug("AddSub_mobile_voiceProcessor: execute() - Completed step A of execution.");
			logger.logDebug("JPROC CSDL_CMD "+jproc.getParam(MobConstants.CSDL_CMD));

			ConnectionHandler connHdlr = (ConnectionHandler) connection;

			// Sample code to extract the parameters from the Input bean.

			String mcli = parms.getMCLI();
			String boic = parms.getBOIC();
			String enable_mms = parms.getENABLE_MMS();
			String imsi = parms.getIMSI();
			String bicro = parms.getBICRO();
			String cfnry = parms.getCFNRY();
			String cfs = parms.getCFS();
			String iccid = parms.getICCID();
			String baic = parms.getBAIC();
			String name = parms.getName();
			String msisdn = parms.getMSISDN();
			String baoc = parms.getBAOC();
			String email = parms.getEmail();
			String cfb = parms.getCFB();
			String roamingallowed = parms.getRoamingAllowed();
			String cfnrc = parms.getCFNRC();
			String payType = parms.getPayType();

			HashMap<String, String> hm = new HashMap<>();

			hm.put(MobConstants.SERVICE, "voice");
			hm.put(MobConstants.MSISDN, msisdn);
			hm.put(MobConstants.IMSI, imsi);
			hm.put(MobConstants.ICCID, iccid);
			hm.put(MobConstants.NAME, name);
			hm.put(MobConstants.PAYTYPE, payType);

			if(roamingallowed != null)
			{
				hm.put(MobConstants.ROAMING, roamingallowed);
			}
			
			if(email != null)
			{
				hm.put(MobConstants.EMAIL, email);
			}
			
			if(enable_mms != null)
			{
				hm.put(MobConstants.ENABLEMMS, enable_mms);
			}

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