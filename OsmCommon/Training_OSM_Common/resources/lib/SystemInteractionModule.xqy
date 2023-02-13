module namespace systeminteractionmodule    = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule";

import module namespace uimlib              = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";

declare namespace UUID                      = "java:java.util.UUID";
declare namespace oms                       = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                    = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace bi                        = "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction";
declare namespace invbi                     = "http://xmlns.oracle.com/communications/inventory/businessinteraction";

(: functions :)

(: 
 : Function to generate a unique UUID
:)

declare function systeminteractionmodule:generateUniqueId()
    as xs:string {
    string(UUID:randomUUID())
};

(:This function generate unique CorrelationID:)
declare function systeminteractionmodule:setCorrelationID (
     $sOrderId as xs:string,
     $sVersion as xs:string) {

    let $sUniqueId                          := systeminteractionmodule:generateUniqueId()
    let $sCorrelationID                     :=  fn:concat($sOrderId, '_', $sUniqueId, '_', $sVersion)
    return
        $sCorrelationID  
};

declare function systeminteractionmodule:getSourceLineOrderItemsFromTransformedLineItem(
    $eTransformedOrderLineItem as element (),
    $eMappingContext as element (),
    $eComponent as element()) as element ()*
{
    let $sTransformedOrderLineItemLineId   := $eTransformedOrderLineItem/oms:orderItemRef/toibns:LineId/text()
    let $eTargetMapping                    := $eMappingContext/oms:ProviderFunction/oms:TargetMapping[oms:target/text()=$sTransformedOrderLineItemLineId]
    
    return (
        for $eSourceMap in $eTargetMapping/oms:SourceMapping
        let $sSourceMapLineId := $eSourceMap/oms:source/text()
        return
            $eComponent/oms:orderItem[oms:orderItemRef/oms:LineId/text()=$sSourceMapLineId]
    )
};

declare function systeminteractionmodule:getPrimarySourceLineOrderItemsFromTransformedLineItem(
    $eTransformedOrderLineItem as element (),
    $eMappingContext as element (),
    $eComponent as element()) as element ()*
{
    let $sTransformedOrderLineItemLineId := $eTransformedOrderLineItem/oms:orderItemRef/toibns:LineId/text()
    let $eTargetMapping := $eMappingContext/oms:ProviderFunction/oms:TargetMapping[oms:target/text()=$sTransformedOrderLineItemLineId]
    
    return (
            for $eSourceMap in  $eTargetMapping/oms:SourceMapping
            let $sSourceMapLineId := $eSourceMap/oms:source/text()
            
            where (fn:contains($eSourceMap/oms:InstantiatingMappingRule/oms:name/text(),"Primary"))
            return
                $eComponent/oms:orderItem[oms:orderItemRef/oms:LineId/text()=$sSourceMapLineId]
    )
};


(: Creates the OSM Order Data Update after a captureInteractionResponse  message :)
declare function systeminteractionmodule:createOrderDataUpdateForCaptureInteraction(
    $processResponse as element()*,
    $orderData as element()*) as element()* {

    let $functionName := $uimlib:DesignServiceFunction
    let $componentKey := $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:componentKey/text()
    let $bid := $processResponse/bi:interaction/invbi:header/invbi:id/text()
    let $biCorrelationId := $orderData/oms:Reference/text()
    return
           <OrderDataUpdate xmlns="http://www.metasolv.com/OMS/OrderDataUpdate/2002/10/25"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <UpdatedNodes>
            <_root>{
                        if(exists($bid))then(
                        <BusinessInteractions>
                            <BusinessInteraction>
                                <Key>{$componentKey}</Key>                      
                                <InteractionID>{$bid}</InteractionID>
                                <CorrelationID>{$biCorrelationId}</CorrelationID>
                            </BusinessInteraction>
                        </BusinessInteractions>
                        )else()
                  } 
            </_root>
        </UpdatedNodes>
       </OrderDataUpdate>
};

