(:  Fetch contents of Fulfillment Pattern from  productSpecMapping.xml :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.somtraining.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace log= "java:org.apache.commons.logging.Log";

declare variable $log external;


     let $productSpecMap := vf:instance('ProductSpecMap')
     let $orderItem := .    
     (: TO DO: Write XQuery to identify Fulfillment Pattern from incoming order :)
     (: Example: let $productSpecName :=  fn:normalize-space(fulfillord:productSpec/text()) :)
     let $productSpecName := fn:normalize-space($orderItem/corecom:ItemReference/corecom:PrimaryClassificationCode/text())
     
	return
	(
		if ($productSpecName = ('SIM Card','4G','Mobile Service') ) then (
		 log:info($log, fn:concat('Inside if productSpecMap:',$productSpecName)),
		
		'SOFP_MOBILE_Standard'
		)
		else if ($productSpecName != '') then (
		 log:info($log, fn:concat('Inside else sif productSpecMap:',$productSpecName)),
		
			fn:normalize-space($productSpecMap/productSpec[fn:lower-case(@name)=fn:lower-case($productSpecName)]/fulfillmentPattern/text())
			 
		)
		else (
		 log:info($log, fn:concat('Inside else productSpecMap:',$productSpecName)))
		 
	)		
		
		