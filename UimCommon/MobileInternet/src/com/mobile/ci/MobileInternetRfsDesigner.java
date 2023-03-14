package com.mobile.ci;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mobile.utils.Constants;
import com.mobile.utils.UimHelper;
import com.mobile.utils.Util;
import com.mobile.utils.PartyHelper;
import com.mobile.utils.PlaceHelper;
import com.mobile.utils.EntityHelper;

import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.TelephoneNumber;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.common.ServiceManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemPropertyType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;
import oracle.communications.inventory.xmlbeans.ConfigurationType;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.inventory.xmlbeans.PropertyType;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.inventory.api.entity.CustomNetworkAddress;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.PlaceSpecification;

public class MobileInternetRfsDesigner extends BaseDesigner {
	
	private static final Log log = LogFactory.getLog(MobileInternetRfsDesigner.class);
	
	/**
	 * This method will handle the SatelliteData_RFS service NewInsatll scenario 
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	@Override
	protected ServiceConfigurationVersion designAdd(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designAdd - START");
		debug(log,Constants.SERVICE_DETAILS,Constants.NAME,Constants.COLON_DELIMITTER,scvConVers.getService().getName(),Constants.SPACE_DELIMITTER, 
				Constants.SPECIFICATION,Constants.COLON_DELIMITTER,scvConVers.getService().getSpecification().getName(),Constants.SPACE_DELIMITTER,
				Constants.CHAR_SERVICEACTION,Constants.COLON_DELIMITTER,orderItem.getService().getAction());		
		
		DesignManager designManager = DesignHelper.makeDesignManager();

		Service cfs = UimHelper.getCfsService(scvConVers, Constants.SPEC_MOBILEINTERNET_CFS);
		serviceAction = orderItem.getService().getAction();
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICE_ACTION, serviceAction);
		
		List<ParameterType> parameters = orderItem.getParameterList();
		HashMap<String, String> paramMap = UimHelper.getParamMap(parameters);
		
		try {
						
			/*PARAM_PLACE*/
			HashMap<String,String> paramPlace = new HashMap<String,String>();
			paramPlace.put(Constants.PARAM_LATITUDE, paramMap.get(Constants.PARAM_LATITUDE));
			paramPlace.put(Constants.PARAM_LONGITUDE, paramMap.get(Constants.PARAM_LONGITUDE));
			paramPlace.put(Constants.PARAM_VILLAGE_NAME, paramMap.get(Constants.PARAM_VILLAGE_NAME));
			paramPlace.put(Constants.PARAM_STREET_HOUSE_NUMBER, paramMap.get(Constants.PARAM_STREET_HOUSE_NUMBER));
			paramPlace.put(Constants.PARAM_BUILDING_NAME, paramMap.get(Constants.PARAM_BUILDING_NAME));
			
			GeographicPlace geographicPlace = PlaceHelper.processPlace(scvConVers, paramPlace);
			if(!Utils.checkNull(geographicPlace))
				designManager.relateServiceToGeographicPlace(scvConVers, geographicPlace);
			
