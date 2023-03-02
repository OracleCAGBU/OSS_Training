package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.characteristic.CharacteristicManager;
import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.common.BaseInvManager;
import oracle.communications.inventory.api.common.EntitySerializationUtils;
import oracle.communications.inventory.api.common.impl.ExtendedRangeLockingStrategy;
import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.configuration.ConfigurationReferenceSearchCriteria;
import oracle.communications.inventory.api.connectivity.PipeManager;
import oracle.communications.inventory.api.connectivity.PipeSearchCriteria;
import oracle.communications.inventory.api.consumer.ConsumerManager;
import oracle.communications.inventory.api.custom.CustomNetworkAddressManager;
import oracle.communications.inventory.api.custom.CustomNetworkAddressSearchCriteria;
import oracle.communications.inventory.api.custom.CustomObjectManager;
import oracle.communications.inventory.api.custom.CustomObjectSearchCriteria;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionAttachment;
import oracle.communications.inventory.api.entity.BusinessInteractionItem;
import oracle.communications.inventory.api.entity.BusinessInteractionSpec;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ConfigurationReferenceState;
import oracle.communications.inventory.api.entity.CustNetAddrSpecification;
import oracle.communications.inventory.api.entity.CustomNetworkAddress;
import oracle.communications.inventory.api.entity.CustomNetworkAddressCharacteristic;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectCharacteristic;
import oracle.communications.inventory.api.entity.CustomObjectSpecification;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceCharacteristic;
import oracle.communications.inventory.api.entity.DeviceInterfaceSpecification;
import oracle.communications.inventory.api.entity.EntityAttachmentCategory;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicAddressConfigurationReference;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.InvGroupRel;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupCharacteristic;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LDAccountCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.LogicalDeviceAccountSpecification;
import oracle.communications.inventory.api.entity.LogicalDeviceAssignmentToService;
import oracle.communications.inventory.api.entity.LogicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.Network;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyCharacteristic;
import oracle.communications.inventory.api.entity.Pipe;
import oracle.communications.inventory.api.entity.PipeAssignmentToService;
import oracle.communications.inventory.api.entity.PipeCharacteristic;
import oracle.communications.inventory.api.entity.PipeSpecification;
import oracle.communications.inventory.api.entity.PlaceServiceRel;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceAssignment;
import oracle.communications.inventory.api.entity.ServiceCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationItemCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceSpecification;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.Status;
import oracle.communications.inventory.api.entity.TelephoneNumber;
import oracle.communications.inventory.api.entity.TelephoneNumberSpecification;
import oracle.communications.inventory.api.entity.common.Assignment;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.ConfigurationReference;
import oracle.communications.inventory.api.entity.common.ConfigurationReferenceEnabled;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.Consumer;
import oracle.communications.inventory.api.entity.common.GroupEnabled;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.inventory.api.framework.persistence.InventorySearchCriteriaHints;
import oracle.communications.inventory.api.framework.policy.LockPolicy;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.framework.resource.MessageResource;
import oracle.communications.inventory.api.framework.security.UserEnvironmentFactory;
import oracle.communications.inventory.api.group.InventoryGroupEntitySearchCriteria;
import oracle.communications.inventory.api.group.InventoryGroupManager;
import oracle.communications.inventory.api.group.InventoryGroupSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.DeviceInterfaceSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.account.LogicalDeviceAccountManager;
import oracle.communications.inventory.api.logicaldevice.account.LogicalDeviceAccountSearchCriteria;
import oracle.communications.inventory.api.number.TelephoneNumberManager;
import oracle.communications.inventory.api.service.impl.ServiceUtils;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.ServiceAreaBestFitSelector;
import oracle.communications.inventory.c2a.ServiceAreaResolver;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointContext;
import oracle.communications.inventory.sfws.adapter.businessinteraction.ActionSerializationUtils;
import oracle.communications.inventory.sfws.businessinteraction.InteractionResponseLevelEnum;
import oracle.communications.inventory.sfws.mapper.InteractionMapper;
import oracle.communications.inventory.techpack.common.CommonManager;
import oracle.communications.inventory.techpack.common.ResourceManager;
import oracle.communications.inventory.techpack.common.ServiceManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionActionEnum;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemActionEnum;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.BusinessInteractionType;
import oracle.communications.inventory.xmlbeans.DeviceInterfaceType;
import oracle.communications.inventory.xmlbeans.GeographicAddressType;
import oracle.communications.inventory.xmlbeans.InteractionDocument;
import oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType;
import oracle.communications.inventory.xmlbeans.LogicalDeviceType;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.inventory.xmlbeans.PartyType;
import oracle.communications.inventory.xmlbeans.PropertyType;
import oracle.communications.inventory.xmlbeans.ServiceType;
import oracle.communications.inventory.xmlbeans.SpecificationType;
import oracle.communications.inventory.xmlbeans.StructuredType;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;

public class DesignManagerImpl extends BaseInvManager implements DesignManager {
	private static Log log = LogFactory.getLog(DesignManager.class);
	private static final String SERVICE_AREA_RESOLVER_REGISTRY = "ServiceAreaResolverRegistry";
	private static final String SERVICE_AREA_BEST_FIT_SELECTOR_REGISTRY = "ServiceAreaBestFitSelectorRegistry";
	
	private static final String BI_ACTION_CREATE = "CREATE";
	private static final String BI_ACTION_CHANGE = "CHANGE";

