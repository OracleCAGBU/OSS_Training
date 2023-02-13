(:  Fetch contents of LineName from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.somtraining.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(:  For example if incoming order contains line id in the <lineName> tag :)
(:  XQuery to fetch line id would look like  :)
(:     fn:normalize-space(fulfillord:lineName/text())     :)

(: TO DO: Write XQuery to identify LineName :)

let $orderItem := .
return fn:normalize-space($orderItem/corecom:ItemReference/corecom:Name/text())
