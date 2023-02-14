package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Collection;
import java.util.List;

import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.CustomNetworkAddress;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.Network;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.Pipe;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.GroupEnabled;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.c2a.impl.NameValueActionParam;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointContext;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.BusinessInteractionType;
import oracle.communications.inventory.xmlbeans.GeographicAddressType;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.inventory.xmlbeans.StructuredType;
import oracle.communications.platform.persistence.Persistent;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;

/**
 * This interface provides methods to enable Design & Assign within the context
 * of the Concept-to-Activate problem space.
 */
public interface DesignManager {
	public BusinessInteraction childServiceActionRequest(String action,
	        String mappingFile, String biSpecification, String configItemName,
	        ServiceConfigurationVersion svcConVers,
	        ExtensionPointContext context) throws ValidationException;

	public BusinessInteraction childServiceActionRequest(String action,
	        String mappingFile, String biSpecification,
	        ServiceConfigurationVersion svcConVers,
	        ExtensionPointContext context) throws ValidationException;

	public BusinessInteraction childResourceActionRequest(
	        ExtensionPointContext context,
	        ServiceConfigurationVersion svcConVers, String mappingFile,
	        String biSpec, List<NameValueActionParam> listnvap, String action,
	        String configItemName) throws ValidationException;

	public void mapServiceBusinessInteractionParameters(
	        ExtensionPointContext extensionPointContext)
	        throws ValidationException;

	public void mapServiceBusinessInteractionParametersExceptPrefix(
	        ExtensionPointContext extensionPointContext, String prefix)
	        throws ValidationException;

	public BusinessInteractionType makeChildServiceOrder(String action,
			ParameterType[] parameters, String serviceSpecification,
			Service theService, String biSpecification, String srvNameSuffix)
			throws ValidationException;
	
	public BusinessInteraction processInteraction(BusinessInteraction bi)
	        throws ValidationException;

	public String getServiceName(ServiceConfigurationVersion config) 
			throws ValidationException;
	
	public BusinessInteraction captureAndProcessChildResourceBusinessInteraction(
	        BusinessInteractionType bi, BusinessInteraction parentBi)
	        throws ValidationException;

	public BusinessInteraction captureAndProcessServiceBusinessInteraction(
	        BusinessInteractionType bitCapture, ServiceConfigurationVersion scv)
	        throws ValidationException;
	
	public boolean checkGeographicPlaceServiceRel(ServiceConfigurationVersion scv, 
			GeographicAddressType addressParam) 
			throws ValidationException;
	
	public boolean checkGeographicPlaceServiceRel(ServiceConfigurationVersion scv, 
			GeographicAddress address) 
			throws ValidationException;
	
	public GeographicAddress relateServiceToGeographicPlace(ServiceConfigurationVersion scv, 
			GeographicAddressType addressParam, String addressSpecName) 
			throws ValidationException;
	
	public void relateServiceToGeographicPlace(ServiceConfigurationVersion scv, 
			GeographicPlace geographicPlace) 
			throws ValidationException;

	public GeographicAddress getServiceAddress(
	        BusinessInteractionItemType itemType) throws ValidationException;
	
	public GeographicAddress getServiceAddress(
			GeographicAddressType srvAddrType, 
			String srvLocationSpecName) throws ValidationException;

	public InventoryGroup findInventoryGroupByChar(String charName,
	        String charValue) throws ValidationException;

	public Collection<CustomObject> findCustomObjectsByCharContainingString(
	        String charName1, String charValue1) throws ValidationException;

	public Collection<InventoryGroup> findServiceAreasByServiceAddressAndCfs(
	        GeographicAddress serviceAddress, String cfs,
	        String[] candidateSpecs) throws ValidationException;

	public InventoryGroup findServiceAreaByServiceAddressAndCfs(
	        GeographicAddress serviceAddress, String cfs,
	        String[] candidateSpecs) throws ValidationException;

	public InventoryGroup findServiceAreaByServiceAddress(
	        GeographicAddress serviceAddress, String[] candidateSpecs)
	        throws ValidationException;

	public InventoryGroup selectBestServiceArea(
	        ServiceConfigurationVersion svcConVers,
	        Collection<InventoryGroup> areas);
	
	public List<GroupEnabled> findEntityForServingArea(InventoryGroup servingArea,
			String entitySpecName, Class entityClass) throws ValidationException;

	public CustomObject findCoBySpecChar(List<NameValueActionParam> list,
	        String specName) throws ValidationException;

	public Pipe findPipeBySpecChar(String charName, String charValue,
	        String specName) throws ValidationException;