	public BusinessInteraction childServiceActionRequest(String action,
	        String mappingFile, String biSpecification,
	        ServiceConfigurationVersion svcConVers,
	        ExtensionPointContext context) throws ValidationException {
		BusinessInteraction childBi = null;
		if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("create")) {
			BusinessInteractionType byt = this
			        .buildCreateCaptureChildServiceInteractionBiInput(context,
			                mappingFile, biSpecification);
			byt.getHeader().setName(
			        buildBusinessInteractionName(action, svcConVers));
			childBi = this.captureAndProcessServiceBusinessInteraction(byt,
			        svcConVers);
		} else {
			this.validExcpt("Invalid childServiceActionRequest parameter combination");
		}
		return childBi;
	}

	public BusinessInteraction childServiceActionRequest(String action,
	        String mappingFile, String biSpecification, String configItemName,
	        ServiceConfigurationVersion svcConVers,
	        ExtensionPointContext context) throws ValidationException {
		BusinessInteraction childBi = null;
		if (!action.equalsIgnoreCase("add")
		        && !action.equalsIgnoreCase("create")
		        && !configItemName.equals(null) && !configItemName.equals("")) {
			BusinessInteractionType byt = this
			        .buildModifyCaptureChildServiceInteractionBiInput(context,
			                mappingFile, biSpecification, action,
			                configItemName);
			byt.getHeader().setName(
			        buildBusinessInteractionName(action, svcConVers));
			childBi = this.captureAndProcessServiceBusinessInteraction(byt,
			        svcConVers);
		} else if (action.equalsIgnoreCase("add")
		        || action.equalsIgnoreCase("create")) {
			BusinessInteractionType byt = this
			        .buildCreateCaptureChildServiceInteractionBiInput(context,
			                mappingFile, biSpecification);
			byt.getHeader().setName(
			        buildBusinessInteractionName(action, svcConVers));
			childBi = this.captureAndProcessServiceBusinessInteraction(byt,
			        svcConVers);
		} else {
			this.validExcpt("Invalid childServiceActionRequest parameter combination");
		}
		return childBi;
	}

	private String buildBusinessInteractionName(String action,
	        ServiceConfigurationVersion config) {
		Service service = config.getService();
		return action + " " + service.getSpecification().getName() + " "
		        + service.getId();
	}

	public BusinessInteraction processInteraction(BusinessInteraction bi)
	        throws ValidationException {
		return bi;
	}

	public String getPrefixForSuffix(String str, String suffix)
	        throws ValidationException {
		String ret = null;

		int i = str.indexOf(suffix);
		ret = str.substring(0, i);

		return ret;
	}

	public String getSuffixForPrefix(String str, String prefix)
	        throws ValidationException {
		String ret = null;

		int i = prefix.length();
		ret = str.substring(i, str.length());

		return ret;
	}

	protected BusinessInteractionType buildCreateCaptureChildServiceInteractionBiInput(
	        ExtensionPointContext extensionPointContext, String mappingFile,
	        String biSpecification) throws ValidationException {
		try {

			ServiceConfigurationVersion scv = (ServiceConfigurationVersion) extensionPointContext
			        .getArguments()[0];
			ServiceManager serviceManager = CommonHelper.makeServiceManager();

			BusinessInteractionItemType parentBiItemType = (BusinessInteractionItemType) extensionPointContext
			        .getArguments()[1];
			List<ParameterType> parentBiItemTypeParamsList = parentBiItemType
			        .getParameterList();

			CommonManager commonManager = CommonHelper.makeCommonManager();
			BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
			        .findAndValidateSpecification(biSpecification);

			if (biSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(),
				        biSpecification);

			SpecificationType biSpecType = EntitySerializationUtils
			        .toSpecification(biSpec);

			// Add business interaction parameters.
			BusinessInteractionType biType = BusinessInteractionType.Factory
			        .newInstance();
			biType.addNewHeader();

			biType.getHeader().setAction(BusinessInteractionActionEnum.CREATE);
			biType.getHeader().setSpecification(biSpecType);
			biType.getHeader().setId("");
			biType.getHeader().addNewExternalIdentity();
			biType.getHeader().getExternalIdentity().setExternalObjectId("");
			// Name is required.
			biType.getHeader().setName(biSpecification);
			biType.getHeader()
			        .setDescription(biSpecification + " description ");
			Calendar cal = new GregorianCalendar();
			Date effDate = new Date();
			cal.setTime(effDate);
			biType.getHeader().setEffectiveDate(cal);

			// Add the Service Item for the BBIA Service.
			BusinessInteractionItemType bbiaServiceItem = BusinessInteractionItemType.Factory
			        .newInstance();
			bbiaServiceItem.setAction(BusinessInteractionItemActionEnum.ADD);
			ServiceType svcParam = ServiceType.Factory.newInstance();
			String childServiceSpecification = DesignHelper
			        .childSpecificationMapParser(mappingFile);
			ServiceSpecification bbiasvSpec = (ServiceSpecification) commonManager
			        .findAndValidateSpecification(childServiceSpecification);
			if (bbiasvSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(), bbiasvSpec);
			SpecificationType bbiaServiceSpec = EntitySerializationUtils
			        .toSpecification(bbiasvSpec);
			svcParam.setSpecification(bbiaServiceSpec);
			svcParam.setAction("create");
			svcParam.setId("");
			svcParam.addNewExternalIdentity();
			svcParam.getExternalIdentity().setExternalObjectId("");
			svcParam.setName(childServiceSpecification
			        + " service created by autodesign");
			svcParam.setDescription(childServiceSpecification
			        + " service created by autodesign");

			bbiaServiceItem.setService(svcParam);

			ArrayList<Parameter> myBiParamsList = DesignHelper
			        .BiParamMapParser(mappingFile);

			Iterator<Parameter> it = myBiParamsList.iterator();
			while (it.hasNext()) {
				Parameter mvParam = (Parameter) it.next();
				ParameterType paramParamType = bbiaServiceItem
				        .addNewParameter();
				paramParamType.setName(mvParam.getName());
				org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
				        .newInstance();
				if (mvParam.getParentBiParameterName() != null) {
					Iterator<ParameterType> itt = parentBiItemTypeParamsList
					        .iterator();
					while (itt.hasNext()) {
						ParameterType p = (ParameterType) itt.next();
						org.apache.xmlbeans.XmlObject xmlObject = p.getValue();

						if (p.getName().equals(
						        mvParam.getParentBiParameterName())) {
							paramParamType.setValue(xmlObject);
						}
					}
				}
				if (mvParam.getDefaultValue() != null) {
					xmlString.setStringValue(mvParam.getDefaultValue());
					paramParamType.setValue(xmlString);
				}
				// New
				if (mvParam.getParentParameterConfigItemName() != null
				        && mvParam.getParentParameterName() != null) {
					List<ServiceConfigurationItem> configItems3 = serviceManager
					        .findServiceConfigItemByName(scv,
					                mvParam.getParentParameterConfigItemName());
					if (configItems3.isEmpty()) {
						this.validExcpt("Config item not found "
						        + mvParam.getParentParameterConfigItemName());
					}
					ServiceConfigurationItem cfgItem = configItems3.get(0);
					xmlString.setStringValue(this.getConfigItemCharacteristic(
					        cfgItem, mvParam.getParentParameterName()));
					paramParamType.setValue(xmlString);
				}
				// end New

			}

			BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
			items[0] = bbiaServiceItem;
			biType.addNewBody();
			biType.getBody().setItemArray(items);

			return biType;

		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	protected BusinessInteractionType buildModifyCaptureChildServiceInteractionBiInput(
	        ExtensionPointContext extensionPointContext, String mappingFile,
	        String biSpecification, String serviceAction, String configItemName)
	        throws ValidationException {
		try {

			ServiceConfigurationVersion scv = (ServiceConfigurationVersion) extensionPointContext
			        .getArguments()[0];
			BusinessInteractionItemType parentBiItemType = (BusinessInteractionItemType) extensionPointContext
			        .getArguments()[1];
			List<ParameterType> parentBiItemTypeParamsList = parentBiItemType
			        .getParameterList();
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			List<ServiceConfigurationItem> configItems = serviceManager
			        .findServiceConfigItemByName(scv, configItemName);
			if (configItems.isEmpty()) {
				this.validExcpt("Config item not found " + configItemName);
			}
			ServiceConfigurationItem configItem = configItems.get(0);

			Service svc = null;
			Persistent ps = configItem.getToEntity();
			if (ps != null) {
				if (ps instanceof ServiceAssignment) {
					svc = ((ServiceAssignment) ps).getService();
				}
			}
			String serviceId = null;
			if (svc != null) {
				serviceId = svc.getId();
			}
			CommonManager commonManager = CommonHelper.makeCommonManager();
			BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
			        .findAndValidateSpecification(biSpecification);

			if (biSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(),
				        biSpecification);

			SpecificationType biSpecType = EntitySerializationUtils
			        .toSpecification(biSpec);

			// Add business interaction parameters.
			BusinessInteractionType biType = BusinessInteractionType.Factory
			        .newInstance();
			biType.addNewHeader();

			biType.getHeader().setAction(BusinessInteractionActionEnum.CREATE);
			biType.getHeader().setSpecification(biSpecType);
			biType.getHeader().setId("");
			biType.getHeader().addNewExternalIdentity();
			biType.getHeader().getExternalIdentity().setExternalObjectId("");
			// Name is required.
			biType.getHeader().setName(biSpecification);
			biType.getHeader()
			        .setDescription(biSpecification + " description ");
			Calendar cal = new GregorianCalendar();
			Date effDate = new Date();
			cal.setTime(effDate);
			biType.getHeader().setEffectiveDate(cal);

			// Add the Service Item for the BBIA Service.
			BusinessInteractionItemType bbiaServiceItem = BusinessInteractionItemType.Factory
			        .newInstance();
			bbiaServiceItem.setAction(BusinessInteractionItemActionEnum.ADD);
			ServiceType svcParam = ServiceType.Factory.newInstance();
			String childServiceSpecification = DesignHelper
			        .childSpecificationMapParser(mappingFile);
			ServiceSpecification bbiasvSpec = (ServiceSpecification) commonManager
			        .findAndValidateSpecification(childServiceSpecification);
			if (bbiasvSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(), bbiasvSpec);
			SpecificationType bbiaServiceSpec = EntitySerializationUtils
			        .toSpecification(bbiasvSpec);
			svcParam.setSpecification(bbiaServiceSpec);
			svcParam.setAction(serviceAction);
			svcParam.setId(serviceId);
			svcParam.addNewExternalIdentity();
			svcParam.getExternalIdentity().setExternalObjectId("");
			svcParam.setName(childServiceSpecification
			        + " service created by autodesign");
			svcParam.setDescription(childServiceSpecification
			        + " service created by autodesign");

			bbiaServiceItem.setService(svcParam);

			ArrayList<Parameter> myBiParamsList = DesignHelper
			        .BiParamMapParser(mappingFile);

			Iterator<Parameter> it = myBiParamsList.iterator();
			while (it.hasNext()) {
				Parameter mvParam = (Parameter) it.next();
				ParameterType paramParamType = bbiaServiceItem
				        .addNewParameter();
				paramParamType.setName(mvParam.getName());
				org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
				        .newInstance();
				if (mvParam.getParentBiParameterName() != null) {
					Iterator<ParameterType> itt = parentBiItemTypeParamsList
					        .iterator();
					while (itt.hasNext()) {
						ParameterType p = (ParameterType) itt.next();
						org.apache.xmlbeans.XmlObject xmlObject = p.getValue();

						if (p.getName().equals(
						        mvParam.getParentBiParameterName())) {
							paramParamType.setValue(xmlObject);
						}
					}
				}
				if (mvParam.getDefaultValue() != null) {
					xmlString.setStringValue(mvParam.getDefaultValue());
					paramParamType.setValue(xmlString);
				}

				// New
				if (mvParam.getParentParameterConfigItemName() != null
				        && mvParam.getParentParameterName() != null) {
					List<ServiceConfigurationItem> configItems3 = serviceManager
					        .findServiceConfigItemByName(scv,
					                mvParam.getParentParameterConfigItemName());
					if (configItems.isEmpty()) {
						this.validExcpt("Config item not found "
						        + configItemName);
					}
					ServiceConfigurationItem cfgItem = configItems3.get(0);
					xmlString.setStringValue(this.getConfigItemCharacteristic(
					        cfgItem, mvParam.getParentParameterName()));
					paramParamType.setValue(xmlString);
				}
				// end New

			}

			BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
			items[0] = bbiaServiceItem;
			biType.addNewBody();
			biType.getBody().setItemArray(items);

			return biType;

		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	protected BusinessInteractionType buildCreateCaptureChildResourceInteractionBiInput(
	        ExtensionPointContext extensionPointContext, String mappingFile,
	        String biSpecification) throws ValidationException {
		try {

			BusinessInteractionItemType parentBiItemType = (BusinessInteractionItemType) extensionPointContext
			        .getArguments()[1];
			List<ParameterType> parentBiItemTypeParamsList = parentBiItemType
			        .getParameterList();

			CommonManager commonManager = CommonHelper.makeCommonManager();
			BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
			        .findAndValidateSpecification(biSpecification);

			if (biSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(),
				        biSpecification);

			SpecificationType biSpecType = EntitySerializationUtils
			        .toSpecification(biSpec);

			// Add business interaction parameters.
			BusinessInteractionType biType = BusinessInteractionType.Factory
			        .newInstance();
			biType.addNewHeader();

			biType.getHeader().setAction(BusinessInteractionActionEnum.CREATE);
			biType.getHeader().setSpecification(biSpecType);
			biType.getHeader().setId("");
			biType.getHeader().addNewExternalIdentity();
			biType.getHeader().getExternalIdentity().setExternalObjectId("");
			// Name is required.
			biType.getHeader().setName("AutoDesignResourceBusinessInteraction");
			biType.getHeader().setDescription(
			        "AutoDesignResourceBusinessInteraction description ");
			Calendar cal = new GregorianCalendar();
			Date effDate = new Date();
			cal.setTime(effDate);
			biType.getHeader().setEffectiveDate(cal);

			// Add the Bi Item for the resource entity.
			BusinessInteractionItemType childResourceItem = BusinessInteractionItemType.Factory
			        .newInstance();
			childResourceItem.setAction(BusinessInteractionItemActionEnum.ADD);
			LogicalDeviceType childResourceParam = LogicalDeviceType.Factory
			        .newInstance();
			String childSpecification = DesignHelper
			        .childSpecificationMapParser(mappingFile);
			LogicalDeviceSpecification childSpec = (LogicalDeviceSpecification) commonManager
			        .findAndValidateSpecification(childSpecification);
			if (childSpec == null)
				log.validationException("c2a.couldNotFind",
				        new java.lang.IllegalArgumentException(), childSpec);
			SpecificationType childResourceSpec = EntitySerializationUtils
			        .toSpecification(childSpec);
			childResourceParam.setSpecification(childResourceSpec);
			// childResourceParam.setAction("create");
			childResourceParam.setId("");
			// childResourceParam.addNewExternalIdentity();
			// childResourceParam.getExternalIdentity().setExternalObjectId("");
			childResourceParam.setName(childSpecification
			        + " created by autodesign");
			childResourceParam.setDescription(childSpecification
			        + " created by autodesign");

			childResourceItem.setEntity(childResourceParam);

			ArrayList<Parameter> myBiParamsList = DesignHelper
			        .BiParamMapParser(mappingFile);

			Iterator<Parameter> it = myBiParamsList.iterator();
			while (it.hasNext()) {
				Parameter mvParam = (Parameter) it.next();
				ParameterType paramParamType = childResourceItem
				        .addNewParameter();
				paramParamType.setName(mvParam.getName());
				org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
				        .newInstance();
				if (mvParam.getParentBiParameterName() != null) {
					// to add mapping from parent BIparam
					Iterator<ParameterType> itt = parentBiItemTypeParamsList
					        .iterator();
					while (itt.hasNext()) {
						ParameterType p = (ParameterType) itt.next();
						if (p.getName().equals(
						        mvParam.getParentBiParameterName())) {
							EntityUtils.validateStringType(p,
							        mvParam.getParentBiParameterName());
							xmlString.setStringValue(EntityUtils.getStringType(
							        p, mvParam.getParentBiParameterName()));
						}
					}
				}
				if (mvParam.getDefaultValue() != null) {
					xmlString.setStringValue(mvParam.getDefaultValue());
				}
				paramParamType.setValue(xmlString);
			}

			BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
			items[0] = childResourceItem;
			biType.addNewBody();
			biType.getBody().setItemArray(items);

			return biType;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public BusinessInteraction captureAndProcessServiceBusinessInteraction(
	        BusinessInteractionType bitCapture, ServiceConfigurationVersion scv)
	        throws ValidationException {

		if (bitCapture == null) {
			log.validationException("c2a.captureInteractionError",
			        new java.lang.IllegalArgumentException());
		}

		// Capture Interaction - creates the child BI and associates it to the
		// parent BI.

		// Flushing to see if this fixes the disconnect usecase.
		this.flushTransaction();

		BusinessInteractionManager biMgr = PersistenceHelper
		        .makeBusinessInteractionManager();
		List<BusinessInteractionItem> parentItems = biMgr
		        .findBusinessInteractionItemsForItem((Persistent) scv);
		log.debug("", new Object[] { "size of parentItems for scv is "
		        + parentItems.size() });

		BusinessInteraction parentBi = null;
		if (!Utils.isEmpty(parentItems))
			parentBi = parentItems.get(0).getBusinessInteraction();

		if (parentBi == null) {
			log.warn("c2a.parentBiNull");
		}
		
		// Check if it is resume of D&A - (service action is "no_action")
		boolean resumeDesign = false;
		BusinessInteraction currentChildBi = null;
		if(!bitCapture.getBody().getItemList().isEmpty()){
			if(bitCapture.getBody().getItemList().get(0).getService().getAction().equals("no_action")){
				Service service = findServiceById(bitCapture.getBody().getItemList().get(0).getService().getId());
				ServiceConfigurationVersion config = getLatestScvForService(service);
				currentChildBi = oracle.communications.inventory.api.entity.utils.ConfigurationUtils.getAssociatedBusinessInteraction(config);
				resumeDesign = true;
			}
		}
		
		// Don't call captureInteraciton, if it is just a resume of D&A
		BusinessInteraction childBi = null;
		if(resumeDesign && currentChildBi != null){
			//Just attach the service order xml to the current child BI.
			childBi = this.createBIAttachment(currentChildBi, bitCapture);
		} else
			childBi = biMgr.captureInteraction(parentBi,
		        bitCapture);
		
		if (FeedbackProviderImpl.hasErrors() || childBi == null) {
			log.validationException("c2a.captureInteractionError",
			        new java.lang.IllegalArgumentException());
		}

		// Need to flush for process interaction to find the bi created in
		// capture interaction.
		this.flushTransaction();

		// Build Process Interaction BI Input
		BusinessInteractionType bitProcess = buildProcessInteractionBiInputType(childBi);

		// Process Interaction - Creates the Service, Service Configuration, and
		// auto-configures it
		// through the service configuration auto-configuration ruleset.
		BusinessInteraction biReturn = biMgr.processInteraction(bitProcess);
		if (FeedbackProviderImpl.hasErrors() || biReturn == null) {
			log.validationException("c2a.processInteractionError",
			        new java.lang.IllegalArgumentException());
		}
		this.flushTransaction();
		return biReturn;
	}
	
	private BusinessInteraction createBIAttachment(BusinessInteraction bi, BusinessInteractionType bit) throws ValidationException{
		BusinessInteractionManager biMgr = PersistenceHelper.makeBusinessInteractionManager();
		BusinessInteractionAttachment biAttachment = biMgr.makeBusinessInteractionAttachment();
		try {
			if (bit != null && bit.getHeader() != null) {
				InteractionDocument doc = InteractionDocument.Factory.newInstance();
				doc.setInteraction(bit);

				String xml = doc.xmlText(new XmlOptions().setSaveAggressiveNamespaces().setSavePrettyPrint().setSavePrettyPrintIndent(1));
				byte [] bytes = biAttachment.convertStringToContent(xml);
				biAttachment.setCategory(EntityAttachmentCategory.REQUEST);
				biAttachment.setContent(bytes);

				//Bug 10382491 - Fix -Need to display description of BI Input
//				if(!Utils.checkBlank(input.getDescription())){
//					biAttachment.setDescription(input.getDescription());
//				}
				
				// Generate a BI Attachment Name
				StringBuilder name = new StringBuilder();
				String id = "";
				if (!Utils.isEmpty(bi.getId()))
					id = bi.getId().trim();
				
				if (bit.getHeader().getAction().toString().equals(BI_ACTION_CREATE))
					name.append(MessageResource.getMessage("businessInteraction.createRequest", id));
				else if (bit.getHeader().getAction().toString().equals(BI_ACTION_CHANGE))
					name.append(MessageResource.getMessage("businessInteraction.changeRequest", id));
				else
					name.append(MessageResource.getMessage("businessInteraction.createRequest", ""));
				
				name.append(" ").append("[").append(bi.getAttachments().size() + 1).append("]");
				biAttachment.setName(name.toString().trim());
			}
		} catch (Exception e) {
			log.error("businessInteraction.entityAttachmentError", e, bit.getHeader().getId());
			throw new ValidationException(e);
		}
		biAttachment = biMgr.createBusinessInteractionAttachment(bi, biAttachment);
		return biAttachment.getBusinessInteraction();
	}

	public BusinessInteractionType buildProcessInteractionBiInputType(
	        BusinessInteraction bi) throws ValidationException {
		try {
			// Add business interaction parameters.
			BusinessInteractionType biType = BusinessInteractionType.Factory
			        .newInstance();
			biType.addNewHeader();
			biType.getHeader().setId(bi.getId());
			return biType;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public void mapServiceBusinessInteractionParameters(
	        ExtensionPointContext extensionPointContext)
	        throws ValidationException {
		log.debug("Entered mapServiceBusinessInteractionParameters");
		String propertiesItemName = "Properties";
		ServiceConfigurationItem propertiesItem = null;
		BusinessInteractionItemType item = (BusinessInteractionItemType) extensionPointContext
		        .getArguments()[1];
		ServiceConfigurationVersion scv = (ServiceConfigurationVersion) extensionPointContext
		        .getArguments()[0];

		if (scv == null)
			log.validationException("c2a.configurationError",
			        new java.lang.IllegalArgumentException(
			                "ServiceConfigurationVersion is null"));
		if (item == null)
			log.validationException("c2a.configurationError",
			        new java.lang.IllegalArgumentException(
			                "BusinessInteractionItemType is null"));

		CommonManager commonManager = CommonHelper.makeCommonManager();
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(scv.getClass());
		scv = connect(scv);
		ServiceManager serviceManager = CommonHelper.makeServiceManager();

		// find service configitem "Properties" and reference it. Assume that it
		// always exists, predefined in config spec
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(scv, propertiesItemName);
		if (!Utils.isEmpty(configItems))
			propertiesItem = configItems.get(0);
		if (propertiesItem == null) {
			log.debug("c2aTechPack.noPropertiesConfigItem");
			return;
		}

		List<ParameterType> paramList = item.getParameterList();
		if (Utils.isEmpty(paramList)) {
			log.debug("c2aTechPack.emptyBiParameterData");
			return;
		}
		for (ParameterType param : paramList) {
			String paramName = param.getName();
			String paramValue = EntityUtils.getStringType(param, paramName);
			if (paramValue == null)
				continue; // skip params with no value

			if (!ConfigurationUtils.isValidConfigItemCharacteristic(
			        propertiesItem, paramName)) {
				log.debug("c2a.configItemCharacteristicNotFound",
				        propertiesItem.getName(), paramName);
				continue; // skip unrecognized params
			}

			// TODO: mapServiceBusinessInteractionParameters - only
			// process String type params
			// TODO: mapServiceBusinessInteractionParameters - do we
			// need an
			// "if paramValue="REMOVE" then remove char, else process as add/update"
			// ??

			Set<ServiceConfigurationItemCharacteristic> chars = propertiesItem
			        .getCharacteristics();
			boolean found = false;
			if (!Utils.isEmpty(chars)) {
				for (CharValue characteristic : chars) {
					if (characteristic.getName().equals(paramName)) {
						characteristic.setValue(paramValue);
						found = true;
					}
				}
			}
			if (!found) {
				CharValue charvalue = commonManager.makeCharValue(
				        (CharacteristicExtensible) propertiesItem, paramName,
				        paramValue);
				chars.add((ServiceConfigurationItemCharacteristic) charvalue);
			}
			propertiesItem.setCharacteristics(chars);
			Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
			updateList.add(propertiesItem);
			configManager.updateConfigurationItem(updateList);
		}
	}

	public void mapServiceBusinessInteractionParametersExceptPrefix(
	        ExtensionPointContext extensionPointContext, String prefix)
	        throws ValidationException {
		log.debug("Entered mapServiceBusinessInteractionParameters");
		String propertiesItemName = "Properties";
		ServiceConfigurationItem propertiesItem = null;
		BusinessInteractionItemType item = (BusinessInteractionItemType) extensionPointContext
		        .getArguments()[1];
		ServiceConfigurationVersion scv = (ServiceConfigurationVersion) extensionPointContext
		        .getArguments()[0];

		if (scv == null)
			log.validationException("c2a.configurationError",
			        new java.lang.IllegalArgumentException(
			                "ServiceConfigurationVersion is null"));
		if (item == null)
			log.validationException("c2a.configurationError",
			        new java.lang.IllegalArgumentException(
			                "BusinessInteractionItemType is null"));

		CommonManager commonManager = CommonHelper.makeCommonManager();
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(scv.getClass());
		scv = connect(scv);
		ServiceManager serviceManager = CommonHelper.makeServiceManager();

		// find service configitem "Properties" and reference it. Assume that it
		// always exists, predefined in config spec
		// TODO: Error case in mapServiceBusinessInteractionParameters if
		// Properties config item does not exist
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(scv, propertiesItemName);
		if (!Utils.isEmpty(configItems))
			propertiesItem = configItems.get(0);

		List<ParameterType> paramList = item.getParameterList();
		if (Utils.isEmpty(paramList)) {
			log.debug("c2aTechPack.emptyBiParameterData");
			return;
		}
		for (ParameterType param : paramList) {
			String paramName = param.getName();
			String paramValue = EntityUtils.getStringType(param, paramName);
			if (paramValue != null && !paramName.startsWith(prefix) && propertiesItem != null) {

				Set<ServiceConfigurationItemCharacteristic> chars = propertiesItem
				        .getCharacteristics();
				boolean found = false;
				if (!Utils.isEmpty(chars)) {
					for (CharValue characteristic : chars) {
						if (characteristic.getName().equals(paramName)) {
							characteristic.setValue(paramValue);
							found = true;
						}
					}
				}
				if (!found) {
					CharValue charvalue = commonManager.makeCharValue(
					        (CharacteristicExtensible) propertiesItem,
					        paramName, paramValue);
					chars.add((ServiceConfigurationItemCharacteristic) charvalue);
				}
				propertiesItem.setCharacteristics(chars);
				Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
				updateList.add(propertiesItem);
				configManager.updateConfigurationItem(updateList);
			}
		}
	}

	public BusinessInteraction captureAndProcessChildResourceBusinessInteraction(
	        BusinessInteractionType bitCapture, BusinessInteraction parentBi)
	        throws ValidationException {
		try {
			if (bitCapture == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}

			// Capture Interaction - creates the child BI and associates it to
			// the parent BI.
			BusinessInteractionManager biMgr = PersistenceHelper
			        .makeBusinessInteractionManager();
			BusinessInteraction childBi = biMgr.captureInteraction(parentBi,
			        bitCapture);

			if (FeedbackProviderImpl.hasErrors() || childBi == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}

			// Need to flush for process interaction to find the bi created in
			// capture interaction.
			this.flushTransaction();

			// Build Process Interaction BI Input
			BusinessInteractionType bitProcess = buildProcessInteractionBiInputType(childBi);

			// Process Interaction - Creates the Resource and auto-configures it
			// by invoking preProcessInteractionItems
			BusinessInteraction biReturn = this.processInteraction(childBi);
			if (FeedbackProviderImpl.hasErrors() || biReturn == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}
			this.flushTransaction();
			return biReturn;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public BusinessInteraction captureAndCustomProcessChildBusinessInteraction(
	        BusinessInteractionType bitCapture, BusinessInteraction parentBi)
	        throws ValidationException {
		try {
			if (bitCapture == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}

			// Capture Interaction - creates the child BI and associates it to
			// the parent BI.
			BusinessInteractionManager biMgr = PersistenceHelper
			        .makeBusinessInteractionManager();
			BusinessInteraction childBi = biMgr.captureInteraction(parentBi,
			        bitCapture);

			if (FeedbackProviderImpl.hasErrors() || childBi == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}

			// Need to flush for process interaction to find the bi created in
			// capture interaction.
			this.flushTransaction();

			// Build Process Interaction BI Input
			BusinessInteractionType bitProcess = buildProcessInteractionBiInputType(childBi);

			// Process Interaction - Creates the Resource and auto-configures it
			// by invoking preProcessInteractionItems
			BusinessInteraction biReturn = this.processInteraction(childBi);
			if (FeedbackProviderImpl.hasErrors() || biReturn == null) {
				log.validationException(
				        "c2a.captureAndProcessChildResourceBusinessInteractionError",
				        new java.lang.IllegalArgumentException());
			}
			this.flushTransaction();
			return biReturn;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public BusinessInteraction childResourceActionRequest(
	        ExtensionPointContext context,
	        ServiceConfigurationVersion svcConVers, String mappingFile,
	        String biSpec, List<NameValueActionParam> listnvap, String action,
	        String configItemName) throws ValidationException {
		try {
			BusinessInteractionType bytlda = null;
			if (action.equalsIgnoreCase("add")
			        || action.equalsIgnoreCase("create")) {
				bytlda = this
				        .buildCreateCaptureChildResourceInteractionBiInput2(
				                context, mappingFile, biSpec, listnvap);
			} else if (action.equalsIgnoreCase("disconnect")
			        || action.equalsIgnoreCase("remove")) {
				// TODO
			} else {
				ConsumableResource lda = this.getAssignedEntity(svcConVers,
				        configItemName);
				bytlda = this
				        .buildModifyCaptureChildResourceInteractionBiInput2(
				                context, mappingFile, lda, biSpec, listnvap);
			}
			BusinessInteraction parentBi = this.getParentBi(svcConVers);
			BusinessInteraction bypra = this
			        .captureAndProcessChildResourceBusinessInteraction(bytlda,
			                parentBi);
			/*
			 * removed as cancel BI does not work if child BI is completed
			 * biMgr.completeBusinessInteraction(bypra); // TODO deprecated
			 * method
			 */
			return bypra;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public GeographicAddress getServiceAddress(BusinessInteractionItemType item)
	        throws ValidationException {
		try {
			GeographicAddress serviceAddress = null;
			CommonManager commonManager = CommonHelper.makeCommonManager();

			List<ParameterType> types = item.getParameterList();
			if (Utils.isEmpty(types)) {
				log.debug("c2aTechPack.emptyBiParameterData");
			} else {
				for (ParameterType type : types) {
					org.apache.xmlbeans.XmlObject xmlObject = type.getValue();
					if (type.getName().equals("Service_Address")) {
						GeographicAddressType addressType = (GeographicAddressType) xmlObject;
						// start new
						List<GeographicAddress> listGeoAddress = commonManager
						        .findAddress(addressType);
						if (!Utils.isEmpty(listGeoAddress)) {
							// TODO - i only pick-up the first one, should be
							// OK, but needs double-checking
							serviceAddress = listGeoAddress.get(0);
						}
						// end new
						else {
							serviceAddress = commonManager.createAddress(
							        addressType, "Generic_Address");
						}
						break;
					}
				}
			}
			return serviceAddress;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public GeographicAddress getServiceAddress(
			GeographicAddressType srvAddrType, 
			String srvLocationSpecName)
			throws ValidationException {
		GeographicAddress serviceAddress = null;
		try {
			CommonManager comMgr = CommonHelper.makeCommonManager();
			List<GeographicAddress> addressList = comMgr.findAddress(srvAddrType);
			if (addressList != null  && !addressList.isEmpty())
				serviceAddress = addressList.get(0);
			else
				serviceAddress = comMgr.createAddress(
						srvAddrType, srvLocationSpecName);
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
		return serviceAddress;
	}
	
	public Collection<CustomObject> findCustomObjectsByCharContainingString(
	        String charName1, String charValue1) throws ValidationException {
		Collection<CustomObject> customObjectList = new ArrayList<CustomObject>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		CustomObjectManager customObjectGroupManager = PersistenceHelper
		        .makeCustomObjectManager();
		CustomObjectSearchCriteria customObjectSearchCriteria = customObjectGroupManager
		        .makeCustomObjectSearchCriteria();

		// prepare the characteristic search criteria
		CriteriaItem charCriteriaItem1 = customObjectSearchCriteria
		        .makeCriteriaItem();
		charCriteriaItem1.setCriteriaClass(CustomObjectCharacteristic.class);
		charCriteriaItem1.setName(charName1);
		charCriteriaItem1.setValue(charValue1);
		charCriteriaItem1.setOperator(CriteriaOperator.CONTAINS);
		criteriaItems.add(charCriteriaItem1);

		customObjectSearchCriteria.addCharacteristicData(criteriaItems);

		// 3. Find the inventory group(s) using the inventory group search
		// criteria
		customObjectList.addAll(customObjectGroupManager
		        .findCustomObjects(customObjectSearchCriteria));

		return customObjectList;
	}

	public LogicalDevice findAndReferenceTarget(
	        ServiceConfigurationVersion svcConVers, InventoryGroup serviceArea,
	        String resourceConfigItem, String targetType)
	        throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		ResourceManager resourceManager = CommonHelper.makeResourceManager();

		List<LogicalDevice> result = new java.util.ArrayList<LogicalDevice>();
		result = resourceManager.findLogicalDevice(serviceArea, targetType);
		if (result.equals(null) || result.size() == 0) {
			this.validExcpt("Could not find a Target of type " + targetType
			        + " in service area " + serviceArea.toString());
		}

		LogicalDevice nt = result.get(0);
		ServiceConfigurationItem networkTargetConfigItem = serviceManager
		        .addChildConfigItem(svcConVers, resourceConfigItem, "Target");
		if (networkTargetConfigItem == null) {
			this.validExcpt("Could not create Target config item under "
			        + resourceConfigItem);
		}
		this.referenceSubjectToParentCi(svcConVers, networkTargetConfigItem, nt);

		return nt;
	}

	public InventoryGroup findInventoryGroupByChar(String charName,
	        String charValue) throws ValidationException {
		List<InventoryGroup> invGroupList = new ArrayList<InventoryGroup>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		InventoryGroupManager inventoryGroupManager = PersistenceHelper
		        .makeInventoryGroupManager();
		InventoryGroupSearchCriteria inventoryGroupSearchCriteria = inventoryGroupManager
		        .makeInventoryGroupSearchCriteria();

		// prepare the characteristic search criteria
		CriteriaItem charCriteriaItem = inventoryGroupSearchCriteria
		        .makeCriteriaItem();
		charCriteriaItem.setCriteriaClass(InventoryGroupCharacteristic.class);
		charCriteriaItem.setName(charName);
		charCriteriaItem.setValue(charValue);
		charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		criteriaItems.add(charCriteriaItem);
		inventoryGroupSearchCriteria.addCharacteristicData(criteriaItems);

		// 3. Find the inventory group(s) using the inventory group search
		// criteria
		invGroupList.addAll(inventoryGroupManager
		        .findInventoryGroup(inventoryGroupSearchCriteria));
		InventoryGroup invGrp = invGroupList.get(0);

		return invGrp;
	}

	public Collection<InventoryGroup> findServiceAreasByServiceAddressAndCfs(
	        GeographicAddress serviceAddress, String cfs,
	        String[] candidateSpecs) throws ValidationException {
		List<InventoryGroup> groups = getServiceAreaResolver(cfs)
		        .findServingAreaForServiceAddress(serviceAddress, cfs,
		                candidateSpecs);
		return groups;
	}

	public InventoryGroup findServiceAreaByServiceAddressAndCfs(
	        GeographicAddress serviceAddress, String cfs,
	        String[] candidateSpecs) throws ValidationException {
		List<InventoryGroup> groups = getServiceAreaResolver(cfs)
		        .findServingAreaForServiceAddress(serviceAddress, cfs,
		                candidateSpecs);
		return groups.isEmpty() ? null : groups.get(0);
	}

	public InventoryGroup findServiceAreaByServiceAddress(
	        GeographicAddress serviceAddress, String[] candidateSpecs)
	        throws ValidationException {
		List<InventoryGroup> groups = getServiceAreaResolver()
		        .findServingAreaForServiceAddress(serviceAddress, null,
		                candidateSpecs);
		return groups.isEmpty() ? null : groups.get(0);
	}

	public InventoryGroup selectBestServiceArea(
	        ServiceConfigurationVersion svcConVers,
	        Collection<InventoryGroup> areas) {
		InventoryGroup best = getServiceAreaBestFitSelector(svcConVers)
		        .selectBestServiceArea(svcConVers, areas);
		return best;
	}
	
	public List<GroupEnabled> findEntityForServingArea(InventoryGroup servingArea,
			String entitySpecName, Class entityClass) {
		List<GroupEnabled> entities = new ArrayList<GroupEnabled>();
		
		InventoryGroupManager mgr = PersistenceHelper.makeInventoryGroupManager();
		InventoryGroupEntitySearchCriteria searchCriteria = mgr.makeInventoryGroupEntitySearchCriteria();
				
		//TODO commenting specification filter for now. Not working because of UIM bug. Create UIM bug
//		CriteriaItem specName = searchCriteria.makeCriteriaItem();
//		specName.setName("specification.name");
//		specName.setOperator(CriteriaOperator.EQUALS);
//		specName.setValue(entitySpecName);
		
		searchCriteria.setInventoryGroup(servingArea);
//		searchCriteria.setSpecificationName(specName);		
		searchCriteria.setEntityClass(entityClass);		
		searchCriteria.setRange(0, 10);
		
		entities = (List<GroupEnabled>)mgr.findEntitiesForInventoryGroup(searchCriteria);
		
		return entities;
	}
	
	public LogicalDevice findLdBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException {
		List<LogicalDevice> ldList = new ArrayList<LogicalDevice>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper
		        .makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria logicalDeviceSearchCriteria = logicalDeviceManager
		        .makeLogicalDeviceSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceSpecification ldSpec = (LogicalDeviceSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceSpecification[] ldSpecList = new LogicalDeviceSpecification[1];
			ldSpecList[0] = ldSpec;
			logicalDeviceSearchCriteria
			        .setLogicalDeviceSpecs(ldSpecList);
		}

		for (NameValueActionParam nvap : list) {
			CriteriaItem charCriteriaItem = logicalDeviceSearchCriteria
			        .makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			criteriaItems.add(charCriteriaItem);
			logicalDeviceSearchCriteria
			        .addCharacteristicData(criteriaItems);
		}

		ldList.addAll(logicalDeviceManager
		        .findLogicalDevice(logicalDeviceSearchCriteria));
		LogicalDevice ld = null;
		if (ldList.isEmpty()) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalStateException(), specName);
		} else {
			ld = ldList.get(0);
		}

		return ld;
	}
	
	public LogicalDevice findUnassignedLdBySpecCharacteristics(
			List<NameValueActionParam> characteristics, 
			String specName) 
			throws ValidationException {			
		return findLdBySpecCharacteristics(
				characteristics, 
				specName,
				AssignmentState.UNASSIGNED);
	}
	
	public LogicalDevice findLdBySpecCharacteristics(
			List<NameValueActionParam> characteristics, 
			String specName,
			AssignmentState state) 
			throws ValidationException {		
		
		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceManager ldManager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria ldCriteria = ldManager.makeLogicalDeviceSearchCriteria();
		LogicalDeviceSpecification ldSpec = 
				(LogicalDeviceSpecification) findAndValidateSpecification(specName); 

		if (ldSpec != null) {
			ldCriteria.setLogicalDeviceSpecification(ldSpec);
		}
		if (state != null) {
			ldCriteria.setAssignmentState(state);
		}
		ldCriteria.setRange(0, 10L);
								
		for (NameValueActionParam nvap : characteristics) {
			CriteriaItem charCriteriaItem = ldCriteria.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			String action = nvap.getAction();
			CriteriaOperator co = (action!=null && action.equalsIgnoreCase("CONTAINS_IGNORE_CASE")) 
					? CriteriaOperator.CONTAINS_IGNORE_CASE : CriteriaOperator.EQUALS_IGNORE_CASE;			
			charCriteriaItem.setOperator(co);
			criteriaItems.add(charCriteriaItem);
			ldCriteria.addCharacteristicData(criteriaItems);
		}
		List<LogicalDevice> ldList = 
				ldManager.findLogicalDevice(ldCriteria);		
		
		LogicalDevice ld = null;
		if(!Utils.isEmpty(ldList))
			ld = ldList.get(0);
		return ld;
	}
	
	public LogicalDevice findAndLockUnassignedLdBySpecCharacteristics(
	        List<NameValueActionParam> characteristics, 
	        String specName,
	        int lockPeriod)
	        throws ValidationException {		
		
		LogicalDevice ld = null;		
		
		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceManager ldManager = PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria ldCriteria = ldManager.makeLogicalDeviceSearchCriteria();
		LogicalDeviceSpecification ldSpec = 
				(LogicalDeviceSpecification) findAndValidateSpecification(specName); 

		if (ldSpec != null)
			ldCriteria.setLogicalDeviceSpecification(ldSpec);
		ldCriteria.setAssignmentState(AssignmentState.UNASSIGNED);		
		ldCriteria.setDistinct(true);
		ldCriteria.setDisableOrdering(true);
		ldCriteria.addHint(InventorySearchCriteriaHints.EXCLUDE_CONDITIONS_IN_UNASSIGNED_QUERY, "true");
		ldCriteria.addHint(InventorySearchCriteriaHints.EXCLUDE_RESERVATIONS_IN_UNASSIGNED_QUERY, "true");
		
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
		lockPolicy.setNumberOfResources(1);
		int expTime = (lockPeriod >= 0) ? lockPeriod : 30000;
		lockPolicy.setExpiration(expTime);		
        lockPolicy.setFilterExistingLocks(true);
        lockPolicy.setLockingStrategy(new ExtendedRangeLockingStrategy());
        ldCriteria.setLockPolicy(lockPolicy);
                
		for (NameValueActionParam nvap : characteristics) {
			CriteriaItem charCriteriaItem = ldCriteria.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			String action = nvap.getAction();
			CriteriaOperator co = (action!=null && action.equalsIgnoreCase("CONTAINS_IGNORE_CASE")) 
					? CriteriaOperator.CONTAINS_IGNORE_CASE : CriteriaOperator.EQUALS_IGNORE_CASE;			
			charCriteriaItem.setOperator(co);
			criteriaItems.add(charCriteriaItem);
			ldCriteria.addCharacteristicData(criteriaItems);
		}
				
		List<LogicalDevice> ldList = 
					ldManager.findLogicalDevice(ldCriteria);					
		
        if(!Utils.isEmpty(ldList))
            ld = ldList.get(0);
		return ld;
	}

		
	public LogicalDeviceAccount findLdaBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException {
		List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceAccountManager logicalDeviceAccountManager = PersistenceHelper
		        .makeLogicalDeviceAccountManager();
		LogicalDeviceAccountSearchCriteria logicalDeviceAccountGroupSearchCriteria = logicalDeviceAccountManager
		        .makeLogicalDeviceAccountSearchCriteria();
		
		LogicalDeviceAccountSpecification ldaSpec = 
				(LogicalDeviceAccountSpecification) findAndValidateSpecification(specName); 
		
		if (ldaSpec != null) {
			LogicalDeviceAccountSpecification[] ldaSpecList = new LogicalDeviceAccountSpecification[1];
			ldaSpecList[0] = ldaSpec;
			logicalDeviceAccountGroupSearchCriteria
			        .setLogicalDeviceAccountSpecs(ldaSpecList);
		}

		for (NameValueActionParam nvap : list) {
			CriteriaItem charCriteriaItem = logicalDeviceAccountGroupSearchCriteria
			        .makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LDAccountCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			criteriaItems.add(charCriteriaItem);
			logicalDeviceAccountGroupSearchCriteria
			        .addCharacteristicData(criteriaItems);
		}

		ldaList.addAll(logicalDeviceAccountManager
		        .findLogicalDeviceAccounts(logicalDeviceAccountGroupSearchCriteria));
		LogicalDeviceAccount lda = null;
		
		//Returning null if not found instead of ValidationException, to achieve the use case of create new if not found.
//		if (ldaList.isEmpty()) {
//			log.validationException("c2a.couldNotFind",
//			        new java.lang.IllegalStateException(), specName);
//		} else {
//			lda = ldaList.get(0);
//		}
		if(!Utils.isEmpty(ldaList))
			lda = ldaList.get(0);

		return lda;
	}
	
	public LogicalDeviceAccount findLdaBySpecCharAndLD(
			List<NameValueActionParam> list, String specName,
			LogicalDevice device) throws ValidationException {
		List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceAccountManager logicalDeviceAccountManager = PersistenceHelper
				.makeLogicalDeviceAccountManager();
		LogicalDeviceAccountSearchCriteria logicalDeviceAccountSearchCriteria = logicalDeviceAccountManager
				.makeLogicalDeviceAccountSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceAccountSpecification ldaSpec = (LogicalDeviceAccountSpecification) commonManager
				.findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
					new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceAccountSpecification[] ldaSpecList = new LogicalDeviceAccountSpecification[1];
			ldaSpecList[0] = ldaSpec;
			logicalDeviceAccountSearchCriteria
					.setLogicalDeviceAccountSpecs(ldaSpecList);
		}

		String charString = "";
		for (NameValueActionParam nvap : list) {
			CriteriaItem charCriteriaItem = logicalDeviceAccountSearchCriteria
					.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LDAccountCharacteristic.class);			
			charCriteriaItem.setName(nvap.getName());			
			charCriteriaItem.setValue(nvap.getValue());
			charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			criteriaItems.add(charCriteriaItem);
			charString += (nvap.getName() + "=" + nvap.getValue() + " ");
			logicalDeviceAccountSearchCriteria
					.addCharacteristicData(criteriaItems);
		}

		if (device != null) {
			logicalDeviceAccountSearchCriteria.setLogicalDevice(device);
		}
		ldaList.addAll(logicalDeviceAccountManager
				.findLogicalDeviceAccounts(logicalDeviceAccountSearchCriteria));
		LogicalDeviceAccount lda = null;

		if (ldaList.isEmpty()) {
			log.validationException("c2a.couldNotFindWithChars",
					new java.lang.IllegalStateException(), specName, charString);
		} else {
			lda = ldaList.get(0);
		}

		return lda;
	}
	
	public LogicalDeviceAccount findFreeLdaBySpecChar(
	        List<NameValueActionParam> list, String specName)
	        throws ValidationException {
		return findLdaBySpecChar(list, specName, AssignmentState.UNASSIGNED);
	}
	
	public LogicalDeviceAccount findLdaBySpecChar(
	        List<NameValueActionParam> list, String specName, AssignmentState state)
	        throws ValidationException {
		List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceAccountManager logicalDeviceAccountManager = PersistenceHelper
		        .makeLogicalDeviceAccountManager();
		LogicalDeviceAccountSearchCriteria ldaSearchCriteria = logicalDeviceAccountManager
		        .makeLogicalDeviceAccountSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceAccountSpecification ldaSpec = (LogicalDeviceAccountSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceAccountSpecification[] ldaSpecList = 
					new LogicalDeviceAccountSpecification[1];
			ldaSpecList[0] = ldaSpec;
			ldaSearchCriteria.setLogicalDeviceAccountSpecs(ldaSpecList);
		}

		if (state != null) {
			ldaSearchCriteria.setAssignmentState(state);
		}
		
		for (NameValueActionParam nvap : list) {
			CriteriaItem charCriteriaItem = ldaSearchCriteria.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(LDAccountCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			criteriaItems.add(charCriteriaItem);
			ldaSearchCriteria.addCharacteristicData(criteriaItems);
		}

		ldaList.addAll(logicalDeviceAccountManager
		        .findLogicalDeviceAccounts(ldaSearchCriteria));
		LogicalDeviceAccount lda = null;
		
		if(!Utils.isEmpty(ldaList))
			lda = ldaList.get(0);

		return lda;
	}

	public CustomObject findCoBySpecChar(List<NameValueActionParam> list,
	        String specName) throws ValidationException {
		List<CustomObject> coList = new ArrayList<CustomObject>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		CustomObjectManager customObjectManager = PersistenceHelper
		        .makeCustomObjectManager();
		CustomObjectSearchCriteria customObjectSearchCriteria = customObjectManager
		        .makeCustomObjectSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		CustomObjectSpecification coSpec = (CustomObjectSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (coSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			customObjectSearchCriteria.setCustomObjectSpecification(coSpec);
		}

		for (NameValueActionParam nvap : list) {
			CriteriaItem charCriteriaItem = customObjectSearchCriteria
			        .makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(CustomObjectCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
			criteriaItems.add(charCriteriaItem);
			customObjectSearchCriteria.addCharacteristicData(criteriaItems);
		}

		coList.addAll(customObjectManager
		        .findCustomObjects(customObjectSearchCriteria));
		CustomObject coo = null;
		if (coList.isEmpty()) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalStateException(), specName);
		} else {
			coo = coList.get(0);
		}

		return coo;
	}

	public LogicalDeviceAccount findLdaBySpecChar(String charName,
	        String charValue, String specName) throws ValidationException {
		List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceAccountManager logicalDeviceAccountManager = PersistenceHelper
		        .makeLogicalDeviceAccountManager();
		LogicalDeviceAccountSearchCriteria logicalDeviceAccountGroupSearchCriteria = logicalDeviceAccountManager
		        .makeLogicalDeviceAccountSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceAccountSpecification ldaSpec = (LogicalDeviceAccountSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceAccountSpecification[] ldaSpecList = new LogicalDeviceAccountSpecification[1];
			ldaSpecList[0] = ldaSpec;
			logicalDeviceAccountGroupSearchCriteria
			        .setLogicalDeviceAccountSpecs(ldaSpecList);
		}

		CriteriaItem charCriteriaItem = logicalDeviceAccountGroupSearchCriteria
		        .makeCriteriaItem();
		charCriteriaItem.setCriteriaClass(LDAccountCharacteristic.class);
		charCriteriaItem.setName(charName);
		charCriteriaItem.setValue(charValue);
		charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		criteriaItems.add(charCriteriaItem);
		logicalDeviceAccountGroupSearchCriteria
		        .addCharacteristicData(criteriaItems);

		ldaList.addAll(logicalDeviceAccountManager
		        .findLogicalDeviceAccounts(logicalDeviceAccountGroupSearchCriteria));
		LogicalDeviceAccount lda = null;
		if (ldaList.isEmpty()) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalStateException(), specName);
		} else {
			lda = ldaList.get(0);
		}
		return lda;
	}

	public DeviceInterface findDeviceInterfaceByLDSpecChar(LogicalDevice ld,
	        String charName, String charValue, String specName)
	        throws ValidationException {
		List<DeviceInterface> ldaList = new ArrayList<DeviceInterface>();
		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper
		        .makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria deviceInterfaceSearchCriteria = logicalDeviceManager
		        .makeDeviceInterfaceSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		DeviceInterfaceSpecification ldaSpec = (DeviceInterfaceSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			deviceInterfaceSearchCriteria
			        .setDeviceInterfaceSpecification(ldaSpec);
		}

		CriteriaItem charCriteriaItem = deviceInterfaceSearchCriteria
		        .makeCriteriaItem();
		charCriteriaItem.setCriteriaClass(DeviceInterfaceCharacteristic.class);
		charCriteriaItem.setName(charName);
		charCriteriaItem.setValue(charValue);
		charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		criteriaItems.add(charCriteriaItem);
		deviceInterfaceSearchCriteria.addCharacteristicData(criteriaItems);

		ldaList.addAll(logicalDeviceManager
		        .findDeviceInterface(deviceInterfaceSearchCriteria));
		DeviceInterface lda = null;
		for (DeviceInterface d : ldaList) {
			if (d.getLogicalDevice().equals(ld)) {
				lda = d;
			}
		}

		return lda;
	}

	public Pipe findPipeBySpecChar(String charName, String charValue,
	        String specName) throws ValidationException {
		List<Pipe> pipeList = new ArrayList<Pipe>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		PipeManager pipeManager = PersistenceHelper.makePipeManager();
		PipeSearchCriteria pipeSearchCriteria = pipeManager
		        .makePipeSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		PipeSpecification ldaSpec = (PipeSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			pipeSearchCriteria.setPipeSpecification(ldaSpec);
		}

		CriteriaItem charCriteriaItem = pipeSearchCriteria.makeCriteriaItem();
		charCriteriaItem.setCriteriaClass(PipeCharacteristic.class);
		charCriteriaItem.setName(charName);
		charCriteriaItem.setValue(charValue);
		charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		criteriaItems.add(charCriteriaItem);
		pipeSearchCriteria.addCharacteristicData(criteriaItems);

		pipeList.addAll(pipeManager.findPipes(pipeSearchCriteria));
		Pipe pipe = null;
		if (pipeList.isEmpty()) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalStateException(), specName);
		} else {
			pipe = pipeList.get(0);
		}
		return pipe;
	}

	public List<LogicalDevice> findLdBySpecChar(String charName,
	        String charValue, String specName) throws ValidationException {
		// TODO - not tested!!

		List<LogicalDevice> ldList = new ArrayList<LogicalDevice>();

		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		LogicalDeviceManager logicalDeviceManager = PersistenceHelper
		        .makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria logicalDeviceSearchCriteria = logicalDeviceManager
		        .makeLogicalDeviceSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceSpecification ldSpec = (LogicalDeviceSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceSpecification[] ldSpecList = new LogicalDeviceSpecification[1];
			ldSpecList[0] = ldSpec;
			logicalDeviceSearchCriteria.setLogicalDeviceSpecs(ldSpecList);
		}

		CriteriaItem charCriteriaItem = logicalDeviceSearchCriteria
		        .makeCriteriaItem();
		charCriteriaItem.setCriteriaClass(LogicalDeviceCharacteristic.class);
		charCriteriaItem.setName(charName);
		charCriteriaItem.setValue(charValue);
		charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		criteriaItems.add(charCriteriaItem);
		logicalDeviceSearchCriteria.addCharacteristicData(criteriaItems);

		ldList.addAll(logicalDeviceManager
		        .findLogicalDevice(logicalDeviceSearchCriteria));

		return ldList;
	}

	public LogicalDevice findLdBySpecCharIvg(String charName, String charValue,
	        String specName, InventoryGroup ivg) throws ValidationException {

		LogicalDevice result = null;
		ResourceManager resourceManager = CommonHelper.makeResourceManager();
		List<LogicalDevice> ldList = resourceManager.findLogicalDevice(ivg,
		        specName);

		for (LogicalDevice ldd : ldList) {
			if (getCharacteristicForEntity(ldd, charName).equals(charValue)) {
				result = ldd;
			}
		}
		return result;
	}

	public LogicalDeviceAccount findLdaByNameAndSpec(String name,
	        String specName) throws ValidationException {
		List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>();
		LogicalDeviceAccount lda = null;

		// Collection<CriteriaItem> criteriaItems = new
		// ArrayList<CriteriaItem>();
		LogicalDeviceAccountManager logicalDeviceAccountManager = PersistenceHelper
		        .makeLogicalDeviceAccountManager();
		LogicalDeviceAccountSearchCriteria logicalDeviceAccountGroupSearchCriteria = logicalDeviceAccountManager
		        .makeLogicalDeviceAccountSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		LogicalDeviceAccountSpecification ldaSpec = (LogicalDeviceAccountSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (ldaSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			LogicalDeviceAccountSpecification[] ldaSpecList = new LogicalDeviceAccountSpecification[1];
			ldaSpecList[0] = ldaSpec;
			logicalDeviceAccountGroupSearchCriteria
			        .setLogicalDeviceAccountSpecs(ldaSpecList);
		}

		CriteriaItem ldaCriteriaItem = logicalDeviceAccountGroupSearchCriteria
		        .makeCriteriaItem();
		ldaCriteriaItem.setName(name);
		ldaCriteriaItem.setValue(name);
		ldaCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
		logicalDeviceAccountGroupSearchCriteria.setName(ldaCriteriaItem);

		ldaList.addAll(logicalDeviceAccountManager
		        .findLogicalDeviceAccounts(logicalDeviceAccountGroupSearchCriteria));
		if (!ldaList.isEmpty()) {
			lda = ldaList.get(0);
		}

		return lda;
	}

	public DeviceInterface findFreeDeviceInterface(LogicalDevice ld,
	        String specName) throws ValidationException {
		List<DeviceInterface> diList = new ArrayList<DeviceInterface>();

		LogicalDeviceManager logicalDeviceManager = PersistenceHelper
		        .makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria deviceInterfaceSearchCriteria = logicalDeviceManager
		        .makeDeviceInterfaceSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		DeviceInterfaceSpecification diSpec = (DeviceInterfaceSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (diSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			deviceInterfaceSearchCriteria
			        .setDeviceInterfaceSpecification(diSpec);
		}
		deviceInterfaceSearchCriteria
		        .setAssignmentState(AssignmentState.UNASSIGNED);        
		deviceInterfaceSearchCriteria.setDistinct(true);
		deviceInterfaceSearchCriteria.setDisableOrdering(true);
		deviceInterfaceSearchCriteria.addHint(
				InventorySearchCriteriaHints.EXCLUDE_CONDITIONS_IN_UNASSIGNED_QUERY, "true");
		deviceInterfaceSearchCriteria.addHint(
				InventorySearchCriteriaHints.EXCLUDE_RESERVATIONS_IN_UNASSIGNED_QUERY, "true");
		
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
		lockPolicy.setNumberOfResources(1);		
		lockPolicy.setExpiration(300000);		
        lockPolicy.setFilterExistingLocks(true);
        lockPolicy.setLockingStrategy(new ExtendedRangeLockingStrategy());
        deviceInterfaceSearchCriteria.setLockPolicy(lockPolicy);
                
		CriteriaItem ldIdCriteriaItem = deviceInterfaceSearchCriteria
		        .makeCriteriaItem();
		ldIdCriteriaItem.setCriteriaClass(LogicalDevice.class);
		ldIdCriteriaItem.setValue(ld.getId());
		ldIdCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		deviceInterfaceSearchCriteria.setLogicalDeviceId(ldIdCriteriaItem);
		deviceInterfaceSearchCriteria.setTotalCount(50L);
				      
		DeviceInterface ldi = null;		
		diList = logicalDeviceManager
			        .findDeviceInterface(deviceInterfaceSearchCriteria);
		
		if(!Utils.isEmpty(diList)) {
			ldi = diList.get(0);
		}
		return ldi;
	}

	public List<DeviceInterface> findDeviceInterfaceBySpecAndLd(
	        LogicalDevice ld, String specName) throws ValidationException {
		List<DeviceInterface> diList = new ArrayList<DeviceInterface>();

		LogicalDeviceManager logicalDeviceManager = PersistenceHelper
		        .makeLogicalDeviceManager();
		DeviceInterfaceSearchCriteria deviceInterfaceSearchCriteria = logicalDeviceManager
		        .makeDeviceInterfaceSearchCriteria();

		CommonManager commonManager = CommonHelper.makeCommonManager();
		DeviceInterfaceSpecification diSpec = (DeviceInterfaceSpecification) commonManager
		        .findAndValidateSpecification(specName);

		if (diSpec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} else {
			deviceInterfaceSearchCriteria
			        .setDeviceInterfaceSpecification(diSpec);
		}

		CriteriaItem ldIdCriteriaItem = deviceInterfaceSearchCriteria
		        .makeCriteriaItem();
		ldIdCriteriaItem.setCriteriaClass(LogicalDevice.class);
		// ldIdCriteriaItem.setName("???");
		ldIdCriteriaItem.setValue(ld.getId());
		ldIdCriteriaItem.setOperator(CriteriaOperator.EQUALS);
		deviceInterfaceSearchCriteria.setLogicalDeviceId(ldIdCriteriaItem);
		diList = logicalDeviceManager
		        .findDeviceInterface(deviceInterfaceSearchCriteria);

		return diList;
	}
	
	public CustomNetworkAddress findAndLockUnassignedCNABySpecCharacteristics(
	        List<NameValueActionParam> characteristics, 
	        String specName,
	        int lockPeriod)
	        throws ValidationException {		
		
		CustomNetworkAddress cna = null;		
		
		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		CustomNetworkAddressManager cnaManager = 
				PersistenceHelper.makeCustomNetworkAddressManager();
		CustomNetworkAddressSearchCriteria cnaCriteria = 
				cnaManager.makeCustomNetworkAddressSearchCriteria();
		CustNetAddrSpecification cnaSpec = 
				(CustNetAddrSpecification) findAndValidateSpecification(specName); 

		if (cnaSpec != null)
			cnaCriteria.setCustomNetworkAddressSpecification(cnaSpec);

		cnaCriteria.setAssignmentState(AssignmentState.UNASSIGNED);		
		cnaCriteria.setDistinct(true);
		cnaCriteria.setDisableOrdering(true);
		cnaCriteria.addHint(
			InventorySearchCriteriaHints.EXCLUDE_CONDITIONS_IN_UNASSIGNED_QUERY, "true");
		cnaCriteria.addHint(
			InventorySearchCriteriaHints.EXCLUDE_RESERVATIONS_IN_UNASSIGNED_QUERY, "true");
		
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
		lockPolicy.setNumberOfResources(1);
		int expTime = (lockPeriod >= 0) ? lockPeriod : 5000;
		lockPolicy.setExpiration(expTime);		
        lockPolicy.setFilterExistingLocks(true);
        lockPolicy.setLockingStrategy(new ExtendedRangeLockingStrategy());
        cnaCriteria.setLockPolicy(lockPolicy);
                
		for (NameValueActionParam nvap : characteristics) {
			CriteriaItem charCriteriaItem = cnaCriteria.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(CustomNetworkAddressCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			String action = nvap.getAction();
			CriteriaOperator co = (action!=null && action.equalsIgnoreCase("CONTAINS_IGNORE_CASE")) 
					? CriteriaOperator.CONTAINS_IGNORE_CASE : CriteriaOperator.EQUALS_IGNORE_CASE;			
			charCriteriaItem.setOperator(co);
			criteriaItems.add(charCriteriaItem);
			cnaCriteria.addCharacteristicData(criteriaItems);
		}
						
		List<CustomNetworkAddress> cnaList = cnaManager.findCustomNetworkAddress(cnaCriteria);					
		
        if(!Utils.isEmpty(cnaList))
        	cna = cnaList.get(0);
		return cna;
	}
	
	public CustomNetworkAddress[] findAndLockUnassignedCNAsBySpecCharacteristics(
	        List<NameValueActionParam> characteristics, 
	        String specName,
	        int lockPeriod,
	        int count)
	        throws ValidationException {		
		
		Collection<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>();
		CustomNetworkAddressManager cnaManager = 
				PersistenceHelper.makeCustomNetworkAddressManager();
		CustomNetworkAddressSearchCriteria cnaCriteria = 
				cnaManager.makeCustomNetworkAddressSearchCriteria();
		CustNetAddrSpecification cnaSpec = 
				(CustNetAddrSpecification) findAndValidateSpecification(specName); 

		if (cnaSpec != null)
			cnaCriteria.setCustomNetworkAddressSpecification(cnaSpec);

		cnaCriteria.setAssignmentState(AssignmentState.UNASSIGNED);		
		cnaCriteria.setDistinct(true);
		cnaCriteria.setDisableOrdering(true);
		cnaCriteria.addHint(
			InventorySearchCriteriaHints.EXCLUDE_CONDITIONS_IN_UNASSIGNED_QUERY, "true");
		cnaCriteria.addHint(
			InventorySearchCriteriaHints.EXCLUDE_RESERVATIONS_IN_UNASSIGNED_QUERY, "true");
		
		LockPolicy lockPolicy = PersistenceHelper.makeLockPolicy();
		int numRes = (count < 1) ? 1: count;
		lockPolicy.setNumberOfResources(numRes);
		int expTime = (lockPeriod >= 0) ? lockPeriod : 1000;
		lockPolicy.setExpiration(expTime);		
        lockPolicy.setFilterExistingLocks(true);
        lockPolicy.setLockingStrategy(new ExtendedRangeLockingStrategy());
        cnaCriteria.setLockPolicy(lockPolicy);
        cnaCriteria.setTotalCount((long)numRes);
                
		for (NameValueActionParam nvap : characteristics) {
			CriteriaItem charCriteriaItem = cnaCriteria.makeCriteriaItem();
			charCriteriaItem.setCriteriaClass(CustomNetworkAddressCharacteristic.class);
			charCriteriaItem.setName(nvap.getName());
			charCriteriaItem.setValue(nvap.getValue());
			String action = nvap.getAction();
			CriteriaOperator co = (action!=null && action.equalsIgnoreCase("CONTAINS_IGNORE_CASE")) 
					? CriteriaOperator.CONTAINS_IGNORE_CASE : CriteriaOperator.EQUALS_IGNORE_CASE;			
			charCriteriaItem.setOperator(co);
			criteriaItems.add(charCriteriaItem);
			cnaCriteria.addCharacteristicData(criteriaItems);
		}
						
		List<CustomNetworkAddress> cnaList = cnaManager.findCustomNetworkAddress(cnaCriteria);        	
		return (!Utils.isEmpty(cnaList)) 
			? cnaList.toArray(new CustomNetworkAddress[cnaList.size()]) : null;
	}

	public void assignSubjectToParent(ServiceConfigurationVersion parentScv,
	        String configItemName, ConsumableResource childResource)
	        throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(parentScv, configItemName);

		if (Utils.isEmpty(configItems))
			log.validationException("c2a.configItemNotFound",
			        new java.lang.IllegalArgumentException(), configItemName);
		boolean bbAccessSetUp = serviceManager.checkItemAssignedReferenced(
		        parentScv, configItems.get(0));
		if (bbAccessSetUp)
			return;
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(parentScv.getClass());
		try {
			configManager.assignResource(configItems.get(0), childResource,
			        null, null);
		} catch (Exception e) {
			log.validationException("c2a.assignResourceError", e,
			        configItemName);
		}
	}

	public void unassignResource(ServiceConfigurationVersion config,
	        String configItemName) throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(config, configItemName);

		if (Utils.isEmpty(configItems))
			log.validationException("c2a.configItemNotFound",
			        new java.lang.IllegalArgumentException(), configItemName);

		boolean hasAllocations = serviceManager.checkItemAssignedReferenced(
		        config, configItems.get(0));
		if (!hasAllocations)
			return;

		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(config.getClass());
		configManager.unallocateInventoryConfigurationItems(configItems);
	}

	public void referenceSubjectToParent(ServiceConfigurationVersion parentScv,
	        String configItemName, ConfigurationReferenceEnabled childResource)
	        throws ValidationException {
		// try {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(parentScv, configItemName);

		if (Utils.isEmpty(configItems))
			log.validationException("c2a.configItemNotFound",
			        new java.lang.IllegalArgumentException(), configItemName);

		boolean bbAccessSetUp = serviceManager.checkItemAssignedReferenced(
		        parentScv, configItems.get(0));
		if (bbAccessSetUp)
			return;

		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(parentScv.getClass());
		// reference loc.
		configManager.referenceEntity(configItems.get(0), childResource);
		if (FeedbackProviderImpl.hasErrors()) {
			log.exception("c2a.referenceSubjectToParent.processError",
			        new java.lang.IllegalArgumentException());
		}
		// }
		// catch (Exception e) {
		// throw new ValidationException(e);
		// }
		// }
	}

	public void referenceSubjectToParentCi(
	        ServiceConfigurationVersion parentScv,
	        ServiceConfigurationItem configItem,
	        ConfigurationReferenceEnabled childResource)
	        throws ValidationException {

		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		boolean bbAccessSetUp = serviceManager.checkItemAssignedReferenced(
		        parentScv, configItem);
		if (bbAccessSetUp)
			return;

		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(parentScv.getClass());

		configManager.referenceEntity(configItem, childResource);
		if (FeedbackProviderImpl.hasErrors()) {
			log.exception("c2a.referenceSubjectToParent.processError",
			        new java.lang.IllegalArgumentException());
		}
	}

	public Service getServiceForBusinessInteraction(BusinessInteraction bi)
	        throws ValidationException {
		Service service = null;

		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			biAttachment = biAttachmentList.get(biAttachmentList.size() - 1);
		}
		if (biAttachment == null) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error(
			        "c2a.getServiceForBusinessInteraction.entityAttachmentError",
			        e, bi.getId());
			throw new ValidationException(e);
		}
		try {
			oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
			        .getInteraction();
			oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
			        .getBody();
			List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
			        .getItemList();
			BusinessInteractionItemType item = items.get(0);
			ServiceType serviceType = (ServiceType) item.getEntity();
			String serviceId = serviceType.getId();
			String extServiceId = null;
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			service = serviceManager.getService(serviceId, extServiceId);
		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
			return null;
		}
		return service;
	}

	public void associateServiceAddressToServiceArea(GeographicPlace geo,
	        InventoryGroup invGrp) throws ValidationException {
		// TODO - this is not checking if the association already exists. Also
		// assumes Roles Service_Address & Service_Area already exist and is not
		// creating them automatically if not
		// CommonManager commonManager = CommonHelper.makeCommonManager();
		InventoryGroupManager inventoryGroupManager = PersistenceHelper
		        .makeInventoryGroupManager();
		// RoleManager invRoleMgr = PersistenceHelper.makeRoleManager();
		inventoryGroupManager.associatePersistentToInventoryGroup(invGrp, geo);
		/**
		 * InvGroupRef invGrpRef; try { invGrpRef =
		 * inventoryGroupManager.makeInvGroupRef();
		 * 
		 * InventoryRoleSearchCriteria roleSearchCriteria
		 * =invRoleMgr.makeInventoryRoleSearchCriteria(); Specification roleSpec
		 * = commonManager.findAndValidateSpecification("Service_Address"); if
		 * (roleSpec == null) log.validationException(
		 * "associateServiceAddressToServiceArea: could not find Service_Address role spec"
		 * , new java.lang.IllegalArgumentException(),
		 * "associateServiceAddressToServiceArea");
		 * roleSearchCriteria.setRoleSpecification(roleSpec); List
		 * <InventoryRole> roles =
		 * invRoleMgr.findInventoryRoles(roleSearchCriteria);
		 * invGrpRef.setToEntityRoleKey(roles.get(0).getOid()); Specification
		 * roleSpec1 =
		 * commonManager.findAndValidateSpecification("Service_Area"); if
		 * (roleSpec1 == null) log.validationException(
		 * "associateServiceAddressToServiceArea: could not find Service_Area role spec"
		 * , new java.lang.IllegalArgumentException(),
		 * "associateServiceAddressToServiceArea");
		 * roleSearchCriteria.setRoleSpecification(roleSpec1); List
		 * <InventoryRole> roles1 =
		 * invRoleMgr.findInventoryRoles(roleSearchCriteria);
		 * invGrpRef.setFromEntityRoleKey(roles1.get(0).getOid());
		 * 
		 * } catch (InstantiationException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (IllegalAccessException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 **/

		// List<InventoryGroup> listInvGrp =
		// inventoryGroupManager.getInventoryGroupsForPlace(geo);
	}

	public String getCharacteristicForEntity(CharacteristicExtensible E,
	        String name) throws ValidationException {
		String charValue = null;
		Set<CharValue> characts = (Set<CharValue>) E.getCharacteristics();
		for (CharValue charVale : characts) {
			if (charVale.getName().equals(name)) {
				charValue = charVale.getValue();
				break;
			}
		}
		return charValue;
	}

	public void updateCharacteristicForEntity(CharacteristicExtensible entity,
	        String name, String value) throws ValidationException {
		CharacteristicSetter setter = new CharacteristicSetter(entity, name);
		setter.setValue(value);
	}

	public void updateCharacteristicForPipe(Pipe pipe, String name, String value)
	        throws ValidationException {
		Set<PipeCharacteristic> pcs = pipe.getCharacteristics();

		for (PipeCharacteristic pc : pcs) {
			if (pc.getName().equals(name)) {
				pc.setValue(value);
			}
		}
		pipe.setCharacteristics(pcs);
		this.flushTransaction();
	}

	public String getStringPropertyForBiItemType(
	        BusinessInteractionItemType itemType, String name)
	        throws ValidationException {
		String stringPropertyValue = null;
		List<ParameterType> paramTypeList = itemType.getParameterList();
		Iterator<ParameterType> itt = paramTypeList.iterator();
		while (itt.hasNext()) {
			ParameterType p = (ParameterType) itt.next();
			if (p.getName().equals(name)) {
				if (!p.isNil()) {
					// EntityUtils.validateStringType(p, name);
					org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
					        .newInstance();
					xmlString
					        .setStringValue(EntityUtils.getStringType(p, name));
					stringPropertyValue = xmlString.getStringValue();
				}
			}
		}
		return stringPropertyValue;
	}

	public Collection<NameValueActionParam> getStringBiItemParams(
	        BusinessInteractionItemType itemType) throws ValidationException {
		Collection<NameValueActionParam> stringBiItemParams = new ArrayList<NameValueActionParam>();
		List<ParameterType> paramTypeList = itemType.getParameterList();
		Iterator<ParameterType> itt = paramTypeList.iterator();
		while (itt.hasNext()) {
			NameValueActionParam nameValueActionParam = new NameValueActionParam();
			ParameterType p = (ParameterType) itt.next();
			nameValueActionParam.setName(p.getName());
			org.apache.xmlbeans.XmlObject xmlObject = p.getValue();
			if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
				org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
				        .newInstance();
				xmlString.setStringValue(EntityUtils.getStringType(p,
				        p.getName()));
				nameValueActionParam.setValue(xmlString.getStringValue());
				stringBiItemParams.add(nameValueActionParam);
			} else if (xmlObject instanceof org.apache.xmlbeans.XmlInt) {
				org.apache.xmlbeans.XmlInt xmlInt = org.apache.xmlbeans.XmlInt.Factory
				        .newInstance();
				xmlInt.setIntValue(getIntegerrType(p, p.getName()));
				nameValueActionParam.setValueInt(xmlInt.getIntValue());
				stringBiItemParams.add(nameValueActionParam);
			}
		}
		return stringBiItemParams;
	}

	public BusinessInteractionItemType getActionBiItemType(
	        BusinessInteraction bi) throws ValidationException {
		BusinessInteractionItemType itemType = null;

		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			biAttachment = biAttachmentList.get(biAttachmentList.size() - 1);
		}
		if (biAttachment == null) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error(
			        "c2a.getServiceForBusinessInteraction.entityAttachmentError",
			        e, bi.getId());
			throw new ValidationException(e);
		}
		try {
			oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
			        .getInteraction();
			oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
			        .getBody();
			List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
			        .getItemList();
			if (!items.isEmpty())
				itemType = items.get(0);
		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
		}
		return itemType;
	}

	public String getActionBiItemParam(BusinessInteractionItemType itemType)
	        throws ValidationException {
		String actionBiItemParam = null;
		try {
			List<ParameterType> paramTypeList = itemType.getParameterList();
			Iterator<ParameterType> itt = paramTypeList.iterator();
			while (itt.hasNext()) {
				ParameterType p = (ParameterType) itt.next();
				if (p.getName().equals("Action")) {
					org.apache.xmlbeans.XmlObject xmlObject = p.getValue();
					if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						xmlString.setStringValue(EntityUtils.getStringType(p,
						        p.getName()));
						actionBiItemParam = xmlString.getStringValue();
					}
				}
			}
		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
		}
		return actionBiItemParam;
	}
	
	public String getBiItemParamByName(BusinessInteraction bi, String name)
	        throws ValidationException {
		String actionBiItemParam = null;

		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			biAttachment = biAttachmentList.get(biAttachmentList.size() - 1);
		}
		if (biAttachment == null) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error(
			        "c2a.getServiceForBusinessInteraction.entityAttachmentError",
			        e, bi.getId());
			throw new ValidationException(e);
		}
		try {
			oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
			        .getInteraction();
			oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
			        .getBody();
			List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
			        .getItemList();
			if (items.isEmpty()) {
				log.error(
				        "c2a.getServiceForBusinessInteractionError",
				        new IllegalStateException("BusinessInteraction "
				                + bi.getId() + " has no items"));
				return null;
			}
			BusinessInteractionItemType itemType = items.get(0);

			List<ParameterType> paramTypeList = itemType.getParameterList();
			Iterator<ParameterType> itt = paramTypeList.iterator();
			while (itt.hasNext()) {
				ParameterType p = (ParameterType) itt.next();
				if (p.getName().equals(name)) {
					org.apache.xmlbeans.XmlObject xmlObject = p.getValue();
					if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						xmlString.setStringValue(EntityUtils.getStringType(p,
						        p.getName()));
						actionBiItemParam = xmlString.getStringValue();
					}
					break;
				}
			}

		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
		}
		return actionBiItemParam;
	}

	public String getBiItemTypeParamByName(
	        BusinessInteractionItemType itemType, String name)
	        throws ValidationException {
		String actionBiItemParam = null;

		try {
			List<ParameterType> paramTypeList = itemType.getParameterList();
			Iterator<ParameterType> itt = paramTypeList.iterator();
			while (itt.hasNext()) {
				ParameterType p = (ParameterType) itt.next();
				if (p.getName().equals(name)) {
					org.apache.xmlbeans.XmlObject xmlObject = p.getValue();
					if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						xmlString.setStringValue(EntityUtils.getStringType(p,
						        p.getName()));
						actionBiItemParam = xmlString.getStringValue();
					}
					break;
				}
			}

		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
		}
		return actionBiItemParam;
	}

	public String getActionBiItemParam(BusinessInteraction bi)
	        throws ValidationException {
		String actionBiItemParam = null;

		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			biAttachment = biAttachmentList.get(biAttachmentList.size() - 1);
		}
		if (biAttachment == null) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error(
			        "c2a.getServiceForBusinessInteraction.entityAttachmentError",
			        e, bi.getId());
			throw new ValidationException(e);
		}
		try {
			oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
			        .getInteraction();
			oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
			        .getBody();
			List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
			        .getItemList();
			BusinessInteractionItemType itemType = items.get(0);

			List<ParameterType> paramTypeList = itemType.getParameterList();
			Iterator<ParameterType> itt = paramTypeList.iterator();
			while (itt.hasNext()) {
				ParameterType p = (ParameterType) itt.next();
				if (p.getName().equals("Action")) {
					org.apache.xmlbeans.XmlObject xmlObject = p.getValue();
					if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						xmlString.setStringValue(EntityUtils.getStringType(p,
						        p.getName()));
						actionBiItemParam = xmlString.getStringValue();
					}
					break;
				}
			}

		} catch (Exception e) {
			if (!FeedbackProviderImpl.hasErrors()) {
				log.error("c2a.getServiceForBusinessInteractionError", e);
			}
		}
		return actionBiItemParam;
	}

	public static Integer getIntegerrType(ParameterType type, String name)
	        throws ValidationException {
		int paramInt = 0;
		Integer returnValue = null;
		org.apache.xmlbeans.XmlObject xmlObject = type.getValue();
		if (xmlObject instanceof org.apache.xmlbeans.XmlInt) {
			org.apache.xmlbeans.XmlInt xmlInt = (org.apache.xmlbeans.XmlInt) xmlObject;
			paramInt = xmlInt.getIntValue();
			returnValue = paramInt;
		}
		return returnValue;
	}

	public void addUpdateConfigItemCharacteristic(
	        ServiceConfigurationVersion scv,
	        ServiceConfigurationItem configItem, String characteristicName,
	        String value) throws ValidationException {
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
			// TODO add characteristic
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
		charmgr.validate(configItem);
	}

	public void addUpdateLogicalDeviceAccountCharacteristic(
	        LogicalDeviceAccount lda, String characteristicName, String value)
	        throws ValidationException {
		boolean found = false;
		CharacteristicManager charmgr = PersistenceHelper
		        .makeCharacteristicManager();
		Set<LDAccountCharacteristic> characterictics = lda.getCharacteristics();
		for (CharValue characteristic : characterictics) {
			if (characteristic.getName().equals(characteristicName)) {
				characteristic.setValue(value);
				log.debug(
				        "",
				        new Object[] { "addUpdateLogicalDeviceAccountCharacteristic:  updating char: "
				                + characteristic.getName()
				                + "with value: "
				                + characteristic.getValue() });
				found = true;
				break;
			}
		}
		if (!found) {
			// TODO add characteristic
			CharacteristicSpecification charSpec = charmgr
			        .getCharacteristicSpecification(characteristicName);
			if (charSpec == null) {
				log.error("c2a.addUpdateConfigItemCharacteristic",
				        characteristicName);
			}

			CharValue e = lda.makeCharacteristicInstance();
			e.setCharacteristicSpecification(charSpec);
			e.setName(characteristicName);
			e.setValue(value);
			e.setLabel(characteristicName);
			characterictics.add((LDAccountCharacteristic) e);
		}
		lda.setCharacteristics(characterictics);
		this.flushTransaction();
		log.debug(
		        "",
		        new Object[] { "addUpdateLogicalDeviceAccountCharacteristic:  characterictics size: "
		                + characterictics.size() });

		charmgr.validate(lda);
	}

	public void addUpdateServiceCharacteristic(
	        Service service, String characteristicName, String value)
	        throws ValidationException {
		log.debug("", "#### addUpdateServiceCharacteristic Starts ####");

		boolean found = false;
		CharacteristicManager charmgr = PersistenceHelper
		        .makeCharacteristicManager();
		Set<ServiceCharacteristic> characterictics = service.getCharacteristics();
		for (CharValue characteristic : characterictics) {
			if (characteristic.getName().equals(characteristicName)) {
				characteristic.setValue(value);
				log.debug(
				        "",
				        new Object[] { "addUpdateServiceCharacteristic:  updating char: "
				                + characteristic.getName()
				                + "with value: "
				                + characteristic.getValue() });
				found = true;
				break;
			}
		}
		if (!found) {
			// TODO add characteristic
			CharacteristicSpecification charSpec = charmgr
			        .getCharacteristicSpecification(characteristicName);
			if (charSpec == null) {
				log.error("c2a.addUpdateConfigItemCharacteristic",
				        characteristicName);
			}

			CharValue e = service.makeCharacteristicInstance();
			e.setCharacteristicSpecification(charSpec);
			e.setName(characteristicName);
			e.setValue(value);
			e.setLabel(characteristicName);
			characterictics.add((ServiceCharacteristic) e);
		}
		service.setCharacteristics(characterictics);
		this.flushTransaction();
		log.debug(
		        "",
		        new Object[] { "addUpdateServiceCharacteristic:  characterictics size: "
		                + characterictics.size() });

		charmgr.validate(service);
		log.debug("", "#### addUpdateServiceCharacteristic Ends ####");
	}


	public ConsumableResource createResource(BusinessInteraction bi)
	        throws ValidationException {
		ConsumableResource createdResource = null;
		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			biAttachment = biAttachmentList.get(biAttachmentList.size() - 1);
		}
		if (biAttachment == null) {
			throw new ValidationException(MessageResource.getMessage(
			        "businessInteraction.entityAttachmentError",
			        bi.getDisplayInfo()));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error("businessInteraction.entityAttachmentError", e,
			        bi.getId());
			throw new ValidationException(e);
		}
		oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
		        .getInteraction();
		oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
		        .getBody();
		List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
		        .getItemList();
		if (items.isEmpty()) {
			this.validExcpt("Business Interaction has no items: "
			        + bitype.xmlText());
		}
		BusinessInteractionItemType itemType = items.get(0);
		if (itemType == null || itemType.getEntity() == null) {
			this.validExcpt("Business Interaction: Item is null: "
			        + bitype.xmlText());
		}
		BusinessInteractionItemType[] itemArray = new BusinessInteractionItemType[1];
		if (itemType.getEntity() instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceType) {
			LogicalDeviceType ldt = (LogicalDeviceType) itemType.getEntity();
			LogicalDeviceManager ldManager = PersistenceHelper
			        .makeLogicalDeviceManager();
			LogicalDevice ld = ldManager.makeLogicalDevice();
			EntityUtils.populate(ld, ldt);
			List<NameValueActionParam> colnvap = (List<NameValueActionParam>) this
			        .getStringBiItemParams(itemType);
			for (NameValueActionParam p : colnvap) {
				String value = p.getValue() != null ? p.getValue() : "";
				EntityUtils.setValue(ld, p.getName(), value);
			}
			Set<LogicalDeviceCharacteristic> newChars = (Set<LogicalDeviceCharacteristic>) ld
			        .getCharacteristics();
			EntityUtils.defaultMissingCharacteristics(ld, newChars, ld
			        .getSpecification().getCharacteristicSpecUsages());
			ld.setCharacteristics(newChars);

			List<LogicalDevice> ldlList = new ArrayList<LogicalDevice>();
			ldlList.add(ld);

			createdResource = ldManager.createLogicalDevice(ldlList).get(0);
			String ldid = createdResource.getId();
			ldt.setId(ldid);
			itemArray[0] = itemType;
			itemArray[0].setEntity(ldt);
		} else if (itemType.getEntity() instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType) {
			String parentDeviceName = "AAA_Server"; // TODO
			LogicalDevice parentDevice = findLogicalDeviceByName(parentDeviceName);
			LogicalDeviceAccountType ldat = (LogicalDeviceAccountType) itemType
			        .getEntity();
			LogicalDeviceAccountManager ldaManager = PersistenceHelper
			        .makeLogicalDeviceAccountManager();
			LogicalDeviceAccount lda = ldaManager.makeLogicalDeviceAccount();
			lda.setLogicalDevice(parentDevice);
			EntityUtils.populate(lda, ldat);
			List<NameValueActionParam> colnvap = (List<NameValueActionParam>) this
			        .getStringBiItemParams(itemType);
			for (NameValueActionParam p : colnvap) {
				String value = p.getValue() != null ? p.getValue() : "";
				EntityUtils.setValue(lda, p.getName(), value);
			}
			Set<LDAccountCharacteristic> newChars = (Set<LDAccountCharacteristic>) lda
			        .getCharacteristics();
			EntityUtils.defaultMissingCharacteristics(lda, newChars, lda
			        .getSpecification().getCharacteristicSpecUsages());
			lda.setCharacteristics(newChars);
			List<LogicalDeviceAccount> ldalList = new ArrayList<LogicalDeviceAccount>();
			ldalList.add(lda);
			createdResource = ldaManager.createLogicalDeviceAccounts(ldalList)
			        .get(0);
			String ldaid = createdResource.getId();
			ldat.setId(ldaid);
			itemArray[0] = itemType;
			itemArray[0].setEntity(ldat);
		} else if (itemType.getEntity() instanceof oracle.communications.inventory.xmlbeans.DeviceInterfaceType) {
			String parentDeviceName = "OLT"; // TODO
			LogicalDevice parentDevice = findLogicalDeviceByName(parentDeviceName);
			DeviceInterfaceType dit = (DeviceInterfaceType) itemType
			        .getEntity();
			LogicalDeviceManager ldManager = PersistenceHelper
			        .makeLogicalDeviceManager();
			DeviceInterface iface = ldManager.makeDeviceInterface();
			EntityUtils.populate(iface, dit);
			List<NameValueActionParam> colnvap = (List<NameValueActionParam>) this
			        .getStringBiItemParams(itemType);
			for (NameValueActionParam p : colnvap) {
				String value = p.getValue() != null ? p.getValue() : "";
				EntityUtils.setValue(iface, p.getName(), value);
			}
			Set<DeviceInterfaceCharacteristic> newChars = (Set<DeviceInterfaceCharacteristic>) iface
			        .getCharacteristics();
			EntityUtils.defaultMissingCharacteristics(iface, newChars, iface
			        .getSpecification().getCharacteristicSpecUsages());
			iface.setCharacteristics(newChars);
			List<DeviceInterface> ifaceList = new ArrayList<DeviceInterface>();
			ifaceList.add(iface);
			createdResource = ldManager.createDeviceInterface(parentDevice,
			        ifaceList).get(0);
			String ifaceId = createdResource.getId();
			dit.setId(ifaceId);
			itemArray[0] = itemType;
			itemArray[0].setEntity(dit);
		} else if (itemType.getEntity() instanceof oracle.communications.inventory.xmlbeans.ServiceType) {
			ServiceType svct = (ServiceType) itemType.getEntity();
			oracle.communications.inventory.api.service.ServiceManager svcManager = PersistenceHelper
			        .makeServiceManager();
			Service service = svcManager.makeService(Service.class);
			EntityUtils.populate(service, svct);
			List<NameValueActionParam> colnvap = (List<NameValueActionParam>) this
			        .getStringBiItemParams(itemType);
			for (NameValueActionParam p : colnvap) {
				String value = p.getValue() != null ? p.getValue() : "";
				EntityUtils.setValue(service, p.getName(), value);
			}
			Set<ServiceCharacteristic> newChars = (Set<ServiceCharacteristic>) service
			        .getCharacteristics();
			EntityUtils.defaultMissingCharacteristics(service, newChars,
			        service.getSpecification().getCharacteristicSpecUsages());
			service.setCharacteristics(newChars);
			List<Service> svcList = new ArrayList<Service>();
			svcList.add(service);
			createdResource = svcManager.createService(svcList).get(0);
			String ifaceId = createdResource.getId();
			svct.setId(ifaceId);
			itemArray[0] = itemType;
			itemArray[0].setEntity(svct);
		} else {
			throw new ValidationException(
			        "Business Interaction Item contains an unrecognized type of entity: "
			                + itemType.xmlText());
		}
		body.setItemArray(itemArray);
		bitype.setBody(body);
		doc = InteractionDocument.Factory.newInstance();
		doc.setInteraction(bitype);

		String xml = doc.xmlText(new XmlOptions().setSaveAggressiveNamespaces()
		        .setSavePrettyPrint().setSavePrettyPrintIndent(1));
		byte[] bytes = biAttachment.convertStringToContent(xml);
		biAttachment.setContent(bytes);
		biAttachmentList.set(0, biAttachment);
		bi.setAttachments(biAttachmentList);

		return createdResource;
	}

	public ConsumableResource updateResource(BusinessInteraction bi,
	        XmlObject resource, BusinessInteractionItemType itemType)
	        throws ValidationException {
		// TODO this assumes the full new version of the new entity is in the
		// BI. Need to support case where there is only the delta in the BI ?
		ConsumableResource updatedResource = null;

		if (resource instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceType) {
			LogicalDevice ld = null;
			LogicalDeviceManager ldManager = PersistenceHelper
			        .makeLogicalDeviceManager();
			ld = ldManager.makeLogicalDevice();
			EntityUtils.populate(ld, resource);
			List<LogicalDevice> ldList = new ArrayList<LogicalDevice>(1);
			ldList.add(ld);
			updatedResource = ldManager.updateLogicalDevice(ldList).get(0);
		}

		if (resource instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType) {
			LogicalDeviceAccount lda = null;
			LogicalDeviceAccountManager ldaManager = PersistenceHelper
			        .makeLogicalDeviceAccountManager();

			Finder f = PersistenceHelper.makeFinder();
			Collection<LogicalDeviceAccount> ldaC = f
			        .findById(
			                LogicalDeviceAccount.class,
			                ((oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType) resource)
			                        .getId());
			f.close();

			Iterator itt = ldaC.iterator();
			while (itt.hasNext()) {
				lda = (LogicalDeviceAccount) itt.next();
				lda.setDescription("Last updated by BI " + bi.getId() + " "
				        + bi.getName());
			}
			List<ParameterType> types = itemType.getParameterList();
			if (Utils.isEmpty(types)) {
				log.debug("c2aTechPack.emptyBiParameterData");
			} else {
				for (ParameterType type : types) {
					// org.apache.xmlbeans.XmlObject xmlObject =
					// type.getValue();
					String paramName = type.getName();
					String paramValue = EntityUtils.getStringType(type,
					        paramName);

					if (!paramName.equals("Action")) {
						// this.addUpdateLogicalDeviceAccountCharacteristic(lda,
						// paramName, paramValue);
						this.updateCharacteristicForEntity(lda, paramName,
						        paramValue);
					}
				}
			}

			/**
			 * lda = ldaManager.makeLogicalDeviceAccount();
			 * EntityUtils.populate(lda, resource); Collection
			 * <LogicalDeviceAccount> ldaList = new
			 * ArrayList<LogicalDeviceAccount>(1); ldaList.add(lda);
			 * updatedResource =
			 * ldaManager.updateLogicalDeviceAccounts(ldaList).get(0);
			 **/
			Collection<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount>(
			        1);
			ldaList.add(lda);
			updatedResource = ldaManager.updateLogicalDeviceAccounts(ldaList)
			        .get(0);
			// updatedResource = lda;
		}

		return updatedResource;
	}

	public void deleteResource(XmlObject resource) throws ValidationException {
		if (resource instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceType) {
			LogicalDeviceManager ldManager = PersistenceHelper
			        .makeLogicalDeviceManager();
			Finder f = PersistenceHelper.makeFinder();
			Collection<LogicalDevice> ldC = f
			        .findById(
			                LogicalDevice.class,
			                ((oracle.communications.inventory.xmlbeans.LogicalDeviceType) resource)
			                        .getId());
			ldManager.deleteLogicalDevice(ldC);
			f.close();
		}

		if (resource instanceof oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType) {
			LogicalDeviceAccountManager ldManager = PersistenceHelper
			        .makeLogicalDeviceAccountManager();
			Finder f = PersistenceHelper.makeFinder();
			Collection<LogicalDeviceAccount> ldaC = f
			        .findById(
			                LogicalDeviceAccount.class,
			                ((oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType) resource)
			                        .getId());
			ldManager.deleteLogicalDeviceAccounts(ldaC);
			f.close();
		}
	}

	protected BusinessInteractionType buildCreateCaptureChildResourceInteractionBiInput2(
	        ExtensionPointContext extensionPointContext, String mappingFile,
	        String biSpecification, List<NameValueActionParam> listnvapcpe)
	        throws ValidationException {
		try {

			{
				BusinessInteractionItemType parentBiItemType = (BusinessInteractionItemType) extensionPointContext
				        .getArguments()[1];
				List<ParameterType> parentBiItemTypeParamsList = parentBiItemType
				        .getParameterList();

				CommonManager commonManager = CommonHelper.makeCommonManager();
				BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
				        .findAndValidateSpecification(biSpecification);

				if (biSpec == null)
					log.validationException("c2a.couldNotFind",
					        new java.lang.IllegalArgumentException(),
					        biSpecification);

				SpecificationType biSpecType = EntitySerializationUtils
				        .toSpecification(biSpec);

				// Add business interaction parameters.
				BusinessInteractionType biType = BusinessInteractionType.Factory
				        .newInstance();
				biType.addNewHeader();

				biType.getHeader().setAction(
				        BusinessInteractionActionEnum.CREATE);
				biType.getHeader().setSpecification(biSpecType);
				biType.getHeader().setId("");
				biType.getHeader().addNewExternalIdentity();
				biType.getHeader().getExternalIdentity()
				        .setExternalObjectId("");
				// Name is required.
				biType.getHeader().setName(
				        "AutoDesignResourceBusinessInteraction");
				biType.getHeader().setDescription(
				        "AutoDesignResourceBusinessInteraction description ");
				Calendar cal = new GregorianCalendar();
				Date effDate = new Date();
				cal.setTime(effDate);
				biType.getHeader().setEffectiveDate(cal);

				// Add the Bi Item for the resource entity.
				BusinessInteractionItemType childResourceItem = BusinessInteractionItemType.Factory
				        .newInstance();
				childResourceItem
				        .setAction(BusinessInteractionItemActionEnum.ADD);
				String childSpecification = DesignHelper
				        .childSpecificationMapParser(mappingFile);
				String childEntityType = DesignHelper
				        .childEntityTypeMapParser(mappingFile);

				if (childEntityType.equals("LogicalDevice")) {
					LogicalDeviceType childResourceParam = LogicalDeviceType.Factory
					        .newInstance();
					LogicalDeviceSpecification childSpec = (LogicalDeviceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpecification);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId("");
					childResourceParam.setEntityNote("create");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("LogicalDeviceAccount")) {
					LogicalDeviceAccountType childResourceParam = LogicalDeviceAccountType.Factory
					        .newInstance();
					LogicalDeviceAccountSpecification childSpec = (LogicalDeviceAccountSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpecification);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId("");
					childResourceParam.setEntityNote("create");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("DeviceInterface")) {
					DeviceInterfaceType childResourceParam = DeviceInterfaceType.Factory
					        .newInstance();
					DeviceInterfaceSpecification childSpec = (DeviceInterfaceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpecification);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId("");
					childResourceParam.setEntityNote("create");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("Service")) {
					ServiceType childResourceParam = ServiceType.Factory
					        .newInstance();
					ServiceSpecification childSpec = (ServiceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpecification);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId("");
					childResourceParam.setEntityNote("create");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					childResourceItem.setEntity(childResourceParam);
				} else {
					this.validExcpt("ChildEntityType not recognized: "
					        + childEntityType);
				}

				ArrayList<Parameter> myBiParamsList = DesignHelper
				        .BiParamMapParser(mappingFile);

				Iterator<Parameter> it = myBiParamsList.iterator();
				while (it.hasNext()) {
					boolean found = false;
					Parameter mvParam = (Parameter) it.next();
					org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
					        .newInstance();
					if (mvParam.getParentBiParameterName() != null) {
						Iterator<ParameterType> itt = parentBiItemTypeParamsList
						        .iterator();
						while (itt.hasNext()) {
							ParameterType p = (ParameterType) itt.next();
							// org.apache.xmlbeans.XmlObject xmlObject =
							// p.getValue();
							if (p.getName().equals(
							        mvParam.getParentBiParameterName()
							                .toString())) {
								EntityUtils.validateStringType(p,
								        mvParam.getParentBiParameterName());
								xmlString.setStringValue(EntityUtils
								        .getStringType(p, mvParam
								                .getParentBiParameterName()));
								found = true;
							}
						}
					}
					if (mvParam.getDefaultValue() != null) {
						xmlString.setStringValue(mvParam.getDefaultValue());
						found = true;
					}
					if (found) {
						ParameterType paramParamType = childResourceItem
						        .addNewParameter();
						paramParamType.setName(mvParam.getName());
						paramParamType.setValue(xmlString);
					}
				}

				// new code
				if (listnvapcpe != null && listnvapcpe.size() > 0) {
					Iterator<NameValueActionParam> nvapit = listnvapcpe
					        .iterator();
					while (nvapit.hasNext()) {
						NameValueActionParam nvap = (NameValueActionParam) nvapit
						        .next();
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						ParameterType paramParamType = childResourceItem
						        .addNewParameter();
						paramParamType.setName(nvap.getName());
						xmlString.setStringValue(nvap.getValue());
						paramParamType.setValue(xmlString);
					}
				}
				// finish new code

				BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
				items[0] = childResourceItem;
				biType.addNewBody();
				biType.getBody().setItemArray(items);

				return biType;
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	// TODO buildModifyCaptureChildResourceInteractionBiInput2

	protected BusinessInteractionType buildModifyCaptureChildResourceInteractionBiInput2(
	        ExtensionPointContext extensionPointContext, String mappingFile,
	        ConsumableResource res, String biSpecification,
	        List<NameValueActionParam> listnvapcpe) throws ValidationException {
		try {

			{
				BusinessInteractionItemType parentBiItemType = (BusinessInteractionItemType) extensionPointContext
				        .getArguments()[1];
				List<ParameterType> parentBiItemTypeParamsList = parentBiItemType
				        .getParameterList();

				CommonManager commonManager = CommonHelper.makeCommonManager();
				BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
				        .findAndValidateSpecification(biSpecification);

				if (biSpec == null)
					log.validationException("c2a.couldNotFind",
					        new java.lang.IllegalArgumentException(),
					        biSpecification);

				SpecificationType biSpecType = EntitySerializationUtils
				        .toSpecification(biSpec);

				// Add business interaction parameters.
				BusinessInteractionType biType = BusinessInteractionType.Factory
				        .newInstance();
				biType.addNewHeader();

				biType.getHeader().setAction(
				        BusinessInteractionActionEnum.CREATE);
				biType.getHeader().setSpecification(biSpecType);
				biType.getHeader().setId("");
				biType.getHeader().addNewExternalIdentity();
				biType.getHeader().getExternalIdentity()
				        .setExternalObjectId("");
				// Name is required.
				biType.getHeader().setName(
				        "AutoDesignResourceBusinessInteraction");
				biType.getHeader().setDescription(
				        "AutoDesignResourceBusinessInteraction description ");
				Calendar cal = new GregorianCalendar();
				Date effDate = new Date();
				cal.setTime(effDate);
				biType.getHeader().setEffectiveDate(cal);

				// Add the Bi Item for the resource entity.
				BusinessInteractionItemType childResourceItem = BusinessInteractionItemType.Factory
				        .newInstance();
				childResourceItem
				        .setAction(BusinessInteractionItemActionEnum.ADD);
				String childSpecification = DesignHelper
				        .childSpecificationMapParser(mappingFile);
				String childEntityType = DesignHelper
				        .childEntityTypeMapParser(mappingFile);

				if (childEntityType.equals("LogicalDevice")) {
					LogicalDeviceType childResourceParam = LogicalDeviceType.Factory
					        .newInstance();
					LogicalDeviceSpecification childSpec = (LogicalDeviceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpec);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.set.setAction(action);
					childResourceParam.setId(res.getId());
					childResourceParam.setEntityNote("change");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					// childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("LogicalDeviceAccount")) {
					LogicalDeviceAccountType childResourceParam = LogicalDeviceAccountType.Factory
					        .newInstance();
					LogicalDeviceAccountSpecification childSpec = (LogicalDeviceAccountSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpec);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId(res.getId());
					childResourceParam.setEntityNote("change");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					// childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("DeviceInterface")) {
					DeviceInterfaceType childResourceParam = DeviceInterfaceType.Factory
					        .newInstance();
					DeviceInterfaceSpecification childSpec = (DeviceInterfaceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpec);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId(res.getId());
					childResourceParam.setEntityNote("change");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					// childResourceItem.setEntity(childResourceParam);
				} else if (childEntityType.equals("Service")) {
					ServiceType childResourceParam = ServiceType.Factory
					        .newInstance();
					ServiceSpecification childSpec = (ServiceSpecification) commonManager
					        .findAndValidateSpecification(childSpecification);
					if (childSpec == null)
						log.validationException("c2a.couldNotFind",
						        new java.lang.IllegalArgumentException(),
						        childSpec);
					SpecificationType childResourceSpec = EntitySerializationUtils
					        .toSpecification(childSpec);
					childResourceParam.setSpecification(childResourceSpec);
					// childResourceParam.setAction("create");
					childResourceParam.setId(res.getId());
					childResourceParam.setEntityNote("change");
					// childResourceParam.addNewExternalIdentity();
					// childResourceParam.getExternalIdentity().setExternalObjectId("");
					childResourceParam.setName(childSpecification
					        + " created by autodesign");
					childResourceParam.setDescription(childSpecification
					        + " created by autodesign");
					childResourceItem.setEntity(childResourceParam);
					// childResourceItem.setEntity(childResourceParam);
				} else {
					this.validExcpt("ChildEntityType not recognized: "
					        + childEntityType);
				}
				ArrayList<Parameter> myBiParamsList = DesignHelper
				        .BiParamMapParser(mappingFile);

				Iterator<Parameter> it = myBiParamsList.iterator();
				while (it.hasNext()) {
					boolean found = false;
					Parameter mvParam = (Parameter) it.next();
					org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
					        .newInstance();
					if (mvParam.getParentBiParameterName() != null) {
						Iterator<ParameterType> itt = parentBiItemTypeParamsList
						        .iterator();
						while (itt.hasNext()) {
							ParameterType p = (ParameterType) itt.next();
							// org.apache.xmlbeans.XmlObject xmlObject =
							// p.getValue();
							if (p.getName().equals(
							        mvParam.getParentBiParameterName()
							                .toString())) {
								EntityUtils.validateStringType(p,
								        mvParam.getParentBiParameterName());
								xmlString.setStringValue(EntityUtils
								        .getStringType(p, mvParam
								                .getParentBiParameterName()));
								found = true;
							}
						}
					}
					if (mvParam.getDefaultValue() != null) {
						xmlString.setStringValue(mvParam.getDefaultValue());
						found = true;
					}
					if (found) {
						ParameterType paramParamType = childResourceItem
						        .addNewParameter();
						paramParamType.setName(mvParam.getName());
						paramParamType.setValue(xmlString);
					}
				}

				// new code
				if (listnvapcpe != null && listnvapcpe.size() > 0) {
					Iterator<NameValueActionParam> nvapit = listnvapcpe
					        .iterator();
					while (nvapit.hasNext()) {
						NameValueActionParam nvap = (NameValueActionParam) nvapit
						        .next();
						org.apache.xmlbeans.XmlString xmlString = org.apache.xmlbeans.XmlString.Factory
						        .newInstance();
						ParameterType paramParamType = childResourceItem
						        .addNewParameter();
						paramParamType.setName(nvap.getName());
						xmlString.setStringValue(nvap.getValue());
						paramParamType.setValue(xmlString);
					}
				}
				// finish new code

				BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
				items[0] = childResourceItem;
				biType.addNewBody();
				biType.getBody().setItemArray(items);

				return biType;
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public BusinessInteractionItemType getBiItemType(BusinessInteraction bi)
	        throws ValidationException {
		BusinessInteractionItemType itemType = null;
		List<BusinessInteractionAttachment> biAttachmentList = bi
		        .getAttachments();
		BusinessInteractionAttachment biAttachment = null;
		if (!Utils.isEmpty(biAttachmentList)) {
			for (int i=biAttachmentList.size() - 1; i>=0; i--) {
				biAttachment = biAttachmentList.get(i);
				if (biAttachment.getName().contains("Technical Action")) {
					biAttachment = null;
				} else break;
			}
		}
		if (biAttachment == null) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		oracle.communications.inventory.xmlbeans.InteractionDocument doc = null;
		try {
			// Assuming that the content is always XML String for now.
			String textualContent = biAttachment.convertContentToString();
			doc = oracle.communications.inventory.xmlbeans.InteractionDocument.Factory
			        .parse(textualContent);
		} catch (Exception e) {
			log.error("businessInteraction.entityAttachmentError", e,
			        bi.getId());
			throw new ValidationException(e);
		}
		oracle.communications.inventory.xmlbeans.BusinessInteractionType bitype = doc
		        .getInteraction();
		oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = bitype
		        .getBody();
		List<oracle.communications.inventory.xmlbeans.BusinessInteractionItemType> items = body
		        .getItemList();
		if (items.isEmpty()) {
			this.validExcpt("Business Interaction has no items " + bi.getId());
		}
		itemType = items.get(0);
		if (items.size() == 0) {
			throw new ValidationException(new String(
			        MessageResource.getMessage(
			                "businessInteraction.entityAttachmentError",
			                bi.getDisplayInfo())));
		}
		return itemType;
	}

	public DeviceInterface findDeviceInterfaceById(String ifaceId)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<DeviceInterface> ifaceList = f.findById(
			        DeviceInterface.class, ifaceId);
			if (ifaceList.isEmpty())
				return null;
			DeviceInterface iface = ifaceList.iterator().next();
			return iface;
		} finally {
			f.close();
		}
	}

	public DeviceInterface findDeviceInterfaceByName(String ifaceName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<DeviceInterface> ifaceList = f.findByName(
			        DeviceInterface.class, ifaceName);
			if (ifaceList.isEmpty())
				return null;
			DeviceInterface iface = ifaceList.iterator().next();
			return iface;
		} finally {
			f.close();
		}
	}

	public Service findServiceById(String serviceId) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Service> serviceList = f.findById(Service.class,
			        serviceId);
			if (serviceList.isEmpty())
				return null;
			Service service = serviceList.iterator().next();
			return service;
		} finally {
			f.close();
		}
	}

	public Service findServiceByName(String serviceName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Service> serviceList = f.findByName(Service.class,
			        serviceName);
			if (serviceList.isEmpty())
				return null;
			Service service = serviceList.iterator().next();
			return service;
		} finally {
			f.close();
		}
	}

	public LogicalDevice findLogicalDeviceById(String logicalDeviceId)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<LogicalDevice> logicalDeviceList = f.findById(
			        LogicalDevice.class, logicalDeviceId);
			if (logicalDeviceList.isEmpty())
				return null;
			LogicalDevice ld = logicalDeviceList.iterator().next();
			return ld;
		} finally {
			f.close();
		}
	}

	public LogicalDevice findLogicalDeviceByName(String logicalDeviceName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<LogicalDevice> logicalDeviceList = f.findByName(
			        LogicalDevice.class, logicalDeviceName);
			if (logicalDeviceList.isEmpty())
				return null;
			LogicalDevice ld = logicalDeviceList.iterator().next();
			return ld;
		} finally {
			f.close();
		}
	}

	public CustomObject findCustomObjectById(String logicalDeviceId)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<CustomObject> customObjectList = f.findById(
			        CustomObject.class, logicalDeviceId);
			if (customObjectList.isEmpty())
				return null;
			CustomObject co = customObjectList.iterator().next();
			return co;
		} finally {
			f.close();
		}
	}

	public CustomObject findCustomObjectByName(String coName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<CustomObject> customObjectList = f.findByName(
			        CustomObject.class, coName);
			if (customObjectList.isEmpty())
				return null;
			CustomObject co = customObjectList.iterator().next();
			return co;
		} finally {
			f.close();
		}
	}

	public Network findNetworkByName(String ntName) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Network> ntList = f.findByName(Network.class, ntName);
			if (ntList.isEmpty())
				return null;
			Network nt = (Network) ntList.iterator().next();
			return nt;
		} finally {
			f.close();
		}
	}

	public Network findNetworkById(String ntId) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Network> ntList = f.findById(Network.class, ntId);
			if (ntList.isEmpty())
				return null;
			Network nt = ntList.iterator().next();
			return nt;
		} finally {
			f.close();
		}
	}

	public InventoryGroup findInventoryGroupByName(String ivgName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<InventoryGroup> inventoryGroupList = f.findByName(
			        InventoryGroup.class, ivgName);
			if (inventoryGroupList.isEmpty())
				return null;
			InventoryGroup ivg = inventoryGroupList.iterator().next();
			return ivg;
		} finally {
			f.close();
		}
	}

	public GeographicPlace findGeographicPlaceByName(String geoName)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<GeographicPlace> geoList = f.findByName(
			        GeographicPlace.class, geoName);
			if (geoList.isEmpty())
				return null;
			GeographicPlace geo = geoList.iterator().next();
			return geo;
		} finally {
			f.close();
		}
	}

	public Pipe findPipeById(String pipeId) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Pipe> pipeList = f.findById(Pipe.class, pipeId);
			if (pipeList.isEmpty())
				return null;
			Pipe ld = pipeList.iterator().next();
			return ld;
		} finally {
			f.close();
		}
	}

	public Pipe findPipeByName(String pipeName) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<Pipe> pipeList = f.findByName(Pipe.class, pipeName);
			if (pipeList.isEmpty())
				return null;
			Pipe ld = pipeList.iterator().next();
			return ld;
		} finally {
			f.close();
		}
	}

	public LogicalDeviceAccount findLogicalDeviceAccountById(
	        String logicalDeviceAccountId) throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			Collection<LogicalDeviceAccount> logicalDeviceAccountList = f
			        .findById(LogicalDeviceAccount.class,
			                logicalDeviceAccountId);
			if (logicalDeviceAccountList.isEmpty())
				return null;
			LogicalDeviceAccount lda = logicalDeviceAccountList.iterator()
			        .next();
			return lda;
		} finally {
			f.close();
		}
	}

	public BusinessInteraction getParentBi(ServiceConfigurationVersion scv)
	        throws ValidationException {
		// Flushing to see if this fixes the disconnect usecase.
		this.flushTransaction();

		BusinessInteractionManager biMgr = PersistenceHelper
		        .makeBusinessInteractionManager();
		List<BusinessInteractionItem> parentItems = biMgr
		        .findBusinessInteractionItemsForItem((Persistent) scv);
		log.debug("", new Object[] { "size of parentItems for scv is "
		        + parentItems.size() });

		BusinessInteraction parentBi = null;
		if (!Utils.isEmpty(parentItems))
			parentBi = parentItems.get(0).getBusinessInteraction();

		if (parentBi == null)
			// log.validationException("c2a.parentBiNull", new
			// java.lang.IllegalArgumentException());
			log.debug("c2a.parentBiNull");

		return parentBi;
	}

	public Service cascadeCompleteServiceConfiguration(
	        ServiceConfigurationVersion scv) throws ValidationException {
		oracle.communications.inventory.api.service.ServiceManager svcMgr = PersistenceHelper
		        .makeServiceManager();
		BusinessInteraction bi = this.getParentBi(scv);

		Service svc = scv.getService();
		log.debug("",
		        new Object[] { "cascadeCompleteServiceConfiguration: service="
		                + svc });
		String serviceAction = this.getBiItemType(bi).getService().getAction();
		log.debug("",
		        new Object[] { "cascadeCompleteServiceConfiguration: action="
		                + serviceAction });

		if (serviceAction.equalsIgnoreCase("suspend")) {
			svcMgr.suspendService(svc);
		} else if (serviceAction.equalsIgnoreCase("resume")) {
			svcMgr.resumeService(svc);
		}
		cascadeCompleteChildServices(serviceAction, scv);
		return svc;
	}

	private void cascadeCompleteChildServices(String serviceAction,
	        ServiceConfigurationVersion scv) throws ValidationException {
		List<ServiceConfigurationItem> items = scv.getConfigItems();
		for (ServiceConfigurationItem item : items) {
			Persistent assignment = item.getAssignment();
			if (assignment != null && assignment instanceof ServiceAssignment) {
				ServiceAssignment serviceAssignment = (ServiceAssignment) assignment;
				Service service = serviceAssignment.getService();
				List<ServiceConfigurationVersion> configs = service
				        .getConfigurations();
				if (configs == null || configs.size() == 0) {
					oracle.communications.inventory.api.service.ServiceManager svcMgr = PersistenceHelper
					        .makeServiceManager();
					svcMgr.completeService(service);
				}
			}
		}
	}
	
	public Service getServiceFromConfigItem(ServiceConfigurationVersion scv, String ciName) {
		List<ServiceConfigurationItem> items = scv.getConfigItems();
		Service service = null;
		for(ServiceConfigurationItem sci : items) {
			if(sci.getSpecification().toString().equalsIgnoreCase(ciName)){
				
				Persistent assignment = sci.getAssignment();
				if (assignment != null && assignment instanceof ServiceAssignment) {
					ServiceAssignment serviceAssignment = (ServiceAssignment) assignment;
					service = serviceAssignment.getService();
					return service;
				}
			}
		}
		return Utils.checkNull(service) ? null : service;
	}

	public Service getAssignedService(ServiceConfigurationVersion scv,
	        String configItemName) throws ValidationException {
		try {
			ServiceManager serviceManager = CommonHelper.makeServiceManager();
			List<ServiceConfigurationItem> configItems = serviceManager
			        .findServiceConfigItemByName(scv, configItemName);
			if (configItems.isEmpty()) {
				this.validExcpt("Config item not found " + configItemName);
			}
			ServiceConfigurationItem configItem = configItems.get(0);

			Service svc = null;
			Persistent ps = configItem.getToEntity();
			if (ps != null) {
				if (ps instanceof ServiceAssignment) {
					svc = ((ServiceAssignment) ps).getService();
				}
			}
			return svc;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public ConsumableResource getAssignedEntity(
	        ServiceConfigurationVersion scv, String configItemName)
	        throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(scv, configItemName);
		if (configItems.isEmpty()) {
			this.validExcpt("Config item not found " + configItemName);
		}
		ServiceConfigurationItem configItem = configItems.get(0);

		Persistent ps = configItem.getToEntity();
		ConsumableResource p = null;
		if (ps != null && ps instanceof Consumer) {
			p = ((Consumer) ps).getResource();
		}
		return p;
	}

	public ConfigurationReferenceEnabled getReferencedEntity(
	        ServiceConfigurationVersion scv, String configItemName)
	        throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(scv, configItemName);
		if (configItems.isEmpty()) {
			this.validExcpt("Config item not found " + configItemName);
		}
		ServiceConfigurationItem configItem = configItems.get(0);
           
		Persistent reference = configItem.getReference();
		ConfigurationReferenceEnabled resource = null;
		if (reference != null && reference instanceof ConfigurationReference) {
			resource = ((ConfigurationReference) reference).getReferenced();
		}
		return resource;
	}
	
	public GeographicAddress getReferencedGeographicAddress(
	        ServiceConfigurationVersion scv, String configItemName)
	        throws ValidationException {
		GeographicAddress p = null;
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(scv, configItemName);
		if (configItems.isEmpty()) {
			this.validExcpt("Config item not found " + configItemName);
		}
		ServiceConfigurationItem configItem = configItems.get(0);

		Persistent ps = null;
		ps = configItem.getReference();
		if (ps != null) {
			if (ps instanceof GeographicAddressConfigurationReference) {
				p = (GeographicAddress) ((GeographicAddressConfigurationReference) ps)
				        .getReferenced();
			}
		}

		return p;
	}

	public Service getServiceForServiceConfigurationVersion(
	        ServiceConfigurationVersion scv) throws ValidationException {
		Service svc = null;
		svc = scv.getService();

		return svc;
	}

	/**
	 * Get the Specification for the entity that is assigned or referenced at a
	 * configuration item.
	 * 
	 * @param scv
	 *            service configuration
	 * @param item
	 *            name of the configuration item
	 * @return Specification of the entity assigned or referenced
	 * @throws ValidationException
	 */
	public Specification getSpecificationForConfigurationItem(
	        ServiceConfigurationVersion scv, String item)
	        throws ValidationException {
		for (ServiceConfigurationItem i : scv.getConfigItems()) {
			if (item.equals(i.getName())) {
				Persistent assignment = i.getAssignment();
				if (assignment != null) {
					ConsumableResource resource = ((Assignment) assignment)
					        .getResource();
					return resource != null ? resource.getSpecification()
					        : null;
				}
				Persistent reference = i.getReference();
				if (reference != null) {
					ConfigurationReferenceEnabled resource = ((ConfigurationReference) reference)
					        .getReferenced();
					return resource != null ? resource.getSpecification()
					        : null;
				}
			}
		}
		return null;
	}

	public void createTnRangeWithIvg(String rangeFrom, String rangeTo,
	        String TnSpec, InventoryGroup ivg) throws ValidationException {
		// TODO - this is not finished!
		InventoryGroup ivg1 = null;
		TelephoneNumberManager tnMgr = PersistenceHelper
		        .makeTelephoneNumberManager();
		InventoryGroupManager ivgMgr = PersistenceHelper
		        .makeInventoryGroupManager();
		CommonManager commonManager = CommonHelper.makeCommonManager();
		TelephoneNumber tn = tnMgr.makeTelephoneNumber();
		TelephoneNumberSpecification tnSpecSpec = (TelephoneNumberSpecification) commonManager
		        .findAndValidateSpecification(TnSpec);
		tn.setSpecification(tnSpecSpec);

		Long intTo = Long.valueOf(rangeTo);
		Long intFrom = Long.valueOf(rangeFrom);
		for (Long i = intFrom; i <= intTo; i++) {
			tn = tnMgr.getExistingTelephoneNumbers(String.valueOf(i));
			if (tn == null) {
				List<oracle.communications.inventory.api.entity.TelephoneNumber> tnList = tnMgr
				        .createTelephoneNumbers(String.valueOf(i),
				                String.valueOf(i), tn);
				Iterator<TelephoneNumber> itt = tnList.iterator();
				while (itt.hasNext()) {
					TelephoneNumber tn1 = itt.next();
					ivg1 = ivgMgr.associatePersistentToInventoryGroup(ivg, tn1);
				}
			} else {
				ivg1 = ivgMgr.associatePersistentToInventoryGroup(ivg, tn);
			}
		}

		/**
		 * java.util.List<oracle.communications.inventory.api.entity.
		 * InventoryGroup> inventoryGroups = new
		 * java.util.ArrayList<oracle.communications
		 * .inventory.api.entity.InventoryGroup>();
		 * java.util.List<oracle.communications
		 * .inventory.api.entity.TNCondition> conditionList = new
		 * java.util.ArrayList
		 * <oracle.communications.inventory.api.entity.TNCondition>();
		 * 
		 * List<oracle.communications.inventory.api.entity.TelephoneNumber>
		 * tnList = tnMgr.createTelephoneNumbers(rangeFrom, rangeTo, tn,
		 * (Set<InventoryGroup>) inventoryGroups, conditionList);
		 **/
	}

	public InventoryGroup getResourceIvgBySpec(ConsumableResource tn,
	        String parentIvgSpec) throws ValidationException {
		InventoryGroup ivg = null;
		InventoryGroup ivg1 = null;
		InventoryGroupManager ivgMgr = PersistenceHelper
		        .makeInventoryGroupManager();
		List<InventoryGroup> ivgList = ivgMgr.getInventoryGroupsForResource(tn);
		if (!ivgList.isEmpty()) {
			Iterator<InventoryGroup> itt = ivgList.iterator();
			while (itt.hasNext()) {
				ivg1 = itt.next();
				if (ivg1.getSpecification().getName().equals(parentIvgSpec)) {
					ivg = ivg1;
				}
			}
		}
		return ivg;
	}

	public List<Pipe> getIvgPipesBySpec(InventoryGroup ivg, String spec)
	        throws ValidationException {
		// TODO not tested.not in manager.
		List<Pipe> result = new java.util.ArrayList<Pipe>();
		PipeManager pipeMgr = PersistenceHelper.makePipeManager();
		PipeSearchCriteria pscr = pipeMgr.makePipeSearchCriteria();
		CriteriaItem critItem = pscr.makeCriteriaItem();
		critItem.setValue(ivg.getName());
		pscr.setInventoryGroupName(critItem);
		List<Pipe> result1 = pipeMgr.findPipes(pscr);
		Pipe p = null;
		Iterator<Pipe> itt = result1.iterator();
		while (itt.hasNext()) {
			p = itt.next();
			if (p.getSpecification().getName().equals(spec))
				result.add(p);
		}
		return result;
	}

	public InventoryGroup getIvgParentIvgBySpec(InventoryGroup ivg,
	        String parentIvgSpec) throws ValidationException {
		InvGroupRel ivgRel = null;
		InventoryGroupManager ivgMgr = PersistenceHelper
		        .makeInventoryGroupManager();
		List<InvGroupRel> ivgRelList = ivgMgr.getParentsForInventoryGroup(ivg);

		if (!ivgRelList.isEmpty()) {
			Iterator<InvGroupRel> itt = ivgRelList.iterator();
			while (itt.hasNext()) {
				ivgRel = itt.next();
				if (ivgRel.getParentInvGroup().getSpecification().getName()
				        .equals(parentIvgSpec)) {
					ivg = ivgRel.getParentInvGroup();
				}
			}
		}
		return ivg;
	}

	public void validExcpt(String message) throws ValidationException {
		throw new ValidationException(MessageResource.getMessage("", message));
	}

	public void applyEntityCharToServiceConfigItem(CharacteristicExtensible E,
	        String charName, String configItemName,
	        ServiceConfigurationVersion svcConVers) throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		List<ServiceConfigurationItem> configItems = serviceManager
		        .findServiceConfigItemByName(svcConVers, configItemName);
		if (Utils.isEmpty(configItems)) {
			this.validExcpt("Error in applyEntityCharsToServiceConfigItem. Could not find config item "
			        + configItemName
			        + " in serviceconfiguration version "
			        + svcConVers);
		}
		ServiceConfigurationItem configItem = configItems.get(0);

		String charValue = this.getCharacteristicForEntity(E, charName);
		this.addUpdateConfigItemCharacteristic(svcConVers, configItem,
		        charName, charValue);
	}

	public void applyProfileParameters(CharacteristicExtensible E,
	        ServiceConfigurationItem propertiesItem) throws ValidationException {
		log.debug("Entered applyProfileParameters");
		String paramName = null;
		String paramValue = null;

		CommonManager commonManager = CommonHelper.makeCommonManager();
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(propertiesItem.getConfiguration()
		                .getClass());

		Set<CharValue> characts = (Set) E.getCharacteristics();
		for (CharValue charVale : characts) {
			paramName = charVale.getName();
			paramValue = charVale.getValue();

			// TODO: mapServiceBusinessInteractionParameters - only process
			// String type params
			// TODO: mapServiceBusinessInteractionParameters - do we need an
			// "if paramValue="REMOVE" then remove char, else process as add/update"
			// ??
			Set<ServiceConfigurationItemCharacteristic> chars = propertiesItem
			        .getCharacteristics();
			if (!Utils.isEmpty(chars)) {
				log.info("chars input size" + chars.size());
				Integer i = 0;
				for (CharValue characteristic : chars) {
					// CharacteristicSpecification charSpec =
					// characteristic.getCharacteristicSpecification();
					if (characteristic.getName().equals(paramName)) {
						characteristic.setValue(paramValue);
						i = 1;
						log.info("updating" + paramName);
					}
				}

				if (i == 0) {
					log.info("adding" + paramName);
					CharValue charvalue = commonManager.makeCharValue(
					        (CharacteristicExtensible) propertiesItem,
					        paramName, paramValue);
					chars.add((ServiceConfigurationItemCharacteristic) charvalue);
				}
			} else {
				log.info("adding 2" + paramName);
				CharValue charvalue = commonManager.makeCharValue(
				        (CharacteristicExtensible) propertiesItem, paramName,
				        paramValue);
				chars.add((ServiceConfigurationItemCharacteristic) charvalue);
			}

			propertiesItem.setCharacteristics(chars);
			log.info("chars output size" + chars.size());
			Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
			updateList.add(propertiesItem);
			configManager.updateConfigurationItem(updateList);
		}
	}

	public void applyEntityParametersToConfigItem(CharacteristicExtensible E,
	        List<String> charList, ServiceConfigurationItem propertiesItem)
	        throws ValidationException {
		// TODO this does not work, needs to be checked

		log.debug("Entered applyEntityParametersToConfigItem");
		String paramName = null;
		String paramValue = null;

		CommonManager commonManager = CommonHelper.makeCommonManager();
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(propertiesItem.getConfiguration()
		                .getClass());

		Iterator<String> itt = charList.iterator();
		while (itt.hasNext()) {
			String charactName = (String) itt.next();
			Set<CharValue> characts = (Set<CharValue>) E.getCharacteristics();
			for (CharValue charVale : characts) {
				paramName = charVale.getName();
				paramValue = charVale.getValue();
				if (charactName.equals(paramName)) {
					Set<ServiceConfigurationItemCharacteristic> chars = propertiesItem
					        .getCharacteristics();
					if (!Utils.isEmpty(chars)) {
						log.info("chars input size" + chars.size());
						Integer i = 0;
						for (CharValue characteristic : chars) {
							// CharacteristicSpecification charSpec =
							// characteristic.getCharacteristicSpecification();
							if (characteristic.getName().equals(paramName)) {
								characteristic.setValue(paramValue);
								i = 1;
								log.info("updating" + paramName);
							}
						}

						if (i == 0) {
							log.info("adding" + paramName);
							CharValue charvalue = commonManager.makeCharValue(
							        (CharacteristicExtensible) propertiesItem,
							        paramName, paramValue);
							chars.add((ServiceConfigurationItemCharacteristic) charvalue);
						}
					} else {
						log.info("adding 2" + paramName);
						CharValue charvalue = commonManager.makeCharValue(
						        (CharacteristicExtensible) propertiesItem,
						        paramName, paramValue);
						chars.add((ServiceConfigurationItemCharacteristic) charvalue);
					}

					propertiesItem.setCharacteristics(chars);
					log.info("chars output size" + chars.size());
					Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
					updateList.add(propertiesItem);
					configManager.updateConfigurationItem(updateList);
				}
			}
		}
	}

	public void copyConfigItemCharValuesByPrefix(
	        ServiceConfigurationItem sourceItem,
	        ServiceConfigurationItem targetItem, String prefix)
	        throws ValidationException {
		log.debug("Entered applyProfileParameters");
		String paramName = null;
		String paramValue = null;

		CommonManager commonManager = CommonHelper.makeCommonManager();
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(targetItem.getConfiguration()
		                .getClass());

		Set<ServiceConfigurationItemCharacteristic> characts = sourceItem
		        .getCharacteristics();
		for (CharValue charVale : characts) {
			paramName = charVale.getName();
			if (paramName != null && paramName.startsWith(prefix)) {
				paramValue = charVale.getValue();

				// TODO: mapServiceBusinessInteractionParameters - only process
				// String type params
				// TODO: mapServiceBusinessInteractionParameters - do we need an
				// "if paramValue="REMOVE" then remove char, else process as add/update"
				// ??
				Set<ServiceConfigurationItemCharacteristic> chars = targetItem
				        .getCharacteristics();
				if (!Utils.isEmpty(chars)) {
					log.info("chars input size" + chars.size());
					Integer i = 0;
					for (CharValue characteristic : chars) {
						// CharacteristicSpecification charSpec =
						// characteristic.getCharacteristicSpecification();
						if (characteristic.getName().equals(paramName)) {
							characteristic.setValue(paramValue);
							i = 1;
							log.info("updating" + paramName);
						}
					}

					if (i == 0) {
						log.info("adding" + paramName);
						CharValue charvalue = commonManager.makeCharValue(
						        (CharacteristicExtensible) targetItem,
						        paramName, paramValue);
						chars.add((ServiceConfigurationItemCharacteristic) charvalue);
					}
				} else {
					log.info("adding 2" + paramName);
					CharValue charvalue = commonManager.makeCharValue(
					        (CharacteristicExtensible) targetItem, paramName,
					        paramValue);
					chars.add((ServiceConfigurationItemCharacteristic) charvalue);
				}

				targetItem.setCharacteristics(chars);
				log.info("chars output size" + chars.size());
				Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
				updateList.add(targetItem);
				configManager.updateConfigurationItem(updateList);
			}
		}

	}

	public void setConfigItemCharValuesByPrefix(String value,
	        ServiceConfigurationItem targetItem, String prefix)
	        throws ValidationException {
		log.debug("Entered setConfigItemCharValuesByPrefix");
		String paramName = null;
		BaseConfigurationManager configManager = PersistenceHelper
		        .makeConfigurationManager(targetItem.getConfiguration()
		                .getClass());
		Set<ServiceConfigurationItemCharacteristic> chars = targetItem
		        .getCharacteristics();
		if (!Utils.isEmpty(chars)) {
			log.info("chars input size" + chars.size());

			for (CharValue characteristic : chars) {
				paramName = characteristic.getName();
				if (paramName != null && paramName.startsWith(prefix))
					characteristic.setValue(value);
				log.info("updating" + paramName);
			}
		}

		targetItem.setCharacteristics(chars);
		log.info("chars output size" + chars.size());
		Collection<InventoryConfigurationItem> updateList = new ArrayList<InventoryConfigurationItem>();
		updateList.add(targetItem);
		configManager.updateConfigurationItem(updateList);
	}

	public List<NameValueActionParam> updateNvapWithConfigItemProps(
	        ServiceConfigurationItem propertiesItem,
	        List<NameValueActionParam> listnvap, String prefix)
	        throws ValidationException {
		NameValueActionParam svcnvap = null;
		Set<ServiceConfigurationItemCharacteristic> chars = propertiesItem
		        .getCharacteristics();
		if (!Utils.isEmpty(chars)) {
			log.info("propchars input size" + chars.size());
			for (CharValue characteristic : chars) {
				if (characteristic.getValue() != null
				        && characteristic.getName().startsWith(prefix)) {
					svcnvap = new NameValueActionParam(
					        characteristic.getName(), characteristic.getValue());
					listnvap.add(svcnvap);
				}
			}
		}
		return listnvap;
	}

	public List<CustomObject> findCustomObjectsBySpecAndIvg(
	        InventoryGroup group, String specName) throws ValidationException {
		try {
			if (group == null)
				return null;

			List<CustomObject> result = new java.util.ArrayList<CustomObject>();

			InventoryGroupManager invGroupMgr = PersistenceHelper
			        .makeInventoryGroupManager();
			InventoryGroupEntitySearchCriteria criteria = invGroupMgr
			        .makeInventoryGroupEntitySearchCriteria();

			criteria.setInventoryGroup(group);
			criteria.setEntityClass(CustomObject.class);
			criteria.setRange(0, 10);

			Collection<? extends GroupEnabled> grpRef = invGroupMgr
			        .findEntitiesForInventoryGroup(criteria);

			for (GroupEnabled e : grpRef) {
				if (e instanceof CustomObject) {
					CustomObject ld = (CustomObject) e;
					if (!Utils.isEmpty(specName)) {
						if (ld.getSpecification() != null)
							if (ld.getSpecification().getName()
							        .equals(specName)
							        && ld.getObjectState()
							                .getValue()
							                .equals(java.lang.String
							                        .valueOf(Status.ACTIVE)))
								result.add(ld);
					} else
						result.add(ld);
				}
			}

			return result;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}

	public List<LogicalDevice> findLogicalDevicesBySpecAndIvg(
	        InventoryGroup group, String specName) throws ValidationException {
		try {
			if (group == null)
				return null;
			
			List<LogicalDevice> result = new java.util.ArrayList<LogicalDevice>();
			InventoryGroupManager invGroupMgr = 
					PersistenceHelper.makeInventoryGroupManager();
			InventoryGroupEntitySearchCriteria criteria = 
					invGroupMgr.makeInventoryGroupEntitySearchCriteria();

			criteria.setInventoryGroup(group);
			criteria.setEntityClass(LogicalDevice.class);
			criteria.setRange(0, 10);

			Collection<? extends GroupEnabled> grpRef = invGroupMgr
			        .findEntitiesForInventoryGroup(criteria);

			for (GroupEnabled e : grpRef) {
				if (e instanceof LogicalDevice) {
					LogicalDevice ld = (LogicalDevice) e;
					if (!Utils.isEmpty(specName)) {
						if (ld.getSpecification() != null)
							if (ld.getSpecification().getName().equals(specName))
								result.add(ld);
					} else {
						result.add(ld);
					}
				}
			}
			return result;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}
	
	public List<Pipe> findPipesBySpecAndIvg(InventoryGroup group,
	        String specName) throws ValidationException {

		List<Pipe> result = new java.util.ArrayList<Pipe>();
		PipeManager pipeMgr = PersistenceHelper.makePipeManager();
		PipeSearchCriteria pscr = pipeMgr.makePipeSearchCriteria();
		CriteriaItem critItem = pscr.makeCriteriaItem();
		critItem.setValue(group.getName());
		pscr.setInventoryGroupName(critItem);
		List<Pipe> resultPrel = pipeMgr.findPipes(pscr);
		Pipe p = null;
		Iterator<Pipe> itt = resultPrel.iterator();
		while (itt.hasNext()) {
			p = itt.next();
			if (p.getSpecification().getName().equals(specName))
				result.add(p);
		}

		return result;
	}

	public List<Pipe> findPipesBySpecAndIvgAndChar(InventoryGroup group,
	        String specName, String charName, String charVal)
	        throws ValidationException {

		List<Pipe> result = new java.util.ArrayList<Pipe>();
		PipeManager pipeMgr = PersistenceHelper.makePipeManager();
		PipeSearchCriteria pscr = pipeMgr.makePipeSearchCriteria();
		CriteriaItem critItem = pscr.makeCriteriaItem();
		critItem.setValue(group.getName());
		pscr.setInventoryGroupName(critItem);
		List<Pipe> resultPrel = pipeMgr.findPipes(pscr);
		Pipe p = null;
		Iterator<Pipe> itt = resultPrel.iterator();
		while (itt.hasNext()) {
			p = itt.next();
			if (p.getSpecification().getName().equals(specName)) {
				if (this.getCharacteristicForEntity(p, charName)
				        .equals(charVal)) {
					result.add(p);
				}
			}
		}

		return result;
	}

	public List<Pipe> findPipesByParentAndAssignmentState(Pipe parent,
	        String assignment) throws ValidationException {
		AssignmentState assgn = null;
		if (assignment.equals("ASSIGNED")) {
			assgn = AssignmentState.ASSIGNED;
		}
		if (assignment.equals("PENDING_ASSIGN")) {
			assgn = AssignmentState.PENDING_ASSIGN;
		}
		if (assignment.equals("PENDING_UNASSIGN")) {
			assgn = AssignmentState.PENDING_UNASSIGN;
		}
		if (assignment.equals("DISCONNECTED")) {
			assgn = AssignmentState.DISCONNECTED;
		}
		if (assignment.equals("UNASSIGNED")) {
			assgn = AssignmentState.UNASSIGNED;
		}

		List<Pipe> result = new java.util.ArrayList<Pipe>();
		PipeManager pipeMgr = PersistenceHelper.makePipeManager();
		PipeSearchCriteria pscr = pipeMgr.makePipeSearchCriteria();
		pscr.setParentPipe(parent);
		pscr.setAssignmentState(assgn);
		result = pipeMgr.findPipes(pscr);

		return result;
	}

	public ServiceConfigurationVersion getLatestScvForService(Service s)
	        throws ValidationException {
		ServiceConfigurationVersion scv = null;

		List<ServiceConfigurationVersion> result = s.getConfigurations();
		int scvId = 0;
		for (ServiceConfigurationVersion v : result) {
			if (v.getVersionNumber() > scvId) {
				scvId = v.getVersionNumber();
				scv = v;
			}
		}

		return scv;
	}

	public List<Service> getPipeServiceConsumerBySpec(ConsumableResource res,
	        String spec) throws ValidationException {
		List<Service> result = new java.util.ArrayList<Service>();
		Collection<Consumer> consumerList = new java.util.ArrayList<Consumer>();
		ConsumerManager consMgr = PersistenceHelper.makeConsumerManager();
		consumerList = consMgr.getConsumers(res);
		for (Consumer e : consumerList) {
			if (e instanceof PipeAssignmentToService) {
				PipeAssignmentToService pp = (PipeAssignmentToService) e;
				Service ss = pp.getConsumer();
				if (ss.getSpecification().getName().equals(spec)) {
					result.add((Service) ss);
				}
			}

		}
		return result;
	}

	public List<Service> getLogicalDeviceServiceConsumerBySpec(
	        ConsumableResource res, String spec) throws ValidationException {
		List<Service> result = new java.util.ArrayList<Service>();
		Collection<Consumer> consumerList = new java.util.ArrayList<Consumer>();
		ConsumerManager consMgr = PersistenceHelper.makeConsumerManager();
		consumerList = consMgr.getConsumers(res);
		for (Consumer e : consumerList) {
			if (e instanceof LogicalDeviceAssignmentToService) {
				LogicalDeviceAssignmentToService pp = (LogicalDeviceAssignmentToService) e;
				Service ss = pp.getConsumer();
				if (ss.getSpecification().getName().equals(spec)) {
					result.add((Service) ss);
				}
			}

		}
		return result;
	}
	
	public boolean checkGeographicPlaceServiceRel(
			ServiceConfigurationVersion scv, 
			GeographicAddressType addressParam) 
			throws ValidationException {
    	
		GeographicAddress address = null;
		CommonManager commonManager = CommonHelper.makeCommonManager();
        List<GeographicAddress> addressList = commonManager.findAddress(addressParam);
        
        if (Utils.isEmpty(addressList))
            return false;
        if (addressList.size() > 0) {
        	if (addressList.size() > 1) {
        		//log.exception("c2a.duplicateAddress", new java.lang.IllegalArgumentException());
        	}
        	address = addressList.get(0);
        }
          
       
            
        
        return this.checkGeographicPlaceServiceRel(scv, address);		
	}
	
	public boolean checkGeographicPlaceServiceRel(
			ServiceConfigurationVersion scv, 
			GeographicAddress address) 
			throws ValidationException {
		
        Service service = (Service)scv.getConfigurable(); 
        Set<PlaceServiceRel> relSet = service.getPlace();
        if (Utils.isEmpty(relSet)) 
            return false;
        
        Iterator<PlaceServiceRel> iterator = relSet.iterator();
        
        for (PlaceServiceRel rel: relSet) {
            rel = (PlaceServiceRel)iterator.next();
            if (rel.getGeographicPlace().getId().equals(address.getId()))
                return true;
        }        
        return false;				
	}

	public GeographicAddress relateServiceToGeographicPlace(
			ServiceConfigurationVersion scv, 
			GeographicAddressType addressParam, 
			String addressSpecName) 
			throws ValidationException {
		
		Finder f = PersistenceHelper.makeFinder();
		try  {
				if (scv == null || addressParam == null)
					log.exception("c2a.addressRequired", new java.lang.IllegalArgumentException()); 							
				
			    CommonManager manager = CommonHelper.makeCommonManager();		    	
			    GeographicAddress address = manager.getAddress(addressParam, addressSpecName);
		    	
		    	if (FeedbackProviderImpl.hasErrors()) {
					log.exception("c2a.processError", new java.lang.IllegalArgumentException());
				}
		    	
		    	if (address == null) {
		    		log.exception("c2a.addressRequired", new java.lang.IllegalArgumentException()); 
		    	}
				
		    	this.relateServiceToGeographicPlace(scv, address);
		    	return address;
		}
		finally { 
			if (f != null)
				f.close();
			RequestPolicyHelper.checkPolicy();
    	}			
	}
	
	public void relateServiceToGeographicPlace(
			ServiceConfigurationVersion scv, 
			GeographicPlace place) 
			throws ValidationException {
		
		Finder f = PersistenceHelper.makeFinder();
		boolean isBIContext = false;
		BusinessInteraction currentBI = (BusinessInteraction)UserEnvironmentFactory.getBusinessInteraction();
		BusinessInteractionManager biMgr = PersistenceHelper.makeBusinessInteractionManager();
		try  {
				if (scv == null || place == null)
					log.exception("c2a.addressRequired", new java.lang.IllegalArgumentException()); 
										
				Service service = (Service)scv.getConfigurable(); 
				
				// if place and service is already 				
				if (!Utils.isEmpty(service.getPlace()))
						return;
				
				// create the rel in live						
				BusinessInteraction newBI = null;					
			    if (currentBI != null) {
			    	isBIContext = true;
			         // Switch to live
			         biMgr.switchContext(newBI, null);	        
			    }
		      
		        AttachmentManager involvementMgr = PersistenceHelper.makeAttachmentManager();
		        PlaceServiceRel rel = (PlaceServiceRel)involvementMgr.makeRel(PlaceServiceRel.class);
		        rel.setService(service);
		        rel.setGeographicPlace(place);
		       		        		        		        		        			        
			    involvementMgr.createRel(rel); 		       
		        
		}
		finally { 
			if (f!= null)
				f.close();
			if (isBIContext)
		    	biMgr.switchContext(currentBI, null);
    		RequestPolicyHelper.checkPolicy();
    	}			
	}

	protected ServiceAreaResolver getServiceAreaResolver(String serviceSpec) {
		ServiceAreaResolver resolver = null;
		try {
			resolver = ExtensionUtils.getImplementation(
			        SERVICE_AREA_RESOLVER_REGISTRY, serviceSpec);
		} catch (Exception e) {
			log.error("c2a.newServiceAreaResolverError", e, serviceSpec);
		}
		if (resolver == null) {
			resolver = new PostalCodeServiceAreaResolver();
		}
		return resolver;
	}

	protected ServiceAreaResolver getServiceAreaResolver() {
		return new PostalCodeServiceAreaResolver();
	}

	protected ServiceAreaBestFitSelector getServiceAreaBestFitSelector(
	        ServiceConfigurationVersion svcConVers) {
		String serviceSpec = svcConVers.getService().getSpecification()
		        .getName();
		ServiceAreaBestFitSelector selector = null;
		try {
			selector = ExtensionUtils.getImplementation(
			        SERVICE_AREA_BEST_FIT_SELECTOR_REGISTRY, serviceSpec);
		} catch (Exception e) {
			log.error("c2a.newServiceAreaBestFitSelectorError", e, serviceSpec);
		}
		if (selector == null) {
			selector = new PriorityServiceAreaSelector();
		}
		return selector;
	}	
	
	/* ServiceConfigurationItem Helper Methods */
	public void setServiceConfigItemProperty(ServiceConfigurationItem configItem,
			String propertyName, String value) throws ValidationException {
		Set<ServiceConfigurationItemCharacteristic> chars = configItem
				.getCharacteristics();

		if (chars == null) {
			chars = new HashSet<ServiceConfigurationItemCharacteristic>();
		}
		boolean found = false;
		for (ServiceConfigurationItemCharacteristic characteristic : chars) {
			CharacteristicSpecification charSpec = characteristic
					.getCharacteristicSpecification();
			if (charSpec.getName().equals(propertyName)) {
				characteristic.setValue(value);
				found = true;
				break;
			}
		}
		if (!found) {
			CommonManager commonManager = CommonHelper.makeCommonManager();
			@SuppressWarnings({ "unchecked", "rawtypes" })
			CharValue charvalue = commonManager.makeCharValue(
					(CharacteristicExtensible) configItem, propertyName, value);
			chars.add((ServiceConfigurationItemCharacteristic) charvalue);
		}
		configItem.setCharacteristics(chars);
	}
	
	public ServiceConfigurationItem getServiceConfigItem(
			ServiceConfigurationVersion config, 
			String sciName) 
			throws ValidationException {
		ServiceConfigurationItem sci = null;		
		List<ServiceConfigurationItem> configItems = CommonHelper.makeServiceManager()
				.findServiceConfigItemByName(config, sciName);
		if (!Utils.isEmpty(configItems)) {
			sci = configItems.get(0);
		}
		return sci;
	}	
	
	public ServiceConfigurationItem getChildServiceConfigItem(
			ServiceConfigurationItem parentConfigItem, String sciName)
			throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		ServiceConfigurationItem configItem = null;
		
		List<ServiceConfigurationItem> configItems = serviceManager
				.getAllChildServiceConfigurationItemsMatching(parentConfigItem,sciName);		
		
		if (Utils.isEmpty(configItems)) {
			configItem = serviceManager.addChildConfigItem(
					parentConfigItem.getConfiguration(),configItem, sciName);
		} else {
			configItem = configItems.get(0);
		}
		return configItem;
	}

	public ServiceConfigurationItem findChildServiceConfigItem(
			ServiceConfigurationItem parentConfigItem, String sciName)
			throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		ServiceConfigurationItem configItem = null;
		
		List<ServiceConfigurationItem> configItems = serviceManager
				.getAllChildServiceConfigurationItemsMatching(parentConfigItem,sciName);
		
		if (!Utils.isEmpty(configItems)) {
			configItem = configItems.get(0);
		}
		return configItem;
	}
	
	public boolean isConfigItemPropertyEquals(
			ServiceConfigurationItem configItem, String propertyName,
			String propertyValue) {
		Set<ServiceConfigurationItemCharacteristic> chars = configItem
				.getCharacteristics();
		for (ServiceConfigurationItemCharacteristic characteristic : chars) {
			CharacteristicSpecification charSpec = characteristic
					.getCharacteristicSpecification();
			if (charSpec.getName().equals(propertyName)) {
				String value = characteristic.getValue();
				return propertyValue.equals(value);
			}
		}
		return false;
	}
	
	public String getConfigItemCharacteristic(
	        ServiceConfigurationItem configItem, String characteristicName)
	        throws ValidationException {
		String value = null;
		Set<ServiceConfigurationItemCharacteristic> characterictics = configItem
		        .getCharacteristics();
		for (CharValue characteristic : characterictics) {
			if (characteristic.getName().equals(characteristicName)) {
				value = characteristic.getValue();
				break;
			}
		}
		return value;
	}
	
	public void dereferenceServiceConfigurationItem(ServiceConfigurationItem configItem) 
			throws ValidationException {
		BaseConfigurationManager configManager = PersistenceHelper
				.makeConfigurationManager(configItem.getConfiguration().getClass());						
		Collection<ServiceConfigurationItem> updateList = 
				new ArrayList<ServiceConfigurationItem>();
		updateList.add(configItem);
		configManager.dereferenceInventoryConfigurationItems(updateList);
	}

	
	public String getServiceAction(ServiceConfigurationVersion scv)
			throws ValidationException {
		String srvAction = "";
		BusinessInteraction bi = this.getParentBi(scv);
		
		if (bi != null) {		
			srvAction = this.getBiItemType(bi).getService().getAction();
		}
		return srvAction;
	}
	
	public String getCharacteristicValue(Party party, String charName) {
		String value = null;
		
		if (party != null && charName != null && !charName.isEmpty()) {
		
			Set<PartyCharacteristic> chars = party.getCharacteristics();
		
			for (PartyCharacteristic char1 : chars) {
				if (char1.getName().equals(charName)) {
					value =  char1.getValue();
				}
			}
		}
		return value;
	}
	
	public XmlString makeXmlString(String s) {
		XmlString value = XmlString.Factory.newInstance();
		value.setStringValue(s);
		return value;
	}
	
	public XmlBoolean makeXmlBoolean(boolean b) {
		XmlBoolean value = XmlBoolean.Factory.newInstance();
		value.setBooleanValue(b);
		return value;
	}

	public XmlInt makeXmlInt(int b) {
		XmlInt value = XmlInt.Factory.newInstance();
		value.setIntValue(b);
		return value;
	}
	
	public void referenceServiceLocation(
		ServiceConfigurationItem configItem,
		ServiceConfigurationVersion config,
		GeographicAddressType serviceAddressType) throws ValidationException {
		try {
			if (configItem == null)
				log.validationException("c2a.configItemNotFound",
						new java.lang.IllegalArgumentException(),
						"ServiceLocation");

			// Find/Create GeographicAddress (UIM entity) corresponding to serviceLocation.
			CommonManager commonMgr = CommonHelper.makeCommonManager();
			GeographicAddress serviceAddress = null;
			List<GeographicAddress> addressList = commonMgr.findAddress(serviceAddressType);
			if(!addressList.isEmpty())
				serviceAddress = addressList.get(0);
			else
				serviceAddress = commonMgr.createAddress(serviceAddressType, 
						"Customer_Site");

			BaseConfigurationManager configManager = PersistenceHelper
					.makeConfigurationManager(config.getClass());
			configManager.referenceEntity(configItem, serviceAddress);

			if (FeedbackProviderImpl.hasErrors()) {
				log.validationException("c2a.designFailed",
					new java.lang.IllegalStateException("ServiceLocation"));
			}
		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}
	
	public ServiceConfigurationItem aquireConfigItem(ServiceConfigurationVersion config,
			final String ciName) throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();		
		List<ServiceConfigurationItem> cis = serviceManager
				.findServiceConfigItemByName(config, ciName);
		ServiceConfigurationItem ci = null;
		
		if (Utils.isEmpty(cis)) {
			ci = serviceManager.addChildConfigItem(config,
				(ServiceConfigurationItem) config.getConfigItemTypeConfig(), ciName);
		} else {
			ci = cis.get(0);
		}
		return ci;
	}
	
	public ServiceConfigurationItem aquireUnusedConfigItem(ServiceConfigurationVersion config,
			final String ciName) throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();		
		List<ServiceConfigurationItem> cis = serviceManager
				.findServiceConfigItemByName(config, ciName);
		ServiceConfigurationItem ci = null;
		
		if (!Utils.isEmpty(cis)) {			
			for (ServiceConfigurationItem item : cis) {
				
				if (!serviceManager.checkItemAssignedReferenced(config, item)) {
					ci = item;
					break;
				}
			}
		}
		
		if (ci == null) {
			ci = serviceManager.addChildConfigItem(config,
				(ServiceConfigurationItem) config.getConfigItemTypeConfig(), ciName);
		}
		return ci;
	}
	
	
	public ServiceConfigurationItem getFirstAssignedComponentConfigItem(			
			ServiceConfigurationVersion config,
			final String configItemName,
			final String specName) throws ValidationException {
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
	
	public ConsumableResource getFirstAssignedComponent(			
			ServiceConfigurationVersion config,
			final String configItemName,
			final String specName) throws ValidationException {
				
		ConsumableResource result = null;
		ServiceConfigurationItem ci = 
			getFirstAssignedComponentConfigItem(config, configItemName, specName);
		
		if (ci != null) {
			Persistent ps = ci.getToEntity();
			ConsumableResource p = null;
			
			if (ps != null && ps instanceof Consumer) {
				result = ((Consumer) ps).getResource();
			}
		}		
		return result;
	}
	
	public void setResourceState(LogicalDevice resource, InventoryState state) 
			throws ValidationException {

		try {
			resource.setAdminState(state);
				
			Collection<LogicalDevice> devs = new ArrayList<LogicalDevice>();
			devs.add(resource);
			PersistenceHelper.makeLogicalDeviceManager().updateLogicalDevice(devs);
		} catch (Exception e) {
			log.validationException("c2a.failedtoUpdate",
					new java.lang.IllegalArgumentException(),
					resource.getName());			
		}		
	}
	
	@Override
	public BusinessInteractionType makeChildServiceOrder(String action,
			ParameterType[] parameters, String serviceSpecification,
			Service theService, String biSpecification, String srvNameSuffix)
			throws ValidationException {
		try {
			CommonManager commonManager = CommonHelper.makeCommonManager();
			BusinessInteractionSpec biSpec = (BusinessInteractionSpec) commonManager
					.findAndValidateSpecification(biSpecification);

			if (biSpec == null) {
				log.validationException("c2a.specNotFound",
					new java.lang.IllegalArgumentException(), biSpecification);
			}

			String serviceId = "";
			if (theService != null) {
				serviceId = theService.getId();
			}

			String serviceOrderName = action + " " + serviceSpecification;
			SpecificationType biSpecType = EntitySerializationUtils
					.toSpecification(biSpec);

			// Add business interaction parameters.
			BusinessInteractionType serviceOrder = 
				BusinessInteractionType.Factory.newInstance();
			serviceOrder.addNewHeader();
			serviceOrder.getHeader().setAction(BusinessInteractionActionEnum.CREATE);
			serviceOrder.getHeader().setSpecification(biSpecType);
			serviceOrder.getHeader().setId("");
			serviceOrder.getHeader().addNewExternalIdentity();
			serviceOrder.getHeader().getExternalIdentity().setExternalObjectId(srvNameSuffix);
			// Name is required.
			serviceOrder.getHeader().setName(serviceOrderName);
			serviceOrder.getHeader().setDescription(
					action + " " + serviceSpecification);
			Calendar cal = new GregorianCalendar();
			Date effDate = new Date();
			cal.setTime(effDate);
			serviceOrder.getHeader().setEffectiveDate(cal);
			// add the BI item for the service.
			BusinessInteractionItemType biItem = BusinessInteractionItemType.Factory
					.newInstance();
			biItem.setAction(BusinessInteractionItemActionEnum.ADD);
			ServiceType serviceType = ServiceType.Factory.newInstance();
			ServiceSpecification serviceSpec = (ServiceSpecification) commonManager
					.findAndValidateSpecification(serviceSpecification);
			
			if (serviceSpec == null) {
				log.validationException("c2a.specNotFound",
					new java.lang.IllegalArgumentException(), serviceSpecification);
			}
			SpecificationType serviceSpecType = 
				EntitySerializationUtils.toSpecification(serviceSpec);
			serviceType.setSpecification(serviceSpecType);
			serviceType.setAction(action);
			serviceType.setId(serviceId);
			serviceType.addNewExternalIdentity();
			serviceType.getExternalIdentity().setExternalObjectId("");
			serviceType.setName(serviceSpecification + "-" + srvNameSuffix);
			serviceType.setDescription(serviceSpecification
					+ " service created by autodesign");
			biItem.setService(serviceType);
			biItem.setParameterArray(Arrays.copyOf(parameters, parameters.length));
			BusinessInteractionItemType items[] = new BusinessInteractionItemType[1];
			items[0] = biItem;
			serviceOrder.addNewBody();
			serviceOrder.getBody().setItemArray(items);
			return serviceOrder;

		} finally {
			RequestPolicyHelper.checkPolicy();
		}
	}
	
	@Override
	public String getServiceName(ServiceConfigurationVersion config) 
			throws ValidationException {
		if (config == null) {
			log.validationException("c2a.configurationIsNull",
					new java.lang.IllegalArgumentException());
		}
		
		config = connect(config);
		Service svc = config.getService();

		if (svc == null && !ServiceUtils.validateEntityExists(svc)) 
			return "";		
		return svc.getName();
	}
	
	@Override
	public void relateCustomerToService(final String customerId, 
		ServiceConfigurationVersion config, final String spec, final String role) 
				throws ValidationException {
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		
		if (customerId != null && !customerId.isEmpty()) {
			PartyType customer = PartyType.Factory.newInstance();
			log.debug(" PartyType : " + customer);
			customer.setName(customerId);
			boolean rel = CommonHelper.makeCommonManager().checkPartyServiceRel(config, customer);
			
			if (!rel) {
				serviceManager.relateServiceToParty(config,customer, spec, role);
			}
		}
	}
	
	@Override
	public void relateServiceAddressToService(final StructuredType address, 
		ServiceConfigurationVersion config, final String spec) 
			throws ValidationException {
		// This method assumes parameter type and name alignment between StructuredType 
		// address and passed specification 
		if (address != null && !address.isNil()) {
			DesignManager designer = DesignHelper.makeDesignManager();		 
			GeographicAddressType addrType = designer.convertServiceAddress(address);				
			
			if (addrType != null) {
				if (addrType.getSpecification()== null) {
					SpecificationType result = SpecificationType.Factory.newInstance();
					result.setName(spec);
					addrType.setSpecification(result);
				}
				else { 
					addrType.getSpecification().setName(spec);
				}
										
				if (!designer.checkGeographicPlaceServiceRel(config, addrType)) {
					designer.relateServiceToGeographicPlace(config,addrType, spec);
				}		
			}
		}
	}
	
	@Override
	public GeographicAddressType convertServiceAddress(final StructuredType address) {	
		GeographicAddressType addrType = null;
		
		if (address != null && !address.isNil() ) {	
			addrType = GeographicAddressType.Factory.newInstance();		
			PropertyType newP = null;
			List<PropertyType> props = address.getPropertyList();
			
			for (PropertyType p : props) {
				newP = addrType.addNewProperty();
	        	newP.setName(p.getName());
	        	newP.setValue(p.getValue());
			}		
		} 
		return addrType;
	}
	/*
	 * HELPER METHODS
	 */
	private Specification findAndValidateSpecification(String specName)
			throws ValidationException {		
		Specification spec = CommonHelper.makeCommonManager()
		        .findAndValidateSpecification(specName);

		if (spec == null) {
			log.validationException("c2a.couldNotFind",
			        new java.lang.IllegalArgumentException(), specName);
		} 
		return spec;
	}

	@Override
        public ServiceConfigurationVersion disconnectService(Service service) throws ValidationException {
            
            final BusinessInteraction bi = (BusinessInteraction) oracle.communications.inventory.api.framework.security.UserEnvironmentFactory.getBusinessInteraction();
    
            PersistenceHelper.makeBusinessInteractionManager().switchContext((String) null, null);
            PersistenceHelper.makeServiceManager().disconnectService(service);
            this.flushTransaction();
            
            ServiceConfigurationVersion  serviceConfigurationVersion = service.getConfigurations().get(service.getConfigurations().size() - 1);
            
            List<InventoryConfigurationVersion> versions = new ArrayList<InventoryConfigurationVersion>();
            versions.add(serviceConfigurationVersion);
            
            PersistenceHelper.makeBusinessInteractionManager().associateBusinessInteractionToConfigVersions(bi, versions);        
            PersistenceHelper.makeBusinessInteractionManager().switchContext(bi, null);
            
            return serviceConfigurationVersion;
        }


    @Override
    public String getJMSCorrelationId() {
        String correlationId = (String)oracle.communications.inventory.api.framework.security.UserEnvironmentFactory.getUserEnvironment().getUserData("JMS_CORRELATION_ID");
		log.debug("CorrelationId : " + correlationId);
        return correlationId;
    }
    
    
    public void sendMessageToOSM(BusinessInteraction bi, String jmsCorrelationId) throws ValidationException{
       
       String soapMessageText = createSoapMessage(bi, jmsCorrelationId);
       
       QueueConnectionFactory qConFactory = null;
       Context jndiContext = null;
       Queue osmQueue = null;
        
       try {
           jndiContext = new InitialContext();
           osmQueue = (Queue) jndiContext.lookup("inventoryWSQueueAlternate");
           qConFactory =
               (QueueConnectionFactory) jndiContext.lookup("inventoryWSQueueAlternateCF");
       } catch (NamingException e) {
           log.error("", e);
       }

        QueueConnection qCon = null;
        QueueSession qSession = null;
        QueueSender qSender = null;

        if (osmQueue != null) {

            try {
            	if (qConFactory != null) {
            		qCon = qConFactory.createQueueConnection();
            	}
                if (qCon != null) {
                	qCon.start();
                	qSession = qCon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                }
                if (qSession != null) {
                	qSender = qSession.createSender(osmQueue);
                	qSender.setDeliveryMode(DeliveryMode.PERSISTENT);
                }
                
                if(qSession != null && soapMessageText != null){
                    
                    TextMessage textMessageSend = qSession.createTextMessage();
                    textMessageSend.setText(soapMessageText);
                    textMessageSend.setStringProperty("_wls_mimehdrContent_Type", "text/xml; charset=utf-8");
                    textMessageSend.setStringProperty("_wls_mimehdrSOAPAction", "\"http://xmlns.oracle.com/communications/inventory/webservice/ProcessInteraction\"");
                    textMessageSend.setJMSCorrelationID(jmsCorrelationId);

                    if (qSender != null)
                    	qSender.send(textMessageSend);
                }
            } 
            catch (JMSException e) {
                log.validationException("activity.errorPostingActivityToJMSQueue",new ValidationException(e));
            } 
            finally {
                if (qSender != null) {
                    try {
                        qSender.close();
                    } catch (JMSException e) {
                        log.error("",e.getMessage());
                    }
                }
                if (qSession != null) {
                    try {
                        qSession.close();
                    } catch (JMSException e) {
                        log.error("",e.getMessage());
                    }
                }
                if (qCon != null) {
                    try {
                        qCon.close();
                    } catch (JMSException e) {
                        log.error("",e.getMessage());
                    }
                }
            }

        }else{
            log.validationException("activity.errorPostingActivityToJMSQueue",new ValidationException());
        }
    }

    private String createSoapMessage(BusinessInteraction bi, String jmsCorrelationId) {
        
        String soapMessageText = null;

        try {
            oracle.communications.inventory.xmlbeans.BusinessInteractionType biXmlObject = convertBiToXmlObject(bi);

            Map<String, String> nameSpaces = new HashMap<String, String>();
            nameSpaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            nameSpaces.put("bus", "http://xmlns.oracle.com/communications/inventory/businessinteraction");

            String headerXml =
                biXmlObject.getHeader().xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(1).setSaveImplicitNamespaces(nameSpaces).setSaveNamespacesFirst());
            headerXml =
                headerXml.replaceAll("<xml-fragment>",
                                     "<n1:header xmlns:n1=\"http://xmlns.oracle.com/communications/inventory/businessinteraction\">");
            headerXml = headerXml.replaceAll("bus:", "n1:");
            headerXml = headerXml.replaceAll("</xml-fragment>", "</n1:header>");

            String bodyXml =
                biXmlObject.getBody().xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(1).setSaveImplicitNamespaces(nameSpaces).setSaveNamespacesFirst());
            bodyXml =
                bodyXml.replaceAll("<xml-fragment>",
                                   "<n2:body xmlns:n2=\"http://xmlns.oracle.com/communications/inventory/businessinteraction\">");
            bodyXml = bodyXml.replaceAll("bus:", "n2:");
            bodyXml = bodyXml.replaceAll("</xml-fragment>", "</n2:body>");

            String interactionText = headerXml + bodyXml;

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement processInteractionResponse =
                soapBody.addChildElement("processInteractionResponse", "bus",
                                         "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction");
            SOAPElement interactionElement = processInteractionResponse.addChildElement("interaction", "bus");
            interactionElement.addChildElement("REPLACE_THIS_TEXT");
            soapMessage.saveChanges();

            // Print the request message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            soapMessageText = new String(out.toByteArray());
            soapMessageText = soapMessageText.replace("<REPLACE_THIS_TEXT/>", interactionText);

            log.debug("Sending JMS Message: " + soapMessageText + " with Correlation Id: " + jmsCorrelationId);

        } catch (Exception e) {
            log.error("", e);
        }

        return soapMessageText;
    }

    private oracle.communications.inventory.xmlbeans.BusinessInteractionType convertBiToXmlObject(BusinessInteraction bi) throws ValidationException {
        
        InteractionMapper mapper = new InteractionMapper(InteractionResponseLevelEnum.interaction_ITEM_ENTITY_CONFIGURATION);

        oracle.communications.inventory.sfws.businessinteraction.BusinessInteractionType businessInteractionType = mapper.fromInteraction(bi);
        oracle.communications.inventory.xmlbeans.BusinessInteractionType xmlBiType = ActionSerializationUtils.fromBusinessInteraction(businessInteractionType);
        oracle.communications.inventory.xmlbeans.BusinessInteractionBodyType body = ActionSerializationUtils.fromBusinessInteractionBody(businessInteractionType.getBody());

        xmlBiType.setBody(body);
        
        oracle.communications.inventory.xmlbeans.BusinessInteractionType[] interactionArray = new oracle.communications.inventory.xmlbeans.BusinessInteractionType[bi.getChildBusinessInteractions().size()];
        
        int i = 0;
        for(BusinessInteraction childBi: bi.getChildBusinessInteractions()){
            oracle.communications.inventory.xmlbeans.BusinessInteractionType childBiXmlObject = convertBiToXmlObject(childBi);
            interactionArray[i] = childBiXmlObject;
            i++;
        }
        
        xmlBiType.getBody().setInteractionArray(interactionArray);
        
        return xmlBiType;
    }


    public BusinessInteraction getTopLevelParentBusinessInteraction(BusinessInteraction bi) {
        if(bi != null){
            
            BusinessInteraction parentBi = bi.getParentBusinessInteraction();
            if(parentBi != null){
                return getTopLevelParentBusinessInteraction(parentBi);
            }else{
                return bi;
            }
        }
        
        return null;
    }
    
	
	@Override
    public boolean isObjectReferencedByOtherService(
        	Service currentService, ConfigurationReferenceEnabled refObject) 
        		throws ValidationException {
        	BusinessInteraction bi = (BusinessInteraction) UserEnvironmentFactory.getBusinessInteraction();

        	BaseConfigurationManager bcm = PersistenceHelper.makeConfigurationManager(ServiceConfigurationVersion.class);
        	ConfigurationReferenceSearchCriteria configRefCriteria = bcm.makeConfigurationReferenceSearchCriteria();
        	configRefCriteria.setEntity(refObject);
    		configRefCriteria.setReferencedByType(Service.class);
    		configRefCriteria.setConfigurationReferenceStates(Arrays.asList(
    			ConfigurationReferenceState.PENDING_REFERENCE,
    		    ConfigurationReferenceState.REFERENCED));
    		
    		Collection<ConfigurationReference> referenceCollection = bcm.findConfigurationReferences(configRefCriteria);
    		bi = (BusinessInteraction) UserEnvironmentFactory.getBusinessInteraction();
    		
    		for (ConfigurationReference configurationReference : referenceCollection) {
    			if (configurationReference.getReferencedBy() instanceof Service) {
    				Service referencedByService = (Service) configurationReference.getReferencedBy();

    				if (!(referencedByService.equals(currentService))) {
    					return true;
    				}
    			}
    		}

    		return false;
    }
}
