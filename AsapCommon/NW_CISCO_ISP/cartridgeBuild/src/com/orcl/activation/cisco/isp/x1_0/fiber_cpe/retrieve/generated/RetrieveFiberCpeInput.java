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

import java.util.Properties;


/**
 * 
 * 
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public class RetrieveFiberCpeInput {

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
	private static final String MACADDRESS = "MACAddress";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String macaddress;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public RetrieveFiberCpeInput() {
		super();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public RetrieveFiberCpeInput(Properties parms) {
		this(parms, "");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public RetrieveFiberCpeInput(Properties parms, String prefix) {
		super();

		mcli = parms.getProperty(prefix + MCLI);

		macaddress = parms.getProperty(prefix + MACADDRESS);

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
	public boolean hasMACAddress() {
		return (macaddress != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getMACAddress() {
		return macaddress;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setMACAddress(String macaddress) {
		this.macaddress = macaddress;
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

		isEmpty = isEmpty && (macaddress == null);

		return isEmpty;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Parameters:\n");

		buff.append("mcli = " + getMCLI() + "\n");

		buff.append("macaddress = " + getMACAddress() + "\n");

		return buff.toString();
	}

}

