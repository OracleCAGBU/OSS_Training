package com.oracle.training.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This example shows how to establish a connection and send messages to the JMS
 * queue. The classes in this package operate on the same JMS queue. Run the
 * classes together to witness messages being sent and received, and to browse
 * the queue for messages. The class is used to send messages to the queue.
 *
 * @author Copyright (c) 1999-2005 by BEA Systems, Inc. All Rights Reserved.
 */
public class QueueSend {
	// Defines the JNDI context factory.
	public final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";

	// Defines the JMS context factory.
	public final static String JMS_FACTORY = "oracle/communications/ordermanagement/osm/ExternalClientConnectionFactory";

	// Defines the queue.
	public final static String QUEUE = "System.DEV.OSM.ASAP.7-2;Comp.XVTEventTopic";

	private QueueConnectionFactory qconFactory;
	private QueueConnection qcon;
	private QueueSession qsession;
	private QueueSender qsender;
	private Queue queue;
	private TextMessage msg;

	/**
	 * Creates all the necessary objects for sending messages to a JMS queue.
	 *
	 * @param ctx       JNDI initial context
	 * @param queueName name of queue
	 * @exception NamingException if operation cannot be performed
	 * @exception JMSException    if JMS fails to initialize due to internal error
	 */
	public void init(Context ctx, String queueName) throws NamingException, JMSException {
		qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
		qcon = qconFactory.createQueueConnection();
		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) ctx.lookup(queueName);
		qsender = qsession.createSender(queue);
		msg = qsession.createTextMessage();
		qcon.start();
	}

	/**
	 * Sends a message to a JMS queue.
	 *
	 * @param message message to be sent
	 * @exception JMSException if JMS fails to send message due to internal error
	 */
	public void send(String message, String primaryKey) throws JMSException {
		//primaryKey="23:302";
		msg.setText(message);
		msg.setJMSCorrelationID(primaryKey);
		
		//CreateOrderByValueResponse
		
		//msg.setStringProperty("OSS_EVENT_TYPE", "sa:createOrderByValueResponse");
		
		//OrderCompleteEvent
		
		msg.setStringProperty("OSS_EVENT_TYPE", "javax.oss.order.OrderStateChangeEvent");
		msg.setStringProperty("OSS_ORDER_CURRENT_STATE", "closed.completed");
		msg.setStringProperty("OSS_ORDER_PRIMARY_KEY", primaryKey);
		
		//GetOrderByKeyResponse_orderCompleteEvent
		
		//msg.setStringProperty("OSS_EVENT_TYPE", "sa:getOrderByKeyResponse");
		//msg.setStringProperty("EVENT_TYPE_TRIGGERED", "orderCompleteEvent");
		
		//msg.setStringProperty("OSS_ORDER_TYPE", "javax.oss.order.CreateOrderValue");
		//msg.setStringProperty("OSS_API_CLIENT_ID", "ActivateBroadband-Training_OSM_TOM_Activate_Broadband-1.0.0.0.0");
		//msg.setStringProperty("EVENT_TYPE_TRIGGERED", "orderCompleteEvent");
		qsender.send(msg);
	}

	public String readFile(String path) throws IOException {
		// charset for encoding
		Charset encoding = Charset.defaultCharset();

		// reading all lines of file as List of strings
		List<String> lines = Files.readAllLines(Paths.get(path), encoding);

		// converting List<String> to palin string using java 8 api.
		String string = lines.stream().collect(Collectors.joining("\n"));
		return string;
	}

	public String getPrimaryKey(String filePath) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(filePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(file);
		NodeList nodeList = dom.getElementsByTagName("*");
		String correlationId = "";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().contains("primaryKey")) {
				// do something with the current element
				correlationId = node.getTextContent();
			}
		}
		return correlationId;
	}

	/**
	 * Closes JMS objects.
	 * 
	 * @exception JMSException if JMS fails to close objects due to internal error
	 */
	public void close() throws JMSException {
		qsender.close();
		qsession.close();
		qcon.close();
	}

	/**
	 * main() method.
	 *
	 * @param args WebLogic Server URL
	 * @exception Exception if operation fails
	 */
	public static void main(String[] args) throws Exception {
		String url = "t3://192.168.56.101:7001";
		String username = "weblogic";
		String password = "admin123";
		//String filePath = "C:/Debapriya/Oracle/Projects/OSS Training Solution/Integration Testing/Broadband/Create/Request and Response Payloads/CreateOrderByValueResponse.xml";
		String filePath = "C:/Debapriya/Oracle/Projects/OSS Training Solution/Integration Testing/Broadband/Create/Request and Response Payloads/OrderCompletionEvent.xml";
		//String filePath = "C:/Debapriya/Oracle/Projects/OSS Training Solution/Integration Testing/Broadband/Create/Request and Response Payloads/GetOrderByKeyResponse.xml";
		InitialContext ic = getInitialContext(url, username, password);
		QueueSend qs = new QueueSend();
		qs.init(ic, QUEUE);
		readAndSend(qs, filePath);
		qs.close();
	}

	private static void readAndSend(QueueSend qs, String filePath)
			throws IOException, JMSException, ParserConfigurationException, SAXException {
		String primaryKey = qs.getPrimaryKey(filePath);
		String text = qs.readFile(filePath);
		if (text != null && text.trim().length() != 0) {

			System.out.println("Primary Key : " + primaryKey);
			System.out.println("JMS Message Sent: " + text + "\n");
			qs.send(text, primaryKey);
		}

	}

	private static InitialContext getInitialContext(String url, String username, String password)
			throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, username);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialContext(env);
	}
}