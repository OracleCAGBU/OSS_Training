import module namespace soaplib                     = "http://www.training.com/comms/ordermanagement/common/soap/library" at "http://www.training.com/resources/lib/SoapLibrary.xqy";
import module namespace uimlib                      = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";
import module namespace systeminteractionmodule     = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule" at "http://www.training.com/resources/lib/SystemInteractionModule.xqy";

declare namespace context                           = "java:com.mslv.oms.automation.TaskContext";
declare namespace outboundMessage                   = "java:javax.jms.TextMessage";
declare namespace log                               = "java:org.apache.commons.logging.Log";
declare namespace automator                         = "java:oracle.communications.ordermanagement.automation.plugin.ScriptSenderContextInvocation";
declare namespace provord                           = "http://www.somtraining.com/inputMessage";
declare namespace corecom                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace fulfillord                        = "http://www.training.com/inputMessage";
declare namespace saxon                             = "http://saxon.sf.net/";
declare namespace xsl                               = "http://www.w3.org/1999/XSL/Transform";
declare namespace oms                               = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                            = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace cttransns                         = "COM_SalesOrderFulfillment";
declare namespace invbi                             = "http://xmlns.oracle.com/communications/inventory/businessinteraction";

declare option saxon:output "method=xml";
declare option saxon:output "saxon:indent-spaces=4";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

declare variable $orderData             := fn:root(.)/oms:GetOrder.Response;
declare variable $sTaskName             := context:getTaskMnemonic($context);
declare variable $sOrderId              := $orderData/oms:OrderID/text();
declare variable $sVersion              := $orderData/oms:Version/text();
declare variable $sUsername             := 'oms-automation';
declare variable $sPassword             := 'admin123';
declare variable $sUimUsername          := 'uimadmin';
declare variable $sUimPassword          := 'oracle123';
declare variable $wsNamespace           := "http://xmlns.oracle.com/communications/ordermanagement";
declare variable $wsPrefix              := "ws:";
declare variable $createOrder           := "CreateOrder";
declare variable $ebmName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}ProcessProvisioningOrderEBM";
declare variable $eboName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}ProvisioningOrderEBO";
declare variable $currentORRVersion     := '%{CARTRIDGE_VERSION}';
declare variable $sBIOrder              := "BI_Order";
declare variable $invstrucNamespace     := "http://xmlns.oracle.com/communications/inventory/structure";
declare variable $invstrucPrefix        := "invstruc:";
declare variable $invconfigNamespace    := "http://xmlns.oracle.com/communications/inventory/configuration";
declare variable $invconfigPrefix       := "invconfig:";
declare variable $invpropNamespace      := "http://xmlns.oracle.com/communications/inventory/property";
declare variable $invpropPrefix         := "invprop:";
declare variable $child                 := "child";
declare variable $serviceManagementURI  := "/InventoryWS/InventoryWSJMS";
declare variable $mimeContextType       := "text/xml; charset=UTF-8";

(: Creates the soap message :)
declare function local:createSoapMessage(
    $orderData as element()*) as element()*
{ 
    <soapenv:Envelope 
        xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:bi="http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction"
        xmlns:com="http://xmlns.oracle.com/communications/inventory/webservice/common"
        xmlns:invbi="http://xmlns.oracle.com/communications/inventory/businessinteraction"
        xmlns:spec="http://xmlns.oracle.com/communications/inventory/specification"
        xmlns:ser="http://xmlns.oracle.com/communications/inventory/service"
        xmlns:par="http://xmlns.oracle.com/communications/inventory/party"
        xmlns:con1="http://xmlns.oracle.com/communications/inventory/connectivity"
        xmlns:place="http://xmlns.oracle.com/communications/inventory/place"
        xmlns:role="http://xmlns.oracle.com/communications/inventory/role"
        xmlns:log="http://xmlns.oracle.com/communications/inventory/logicaldevice"
        xmlns:num="http://xmlns.oracle.com/communications/inventory/number"
        xmlns:cus="http://xmlns.oracle.com/communications/inventory/customobject"
        xmlns:gro="http://xmlns.oracle.com/communications/inventory/group"
        xmlns:invprop="http://xmlns.oracle.com/communications/inventory/property"
        xmlns:ent="http://xmlns.oracle.com/communications/inventory/entity"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
        xmlns:invconfig="http://xmlns.oracle.com/communications/inventory/configuration"> 
        { 
            (: Add the security header to the soap request :)
            soaplib:createHeaderForCustomCredential($sUimUsername, $sUimPassword),
            local:addSoapBody( $orderData ) 
        }
    </soapenv:Envelope>            
};

