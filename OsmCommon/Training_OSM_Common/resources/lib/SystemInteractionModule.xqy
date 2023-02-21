module namespace systeminteractionmodule                                    = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule";

import module namespace uimlib                                              = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";

declare namespace UUID                                                      = "java:java.util.UUID";
declare namespace oms                                                       = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                                                    = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace bi                                                        = "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction";
declare namespace invbi                                                     = "http://xmlns.oracle.com/communications/inventory/businessinteraction";
declare namespace ser                                                       = "http://xmlns.oracle.com/communications/inventory/service";
declare namespace ent                                                       = "http://xmlns.oracle.com/communications/inventory/entity";
declare namespace con                                                       = "http://xmlns.oracle.com/communications/inventory/configuration";
declare namespace spec                                                      = "http://xmlns.oracle.com/communications/inventory/specification";
declare namespace techws                                                    = "http://xmlns.oracle.com/communications/inventory/webservice/technical";

declare variable $systeminteractionmodule:ORDER_ITEM_NS                    := "urn:com:metasolv:oms:xmlapi:1";
declare variable $systeminteractionmodule:ORDER_ITEM_TYPE                  := "omsAny";
declare variable $systeminteractionmodule:ORDER_ITEM_FULLTYPE              := fn:concat("{", $systeminteractionmodule:ORDER_ITEM_NS, "}", 
                                                                              $systeminteractionmodule:ORDER_ITEM_TYPE);

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
            systeminteractionmodule:addInteraction('',$orderData),
            uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:executeProcess, $uimlib:false),
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
           systeminteractionmodule:addInteraction('',$orderData)

       }
};

(: Create the bi:updateInteractionRequest element :)
declare function systeminteractionmodule:addUpdateInteraction(
    $tmpaction as xs:string, 
    $orderData as element()*) as element()*
{ 
   (: <bi:updateInteractionRequest> :)
   let $elementname := fn:QName($uimlib:biNamespace, concat($uimlib:biPrefix, $uimlib:updateInteractionRequest))
   
   where (exists($orderData ))
   return
       element {$elementname } {
           uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:responseLevel, $uimlib:InteractionLevel),
           systeminteractionmodule:addInteraction($tmpaction, $orderData)
       }
};

(: Creates the bi:interaction element :)
declare function systeminteractionmodule:addInteraction($tmpaction as xs:string?,
    $orderData as element()*) as element()*
{ 
    (: <bi:interaction> :)
    let $elementname    := fn:QName($uimlib:biNamespace, fn:concat($uimlib:biPrefix, $uimlib:interaction)) 
    let $taskName       := $orderData/oms:Task/text()

    where (fn:exists($orderData))
    return
        element {$elementname} {
            systeminteractionmodule:addInteractionHeader($tmpaction,$orderData),
            if($taskName=$uimlib:CaptureBITask)
            then systeminteractionmodule:addInteractionBody($orderData)
            else ()
        }
};

(: Creates the invbi:header element :)
declare function systeminteractionmodule:addInteractionHeader(
    $tmpaction as xs:string?,
    $orderData as element() *) as element()*
{
    (: <invbi:header> :)
    let $elementname            := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:header))
    let $taskName               := $orderData/oms:Task/text()
    let $bicorrID               := uimlib:getBICorrelationID($orderData)/text()
    let $bicorrIDModified       := if(fn:empty($bicorrID))
                                   then ''
                                   else $bicorrID
    let $friendlyReference      := $orderData/oms:Reference
    let $interactionName        := "Broadband CFS Create Order"        
    
      where (exists($orderData))
        return
            element {$elementname} {
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:id, $bicorrIDModified),
                if($taskName=($uimlib:ApproveTask,$uimlib:IssueTask,$uimlib:CompleteTask))then(
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $tmpaction)
                )else(),
                if($taskName=$uimlib:CaptureBITask)then(
                uimlib:createQualifiedSpecificationName($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:sBIOrder),
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $uimlib:CREATE),         
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:name, $interactionName),
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
    let $serviceCorrelationID := fn:substring-after($orderitem/oms:LineId/text(),'CSO_')
     
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

(: ************************************** Functions to handle a ProcessInteractionResponse message  **************************************************************  :)

