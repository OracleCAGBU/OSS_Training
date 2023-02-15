(:  Fetch contents of LineId from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.tomtraining.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(:  For example if incoming order contains line id in the <lineId> tag :)
(:  XQuery to fetch line id would look like  :)
(:    fn:normalize-space(fulfillord:lineId/text())       :)

(: TO DO: Write XQuery to identify LineId :)

let $orderItem := .
return fn:normalize-space($orderItem/fulfillord:ActionCode/text())