(: Create the soapenv:Body element. Child elements should be created directly here. Groups should be created through a call to another function :)   
declare function local:addSoapBody(
    $orderData as element()*) as element()*
{ 
   (: <soapenv:Body> :)
   let $elementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Body))
   
   where (exists($orderData ))
   return
       element {$elementname } {
             local:addCaptureInteraction($orderData)
       }
};

(: Creates the bi:captureInteractionRequest element. 
Child elements should be created directly here. Groups should be created through a call to another function :)
declare function local:addCaptureInteraction(
    $orderData as element()*) as element()*
{ 
    (: <bi:captureInteractionRequest :)    
    let $elementname := fn:QName($uimlib:biNamespace, concat($uimlib:biPrefix, $uimlib:captureInteractionRequest))
    
    where(exists($orderData))
    return
        element {$elementname } {
            uimlib:createQualifiedElementFromString($uimlib:comNamespace, $uimlib:comPrefix, $uimlib:header, ''),
            local:addInteraction($orderData),
            uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:executeProcess, $uimlib:true),
            uimlib:createQualifiedElementFromString($uimlib:biNamespace, $uimlib:biPrefix, $uimlib:responseLevel, $uimlib:AllExpandedLevel)
        }
};

(: Creates the bi:interaction element :)
declare function local:addInteraction(
    $orderData as element()*) as element()*
{ 
    (: <bi:interaction> :)
    let $elementname := fn:QName($uimlib:biNamespace, concat($uimlib:biPrefix, $uimlib:interaction)) 

    where (exists($orderData))
    return
        element {$elementname} {
            local:addInteractionHeader($orderData),
            local:addInteractionBody($orderData)
        }
};

(: Creates the invbi:header element :)
declare function local:addInteractionHeader(
    $orderData as element() *) as element()*
{
    (: <invbi:header> :)
    let $elementname := fn:QName($uimlib:invbiNamespace, concat($uimlib:invbiPrefix, $uimlib:header))
    
    let $bicorrID := uimlib:getBICorrelationID($orderData)
    let $friendlyReference := $orderData/oms:Reference
    
      where (exists($orderData))
        return
            element {$elementname} {
                uimlib:createQualifiedSpecificationName($uimlib:invbiNamespace, $uimlib:invbiPrefix, $sBIOrder),
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $uimlib:CREATE), 
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:id, ''),             
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:name, $uimlib:ossintegration),
               (: uimlib:createFriendlyQualifiedExternalIdentity($uimlib:invbiNamespace, $uimlib:invbiPrefix, $bicorrID, $friendlyReference),:)
                uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:effDate, (current-dateTime() cast as xs:string) )
       }
};

(: Creates the invbi:body element :)
declare function local:addInteractionBody(
    $orderData as element()*) as element()*
{
    (: <invbi:body> :)
    let $elementname := fn:QName($uimlib:invbiNamespace, concat($uimlib:invbiPrefix, $uimlib:body))   
    where(exists($orderData))
    return
        element {$elementname } {
        (
            (:for $oi in $orderData/oms:_root/oms:ControlData/oms:OrderItem:)
            for $oi in $orderData/oms:_root/oms:ControlData/oms:Functions/oms:DesignServiceFunction/oms:orderItem/oms:orderItemRef
            let $action := uimlib:getOIActionCode($oi)
             return
                (: If service domains require a different request, they should copy this file and re-implement processing from this point on :)
                local:createItemForCaptureRequest($oi, $action/text())
         )
       }             
};

(: Creates the invbi:item elements for the CaptureInteractionRequest as needed. This supports the creation of more than one item
Returns a sequence of invbi:item elements :)
declare function local:createItemForCaptureRequest(
    $orderitem as element(),
    $action as xs:string) as item()*
{ 
    (: <invbi:item> :)    
    let $elementname := fn:QName($uimlib:invbiNamespace, concat($uimlib:invbiPrefix, $uimlib:item))    
    where(exists($orderitem))
    return
        element {$elementname } {
            uimlib:createQualifiedElementFromString($uimlib:invbiNamespace, $uimlib:invbiPrefix, $uimlib:action, $uimlib:ADD),
            local:addService($orderitem, $action),
            (:local:addSubscriberToCaptureRequest($orderitem),
            local:addAddressToCaptureRequest($orderitem),:)
            local:addExtensibleAttributesToParameters($orderitem)
        }
};

