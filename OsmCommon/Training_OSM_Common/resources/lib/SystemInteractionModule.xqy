module namespace systeminteractionmodule    = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule";

import module namespace uimlib              = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";

declare namespace UUID                      = "java:java.util.UUID";
declare namespace oms                       = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                    = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace bi                        = "http://xmlns.oracle.com/communications/inventory/webservice/businessinteraction";
declare namespace invbi                     = "http://xmlns.oracle.com/communications/inventory/businessinteraction";

(: functions :)

(: 
 : Function to generate a unique UUID
:)

declare function systeminteractionmodule:generateUniqueId()
    as xs:string {
    string(UUID:randomUUID())
};

(:This function generate unique CorrelationID:)
declare function systeminteractionmodule:setCorrelationID (
     $sOrderId as xs:string,
     $sVersion as xs:string) {

    let $sUniqueId                          := systeminteractionmodule:generateUniqueId()
    let $sCorrelationID                     :=  fn:concat($sOrderId, '_', $sUniqueId, '_', $sVersion)
    return
        $sCorrelationID  
};

declare function systeminteractionmodule:getSourceLineOrderItemsFromTransformedLineItem(
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

declare function systeminteractionmodule:getPrimarySourceLineOrderItemsFromTransformedLineItem(
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


(: Creates the OSM Order Data Update after a captureInteractionResponse  message :)
declare function systeminteractionmodule:createOrderDataUpdateForCaptureInteraction(
    $processResponse as element()*,
    $orderData as element()*) as element()* {

    let $functionName := $uimlib:DesignServiceFunction
    let $componentKey := $orderData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$functionName]/oms:componentKey/text()
    let $bid := $processResponse/bi:interaction/invbi:header/invbi:id/text()
    let $biCorrelationId := $orderData/oms:Reference/text()
    return
           <OrderDataUpdate xmlns="http://www.metasolv.com/OMS/OrderDataUpdate/2002/10/25"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <UpdatedNodes>
            <_root>{
                        if(exists($bid))then(
                        <BusinessInteractions>
                            <BusinessInteraction>
                                <Key>{$componentKey}</Key>                      
                                <InteractionID>{$bid}</InteractionID>
                                <CorrelationID>{$biCorrelationId}</CorrelationID>
                            </BusinessInteraction>
                        </BusinessInteractions>
                        )else()
                  } 
            </_root>
        </UpdatedNodes>
       </OrderDataUpdate>
};