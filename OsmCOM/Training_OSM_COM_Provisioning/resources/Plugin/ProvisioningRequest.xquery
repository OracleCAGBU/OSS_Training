import module namespace soaplib                     = "http://www.training.com/comms/ordermanagement/common/soap/library" at "http://www.training.com/resources/lib/SoapLibrary.xqy";
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
 declare namespace im                                = "COM_SalesOrderFulfillment";

declare option saxon:output "method=xml";
declare option saxon:output "saxon:indent-spaces=4";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

declare variable $eTaskData             := fn:root(.)/oms:GetOrder.Response;
declare variable $sTaskName             := context:getTaskMnemonic($context);
declare variable $sOrderId              := $eTaskData/oms:OrderID/text();
declare variable $sVersion              := $eTaskData/oms:Version/text();
declare variable $sUsername             := 'oms-automation';
declare variable $sPassword             := 'admin123';
declare variable $wsNamespace           := "http://xmlns.oracle.com/communications/ordermanagement";
declare variable $wsPrefix              := "ws:";
declare variable $createOrder           := "CreateOrder";
declare variable $ebmName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}ProcessProvisioningOrderEBM";
declare variable $eboName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1}ProvisioningOrderEBO";
declare variable $currentORRVersion     := '%{CARTRIDGE_VERSION}';
declare variable $osmURI                := "/osm/wsapi";
declare variable $mimeContextType       := "text/xml; charset=UTF-8";

(: Generate unique Id :)

declare function local:generateUniqueId(
    $sOrderId as xs:string,
    $sVersion as xs:string) as xs:string {

systeminteractionmodule:setCorrelationID ($sOrderId,$sVersion)

};

(: Build up the soap message containing the delivery order request :)

declare function local:createSoapMessage(
    $eTaskData as element()*,
    $sEbmId as xs:string*) as element()*
{ 
    <soapenv:Envelope 
          xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
          xmlns:ws="http://xmlns.oracle.com/communications/ordermanagement"
          xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
          xmlns:provord="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1"
          xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
    {
        soaplib:createHeaderForCustomCredential($sUsername,$sPassword),
        local:addBody($eTaskData,$sEbmId) 
    }
    </soapenv:Envelope>            
};

(: Create the Body element. :) 

declare function local:addBody(
    $eTaskData as element()*,
    $sEbmId as xs:string) as element()*
{ 
    (: <soapenv:Body> :)
    let $sElementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Body))
    where (exists($eTaskData))
    return
        element {$sElementname} {
            local:addCreateOrder($eTaskData,$sEbmId)
        }
};

(: Create the CreateOrder element :) 
  
declare function local:addCreateOrder(
    $eTaskData as element()*,
    $sEbmId as xs:string) as element()*
{ 
   (: <ws:CreateOrder> :)
    let $elementname := fn:QName($wsNamespace, concat($wsPrefix, $createOrder))
    where (exists($eTaskData))
    return
        element {$elementname } {     
            local:addProcessProvisioningOrderEBM($eTaskData,$sEbmId)
        }
};

(: Create the ProcessProvisioningOrderEBM element :)

declare function local:addProcessProvisioningOrderEBM(
    $eTaskData as element(),
    $sEbmId as xs:string) as element()?
{ 
    <provord:ProcessProvisioningOrderEBM>
    {
        local:addEBMHeader($eTaskData, $sEbmId),
        local:addDataArea($eTaskData, $sEbmId)
    }
    </provord:ProcessProvisioningOrderEBM>
};

(: This creates the EBM Header element :)

declare function local:addEBMHeader(
    $orderData as element()*,
    $sEbmId as xs:string) as element()*
{ 
    <corecom:EBMHeader>
        <corecom:EBMID>{$sEbmId}</corecom:EBMID> 
        <corecom:EBMName>{$ebmName}</corecom:EBMName> 
        <corecom:EBOName>{$eboName}</corecom:EBOName> 
        <corecom:CreationDateTime>{fn:current-dateTime()}</corecom:CreationDateTime> 
        <corecom:VerbCode>process</corecom:VerbCode> 
        <corecom:MessageProcessingInstruction>
            <corecom:EnvironmentCode>TRAINING</corecom:EnvironmentCode> 
        </corecom:MessageProcessingInstruction>
        <corecom:Sender>
            <corecom:ID>OSMCFS_01</corecom:ID> 
            <corecom:Description>OSM Central Fulfillment System</corecom:Description> 
            <corecom:IPAddress>ipaddress.osm.com</corecom:IPAddress> 
            <corecom:SenderMessageID>{$sEbmId}</corecom:SenderMessageID> 
            <corecom:TransactionCode>ProvisioningOrder</corecom:TransactionCode> 
            <corecom:Application>
                <corecom:ID>OSMCFS_01</corecom:ID> 
                <corecom:Version>{$currentORRVersion}</corecom:Version> 
            </corecom:Application>
            <corecom:ContactName>OSM Contact</corecom:ContactName> 
            <corecom:ContactEmail>contact@osm.com</corecom:ContactEmail> 
            <corecom:ContactPhoneNumber>1234567890</corecom:ContactPhoneNumber> 
        </corecom:Sender>
    </corecom:EBMHeader>
};

