(:  returns Fulfillment mode expression :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.tomtraining.com/inputMessage";

(: Replace TOM_Deliver with TOM_Cancel when relevant. :)
(: Returns delivery mode "TOM_Deliver" and name space "TOM_TechnicalOrderFulfillment" :)
declare variable $SALESORDER_FULFILLMENT_MODE := "TOM_Deliver";
declare variable $SALESORDER_FULFILLMENT_NS := "TOM_TechnicalOrderFulfillment";
let $fulfillmentModeCode := <osm:fulfillmentMode name="{$SALESORDER_FULFILLMENT_MODE}" namespace="{$SALESORDER_FULFILLMENT_NS}"/>

return  $fulfillmentModeCode  
