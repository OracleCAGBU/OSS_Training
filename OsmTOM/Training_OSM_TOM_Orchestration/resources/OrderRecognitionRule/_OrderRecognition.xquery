(:  Identify incoming order :)

(: Declare OSM name space :)
declare namespace osm           = "http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord    = "http://www.tomtraining.com/inputMessage";
declare namespace corecom       = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Ensure that incoming order has mentioned name space:)

let $eOrderData      := fn:root(.)//fulfillord:DataArea
let $sTypeCode       := $eOrderData/fulfillord:ProcessTechnicalOrder/corecom:TypeCode/text()

return 
    (
        fn:namespace-uri(.) = 'http://www.tomtraining.com/inputMessage' and $sTypeCode='TECHNICAL ORDER'
    )
