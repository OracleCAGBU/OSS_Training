/*
 * WARNING: DO NOT MODIFY THE CONTENTS OF THIS FILE.
 *
 *    This file contains generated code.  Any changes to the file may be overwritten.
 * All process logic should be implemented in the related Processor class and 
 * non-generated supporting classes.
 *
 *    Do not place this file under source control.  Doing so may prevent proper generation
 * and synchronization of this file with the supporting model.
 *
 *=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 */
package com.orcl.activation.cisco.isp.x1_0.fiber_cpe.retrieve.generated;

import com.orcl.activation.cisco.isp.x1_0.fiber_cpe.retrieve.RetrieveFiberCpeProcessor;


import java.util.Properties;

import com.mslv.activation.jinterpreter.NEConnection;
import com.mslv.studio.activation.implementation.BaseProxy;
import com.mslv.studio.activation.implementation.DiagnosisLogger;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.ILogger;
import com.mslv.studio.activation.implementation.IExitType;


/**
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public final class RetrieveFiberCpeProxy extends BaseProxy {

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String DIAG_TOKEN = "RetrieveFiberCpeProxy";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private RetrieveFiberCpeInput bean;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private RetrieveFiberCpeProcessorInterface processor;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private ILogger logger;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public RetrieveFiberCpeProxy() {
		super();

		logger = new DiagnosisLogger();

		logger.logDebug(DIAG_TOKEN + ".Proxy");

		processor = new RetrieveFiberCpeProcessor();

		// Set the logger context prior to use of the processor.
		logger.setContext(processor);

		processor.init(this, logger);

		logger.logDebug(DIAG_TOKEN + ".Proxy constructed Processor class.");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	/**
	  *In Studio generated code, all the parameters are set/get with String format into/from Java Properties,which takes name-value format in String always.Studio just generates a skeleton of Java programs.It is recommended to convert the XML document in the end,just before the Java program really needs to use the XML parameter.
	  */
	public void execute() {

		ExitType exitType = new ExitType(this);

		try {

			logger.logDebug("execute() - Start");

			exitType.setType(IExitType.SUCCEED, "");

			Properties params = this.getAllActionParams();

			ISystemParameters systemParameters = new SystemParameters(params, this, getSecurity(), logger);

			bean = new RetrieveFiberCpeInput(params);
			RetrieveFiberCpeOutput output = new RetrieveFiberCpeOutput();

			// Dump the parameters for Diagnosis
			logger.logDebug("execute() - Parameters passed:\n" + bean.toString());

			NEConnection connection = getConnection();

			if (connection instanceof IConnectionHandler) {
				processor.init(this, logger);

				processor.execute(bean, output, (IConnectionHandler) connection, exitType, systemParameters);

				String description = exitType.getDescription();

				if (description != null) {
					logger.logDebug("Exit Type: " + getType() + " Description: " + description);
				} else {
					logger.logDebug("Exit Type: " + getType());
				}

				populateReturnParameters(output);
			} else {
				throw new Exception(
						"Unexpected connection type.  Expection connection implementing IConnectionHandler.");
			}

			logger.logDebug("execute() - End");

		} catch (Exception ex) {
			logger.logDebug("execute() - Exception: " + ex.getMessage());
			exitType.setTypeByMatch(ex.getMessage(), ex.getMessage(), IExitType.FAIL);
		}
	}

	/**
	 * The getDescription method returns the error text.
	 *
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getDescription() {

		return super.getDescription();

	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String setTypeByMatch(String source) {
		String exitType = this.matchNEResponse(source);

		return exitType;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private class ExitType implements IExitType {

		private BaseProxy proxy = null;

		public ExitType(BaseProxy processor) {
			this.proxy = processor;
		}

		public String getDescription() {
			return proxy.getErrorText();
		}

		public String getType() {
			return proxy.getType();
		}

		public void setDescription(String description) {
			proxy.setDescription(description);
		}

		public void setType(String exitType, String description) {
			proxy.setType(exitType, description);
		}

		public String setTypeByMatch(String source, String description, String exitTypeDefault) {
			return proxy.setTypeByMatch(source, description, exitTypeDefault);
		}

		public String setTypeByMatch(String source) {
			return proxy.setTypeByMatch(source);
		}

	}

}