	public DeviceInterface findDeviceInterfaceByLDSpecChar(LogicalDevice ld,
	        String charName, String charValue, String specName)
	        throws ValidationException;

	public LogicalDeviceAccount findLdaBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException;
	
	public LogicalDeviceAccount findLdaBySpecCharAndLD(
			List<NameValueActionParam> list, String specName,
			LogicalDevice device) throws ValidationException;
	
	public LogicalDeviceAccount findFreeLdaBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException;
	
	public LogicalDeviceAccount findLdaBySpecChar(
	        List<NameValueActionParam> list, String specName, 
	        AssignmentState state)
	        throws ValidationException;
	
	public LogicalDevice findLdBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException;

	public LogicalDevice findUnassignedLdBySpecCharacteristics(
	        List<NameValueActionParam> characteristics, 
	        String specName)
	        throws ValidationException;
	
	public LogicalDevice findLdBySpecCharacteristics(
			List<NameValueActionParam> characteristics, 
			String specName,
			AssignmentState state) 
			throws ValidationException;
	
	public LogicalDevice findAndLockUnassignedLdBySpecCharacteristics(
	        List<NameValueActionParam> characteristics, 
	        String specName,
	        int lockPeriod)
	        throws ValidationException;
	
	public LogicalDeviceAccount findLdaBySpecChar(String charName,
	        String charValue, String specName) throws ValidationException;

	public LogicalDeviceAccount findLdaByNameAndSpec(String name,
	        String specName) throws ValidationException;

	public void assignSubjectToParent(ServiceConfigurationVersion parentScv,
	        String configItemName, ConsumableResource childService)
	        throws ValidationException;

	public void unassignResource(ServiceConfigurationVersion config,
	        String configItemName)
	        throws ValidationException;

	public Service getServiceForBusinessInteraction(BusinessInteraction bi)
	        throws ValidationException;

	public void associateServiceAddressToServiceArea(GeographicPlace geo,
	        InventoryGroup invGrp) throws ValidationException;

	public String getCharacteristicForEntity(CharacteristicExtensible E,
	        String name) throws ValidationException;

	public Service getAssignedService(ServiceConfigurationVersion scv,
	        String configItemName) throws ValidationException;

	public void referenceSubjectToParent(ServiceConfigurationVersion parentScv,
	        String configItemName, ConfigurationReferenceEnabled childService)
	        throws ValidationException;

	public void referenceSubjectToParentCi(
	        ServiceConfigurationVersion parentScv,
	        ServiceConfigurationItem configItem,
	        ConfigurationReferenceEnabled childResource)
	        throws ValidationException;

	public String getStringPropertyForBiItemType(
	        BusinessInteractionItemType itemType, String name)
	        throws ValidationException;

	public Collection<NameValueActionParam> getStringBiItemParams(
	        BusinessInteractionItemType itemType) throws ValidationException;

	public DeviceInterface findFreeDeviceInterface(LogicalDevice ld,
	        String specName) throws ValidationException;

	public void addUpdateConfigItemCharacteristic(
	        ServiceConfigurationVersion scv,
	        ServiceConfigurationItem configItem, String characteristicName,
	        String value) throws ValidationException;

	public String getConfigItemCharacteristic(
	        ServiceConfigurationItem configItem, String characteristicName)
	        throws ValidationException;

	public ConsumableResource createResource(BusinessInteraction bi)
	        throws ValidationException;

	public ConsumableResource updateResource(BusinessInteraction bi,
	        XmlObject resource, BusinessInteractionItemType itemType)
	        throws ValidationException;

	public void deleteResource(XmlObject resource) throws ValidationException;

	public BusinessInteractionItemType getBiItemType(BusinessInteraction bi)
	        throws ValidationException;

	public LogicalDevice findAndReferenceTarget(
	        ServiceConfigurationVersion svcConVers, InventoryGroup serviceArea,
	        String resourceConfigItem, String targetType)
	        throws ValidationException;

	public Service findServiceById(String serviceId) throws ValidationException;

	public Service findServiceByName(String serviceName)
	        throws ValidationException;

	public LogicalDevice findLogicalDeviceById(String logicalDeviceId)
	        throws ValidationException;

	public LogicalDevice findLogicalDeviceByName(String logicalDeviceName)
	        throws ValidationException;

	public Pipe findPipeById(String logicalDeviceId) throws ValidationException;

	public Pipe findPipeByName(String pipeName) throws ValidationException;

	public Network findNetworkByName(String ntName) throws ValidationException;

	public Network findNetworkById(String ntId) throws ValidationException;

	public CustomObject findCustomObjectByName(String coName)
	        throws ValidationException;

