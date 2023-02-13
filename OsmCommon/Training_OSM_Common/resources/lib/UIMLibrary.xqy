(: Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. :)
(: This class represents helper functions for creating UIM data elements that should be standard across service provisioning cartridges. 
To use these functions, include the following line in your xquery:
import module namespace uimlib = "http://xmlns.oracle.com/comms/ordermanagement/oss/uim/library" at "http://oracle.communications.ordermanagement.oss.resources/xquery/library/UIMLibrary.xqy";
:)

module namespace uimlib                                     = "http://www.training.com/comms/ordermanagement/common/uim/library";

declare namespace oms                                       = "urn:com:metasolv:oms:xmlapi:1";

(: namespaces for UIM system interaction :)
declare variable $uimlib:true                               := "true";
declare variable $uimlib:false                              := "false";
declare variable $uimlib:CREATE                             := "CREATE";
declare variable $uimlib:create                             := "create";
declare variable $uimlib:add                                := "add";
declare variable $uimlib:ADD                                := "ADD";
declare variable $uimlib:PSR_addAction                      := "Add";
declare variable $uimlib:PSR_createAction                   := "create";
declare variable $uimlib:input                              := "input";
declare variable $uimlib:body                               := "body";
declare variable $uimlib:Yes                                := "Yes";
declare variable $uimlib:interaction                        := "interaction";
declare variable $uimlib:id                                 := "id";
declare variable $uimlib:ossintegration                     := "ossintegration";
declare variable $uimlib:effDate                            := "effectiveDate";
declare variable $uimlib:header                             := "header";
declare variable $uimlib:action                             := "action";
declare variable $uimlib:item                               := "item";
declare variable $uimlib:service                            := "service";
declare variable $uimlib:characteristic                     := "characteristic";
declare variable $uimlib:property                           := "property";
declare variable $uimlib:name                               := "name";
declare variable $uimlib:party                              := "party";
declare variable $uimlib:value                              := "value";
declare variable $uimlib:parameter                          := "parameter";
declare variable $uimlib:usAddress                          := "usAddress";
declare variable $uimlib:serviceAddress                     := "serviceAddress";
declare variable $uimlib:lineone                            := "addressLine1";
declare variable $uimlib:linetwo                            := "addressLine2";
declare variable $uimlib:city                               := "americanCity";
declare variable $uimlib:state                              := "americanState";
declare variable $uimlib:country                            := "countryCode";
declare variable $uimlib:zipcode                            := "americanZipCode";
declare variable $uimlib:specification                      := "specification";
declare variable $uimlib:externalObjectId                   := "externalObjectId";
declare variable $uimlib:externalIdentity                   := "externalIdentity";
declare variable $uimlib:externalName                       := "externalName";
declare variable $uimlib:comNamespace                       := "http://xmlns.oracle.com/communications/inventory/webservice/common";
declare variable $uimlib:comPrefix                          := "com:";
declare variable $uimlib:techPrefix                         := "tech:";
declare variable $uimlib:techNamespace                      := "http://xmlns.oracle.com/communications/inventory/webservice/technical";
declare variable $uimlib:specPrefix                         := "spec:";
declare variable $uimlib:specNamespace                      := "http://xmlns.oracle.com/communications/inventory/specification";
declare variable $uimlib:charPrefix                         := "char:";
declare variable $uimlib:charNamespace                      := "http://xmlns.oracle.com/communications/inventory/characteristic";
declare variable $uimlib:biPrefix                           := "bi:";
declare variable $uimlib:biNamespace                        := "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction";
declare variable $uimlib:invbiPrefix                        := "invbi:";
declare variable $uimlib:invbiNamespace                     := "http://xmlns.oracle.com/communications/inventory/businessinteraction";
declare variable $uimlib:invpropNamespace                   := "http://xmlns.oracle.com/communications/inventory/property";
declare variable $uimlib:invpropPrefix                      := "invprop:";
declare variable $uimlib:partyPrefix                        := "par:";
declare variable $uimlib:partyNamespace                     := "http://xmlns.oracle.com/communications/inventory/party";
declare variable $uimlib:svcPrefix                          := "ser:";
declare variable $uimlib:svcNamespace                       := "http://xmlns.oracle.com/communications/inventory/service";
declare variable $uimlib:placePrefix                        := "place:";
declare variable $uimlib:placeNamespace                     := "http://xmlns.oracle.com/communications/inventory/place";
declare variable $uimlib:numPrefix                          := "num:";
declare variable $uimlib:numNamespace                       := "http://xmlns.oracle.com/communications/inventory/number";
declare variable $uimlib:entPrefix                          := "ent:";
declare variable $uimlib:entNamespace                       :="http://xmlns.oracle.com/communications/inventory/entity";
declare variable $uimlib:rolePrefix                         := "role:";
declare variable $uimlib:logPrefix                          := "log:";
declare variable $uimlib:completeAction                     := "COMPLETE";
declare variable $uimlib:cancelAction                       := "CANCEL";
declare variable $uimlib:issueAction                        := "ISSUE";
declare variable $uimlib:approveAction                      := "APPROVE";
declare variable $uimlib:updateInteractionRequest           := "updateInteractionRequest";
declare variable $uimlib:processInteractionRequest          := "processInteractionRequest";
declare variable $uimlib:captureInteractionRequest          := "captureInteractionRequest";
declare variable $uimlib:calculateTechnicalActionsRequest   := "calculateTechnicalActionsRequest";
declare variable $uimlib:responseLevel                      := "responseLevel";
declare variable $uimlib:InteractionLevel                   := "INTERACTION";
declare variable $uimlib:ItemExpandedLevel                  := "INTERACTION_ITEM";
declare variable $uimlib:EntityExpandedLevel                := "INTERACTION_ITEM_ENTITY";
declare variable $uimlib:EntityConfigurationLevel           := "INTERACTION_ITEM_ENTITY_CONFIGURATION";
declare variable $uimlib:AllExpandedLevel                   := "INTERACTION_ITEM_ENTITY_CONFIGURATION_EXPANDED";
declare variable $uimlib:executeProcess                     := "executeProcess";
declare variable $uimlib:DesignServiceFunction              := "DesignServiceFunction";

