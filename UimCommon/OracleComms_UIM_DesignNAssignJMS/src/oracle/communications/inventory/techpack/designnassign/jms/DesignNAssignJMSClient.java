package oracle.communications.inventory.techpack.designnassign.jms;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionState;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.platform.persistence.PersistenceHelper;
import weblogic.jms.extensions.WLMessageProducer;

public class DesignNAssignJMSClient {
	
	private static Log log = LogFactory.getLog(DesignNAssignJMSClient.class);
	public final static String resume_design = "RESUME_PENDING_DESIGN";
	public final static String process_new = "PROCESS_NEXT_DESIGN";
	
	private ServiceConfigurationVersion config;
	private BusinessInteractionItemType orderItem;
	private static String manualDesignComponent;

	QueueConnection queueConnection = null;
	QueueSession queueSession = null;
	Queue designNAssignQueue;
	ObjectMessage message  = null;
	
	/**
	 * Log the debug data values in the debug log if the log is enabled. 
	 * 
	 * @param objects
	 */
	
	private void debug(Object ...objects) {
		try {
			if (log.isDebugEnabled()) {
				StringBuilder sb=new StringBuilder();
				for(Object obj:objects) {
					sb.append(obj);
				}
				log.debug("",sb.toString());
			}
		} catch (Exception e) {
			log.debug("", "Error Message :"+e.getMessage());
		}
	}
	
	public DesignNAssignJMSClient()
    {
        debug("Constructor DesignNAssignJMSClient called");
    }
	
	
	public static String getManualDesignComponent() {
		return manualDesignComponent;
	}

	public static void setManualDesignComponent(String manualDesignComponent) {
		DesignNAssignJMSClient.manualDesignComponent = manualDesignComponent;
	}
	
    public void initializeDesignNAssignJMSClient() throws JMSException {
        debug("#### initializeDesignNAssignJMSClient START");
        
        String                  queueName = "inventoryWSQueueAlternate";
        String                  queueConnectionFactoryName = "inventoryWSQueueAlternateCF";
        Context                 jndiContext = null;
        QueueConnectionFactory  queueConnectionFactory = null;
        
        /*
         * Set the Context Object.
         * Lookup the Queue Connection Factory.
         * Lookup the JMS Destination.
         */        
        try {
            jndiContext = new InitialContext();
            queueConnectionFactory = (QueueConnectionFactory)
                jndiContext.lookup(queueConnectionFactoryName);
            designNAssignQueue = (Queue) jndiContext.lookup(queueName);
            debug(log,"Queue name : "+designNAssignQueue.getQueueName());
            queueConnection = queueConnectionFactory.createQueueConnection();
            debug(log,"queueConnection : " + queueConnection);
			queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            debug(log,"queueSession : " + queueSession);

        } catch (NamingException e) {
            debug("JNDI lookup failed: " +
                e.toString());
            System.exit(1);
        }
        debug("#### initializeDesignNAssignJMSClient END");
    }
    
