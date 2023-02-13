(:  Fetch contents of LineId from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord                        = "http://www.training.com/inputMessage";
declare namespace corecom                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace provord                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/ProvisioningOrder/V1";
declare namespace saxon                             = "http://saxon.sf.net/";
declare namespace xsl                               = "http://www.w3.org/1999/XSL/Transform";
declare namespace oms                               = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                            = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace cttransns                         = "COM_SalesOrderFulfillment";

(:  For example if incoming order contains line id in the <lineId> tag :)
(:  XQuery to fetch line id would look like  :)
(:    fn:normalize-space(fulfillord:lineId/text())       :)

(: TO DO: Write XQuery to identify LineId :)

declare function local:getSourceLineOrderItemsFromTransformedLineItem(
    $eTransformedOrderLineItem as element (),
    $eMappingContext as element (),
    $eComponent as element()) as element ()*
{
    let $sTransformedOrderLineItemLineId   := $eTransformedOrderLineItem/oms:orderItemRef/toibns:LineId/text()
    let $eTargetMapping                    := $eMappingContext/oms:ProviderFunction/oms:TargetMapping[oms:target/text()=$sTransformedOrderLineItemLineId]
    
    return (
        for $eSourceMap in $eTargetMapping/oms:SourceMapping
        let $sSourceMapLineId := $eSourceMap/oms:source/text()
        return
            $eComponent/oms:orderItem[oms:orderItemRef/oms:LineId/text()=$sSourceMapLineId]
    )
};

declare function local:getPrimarySourceLineOrderItemsFromTransformedLineItem(
    $eTransformedOrderLineItem as element (),
    $eMappingContext as element (),
    $eComponent as element()) as element ()*
{
    let $sTransformedOrderLineItemLineId := $eTransformedOrderLineItem/oms:orderItemRef/toibns:LineId/text()
    let $eTargetMapping := $eMappingContext/oms:ProviderFunction/oms:TargetMapping[oms:target/text()=$sTransformedOrderLineItemLineId]
    
    return (
            for $eSourceMap in  $eTargetMapping/oms:SourceMapping
            let $sSourceMapLineId := $eSourceMap/oms:source/text()
            
            where (fn:contains($eSourceMap/oms:InstantiatingMappingRule/oms:name/text(),"Primary"))
            return
                $eComponent/oms:orderItem[oms:orderItemRef/oms:LineId/text()=$sSourceMapLineId]
    )
};

let $eOrderData := fn:doc('C:/Debapriya/Oracle/Projects/OSS Training Solution/GetOrderResponse/GetOrderResponse.xml')/oms:GetOrder.Response
let $eMappingContext := $eOrderData/oms:_root/oms:ControlData/oms:MappingContext
let $eComponent := $eOrderData/oms:_root/oms:ControlData/oms:Functions/oms:ProvisioningFunction
let $sComponentKey := $eComponent/oms:componentKey/text()
let $eTransformedOrderLineItems := $eComponent/oms:transformedOrderItem
    
    return(
        for $eTransformedOrderLineItem in $eTransformedOrderLineItems
        let $eSourceLineOrderItems       := local:getSourceLineOrderItemsFromTransformedLineItem($eTransformedOrderLineItem,$eMappingContext,$eComponent)
        let $ePrimarySourceLineOrderItem := local:getPrimarySourceLineOrderItemsFromTransformedLineItem($eTransformedOrderLineItem,$eMappingContext,$eComponent)
        return 
           $ePrimarySourceLineOrderItem
    )

