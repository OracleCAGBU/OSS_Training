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
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.res.generated;

import com.mslv.studio.activation.implementation.IActionProcessor;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.IExitType;


/**
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public interface ResSub_mobile_dataProcessorInterface extends IActionProcessor {

	/*
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public static final String VENDOR = "ERIC";
	public static final String TECHNOLOGY = "MOBILE";
	public static final String SOFTWARE_LOAD = "2-0";
	public static final String ACTION = "RES";
	public static final String ENTITY = "SUB_MOBILE_DATA";

	public static final String PARAMETER_PREFIX = "RES_SUB_MOBILE_DATA_";

	public static final String PARAMETER_ERROR = PARAMETER_PREFIX + "ERROR";
	public static final String PARAMETER_VENDOR = PARAMETER_PREFIX + "VENDOR";
	public static final String PARAMETER_TECHNOLOGY = PARAMETER_PREFIX + "TECHNOLOGY";
	public static final String PARAMETER_SOFTWARE_LOAD = PARAMETER_PREFIX + "SOFTWARE_LOAD";
	public static final String PARAMETER_ACTION = PARAMETER_PREFIX + "ACTION";
	public static final String PARAMETER_ENTITY = PARAMETER_PREFIX + "ENTITY";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void execute(ResSub_mobile_dataInput bean, ResSub_mobile_dataOutput output, IConnectionHandler connection,
			IExitType exitType, ISystemParameters systemParameters) throws Exception;

}