(: Creates the bi:captureInteractionRequest element. 
Child elements should be created directly here. Groups should be created through a call to another function :)
declare function systeminteractionmodule:addCaptureInteraction(
    $orderData as element()*) as element()*
{ 
    (: <bi:captureInteractionRequest :)    
    let $elementname := fn:QName($uimlib:biNamespace, concat($uimlib:biPrefix, $uimlib:captureInteractionRequest))
    
    where(exists($orderData))
    return
        element {$elementname } {
            uimlib:createQualifiedElementFromString($uimlib:comNamespace, $uimlib:comPrefix, $uimlib:header, ''),
            systeminteractionmodule:addInteraction($orderData),
            uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:executeProcess, $uimlib:true),
            uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:responseLevel, $uimlib:AllExpandedLevel)
        }
};

(: Create the bi:processInteractionRequest element :)
declare function systeminteractionmodule:addProcessInteraction(
    $orderData as element()*) as element()*
{ 
   (: <bi:processInteractionRequest> :)
   let $elementname := fn:QName($uimlib:biNamespace, fn:concat($uimlib:biPrefix, $uimlib:processInteractionRequest))
   
   where (exists($orderData ))
   return
       element {$elementname } {
           uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:responseLevel, $uimlib:EntityConfigurationLevel),
           systeminteractionmodule:addInteraction($orderData)

       }
};

(: Creates the bi:interaction element :)
declare function systeminteractionmodule:addInteraction(
    $orderData as element()*) as element()*
{ 
    (: <bi:interaction> :)
    let $elementname    := fn:QName($uimlib:biNamespace, fn:concat($uimlib:biPrefix, $uimlib:interaction)) 
    let $taskName       := $orderData/oms:Task/text()

    where (fn:exists($orderData))
    return
        element {$elementname} {
            systeminteractionmodule:addInteractionHeader($orderData),
            if($taskName=$uimlib:CaptureBITask)
            then systeminteractionmodule:addInteractionBody($orderData)
            else ()
        }
};

(: Creates the invbi:header element :)
declare function systeminteractionmodule:addInteractionHeader(
    $orderData as element() *) as element()*
{
    (: <invbi:header> :)
    let $elementname            := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:header))
    let $taskName               := $orderData/oms:Task/text()
    let $bicorrID               := uimlib:getBICorrelationID($orderData)/text()
    let $bicorrIDModified       := if(fn:empty($bicorrID))
                                   then ''
                                   else $bicorrID
    let $friendlyReference := $orderData/oms:Reference
    
      where (exists($orderData))
        return
            element {$elementname} {
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:id, $bicorrIDModified),
                if($taskName=$uimlib:CaptureBITask)then(
                uimlib:createQualifiedSpecificationName($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:sBIOrder),
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $uimlib:CREATE),         
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:name, $uimlib:ossintegration),
               (: uimlib:createFriendlyQualifiedExternalIdentity($uimlib:invbiNamespace, $uimlib:invbiPrefix, $bicorrID, $friendlyReference),:)
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:effDate, (current-dateTime() cast as xs:string) )
                )else()
       }
};

(: Creates the invbi:body element :)
declare function systeminteractionmodule:addInteractionBody(
    $orderData as element()*) as element()*
{
    (: <invbi:body> :)
    let $elementname := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:body))   
    where(exists($orderData))
    return
        element {$elementname } {
        (
            (:for $oi in $orderData/oms:_root/oms:ControlData/oms:OrderItem:)
            for $oi in $orderData/oms:_root/oms:ControlData/oms:Functions/oms:DesignServiceFunction/oms:orderItem/oms:orderItemRef
            let $action := uimlib:getOIActionCode($oi)
             return
                (: If service domains require a different request, they should copy this file and re-implement processing from this point on :)
                systeminteractionmodule:createItemForCaptureRequest($oi, $action/text(),$orderData)
         )
       }             
};

