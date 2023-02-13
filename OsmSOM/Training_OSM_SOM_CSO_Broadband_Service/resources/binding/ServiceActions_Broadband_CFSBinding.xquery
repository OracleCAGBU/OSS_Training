(: This file contains basic constructs required. Please verify and complete the required parts. :)
(: Declare OSM name space :)

import module namespace sompsbinding = "http://www.somtraining.com/sompsbinding" at "http://www.somtraining.com/binding_module/ParameterBindingModule.xquery";

declare namespace osm                = "http://xmlns.oracle.com/communications/ordermanagement/model";

declare variable $inputDoc as document-node() external;

let $sFileName              := "ServiceActions_Broadband_CFSBinding.xml"
let $sFic                   := "ServiceActions_Broadband_CFS"
let $eMap                   := fn:doc(sompsbinding:constructBindingUrl($sFic, $sFileName))
let $sConceptualModelUri    := $eMap/attributes/@conceptualModelURI
let $eExtAttributes         := sompsbinding:getOrderLineAttributes($sFic, .)
return
(
    for $param in $eExtAttributes
    return
    (
        sompsbinding:getAttributeEntry($sConceptualModelUri, $eMap, $param)
    )
)