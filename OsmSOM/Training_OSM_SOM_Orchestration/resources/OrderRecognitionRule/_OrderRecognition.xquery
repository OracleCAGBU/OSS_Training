(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.somtraining.com/inputMessage";

(: Ensure that incoming order has mentioned name space:)

let $eOrderData      := fn:root(.)//fulfillord:DataArea
let $sTypeCode       := $eOrderData/fulfillord:ProcessProvisioningOrder/fulfillord:TypeCode/text()

return 
    (
        fn:namespace-uri(.) = 'http://www.somtraining.com/inputMessage' and $sTypeCode='SERVICE ORDER'
    )
