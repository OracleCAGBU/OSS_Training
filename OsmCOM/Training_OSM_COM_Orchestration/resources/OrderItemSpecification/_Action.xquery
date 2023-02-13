(:  Fetch contents of LineId from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(:  For example if incoming order contains action code in the <action> tag :)
(:  XQuery to fetch action code would look like  :)
(:    fn:normalize-space(fulfillord:action/text())       :)

(: TO DO: Write XQuery to identify actionCode :)

let $orderItem := .
return fn:normalize-space($orderItem/fulfillord:ServiceActionCode/text())
