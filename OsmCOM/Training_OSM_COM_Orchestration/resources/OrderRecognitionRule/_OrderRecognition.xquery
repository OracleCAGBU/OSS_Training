(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";

(: Ensure that incoming order has mentioned name space:)

let $eOrderData      := fn:root(.)//fulfillord:DataArea
let $sTypeCode       := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/fulfillord:TypeCode/text()

return 
    (
        fn:namespace-uri(.) = 'http://www.training.com/inputMessage' and $sTypeCode='SALES ORDER'
    )