declare variable $uimlib:invstrucNamespace                  := "http://xmlns.oracle.com/communications/inventory/structure";
declare variable $uimlib:invstrucPrefix                     := "invstruc:";
declare variable $uimlib:invconfigNamespace                 := "http://xmlns.oracle.com/communications/inventory/configuration";
declare variable $uimlib:invconfigPrefix                    := "invconfig:";
declare variable $uimlib:child                              := "child";
declare variable $uimlib:sBIOrder                           := "BI_Order";

(: Creates a namespace qualified property element with name/value child elements 
<$prefix:property>
    <invprop:name>$tmpname</invprop:name>
    <invprop:value>$tmpvalue</invprop:value>
</$prefix:property>  :)
declare function uimlib:createQualifiedProperty(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
            
    let $elementname := fn:QName($namespace, concat($prefix, $uimlib:property))
    
    where(exists($tmpvalue))
    return
        (: <$prefix:property> :)
        element {$elementname} {
            (: <invprop:name> and <invprop:value> :)
            uimlib:createQualifiedElementFromString( $uimlib:invpropNamespace, $uimlib:invpropPrefix, $uimlib:name, $tmpname),
            uimlib:createQualifiedElement( $uimlib:invpropNamespace, $uimlib:invpropPrefix, $uimlib:value, $tmpvalue)
        }
    
};

(: Creates a namespace qualified parameter element with name/value child elements, where the type is xs:boolean 
<parameter>
    <name>$tmpname</name>
    <value xsi:type="xs:boolean">$tmpvalue</value>
</parameter>  :)
declare function uimlib:createQualifiedBooleanParameter(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
 
    let $bool := 'xs:boolean'
    where (exists($tmpvalue))
    return
        uimlib:createParameter($namespace, $prefix, $bool, $tmpname, $tmpvalue)  
    
};

