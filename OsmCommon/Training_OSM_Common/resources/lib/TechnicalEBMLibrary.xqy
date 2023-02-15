(: Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. :)

(: This class represents helper functions for creating Technical Order EBM sections :)
module namespace techebm                                        = "http://xmlns.oracle.com/comms/ordermanagement/oss/ebm/technical/library";

(:Default OSM namespace :)
declare namespace oms                                           = "urn:com:metasolv:oms:xmlapi:1";

(: AIA  namespaces :)
declare namespace corecom                                       = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace techord                                       = "http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1";

declare namespace javaSystem                                    = "java:java.lang.System";
declare namespace javaPrintStream                               = "java:java.io.PrintStream";
declare namespace javaString                                    = "java:java.lang.String";

declare option saxon:output "method=xml";
declare option saxon:output "saxon:indent-spaces=4";
declare variable $techebm:printStream                           := javaSystem:out(); 

(: Prefixes required to create QNames :)
declare variable $techebm:corecomPrefix                         := "corecom:";
declare variable $techebm:corecomNamespace                      := "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare variable $techebm:techordNamespace                      := "http://www.tomtraining.com/inputMessage";
declare variable $techebm:techordPrefix                         := "techord:";

(: String constants :)
declare variable $techebm:ClassificationCode                    := "ClassificationCode";
declare variable $techebm:FulfillmentItemCode                   := "FulfillmentItemCode";
declare variable $techebm:listID                                := "listID";
declare variable $techebm:ItemReference                         := "ItemReference";
declare variable $techebm:ServiceInventoryRefIdentity           := "ServiceInventoryRefIdentity";
declare variable $techebm:CFSReference                          := "CFSReference";
declare variable $techebm:ServiceCommercialRefIdentity          := "ServiceCommercialRefIdentity";
declare variable $techebm:TechnicalAction                       := "TechnicalAction";
declare variable $techebm:ActionCode                            := "ActionCode";
declare variable $techebm:SpecializedActionCode                 := "SpecializedAction";
declare variable $techebm:FulfillmentSystemType                 := "FulfillmentSystemType";
declare variable $techebm:Target                                := "Target";
declare variable $techebm:ID                                    := "ID";
declare variable $techebm:Role                                  := "Role";
declare variable $techebm:Specification                         := "Specification";
declare variable $techebm:OutputSpecification                   := "OutputSpecification";
declare variable $techebm:SpecificationGroup                    := "SpecificationGroup";
declare variable $techebm:Name                                  := "Name";
declare variable $techebm:TechnicalParameters                   := "TechnicalParameters";
declare variable $techebm:Value                                 := "Value";
declare variable $techebm:Direction                             := "Direction";
declare variable $techebm:out                                   := "out";
declare variable $techebm:in                                    := "in";
declare variable $techebm:OutputPath                            := "OutputPath";
declare variable $techebm:ReferToParent                         := "ReferToParent";
declare variable $techebm:ConfigId                              := "ConfigId";
declare variable $techebm:ConfigVersion                         := "ConfigVersion";
declare variable $techebm:Version                               := "Version";
declare variable $techebm:DataTypeCode                          := "DataTypeCode";
declare variable $techebm:SolRefIdentity                        := "SolRefIdentity";
declare variable $techebm:OriginalTolRefIdentity                := "OriginalTolRefIdentity";
declare variable $techebm:ParentTolIdentity                     := "ParentTolIdentity";
declare variable $techebm:EffectiveTimePeriod                   := "EffectiveTimePeriod";
declare variable $techebm:StartDateTime                         := "StartDateTime";
declare variable $techebm:EndDateTime                           := "EndDateTime";
declare variable $techebm:RequestedDeliveryDateTime             := "RequestedDeliveryDateTime";
declare variable $techebm:ExpectedDeliveryDateTime              := "ExpectedDeliveryDateTime";
declare variable $techebm:EarliestDeliveryDateTime              := "EarliestDeliveryDateTime";
declare variable $techebm:ServiceAddress                        := "ServiceAddress";
declare variable $techebm:LineOne                               := "LineOne";
declare variable $techebm:LineTwo                               := "LineTwo";
declare variable $techebm:CityName                              := "CityName";
declare variable $techebm:StateName                             := "StateName";
declare variable $techebm:CountryCode                           := "CountryCode";
declare variable $techebm:PostalCode                            := "PostalCode";
declare variable $techebm:updateProvEbmName                     := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}UpdateProvisioningOrderEBM";
declare variable $techebm:updateProvEboName                     := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}ProvisioningOrderEBO";
declare variable $techebm:TechnicalActionCode                   := "TechnicalActionCode";
declare variable $techebm:ProductClass                          := "ProductClass";
declare variable $techebm:TOL                                   := "TOL";
declare variable $techebm:ExtendedProperties                    := "ExtendedProperties";
declare variable $techebm:TOM_ACTIVATION_FAILED                 := "TOM_ACTIVATION_FAILED";
declare variable $techebm:BusinessComponentID                   := "BusinessComponentID";

