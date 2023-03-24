(:  Fetch contents of Fulfillment Pattern from  productSpecMapping.xml :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";
declare namespace log                               = "java:org.apache.commons.logging.Log";
(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";


declare variable $log external;

     (: TO DO: Write XQuery to identify Fulfillment Pattern from incoming order :)
     (: Example: let $productSpecName :=  fn:normalize-space(fulfillord:productSpec/text()) :)
     let $orderItem := .
     let $productSpecName := fn:normalize-space($orderItem/corecom:ItemReference/corecom:ClassificationCode[@listID="FulfillmentItemCode"]/text())
     let $productSpecMap := vf:instance('ProductSpecMap')
     let $fulfillmentPattern := fn:normalize-space($productSpecMap/productSpec[fn:lower-case(@name)=fn:lower-case($productSpecName)]/fulfillmentPattern/text())
     
    return
       (
            (: Mobile Changes :)
           if ($productSpecName = ('Mobile Service','4G','Bundle','BRM Technical Products', 'SIM Card','Handset')) then
            "Service.Mobile"
           else
            $fulfillmentPattern
       )