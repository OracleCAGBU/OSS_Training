(: This file contains basic constructs required. Please verify and complete the required parts. :)

module namespace compsbinding                    = "http://www.training.com/compsbinding";

(: namespace of the incoming order :)
declare namespace oms                            = "urn:com:metasolv:oms:xmlapi:1";
declare namespace corecom                        = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace fulfillord                     = "http://www.training.com/inputMessage";

(: Java object namespace :)
declare namespace log                            = "java:org.apache.commons.logging.Log";

declare variable $compsbinding:BINDING_DATA_URL := "http://www.training.com/binding/"; 


(:
 : This function will return a single element for the given key value pair with it's name looked up in the given map.
 :)
declare function compsbinding:getAttributeEntry(
    $eMap as node(),
    $eSpecification as element(),
    $inputDoc as document-node()) as element()* 
{ 
    let $sKey                       := $eSpecification/corecom:Name/text()
    let $sAttribute                 := $eMap/attributes/attribute[key = $sKey]
    let $sConceptualModelUri        := $eMap/attributes/@conceptualModelURI
    let $sClassificationCode        := $eMap/attributes/@classificationCode
    let $sValue                     := $sAttribute/value/text()
    let $eResult                    :=
                                    if (fn:exists($sValue))
                                    then
                                    (
                                        element {QName($sConceptualModelUri, $sValue)}{
                                            $eSpecification/corecom:Value/text()
                                        }
                                    )
                                    else ()
   return 
         (
            $eResult
         )
 };
 
 
(:
 : This function will return a single element for the given custom key with it's name looked up in the given map.
 :)
declare function compsbinding:getAttributeEntryCustom(
    $eMap as node(),
    $eOrderItem as element(),
    $inputDoc as document-node()) as element()* 
{ 
    let $eOrderData                 := $inputDoc//fulfillord:DataArea
    let $sPartyName                 := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountName/text()
    let $eAddress                   := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountContactAddressCommunication/
                                       corecom:AddressCommunication/corecom:Address
    let $sHouseNumber               := $eAddress/corecom:HouseNumber                                      
    let $sBuildingName              := $eAddress/corecom:BuildingName
    let $sStreetName                := $eAddress/corecom:StreetName
    let $sLatitude                  := $eAddress/corecom:Latitude
    let $sLongitude                 := $eAddress/corecom:Longitude
    let $sProductAction             := $eOrderItem/fulfillord:ServiceActionCode
    let $sConceptualModelUri        := $eMap/attributes/@conceptualModelURI
    let $sClassificationCode        := $eMap/attributes/@classificationCode
    let $eResult                    :=

        (
          if($sProductAction='Add')
          then
          (
            if($eMap/attributes/attribute[key = 'PartyName'] and fn:exists($sPartyName)) then
                element {QName($sConceptualModelUri, 'PartyName')}{
                    $sPartyName
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'HouseNumber'] and fn:exists($sHouseNumber)) then
                element {QName($sConceptualModelUri, 'HouseNumber')}{
                    $sHouseNumber
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'BuildingName'] and fn:exists($sBuildingName)) then
                element {QName($sConceptualModelUri, 'BuildingName')}{
                    $sBuildingName
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'StreetName'] and fn:exists($sStreetName)) then
                element {QName($sConceptualModelUri, 'StreetName')}{
                    $sStreetName
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'Latitude'] and fn:exists($sLatitude)) then
                element {QName($sConceptualModelUri, 'Latitude')}{
                    $sLatitude
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'Longitude'] and fn:exists($sLongitude)) then
                element {QName($sConceptualModelUri, 'Longitude')}{
                    $sLongitude
                }
            else(),
            
            if($eMap/attributes/attribute[key = 'ProductAction'] and fn:exists($sProductAction)) then
                element {QName($sConceptualModelUri, 'ProductAction')}{
                    $sProductAction
                }
            else()
          )
          else()
        )
    return 
    (
        $eResult
    )
 }; 
 
declare function compsbinding:getOrderLineAttributes(
    $sFulfillmentItemCode as xs:string,
    $eOrderItem as element()) as element()* 
{ 
    let $eExtAttributes :=  $eOrderItem/fulfillord:SalesOrderLineSpecificationGroup
    let $eSpecifications := $eExtAttributes/corecom:SpecificationGroup/corecom:Specification
    return
    ( 
        $eSpecifications
    )
};

declare function compsbinding:constructBindingUrl(
    $sFulfillmentItemCode as xs:string,
    $eMapFileName as xs:string) as xs:string 
{ 
    let $sFic := $sFulfillmentItemCode
    let $sUrl := fn:concat($compsbinding:BINDING_DATA_URL, $eMapFileName)
    return
    (
        $sUrl
    )
};    
 