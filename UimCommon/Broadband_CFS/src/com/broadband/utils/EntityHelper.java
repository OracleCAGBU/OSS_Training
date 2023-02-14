package com.broadband.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.broadband.bulkrules.common.BulkImportHelper;
import com.broadband.bulkrules.common.ImportConstants;
import com.broadband.utils.Constants;

import oracle.communications.inventory.api.TimeBound;
import oracle.communications.inventory.api.characteristic.CharacteristicManager;
import oracle.communications.inventory.api.characteristic.impl.CharacteristicHelper;
import oracle.communications.inventory.api.common.EntityUtils;
import oracle.communications.inventory.api.custom.CustomObjectManager;
import oracle.communications.inventory.api.custom.CustomNetworkAddressManager;
import oracle.communications.inventory.api.custom.CustomObjectSearchCriteria;
import oracle.communications.inventory.api.entity.CharacteristicSpecUsage;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ControlType;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectCharacteristic;
import oracle.communications.inventory.api.entity.CustomObjectSpecification;
import oracle.communications.inventory.api.entity.CustomNetworkAddress;
import oracle.communications.inventory.api.entity.CustomNetworkAddressCharacteristic;
import oracle.communications.inventory.api.entity.CustNetAddrSpecification;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceSpecification;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicLocation;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.PlaceSpecification;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationItemCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.RootEntity;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.place.PlaceManager;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.inventory.xmlbeans.ConfigurationItemPropertyType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;

public class EntityHelper {
	
private static final Log log = LogFactory.getLog(EntityHelper.class);
	
	private static void debug(String message){
		if(log.isDebugEnabled())
			log.debug("", message);
	}
	
