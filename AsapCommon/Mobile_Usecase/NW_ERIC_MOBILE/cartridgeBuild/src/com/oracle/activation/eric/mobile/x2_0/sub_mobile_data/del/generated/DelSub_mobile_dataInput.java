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
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.del.generated;

import java.util.Properties;


/**
 * 
 * 
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public class DelSub_mobile_dataInput {

	// Repeat for each Parameter
	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String MCLI = "MCLI";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String mcli;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String IMSI = "IMSI";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String imsi;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String MSISDN = "MSISDN";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String msisdn;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public DelSub_mobile_dataInput() {
		super();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public DelSub_mobile_dataInput(Properties parms) {
		this(parms, "");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public DelSub_mobile_dataInput(Properties parms, String prefix) {
		super();

		mcli = parms.getProperty(prefix + MCLI);

		imsi = parms.getProperty(prefix + IMSI);

		msisdn = parms.getProperty(prefix + MSISDN);

	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasMCLI() {
		return (mcli != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getMCLI() {
		return mcli;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setMCLI(String mcli) {
		this.mcli = mcli;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasIMSI() {
		return (imsi != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getIMSI() {
		return imsi;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setIMSI(String imsi) {
		this.imsi = imsi;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasMSISDN() {
		return (msisdn != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getMSISDN() {
		return msisdn;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setMSISDN(String msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * This method is used to determine if the bean is populated.  The method returns true if
	 * all properties of the bean are not present, or if the properties of the bean cannot be determined.
	 *
	 * Compound entries without named members cannot be validated for presence.  As a result, this 
	 * method assumes they are not present and factors it into the overall result.
	 *
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean isEmpty() {
		boolean isEmpty = true;

		isEmpty = isEmpty && (mcli == null);

		isEmpty = isEmpty && (imsi == null);

		isEmpty = isEmpty && (msisdn == null);

		return isEmpty;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Parameters:\n");

		buff.append("mcli = " + getMCLI() + "\n");

		buff.append("imsi = " + getIMSI() + "\n");

		buff.append("msisdn = " + getMSISDN() + "\n");

		return buff.toString();
	}

}

