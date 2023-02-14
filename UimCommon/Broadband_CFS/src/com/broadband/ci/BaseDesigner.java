package com.broadband.ci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.broadband.utils.Constants;
import com.broadband.utils.DeviceHelper;
import com.broadband.utils.EntityHelper;
import com.broadband.utils.UimHelper;

import oracle.communications.inventory.api.common.BaseInvManager;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceAssignmentToService;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceLogicalDeviceRel;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationItemCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceStatus;
import oracle.communications.inventory.api.entity.common.Assignment;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.framework.security.UserEnvironment;
import oracle.communications.inventory.api.framework.security.UserEnvironmentFactory;
import oracle.communications.inventory.api.service.impl.ServiceTransition;
import oracle.communications.inventory.api.service.impl.ServiceUtils;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.ConfigurationUtils;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.common.ServiceManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.BusinessInteractionType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemPropertyType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;
import oracle.communications.inventory.xmlbeans.ConfigurationType;
import oracle.communications.inventory.xmlbeans.ServiceType;
import oracle.communications.platform.entity.impl.CustomObjectAssignmentToServiceDAO;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;

/**
 * Base Designer class for all the CFS and RFS designers which are service specific. 
 */
public class BaseDesigner extends BaseInvManager {
	
	protected String serviceAction;
	protected String serviceSpec;
	protected String extObjId;
	protected String medium;
	protected String payType;
	protected String customerName;
	protected String division = null;
	protected String serviceType = null;
	protected String deviceType  = null;
	protected String deviceIPAddress = null; 
	protected String subscriberTelephoneNumber = null; 
	protected String slotNumber  = null;
	protected String portNumber  = null;
	protected String frameNumber = null;
	protected String terminalId = null;
	protected String activeDeviceName = null;
	
	// Below properties are to be fetched from the Old Device
	protected String oldDeviceType  = null; 
	protected String oldDeviceIPAddress = null;
	protected String oldPEName = null;
	protected String newPEName = null;
	protected String oldSlotNumber  = null;
	protected String oldPortNumber = null;
	protected String oldFrameNumber  = null;
	protected String oldTerminalId = null;
	
	protected DeviceInterface pendingUnassignDownlinkPort = null;
	protected DeviceInterface unassignedDownlinkPort = null;
	protected DeviceInterface downlinkPort = null;
	protected DeviceInterface serviceDownlinkPort = null; 
	protected DeviceInterface comboDownlinkPort = null;
	protected DeviceInterface unassignedComboDownlinkPort = null;
	protected DeviceInterface pendingUnassignVirtualPort = null;
	protected DeviceInterface unassignedVirtualPort = null;
	protected DeviceInterface virtualPort = null;
	protected DeviceInterface pendingUnassignDpPort = null;
	protected DeviceInterface unassignedDpPort = null;
	protected DeviceInterface dpPort = null;
	protected DeviceInterface pendingUnassignTbPort = null;
	protected DeviceInterface unassignedTbPort = null;
	protected DeviceInterface tbPort = null;
	protected DeviceInterface cabinetESidePort = null; 
	protected DeviceInterface cabinetDSidePort = null;
	protected DeviceInterface mdfPort = null;
	protected String reservationId = null;
	protected Service cfs=null;
	
	private static final Log log = LogFactory.getLog(BaseDesigner.class);
	
	/**
	 * Log the debug data values in the debug log if the log is enabled. 
	 * @param logger
	 * @param objects	 
	 * @return
	 * @throws 
	 */
	public static void debug(Log logger, Object ...objects) {
		try {
			if (logger.isDebugEnabled()) {
				StringBuilder sb=new StringBuilder();
				for(Object obj:objects) {
					sb.append(obj);
				}
				logger.debug("",sb.toString());
			}
		} catch (Exception e) {
			logger.error("", "Exception :"+e.getMessage(),e);			
		}
	}
	
	/**
	 * Design method which will invoke all the framework methods of CFS and RFS subclasses as per the type of the order. 
	 * 
	 * @param scvConVers ServiceConfiguationItem object. 
	 * @param orderItem BusinessInteractionItemType object. 
	 * @return
	 * @throws ValidationException
	 */
	public ServiceConfigurationVersion design(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException {
		debug(log,"design - START");
		
		try {
			/*
			 * Service configuration is required whether it is called from UI or
			 * web service. If orderItem is null, it means that this method is
			 * called from the UI. If orderItem is given, it can assign/reference
			 * resources to service based on order info.
			 */
			if (scvConVers == null){
				log.validationException("Service.configurationIsNull", new java.lang.IllegalArgumentException());
			}
			serviceSpec = orderItem.getService().getSpecification().getName();
			if(serviceSpec.contains("CFS")){
				extObjId = orderItem.getService().getExternalIdentity().getExternalObjectId();
				if(Utils.isBlank(extObjId)){
					log.validationException("Service.externalObjectIdNotFound", new java.lang.IllegalArgumentException());
				}
			}
			
			
			BaseDesigner designer = null;
			
			designer = initializeDesigner(serviceSpec, designer);

			serviceAction = orderItem.getService().getAction();
			
			debug(log,"serviceAction : "+serviceAction);
			debug(log,"serviceAction ==============================================: "+serviceAction);
			
			if (serviceAction.equalsIgnoreCase(Constants.SA_CREATE)) {
				designer.serviceAction = serviceAction;
				scvConVers = designer.designAdd(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_CHANGE) || 
					serviceAction.equalsIgnoreCase(Constants.SA_CHANGECPE) || 
					serviceAction.equalsIgnoreCase(Constants.SA_CHANGEUPLOADSPEED)) {
				debug(log,"inside design Change : " + serviceAction);
				designer.serviceAction = serviceAction;				
				scvConVers = designer.designChange(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_DISCONNECT)) {
				scvConVers = designer.designDisconnect(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_SUSPEND)) {
				debug(log,"inside design suspend : " + serviceAction);
				designer.serviceAction = serviceAction;
				scvConVers = designer.designSuspend(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_RESUME)) {
				debug(log,"inside design resume : " + serviceAction);
				designer.serviceAction = serviceAction;
				scvConVers = designer.designResume(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_SUSPENDWITHCONFIGURATION)) {
				debug(log,"inside design resume : " + serviceAction);
				designer.serviceAction = serviceAction;
				scvConVers = designer.designSuspend(scvConVers, orderItem);
			} else if (serviceAction.equalsIgnoreCase(Constants.SA_RESUMEWITHCONFIGURATION)) {
				debug(log,"inside design resume : " + serviceAction);
				designer.serviceAction = serviceAction;
				scvConVers = designer.designResume(scvConVers, orderItem);
			} else {
				debug(log,"Action not Matched  : "+serviceAction);
				log.validationException("Service.unrecognizedServiceAction", new java.lang.IllegalArgumentException(serviceAction));
			}
			
			return scvConVers;
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"design - END");
		}
	}
	
