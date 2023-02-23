(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm               = "http://xmlns.oracle.com/communications/ordermanagement/model";
declare namespace context           = "java:com.mslv.oms.automation.OrderNotificationContext";
declare namespace outboundMessage   = "java:javax.jms.TextMessage";
declare namespace oms               = "urn:com:metasolv:oms:xmlapi:1";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

declare variable $eOrderData             := fn:root(.)/oms:GetOrder.Response;


(: Ensure that incoming order has been mentioned name space:)

let $sCOMCorrelationID                  := $eOrderData/oms:_root/oms:COMCorrelationID/text()

let $sCompletionMilestone               := <Milestone>
                                                <Status>SOM_Completed</Status>
                                           </Milestone>
                             
return 
        (
            outboundMessage:setStringProperty($outboundMessage,'COM_ORDER_CORRELATION_PROPERTY',$sCOMCorrelationID),
            $sCompletionMilestone 
        )
