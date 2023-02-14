package com.broadband.bulkrules.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.broadband.bulkrules.common.ImportConstants;
import com.broadband.utils.EntityHelper;

import oracle.communications.inventory.api.common.BaseInvManager;
import oracle.communications.inventory.api.common.EntityUtils;
import oracle.communications.inventory.api.custom.CustomObjectManager;
import oracle.communications.inventory.api.custom.CustomObjectSearchCriteria;
import oracle.communications.inventory.api.entity.CharacteristicSpecUsage;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectSpecification;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.PersistenceHelper;

public class BulkImportHelper {

	protected static Log log = LogFactory.getLog(BulkImportHelper.class);

	/**
	 * Use this wrapper method for debug logging. Create separately in each class.
	 * @param s
	 */
	private static void debug(String s){
		if(log.isDebugEnabled())
			log.debug("", s);
	}
	
	@SuppressWarnings("rawtypes")
	public void addCharacteristics(Specification spec, CharacteristicExtensible characteristicExtensible,
			Map<String, String> charMap) throws ValidationException{
		try {
			Set<CharacteristicSpecUsage> characteristicSpecUsages = spec.getCharacteristicSpecUsages();
			HashSet<CharacteristicSpecification> specCharacteristics = new HashSet<>();
			if (characteristicSpecUsages != null) {
				for (CharacteristicSpecUsage characteristicSpecUsage : characteristicSpecUsages) {
					specCharacteristics.add(characteristicSpecUsage.getCharacteristicSpecification());
				}
			}
			for (Entry<String, String> headerEntry : charMap.entrySet()) {
				if (headerEntry.getKey().equalsIgnoreCase(ImportConstants.SPECIFICATION)
						|| headerEntry.getKey().equalsIgnoreCase(ImportConstants.DESCRIPTION) || headerEntry.getKey().equalsIgnoreCase(ImportConstants.NAME)) {
					continue;
				}
				for (CharacteristicSpecification specCharacteristic : specCharacteristics) {
					if (headerEntry.getKey().equals(specCharacteristic.getName())) {
						String val = headerEntry.getValue();
						if (!Utils.checkNull(val) && !Utils.isBlank(val)) {
							EntityUtils.setValue(characteristicExtensible, specCharacteristic.getName(), val);
							break;
						}
					}

				}
			}
		} catch (Exception exe) {
			log.error("Exception on addCharacteristics method :"+ exe.getMessage(),exe);
			throw new ValidationException(exe.getMessage());
		}
	}
	    
    /**
	 * addCharacteristics method to add characteristics and its value into
	 * Characteristic HashSet object only for Custom Object
	 * 
	 * @param spec
	 * @param characteristicExtensible
	 * @param List
	 *            <String> row
	 * @param List
	 *            <String> heading
	 */
    public void addCharacteristicsForCO(Specification spec, CharacteristicExtensible characteristicExtensible, List<String> row, List<String> heading) throws Exception {
		debug("Entered into addCharacteristicsForCO");
		try {
			// get the characteristics of the Specification
			Set<CharacteristicSpecUsage> characteristicSpecUsages = spec.getCharacteristicSpecUsages();
			HashSet<String> specCharacteristics = new HashSet<String>();
			if (characteristicSpecUsages != null) {
				for (CharacteristicSpecUsage characteristicSpecUsage : characteristicSpecUsages) {
					specCharacteristics.add(characteristicSpecUsage.getCharacteristicSpecification().getName());
				}
			}
			debug( "No of characterstics in " + spec.getName() + " is "	+ specCharacteristics.size());
			
			for (String header : heading) {
				if(header.equalsIgnoreCase("Name") || header.equalsIgnoreCase("Description") || header.equalsIgnoreCase("Specification")){ 
					continue;
				}
				if (specCharacteristics.contains(header))
				{
					String val = row.get(heading.indexOf(header));
					
					debug("Char Value = "+val);
					if (val == null || val.trim().equals("")) {
						debug("Not updating the Char Value for Char Name = "+header + " as the Value is empty.");
						//break;
					}
					else
					{
						EntityHelper.setValue(characteristicExtensible, header, val);
					}
				}
			}
		} catch (Exception exe) {
			log.error("Exception occured in addCharacteristicsForCO method :"+ exe.getMessage(),exe);
			throw new ValidationException(exe.getMessage());
		}
		debug("Exited from addCharacteristicsForCO method.");
	}
    
	/**
	 * To get the CustomObjects object.
	 * 
	 * @param ObjectSpec
	 * @param name
	 * @return
	 * @throws ValidationException
	 * @throws oracle.communications.inventory.api.exception.ValidationException
	 */
	public List<CustomObject> getCustomObject(CustomObjectSpecification spec, String name)	throws ValidationException{
		debug( "Entered getCustomObject");
		CustomObjectManager mgr = PersistenceHelper.makeCustomObjectManager();
		List<CustomObject> list = new ArrayList<CustomObject>();

		CustomObjectSearchCriteria criteria = mgr.makeCustomObjectSearchCriteria();
		criteria.setCustomObjectSpecification(spec);

		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(name);
		nameCriteria.setOperator(CriteriaOperator.EQUALS);
		criteria.setName(nameCriteria);

		try {
			list = mgr.findCustomObjects(criteria);
			if (list != null && list.size() == 1) {
				debug("CustomObject found for Name " + name);
				debug("getCustomObject, List Size=" + list.size());
			} else if (list != null && list.size() > 1) {
				log.warn("", "More than 1 CustomObject found for Name " + name);
			} else {
				debug("CustomObject Not found for Name " + name);
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Custom objects.", e);
			throw e;
		}
		debug( "getCustomObject() End");
		return list;
	}

	/**
	 * To get the Logical Device object.
	 * 
	 * @param ObjectSpec
	 * @param name
	 * @param desc
	 * @return
	 * @throws ValidationException
	 * @throws oracle.communications.inventory.api.exception.ValidationException
	 */
	public List<LogicalDevice> getLogicalDevice(LogicalDeviceSpecification spec, String name)	throws ValidationException{
		debug("Entered getLogicalDevice()");
		
		LogicalDeviceManager mgr = PersistenceHelper.makeLogicalDeviceManager();
		List<LogicalDevice> list = new ArrayList<LogicalDevice>();

		LogicalDeviceSearchCriteria criteria = mgr.makeLogicalDeviceSearchCriteria();
		criteria.setLogicalDeviceSpecification(spec);

		// Name Criteria
		CriteriaItem nameCriteria = criteria.makeCriteriaItem();
		nameCriteria.setValue(name);
		nameCriteria.setOperator(CriteriaOperator.EQUALS);
		criteria.setName(nameCriteria);
		try {
			list = mgr.findLogicalDevice(criteria);
			if (list != null) {
				debug("getLogicalDeviceByName, List Size=" + list.size());
				if (list.size() == 1) {
					debug("Logical Device found for Name " + name);
				} else if (list.size() > 1) {
					log.warn("", "More than 1 Logical Device found for Name " + name);
				}
			} else {
				debug("Logical Device Not found for Name " + name);
			}
		} catch (oracle.communications.inventory.api.exception.ValidationException e) {
			log.error("", "Error while getting the Logical Device.", e);
			throw e;
		}
		debug("getLogicalDevice() End");
		return list;
	}
}
