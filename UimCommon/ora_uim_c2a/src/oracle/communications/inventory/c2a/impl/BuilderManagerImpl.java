package oracle.communications.inventory.c2a.impl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.configuration.ConfigurationManager;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ConfigurationReferenceState;
import oracle.communications.inventory.api.entity.InventoryConfigurationSpec;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.SpecificationRel;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.Configurable;
import oracle.communications.inventory.api.entity.common.ConfigurationReference;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.Consumer;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.BuilderManager;
import oracle.communications.inventory.techpack.common.CommonManager;
import oracle.communications.inventory.techpack.common.ServiceManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.PropertyType;
import oracle.communications.inventory.xmlbeans.StructuredType;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;


public class BuilderManagerImpl implements BuilderManager {
	private static Log log = LogFactory.getLog(BuilderManager.class);
	
	@Override
	public InventoryConfigurationItem[] getConfigItemsByName(
		InventoryConfigurationVersion config, final String ciName) 
			throws ValidationException {
		InventoryConfigurationItem[] items = null;
		
		if (config instanceof ServiceConfigurationVersion) {  
			items = getServiceConfigItemsByName((ServiceConfigurationVersion) config, ciName);
		} else {
			//TODO Generalize the method or add other config type logic
		}		
		return items;
	}
	
	@Override
	public InventoryConfigurationItem getPendingConfigItemsByName(
		InventoryConfigurationVersion config, final String ciName) 
			throws ValidationException {
		InventoryConfigurationItem item = null;
		
		if (config instanceof ServiceConfigurationVersion) {  
			InventoryConfigurationItem[] items = getServiceConfigItemsByName((ServiceConfigurationVersion) config, ciName);
			for(InventoryConfigurationItem configItem : items){
				ConsumableResource assignmentEntity = this.getAssignedEntity(configItem);
    			if(assignmentEntity!=null){
    				if(assignmentEntity.getCurrentAssignment().getAdminState().equals(AssignmentState.PENDING_ASSIGN)){
    					item = configItem;
    					break;
    				}
    			} else {
    				if(configItem.getReference()!=null && ((ConfigurationReference)configItem.getReference()).getAdminState().equals(ConfigurationReferenceState.PENDING_REFERENCE)){
    					item = configItem;
    					break;
    				}
    			}
			}
		} else {
			//TODO Generalize the method or add other config type logic
		}	
		return item;
	}
	
	@Override
	public InventoryConfigurationItem acquireUnusedConfigItem(
		InventoryConfigurationVersion config, final String ciName, final int maxAllowed) 
			throws ValidationException {		
		InventoryConfigurationItem ci = null;
		
		if (config instanceof ServiceConfigurationVersion) {  
			ci = acquireUnusedServiceConfigItem(
				(ServiceConfigurationVersion) config, ciName, maxAllowed); 
		} else {
			//TODO Generalize the method or add other config type logic
		}
		return ci;
	}
	
	@Override
	public InventoryConfigurationItem acquireUnusedConfigItem(
		InventoryConfigurationVersion config, final String ciName) 
			throws ValidationException {
		//maxAllowed=0 means there is no limits on number of CIs
		return acquireUnusedConfigItem(config, ciName, 0);
	}
	
	@Override
	public InventoryConfigurationItem getFirstAssignedComponentConfigItem(			
		InventoryConfigurationVersion config,
		final String configItemName,
		final String specName) 
			throws ValidationException {
		InventoryConfigurationItem ci = null;
		
		if (config instanceof ServiceConfigurationVersion) {  		
			ci = getFirstAssignedComponentServiceConfigItem(
				(ServiceConfigurationVersion) config, configItemName, specName);
		} else {
			// Do nothing for now - for future implementation
		}	
		return ci;
	}
	