	/**
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	protected ServiceConfigurationVersion designAdd(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designAdd - START");
		debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName() + " , Service Action: " + orderItem.getService().getAction());	
		makeProcessChildServiceOrder(scvConVers, orderItem);
		debug(log,"designAdd - END");
		return scvConVers;
	}
	
	/**
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	protected ServiceConfigurationVersion designChange(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designChange - START");
		debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName() + " , Service Action: " + orderItem.getService().getAction());
		makeProcessChildServiceOrder(scvConVers, orderItem);
		debug(log,"designChange - END");
		return scvConVers;
	}
	
	/**
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	protected ServiceConfigurationVersion designDisconnect(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designDisconnect - START");
		debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName() + " , Service Action: " + serviceAction);	
		
		serviceSpec = scvConVers.getService().getSpecification().getName();
		if(serviceSpec.endsWith("CFS")){
			makeProcessChildServiceOrder(scvConVers, orderItem);
		} 
		debug(log,"designDisconnect - END");
		return scvConVers;
	}
	
	/**
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	protected ServiceConfigurationVersion designSuspend(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designSuspend - START");
		debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName() + " , Service Action: " + serviceAction);	
		
		serviceSpec = scvConVers.getService().getSpecification().getName();
		if(serviceSpec.endsWith("CFS")){
			makeProcessChildServiceOrder(scvConVers, orderItem);
		} 
		
		debug(log,"designSuspend - END");
		return scvConVers;
	}
	
	/**
	 * This method will only be invoked for CFS to handle the resume
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	protected ServiceConfigurationVersion designResume(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designResume - START");
		debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName() + " , Service Action: " + serviceAction);	
		makeProcessChildServiceOrder(scvConVers, orderItem);
		debug(log,"designResume - END");
		return scvConVers;
	}
	
	/**
	 * This method is used to create and process child service order for create, change, suspend, resume and disconnect. 
	 * @param scvConVers
	 * @param orderItem
	 * @throws ValidationException
	 */
	private void makeProcessChildServiceOrder(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException {
		debug(log,"makeProcessChildServiceOrder - START");
		
		serviceSpec = orderItem.getService().getSpecification().getName();
		if(serviceSpec.endsWith(Constants.CFS)){
			DesignManager designManager = DesignHelper.makeDesignManager();
			BusinessInteractionType serviceOrder = null;
			String rfsSpecName = serviceSpec.replace(Constants.CFS, Constants.RFS);
			Service rfs = null;
			BusinessInteraction rfsBI = null;
			// String cfsBiId = null;
			Service cfs = scvConVers.getService();
			String cfsServiceId = cfs.getExternalObjectId();
			String cfsName = cfsServiceId + "-" + cfs.getSpecification().getName();  
			cfs.setName(cfsName);
			
			/*if(null!=config.getDescription()) {
				cfsBiId = Constants.UNDERSCORE + config.getDescription().substring(config.getDescription().indexOf("BI ")+3, config.getDescription().length());
			}*/ 
			
			serviceAction = orderItem.getService().getAction();
			extObjId = cfsServiceId;
			debug(log,"serviceNumber : " + extObjId);
			if(serviceAction.equals(Constants.SA_CREATE)){
				// Create and Assign RFS
				debug(log,"Create and Assign RFS");
				serviceOrder = designManager.makeChildServiceOrder(serviceAction, orderItem.getParameterArray(), rfsSpecName, null, Constants.SPEC_BI, extObjId);
				String rfsName = cfsServiceId + "-" + rfsSpecName;
				serviceOrder.getBody().getItemList().get(0).getService().setName(rfsName);
				prepareRfsServiceOrder(orderItem, serviceOrder, rfsSpecName);
				rfsBI = designManager.captureAndProcessServiceBusinessInteraction(serviceOrder, scvConVers);
				rfs = ConfigurationUtils.getAssociatedService(rfsBI);
				rfs.setName(rfsName);
				ServiceConfigurationItem configItem = designManager.aquireConfigItem(scvConVers,  Constants.CI_RFS_HOLDER);
				UimHelper.assignResourceToConfigItem(scvConVers, configItem, rfs);
			} else {
				// Find and Update RFS
				debug(log,"Find and Update RFS");
				rfs = (Service) designManager.getAssignedEntity(scvConVers, Constants.CI_RFS_HOLDER);
				serviceOrder = designManager.makeChildServiceOrder(serviceAction, orderItem.getParameterArray(), rfsSpecName, rfs, Constants.SPEC_BI, extObjId); //serviceNumber
				String rfsName = cfsServiceId + "-" + rfs.getSpecification().getName();
				serviceOrder.getBody().getItemList().get(0).getService().setName(rfsName);
				prepareRfsServiceOrder(orderItem, serviceOrder, rfsSpecName);
				rfsBI = designManager.captureAndProcessServiceBusinessInteraction(serviceOrder, scvConVers);
				/*if(null!=cfsBiId && cfsBiId.length()>1) {
					rfsBI.setId(cfsBiId);//Forcing the BI ID of RFS Config to be same RFS Config with Underscore as Prefix
				}*/
			}
			
			debug(log,"RFS Service Order Generated for action - " + serviceAction + ":______________________________________________________________________________");
			debug(log,serviceOrder.getBody().toString());
		}
		
		debug(log,"makeProcessChildServiceOrder - END");
	}
	
	
	/**
	 * Process RFS Service Order. 
	 * 
	 * @param cfsOrderItem
	 * @param rfsServiceOrder
	 * @param rfsSpecName
	 * @return
	 */
	private BusinessInteractionType prepareRfsServiceOrder(BusinessInteractionItemType cfsOrderItem, BusinessInteractionType rfsServiceOrder, String rfsSpecName){
		debug(log,"prepareRfsServiceOrder - START");
		
		serviceAction = cfsOrderItem.getService().getAction();
		
		ServiceType cfsServiceType = cfsOrderItem.getService();
		debug(log,"cfsServiceType : " + cfsServiceType);
		debug(log,"rfsServiceOrder : " + rfsServiceOrder);
		if(serviceAction.equals(Constants.SA_CREATE)){
			debug(log,"Inside create");
			cfsServiceType.getSpecification().setName(rfsSpecName); // Set the RFS spec name
			cfsServiceType.setName(extObjId + " - " + cfsServiceType.getName().replace("CFS", "RFS"));//Set the RFS name
			cfsServiceType.unsetId();//Remove the Service identifier from child service order.
			debug(log," 378");
			//cfsServiceType.getConfigurationList().get(0).getConfigSpec().setName(rfsSpecName+Constants.SUFFIX_CONFIG_SPEC);//Change the scvConVers spec.
			cfsServiceType.getPropertyList().clear();//Remove the CFS related properties from child service order.
			cfsServiceType.unsetExternalIdentity();//Remove the External Object Id from child service order.
			debug(log," 382");
			cfsServiceType.getPartyList().clear();//Remove the party from child service order.
			cfsServiceType.getPlaceList().clear();//Remove the place from child service order.
			rfsServiceOrder.getBody().getItemList().get(0).setService(cfsServiceType);
			debug(log," 384");
		} else {
			debug(log,"Inside create");

			//Copy the service configuration element from CFS Service Order to the RFS Service Order
			if(cfsServiceType.getConfigurationList().size()==1){
				debug(log,"Copy the service configuration element from CFS Service Order to the RFS Service Order");
				rfsServiceOrder.getBody().getItemList().get(0).getService().getConfigurationList().add(cfsServiceType.getConfigurationList().get(0));
				debug(log," 394");

			}
		}
		
		debug(log,"prepareRfsServiceOrder - END");
		return rfsServiceOrder;
	}
	
	
	
	protected static String getConfigItemTypePropertyValue(ConfigurationItemType configItemType, String propName){
		debug(log,"getConfigItemTypePropertyValue - START");
		if(null!=configItemType){
			List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
			for(ConfigurationItemPropertyType prop: propList){
				if(prop.getName().equals(propName)){
					debug(log,"Property found with name: " + propName + " , value: " + prop.getValue());
					return prop.getValue();
				}
			}
		}
		debug(log,"getConfigItemTypePropertyValue - END");
		return null;
	}

	/**
	 * This method will set the scvConVers item properties based on the CI request parameters.
	 * @param configItem
	 * @param charMap
	 * @throws ValidationException
	 */
	/*protected void setConfigItemProperties(ServiceConfigurationVersion scvConVers, HashMap<String,String> charMap) throws ValidationException{
		debug(log,"setConfigItemProperties - START ");
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem sci = designManager.aquireConfigItem(scvConVers, configItemType.getName());
		UimHelper.setConfigItemChars(scvConVers, sci, charMap);
		
		debug(log,"setConfigItemProperties - END");
	}*/
	
	
	/**
	 * This method will suspend the service.
	 * @param service
	 * @throws ValidationException
	 */
	public void suspendService(Service service) throws ValidationException {
		debug(log,"suspendService - START");
		try {
			if (service == null) {
				throw new ValidationException(Constants.ERR_SERVICE_IS_NULL, new java.lang.IllegalArgumentException());
			}
			debug(log,"Service: " + service.getSpecification().getName());
			debug(log,"Service Name: " + service.getName() + " , Service Spec: " + service.getSpecification().getName());
			if (service.getSuspended()) {
				return;
			}
			
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			// find or create a service configuration - triggers the automateServiceConfiguration extension point to be called
			serviceManager.getServiceConfigurationVersion(service, null);

			
			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("Service.suspendConfigError", new java.lang.IllegalStateException());
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"suspendService - END");
		}
	}
	
