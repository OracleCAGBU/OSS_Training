(: This file contains basic constructs required. Please verify and complete the required parts. :)
(: Declare OSM name space :)

import module namespace compsbinding = "http://www.training.com/compsbinding" at "http://www.training.com/binding_module/ParameterBindingModule.xquery";

declare namespace osm                = "http://xmlns.oracle.com/communications/ordermanagement/model";

declare variable $inputDoc as document-node() external;

let $sFileName          := "Broadband_Bandwidth_PSBinding.xml"
let $sFic               := "Broadband_Bandwidth_PS"
let $eExtAttributes     := compsbinding:getOrderLineAttributes($sFic, .)
let $eMap               := fn:doc(compsbinding:constructBindingUrl($sFic, $sFileName))
return
(
    for $param in $eExtAttributes
    return
    (
        compsbinding:getAttributeEntry($eMap, $param, $inputDoc)
    )
)