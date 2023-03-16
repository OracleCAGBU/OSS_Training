package com.orcl.activation.cisco.isp.x1_0.helper;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.orcl.activation.cisco.isp.x1_0.request.ModifyRequest;
import com.orcl.activation.cisco.isp.x1_0.request.ProvisionRequest;
import com.orcl.activation.cisco.isp.x1_0.request.QueryRequest;
import com.orcl.activation.cisco.isp.x1_0.request.TerminateRequest;
import com.orcl.activation.cisco.isp.x1_0.response.ModifyResponse;
import com.orcl.activation.cisco.isp.x1_0.response.ProvisionResponse;
import com.orcl.activation.cisco.isp.x1_0.response.QueryResponse;
import com.orcl.activation.cisco.isp.x1_0.response.TerminateResponse;



public class ISPHelper {
	/**
	 * Mandatory parameters check
	 * 
	 * @param val
	 * @param param
	 * @throws MandatoryAttributeNotSetException
	 */
	public void isNotNull(String val, String param) throws MandatoryAttributeNotSetException {
		if (val != null && !val.trim().equals("")) {
			// do nothing
		} else
			throw new MandatoryAttributeNotSetException("Cannot Find Mandatory Parameter " + param);

	}


	/**
	 * UnMarshal provision response to read response code
	 * 
	 * @param ProvisionResponse
	 * @return String
	 * @throws JAXBException
	 */
	public String getQueryResponse(QueryResponse responseStr) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(responseStr.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(responseStr, sw);

		return sw.toString();

	}

	public String getProvisionResponse(ProvisionResponse responseStr) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(responseStr.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(responseStr, sw);

		return sw.toString();

	}

	public String getModifyResponse(ModifyResponse responseStr) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(responseStr.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(responseStr, sw);

		return sw.toString();

	}

	public String getTerminateResponse(TerminateResponse responseStr) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(responseStr.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(responseStr, sw);

		return sw.toString();

	}

	/**
	 * This method converts object to string
	 * 
	 * @param provReq
	 * @return
	 * @throws JAXBException
	 */
	public String getRequestString(QueryRequest queryReq) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(queryReq.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(queryReq, sw);

		return sw.toString();

	}

	public String getRequestString(ProvisionRequest provReq) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(provReq.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(provReq, sw);

		return sw.toString();

	}

	public String getRequestString(ModifyRequest modReq) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(modReq.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(modReq, sw);

		return sw.toString();

	}

	public String getRequestString(TerminateRequest terReq) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(terReq.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(terReq, sw);

		return sw.toString();

	}

	/**
	 * This method checks for exception
	 * 
	 * @param Exception
	 * @return String
	 * 
	 */
	public String getExceptionResponseCode(Exception ex){

		String responseCode = null;

		if(ex instanceof MandatoryAttributeNotSetException)
			responseCode = ISPConstants.PARAM_MISSING;
		else if(ex instanceof UnknownHostException)
			responseCode = ISPConstants.UNKNOWN_HOST_EXCEPTION_CODE;
		else 
			responseCode = ISPConstants.PROV_EXCEPTION_CODE;

		return responseCode;
	}	
}