	@Override
	public ConsumableResource getFirstAssignedComponent(			
		InventoryConfigurationVersion config,
		final String configItemName,
		final String specName) 
			throws ValidationException {
				
		ConsumableResource result = null;
		InventoryConfigurationItem ci = 
			getFirstAssignedComponentConfigItem(config, configItemName, specName);
		
		if (ci != null) {
			Persistent ps = ci.getToEntity();
			
			if (ps != null && ps instanceof Consumer) {
				result = ((Consumer) ps).getResource();
			}
		}		
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setConfigItemProperty(
		InventoryConfigurationItem configItem,
		String propertyName, String value) 
			throws ValidationException {
		boolean found = false;
		Set<CharValue> chars = ((CharacteristicExtensible)configItem).getCharacteristics();

		if (chars == null) {
			chars = new HashSet<CharValue>();
		}
			
		for (CharValue characteristic : chars) {
			CharacteristicSpecification charSpec = 
				characteristic.getCharacteristicSpecification();
			
			if (charSpec.getName().equals(propertyName)) {
				characteristic.setValue(value);
				found = true;
				break;
			}
		}
		if (!found) {
			CommonManager commonManager = CommonHelper.makeCommonManager();					
			CharValue charvalue = commonManager.makeCharValue(
				(CharacteristicExtensible) configItem, propertyName, value);
			chars.add(charvalue);
		}
		((CharacteristicExtensible)configItem).setCharacteristics(chars);
	}
	
	@Override
	public void setEntityProperty(
		CharacteristicExtensible<?> entity,
	    String name, String value) 
	    	throws ValidationException {
		CharacteristicSetter setter = new CharacteristicSetter(entity, name);
		setter.setValue(value);
	}
	
	@Override
	public  Specification checkSpecificationExistance(
		Class<?> cls, String specName)  
			throws ValidationException {
		Specification spec = null;
		try {
			spec = PersistenceHelper.makeSpecManager().findLatestActiveSpec(
				specName, cls, false);
		} catch (Exception x) {
			log.validationException("c2a.specNotFound", x, specName);
		}
		return spec;
	}	

	@Override	
	public Persistent getConfigItemTarget(
		InventoryConfigurationItem configItem)  
			throws ValidationException {				
		Persistent result = null;
		
		if (configItem != null) {
			result = configItem.getToEntity();
			
			if (result == null) { 
				result = configItem.getReference();				
			}
		}				
		return result;
	}
	
	@Override
	public ConfigurationReferenceEnabled getReferencedEntity(
		InventoryConfigurationItem configItem)  
			throws ValidationException {							
		ConfigurationReferenceEnabled result = null;
		
		if (configItem != null) {
			Persistent p = configItem.getReference();
							
			if (p != null && p instanceof ConfigurationReference) {
				result = ((ConfigurationReference) p).getReferenced();
			}
		}				
		return result;
	}
	
	@Override
	public ConsumableResource getAssignedEntity(
		InventoryConfigurationItem configItem)  
			throws ValidationException {							
		ConsumableResource result = null;
		
		if (configItem != null) {
			Persistent p = configItem.getToEntity();
							
			if (p != null && p instanceof Consumer) {
				result = ((Consumer) p).getResource();
			}
		}				
		return result;
	}
	
	@Override
	public void populateStructuredParameterConfigItem(
		InventoryConfigurationItem configItem,
		StructuredType complexParam) 
			throws ValidationException {
		//TODO - Support for child StructuredType members 
		if (configItem!=null && complexParam!=null) {			
			List<PropertyType> props = complexParam.getPropertyList();					
			
			for (PropertyType p : props) {
				String n = p.getName();							
				String v = p.getValue();
				setConfigItemProperty(configItem, n, v);				
			}				
			
			List<StructuredType> children = complexParam.getChildList();					
			
			for (StructuredType child : children) {
				populateStructuredParameter(configItem, child);			
			}
		}
		
		BaseConfigurationManager configManager = PersistenceHelper
			.makeConfigurationManager(configItem.getConfiguration().getClass());
		Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
		updateList.add(configItem);
		try {
			configManager.updateConfigurationItem(updateList);				
		}catch(Exception ve) {
			ve.printStackTrace();
			throw new ValidationException(ve.getMessage());
		}
	}
	
	@Override
	public void populateStructuredParameters(
		InventoryConfigurationItem parentConfigItem,
		Iterator<StructuredType> iterator)
			throws ValidationException {	
		String name = "";
		InventoryConfigurationItem preconfiguredItem = null;
		if (parentConfigItem!=null && iterator!=null) {
			while(iterator.hasNext()) {
				StructuredType complexParam = iterator.next();
				if((name = complexParam.getName())!=null && !name.isEmpty()) {
					List<InventoryConfigurationItem> children = 
							(List<InventoryConfigurationItem>) parentConfigItem.getChildConfigItems();
						InventoryConfigurationItem paramCi = null;
						if(preconfiguredItem == null) {
							for (InventoryConfigurationItem i : children) {
								if (i.getName().equalsIgnoreCase(name)) {
									paramCi = i;
									break;
								}
							}
						}
						if (paramCi == null) {				
							paramCi = addChildConfigItem(parentConfigItem, name);
						}
						preconfiguredItem = paramCi;
						if (paramCi != null) {
							populateStructuredParameterConfigItem(paramCi,complexParam);
						}
				}
				else {
					log.validationException("c2a.failedToPopulateCI", 
						new java.lang.IllegalArgumentException());
				}
			}
		}else {
			log.validationException("c2a.failedToPopulateCI", 
					new java.lang.IllegalArgumentException());
			}
				//&& (name = complexParam.getName())!=null && !iterator..isEmpty()) {
			
	}
	
	@Override
	public void populateStructuredParameter(
		InventoryConfigurationItem parentConfigItem,
		StructuredType complexParam)
			throws ValidationException {	
		String name = "";
		
		if (parentConfigItem!=null && complexParam!=null 
			&& (name = complexParam.getName())!=null && !name.isEmpty()) {
				
			@SuppressWarnings("unchecked")
			List<InventoryConfigurationItem> children = 
				(List<InventoryConfigurationItem>) parentConfigItem.getChildConfigItems();
			InventoryConfigurationItem paramCi = null;
			
			for (InventoryConfigurationItem i : children) {
				if (i.getName().equalsIgnoreCase(name)) {
					paramCi = i;
					break;
				}
			}
			
			if (paramCi == null) {				
				paramCi = addChildConfigItem(parentConfigItem, name);
			}
			
			if (paramCi != null) {
				populateStructuredParameterConfigItem(paramCi,complexParam);
			}
		} else {
			log.validationException("c2a.failedToPopulateCI", 
				new java.lang.IllegalArgumentException());
		}
	 }
	
	@Override
	public InventoryConfigurationItem addChildConfigItem(
			InventoryConfigurationItem parentItem,
	        String childItemName) 
	        	throws ValidationException {
		InventoryConfigurationVersion scv = null;			 
		Finder finder = PersistenceHelper.makeFinder();
		
		try {
			 if (parentItem == null || Utils.isEmpty(childItemName) || (scv=parentItem.getConfiguration()) == null)
				 return null;

			 Specification childSpec = null;
			 if (parentItem.getConfigSpec() != null) {
				 Set<SpecificationRel> setOfRelatedSpecs = parentItem.getConfigSpec().getRelatedSpecs();
				 
				 if (!Utils.isEmpty(setOfRelatedSpecs)) {
					 for (SpecificationRel relatedSpec : setOfRelatedSpecs) {
						 Specification spec = relatedSpec.getChild();
					
						 if (childItemName.equals(spec.getName())) {
							 childSpec = spec;
							 break;
						 }
					 }
				 }
			 }

			 if (childSpec == null)
				 log.validationException("c2a.specRequiredForConfigItem", 
					new java.lang.IllegalArgumentException(), childItemName);

			 InventoryConfigurationItem item = null;
			 Collection<InventoryConfigurationItem> items = PersistenceHelper.makeConfigurationManager(scv.getClass())
					 .createConfigurationItems(parentItem,(InventoryConfigurationSpec) childSpec,1);
			 			 
			 if (!Utils.isEmpty(items))
				 item = (InventoryConfigurationItem) items.iterator().next();

			 return item;
		 } finally {
			 if (finder != null)
				 finder.close();
			 RequestPolicyHelper.checkPolicy();
		 }
	}
	
	@Override
	public void dereferenceConfigurationItem(
		InventoryConfigurationItem configItem) 
			throws ValidationException {
		BaseConfigurationManager configManager = PersistenceHelper
				.makeConfigurationManager(configItem.getConfiguration().getClass());						
		Collection<InventoryConfigurationItem> updateList = 
				new ArrayList<InventoryConfigurationItem>();
		updateList.add(configItem);
		configManager.dereferenceInventoryConfigurationItems(updateList);
	}
	

	@Override	
	public InventoryConfigurationVersion createConfiguration(
		Class entityClass, 
		Configurable entityInstance,
		final String specification) 
			throws ValidationException {
		ConfigurationManager cManager = PersistenceHelper.makeConfigurationManager();			
		Specification spec = CommonHelper.makeCommonManager().findAndValidateSpecification(specification);			
		List< InventoryConfigurationSpec > configSpecs = cManager.getConfigSpecTypeConfig(spec, true);
		InventoryConfigurationSpec invSpec = configSpecs.iterator().next();
		
		BaseConfigurationManager bcd =PersistenceHelper.makeConfigurationManager (entityClass);				
		InventoryConfigurationVersion scv =  bcd.makeConfigurationVersion(entityInstance);
		String name = entityInstance.getName();
		if (name!=null && !name.isEmpty()) {
			scv.setDescription(name +" Configuration");
			scv.setName(name +" Configuration");
		}
		scv.setEffDate(new Date());
		return bcd.createConfigurationVersion(entityInstance, scv, invSpec);
	}
	
	// Private & Entity Type Specific API
	//////////////////////////////////////
	public ServiceConfigurationItem acquireUnusedServiceConfigItem(
		ServiceConfigurationVersion config, 
		final String ciName, final int maxAllowed) 
			throws ValidationException {
		
		ServiceConfigurationItem ci = null;		
		ServiceManager serviceManager = CommonHelper.makeServiceManager();		
		List<ServiceConfigurationItem> cis = serviceManager
				.findServiceConfigItemByName(config, ciName);
				
		if (!Utils.isEmpty(cis)) {			
			for (ServiceConfigurationItem item : cis) {
				
				if (!serviceManager.checkItemAssignedReferenced(config, item)) {
					ci = item;
					break;
				}
			}
		}
		
		if (ci==null && (maxAllowed==0 || (!Utils.isEmpty(cis) && cis.size()<maxAllowed))) {
			ci = serviceManager.addChildConfigItem(config,
				(ServiceConfigurationItem) config.getConfigItemTypeConfig(), ciName);
		}
		return ci;
	}
	
	public InventoryConfigurationItem[] getServiceConfigItemsByName(
			ServiceConfigurationVersion config, final String ciName) 
				throws ValidationException {
			
			ServiceConfigurationItem[] sci = null;		
			ServiceManager serviceManager = CommonHelper.makeServiceManager();		
			List<ServiceConfigurationItem> cis = serviceManager
					.findServiceConfigItemByName(config, ciName);
					
			if (!Utils.isEmpty(cis)) {			
				sci = cis.toArray(new ServiceConfigurationItem[cis.size()]);				
			}			
			return sci;
		}
	
	private ServiceConfigurationItem getFirstAssignedComponentServiceConfigItem(			
		ServiceConfigurationVersion config,
		final String configItemName,
		final String specName) 
			throws ValidationException {
		ServiceConfigurationItem result = null;
		
		List<ServiceConfigurationItem> configItems = CommonHelper.makeServiceManager()
				.findServiceConfigItemByName(config, configItemName);
		
		if (!Utils.isEmpty(configItems)) {			
			for (ServiceConfigurationItem ci : configItems) {
				Persistent ps = ci.getToEntity();
				ConsumableResource p = null;
				
				if (ps != null && ps instanceof Consumer) {
					p = ((Consumer) ps).getResource();
					
					if (p != null) {
						Specification spec = p.getSpecification();
						
						if (spec != null) {
							if (spec.getName().equalsIgnoreCase(specName)) {
								result = ci;
								break;
							}
						}
					}
				}				
			}							
		}
		return result;
	}	
}
