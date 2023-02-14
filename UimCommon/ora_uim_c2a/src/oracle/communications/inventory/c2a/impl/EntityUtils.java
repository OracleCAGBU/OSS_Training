package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Collection;
import java.util.List;
import java.util.Set;

import oracle.communications.inventory.api.TimeBound;
import oracle.communications.inventory.api.characteristic.impl.CharacteristicHelper;
import oracle.communications.inventory.api.common.EntitySerializationUtils;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.CharacteristicSpecUsage;
import oracle.communications.inventory.api.entity.CharacteristicSpecValue;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ControlType;
import oracle.communications.inventory.api.entity.CustNetAddrSpecification;
import oracle.communications.inventory.api.entity.CustomNetworkAddress;
import oracle.communications.inventory.api.entity.CustomNetworkAddressCharacteristic;
import oracle.communications.inventory.api.entity.CustomObject;
import oracle.communications.inventory.api.entity.CustomObjectCharacteristic;
import oracle.communications.inventory.api.entity.CustomObjectSpecification;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.DeviceInterfaceCharacteristic;
import oracle.communications.inventory.api.entity.DeviceInterfaceSpecification;
import oracle.communications.inventory.api.entity.DiscreteCharSpecValue;
import oracle.communications.inventory.api.entity.DiscreteCharSpecValueUsage;
import oracle.communications.inventory.api.entity.Equipment;
import oracle.communications.inventory.api.entity.EquipmentCharacteristic;
import oracle.communications.inventory.api.entity.EquipmentHolder;
import oracle.communications.inventory.api.entity.EquipmentHolderCharacteristic;
import oracle.communications.inventory.api.entity.EquipmentHolderEquipmentRel;
import oracle.communications.inventory.api.entity.EquipmentHolderSpecification;
import oracle.communications.inventory.api.entity.EquipmentSpecification;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicLocation;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.GeographicSite;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupCharacteristic;
import oracle.communications.inventory.api.entity.InventoryGroupSpecification;
import oracle.communications.inventory.api.entity.InventoryRole;
import oracle.communications.inventory.api.entity.LDAccountCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.LogicalDeviceAccountSpecification;
import oracle.communications.inventory.api.entity.LogicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.MediaResourceCharacteristic;
import oracle.communications.inventory.api.entity.MediaStream;
import oracle.communications.inventory.api.entity.MediaStreamSpecification;
import oracle.communications.inventory.api.entity.Network;
import oracle.communications.inventory.api.entity.NetworkCharacteristic;
import oracle.communications.inventory.api.entity.NetworkSpecification;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyCharacteristic;
import oracle.communications.inventory.api.entity.PartySpecification;
import oracle.communications.inventory.api.entity.PhysicalConnector;
import oracle.communications.inventory.api.entity.PhysicalConnectorCharacteristic;
import oracle.communications.inventory.api.entity.PhysicalConnectorSpecification;
import oracle.communications.inventory.api.entity.PhysicalDevice;
import oracle.communications.inventory.api.entity.PhysicalDeviceCharacteristic;
import oracle.communications.inventory.api.entity.PhysicalDeviceSpecification;
import oracle.communications.inventory.api.entity.PhysicalPort;
import oracle.communications.inventory.api.entity.PhysicalPortCharacteristic;
import oracle.communications.inventory.api.entity.PhysicalPortSpecification;
import oracle.communications.inventory.api.entity.Pipe;
import oracle.communications.inventory.api.entity.PipeCharacteristic;
import oracle.communications.inventory.api.entity.PipeSpecification;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.entity.PlaceSpecification;
import oracle.communications.inventory.api.entity.ReservedForType;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceSpecification;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.TNCharacteristic;
import oracle.communications.inventory.api.entity.TelephoneNumber;
import oracle.communications.inventory.api.entity.TelephoneNumberSpecification;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.RootEntity;
import oracle.communications.inventory.api.equipment.EquipmentManager;
import oracle.communications.inventory.api.equipment.EquipmentSearchCriteria;
import oracle.communications.inventory.api.equipment.PhysicalDeviceSearchCriteria;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.specification.SpecManager;
import oracle.communications.inventory.xmlbeans.BusinessInteractionType;
import oracle.communications.inventory.xmlbeans.ConnectivityType;
import oracle.communications.inventory.xmlbeans.CustomObjectType;
import oracle.communications.inventory.xmlbeans.DeviceInterfaceType;
import oracle.communications.inventory.xmlbeans.EntityClassEnum;
import oracle.communications.inventory.xmlbeans.EquipmentHolderType;
import oracle.communications.inventory.xmlbeans.EquipmentType;
import oracle.communications.inventory.xmlbeans.GeographicAddressType;
import oracle.communications.inventory.xmlbeans.GeographicLocationType;
import oracle.communications.inventory.xmlbeans.GeographicSiteType;
import oracle.communications.inventory.xmlbeans.InventoryGroupType;
import oracle.communications.inventory.xmlbeans.LogicalDeviceAccountType;
import oracle.communications.inventory.xmlbeans.LogicalDeviceType;
import oracle.communications.inventory.xmlbeans.MediaStreamType;
import oracle.communications.inventory.xmlbeans.NetworkAddressType;
import oracle.communications.inventory.xmlbeans.NetworkType;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.inventory.xmlbeans.PartyType;
import oracle.communications.inventory.xmlbeans.PhysicalConnectorType;
import oracle.communications.inventory.xmlbeans.PhysicalDeviceType;
import oracle.communications.inventory.xmlbeans.PhysicalPortType;
import oracle.communications.inventory.xmlbeans.PlaceType;
import oracle.communications.inventory.xmlbeans.PropertyType;
import oracle.communications.inventory.xmlbeans.ReservedForTypeEnum;
import oracle.communications.inventory.xmlbeans.RoleType;
import oracle.communications.inventory.xmlbeans.ServiceConfigurationType;
import oracle.communications.inventory.xmlbeans.ServiceType;
import oracle.communications.inventory.xmlbeans.SpecificationType;
import oracle.communications.inventory.xmlbeans.TelephoneNumberType;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;