	/* This method will create the Custom Object
	 * @param coName
	 * @param coSpecName
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	
	public static CustomObject createCustomObject(String coName, String coSpecName, Map<String,String> charMap) throws ValidationException {
		debug("createCustomObject - START");
		
		CustomObjectManager manager = PersistenceHelper.makeCustomObjectManager();
		CustomObject co = manager.makeCustomObject();
		co.setName(coName);
		co.setSpecification(EntityUtils.findSpecification(CustomObjectSpecification.class, coSpecName));
		if(charMap!=null && charMap.size()>0){
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				EntityHelper.setValue(co, entry.getKey(), entry.getValue());
			}
		}
		
		List<CustomObject> list = new ArrayList<>();
		list.add(co);
		list = manager.createCustomObjects(list);
		if(log.isDebugEnabled()) {
			log.debug("", list.get(0));
		}
		debug("createCustomObject - END");
		return !Utils.isEmpty(list) ? list.get(0) : null;
	}
	
	/**
	 * This method will create the Custom Network Address
	 * @param coName
	 * @param coSpecName
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	public static CustomNetworkAddress createCustomNetworkAddress(String ciName, String ciSpecName, ConfigurationItemType configItemType) throws ValidationException {
		debug("createCustomNetworkAddress - START");
		
		CustomNetworkAddressManager manager = PersistenceHelper.makeCustomNetworkAddressManager();
		CustomNetworkAddress co = manager.makeCustomNetworkAddress();
		co.setName(ciName);
		co.setSpecification(EntityUtils.findSpecification(CustNetAddrSpecification.class, ciSpecName));
		
		HashMap<String,String> charMap = new HashMap<>();
		List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
		for(ConfigurationItemPropertyType prop: propList){
			charMap.put(prop.getName(), prop.getValue());
		}
		
		if(charMap!=null && charMap.size()>0) {
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				EntityHelper.setValue(co, entry.getKey(), entry.getValue());
			}
		}
		
		List<CustomNetworkAddress> list = new ArrayList<>();
		list.add(co);
		list = manager.createCustomNetworkAddress(list);
		if(log.isDebugEnabled()) {
			log.debug("", list.get(0));
		}
		debug("createCustomNetworkAddress - END");
		return !Utils.isEmpty(list) ? list.get(0) : null;
	}
	
	/**
	 * This methods finds customobject based on search criteria
	 * @author rpamuru
	 * @param name
	 * @param coSpecName
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	public static List<CustomObject> findCustomObjects(String name, String coSpecName, Map<String, String> charMap, CriteriaOperator criteriaOperator) throws ValidationException {
		debug("findCustomObject Method: Start");
		if(log.isDebugEnabled()) {
			log.debug("", "Custom Object spec: "+coSpecName+" and name: "+name);
		}
		CustomObjectManager coManager = PersistenceHelper.makeCustomObjectManager();
		CustomObjectSearchCriteria coSearchCriteria = coManager.makeCustomObjectSearchCriteria();
		List<CustomObject> coList = null;
		coSearchCriteria.setAdminState(InventoryState.INSTALLED);
		if(!Utils.checkBlank(coSpecName)) {
			coSearchCriteria.setCustomObjectSpecification(EntityUtils.findSpecification(CustomObjectSpecification.class, coSpecName));			
		}
		if(!Utils.checkBlank(name)) {
			CriteriaItem nameCI = coSearchCriteria.makeCriteriaItem();
			nameCI.setName("name");
			nameCI.setValue(name);
			nameCI.setOperator(CriteriaOperator.EQUALS);
			coSearchCriteria.setCriteriaItem(nameCI);
		}
		Collection<CriteriaItem> collCI = new ArrayList<>();
		if(!Utils.checkNull(charMap)) {
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = coSearchCriteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(CustomObjectCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				if(Utils.checkNull(criteriaOperator)) {
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS);
				} else {
					charCriteriaItem.setOperator(criteriaOperator);
				}
				collCI.add(charCriteriaItem);
			}
		}
		if(!Utils.isEmpty(collCI)) {
			coSearchCriteria.addCharacteristicData(collCI);
		}
		coList = coManager.findCustomObjects(coSearchCriteria);		
		debug("findCustomObject Method: End");
		return coList;
	}
	
	@SuppressWarnings("rawtypes")
	public void addCharacteristics(Specification spec, CharacteristicExtensible characteristicExtensible,
			Map<String, String> charMap) {
		try {

			Set<CharacteristicSpecUsage> characteristicSpecUsages = spec.getCharacteristicSpecUsages();
			HashSet<CharacteristicSpecification> specCharacteristics = new HashSet<>();
			if (characteristicSpecUsages != null) {
				for (CharacteristicSpecUsage characteristicSpecUsage : characteristicSpecUsages) {
					specCharacteristics.add(characteristicSpecUsage.getCharacteristicSpecification());
				}
			}

			for (String header : charMap.keySet()) {
				if (header.equalsIgnoreCase(ImportConstants.SPECIFICATION)
						|| header.equalsIgnoreCase(ImportConstants.DESCRIPTION) || header.equalsIgnoreCase(ImportConstants.NAME)) {
					continue;
				}
				for (CharacteristicSpecification specCharacteristic : specCharacteristics) {

					if (header.equals(specCharacteristic.getName())) {
						String val = charMap.get(header); 

						if (val == null || val.trim().equals("")) {
							break;
						}
						EntityUtils.setValue(characteristicExtensible, specCharacteristic.getName(), val);
						break;
					}

				}
			}
		} catch (Exception exe) {
			log.error("", exe);			
		}
	}
	
	/**
	 * This method creates the Logical Device.
	 * @param ldName
	 * @param ldSpecName
	 * @param charMap
	 * @return
	 * @throws ValidationException
	 */
	public static LogicalDevice createLogicalDevice(String ldName, String specName, Map<String,String> charMap) throws ValidationException {
		debug("createLogicalDevice - START");	
		
		LogicalDevice ld = makeLogicalDevice(specName, ldName, charMap);
		debug("LD Name: " + ld.getName() + " make.");
		List<LogicalDevice> ldList = new ArrayList<>(1);
		ldList.add(ld);
		ldList = PersistenceHelper.makeLogicalDeviceManager().createLogicalDevice(ldList);

		if(!Utils.isEmpty(ldList)) {
			ld = ldList.get(0);
			debug("LD Name: " + ld.getName() + " created.");
		} else {
			throw new ValidationException("Device creation failed.");
		}
		
		debug("createLogicalDevice - END");
		return !Utils.isEmpty(ldList) ? ldList.get(0): null;
	}
	
	/**
	 * This method makes a transient Device Interface.
	 * @param device
	 * @param diName
	 * @param diSpecName
	 * @param charMap
	 * @return
	 * @throws ValidationException 
	 */
	public static DeviceInterface makeDeviceInterface(String diName, String diSpecName, Map<String,String> charMap) throws ValidationException{
		debug("makeDeviceInterface - START");
		
		LogicalDeviceManager ldManager = PersistenceHelper.makeLogicalDeviceManager();
		DeviceInterface di = ldManager.makeDeviceInterface();
		di.setName(diName);
		di.setSpecification(EntityUtils.findSpecification(DeviceInterfaceSpecification.class, diSpecName));
		if(charMap!=null && charMap.size()>0){
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				EntityHelper.setValue(di, entry.getKey(), entry.getValue());
			}
		}
		
