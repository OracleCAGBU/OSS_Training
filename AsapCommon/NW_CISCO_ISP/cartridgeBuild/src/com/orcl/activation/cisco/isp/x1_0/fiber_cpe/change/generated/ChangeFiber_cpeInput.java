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
package com.orcl.activation.cisco.isp.x1_0.fiber_cpe.change.generated;

import java.util.Properties;


/**
 * 
 * 
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public class ChangeFiber_cpeInput {

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
	private static final String DOWNLOADSPEED = "DownloadSpeed";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String downloadspeed;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String SERIALNUMBER = "SerialNumber";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String serialnumber;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String UPLOADSPEED = "UploadSpeed";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String uploadspeed;

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
	private static final String OLD_SERIALNUMBER = "Old_SerialNumber";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String old_serialnumber;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String STATUS = "Status";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String status;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public ChangeFiber_cpeInput() {
		super();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public ChangeFiber_cpeInput(Properties parms) {
		this(parms, "");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public ChangeFiber_cpeInput(Properties parms, String prefix) {
		super();

		mcli = parms.getProperty(prefix + MCLI);

		downloadspeed = parms.getProperty(prefix + DOWNLOADSPEED);

		serialnumber = parms.getProperty(prefix + SERIALNUMBER);

		uploadspeed = parms.getProperty(prefix + UPLOADSPEED);

		macaddress = parms.getProperty(prefix + MACADDRESS);

		old_serialnumber = parms.getProperty(prefix + OLD_SERIALNUMBER);

		status = parms.getProperty(prefix + STATUS);

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
	public boolean hasDownloadSpeed() {
		return (downloadspeed != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getDownloadSpeed() {
		return downloadspeed;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setDownloadSpeed(String downloadspeed) {
		this.downloadspeed = downloadspeed;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasSerialNumber() {
		return (serialnumber != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getSerialNumber() {
		return serialnumber;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setSerialNumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasUploadSpeed() {
		return (uploadspeed != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getUploadSpeed() {
		return uploadspeed;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setUploadSpeed(String uploadspeed) {
		this.uploadspeed = uploadspeed;
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
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasOld_SerialNumber() {
		return (old_serialnumber != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getOld_SerialNumber() {
		return old_serialnumber;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setOld_SerialNumber(String old_serialnumber) {
		this.old_serialnumber = old_serialnumber;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasStatus() {
		return (status != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setStatus(String status) {
		this.status = status;
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

		isEmpty = isEmpty && (downloadspeed == null);

		isEmpty = isEmpty && (serialnumber == null);

		isEmpty = isEmpty && (uploadspeed == null);

		isEmpty = isEmpty && (macaddress == null);

		isEmpty = isEmpty && (old_serialnumber == null);

		isEmpty = isEmpty && (status == null);

		return isEmpty;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Parameters:\n");

		buff.append("mcli = " + getMCLI() + "\n");

		buff.append("downloadspeed = " + getDownloadSpeed() + "\n");

		buff.append("serialnumber = " + getSerialNumber() + "\n");

		buff.append("uploadspeed = " + getUploadSpeed() + "\n");

		buff.append("macaddress = " + getMACAddress() + "\n");

		buff.append("old_serialnumber = " + getOld_SerialNumber() + "\n");

		buff.append("status = " + getStatus() + "\n");

		return buff.toString();
	}

}

