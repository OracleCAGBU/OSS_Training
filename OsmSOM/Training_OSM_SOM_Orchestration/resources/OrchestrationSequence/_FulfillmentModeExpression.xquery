(:  returns Fulfillment mode expression :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.somtraining.com/inputMessage";

(: Replace SOM_Deliver with SOM_Cancel when relevant. :)
(: Returns delivery mode "SOM_Deliver" and name space "SOM_ProvisioningOrderFulfillment" :)
declare variable $SALESORDER_FULFILLMENT_MODE := "SOM_Deliver";
declare variable $SALESORDER_FULFILLMENT_NS := "SOM_ProvisioningOrderFulfillment";
let $fulfillmentModeCode := <osm:fulfillmentMode name="{$SALESORDER_FULFILLMENT_MODE}" namespace="{$SALESORDER_FULFILLMENT_NS}"/>

return  $fulfillmentModeCode  
