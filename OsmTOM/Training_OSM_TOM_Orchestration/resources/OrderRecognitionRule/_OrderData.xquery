(:  Identify contents of the incoming order :)

(: Declare OSM name space :)
declare namespace osm           = "http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord    = "http://www.tomtraining.com/inputMessage";
declare namespace corecom       = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Returns all contents of incoming order. :)
(: TO DO: Modify the XQuery to only select subset of incoming order data.:)
(: This XQuery selects all the contents of the incoming order. :)

let $eOrderData             := fn:root(.)//fulfillord:DataArea
let $sSOMCorrelationID      := $eOrderData/fulfillord:ProcessTechnicalOrder/corecom:Correlation/corecom:SOMCorrelationID/text()

(: Returns all contents of incoming order. :)
(: TO DO: Modify the XQuery to only select subset of incoming order data.:)
(: This XQuery selects all the contents of the incoming order. :)

return

<_root>
    <SOMCorrelationID>{$sSOMCorrelationID}</SOMCorrelationID>
</_root>