(: Creates the invbi:item elements for the CaptureInteractionRequest as needed. This supports the creation of more than one item
Returns a sequence of invbi:item elements :)
declare function systeminteractionmodule:createItemForCaptureRequest(
    $orderitem as element(),
    $action as xs:string,
    $orderData as element()) as item()*
{ 
    (: <invbi:item> :)    
    let $elementname := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:item))    
    where(exists($orderitem))
    return
        element {$elementname } {
            uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $uimlib:ADD),
            systeminteractionmodule:addService($orderitem, $action, $orderData),
            (:local:addSubscriberToCaptureRequest($orderitem),
            local:addAddressToCaptureRequest($orderitem),:)
            systeminteractionmodule:addExtensibleAttributesToParameters($orderitem)
        }
};

(: Creates the invbi:service element :)
declare function systeminteractionmodule:addService(
    $orderitem as element()*,
    $action as xs:string,
    $orderData as element()) as element()*
{ 
    (: <invbi:service> :)
    let $elementname := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:service))
    
    let $invSpec := $orderitem/oms:ServiceSpecification
    
    (: UIM expects serivceAction to start with 'create' not 'add' :)
    let $serviceAction := if($action = $uimlib:PSR_addAction)
        then ($uimlib:PSR_createAction)
        else ($action)
    
    (: Service Correlation ID  :)
    let $serviceCorrelationID := $orderData/oms:Reference/text()
     
    where(exists($orderitem))
    return
        element {$elementname } {
            if (fn:exists($orderitem/oms:ServiceId) and $orderitem/oms:ServiceId/text() != "" and $orderitem/oms:ServiceId/text() != "no-service-id")
            then uimlib:createQualifiedElementFromString($uimlib:svcNamespace, $uimlib:svcPrefix,  $uimlib:id, $orderitem/oms:ServiceId/text())
            else (),                
            uimlib:createQualifiedSpecificationName($uimlib:svcNamespace, $uimlib:svcPrefix, $invSpec),
            uimlib:createQualifiedElementFromString($uimlib:svcNamespace, $uimlib:svcPrefix,  $uimlib:action, $serviceAction),
            uimlib:createQualifiedElementFromString($uimlib:svcNamespace, $uimlib:svcPrefix, $uimlib:name, fn:replace($orderitem/oms:LineName/text(), "_", " ")), 
            uimlib:createQualifiedExternalIdentity( $uimlib:svcNamespace, $uimlib:svcPrefix,  $serviceCorrelationID)
      }
};