declare function local:addDataArea(
    $eOrderData as element()*,
    $sEbmId as xs:string) as element()*
{ 
    (: Initialize all paramaters :)
    let $eCustomerPartyReference        := $eOrderData/oms:_root/oms:Order/oms:CustomerPartyReference
    let $sFirstName                     := $eCustomerPartyReference/oms:FirstName/text()
    let $sLastName                      := $eCustomerPartyReference/oms:LastName/text()
    let $sCustomerAccountName           := fn:concat($sFirstName,',',$sLastName)
    let $sOrderNumber                   := $eOrderData/oms:_root/oms:Order/oms:OrderNumber/text()
    let $sBusinessComponentID           := systeminteractionmodule:generateUniqueId()
    
    return  
    (
        <provord:DataArea>
            <corecom:Process/> 
            <provord:ProcessProvisioningOrder>
                <corecom:Identification>
                    <corecom:BusinessComponentID schemeID="PROVISIONINGORDER_ID" schemeAgencyID="COMMON">{$sBusinessComponentID}</corecom:BusinessComponentID> 
                    <corecom:ID schemeID="SALESORDER_ID" schemeAgencyID="SEBL_01">{$sOrderNumber}</corecom:ID>  
                    <corecom:Revision>
                        <corecom:Number>1</corecom:Number> 
                    </corecom:Revision>
                </corecom:Identification>
                <provord:RequestedDeliveryDateTime/>
                <provord:OrderSubject>SERVICE</provord:OrderSubject>
                <provord:TypeCode>SERVICE ORDER</provord:TypeCode>
                <provord:FulfillmentPriorityCode></provord:FulfillmentPriorityCode>         
                <provord:FulfillmentModeCode>SOM_Deliver</provord:FulfillmentModeCode> 
                <corecom:Status>
                    <corecom:Code>IN PROGRESS</corecom:Code>
                    <corecom:ReasonCode/>
                    <corecom:Description/>
                </corecom:Status>
                <corecom:CustomerPartyReference xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                    <corecom:CustomerPartyAccountName>{$sCustomerAccountName}</corecom:CustomerPartyAccountName>
                </corecom:CustomerPartyReference>
                <corecom:SalesOrderReference>
                    <corecom:SalesOrderIdentification>
                        <corecom:ID schemeID="SALESORDER_ID" schemeAgencyID="SEBL_01">{$sOrderNumber}</corecom:ID>
                    </corecom:SalesOrderIdentification>
                </corecom:SalesOrderReference>
                {
                    local:createProvisioningOrderLineItems($eOrderData,$sEbmId)
                }     
            </provord:ProcessProvisioningOrder>    
        </provord:DataArea>
    )
};

declare function local:createProvisioningOrderLineItems(
    $eOrderData as element()*,
    $sEbmId as xs:string) as element()*
{
    let $eMappingContext := $eOrderData/oms:_root/oms:ControlData/oms:MappingContext
    let $eComponent := $eOrderData/oms:_root/oms:ControlData/oms:Functions/oms:ProvisioningFunction
    let $sComponentKey := $eComponent/oms:componentKey/text()
    let $eTransformedOrderLineItems := $eComponent/oms:transformedOrderItem
    let $eSourceOrderLineItems := $eComponent/oms:orderItem
    return(
    if (fn:exists($eTransformedOrderLineItems)) then (
        for $eTransformedOrderLineItem in $eTransformedOrderLineItems
        let $eSourceLineOrderItems       := systeminteractionmodule:getSourceLineOrderItemsFromTransformedLineItem($eTransformedOrderLineItem,$eMappingContext,$eComponent)
        let $ePrimarySourceLineOrderItem := systeminteractionmodule:getPrimarySourceLineOrderItemsFromTransformedLineItem($eTransformedOrderLineItem,$eMappingContext,$eComponent)
        return local:createProvisioningOrderLineItemFromTransformedLineItem($eOrderData,$eTransformedOrderLineItem,$ePrimarySourceLineOrderItem,$eSourceLineOrderItems)
    
    ) else (
         for $eSourceOrderLineItem in $eSourceOrderLineItems
        return local:createProvisioningOrderLineItemFromSourceOrderLineItem($eOrderData,$eSourceOrderLineItem)
        )
    
    )
    
};

