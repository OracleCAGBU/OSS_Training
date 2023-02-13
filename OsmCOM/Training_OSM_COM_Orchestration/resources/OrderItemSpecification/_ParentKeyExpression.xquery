(:  Fetch contents of ParentLineId from properties :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare order item spec name space:)
declare namespace im="COM_SalesOrderFulfillment";

(:  For example if order item defined a property called ParentLineId :)
(:  XQuery to fetch line id would look like  :)
(:    fn:normalize-space(osm:properties/im:ParentLineId/text())       :)

(: TO DO: Write XQuery to identify ParentLineId :)
fn:normalize-space(osm:properties/im:ParentLineId/text())
