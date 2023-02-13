(:  returns Fulfillment mode expression :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Replace COM_Deliver with COM_Deliver when relevant. :)
(: Returns delivery mode "COM_Deliver" and name space "COM_SalesOrderFulfillment" :)
declare variable $SALESORDER_FULFILLMENT_MODE := "COM_Deliver";
declare variable $SALESORDER_FULFILLMENT_NS := "COM_SalesOrderFulfillment";
let $fulfillmentModeCode := <osm:fulfillmentMode name="{$SALESORDER_FULFILLMENT_MODE}" namespace="{$SALESORDER_FULFILLMENT_NS}"/>

return  $fulfillmentModeCode  