public class EntityUtils
{
    private static Log log = LogFactory.getLog( EntityUtils.class );
    private static SpecManager specMgr = null;
    
    public static void populate( Persistent resource, XmlObject template )
        throws ValidationException
    {
        // TODO: isCompatibleWith is redundant - optimize: remove it
        if( !isCompatibleWith( resource, template ) )
            return;
        if( resource instanceof CustomObject
            && template instanceof CustomObjectType )
        {
            populateCustomObject( (CustomObject) resource,
                (CustomObjectType) template );
        }
        else if( resource instanceof DeviceInterface
            && template instanceof DeviceInterfaceType )
        {
            populateDeviceInterface( (DeviceInterface) resource,
                (DeviceInterfaceType) template );
        }
        else if( resource instanceof Equipment
            && template instanceof EquipmentType )
        {
            populateEquipment( (Equipment) resource, (EquipmentType) template );
        }
        else if( resource instanceof EquipmentHolder
            && template instanceof EquipmentHolderType )
        {
            populateEquipmentHolder( (EquipmentHolder) resource,
                (EquipmentHolderType) template );
        }
        else if( resource instanceof GeographicPlace
            && template instanceof PlaceType )
        {
            populatePlace( (GeographicPlace) resource, (PlaceType) template );
        }
        else if( resource instanceof InventoryGroup
            && template instanceof InventoryGroupType )
        {
            populateInventoryGroup( (InventoryGroup) resource,
                (InventoryGroupType) template );
        }
        else if( resource instanceof LogicalDevice
            && template instanceof LogicalDeviceType )
        {
            populateLogicalDevice( (LogicalDevice) resource,
                (LogicalDeviceType) template );
        }
        else if( resource instanceof LogicalDeviceAccount
            && template instanceof LogicalDeviceAccountType )
        {
            populateLogicalDeviceAccount( (LogicalDeviceAccount) resource,
                (LogicalDeviceAccountType) template );
        }
        else if( resource instanceof Network && template instanceof NetworkType )
        {
            populateNetwork( (Network) resource, (NetworkType) template );
        }
        else if( resource instanceof CustomNetworkAddress
            && template instanceof NetworkAddressType )
        {
            populateNetworkAddress( (CustomNetworkAddress) resource,
                (NetworkAddressType) template );
        }
        else if( resource instanceof Party && template instanceof PartyType )
        {
            populateParty( (Party) resource, (PartyType) template );
        }
        else if( resource instanceof PhysicalConnector
            && template instanceof PhysicalConnectorType )
        {
            populatePhysicalConnector( (PhysicalConnector) resource,
                (PhysicalConnectorType) template );
        }
        else if( resource instanceof PhysicalDevice
            && template instanceof PhysicalDeviceType )
        {
            populatePhysicalDevice( (PhysicalDevice) resource,
                (PhysicalDeviceType) template );
        }
        else if( resource instanceof PhysicalPort
            && template instanceof PhysicalPortType )
        {
            populatePhysicalPort( (PhysicalPort) resource,
                (PhysicalPortType) template );
        }
        else if( resource instanceof Pipe && template instanceof ConnectivityType )
        {
            populatePipe( (Pipe) resource, (ConnectivityType) template );
        }
        else if( resource instanceof Service && template instanceof ServiceType )
        {
            populateService( (Service) resource, (ServiceType) template );
        }
        else if( resource instanceof MediaStream && template instanceof MediaStreamType )
        {
            populateMediaStream( (MediaStream) resource, (MediaStreamType) template );
        }
        else if( resource instanceof TelephoneNumber
            && template instanceof TelephoneNumberType )
        {
            populateTelephoneNumber( (TelephoneNumber) resource,
                (TelephoneNumberType) template );
        }
        else
            log.validationException("ws.entityUtilsResTypeNotRecognized", new java.lang.IllegalArgumentException(), resource.getClass().getName());
    }

