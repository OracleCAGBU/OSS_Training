(:  Identifies order items from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.tomtraining.com/inputMessage";

(:  For example if incoming order items are contained in <salesOrderLine> tag :)
(: XQuery might look like: :)
(:    .// fulfillord:salesOrderLine    :)

(: TO DO: Write XQuery to identify line items from incoming order items:)

let $orderItems := .//fulfillord:DataArea/fulfillord:ProcessTechnicalOrder/fulfillord:TOL
return $orderItems
