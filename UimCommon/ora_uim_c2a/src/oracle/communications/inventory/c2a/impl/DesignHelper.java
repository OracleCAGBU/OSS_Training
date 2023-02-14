package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.c2a.impl.Parameter;

/**
 * This class returns the DesignManagerImpl Object.
 */
public class DesignHelper {
	static Log log = LogFactory.getLog();

	public static oracle.communications.inventory.c2a.DesignManager makeDesignManager() {
		return new oracle.communications.inventory.c2a.impl.DesignManagerImpl();
	}
	
	public static oracle.communications.inventory.c2a.LiveEntityCreator makeLiveEntityCreator() {
		return new oracle.communications.inventory.c2a.impl.LiveEntityCreatorImpl();
	}

	public static oracle.communications.inventory.c2a.BuilderManager makeBuilderManager() {
		return new oracle.communications.inventory.c2a.impl.BuilderManagerImpl();
	}
	
	public static ArrayList<Parameter> BiParamMapParser(String mappingFile) {

		// parse the xml file and get the dom object
		Document dom = parseXmlFile(mappingFile);
		// get each Parameter element and create a Params object
		ArrayList<Parameter> myParams = parseDocument(dom);

		return myParams;
	}

	public static String childSpecificationMapParser(String mappingFile) {
		Document dom = parseXmlFile(mappingFile);
		Element docEle = dom.getDocumentElement();
		String childSpecification = getChildSpecification(docEle);
		return childSpecification;
	}

	public static String childEntityTypeMapParser(String mappingFile) {
		Document dom = parseXmlFile(mappingFile);
		Element docEle = dom.getDocumentElement();
		String childEntityType = getChildEntityType(docEle);
		return childEntityType;
	}

	public static String parentSpecificationMapParser(String mappingFile) {
		Document dom = parseXmlFile(mappingFile);
		Element docEle = dom.getDocumentElement();
		String parentSpecification = getParentSpecification(docEle);
		return parentSpecification;
	}

	private static String getResourceName(String mappingFile) {
		int lastSlash = mappingFile.lastIndexOf("/");
		if (lastSlash >= 0 && lastSlash + 1 < mappingFile.length()) {
			return mappingFile.substring(lastSlash + 1);
		}
		return mappingFile;
	}

	private static Document parseXmlFile(String mappingFile) {

		Document dom = null;
		InputStream inputStream = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			ClassLoader classLoader = Thread.currentThread()
			        .getContextClassLoader();
			String resourceName = getResourceName(mappingFile);
			inputStream = classLoader
			        .getResourceAsStream(resourceName);
			if (inputStream == null) {
				log.error("", "unable to read metadata: " + mappingFile);
			}
			dom = db.parse(inputStream);
			// dom = db.parse(mappingFile);
		} catch (ParserConfigurationException e) {
			log.error("", e, "unable to parse metadata: " + mappingFile);
		} catch (SAXException e) {
			log.error("", e, "unable to parse metadata: " + mappingFile);
		} catch (IOException e) {
			log.error("", e, "unable to parse metadata: " + mappingFile);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("", "unable to read metadata: " + mappingFile);
				}
			}
		}
		return dom;
	}

	private static ArrayList<Parameter> parseDocument(Document dom) {
		ArrayList<Parameter> myParams = new ArrayList<Parameter>();
		if (dom == null)
			return myParams;
		// get the root elememt
		Element docEle = dom.getDocumentElement();
		// get a nodelist of <Parameter> elements
		NodeList nl = docEle.getElementsByTagName("Parameter");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the Parameter element
				Element el = (Element) nl.item(i);
				// get the Parameter object
				Parameter e = getParameter(el);
				// add it to list
				myParams.add(e);
			}
		}
		return myParams;
	}

	private static Parameter getParameter(Element parameterEl) {
		// for each <Parameter> element get text of
		// name ,parentBiParameterName, defaultValue
		String name = getTextValue(parameterEl, "Name");
		String defaultValue = getTextValue(parameterEl, "DefaultValue");
		String parentBiParameterName = getTextValue(parameterEl,
		        "ParentBiParameterName");
		String parentParameterName = getTextValue(parameterEl,
		        "ParentParameterName");
		String parentParameterConfigItemName = getTextValue(parameterEl,
		        "ParentParameterConfigItemName");
		// Create a new Parameter with the value read from the xml nodes
		Parameter e = new Parameter(name, parentBiParameterName, defaultValue,
		        parentParameterName, parentParameterConfigItemName);
		return e;
	}

	private static String getChildSpecification(Element parameterEl) {
		String childSpecification = getTextValue(parameterEl,
		        "ChildSpecification");
		return childSpecification;
	}

	private static String getChildEntityType(Element parameterEl) {
		String childEntityType = getTextValue(parameterEl, "ChildEntityType");
		return childEntityType;
	}

	private static String getParentSpecification(Element parameterEl) {
		String parentSpecification = getTextValue(parameterEl,
		        "ParentSpecification");
		return parentSpecification;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text
	 * content i.e for <employee><name>John</name></employee> xml snippet if the
	 * Element points to employee node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		return textVal;
	}
}