(: Creates the OSM Order Data Update after a processInteractionResponse message :)
declare function systeminteractionmodule:createOrderDataUpdateForDesignInteraction(
            $processResponse as element()*,
            $orderData as element()*) as element()* {

    let $functionName := $uimlib:DesignServiceFunction
    let $componentKey := $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:componentKey/text()
    let $sTaskName := $orderData/oms:Task/text()
    let $sOrderID := $orderData/oms:OrderID/text()
        
    return
           <OrderDataUpdate xmlns="http://www.metasolv.com/OMS/OrderDataUpdate/2002/10/25"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <UpdatedNodes>
                <_root>                
                    <ControlData>
                        <Functions>
                        {
                            element{$functionName}{
                                <componentKey>{$componentKey}</componentKey>,
                                (: Need to loop through the 'OrderItem's and update each one with their specific info :)
                                for $oi in $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:orderItem
                                let $osmitem := $oi/oms:orderItemRef
                                
                                (: OrderLineID is used as the key for order item so this must be included in any update to an order item :)
                                let $baseLineIDValue := $osmitem/oms:LineId/text()  
                                
                                return
                                <orderItem>
                                    {
                                        let $osmCorrelator := $orderData/oms:Reference
                                        let $service := $processResponse/bi:interaction/invbi:body/invbi:item/invbi:entity[ser:externalIdentity/ent:externalObjectId=$osmCorrelator]
                                        let $serviceAssignment  :=  $service/ser:configuration/con:configurationItem/con:configurationItem/con:entityAssignment/con:entity[ser:specification/spec:entityClass="Service"]
                                        (: Find the child service being assigned :)
                                        let $childService := $processResponse/bi:interaction/invbi:body/invbi:interaction/invbi:body/invbi:item/invbi:entity[ser:id/text() = $serviceAssignment/ser:id/text()]
                                        return (
                                           <orderItemRef type="{$systeminteractionmodule:ORDER_ITEM_FULLTYPE}">
                                            <LineId>{$baseLineIDValue}</LineId>
                                            {
                                                systeminteractionmodule:createServiceConfiguration($service[1], $processResponse),
                                                systeminteractionmodule:setServiceId($osmCorrelator, $processResponse),
                                                systeminteractionmodule:setEntityCharacteristics($osmCorrelator, $service[1])
                                            }
                                           </orderItemRef>
                                        )
                                    }
                                </orderItem>
                                }
                            }
                        </Functions>
                    </ControlData>
                </_root>
           </UpdatedNodes>
       </OrderDataUpdate>
};


declare function systeminteractionmodule:isServiceType(
    $type as xs:string?)
{
    if (fn:exists($type))
    then fn:ends-with($type, ":ServiceType")
    else fn:false()
};

declare function systeminteractionmodule:setServiceId(
    $osmCorrelator as xs:string?,
    $processResponse as element()*)
{
    let $service := $processResponse/bi:interaction/invbi:body/invbi:item/invbi:entity[systeminteractionmodule:isServiceType(fn:data(@xsi:type))=fn:true() and ser:externalIdentity/ent:externalObjectId=$osmCorrelator]
    where (fn:exists($service))
    return 
        <ServiceId>{$service[1]/ser:id/text()}</ServiceId>
};