		debug("makeDeviceInterface - END");
		return di;
	}
	
	/**
	 * This method creates a list of Device Interfaces under a given Logical Device
	 * @param device
	 * @param diList
	 * @throws ValidationException
	 */
	public static void createDeviceInterface(LogicalDevice device, List<DeviceInterface> diList) throws ValidationException {
		debug("createDeviceInterface - START, device: " + device + " ,diList: " + diList.size());
		
		LogicalDeviceManager ldManager = PersistenceHelper.makeLogicalDeviceManager();
		ldManager.createDeviceInterface(device, diList);
		
		debug("createDeviceInterface - END");
	}
	
	/**
	 * This method will create the Place of type Address or Location based on the spec.
	 * @param name
	 * @param specName
	 * @param charMap
	 * @return
	 * @throws Exception  
	 */
	public static GeographicPlace createPlace(String name, String specName, Map<String,String> charMap, String latitude, String longitude) throws ValidationException {
		debug("createPlace - START name:"+name +" specName : "+specName+ " charMap: "+charMap+ " latitude: "+latitude + " longitude: "+longitude);
		
		PlaceManager placeManager = PersistenceHelper.makePlaceManager();
		Collection<GeographicPlace> placeList = new ArrayList<>();
		PlaceSpecification spec = EntityUtils.findSpecification(PlaceSpecification.class, specName);
		GeographicAddress address = null;
		GeographicLocation location = null;
		BulkImportHelper helper = new BulkImportHelper();
		debug ("spec: "+spec);
		debug ("spec getEntityType : "+spec.getEntityType());
		debug ("spec getInstanceType : "+spec.getInstanceType());
		if (!Utils.isBlank(specName) && specName.equals(Constants.SPEC_DEVICEADDRESS)) {
			charMap.put(Constants.COUNTRY, Constants.COUNTRY_FIJI);
		}
		if(spec.getInstanceType().contains("GeographicAddress")){
			debug("GeographicAddress ");
			address = placeManager.makeGeographicPlace(GeographicAddress.class);
			address.setSpecification(spec);
			address.setName(name);
			if(!Utils.checkBlank(latitude) && !Utils.checkBlank(longitude))
			{
				address.setLatitude(latitude);
				address.setLongitude(longitude);
			}
				
			if(charMap!=null && charMap.size()>0){
				helper.addCharacteristics(spec, address, charMap);
			}
			placeList.add(address);
		} else if(spec.getInstanceType().contains("GeographicLocation")){
			debug("GeographicLocation ");
			location = placeManager.makeGeographicPlace(GeographicLocation.class);
			location.setSpecification(spec);
			location.setName(name);
			if(!Utils.checkBlank(latitude) && !Utils.checkBlank(longitude))
			{
				location.setLatitude(latitude);
				location.setLongitude(longitude);
			}
			if(charMap!=null && charMap.size()>0){
				helper.addCharacteristics(spec, location, charMap);
			}
			placeList.add(location);
		}
		
		debug("placeList.size: "+placeList.size());
		debug("address :"+address);
		List<? extends GeographicPlace> createdPlaces = placeManager.createGeographicPlace(placeList);
				
		debug("createPlace - END"+createdPlaces);
		if(!Utils.isEmpty(createdPlaces))
			return createdPlaces.get(0);
		else
			return null;
	}
	
	public static LogicalDevice makeLogicalDevice(String specName, String ldName, Map<String,String> charMap) throws ValidationException {
		debug("makeLogicalDevice - START");
		
		LogicalDeviceManager ldManager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDevice ld = ldManager.makeLogicalDevice();
		if(!Utils.isBlank(ldName)){
			ld.setName(ldName);
		}
		ld.setSpecification(EntityUtils.findSpecification(LogicalDeviceSpecification.class, specName));	  
	    
		HashSet<CharacteristicSpecification>validCharSpecs = CharacteristicHelper.getCharacteristicSpecifications(ld.getSpecification());
		
		HashSet<LogicalDeviceCharacteristic> charSet = new HashSet<>();
		if(charMap!=null && charMap.size()>0){
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				
				LogicalDeviceCharacteristic ldChar = ld.makeCharacteristicInstance();
				ldChar.setName(entry.getKey());
				ldChar.setValue(entry.getValue());
				
				CharacteristicSpecification charSpec = PersistenceHelper.makeCharacteristicManager().getCharacteristicSpecification(entry.getKey());
				
				boolean isCharSpecValid = false;
				if (null != charSpec && null!=charSpec.getName()) {
					for (CharacteristicSpecification validCharSpec : validCharSpecs) {
						if (validCharSpec.getName().equals(charSpec.getName())) {
							isCharSpecValid = true;
							break;
						}
					}
				} 
				if(isCharSpecValid) {
					ldChar.setCharacteristicSpecification(charSpec);
					charSet.add(ldChar);
				} else {
					log.debug("", "Characteristic Specification: " + entry.getKey() + " not valid for the LogicalDevice Specification: " + ld.getSpecification().getName());
					throw new ValidationException("Characteristic Specification: " + entry.getKey() + " not valid for the LogicalDevice Specification: " + ld.getSpecification().getName());	
				}
			}
			ld.setCharacteristics(charSet);
		}
		
		debug("makeLogicalDevice - END");
		return ld;
	}
	
	/**
	 * A wrapper method to Product API to do some additional Validations.
	 * @param target
	 * @param name
	 * @param value
	 * @throws ValidationException 
	 */
	public static void setValue(CharacteristicExtensible target, String name, Object value) throws ValidationException {
		debug("setValue - START, Char Name: " + name + " , Char Value: " + value + " , Char Target: " + target);
		
		if(null==target) {
			throw new ValidationException("Target Entity is null.");
		}
		if(Utils.isEmpty(name)) {
			throw new ValidationException("Char Name is null or empty.");
		}
		if(null==value) {
			throw new ValidationException("Char Value is null.");
		}
		if(Utils.isEmpty(value.toString())) {
			debug("Char Value is empty.");//This method allows to set the Char Value as Empty String.
		}
		
		debug("Setting Char Name: " + name + " , Char Value: " + value + " on Resource: " + target.getSpecification().getName());
		setValueC2a(target, name, value);//Calling c2a code to make sure that dropdown char label is set to null.
		debug("setValue - END");
	}
	
	
	/**
	 * This method is copied from c2a cartridge removed the validation as Validation was causing issue in setting ServiceAction field.
	 * @param scv
	 * @param configItem
	 * @param characteristicName
	 * @param value
	 */
	public static void addUpdateConfigItemCharacteristic(
	        ServiceConfigurationVersion scv,
	        ServiceConfigurationItem configItem, String characteristicName,
	        String value){
		boolean found = false;
		CharacteristicManager charmgr = PersistenceHelper
		        .makeCharacteristicManager();
		Set<ServiceConfigurationItemCharacteristic> characterictics = configItem
		        .getCharacteristics();
		for (CharValue characteristic : characterictics) {
			if (characteristic.getName().equals(characteristicName)) {
				characteristic.setValue(value);
				found = true;
				break;
			}
		}
		if (!found) {
			CharacteristicSpecification charSpec = charmgr
			        .getCharacteristicSpecification(characteristicName);
			if (charSpec == null) {
				log.error("c2a.addUpdateConfigItemCharacteristic",
				        characteristicName);
			}

			CharValue e = configItem.makeCharacteristicInstance();
			e.setCharacteristicSpecification(charSpec);
			e.setName(characteristicName);
			e.setValue(value);
			e.setLabel(characteristicName);
			characterictics.add((ServiceConfigurationItemCharacteristic) e);
		}
		configItem.setCharacteristics(characterictics);
	}
	
	/**
	 * Method copied from from c2a cartridge to set Label null as CFS Search Results is showing Label instead of Value for chars like Medium, AccountId.
	 * @param target
	 * @param name
	 * @param value
	 */
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	  public static void setValueC2a( CharacteristicExtensible target, String name, Object value )
	  {
	      Set<CharValue> chars = target.getCharacteristics();
	      CharValue thisChar = null;
	      for( CharValue c : chars )
	      {
	          if( name.equals( c.getName() ) )
	          {
	              thisChar = c;
	              break;
	          }
	      }
	      if( thisChar == null )
	      {
	          Specification spec = null;
	          if( target instanceof RootEntity )
	          {
	              RootEntity entity = (RootEntity) target;
	              spec = entity.getSpecification();
	          }
	          else if( target instanceof InventoryConfigurationItem )
	          {
	              InventoryConfigurationItem configItem = (InventoryConfigurationItem) target;
	              spec = configItem.getConfigSpec();
	          }
	          if( spec == null )
	          {
	              return;
	          }
	          CharacteristicSpecification cs = EntityUtils
	              .getCharacteristicSpecification( spec, name );
	          if( cs == null )
	          {
	              return;
	          }
	          thisChar = target.makeCharacteristicInstance();
	          thisChar.setCharacteristicSpecification( cs );
	          thisChar.setName( cs.getName() );
	          if( target instanceof TimeBound )
	          {
	              thisChar.setValidFor( ((TimeBound) target).getValidFor() );
	          }
	          if( cs.getEntityLinkClass() != null && value == null )
	          {
	              // not allowed to set reference to null on a characteristic
	              return;
	          }
	          chars.add( thisChar );
	          target.setCharacteristics( chars );
	      }
	      if( value instanceof RootEntity )
	      {
	          RootEntity entity = (RootEntity) value;
	          thisChar.setValue( entity.getName() );
	      }
	      else if( ControlType.CHECKBOX.equals( thisChar
	          .getCharacteristicSpecification().getControlType() ) )
	      {
	          String booleanValue = "true".equals( value.toString() )
	              ? "true"
	              : "false";
	          thisChar.setValue( booleanValue );
	      }
	      else
	      {
	          thisChar.setValue( value.toString() );
	      }
	      thisChar.setLabel(null);//CI WS is setting Char Name as Label and it is being displayed on Service Search Page. If Label is nullified then it picks from the Value field.
	  }
	 
	 
	@SuppressWarnings("unchecked")
	public static void addCharacteristics(Specification spec, CharacteristicExtensible characteristicExtensible,
				List<String> row, List<String> heading) {
		Set<CharacteristicSpecUsage> characteristicSpecUsages = spec.getCharacteristicSpecUsages();
		Set<CharacteristicSpecification> specCharacteristics = new HashSet<>();
		if (characteristicSpecUsages != null) {
			for (CharacteristicSpecUsage characteristicSpecUsage : characteristicSpecUsages) {
				specCharacteristics.add(characteristicSpecUsage.getCharacteristicSpecification());
			}
		}
		Set<CharValue> newCharacteristics = new HashSet<>();

		for (String header : heading) {

			for (CharacteristicSpecification specCharacteristic : specCharacteristics) {

				if (header.equals(specCharacteristic.getName()) && (row.get(heading.indexOf(header))).length() > 0) {
					CharValue charValue = characteristicExtensible.makeCharacteristicInstance();
					charValue.setCharacteristicSpecification(specCharacteristic);
					charValue.setName(specCharacteristic.getName());
					charValue.setValue(row.get(heading.indexOf(header)));
					newCharacteristics.add(charValue);
				}
			}
		}

		if (!Utils.isEmpty(newCharacteristics)) {
			Set<CharValue> oldCharacteristics = characteristicExtensible.getCharacteristics();
			if (!Utils.isEmpty(oldCharacteristics)) {
				Iterator<CharValue> oldCharacteristicsIterator = oldCharacteristics.iterator();
				Iterator<CharValue> newCharacteristicsIterator = newCharacteristics.iterator();
				Set<CharValue> temp = new HashSet<>();
				CharValue newCharValue = null;
				CharValue oldCharValue = null;
				while (newCharacteristicsIterator.hasNext()) {
					newCharValue = newCharacteristicsIterator.next();
					oldCharacteristicsIterator = oldCharacteristics.iterator();
					while (oldCharacteristicsIterator.hasNext()) {
						oldCharValue = oldCharacteristicsIterator.next();
						if (oldCharValue.getName().equalsIgnoreCase(newCharValue.getName()))
							temp.add(oldCharValue);
					}

					oldCharacteristicsIterator = null;
					newCharValue = null;
					oldCharValue = null;
				}
				oldCharacteristics.removeAll(temp);
				newCharacteristics.addAll(oldCharacteristics);
			}
			characteristicExtensible.setCharacteristics(newCharacteristics);
		}
	}
	
	public static Specification findAndValidateSpecification(String specName) throws ValidationException {
		try {
			return CommonHelper.makeCommonManager().findAndValidateSpecification(specName);
		} catch (Exception e) {
			if (FeedbackProviderImpl.hasErrors()) {
				FeedbackProviderImpl.getFeedbackProvider().reset();
			}
			return null;
		}
	}
}
