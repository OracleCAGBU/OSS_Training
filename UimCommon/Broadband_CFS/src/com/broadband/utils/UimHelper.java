package com.broadband.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.broadband.ci.BaseDesigner;
import com.broadband.utils.Constants;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionItemSearchCriteria;
import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.characteristic.CharacteristicManager;
import oracle.communications.inventory.api.characteristic.impl.CharacteristicHelper;
import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.common.EntityUtils;
import oracle.communications.inventory.api.common.RowLockManager;
import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.configuration.ConfigurationManager;
import oracle.communications.inventory.api.consumer.AssignmentManager;
import oracle.communications.inventory.api.consumer.AssignmentSearchCrteria;
import oracle.communications.inventory.api.consumer.ConsumerUtils;
import oracle.communications.inventory.api.consumer.ReservationManager;
import oracle.communications.inventory.api.custom.CustomObjectManager;
import oracle.communications.inventory.api.custom.CustomObjectSearchCriteria;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionAttachment;
import oracle.communications.inventory.api.entity.BusinessInteractionChar;
import oracle.communications.inventory.api.entity.BusinessInteractionItem;
import oracle.communications.inventory.api.entity.BusinessInteractionItemVisibility;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ConfigurationReferenceState;
import oracle.communications.inventory.api.entity.ConfigurationStatus;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectCharacteristic;
import oracle.communications.inventory.api.entity.CustomObjectReservation;
import oracle.communications.inventory.api.entity.CustomObjectSpecification;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceCharacteristic;
import oracle.communications.inventory.api.entity.DeviceInterfaceSpecification;
import oracle.communications.inventory.api.entity.FlowIdentifier;
import oracle.communications.inventory.api.entity.FlowIdentifierCharacteristic;
import oracle.communications.inventory.api.entity.FlowIdentifierSpecification;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.IPSubnet;
import oracle.communications.inventory.api.entity.InvGroupRef;
import oracle.communications.inventory.api.entity.InventoryConfigurationSpec;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupSpecification;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDeviceConfigurationItem;
import oracle.communications.inventory.api.entity.LogicalDeviceConfigurationVersion;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.NetworkAddressDomain;
import oracle.communications.inventory.api.entity.NetworkAddressDomainSpec;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyCharacteristic;
import oracle.communications.inventory.api.entity.PartyServiceRel;
import oracle.communications.inventory.api.entity.PhysicalPort;
import oracle.communications.inventory.api.entity.Pipe;
import oracle.communications.inventory.api.entity.PipeCharacteristic;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.entity.PlaceLogicalDeviceRel;
import oracle.communications.inventory.api.entity.PlaceServiceRel;
import oracle.communications.inventory.api.entity.PlaceSpecification;
import oracle.communications.inventory.api.entity.ReservationType;
import oracle.communications.inventory.api.entity.ReservedForType;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceAssignment;
import oracle.communications.inventory.api.entity.ServiceCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationItemCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceSpecification;
import oracle.communications.inventory.api.entity.ServiceStatus;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.TNCharacteristic;
import oracle.communications.inventory.api.entity.TelephoneNumber;
import oracle.communications.inventory.api.entity.TelephoneNumberSpecification;
import oracle.communications.inventory.api.entity.common.Assignment;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.EntityConsumer;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.entity.common.Reservation;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.persistence.InventoryFinder;
import oracle.communications.inventory.api.framework.policy.LockPolicy;
import oracle.communications.inventory.api.framework.resource.MessageResource;
import oracle.communications.inventory.api.framework.security.UserEnvironment;
import oracle.communications.inventory.api.framework.security.UserEnvironmentFactory;
import oracle.communications.inventory.api.group.InventoryGroupEntitySearchCriteria;
import oracle.communications.inventory.api.group.InventoryGroupManager;
import oracle.communications.inventory.api.group.InventoryGroupSearchCriteria;
import oracle.communications.inventory.api.ip.IPNetworkManager;
import oracle.communications.inventory.api.ip.IPSubnetSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.DeviceInterfaceSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.networkaddress.FlowIdentifierManager;
import oracle.communications.inventory.api.networkaddress.FlowIdentifierRange;
import oracle.communications.inventory.api.networkaddress.FlowIdentifierSearchCriteria;
import oracle.communications.inventory.api.networkaddress.NetworkAddressDomainManager;
import oracle.communications.inventory.api.networkaddress.NetworkAddressDomainSearchCriteria;
import oracle.communications.inventory.api.number.TelephoneNumberManager;
import oracle.communications.inventory.api.number.TelephoneNumberSearchCriteria;
import oracle.communications.inventory.api.place.PlaceManager;
import oracle.communications.inventory.api.place.PlaceSearchCriteria;
import oracle.communications.inventory.api.place.RelPlaceSearchCriteria;
import oracle.communications.inventory.api.service.ServiceConfigurationManager;
import oracle.communications.inventory.api.service.ServiceManager;
import oracle.communications.inventory.api.service.ServiceSearchCriteria;
import oracle.communications.inventory.api.specification.SpecManager;
import oracle.communications.inventory.api.specification.SpecSearchCriteria;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.BusinessInteractionType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemPropertyType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;
import oracle.communications.inventory.xmlbeans.InteractionDocument;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.platform.entity.impl.PlaceLogicalDeviceRelDAO;
import oracle.communications.platform.entity.impl.ServiceAssignmentToServiceDAO;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;
import oracle.communications.platform.persistence.SearchCriteria;

public class UimHelper {

	private static final Log log = LogFactory.getLog(UimHelper.class);	
	
	private static final String CHAR_DELIMITTER = "Char :";
	
	private static final String REMOVED = "removed";
	
	private static final String C2A_COULD_NOT_FIND = "c2a.couldNotFind";
	
