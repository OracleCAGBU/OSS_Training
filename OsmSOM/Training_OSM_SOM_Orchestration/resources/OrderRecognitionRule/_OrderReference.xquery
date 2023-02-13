(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord    = "http://www.somtraining.com/inputMessage";
declare namespace corecom       = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Ensure that incoming order has been mentioned name space:)

let $eOrderData      := fn:root(.)//fulfillord:DataArea
let $sOrderNumber    := $eOrderData/fulfillord:ProcessProvisioningOrder/corecom:SalesOrderReference/corecom:SalesOrderIdentification/corecom:ID/text()

return $sOrderNumber