	public CustomObject findCustomObjectById(String logicalDeviceId)
	        throws ValidationException;

	public InventoryGroup findInventoryGroupByName(String ivgName)
	        throws ValidationException;

	public GeographicPlace findGeographicPlaceByName(String geoName)
	        throws ValidationException;

	public LogicalDeviceAccount findLogicalDeviceAccountById(
	        String logicalDeviceAccountId) throws ValidationException;

	public BusinessInteraction getParentBi(ServiceConfigurationVersion scv)
	        throws ValidationException;

	public Service cascadeCompleteServiceConfiguration(
	        ServiceConfigurationVersion scv) throws ValidationException;

	public Persistent getAssignedEntity(ServiceConfigurationVersion scv,
	        String configItemName) throws ValidationException;

	public Persistent getReferencedEntity(ServiceConfigurationVersion scv,
	        String configItemName) throws ValidationException;
	
	public boolean isConfigItemPropertyEquals(ServiceConfigurationItem configItem, 
			String propertyName, String propertyValue);
	
	public GeographicAddress getReferencedGeographicAddress(
	        ServiceConfigurationVersion scv, String configItemName)
	        throws ValidationException;

	public Service getServiceForServiceConfigurationVersion(
	        ServiceConfigurationVersion scv) throws ValidationException;

	public Specification getSpecificationForConfigurationItem(
	        ServiceConfigurationVersion scv, String item) throws ValidationException;

	public String getActionBiItemParam(BusinessInteraction bi)
	        throws ValidationException;

	public String getActionBiItemParam(BusinessInteractionItemType itemType)
	        throws ValidationException;		

	public String getBiItemParamByName(BusinessInteraction bi, String name)
	        throws ValidationException;

	public String getBiItemTypeParamByName(
	        BusinessInteractionItemType itemType, String name)
	        throws ValidationException;

	public BusinessInteractionItemType getActionBiItemType(
	        BusinessInteraction bi) throws ValidationException;

	public void addUpdateLogicalDeviceAccountCharacteristic(
	        LogicalDeviceAccount lda, String characteristicName, String value)
	        throws ValidationException;

	public void addUpdateServiceCharacteristic(
	        Service service, String characteristicName, String value)
	        throws ValidationException;
	
	public void createTnRangeWithIvg(String rangeFrom, String rangeTo,
	        String TnSpec, InventoryGroup ivg) throws ValidationException;

	public InventoryGroup getResourceIvgBySpec(ConsumableResource tn,
	        String ivgSpec) throws ValidationException;

	public InventoryGroup getIvgParentIvgBySpec(InventoryGroup ivg,
	        String parentIvgSpec) throws ValidationException;

	public void validExcpt(String message) throws ValidationException;

	public void applyProfileParameters(CharacteristicExtensible E,
	        ServiceConfigurationItem propertiesItem) throws ValidationException;

	public void applyEntityParametersToConfigItem(CharacteristicExtensible E,
	        List<String> charList, ServiceConfigurationItem propertiesItem)
	        throws ValidationException;

	public void applyEntityCharToServiceConfigItem(CharacteristicExtensible E,
	        String charName, String configItemName,
	        ServiceConfigurationVersion svcConVers) throws ValidationException;

	public void copyConfigItemCharValuesByPrefix(
	        ServiceConfigurationItem sourceItem,
	        ServiceConfigurationItem targetItem, String prefix)
	        throws ValidationException;

	public List<NameValueActionParam> updateNvapWithConfigItemProps(
	        ServiceConfigurationItem propertiesItem,
	        List<NameValueActionParam> listnvap, String prefix)
	        throws ValidationException;

	public void setConfigItemCharValuesByPrefix(String value,
	        ServiceConfigurationItem targetItem, String prefix)
	        throws ValidationException;

	public String getPrefixForSuffix(String str, String suffix)
	        throws ValidationException;

	public String getSuffixForPrefix(String str, String prefix)
	        throws ValidationException;

	public List<CustomObject> findCustomObjectsBySpecAndIvg(
	        InventoryGroup group, String specName) throws ValidationException;
	
	public List<LogicalDevice> findLogicalDevicesBySpecAndIvg(
	        InventoryGroup group, String specName) throws ValidationException;

	public List<Pipe> findPipesBySpecAndIvg(InventoryGroup group,
	        String specName) throws ValidationException;

	public List<Pipe> findPipesBySpecAndIvgAndChar(InventoryGroup group,
	        String specName, String charName, String charVal)
	        throws ValidationException;

	public List<Pipe> findPipesByParentAndAssignmentState(Pipe parent,
	        String assignment) throws ValidationException;