(: Creates a namespace qualified parameter element with name/value child elements, where the type is xs:integer 
<parameter>
    <name>$tmpname</name>
    <value xsi:type="xs:integer">$tmpvalue</value>
</parameter>  :)
declare function uimlib:createQualifiedIntegerParameter(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
 
    let $in := 'xs:integer'
    where (exists($tmpvalue))
    return 
        uimlib:createParameter($namespace, $prefix, $in, $tmpname, $tmpvalue)  
    
};

(: Creates a namespace qualified parameter element with name/value child elements, where the type is xs:string 
<parameter>
    <name>$tmpname</name>
    <value xsi:type="xs:string">$tmpvalue</value>
</parameter>  :)
declare function uimlib:createQualifiedStringParameter(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
    
    let $str := 'xs:string'
    where (exists($tmpvalue))
    return
        uimlib:createParameter($namespace, $prefix, $str, $tmpname, $tmpvalue)     

};

(: Creates a namespace qualified parameter element with name/value child elements, where the type is xs:int 
<parameter>
    <name>$tmpname</name>
    <value xsi:type="xs:int">$tmpvalue</value>
</parameter>  :)
declare function uimlib:createQualifiedIntParameter(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
   
    let $in := 'xs:int'
    where (exists($tmpvalue))
    return
        uimlib:createParameter($namespace, $prefix, $in, $tmpname, $tmpvalue)     

};

(: Creates a namespace qualified parameter element with name and value child elements, and the specified xsi:type
<$prefix:parameter>
    <$prefix:name>$tmpname</$prefix:name>
    <$prefix:value xsi:type=$xsitype>$tmpvalue</$prefix:value>
</$prefix:parameter>  :)
declare function uimlib:createParameter(
            $namespace as xs:string,
            $prefix as xs:string,
            $xsitype as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
            
    let $nvname :=  $tmpname (: The content of the <name> element :)
    let $nvvalue := $tmpvalue (: The content of the <value> element :)
    
    let $attrValue := $xsitype
    let $parametername := fn:QName($namespace, concat($prefix, $uimlib:parameter))
    let $valueElement := uimlib:createQualifiedElementWithXSIType($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:value, $attrValue, $nvvalue)
    
        let $elem := element {$parametername} {
                uimlib:createQualifiedElementFromString($namespace, $prefix, $uimlib:name, $nvname),
                $valueElement
        }
        return $elem

};

(: Creates a namespace qualified usAddress specification element
<$prefix:specification>
    <spec:name>usAddress</spec:name>
</$prefix:specification>
:)
declare function uimlib:createQualifiedUSAddressSpec(
            $namespace as xs:string,
            $prefix as xs:string) as element()* { 
    
    uimlib:createQualifiedSpecificationName($namespace, $prefix, $uimlib:usAddress)
};

(: Creates a namespace qualified specification element with name.  
<$prefix:specification>
    <spec:name>$tmpvalue</spec:name>
</$prefix:specification>   :)
declare function uimlib:createQualifiedSpecificationName(
            $namespace as xs:string,
            $prefix as xs:string,
            $value as xs:string) as element()* { 
            
    let $elementname := fn:QName($namespace, concat($prefix, $uimlib:specification))
    
    return
        element {$elementname } {
            uimlib:createQualifiedElementFromString( $uimlib:specNamespace, $uimlib:specPrefix,  $uimlib:name, $value)
        }             
};