	/**
	 * Log the debug data values in the debug log if the log is enabled. 
	 * @param lg
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
	 * @param Spec
	 * @param id
	 * @return TelephoneNumber
	 * @throws ValidationException
	 * @throws oracle.communications.inventory.api.exception.ValidationException
	 */
	public static TelephoneNumber getTelephoneNumberByID(String tnSpecName, String id)	throws ValidationException {
		debug(log,"getTelephoneNumberByID - START, Id="+id);
		TelephoneNumberManager mgr = PersistenceHelper.makeTelephoneNumberManager();
		List<TelephoneNumber> list = null;

		TelephoneNumberSearchCriteria criteria = mgr.makeTelephoneNumberSearchCriteria();
		TelephoneNumberSpecification spec= EntityUtils.findSpecification(TelephoneNumberSpecification.class, tnSpecName);
		criteria.setTelephoneNumberSpecification(spec);

		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(id);
		nameCriteria.setOperator(CriteriaOperator.EQUALS);
		criteria.setId(nameCriteria);
		criteria.setDisableOrdering(true);
		try {

			list = mgr.findTelephoneNumbers(criteria);
			if (list != null && list.size() == 1) {
				debug(log, "Telephone Number found for Id " + id);
				debug(log,"List Size=" + list.size());
			} else if (list != null && list.size() > 1) {
				log.warn("", "More than 1 Telephone Number found for Id " + id);
			} else {
				debug(log, "Telephone Number Not found for Id " + id);
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Telephone Number.", e);
			throw e;
		}
		debug(log,"getTelephoneNumberByID - END");
		return (list != null && !list.isEmpty()) ?  list.get(0) : null;
	}

	/**
	 * This method finds the CO by Name & Spec.
	 * @param coSpecName
	 * @param name
	 * @param nameCriteriaOperator - optional parameter defaulted to EQUALS
	 * @param charMap
	 * @param charCriteriaOperator
	 * @param quantity
	 * @param assignmentState
	 * @return List<CustomObject>
	 * @throws ValidationException
	 */
	public static List<CustomObject> findCustomObject(String coSpecName, String name, CriteriaOperator nameCriteriaOperator, 
			         Map<String,String> charMap, CriteriaOperator charCriteriaOperator, Integer quantity, AssignmentState assignmentState) throws ValidationException {
		debug(log,"findCustomObject - START");

		CustomObjectManager mgr = PersistenceHelper.makeCustomObjectManager();
		List<CustomObject> list = null;

		CustomObjectSearchCriteria criteria = mgr.makeCustomObjectSearchCriteria();

		//Set Assignment State Criteria
		if(assignmentState!=null){
			criteria.setAssignmentState(assignmentState);
		}
		
		//Set Quantity Criteria in cases where only specific no. of DI are required.
		if(quantity!=null){
			long max = quantity.longValue()-1;
			criteria.setRange(0, max);
		}

		//Set Specification Criteria
		if(null!=coSpecName){
			CustomObjectSpecification spec= EntityUtils.findSpecification(CustomObjectSpecification.class, coSpecName);
			criteria.setCustomObjectSpecification(spec);
		}

		// Set Name Criteria
		if(name!=null){
			CriteriaItem nameCI = criteria.makeCriteriaItem();
			nameCI.setValue(name);
			if(nameCriteriaOperator==null){
				nameCI.setOperator(CriteriaOperator.EQUALS);
			} else {
				nameCI.setOperator(nameCriteriaOperator);
			}
			criteria.setName(nameCI);
		}


		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(CustomObjectCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

		criteria.setDisableOrdering(true);
		list = mgr.findCustomObjects(criteria);
		debug(log,"findCustomObject - END, List Size=" + list.size());
		return list;
	}
	
	/**
	 * This method finds the CO by Name & Spec.
	 * @param coSpecName
	 * @param name
	 * @param nameCriteriaOperator - optional parameter defaulted to EQUALS
	 * @param charMap
	 * @param charCriteriaOperator
	 * @return List<GeographicPlace>
	 * @throws ValidationException
	 */
	public static List<GeographicPlace> findGeographicPlace(String gpSpecName, String name, CriteriaOperator nameCriteriaOperator, Map<String,String> charMap, 
			CriteriaOperator charCriteriaOperator) throws ValidationException {
		debug(log,"findGeographicPlace - START");

		PlaceManager mgr = PersistenceHelper.makePlaceManager();
		List<GeographicPlace> list = null;

		PlaceSearchCriteria criteria = mgr.makePlaceSearchCriteria();		
		
		//Set Specification Criteria
		if(null != gpSpecName){
			PlaceSpecification spec= EntityUtils.findSpecification(PlaceSpecification.class, gpSpecName);
			CriteriaItem specCI = criteria.makeCriteriaItem();
			specCI.setValue(spec);
			criteria.setSpec(specCI);
		}

		// Set Name Criteria
		if(name!=null){
			CriteriaItem nameCI = criteria.makeCriteriaItem();
			nameCI.setValue(name);
			if(nameCriteriaOperator==null){
				nameCI.setOperator(CriteriaOperator.EQUALS);
			} else {
				nameCI.setOperator(nameCriteriaOperator);
			}
			criteria.setName(nameCI);
		}


		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(CustomObjectCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

		criteria.setDisableOrdering(true);
		list = mgr.findGeographicPlaces(criteria, GeographicAddress.class);
		debug(log,"findGeographicPlace - END, List Size=" + list.size());
		return list;
	}

	/**
	 * This method returns the matching CO or null value.
	 * @param coSpecName
	 * @param name
	 * @param nameCriteriaOperator
	 * @return CustomObject
	 * @throws ValidationException
	 */
	public static CustomObject getCustomObjectByNameAndSpec(String coSpecName, String name, CriteriaOperator nameCriteriaOperator) throws ValidationException{
		debug(log,"getCustomObjectByName (Single) - START");
		List<CustomObject> coList = findCustomObject(coSpecName, name, nameCriteriaOperator, null, null, 1, null); 
		if(!Utils.isEmpty(coList)){
			debug(log,"getCustomObjectByName (Single) - END, co");
			return coList.get(0);
		} else {
			debug(log,"getCustomObjectByName (Single) - END, null");
			return null;
		}
	}

	/**
	 * This method finds the LD by Name & Spec
	 * @param specName
	 * @param name
	 * @param nameCriteriaOperator - optional parameter defaulted to EQUALS
	 * @return LogicalDevice
	 * @throws ValidationException
	 */
	public static LogicalDevice getLogicaldDeviceByNameAndSpec(String specName, String name, CriteriaOperator nameCriteriaOperator) throws ValidationException {
		debug(log,"getLogicaldDeviceByNameAndSpec - START");

		LogicalDeviceManager entityManager = PersistenceHelper.makeLogicalDeviceManager();
		List<LogicalDevice> list = null;

		LogicalDeviceSearchCriteria criteria = entityManager.makeLogicalDeviceSearchCriteria();

		if(specName!=null){
			LogicalDeviceSpecification spec= EntityUtils.findSpecification(LogicalDeviceSpecification.class, specName);
			criteria.setLogicalDeviceSpecification(spec);			
		}
		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(name);
		
		if(nameCriteriaOperator==null){
			nameCriteria.setOperator(CriteriaOperator.EQUALS);
		} else {
			nameCriteria.setOperator(nameCriteriaOperator);
		}
		criteria.setName(nameCriteria);
		criteria.setDisableOrdering(true);
		//criteria.set
		try {

			list = entityManager.findLogicalDevice(criteria);
			if (list != null && list.size() == 1) {
				debug(log,"Logical Device found for Name: " + name);
				debug(log,"List Size= " + list.size());
			} else if (list != null && list.size() > 1) {
				log.warn("", "More than 1 Logical Device found for Name: " + name);
			} else {
				debug(log,"Logical Device Not found for Name: " + name);
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Logical Device.", e);
			throw e;
		}

		debug(log,"getLogicaldDeviceByNameAndSpec - END");
		return (list != null && !list.isEmpty()) ?  list.get(0) : null;
	}
	
	/**
	 * This method finds the LD by ID & Spec
	 * @param specName
	 * @param id
	 * @param nameCriteriaOperator - optional parameter defaulted to EQUALS
	 * @return LogicalDevice
	 * @throws ValidationException
	 */
	public static LogicalDevice getLogicaldDeviceByIdAndSpec(String specName, String id, CriteriaOperator nameCriteriaOperator) throws ValidationException {
		debug(log,"getLogicaldDeviceByNameAndSpec - START");

		LogicalDeviceManager entityManager = PersistenceHelper.makeLogicalDeviceManager();
		List<LogicalDevice> list = null;

		LogicalDeviceSearchCriteria criteria = entityManager.makeLogicalDeviceSearchCriteria();

		if(specName!=null){
			LogicalDeviceSpecification spec= EntityUtils.findSpecification(LogicalDeviceSpecification.class, specName);
			criteria.setLogicalDeviceSpecification(spec);			
		}
		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(id);
		
		if(nameCriteriaOperator==null){
			nameCriteria.setOperator(CriteriaOperator.EQUALS);
		} else {
			nameCriteria.setOperator(nameCriteriaOperator);
		}
		criteria.setId(nameCriteria);
		criteria.setDisableOrdering(true);
		//criteria.set
		try {

			list = entityManager.findLogicalDevice(criteria);
			if (list != null && list.size() == 1) {
				debug(log,"Logical Device found for ID: " + id);
				debug(log,"List Size= " + list.size());
			} else if (list != null && list.size() > 1) {
				log.warn("", "More than 1 Logical Device found for ID: " + id);
			} else {
				debug(log,"Logical Device Not found for ID: " + id);
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Logical Device.", e);
			throw e;
		}

		debug(log,"getLogicaldDeviceByNameAndSpec - END");
		return (list != null && !list.isEmpty()) ?  list.get(0) : null;
	}

	/**
	 * This method sets the chars on the Config Item
	 * @param configItem
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	public static void setConfigItemChars(ServiceConfigurationVersion config, ServiceConfigurationItem configItem, Map<String,String> charMap)
			throws ValidationException{
		debug(log,"setConfigItemChars - START, CharMap Size: "+charMap.size());

		debug(log,"ConfigItem Name: "+configItem.getName());
		ServiceConfigurationItem parentConfigItem = configItem.getParentConfigItem();
		if(parentConfigItem != null )
			debug(log,"Parent ConfigItem Name: " + parentConfigItem.getName());

		for (Map.Entry<String, String> entry : charMap.entrySet()) {
			try
			{
				debug(log,"Key=="+entry.getKey() + " , Value=" +entry.getValue());
				if(null==entry.getValue()){
					debug(log,"Value of "+entry.getKey() + " is null.");
				}
				else
				{
					EntityHelper.addUpdateConfigItemCharacteristic(config, configItem, entry.getKey(), entry.getValue());
					debug(log,"Value of "+entry.getKey() + " is successfully set as " + entry.getValue() + ".");
				}

			}catch (Exception e) {								
				log.error("", Constants.EXCEPTION_DELIMITTER+e.getMessage(),e);
			}

		}
		debug(log,"setConfigItemChars - END");
	}

	/**
	 * This method sets the given char value on the service configuration.
	 * @param config
	 * @param configItemName - Optional. If not provided then char will be set at the root level.
	 * @param charName
	 * @param charValue
	 * @return
	 * @throws ValidationException
	 */
	public static void setConfigItemCharValue(ServiceConfigurationVersion config, String configItemName, String charName, String charValue) throws ValidationException{
		debug(log,"setConfigItemCharValue - START1");
		debug(log, "ConfigItem Name :" , configItemName," , Char Name :", charName ," , Char Value :" , charValue);
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem configItem = null;
		if(Utils.isEmpty(configItemName)){
			configItem = (ServiceConfigurationItem) config.getConfigItemTypeConfig();
		} else {
			configItem = designManager.aquireConfigItem(config, configItemName);
		}
		if("".equals(charValue)){
			Map<String,ServiceConfigurationItemCharacteristic> configItemCharMap = configItem.getCharacteristicMap();
			if(configItemCharMap.containsKey(charName)){
				configItemCharMap.remove(charName);
				debug(log,CHAR_DELIMITTER , charName , REMOVED);
			}
		} else {
			EntityHelper.addUpdateConfigItemCharacteristic(config, configItem, charName, charValue);
			debug(log,CHAR_DELIMITTER , charName , " updated with value :" , charValue);
		}

		debug(log,"setConfigItemCharValue - END1");
	}

	/**
	 * This method sets the chars on the entity like LD, DI, CO, etc.
	 * @param entity
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	public static void setEntityChars(CharacteristicExtensible entity, Map<String,String> charMap) throws ValidationException{
		debug(log,"setEntityChars - START");

		if(charMap!=null && charMap.size()>0){
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				String value = entry.getValue();
				if(value != null)
					EntityHelper.setValue(entity, entry.getKey(), value);
				debug(log,"Entity name: " + entity.getSpecification().getName() + " updated with char >> " +  entry.getKey() + "=" + entry.getValue());
				
			}
		}

		debug(log,"setEntityChars - END");
	}

	/**
	 * This method finds the ports reserved during FetchAutoRoute call
	 * @param reservationNumber
	 * @return Collection<CustomObjectReservation>
	 * @throws ValidationException
	 */
	public static Collection<CustomObjectReservation> findRouteReservation(String reservationNumber) throws ValidationException{
		debug(log,"findReservations - START");
		
		if(Utils.isEmpty(reservationNumber)) {
			throw new ValidationException("reservationNumber is required to findRouteReservation.");
		}
		
		InventoryFinder finder = null;
		Collection<CustomObjectReservation> resList= null;
		try{
			finder = (InventoryFinder)PersistenceHelper.makeFinder();
			finder.setSecurityFilterDisabled(true);   
			finder.setResultClass(CustomObjectReservation.class);
			finder.setJPQLFilter("o.reservationNumber = :preservationNumber");
			finder.setParameters(new String[] { "preservationNumber" }, new Object[] { Long.parseLong(reservationNumber) });
			resList = finder.findMatches();
		}
		finally {
			if (finder != null)
				finder.close();
		}

		if(Utils.isEmpty(resList)){
			debug(log,"No resource found for reservation id  " + reservationNumber);
		}

		debug(log,"findReservations - END");
		return resList;
	}
	
	/**
	 * To reserve Fixedline Number resource and return reservation object.
	 * @param request
	 * @param tn
	 * @return Reservation
	 * @throws ValidationException	 
	 */
	public static Reservation reserveFixedlineNumberResource(TelephoneNumber tn, String reservedFor, String reason, String expiryDate) throws ValidationException {
		debug(log,"reserveFixedlineNumberResource - START");
		
		ReservationManager rMgr = PersistenceHelper.makeReservationManager();
		Reservation reservation = rMgr.makeReservation(rMgr.getReservationClass(tn));
		reservation.setResource(tn);
		reservation.setReservedFor(reservedFor);
		reservation.setReservedForType(ReservedForType.CUSTOMER);
		reservation.setReservationType(ReservationType.LONGTERM);
		if(!Utils.isEmpty(reason)) {
			reservation.setReason(reason);
		}
		if(Utils.checkBlank(expiryDate)) {
			Date today = new Date();
			int numberReservationExpiry=0;
			try {
				numberReservationExpiry = Integer.valueOf(UimHelper.getSystemConfigPropertyValue(Constants.CHAR_FIXEDLINENUMBERRESERVATIONEXPIRY));
			} catch (NumberFormatException | ValidationException e) {
				log.error("", e);
			}
			reservation.setExpiry(Util.addDays(today, numberReservationExpiry));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
			try {
				reservation.setExpiry(sdf.parse(expiryDate));
			} catch (ParseException e) {
				log.error("", e);
			}
		}
		
		debug(log,"reserveFixedlineNumberResource - END, Reservation Number: " + reservation.getReservationNumber());
		return reservation;
	}

	/**
	 * This method retrieves the char value from the entity.
	 * @param entity
	 * @param charName
	 * @return String
	 */
	public static String getEntityCharValue(Persistent entity, String charName) {
		debug(log,"Entering getEntityCharValue method.");

		String charValue = null;
		TelephoneNumber tn = null;
		CustomObject co = null;
		LogicalDevice ld = null;
		DeviceInterface di = null;
		Service service = null;
		GeographicPlace place = null;
		Pipe pipe = null;
		BusinessInteraction bi = null;
		Party party = null;
		
		if(entity instanceof TelephoneNumber){
			tn = (TelephoneNumber) entity;
			Set<TNCharacteristic> tnCharSet = tn.getCharacteristics();
			for(TNCharacteristic tnChar : tnCharSet){			
				if (tnChar.getName().trim().equals(charName)) {
					charValue = tnChar.getValue();
					debug(log,"TN Id: ",tn.getId());					
					debug(log,"");
					break;
				}
			}
		} else if(entity instanceof CustomObject){
			co = (CustomObject) entity;
			Set<CustomObjectCharacteristic> coCharSet = co.getCharacteristics();
			for(CustomObjectCharacteristic coChar : coCharSet){			
				if (coChar.getName().trim().equals(charName)) {
					charValue = coChar.getValue();
					debug(log,"CO Id: ",co.getId());					
					break;
				}
			}
		} else if(entity instanceof LogicalDevice){
			ld = (LogicalDevice) entity;
			Set<LogicalDeviceCharacteristic> ldCharSet = ld.getCharacteristics();
			for(LogicalDeviceCharacteristic ldChar : ldCharSet){			
				if (ldChar.getName().trim().equals(charName)) {
					charValue = ldChar.getValue();
					debug(log,"LD Id: "+ld.getId());					
					break;
				}
			}
		} else if(entity instanceof DeviceInterface){
			di = (DeviceInterface) entity;
			Set<DeviceInterfaceCharacteristic> diCharSet = di.getCharacteristics();
			for(DeviceInterfaceCharacteristic diChar : diCharSet){			
				if (diChar.getName().trim().equals(charName)) {
					charValue = diChar.getValue();
					debug(log,"DI Id: "+di.getId());					
					break;
				}
			}
		} else if(entity instanceof Service){
			service = (Service) entity;
			Set<ServiceCharacteristic> serviceCharSet = service.getCharacteristics();
			for(ServiceCharacteristic serviceChar : serviceCharSet){			
				if (serviceChar.getName().trim().equals(charName)) {
					charValue = serviceChar.getValue();
					debug(log,"Service Id: "+service.getId());					
					break;
				}
			}
		}  else if(entity instanceof GeographicPlace){
			place = (GeographicPlace) entity;
			Set<PlaceCharacteristic> placeCharSet = place.getCharacteristics();
			for(PlaceCharacteristic placeChar : placeCharSet){			
				if (placeChar.getName().trim().equals(charName)) {
					charValue = placeChar.getValue();
					debug(log,"Place Id: "+place.getId());					
					break;
				}
			}
		}  else if(entity instanceof Pipe){
			pipe = (Pipe) entity;
			Set<PipeCharacteristic> pipeCharSet = pipe.getCharacteristics();
			for(PipeCharacteristic pipeChar : pipeCharSet){			
				if (pipeChar.getName().trim().equals(charName)) {
					charValue = pipeChar.getValue();
					debug(log,"Pipe Id: "+pipe.getId());					
					break;
				}
			}
		} else if(entity instanceof BusinessInteraction){
			bi = (BusinessInteraction) entity;
			Set<BusinessInteractionChar> biCharSet = bi.getCharacteristics();
			for(BusinessInteractionChar biChar : biCharSet){			
				if (biChar.getName().trim().equals(charName)) {
					charValue = biChar.getValue();
					debug(log,"BusinessInteraction Id: " + bi.getId());
					break;
				}
			}
		} else if(entity instanceof Party){
			party = (Party) entity;
			Set<PartyCharacteristic> partyCharSet = party.getCharacteristics();
			for(PartyCharacteristic partyChar : partyCharSet){			
				if (partyChar.getName().trim().equals(charName)) {
					charValue = partyChar.getValue();
					debug(log,"Party Id: " + party.getId());
					break;
				}
			}
		} 

		debug(log,"Found value of ",charName," : "+charValue);
		
		if (Utils.checkBlank(charValue)){
			debug(log,"Entity Id: ", entity.getOid() + " , " ,charName , " - value not found.");
		}

		debug(log,"Exiting getEntityCharValue method.");
		return charValue;
	}


	/**
	 * This method retrieves the char value from given Config Item provided that ConfigItem is at root level.
	 * @param config
	 * @param configItemName - Optional. If not provided then char will be set at the root level.
	 * @param charName
	 * @return String
	 * @throws ValidationException
	 */
	public static String getConfigItemCharValue(ServiceConfigurationVersion config, String configItemName, String charName) throws ValidationException{
		debug(log,"getConfigItemCharValue - START, configItemName: " + configItemName + ", charName: " + charName);

		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem configItem = null;
		String charValue = null;
		if(!Utils.isEmpty(configItemName)){
			configItem = getConfigItem(config, configItemName, null);//Fix for UAT defect
			if(null!=configItem) {
				charValue = designManager.getConfigItemCharacteristic(configItem, charName);
			}
		}

		debug(log,"getConfigItemCharValue - END, charValue: " + charValue);
		return charValue;
	}

	/**
	 * This method assigns the resource to the config item.
	 * @param config
	 * @param configItem
	 * @param resource
	 * @return
	 * @throws ValidationException
	 */
	public static void assignResourceToConfigItem(ServiceConfigurationVersion config, ServiceConfigurationItem configItem, ConsumableResource resource) throws ValidationException{
		debug(log,"assignResourceToConfigItem - START.");
		BaseConfigurationManager configManager = PersistenceHelper.makeConfigurationManager(config.getClass());
		configManager.assignResource(configItem, resource, null, null);
		debug(log,"assignResourceToConfigItem - END.");
	}

	/**
	 * This method will unallocate the resources from the configItem.
	 * @param config
	 * @param configItem	 
	 * @return ServiceConfigurationItem
	 * @throws ValidationException
	 */
	public static ServiceConfigurationItem unallocateResourceToConfigItem(ServiceConfigurationVersion config, String configItemName) 
			throws ValidationException{
		debug(log,"unallocateResourceToConfigItem() - START.");
		debug(log,"configItemName : " , configItemName);
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem sci = designManager.aquireConfigItem(config, configItemName);
		if(null!=sci && null!=sci.getAssignment()) {
			designManager.unassignResource(config, configItemName);
			debug(log,"Resource unallocated from ServiceConfigItem: " + configItemName);
		}else if(null!=sci && null!=sci.getReference()) {
			designManager.dereferenceServiceConfigurationItem(sci);
			debug(log,"Resource dereferenced from ServiceConfigItem: " + configItemName);
		}else {
			throw new ValidationException("No resource allocation/reference found on " + configItemName);
		}
		debug(log,"unallocateResourceToConfigItem() - END.");
		return sci;
	}

	/**
	 * This method unallocates the resource from the config item by the given config item.
	 * @param config
	 * @param configItem
	 * @return ServiceConfigurationItem
	 * @throws ValidationException
	 */
	public static ServiceConfigurationItem unallocateResourceToConfigItem(ServiceConfigurationVersion config, ServiceConfigurationItem configItem) throws ValidationException{
		debug(log,"unallocateResourceToConfigItem(configItem) - START.");
		BaseConfigurationManager configManager = PersistenceHelper.makeConfigurationManager(config.getClass());
		List<ServiceConfigurationItem> configItems = new ArrayList<>(1);
		configItems.add(configItem);
		String configItemName = configItem.getName();
		
		if(!Utils.checkNull(configItem) && !Utils.checkNull(configItem.getAssignment())){		
			debug (log,"configItem.getName() :",configItem.getName(),", configItem.getAssignment() :",configItem.getAssignment());
			configManager.unallocateInventoryConfigurationItems(configItems);
			debug(log,"Resource unallocated from ServiceConfigItem: " + configItemName);
		}else if(!Utils.checkNull(configItem) && !Utils.checkNull(configItem.getReference())){ 	
			debug (log,"configItem.getName() : ",configItem.getName(),", configItem.getReference() :",configItem.getReference());			
			configManager.dereferenceInventoryConfigurationItems(configItems);
			debug(log,"Resource dereferenced from ServiceConfigItem: " , configItemName);
		}else{
			debug(log,"No resource allocation/refernce found on " , configItemName);
		}
		debug(log,"unallocateResourceToConfigItem(configItem) - END.");
		return configItem;
	}

	/**
	 * This method refers the resource to the config item.
	 * @param config
	 * @param configItem
	 * @param resource
	 * @throws ValidationException
	 */
	/*public static void referResourceToConfigItem(ServiceConfigurationVersion config, ServiceConfigurationItem configItem, ConfigurationReferenceEnabled resource) throws ValidationException{
		debug(log,"referResourceToConfigItem - START.");
		BaseConfigurationManager configManager = PersistenceHelper.makeConfigurationManager(config.getClass());
		configManager.referenceEntity(configItem, resource);
		debug(log,"referResourceToConfigItem - END.");
	}*/

	/**
	 * This method will search the Service by ExternalObjectID from the input
	 * @param extObjId
	 * @param Service	 
	 * @throws ValidationException
	 */
	public static Service getServiceByExternalObjectId(String extObjId) throws ValidationException{
		debug(log,"getServiceByExternalObjectId - START");  
		if(Utils.isEmpty(extObjId)) {
			return null;
		}
		ServiceManager serviceMgr = PersistenceHelper.makeServiceManager();
		ServiceSearchCriteria serviceSearch = serviceMgr.makeServiceSearchCriteria();
		CriteriaItem crit = serviceSearch.makeCriteriaItem();
		crit.setOperator(CriteriaOperator.EQUALS);
		crit.setValue(extObjId);
		serviceSearch.setExternalObjectId(crit);
		List<Service> results = serviceMgr.findServices(serviceSearch);
		debug(log,"getServiceByExternalObjectId - END");
		return !Utils.isEmpty(results) ? results.get(0) : null;
	}	

	/**
	 * Method to get resource assignments
	 * @param consumerType
	 * @param resourceType
	 * @param resource
	 * @return Collection<Assignment>
	 */
	public static Collection<Assignment> getConsumers(Class<? extends Persistent> consumerType, 
			Class<? extends ConsumableResource> resourceType, ConsumableResource resource) {
		debug(log,"getConsumers - START");
		Collection<Assignment> assignments = new ArrayList<>();
		try {
			AssignmentSearchCrteria criteria = PersistenceHelper.makeAssignmentManager().makeAssignmentSearchCrteria();
			criteria.setConsumerType(consumerType);
			criteria.setResourceClass(resourceType);
			criteria.setResource(resource);
			assignments = PersistenceHelper.makeAssignmentManager().findAssignment(criteria);
			if (Utils.checkNull(assignments)) {
				assignments = new ArrayList<>();
			}	
		}
		catch (Exception e){
			log.error("", Constants.EXCEPTION_DELIMITTER+e.getMessage(),e);
		}
		debug(log,"getConsumers - END");
		return assignments;
	}

	/**
	 * This method finds the given place based on name and spec.
	 * @param specName
	 * @param placeName
	 * @param charMap
	 * @return GeographicPlace
	 * @throws ValidationException
	 */
	public static GeographicPlace getPlace(String specName, String name, Map<String,String> charMap) throws ValidationException {
		debug(log,"getPlace - START");
		PlaceManager placeManager = PersistenceHelper.makePlaceManager();
		PlaceSearchCriteria criteria = placeManager.makePlaceSearchCriteria();

		//Specification - Start
		if(null!=specName){
			debug(log,"Criteria Spec Name: "+ specName );
			PlaceSpecification placeSpec = EntityUtils.findSpecification(PlaceSpecification.class, specName);
			if (placeSpec == null) {
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			} else {
				PlaceSpecification[] specList = new PlaceSpecification[1];
				specList[0] = placeSpec;
				criteria.setPlaceSpecs(specList);
			}
		}
		//Specification - End

		//Characteristics - Start
		if(null!=charMap){
			debug(log,"Criteria CharMap Size: " + charMap.size());
			Collection<CriteriaItem> criteriaItems = new ArrayList<>();
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				debug(log,"Criteria Char: " + entry.getKey() + "=" + entry.getValue());
				if(!Utils.isEmpty(entry.getValue())){
					charCriteriaItem = criteria.makeCriteriaItem();
					charCriteriaItem.setCriteriaClass(PlaceCharacteristic.class);
					charCriteriaItem.setName(entry.getKey());
					charCriteriaItem.setValue(entry.getValue());
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
					criteriaItems.add(charCriteriaItem);
				}
			}
			criteria.addCharacteristicData(criteriaItems);
		}
		//Characteristics - End

		//Set Name - Start
		if(null!=name){
			debug(log,"Criteria Place Name: " + name);
			CriteriaItem nameCI = criteria.makeCriteriaItem();
			nameCI.setValue(name);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			criteria.setName(nameCI);
		}
		//Set Name - End

		List<GeographicPlace> placeList = placeManager.findGeographicPlaces(criteria, GeographicPlace.class);
		debug(log,"getPlace - END, Place Found: " + placeList.size());
		return !Utils.isEmpty(placeList) ? placeList.get(0) : null;
	}

	/**
	 * This method searches for Service by Characteristics and Specification
	 * 
	 * @param serviceId
	 * @param serviceName
	 * @param specName
	 * @param charMap
	 * @param serviceStatus
	 * @param placeName
	 * @return List<Service>
	 * @throws ValidationException
	 */
	public static List<Service> findService(String serviceExternalObjectId, String serviceName, String specName, Map<String,String> charMap, ServiceStatus serviceStatus, String placeName) throws ValidationException {	
		debug(log,"findService - START, Spec: "+ specName);
		
		oracle.communications.inventory.api.service.ServiceManager serManager = PersistenceHelper.makeServiceManager();
		ServiceSearchCriteria serviceSearchCriteria = serManager.makeServiceSearchCriteria();
		
		//Set Name
		if(serviceExternalObjectId != null) {
			debug(log,"serviceExternalObjectId="+serviceExternalObjectId);
			CriteriaItem nameCriteriaItem = serviceSearchCriteria.makeCriteriaItem();
			nameCriteriaItem.setValue(serviceExternalObjectId);
			nameCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			serviceSearchCriteria.setExternalObjectId(nameCriteriaItem);
		}
				
		//Set Name
		if(serviceName != null) {
			debug(log,"serviceName="+serviceName);
			CriteriaItem nameCriteriaItem = serviceSearchCriteria.makeCriteriaItem();
			nameCriteriaItem.setValue(serviceName);
			nameCriteriaItem.setOperator(CriteriaOperator.CONTAINS_IGNORE_CASE);
			serviceSearchCriteria.setName(nameCriteriaItem);
		}
		
		//Set Specification
		if(null!=specName){
			debug(log,"specName="+specName);
			try {
				ServiceSpecification serviceSpec = EntityUtils.findSpecification(ServiceSpecification.class, specName);
				if(serviceSpec != null) {
					serviceSearchCriteria.setServiceSpecification(serviceSpec);
				}
			} catch (Exception e){
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			}
		}

		//Set Characteristics
		if(null!=charMap){
			Collection<CriteriaItem> criteriaItems = new ArrayList<>();
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = serviceSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(ServiceCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			serviceSearchCriteria.addCharacteristicData(criteriaItems);
		}
		
		//Set Assignment State
		if(null!=serviceStatus){
			debug(log,"serviceStatus="+serviceStatus.getValue());
			CriteriaItem assignmentStateCriteriaItem = serviceSearchCriteria.makeCriteriaItem();
			assignmentStateCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			assignmentStateCriteriaItem.setValue(serviceStatus.getValue());
			serviceSearchCriteria.setAdminState(assignmentStateCriteriaItem); //Not able to set the criteria on ServiceStatus
		}
		
		//Set Place Name Associated with Service
		if(null!=placeName) {
			PlaceManager placeManager = PersistenceHelper.makePlaceManager();
			RelPlaceSearchCriteria relPlaceSearchCriteria = placeManager.makeRelPlaceSearchCriteria();
			CriteriaItem placeNameCriteriaItem = relPlaceSearchCriteria.makeCriteriaItem();
			placeNameCriteriaItem.setValue(placeName);
			placeNameCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			relPlaceSearchCriteria.setName(placeNameCriteriaItem);
			serviceSearchCriteria.setRelPlaceSearchCriteria(relPlaceSearchCriteria);
		}
		
		List<Service> services = serManager.findServices(serviceSearchCriteria);	
		debug(log,"findService - END, Services Found: "+ services.size());
		return services;
	}

	/**
	 * This method will search the Consumable resource
	 * @param consumer
	 * @param resourceClass
	 * @return ConsumableResource
	 * @throws ValidationException
	 * 
	 * Get consumer resource by using consumer and ConsumableResource class.
	 */
	public static ConsumableResource getConsumedResource(EntityConsumer consumer,	Class<? extends ConsumableResource> resourceClass) throws ValidationException{
		debug(log,"getConsumedResource - START");

		AssignmentManager aMgr = PersistenceHelper.makeAssignmentManager();
		ConsumableResource resource = null;
		AssignmentSearchCrteria criteria = aMgr.makeAssignmentSearchCrteria();
		criteria.setConsumer(consumer);
		criteria.setResourceClass(resourceClass);
		try {
			Collection<Assignment> assignments = aMgr.findAssignment(criteria);		
			for (Assignment assignment : assignments) {
				if (assignment.getResource().getClass().getSimpleName().contains(resourceClass.getSimpleName())) {					
					resource = assignment.getResource();
					break;
				}
			}			
		} catch (Exception e) {
			log.error("", Constants.EXCEPTION_DELIMITTER+e.getMessage(),e);
		}

		debug(log,"getConsumedResource - END");
		return resource;
	}

	/**
	 * This method returns the property value of a given property name.
	 * @param propertyName
	 * @return String
	 * @throws ValidationException
	 */
	public static String getSystemConfigPropertyValue(String propertyName) throws ValidationException{
		debug(log,"getSystemConfigPropertyValue - START");
		CustomObject co = DesignHelper.makeDesignManager().findCustomObjectByName(Constants.SPEC_SYSTEMCONFIG);
		if(null==co){
			throw new ValidationException("System Config Custom Object not found.");
		}
		String propValue = UimHelper.getEntityCharValue(co, propertyName); 
		if(Utils.isEmpty(propValue)) {
			throw new ValidationException("Value not found for System Config Property: " + propertyName);
		}
		debug(log,"getSystemConfigPropertyValue - END");
		return propValue;
	}

	/**
	 * Find the TelephoneNumber based on spec name, phone number and chars.
	 * 
	 * @param tnSpec
	 * @param name
	 * @param value
	 * @param criteriaOperator
	 * @return List<TelephoneNumber> 
	 * @throws ValidationException
	 */
	public static List<TelephoneNumber> findTelephoneNumbers(String specName, String phoneNumber, Map<String, String> charMap, CriteriaOperator criteriaOperator, String assignmentState, String adminState, String reservedForUser) throws  ValidationException {
		debug(log,"findTelephoneNumbers - START");

		TelephoneNumberManager manager = PersistenceHelper.makeTelephoneNumberManager();
		TelephoneNumberSearchCriteria criteria =  manager.makeTelephoneNumberSearchCriteria();

		//Set Specification
		if(null!=specName){
			try {
				TelephoneNumberSpecification spec = EntityUtils.findSpecification(TelephoneNumberSpecification.class, specName);
				if(spec != null) {
					criteria.setTelephoneNumberSpecification(spec);
				}
			} catch (Exception e){
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			}
		}

		//Set Name
		if(!Utils.checkBlank(phoneNumber)) {
			CriteriaItem phoneNumberCriteriaItem = criteria.makeCriteriaItem();
			phoneNumberCriteriaItem.setOperator(criteriaOperator);
			phoneNumberCriteriaItem.setValue(phoneNumber);
			criteria.setName(phoneNumberCriteriaItem);
		}
		
		//Set Inventory State
		if(null != adminState)
		{
			CriteriaItem adminStateCriteriaItem = criteria.makeCriteriaItem();
			adminStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			adminStateCriteriaItem.setValue(adminState);
			criteria.setAdminState(adminStateCriteriaItem);
		}
		
		//Set Assignment State
		if(null !=assignmentState)
		{
			CriteriaItem assignmentStateCriteriaItem = criteria.makeCriteriaItem();
			assignmentStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			assignmentStateCriteriaItem.setValue(assignmentState);
			criteria.setAssignmentType(assignmentStateCriteriaItem);
		}

		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(TNCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}
		
		//Set ReservedFor
		if(!Utils.checkBlank(reservedForUser)) {
			CriteriaItem reservedForCriteriaItem = criteria.makeCriteriaItem();
			reservedForCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			reservedForCriteriaItem.setValue(reservedForUser);
			criteria.setReservedFor(reservedForCriteriaItem);
		} else {
			criteria.setExcludeReservations(true);//By default exclude reserved TNs if ReservedFor criteria is not provided.
		}
		
		List<TelephoneNumber> tnList = manager.findTelephoneNumbers(criteria);
		debug(log,"findTelephoneNumbers - END, TNs Found: " + tnList.size());
		return tnList;
	}
	
	/**
	 * Find the LogicalDevice based on spec name, phone number and chars.
	 * 
	 * @param ldSpec
	 * @param name
	 * @param value
	 * @param criteriaOperator
	 * @return List<LogicalDevice>
	 * @throws ValidationException
	 */
	public static List<LogicalDevice> findLogicalDevices(String specName, String name, Map<String, String> charMap,
			CriteriaOperator criteriaOperator, AssignmentState assignmentState, String adminState, String reservedForUser) throws  ValidationException {
		debug(log,"findLogicalDevices - START");

		LogicalDeviceManager manager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria criteria =  manager.makeLogicalDeviceSearchCriteria();

		//Set Specification
		if(null!=specName){
			try {
				LogicalDeviceSpecification spec = EntityUtils.findSpecification(LogicalDeviceSpecification.class, specName);
				debug(log,"spec : " + spec);
				if(spec != null) {
					criteria.setLogicalDeviceSpecification(spec);
				}
			} catch (Exception e){
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			}
		}

		//Set Name
		if(null!=name) {
			CriteriaItem nameCriteriaItem = criteria.makeCriteriaItem();
			nameCriteriaItem.setOperator(criteriaOperator);
			nameCriteriaItem.setValue(name);
			criteria.setName(nameCriteriaItem);
		}

		debug(log,"Line no 1268");

		//Set Assignment State
		if(null !=assignmentState)
		{
			criteria.setAssignmentState(assignmentState);
		}
		
		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			debug(log,"Inside charMap");

			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				debug(log,"Line no 1284");

				charCriteriaItem = criteria.makeCriteriaItem();
				debug(log,"Line no 1287");

				charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
				debug(log,"Line no 1290");
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);

			}
			debug(log,"Line no 1295");

			criteria.addCharacteristicData(criteriaItems);
			debug(log,"Line no 1292");

		}

		debug(log,"Line no 1294");

		List<LogicalDevice> ldList = manager.findLogicalDevice(criteria);
		debug(log,"Line no 1292");

		debug(log,"findLogicalDevices - END, LDs Found: " + ldList.size());
		return ldList;
	}
	
	/**
	 * Find the DeviceInterface based on spec name, phone number and chars.	 * 
	 * @param specName
	 * @param name
	 * @param charMap
	 * @param criteriaOperator
	 * @return List<DeviceInterface>
	 * @throws ValidationException
	 */
	public static List<DeviceInterface> findDeviceInterface(String specName, String name, Map<String, String> charMap, CriteriaOperator criteriaOperator, 
			AssignmentState assignmentState) throws  ValidationException {
		debug(log,"findDeviceInterface - START");

		LogicalDeviceManager manager = PersistenceHelper.makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria criteria =  manager.makeDeviceInterfaceSearchCriteria();

		//Set Specification
		if(null!=specName){
			try {
				DeviceInterfaceSpecification spec = EntityUtils.findSpecification(DeviceInterfaceSpecification.class, specName);
				if(spec != null) {
					criteria.setDeviceInterfaceSpecification(spec);
				}
			} catch (Exception e){
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			}
		}

		//Set Name
		if(null!=name) {
			CriteriaItem nameCriteriaItem = criteria.makeCriteriaItem();
			nameCriteriaItem.setOperator(criteriaOperator);
			nameCriteriaItem.setValue(name);
			criteria.setName(nameCriteriaItem);
		}

		//Set Assignment State
		if(null !=assignmentState)
		{
			criteria.setAssignmentState(assignmentState);
		}
		
		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(TNCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

	
		List<DeviceInterface> diList = manager.findDeviceInterface(criteria);
		debug(log,"findDeviceInterface - END, DIs Found: " + diList.size());
		return diList;
	}
	
	/**
	 * This method retrieves an available subnet based on service type 
	 * @param ipSubnetGroup
	 * @param suffix
	 * @return IPSubnet
	 * @throws ValidationException
	 */
	public static IPSubnet getIPSubnetByIGNamenSuffix(String ipSubnetGroup, String suffix) throws ValidationException{
		debug(log,"getIPSubnetByServiceType - START, serviceType: " + ipSubnetGroup);
		String igName = "";

		DesignManager designManager = DesignHelper.makeDesignManager();
		InventoryGroup ig = designManager.findInventoryGroupByName(ipSubnetGroup);
		if(null==ig){
			debug(log,"Inventory Group named: "+igName+" doesn't exist in UIM.");
			throw new ValidationException("IP Address Pool not found for " + igName);
		} else {
			igName = ig.getName();
			debug(log,"Inventory Group Found:" + ig.getName());
			InventoryGroupManager igManager = PersistenceHelper.makeInventoryGroupManager();
			InventoryGroupEntitySearchCriteria igCriteria = igManager.makeInventoryGroupEntitySearchCriteria();
			igCriteria.setInventoryGroup(ig);
			igCriteria.setEntityClass(IPSubnet.class);
			igCriteria.setRange(0, 100);
			
			
			List<InvGroupRef> invGroupRefs = igManager.findInvGroupRefsForInventoryGroup(igCriteria);
			debug(log,"invGroupRefs found: "+ invGroupRefs.size());
			InvGroupRef invGroupRef =null;
			//3-17418331646   2   27460959   Enhancement   The utilization   of the parent subnet/network is not changing. It remains 0.0.
			IPSubnet mainSubnet = null;
			Collection<IPSubnet> ipSubnets = null;
			for (InvGroupRef igRef :invGroupRefs) {
				mainSubnet = igRef.getIPSubnet();
				ipSubnets = findIPSubnet(mainSubnet.getIpAddressDomain().getName(), mainSubnet, suffix, ig);
				if(!Utils.isEmpty(ipSubnets)) {
					invGroupRef = igRef;
					break;
				}
				debug(log,"ipSubnet.getAvailableHostCount(): "+mainSubnet.getAvailableHostCount());
			}
			
			IPSubnet childIPSubnet = null;
			if(mainSubnet != null && !Utils.isEmpty(ipSubnets)){
				childIPSubnet = ipSubnets.iterator().next();
				debug(log,"childIPSubnet found: "+childIPSubnet.getNetworkAddressAsString());
			}
	
			debug(log,"getIPSubnetByServiceType - END");
			return childIPSubnet;
		}
	}

	/**
	 * This method finds the IP Subnet of a given prefix length and a given IP Range
	 * @param specName
	 * @param phoneNumber
	 * @param charMap
	 * @param criteriaOperator
	 * @return Collection<IPSubnet> 
	 * @throws ValidationException
	 */
	public static Collection<IPSubnet> findIPSubnet(String nadName, IPSubnet mainIPSubnet, String prefix, InventoryGroup ig) throws  ValidationException {
		debug(log,"findIPSubnet - START, Domain: "+nadName+ " , Starting IP: "+ mainIPSubnet + " , Prefix: " + prefix);
		
		IPNetworkManager manager = PersistenceHelper.makeIPNetworkManager();
		IPSubnetSearchCriteria criteria =  manager.makeIPv4SubnetSearchCriteria();
		IPSubnet parentIPSubnet = null;
		debug(log,"mainIPSubnet.getSpecification().getName(): "+mainIPSubnet.getSpecification().getName());
		if(mainIPSubnet.getSpecification().getName().equals("IPv4Network"))
		{
			Collection<IPSubnet> childSubnets = manager.getChildSubnets(mainIPSubnet);
			
			debug(log,"childSubnets: "+childSubnets);
			
			parentIPSubnet = childSubnets.iterator().next();
		}
		else
		{
			parentIPSubnet = mainIPSubnet;
			
		}
		
		NetworkAddressDomain ipAddressDomain = parentIPSubnet.getIpAddressDomain();
		
		debug(log,"ipSubnet.getAvailableHostCount(): "+parentIPSubnet.getAvailableHostCount());
		debug(log,"parentIPSubnet.getParentBlock() : "+parentIPSubnet.getParentBlock());
		debug(log,"parentIPSubnet.getIpAddressDomain() : "+ipAddressDomain);
		debug(log,"parentIPSubnet.getNetworkTargets() : "+parentIPSubnet.getNetworkTargets());
		debug(log,"parentIPSubnet.getRootParentBlock().getIpAddressDomain().getName() : "+parentIPSubnet.getRootParentBlock().getIpAddressDomain().getName());
		debug(log,"ipSubnet: "+parentIPSubnet.getShortNotationEndAddress());		
		debug(log,"parentIPSubnet: "+parentIPSubnet);	

		//Set Prefix
		CriteriaItem prefixCI = criteria.makeCriteriaItem();
		int intPrefix = Integer.parseInt(prefix);
		prefixCI.setValue(intPrefix);
		prefixCI.setOperator(CriteriaOperator.EQUALS);
		criteria.setPrefixLengthFrom(prefixCI);

		//Set Assignment State
		criteria.serAssignmentState(AssignmentState.UNASSIGNED);
		
		if(ipAddressDomain != null)
		{
			CriteriaItem networkDomain = criteria.makeCriteriaItem();
			networkDomain.setValue(ipAddressDomain.getName());
			networkDomain.setOperator(CriteriaOperator.EQUALS);
			criteria.setIPAddressDomainName(networkDomain);
		}
			
		//Set Starting IP Address Range
		CriteriaItem parent = criteria.makeCriteriaItem();
		parent.setValue(parentIPSubnet);
		parent.setOperator(CriteriaOperator.EQUALS);
		criteria.setParentBlock(parent);

		Collection<IPSubnet> ipSubnets = null;
		ipSubnets = manager.findNetworkAddressBlocks(criteria);

		debug(log,"findIPSubnet - END, Found: " + ipSubnets.size());

		return ipSubnets;
	}

	/**
	 * This method returns the first available VLAN or given VLAN.
	 * 
	 * @param specName
	 * @param name
	 * @param invGroup
	 * @param assignmentState
	 * @param identifier
	 * @param nad
	 * @return FlowIdentifier
	 * @throws ValidationException
	 */
	public static FlowIdentifier findFlowIdentifier(String specName, String name, InventoryGroup invGroup, AssignmentState assignmentState, String identifier, NetworkAddressDomain nad) throws ValidationException {
		debug(log,"findFlowIdentifier - START");

		FlowIdentifierManager flowIdentifierManager = PersistenceHelper.makeFlowIdentifierManager();
		FlowIdentifierSearchCriteria flowIdentifierSearchCriteria = flowIdentifierManager.makeFlowIdentifierSearchCriteria();
		
		// Set name
		if(!Utils.checkNull(name)) {
			debug(log,"Set name="+name);
			CriteriaItem nameCI = flowIdentifierSearchCriteria.makeCriteriaItem();
			nameCI.setValue(name);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			flowIdentifierSearchCriteria.setName(nameCI);
		}
		
		//Set Identifier
		if (!Utils.checkNull(identifier)) {
			debug(log,"Set Identifier="+identifier);
			CriteriaItem identifierCI = flowIdentifierSearchCriteria.makeCriteriaItem();
			identifierCI.setValue(Integer.valueOf(identifier));
			identifierCI.setOperator(CriteriaOperator.EQUALS);
			flowIdentifierSearchCriteria.setIdentifier(identifierCI);
		} else {
			debug(log,"Search Identifier Greather Than 1 as 0 and 1 are not usable CVLANs.");
			CriteriaItem identifierCI = flowIdentifierSearchCriteria.makeCriteriaItem();
			identifierCI.setValue(1);
			identifierCI.setOperator(CriteriaOperator.GREATER_THAN);
			flowIdentifierSearchCriteria.setIdentifier(identifierCI);
		}

		//Set Network Address Domain
		if (!Utils.checkNull(nad)) {
			debug(log,"Set Network Address Domain="+nad.getName());
			flowIdentifierSearchCriteria.setNetworkAddressDomain(nad);
		}

		//Set Assignment State
		if(!Utils.checkNull(assignmentState)){
			debug(log,"Set Assignment State="+assignmentState.getValueAsString());
			flowIdentifierSearchCriteria.setAssignmentState(assignmentState);
		}

		//Set Specification
		FlowIdentifierSpecification flowIdentifierSpecification = EntityUtils.findSpecification(FlowIdentifierSpecification.class, specName);
		if(!Utils.checkNull(flowIdentifierSpecification)) {
			debug(log,"Set Specification="+flowIdentifierSpecification.getName());
			flowIdentifierSearchCriteria.setFlowIdentifierSpecification(flowIdentifierSpecification);
		}

		//Set InventoryGroup
		if (!Utils.checkNull(invGroup)) {
			debug(log,"Set Inventory Group=" + invGroup.getName());			
			List<InventoryGroup> invGroupList = new ArrayList<>();
			invGroupList.add(invGroup);
			flowIdentifierSearchCriteria.setInventoryGroup(invGroupList);
		}


		List<FlowIdentifier> flowIdentifierList = flowIdentifierManager.findFlowIdentifier(flowIdentifierSearchCriteria);
		if(Utils.isEmpty(flowIdentifierList)){
			log.validationException("FI.vlanNotFound", new java.lang.IllegalStateException());
		}
		flowIdentifierList = sortVlanByIdentifier(flowIdentifierList);
		debug(log,"FlowIdentifiers list size :",flowIdentifierList.size());

		debug(log,"findFlowIdentifier - END");		
		return (!Utils.isEmpty(flowIdentifierList))? flowIdentifierList.get(0) : null;
	}

	/**
	 * This method finds the first available ONT Id from the given ONT Domain.
	 * @param invGroup
	 * @param assignmentState
	 * @param identifier
	 * @param nad
	 * @return FlowIdentifier
	 * @throws ValidationException
	 */
	public static FlowIdentifier getOntId(String nadName) throws ValidationException {
		debug(log,"getOntId - START");

		FlowIdentifierManager flowIdentifierManager = PersistenceHelper.makeFlowIdentifierManager();
		FlowIdentifierSearchCriteria flowIdentifierSearchCriteria = flowIdentifierManager.makeFlowIdentifierSearchCriteria();
		NetworkAddressDomain nad = null;

		//Set Network Address Domain
		if (!Utils.checkNull(nadName)) {
			nad = UimHelper.findNetworkAddressDomain(nadName, CriteriaOperator.EQUALS);
			if(nad == null)
				throw new ValidationException("Network Address Domain Not found with name "+nadName);
			debug(log,"Set Network Address Domain :",nad.getName());
			flowIdentifierSearchCriteria.setNetworkAddressDomain(nad);
		}

		flowIdentifierSearchCriteria.setAssignmentState(AssignmentState.UNASSIGNED);
		flowIdentifierSearchCriteria.setAdminState(InventoryState.INSTALLED);

		//Set Specification
		FlowIdentifierSpecification flowIdentifierSpecification = EntityUtils.findSpecification(FlowIdentifierSpecification.class, Constants.SPEC_ONTID);
		if(Utils.checkNull(flowIdentifierSpecification)) {
			debug(log,"Set Specification :",flowIdentifierSpecification.getName());
			flowIdentifierSearchCriteria.setFlowIdentifierSpecification(flowIdentifierSpecification);
		}

		List<FlowIdentifier> flowIdentifierList = flowIdentifierManager.findFlowIdentifier(flowIdentifierSearchCriteria);
		if(Utils.isEmpty(flowIdentifierList)){
			log.validationException("FI.ontIdNotFound", new java.lang.IllegalStateException());
		}
		flowIdentifierList = sortVlanByIdentifier(flowIdentifierList);
		debug(log,"FlowIdentifiers list size :",flowIdentifierList.size());

		debug(log,"getOntId - END , Flow Identifiers Found :" , flowIdentifierList.size());
		return !Utils.isEmpty(flowIdentifierList) ? flowIdentifierList.get(0) : null;
	}

	/**
	 * This method will sort the Flow Identifiers by Identifier as API is not returning the sorted set.
	 * @param vlanList
	 * @return List<FlowIdentifier>
	 */
	public static List<FlowIdentifier> sortVlanByIdentifier(List<FlowIdentifier> vlanList){
		debug(log,"sortVlanByIdentifier - START");
		TreeMap<Integer, FlowIdentifier> sortedVlanMap = new TreeMap<>();

		for(FlowIdentifier fi : vlanList){
			sortedVlanMap.put(fi.getIdentifier(), fi);
		}

		List<FlowIdentifier> vlanSortedList = new ArrayList<>(sortedVlanMap.values());
		debug(log,"sortVlanByIdentifier - END");
		return vlanSortedList;
	}

	/**
	 * This method returns the ServiceType from Service Spec
	 * @param serviceSpec - PSTN_CFS
	 * @return PSTN
	 */
	public static String getServiceTypeFromServiceSpec(String serviceSpec){
		debug(log,"getServiceTypeFromServiceSpec - START");
		debug(log,"getServiceTypeFromServiceSpec - END");
		return serviceSpec.substring(0, serviceSpec.indexOf(Constants.UNDERSCORE));
	}

	/**
	 * Find the TelephoneNumber based on criteria.
	 * 
	 * @param tnSpec
	 * @param name
	 * @param value
	 * @param criteriaOperator
	 * @return List<TelephoneNumber>
	 * @throws ValidationException
	 */
	public static List<TelephoneNumber> findTNRange(String tnSpecName, String startRange, String endRange,
			Map<String, String> charMap, Boolean excludeReservations) throws  ValidationException 
	{
		debug(log,"findTNRange - START");
		TelephoneNumberManager manager = null;
		TelephoneNumberSearchCriteria criteria = null;
		List<TelephoneNumber> tnList = null;

		manager = PersistenceHelper.makeTelephoneNumberManager();
		criteria = manager.makeTelephoneNumberSearchCriteria();

		//Set Specification
		TelephoneNumberSpecification spec = EntityUtils.findSpecification(TelephoneNumberSpecification.class, tnSpecName);
		criteria.setTelephoneNumberSpecification(spec);

		//Set Range From
		CriteriaItem rangeFromCriteriaItem = criteria.makeCriteriaItem();
		rangeFromCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		rangeFromCriteriaItem.setValue(startRange);
		criteria.setRangeFrom(rangeFromCriteriaItem);

		//Set Range To
		CriteriaItem rangeToCriteriaItem = criteria.makeCriteriaItem();
		rangeToCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		rangeToCriteriaItem.setValue(endRange);
		criteria.setRangeTo(rangeToCriteriaItem);

		//Set InventoryState to INSTALLED
		CriteriaItem adminStateCriteriaItem = criteria.makeCriteriaItem();
		adminStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		adminStateCriteriaItem.setValue(InventoryState.INSTALLED.toString());
		criteria.setAdminState(adminStateCriteriaItem);

		//Set Assignment State to UNASSIGNED
		CriteriaItem assignmentStateCriteriaItem = criteria.makeCriteriaItem();
		assignmentStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		assignmentStateCriteriaItem.setValue(AssignmentState.UNASSIGNED.toString());
		criteria.setAssignmentType(assignmentStateCriteriaItem);

		//Set Characteristic 
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(TNCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

		//Set Exclude Reservations
		criteria.setExcludeReservations(excludeReservations);

		//Set Disable Ordering TRUE
		criteria.setDisableOrdering(true);

		tnList = manager.findTelephoneNumbers(criteria);
		debug(log,"findTNRange - END");
		return tnList;
	}

	/**
	 * This Method will create the Service the Service and assign Lis of TelephoneNumbers to services. Method can be used to assign the
	 * TNs to DIDBlock Service
	 * @param tnList
	 * @param SrvSpecification
	 * @param SrvCongVerSpecification
	 * @param name
	 * @param charMap
	 * @return Service
	 * @throws ValidationException
	 */

	public static Service createServiceAndAssignTN(List<TelephoneNumber> tnList, String srvSpecification, String srvCongVerSpecification,String name,Map<String,String> charMap) throws ValidationException{
		debug(log,"createServiceAndAssignTN - START");

		//Make Service 
		ServiceManager serviceManager = PersistenceHelper.makeServiceManager();
		List<Service> serviceToBeCreatedList = new ArrayList<>();
		Service service = serviceManager.makeService(Service.class);
		ServiceSpecification spec = PersistenceHelper.makeSpecManager().findSpecification(ServiceSpecification.class, srvSpecification);
		service.setSpecification(spec);
		service.setName(name);
		//SEt Chars
		if(charMap!=null && charMap.size()>0){
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				EntityHelper.setValue(service, entry.getKey(), entry.getValue());
			}
		}

		//Create Service
		serviceToBeCreatedList.add(service);
		debug(log,"serviceToBeCreatedList: size"+serviceToBeCreatedList.size());
		List<Service> serviceList = serviceManager.createService(serviceToBeCreatedList);

		//Create service Configuration
		ServiceConfigurationVersion scv = createServiceConfiguration(serviceList.get(0), srvCongVerSpecification);
		//assign Tn to Service configuration
		assignTN(scv,tnList);

		debug(log,"createServiceAndAssignTN - END");
		return serviceList.get(0);
	}

	/**
	 * This method creates the service configuration version of the given service.
	 * @param Service - service
	 * @return ServiceConfigurationVersion
	 * @throws Exception
	 */
	public static ServiceConfigurationVersion createServiceConfiguration(Service service, String configSpecName) throws ValidationException {
		debug(log,"createServiceConfiguration - START");
		ServiceConfigurationVersion serviceVersion = null;
		try{
			BaseConfigurationManager configurationManager = PersistenceHelper.makeConfigurationManager(ServiceConfigurationVersion.class);
			InventoryConfigurationVersion configuration = configurationManager.makeConfigurationVersion(service);

			Specification configSpec = null;
			if(!Utils.checkNull(configSpecName)){
				configSpec = PersistenceHelper.makeSpecManager().findSpecification(InventoryConfigurationSpec.class, configSpecName);
			}else{
				ConfigurationManager cm = PersistenceHelper.makeConfigurationManager();
				List<InventoryConfigurationSpec> specs = cm.getConfigSpecTypeConfig(service.getSpecification(), true);
				configSpec = specs.get(0);
			}

			if(Utils.isEmpty(service.getConfigurations())) {
				configuration.setVersionNumber(0);
			}else {
				configuration.setVersionNumber(service.getConfigurations().size() + 1);
			}

			if (configSpec instanceof InventoryConfigurationSpec) {                                                            //No need to check for Null before instanceof.
				configuration.setConfigSpec((InventoryConfigurationSpec) configSpec);
			}
			configuration.setEffDate(new Date());

			if(!Utils.isEmpty(service.getConfigurations())) {
				serviceVersion = (ServiceConfigurationVersion) configurationManager.createConfigurationVersion(service, configuration);               
			} else {
				serviceVersion = (ServiceConfigurationVersion) configurationManager.createConfigurationVersion(service, configuration, (InventoryConfigurationSpec) configSpec);            
			}
		}catch(Exception ex){
			log.error("", Constants.EXCEPTION_DELIMITTER+ex.getMessage(),ex);
			throw new ValidationException(ex.getMessage());
		}
		debug(log,"createServiceConfiguration - END");
		return serviceVersion;
	}

	/**
	 * This method creates the logical device configuration version of the given device.
	 * @param device
	 * @param configSpecName
	 * @return LogicalDeviceConfigurationVersion
	 * @throws ValidationException
	 */
	public static LogicalDeviceConfigurationVersion createLogicalDeviceConfiguration(LogicalDevice device, String configSpecName) throws ValidationException {
		debug(log,"createLogicalDeviceConfiguration - START");
		LogicalDeviceConfigurationVersion config = null;
		try{
			BaseConfigurationManager configurationManager = PersistenceHelper.makeConfigurationManager(LogicalDeviceConfigurationVersion.class);
			InventoryConfigurationVersion configuration = configurationManager.makeConfigurationVersion(device);

			Specification configSpec = null;
			if(!Utils.checkNull(configSpecName)){
				configSpec = PersistenceHelper.makeSpecManager().findSpecification(InventoryConfigurationSpec.class, configSpecName);
			}else{
				ConfigurationManager cm = PersistenceHelper.makeConfigurationManager();
				List<InventoryConfigurationSpec> specs = cm.getConfigSpecTypeConfig(device.getSpecification(), true);
				configSpec = specs.get(0);
			}

			if(Utils.isEmpty(device.getConfigurations())) {
				configuration.setVersionNumber(0);
			}else {
				configuration.setVersionNumber(device.getConfigurations().size() + 1);
			}

			if (configSpec instanceof InventoryConfigurationSpec) {                                                            //No need to check for Null before instanceof.
				configuration.setConfigSpec((InventoryConfigurationSpec) configSpec);
			}
			configuration.setEffDate(new Date());

			if(!Utils.isEmpty(device.getConfigurations())) {
				config = (LogicalDeviceConfigurationVersion) configurationManager.createConfigurationVersion(device, configuration);               
			} else {
				config = (LogicalDeviceConfigurationVersion) configurationManager.createConfigurationVersion(device, configuration, (InventoryConfigurationSpec) configSpec);            
			}
		}catch(Exception ex){
			log.error("", Constants.EXCEPTION_DELIMITTER+ex.getMessage(),ex);
		}
		debug(log,"createLogicalDeviceConfiguration - END");
		return config;
	}

	/**
	 * This method will assign the given TN to the FixedLineService SCV
	 * @param scv
	 * @return
	 * @throws ValidationException
	 */
	public static void assignTN(ServiceConfigurationVersion scv, List<TelephoneNumber> tnList) throws ValidationException {
		debug(log,"assignTN - START"); 

		oracle.communications.inventory.techpack.common.ServiceManager serviceManager = CommonHelper.makeServiceManager();
		debug(log,"tnList.size() "+ tnList.size());
		for(TelephoneNumber tn : tnList)
		{
			debug(log,"Inside For for each TN  "+ tn );
			ServiceConfigurationItem sci = serviceManager.addChildConfigItem(scv, (ServiceConfigurationItem)scv.getConfigItemTypeConfig(), Constants.PARAM_FIXEDLINENUMBER_CI);
			UimHelper.assignResourceToConfigItem(scv, sci, tn);
		}

		debug(log,"assignTN - END"); 
	}

	/**
	 * This method returns the L2/L3 based on the medium.
	 * Used only in ST, CV and ISDN service.
	 * @param medium
	 * @return String
	 * @throws ValidationException
	 */
	public static String getTechnologyBasedOnMedium(String medium) throws ValidationException
	{
		if(Utils.isEmpty(medium)) {
			throw new ValidationException("Medium is required to determine Technology: L2/L3.");
		}
		if(medium.contentEquals("AGG")) //Constants.EnumMediumType.AGG.toString()))
			return Constants.L3;
		else if(medium.contentEquals(Constants.EnumMediumType.Copper.toString()) || medium.contentEquals(Constants.EnumMediumType.Fibre.toString()))
			return Constants.L2;
		return null;
	}

	/**
	 * This method is used to switch from Current BI Context to Live Context 
	 * @return - The original BI which was switched.
	 */
	public static BusinessInteraction switchToLiveContext(){
		debug(log,"switchToLiveContext -  START");		
		BusinessInteractionManager biMgr = PersistenceHelper.makeBusinessInteractionManager();
		BusinessInteraction currentBI = (BusinessInteraction) UserEnvironmentFactory.getBusinessInteraction();
		debug(log,"Current BI Context Id=" , currentBI.getId() ,", Name :" , currentBI.getName());		
		debug(log,"Switching from BI Context to Live Context.");
		biMgr.switchContext(currentBI, null);		
		debug(log,"switchToLiveContext -  END");
		return currentBI;
	}

	/**
	 * This method is used to switch to ServiceOrder BI Context before update entity characteristics so they can be rolled back in case of BI Cancellation.
	 * @param rfsConfig
	 * @return BusinessInteraction- The original BI which was switched.
	 */
	public static BusinessInteraction switchToServiceOrderBI(ServiceConfigurationVersion rfsConfig){
		debug(log,"switchToServiceOrderBI -  START");

		UserEnvironment env = UserEnvironmentFactory.getUserEnvironment();
		BusinessInteraction currentBI = (BusinessInteraction) env.getBusinessInteraction();

		if(currentBI!=null){
			debug(log,"Current BI Context: Id=" + currentBI.getId() + " , Name: " + currentBI.getName());
		} else{
			debug(log,"Currently in Live Context."); 
		}

		BusinessInteraction serviceOrderBI = BaseDesigner.getCurrentBIFromRFSConfiguration(rfsConfig);
		debug(log,"Switching to ServiceOrder BI Context...");
		PersistenceHelper.makeBusinessInteractionManager().switchContext(serviceOrderBI, null);
		BusinessInteraction newBiFromEnv  = (BusinessInteraction) env.getBusinessInteraction();
		debug(log,"The current BI after switching to ServiceOrder BI is: Id=" + newBiFromEnv.getId() + " , Name=" +newBiFromEnv.getName());

		debug(log,"switchToServiceOrderBI -  END");
		return currentBI;
	}

	/**
	 * This method returns the RFS from CFS.
	 * @param cfs
	 * @return Service
	 * @throws ValidationException
	 */
	public static Service getRfsFromCfs(Service cfs) throws ValidationException
	{
		debug(log,"getRfsFromCfs - START");
		ServiceConfigurationVersion config = getLatestServiceConfig(cfs);
		DesignManager designManager = DesignHelper.makeDesignManager();
		ServiceConfigurationItem rfsCI = designManager.aquireConfigItem(config, Constants.CI_RFS_HOLDER);
		ServiceAssignmentToServiceDAO assignment = (ServiceAssignmentToServiceDAO)rfsCI.getAssignment();
		Service rfs = assignment.getService();
		debug(log,"getRfsFromCfs - END, RFS Id=" + rfs.getId() + " , Name= " + rfs.getName());
		return rfs;
	}
	
	public static Service getResource(ServiceConfigurationItem configItem) throws ValidationException
	{
		debug(log,"getRfsFromCfs - START");
		ServiceAssignmentToServiceDAO assignment = (ServiceAssignmentToServiceDAO)configItem.getAssignment();
		debug(log,"getRfsFromCfs - END");
		return (assignment != null) ? assignment.getService() : null;
	}
	
	/**
	 * This method returns the RFS from CFS.
	 * @param cfs
	 * @return Service
	 * @throws ValidationException
	 */
	public static Service getCfsFromRfs(Service rfs) throws ValidationException
	{
		debug(log,"getCfsFromRfs - START");
		ServiceAssignment sa = rfs.getCurrentAssignment();
		Service cfs = null;
		if(sa != null) {
			cfs = (Service) sa.getConsumer();
		}
		debug(log,"cfs - " + cfs);
		debug(log,"getCfsFromRfs - END, RFS Id=" + rfs.getId() + " , Name= " + rfs.getName());
		return cfs;
	} 

	/**
	 * This method returns the latest service configuration version of the given service.
	 * @param service
	 * @return ServiceConfigurationVersion
	 */
	 public static ServiceConfigurationVersion getLatestServiceConfig(Service service){
			debug(log,"getLatestServiceConfig - START");
			debug(log,"service Name :" , service.getName());
			List<ServiceConfigurationVersion> configList = service.getConfigurations();
			ServiceConfigurationVersion config = null;
			
			for(int i=configList.size()-1; i>=0; i--) {
				config = configList.get(i);
				//for designed we are assuming this will never come to D&A
				debug(log,"Config Name :", config.getName() , ", Config State :" , config.getConfigState() , ", Version :" , config.getVersionId());
				if(!config.getConfigState().equals(ConfigurationStatus.CANCELLED) &&
						!config.getConfigState().equals(ConfigurationStatus.PENDING_CANCEL))	{
					debug(log,"getLatestServiceConfig - END, Config Version :", config.getVersionId());
					return config;
				}
			}					
			debug(log,"getLatestServiceConfig - END , config: " + config);
			return config;
		}

	/**
	 * This method finds and returns the config item under a parent config item.
	 * @param config
	 * @param ciName
	 * @param parentConfigItem
	 * @return ServiceConfigurationItem
	 * @throws ValidationException
	 */
	public static ServiceConfigurationItem getConfigItem(ServiceConfigurationVersion config, String ciName ,ServiceConfigurationItem parentConfigItem) {
		debug(log,"getConfigItem (ServiceConfigurationVersion) - START");
		if(null==parentConfigItem){
			parentConfigItem = (ServiceConfigurationItem)config.getConfigItemTypeConfig();
		}

		List<ServiceConfigurationItem> childConfigItems = parentConfigItem.getChildConfigItems();
		for(ServiceConfigurationItem sci: childConfigItems){
			if(sci.getName().equals(ciName)){
				debug(log,"getConfigItem (ServiceConfigurationVersion) - END, success");
				return sci;
			}
		}
		debug(log,"getConfigItem (ServiceConfigurationVersion) - END, null");
		return null;
	}

	/**
	 * This method finds and returns the config item under a parent config item.
	 * @param config
	 * @param ciName
	 * @param parentConfigItem
	 * @return LogicalDeviceConfigurationItem
	 * @throws ValidationException
	 */
	public static LogicalDeviceConfigurationItem getConfigItem(LogicalDeviceConfigurationVersion config, String ciName, LogicalDeviceConfigurationItem parentConfigItem){
		debug(log,"getConfigItem (LogicalDeviceConfigurationVersion) - START");
		if(null==parentConfigItem){
			parentConfigItem = (LogicalDeviceConfigurationItem)config.getConfigItemTypeConfig();
		}

		List<LogicalDeviceConfigurationItem> childConfigItems = parentConfigItem.getChildConfigItems();
		for(LogicalDeviceConfigurationItem ci: childConfigItems){
			if(ci.getName().equals(ciName)){
				return ci;
			}
		}
		debug(log,"getConfigItem (LogicalDeviceConfigurationVersion) - END");
		return null;
	}

	/**
	 * This method returns the place associated with the service.
	 * @param cfs
	 * @return GeographicPlace
	 */
	public static GeographicPlace getServiceAddressFromCfs(Service cfs){
		debug(log,"getServiceAddressFromCfs - START");
		Set<PlaceServiceRel> placeServiceList = cfs.getPlace();
		debug(log,"No. of places associated with the service: " + placeServiceList.size());
		GeographicPlace place = null;
		if(!Utils.isEmpty(placeServiceList)) {
			Iterator<PlaceServiceRel> iterator = placeServiceList.iterator();
			PlaceServiceRel placeServiceRel = iterator.next();
			place = placeServiceRel.getGeographicPlace();
			debug(log,"Place Name: " + placeServiceRel.getGeographicPlace().getName() + " , Service Id: " + placeServiceRel.getService().getId());
			debug(log,"When was place associated with service : " + placeServiceRel.getLastModifiedDate());
		}
		debug(log,"getServiceAddressFromCfs - END");
		return place;
	}

	/**
	 * This method will relate the Place to LD if not already related.
	 * @param place
	 * @param ld
	 * @return
	 * @throws ValidationException
	 */
	public static void relatePlaceToLogicalDevice(GeographicPlace place, LogicalDevice ld) throws ValidationException {
		debug(log,"relatePlaceToLogicalDevice - START");

		boolean alreadyAssociated = false;
		Set<PlaceLogicalDeviceRel> placeLdRel = place.getPlaceLogicalDeviceRels();

		for (PlaceLogicalDeviceRel rel : placeLdRel) {
			LogicalDevice ldevice = rel.getLogicalDevice();
			if (null!=ldevice && ldevice.getName().equalsIgnoreCase(ld.getName())) {
				debug(log,"Place: " + place.getName() + " and Device: " + ld.getName() + " are already associated.");
				alreadyAssociated = true;
				break;
			}
		}

		if(!alreadyAssociated){
			AttachmentManager involvementMgr = PersistenceHelper.makeAttachmentManager();
			PlaceLogicalDeviceRelDAO placeLDRel = (PlaceLogicalDeviceRelDAO) involvementMgr.makeRel(PlaceLogicalDeviceRelDAO.class);
			placeLDRel.setLogicalDevice(ld, false);
			placeLDRel.setGeographicPlace(place, false);
			involvementMgr.createRel(placeLDRel);
			debug(log,"Place: " + place.getName() + " and Device: " + ld.getName() + " association done.");
		}

		debug(log,"relatePlaceToLogicalDevice - END");
	}
	
	/**
	 * Get Resource Pool Inventory Group object. 
	 * 
	 * @param name
	 * @param igSpec
	 * @param igMgr
	 * @return InventoryGroup
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 	
	 */
	public static InventoryGroup getResourcePool(String name,Specification igSpec, InventoryGroupManager igMgr) throws InstantiationException, IllegalAccessException  {
		debug(log,"getResourcePool - START");
		InventoryGroupSearchCriteria criteria = igMgr.makeInventoryGroupSearchCriteria();
		//Set Name
		CriteriaItem nameCI = criteria.makeCriteriaItem();
		nameCI.setValue(name);
		nameCI.setOperator(CriteriaOperator.EQUALS);
		criteria.setName(nameCI);
		Collection<InventoryGroup> ipPoolList = igMgr.findInventoryGroup(criteria);
		if(ipPoolList.isEmpty()){
			InventoryGroup ipPool = igMgr.makeInventoryGroup();
			ipPool.setName(name);
			ipPool.setSpecification((InventoryGroupSpecification) igSpec);
			ipPool = igMgr.createInventoryGroup(ipPool);
			return ipPool;
		}
		debug(log,"getResourcePool - END");

		return ipPoolList.iterator().next();
	}

	/**
	 * Find Network Address Domain by Name
	 * 
	 * @param nadName
	 * @param operator 
	 * @return NetworkAddressDomain
	 * @throws ValidationException
	 */
	public static NetworkAddressDomain findNetworkAddressDomain(String nadName, CriteriaOperator operator) throws ValidationException{
		debug(log,"findNetworkAddressDomain - START");

		NetworkAddressDomainManager nadManager = PersistenceHelper.makeNetworkAddressDomainManager();
		NetworkAddressDomainSearchCriteria nadSearchCriteria = nadManager.makeNetworkAddressDomainSearchCriteria();

		//Set Name
		CriteriaItem nameCI = nadSearchCriteria.makeCriteriaItem();
		nameCI.setValue(nadName);
		nameCI.setOperator(operator);
		nadSearchCriteria.setName(nameCI);

		Collection<NetworkAddressDomain> nadList = nadManager.findNetworkAddressDomains(nadSearchCriteria);
		debug(log,"findNetworkAddressDomain - END");

		return !Utils.isEmpty(nadList) ? nadList.iterator().next() : null;
	}


	/**
	 * Make Network Address Domain object. 
	 * 
	 * @param nadName
	 * @return NetworkAddressDomain
	 * @throws ValidationException 	 *  
	 */
	public static NetworkAddressDomain makeNetworkAddressDomain(String nadName,Specification nadSpec) throws ValidationException{
		debug(log,"makeNetworkAddressDomain - START");
		NetworkAddressDomainManager nadManager = PersistenceHelper.makeNetworkAddressDomainManager();
		NetworkAddressDomain nad = nadManager.makeNetworkAddressDomain();
		nad.setName(nadName);
		nad.setSpecification((NetworkAddressDomainSpec) nadSpec);
		Collection<NetworkAddressDomain> nadList = new ArrayList<>();
		nadList.add(nad);
		nadList = nadManager.createNetworkAddressDomains(nadList);
		debug(log,"makeNetworkAddressDomain - END");

		return !Utils.isEmpty(nadList) ? nadList.iterator().next() : null;
	}

	
	/**
	 * Find IP Subnet objects. 
	 * 
	 * @param nadName
	 * @param lowerIP
	 * @param prefix
	 * @return Collection<IPSubnet>
	 * @throws ValidationException
	 */
	public static Collection<IPSubnet> findIPSubnets(String nadName, String lowerIP, int prefix)
			throws ValidationException {
		debug(log,"getIPSubnets - START");


		IPNetworkManager ipNetMgr = PersistenceHelper.makeIPNetworkManager();
		IPSubnetSearchCriteria ipSubNetCriteria = ipNetMgr.makeIPv4SubnetSearchCriteria();

		CriteriaItem netDomainCriteriItm = ipSubNetCriteria.makeCriteriaItem();
		netDomainCriteriItm.setValue(nadName);
		netDomainCriteriItm.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);

		CriteriaItem prefixCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		prefixCriteriaItem.setValue(prefix);
		prefixCriteriaItem.setOperator(CriteriaOperator.EQUALS);

		CriteriaItem addrCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		addrCriteriaItem.setValue(lowerIP);
		addrCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);

		ipSubNetCriteria.setIPAddressDomainName(netDomainCriteriItm);
		ipSubNetCriteria.setPrefixLengthFrom(prefixCriteriaItem);

		ipSubNetCriteria.setLowerValue(addrCriteriaItem);
		CriteriaItem isIPNetworkCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		isIPNetworkCriteriaItem.setValue(Boolean.FALSE);
		isIPNetworkCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		ipSubNetCriteria.setIsIPNetwork(isIPNetworkCriteriaItem);

		Collection<IPSubnet> ipSubnets = null;
		ipSubnets = ipNetMgr.findNetworkAddressBlocks(ipSubNetCriteria);

		debug(log,"getIPSubnets - END");
		return ipSubnets;
	}

	/**
	 * Get VLAN Range for Network Address Domain. 
	 * 
	 * @param nad
	 * @param lowerRange
	 * @param upperRange
	 * @param vlanType
	 * @param fiMgr
	 * @param resourcePool
	 * @param vlanSpec
	 * @return Collection<FlowIdentifier>
	 * @throws ValidationException
	 */
	public static Collection<FlowIdentifier> getVLANRangeForNAD(NetworkAddressDomain nad, Integer lowerRange, Integer upperRange, FlowIdentifierManager fiMgr, InventoryGroup resourcePool, Specification vlanSpec) throws ValidationException {
		debug(log,"getVLANRangeForNAD - START");
		Collection<FlowIdentifier> vlanList = null;
		FlowIdentifier vlan = fiMgr.makeFlowIdentifier();
		vlan.setDomain(nad);
		vlan.setSpecification((FlowIdentifierSpecification) vlanSpec);
		FlowIdentifierRange fiRange= new FlowIdentifierRange();
		fiRange.setRangeFrom(lowerRange);
		if(!upperRange.equals(lowerRange)){
			fiRange.setRangeTo(upperRange);
		}
		vlanList = fiMgr.createFlowIdentifier(vlan, fiRange, resourcePool);
		fiMgr.flushTransaction();
		debug(log,"getVLANRangeForNAD - END, vlanList Size :" , vlanList.size());
		return vlanList;
	}

	/**
	 * Create or updat Char. 
	 * 
	 * @param vlan
	 * @param name
	 * @param value
	 * @param vlanSpec
	 * @return 
	 */
	public static void createOrUpdateChar(FlowIdentifier vlan, String name, String value,FlowIdentifierSpecification vlanSpec) {
		debug(log,"createOrUpdateChar - START");
		Set<CharacteristicSpecification> charSpecs =null;
		Iterator<CharacteristicSpecification> charSpecsIterator = null;
		Map<String, CharacteristicSpecification> characteristicMap = new HashMap<>();

		if(Utils.checkNull(vlan.getCharacteristicMap().get(name))){
			charSpecs = CharacteristicHelper
					.getCharacteristicSpecifications(vlanSpec);
			charSpecsIterator = charSpecs.iterator();

			while (charSpecsIterator.hasNext()) {
				CharacteristicSpecification charSpec =  charSpecsIterator
						.next();
				String charName = charSpec.getName();
				characteristicMap.put(charName, charSpec);
			}
			makeFlowIdChar(vlan,characteristicMap, name,value, true);
		}
		else
			vlan.getCharacteristicMap().get(name).setValue(value);
		debug(log,"createOrUpdateChar - END");

	}


	/**
	 * Make Flow Identifier Char. 
	 * 
	 * @param ld
	 * @param characteristicMap
	 * @param name
	 * @param value
	 * @param isDropdown
	 * @return FlowIdentifier
	 */
	public static FlowIdentifier makeFlowIdChar(FlowIdentifier fl,
			Map<String, CharacteristicSpecification> characteristicMap,
			String name, String value, boolean isDropdown) {
		debug(log,"makeFlowIdChar - START");

		CharValue ldChar = fl
				.makeCharacteristicInstance();
		CharacteristicSpecification characteristicSpecification = characteristicMap
				.get(name);
		ldChar.setCharacteristicSpecification(characteristicSpecification);
		ldChar.setName(name);
		ldChar.setValue(value);
		if (isDropdown) {
			ldChar.setLabel(value);
		} else {
			ldChar.setLabel(null);
		}

		fl.getCharacteristics().add(
				(FlowIdentifierCharacteristic) ldChar);
		debug(log,"makeFlowIdChar - END");

		return fl;
	}

	/**
	 * Create a new CriteriaItem and set its parameters
	 * 
	 * @param criteria
	 * @param name
	 * @param value
	 * @param criteriaClass
	 * @param criteriaOperator
	 * @return CriteriaItem
	 */
	public static CriteriaItem makeCriteriaItem(SearchCriteria criteria, String name, String value, Class<? extends Persistent> criteriaClass,
			CriteriaOperator criteriaOperator)
	{
		debug(log,"makeCriteriaItem - START");

		CriteriaItem criteriaItem = criteria.makeCriteriaItem();
		criteriaItem.setName(name);
		criteriaItem.setValue(value);
		criteriaItem.setOperator(criteriaOperator);
		criteriaItem.setCriteriaClass(criteriaClass);
		debug(log,"makeCriteriaItem - END");

		return criteriaItem;
	}


	/**
	 * This method is being used in QueryServiceDetails Web Service only.
	 * @param map
	 * @return Persistent
	 */
	public static Persistent getCurrentAssignment(Map<ConsumableResource, AssignmentState> map){
		debug(log,"getCurrentAssignment - START");
		Iterator<ConsumableResource> itr = map.keySet().iterator();

		while (itr.hasNext()) {
			ConsumableResource e = itr.next();
			AssignmentState state = map.get(e);
			debug(log,"Assignment Found >>> Name: " + e.getName() + ", Id: " + e.getId() + ", Spec: " + e.getSpecification().getName() + ", Status: " + state);
			if (AssignmentState.ASSIGNED.equals(state) || AssignmentState.PENDING_ASSIGN.equals(state)){
				debug(log,"getCurrentAssignment - END, Found");
				return e;
			}
		}		
		debug(log,"getCurrentAssignment - END, Not Found");
		return null;
	}


	/**
	 * This method will return the ConfigItem Referenced LogicalDevice/PhysicalPort
	 * @param configitem
	 * @param isLogicalDevice
	 * @param isPhysicalPort
	 * @return ConfigurationReferenceEnabled
	 */
	public static ConfigurationReferenceEnabled getReference(ServiceConfigurationItem configitem,boolean isLogicalDevice,boolean isPhysicalPort){
		
		debug(log,"getReference - START");
		ConfigurationReferenceEnabled refEntity = null;
		Map<ConfigurationReferenceEnabled, ConfigurationReferenceState> refMap = configitem.getReferencesMap();
		
		if(Utils.checkNull(refMap)|| refMap.size()==0) {
			return refEntity;
		}
		
		for (Map.Entry<ConfigurationReferenceEnabled, ConfigurationReferenceState> entry: refMap.entrySet()) {			
			if (ConfigurationReferenceState.PENDING_REFERENCE.equals(entry.getValue())
					|| ConfigurationReferenceState.REFERENCED.equals(entry.getValue())) { 
				if(isLogicalDevice && entry.getKey() instanceof LogicalDevice){						
					return entry.getKey();
				}
				if(isPhysicalPort && entry.getKey() instanceof PhysicalPort){						
					return entry.getKey();
				}				
			}			
		}		
		
		debug(log,"getReference - END");

		return refEntity;
	}
	
	/**
	 * This method retrieves the char value from given Config Item.
	 * @param config
	 * @param configItemName - Optional. If not provided then char will be set at the root level.
	 * @param charName
	 * @return String
	 * @throws ValidationException
	 */
	public static String getConfigItemCharValue(ServiceConfigurationVersion config, ServiceConfigurationItem configItem, String charName) throws ValidationException{
		debug(log,"getConfigItemCharValue - START.");
		debug(log,"config :", config.getVersionNumber());
		DesignManager designManager = DesignHelper.makeDesignManager();
				
		debug(log,"getConfigItemCharValue - END.");
		return designManager.getConfigItemCharacteristic(configItem, charName);
	}
	
	/**
	 * This method finds the UnusedConfigItems.
	 * @param config
	 * @param ciName	 
	 * @return List<ServiceConfigurationItem>
	 * @throws ValidationException
	 */
	public static List<ServiceConfigurationItem> findUnusedConfigItem(ServiceConfigurationVersion config,	String ciName) throws ValidationException {
		oracle.communications.inventory.techpack.common.ServiceManager serviceManager = CommonHelper.makeServiceManager();		
		List<ServiceConfigurationItem> cis = serviceManager
				.findServiceConfigItemByName(config, ciName);
		List<ServiceConfigurationItem> ciList = new ArrayList<>();
		
		if (!Utils.isEmpty(cis)) {			
			for (ServiceConfigurationItem item : cis) {
				
				if (!serviceManager.checkItemAssignedReferenced(config, item)) {
					ciList.add(item) ;
				}
			}
		}

		return ciList;
	}
	
	/**
	 * This method will retrieve the available ONT Id 
	 * @param ontDomain
	 * @return FlowIdentifier
	 * @throws ValidationException
	 */
	public FlowIdentifier getOntIdByDomain(String ontDomain) throws ValidationException{
		debug(log,"getOntIdByDomain - START");
		NetworkAddressDomain nad = UimHelper.findNetworkAddressDomain(ontDomain, CriteriaOperator.EQUALS);
		
		FlowIdentifierManager flowIdentifierManager = PersistenceHelper.makeFlowIdentifierManager();
		FlowIdentifierSearchCriteria flowIdentifierSearchCriteria = flowIdentifierManager.makeFlowIdentifierSearchCriteria();

		// Set Network Address Domain
		if (nad != null) {
			debug(log,"Set Network Address Domain="+nad.getName());
			flowIdentifierSearchCriteria.setNetworkAddressDomain(nad);
		}

		//Set Assignment State
		flowIdentifierSearchCriteria.setAssignmentState(AssignmentState.UNASSIGNED);

		//Set Specification
		FlowIdentifierSpecification flowIdentifierSpecification = EntityUtils.findSpecification(FlowIdentifierSpecification.class, Constants.SPEC_ONTID);
		if(Utils.checkNull(flowIdentifierSpecification)) {
			debug(log,"Set Specification="+flowIdentifierSpecification.getName());
			flowIdentifierSearchCriteria.setFlowIdentifierSpecification(flowIdentifierSpecification);
		}

		List<FlowIdentifier> flowIdentifierList = flowIdentifierManager.findFlowIdentifier(flowIdentifierSearchCriteria);
		if(Utils.isEmpty(flowIdentifierList)){
			log.validationException("FI.vlanNotFound", new java.lang.IllegalStateException());
		}
		flowIdentifierList = sortVlanByIdentifier(flowIdentifierList);
		debug(log,"FlowIdentifiers list size: "+flowIdentifierList.size());
		
		debug(log,"getOntIdByDomain - END");
		return !Utils.isEmpty(flowIdentifierList) ? flowIdentifierList.get(0) : null;
	}
	
	/**
	 * This method sets the given char value on the service configuration.
	 * 
	 * @param config
	 * @param configItemName
	 *            - Optional. If not provided then char will be set at the root
	 *            level.
	 * @param charName
	 * @param charValue
	 * @throws ValidationException
	 */
	public static void setConfigItemCharValue(ServiceConfigurationVersion config, String configItemName, String charName, String charValue, String parentConfigItemName) throws ValidationException {
		debug(log,"setConfigItemCharValue - START2");
		debug(log,"configItemName :", configItemName , ", charName:" , charName ,
				", charValue :" , charValue , ", ParentConfigItemName :" , parentConfigItemName);

		try {
			DesignManager designManager = DesignHelper.makeDesignManager();
			ServiceConfigurationItem configItem = null;
			ServiceConfigurationItem parentsci = null;

			if (Utils.isEmpty(configItemName)) {
				configItem = (ServiceConfigurationItem) config.getConfigItemTypeConfig();
			} else {
				if (Utils.isEmpty(parentConfigItemName))
					parentsci = (ServiceConfigurationItem) config.getConfigItemTypeConfig();
				else
					parentsci = designManager.aquireConfigItem(config, parentConfigItemName);

				if (!Utils.checkNull(parentsci)) {
					configItem = UimHelper.getConfigItem(config, configItemName, parentsci);
				}
			}
			
			if(configItem != null) {
				if (charValue.equals("")) {
					Map<String, ServiceConfigurationItemCharacteristic> configItemCharMap = configItem.getCharacteristicMap();
					if (configItemCharMap.containsKey(charName)) {
						configItemCharMap.remove(charName);
						debug(log,CHAR_DELIMITTER , charName , REMOVED);
					}
				} else {
					EntityHelper.addUpdateConfigItemCharacteristic(config, configItem, charName, charValue);
					debug(log,CHAR_DELIMITTER , charName , " updated with value: " + charValue);
				}
			}

		} catch (Exception e) {
			log.error("", Constants.EXCEPTION_DELIMITTER+e.getMessage(),e);			
		}
		debug(log,"setConfigItemCharValue - END2");
	}
	
	
	/**
	 * This method sets the given char value on the service configuration.
	 * 
	 * @param config
	 * @param configItemName	 *          
	 * @param charName
	 * @param charValue
	 * @param parentConfigItem
	 * @throws ValidationException
	 */
	protected static void setConfigItemCharValue(ServiceConfigurationVersion config, String configItemName,
			String charName, String charValue, ServiceConfigurationItem parentConfigItem) throws ValidationException {		
		debug(log,"setConfigItemCharValue - START");
		debug(log,"configItemName: " , configItemName , " charName:" , charName
				, " charValue:" , charValue , " ParentConfigItemName:" , parentConfigItem);

		try {
			ServiceConfigurationItem configItem = null;
			ServiceConfigurationItem parentCI = null;

			if (Utils.checkNull(parentConfigItem) ){
				parentCI = (ServiceConfigurationItem) config.getConfigItemTypeConfig(); // Set parent as root config item.
			}else {
				parentCI = parentConfigItem;
			}			

			if (!Utils.checkNull(parentCI)) {
				configItem = UimHelper.getConfigItem(config, configItemName, parentCI);
			}
			if(configItem != null) {
				if (charValue.equals("")) {
					Map<String, ServiceConfigurationItemCharacteristic> configItemCharMap = configItem.getCharacteristicMap();
					if (configItemCharMap.containsKey(charName)) {
						configItemCharMap.remove(charName);
						debug(log,CHAR_DELIMITTER+ charName + REMOVED);
					}
				} else {
					EntityHelper.addUpdateConfigItemCharacteristic(config, configItem, charName, charValue);
					debug(log,CHAR_DELIMITTER + charName + " updated with value: " + charValue);
				}
			}

		} catch (Exception e) {
			log.error("", Constants.EXCEPTION_DELIMITTER+e.getMessage(),e);
		}
		debug(log,"setConfigItemCharValue - END");
	}	
	
	/**
	 * This method retrieves the service number from the service.
	 * @param service
	 * @return String
	 */
	public static String getServiceNumberFromService(Service service){
		debug(log,"getServiceNumberFromService - START");
		String name = service.getName();
		debug(log,"name: "+name);
		String serviceNumber = name.substring(name.indexOf(Constants.HYPHEN)+1, name.length()); 
		debug(log,"getServiceNumberFromService - END, serviceNumber: " + serviceNumber);
		return serviceNumber;
	}
	
	/**
	 * This method will unreserve the CustomObjects
	 * @param service
	 * @return 
	 * @throws ValidationException
	 */
	public static void redeemRouteReservation(String reservationId) throws ValidationException{
		debug(log,"redeemRouteReservation - START");
		Collection<CustomObjectReservation> coReservationList = UimHelper.findRouteReservation(reservationId);
		Collection<CustomObject> coList = new ArrayList<>();
		CustomObject co = null;
		if(!Utils.isEmpty(coReservationList)){
			debug(log,"Redeemed the reservation during D&A.");
			co = coReservationList.iterator().next().getCustomObject();
			coList.add(co);
			PersistenceHelper.makeReservationManager().unreserveResource(coList);
			debug(log,"Deleting reserved custom object ");
			PersistenceHelper.makeCustomObjectManager().deleteCustomObjects(coList);
		}
		debug(log,"redeemRouteReservation - END");
	}
	
	/**
	 * This method will will check Bandwidth Available for Copper
	 * @param uploadBw
	 * @param downloadBw
	 * @param serviceType
	 * @return boolean
	 * @throws ValidationException
	 */
	public static boolean isBandwidthAvailableOnCopper(String uploadBw, String downloadBw, String serviceType) throws ValidationException {
		debug(log,"isBandwidthAvailableOnCopper - START, ServiceType: " + serviceType);
		HashMap<String,String> charMap = new HashMap<>();
		charMap.put(Constants.CHAR_SUPPORTEDSERVICES, serviceType);
		List<CustomObject> cardTypes = findCustomObject(Constants.SPEC_CARDTYPE, null, null, charMap, CriteriaOperator.CONTAINS_IGNORE_CASE, null, null);
		Double maxUploadBwSupported = 0.0;
		Double maxDownloadBwSupported = 0.0;
		for(CustomObject co: cardTypes) {
			String uploadBwOnCard = getEntityCharValue(co, Constants.CHAR_UPLOADBW);
			String downloadBwOnCard = getEntityCharValue(co, Constants.CHAR_DOWNLOADBW);
			debug(log,"CardType: " + co.getName() + "supports " + uploadBw + " Mbps Upload bandwidth.");
			debug(log,"CardType: " + co.getName() + "supports " + downloadBw + " Mbps Download bandwidth.");
			if(!Utils.isEmpty(uploadBwOnCard) && !Utils.isEmpty(downloadBwOnCard)) {
				if(Double.valueOf(uploadBwOnCard) > maxUploadBwSupported) {
					maxUploadBwSupported = Double.valueOf(uploadBwOnCard);
				}
				if(Double.valueOf(downloadBwOnCard) > maxDownloadBwSupported) {
					maxDownloadBwSupported = Double.valueOf(downloadBwOnCard);
				}
			}
		}
		debug(log,"Max Upload Bandwidth supported on Copper is " + maxUploadBwSupported + " Mbps");
		debug(log,"Max Download Bandwidth supported on Copper is " + maxDownloadBwSupported + " Mbps");
		
		if(Double.valueOf(uploadBw)<=maxUploadBwSupported && Double.valueOf(downloadBw)<=maxDownloadBwSupported) {
			debug(log,"isBandwidthAvailableOnCopper - END, true");
			return true;
		} else {
			debug(log,"isBandwidthAvailableOnCopper - END, false");
			return false;
		}
	}
	
	/**
	 * This method returns the reservation reason attribute value from below string.
	 * AccountId: accountId , OrderType: orderType , Medium: medium
	 * @param reason
	 * @param attribute
	 * @return String
	 */
	public static String getReservationReasonAttribute(String reason, String attribute) {
		debug(log,"getReservationReasonAttribute - START");
		String[] reasonAttributesArray = reason.split(",");
		String attributeHolder = "";
		for(String attributeItem: reasonAttributesArray) {
			if(attributeItem.contains(attribute)) {
				attributeHolder = attributeItem;
			}
		}
		String[] attribArray = attributeHolder.split(":");
		debug(log,"getReservationReasonAttribute - END");
		return attribArray[1].trim();
	}
	
	/**
	 * Method to make transitions of states of Service and Service configuration version.
	 * @param newServiceConfigVersion
	 * @return
	 * @throws Exception
	 */
	public static void makeTransitions(ServiceConfigurationVersion newServiceConfigVersion) {
		debug(log,"makeTransitions - START for Completeing the ServiceConfig: " + newServiceConfigVersion.getName());
		ServiceConfigurationManager configMgr = PersistenceHelper.makeServiceConfigurationManager();
		try {
			debug(log,"Name: " + newServiceConfigVersion.getService().getName() + " , Status: " + newServiceConfigVersion.getConfigState());
			if(newServiceConfigVersion.getConfigState().equals(ConfigurationStatus.IN_PROGRESS)) {
				configMgr.approveConfigurationVersion(newServiceConfigVersion);
				configMgr.issueConfigurationVersion(newServiceConfigVersion);
				configMgr.completeConfigurationVersion(newServiceConfigVersion);
				PersistenceHelper.makePersistenceManager().flushTransaction();
			}
		} catch (ValidationException e) {
			debug(log,"Error Completing service configuration..");
		}
		debug(log,"makeTransitions - END");
	}
	
	/**
	 * This method is used to find SCI by Value. This is used for deleting blank config items.
	 * Changing The SCI Name/Label is causing issue in CTA so changing the Value.
	 * @param config
	 * @param label
	 * @return List<ServiceConfigurationItem>
	 */
	public static List<ServiceConfigurationItem> findServiceConfigItemByValue(ServiceConfigurationVersion config, String value){
		debug(log,"findServiceConfigItemByValue - START");
		List<ServiceConfigurationItem> configItems = new ArrayList<>();
		debug(log,"SCV: " + config.getName() + " , Service Name: " + config.getService().getName());
		for(ServiceConfigurationItem sci: config.getConfigItems()) {
			if(value.equals(sci.getValue())) {	
				debug(log,"SCI >>> Id: " + sci.getEntityId() + " , Name: " + sci.getName() + " , Label: " + sci.getLabel() + " , Value: " + sci.getValue());
				configItems.add(sci);
			}
		}
		debug(log,"findServiceConfigItemByValue - END, Found: " + configItems.size());
		return configItems;
	}
	
	/**
	 * @param config
	 * @param ciName
	 * @param parentConfigItem
	 * @return ServiceConfigurationItem
	 * @throws ValidationException
	 */
	public static ServiceConfigurationItem aquireUnusedConfigItem(ServiceConfigurationVersion config, String ciName, ServiceConfigurationItem parentConfigItem) throws ValidationException{
		debug(log,"aquireUnusedConfigItem - START");
		oracle.communications.inventory.techpack.common.ServiceManager serviceManager = CommonHelper.makeServiceManager();		
		ServiceConfigurationItem ci = null;

		if(null==parentConfigItem){
			parentConfigItem = (ServiceConfigurationItem)config.getConfigItemTypeConfig();
		}

		List<ServiceConfigurationItem> childConfigItems = parentConfigItem.getChildConfigItems();
		for(ServiceConfigurationItem sci: childConfigItems){
			if(sci.getName().equals(ciName) && !serviceManager.checkItemAssignedReferenced(config, sci)){
				ci = sci;
				ci.setValue(null);//Mark the SCI value as NULL as it is being used again.
				debug(log,"Existing Config Item Used >> Name: " + ciName + ", Id: " + ci.getEntityId() + " , Value: " + ci.getValue());
				break;
			}
		}
				
		if (ci == null) {
			ci = serviceManager.addChildConfigItem(config, parentConfigItem, ciName);
			debug(log,"New Config Item Created: " + ciName);
		}
		debug(log,"aquireUnusedConfigItem - END");
		return ci;
	}

	/**
	 * Find the TelephoneNumber based on criteria.
	 * 
	 * @param tnSpec
	 * @param name
	 * @param value
	 * @param criteriaOperator
	 * @return List<TelephoneNumber>
	 * @throws ValidationException
	 */
	public static List<TelephoneNumber> findTelephoneNumberRange(String tnSpecName, String startRange, String endRange,
			Map<String, String> charMap) throws  ValidationException {
		debug(log,"findTelephoneNumberRange - START");
		TelephoneNumberManager manager = null;
		TelephoneNumberSearchCriteria criteria = null;
		List<TelephoneNumber> tnList = null;

		manager = PersistenceHelper.makeTelephoneNumberManager();
		criteria = manager.makeTelephoneNumberSearchCriteria();

		//Set Specification
		if(tnSpecName!=null) {
			TelephoneNumberSpecification spec = EntityUtils.findSpecification(TelephoneNumberSpecification.class, tnSpecName);
			criteria.setTelephoneNumberSpecification(spec);
		}

		//Set Range From
		CriteriaItem rangeFromCriteriaItem = criteria.makeCriteriaItem();
		rangeFromCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		rangeFromCriteriaItem.setValue(startRange);
		criteria.setRangeFrom(rangeFromCriteriaItem);

		//Set Range To
		CriteriaItem rangeToCriteriaItem = criteria.makeCriteriaItem();
		rangeToCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		rangeToCriteriaItem.setValue(endRange);
		criteria.setRangeTo(rangeToCriteriaItem);

		//Set Characteristic 
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(TNCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

		//Set Disable Ordering TRUE
		criteria.setDisableOrdering(true);

		tnList = manager.findTelephoneNumbers(criteria);
		debug(log,"findTelephoneNumberRange - END");
		return tnList;
	}
	
	/**
	 * 
	 * @param lowerIP
	 * @param prefix
	 * @return Collection<IPSubnet>
	 * @throws ValidationException
	 */
	public static Collection<IPSubnet> findIPSubnets(String lowerIP, int prefix) throws ValidationException {
		debug(log,"getIPSubnets - START");
		IPNetworkManager ipNetMgr = PersistenceHelper.makeIPNetworkManager();
		IPSubnetSearchCriteria ipSubNetCriteria = ipNetMgr.makeIPv4SubnetSearchCriteria();

		CriteriaItem prefixCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		prefixCriteriaItem.setValue(prefix);
		prefixCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		ipSubNetCriteria.setPrefixLengthFrom(prefixCriteriaItem);

		CriteriaItem addrCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		addrCriteriaItem.setValue(lowerIP);
		addrCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		ipSubNetCriteria.setLowerValue(addrCriteriaItem);

		CriteriaItem isIPNetworkCriteriaItem = ipSubNetCriteria.makeCriteriaItem();
		isIPNetworkCriteriaItem.setValue(Boolean.FALSE);
		isIPNetworkCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		ipSubNetCriteria.setIsIPNetwork(isIPNetworkCriteriaItem);

		Collection<IPSubnet> ipSubnets = null;
		ipSubnets = ipNetMgr.findNetworkAddressBlocks(ipSubNetCriteria);

		debug(log,"getIPSubnets - END");
		return ipSubnets;
	}
	
	/**
	 * This method validates the LTE SIM while creating
	 * @author Brijmohan
	 * @param ldList
	 * @return 
	 * @throws ValidationException
	 */
	public void validateLTESIM(List<LogicalDevice> ldList) throws ValidationException{
		debug(log,"","Entered the validate method");
		String serviceNumber;
		String specification;
		List<LogicalDevice> ldList1;
		LogicalDevice logDev=null;
		logDev=ldList.get(0);
		Map<String, LogicalDeviceCharacteristic> ldChar = logDev.getCharacteristicMap();
		HashMap<String, String> charMap = new HashMap<>();
		if (ldChar.containsKey(Constants.SERVICENUMBER)) {
			serviceNumber = ldChar.get(Constants.SERVICENUMBER).getValue();
			charMap.put(Constants.SERVICENUMBER, serviceNumber);
			charMap.put("Available", Constants.TRUE);
		} else {
			throw new ValidationException("ServiceNumber is Blank.");
		}
		if (!ldChar.containsKey(Constants.IMSI)) {
			throw new ValidationException("IMSI is Blank.");
		}
		if (!ldChar.containsKey(Constants.ICCID)) {
			throw new ValidationException("ICCID is Blank.");
		}
		specification = logDev.getSpecification().getName();
		TelephoneNumber tele = getTelephoneNumberByID(Constants.MSISDN, serviceNumber);
		if (tele != null) {
			ldList1 = DeviceHelper.findLdByCharAndSpec(charMap, specification);
			if (!Utils.isEmpty(ldList1)) {
				throw new ValidationException(specification + " with the Service Number is already Present in UIM");
			}
		} else {
			throw new ValidationException(serviceNumber + " doesnot exist in UIM");
		}
	}
	
	/**
	 * Delete Configuration Item. 	 * 
	 * @param configItem
	 * @return
	 * @throws ValidationException
	 */
	public static void deleteServiceConfigurationItem(InventoryConfigurationItem configItem) throws ValidationException{
		debug(log,"", "Entered deleteServiceConfigurationItem() ");
		if (configItem != null) 
		{
			BaseConfigurationManager serviceConfigurationManager = PersistenceHelper.makeConfigurationManager(configItem.getClass());
			Collection<InventoryConfigurationItem> serviceConfigItems = new ArrayList<>();
			serviceConfigItems.add(configItem);
			debug(log,"", "serviceConfigItems : " + serviceConfigItems);
			try {
				serviceConfigurationManager.deleteConfigurationItems(serviceConfigItems);
			} catch (Exception e) {
				log.validationError("", "ignore this error : " + e.getMessage());
			}
			debug(log,"", "items unallocated :" + configItem);
			serviceConfigurationManager.flushTransaction();
		} else {
			String errorMessage = MessageResource.getMessage("ws.ConfigItemNotNull");
			debug(log,"", errorMessage + ",configItem is NULL");
			throw new ValidationException(errorMessage);
		}
		debug(log,"", "Exiting deleteServiceConfigurationItem()");
	}

	/**
	 * This method will validate the DIDBlock TelephoneNumber
	 * @param didNumber
	 * @return 
	 * @throws 
	 */
	public void validateDIDNumber(TelephoneNumber didNumber) {
		debug(log,"", "Entered the validate method");
		try {
			Map<String, TNCharacteristic> tnChar = didNumber.getCharacteristicMap();
			debug(log,"","tnChar:- "+tnChar);
			if (!tnChar.containsKey(Constants.STARTNUMBER)) {
				throw new ValidationException("Start Number is Blank.");
			}
			if (!tnChar.containsKey(Constants.ENDNUMBER)) {
				throw new ValidationException("End Number is Blank.");
			}
		} catch (Exception e) {
			debug(log,"", "Entered the catch block");
			didNumber.resetCharacteristicMap();
			log.error("Error", e, e.getMessage());
		}
	}
	
	/**
	 * This method will create the TelephoneNumber of Input Spec
	 * @param diNumMap
	 * @param name
	 * @param spec
	 * @param desc
	 * @return TelephoneNumber
	 * @throws ValidationException
	 */
	public static TelephoneNumber createTelephoneNumber(Map<String, String> diNumMap, String name, String spec,String desc) throws ValidationException {
		debug(log,"Entered the createTelephoneNumber");
		Finder find = PersistenceHelper.makeFinder();
		Collection<TelephoneNumberSpecification> tnSpecCol = find.findByName(TelephoneNumberSpecification.class, spec);
		TelephoneNumberSpecification tnSpec = tnSpecCol.iterator().next();
		TelephoneNumberManager tnMng = PersistenceHelper.makeTelephoneNumberManager();
		TelephoneNumber tn = tnMng.makeTelephoneNumber();
		tn.setSpecification(tnSpec);
		if (!Utils.checkNull(desc)) {
			tn.setDescription(desc);
		}
		List<TelephoneNumber> liTn = null;
		liTn = tnMng.createTelephoneNumbers(name, null, tn);
		if(!Utils.isEmpty(liTn)) {
			TelephoneNumber telephoneNumber = liTn.get(0);
			Set<TNCharacteristic> tnCharacteristics = new HashSet<>();
			CharacteristicManager charaManager = PersistenceHelper.makeCharacteristicManager();
			Set<String> keyValueSet = diNumMap.keySet();
			debug(log,"", "KeyValue:- " + keyValueSet);
			Iterator<String> it = keyValueSet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = diNumMap.get(key);
				CharacteristicSpecification charSpec = charaManager.getCharacteristicSpecification(telephoneNumber.getSpecification(), key);
				TNCharacteristic didChar = telephoneNumber.makeCharacteristicInstance();
				didChar.setCharacteristicSpecification(charSpec);
				didChar.setName(key);
				didChar.setValue(value);
				tnCharacteristics.add(didChar);
			}
			telephoneNumber.setCharacteristics(tnCharacteristics);
			List<TelephoneNumber> tnlist = new ArrayList<>();
			tnlist.add(telephoneNumber);
			PersistenceHelper.makeTelephoneNumberManager().updateTelephoneNumbers(tnlist);
		}else {
			throw new ValidationException("tfl.rule.specNotFound");
		}
		debug(log,"", "Exiting the createTelephoneNumber");
		return liTn.get(0);
	}

	/**
	 * Get the Technology value for the given cardType. 
	 *  
	 * @param cardType
	 * @return String
	 * @throws ValidationException
	 */
	public static String getTechnology(String cardType) throws ValidationException {
		debug(log,"", "Entered the getTechnology");
		
		String technology = null;
		List<CustomObject> cardTypeList = UimHelper.findCustomObject(Constants.SPEC_CARDTYPE, cardType, CriteriaOperator.EQUALS_IGNORE_CASE, null, null, null, null);
		
		if(!Utils.isEmpty(cardTypeList)) {
			technology = UimHelper.getEntityCharValue(cardTypeList.get(0), Constants.PARAM_TECHNOLOGY);
			
			if(technology != null && technology.indexOf(Constants.ADSL) > -1 && technology.indexOf(Constants.VDSL) > -1) {
				technology = Constants.VDSL;
			}
		}
		debug(log,"", "technology : " + technology);
		debug(log,"", "Exiting the getTechnology");
		
		return technology;
	}
	
	/**
	 * Get the Service Type value for the given cardType. 
	 *  
	 * @param cardType
	 * @return String
	 * @throws ValidationException
	 */
	public static String getServiceTypeForCardType(String cardType) throws ValidationException {
		debug(log,"", "Entered the getServiceTypeForCardType");
		
		String serviceType = null;
		List<CustomObject> cardTypeList = UimHelper.findCustomObject(Constants.SPEC_CARDTYPE, cardType, CriteriaOperator.EQUALS_IGNORE_CASE, null, null, null, null);
		
		if(!Utils.isEmpty(cardTypeList)) {
			serviceType = UimHelper.getEntityCharValue(cardTypeList.get(0), Constants.PARAM_SERVICE_TYPE);
			
		}
		debug(log,"", "serviceType : " + serviceType);
		debug(log,"", "Exiting the getServiceTypeForCardType");
		
		return serviceType;
	}
	
	/**
	 * Method to find specification of a entity like service specification, physical device specification etc by name only.
	 * 
	 * @param String - name - Specification name to be found.
	 * @return Specification  - Specification found.
	 * @throws ValidationException
	 */
	public static Specification getSpecification(String name) throws ValidationException {
		Specification spec = null;
		if (name != null) {
			SpecManager specMgr = PersistenceHelper.makeSpecManager();
			SpecSearchCriteria specSearchCriteria = specMgr.makeSpecSearchCriteria();
			CriteriaItem criteriaItem = specSearchCriteria.makeCriteriaItem();
			criteriaItem.setValue(name);
			criteriaItem.setOperator(CriteriaOperator.EQUALS);
			specSearchCriteria.setName(criteriaItem);
			try{
				List<Specification> specs = specMgr.findSpecifications(specSearchCriteria);
				if (!Utils.isEmpty(specs))
					spec = specs.iterator().next();
			}catch(Exception e){
				throw new ValidationException("tfl.rule.specNotFound");
			}
		}
		return spec;
	}
	
	/**
	 * This method will return the characteristic value
	 * @param entity
	 * @param inName
	 * @return String
	 */
	public static String getCommonEntityCharValue( CharacteristicExtensible entity, String inName ){		
		debug(log,"getCommonEntityCharValue - START");	
		Set<CharValue> charSet = entity.getCharacteristics();
		for (CharValue cv : charSet) {			
			if(inName.equalsIgnoreCase(cv.getName())) {
				debug(log,"Found CharName :",cv.getName(),", CharValue :",cv.getValue());
				return cv.getValue();
			}
		}		
		debug(log,"getCommonEntityCharValue - End");
		return null;
	 }
	
	/**
	 * This method will update the CPE DateInstalled
	 * @param cpe
	 * @param inputDate
	 */
	public static void updateCPEDateInstalled(LogicalDevice cpe, String inputDate) {
		debug(log,"updateDateInstalledForCPE - Start");
		
		if(!Utils.isEmpty(inputDate)) {
			EntityUtils.setValue(cpe, Constants.PARAM_DATE_INSTALLED, inputDate);
			debug(log,"Successfully updated DateInstalled to the value InputDate :", inputDate);
			return;
		}		
		
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT); 
		LocalDateTime localDateTime = LocalDateTime.now(); 
		String currentDate=dateFormat.format(localDateTime);		
 		 		
		Map<String, LogicalDeviceCharacteristic> cpeCharMap = cpe.getCharacteristicMap();
		if(cpeCharMap.containsKey(Constants.PARAM_DATE_INSTALLED) && 
				!Utils.checkNull(cpeCharMap.get(Constants.PARAM_DATE_INSTALLED)) &&
				!Utils.isEmpty(cpeCharMap.get(Constants.PARAM_DATE_INSTALLED).getValue())) {
			debug(log, "DateInstalled update is not required as CPE already has the value and InputDate is null/empty.");
			return;
			
		}
		EntityUtils.setValue(cpe, Constants.PARAM_DATE_INSTALLED, currentDate);	
		debug(log,"Successfully updated DateInstalled to the Current Date :", currentDate);
		debug(log,"updateDateInstalledForCPE - End");		
	}
	