(: Creates the invbi:item elements for the CaptureInteractionRequest as needed. This supports the creation of more than one item
Returns a sequence of invbi:item elements :)
declare function systeminteractionmodule:addExtensibleAttributesToParameters(
    $orderitem as element()*) as element()*
{ 
    (: Dynamic Params :)
    
     (: <invbi:parameter> :)        
    for $attribute in $orderitem/oms:dynamicParams/node()
    return 
        if(exists($attribute/*)) 
        then (
            systeminteractionmodule:addComplexParameter($attribute)
        )
        else
        (
            systeminteractionmodule:addSimpleParameter($attribute)
        ) 
          
};

declare function systeminteractionmodule:addSimpleParameter(
    $attribute as element()*) as element()*
{
    let $name := fn:local-name($attribute)
    let $value := $attribute/text()
    
    (: Only populate attributes that have a value :)
    where(exists($value))
    return (
        (: Need to convert OSM boolean (Yes/No) to true boolean for UIM (true/false) :)
        if($value='Yes' or $value='No')
        then (
            uimlib:createQualifiedBooleanParameter($uimlib:invbiNamespace, $uimlib:invbiPrefix, $name, uimlib:convertYesNoToTrueFalse($value))
        )
        else (
            (: Send the attribute element and constructor method will extract value :)
            uimlib:createQualifiedStringParameter($uimlib:invbiNamespace, $uimlib:invbiPrefix, $name, $attribute)
        )
    )
};

(: Creates a config item for the top of the structure. Simple parameters are then created as properties and nested structures created as nested config items :)
declare function systeminteractionmodule:addComplexParameter(
    $structure as element()) as element()*
{
    let $parameterElement := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:parameter))
    let $valueElement := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:value))    
    
    return
            element{$parameterElement}{
                <invbi:name>{fn:local-name($structure)}</invbi:name>,
                element{$valueElement} {
                    attribute{"xsi:type"}{data("invstruc:StructuredType")},
                    uimlib:createQualifiedElementFromString($uimlib:invstrucNamespace, $uimlib:invstrucPrefix, $uimlib:name, fn:local-name($structure)),
                        for $nested in $structure/node()
                        return
                            if(exists($nested/*)) 
                            then (
                                systeminteractionmodule:addChildStructure($nested)
                            )
                            else
                            (
                                systeminteractionmodule:addStructureProperty($nested)
                            ) 
                           
                }
            }
  
};

declare function systeminteractionmodule:addChildStructure(
    $attribute as element()*) as element()*
{
    (: nested structures are placed inside <child> element :)
    let $childelement := fn:QName($uimlib:invstrucNamespace, concat($uimlib:invstrucPrefix, $uimlib:child))
    let $structurename := fn:local-name($attribute)
    
    return 
        element{$childelement} {
        attribute{"xsi:type"}{data("invstruc:StructuredType")},
        uimlib:createQualifiedElementFromString($uimlib:invstrucNamespace, $uimlib:invstrucPrefix, $uimlib:name, $structurename),
            for $nested in $attribute/node()
            return
                if(exists($nested/*))
                then (
                    systeminteractionmodule:addChildStructure($nested)
                )
                else (
                    systeminteractionmodule:addStructureProperty($nested)
                )
               
        }
};

(: Adds property elements in the StructuredType namespace with Name/Value child elements
 <invstruc:property>
    <invprop:name>username</invprop:name>
    <invprop:value>PhilipTheFair</invprop:value>
</invstruc:property>
:)
declare function systeminteractionmodule:addStructureProperty(
    $attribute as element()*) as element()*
{
    let $name := fn:local-name($attribute)
    let $value := $attribute/text()
    
    (: Only populate attributes that have a value :)
    where(exists($value))
    return 
            (: Send the attribute element and constructor method will extract value :)
            systeminteractionmodule:createQualifiedStringProperty($uimlib:invstrucNamespace, $uimlib:invstrucPrefix, $name, $attribute)
        
};

(: Creates a namespace qualified parameter element with name/value child elements, where the type of the value element is xs:string 
<$prefix:property>
    <invprop:name>$tmpname</invprop:name>
    <invprop:value xsi:type="xs:string">$tmpvalue</invprop:value>
</$prefix:property>  :)
declare function systeminteractionmodule:createQualifiedStringProperty(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
    
    systeminteractionmodule:createProperty($namespace, $prefix, 'xs:string', $tmpname, $tmpvalue)   

};

(: Creates a namespace qualified property element with name/value child elements where the value element also has an xsi type
<$prefix:property>
    <invprop:name>$tmpname</invprop:name>
    <invprop:value xsi:type="$xsitype">$tmpvalue</invprop:value>
</$prefix:property>  :)
declare function systeminteractionmodule:createProperty(
            $namespace as xs:string,
            $prefix as xs:string,
            $xsitype as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
            
    let $propertyelement := fn:QName($namespace, fn:concat($prefix, $uimlib:property))
    let $valueElement := uimlib:createQualifiedElementWithXSIType($uimlib:invpropNamespace, $uimlib:invpropPrefix, $uimlib:value, $xsitype, $tmpvalue)
    
    return 
    element {$propertyelement} {
            uimlib:createQualifiedElementFromString($uimlib:invpropNamespace, $uimlib:invpropPrefix, $uimlib:name, $tmpname),
            $valueElement
        }
    
};