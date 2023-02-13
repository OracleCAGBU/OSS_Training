import module namespace soaplib                     = "http://www.training.com/comms/ordermanagement/common/soap/library" at "http://www.training.com/resources/lib/SoapLibrary.xqy";
import module namespace systeminteractionmodule     = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule" at "http://www.training.com/resources/lib/SystemInteractionModule.xqy";
import module namespace uimlib                      = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";

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
declare variable $serviceManagementURI  := "/InventoryWS/InventoryWSJMS";
declare variable $mimeContextType       := "text/xml; charset=UTF-8";


(: Creates the soap message :)
declare function local:createSoapMessage(
    $tmpaction as xs:string,
    $orderData as element()*) as element()*
{ 
    <soapenv:Envelope 
        xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:bi="http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction"
        xmlns:invbi="http://xmlns.oracle.com/communications/inventory/businessinteraction"
        xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"> 
        { 
            (: Add the security header to the soap request :)
            soaplib:createHeaderForCustomCredential($sUimUsername, $sUimPassword),
            local:addSoapBody($tmpaction, $orderData) 
        }
    </soapenv:Envelope>            
};

(: Create the soapenv:Body element. Child elements should be created directly here. Groups should be created through a call to another function :)   
declare function local:addSoapBody(
    $tmpaction as xs:string,
    $orderData as element()*) as element()*
{ 
   (: <soapenv:Body> :)
   let $elementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Body))
   let $interactionAction := $tmpaction
   where (exists($orderData ))
   return
       element {$elementname } {
             systeminteractionmodule:addUpdateInteraction($interactionAction, $orderData)
       }
};

(: Determines the action that goes along with the request. This is dependent on the task that is calling this xquery :)
declare function local:processInteractionAction(
    $taskname as xs:string, 
    $order as element()*) as element()*
{
    if(fn:exists($taskname) and $taskname = $uimlib:CompleteTask)
    then (
         let $interactionAction := $uimlib:completeAction
         return local:createSoapMessage($interactionAction, $order)
    )
    else if(fn:exists($taskname) and $taskname = $uimlib:IssueTask)
    then (
        let $interactionAction := $uimlib:issueAction
        return local:createSoapMessage($interactionAction, $order)
    )
    else if(fn:exists($taskname) and $taskname = $uimlib:ApproveTask)
    then (
        let $interactionAction := $uimlib:approveAction
        return local:createSoapMessage($interactionAction, $order)
    )
    else ()
}; 

let $eUpdateRequest                      := local:processInteractionAction( $sTaskName, $orderData )
let $sUpdateRequest                      := saxon:serialize($eUpdateRequest, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sOrderData                          := saxon:serialize($orderData, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)

return
    (
        log:info($log, fn:concat($sTaskName, ' : ', $sUpdateRequest)),
        outboundMessage:setStringProperty($outboundMessage, "URI", $serviceManagementURI),
        outboundMessage:setStringProperty($outboundMessage, "_wls_mimehdrContent_Type", $mimeContextType),
        $eUpdateRequest
    )