(: Creates an element with the specified name and value :)
declare function techebm:createQualifiedElementFromString(
    $paramURI as xs:string,
    $prefix as xs:string,
    $tmpname as xs:string, 
    $value as xs:string?) as element()* {
        
    let $elementname := fn:QName($paramURI, concat($prefix, $tmpname))

    where (exists($value))
    return
    element {$elementname}  {
        data($value)
    }
};

declare function techebm:createClassificationCode(
    $fic as xs:string*) as element()*
{
    let $elementname := fn:QName($techebm:corecomNamespace, concat($techebm:corecomPrefix, $techebm:ClassificationCode))
  
    return  
    element{$elementname} {
        attribute{$techebm:listID}{data($techebm:FulfillmentItemCode)},
        data($fic)
    }

};

(: Creates the <techord:ParentTolIdentity> element with minimal child elements :)
declare function techebm:createParentTol(
    $id as xs:string*) as element()*
{
    let $parentReference := fn:QName($techebm:techordNamespace, fn:concat($techebm:techordPrefix, $techebm:ParentTolIdentity))

    where (exists($id))
    return
            element{$parentReference} {
                techebm:addBusinessComponentId($id)
            }

};

(: Creates the <corecom:BusinessComponentID> :)
declare function techebm:addBusinessComponentId(
    $id as xs:string) as element()*
{
    techebm:createQualifiedElementFromString($techebm:corecomNamespace, $techebm:corecomPrefix, $techebm:BusinessComponentID, fn:normalize-space($id))
};

(: Creates the <corecom:Identification> element with specified child elements :)
declare function techebm:createIdentification(
    $bcid as xs:string*,
    $id as xs:string*) as element()*
{
        <corecom:Identification>
            <corecom:BusinessComponentID>{$bcid}</corecom:BusinessComponentID>
            <corecom:ID>{$id}</corecom:ID>    
        </corecom:Identification>

};

(: Creates an element with the specified namespace :)
declare function techebm:createQualifiedElement(
    $paramURI as xs:string,
    $prefix as xs:string,
    $tmpname as xs:string, 
    $value as element()*) as element()* {
        
    let $elementname := fn:QName($paramURI, concat($prefix, $tmpname))
    where (exists($value))
    return
        element {$elementname}  {
            data($value)
    }
};

(: Creates the <techord:OriginalTolRefIdentity> element with minimal child elements :)
declare function techebm:createOriginalTolRef(
    $id as xs:string*) as element()*
{
    let $tolReference := fn:QName($techebm:techordNamespace, concat($techebm:techordPrefix, $techebm:OriginalTolRefIdentity))

    where (exists($id))
    return
            element{$tolReference} {
                techebm:addBusinessComponentId($id)
            }

};