(: Creates the invbi:service element :)
declare function local:addService(
    $orderitem as element()*,
    $action as xs:string) as element()*
{ 
    (: <invbi:service> :)
    let $elementname := fn:QName($uimlib:invbiNamespace, concat($uimlib:invbiPrefix, $uimlib:service))
    
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
declare function local:addExtensibleAttributesToParameters(
    $orderitem as element()*) as element()*
{ 
    (: Dynamic Params :)
    
     (: <invbi:parameter> :)        
    for $attribute in $orderitem/oms:dynamicParams/node()
    return 
        if(exists($attribute/*)) 
        then (
            local:addComplexParameter($attribute)
        )
        else
        (
            local:addSimpleParameter($attribute)
        ) 
          
};

declare function local:addSimpleParameter(
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
declare function local:addComplexParameter(
    $structure as element()) as element()*
{
    let $parameterElement := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:parameter))
    let $valueElement := fn:QName($uimlib:invbiNamespace, fn:concat($uimlib:invbiPrefix, $uimlib:value))    
    
    return
            element{$parameterElement}{
                <invbi:name>{fn:local-name($structure)}</invbi:name>,
                element{$valueElement} {
                    attribute{"xsi:type"}{data("invstruc:StructuredType")},
                    uimlib:createQualifiedElementFromString($invstrucNamespace, $invstrucPrefix, $uimlib:name, fn:local-name($structure)),
                        for $nested in $structure/node()
                        return
                            if(exists($nested/*)) 
                            then (
                                local:addChildStructure($nested)
                            )
                            else
                            (
                                local:addStructureProperty($nested)
                            ) 
                           
                }
            }
  
};

declare function local:addChildStructure(
    $attribute as element()*) as element()*
{
    (: nested structures are placed inside <child> element :)
    let $childelement := fn:QName($invstrucNamespace, concat($invstrucPrefix, $child))
    let $structurename := fn:local-name($attribute)
    
    return 
        element{$childelement} {
        attribute{"xsi:type"}{data("invstruc:StructuredType")},
        uimlib:createQualifiedElementFromString($invstrucNamespace, $invstrucPrefix, $uimlib:name, $structurename),
            for $nested in $attribute/node()
            return
                if(exists($nested/*))
                then (
                    local:addChildStructure($nested)
                )
                else (
                    local:addStructureProperty($nested)
                )
               
        }
};

(: Adds property elements in the StructuredType namespace with Name/Value child elements
 <invstruc:property>
    <invprop:name>username</invprop:name>
    <invprop:value>PhilipTheFair</invprop:value>
</invstruc:property>
:)
declare function local:addStructureProperty(
    $attribute as element()*) as element()*
{
    let $name := fn:local-name($attribute)
    let $value := $attribute/text()
    
    (: Only populate attributes that have a value :)
    where(exists($value))
    return 
            (: Send the attribute element and constructor method will extract value :)
            local:createQualifiedStringProperty($invstrucNamespace, $invstrucPrefix, $name, $attribute)
        
};

(: Creates a namespace qualified parameter element with name/value child elements, where the type of the value element is xs:string 
<$prefix:property>
    <invprop:name>$tmpname</invprop:name>
    <invprop:value xsi:type="xs:string">$tmpvalue</invprop:value>
</$prefix:property>  :)
declare function local:createQualifiedStringProperty(
            $namespace as xs:string,
            $prefix as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
    
    local:createProperty($namespace, $prefix, 'xs:string', $tmpname, $tmpvalue)   

};

(: Creates a namespace qualified property element with name/value child elements where the value element also has an xsi type
<$prefix:property>
    <invprop:name>$tmpname</invprop:name>
    <invprop:value xsi:type="$xsitype">$tmpvalue</invprop:value>
</$prefix:property>  :)
declare function local:createProperty(
            $namespace as xs:string,
            $prefix as xs:string,
            $xsitype as xs:string,
            $tmpname as xs:string,
            $tmpvalue as element()*) as element()* { 
            
    let $propertyelement := fn:QName($namespace, concat($prefix, $uimlib:property))
    let $valueElement := uimlib:createQualifiedElementWithXSIType($uimlib:invpropNamespace, $invpropPrefix, $uimlib:value, $xsitype, $tmpvalue)
    
    return 
    element {$propertyelement} {
            uimlib:createQualifiedElementFromString($uimlib:invpropNamespace, $invpropPrefix, $uimlib:name, $tmpname),
            $valueElement
        }
    
};

let $eCaptureRequest                     := local:createSoapMessage($orderData)
let $sOrderData                          := saxon:serialize($orderData, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sCaptureRequest                     := saxon:serialize($eCaptureRequest, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)

return
    (
        (:log:info($log, fn:concat('GetOrder.Response : ', $sOrderData)),:)
        log:info($log, fn:concat('CaptureBI Request : ', $sCaptureRequest)),
        outboundMessage:setStringProperty($outboundMessage, "URI", $serviceManagementURI),
        outboundMessage:setStringProperty($outboundMessage, "_wls_mimehdrContent_Type", $mimeContextType),
        $eCaptureRequest
    )