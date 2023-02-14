package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/

import java.util.Iterator;

import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.Configurable;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.xmlbeans.StructuredType;
import oracle.communications.platform.persistence.Persistent;

public interface BuilderManager {
	
	public InventoryConfigurationItem[] getConfigItemsByName(
		InventoryConfigurationVersion config, final String ciName) 
			throws ValidationException;
	
	public InventoryConfigurationItem getPendingConfigItemsByName(
			InventoryConfigurationVersion config, final String ciName) 
				throws ValidationException;
	
	public InventoryConfigurationItem acquireUnusedConfigItem(
		InventoryConfigurationVersion config,
		final String ciName) 
			throws ValidationException;

	public InventoryConfigurationItem acquireUnusedConfigItem(
		InventoryConfigurationVersion config,
		final String ciName,
		final int maxAllowed) 
			throws ValidationException;
	
	public void setConfigItemProperty(
		InventoryConfigurationItem configItem,
		String propertyName, 
		String value) 
			throws ValidationException;
	
	public void populateStructuredParameters(
			InventoryConfigurationItem parentConfigItem,
			Iterator<StructuredType> iterator) 
				throws ValidationException;
	
	 public void populateStructuredParameter(
		InventoryConfigurationItem parentConfigItem,
		StructuredType complexParam)
			throws ValidationException;
	 
	public void populateStructuredParameterConfigItem(
		InventoryConfigurationItem configItem,
		StructuredType complexParam) 
			throws ValidationException;
	
	public void setEntityProperty(
		CharacteristicExtensible<?> entity,
	    String name, String value) 
	    	throws ValidationException; 
	
	public Persistent getConfigItemTarget(
		InventoryConfigurationItem configItem)  
			throws ValidationException;
	
	public ConsumableResource getAssignedEntity(
		InventoryConfigurationItem configItem)  
			throws ValidationException;
		
	public ConfigurationReferenceEnabled getReferencedEntity(
		InventoryConfigurationItem configItem)  
			throws ValidationException;	
				
	public ConsumableResource getFirstAssignedComponent(			
		InventoryConfigurationVersion config,
		final String configItemName,
		final String specName) 
			throws ValidationException;
	
	public InventoryConfigurationItem getFirstAssignedComponentConfigItem(			
		InventoryConfigurationVersion config,
		final String configItemName,
		final String specName) 
			throws ValidationException;
	
	public  Specification checkSpecificationExistance(
		Class<?> cls, 
		String specificationName)  
			throws ValidationException;
	
	public InventoryConfigurationItem addChildConfigItem(
		InventoryConfigurationItem parentItem,
	    String childItemName) 
	       	throws ValidationException; 
	
	public void dereferenceConfigurationItem(
		InventoryConfigurationItem configItem) 
			throws ValidationException;
	
	public InventoryConfigurationVersion createConfiguration(
		Class entityClass, 
		Configurable entityInstance,
		final String specification) 
			throws ValidationException;
}
