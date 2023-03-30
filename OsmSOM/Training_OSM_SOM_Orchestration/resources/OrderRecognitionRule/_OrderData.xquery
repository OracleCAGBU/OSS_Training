(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm               ="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord        ="http://www.somtraining.com/inputMessage";
declare namespace corecom           = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Ensure that incoming order has mentioned name space:)

let $eOrderData             := fn:root(.)//fulfillord:DataArea
let $sCOMCorrelationID      := $eOrderData/fulfillord:ProcessProvisioningOrder/corecom:Correlation/corecom:COMCorrelationID/text()
let $sOrderNumber           := $eOrderData/fulfillord:ProcessProvisioningOrder/corecom:Identification/corecom:ID/text()
let $sRevision              := $eOrderData/fulfillord:ProcessProvisioningOrder/corecom:Identification/corecom:Revision/corecom:Number/text()

(: Returns all contents of incoming order. :)
(: TO DO: Modify the XQuery to only select subset of incoming order data.:)
(: This XQuery selects all the contents of the incoming order. :)

return

<_root>
    <Order>
        <OrderNumber>{$sOrderNumber}</OrderNumber>
        <Version>{$sRevision}</Version>
    </Order>
    <COMCorrelationID>{$sCOMCorrelationID}</COMCorrelationID>
</_root>
