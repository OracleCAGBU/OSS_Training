package com.mobile.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mobile.utils.Constants;
import com.mobile.utils.Constants.*;

import oracle.communications.inventory.api.consumer.AssignmentManager;
import oracle.communications.inventory.api.consumer.AssignmentSearchCrteria;
import oracle.communications.inventory.api.consumer.ConsumerUtils;
import oracle.communications.inventory.api.consumer.ReservationManager;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionAttachment;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectReservation;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceCharacteristic;
import oracle.communications.inventory.api.entity.DeviceInterfaceSpecification;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDeviceLogicalDeviceRel;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.PipeTerminationPoint;
import oracle.communications.inventory.api.entity.ReservationType;
import oracle.communications.inventory.api.entity.ReservedForType;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.common.Assignment;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.Reservation;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.policy.LockPolicy;
import oracle.communications.inventory.api.importexport.handler.BaseEntityImportExportHandler.ASSOCIATION_TYPE;
import oracle.communications.inventory.api.logicaldevice.DeviceInterfaceSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.common.CommonManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;
import oracle.communications.inventory.xmlbeans.InteractionDocument;
import oracle.communications.platform.entity.impl.ServiceDAO;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;
import oracle.communications.platform.util.Utils;

public class DeviceHelper {
	
	private static final Log log = LogFactory.getLog(DeviceHelper.class);
	
	private static void debug(String message){
		if(log.isDebugEnabled())
			log.debug("", message);
	}
	
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
	 * This method finds the DPs mapped to the given building and then sorts them based on the DP Selection Order 
	 * and then find the first available spare port.
	 * @param placeName
	 * @param inputMedium
	 * @return
	 * @throws Exception 
	 */
	public static DeviceInterface getSparePortFromDp(String deviceName) throws ValidationException{
		debug("getSparePortFromDp - START");
		
		DeviceInterface spareSubscriberPort = null;
		Finder finder = PersistenceHelper.makeFinder();		
		try {
			Collection<LogicalDevice> collLD = finder.findByName(LogicalDevice.class, deviceName);
			if(Utils.isEmpty(collLD) || collLD.size()>1) {
				log.validationException("ws.internalError", new java.lang.IllegalArgumentException(), "DP/TB not found or more than one DP/TB found with same name");
			}
			LogicalDevice device = collLD.iterator().next();
			debug("Finding Spare port in Subscriber Device: " + device.getName());
			spareSubscriberPort = getSparePortFromDevice(device);
			if(spareSubscriberPort == null) {
				log.validationException("ws.internalError", new java.lang.IllegalArgumentException(), "Spare Subscriber port not found.");
			} else {			
				debug("Spare Subscriber Port Found is: " + spareSubscriberPort.getName() + " under Device: " + UimHelper.getEntityCharValue(spareSubscriberPort, Constants.CHAR_PARENTDEVICE));				
			}		
		
		} finally {
			finder.close();
		}
		debug("getSparePortFromDp - END");
		return spareSubscriberPort;
	}
	
	/**
	 * This method returns the spare subscriber port from device list (FDP/DP/AGG).
	 * In case of COPPER and AGG it looks for the SPARE Port.
	 * In case of FIBRE it looks for D or E Side FDP Port on which Bandwidth is available.
	 * @param device
	 * @param requestedUploadBw
	 * @param requestedDownloadBw
	 * @param orderType
	 * @param serviceType
	 * @return
	 * @throws ValidationException
	 */
	public static DeviceInterface getSparePortFromDevice(LogicalDevice device, String requestedDownloadBw, String orderType, String serviceType) throws ValidationException{
		debug("getSparePortFromDevice - START");
		Map<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_OPERATIONALSTATUS, Constants.EnumOperationalStatus.Spare.toString());		
		List<DeviceInterface> diList = null;
		DeviceInterface sparePort = null;

		String deviceName = device.getName();
		String deviceSpec = device.getSpecification().getName();
		String diSpecName = deviceSpec.concat(Constants.SUFFIX_E_PORT);
		//device.getSpecification().getRelatedSpecs().iterator().next().
		charMap.put(Constants.CHAR_PARENTDEVICE, deviceName);		
		if(deviceSpec.equals(Constants.EnumCopperDevices.DP.toString())){
			diList = findDIByNameCharSpec(null, charMap, diSpecName, null, 1, AssignmentState.ASSIGNED);
			if(!Utils.isEmpty(diList)) {
				sparePort = diList.get(0);
				debug("Found the spare Copper port (E Side)");
			}
		} 
		
