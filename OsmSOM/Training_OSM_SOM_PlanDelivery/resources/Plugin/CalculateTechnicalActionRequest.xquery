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
declare variable $sReference            := $orderData/oms:Reference/text();
declare variable $sUsername             := 'oms-automation';
declare variable $sPassword             := 'admin123';
declare variable $sUimUsername          := 'uimadmin';
declare variable $sUimPassword          := 'oracle123';
declare variable $ctoURI                := "/CTOUim/CTOUimJMS";
declare variable $mimeContextType       := "text/xml; charset=UTF-8";

(: Creates the soap message :)
declare function local:createSoapMessage(
    $bid as xs:string *) as element()*
{ 
    <soapenv:Envelope 
        xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:bi="http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction"
        xmlns:invbi="http://xmlns.oracle.com/communications/inventory/businessinteraction"
        xmlns:techws="http://xmlns.oracle.com/communications/inventory/webservice/technical"
        xmlns:invtech="http://xmlns.oracle.com/communications/inventory/technical"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"> 
        { 
            (: Add the security header to the soap request :)
            soaplib:createHeaderForCustomCredential($sUimUsername, $sUimPassword),
            local:addSoapBody( $bid ) 
        }
    </soapenv:Envelope>            
};

(: Create the soapenv:Body element. Child elements should be created directly here. Groups should be created through a call to another function :)   
declare function local:addSoapBody(
    $bid as xs:string *) as element()*
{ 
   (: <soapenv:Body> :)
   let $elementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Body))
   
   where (exists($bid ))
   return
       element {$elementname } {
             systeminteractionmodule:addCalculateTechnicalActionsRequest($bid)
       }
};

let $bid                                                  := uimlib:getBICorrelationID($orderData)/text()
let $eCalculateTechnicalActionRequest                     := local:createSoapMessage($bid)
let $sOrderData                                           := saxon:serialize($orderData, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sCalculateTechnicalActionRequest                     := saxon:serialize($eCalculateTechnicalActionRequest, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sCorrelationId                                       := systeminteractionmodule:getCorrelationID($orderData)

return
    (
        (:log:info($log, fn:concat('GetOrder.Response : ', $sOrderData)),:)
        log:info($log, fn:concat('CalculateTechnicalAction Request : ', $sCalculateTechnicalActionRequest)),
        outboundMessage:setJMSCorrelationID($outboundMessage,$sCorrelationId),
        outboundMessage:setStringProperty($outboundMessage, "URI", $ctoURI),
        outboundMessage:setStringProperty($outboundMessage, "_wls_mimehdrContent_Type", $mimeContextType),
        $eCalculateTechnicalActionRequest
    )