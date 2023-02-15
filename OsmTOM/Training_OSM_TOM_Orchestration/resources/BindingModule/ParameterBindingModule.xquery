(: This file contains basic constructs required. Please verify and complete the required parts. :)

module namespace binding                    = "http://xmlns.oracle.com/communications/ordermanagement/binding"; 

declare namespace techord                   = "http://www.tomtraining.com/inputMessage";
declare namespace corecom                   = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

declare variable $binding:BINDING_DATA_URL := "http://www.tomtraining.com/binding/"; 

declare function binding:constructBindingUrl(
    $sFulfillmentItemCode as xs:string,
    $eMapFileName as xs:string) as xs:string 
{ 
    let $sFic := $sFulfillmentItemCode
    let $sUrl := fn:concat($binding:BINDING_DATA_URL, $eMapFileName)
    return
    (
        $sUrl
    )
};

(:This function will return the list of key value pairs elements defined on the order item:)
declare function binding:getIncomingParameters ($tol as element()) as element()* 
{ 
    let $techOrderLine := $tol
    return $techOrderLine/techord:TechnicalParameters/*
  
 };
 
 (:This function will return a single element for the given key value pair with it's name looked up in the given map.:)
 declare function binding:getAttributeEntry ( $uri as xs:string, $map as node(), $param as element() ) as element()* 
{ 
    (: this is the name of the incoming param :)
    let $key := local-name($param)    
    (: this is the matched attribute from the $map :)
    let $attribute := $map/attributes/attribute[key = $key]
    
    (: this is the label that we should use for this attribute, from the $map :)
    let $label := $attribute/value/text()

    
    return 
    (
        if (exists($label)) then
            element {QName($uri, $label)}{
               if (count($attribute/attribute) > 0) then
                    for $p in $param/*
                        return
                        (binding:getContextAttributeEntry($uri, $attribute, $p))
                else 
                    (: This is the incoming parameter value :)                
                    $param/text()
            }
           
        else
            ()
    )
 }; 
 
 declare function binding:getContextAttributeEntry($uri as xs:string, $map as node(), $specification as element()) as element()*
{
    let $key := fn:local-name($specification)
    let $attribute := $map/attribute[key = $key]

    (: this is the label that we should use for this attribute, from the $map :)
    let $label := $attribute/value/text()
    
    return 
    (
        if (exists($label)) then
            element {QName($uri, $label)} {
                if (count($attribute/attribute) > 0) then
                    for $param in $specification/*
                        return
                        (binding:getContextAttributeEntry($uri, $attribute, $param))
                else 
                    (: This is the incoming parameter value :)                
                    $specification/text()
            }
        else
            ()
    )
};


