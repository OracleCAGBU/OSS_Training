declare namespace saxon             = "http://saxon.sf.net/";
declare namespace xsl               = "http://www.w3.org/1999/XSL/Transform";
declare namespace javaSystem        = "java:java.lang.System";
declare namespace javaPrintStream   = "java:java.io.PrintStream";
declare namespace javaString        = "java:java.lang.String";
declare namespace osm               = "http://xmlns.oracle.com/communications/ordermanagement/model";
declare namespace prop              = "COM_SalesOrderFulfillment";

declare variable $sourceValue external;
declare variable $currentTargetValue external;

let $printStream                 := javaSystem:out()
let $sourceOrderItem             := .
let $transformedOrderItems       := otmfn:transformedOrderItems(.)
let $sourceOrderItemStr          := saxon:serialize($sourceOrderItem, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $transformedOrderItemsStr    := saxon:serialize($transformedOrderItems, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $serialNumber                := $sourceOrderItem//*:SerialNumber/text()


return
(
if($sourceValue='Modify' and fn:string-length($serialNumber)>0)
then 'ChangeCPE'
else $sourceValue
)