declare function systeminteractionmodule:setEntityCharacteristics(
    $osmCorrelator as xs:string?,
    $service as element()*)
{
        let $name := $service/con:name/text()
        return
        (
        <ServiceEntityData>
            <EntityCharacteristics>
                {
                    for $property in $service/*:property
                    return (
                    <Characteristic>
                        {
                            let $charName := $property/*:name/text()
                            let $charValue := $property/*:value/text()
                            return (
                                <Name>{$charName}</Name>,
                                <Value>{$charValue}</Value>
                            )
                        }
                    </Characteristic>
                        )  
                }
             </EntityCharacteristics>
        </ServiceEntityData>
        )
};

declare function systeminteractionmodule:createServiceConfiguration(
            $service as element()?,
            $processResponse as element()*) as element()* 
{ 
    systeminteractionmodule:createServiceConfig($service),
    for $serviceAssignment in $service/ser:configuration/con:configurationItem/con:configurationItem/con:entityAssignment/con:entity[ser:specification/spec:entityClass="Service"]
    (: Find the child service being assigned :)
    let $childService := $processResponse/bi:interaction/invbi:body/invbi:interaction/invbi:body/invbi:item/invbi:entity[ser:id/text() = $serviceAssignment/ser:id/text()]
    where exists($childService)
    return
        systeminteractionmodule:createServiceConfig($childService[1])    
};  

declare function systeminteractionmodule:createServiceConfig(
            $service as element()?) as element()* { 
  
      let $config := $service/ser:configuration
      where exists($config)
      return
          <ServiceConfiguration>
              <version>{$config/con:version/text()}</version>
              <ServiceID>{$service/ser:id/text()}</ServiceID>
              <ExternalObjectID>{$service/ser:externalIdentity/ent:externalObjectId/text()}</ExternalObjectID>
              {
                  for $configItem at $position in $config/con:configurationItem
                  return (
                      systeminteractionmodule:createConfigItem("", $configItem, $position)
                  )
              }
              <serviceSpecName>{$service/ser:specification/spec:name/text()}</serviceSpecName>
          </ServiceConfiguration>
};  

declare function systeminteractionmodule:createConfigItem(
            $parentId as xs:string,
            $configItem as element()*,
            $configPosition as xs:integer*) as element()* { 

    let $name := $configItem/con:name/text()
    let $id := if ($parentId="") then "/root" else concat($parentId,"/",$configPosition)
    return (
        <configurationItem>
            <id>{$id}</id>
            <parentId>{$parentId}</parentId>
            <name>{$name}</name>
            {
                for $property in $configItem/*[local-name()='property']
                return (
                    <characteristic>
                        {
                            let $charName := $property/*[local-name()='name']/text()
                            let $charValue := $property/*[local-name()='value']/text()
                            return (
                                <Name>{$charName}</Name>,
                                <Value>{$charValue}</Value>
                            )
                    }
                    </characteristic>
                ),
                systeminteractionmodule:processEntityAssignment($configItem),
                systeminteractionmodule:processEntityReference($configItem)
            }
        </configurationItem>,
        for $childItem at $position in $configItem/con:configurationItem
                return (
                    systeminteractionmodule:createConfigItem($id,$childItem, $position)
                )
    )

};

declare function systeminteractionmodule:processEntityAssignment(
            $configItem as element()*) as element()* { 

    let $assignment := $configItem/con:entityAssignment
    where exists($assignment) and not (fn:exists($assignment[@xsi:nil="true"])) and (exists($assignment/con:resource) or exists($assignment/con:entity))
    return
        if (exists($assignment/con:resource))
        then (
            <entityAssignment>
                <refState>{$assignment/con:state/text()}</refState>
                {systeminteractionmodule:processResource($assignment/con:resource)}
            </entityAssignment>
        ) else if (exists($assignment/con:entity))
        then (
            <entityAssignment>
                <refState>{$assignment/con:state/text()}</refState>
                {systeminteractionmodule:processResource($assignment/con:entity)}
            </entityAssignment>
        ) else ()

};

declare function systeminteractionmodule:processEntityReference(
            $configItem as element()*) as element()* { 

    let $assignment := $configItem/con:entityReference
    where exists($assignment) and not (fn:exists($assignment[@xsi:nil="true"])) and (exists($assignment/con:resource) or exists($assignment/con:entity))
    return
        if (exists($assignment/con:resource))
        then (
            <entityReference>
                <refState>{$assignment/con:state/text()}</refState>
                {systeminteractionmodule:processResource($assignment/con:resource)}
            </entityReference>
        ) else if (exists($assignment/con:entity))
        then (
            <entityReference>
                <refState>{$assignment/con:state/text()}</refState>
                {systeminteractionmodule:processResource($assignment/con:entity)}
            </entityReference>
        ) else ()

};

declare function systeminteractionmodule:processResource(
            $resource as element()*) as element()* {

    let $id := $resource/*[local-name()='id']/text()
    let $name := $resource/*[local-name()='name']/text()
    let $specification := $resource/*[local-name()='specification']
    let $specName := $specification/*[local-name()='name']/text()
    let $state := $resource/*[local-name()='state']/text()
    return (
        <id>{$id}</id>,
        <name>{$name}</name>,
        <specification>{$specName}</specification>,
        <entityState>{$state}</entityState>,
        for $property in $resource/*[local-name()='property']
        return
            <characteristic>
                {
                    let $charName := $property/*[local-name()='name']/text()
                    let $charValue := $property/*[local-name()='value']/text()
                    return (
                        <Name>{$charName}</Name>,
                        <Value>{$charValue}</Value>
                        )
                }
            </characteristic>
    )

};

(: Creates the OSM Order Data Update after a CalculateTechnicalAssetsResponse  message :)
declare function systeminteractionmodule:createOrderDataUpdateForCTA(
    $ctaResponse as element()*,
    $orderData as element()*) as element()* {

    let $functionName := $uimlib:PlanDeliveryFunction
    let $componentKey := $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:componentKey/text()
    let $eOrderItem   := $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:orderItem
    
    let $result :=
       <OrderDataUpdate xmlns="http://www.metasolv.com/OMS/OrderDataUpdate/2002/10/25">
        <UpdatedNodes>
            <_root>
                <ControlData>
                    <Functions>
                    {
                        element{$functionName}{
                            <componentKey>{$componentKey}</componentKey>,
                            
                                (: Need to loop through the 'OrderItem's and update each one with their specific info :)
                                for $oi in $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:orderItem
                                return
                                <orderItem>
                                    {
                                        let $osmitem := $oi/oms:orderItemRef                                        
                                        (: OrderLineID is used as the key for order item so this must be included in any update to an order item :)
                                        let $baseLineIDValue := $osmitem/oms:LineId/text()

                                        return
                                        (
                                        <orderItemRef type="{$systeminteractionmodule:ORDER_ITEM_FULLTYPE}">
                                            <LineId>{$baseLineIDValue}</LineId>                                                                                                                    
                                            {   
                                                let $ctaTechnicalAction:= $ctaResponse/techws:technicalAction

                                                return
                                                    (
                                                            for $ta in $ctaTechnicalAction
                                                                return
                                                                (
                                                                        (: return a list of generic actions :)
                                                                  <TechnicalAction>
                                                                     {systeminteractionmodule:createGenericAction($ta)}
                                                                  </TechnicalAction>
                                                                )
                                                    )
                                           }
                                       </orderItemRef>
                                       )
                                    }
                                </orderItem>
                            
                        }
                    }
                    </Functions>
               </ControlData>
            </_root>
        </UpdatedNodes>
       </OrderDataUpdate>
    return
    (
        $result
    )
};


declare function systeminteractionmodule:createGenericAction(
    $ta as element()*) as element()*
{
    let $targetSpec  := systeminteractionmodule:getTargetSpecFromTA($ta)
    let $subjectSpec := systeminteractionmodule:getSubjectSpecFromTA($ta)
    let $generatedValue := $targetSpec
    let $result :=
        <TechnicalAction>
            <ActionCode>{systeminteractionmodule:getActionCodeFromTA($ta)}</ActionCode>
            <ActionId>{systeminteractionmodule:getActionIdFromTA($ta)}</ActionId>
            <ParentId>{systeminteractionmodule:getActionIdFromTA($ta)}</ParentId>                                         
            <SpecializedActionCode>{systeminteractionmodule:getSpecializedActionFromTA($ta)}</SpecializedActionCode>
            <Subject>{$generatedValue}</Subject>
            <TechnicalParameters>
            {
                for $param in $ta/techws:parameter 
                return(
                    element{$param/techws:name/text()}       {
                         if(fn:exists($param/techws:value/text()))then(
                         fn:data($param/techws:value)
                         )else(),
                         for $prop in $param/techws:value/*:property
                          return
                              element{$prop/*:name/text()} {
                                  fn:data($prop/*:value/text()) 
                          }                       
                        
                    }
                )
            }        
            </TechnicalParameters>   
        </TechnicalAction>    
    return       
    (
        $result
    )
};

(: Create the techws:CalculateTechnicalActionsRequest element :)
declare function systeminteractionmodule:addCalculateTechnicalActionsRequest(
    $bid as xs:string *) as element()*
{ 
   (: <techws:CalculateTechnicalActionsRequest> :)
   let $elementname := fn:QName($uimlib:techwsNamespace, concat($uimlib:techwsPrefix, $uimlib:calculateTechnicalActionsRequest))
   
   where (exists($bid))

   return
       element {$elementname } {
            <techws:businessInteraction>
                <invbi:header>
                    <invbi:id>{$bid}</invbi:id>                    
                </invbi:header>
            </techws:businessInteraction>,  
            <techws:includeConfigItemDifferences>true</techws:includeConfigItemDifferences>            
       }
};


declare function systeminteractionmodule:getServiceIDFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:serviceConfiguration/techws:serviceId/text()
};

declare function systeminteractionmodule:getServiceVersionFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:serviceConfiguration/techws:serviceId/text()
};

declare function systeminteractionmodule:getActionCodeFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:action/text()
};

declare function systeminteractionmodule:getActionIdFromTA(
    $ta as element()*) as xs:string * {

    $ta/@id
};

(: This function returns the parentId if it exists, otherwise it returns the action id. This case would represent a generic TA :)
declare function systeminteractionmodule:getParentIdFromTA(
    $ta as element()*) as xs:string * {

  (:  $ta/@parent:)
    
    let $id := if(exists($ta/@parent))
    then ($ta/@parent)
    else (systeminteractionmodule:getActionIdFromTA($ta))
        return $id
        
};

declare function systeminteractionmodule:getSpecializedActionFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:specializedAction/text()
};

declare function systeminteractionmodule:getFulfillmentSystemFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:fulfillmentSystemType/text()
};

declare function systeminteractionmodule:getSubjectSpecFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:subjectSpec/text()
};

declare function systeminteractionmodule:getTargetSpecFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:targetSpec/text()
};
declare function systeminteractionmodule:getTargetFromTA(
    $ta as element()*) as xs:string * {

    $ta/techws:target/text()
};