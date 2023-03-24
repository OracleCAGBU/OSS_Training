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
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_voice.add.generated;

import java.util.Properties;


/**
 * 
 * 
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
public class AddSub_mobile_voiceInput {

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
	private static final String BOIC = "BOIC";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String boic;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String ENABLE_MMS = "ENABLE_MMS";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String enable_mms;

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
	private static final String BICRO = "BICRO";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String bicro;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String CFNRY = "CFNRY";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String cfnry;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String CFS = "CFS";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String cfs;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String ICCID = "ICCID";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String iccid;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String BAIC = "BAIC";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String baic;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String NAME = "Name";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String name;

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
	private static final String PAYTYPE = "PayType";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String paytype;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String BAOC = "BAOC";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String baoc;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String EMAIL = "Email";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String email;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String CFB = "CFB";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String cfb;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String ROAMINGALLOWED = "RoamingAllowed";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String roamingallowed;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String CFNRC = "CFNRC";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private String cfnrc;

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddSub_mobile_voiceInput() {
		super();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddSub_mobile_voiceInput(Properties parms) {
		this(parms, "");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public AddSub_mobile_voiceInput(Properties parms, String prefix) {
		super();

		mcli = parms.getProperty(prefix + MCLI);

		boic = parms.getProperty(prefix + BOIC);

		enable_mms = parms.getProperty(prefix + ENABLE_MMS);

		imsi = parms.getProperty(prefix + IMSI);

		bicro = parms.getProperty(prefix + BICRO);

		cfnry = parms.getProperty(prefix + CFNRY);

		cfs = parms.getProperty(prefix + CFS);

		iccid = parms.getProperty(prefix + ICCID);

		baic = parms.getProperty(prefix + BAIC);

		name = parms.getProperty(prefix + NAME);

		msisdn = parms.getProperty(prefix + MSISDN);

		paytype = parms.getProperty(prefix + PAYTYPE);

		baoc = parms.getProperty(prefix + BAOC);

		email = parms.getProperty(prefix + EMAIL);

		cfb = parms.getProperty(prefix + CFB);

		roamingallowed = parms.getProperty(prefix + ROAMINGALLOWED);

		cfnrc = parms.getProperty(prefix + CFNRC);

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
	public boolean hasBOIC() {
		return (boic != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getBOIC() {
		return boic;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setBOIC(String boic) {
		this.boic = boic;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasENABLE_MMS() {
		return (enable_mms != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getENABLE_MMS() {
		return enable_mms;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setENABLE_MMS(String enable_mms) {
		this.enable_mms = enable_mms;
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
	public boolean hasBICRO() {
		return (bicro != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getBICRO() {
		return bicro;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setBICRO(String bicro) {
		this.bicro = bicro;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasCFNRY() {
		return (cfnry != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getCFNRY() {
		return cfnry;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setCFNRY(String cfnry) {
		this.cfnry = cfnry;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasCFS() {
		return (cfs != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getCFS() {
		return cfs;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setCFS(String cfs) {
		this.cfs = cfs;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasICCID() {
		return (iccid != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getICCID() {
		return iccid;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setICCID(String iccid) {
		this.iccid = iccid;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasBAIC() {
		return (baic != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getBAIC() {
		return baic;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setBAIC(String baic) {
		this.baic = baic;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasName() {
		return (name != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasPayType() {
		return (paytype != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getPayType() {
		return paytype;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setPayType(String paytype) {
		this.paytype = paytype;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasBAOC() {
		return (baoc != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getBAOC() {
		return baoc;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setBAOC(String baoc) {
		this.baoc = baoc;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasEmail() {
		return (email != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasCFB() {
		return (cfb != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getCFB() {
		return cfb;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setCFB(String cfb) {
		this.cfb = cfb;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasRoamingAllowed() {
		return (roamingallowed != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getRoamingAllowed() {
		return roamingallowed;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setRoamingAllowed(String roamingallowed) {
		this.roamingallowed = roamingallowed;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public boolean hasCFNRC() {
		return (cfnrc != null);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String getCFNRC() {
		return cfnrc;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void setCFNRC(String cfnrc) {
		this.cfnrc = cfnrc;
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

		isEmpty = isEmpty && (boic == null);

		isEmpty = isEmpty && (enable_mms == null);

		isEmpty = isEmpty && (imsi == null);

		isEmpty = isEmpty && (bicro == null);

		isEmpty = isEmpty && (cfnry == null);

		isEmpty = isEmpty && (cfs == null);

		isEmpty = isEmpty && (iccid == null);

		isEmpty = isEmpty && (baic == null);

		isEmpty = isEmpty && (name == null);

		isEmpty = isEmpty && (msisdn == null);

		isEmpty = isEmpty && (paytype == null);

		isEmpty = isEmpty && (baoc == null);

		isEmpty = isEmpty && (email == null);

		isEmpty = isEmpty && (cfb == null);

		isEmpty = isEmpty && (roamingallowed == null);

		isEmpty = isEmpty && (cfnrc == null);

		return isEmpty;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Parameters:\n");

		buff.append("mcli = " + getMCLI() + "\n");

		buff.append("boic = " + getBOIC() + "\n");

		buff.append("enable_mms = " + getENABLE_MMS() + "\n");

		buff.append("imsi = " + getIMSI() + "\n");

		buff.append("bicro = " + getBICRO() + "\n");

		buff.append("cfnry = " + getCFNRY() + "\n");

		buff.append("cfs = " + getCFS() + "\n");

		buff.append("iccid = " + getICCID() + "\n");

		buff.append("baic = " + getBAIC() + "\n");

		buff.append("name = " + getName() + "\n");

		buff.append("msisdn = " + getMSISDN() + "\n");

		buff.append("paytype = " + getPayType() + "\n");

		buff.append("baoc = " + getBAOC() + "\n");

		buff.append("email = " + getEmail() + "\n");

		buff.append("cfb = " + getCFB() + "\n");

		buff.append("roamingallowed = " + getRoamingAllowed() + "\n");

		buff.append("cfnrc = " + getCFNRC() + "\n");

		return buff.toString();
	}

}