	/**
	 * This method will resume the service.
	 * @param service
	 * @throws ValidationException
	 */
	public void resumeService(Service service) throws ValidationException{
		debug(log,"resumeService - START");
		try {
			ServiceConfigurationVersion scv = null;
			Service rfs = null;

			if (service == null) {
				throw new ValidationException(Constants.ERR_SERVICE_IS_NULL, new java.lang.IllegalArgumentException());
			}
			debug(log,"Service Name: " + service.getName() + " , Service Spec: " + service.getSpecification().getName());
			
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			//find or create a service configuration - triggers the automateServiceConfiguration extension point to be called
			serviceManager.getServiceConfigurationVersion(service, null);
			
			if(!Utils.checkNull(scv)) {

				debug(log," ### Resuming the cfs. ###");
				
				//Resuming the CFS
				if(service.getAdminState().equals(ServiceStatus.SUSPENDED))
					service.setAdminState(ServiceStatus.IN_SERVICE);
				
				debug(log,"" + service.getSpecification().getName() + " : " + service.getAdminState().toString());

				// Fetch the assigned RFS
				ServiceConfigurationItem rfsCi = ConfigurationUtils.getConfigurationItem(scv, "RFS_CI");
				
				debug(log," rfsCi: " + rfsCi);

				if(rfsCi != null) {
					debug(log,"Fetching the RFS");
					rfs = UimHelper.getResource(rfsCi);
				}
				
				//Resuming the RFS
				if(!Utils.checkNull(rfs) && rfs.getAdminState().equals(ServiceStatus.SUSPENDED)) {
					debug(log,"RFS : " + rfs.getAdminState().toString());
					rfs.setAdminState(ServiceStatus.IN_SERVICE);
					debug(log,"RFS Resumed");
				}
				
				UimHelper.makeTransitions(scv);
				
				debug(log," ### Resume complete. ###");
			}
			
			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("Service.resumeConfigError", new java.lang.IllegalStateException());
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"resumeService - END");
		}
	}
	
	/*
	 * This method will disconnect the service.
	 * @param service
	 * @throws ValidationException
	 */
	public void disconnectService(Service service) throws ValidationException{
		debug(log,"disconnectService - START");
		try {
			DesignManager designManager = DesignHelper.makeDesignManager();

			ServiceConfigurationVersion scvCfs = null;
			ServiceConfigurationVersion scvRfs = null;
			Service rfs = null;

			if (service == null) {
				throw new ValidationException(Constants.ERR_SERVICE_IS_NULL, new java.lang.IllegalArgumentException());
			}
			debug(log,"Service Name: " + service.getName() + " , Service Spec: " + service.getSpecification().getName());
			
			//Fetching the latest ConfigurationVersion of the CFS
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			// find or create a service configuration - triggers the automateServiceConfiguration extension point to be called
			scvCfs = UimHelper.getLatestServiceConfig(service);
			if(!Utils.checkNull(scvCfs)) {

				// Fetch the assigned RFS
				ServiceConfigurationItem rfsCi = ConfigurationUtils.getConfigurationItem(scvCfs, "RFS_CI");
				
				debug(log," rfsCi: " + rfsCi.getName());

				if(rfsCi != null) {
					debug(log,"Fetching the RFS");
					rfs = UimHelper.getResource(rfsCi);
				}
				
				//Disconnecting the RFS
				if(!Utils.checkNull(rfs) && ( rfs.getAdminState().equals(ServiceStatus.IN_SERVICE) || 
											  rfs.getAdminState().equals(ServiceStatus.SUSPENDED) )) {
					debug(log,"RFS : " + rfs.getAdminState().toString());
					rfs.setAdminState(ServiceStatus.PENDING_DISCONNECT);
					debug(log,"RFS Disconnected");
					scvRfs = UimHelper.getLatestServiceConfig(rfs);
					debug(log,"Completing the RFS scv");
					UimHelper.makeTransitions(scvRfs);
				}
				
				
				//Un-allocate ServiceConfigurationItem 
//				if(rfsCi != null) {
//					debug(log,"Fetching the RFS");
//					designManager.unassignResource(scvCfs, "RFS_CI");
//				}

				//Disconnecting the CFS
				if(service.getAdminState().equals(ServiceStatus.IN_SERVICE) || service.getAdminState().equals(ServiceStatus.SUSPENDED))
					service.setAdminState(ServiceStatus.PENDING_DISCONNECT);
				debug(log," ### Disconnecting the cfs. ###");

				UimHelper.makeTransitions(scvCfs);

				debug(log," ### Disconnect complete. ###");
			}
			
			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("Service.disconnectConfigError", new java.lang.IllegalStateException());
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"disconnectService - END");
		}
	}
	
	
	/**
	 * This method will be invoked before Complete.
	 * @param scvConVers
	 * @throws ValidationException
	 */
	public void beforeComplete(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"beforeComplete - START");
		
		try {
			if (scvConVers == null) {
				throw new ValidationException(Constants.ERR_SERVICE_CONFIGURATION_IS_NULL, new java.lang.IllegalArgumentException());
			}
			debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName());
			
			String specName = scvConVers.getService().getSpecification().getName();
			debug(log,specName);
			
			BaseDesigner designer = null;
			initializeDesigner(specName, designer);
			
			Service srv = scvConVers.getService();
			if (srv == null || !ServiceUtils.validateEntityExists(srv)) {
				throw new ValidationException(Constants.ERR_SERVICE_IS_NULL, new java.lang.IllegalArgumentException());
			}

			String serviceStatus = srv.getAdminState().toString();
			
			if (serviceStatus.contains("CANCEL")) {
				debug(log,"Skipped BEFORE_COMPLETE because Service Status was :" + serviceStatus);
				debug(log,"beforeComplete - END, Skipped Logic");
				return;
			}
			boolean resume = isResuming(scvConVers);

			if (srv.getAdminState().equals(ServiceStatus.PENDING_DISCONNECT)) {
				if(specName.contains(Constants.RFS)) cfs = UimHelper.getCfsFromRfs(srv);
				else cfs = srv;
				if(cfs == null  || (cfs != null && cfs.getSpecification().getName().endsWith("RFS")) ) {
					cfs = UimHelper.getCfsService(scvConVers);
					srv.setExternalObjectId(cfs.getExternalObjectId());
				}
				if (resume && srv.getSuspended()) {
					ServiceTransition serviceTransition = new ServiceTransition();
					serviceTransition.resumeService(srv);
					serviceTransition.resumeService(cfs);// Commented as the resume operation was not working. 
				}
			} else if(resume) {
				
				ServiceTransition serviceTransition = new ServiceTransition();
				debug(log," After resuming Service");
				serviceTransition.resumeService(scvConVers.getService());
				debug(log," After resuming Service : " + scvConVers.getService().getSpecification().getName());
			}
			ServiceConfigurationVersion latestSCV = UimHelper.getLatestServiceConfig(srv);
			String rfsServiceAction = UimHelper.getConfigItemCharValue(latestSCV, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION); 
			
			if(scvConVers.getService().getSpecification().getName().endsWith("RFS") && 
					!(rfsServiceAction.equals(Constants.SA_SUSPEND) || rfsServiceAction.equals(Constants.SA_RESUME)) ){
				cleanUpDeletedConfigItems(scvConVers);
				transitionActions(scvConVers);
			}
		}catch(Exception e) {
			log.error("", e.getMessage());
			throw new ValidationException(e.getMessage());
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"beforeComplete - END");
		}
	}

	/**
	 * This method will invoke after complete.
	 * @param scvConVers
	 * @throws ValidationException
	 */
	public void afterComplete(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"afterComplete - START");

		try {
			if (scvConVers == null) {
				throw new ValidationException(Constants.ERR_SERVICE_CONFIGURATION_IS_NULL, new java.lang.IllegalArgumentException());
			}
			
			debug(log,"Service Name: " + scvConVers.getService().getName() + " , Service Spec: " +scvConVers.getService().getSpecification().getName());
			String specName = scvConVers.getService().getSpecification().getName();
			debug(log,specName);
			
			scvConVers = connect(scvConVers);
			Service rfs = scvConVers.getService();
			
			// Make sure we have a valid service
			if (rfs != null && ServiceUtils.validateEntityExists(rfs)) {
				ServiceStatus status = rfs.getAdminState();
				
				// 22Sep2019 - If BI is Cancelled then custom code for BEFORE_COMPLETE should be skipped.
				if (status.toString().contains("CANCEL")) {
					debug(log,"Skipped AFTER_COMPLETE because Service Status was :" + status);
					debug(log,"afterComplete - END, Skipped Logic");
					return;
				}
				
				String rfsServiceName = scvConVers.getService().getSpecification().getName();
				debug(log," rfsServiceName : " + rfsServiceName);
				
				boolean suspend = isSuspending(scvConVers);
				debug(log,"suspend  : " + suspend);
				if(specName.contains("RFS")) cfs = UimHelper.getCfsFromRfs(rfs);
				else cfs = scvConVers.getService();
				
				debug(log," rfs.getSuspended()  : " + rfs.getSuspended());
				if (suspend && !rfs.getSuspended()) {
					ServiceTransition serviceTransition = new ServiceTransition();
					serviceTransition.suspendService(rfs);
					debug(log," After suspending rfs");
					serviceTransition.suspendService(cfs); // Commented as the resume operation was not working. 
				}
			}
		} catch(Exception e){
			log.validationException(Constants.ERR_INTERNAL_ERROR, e);
		}finally {
			// RequestPolicyHelper.checkPolicy();
			debug(log,"afterComplete - END");
		}
	}
	
	/**
	 * Transition the Actions under the Configuration Items.  
	 * 
	 * @param scvConVers
	 * @throws ValidationException
	 */
	private void transitionActions(ServiceConfigurationVersion scvConVers) 
			throws ValidationException {
		debug(log,"transitionServiceActions - START");
	
		try {
			List<ServiceConfigurationItem> configItemList = scvConVers.getConfigItems();
			for(ServiceConfigurationItem  configItem : configItemList) {
				if(configItem.getConfigType().equals(oracle.communications.inventory.api.entity.ConfigurationType.ITEM)
						&& !Utils.isBlank(configItem.getName()) ) {			
					String action = UimHelper.getConfigItemCharValue(scvConVers, configItem, Constants.PARAM_ACTION);
					if(!configItem.getName().equals(Constants.PARAM_PROPERTIES)) {
						if(!Utils.isBlank(action) && (action.equals(Constants.PARAM_ADD)|| action.equals(Constants.RELOCATE))) {
							HashMap<String, String> charMap = new HashMap<>();
							charMap.put(Constants.PARAM_ACTION, Constants.PARAM_EXISTING);
							UimHelper.setConfigItemChars(scvConVers, configItem, charMap);
						}
						
						if(configItem.getName().equals(Constants.PARAM_VAS_CI)) {
							Map<String, ServiceConfigurationItemCharacteristic> sciCharMap = configItem.getCharacteristicMap();
							
							Set<String> sciCharNames = sciCharMap.keySet();
							Iterator<String> sciCharNamesIter = sciCharNames.iterator();
							while(sciCharNamesIter.hasNext()) {
								String sciCharName = sciCharNamesIter.next();
								
								ServiceConfigurationItemCharacteristic sciChar = sciCharMap.get(sciCharName);
								if(!Utils.isBlank(sciChar.getValue()) && sciChar.getValue().equals(Constants.DELETE)) {
									sciChar.setValue(Constants.EMPTY);
								}
								
								if(!Utils.isBlank(sciChar.getValue()) && sciChar.getValue().equals(Constants.ADD)) {
									sciChar.setValue(Constants.PARAM_EXISTING);
								}
							}
						}
					}
				}
			}	
		} catch(Exception e){
			log.validationException(Constants.ERR_INTERNAL_ERROR, e);
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	
		debug(log,"transitionServiceActions - END");
	}
	
	/**
	 * Fetch the Port Objects from the RFS configuration. 
	 * 
	 * @param scvConVers
	 * @throws ValidationException
	 */
	public void fetchPortObjects(ServiceConfigurationVersion scvConVers) {
		debug(log,"fetchPortObjects - START");
		List<ServiceConfigurationItem> rfsServiceConfigItemsList = scvConVers.getConfigItems();
		for(ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList){
			if(serviceConfigItem != null && serviceConfigItem.getName() != null){
				String serviceConfigItemName = serviceConfigItem.getName();
				debug(log,"ServiceConfigItemName CI: " + serviceConfigItemName);
				Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem.getAssignmentsMap();
				if(serviceConfigItemName.equals(Constants.CI_SUBSCRIBER_PORT)){
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while(consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface)consResIter.next();
						debug(log,"subscriber port: " + port);
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log,"subscriber port state: " + consResAssignState);
						if(port.getSpecification().getName().equals(Constants.SPEC_DP_PORT)) {
							if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
								unassignedDpPort = port;
							} else if(consResAssignState.equals(AssignmentState.PENDING_UNASSIGN)) {
								pendingUnassignDpPort = port;
							} else {
								dpPort = port;
							}
						} else if(port.getSpecification().getName().equals(Constants.SPEC_TB_PORT)) {
							if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
								unassignedTbPort = port;
							}else if(consResAssignState.equals(AssignmentState.PENDING_UNASSIGN)) {
								pendingUnassignTbPort = port;
							}else {
								tbPort = port;
							}
						}
					}
				} else if(serviceConfigItemName.equals(Constants.CI_SERVICE_PORT)){
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while(consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						debug(log,"service port: " + port);
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log,"service port state: " + consResAssignState);
						if(port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
							if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
								unassignedDownlinkPort = port;
							}else if(consResAssignState.equals(AssignmentState.PENDING_UNASSIGN)) {
								pendingUnassignDownlinkPort = port;
							}else {
								downlinkPort = port;
								serviceDownlinkPort = port;
							}
						} else if(port.getSpecification().getName().equals(Constants.SPEC_VIRTUAL_PORT)) {
							if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
								unassignedVirtualPort = port;
							}else if(consResAssignState.equals(AssignmentState.PENDING_UNASSIGN)) {
								pendingUnassignVirtualPort = port;
							}else {
								virtualPort = port;
							}
						}
					}
				}else if(serviceConfigItemName.equals(Constants.CI_COMBO_PORT)){
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while(consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						debug(log,"service port: " + port);
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log,"service port state: " + consResAssignState);
						if(port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
							if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
								unassignedComboDownlinkPort = port;
							}else if(consResAssignState.equals(AssignmentState.PENDING_UNASSIGN)) {
								// pendingUnassignComboDownlinkPort = port;
							}else {
								comboDownlinkPort = port;
							}
						}
					}
				}
			}
		}
		debug(log,"fetchPortObjects - END");
	}
	
	/**
	 * This method checks if service is about to be fully suspended.
	 * @param scvConVers
	 * @return
	 * @throws ValidationException
	 */
	protected boolean isSuspending(ServiceConfigurationVersion scvConVers) throws ValidationException {		
		debug(log,"isSuspending - START, Config Name: " + scvConVers.getName());
		
		if(scvConVers.getService().getSpecification().getName().endsWith("CFS")){	
			ServiceConfigurationItem rfsHolderCI = UimHelper.getConfigItem(scvConVers, Constants.CI_RFS_HOLDER, null);
			debug(log,"rfsHolderCI" + rfsHolderCI);
			Service rfs = (Service)UimHelper.getCurrentAssignment(rfsHolderCI.getAssignmentsMap());
			debug(log,"rfs" + rfs);
			if(null!=rfs) {
				ServiceConfigurationVersion latestSCV = UimHelper.getLatestServiceConfig(rfs);
				// serviceAction = UimHelper.getEntityCharValue(rfs, Constants.PARAM_SERVICE_ACTION);
				serviceAction = UimHelper.getConfigItemCharValue(latestSCV, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION); 
			}
		} else if(scvConVers.getService().getSpecification().getName().endsWith("RFS")){	
			debug(log,"rfs " + scvConVers.getService());
			// serviceAction = UimHelper.getEntityCharValue(scvConVers.getService(), Constants.CHAR_SERVICEACTION);
			serviceAction = UimHelper.getConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION);
		}
		debug(log,"Service Action: "+ serviceAction);
		
		Boolean returnValue = Constants.SA_SUSPEND.equals(serviceAction) || Constants.SA_TOS.equals(serviceAction); 
		debug(log,"isSuspending - END, " + returnValue);
		return returnValue;
	}
	
	/**
	 * This method checks if service is about to be fully resumed.
	 * @param scvConVers
	 * @return
	 * @throws ValidationException
	 */
	protected boolean isResuming(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"isResuming - START");
		
		if(scvConVers.getService().getSpecification().getName().endsWith("CFS")){	
			Service rfs = UimHelper.getRfsFromCfs(scvConVers.getService());
			debug(log,"rfs" + rfs);
			if(null!=rfs) {
				ServiceConfigurationVersion latestSCV = UimHelper.getLatestServiceConfig(rfs);
				serviceAction = UimHelper.getConfigItemCharValue(latestSCV, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION); 
			}
		} else if(scvConVers.getService().getSpecification().getName().endsWith("RFS")){	
			debug(log,"rfs" + scvConVers.getService());
			serviceAction = UimHelper.getConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION);
		}
		debug(log,"Service Action: "+ serviceAction);
		
		Boolean returnValue = Constants.SA_RESUME.equalsIgnoreCase(serviceAction);
		debug(log,"isResuming - END, " + returnValue);
		return returnValue;
	}
	
	/**
	 * Get the BI from given activity.
	 * @param activity
	 * @return
	 */
	public static BusinessInteraction getCurrentBIFromRFSConfiguration(ServiceConfigurationVersion scvConVers){
		debug(log,"getCurrentBIFromRFSConfiguration - START");
		debug(log,"Service Configuration: Id=" + scvConVers.getId() + " , Name=" +scvConVers.getName());
		Finder finder = null;
		BusinessInteraction bi = null;
		String entityId =null;

		try {					
			debug(log,"Configuration ID is "+scvConVers.getEntityId());	
			finder = PersistenceHelper.makeFinder();
			String sqlString  ="SELECT BI.ENTITYID  FROM BUSINESSINTERACTION BI, BUSINESSINTERACTIONITEM ITEM, SERVICECONFIGURATIONVERSION SCV "
					+ "WHERE SCV.ENTITYID = ITEM.TOENTITYREF AND ITEM.toEntityclass='ServiceConfigurationVersionDAO' AND ITEM.BUSINESSINTERACTION = BI.ENTITYID AND SCV.ENTITYID='"+ scvConVers.getEntityId()+"'";
			debug(log,sqlString);
			Collection resultSet = finder.findByNativeSQL(sqlString);
			
			// Check if search criteria is entered
			if(resultSet != null && resultSet.size()>0){
				entityId = ((Object)resultSet.iterator().next()).toString();
			}			
			if(null != entityId){
				bi  = (BusinessInteraction)PersistenceHelper.makePersistenceManager().getObjectById(BusinessInteraction.class, Long.parseLong(entityId));
				debug(log,"RFS Configuration BI: Id=" + bi.getId() + " , Name=" + bi.getName());
				bi = bi.getParentBusinessInteraction();
				debug(log,"Service Order BI: Id=" + bi.getId() + " , Name=" + bi.getName());
			}
		} catch (Exception e) {
			debug(log,"Error fetching input data from db... "+e.getLocalizedMessage());
			return bi;
		}
		finally{
			if(finder!=null) {
				finder.close();
			}	
		}
		debug(log,"getCurrentBIFromRFSConfiguration - END");
		return bi;
	}
    
    
	/**
	 * This method will set the the Properties CI.
	 * In case a VAS is already Active and Activate request is coming then don't do anything. Similarly handled Inactive.
	 * @param configItem
	 * @param charMap
	 * @throws ValidationException
	 */
	protected void setPropertiesCI(ServiceConfigurationVersion scvConVers, ConfigurationItemType configItemType) throws ValidationException {
		debug(log,"setPropertiesCI - START");
		
		HashMap<String,String> charMap = new HashMap<>();
		List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
		
		for(ConfigurationItemPropertyType prop: propList)
		{
			debug(log,"Incoming Property: " + prop.getName() + "=" + prop.getValue());
			String existingValue = UimHelper.getConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, prop.getName());
			debug(log,"Existing Property Value: " + prop.getName() + "=" + existingValue);

			if(!prop.getValue().equals(existingValue)){
				charMap.put(prop.getName(), prop.getValue());
			} else {
				debug(log,"Warning: The existing VAS value is same as incoming VAS value.");
			}
		}
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem sci = designManager.aquireConfigItem(scvConVers, configItemType.getName());
		UimHelper.setConfigItemChars(scvConVers, sci, charMap);
		
		debug(log,"setPropertiesCI - END");
	}
	
	/**
	 * This method initializes the respective class designer based on the input spec.
	 * @param specName
	 * @param designer
	 * @return
	 */
	public static BaseDesigner initializeDesigner(String specName, BaseDesigner designer){
		debug(log,"initializeDesigner - START , ServiceSpec: "+specName + " , Designer:" + designer);
		if(specName.equals(Constants.SPEC_BROADBAND_CFS)){
			designer = new BroadbandCfsDesigner();
		} else if (specName.equals(Constants.SPEC_BROADBAND_RFS)) {
			designer = new BroadbandRfsDesigner();
		}
		debug(log,"initializeDesigner - END, Designer Name: " + designer.getClass().getSimpleName());
		return designer;
	}
	
	/**
	 * This method will invoke after cancel. Only attaching this rule to the CFS.
	 * @param scvConVers
	 * @throws ValidationException
	 */
	public void afterCancel(ServiceConfigurationVersion scvConVers)  {
		debug(log,"afterCancel - START");
		
		// This method to be implemented if the as the functionality evolves.  
		
		debug(log,"afterCancel - END");
	}
	
	/**
	 * Return the BI object associated to the parent service configuration. 
	 * 
	 * @param scvConVers
	 * @return
	 * @throws ValidationException
	 */
	public static BusinessInteraction getAssociatedParentBi(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"scvConVers : " + scvConVers);
		BusinessInteraction bi = oracle.communications.inventory.api.entity.utils.ConfigurationUtils.getAssociatedBusinessInteraction(scvConVers);
		BusinessInteraction associatedParentBi = null;
		debug(log,"bi : " + bi);
		if(bi == null) {
			UserEnvironment env = UserEnvironmentFactory.getUserEnvironment();
			debug(log,"env : " + env);
			bi = (BusinessInteraction) env.getBusinessInteraction();
			debug(log,"bi : " + bi);
			if(bi != null) {
				associatedParentBi = bi.getParentBusinessInteraction();
				if(associatedParentBi == null) {
					BusinessInteraction associatedBi = oracle.communications.inventory.api.entity.utils.ConfigurationUtils.getAssociatedBusinessInteraction((InventoryConfigurationVersion) bi);
					debug(log,"associatedBi : " + associatedBi);
					associatedParentBi = associatedBi.getParentBusinessInteraction();
				}
			}
			debug(log,"associatedParentBi : " + associatedParentBi);
		}else {
			associatedParentBi = bi.getParentBusinessInteraction();
			debug(log,"associatedParentBi : " + associatedParentBi);
		}
		
		return associatedParentBi;
	}
	
	/**
	 * This method finds the Service Port of type DownlinkPort and assigns it to the Service.
	 * 
	 * @param scvConVers
	 * @param configItemType
	 * @return
	 * @throws ValidationException
	 */
	public String findnManageServicePort(ServiceConfigurationVersion scvConVers, ConfigurationItemType configItemType) throws ValidationException{
		debug(log,"findnManageServicePort - START");
		
		String activeDeviceName = "";
		String portName = "";
		
		activeDeviceName = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_ACTIVE_DEVICE_NAME);
		portName = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_PORT_NAME);
		
		debug(log,"activeDeviceName : " + activeDeviceName + " portName : " + portName);
		HashMap<String, String> charMap = new HashMap<>();
		charMap.put(Constants.PARAM_PARENT_DEVICE, activeDeviceName);
		List<DeviceInterface> virtualList = null;
		String rfsServiceName = scvConVers.getService().getSpecification().getName();
		
		if(Constants.SPEC_ISDN_RFS.equals(rfsServiceName)) {
			LogicalDevice activeDevice = UimHelper.getLogicaldDeviceByNameAndSpec(null, activeDeviceName, CriteriaOperator.EQUALS);
			if(activeDevice.getSpecification().getName().equals(Constants.SPEC_UMG)) {
				virtualList = UimHelper.findDeviceInterface(Constants.SPEC_DOWNLINK_PORT, portName, charMap, CriteriaOperator.BEGINS_WITH, AssignmentState.UNASSIGNED);
			}else {
				virtualList = UimHelper.findDeviceInterface(Constants.SPEC_VIRTUAL_PORT, portName, charMap, CriteriaOperator.BEGINS_WITH, AssignmentState.UNASSIGNED);
			}
		}else {
			virtualList = UimHelper.findDeviceInterface(Constants.SPEC_VIRTUAL_PORT, portName, charMap, CriteriaOperator.BEGINS_WITH, AssignmentState.UNASSIGNED);
		}
		if(!Utils.isEmpty(virtualList)) {
			DeviceInterface servicePort = virtualList.get(0);
			DesignManager designManager = DesignHelper.makeDesignManager();
			try {
				ServiceConfigurationItem servicePortCI = designManager.aquireConfigItem(scvConVers, Constants.CI_SERVICE_PORT); 
				if(servicePortCI.getAssignment() != null) {
					DeviceInterface oldServicePort = ((DeviceInterfaceAssignmentToService)servicePortCI.getAssignment()).getDeviceInterface();
					oldPortNumber = oldServicePort.getName();
					LogicalDevice card = oldServicePort.getParentLogicalDevice();
					LogicalDeviceLogicalDeviceRel msanRel = card.getParentLogicalDevice();
					if(msanRel == null) {
						throw new ValidationException("Error occured due to missing card to device combination.");
					}
					LogicalDevice parentDevice = msanRel.getParentLogicalDevice();
					activeDeviceName = parentDevice.getName();
					String rfsSpecName = scvConVers.getService().getSpecification().getName();
					if(rfsSpecName.equals(Constants.SPEC_SIPTRUNK_RFS) || rfsSpecName.equals(Constants.SPEC_ISDN_RFS)) {
						oldDeviceIPAddress = UimHelper.getEntityCharValue(parentDevice, Constants.PARAM_VOICEIP);	
					}else if(rfsSpecName.equals(Constants.SPEC_IPVPN_RFS) || rfsSpecName.equals(Constants.SPEC_DIA_RFS) || 
							rfsSpecName.equals(Constants.SPEC_IPLC_RFS) || rfsSpecName.equals(Constants.SPEC_EPL_RFS)) {
						oldDeviceIPAddress = UimHelper.getEntityCharValue(parentDevice, Constants.PARAM_DATAIP);
					}
					UimHelper.unallocateResourceToConfigItem(scvConVers, servicePortCI);
				}
				UimHelper.assignResourceToConfigItem(scvConVers, servicePortCI, servicePort);
				LogicalDevice card = servicePort.getParentLogicalDevice();
				LogicalDeviceLogicalDeviceRel parentDeviceRel = card.getParentLogicalDevice();
				if(parentDeviceRel == null) {
					throw new ValidationException("Error occured due to missing card to device combination.");
				}
				LogicalDevice parentDevice = parentDeviceRel.getParentLogicalDevice();
				String rfsSpecName = scvConVers.getService().getSpecification().getName();
				if(rfsSpecName.equals(Constants.SPEC_SIPTRUNK_RFS) || rfsSpecName.equals(Constants.SPEC_ISDN_RFS)) {
					deviceIPAddress = UimHelper.getEntityCharValue(parentDevice, Constants.PARAM_VOICEIP);	
				}else if(rfsSpecName.equals(Constants.SPEC_IPVPN_RFS) || rfsSpecName.equals(Constants.SPEC_DIA_RFS) || 
						rfsSpecName.equals(Constants.SPEC_IPLC_RFS) || rfsSpecName.equals(Constants.SPEC_EPL_RFS)) {
					deviceIPAddress = UimHelper.getEntityCharValue(parentDevice, Constants.PARAM_DATAIP);
				}
				
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_SERVICE_PORT, downlinkPort);	
			}
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_ACTIVE_DEVICE_NAME, activeDeviceName);
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_DEVICE_IPADDRESS, deviceIPAddress);
		}else {
			throw new ValidationException("Invalid Virtual port provided, as the Virtual Port not found or provided port is already assigned. ");
		}
		
		debug(log,"findnManageServicePort - END");
		
		return activeDeviceName + " " + portName;
	}
	
	/**
	 * This method finds the Aggregation Port of type Downlink and assigns it to the Service.
	 * 
	 * @param scvConVers
	 * @param configItemType
	 * @return
	 * @throws ValidationException
	 */
	public String findnManageAggregationPort(ServiceConfigurationVersion scvConVers, ConfigurationItemType configItemType) throws ValidationException{
		debug(log,"findnManageAggregationPort - START");
		
		String aggregationNodeName = "";
		String aggregationPort = "";
		
		List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
		for(ConfigurationItemPropertyType prop: propList){
			String name = prop.getName();
			String value = prop.getValue();
			  
			if(name.equals(Constants.PARAM_AGGREGATION_NODE_NAME)){
				aggregationNodeName = value;
				if(Utils.isBlank(aggregationNodeName))
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_AGGREGATION_NODE_NAME);
			} else if(name.equals(Constants.PARAM_AGGREGATION_PORT)){
				aggregationPort = value;
				if(Utils.isBlank(aggregationPort))
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_AGGREGATION_PORT);
			}
		}
		debug(log,"aggregationNodeName : " + aggregationNodeName + " aggregationPort : " + aggregationPort);
		HashMap<String, String> charMap = new HashMap<>();
		charMap.put(Constants.PARAM_PARENT_DEVICE, aggregationNodeName);
		
		List<DeviceInterface> downlinkList = UimHelper.findDeviceInterface(Constants.SPEC_DOWNLINK, aggregationPort, charMap, CriteriaOperator.EQUALS, null);
		if(!Utils.isEmpty(downlinkList)) {
			DeviceInterface downlink = downlinkList.get(0);
			DesignManager designManager = DesignHelper.makeDesignManager();
			try {
				ServiceConfigurationItem aggregationPortCI = designManager.aquireConfigItem(scvConVers, Constants.CI_AGGREGATION_PORT); 
				if(aggregationPortCI.getAssignment() != null) {
					UimHelper.unallocateResourceToConfigItem(scvConVers, aggregationPortCI);
				}
				UimHelper.assignResourceToConfigItem(scvConVers, aggregationPortCI, downlink);
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_AGGREGATION_PORT, downlinkPort);	
			}
		}else {
			throw new ValidationException("Invalid Aggregation Device or Downlink port provided, as no port found. ");
		}
		
		debug(log,"findnManageAggregationPort - END");
		
		return aggregationNodeName + " " + aggregationNodeName;
	}
	
	/**
	 * Search for the Subscriber port and Service port based on the reservation and assign to the RFS Service Configuration Items.
	 * 
	 * @param scvConVers
	 * @param orderItem
	 * @param serviceAction
	 * @throws ValidationException
	 */
	public void processPortsCI(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"processPortsCI - START");
		BusinessInteraction associatedParentBi = getAssociatedParentBi(scvConVers);
		
		debug(log,"associatedParentBi: " + associatedParentBi);
		String reservationId = UimHelper.getEntityCharValue(associatedParentBi, Constants.RESERVATION_ID);
		debug(log,"reservationId: " + reservationId);
		List<DeviceInterface> diList = null;
		if(reservationId != null) {
			HashMap<String, String> charMap = new HashMap<>();; 
			charMap.put(Constants.RESERVATION_ID, reservationId);
			diList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, null, null, null);
			debug(log,"diList count: " + diList.size());
			if(Utils.isEmpty(diList)) {
				log.validationException(Constants.ERR_SERVICE_MISSING_RESOURCES, new IllegalArgumentException(), "Ports are missing in the reservation");
			}
		} else {
			log.validationException("Service.missingReservationId", new IllegalArgumentException(), "Reservation Id is missing in the request");
		}
		boolean servicePortAvailable = false;
		if(diList != null) {
			int downlinkPortCount = 0;
			for(DeviceInterface di : diList) {
				if(di.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
					downlinkPortCount ++;
				}
			}
			
			debug(log,"Total Downlink Ports: " + downlinkPortCount);
			for(DeviceInterface di : diList) {
				if(di.getSpecification().getName().equals(Constants.SPEC_DP_PORT)) {
					dpPort = di;
				}else if(di.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
					servicePortAvailable = true;
					if(deviceType == null) {
						LogicalDevice card = di.getParentLogicalDevice();
						debug(log,"card: " + card);
						LogicalDeviceLogicalDeviceRel deviceRel = card.getParentLogicalDevice();
						debug(log,"deviceRel: " + deviceRel);
						if(deviceRel == null) {
							throw new ValidationException("Error occured due to missing card to device combination.");
						}
						LogicalDevice device = deviceRel.getParentLogicalDevice();
						deviceType = device.getSpecification().getName();
					}
					debug(log,"Service Spec: " + scvConVers.getService().getSpecification().getName());
					debug(log,"deviceType : ", deviceType);
					if(scvConVers.getService().getSpecification().getName().equals(Constants.SPEC_DIA_RFS) || 
							scvConVers.getService().getSpecification().getName().equals(Constants.SPEC_IPVPN_RFS)) {
						String portName = di.getName();
						int lastDigit = 0;
						
						if(Constants.SPEC_ISAM.equalsIgnoreCase(deviceType)) {
							lastDigit = Integer.parseInt( portName.substring(portName.lastIndexOf('-')+1, portName.length()) );
						}else {
							lastDigit = Integer.parseInt( portName.substring(portName.lastIndexOf('/')+1, portName.length()) );
						}
						
						debug(log,"lastDigit: ", lastDigit);
						if(downlinkPortCount == 1) {
							debug(log,"Processing only Service port");
							serviceDownlinkPort = di;
						}else if(downlinkPortCount == 2){
							debug(log,"Processing both Service port and combo port");
							// Adding DSLAM to support DataCircuit 
							if(deviceType.equals(Constants.SPEC_MSAN)||deviceType.equals(Constants.SPEC_DSLAM)) {
								if(lastDigit%2 == 0) {
									debug(log,"lastDigit: " + lastDigit + "is Even ");
									serviceDownlinkPort = di;
								}else {
									debug(log,"lastDigit: " + lastDigit + "is Odd");
									comboDownlinkPort = di;
								}
							}else if(deviceType.equals(Constants.SPEC_ISAM)) {
								if(lastDigit%2 == 0) {
									debug(log,"lastDigit: " + lastDigit + "is Even");
									comboDownlinkPort = di;
								}else {
									debug(log,"lastDigit: " + lastDigit + "is Odd ");
									serviceDownlinkPort = di;
								}
							}
						}
					}else {
						downlinkPort = di;
					}
				}else {
					debug(log,"This port not assigned to service: " + di.getSpecification().getName() + " Id : " + di.getId());
				}
			}
		}
		
		if(!servicePortAvailable && serviceAction.equals(Constants.SA_CREATE)){
			throw new ValidationException("Active Device Port missing. Service cannot be created. ");
		}
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		// Process dpPort
		if(dpPort != null) {
			try {
				UimHelper.unallocateResourceToConfigItem(scvConVers, Constants.CI_SUBSCRIBER_PORT);
				designManager.assignSubjectToParent(scvConVers, Constants.CI_SUBSCRIBER_PORT, dpPort);	
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_SUBSCRIBER_PORT, dpPort);	
			}
		}
		
		// Process downlinkPort
		if(downlinkPort != null) {
			try {
				ServiceConfigurationItem servicePortCI = designManager.aquireConfigItem(scvConVers, Constants.CI_SERVICE_PORT); 
				if(servicePortCI.getAssignment() != null) {
					UimHelper.unallocateResourceToConfigItem(scvConVers, servicePortCI);
				}
				UimHelper.assignResourceToConfigItem(scvConVers, servicePortCI, downlinkPort);  
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_SERVICE_PORT, downlinkPort);	
			}
		}
		
		// Process serviceDownlinkPort
		if(serviceDownlinkPort != null) {
			try {
				ServiceConfigurationItem servicePortCI = designManager.aquireConfigItem(scvConVers, Constants.CI_SERVICE_PORT); 
				if(servicePortCI.getAssignment() != null) {
					UimHelper.unallocateResourceToConfigItem(scvConVers, servicePortCI);
				}
				UimHelper.assignResourceToConfigItem(scvConVers, servicePortCI, serviceDownlinkPort);  
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_SERVICE_PORT, serviceDownlinkPort);	
			}
		}
		
		// Process comboDownlinkPort
		if(comboDownlinkPort != null) {
			try {
				ServiceConfigurationItem comboPortCI = designManager.aquireConfigItem(scvConVers, Constants.CI_COMBO_PORT); 
				if(comboPortCI.getAssignment() != null) {
					UimHelper.unallocateResourceToConfigItem(scvConVers, comboPortCI);
				}
				UimHelper.assignResourceToConfigItem(scvConVers, comboPortCI, comboDownlinkPort);  
			}catch(ValidationException ve) {
				//Assign the subscriberPort to Service Config Item 
				designManager.assignSubjectToParent(scvConVers, Constants.CI_COMBO_PORT, comboDownlinkPort);	
			}
		}
		
		// Process virtualPort
		if(virtualPort != null) {
			ServiceConfigurationItem comboPortsCI = designManager.aquireConfigItem(scvConVers, Constants.CI_COMBO_PORTS);
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			ServiceConfigurationItem comboPortCI = serviceManager.addChildConfigItem(scvConVers, comboPortsCI , Constants.CI_COMBO_PORT);
			// Assign the above found comboPort to given Service Config Item
			UimHelper.assignResourceToConfigItem(scvConVers, comboPortCI, virtualPort);
		}
		
		fetchPortObjects(scvConVers);
		
		if(serviceDownlinkPort != null) {
			LogicalDevice card = downlinkPort.getParentLogicalDevice();
			debug(log,"card: " + card);
			LogicalDeviceLogicalDeviceRel msanRel = card.getParentLogicalDevice();
			if(msanRel == null) {
				throw new ValidationException("Error occured due to missing card to device combination.");
			}
			debug(log,"msanRel: " + msanRel);
			LogicalDevice msan = msanRel.getParentLogicalDevice();
			debug(log,"msan: " + msan);
			activeDeviceName = msan.getName();
			deviceIPAddress = UimHelper.getEntityCharValue(msan, Constants.PARAM_DATAIP);	
			slotNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_SLOTNUMBER);
			portNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_PORTNUMBER);
			frameNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_FRAMENUMBER);
			terminalId = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_TERMINALID);
			
			LogicalDevice newCard = downlinkPort.getParentLogicalDevice();
			debug(log,"newCard: " + newCard);
			LogicalDeviceLogicalDeviceRel parentDeviceRel = newCard.getParentLogicalDevice();
			if(parentDeviceRel == null) {
				throw new ValidationException("Error occured due to missing card to device combination.");
			}
			debug(log,"parentDeviceRel: " + parentDeviceRel);
			LogicalDevice parentDevice = parentDeviceRel.getParentLogicalDevice();
			debug(log,"parentDevice: " + parentDevice);
		}
		
		if(pendingUnassignDownlinkPort != null) {
			LogicalDevice oldCard = pendingUnassignDownlinkPort.getParentLogicalDevice();
			debug(log,"oldCard: " + oldCard);
			LogicalDeviceLogicalDeviceRel oldParentDeviceRel = oldCard.getParentLogicalDevice();
			if(oldParentDeviceRel == null) {
				throw new ValidationException("Error occured due to missing card to device combination.");
			}
			debug(log,"oldParentDeviceRel: " + oldParentDeviceRel);
			LogicalDevice oldParentDevice = oldParentDeviceRel.getParentLogicalDevice();
			debug(log,"oldParentDevice: " + oldParentDevice);
			
			oldDeviceIPAddress = UimHelper.getEntityCharValue(oldParentDevice, Constants.PARAM_DATAIP);	
			oldSlotNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_SLOTNUMBER);
			oldPortNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_PORTNUMBER);
			oldFrameNumber = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_FRAMENUMBER);
			oldTerminalId = UimHelper.getEntityCharValue(downlinkPort, Constants.PARAM_TERMINALID);	
			oldDeviceType = oldParentDevice.getSpecification().getName();
		}
		
		debug(log,"processPortsCI - End");
	}
	
	/**
	 * Get the PE Name from the Request BusinessInteractionItemType.
	 * 
	 * @param orderItem
	 * @return
	 */
	public String getPEName(BusinessInteractionItemType orderItem){
		debug(log,"getPEName - START");
		String peName=null;
		ConfigurationType configType =  orderItem.getService().getConfigurationList().get(0);
		List<ConfigurationItemType> configItemTypeList = configType.getConfigurationItemList();
		for(ConfigurationItemType configItemType: configItemTypeList){
			if(configItemType.getName().equals(Constants.PARAM_PROPERTIES)){					
				List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
				for(ConfigurationItemPropertyType prop: propList){
					String vasName = prop.getName();									
					if(vasName != null && vasName.equals(Constants.PARAM_PENAME)) {
						String value = prop.getValue();	
						peName = value;
						break;
					}
				}	
				break;
			} 
		}
		debug(log,"peName :"+peName);
		debug(log,"getPEName - End");
		return peName;
	}
	
	/**
	 * Cleanup Deleted Service Configuation Items. 
	 * 
	 * @param scvConVers
	 * @throws ValidationException
	 */
	private void cleanUpDeletedConfigItems(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"cleanUpDeletedConfigItems - START");	
		try {
			List<ServiceConfigurationItem> ciList = scvConVers.getConfigItems();
			debug(log, "ciList Size :",ciList.size());
			List<ServiceConfigurationItem> deleteCiList = new ArrayList<>();					
			
			for (ServiceConfigurationItem ci : ciList) {
				if(ci.getConfigType().equals(oracle.communications.inventory.api.entity.ConfigurationType.ITEM)) {								
					String ciAction = UimHelper.getConfigItemCharValue(scvConVers, ci, Constants.PARAM_ACTION);
					debug(log, "ci Name :",ci.getName(),", ciAction :", ciAction);		
					Assignment parentAssignment=(Assignment)ci.getAssignment();
					debug(log, "parentAssignment :",parentAssignment);
					if(Constants.PARAM_DELETE.equalsIgnoreCase(ciAction) && Utils.checkNull(parentAssignment)){
						deleteCiList.add(ci);
						debug(log, "Added the CI :",ci.getName()," to the Delete List");
					}	
				}				
			}				
			debug(log, "Delete List Size :",deleteCiList.size());
			for(ServiceConfigurationItem deleteCI : deleteCiList) {
				if(null != deleteCI.getName()) {
					UimHelper.deleteServiceConfigurationItem(deleteCI);
				}
			}
		} catch(Exception e){
			log.validationException(Constants.ERR_INTERNAL_ERROR, e);
		} finally {
			RequestPolicyHelper.checkPolicy();
		}	
		debug(log,"cleanUpDeletedConfigItems - END");
	}
	
	/**
	 * This method will upadte the parmeter "Available=false" for Unassigned LTE_SIM 
	 * 
	 * @param scvConVers
	 * @return
	 * @throws ValidationException
	 */
	public void upadteUnassignedSIMProperty(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"upadteUnassignedSIMProperty - START");
		String rfsServiceName = scvConVers.getService().getSpecification().getName();		
		//Updating  "Available=false" for unassigned LTE_SIM
		if(rfsServiceName.equals(Constants.SPEC_MOBILEBROADBAND_RFS)) {					
			ServiceConfigurationItem simCI = UimHelper.getConfigItem(scvConVers, Constants.PARAM_SIM_CI, null);
			debug(log, "simCI :"+simCI);
			if(!Utils.checkNull(simCI)) {
				Map<ConsumableResource, AssignmentState> raMap = simCI.getAssignmentsMap();	
				log.debug("", "raMap Size :"+raMap.size());
				for(Map.Entry<ConsumableResource, AssignmentState> entry:raMap.entrySet()) {
					ConsumableResource cr=entry.getKey();
					AssignmentState state=entry.getValue();
					if(cr instanceof LogicalDevice && AssignmentState.UNASSIGNED.equals(state)) {						
						LogicalDevice ld=(LogicalDevice)cr;
						debug(log, "Found Unassigned SIM Name :", ld.getName());
						EntityHelper.setValue(ld, Constants.AVAILABLE, Constants.FALSE);
					}
				}
			}			
		}
		debug(log,"upadteUnassignedSIMProperty - END");
	}
	
	/**
	 * Manage the Old and New Properties CI. 
	 * 
	 * @param scvConVers
	 * @throws Exception
	 */
	public void manageOldnNewPropertiesCI(ServiceConfigurationVersion scvConVers) throws ValidationException {
		debug(log,"manageOldnNewPropertiesCI - START");
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_DATAIP, deviceIPAddress);
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem pSci = designManager.aquireConfigItem(scvConVers, Constants.PARAM_PROPERTIES);
		Map<String, String> pSciMap = UimHelper.getMapFromPropertiesMap(pSci);
		HashMap<String, String> oldCharMap = new HashMap<>(pSciMap);
		
		oldCharMap.put(Constants.PARAM_ACTION,  Constants.DELETE);
		oldCharMap.put(Constants.PARAM_PENAME,  oldPEName);
		
		String rfsSpecName = scvConVers.getService().getSpecification().getName();
		if(rfsSpecName.equals(Constants.SPEC_SIPTRUNK_RFS) || rfsSpecName.equals(Constants.SPEC_ISDN_RFS)) {
			oldCharMap.put(Constants.PARAM_VOICEIP,  oldDeviceIPAddress);
		}else if(rfsSpecName.equals(Constants.SPEC_IPVPN_RFS) || rfsSpecName.equals(Constants.SPEC_DIA_RFS) || 
				rfsSpecName.equals(Constants.SPEC_IPLC_RFS) || rfsSpecName.equals(Constants.SPEC_EPL_RFS)) {
			oldCharMap.put(Constants.PARAM_DATAIP,  oldDeviceIPAddress);
		}
		
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		ServiceConfigurationItem oldPropSci = 
					serviceManager.addChildConfigItem(scvConVers, (ServiceConfigurationItem) scvConVers.getConfigItemTypeConfig(), 
										Constants.PARAM_PROPERTIES);
		UimHelper.setConfigItemChars(scvConVers, oldPropSci, oldCharMap);
		debug(log,"manageOldnNewPropertiesCI - END");
	}
	
	/**
	 * Validate the Input data for Data Circuit Services. 
	 * 
	 * @param scvConVers
	 * @param orderItem
	 * @throws ValidationException
	 */
	public void validateDataCircuitServiceInput(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"validateInput - START");
		
		String fibreSubscriberPort = null;
		String activeDeviceName = "";
		String portName = "";
		ConfigurationType configType =  orderItem.getService().getConfigurationList().get(0);		
		List<ConfigurationItemType> configItemTypeList = configType.getConfigurationItemList();
		
		for(ConfigurationItemType configItemType: configItemTypeList){		
			debug(log, "configItem Name :",configItemType.getName());
			if(configItemType.getName().equals(Constants.PARAM_RADIO_CI)){
				if(medium.equals(Constants.MICROWAVE)) {
					String radioName = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_RADIONAME);
					String radioPort = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_RADIOPORT);
					if(Utils.isEmpty(radioName) || Utils.isEmpty(radioPort)) {
						log.validationException("Service.missingParameter", new IllegalArgumentException(), "RadioName and RadioPort are mandatory for the Microwave medium. ");
					}
				}
			} else if(configItemType.getName().equals(Constants.CI_SUBSCRIBER_PORT)){ // This Option is for the Fibre Use Case
				fibreSubscriberPort = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_FIBRESUBSCRIBERPORT);
			} else if(configItemType.getName().equals(Constants.CI_SERVICE_PORT)){ // This Option is for the Fibre Use Case
				activeDeviceName = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_ACTIVE_DEVICE_NAME);
				portName = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_PORT_NAME);
				if(activeDeviceName == null)
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_ACTIVE_DEVICE_NAME);
				if(portName == null)
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_PORT_NAME);
			} 	 		
		}
		if(medium.equals(Constants.FIBER) && (Utils.isEmpty(activeDeviceName) || Utils.isEmpty(portName) || Utils.isEmpty(fibreSubscriberPort)) ) {
			log.validationException("Service.missingParameter", new IllegalArgumentException(), "FibreSubscriberPort, Active_Device_Name and Portname are mandatory for the Fibre medium. ");
		}
		
		debug(log,"validateInput - END");
	}
	
	/**
	 * This method compares the Old and New DomainName, if they are same then it will throw the error
	 * @param configItemType
	 * @param scvConVers
	 * @throws ValidationException
	 */
	public void validateDomainNameChange(ConfigurationItemType configItemType,ServiceConfigurationItem domainNameCI) throws ValidationException{
		debug(log,"validateDomainNameChange - START");
		String newDomainName=BaseDesigner.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_DOMAIN_NAME);
		debug(log,"New DomainName Name:" ,newDomainName);							
		if(domainNameCI.getAssignment()!=null){
			Persistent persistent = domainNameCI.getAssignment();
			if(persistent instanceof CustomObjectAssignmentToServiceDAO) {
				CustomObjectAssignmentToServiceDAO coaDAO=(CustomObjectAssignmentToServiceDAO)persistent;							
				CustomObject oldDN=coaDAO.getCustomObject();
				String oldDomainName=oldDN.getName();
				debug(log,"Old DomainName Name :" ,oldDomainName);							
				if(newDomainName!=null && newDomainName.equalsIgnoreCase(oldDomainName)) {
					log.validationException("DN.InvalidChangeDomainName", new java.lang.IllegalArgumentException(), newDomainName,oldDomainName);
				}
			}	
		}
		debug(log,"validateDomainNameChange - END");
	}
}