(: Creates a namespace qualified externalIdentity group with externalObjectId child element
<$namespace:externalIdentity>
    <ent:externalObjectId>$tmpvalue</ent:externalObjectId>
</$namespace:externalIdentity>  :)
declare function uimlib:createQualifiedExternalIdentity(
        $namespace as xs:string,
        $prefix as xs:string,
        $value as xs:string) as element()* {
        
    let $elementname := fn:QName($namespace, concat($prefix, $uimlib:externalIdentity))

    return
        element{$elementname}{
            uimlib:createQualifiedElementFromString($uimlib:entNamespace, $uimlib:entPrefix, $uimlib:externalObjectId, $value)
        }
};

(: Creates a namespace qualified externalIdentity group with externalObjectId child element and the user friendly value in externalName
<$namespace:externalIdentity>
    <ent:externalObjectId>$value</ent:externalObjectId>
    <ent:externalName>$friendly</ent:externalName>
</$namespace:externalIdentity>  :)
declare function uimlib:createFriendlyQualifiedExternalIdentity(
        $namespace as xs:string,
        $prefix as xs:string,
        $value as xs:string,
        $friendly as xs:string) as element()* {
        
    let $elementname := fn:QName($namespace, concat($prefix, $uimlib:externalIdentity))

    return
        element{$elementname}{
            uimlib:createQualifiedElementFromString($uimlib:entNamespace, $uimlib:entPrefix, $uimlib:externalObjectId, $value),
            uimlib:createQualifiedElementFromString($uimlib:entNamespace, $uimlib:entPrefix, $uimlib:externalName, $friendly)
        }
};

(: Creates an element with the specified namespace :)
declare function uimlib:createQualifiedElement(
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

(: Creates an element with the specified name and value :)
declare function uimlib:createQualifiedElementFromString(
    $paramURI as xs:string,
    $prefix as xs:string,
    $tmpname as xs:string, 
    $value as xs:string) as element()* {
        
    let $elementname := fn:QName($paramURI, concat($prefix, $tmpname))

    where (exists($value))
    return
    element {$elementname}  {
        data($value)
    }
};

(: Creates a namespace qualified element with the name, value and xsi type
<name xsi:type="xs:string">myname</name>
:)
declare function uimlib:createQualifiedElementWithXSIType(
    $namespace as xs:string,
    $prefix as xs:string,
    $tmpelementname as xs:string, 
    $attrValue as xs:string,
    $elementvalue as element()*) as element()* {
        
    let $attrName := "xsi:type"
    let $elementname := fn:QName($namespace, concat($prefix, $tmpelementname))
    let $elem := element{$elementname}{
            attribute{$attrName}{data($attrValue)},
            data($elementvalue)
        }
    return $elem
};

(: Returns the element that represents the Business Interaction Correlation ID. This value would be passed to UIM as the external Object id
of the Business Interaction and would correlate to the business component ID for the AIA EBM Provisioning order :)
declare function uimlib:getBICorrelationID(
             $taskData as element()*) as element()* {
   
       let $corrid := $taskData/oms:_root/oms:CustomerHeaders/oms:Identification/oms:BusinessComponentID
       where (exists($corrid))
          return $corrid
      
};

(: Returns the element that represents the Order Action Code. :)
declare function uimlib:getOIActionCode(
             $orderItem as element()*) as element()* {
   
       let $action := $orderItem/oms:Action
       where (exists($action))
           return $action
      
};

(: Returns the element that represents the ServiceCorrelationID. :)
declare function uimlib:getServiceCorrelationID(
            $orderitem as element()*) as element()* {

       let $id := $orderitem/oms:ServiceCorrelationID
       where (exists($id))
          return $id
      
};

(: Converts boolean represented as Yes/No to a boolean represented by true/false :)
declare function uimlib:convertYesNoToTrueFalse(
    $value as xs:string) as element()* {

    let $yn := $value
    where (exists($yn)) 
    return
        let $elem :=
            if ($yn = $uimlib:Yes)
            then (
                  element{'uimboolean'}{
                    data($uimlib:true)
                }
            )
            else (
                 element{'uimboolean'}{
                    data($uimlib:false)
                }
            )
        return $elem
};
