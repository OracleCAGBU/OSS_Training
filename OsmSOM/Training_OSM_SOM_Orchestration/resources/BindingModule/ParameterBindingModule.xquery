(: This file contains basic constructs required. Please verify and complete the required parts. :)

module namespace sompsbinding                    = "http://www.somtraining.com/sompsbinding";

(: namespace of the incoming order :)
declare namespace oms                            = "urn:com:metasolv:oms:xmlapi:1";
declare namespace corecom                        = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace fulfillord                     = "http://www.somtraining.com/inputMessage";

(: Java object namespace :)
declare namespace log                            = "java:org.apache.commons.logging.Log";
declare namespace trans                          = "COM_SalesOrderFulfillment";

declare variable $sompsbinding:BINDING_DATA_URL := "http://www.somtraining.com/binding/";  
 
declare function sompsbinding:getOrderLineAttributes(
    $sFulfillmentItemCode as xs:string,
    $eOrderItem as element()) as element()* 
{ 
    let $eExtAttributes :=  $eOrderItem/fulfillord:CSODynamicParams/trans:dynamicParams/*
    return
    ( 
        $eExtAttributes
    )
};

declare function sompsbinding:constructBindingUrl(
    $sFulfillmentItemCode as xs:string,
    $eMapFileName as xs:string) as xs:string 
{ 
    let $sFic := $sFulfillmentItemCode
    let $sUrl := fn:concat($sompsbinding:BINDING_DATA_URL, $eMapFileName)
    return
    (
        $sUrl
    )
};

(: This function will return a single complex element for the given key value pair with it's name looked up in the given map. :)

declare function sompsbinding:getAttributeEntry(
    $uri as xs:string, 
    $map as node(), 
    $param as element()) as element()* 
{ 
    (: this is the name of the incoming param :)
    let $key := local-name($param)    
    (: this is the matched attribute from the $map :)
    (: Note: need to use the value part since CDT type is using value as xml tag name  :)
    let $attribute := $map/attributes/attribute[value = $key]
    where (fn:exists($attribute))
    return
        sompsbinding:getChildContextAttributeEntry($uri, $param, $attribute)
}; 

(: This function will return a single complex element for the given key value pair with it's name looked up in the given map. :)

declare function sompsbinding:getChildContextAttributeEntry(
    $uri as xs:string, 
    $specification as element(), 
    $attribute as element()) as element()*
{
    (: this is the label that we should use for this attribute, from the $map :)
    let $label := $attribute/value/text()
    where (fn:exists($label) and $label != "")
    return
    (    
        element {QName($uri, $label)}
        {
            if (fn:count($attribute/attribute) > 0)
            then
            (
                for $param in $specification/*
                let $childkey := fn:local-name($param)
                (: Note: need to use the value part since CDT type is using value as xml tag name  :)
                let $childAttribute := $attribute/attribute[value = $childkey]
                where (fn:exists($childAttribute))
                return
                    sompsbinding:getChildContextAttributeEntry($uri, $param, $childAttribute)
            )                        
            else
            (                
                (: This is the incoming parameter value :)                
                $specification/text()
            )                    
        }
    )            
};
 