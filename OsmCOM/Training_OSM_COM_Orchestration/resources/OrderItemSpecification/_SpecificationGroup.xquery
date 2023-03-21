(:  Fetch contents of LineId from incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord="http://www.training.com/inputMessage";
declare namespace corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(:  For example if incoming order contains line id in the <classificationCode> tag :)
(:  XQuery to fetch line id would look like  :)
(:     fn:normalize-space(fulfillord: classificationCode /text())     :)

(: TO DO: Write XQuery to identify Product Specification :)

let $orderItem := .
let $specificationGroup := $orderItem/*:SalesOrderLineSpecificationGroup/corecom:SpecificationGroup[corecom:Name/text()="ExtensibleAttributes"]/corecom:Specification
let $specificationGroupAttributes := <SpecificationGroups>
                                             {$specificationGroup}
                                     </SpecificationGroups>
return
    $specificationGroupAttributes