    private static boolean isCompatibleWith( Persistent resource,
        XmlObject other )
    {
        try
        {
            EntityClassEnum.Enum resourceType = EntitySerializationUtils
                .toEntityClass( resource );
            EntityClassEnum.Enum otherType = toEntityClass( other );
            return resourceType.equals( otherType );
        }
        catch( ValidationException e )
        {
            return false;
        }
    }

    public static EntityClassEnum.Enum toEntityClass( XmlObject entity )
        throws ValidationException
    {
        if( entity instanceof CustomObjectType )
            return EntityClassEnum.CUSTOM_OBJECT;
        if( entity instanceof DeviceInterfaceType )
            return EntityClassEnum.DEVICE_INTERFACE;
        if( entity instanceof EquipmentType )
            return EntityClassEnum.EQUIPMENT;
        if( entity instanceof EquipmentHolderType )
            return EntityClassEnum.EQUIPMENT_HOLDER;
        if( entity instanceof GeographicAddressType )
            return EntityClassEnum.GEOGRAPHIC_ADDRESS;
        if( entity instanceof GeographicLocationType )
            return EntityClassEnum.GEOGRAPHIC_LOCATION;
        if( entity instanceof GeographicSiteType )
            return EntityClassEnum.GEOGRAPHIC_SITE;
        // put place after subclasses (address, location, site)
        if( entity instanceof PlaceType )
            return EntityClassEnum.GEOGRAPHIC_PLACE;
        if( entity instanceof InventoryGroupType )
            return EntityClassEnum.INVENTORY_GROUP;
        if( entity instanceof LogicalDeviceType )
            return EntityClassEnum.LOGICAL_DEVICE;
        if( entity instanceof LogicalDeviceAccountType )
            return EntityClassEnum.LOGICAL_DEVICE_ACCOUNT;
        if( entity instanceof MediaStreamType )
            return EntityClassEnum.MEDIA_STREAM;
        if( entity instanceof NetworkType )
            return EntityClassEnum.NETWORK;
        if( entity instanceof NetworkAddressType )
            return EntityClassEnum.NETWORK_ADDRESS;
        if( entity instanceof PartyType )
            return EntityClassEnum.PARTY;
        if( entity instanceof PhysicalConnectorType )
            return EntityClassEnum.PHYSICAL_CONNECTOR;
        if( entity instanceof PhysicalDeviceType )
            return EntityClassEnum.PHYSICAL_DEVICE;
        if( entity instanceof PhysicalPortType )
            return EntityClassEnum.PHYSICAL_PORT;
        if( entity instanceof ConnectivityType )
            return EntityClassEnum.PIPE;
        if( entity instanceof RoleType )
            return EntityClassEnum.ROLE;
        if( entity instanceof ServiceType )
            return EntityClassEnum.SERVICE;
        if( entity instanceof ServiceConfigurationType )
            return EntityClassEnum.SERVICE_CONFIGURATION_VERSION;
        if( entity instanceof TelephoneNumberType )
            return EntityClassEnum.TELEPHONE_NUMBER;
        // put this last because it is a superclass
        if( entity instanceof BusinessInteractionType )
            return EntityClassEnum.BUSINESS_INTERACTION;
        log.validationException("ws.entityUtilsEntityNotRecognized", new java.lang.IllegalArgumentException(), entity.getClass().toString());
        return null;
    }

