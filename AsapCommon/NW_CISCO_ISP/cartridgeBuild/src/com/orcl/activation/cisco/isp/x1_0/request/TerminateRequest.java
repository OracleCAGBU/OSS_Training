//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.20 at 07:39:55 PM IST 
//


package com.orcl.activation.cisco.isp.x1_0.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MACAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credential" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Authentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "macAddress",
    "action",
    "credential"
})
@XmlRootElement(name = "TerminateRequest")
public class TerminateRequest {

    @XmlElement(name = "MACAddress")
    protected String macAddress;
    @XmlElement(name = "Action")
    protected String action;
    @XmlElement(name = "Credential")
    protected TerminateRequest.Credential credential;

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMACAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMACAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the credential property.
     * 
     * @return
     *     possible object is
     *     {@link TerminateRequest.Credential }
     *     
     */
    public TerminateRequest.Credential getCredential() {
        return credential;
    }

    /**
     * Sets the value of the credential property.
     * 
     * @param value
     *     allowed object is
     *     {@link TerminateRequest.Credential }
     *     
     */
    public void setCredential(TerminateRequest.Credential value) {
        this.credential = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Authentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "user",
        "authentication"
    })
    public static class Credential {

        @XmlElement(name = "User", required = true)
        protected String user;
        @XmlElement(name = "Authentication")
        protected String authentication;

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        }

        /**
         * Gets the value of the authentication property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthentication() {
            return authentication;
        }

        /**
         * Sets the value of the authentication property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthentication(String value) {
            this.authentication = value;
        }

    }

}
