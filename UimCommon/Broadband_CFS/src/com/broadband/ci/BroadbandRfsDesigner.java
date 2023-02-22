package com.broadband.ci;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.broadband.utils.Constants;
import com.broadband.utils.UimHelper;
import com.broadband.utils.PartyHelper;
import com.broadband.utils.PlaceHelper;
import com.broadband.utils.EntityHelper;

import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
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
import oracle.communications.inventory.api.entity.PlaceSpecification;

public class BroadbandRfsDesigner extends BaseDesigner {
	
	private static final Log log = LogFactory.getLog(BroadbandRfsDesigner.class);
	
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

		serviceAction = orderItem.getService().getAction();
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.CHAR_SERVICEACTION, serviceAction);
				
		List<ParameterType> parameters = orderItem.getParameterList();
		HashMap<String, String> paramMap = UimHelper.getParamMap(parameters);
		
		debug(log,"Line no 69");

		debug(log,"paramMap.get(Constants.PARAM_PAYTYPE) : " + paramMap.get(Constants.PARAM_PAYTYPE));

		debug(log,"Line no 72");

		/*for(PropertyType i : orderItem.getService().getPropertyList()) {
			if(i.getName().equals("PayType")) {
				debug(log," PayType : " + i.getValue());
				designManager.addUpdateServiceCharacteristic(cfs, Constants.PARAM_PAYTYPE, i.getValue());
			}
		}*/
		
		try {
			
			/*PARAM_PROPERTIES*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_PROPERTIES);
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.CHAR_SERVICEACTION, paramMap.get(Constants.CHAR_SERVICEACTION));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_ACCESS_TECHNOLOGY, paramMap.get(Constants.PARAM_ACCESS_TECHNOLOGY));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_CUSTOMERSEGMENT, paramMap.get(Constants.PARAM_CUSTOMERSEGMENT));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_PAYTYPE, paramMap.get(Constants.PARAM_PAYTYPE));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_PLANNAME, paramMap.get(Constants.PARAM_PLANNAME));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_DOWNLOADSPEED, paramMap.get(Constants.PARAM_DOWNLOADSPEED));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, Constants.PARAM_UPLOADSPEED, paramMap.get(Constants.PARAM_UPLOADSPEED));
	
			/*PARAM_CPE_CI*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_CPE_CI);
			Map<String,String> paramCPE = new HashMap<String,String>();
			paramCPE.put(Constants.PARAM_SERIAL_NUMBER, paramMap.get(Constants.PARAM_SERIAL_NUMBER));
			paramCPE.put(Constants.CPEMACADDRESS, paramMap.get(Constants.CPEMACADDRESS));
			paramCPE.put(Constants.CHAR_IP_ADDRESS, paramMap.get(Constants.CHAR_IP_ADDRESS));
			paramCPE.put(Constants.CHAR_CPE_TYPE, paramMap.get(Constants.CHAR_CPE_TYPE));
			paramCPE.put(Constants.CPEVENDOR, paramMap.get(Constants.CPEVENDOR));
			paramCPE.put(Constants.CPEMODEL, paramMap.get(Constants.CPEMODEL));
			processCPE(scvConVers, paramCPE, serviceAction);
			
			/*PARAM_USER_CREDENTIALS*/
			designManager.aquireConfigItem(scvConVers, Constants.PARAM_USER_CREDENTIALS);
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_USER_CREDENTIALS, Constants.PARAM_USERNAME, paramMap.get(Constants.PARAM_USERNAME));
			UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_USER_CREDENTIALS, Constants.PARAM_PASSWORD, paramMap.get(Constants.PARAM_PASSWORD));
			
			/*PARAM_SERVICE_ADDRESS_CI*/
			ServiceConfigurationItem serviceAddressCi = designManager.aquireConfigItem(scvConVers, Constants.PARAM_SERVICE_ADDRESS_CI);
			
			HashMap<String,String> paramPlace = new HashMap<String,String>();
			paramPlace.put(Constants.PARAM_LATITUDE, paramMap.get(Constants.PARAM_LATITUDE));
			paramPlace.put(Constants.PARAM_LONGITUDE, paramMap.get(Constants.PARAM_LONGITUDE));
			paramPlace.put(Constants.PARAM_VILLAGE_NAME, paramMap.get(Constants.PARAM_VILLAGE_NAME));
			paramPlace.put(Constants.PARAM_STREET_HOUSE_NUMBER, paramMap.get(Constants.PARAM_STREET_HOUSE_NUMBER));
			paramPlace.put(Constants.PARAM_BUILDING_NAME, paramMap.get(Constants.PARAM_BUILDING_NAME));
			
			GeographicPlace geographicPlace = PlaceHelper.processPlace(scvConVers, paramPlace, Constants.SPEC_BROADBAND_CFS_SC);
			debug(log,"GeographicPlace : "+geographicPlace);
			if(!Utils.checkNull(geographicPlace))
				designManager.referenceSubjectToParentCi(scvConVers, serviceAddressCi, (ConfigurationReferenceEnabled)geographicPlace);
			
			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("Service.designFailed",	new java.lang.IllegalStateException(), Constants.CI_RFS_HOLDER);
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"processRfs - END");
		}
		
		/*ConfigurationType configType =  orderItem.getService().getConfigurationList().get(0);
		List<ConfigurationItemType> configItemTypeList = configType.getConfigurationItemList();
		debug(log,"Config Items List Size :" ,configItemTypeList.size());*/
		/*try {			
			for(ConfigurationItemType configItemType: configItemTypeList){
				
				if(configItemType.getName().equals(Constants.PARAM_PROPERTIES)) {
					setConfigItemProperties(scvConVers, configItemType);
					configItemType.getPropertyList().get(0).getName();
					for(ConfigurationItemPropertyType i : configItemType.getPropertyList()) {
						UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, i.getName(), i.getValue());
					}					
				}
				
				else if(configItemType.getName().equals(Constants.PARAM_CPE_CI)) {
					designManager.aquireConfigItem(scvConVers, Constants.PARAM_CPE_CI);
					processCPE(scvConVers, configItemType, serviceAction);
				}
				
				else if(configItemType.getName().equals(Constants.PARAM_USER_CREDENTIALS)) {
					designManager.aquireConfigItem(scvConVers, Constants.PARAM_USER_CREDENTIALS);
					for(ConfigurationItemPropertyType i : configItemType.getPropertyList()) {
						UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_USER_CREDENTIALS, i.getName(), i.getValue());
					}
				}
				
				else if(configItemType.getName().equals(Constants.PARAM_SERVICE_ADDRESS_CI)){
					debug(log,"Inside PARAM_SERVICE_ADDRESS_CI");
					GeographicPlace geographicPlace = PlaceHelper.processPlace(scvConVers, configItemType, Constants.SPEC_BROADBAND_CFS_SC);
					debug(log,"GeographicPlace : "+geographicPlace);
					ServiceConfigurationItem serviceAddressCi = designManager.aquireConfigItem(scvConVers, Constants.PARAM_SERVICE_ADDRESS_CI);
					designManager.referenceSubjectToParentCi(scvConVers, serviceAddressCi, (ConfigurationReferenceEnabled)geographicPlace);
				}
			}
			
			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("Service.designFailed",	new java.lang.IllegalStateException(), Constants.CI_RFS_HOLDER);
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
			debug(log,"processRfs - END");
		}*/
		
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
		
		/*UimHelper.setConfigItemCharValue(scvConVers, "Properties_CI", "ServiceAction", orderItem.getService().getAction());*/

		/*UimHelper.setConfigItemCharValue(scvConVers, "Properties_CI", "ServiceNumber", serviceNumber);*/
		
		List<ParameterType> parameters = orderItem.getParameterList();
		HashMap<String, String> paramMap = UimHelper.getParamMap(parameters);
		
		serviceAction = orderItem.getService().getAction();
		DesignManager designManager = DesignHelper.makeDesignManager();

		if(serviceAction.equals(Constants.SA_CHANGECPE)) {
			
			HashMap<String,String> paramLD = new HashMap<String,String>();
			paramLD.put(Constants.CHAR_SERIAL_NUMBER, paramMap.get(Constants.CHAR_SERIAL_NUMBER));
			
			if(Utils.checkBlank(paramMap.get(Constants.CHAR_SERIAL_NUMBER))) {
				log.validationException("", new oracle.communications.inventory.api.exception.ValidationException(),
						new Object[] { "Mandatory parameter SerialNumber is missing" });		
			}
			
			ServiceConfigurationItem cpeCI = designManager.aquireConfigItem(scvConVers, Constants.PARAM_CPE_CI);
			
			if(!Utils.checkNull(cpeCI) && !Utils.checkNull(cpeCI.getAssignment())) 
				//un-assigning the previous CPE 
				UimHelper.unallocateResourceToConfigItem(scvConVers, cpeCI);
						
			LogicalDevice ld = null;
			List<LogicalDevice> ldList = UimHelper.findLogicalDevices(Constants.SPEC_CPE, null, paramLD, CriteriaOperator.EQUALS, null, null, null);
			
			if(ldList.iterator().hasNext())
				ld = ldList.iterator().next();
			
			debug(log, "Logical Device : " + ld);

			if(!Utils.checkNull(ld)) {
				debug(log, "Logical Device Assignment Status : " + ld.getCurrentAssignment());
				if(Utils.checkNull(ld.getCurrentAssignment()))
					designManager.assignSubjectToParent(scvConVers, Constants.PARAM_CPE_CI, ld);
				else 
					throw new ValidationException("The LogicalDevice of ID : " + paramMap.get(Constants.CHAR_SERIAL_NUMBER) + 
							" ,is already assigned to another service.");
			} else 
				throw new ValidationException("The LogicalDevice of Specification : CPE, id : " + paramMap.get(Constants.CHAR_SERIAL_NUMBER) + 
						" does not exists.");
		
			
			/*for(ConfigurationItemType configItemType: configItemTypeList){
				if(configItemType.getName().equals(Constants.PARAM_CPE_CI)) {
					
					//Finding an existing CPE
					for(ConfigurationItemPropertyType i : configItemType.getPropertyList()) {
						if(i.getName().equals("ID")) {
							LogicalDevice ld = UimHelper.getLogicaldDeviceByIdAndSpec(Constants.SPEC_CPE, i.getValue(), CriteriaOperator.EQUALS);
							debug(log," LogicalDevice : " + ld);
							if(!Utils.checkNull(ld)) {
								debug(log, "Logical Device Assignment Status : " + ld.getCurrentAssignment());
								if(Utils.checkNull(ld.getCurrentAssignment()))
									designManager.assignSubjectToParent(scvConVers, Constants.PARAM_CPE_CI, ld);
								else 
									throw new ValidationException("The LogicalDevice of ID : " + i.getValue() + " ,is already assigned to another service.");
							} else 
								throw new ValidationException("The LogicalDevice of Specification : CPE, id : " + i.getValue() + " does not exists.");
						}
					}
					//Creating a new CPE & assigning it to CPE_CI
					//processCPE(scvConVers, configItemType, Constants.SA_CREATE);
				}
			}*/
			
		} else if(serviceAction.equals(Constants.SA_CHANGEUPLOADSPEED)) {
			HashMap<String,String> paramSpeed = new HashMap<String,String>();

			debug(log,"Inside SA_CHANGEUPLOADSPEED");
			//if(configItemTypeList.iterator().hasNext() && configItemTypeList.iterator().next().getName().equals(Constants.PARAM_PROPERTIES))
			ServiceConfigurationItem sci = designManager.aquireConfigItem(scvConVers, Constants.PARAM_PROPERTIES);
			
			debug(log, "PARAM_UPLOADSPEED " + paramMap.get(Constants.PARAM_UPLOADSPEED));
			paramSpeed.put(Constants.PARAM_UPLOADSPEED, paramMap.get(Constants.PARAM_UPLOADSPEED));
			debug(log, "PARAM_DOWNLOADSPEED " + paramMap.get(Constants.PARAM_DOWNLOADSPEED));
			paramSpeed.put(Constants.PARAM_DOWNLOADSPEED, paramMap.get(Constants.PARAM_DOWNLOADSPEED));
			
			UimHelper.setConfigItemChars(scvConVers, sci, paramSpeed);
			
			if(Utils.checkBlank(paramMap.get(Constants.PARAM_UPLOADSPEED)) && Utils.checkBlank(paramMap.get(Constants.PARAM_DOWNLOADSPEED)))
				log.validationException("", new oracle.communications.inventory.api.exception.ValidationException(),
						new Object[] { "Mandatory parameters UploadSpeed/DownloadSpeed are missing" });		
			} 
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, "ServiceAction", serviceAction);
		
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

		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, "ServiceAction", orderItem.getService().getAction());
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
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, "ServiceAction", orderItem.getService().getAction());
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
		
		UimHelper.setConfigItemCharValue(scvConVers, Constants.PARAM_PROPERTIES, "ServiceAction", orderItem.getService().getAction());
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
	public void processCPE(ServiceConfigurationVersion scvConVers, Map<String,String> paramCPE, String action) throws ValidationException{
		debug(log, "processIPAddress - START");
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		if(action.equals(Constants.SA_CREATE)){
			
			//Map<String, String> ldMap = configItemType.getPropertyList().stream().collect( Collectors.toMap(x -> x.getName() , x -> x.getValue()));
			
			debug(log," LogicalDevice Map : " + paramCPE);
			LogicalDevice ld = EntityHelper.createLogicalDevice(Constants.PARAM_CPE, Constants.PARAM_CPE, paramCPE);
			debug(log," LogicalDevice : " + ld);
			
			designManager.assignSubjectToParent(scvConVers, Constants.PARAM_CPE_CI, ld);
		}
		
		debug(log, "processIPAddress - END");
	}
	
}
