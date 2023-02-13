(:  Fetch contents of Fulfillment Pattern from  productSpecMapping.xml :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

     let $productSpecMap := vf:instance('ProductSpecMap')
     
     (: TO DO: Write XQuery to identify Fulfillment Pattern from incoming order :)
     (: Example: let $productSpecName :=  fn:normalize-space(fulfillord:productSpec/text()) :)
     let $orderItem := .
     let $productSpecName := fn:normalize-space($orderItem/corecom:ItemReference/corecom:PrimaryClassificationCode/text())
     
	return
	   (:
		if ($productSpecName != '')
		then
			fn:normalize-space($productSpecMap/productSpec[fn:lower-case(@name)=fn:lower-case($productSpecName)]/fulfillmentPattern/text())
		else ()
	   :) "Service.Broadband"
	   
		
		