    public static Class<? extends Persistent> toEntityClass(
        EntityClassEnum.Enum entityClass ) throws ValidationException
    {
        if( entityClass == null )
            return null;
        if( entityClass.equals( EntityClassEnum.CUSTOM_OBJECT ) )
            return CustomObject.class;
        if( entityClass.equals( EntityClassEnum.DEVICE_INTERFACE ) )
            return DeviceInterface.class;
        if( entityClass.equals( EntityClassEnum.EQUIPMENT ) )
            return Equipment.class;
        if( entityClass.equals( EntityClassEnum.EQUIPMENT_HOLDER ) )
            return EquipmentHolder.class;
        if( entityClass.equals( EntityClassEnum.GEOGRAPHIC_ADDRESS ) )
            return GeographicAddress.class;
        if( entityClass.equals( EntityClassEnum.GEOGRAPHIC_LOCATION ) )
            return GeographicLocation.class;
        if( entityClass.equals( EntityClassEnum.GEOGRAPHIC_SITE ) )
            return GeographicSite.class;
        // put place after subclasses (address, location, site)
        if( entityClass.equals( EntityClassEnum.GEOGRAPHIC_PLACE ) )
            return GeographicPlace.class;
        if( entityClass.equals( EntityClassEnum.INVENTORY_GROUP ) )
            return InventoryGroup.class;
        if( entityClass.equals( EntityClassEnum.LOGICAL_DEVICE ) )
            return LogicalDevice.class;
        if( entityClass.equals( EntityClassEnum.LOGICAL_DEVICE_ACCOUNT ) )
            return LogicalDeviceAccount.class;
        if( entityClass.equals( EntityClassEnum.MEDIA_STREAM ) )
            return MediaStream.class;
        if( entityClass.equals( EntityClassEnum.NETWORK ) )
            return Network.class;
        if( entityClass.equals( EntityClassEnum.NETWORK_ADDRESS ) )
            return CustomNetworkAddress.class;
        if( entityClass.equals( EntityClassEnum.PARTY ) )
            return Party.class;
        if( entityClass.equals( EntityClassEnum.PHYSICAL_CONNECTOR ) )
            return PhysicalConnector.class;
        if( entityClass.equals( EntityClassEnum.PHYSICAL_DEVICE ) )
            return PhysicalDevice.class;
        if( entityClass.equals( EntityClassEnum.PHYSICAL_PORT ) )
            return PhysicalPort.class;
        if( entityClass.equals( EntityClassEnum.PIPE ) )
            return Pipe.class;
        if( entityClass.equals( EntityClassEnum.ROLE ) )
            return InventoryRole.class;
        if( entityClass.equals( EntityClassEnum.SERVICE ) )
            return Service.class;
        if( entityClass.equals( EntityClassEnum.SERVICE_CONFIGURATION_VERSION ) )
            return ServiceConfigurationVersion.class;
        if( entityClass.equals( EntityClassEnum.TELEPHONE_NUMBER ) )
            return TelephoneNumber.class;
        // put this last because it is a superclass
        if( entityClass.equals( EntityClassEnum.BUSINESS_INTERACTION ) )
            return BusinessInteraction.class;
        log.validationException("ws.entityUtilsEntityNotRecognized", new java.lang.IllegalArgumentException(), entityClass.toString());
        return null;
    }

