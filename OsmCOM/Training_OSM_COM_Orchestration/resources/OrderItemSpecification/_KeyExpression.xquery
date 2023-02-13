(:  Fetch contents of LineId from properties :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare order item spec name space:)
declare namespace im="COM_SalesOrderFulfillment";

(:  For example if order item defined a property called LineId :)
(:  XQuery to fetch line id would look like  :)
(:    fn:normalize-space(osm:properties/im:LineId/text())       :)

(: TO DO: Write XQuery to identify LineId :)
fn:normalize-space(osm:properties/im:LineId/text())