			/*PARAM_PROPERTIES*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_PROPERTIES_CI);
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.CHAR_SERVICEACTION, paramMap.get(Constants.CHAR_SERVICEACTION));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_PAYTYPE, paramMap.get(Constants.PARAM_PAYTYPE));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICEPLAN, paramMap.get(Constants.PARAM_SERVICEPLAN));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_BAROUTGOINGCALLS, paramMap.get(Constants.PARAM_BAROUTGOINGCALLS));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_ROAMINGALLOWED, paramMap.get(Constants.PARAM_ROAMINGALLOWED));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_PARTYNAME, paramMap.get(Constants.PARAM_PARTYNAME));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_EMAIL, paramMap.get(Constants.PARAM_EMAIL));

			/*PARAM_MSISDN_CI*/
			Map<String,String> paramMSISDN = new HashMap<String,String>();
			paramMSISDN.put(Constants.PARAM_ID, paramMap.get(Constants.PARAM_RANGEFROM));
			paramMSISDN.put(Constants.NAME, paramMap.get(Constants.PARAM_MSISDNNAME));
			paramMSISDN.put(Constants.PARAM_VENDOR, paramMap.get(Constants.PARAM_VENDOR));
			
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_MSISDN_CI);
			TelephoneNumber tn = EntityHelper.createTNByRange(paramMap.get(Constants.PARAM_RANGEFROM), null, paramMSISDN, Constants.SPEC_MSISDN );
			if(!Utils.checkNull(tn))
				designManager.assignSubjectToParent(scvConVers, Constants.PARAM_MSISDN_CI, tn);	
	
			/*PARAM_SIM_CI*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_SIM_CI);
			
			Map<String,String> paramLD = new HashMap<String,String>();
			paramLD.put(Constants.PARAM_ID, paramMap.get(Constants.PARAM_SIMID));
			paramLD.put(Constants.PARAM_ICCID, paramMap.get(Constants.PARAM_ICCID));
			paramLD.put(Constants.PARAM_IMSI, paramMap.get(Constants.PARAM_IMSI));
			paramLD.put(Constants.PARAM_VENDOR, paramMap.get(Constants.PARAM_VENDOR));
	
			processLogicalDevice(scvConVers, paramLD, serviceAction, Constants.PARAM_SIM_CI);
	
			/*PARAM_IMSI_CI*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_IMSI_CI);
			LogicalDeviceAccount lda = EntityHelper.createLogicalDeviceAccount(paramMap.get(Constants.PARAM_IMSIID),Constants.SPEC_IMSI,null );
			if(!Utils.checkNull(lda))
				designManager.assignSubjectToParent(scvConVers, Constants.PARAM_IMSI_CI, lda);
			
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"processRfs - END");
		}
		
		debug(log,"designAdd - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(scvConVers);
	}
	
	/**
	 * This method will handle the SatelliteData_RFS service Change scenario 
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	@Override
	protected ServiceConfigurationVersion designChange(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designChange - START");
		debug(log,Constants.SERVICE_DETAILS,Constants.NAME,Constants.COLON_DELIMITTER,scvConVers.getService().getName(),Constants.SPACE_DELIMITTER, 
				Constants.SPECIFICATION,Constants.COLON_DELIMITTER,scvConVers.getService().getSpecification().getName(),Constants.SPACE_DELIMITTER,
				Constants.CHAR_SERVICEACTION,Constants.COLON_DELIMITTER,orderItem.getService().getAction());	
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		Service cfs = UimHelper.getCfsService(scvConVers);
		
		List<ParameterType> parameters = orderItem.getParameterList();
		HashMap<String, String> paramMap = UimHelper.getParamMap(parameters);
		
		serviceAction = orderItem.getService().getAction();

		if(serviceAction.equals(Constants.SA_CHANGEMSISDN)) {
			
			ServiceConfigurationItem cpeCI = designManager.aquireConfigItem(scvConVers, Constants.PARAM_MSISDN_CI);
			
			if(!Utils.checkNull(cpeCI) && !Utils.checkNull(cpeCI.getAssignment())) 
				//un-assigning the previous TelephoneNumber 
				UimHelper.unallocateResourceToConfigItem(scvConVers, cpeCI);
			
			List<TelephoneNumber> tnList = UimHelper.findTelephoneNumbers(Constants.SPEC_MSISDN, paramMap.get(Constants.PARAM_RANGEFROM), null, CriteriaOperator.EQUALS, null, null, null);
			TelephoneNumber tn = null;
			if(tnList.iterator().hasNext())
				tn = tnList.iterator().next();
			
			if(!Utils.checkNull(tn)) {
				debug(log, "TelephoneNumber Assignment Status : " + tn.getCurrentAssignment());
				if(Utils.checkNull(tn.getCurrentAssignment()))
					designManager.assignSubjectToParent(scvConVers, Constants.PARAM_MSISDN_CI, tn);
				else 
					throw new ValidationException("The TelephoneNumber of ID : " + paramMap.get(Constants.PARAM_RANGEFROM) + " ,is already assigned to another service.");
			} else 
				throw new ValidationException("The TelephoneNumber of Specification : MSISDN, id : " + paramMap.get(Constants.PARAM_RANGEFROM) + " does not exists.");
		
		} else if(serviceAction.equals(Constants.SA_CHANGEPAYTYPE)) {
			
			debug(log,"Inside SA_CHANGEPAYTYPE");
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_PROPERTIES_CI);
			designManager.addUpdateServiceCharacteristic(cfs, Constants.PARAM_PAYTYPE, paramMap.get(Constants.PARAM_PAYTYPE));
			designManager.addUpdateServiceCharacteristic(cfs, Constants.PARAM_SERVICEPLAN, paramMap.get(Constants.PARAM_SERVICEPLAN));

			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_PAYTYPE, paramMap.get(Constants.PARAM_PAYTYPE));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICEPLAN, paramMap.get(Constants.PARAM_SERVICEPLAN));
		} 
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICE_ACTION, serviceAction);
//		}
		
		debug(log,"designChange - END");
		return scvConVers;
	}
	
	/**
	 * This method will handle the SatelliteData_RFS service Disconnect scenario 
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	@Override
	protected ServiceConfigurationVersion designDisconnect(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designDisconnect - START");
		debug(log,Constants.SERVICE_DETAILS,Constants.NAME,Constants.COLON_DELIMITTER,scvConVers.getService().getName(),Constants.SPACE_DELIMITTER, 
				Constants.SPECIFICATION,Constants.COLON_DELIMITTER,scvConVers.getService().getSpecification().getName(),Constants.SPACE_DELIMITTER,
				Constants.CHAR_SERVICEACTION,Constants.COLON_DELIMITTER,orderItem.getService().getAction());	
		
		debug(log,"ServiceAction : " + orderItem.getService().getAction());

		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICE_ACTION, orderItem.getService().getAction());
		//Process Service Order`
		super.designDisconnect(scvConVers, orderItem);
		
		debug(log,"designDisconnect - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(scvConVers);
	}
	
	/**
	 * This method will handle the SatelliteData_RFS service Suspend scenario 
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	@Override
	protected ServiceConfigurationVersion designSuspend(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designSuspend - START");
		debug(log,Constants.SERVICE_DETAILS,Constants.NAME,Constants.COLON_DELIMITTER,scvConVers.getService().getName(),Constants.SPACE_DELIMITTER, 
				Constants.SPECIFICATION,Constants.COLON_DELIMITTER,scvConVers.getService().getSpecification().getName(),Constants.SPACE_DELIMITTER,
				Constants.CHAR_SERVICEACTION,Constants.COLON_DELIMITTER,orderItem.getService().getAction());	
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICE_ACTION, orderItem.getService().getAction());
		super.designSuspend(scvConVers, orderItem);
		
		debug(log,"designSuspend - END");		
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(scvConVers);
	}
	
	/**
	 * This method will handle the SatelliteData_RFS service Resume scenario 
	 * @param scvConVers
	 * @param orderItem
	 * @return
	 * @throws ValidationException
	 */
	@Override
	protected ServiceConfigurationVersion designResume(ServiceConfigurationVersion scvConVers, BusinessInteractionItemType orderItem) throws ValidationException {
		debug(log,"designResume - START");
		debug(log,Constants.SERVICE_DETAILS,Constants.NAME,Constants.COLON_DELIMITTER,scvConVers.getService().getName(),Constants.SPACE_DELIMITTER, 
				Constants.SPECIFICATION,Constants.COLON_DELIMITTER,scvConVers.getService().getSpecification().getName(),Constants.SPACE_DELIMITTER,
				Constants.CHAR_SERVICEACTION,Constants.COLON_DELIMITTER,orderItem.getService().getAction());		
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES_CI, Constants.PARAM_SERVICE_ACTION, orderItem.getService().getAction());
		debug(log,"designResume - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(scvConVers);
	}
	
	/**
	 * This method will handle the IP Address during ADD or CHANGE order.
	 * During ADD order IP Address will be assigned. Input required action=Activate.
	 * During CHANGE order IP Address will be unassigned. Input required action=Deactivate.
	 * 
	 * @param config
	 * @param action
	 * @param accesstechnology
	 * @throws ValidationException
	 */
	public void processLogicalDevice(ServiceConfigurationVersion scvConVers, Map<String,String> paramCPE, String action, String ci) throws ValidationException{
		debug(log, "processIPAddress - START");
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		if(action.equals(Constants.SA_CREATE)){
			
			LogicalDevice ld = null;

			if(!Utils.checkNull(ci) && ci.equalsIgnoreCase(Constants.PARAM_CPE_CI)) {
				debug(log," LogicalDevice Map : " + paramCPE);
				ld = EntityHelper.createLogicalDevice(Constants.PARAM_CPE, Constants.PARAM_CPE, paramCPE);
				debug(log," LogicalDevice : " + ld);
				
				designManager.assignSubjectToParent(scvConVers, Constants.PARAM_CPE_CI, ld);
			} 
			else if(!Utils.checkNull(ci) && ci.equalsIgnoreCase(Constants.PARAM_SIM_CI)) {
				debug(log," LogicalDevice Map : " + paramCPE);
				ld = EntityHelper.createLogicalDevice(Constants.PARAM_SIM, Constants.PARAM_SIM, paramCPE);
				debug(log," LogicalDevice : " + ld);
				
				designManager.assignSubjectToParent(scvConVers, Constants.PARAM_SIM_CI, ld);
			}
			/*Map<String, String> ldMap = configItemType.getPropertyList().stream().collect( Collectors.toMap(x -> x.getName() , x -> x.getValue()));
			debug(log," LogicalDevice Map : " + ldMap);
			
			if(configItemType.getName().equalsIgnoreCase(Constants.PARAM_CPE_CI)) {
				ld = EntityHelper.createLogicalDevice(Constants.PARAM_CPE, Constants.PARAM_CPE, ldMap);
				debug(log," LogicalDevice : " + ld);
				
			} else if(configItemType.getName().equalsIgnoreCase(Constants.PARAM_SIM_CI)) {
				ld = EntityHelper.createLogicalDevice(Constants.PARAM_SIM, Constants.PARAM_SIM, ldMap);
				debug(log," LogicalDevice : " + ld);
				
			}
			designManager.assignSubjectToParent(scvConVers, configItemType.getName(), ld);*/
		}
		
		debug(log, "processIPAddress - END");
	}
	
}