    private static void populateCustomObject( CustomObject resource,
        CustomObjectType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        CustomObjectSpecification spec = findSpecification(
            CustomObjectSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<CustomObjectCharacteristic> newChars = (Set<CustomObjectCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateDeviceInterface( DeviceInterface resource,
        DeviceInterfaceType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        DeviceInterfaceSpecification spec = findSpecification(
            DeviceInterfaceSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // roles
        // physical device
        // physical connector
        // physical port
        LogicalDevice logicalDevice = findLogicalDevice( source
            .getLogicalDevice() );
        resource.setLogicalDevice( logicalDevice );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<DeviceInterfaceCharacteristic> newChars = (Set<DeviceInterfaceCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateEquipment( Equipment resource,
        EquipmentType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        EquipmentSpecification spec = findSpecification(
            EquipmentSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        resource.setSerialNumber( source.getSerialNumber() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<EquipmentCharacteristic> newChars = (Set<EquipmentCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateEquipmentHolder( EquipmentHolder resource,
        EquipmentHolderType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        EquipmentHolderSpecification spec = findSpecification(
            EquipmentHolderSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        Equipment equipment = findEquipment( source.getEquipment() );
        resource.setEquipment( equipment );
        equipment = findEquipment( source.getChildEquipment() );
        resource
            .setChildEquipment( makeChildEquipmentRel( resource, equipment ) );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<EquipmentHolderCharacteristic> newChars = (Set<EquipmentHolderCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateInventoryGroup( InventoryGroup resource,
        InventoryGroupType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        InventoryGroupSpecification spec = findSpecification(
            InventoryGroupSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // child groups
        // place
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<InventoryGroupCharacteristic> newChars = (Set<InventoryGroupCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateLogicalDevice( LogicalDevice resource,
        LogicalDeviceType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        LogicalDeviceSpecification spec = findSpecification(
            LogicalDeviceSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<LogicalDeviceCharacteristic> newChars = (Set<LogicalDeviceCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateLogicalDeviceAccount(
        LogicalDeviceAccount resource, LogicalDeviceAccountType source )
        throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        LogicalDeviceAccountSpecification spec = findSpecification(
            LogicalDeviceAccountSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        if ( source.getLogicalDevice() != null ){
            LogicalDevice logicalDevice = findLogicalDevice( source
                .getLogicalDevice() );
            resource.setLogicalDevice( logicalDevice );
        }
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<LDAccountCharacteristic> newChars = (Set<LDAccountCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }
    
    private static void populateMediaStream( MediaStream stream,
        MediaStreamType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        MediaStreamSpecification spec = findSpecification(
            MediaStreamSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        stream.setSpecification( spec );
        stream.setId( source.getId() );
        stream.setName( source.getName() );
        stream.setDescription( source.getDescription() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( stream, p.getName(), p.getValue() );
        }
        Set<MediaResourceCharacteristic> newChars = (Set<MediaResourceCharacteristic>)stream
            .getCharacteristics();
        defaultMissingCharacteristics( stream, newChars, spec
            .getCharacteristicSpecUsages() );
        stream.setCharacteristics( newChars );
    }

    private static void populateNetwork( Network resource, NetworkType source )
        throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        NetworkSpecification spec = findSpecification(
            NetworkSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // activity
        // configurations
        // groups
        // roles
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<NetworkCharacteristic> newChars = (Set<NetworkCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateNetworkAddress( CustomNetworkAddress resource,
        NetworkAddressType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        CustNetAddrSpecification spec = findSpecification(
            CustNetAddrSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // activity
        // groups
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<CustomNetworkAddressCharacteristic> newChars = (Set<CustomNetworkAddressCharacteristic>)resource
            .getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateParty( Party resource, PartyType source )
        throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PartySpecification spec = findSpecification( PartySpecification.class,
            sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        //TODO - ALAIN
        // groups
        // roles
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PartyCharacteristic> newChars = (Set<PartyCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populatePlace( GeographicPlace resource,
        PlaceType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PlaceSpecification spec = findSpecification( PlaceSpecification.class,
            sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // child places
        // configurations
        // horizontal, vertical
        // latitude, longitude
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PlaceCharacteristic> newChars = (Set<PlaceCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populatePhysicalConnector( PhysicalConnector resource,
        PhysicalConnectorType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PhysicalConnectorSpecification spec = findSpecification(
            PhysicalConnectorSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        Equipment equipment = findEquipment( source.getEquipment() );
        resource.setEquipment( equipment );
        PhysicalDevice physicalDevice = findPhysicalDevice( source
            .getPhysicalDevice() );
        resource.setPhysicalDevice( physicalDevice );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PhysicalConnectorCharacteristic> newChars = (Set<PhysicalConnectorCharacteristic>)resource
            .getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populatePhysicalDevice( PhysicalDevice resource,
        PhysicalDeviceType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PhysicalDeviceSpecification spec = findSpecification(
            PhysicalDeviceSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        resource.setSerialNumber( source.getSerialNumber() );
        resource.setPhysicalAddress( source.getPhysicalAddress() );
        resource.setPhysicalLocation( source.getPhysicalLocation() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PhysicalDeviceCharacteristic> newChars = (Set<PhysicalDeviceCharacteristic>)resource
            .getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populatePhysicalPort( PhysicalPort resource,
        PhysicalPortType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PhysicalPortSpecification spec = findSpecification(
            PhysicalPortSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        Equipment equipment = findEquipment( source.getEquipment() );
        resource.setEquipment( equipment );
        PhysicalDevice physicalDevice = findPhysicalDevice( source
            .getPhysicalDevice() );
        resource.setPhysicalDevice( physicalDevice );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PhysicalPortCharacteristic> newChars = (Set<PhysicalPortCharacteristic>)resource
            .getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populatePipe( Pipe resource, ConnectivityType source )
        throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        PipeSpecification spec = findSpecification(
            PipeSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        if (source.getExternalIdentity() != null) {
            resource.setExternalObjectId( source.getExternalIdentity().getExternalObjectId() );
            resource.setExternalName( source.getExternalIdentity().getExternalName() );
            resource.setExternalManagementDomain( source.getExternalIdentity().getExternalManagementDomain() );
        }
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<PipeCharacteristic> newChars = (Set<PipeCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateService( Service resource, ServiceType source )
        throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        ServiceSpecification spec = findSpecification(
            ServiceSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        if (source.getExternalIdentity() != null) {
            resource.setExternalObjectId( source.getExternalIdentity().getExternalObjectId() );
            resource.setExternalName( source.getExternalIdentity().getExternalName() );
            resource.setExternalManagementDomain( source.getExternalIdentity().getExternalManagementDomain() );
        }
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // activity
        // configurations
        // parties
        // places
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<ServiceCharacteristic> newChars = (Set<ServiceCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    private static void populateTelephoneNumber( TelephoneNumber resource,
        TelephoneNumberType source ) throws ValidationException
    {
        SpecificationType sourceSpec = source.getSpecification();
        TelephoneNumberSpecification spec = findSpecification(
            TelephoneNumberSpecification.class, sourceSpec.getName() );
        if( spec == null )
        {
            log.validationException("ws.entityUtilsCannotFindSpec", new java.lang.IllegalArgumentException(), sourceSpec.getName());
        }
        resource.setSpecification( spec );
        resource.setId( source.getId() );
        resource.setName( source.getName() );
        resource.setDescription( source.getDescription() );
        // activity
        // groups
        List<PropertyType> props = source.getPropertyList();
        for( PropertyType p : props )
        {
            setValue( resource, p.getName(), p.getValue() );
        }
        Set<TNCharacteristic> newChars = (Set<TNCharacteristic>)resource.getCharacteristics();
        defaultMissingCharacteristics( resource, newChars, spec
            .getCharacteristicSpecUsages() );
        resource.setCharacteristics( newChars );
    }

    public static <E extends Specification> E findSpecification(Class<E> type, String name) {
        if(specMgr == null) {
            specMgr = PersistenceHelper.makeSpecManager();
        }
        return specMgr.findSpecification(type, name);
    }

    @SuppressWarnings("unused")
    private static DiscreteCharSpecValue getDefaultCharacteristicValue(
        CharacteristicSpecification spec )
    {
        Set<CharacteristicSpecValue> validValues = spec.getValues();
        DiscreteCharSpecValue firstValue = null;
        for( CharacteristicSpecValue csv : validValues )
        {
            if( csv instanceof DiscreteCharSpecValue )
            {
                DiscreteCharSpecValue dcsv = (DiscreteCharSpecValue) csv;
                if( firstValue == null )
                    firstValue = dcsv;
                if( dcsv.getDefaultValue() )
                {
                    return dcsv;
                }
            }
        }
        return firstValue; // default value of last resort
    }

    public static <E extends CharValue> void defaultMissingCharacteristics(
        CharacteristicExtensible<E> entity, Set<E> newChars,
        Set<CharacteristicSpecUsage> specs )
    {
        for( CharacteristicSpecUsage csu : specs )
        {
        	CharacteristicSpecification spec = csu.getCharacteristicSpecification();
        	
        	DiscreteCharSpecValue defaultValue = null;
        	
        	//Get the default value from the char specification usage defined for characteristic specification in the context of specification
            
        	DiscreteCharSpecValueUsage defaultValueUsage = CharacteristicHelper.getDefaultValueUsage(csu);
            
            if ( defaultValueUsage == null ) {
            	// Get the default value from CharacteristicSpecValues defined characteristic specification
            	defaultValue = CharacteristicHelper.getDefaultValue(spec);
            }
            
            if( !csu.getRequired() && ( defaultValueUsage == null && defaultValue == null ) ) {
                continue; // skip it
            }
            
            int count = countCharacteristic( newChars, spec.getName() );
            if( count < spec.getMinLength() || count == 0 )
            {
                int missing = (count == 0) ? 1 : spec.getMinLength() - count;
                for( int i = 0; i < missing; i++ )
                {
                    E newChar = entity.makeCharacteristicInstance();
                    newChar.setCharacteristicSpecification( spec );
                    newChar.setName( spec.getName() );
                    newChar.setLabel( spec.getDisplayLabel() );
                    if ( defaultValueUsage != null )
                        newChar.setValue( defaultValueUsage.getValue() );
                    else if ( defaultValue != null )
                    	newChar.setValue( defaultValue.getValue() );
                    else if ( ControlType.CHECKBOX.equals( spec.getControlType() ) )
                        newChar.setValue( "false" );
                    else
                    {
                        log.warn( "design.characteristicHasNoDefaultValue", spec.getName() );
                    }
                    newChars.add( newChar );
                }
            }
        }
    }

    private static int countCharacteristic( Set<? extends CharValue> chars,
        String name )
    {
        int count = 0;
        for( CharValue c : chars )
        {
            if( name.equals( c.getName() ) )
                count++;
        }
        return count;
    }

    private static Equipment findEquipment( String id )
    {
        EquipmentManager manager = PersistenceHelper.makeEquipmentManager();
        EquipmentSearchCriteria criteria = manager
            .makeEquipmentSearchCriteria();
        CriteriaItem idSearch = criteria.makeCriteriaItem();
        idSearch.setOperator( CriteriaOperator.EQUALS );
        idSearch.setValue( id );
        criteria.setId( idSearch );
        try
        {
            List<Equipment> results = manager.findEquipment( criteria );
            if( results.size() == 0 )
                return null;
            return results.get( 0 );
        }
        catch( ValidationException e )
        {
            return null;
        }
    }

    private static LogicalDevice findLogicalDevice( String id )
    {
        LogicalDeviceManager manager = PersistenceHelper
            .makeLogicalDeviceManager();
        LogicalDeviceSearchCriteria criteria = manager
            .makeLogicalDeviceSearchCriteria();
        CriteriaItem idSearch = criteria.makeCriteriaItem();
        idSearch.setOperator( CriteriaOperator.EQUALS );
        idSearch.setValue( id );
        criteria.setId( idSearch );
        try
        {
            List<LogicalDevice> results = manager.findLogicalDevice( criteria );
            if( results.size() == 0 )
                return null;
            return results.get( 0 );
        }
        catch( ValidationException e )
        {
            return null;
        }
    }

    private static PhysicalDevice findPhysicalDevice( String id )
    {
        EquipmentManager manager = PersistenceHelper.makeEquipmentManager();
        PhysicalDeviceSearchCriteria criteria = manager
            .makePhysicalDeviceSearchCriteria();
        CriteriaItem idSearch = criteria.makeCriteriaItem();
        idSearch.setOperator( CriteriaOperator.EQUALS );
        idSearch.setValue( id );
        criteria.setId( idSearch );
        try
        {
            List<PhysicalDevice> results = manager
                .findPhysicalDevices( criteria );
            if( results.size() == 0 )
                return null;
            return results.get( 0 );
        }
        catch( ValidationException e )
        {
            return null;
        }
    }

    private static EquipmentHolderEquipmentRel makeChildEquipmentRel(
        EquipmentHolder parent, Equipment child )
    {
        EquipmentManager manager = PersistenceHelper.makeEquipmentManager();
        EquipmentHolderEquipmentRel result = manager
            .makeEquipmentHolderEquipmentRel();
        result.setEquipmentHolder( parent );
        result.setEquipment( child );
        return result;
    }

    public static CharacteristicSpecification getCharacteristicSpecification(
        Specification spec, String name )
    {
        Set<CharacteristicSpecUsage> charSpecs = spec
            .getCharacteristicSpecUsages();
        if( charSpecs.size() == 0 )
            return null;
        for( CharacteristicSpecUsage csu : charSpecs )
        {
            CharacteristicSpecification cs = csu
                .getCharacteristicSpecification();
            if( name.equals( cs.getName() ) )
                return cs;
        }
        return null;
    }

//    public static Specification getSpecification( String name )
//    {
//        Specification result = null;
//        SpecManager specMgr = PersistenceHelper.makeSpecManager();
//        try
//        {
//            result = specMgr.findLatestActiveSpec( name, Specification.class,
//                false );
//        }
//        catch( Exception e )
//        {
//        }
//        return result;
//    }

//    @SuppressWarnings("unchecked")
//    public static void setProperty( Persistent target, String name, Object value )
//    {
//        Class<?> propertyType = null;
//        Setter setter = null;
//        try
//        {
//            Method getter = target.getClass().getMethod(calcGetter( name ) );
//            propertyType = getter.getReturnType();
//            setter = new PropertySetter( target, name, propertyType );
//        }
//        catch( Exception e )
//        {
//        }
//        if( setter == null && target instanceof CharacteristicExtensible )
//        {
//            setter = new CharacteristicSetter(
//                (CharacteristicExtensible) target, name );
//        }
//        if( setter != null )
//        {
//            setter.setValue( value );
//        }
//        else
//        {
//            log.warn( "design.propertyNotFound", name, target.getOid() );
//        }
//    }
    
  public static String calcGetter( String property )
  {
      StringBuffer result = new StringBuffer( "get" );
      if( property.length() > 0 )
          result.append( property.substring( 0, 1 ).toUpperCase() );
      if( property.length() > 1 )
          result.append( property.substring( 1 ) );
      return result.toString();
  }

    public static ReservedForType toReservedForType(
        ReservedForTypeEnum.Enum type ) throws ValidationException
    {
        if( ReservedForTypeEnum.AREA.equals( type ) )
            return ReservedForType.AREA;
        if( ReservedForTypeEnum.CSR.equals( type ) )
            return ReservedForType.CSR;
        if( ReservedForTypeEnum.CUSTOMER.equals( type ) )
            return ReservedForType.CUSTOMER;
        if( ReservedForTypeEnum.ORDER.equals( type ) )
            return ReservedForType.ORDER;
        if( ReservedForTypeEnum.PROJECT.equals( type ) )
            return ReservedForType.PROJECT;
        if( ReservedForTypeEnum.SERVICE.equals( type ) )
            return ReservedForType.SERVICE;
        if( ReservedForTypeEnum.SERVICE_SPEC.equals( type ) )
            return ReservedForType.SERVICE_SPEC;
        if( ReservedForTypeEnum.SUBSCRIBER.equals( type ) )
            return ReservedForType.SUBSCRIBER;
        throw new ValidationException( type + " ReservedForType not recognized" );
    }

    public static Persistent getEntity( XmlObject entity )
        throws ValidationException
    {
        if( entity == null )
            return null;
        XmlObject[] idNodes = entity.selectChildren( null, "id" );
        if( idNodes.length < 1 )
            return null;
        String id = ((XmlString) idNodes[0]).getStringValue();
        EntityClassEnum.Enum ecEnum = EntityUtils.toEntityClass( entity );
        Class<? extends Persistent> ec = EntityUtils.toEntityClass( ecEnum );
        Finder finder = PersistenceHelper.makeFinder();
        Collection<? extends Persistent> results = finder.findById( ec, id );
        Persistent result = null;
        if( ! results.isEmpty() )
            result = results.iterator().next();
        finder.close();
        return result;
    }
    
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void setValue( CharacteristicExtensible target, String name, Object value )
  {
      Set<CharValue> chars = (Set<CharValue>)target.getCharacteristics();
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
      // leave the label unset - CharacteristicManager.validateDropdownList
      // will set it later.
  }
  
  public static void validateStringType(ParameterType type, String name) throws ValidationException {
		
      org.apache.xmlbeans.XmlObject xmlObject = (org.apache.xmlbeans.XmlObject) type.getValue();
      if (! (xmlObject instanceof org.apache.xmlbeans.XmlString)) 
			log.validationError("businessInteraction.validateParamTypeStringError", new java.lang.IllegalArgumentException(), name);

  }
  
  public static String getStringType(ParameterType type, String name) throws ValidationException {
    String paramString = null;

    org.apache.xmlbeans.XmlObject xmlObject = (org.apache.xmlbeans.XmlObject) type.getValue();

    if (xmlObject instanceof org.apache.xmlbeans.XmlString) {
      org.apache.xmlbeans.XmlString xmlString = (org.apache.xmlbeans.XmlString) xmlObject;
      paramString = xmlString.getStringValue();
      if( log.isDebugEnabled() ){
        log.debug("", "getStringType: The ParameterType is a string = " + paramString+" for "+name);
      }
    } 
    return paramString;
  }

  public static void validateBooleanType(ParameterType type, String name) throws ValidationException {

    org.apache.xmlbeans.XmlObject xmlObject = (org.apache.xmlbeans.XmlObject) type.getValue();
    if (! (xmlObject instanceof org.apache.xmlbeans.XmlBoolean)) 
        log.validationError("businessInteraction.validateParamTypeBooleanError", new java.lang.IllegalArgumentException(), name);

  }
  
  public static String getBooleanType(ParameterType type, String name) throws ValidationException {
      
    Boolean paramBoolean = false;
    String returnValue = null;

    org.apache.xmlbeans.XmlObject xmlObject = (org.apache.xmlbeans.XmlObject) type.getValue();

    if (xmlObject instanceof org.apache.xmlbeans.XmlBoolean) {
        
      org.apache.xmlbeans.XmlBoolean xmlBoolean = (org.apache.xmlbeans.XmlBoolean) xmlObject;
      paramBoolean = xmlBoolean.getBooleanValue();
      if(log.isDebugEnabled()){
        log.debug("", "getBooleanType: the ParameterType is a boolean = " + paramBoolean.toString()+" for "+name);
      }
      returnValue = paramBoolean.toString();
    } 
    return returnValue;
  }


  public static void validateIntegerType(ParameterType type, String name) throws ValidationException {
		
      org.apache.xmlbeans.XmlObject xmlObject = type.getValue();
      if (!(xmlObject instanceof org.apache.xmlbeans.XmlInt)) {
        log.validationError("businessInteraction.validateParamTypeNumberError", new java.lang.IllegalArgumentException(), name);
      }
  }
  
  public static String getIntegerType(ParameterType type, String name) throws ValidationException {
    int paramInt = 0;
    String returnValue = null;
    org.apache.xmlbeans.XmlObject xmlObject = type.getValue();
    if (xmlObject instanceof org.apache.xmlbeans.XmlInt) {
      org.apache.xmlbeans.XmlInt xmlInt = (org.apache.xmlbeans.XmlInt) xmlObject;
      paramInt = xmlInt.getIntValue();
      if(log.isDebugEnabled()){
        log.debug("", "getIntegerType: the ParameterType is an int = " + paramInt+" for "+name);
      }
      returnValue = String.valueOf(paramInt);
    } 
    return returnValue;
  }


}
