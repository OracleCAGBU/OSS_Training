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
package com.oracle.activation.cisco.isp.x1_0.fiber_cpe.add.generated;

import java.util.Properties;


/**
 * 
 * 
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public class AddFiber_cpeInput {

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
	private static final String IPADDRESS = "IPAddress";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String ipaddress;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String PASSWORD = "Password";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String password;

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
	private static final String VENDOR = "Vendor";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String vendor;

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
	private static final String MODEL = "Model";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String model;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String USERNAME = "Username";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String username;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String CPETYPE = "CPEType";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String cpetype;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddFiber_cpeInput() {
		super();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddFiber_cpeInput(Properties parms) {
		this(parms, "");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddFiber_cpeInput(Properties parms, String prefix) {
		super();

		mcli = parms.getProperty(prefix + MCLI);

		downloadspeed = parms.getProperty(prefix + DOWNLOADSPEED);

		ipaddress = parms.getProperty(prefix + IPADDRESS);

		password = parms.getProperty(prefix + PASSWORD);

		serialnumber = parms.getProperty(prefix + SERIALNUMBER);

		uploadspeed = parms.getProperty(prefix + UPLOADSPEED);

		vendor = parms.getProperty(prefix + VENDOR);

		macaddress = parms.getProperty(prefix + MACADDRESS);

		model = parms.getProperty(prefix + MODEL);

		username = parms.getProperty(prefix + USERNAME);

		cpetype = parms.getProperty(prefix + CPETYPE);

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
	public boolean hasIPAddress() {
		return (ipaddress != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getIPAddress() {
		return ipaddress;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setIPAddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasPassword() {
		return (password != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setPassword(String password) {
		this.password = password;
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
	public boolean hasVendor() {
		return (vendor != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
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
	public boolean hasModel() {
		return (model != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasUsername() {
		return (username != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasCPEType() {
		return (cpetype != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getCPEType() {
		return cpetype;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setCPEType(String cpetype) {
		this.cpetype = cpetype;
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

		isEmpty = isEmpty && (ipaddress == null);

		isEmpty = isEmpty && (password == null);

		isEmpty = isEmpty && (serialnumber == null);

		isEmpty = isEmpty && (uploadspeed == null);

		isEmpty = isEmpty && (vendor == null);

		isEmpty = isEmpty && (macaddress == null);

		isEmpty = isEmpty && (model == null);

		isEmpty = isEmpty && (username == null);

		isEmpty = isEmpty && (cpetype == null);

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

		buff.append("ipaddress = " + getIPAddress() + "\n");

		buff.append("password = " + getPassword() + "\n");

		buff.append("serialnumber = " + getSerialNumber() + "\n");

		buff.append("uploadspeed = " + getUploadSpeed() + "\n");

		buff.append("vendor = " + getVendor() + "\n");

		buff.append("macaddress = " + getMACAddress() + "\n");

		buff.append("model = " + getModel() + "\n");

		buff.append("username = " + getUsername() + "\n");

		buff.append("cpetype = " + getCPEType() + "\n");

		return buff.toString();
	}

}