		debug("getSparePortFromDevice - END");
		return sparePort;
	}
	
	/**
	 * Get Spare Port from the device. 
	 * 
	 * @param device
	 * @return
	 * @throws ValidationException
	 */
	public static DeviceInterface getSparePortFromDevice(LogicalDevice device) throws ValidationException {
		debug("getSparePortFromDevice - START");
		Map<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_OPERATIONALSTATUS, "Intact");
		charMap.put(Constants.CHAR_PARENTDEVICE, device.getName());	
		List<DeviceInterface> diList = null;
		DeviceInterface sparePort = null;			
		diList = findDIByNameCharSpec(device, null, charMap, null, AssignmentState.ASSIGNED);
		if(!Utils.isEmpty(diList)) {
			//Defect #977
			for(DeviceInterface di:diList) {
				if(Utils.checkBlank(UimHelper.getEntityCharValue(di, Constants.CHAR_RESERVATIONID))) {
					sparePort = di;
					debug("Found the Intact Copper port: "+sparePort);
					break;
				}
			}
		}
		
		if(Utils.checkNull(sparePort)) {
			charMap.put(Constants.CHAR_OPERATIONALSTATUS, Constants.EnumOperationalStatus.Spare.toString());
			diList = findDIByNameCharSpec(device, null, charMap, null, AssignmentState.ASSIGNED);
		}
		
		if(log.isDebugEnabled()) {
			log.debug("", "diList size: "+diList.size());
		}
		
		if(!Utils.isEmpty(diList) && Utils.checkNull(sparePort)) {
			for(DeviceInterface di:diList) {
				if(Utils.checkBlank(UimHelper.getEntityCharValue(di, Constants.CHAR_RESERVATIONID)) && Utils.checkBlank(UimHelper.getEntityCharValue(di, Constants.CHAR_SERVICEID))) {
					sparePort = di;
					debug("Found the Spare Copper port: "+sparePort);
					break;
				}
			}			
		}		
		debug("getSparePortFromDevice - END");
		return sparePort;
	}	
		
	/**
	 * This method searches for Logical Device by Characteristics and Specification
	 * @param charMap CharNameValue map
	 * @param specName
	 * @return
	 * @throws ValidationException
	 */
	public static List<LogicalDevice> findLdByCharAndSpec(Map<String,String> charMap, String specName) throws ValidationException {
		debug("findLdByCharAndSpec - START");
		
		List<LogicalDevice> ldListByChar = new ArrayList<>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria logicalDeviceSearchCriteria = logicalDeviceManager.makeLogicalDeviceSearchCriteria();
		logicalDeviceSearchCriteria.setAdminState(InventoryState.INSTALLED);

		//Specification - Start
		if(null!=specName){
			CommonManager commonManager = CommonHelper.makeCommonManager();
			LogicalDeviceSpecification ldSpec = (LogicalDeviceSpecification) commonManager.findAndValidateSpecification(specName);
			if (ldSpec == null) {
				log.validationException("c2a.couldNotFind", new java.lang.IllegalArgumentException(), specName);
			} else {
				LogicalDeviceSpecification[] ldSpecList = new LogicalDeviceSpecification[1];
				ldSpecList[0] = ldSpec;
				logicalDeviceSearchCriteria.setLogicalDeviceSpecs(ldSpecList);
			}
		}
		//Specification - End

		//Characteristics - Start
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = logicalDeviceSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			logicalDeviceSearchCriteria.addCharacteristicData(criteriaItems);
		}
		ldListByChar.addAll(logicalDeviceManager.findLogicalDevice(logicalDeviceSearchCriteria));
		//Characteristics - End
		
		debug("findLdByCharAndSpec - END");
		return ldListByChar;
	}
	
	/**
	 * This method searches for Logical Device by Characteristics and Specification
	 * @param charMap CharNameValue map
	 * @param specName
	 * @return
	 * @throws ValidationException
	 */
	public static List<LogicalDevice> findLdByNameAndSpec(String ldName, String specName) throws ValidationException {
		debug("findLdByNameAndSpec - START");
		
		List<LogicalDevice> ldList = new ArrayList<>();

		LogicalDeviceManager logicalDeviceManager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria logicalDeviceSearchCriteria = logicalDeviceManager.makeLogicalDeviceSearchCriteria();
		
		if(null!=ldName){
			CriteriaItem nameCI = logicalDeviceSearchCriteria.makeCriteriaItem();
			nameCI.setValue(ldName);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			logicalDeviceSearchCriteria.setName(nameCI);
		}

		//Specification - Start
		if(null!=specName){
			CommonManager commonManager = CommonHelper.makeCommonManager();
			LogicalDeviceSpecification ldSpec = (LogicalDeviceSpecification) commonManager.findAndValidateSpecification(specName);
			if (ldSpec == null) {
				log.validationException("c2a.couldNotFind", new java.lang.IllegalArgumentException(), specName);
			} else {
				LogicalDeviceSpecification[] ldSpecList = new LogicalDeviceSpecification[1];
				ldSpecList[0] = ldSpec;
				logicalDeviceSearchCriteria.setLogicalDeviceSpecs(ldSpecList);
			}
		}
		//Specification - End
		ldList.addAll(logicalDeviceManager.findLogicalDevice(logicalDeviceSearchCriteria));
		debug("findLdByNameAndSpec - END");
		return ldList;
	}
	
	/**
	 * This method searches for Logical Device by Name/Characteristics/Specification
	 * @param diName
	 * @param charMap
	 * @param specName
	 * @param charCriteriaOperator
	 * @param assignmentState
	 * @return
	 * @throws ValidationException
	 */
	public static List<DeviceInterface> findDIByNameCharSpec(String diName, Map<String,String> charMap, String specName, CriteriaOperator charCriteriaOperator, Integer quantity, AssignmentState assignmentState) throws ValidationException {
		debug("findDIByNameCharSpec - START, diName="+diName+" , diSpec="+specName);
		
		List<DeviceInterface> diListByChar = new ArrayList<>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper.makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria diSearchCriteria = logicalDeviceManager.makeDeviceInterfaceSearchCriteria();
		diSearchCriteria.setAdminState(InventoryState.INSTALLED);
		
		if(assignmentState!=null){
			diSearchCriteria.setAssignmentState(assignmentState);
		}
		
		//Set Quantity Criteria in cases where only specific no. of DI are required.
		if(quantity!=null){
			long max = quantity.longValue()-1;
			diSearchCriteria.setRange(0, max);
		}
		
		//Set Name Criteria
		if(null!=diName){
			CriteriaItem nameCI = diSearchCriteria.makeCriteriaItem();
			nameCI.setValue(diName);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			diSearchCriteria.setName(nameCI);
		}
		
		//Set Specification Criteria
		if(null!=specName){
			CommonManager commonManager = CommonHelper.makeCommonManager();
			DeviceInterfaceSpecification diSpec = (DeviceInterfaceSpecification) commonManager.findAndValidateSpecification(specName);
			if (diSpec == null) {
				log.validationException("c2a.couldNotFind", new java.lang.IllegalArgumentException(), specName);
			} else {
				diSearchCriteria.setDeviceInterfaceSpecification(diSpec);
			}
		}

		//Set Characteristics Criteria
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = diSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(DeviceInterfaceCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				if(Utils.isEmpty(entry.getValue())) {
					throw new ValidationException("Cannnot build DI Search Criteria for Char: " + entry.getKey() + " with null value.");
				} else {
					charCriteriaItem.setValue(entry.getValue());
				}
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			diSearchCriteria.addCharacteristicData(criteriaItems);
		}
		diListByChar.addAll(logicalDeviceManager.findDeviceInterface(diSearchCriteria));

		debug("findDIByNameCharSpec - END, Ports Found: "+ diListByChar.size());
		return diListByChar;
	}
	/**
	 * @author rpamuru
	 */
	public static List<DeviceInterface> findDIByNameCharSpec(LogicalDevice ld, String specName, Map<String,String> charMap, CriteriaOperator charCriteriaOperator, AssignmentState assignmentState) throws ValidationException {
		debug("findDIByNameCharSpec - START, LD Name="+ld);
		
		List<DeviceInterface> diListByChar = new ArrayList<>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper.makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria diSearchCriteria = logicalDeviceManager.makeDeviceInterfaceSearchCriteria();
		diSearchCriteria.setAdminState(InventoryState.INSTALLED);
		
		if(assignmentState!=null){
			diSearchCriteria.setAssignmentState(assignmentState);
		}
		//diSearchCriteria.setRange(0, 1);
		
		//Set Specification Criteria
		if(null!=specName){
			CommonManager commonManager = CommonHelper.makeCommonManager();
			DeviceInterfaceSpecification diSpec = (DeviceInterfaceSpecification) commonManager.findAndValidateSpecification(specName);
			if (diSpec == null) {
				log.validationException("c2a.couldNotFind", new java.lang.IllegalArgumentException(), specName);
			} else {
				diSearchCriteria.setDeviceInterfaceSpecification(diSpec);
			}
		}
		if(!Utils.checkNull(ld)) {
			CriteriaItem ldCriteriaItem = diSearchCriteria.makeCriteriaItem();
			ldCriteriaItem.setName("id");
			ldCriteriaItem.setValue(ld.getId());
			ldCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			diSearchCriteria.setLogicalDeviceId(ldCriteriaItem);
		}		
		//Set Characteristics Criteria
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = diSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(DeviceInterfaceCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				if(log.isDebugEnabled()) {
					log.debug("", "Char Name: "+entry.getKey()+" Char value: "+entry.getValue());
				}
				if(Utils.isEmpty(entry.getValue())) {
					throw new ValidationException("Cannnot build DI Search Criteria for Char: " + entry.getKey() + " with null value.");
				} else {
					charCriteriaItem.setValue(entry.getValue());
				}
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			diSearchCriteria.addCharacteristicData(criteriaItems);
		}
		//Added for excluding locked DI objects
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
        lockPolicy.setFilterExistingLocks(true);
        diSearchCriteria.setLockPolicy(lockPolicy);

		diListByChar.addAll(logicalDeviceManager.findDeviceInterface(diSearchCriteria));

		debug("findDIByNameCharSpec - END, Ports Found: "+ diListByChar.size());
		return diListByChar;
	}
	
	/**
	 * To fine the DeviceInterface based on the below criteria.
	 * @param ld
	 * @param diName
	 * @param specName
	 * @param charMap
	 * @param charCriteriaOperator
	 * @param assignmentState
	 * @return
	 * @throws ValidationException
	 */
	public static List<DeviceInterface> findDIByNameCharSpec(LogicalDevice ld, String diName, String specName, Map<String,String> charMap, CriteriaOperator charCriteriaOperator, AssignmentState assignmentState) throws ValidationException {
		debug("findDIByNameCharSpec - START, LD Name="+ld);
		
		List<DeviceInterface> diListByChar = new ArrayList<>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper.makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria diSearchCriteria = logicalDeviceManager.makeDeviceInterfaceSearchCriteria();
		diSearchCriteria.setAdminState(InventoryState.INSTALLED);
		
		if(assignmentState!=null){
			diSearchCriteria.setAssignmentState(assignmentState);
		}
		diSearchCriteria.setRange(0, 1);
		
		//Set Name Criteria
		if(null!=diName){
			CriteriaItem nameCI = diSearchCriteria.makeCriteriaItem();
			nameCI.setValue(diName);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			diSearchCriteria.setName(nameCI);
		}
		
		//Set Specification Criteria
		if(null!=specName){
			CommonManager commonManager = CommonHelper.makeCommonManager();
			DeviceInterfaceSpecification diSpec = (DeviceInterfaceSpecification) commonManager.findAndValidateSpecification(specName);
			if (diSpec == null) {
				log.validationException("c2a.couldNotFind", new java.lang.IllegalArgumentException(), specName);
			} else {
				diSearchCriteria.setDeviceInterfaceSpecification(diSpec);
			}
		}
		if(!Utils.checkNull(ld)) {
			CriteriaItem ldCriteriaItem = diSearchCriteria.makeCriteriaItem();
			ldCriteriaItem.setName("id");
			ldCriteriaItem.setValue(ld.getId());
			ldCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			diSearchCriteria.setLogicalDeviceId(ldCriteriaItem);
		}		
		//Set Characteristics Criteria
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = diSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(DeviceInterfaceCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				if(log.isDebugEnabled()) {
					log.debug("", "Char Name: "+entry.getKey()+" Char value: "+entry.getValue());
				}
				if(Utils.isEmpty(entry.getValue())) {
					throw new ValidationException("Cannnot build DI Search Criteria for Char: " + entry.getKey() + " with null value.");
				} else {
					charCriteriaItem.setValue(entry.getValue());
				}
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			diSearchCriteria.addCharacteristicData(criteriaItems);
		}
		//excluding locked DI objects
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
        lockPolicy.setFilterExistingLocks(true);
        diSearchCriteria.setLockPolicy(lockPolicy);

		diListByChar.addAll(logicalDeviceManager.findDeviceInterface(diSearchCriteria));

		debug("findDIByNameCharSpec - END, Ports Found: "+ diListByChar.size());
		return diListByChar;
	}
		
	/**
	 * This method will return the reserved ports from the given reservation number in random order.
	 * @param reservationId
	 * @return
	 * @throws ValidationException 
	 */
	public static List<DeviceInterface> getReservedPorts(String reservationId) throws ValidationException{
		debug("getReservedPorts - START");
		
		Map<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_RESERVATIONID, reservationId);
		
		debug("getReservedPorts - END");
		return findDIByNameCharSpec(null, charMap, null, null, null, null);
	}
	
	public static List<DeviceInterface> getReservedPortsForService(ServiceConfigurationVersion config, String reservationId) throws ValidationException{
		debug("getReservedPortsForService - START");
		HashMap<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_RESERVATIONID, reservationId);
		List<DeviceInterface> reservedPorts = findDIByNameCharSpec(null, charMap, null, null, null, null);
		debug("getReservedPortsForService - END");
		return reservedPorts;
	}
	
	/**
	 * This method retrieves the DI based on given service number.
	 * @param serviceId
	 * @return
	 * @throws ValidationException
	 */
	public static List<DeviceInterface> findDIByServiceId(String serviceId) throws ValidationException {
		debug("findDIByServiceId - START, serviceId="+serviceId);
		
		HashMap<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_SERVICEID, serviceId);
		
		List<DeviceInterface> diList= findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS, null, null);
		
		debug("findDIByServiceId - END, Ports Found: "+diList.size());
		return diList;
	}
	
	/**
	 * @param serviceId
	 * @throws ValidationException
	 */
	public static void cleanUpPortOnDisconnect(String serviceId) throws ValidationException{
		debug("cleanUpPortOnDisconnect - START, serviceId="+serviceId);
		List<DeviceInterface> diList = findDIByServiceId(serviceId);
		for(DeviceInterface di: diList){
			String servicesOnPort = UimHelper.getEntityCharValue(di, Constants.CHAR_SERVICEID);
			if(servicesOnPort.equals(serviceId)){
				EntityHelper.setValue(di, Constants.CHAR_OPERATIONALSTATUS, EnumOperationalStatus.Spare.toString());
				EntityHelper.setValue(di, Constants.CHAR_RESERVATIONID, Constants.EMPTY);
				EntityHelper.setValue(di, Constants.CHAR_SERVICEID, Constants.EMPTY);
			} else {
				EntityHelper.setValue(di, Constants.CHAR_SERVICEID, servicesOnPort.replace(serviceId, Constants.EMPTY));
			}
		}
		debug("cleanUpPortOnDisconnect - END");
	}
	
	/**
	 * @param diList
	 * @param serviceId
	 * @param diServiceMapping
	 * @throws ValidationException
	 */
	public static void cleanUpPortOnDisconnectService(List<DeviceInterface> diList, String serviceId, Map<DeviceInterface, String> diServiceMapping) throws ValidationException{
		debug("cleanUpPortOnDisconnectService - START");
		
		for(DeviceInterface di: diList)
		{
			HashMap<String,String> portCharMap = new HashMap<>();
			String servicesOnPort = diServiceMapping.get(di);
			
			debug(" :::: servicesOnPort :: "+servicesOnPort );
			debug(" :::: serviceId  ::"+serviceId);
			debug(" :::: di.name  ::"+di.getName());
			
			if(servicesOnPort.equalsIgnoreCase(serviceId)){
				debug(" If cleanUpPortOnDisconnectService");
				portCharMap.put(Constants.CHAR_OPERATIONALSTATUS, Constants.EnumOperationalStatus.Spare.toString());
				portCharMap.put(Constants.CHAR_RESERVATIONID, Constants.EMPTY);
				portCharMap.put(Constants.CHAR_SERVICEID, Constants.EMPTY);
			} else {
				debug("Else cleanUpPortOnDisconnectService");
				String removeServiceIdFromList = servicesOnPort.replace(serviceId, Constants.EMPTY);
				String removeExtraComma = removeServiceIdFromList.replace(Constants.COMMA+Constants.COMMA, Constants.COMMA);
				portCharMap.put(Constants.CHAR_RESERVATIONID, Constants.EMPTY);
				portCharMap.put(Constants.CHAR_SERVICEID, removeExtraComma);
			}
			UimHelper.setEntityChars(di, portCharMap);
		}
		debug("cleanUpPortOnDisconnectService - END");
	}
	
		
	/**
	 * This method will retrieve the PTPs associated with the port.
	 * In case of copper there should be only 1 PTP.
	 * In case of Fibre there can be 1 or upto 2 PTP.
	 * @param port
	 * @return
	 * @throws ValidationException
	 */
	public static List<PipeTerminationPoint> getRelatedPTP(DeviceInterface port) throws ValidationException{
		debug("getRelatedPTP - START, Port Id: "+port.getId());
		AssignmentManager assignmentManager = PersistenceHelper.makeAssignmentManager();
		AssignmentSearchCrteria criteria = assignmentManager.makeAssignmentSearchCrteria();
		criteria.setResource(port);
		criteria.setResourceClass(DeviceInterface.class);
		criteria.setConsumerType(PipeTerminationPoint.class);
		
		Collection<Assignment> assignments = assignmentManager.findAssignment(criteria);
		List<PipeTerminationPoint> ptpList = new ArrayList<>(2);
		for (Assignment assignment : assignments) {
			if (!(assignment.getConsumer() instanceof PipeTerminationPoint)){
				continue;
			} 
			ptpList.add((PipeTerminationPoint) assignment.getConsumer()); //Found the related PTP
			//break; for fiber ports having dual PTP assigned
		}
		debug("getRelatedPTP - END, PTP Found: "+ptpList.size());
		return ptpList;
	}
	
		
	/**
	 * @author rpamuru
	 * @param deviceInterfaceSpec
	 * @return
	 * @throws ValidationException 
	 */
	public static String getDeviceTypeFromPort(DeviceInterface di) throws ValidationException{
		debug("getDeviceTypeFromPort - START");
		if(log.isDebugEnabled()) {
			log.debug("", "deviceInterfaceSpec: "+di);
		}
		Finder finder = null;
		String parentSpec= null;
		try {
			finder = PersistenceHelper.makeFinder();
			String parentDevice = UimHelper.getEntityCharValue(di, Constants.CHAR_PARENTDEVICE);
			if(log.isDebugEnabled()) {
				log.debug("", "parentDevice: "+parentDevice);
			}
			Collection<LogicalDevice> ldColl = finder.findByName(LogicalDevice.class, parentDevice);
			if(!Utils.isEmpty(ldColl)) {
				LogicalDevice ld = ldColl.iterator().next();
				parentSpec= ld.getSpecification().getName();
			}
			if(log.isDebugEnabled()) {
				log.debug("", "parentSpec: "+parentSpec);
			}
		} catch(Exception e) {
			throw new ValidationException(e);
		} finally {
			if(finder!=null) {
				finder.close();
			}
		}
		debug("getDeviceTypeFromPort - END");
		return parentSpec;
	}
	
		
	/**
	 * This method is used to reserve the route (ports retrieved by FetchAutoRoute WS).
	 * @param portsInRoute
	 * @return
	 * @throws ValidationException
	 */
	public static String reserveRoute(List<DeviceInterface> portsInRoute, String reservedFor, String accountId, String orderId, String medium) throws ValidationException{
		debug("reserveRoute - START");
		
		ReservationManager resMgr = PersistenceHelper.makeReservationManager();	
		Reservation reservation = resMgr.makeReservation(CustomObjectReservation.class);
        reservation.setReservedFor(reservedFor);
		reservation.setReason("AccountId:"+accountId+ " , Order Id:" + orderId + " , Medium: " + medium);
		Date today = new Date();
		int portReservationExpiry = Integer.parseInt(UimHelper.getSystemConfigPropertyValue(Constants.CHAR_PORTRESERVATIONEXPIRY));
		reservation.setExpiry(Util.addDays(today, portReservationExpiry));
		reservation.setReservedForType(ReservedForType.SERVICE);
		reservation.setReservationType(ReservationType.LONGTERM);
		long reservationNo = resMgr.reserveResource(portsInRoute, reservation);
		debug("reserveRoute - END");
		return String.valueOf(reservationNo);
	}
	
		
	/**
	 * This method will unreserve the route based on reservation number.
	 * @param reservationNumber
	 * @throws ValidationException
	 */
	public static void unreserveRoute(String reservationNumber) throws ValidationException{
		debug("unreserveRoute - START");
		List<DeviceInterface> diList = getReservedPorts(reservationNumber);
		debug("Ports in Route: "+ diList.size() + " which will be unreserved.");
		if(!diList.isEmpty()){
			boolean isReservationToClear = true;
			//code change for not to clear reservation if service/subscriber ports are associated to any services.
			for(DeviceInterface di: diList) {
				
				if(Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) || Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())
						|| Constants.SPEC_DP_PORT.equals(di.getSpecification().getName())) {					
					List<Persistent> list = getAssignedEntities(di);
					if(!Utils.isEmpty(list)) {
						debug("setting reservation not to clear as downlink/virtual/DP port "+di.getId()+" is already associated to service");
						isReservationToClear = false;
						break;
					}
				} 
			}
			if(isReservationToClear) {
				for(DeviceInterface di: diList){
					di=di.refresh();
					EntityHelper.setValue(di, Constants.CHAR_RESERVATIONID, Constants.EMPTY);
					debug("Port Id: " + di.getId() + ", Name: " + di.getName() + " unreserved.");
					di.setDescription(null);
				}
			}		
		}		
		debug("unreserveRoute - END");
	}
	
	/**
	 * This method will return the list of ServiceDAO assigned to the DeviceInterface.
	 * @param resource
	 * @return
	 */
	public static List<Persistent> getAssignedEntities(ConsumableResource resource) {
		debug(log,"getAssignedEntities start ");
		AssignmentManager manager = PersistenceHelper.makeAssignmentManager();
		List<Persistent> list = new ArrayList<>();

		AssignmentSearchCrteria assignmentSearchCritieria = manager.makeAssignmentSearchCrteria();
		assignmentSearchCritieria.setResource(resource);
		//
		assignmentSearchCritieria.setConsumerType(ServiceDAO.class);
		Collection<AssignmentState> assignStatusList=new ArrayList<AssignmentState>();
		assignStatusList.add(AssignmentState.PENDING_ASSIGN);
		assignStatusList.add(AssignmentState.ASSIGNED);		
		assignmentSearchCritieria.setAssignmentStates(assignStatusList);
		Collection<oracle.communications.inventory.api.entity.common.Assignment> assignments = null;
		try {
			assignments = manager.findAssignment(assignmentSearchCritieria);
		} catch (ValidationException e) {
			log.error("", e);
		}
		debug(log,"Assignment Size ", assignments.size());
		if (!Utils.isEmpty(assignments)) {
			debug(log,"total assignments connected ", assignments.size(), " on " , resource.getId());
			for (oracle.communications.inventory.api.entity.common.Assignment assignment : assignments) {
				debug(log,"assignment.getConsumer()", assignment.getConsumer());
				if (assignment.getConsumer() instanceof ServiceDAO) {
					list.add(assignment.getConsumer());					
				}
			}
		}
		debug(log,"getAssignedEntities() end");
		return list;
	}
	
			
	/**
	 * This method will retrieve the splitter IN port from OUT port.
	 * @param splitterOutletPort
	 * @return
	 */
	public static DeviceInterface getSplitterInletPort(DeviceInterface splitterOutletPort) throws ValidationException{
		debug("getSplitterInletPort - START");
		LogicalDevice splitter = splitterOutletPort.getParentLogicalDevice();
		debug("Splitter Name: " + splitter.getName());
		//Assuming first port will always be the IN port
		DeviceInterface splitterInletPort = null;		
		List<DeviceInterface> diList = splitter.getDeviceInterfaces();
		for (DeviceInterface di : diList) {
			if(Constants.SPEC_INLET_PORT.equalsIgnoreCase(di.getSpecification().getName())) {				
				splitterInletPort=di;
				debug("Splitter Inlet Port: Id=" + splitterInletPort.getId() + ", Name=" + splitterInletPort.getName());
				break;
			}
		}
		if(splitterInletPort==null) {
			throw new ValidationException("InletPort not found for the device "+splitter.getName());
		}
		debug("getSplitterInletPort - END");
		return splitterInletPort;
	}
	
		
	/**
	 * Reserve List of ports. 
	 * 
	 * @param portsList
	 * @param reservedFor
	 * @param reason
	 * @return
	 * @throws ValidationException
	 */
	public static String reserveListOfPorts(List<DeviceInterface> portsList, String reservedFor, String reason) throws ValidationException{
		debug("reserveRoute - START");
		
		ReservationManager resMgr = PersistenceHelper.makeReservationManager();	
		Reservation reservation = resMgr.makeReservation(CustomObjectReservation.class);
        reservation.setReservedFor(reservedFor);
		reservation.setReason(reason);
		Date today = new Date();
		int portReservationExpiry = Integer.parseInt(UimHelper.getSystemConfigPropertyValue(Constants.CHAR_PORTRESERVATIONEXPIRY));
		reservation.setExpiry(Util.addDays(today, portReservationExpiry));
		reservation.setReservedForType(ReservedForType.CUSTOMER);
		reservation.setReservationType(ReservationType.LONGTERM);
		long reservationNo = resMgr.reserveResource(portsList, reservation);
		
		debug("reserveRoute - END");
		return String.valueOf(reservationNo);
	}
	
	
	
	public static String getDeviceNameWithoutSite(String deviceName) {
		debug("getDeviceNameWithoutSite - START, deviceName: " + deviceName);
		String deviceNameWithoutSite = deviceName.substring(0,deviceName.lastIndexOf(Constants.DELIMITER_PIPE_SEPARATOR));
		debug("getDeviceNameWithoutSite - END, deviceName: " + deviceNameWithoutSite);
		return deviceNameWithoutSite;
	}
	
		
	/**
	 * To check the CardType is GPON or not.
	 * @param cardType
	 * @return
	 * @throws ValidationException
	 */
	public static boolean isGPONCard(String cardType) throws ValidationException {
		debug("isFiberCard() CardType : " + cardType);
		Map<String, String> charMap = new HashMap<>();
		charMap.put("Technology", Constants.GPON);
		List<CustomObject> cardTypes = EntityHelper.findCustomObjects(cardType, Constants.SPEC_CARDTYPE, charMap, null);
		if (!cardTypes.isEmpty()) {
			debug("Card type is GPON");
			return true;
		} else {
			debug("Card type is not GPON");
			return false;
		}
	}
	
	/**
	 * Update Ports During New Install Operation at afterComplete stage.
	 * @param cfsServiceId
	 * @param dpPort
	 * @param tbPort
	 * @param config
	 * @param reservationId
	 * @param associatedParentBi
	 * @throws ValidationException
	 */
	public static void updatePortsDuringNewInstall(String cfsServiceId, DeviceInterface dpPort, DeviceInterface tbPort,
			ServiceConfigurationVersion config, String reservationId, BusinessInteraction associatedParentBi) throws ValidationException {
		debug(log,"updatePortsDuringNewInstall- START");
		
		String reservedId = null;
		boolean isRedeemReservationRequired = false;
		List<DeviceInterface> portList = null;
		
		// Update the dpPort
		if(dpPort != null) {
			debug(log,"dpPort : " + dpPort.getName());
			reservedId = UimHelper.getEntityCharValue(dpPort, Constants.RESERVATION_ID);
			String dpPortServiceId = UimHelper.getEntityCharValue(dpPort, Constants.SERVICE_ID);
			if(!Utils.checkBlank(reservedId)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.RESERVATION_ID, reservedId);
				portList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS, null, null);
				if(Utils.isEmpty(portList)) {
					throw new ValidationException("No ports found with reservation id: "+reservedId);
				}
				debug(log, "Total ports found for the Reservation Id ",reservedId," : ",portList.size());
				//new service creation
				for(DeviceInterface di:portList) {
					debug(log, "Setting serviceId for the reserved port ", di.getId()," ",di.getSpecification().getName()," ",di.getName());
					if(Utils.checkBlank(dpPortServiceId)
							|| (!Utils.checkBlank(dpPortServiceId) && dpPortServiceId.indexOf("#") > -1)) {
						EntityHelper.setValue(di, Constants.SERVICE_ID, cfsServiceId);
						EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
						EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					} else if (!Utils.checkBlank(dpPortServiceId) && dpPortServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) == -1
							&& dpPortServiceId.indexOf("#") == -1) { // for combination of orders
						EntityHelper.setValue(di, Constants.SERVICE_ID, dpPortServiceId+Constants.DELIMITER_PIPE_SEPARATOR+cfsServiceId);
						EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
						EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					}
					String portReserId = UimHelper.getEntityCharValue(di, Constants.RESERVATION_ID);
					String portServId = UimHelper.getEntityCharValue(di, Constants.RESERVATION_ID);
					debug(log, "Port ", di.getId()," ",di.getName()," ReservationId ",portReserId, " portServId ",portServId);
				}
				isRedeemReservationRequired=true;
			} else if(!Utils.checkBlank(dpPortServiceId)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.SERVICE_ID, dpPortServiceId);
				if(!Utils.isBlank(dpPortServiceId) && dpPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
					portList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS, null, null);
				}else {
					portList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS, null, null);
				}
				
				if(Utils.isEmpty(portList)) {
					throw new ValidationException("No ports found with DP port service Id: "+dpPortServiceId);
				}
				debug(log, "Total ports found for the DP port service Id ",dpPortServiceId," : ",portList.size());
				for(DeviceInterface di:portList) {
					if (!dpPortServiceId.contains(cfsServiceId)) {
						EntityHelper.setValue(di, Constants.SERVICE_ID, dpPortServiceId + Constants.DELIMITER_PIPE_SEPARATOR + cfsServiceId);
					}
					EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
				}
			}
		} else if(tbPort != null) {
			debug(log,"tbPort : " + tbPort.getName());
			reservedId = UimHelper.getEntityCharValue(tbPort, Constants.RESERVATION_ID);
			String tbPortServiceId = UimHelper.getEntityCharValue(tbPort, Constants.SERVICE_ID);
			if(!Utils.checkBlank(reservedId)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.RESERVATION_ID, reservedId);
				portList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS, null, null);
				if(Utils.isEmpty(portList)) {
					throw new ValidationException("No ports found with reservation id: "+reservedId);
				}
				debug(log, "Total ports found for the Reservation Id ",reservedId," : ",portList.size());
				//new service creation
				for(DeviceInterface di:portList) {
					debug(log, "Setting serviceId for the reserved port ", di.getId()," ",di.getSpecification().getName()," ",di.getName());
					if(Utils.checkBlank(tbPortServiceId)) {
						EntityHelper.setValue(di, Constants.SERVICE_ID, cfsServiceId);
						EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
						EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					} else if (!Utils.checkBlank(tbPortServiceId) && tbPortServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) == -1) { // for combination of orders
						EntityHelper.setValue(di, Constants.SERVICE_ID, tbPortServiceId+Constants.DELIMITER_PIPE_SEPARATOR+cfsServiceId);
						EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
						EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					}
					String portReserId = UimHelper.getEntityCharValue(di, Constants.RESERVATION_ID);
					String portServId = UimHelper.getEntityCharValue(di, Constants.RESERVATION_ID);
					debug(log, "Port ", di.getId()," ",di.getName()," ReservationId ",portReserId, " portServId ",portServId);
				}
				isRedeemReservationRequired=true;
			} else if(!Utils.checkBlank(tbPortServiceId)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.SERVICE_ID, tbPortServiceId);
				portList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS, null, null);
				if(Utils.isEmpty(portList)) {
					throw new ValidationException("No ports found with TB port service Id: "+tbPortServiceId);
				}
				debug(log, "Total ports found for the TB port service Id ",tbPortServiceId," : ",portList.size());
				for(DeviceInterface di:portList) {
					if (!tbPortServiceId.contains(cfsServiceId)) {
						EntityHelper.setValue(di, Constants.SERVICE_ID, tbPortServiceId + Constants.DELIMITER_PIPE_SEPARATOR +cfsServiceId);
					}
					EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
				}
			}
		}
		
		// Redeem Reservation.
		try {
			if(!Utils.isBlank(reservedId) && isRedeemReservationRequired) {
				UimHelper.redeemRouteReservation(reservedId);
			}
		} catch (Exception e) {
			log.validationError("", "Error on removing reservation "+reservedId+"  "+e.getMessage());
		}
		
		// Data circuit services with no reservation.
		String rfsServiceSpec = config.getService().getSpecification().getName();
		String medium = UimHelper.getConfigItemCharValue(config, Constants.PARAM_PROPERTIES, Constants.PARAM_MEDIUM);
		if (Constants.FIBER.equals(medium) && Utils.isBlank(reservationId)
				&& (rfsServiceSpec.equals(Constants.SPEC_DIA_RFS) || rfsServiceSpec.equals(Constants.SPEC_IPLC_RFS) ||
						rfsServiceSpec.equals(Constants.SPEC_SIPTRUNK_RFS) || rfsServiceSpec.equals(Constants.SPEC_IPVPN_RFS) ||
						rfsServiceSpec.equals(Constants.SPEC_ISDN_RFS) || rfsServiceSpec.equals(Constants.SPEC_EPL_RFS))) {
			debug(log, "New Install - DataCircuit Services START");
			List<ServiceConfigurationItem> rfsServiceConfigItemsList = config.getConfigItems();
			for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
				if (serviceConfigItem != null && serviceConfigItem.getName() != null) {
					String serviceConfigItemName = serviceConfigItem.getName();
					debug(log, "ConfigItemName : ", serviceConfigItemName);
					Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem
							.getAssignmentsMap();
					if (serviceConfigItemName.equals(Constants.CI_SERVICE_PORT)) {
						Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
						Iterator<ConsumableResource> consResIter = consResSet.iterator();
						while (consResIter.hasNext()) {
							DeviceInterface port = (DeviceInterface) consResIter.next();
							AssignmentState consResAssignState = resourceAssignmentMap.get(port);
							debug(log, "Service port: ", port.getId(), " ", port.getSpecification().getName(), " ",
									port.getName(), " state: ", consResAssignState);
							if (port.getSpecification().getName().equals(Constants.SPEC_VIRTUAL_PORT)) {
								String virtualPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
								if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
									setServiceIdForAssignedDownlinkPort(port, cfsServiceId, virtualPortServiceId, null,
											rfsServiceSpec);
								}
							}
						}
					} // service port
				} // configItem not null
			} // Loop configItems
			debug(log, "New Install - DataCircuit Services END");
		} // fiber-data circuit services
		
		debug(log,"updatePortsDuringNewInstall- END");
		
	}
	
	/**
	 * To update the ServiceId and ResercationId attributes on Ports.
	 * @param rfsServiceAction
	 * @param cfsServiceSpec
	 * @param cfsServiceId
	 * @param medium
	 * @param reservationId
	 * @param unassignedDpPort
	 * @param dpPort
	 * @param unassignedTbPort
	 * @param tbPort
	 * @param unassignedDownlinkPort
	 * @param serviceDownlinkPort
	 * @param unassignedComboDownlinkPort
	 * @param comboDownlinkPort
	 * @param unassignedVirtualPort
	 * @param virtualPort
	 * @throws ValidationException
	 */
	public static void updatePortsDuringPortChange(String rfsServiceAction, String cfsServiceSpec, String cfsServiceId,
			String medium, String reservationId, DeviceInterface unassignedDpPort, DeviceInterface dpPort,
			DeviceInterface unassignedTbPort, DeviceInterface tbPort, DeviceInterface unassignedDownlinkPort,
			DeviceInterface serviceDownlinkPort, DeviceInterface unassignedComboPort,
			DeviceInterface comboPort, DeviceInterface unassignedVirtualPort, DeviceInterface virtualPort)
			throws ValidationException {
		debug("updatePortsDuringPortChange- START");

		DeviceInterface oldServicePort = null;
		DeviceInterface oldComboPort = null;
		DeviceInterface newDpPort = null;
		DeviceInterface newServicePort = null;
		DeviceInterface newComboPort = null;
		
		// Same DP case
		if(unassignedDpPort==null && dpPort!=null) {
			debug(log, "No change in DP Port");
		}
		
		if(rfsServiceAction.equals(Constants.SA_DISCONNECT)) {
			debug(log, "Disconnect - Reset ServiceId Starts for the Service "+cfsServiceId+ " Medium "+medium);
			
			if (Constants.COPPER.equals(medium)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.SERVICE_ID, cfsServiceId);
				List<DeviceInterface> cfsServicePortList = null;
				if(!Utils.isBlank(cfsServiceId) && cfsServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
					cfsServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
							CriteriaOperator.EQUALS, null, null);
				}else {
					cfsServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
							CriteriaOperator.CONTAINS, null, null);
				}
				
				if (!Utils.isEmpty(cfsServicePortList)) {
					debug(log, "Total ports found with CFS Service Id : " + cfsServiceId + " is "
							+ cfsServicePortList.size());
					String dpLabel = getDPLabel(cfsServicePortList);
					for (DeviceInterface di : cfsServicePortList) {
						String diServiceId = di.getCharacteristicMap().get(Constants.SERVICE_ID).getValue();
						if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName())
								|| Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())
								|| Constants.SPEC_TB_PORT.equals(di.getSpecification().getName())) {
							setServiceIdForUnassignedDownlinkPort(di, cfsServiceId, diServiceId);
						} else {
							setServiceIdForUnassignedDeviceInterfaces(di, cfsServiceId, diServiceId, dpLabel, Constants.INTACT);
						}
					}
				}
				
			} else {
				// TODO For other medium.			
			}
			debug(log, "Disconnect - Reset ServiceId END for the Service "+cfsServiceId);
		} else {
		
			// Get service Id from Unassigned DP
			if (unassignedDpPort != null) {
				debug(log, "Unassigned DP port found");
				String unassignedDPLabel = unassignedDpPort.getLogicalDevice().getName()+"#"+unassignedDpPort.getName();
				String unassignedDPPortServiceId = UimHelper.getEntityCharValue(unassignedDpPort, Constants.SERVICE_ID);
				debug(log, "Unassigned DP port serviceId : "+unassignedDPPortServiceId);
				if (!Utils.checkBlank(unassignedDPPortServiceId)) {
					Map<String, String> charMap = new HashMap<>();
					charMap.put(Constants.SERVICE_ID, unassignedDPPortServiceId);
					List<DeviceInterface> unassignedDPPortList = null;
					if(!Utils.isBlank(unassignedDPPortServiceId) && unassignedDPPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
						unassignedDPPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS,
								null, null);
					}else {
						unassignedDPPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS,
								null, null);
					}
					
					if (!Utils.isEmpty(unassignedDPPortList)) {
						debug(log, "Total DP ports found with Service Id : " + unassignedDPPortServiceId + " is "
								+ unassignedDPPortList.size());
						for (DeviceInterface di : unassignedDPPortList) {
							if (!Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) 
									&& !Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
								setServiceIdForUnassignedDeviceInterfaces(di, cfsServiceId, unassignedDPPortServiceId, unassignedDPLabel, Constants.INTACT);
							}
						}
					}
				}
			}
			
			// Set ServiceId for Reserved ports [ Downlink Port, Virtual Port, MDF, CAB, DP ]
			if(!Utils.checkBlank(reservationId)) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.RESERVATION_ID, reservationId);
				List<DeviceInterface> reservedPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS, null, null);
				if(!Utils.isEmpty(reservedPortList)) {
					debug(log, "Total Ports found with Reservation Id : ",reservationId, " is ",reservedPortList.size());
					for(DeviceInterface di:reservedPortList) {
						String portServiceId = UimHelper.getEntityCharValue(di, Constants.SERVICE_ID);
						setServiceIdForReservedPorts(di, cfsServiceId, portServiceId);
					}
				}
			}
			
			// Process Second Order from same/different Business Interaction.
			if (dpPort != null) {
				String dpReservationId = UimHelper.getEntityCharValue(dpPort, Constants.RESERVATION_ID);
				String dpPortServiceId = UimHelper.getEntityCharValue(dpPort, Constants.SERVICE_ID);
				if (!Utils.isBlank(dpPortServiceId) && !dpPortServiceId.contains(cfsServiceId)) {
					debug(log, "DP Reservation Id ", dpReservationId, "DP Service Id ", dpPortServiceId);
					if (Utils.isBlank(dpReservationId)) {
						debug(log, "Second Order DP port serviceId : " + dpPortServiceId);
						if (dpPort != null && unassignedDpPort != null && dpPort != unassignedDpPort) {
							Map<String, String> charMap = new HashMap<>();
							charMap.put(Constants.SERVICE_ID, dpPortServiceId);
							List<DeviceInterface> dpPortDIList = null;
							
							if(!Utils.isBlank(dpPortServiceId) && dpPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
								dpPortDIList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.EQUALS, null, null);
							}else {
								dpPortDIList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.CONTAINS, null, null);
							}
							if (!Utils.isEmpty(dpPortDIList)) {
								debug(log, "Total DP ports found with Service Id : " + dpPortServiceId + " is "
										+ dpPortDIList.size());
								for (DeviceInterface di : dpPortDIList) {
									if (!Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) 
											&& !Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
										setServiceIdForAssignedDeviceInterfaces(di, cfsServiceId, dpPortServiceId);
									}
								}
							}
						}
					}
				} else {
					debug(log, "DP port already contains CFS serviceId : " + cfsServiceId);
				}
			}
			
			// Set service Id for Unassigned Downlink Port/ComboPort/VirtualPort.
			// Downlink/ComboPort/VirtualPort separately handled in case of technology change.
			String unassignedDownlinkPortServiceId = null;
			if (unassignedDownlinkPort != null) {
				debug(log, "Unassigned DownlinkPort port found");
				unassignedDownlinkPortServiceId = UimHelper.getEntityCharValue(unassignedDownlinkPort, Constants.SERVICE_ID);
				debug(log, "Unassigned DownlinkPort port serviceId : "+unassignedDownlinkPortServiceId);
				if (unassignedDownlinkPortServiceId.contains(cfsServiceId)) {
					if (!Utils.checkBlank(unassignedDownlinkPortServiceId)) {
						Map<String, String> charMap = new HashMap<>();
						charMap.put(Constants.SERVICE_ID, cfsServiceId);
						List<DeviceInterface> unassignedDownlinkPortList = null;
						if(!Utils.isBlank(cfsServiceId) && cfsServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
							unassignedDownlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap,
									null, CriteriaOperator.EQUALS, null, null);
						}else {
							unassignedDownlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
									CriteriaOperator.CONTAINS, null, null);
						}
						
						if (!Utils.isEmpty(unassignedDownlinkPortList)) {
							debug(log, "Total unassigned Downlink ports found with Service id: "
									+ unassignedDownlinkPortServiceId + " is " + unassignedDownlinkPortList.size());
							for (DeviceInterface di : unassignedDownlinkPortList) {
								if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName())
										|| Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
									AssignmentState assignState = ConsumerUtils.getAssignmentState(di);
									if (assignState.equals(AssignmentState.UNASSIGNED)) {
										if (rfsServiceAction.equals(Constants.SA_CHANGETECHNOLOGY)
												&& (cfsServiceSpec.equals(Constants.SPEC_FIXEDVOICE_CFS)
														|| cfsServiceSpec.equals(Constants.SPEC_BROADBAND_CFS))) {
											EntityHelper.setValue(di, Constants.SERVICE_ID, Constants.EMPTY);
											EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.SPARE);
										} else {
											setServiceIdForUnassignedDownlinkPort(di, cfsServiceId,
													unassignedDownlinkPortServiceId);
										}
									} // unassigned port
								}
							}
						}
					}
				} else {
					debug(log, "unassignedDownlinkPort "+unassignedDownlinkPort.getName() +" does not contain cfsServceId "+cfsServiceId);
				}
			}
			
			// Required only in case of Combo Port change without ServicePort change.
			if (cfsServiceSpec.equals(Constants.SPEC_DIA_CFS) || cfsServiceSpec.equals(Constants.SPEC_IPVPN_CFS)) {

				// Set service Id for Unassigned ComboPort/VirtualPort.
				// ComboPort/VirtualPort separately handled in case of technology change.
				String unassignedComboPortServiceId = null;
				if (unassignedComboPort != null) {
					debug(log, "Unassigned Combo Port port found");
					unassignedComboPortServiceId = UimHelper.getEntityCharValue(unassignedComboPort,
							Constants.SERVICE_ID);
					debug(log, "Unassigned Combo Port port serviceId : " + unassignedComboPortServiceId);
					if (!Utils.isBlank(unassignedComboPortServiceId) && unassignedComboPortServiceId.contains(cfsServiceId)) {
						if (!Utils.checkBlank(unassignedComboPortServiceId)) {
							Map<String, String> charMap = new HashMap<>();
							charMap.put(Constants.SERVICE_ID, unassignedComboPortServiceId);
							List<DeviceInterface> unassignedComboPortList = null;
							
							if(!Utils.isBlank(unassignedComboPortServiceId) && unassignedComboPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
								unassignedComboPortList = DeviceHelper.findDIByNameCharSpec(null,
										charMap, null, CriteriaOperator.EQUALS, null, null);
							}else {
								unassignedComboPortList = DeviceHelper.findDIByNameCharSpec(null,
										charMap, null, CriteriaOperator.CONTAINS, null, null);
							}
							
							if (!Utils.isEmpty(unassignedComboPortList)) {
								debug(log, "Total unassigned Combo ports found with Service id: "
										+ unassignedComboPortServiceId + " is " + unassignedComboPortList.size());
								for (DeviceInterface di : unassignedComboPortList) {
									if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName())
											|| Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
										AssignmentState assignState = ConsumerUtils.getAssignmentState(di);
										if (assignState.equals(AssignmentState.UNASSIGNED)) {
											if (cfsServiceSpec.equals(Constants.SPEC_DIA_CFS)
													|| cfsServiceSpec.equals(Constants.SPEC_IPVPN_CFS)) {
												setServiceIdForUnassignedDownlinkPort(di, cfsServiceId, unassignedComboPortServiceId);
											}
										} // unassigned port
									}
								}
							}
						}
					} else {
						debug(log, "Unassigned Combo Port " + unassignedComboPort.getName()
								+ " does not contain cfsServceId " + cfsServiceId);
					}
				}
			} // DIA or IPVPN
			
			// Reserved Downlink ports covered already. 
			
			// Process Second Order from same/different Business Interaction.
			if (serviceDownlinkPort != null) {
				String downlinkPortReservationId = UimHelper.getEntityCharValue(serviceDownlinkPort, Constants.RESERVATION_ID);
				String downlinkPortPortServiceId = UimHelper.getEntityCharValue(serviceDownlinkPort, Constants.SERVICE_ID);
				if (!Utils.isBlank(downlinkPortPortServiceId) && !downlinkPortPortServiceId.contains(cfsServiceId)) {
					debug(log, "DownLink Port Reservation Id ", downlinkPortReservationId, "Downlink Port Service Id ", downlinkPortPortServiceId);
					if (Utils.isBlank(downlinkPortReservationId)) {
						debug(log, "Second Order DownLink port serviceId : " + downlinkPortPortServiceId);
						if (serviceDownlinkPort != null && unassignedDownlinkPort != null && serviceDownlinkPort != unassignedDownlinkPort) {
							Map<String, String> charMap = new HashMap<>();
							charMap.put(Constants.SERVICE_ID, downlinkPortPortServiceId);
							List<DeviceInterface> downlinkPortList = null;
							if(!Utils.isBlank(downlinkPortPortServiceId) && downlinkPortPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
								downlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.EQUALS, null, null);
							}else {
								downlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.CONTAINS, null, null);
							}
							
							
							if (!Utils.isEmpty(downlinkPortList)) {
								debug(log, "Total DownLink Port ports found with Service id: " + downlinkPortPortServiceId + " is "
										+ downlinkPortList.size());
								for (DeviceInterface di : downlinkPortList) {
									if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) ||
											Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
										AssignmentState assignState = ConsumerUtils.getAssignmentState(di);
										if (assignState.equals(AssignmentState.ASSIGNED)) {
											setServiceIdForAssignedDownlinkPort(di, cfsServiceId,
													downlinkPortPortServiceId, null, null);
										}
									}
								}
							}
						}
					}
				} else {
					debug(log, "Change Technology...Copy all service ids from unassigned downlink port.");
					if (!Utils.isBlank(downlinkPortPortServiceId)) {
						if (rfsServiceAction.equals(Constants.SA_CHANGETECHNOLOGY)
								&& (cfsServiceSpec.equals(Constants.SPEC_FIXEDVOICE_CFS)
										|| cfsServiceSpec.equals(Constants.SPEC_BROADBAND_CFS))
								&& !Utils.isBlank(unassignedDownlinkPortServiceId)
								&& unassignedDownlinkPortServiceId.contains(cfsServiceId)) {
	
							Map<String, String> charMap = new HashMap<>();
							charMap.put(Constants.SERVICE_ID, downlinkPortPortServiceId);
							List<DeviceInterface> downlinkPortList = null;
							if(!Utils.isBlank(downlinkPortPortServiceId) && downlinkPortPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
								downlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.EQUALS, null, null);
							}else {
								downlinkPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
										CriteriaOperator.CONTAINS, null, null);
							}
							
							
							if (!Utils.isEmpty(downlinkPortList)) {
								debug(log, "Total DownLink Port ports found with Service id " + downlinkPortPortServiceId + " is "
										+ downlinkPortList.size());
								for (DeviceInterface di : downlinkPortList) {
									if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) ||
											Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
										EntityHelper.setValue(serviceDownlinkPort, Constants.SERVICE_ID,
												unassignedDownlinkPortServiceId);
									}
								}
							}
						}
					}
				}
			}
			
			// Update the tbPort
			if(tbPort != null) {
				debug(log,"Updating tbPort ");
				String tbPortServiceId = UimHelper.getEntityCharValue(tbPort, Constants.SERVICE_ID);
				if(tbPortServiceId == null) {
					EntityHelper.setValue(tbPort, Constants.SERVICE_ID, cfsServiceId);
					EntityHelper.setValue(tbPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					EntityHelper.setValue(tbPort, Constants.RESERVATION_ID, Constants.EMPTY);
				}else if(tbPortServiceId.indexOf(',') > -1) {
					tbPortServiceId = tbPortServiceId.replaceAll(cfsServiceId, "");
					tbPortServiceId = tbPortServiceId.replace(",", "");
					EntityHelper.setValue(tbPort, Constants.SERVICE_ID, tbPortServiceId);
					EntityHelper.setValue(tbPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
				}
			}
			
			if(unassignedTbPort != null) {
				debug(log,"Updating unassignedTbPort ");
				String tbPortServiceId = UimHelper.getEntityCharValue(unassignedTbPort, Constants.SERVICE_ID);
				if(tbPortServiceId == null) {
					EntityHelper.setValue(unassignedTbPort, Constants.SERVICE_ID, cfsServiceId);
					EntityHelper.setValue(unassignedTbPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					EntityHelper.setValue(unassignedTbPort, Constants.RESERVATION_ID, Constants.EMPTY);
				}else if(tbPortServiceId.indexOf(',') > -1) {
					tbPortServiceId = tbPortServiceId.replaceAll(cfsServiceId, "");
					tbPortServiceId = tbPortServiceId.replace(",", "");
					EntityHelper.setValue(unassignedTbPort, Constants.SERVICE_ID, tbPortServiceId);
					EntityHelper.setValue(unassignedTbPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
				}else {
					EntityHelper.setValue(unassignedTbPort, Constants.SERVICE_ID, Constants.EMPTY);
					EntityHelper.setValue(unassignedTbPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.SPARE);
				}
			}
			
			// Update the virtualPort
			if(virtualPort != null) {
				debug(log,"Updating virtualPort ");
				String virtualPortServiceId = UimHelper.getEntityCharValue(virtualPort, Constants.SERVICE_ID);
				
				if(virtualPortServiceId == null) {
					EntityHelper.setValue(virtualPort, Constants.SERVICE_ID, cfsServiceId);
					EntityHelper.setValue(virtualPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
					EntityHelper.setValue(virtualPort, Constants.RESERVATION_ID, Constants.EMPTY);
				}else if(virtualPortServiceId.indexOf(',') > -1) {
					virtualPortServiceId = virtualPortServiceId.replaceAll(cfsServiceId, "");
					virtualPortServiceId = virtualPortServiceId.replace(",", "");
					EntityHelper.setValue(virtualPort, Constants.SERVICE_ID, virtualPortServiceId);
					EntityHelper.setValue(virtualPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
				}
			}
			
			// Update the unassignedVirtualPort
			if(unassignedVirtualPort != null) {
				debug(log,"Updating unassignedVirtualPort ");
				String unassignedVirtualPortServiceId = UimHelper.getEntityCharValue(unassignedVirtualPort, Constants.SERVICE_ID);
				if(unassignedVirtualPortServiceId != null) {
					EntityHelper.setValue(unassignedVirtualPort, Constants.SERVICE_ID, Constants.EMPTY);
					EntityHelper.setValue(unassignedVirtualPort, Constants.PARAM_OPERATIONAL_STATUS, Constants.SPARE);
				}
			}
		} // rfsServiceAction
		debug(log,"updatePortsDuringPortChange- END");
	}
	
	/**
	 * Prepare DP default label [parentLogicalDevice#PortName]
	 * @param cfsServicePortList
	 * @return
	 * @throws ValidationException
	 */
	private static String getDPLabel(List<DeviceInterface> cfsServicePortList) throws ValidationException {
		debug(log,"getDPLabel- END Service Count: " + cfsServicePortList.size());
		String dpLabel = null;
		for (DeviceInterface di : cfsServicePortList) {
			if (Constants.SPEC_DP_PORT.equals(di.getSpecification().getName())) {
				dpLabel = di.getParentLogicalDevice().getName()+"#"+di.getName();
				debug(log, "dpLabel : "+dpLabel);
				break;
			}
		}
		debug(log,"getDPLabel- END");
		return dpLabel;
	}

	// UNASSIGNED state
	private static void setServiceIdForUnassignedDeviceInterfaces(DeviceInterface di, String cfsServiceId,
			String portServiceId, String unassignedDPLabel, String operationalStatus) throws ValidationException {
		debug(log, "setServiceIdForUnassignedDeviceInterfaces() START Port:",di.getId()," ",di.getName());
		debug(log, "cfsServiceId ",cfsServiceId," unassignedDPPortServiceId ",portServiceId);
		if (portServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) > -1) {
			if (portServiceId.contains(cfsServiceId)) {
				portServiceId = portServiceId.replaceAll(cfsServiceId, Constants.EMPTY);
				portServiceId = portServiceId.replace(Constants.DELIMITER_PIPE_SEPARATOR, Constants.EMPTY);
				EntityHelper.setValue(di, Constants.SERVICE_ID, portServiceId);
				EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
			}
		} else {
			// Single Order Disconnect
			if (!Utils.isBlank(portServiceId) && portServiceId.contains(cfsServiceId)) {
				portServiceId = portServiceId.replaceAll(cfsServiceId, Constants.EMPTY);
			}
			// If it is empty then set subscriber default portName.
			if (Utils.isBlank(portServiceId)) {
				if(Utils.isBlank(unassignedDPLabel)) {
					EntityHelper.setValue(di, Constants.SERVICE_ID, Constants.EMPTY);
				} else {
					EntityHelper.setValue(di, Constants.SERVICE_ID, unassignedDPLabel);
				}
				EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, operationalStatus);
			}
		}
		debug(log, "setServiceIdForUnassignedDeviceInterfaces() END");
	}
	
	// UNASSIGNED state
	private static void setServiceIdForUnassignedDownlinkPort(DeviceInterface di, String cfsServiceId,
			String portServiceId) throws ValidationException {
		debug(log, "setServiceIdForUnassignedDownlinkPort() START Port:",di.getId()," ",di.getName());
		debug(log, "cfsServiceId ",cfsServiceId," unassignedDownlinkPortServiceId ",portServiceId);
		if (portServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) > -1) {
			if(portServiceId.contains(cfsServiceId)) {
				portServiceId = portServiceId.replaceAll(cfsServiceId,
						Constants.EMPTY);
				portServiceId = portServiceId.replace(Constants.DELIMITER_PIPE_SEPARATOR,
						Constants.EMPTY);
				EntityHelper.setValue(di, Constants.SERVICE_ID, portServiceId);
				EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
			}
		} else {
			// Single Order Disconnect
			if (!Utils.isBlank(portServiceId)
					&& portServiceId.contains(cfsServiceId)) {
				portServiceId = portServiceId.replaceAll(cfsServiceId,
						Constants.EMPTY);
			}
			if (Utils.isBlank(portServiceId)) {
				EntityHelper.setValue(di, Constants.SERVICE_ID, Constants.EMPTY);
				EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.SPARE);
			}
		}
		debug(log, "setServiceIdForUnassignedDownlinkPort() END");
	}
	
	/**
	 * To set or clear the service Id on the secondary service FixedVoice/Broadband.   
	 * @param di
	 * @param cfsServiceId
	 * @param portServiceId
	 * @param rfsServiceSpec 
	 * @throws ValidationException
	 */
	private static void setServiceIdForSecondaryService(DeviceInterface di, String cfsServiceId, String portServiceId, String rfsServiceSpec)
			throws ValidationException {
		debug(log, "setServiceIdForSecondaryService() - START ","cfsServiceId ", cfsServiceId, " portServiceId ",portServiceId);
		debug(log, di.getId(), " ",di.getSpecification().getName(), " ",di.getName());
		
		if (!Utils.isBlank(portServiceId)) {
			if(isActiveDevice(di,Constants.SPEC_ISAM) && rfsServiceSpec!=null 
					&& (rfsServiceSpec.equals(Constants.SPEC_FIXEDVOICE_RFS)
					|| rfsServiceSpec.equals(Constants.SPEC_BROADBAND_RFS))) {
				String secondaryServiceId = null;
				if (portServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) > -1) {
					String serviceIdArray[] = portServiceId.split("\\|");
					
					if (!serviceIdArray[0].equals(cfsServiceId)) {
						secondaryServiceId = serviceIdArray[0];
					} else {
						secondaryServiceId = serviceIdArray[1];
					}
				} else {
					secondaryServiceId=portServiceId;
				}
				debug(log, "SecondaryService Id ", secondaryServiceId);
				if (!Utils.checkBlank(secondaryServiceId)) {
					Map<String, String> charMap = new HashMap<>();
					charMap.put(Constants.SERVICE_ID, secondaryServiceId);
					List<DeviceInterface> secondaryServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap,
							null, CriteriaOperator.CONTAINS, null, null);
					debug(log, "Total Ports found with for the secondary service Id : ", secondaryServiceId, " is ",
							secondaryServicePortList.size());
					if (!Utils.isEmpty(secondaryServicePortList)) {
						for (DeviceInterface secondServiceDI : secondaryServicePortList) {
							if (Constants.SPEC_DOWNLINK_PORT.equals(secondServiceDI.getSpecification().getName())) {
								// Getting other DI.
								if (di.getId() != secondServiceDI.getId()) {
									AssignmentState diState = ConsumerUtils.getAssignmentState(secondServiceDI);
									debug(log, "Setting the value for other port on secondary service...");
									debug(log, "Secondary DI ", secondServiceDI.getId(), " ",
											secondServiceDI.getSpecification().getName(), " ",
											secondServiceDI.getName());
									String secondaryPortServiceId = UimHelper.getEntityCharValue(secondServiceDI,
											Constants.SERVICE_ID);
									debug(log, "secondaryPortServiceId ", secondaryPortServiceId);
									if (diState.equals(AssignmentState.UNASSIGNED)
											|| diState.equals(AssignmentState.PENDING_UNASSIGN)) {
										setServiceIdForUnassignedDownlinkPort(secondServiceDI, cfsServiceId,
												secondaryPortServiceId);
									} else if (diState.equals(AssignmentState.ASSIGNED)
											|| diState.equals(AssignmentState.PENDING_ASSIGN)) {
										setServiceIdForAssignedDownlinkPort(secondServiceDI, cfsServiceId,
												secondaryPortServiceId, portServiceId, rfsServiceSpec);
									}
								}
							}
						}
					}
				}
			} // ISAM
		}
		debug(log, "setServiceIdForSecondaryService() - END");
	}

	public static boolean isActiveDevice(DeviceInterface di, String activeDeviceSpec) throws ValidationException {
		debug(log, "isActiveDevice() START ",di.getId()," ",di.getName()," "," activeDeviceSpec ", activeDeviceSpec);
		if (di.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
			LogicalDeviceLogicalDeviceRel ldRel = di.getParentLogicalDevice().getParentLogicalDevice();
			LogicalDevice activeDevice = ldRel.getParentLogicalDevice();
			if (activeDevice.getSpecification().getName().equals(activeDeviceSpec)) {
				debug(log, "Active Device : " , activeDevice.getSpecification().getName());
				return true;
			} else {
				debug(log, "Active Device : " , activeDevice.getSpecification().getName());
				return false;
			}
		} else {
			throw new ValidationException("Device Interface specification must be Downlink_Port.");
		}
	}

	// UNASSIGNED state
	private static void setServiceIdForAssignedDeviceInterfaces(DeviceInterface di, String cfsServiceId, String portServiceId) throws ValidationException {
		debug(log, "setServiceIdForAssignedDeviceInterfaces() START Port:",di.getId()," ",di.getName());
		debug(log, "cfsServiceId ",cfsServiceId," assignedDPPortServiceId ",portServiceId);
		if (Utils.isBlank(portServiceId)
				|| (!Utils.isBlank(portServiceId) && portServiceId.contains("#"))) {
			EntityHelper.setValue(di, Constants.SERVICE_ID, cfsServiceId);
			EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
		} else if (!portServiceId.contains(cfsServiceId)) {
			portServiceId = portServiceId + Constants.DELIMITER_PIPE_SEPARATOR + cfsServiceId;
			EntityHelper.setValue(di, Constants.SERVICE_ID, portServiceId);
		}
		debug(log, "setServiceIdForAssignedDeviceInterfaces() END");
	}

	private static void setServiceIdForAssignedDownlinkPort(DeviceInterface di, String cfsServiceId,
			String portServiceId, String unassignedDownlinkPortServiceId, String rfsServiceSpec) throws ValidationException {
		debug(log, "setServiceIdForAssignedDownlinkPorts() START Port:",di.getId()," ",di.getName());
		debug(log, "cfsServiceId ",cfsServiceId," downlinkPortPortServiceId ",portServiceId);
		if(rfsServiceSpec!=null && (rfsServiceSpec.equals(Constants.SPEC_FIXEDVOICE_RFS)
				|| rfsServiceSpec.equals(Constants.SPEC_BROADBAND_RFS)) 
				&& !Utils.isBlank(unassignedDownlinkPortServiceId)
				&& unassignedDownlinkPortServiceId.contains(Constants.DELIMITER_PIPE_SEPARATOR)
				&& isActiveDevice(di,Constants.SPEC_ISAM)) {  
			EntityHelper.setValue(di, Constants.SERVICE_ID, unassignedDownlinkPortServiceId);
			EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
		} else if (Utils.isBlank(portServiceId)) {
			EntityHelper.setValue(di, Constants.SERVICE_ID, cfsServiceId);
			EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
		} else if (!portServiceId.contains(cfsServiceId)) {
			portServiceId = portServiceId + Constants.DELIMITER_PIPE_SEPARATOR + cfsServiceId;
			EntityHelper.setValue(di, Constants.SERVICE_ID, portServiceId);
		}
		debug(log, "setServiceIdForAssignedDownlinkPorts() END");
	}

	// Reserved Ports
	private static void setServiceIdForReservedPorts(DeviceInterface di, String cfsServiceId, String portServiceId) throws ValidationException {
		debug(log, "setServiceIdForReservedPorts() START ");
		debug(log, di.getId()," ",di.getSpecification().getName() , ":" ,di.getName(), " cfsServiceId ",cfsServiceId);
		if (Utils.isBlank(portServiceId) || (!Utils.isBlank(portServiceId) && portServiceId.indexOf("#") > -1)) {
			EntityHelper.setValue(di, Constants.SERVICE_ID, cfsServiceId);
			EntityHelper.setValue(di, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
		} else if (!portServiceId.contains(cfsServiceId)) {
			portServiceId = portServiceId + Constants.DELIMITER_PIPE_SEPARATOR + cfsServiceId;
			EntityHelper.setValue(di, Constants.SERVICE_ID, portServiceId);
		}
		// Reset ReservationId
		EntityHelper.setValue(di, Constants.RESERVATION_ID, Constants.EMPTY);
		debug(log, "setServiceIdForReservedPorts() END");
	}

	/**
	 * To get unassigned DP Port serviceId
	 * @param rfsServiceConfigItemsList
	 * @return
	 */
	private static String getUnassignedDPPortServiceId(List<ServiceConfigurationItem> rfsServiceConfigItemsList) {
		debug(log, "getUnassignedDPPortServiceId - start");
		String unassignedDPPortServiceId = null;
		for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
			if (serviceConfigItem != null && serviceConfigItem.getName() != null) {
				Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem.getAssignmentsMap();
				if (serviceConfigItem.getName().equals(Constants.CI_SUBSCRIBER_PORT)) {
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while (consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log, "Subscriber port ",port.getId()," ",port.getName()," state ",consResAssignState);
						if (port.getSpecification().getName().equals(Constants.SPEC_DP_PORT)
								&& consResAssignState.equals(AssignmentState.UNASSIGNED)) {
							unassignedDPPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
							break;
						}
					}
				}
			}
			if(!Utils.isBlank(unassignedDPPortServiceId)) {
				break;
			}
		} // loop config items.
		debug(log, "unassignedDPPortServiceId ",unassignedDPPortServiceId);
		debug(log, "getUnassignedDPPortServiceId - end");
		return unassignedDPPortServiceId;
	}

	/**
	 * To get unassigned Downlink Port serviceId
	 * @param rfsServiceConfigItemsList
	 * @return
	 */
	private static String getUnassignedDownlinkPortServiceId(List<ServiceConfigurationItem> rfsServiceConfigItemsList) {
		debug(log, "getUnassignedDownlinkPortServiceId - start");
		String unassignedDownlinkPortServiceId=null;
		for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
			if (serviceConfigItem.getName() != null
					&& serviceConfigItem.getName().equals(Constants.CI_SERVICE_PORT)) {
				Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem.getAssignmentsMap();
				Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
				Iterator<ConsumableResource> consResIter = consResSet.iterator();
				while (consResIter.hasNext()) {
					DeviceInterface port = (DeviceInterface) consResIter.next();
					AssignmentState consResAssignState = resourceAssignmentMap.get(port);
					debug(log, "Service/Combo port: ",port.getId()," ",port.getName()," state ",consResAssignState);
					if (port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)
							&& consResAssignState.equals(AssignmentState.UNASSIGNED)) {
						unassignedDownlinkPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
						break;
					}
				}
			}
			if(!Utils.isBlank(unassignedDownlinkPortServiceId)) {
				break;
			}
		} // loop configitems
		debug(log, "unassignedDownlinkPortServiceId ",unassignedDownlinkPortServiceId);
		debug(log, "getUnassignedDownlinkPortServiceId - end");
		return unassignedDownlinkPortServiceId;
	}

	private static void updateAssignedPassivePorts(String cfsServiceId, DeviceInterface assignedDPPort) throws ValidationException {
		debug(log, "updateAssignedPassivePorts() START assignedDPPort ",assignedDPPort.getId()," "+assignedDPPort.getName());
		String dpPortServiceId = UimHelper.getEntityCharValue(assignedDPPort, Constants.SERVICE_ID);
		if (!Utils.isBlank(dpPortServiceId)
				&& !dpPortServiceId.contains(cfsServiceId)) {
			debug(log, "DP port serviceId - existing : " + dpPortServiceId);
			Map<String, String> charMap = new HashMap<>();
			charMap.put(Constants.SERVICE_ID, dpPortServiceId);
			List<DeviceInterface> dpPortDIList = null;
			if(!Utils.isBlank(dpPortServiceId) && dpPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
				dpPortDIList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
						CriteriaOperator.EQUALS, null, null);
			}else {
				dpPortDIList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
						CriteriaOperator.CONTAINS, null, null);
			}
			
			
			if (!Utils.isEmpty(dpPortDIList)) {
				debug(log,
						"Total ports found with DP Service Id : " , dpPortServiceId , " is " , dpPortDIList.size());
				for (DeviceInterface di : dpPortDIList) {
					if (!Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName())
							&& !Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
						debug(log, "Set ",cfsServiceId," to ", di.getId()," "+di.getName());
						setServiceIdForAssignedDeviceInterfaces(di, cfsServiceId, dpPortServiceId);
					}
				}
			}
		} else {
			debug(log, "DP port already contains CFS serviceId : " , cfsServiceId);
		}
		debug(log, "updateAssignedPassivePorts() END");
	}

	private static void updateUnassignedPassivePorts(String cfsServiceId, DeviceInterface unassignedDPPort, String unassignedDPPortServiceId) throws ValidationException {
		debug(log, "updateUnassignedPassivePorts() START unassignedDpPort:",unassignedDPPort.getName());
		String unassignedDPLabel = unassignedDPPort.getLogicalDevice().getName()+"#"+unassignedDPPort.getName();
		debug(log, "Unassigned DP port serviceId : ",unassignedDPPort.getName());
		if (!Utils.checkBlank(unassignedDPPortServiceId)) {
			Map<String, String> charMap = new HashMap<>();
			charMap.put(Constants.SERVICE_ID, unassignedDPPortServiceId);
			List<DeviceInterface> unassignedDPPortList = null;
			if(!Utils.isBlank(unassignedDPPortServiceId) && unassignedDPPortServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
				unassignedDPPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS,
						null, null);
			}else {
				unassignedDPPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.CONTAINS,
						null, null);
			}
			
			if (!Utils.isEmpty(unassignedDPPortList)) {
				debug(log, "Total ports found with DP Service Id : " , unassignedDPPortServiceId , " is "
						, unassignedDPPortList.size());
				for (DeviceInterface di : unassignedDPPortList) {
					if (!Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName()) 
							&& !Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())) {
						debug(log, "Set ",cfsServiceId," to ", di.getId()," ",di.getSpecification().getName()," ",di.getName());
						setServiceIdForUnassignedDeviceInterfaces(di, cfsServiceId, unassignedDPPortServiceId, unassignedDPLabel, Constants.INTACT);
					}
				}
			}
		}
		debug(log, "updateUnassignedPassivePorts() END");
	}
	
	private static void updateDisconnectedDeviceInterfaces(String cfsServiceId, String medium) throws ValidationException {
		debug(log, "Disconnect - Reset ServiceId Starts for the Service " , cfsServiceId , " Medium " , medium);
		Map<String, String> charMap = new HashMap<>();
		charMap.put(Constants.SERVICE_ID, cfsServiceId);
		List<DeviceInterface> cfsServicePortList = null;
		
		if(!Utils.isBlank(cfsServiceId) && cfsServiceId.startsWith(Constants.MAPSERVICETYPE.get(Constants.SN_IPVPN))) {
			cfsServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
					CriteriaOperator.EQUALS, null, null);
		}else {
			cfsServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
					CriteriaOperator.CONTAINS, null, null);
		}
		
		String dpLabel = null;
		if (!Utils.isEmpty(cfsServicePortList)) {
			debug(log,
					"Total ports found with CFS Service Id : " , cfsServiceId , " is " , cfsServicePortList.size());
			if(medium.equals(Constants.COPPER)) {
				dpLabel = getDPLabel(cfsServicePortList);
			}
			for (DeviceInterface di : cfsServicePortList) {
				String diServiceId = di.getCharacteristicMap().get(Constants.SERVICE_ID).getValue();
				if (Constants.SPEC_DOWNLINK_PORT.equals(di.getSpecification().getName())
						|| Constants.SPEC_VIRTUAL_PORT.equals(di.getSpecification().getName())
						|| Constants.SPEC_TB_PORT.equals(di.getSpecification().getName())) {
					debug(log, "Set ",cfsServiceId," to ", di.getId()," ",di.getSpecification().getName()," ",di.getName());
					setServiceIdForUnassignedDownlinkPort(di, cfsServiceId, diServiceId);
				} else {
					debug(log, "Set ",cfsServiceId," to ", di.getId()," ",di.getSpecification().getName()," ",di.getName());
					setServiceIdForUnassignedDeviceInterfaces(di, cfsServiceId, diServiceId, dpLabel, Constants.INTACT);
				}
			}
		}
		debug(log, "Disconnect - Reset ServiceId END for the Service " + cfsServiceId);
	}
	

	/**
	 * Set ServiceId and Reset ReservationId for all the Reserved ports.
	 * @param reservationId
	 * @param cfsServiceId
	 * @param combinedServiceId 
	 * @throws ValidationException 
	 */
	private static void updateReservedPorts(String reservationId, String cfsServiceId) throws ValidationException {
		debug(log, "updateReservedPorts() - START");
		if(!Utils.checkBlank(reservationId)) {
			Map<String, String> charMap = new HashMap<>();
			charMap.put(Constants.RESERVATION_ID, reservationId);
			List<DeviceInterface> reservedPortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null, CriteriaOperator.EQUALS, null, null);
			debug(log, "Total Ports found with Reservation Id : ",reservationId, " is ",reservedPortList.size());
			if(!Utils.isEmpty(reservedPortList)) {
				for(DeviceInterface di:reservedPortList) {
					debug(log, "##### ", "Reserving port ",di.getId(), " ", di.getSpecification().getName(), " ", di.getName());
					String portServiceId = UimHelper.getEntityCharValue(di, Constants.SERVICE_ID);
					setServiceIdForReservedPorts(di, cfsServiceId, portServiceId);
				}
			}
			try {
				UimHelper.redeemRouteReservation(reservationId);
			} catch (Exception e) {
				log.validationError("", "Error on removing reservation "+e.getMessage());
			}
		}
		debug(log, "updateReservedPorts() - END");
	}
	
	/**
	 * Update ports - Set or clear the serviceId and clear the reservation id.
	 * @param cfsServiceId
	 * @param config
	 * @param reservationId
	 * @param associatedParentBi
	 * @param rfsServiceName
	 * @throws ValidationException
	 */
	public static void updatePorts(String cfsServiceId, ServiceConfigurationVersion config, String reservationId, 
			BusinessInteraction associatedParentBi, String rfsServiceName) throws ValidationException {
		debug(log, "updatePorts - START");
		
		String rfsServiceSpec = config.getService().getSpecification().getName();
		String rfsServiceAction = UimHelper.getConfigItemCharValue(config, Constants.PARAM_PROPERTIES, Constants.PARAM_SERVICE_ACTION); 
		debug(log,"cfsServiceId:" ,cfsServiceId," Service Spec:",rfsServiceSpec, "rfsServiceAction:", rfsServiceAction);
		String medium = UimHelper.getConfigItemCharValue(config, Constants.PARAM_PROPERTIES, Constants.PARAM_MEDIUM);
		List<ServiceConfigurationItem> rfsServiceConfigItemsList = config.getConfigItems();
		if (rfsServiceAction.equals(Constants.SA_DISCONNECT)) {
			updateDisconnectedDeviceInterfaces(cfsServiceId, medium);
		} else {
			String unassignedDPPortServiceId = getUnassignedDPPortServiceId(rfsServiceConfigItemsList);
			String unassignedDownlinkPortServiceId = getUnassignedDownlinkPortServiceId(rfsServiceConfigItemsList);
			
			for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
				if (serviceConfigItem != null && serviceConfigItem.getName() != null) {
					String serviceConfigItemName = serviceConfigItem.getName();
					debug(log, "ServiceConfigItemName CI: " , serviceConfigItemName);
					Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem
							.getAssignmentsMap();
					if (serviceConfigItemName.equals(Constants.CI_SUBSCRIBER_PORT)) {
						Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
						Iterator<ConsumableResource> consResIter = consResSet.iterator();
						while (consResIter.hasNext()) {
							DeviceInterface port = (DeviceInterface) consResIter.next();
							AssignmentState consResAssignState = resourceAssignmentMap.get(port);
							debug(log, "Subscriber port: " , port.getId() ," ",port.getName() , " state: " , consResAssignState);
							if (port.getSpecification().getName().equals(Constants.SPEC_DP_PORT)) {
								if (consResAssignState.equals(AssignmentState.UNASSIGNED)) {
									// Remove serviceId from DP, MDF, CABCABINET ports
									updateUnassignedPassivePorts(cfsServiceId, port, unassignedDPPortServiceId);
								} else if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
									// Set serviceId from DP, MDF and CABINET ports
									updateAssignedPassivePorts(cfsServiceId, port);
								}
							} else if (port.getSpecification().getName().equals(Constants.SPEC_TB_PORT)) {
								String tbPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
								if (consResAssignState.equals(AssignmentState.UNASSIGNED)) {
									setServiceIdForUnassignedDeviceInterfaces(port, cfsServiceId,tbPortServiceId, null, Constants.SPARE);
								} else if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
									setServiceIdForAssignedDeviceInterfaces(port, cfsServiceId, tbPortServiceId);
								}
							}
						}
					} else if (serviceConfigItemName.equals(Constants.CI_SERVICE_PORT)) {
						Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
						Iterator<ConsumableResource> consResIter = consResSet.iterator();
						while (consResIter.hasNext()) {
							DeviceInterface port = (DeviceInterface) consResIter.next();
							AssignmentState consResAssignState = resourceAssignmentMap.get(port);
							debug(log, "Service port: " , port.getId() ," ", port.getName() , " state: " , consResAssignState);
							if (port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
								if (consResAssignState.equals(AssignmentState.UNASSIGNED)) {
									setServiceIdForUnassignedDownlinkPort(port, cfsServiceId, unassignedDownlinkPortServiceId);
									setServiceIdForSecondaryService(port, cfsServiceId, unassignedDownlinkPortServiceId, rfsServiceSpec);
								} else if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
									String downlinkPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
									setServiceIdForAssignedDownlinkPort(port, cfsServiceId, downlinkPortServiceId,
											unassignedDownlinkPortServiceId, rfsServiceSpec);
									setServiceIdForSecondaryService(port, cfsServiceId, unassignedDownlinkPortServiceId, rfsServiceSpec);
								}
							} else if (port.getSpecification().getName().equals(Constants.SPEC_VIRTUAL_PORT)) {
								String virtualPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
								if (consResAssignState.equals(AssignmentState.UNASSIGNED)) {
									setServiceIdForUnassignedDeviceInterfaces(port, cfsServiceId,virtualPortServiceId, null, Constants.SPARE);
								} else if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
									setServiceIdForAssignedDownlinkPort(port, cfsServiceId, virtualPortServiceId, null, rfsServiceSpec);
								}
							}
						}
					} else if(serviceConfigItemName.equals(Constants.CI_COMBO_PORT)){
						Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
						Iterator<ConsumableResource> consResIter = consResSet.iterator();
						while(consResIter.hasNext()) {
							DeviceInterface port = (DeviceInterface) consResIter.next();
							AssignmentState consResAssignState = resourceAssignmentMap.get(port);
							debug(log, "Combo port: " , port.getId() ," ",port.getName() , " state: " , consResAssignState);
							if(port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
								if(consResAssignState.equals(AssignmentState.UNASSIGNED)) {
									String unassignedComboPortServiceId = UimHelper.getEntityCharValue(port,
											Constants.SERVICE_ID);
									setServiceIdForUnassignedDownlinkPort(port, cfsServiceId, unassignedComboPortServiceId);
								}else if(consResAssignState.equals(AssignmentState.ASSIGNED)) {
									String downlinkPortServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
									setServiceIdForAssignedDownlinkPort(port, cfsServiceId, downlinkPortServiceId, null, rfsServiceSpec);
								}
							}
						}
					}
				} // configItem not null
			} // Loop configItems 
			// Set serviceId for reserved ports. 
			updateReservedPorts(reservationId, cfsServiceId);
		} // disconnect
		debug(log, "Checking for invalid data combination.");
		if(rfsServiceName.equals(Constants.SPEC_FIXEDVOICE_RFS) || rfsServiceName.equals(Constants.SPEC_BROADBAND_RFS)) {
			boolean isDoubleAction = UimHelper.isDoubleAction(associatedParentBi);
			if(isDoubleAction) {
				Map<String, String> charMap = new HashMap<>();
				charMap.put(Constants.SERVICE_ID, cfsServiceId);
				List<DeviceInterface> cfsServicePortList = DeviceHelper.findDIByNameCharSpec(null, charMap, null,
						CriteriaOperator.CONTAINS, null, null);
				
				if(!Utils.isEmpty(cfsServicePortList)) {
					String portServiceId = null;
					for(DeviceInterface cfsServicePort : cfsServicePortList) {
						portServiceId = UimHelper.getEntityCharValue(cfsServicePort, Constants.SERVICE_ID);
						if(portServiceId.indexOf(Constants.DELIMITER_PIPE_SEPARATOR) > -1 
								|| portServiceId.indexOf(Constants.DELIMITER_DEVICE_PORT) > -1) {
							String serviceIds [] = portServiceId.split("\\|");
							for(String serviceId : serviceIds) {
								if(serviceId.indexOf(Constants.DELIMITER_DEVICE_PORT) > -1) {
									serviceId = portServiceId.replaceAll(serviceId, Constants.EMPTY);
									serviceId = portServiceId.replaceAll(Constants.DELIMITER_PIPE_SEPARATOR, Constants.EMPTY);
									EntityHelper.setValue(cfsServicePort, Constants.PARAM_OPERATIONAL_STATUS, Constants.ACTIVE);
								}
							}
						}
					}
				}
			}
		}
		debug(log, "#####////////////// Print Port Details Start /////////////// ",cfsServiceId);
		for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
			if (serviceConfigItem != null && serviceConfigItem.getName() != null) {
				String serviceConfigItemName = serviceConfigItem.getName();
				debug(log, "ServiceConfigItemName CI: " , serviceConfigItemName);
				Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem
						.getAssignmentsMap();
				if (serviceConfigItemName.equals(Constants.CI_SUBSCRIBER_PORT)) {
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while (consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						String portServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
						String portReservationId = UimHelper.getEntityCharValue(port, Constants.RESERVATION_ID);
						logPortDetails(port, portServiceId, portReservationId, consResAssignState);
					}
				} else if (serviceConfigItemName.equals(Constants.CI_SERVICE_PORT)) {
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while (consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log, port.getName(), " state: ", consResAssignState);
						String portServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
						String portReservationId = UimHelper.getEntityCharValue(port,
								Constants.RESERVATION_ID);
						logPortDetails(port, portServiceId, portReservationId, consResAssignState);
					}
				} else if(serviceConfigItemName.equals(Constants.CI_COMBO_PORT)){
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while(consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log, port.getName() , "state: " , consResAssignState);
						if(port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
							String portServiceId = UimHelper.getEntityCharValue(port, Constants.SERVICE_ID);
							String portReservationId = UimHelper.getEntityCharValue(port, Constants.RESERVATION_ID);
							logPortDetails(port, portServiceId, portReservationId, consResAssignState);
						}
					}
				}
			} // configItem not null
		} // Loop print configItems 
		
		debug(log, "#####////////////// Print Port Details End ///////////////", cfsServiceId);
		debug(log, "updatePorts - END ",cfsServiceId);
	}

	/**
	 * To log the port status after complete.
	 * @param port
	 * @param portServiceId
	 * @param portReservationId
	 * @param consResAssignState
	 */
	private static void logPortDetails(DeviceInterface port, String portServiceId, String portReservationId,
			AssignmentState consResAssignState) {
		debug(log, "##### ", port.getId(), " ", port.getSpecification().getName(), " ", port.getName(), " state: ",
				consResAssignState, " ReservationId " , portReservationId , " serviceId " , portServiceId);
	}
	
	/**
	 * This method finds the CPE or Enterprise_CPE and assigns it to the Service.
	 * 
	 * @param config
	 * @param configItemType
	 * @param cpeSpec
	 * @throws ValidationException
	 */
	public static void findnAssignCPE(ServiceConfigurationVersion config, ConfigurationItemType configItemType, String cpeSpec) throws ValidationException{
		debug(log,"findnAssignCPE - START");
		
		String imei = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_IMEI);
		if(Utils.isEmpty(imei)) {
			log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_IMEI);
		}
		debug(log,"imei : " , imei);		
		
		String dateInstalled= UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_DATE_INSTALLED);
		
		Map<String,String> charMap = new HashMap<>();
		charMap.put(Constants.PARAM_IMEI, imei);
		
		List<LogicalDevice> unreservedLdList = UimHelper.findLogicalDevices(cpeSpec, null, charMap, CriteriaOperator.EQUALS, AssignmentState.UNASSIGNED, InventoryState.INSTALLED.toString(), null);
		debug(log,"unreservedLdList size() : " , unreservedLdList.size());
		if(Utils.isEmpty(unreservedLdList)) {
			log.validationException("CPE.numberNotFound", new java.lang.IllegalArgumentException(), imei);
		}		
		LogicalDevice cpe = unreservedLdList.get(0);
		debug(log, "For IMEI :", imei," found CPE :", cpe.getId());		
		
		//Update CPE DateInstalled
		UimHelper.updateCPEDateInstalled(cpe, dateInstalled);
		
		//Assign the above found LD to given Service Config Item 
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem sci = designManager.aquireConfigItem(config, configItemType.getName());
		UimHelper.assignResourceToConfigItem(config, sci, cpe);		
		debug(log,"findnAssignCPE - END");		
	}

	/**
	 * To check config item has assignment or not.
	 * 
	 * @param configVersion
	 * @param configItemName
	 * @return
	 */
	public static boolean isConfigItemHasAssignment(ServiceConfigurationVersion configVersion,
			String configItemName) {
		debug(log, "isConfigItemHasAssignment() " + configItemName);
		boolean hasAssignment = false;
		List<ServiceConfigurationItem> rfsServiceConfigItemsList = configVersion.getConfigItems();
		for (ServiceConfigurationItem serviceConfigItem : rfsServiceConfigItemsList) {
			if (serviceConfigItem != null && serviceConfigItem.getName() != null) {
				String serviceConfigItemName = serviceConfigItem.getName();
				debug(log, "ServiceConfigItemName CI: ", serviceConfigItemName);
				Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem.getAssignmentsMap();
				if (serviceConfigItemName.equals(Constants.CI_COMBO_PORT)) {
					Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
					Iterator<ConsumableResource> consResIter = consResSet.iterator();
					while (consResIter.hasNext()) {
						DeviceInterface port = (DeviceInterface) consResIter.next();
						AssignmentState consResAssignState = resourceAssignmentMap.get(port);
						debug(log, "Combo port: ", port.getId(), " ", port.getName(), " state: ", consResAssignState);
						if (port.getSpecification().getName().equals(Constants.SPEC_DOWNLINK_PORT)) {
							if (consResAssignState.equals(AssignmentState.ASSIGNED)) {
								hasAssignment = true;
								break;
							}
						}
					}
				}
			} // configItem not null
			if (hasAssignment) {
				break;
			}
		} // Loop configItems
		debug(log, "isConfigItemHasAssignment() - END " + hasAssignment);
		return hasAssignment;
	}

	/**
	 * To check port is existing with respective assignment or not.
	 * 
	 * @param latestRfsSCV
	 * @param oldPort
	 * @param configItemName
	 * @param portSpec
	 * @param assignmentState
	 * @return
	 */
	public static boolean isPortExists(ServiceConfigurationVersion latestRfsSCV, DeviceInterface oldPort,
			String configItemName, String portSpec,
			AssignmentState assignmentState) {
		debug(log, "isPortExists Start : config Item Name ", configItemName, " Port Spec : ", portSpec);
		boolean isPortExistInConfig = false;
		ServiceConfigurationItem serviceConfigItem = UimHelper.getConfigItem(latestRfsSCV, configItemName, null);
		if (null != serviceConfigItem) {
			Map<ConsumableResource, AssignmentState> resourceAssignmentMap = serviceConfigItem.getAssignmentsMap();
			Set<ConsumableResource> consResSet = resourceAssignmentMap.keySet();
			Iterator<ConsumableResource> consResIter = consResSet.iterator();
			while (consResIter.hasNext()) {
				DeviceInterface port = (DeviceInterface) consResIter.next();
				AssignmentState consResAssignState = resourceAssignmentMap.get(port);
				debug(log, "Port: ", port.getId(), " ", port.getName(), " state: ", consResAssignState);
				if (port.getSpecification().getName().equals(portSpec)
						&& port.getId().equals(oldPort.getId())
						&& consResAssignState.equals(assignmentState)) {
					isPortExistInConfig = true;
					break;
				}
			}
		} else {
			debug(log, "ServiceConfigItem " + configItemName + " not found in configuration.");
		}
		debug(log, "isPortExists End  isPortExistInConfig : ", isPortExistInConfig);
		return isPortExistInConfig;
	}

	/**
	 * To check the card is valid for RFS Service.
	 * 
	 * @param rfsServiceSpec
	 * @param card
	 * @return
	 * @throws ValidationException
	 */
	public static boolean isValidCard(String rfsServiceSpec, LogicalDevice card) throws ValidationException {
		debug(log, "isValidCard() Start, RFS service spec : ", rfsServiceSpec, " Card : ", card.getId(), " ",
				card.getName());
		boolean isCardValid = false;
		String cardType = UimHelper.getEntityCharValue(card, Constants.PARAM_CARDTYPE);
		String cardTechnology = UimHelper.getTechnology(cardType);
		debug(log, "cardType : ", cardType, " Technology : ", cardTechnology);
		
		if (rfsServiceSpec.equals(Constants.SPEC_FIXEDVOICE_RFS) || rfsServiceSpec.equals(Constants.SPEC_BROADBAND_RFS)) {
			if (cardTechnology.equals(Constants.ADSL) || cardTechnology.equals(Constants.VDSL)) {
				isCardValid = true;
			}
		} else if (rfsServiceSpec.equals(Constants.SPEC_DIA_RFS) || rfsServiceSpec.equals(Constants.SPEC_IPVPN_RFS)
				|| rfsServiceSpec.equals(Constants.SPEC_SIPTRUNK_RFS)
				|| rfsServiceSpec.equals(Constants.SPEC_IPLC_RFS) || rfsServiceSpec.equals(Constants.SPEC_EPL_RFS)) {
			if (cardTechnology.equals(Constants.SHDSL)) {
				isCardValid = true;
			}
		} else if (rfsServiceSpec.equals(Constants.SPEC_ISDN_RFS)) {
			if (cardTechnology.equals(Constants.SHDSL) || cardTechnology.equals(Constants.GHDSL)) {
				isCardValid = true;
			}
		}
		debug(log, "isValidCard() End isCardValid : ", isCardValid);
		return isCardValid;
	}

}