	public List<Service> getPipeServiceConsumerBySpec(ConsumableResource res,
	        String spec) throws ValidationException;

	public List<Service> getLogicalDeviceServiceConsumerBySpec(
	        ConsumableResource res, String spec) throws ValidationException;

	public BusinessInteraction captureAndCustomProcessChildBusinessInteraction(
	        BusinessInteractionType bitCapture, BusinessInteraction parentBi)
	        throws ValidationException;

	public List<LogicalDevice> findLdBySpecChar(String charName,
	        String charValue, String specName) throws ValidationException;

	public LogicalDevice findLdBySpecCharIvg(String charName, String charValue,
	        String specName, InventoryGroup ivg) throws ValidationException;

	public DeviceInterface findDeviceInterfaceById(String ifaceId)
	        throws ValidationException;

	public DeviceInterface findDeviceInterfaceByName(String ifaceName)
	        throws ValidationException;

	public List<DeviceInterface> findDeviceInterfaceBySpecAndLd(
	        LogicalDevice ld, String specName) throws ValidationException;
	
	public CustomNetworkAddress findAndLockUnassignedCNABySpecCharacteristics(
	        List<NameValueActionParam> characteristics, String specName,
	        int lockPeriod) throws ValidationException;
	
	public CustomNetworkAddress[] findAndLockUnassignedCNAsBySpecCharacteristics(
	        List<NameValueActionParam> characteristics, String specName,
	        int lockPeriod, int count) throws ValidationException;

	public void updateCharacteristicForEntity(CharacteristicExtensible E,
	        String name, String value) throws ValidationException;

	public void updateCharacteristicForPipe(Pipe pipe, String name, String value)
	        throws ValidationException;
	
	public void setServiceConfigItemProperty(ServiceConfigurationItem configItem,
			String propertyName, String value) throws ValidationException;
	
	public ServiceConfigurationItem getServiceConfigItem(
			ServiceConfigurationVersion config,String sciName) 
			throws ValidationException;
	
	public ServiceConfigurationItem getChildServiceConfigItem(
			ServiceConfigurationItem parentConfigItem, String sciName)
			throws ValidationException;
	
	public ServiceConfigurationItem findChildServiceConfigItem(
			ServiceConfigurationItem parentConfigItem, String sciName)
			throws ValidationException;
	
	public void dereferenceServiceConfigurationItem(ServiceConfigurationItem configItem) 
			throws ValidationException;
	
	public String getServiceAction(ServiceConfigurationVersion config)
			throws ValidationException;
	
	public String getCharacteristicValue(Party party, String charName); 
	
	public XmlBoolean makeXmlBoolean(boolean b);
	
	public XmlString makeXmlString(String b);	
	
	public XmlInt makeXmlInt(int b);
	
	public void referenceServiceLocation(ServiceConfigurationItem configItem,
			ServiceConfigurationVersion config,
			GeographicAddressType serviceAddressType)
			throws ValidationException;
	
	public ServiceConfigurationItem aquireConfigItem(
			ServiceConfigurationVersion config,
			final String ciName) 
			throws ValidationException;
	
	public ServiceConfigurationItem aquireUnusedConfigItem(
			ServiceConfigurationVersion config,
			final String ciName) 
			throws ValidationException;
	
	public ConsumableResource getFirstAssignedComponent(			
			ServiceConfigurationVersion config,
			final String configItemName,
			final String specName) 
			throws ValidationException;
	
	public ServiceConfigurationItem getFirstAssignedComponentConfigItem(			
			ServiceConfigurationVersion config,
			final String configItemName,
			final String specName) 
			throws ValidationException;
	
	public void setResourceState(LogicalDevice resource, InventoryState state) 
			throws ValidationException;
	
	public void relateCustomerToService(
		final String customerId, 
		ServiceConfigurationVersion config, 
		final String spec, final String role)
			throws ValidationException;
	
	public void relateServiceAddressToService(
		final StructuredType address, 
		ServiceConfigurationVersion config, 
		final String spec) 
			throws ValidationException; 
					
	public ServiceConfigurationVersion disconnectService(
		Service service) 
			throws ValidationException;
	
	public boolean isObjectReferencedByOtherService(
        Service currentService, 
        ConfigurationReferenceEnabled refObject) 
        	throws ValidationException;
        		
	public GeographicAddressType convertServiceAddress(final StructuredType address);
        
    public String getJMSCorrelationId();
            
    public void sendMessageToOSM(BusinessInteraction bi, String jmsCorrelationId) throws ValidationException;
        
    public BusinessInteraction getTopLevelParentBusinessInteraction(BusinessInteraction bi);

}
