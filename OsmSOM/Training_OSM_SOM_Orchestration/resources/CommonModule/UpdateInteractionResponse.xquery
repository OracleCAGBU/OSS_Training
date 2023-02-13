import module namespace soaplib                     = "http://www.training.com/comms/ordermanagement/common/soap/library" at "http://www.training.com/resources/lib/SoapLibrary.xqy";
import module namespace uimlib                      = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";
import module namespace systeminteractionmodule     = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule" at "http://www.training.com/resources/lib/SystemInteractionModule.xqy";

declare namespace context                           = "java:com.mslv.oms.automation.TaskContext";
declare namespace outboundMessage                   = "java:javax.jms.TextMessage";
declare namespace log                               = "java:org.apache.commons.logging.Log";
declare namespace automator                         = "java:oracle.communications.ordermanagement.automation.plugin.ScriptReceiverContextInvocation";
declare namespace provord                           = "http://www.somtraining.com/inputMessage";
declare namespace corecom                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace fulfillord                        = "http://www.training.com/inputMessage";
declare namespace saxon                             = "http://saxon.sf.net/";
declare namespace xsl                               = "http://www.w3.org/1999/XSL/Transform";
declare namespace oms                               = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                            = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace cttransns                         = "COM_SalesOrderFulfillment";
declare namespace bi                                = "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction";
declare namespace invbi                             = "http://xmlns.oracle.com/communications/inventory/businessinteraction";
declare namespace soapenv                           = "http://schemas.xmlsoap.org/soap/envelope/";

declare option saxon:output "method=xml";
declare option saxon:output "saxon:indent-spaces=4";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

declare variable $eOrderData            := fn:root(automator:getOrderAsDOM($automator))/oms:GetOrder.Response;
declare variable $sTaskName             := context:getTaskMnemonic($context);
declare variable $sOrderId              := $eOrderData/oms:OrderID/text();
declare variable $sVersion              := $eOrderData/oms:Version/text();
declare variable $response              := fn:root(.);
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

let $soapBody                           := $response/soapenv:Envelope/soapenv:Body
let $eResponseMessage                   := $soapBody/bi:updateInteractionResponse
let $sResponseMessage                   := saxon:serialize($eResponseMessage,<xsl:output method="xml" omit-xml-declaration="yes" indent="yes" saxon:indent-spaces="4"/>)

return
    (
        log:info($log, fn:concat('UpdateInteractionResponse : ', $sResponseMessage)),
        if($eResponseMessage/bi:interaction/invbi:header/invbi:state/text()=('IN_PROGRESS','COMPLETED'))then(
        log:info($log, fn:concat($sTaskName, ' ', 'completing')),
        context:completeTaskOnExit($context,'success')
        )
        else(log:info($log, 'Failed response received'))
    )