    public MessageProducer createMessageSender(String biID){
        debug("#### createMessageSender START");
        
    	WLMessageProducer queueSender = null;
    	try {
			queueSender = (WLMessageProducer)queueSession.createProducer(designNAssignQueue);
			queueSender.setUnitOfOrder(biID);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        debug("#### createMessageSender END");

    	return queueSender;
    }
    
    public MessageConsumer createMessageReceiver(String biID, String resumeOrNew){
        debug("#### createMessageReceiver START");

    	MessageConsumer queueReceiver = null;
    	try {
    		String messageSelector = null;
    		if(resumeOrNew.equals(DesignNAssignJMSClient.process_new))
    			messageSelector = "JMS_BEA_UnitOfOrder = " + "'"+biID+"'";
    		else if(resumeOrNew.equals(DesignNAssignJMSClient.resume_design))
    			messageSelector = "JMS_BEA_UnitOfOrder = " + "'"+biID+"' AND designstatus = 'PENDING_DESIGN'";
    		queueReceiver = queueSession.createConsumer(designNAssignQueue , messageSelector);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        debug("#### createMessageReceiver END");

    	return queueReceiver;
    }
    
    public void sendObjectMessage(Serializable object, MessageProducer queueSender, String resumeOrNew){
        debug("#### sendObjectMessage START");

    	try {
    		this.message = queueSession.createObjectMessage();
			this.message.setObject(object);
			if(resumeOrNew.equals(DesignNAssignJMSClient.resume_design)){
				this.message.setStringProperty("designstatus", "PENDING_DESIGN");
				this.message.setStringProperty("manualdesigncomponent", DesignNAssignJMSClient.getManualDesignComponent());
			}
			debug(log,"Producing message: " + message.getObject());
			queueSender.send(message);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				queueSender.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        debug("#### sendObjectMessage END");

    }
    
    public Message processMessage(MessageConsumer queueReceiver, HashMap<ServiceConfigurationVersion, BusinessInteractionItemType> configItemMap) throws Exception {
        debug("#### processMessage START");

    	Message msg = null;
    	try {    		
    		queueConnection.start();
    		msg = queueReceiver.receive(1000);
            debug(log,"msg : " + msg);

			if (configItemMap != null) {
//				if (msg instanceof ObjectMessage){
//					message = (ObjectMessage) msg;
					//DesignNAssignJMSClient.setManualDesignComponent(message.getStringProperty("manualdesigncomponent"));
					//HashMap<ServiceConfigurationVersion, BusinessInteractionItemType> configMap = (HashMap<ServiceConfigurationVersion, BusinessInteractionItemType>) message.getObject();
					
					HashMap<ServiceConfigurationVersion, BusinessInteractionItemType> configMap = configItemMap;
					debug("Consuming message: " + configMap.keySet());
					Iterator<ServiceConfigurationVersion> keyItr = configMap.keySet().iterator();
					if(keyItr.hasNext()){
						config = keyItr.next();
						orderItem = configMap.get(config);
						debug(" ###### Process Message #####" + config.getId() + "#####" + config.getName());
						debug(" ###### config #####" + config + "##### + orderItem" + orderItem);

						BaseConfigurationManager baseConfigMgr = PersistenceHelper.makeConfigurationManager(config.getClass());
						baseConfigMgr.automateConfiguration(config, orderItem);
					}
//				}
				//Call postDesign
				if(!queueReceiver.getMessageSelector().contains("designstatus = 'PENDING_DESIGN'")){
					//Get Service Order BI
					BusinessInteraction serviceOrderBI = oracle.communications.inventory.api.entity.utils.ConfigurationUtils.getAssociatedBusinessInteraction(config);
					this.postDesign(serviceOrderBI);
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally{
			//Close the receiver
			queueReceiver.close();
    	}
        debug("#### processMessage END");

    	return msg;
    }
    
    public void postDesign(BusinessInteraction serviceOrderBI){
        debug("#### postDesign START");

    	try {
			Set<BusinessInteraction> manualBIs = serviceOrderBI.getChildBusinessInteractions();
			
			boolean manualDesignPending = false;
			//Check if any manual design pending..
			for(BusinessInteraction bi : manualBIs){				
				if(bi.getSpecification().getName().equals("Engineering Work Order") && (bi.getAdminState() == BusinessInteractionState.CREATED || bi.getAdminState() == BusinessInteractionState.IN_PROGRESS)){
					manualDesignPending = true;
					break;
				}
			}
			
			//If manual design pending, resend the message back to DesignNAssignQueue
			if(manualDesignPending){
                HashMap<ServiceConfigurationVersion, BusinessInteractionItemType> configItemMap = new HashMap<ServiceConfigurationVersion, BusinessInteractionItemType>();
        		configItemMap.put(config, orderItem);
        		MessageProducer sender = this.createMessageSender(serviceOrderBI.getId());
        		debug("ManualDesignComponent : "+ getManualDesignComponent());
        		this.sendObjectMessage(configItemMap, sender, DesignNAssignJMSClient.resume_design); 
        		this.commitOrRollbackJMSSession();
			}
			else{
				debug(log,"Before process message");
				//If no manual design pending
				MessageConsumer receiver = this.createMessageReceiver(serviceOrderBI.getId(), DesignNAssignJMSClient.process_new);
				Message nextItem = this.processMessage(receiver, null);
				
				//If no manual design pending and no next message in the queue, send the final ProcessInteraction response to OSM
				if(nextItem==null){					
					sendFinalProcessInteractionResponseToOSM(serviceOrderBI);
					this.commitOrRollbackJMSSession();
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        debug("#### postDesign END");

    }
    
    public void postDesign(BusinessInteraction serviceOrderBI, boolean initializeSession){
        debug("#### postDesign START");

    	if(initializeSession)
			try {
				this.initializeDesignNAssignJMSClient();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	this.postDesign(serviceOrderBI);
        debug("#### postDesign END");
    }
    
    public void sendFinalProcessInteractionResponseToOSM(BusinessInteraction bi){
        debug("#### sendFinalProcessInteractionResponseToOSM START");

    	String jmsCorrelationId = null;
    	
    	Set<BusinessInteraction> childBIs = bi.getChildBusinessInteractions();
    	for(BusinessInteraction ewo : childBIs){
    		if(ewo.getSpecification().getName().equals("Engineering Work Order")){
    			if(ewo.getExternalObjectId()!=null){
    				jmsCorrelationId = ewo.getExternalObjectId();
    				break;
    			}
    		}
    	}
        
        debug("JMS Correlation Id: " + jmsCorrelationId);
        
        DesignManager designManager = DesignHelper.makeDesignManager();
        
        BusinessInteraction parentBi = designManager.getTopLevelParentBusinessInteraction(bi);
        try{
        	if(jmsCorrelationId !=null)
        		designManager.sendMessageToOSM(parentBi, jmsCorrelationId);
        }catch(ValidationException e){
        	//log.error("", e.getMessage());
        }
        debug("#### sendFinalProcessInteractionResponseToOSM END");

    }
	
    public void commitOrRollbackJMSSession() throws JMSException {
        debug("#### commitOrRollbackJMSSession START");

    	try{
	    	if(FeedbackProviderImpl.hasErrors()) {
				queueSession.rollback();
				debug(log,"Message rollbacked");
	    	}
			else {
				debug(log,"Message Committed");
				queueSession.commit();
			}
			
			queueSession.close();
			queueConnection.close();
    	} catch(JMSException e){
    		throw e;
    	}
        debug("#### commitOrRollbackJMSSession END");

    }
}