declare function local:createProvisioningOrderLineItemFromTransformedLineItem(
    $eOrderData as element()*,
    $eTransformedOrderLineItem as element(),
    $ePrimarySourceLineOrderItem as element(),
    $eSourceLineOrderItems as element()*) as element()*
{
    let $sTransformedOrderLineId := $eTransformedOrderLineItem/oms:orderItemRef/toibns:LineId/text()
    let $sOrderReference := $eOrderData/oms:Reference/text()
    let $sRecognitionSpec := fn:normalize-space(data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:Recognition))
    let $sRecognition := fn:substring-before($sRecognitionSpec, 'Spec')
    let $sFic := fn:substring-after($sRecognition, '}')
    
    return(
        <provord:ProvisioningOrderLine>
            <corecom:Identification xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:BusinessComponentID schemeID="PROVISIONINGORDER_LINEID" schemeAgencyID="COMMON">{$sTransformedOrderLineId}</corecom:BusinessComponentID>
                <corecom:ID schemeID="SALESORDER_LINEID" schemeAgencyID="SEBL_01">{$sTransformedOrderLineId}</corecom:ID>
            </corecom:Identification>
            <provord:ServiceActionCode>{data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:ServiceAction)}</provord:ServiceActionCode>
            <provord:ServicePointCode />
            <provord:MilestoneCode />
            <corecom:Status xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:Code></corecom:Code>
            </corecom:Status>
            <corecom:ServiceAddress xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:LineOne></corecom:LineOne>
                <corecom:CityName></corecom:CityName>
                <corecom:StateName></corecom:StateName>
                <corecom:CountryCode></corecom:CountryCode>
                <corecom:PostalCode></corecom:PostalCode>
            </corecom:ServiceAddress>
            <corecom:SalesOrderLineReference xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:SalesOrderLineIdentification>
                    <corecom:BusinessComponentID schemeID="SALESORDER_ID" schemeAgencyID="COMMON">{fn:data($eOrderData/oms:_root/oms:Order/oms:OrderNumber)}</corecom:BusinessComponentID>
                    <corecom:ID xmlns="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/SalesOrder/V2"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xmlns:sord="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/SalesOrder/V2"
                        schemeAgencyID="SEBL_01" schemeID="SALESORDER_LINEID">{$sTransformedOrderLineId}</corecom:ID>
                </corecom:SalesOrderLineIdentification>
            </corecom:SalesOrderLineReference>
            <corecom:ItemReference xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:ItemIdentification>
                    <corecom:BusinessComponentID schemeAgencyID="COMMON" schemeID="ITEM_ID">{$sTransformedOrderLineId}</corecom:BusinessComponentID>
                </corecom:ItemIdentification>
                <corecom:Name>{data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:LineName)}</corecom:Name>
                <corecom:ClassificationCode listID="PermittedTypeCode" />
                <corecom:ClassificationCode listID="BillingProductTypeCode" />
                <corecom:ClassificationCode listID="FulfillmentItemCode">{$sFic}</corecom:ClassificationCode>
                <corecom:TypeCode>SERVICE</corecom:TypeCode>
                <corecom:Description>{data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:LineName)}</corecom:Description>
                <corecom:FulfillmentCompositionTypeCode />
                <corecom:FulfillmentSuccessCode />
                <corecom:NetworkItemTypeCode />
                <corecom:PrimaryClassificationCode>{data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:LineName)}</corecom:PrimaryClassificationCode>
            </corecom:ItemReference>
            <provord:ProvisioningOrderSchedule>
                <provord:RequestedDeliveryDateTime>{data($eTransformedOrderLineItem/oms:orderItemRef/cttransns:RequestedDeliveryDate)}</provord:RequestedDeliveryDateTime>          
            </provord:ProvisioningOrderSchedule>
            <provord:CSODynamicParams>{$eTransformedOrderLineItem/oms:orderItemRef/cttransns:dynamicParams}</provord:CSODynamicParams>                        
        </provord:ProvisioningOrderLine>
    )
};
(: Function to create ProvisionOrderLine Item for Mobile:)
declare function local:createProvisioningOrderLineItemFromSourceOrderLineItem(
    $eOrderData as element()*,
    $eSourceOrderLineItem as element()*) as element()*
{
    let $sOrderLineId := $eSourceOrderLineItem/oms:orderItemRef/oms:LineId/text()
    let $sOrderReference := $eOrderData/oms:Reference/text()
    let $sRecognitionSpec := fn:normalize-space(data($eSourceOrderLineItem/oms:orderItemRef/oms:Recognition))
    let $sRecognition := fn:substring-before($sRecognitionSpec, 'Spec')
    let $sFic := fn:substring-after($sRecognition, '}')
    
    return(
        <provord:ProvisioningOrderLine>
            <corecom:Identification xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:BusinessComponentID schemeID="PROVISIONINGORDER_LINEID" schemeAgencyID="COMMON">{$sOrderLineId}</corecom:BusinessComponentID>
                <corecom:ID schemeID="SALESORDER_LINEID" schemeAgencyID="SEBL_01">{$sOrderLineId}</corecom:ID>
            </corecom:Identification>
            <provord:ServiceActionCode>{data($eSourceOrderLineItem/oms:orderItemRef/oms:Action)}</provord:ServiceActionCode>
            <provord:ServicePointCode />
            <provord:MilestoneCode />
            <corecom:Status xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:Code></corecom:Code>
            </corecom:Status>
            <corecom:ServiceAddress xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:LineOne></corecom:LineOne>
                <corecom:CityName></corecom:CityName>
                <corecom:StateName></corecom:StateName>
                <corecom:CountryCode></corecom:CountryCode>
                <corecom:PostalCode></corecom:PostalCode>
            </corecom:ServiceAddress>
            <corecom:SalesOrderLineReference xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:SalesOrderLineIdentification>
                    <corecom:BusinessComponentID schemeID="SALESORDER_ID" schemeAgencyID="COMMON">{fn:data($eOrderData/oms:_root/oms:Order/oms:OrderNumber)}</corecom:BusinessComponentID>
                    <corecom:ID xmlns="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/SalesOrder/V2"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xmlns:sord="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/SalesOrder/V2"
                        schemeAgencyID="SEBL_01" schemeID="SALESORDER_LINEID">{$sOrderLineId}</corecom:ID>
                </corecom:SalesOrderLineIdentification>
            </corecom:SalesOrderLineReference>
            <corecom:ItemReference xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2">
                <corecom:ItemIdentification>
                    <corecom:BusinessComponentID schemeAgencyID="COMMON" schemeID="ITEM_ID">{$sOrderLineId}</corecom:BusinessComponentID>
                </corecom:ItemIdentification>
                <corecom:Name>{data($eSourceOrderLineItem/oms:orderItemRef/oms:LineName)}</corecom:Name>
                <corecom:ClassificationCode listID="PermittedTypeCode" />
                <corecom:ClassificationCode listID="BillingProductTypeCode" />
                <corecom:ClassificationCode listID="FulfillmentItemCode">{$sFic}</corecom:ClassificationCode>
                <corecom:TypeCode>SERVICE</corecom:TypeCode>
                <corecom:Description>{data($eSourceOrderLineItem/oms:orderItemRef/oms:LineName)}</corecom:Description>
                <corecom:FulfillmentCompositionTypeCode />
                <corecom:FulfillmentSuccessCode />
                <corecom:NetworkItemTypeCode />
                <corecom:PrimaryClassificationCode>{data($eSourceOrderLineItem/oms:orderItemRef/oms:LineName)}</corecom:PrimaryClassificationCode>
            </corecom:ItemReference>
            <provord:ProvisioningOrderSchedule>
                <provord:RequestedDeliveryDateTime>{data($eSourceOrderLineItem/oms:orderItemRef/oms:RequestedDeliveryDate)}</provord:RequestedDeliveryDateTime>          
            </provord:ProvisioningOrderSchedule>
                                 
        </provord:ProvisioningOrderLine>
    )
};


let $sEbmId                         := local:generateUniqueId($sOrderId, $sVersion)
let $somRequest                     := local:createSoapMessage($eTaskData,$sEbmId)
let $sSomRequest                    := saxon:serialize($somRequest, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sTaskData                      := saxon:serialize($eTaskData, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)

return
    (
        log:info($log, fn:concat('GetOrder.Response : ', $sTaskData)),
        log:info($log, fn:concat('SOM Order Payload : ', $sSomRequest)),
        outboundMessage:setStringProperty( $outboundMessage, "URI", $osmURI),
        outboundMessage:setStringProperty( $outboundMessage, "_wls_mimehdrContent_Type", $mimeContextType),
        $sSomRequest
    )