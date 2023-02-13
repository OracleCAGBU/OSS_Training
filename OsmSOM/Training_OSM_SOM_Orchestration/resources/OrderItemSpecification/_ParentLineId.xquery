(:  Fetch contents of ParentLineId from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.somtraining.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(:  For example if incoming order contains line id in the <parentLineId> tag :)
(:  XQuery to fetch line id would look like  :)
(:    fn:normalize-space(fulfillord:parentLineId/text())       :)

(: TO DO: Write XQuery to identify ParentLineId :)

let $orderItem := .
return fn:normalize-space($orderItem/corecom:ParentSalesOrderLineIdentification/corecom:BusinessComponentID/text())
