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
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.platform.persistence.PersistenceHelper;
import weblogic.jms.extensions.WLMessageProducer;

public class DesignNAssignJMSClient {
	
	public final static String resume_design = "RESUME_PENDING_DESIGN";
	public final static String process_new = "PROCESS_NEXT_DESIGN";
	
	private ServiceConfigurationVersion config;
	private BusinessInteractionItemType orderItem;
	private static String manualDesignComponent;

	QueueConnection queueConnection = null;
	QueueSession queueSession = null;
	Queue designNAssignQueue;
	ObjectMessage message  = null;
	
	public DesignNAssignJMSClient()
    {
        System.out.println("Constructor DesignNAssignJMSClient called");
    }
	
	
	public static String getManualDesignComponent() {
		return manualDesignComponent;
	}

	public static void setManualDesignComponent(String manualDesignComponent) {
		DesignNAssignJMSClient.manualDesignComponent = manualDesignComponent;
	}
	
    public void initializeDesignNAssignJMSClient() throws JMSException {
        String                  queueName = "DesignNAssignQueue";
        String                  queueConnectionFactoryName = "weblogic.jms.XAConnectionFactory";
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
            System.out.println("Queue name : "+designNAssignQueue.getQueueName());
            queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (NamingException e) {
            System.out.println("JNDI lookup failed: " +
                e.toString());
            System.exit(1);
        }
    }
    
    public MessageProducer createMessageSender(String biID){
    	WLMessageProducer queueSender = null;
    	try {
			queueSender = (WLMessageProducer)queueSession.createProducer(designNAssignQueue);
			queueSender.setUnitOfOrder(biID);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return queueSender;
    }
    
    public MessageConsumer createMessageReceiver(String biID, String resumeOrNew){
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
    	return queueReceiver;
    }
    
    public void sendObjectMessage(Serializable object, MessageProducer queueSender, String resumeOrNew){
    	try {
    		this.message = queueSession.createObjectMessage();
			this.message.setObject(object);
			if(resumeOrNew.equals(DesignNAssignJMSClient.resume_design)){
				this.message.setStringProperty("designstatus", "PENDING_DESIGN");
				this.message.setStringProperty("manualdesigncomponent", DesignNAssignJMSClient.getManualDesignComponent());
			}
			//System.out.println("Producing message: " + message.getObject());
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
    }
    
    public Message processMessage(MessageConsumer queueReceiver) throws Exception {
    	Message msg = null;
    	try {    		
    		queueConnection.start();
    		msg = queueReceiver.receive(1000);
			if (msg != null) {
				if (msg instanceof ObjectMessage){
					message = (ObjectMessage) msg;
					DesignNAssignJMSClient.setManualDesignComponent(message.getStringProperty("manualdesigncomponent"));
					
					HashMap<ServiceConfigurationVersion, BusinessInteractionItemType> configMap = (HashMap<ServiceConfigurationVersion, BusinessInteractionItemType>) message.getObject();
					System.out.println("Consuming message: " + configMap.keySet());
					Iterator<ServiceConfigurationVersion> keyItr = configMap.keySet().iterator();
					if(keyItr.hasNext()){
						config = keyItr.next();
						orderItem = configMap.get(config);
						System.out.println(" ###### Process Message #####" + config.getId() + "#####" + config.getName());
						BaseConfigurationManager baseConfigMgr = PersistenceHelper.makeConfigurationManager(config.getClass());
						baseConfigMgr.automateConfiguration(config, orderItem);
					}
				}
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
    	return msg;
    }
    
    public void postDesign(BusinessInteraction serviceOrderBI){
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
        		System.out.println("ManualDesignComponent : "+ getManualDesignComponent());
        		this.sendObjectMessage(configItemMap, sender, DesignNAssignJMSClient.resume_design); 
        		this.commitOrRollbackJMSSession();
			}
			else{
				//If no manual design pending
				MessageConsumer receiver = this.createMessageReceiver(serviceOrderBI.getId(), DesignNAssignJMSClient.process_new);
				Message nextItem = this.processMessage(receiver);
				
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
    }
    
    public void postDesign(BusinessInteraction serviceOrderBI, boolean initializeSession){
    	if(initializeSession)
			try {
				this.initializeDesignNAssignJMSClient();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	this.postDesign(serviceOrderBI);
    }
    
    public void sendFinalProcessInteractionResponseToOSM(BusinessInteraction bi){
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
        
        System.out.println("JMS Correlation Id: " + jmsCorrelationId);
        
        DesignManager designManager = DesignHelper.makeDesignManager();
        
        BusinessInteraction parentBi = designManager.getTopLevelParentBusinessInteraction(bi);
        try{
        	if(jmsCorrelationId !=null)
        		designManager.sendMessageToOSM(parentBi, jmsCorrelationId);
        }catch(ValidationException e){
        	//log.error("", e.getMessage());
        }
    }
	
    public void commitOrRollbackJMSSession() throws JMSException {
    	try{
	    	if(FeedbackProviderImpl.hasErrors())
				queueSession.rollback();
			else
				queueSession.commit();
			
			queueSession.close();
			queueConnection.close();
    	} catch(JMSException e){
    		throw e;
    	}
    }
}