	/**
	 * Get Reserved TN By ID. 
	 * @param tnSpecName
	 * @param Id
	 * @return
	 * @throws ValidationException
	 */
	public static TelephoneNumber getReservedTelephoneNumberByID(String tnSpecName, String Id)	throws ValidationException {
		debug(log,"getReservedTelephoneNumberByID - START, Id="+Id);
		debug(log,"Input tnSpecName: "+tnSpecName+", Id: "+Id);
		
		TelephoneNumberManager mgr = PersistenceHelper.makeTelephoneNumberManager();
		List<TelephoneNumber> tnList = new ArrayList<>();
		List<TelephoneNumber> reservedTNList = new ArrayList<>();
		TelephoneNumber reservedTN=null;		

		TelephoneNumberSearchCriteria criteria = mgr.makeTelephoneNumberSearchCriteria();
		TelephoneNumberSpecification spec= EntityUtils.findSpecification(TelephoneNumberSpecification.class, tnSpecName);
		criteria.setTelephoneNumberSpecification(spec);		

		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(Id);
		nameCriteria.setOperator(CriteriaOperator.EQUALS);
		criteria.setId(nameCriteria);
		criteria.setDisableOrdering(true);
		
		try {
			tnList = mgr.findTelephoneNumbers(criteria);
			if (tnList != null && tnList.size() == 1) {
				debug(log, "Telephone Number found for Id " + Id);
			} else if (tnList != null && tnList.size() > 1) {
				log.warn("", "More than 1 Telephone Number found for Id " + Id);
			} else {
				debug(log, "Telephone Number Not found for Id " + Id);				
				log.validationException(Constants.ERR_SERVICE_MISSING_RESOURCES, new IllegalArgumentException(), tnSpecName+" TelephoneNumber :"+Id+" is not found in Inventory");
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Telephone Number.", e);
			throw e;
		}
		debug(log,"tnList size :"+tnList);		
		
		int tnCounter=0;
		for(TelephoneNumber tn : tnList){
			tnCounter++;
			String reservationTypeAsString = ConsumerUtils.getReservationTypeAsString(tn);
			if(null!=reservationTypeAsString) {
				debug(log,tnCounter+") Reserved TN ID:"+tn.getId()+", reservationTypeAsString :"+reservationTypeAsString);
				reservedTNList.add(tn);
			}
		}
		debug(log,"reservedTNList count : " + reservedTNList.size());
		debug(log,"findReservedTelephoneNumbers - END");
		
		if(Utils.isEmpty(reservedTNList)) {
			log.validationException(Constants.ERR_SERVICE_MISSING_RESOURCES, new IllegalArgumentException(), tnSpecName+" TelephoneNumber :"+Id+" is not reserved");
		}
		reservedTN=reservedTNList.get(0);
		
		debug(log,"getReservedTelephoneNumberByID - END, reservedTN :" + reservedTN);
		return  reservedTN;
	}
	
	/**
	 * 
	 * @param peName
	 * @param suffix
	 * @param ipAddress
	 * @return
	 * @throws ValidationException
	 */
	public static IPSubnet getIPSubnetByPENamenIPAddress(String peName, String suffix, String ipAddress) throws ValidationException{
		debug(log,"getIPSubnetByPENamenIPAddress - START");
		debug(log,"peName: " + peName);
		debug(log,"suffix: " + suffix);
		debug(log,"ipAddress: " + ipAddress);	
		IPSubnet childIPSubnet = null;

		DesignManager designManager = DesignHelper.makeDesignManager();
		InventoryGroup ig = designManager.findInventoryGroupByName(peName);
		if(null==ig){
			debug(log,"Inventory Group named: "+peName+" doesn't exist in UIM.");
			throw new ValidationException("IP Address Pool not found for " + peName);
		} else {
			debug(log,"Inventory Group Found:" + ig.getName());
			InventoryGroupManager igManager = PersistenceHelper.makeInventoryGroupManager();
			InventoryGroupEntitySearchCriteria igCriteria = igManager.makeInventoryGroupEntitySearchCriteria();
			igCriteria.setInventoryGroup(ig);
			igCriteria.setEntityClass(IPSubnet.class);
			igCriteria.setRange(0, 100);			
			
			List<InvGroupRef> invGroupRefs = igManager.findInvGroupRefsForInventoryGroup(igCriteria);
			debug(log,"invGroupRefs found: "+ invGroupRefs.size());
			
			IPSubnet mainSubnet = null;
			Collection<IPSubnet> ipSubnets = null;
			int mcounter=1;
			for (InvGroupRef igRef :invGroupRefs) {
				mainSubnet = igRef.getIPSubnet();
				debug(log,mcounter+") mainSubnet :"+mainSubnet.getId());				
				ipSubnets = UimHelper.findIPSubnet(mainSubnet.getIpAddressDomain().getName(), mainSubnet, suffix, ig);					
				if(!Utils.isEmpty(ipSubnets)) {
					for (IPSubnet ips : ipSubnets) {
						String isIPAddress=ips.getId().split("\\/")[0];
						if(ipAddress.equals(isIPAddress)) {
							childIPSubnet = ips;
							debug(log,"childIPSubnet found: "+childIPSubnet.getNetworkAddressAsString());
							break;
						}
					}					
				}				
				mcounter++;
				debug(log,"ipSubnet.getAvailableHostCount(): "+mainSubnet.getAvailableHostCount());
			}
			if(childIPSubnet==null) {
				throw new ValidationException(ipAddress+"/"+suffix+" IPSubnet not found");
			}
			
			debug(log,"getIPSubnetByPENamenIPAddress - END");
			return childIPSubnet;
		}
	}
	
	/**
	 * This method will get Property Vlue by the ConfigItemType 
	 * @param configItemType
	 * @param propName
	 * @return
	 */
	public static String getConfigItemTypePropertyValue(ConfigurationItemType configItemType, String propName){
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
	 * Convert the Properties map to HashMap
	 * 
	 * @param sci
	 * @return
	 */
	public static Map<String, String> getMapFromPropertiesMap(ServiceConfigurationItem sci){
		debug(log,"getMapFromPropertiesMap - START");
		Map<String, String> stringMap = new HashMap<>();
		
		Map<String, ServiceConfigurationItemCharacteristic> sciMap = sci.getCharacteristicMap();
		Set<String> keySet = sciMap.keySet();
		Iterator<String> iter = keySet.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			ServiceConfigurationItemCharacteristic sciChar = sciMap.get(key);
			
			stringMap.put(key, sciChar.getValue());
		}
		debug(log,"getMapFromPropertiesMap - END");
		return stringMap;
	}
	
	/**
	 * Fetch the value of a char from the specific network profile object.  
	 * 
	 * @param networkProfileName
	 * @param charName
	 * @return
	 * @throws ValidationException
	 */
	public static String getCharValueFromNetworkProfile(String networkProfileName, String charName) throws ValidationException {
		debug(log, "getCharValueFromNetworkProfile - Start");
		String vlanId = null;
		// Fetch the vlanId
		List<CustomObject> networkProfiles = 
				UimHelper.findCustomObject(Constants.SPEC_NETWORK_PROFILE, networkProfileName, CriteriaOperator.EQUALS_IGNORE_CASE, null, null, null, null);
		
		if(!Utils.isEmpty(networkProfiles)) {
			CustomObject networkProfile = networkProfiles.get(0);
			
			vlanId = UimHelper.getEntityCharValue(networkProfile, charName);
		}
		debug(log, "getCharValueFromNetworkProfile - End");
		
		return vlanId;
	}
	
	/**
	 * Get the CFS Service object from the current BI. 
	 * 
	 * @param config
	 * @return
	 * @throws ValidationException
	 */
	public static Service getCfsService(ServiceConfigurationVersion config, String...cfsSCSpec) throws ValidationException {
		debug(log,"getCfsService - START " + config);
		
		debug(log,"config : " + config);
		BusinessInteraction associatedBi = 
				oracle.communications.inventory.api.entity.utils.ConfigurationUtils.getAssociatedBusinessInteraction(config);
		debug(log,"associatedBi : " + associatedBi);
		BusinessInteraction associatedParentBi = associatedBi.getParentBusinessInteraction();
		debug(log,"associatedParentBi : " + associatedParentBi);
		
		BusinessInteractionManager businessInteractionManager = PersistenceHelper.makeBusinessInteractionManager();			
		BusinessInteractionItemSearchCriteria criteria = businessInteractionManager.makeBusinessInteractionItemSearchCriteria();
        CriteriaItem cItem = null;
        cItem = criteria.makeCriteriaItem();
        cItem.setValue(associatedParentBi);
        cItem.setOperator(CriteriaOperator.EQUALS);
        criteria.setBusinessInteraction(cItem);
        cItem = criteria.makeCriteriaItem();
        cItem.setValue(BusinessInteractionItemVisibility.SHOW);
        cItem.setOperator(CriteriaOperator.EQUALS);
        criteria.setVisibility(cItem);
        List<BusinessInteractionItem> biItemList = businessInteractionManager.findBusinessInteractionItem(criteria);
        
        Service cfs = null;
        
        for(BusinessInteractionItem biItem : biItemList) {
        	debug(log,"biItem: " + biItem);
        	debug(log,"bi: " + biItem.getBusinessInteraction());
        	if(cfsSCSpec != null && cfsSCSpec.length > 0) {
	        	if(biItem.getParticipatingEntitySpecName().contentEquals(cfsSCSpec[0])) {
		        	ServiceConfigurationVersion cfsSc = null;
					cfsSc = (ServiceConfigurationVersion)biItem.getToEntity();
		        	cfs = cfsSc.getService();
		        	debug(log,"CFS Service Id: " + cfs.getId());
	        	}
        	}else {
        		ServiceConfigurationVersion cfsSc = null;
				cfsSc = (ServiceConfigurationVersion)biItem.getToEntity();
	        	cfs = cfsSc.getService();
	        	debug(log,"CFS Service Id: " + cfs.getId());
        	}
        }       

		debug(log,"getCfsService - END");
		
		return cfs;
	}
	
	/**
	 * This method will create config item and Add LocalTollFree. 
	 * 
	 * @param config
	 * @param configItemType
	 * @return
	 * @throws ValidationException
	 */
	public static String addLocalTollFree(ServiceConfigurationVersion config, ConfigurationItemType configItemType) throws ValidationException{
		debug(log,"addLocalTollFree - START");
		
		String fixedLineNumber = "";
		String action = null;
		
		List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
		for(ConfigurationItemPropertyType prop: propList){
			String name = prop.getName();
			String value = prop.getValue();
			if(name.equals(Constants.PARAM_FIXEDLINENUMBER)){
				fixedLineNumber = value;
				if(fixedLineNumber == null)
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_FIXEDLINENUMBER);
			} else if(name.equals(Constants.PARAM_ACTION)){
				action = value;
				if(action == null)
					log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), Constants.PARAM_ACTION);
			}else {
				log.validationException(Constants.ERR_SERVICE_MISSING_PARAMETER, new IllegalArgumentException(), "");
			}
		}
		if(Constants.ADD.equalsIgnoreCase(action)) {
			// Assign the above found TN to given Service Config Item 
			DesignManager designManager = DesignHelper.makeDesignManager();
			ServiceConfigurationItem localTollFreeGroupCI = designManager.aquireConfigItem(config, Constants.PARAM_LOCALTOLLFREEGROUP_CI);
			oracle.communications.inventory.techpack.common.ServiceManager serviceManager = CommonHelper.makeServiceManager();
			ServiceConfigurationItem localtollfreeCI = serviceManager.addChildConfigItem(config, localTollFreeGroupCI , Constants.PARAM_LOCALTOLLFREE_CI);
			HashMap<String, String> charMap = new HashMap<>();
			charMap.put(Constants.PARAM_FIXEDLINENUMBER, fixedLineNumber);
			charMap.put(Constants.PARAM_ACTION, action);
			UimHelper.setConfigItemChars(config, localtollfreeCI, charMap);
		}else if(Constants.DELETE.equalsIgnoreCase(action)) {
			DesignManager designManager = DesignHelper.makeDesignManager();
			ServiceConfigurationItem localTollFreeGroupCI = designManager.aquireConfigItem(config, Constants.PARAM_LOCALTOLLFREEGROUP_CI);
			List<ServiceConfigurationItem> childSciList = localTollFreeGroupCI.getChildConfigItems();
			for(ServiceConfigurationItem childSci : childSciList) {
				String childSciFixedLineNumber = UimHelper.getConfigItemCharValue(config, childSci, Constants.PARAM_FIXEDLINENUMBER);
				if(childSciFixedLineNumber.equals(fixedLineNumber)) {
					HashMap<String, String> charMap = new HashMap<>();
					charMap.put(Constants.PARAM_ACTION, Constants.DELETE);
					UimHelper.setConfigItemChars(config, childSci, charMap);
					break;
				}
			}
		}
		debug(log,"addLocalTollFree - END");
		
		return fixedLineNumber;
	}
	
	
	/**
	 * This method will create config item and Add PBX_Auxillary_CI. 
	 * 
	 * @param config
	 * @param configItemType
	 * @throws ValidationException
	 */
	public static void managePBXAuxillaryNumbers(ServiceConfigurationVersion config, ConfigurationItemType configItemType) throws ValidationException{
		debug(log,"addPBXAuxillaryNumbers - START");
		
		String action = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_ACTION);
		String memberNumber = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_MEMBER_NUMBER);
		String auxillaryNumber = UimHelper.getConfigItemTypePropertyValue(configItemType, Constants.PARAM_AUXILIARY_NUMBER);
		
		if(Constants.ADD.equalsIgnoreCase(action)) {
			// Assign the above found TN to given Service Config Item 
			DesignManager designManager = DesignHelper.makeDesignManager();
			ServiceConfigurationItem localTollFreeGroupCI = designManager.aquireConfigItem(config, Constants.PARAM_PBXAUXILIARYNUMBERS_CI);
			oracle.communications.inventory.techpack.common.ServiceManager serviceManager = CommonHelper.makeServiceManager();
			ServiceConfigurationItem localtollfreeCI = serviceManager.addChildConfigItem(config, localTollFreeGroupCI , Constants.PARAM_PBXAUXILIARY_CI);
			HashMap<String, String> charMap = new HashMap<>();
			charMap.put(Constants.PARAM_MEMBER_NUMBER, memberNumber);
			charMap.put(Constants.PARAM_AUXILIARY_NUMBER, auxillaryNumber);
			charMap.put(Constants.PARAM_ACTION, action);
			UimHelper.setConfigItemChars(config, localtollfreeCI, charMap);
		}else if(Constants.DELETE.equalsIgnoreCase(action)) {
			DesignManager designManager = DesignHelper.makeDesignManager();
			ServiceConfigurationItem pbxAuxillaryNumbersCI = designManager.aquireConfigItem(config, Constants.PARAM_PBXAUXILIARYNUMBERS_CI);
			List<ServiceConfigurationItem> childSciList = pbxAuxillaryNumbersCI.getChildConfigItems();
			for(ServiceConfigurationItem childSci : childSciList) {
				String childMemberNumber = UimHelper.getConfigItemCharValue(config, childSci, Constants.PARAM_MEMBER_NUMBER);
				String childAuxillaryNumber = UimHelper.getConfigItemCharValue(config, childSci, Constants.PARAM_AUXILIARY_NUMBER);
				
				if(childMemberNumber.equals(memberNumber) && childAuxillaryNumber.equals(auxillaryNumber)) {
					HashMap<String, String> charMap = new HashMap<>();
					charMap.put(Constants.PARAM_ACTION, Constants.DELETE);
					UimHelper.setConfigItemChars(config, childSci, charMap);
					break;
				}
			}
		}
		debug(log,"addPBXAuxillaryNumbers - END");
	}
	
	/**
	 * Get the CustomerType from CFS service. 
	 * 
	 * @param cfs
	 * @return
	 */
	public static String getCustomerType(Service cfs) {
		Party customer = null;
		String customerType = null;
		if(cfs != null) {
	        // Fetch Party and set customer attributes
			Set<PartyServiceRel> partySet = cfs.getParty();
			Iterator<PartyServiceRel> partyIter = partySet.iterator();
			while(partyIter.hasNext()) {
				PartyServiceRel partyServiceRel = partyIter.next();
				customer = partyServiceRel.getParty();
				customerType = UimHelper.getEntityCharValue(customer, Constants.PARAM_CUSTOMERTYPE);
				break;
			}
        }
		
		return customerType;
	}
	
	/**
	 * Find the TelephoneNumber based on spec name, phone number and chars.
	 * 
	 * @param tnSpec
	 * @param name
	 * @param value
	 * @param criteriaOperator
	 * @return List<TelephoneNumber> 
	 * @throws ValidationException
	 */
	public static List<TelephoneNumber> findAllTelephoneNumbers(String specName, String phoneNumber, Map<String, String> charMap, CriteriaOperator criteriaOperator, String assignmentState, String adminState) throws  ValidationException {
		debug(log,"findAllTelephoneNumbers - START");

		TelephoneNumberManager manager = PersistenceHelper.makeTelephoneNumberManager();
		TelephoneNumberSearchCriteria criteria =  manager.makeTelephoneNumberSearchCriteria();

		//Set Specification
		if(null!=specName){
			try {
				TelephoneNumberSpecification spec = EntityUtils.findSpecification(TelephoneNumberSpecification.class, specName);
				if(spec != null) {
					criteria.setTelephoneNumberSpecification(spec);
				}
			} catch (Exception e){
				log.validationException(C2A_COULD_NOT_FIND, new java.lang.IllegalArgumentException(), specName);
			}
		}

		//Set Name
		if(!Utils.checkBlank(phoneNumber)) {
			CriteriaItem phoneNumberCriteriaItem = criteria.makeCriteriaItem();
			phoneNumberCriteriaItem.setOperator(criteriaOperator);
			phoneNumberCriteriaItem.setValue(phoneNumber);
			criteria.setName(phoneNumberCriteriaItem);
		}
		
		//Set Inventory State
		if(null != adminState)
		{
			CriteriaItem adminStateCriteriaItem = criteria.makeCriteriaItem();
			adminStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			adminStateCriteriaItem.setValue(adminState);
			criteria.setAdminState(adminStateCriteriaItem);
		}
		
		//Set Assignment State
		if(null !=assignmentState)
		{
			CriteriaItem assignmentStateCriteriaItem = criteria.makeCriteriaItem();
			assignmentStateCriteriaItem.setOperator(CriteriaOperator.EQUALS);
			assignmentStateCriteriaItem.setValue(assignmentState);
			criteria.setAssignmentType(assignmentStateCriteriaItem);
		}

		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(TNCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}		
		
		List<TelephoneNumber> tnList = manager.findTelephoneNumbers(criteria);
		debug(log,"findAllTelephoneNumbers - END, TNs Found: " + tnList.size());
		return tnList;
	}
	
	/**
	 * This method applies locking to a DI object
	 * @param di
	 * @param secs
	 * @return DeviceInterface
	 * @throws ValidationException
	 */
	public static DeviceInterface applyRowLock(DeviceInterface di, Integer secs) throws ValidationException {
        debug(log, "applyRowLock - START ", di);
        Collection<DeviceInterface> collection = new ArrayList<>();
        collection.add(di);
        LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
        lockPolicy.setNumberOfResources(1);
        lockPolicy.setExpiration(secs * 1000); //Locking for 60 sec
        lockPolicy.setFilterExistingLocks(true);
        RowLockManager rowLockMgr = PersistenceHelper.makeRowLockManager();
        Collection<DeviceInterface> lockedCollection = rowLockMgr.lock(collection, lockPolicy);
        if(lockedCollection == null || lockedCollection.isEmpty()) {
        	throw new ValidationException("Locking of the object failed. Please retry.");
        }        
        debug(log, "applyRowLock - END with size " + lockedCollection.size());
        return lockedCollection.iterator().next();        
	}
    
	/**
	 * This method releases lock for list of DI objects
	 * @param list
	 * @throws ValidationException
	 */
	public static void releaseRowLock(List<DeviceInterface> list)  throws ValidationException {
        debug(log, "releaseRowLock - START, List Size: " + list.size());        
        RowLockManager rowLockMgr = PersistenceHelper.makeRowLockManager();
        rowLockMgr.releaseLock(list);
        debug(log, "releaseRowLock - END");
    }
	
	/**
	 * Convert the Entity Char Map to a HashMap with String and String. 
	 */
	public static Map<String, String> getCharacteristicMap(Map<String, ServiceConfigurationItemCharacteristic> entityCharMap) {
		debug(log, "getCharacteristicMap - START");
		HashMap<String, String> charMap = new HashMap<>();
		Set<String> charKeySet = entityCharMap.keySet();
		Iterator<String> charKeyIter = charKeySet.iterator();
		while(charKeyIter.hasNext()) {
			String key = charKeyIter.next();
			CharValue charValue = entityCharMap.get(key);
			charMap.put(charValue.getName(), charValue.getValue());
			debug(log, "Name: " + charValue.getName() + "\tValue: " + charValue.getValue());
		}
		
		debug(log, "getCharacteristicMap - END");
		
		return charMap;
	}
	
	
	/**
	 * Convert the List to a HashMap with String and String. 
	 */
	public static HashMap<String, String> getParamMap(List<ParameterType> paramaters) {
		debug(log, "getParamMap - START");
		HashMap<String, String> paramMap = new HashMap<>();
		for(ParameterType param : paramaters) {
			if(Utils.checkNull(param.getValue()))
				paramMap.put(param.getName(), "");
			else {
				try {
					paramMap.put(param.getName(), EntityUtils.getStringType(param, param.getName()));
					debug(log, "Name " + param.getName() + " Value : " + EntityUtils.getStringType(param, param.getName()));

				} catch (ValidationException e) {
					debug(log, "Exception : Name " + param.getName());
				}
			}
		}
		
		debug(log, "getParamMap - END");
		return paramMap;
	}
	
	/**
	 * Fetch the actions from the BusinessInteractionAttachment from the actions. 
	 * @param associatedParentBi
	 * @return
	 * @throws ValidationException
	 */
	public static boolean isDoubleAction(BusinessInteraction associatedParentBi) 
			throws ValidationException{
		debug(log, "isDoubleAction - Start");
		List<BusinessInteractionAttachment> biAttachList = associatedParentBi.getAttachments();
		boolean disconnectAction = false;
		boolean createAction = false;
		boolean validCombination = false;
		try {
			for(BusinessInteractionAttachment biAttachment : biAttachList) {
				String xmlContent = biAttachment.convertContentToString();
				debug(log,"xmlContent:" ,xmlContent);
				if(xmlContent.startsWith("<bus:interaction")) {
					InteractionDocument doc = InteractionDocument.Factory.parse(xmlContent);
					// Get BIItemType Array
					List<BusinessInteractionItemType> orderItemList = doc.getInteraction().getBody().getItemList();
					if(!Utils.isEmpty(orderItemList) && orderItemList.size() > 1) {
						for(BusinessInteractionItemType orderItem : orderItemList) {
							String action = orderItem.getService().getAction();
							String serviceSpecName =  orderItem.getService().getSpecification().getName();
							log.debug("", "Service action : " + action + " serviceSpecName : " + serviceSpecName);
							if(Constants.SA_DISCONNECT.equals(action)) {
								disconnectAction = true;
							}
							if(Constants.SA_CREATE.equals(action)) {
								createAction = true;
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("", e.getMessage());
			throw new ValidationException(e.getMessage());
		}
		if(disconnectAction && createAction) {
			validCombination = true;
		}else {
			validCombination = false;
		}
		
		debug(log, "isDoubleAction - END validCombination : " + validCombination);
		return validCombination;
	}
}
