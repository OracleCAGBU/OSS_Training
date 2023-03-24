(:  Fetch contents of Fulfillment Pattern from  productSpecMapping.xml :)

(: Declare OSM name space :)
declare namespace osm           = "http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord    = "http://www.tomtraining.com/inputMessage";
declare namespace corecom       = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

     let $productSpecMap := vf:instance('ProductSpecMap')
     
     (: TO DO: Write XQuery to identify Fulfillment Pattern from incoming order :)
     (: Example: let $productSpecName :=  fn:normalize-space(fulfillord:productSpec/text()) :)
      let $orderItem := .
      let $productSpecName := fn:normalize-space($orderItem/corecom:ClassificationCode/text())
      let $fulfillmentPattern := fn:normalize-space($productSpecMap/productSpec[fn:lower-case(@name)=fn:lower-case($productSpecName)]/fulfillmentPattern/text())
     
	return
		if ($fulfillmentPattern != '')
		then $fulfillmentPattern
		else if($productSpecName='MobileInternet_RFS')
		then "TOFP_MOBILE_Standard"
        else ()
			(: TO DO: If there is a product specification with no matching fulfillment pattern, return generic fulfillment pattern name :)
			(: For this to work at run time either create a generic fulfillment pattern with name 'Non.Service.Offer' or change it what needs to be returned :)
		
		