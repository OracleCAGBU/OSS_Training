import module namespace soaplib                     = "http://www.training.com/comms/ordermanagement/common/soap/library" at "http://www.training.com/resources/lib/SoapLibrary.xqy";
import module namespace uimlib                      = "http://www.training.com/comms/ordermanagement/common/uim/library" at "http://www.training.com/resources/lib/UIMLibrary.xqy";
import module namespace techebm                     = "http://xmlns.oracle.com/comms/ordermanagement/oss/ebm/technical/library" at "http://www.training.com/resources/lib/TechnicalEBMLibrary.xqy";
import module namespace systeminteractionmodule     = "http://www.training.com/comms/ordermanagement/common/systeminteractionmodule" at "http://www.training.com/resources/lib/SystemInteractionModule.xqy";

declare namespace context                           = "java:com.mslv.oms.automation.TaskContext";
declare namespace outboundMessage                   = "java:javax.jms.TextMessage";
declare namespace log                               = "java:org.apache.commons.logging.Log";
declare namespace automator                         = "java:oracle.communications.ordermanagement.automation.plugin.ScriptSenderContextInvocation";
declare namespace provord                           = "http://www.somtraining.com/inputMessage";
declare namespace corecom                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";
declare namespace fulfillord                        = "http://www.training.com/inputMessage";
declare namespace saxon                             = "http://saxon.sf.net/";
declare namespace xsl                               = "http://www.w3.org/1999/XSL/Transform";
declare namespace oms                               = "urn:com:metasolv:oms:xmlapi:1";
declare namespace toibns                            = "http://xmlns.oracle.com/communications/studio/ordermanagement/transformation";
declare namespace cttransns                         = "COM_SalesOrderFulfillment";
declare namespace techord                           = "http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1";
declare namespace UUID                              = "java:java.util.UUID";

declare option saxon:output "method=xml";
declare option saxon:output "saxon:indent-spaces=4";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

declare variable $eTaskData             := fn:root(.)/oms:GetOrder.Response;
declare variable $sTaskName             := context:getTaskMnemonic($context);
declare variable $sOrderId              := $eTaskData/oms:OrderID/text();
declare variable $sVersion              := $eTaskData/oms:Version/text();
declare variable $sUsername             := 'oms-automation';
declare variable $sPassword             := 'admin123';
declare variable $sUimUsername          := 'uimadmin';
declare variable $sUimPassword          := 'oracle123';
declare variable $wsNamespace           := "http://xmlns.oracle.com/communications/ordermanagement";
declare variable $wsPrefix              := "ws:";
declare variable $createOrder           := "CreateOrder";
declare variable $eboNamespace          := "http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1";
declare variable $eboPrefix             := "ebo:";
declare variable $currentORRVersion     := '%{CARTRIDGE_VERSION}';
declare variable $TechnicalOrderEBM     := "ProcessTechnicalOrderEBM";
declare variable $ebmName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1}ProcessTechnicalOrderEBM";
declare variable $eboName               := "{http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1}TechnicalOrderEBO";
declare variable $osmURI                := "/osm/wsapi";
declare variable $mineContextType       := "text/xml; charset=UTF-8";

(: Generates a unique ID :)
declare function local:generateUniqueId() as xs:string
{
    string(UUID:randomUUID())
};

(: Build up the soap message containing the delivery order request :)
declare function local:createSoapMessage(
    $eTaskData as element()*) as element()*
{ 
    <soapenv:Envelope 
          xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
          xmlns:ws="http://xmlns.oracle.com/communications/ordermanagement"
          xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
          xmlns:ebo="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1"
          xmlns:corecom="http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2"
          xmlns:techord="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/TechnicalOrder/V1">
    {
        soaplib:createHeaderForCustomCredential($sUimUsername, $sUimPassword),
        local:addBody( $eTaskData ) 
    }
    </soapenv:Envelope>            
};

(: Create the Body element. :)   
declare function local:addBody(
    $eTaskData as element()*) as element()*
{ 
    (: <soapenv:Body> :)
    let $elementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Body))
    where (exists($eTaskData))
    return
        element {$elementname } {
            local:addCreateOrder($eTaskData)
        }
};

(: Create the CreateOrder element :)   
declare function local:addCreateOrder(
    $eTaskData as element()*) as element()*
{ 
   (: <ws:CreateOrder> :)
    let $elementname := fn:QName($wsNamespace, concat($wsPrefix, $createOrder))
    where (exists($eTaskData))
    return
        element {$elementname } {     
            local:addProcessTechnicalOrderEBM($eTaskData)
        }
};

(: Create the ProcessTechnicalOrderEBM element :)
declare function local:addProcessTechnicalOrderEBM(
    $eTaskData as element()) as element()?
{ 
    let $tomEbmId := systeminteractionmodule:generateUniqueId()
    let $tomOrderId := fn:normalize-space($eTaskData/oms:Reference/text()) 
    (: <ebo:ProcessTechnicalOrderEBM> :)    
    let $elementname := fn:QName($eboNamespace, concat($eboPrefix, $TechnicalOrderEBM))

    return
        element{$elementname}{
            (: Add EBMHeader :)
            local:addEBMHeader($eTaskData, $tomEbmId),
            (: Add DataArea :)
            local:addDataArea($eTaskData, $tomOrderId)
        }
};

(: This creates the EBM Header element :)
declare function local:addEBMHeader(
    $orderData as element()*,
    $tomEbmId as xs:string) as element()*
{ 
    <corecom:EBMHeader>
        <corecom:EBMID>{$tomEbmId}</corecom:EBMID> 
        <corecom:EBMName>{$ebmName}</corecom:EBMName> 
        <corecom:EBOName>{$eboName}</corecom:EBOName> 
        <corecom:CreationDateTime>{current-dateTime()}</corecom:CreationDateTime> 
        <corecom:VerbCode>process</corecom:VerbCode> 
        <corecom:MessageProcessingInstruction>
        <corecom:EnvironmentCode>PRODUCTION</corecom:EnvironmentCode> 
        </corecom:MessageProcessingInstruction>
        <corecom:Sender>
            <corecom:ID>OSMSOM_01</corecom:ID> 
            <corecom:Description>OSM Service Order Management</corecom:Description> 
            <corecom:IPAddress>ipaddress.osm.com</corecom:IPAddress> 
            <corecom:SenderMessageID></corecom:SenderMessageID> 
            <corecom:TransactionCode>TechnicalOrder</corecom:TransactionCode> 
            <corecom:Application>
            <corecom:ID>OSMCFS_01</corecom:ID> 
            <corecom:Version>{$currentORRVersion}</corecom:Version> 
            </corecom:Application>
            <corecom:ContactName>OSM Contact</corecom:ContactName> 
            <corecom:ContactEmail>contact@osm.com</corecom:ContactEmail> 
            <corecom:ContactPhoneNumber>1234567890</corecom:ContactPhoneNumber> 
        </corecom:Sender>
    </corecom:EBMHeader>
};

(: This creates the DataArea element  :)
declare function local:addDataArea(
    $orderData as element()*,
    $tomOrderId as xs:string) as element()*
{                               
    let $eorderItems := $eTaskData/oms:_root/oms:ControlData/oms:Functions/*[local-name()=$uimlib:DeliverOrderFunction]/oms:orderItem
    
    return  
    (
        <techord:DataArea>
            <corecom:Process/> 
            <techord:ProcessTechnicalOrder>
                <techord:Subject>Technical</techord:Subject> 
                <corecom:Identification>
                <corecom:BusinessComponentID>{$tomOrderId}</corecom:BusinessComponentID> 
                <corecom:ID></corecom:ID>  
                <corecom:Revision>
                    <corecom:Number>1</corecom:Number> 
                </corecom:Revision>
                </corecom:Identification>
                <corecom:RequestedDeliveryDateTime/>
                <corecom:FulfillmentPriorityCode></corecom:FulfillmentPriorityCode>         
                <corecom:FulfillmentModeCode>DELIVER</corecom:FulfillmentModeCode> 
                <corecom:OrderType></corecom:OrderType>
                <corecom:ServiceType></corecom:ServiceType>
                <corecom:TypeCode>TECHNICAL ORDER</corecom:TypeCode>
                <corecom:Command></corecom:Command>                                                                                    
                <corecom:PartialFulfillmentAllowedIndicator>Default</corecom:PartialFulfillmentAllowedIndicator> 
                <corecom:PartialFulfillmentAllowedThreshold>2</corecom:PartialFulfillmentAllowedThreshold> 
                <corecom:Status>
                    <corecom:Code>IN PROGRESS</corecom:Code>
                    <corecom:ReasonCode/>
                    <corecom:Description/>
                </corecom:Status>
                <techord:Timeout>1</techord:Timeout> 
                <techord:ProcessingNumber>12354949344</techord:ProcessingNumber> 
                <techord:ProcessingTypeCode>HETEROGENEOUS</techord:ProcessingTypeCode> 
                <techord:ProcessingSequenceNumber/> 
                <techord:ProcessingQuantity/> 
                {   
                    (: Loop through the order items :) 
                for $oi in $eorderItems
    
                     (: Each technical action creates a TOL. :)
                        for $ta in $oi/oms:orderItemRef/oms:TechnicalAction/*:TechnicalAction[*:ActionId/text() = *:ParentId/text()]
                        return local:createTOL(local:generateUniqueId(),local:generateUniqueId(),$ta) 
                }
            </techord:ProcessTechnicalOrder>    
        </techord:DataArea>
    )
};

(: Returns a technical order line :)
declare function local:createTOL(
    $parentLineId as xs:string*,
    $lineId as xs:string*,
    $ta as element()*) as element()*
{
    let $tol := fn:QName($techebm:techordNamespace, concat($techebm:techordPrefix, $techebm:TOL))
    
    let $linenumber         := local:getTAActionId($ta)
    let $subject            := local:getTASubject($ta)/text() 
    let $specialized        := local:getTASpecializedActionCode($ta)
    let $action             := local:getTAActionCode($ta)
    let $ffsystem           := local:getTAFulfillmentSystem($ta)
    let $parameters         := $ta/*:TechnicalParameters
    let $target             := local:getTATarget($ta)/text() 
    let $targetspec         := local:getTATargetSpec($ta)/text() 
    

    return
       element {$tol} {
			(: This will generate a unique business component id :)
			techebm:createIdentification($lineId, $linenumber),
			techebm:createQualifiedElement($techebm:techordNamespace, $techebm:techordPrefix, $techebm:ActionCode, $action),
			(: This id is consistent across revisions. Because this is the DO xquery we use the line Id. :)
			techebm:createOriginalTolRef($lineId),
			if($parentLineId != $lineId)
			then (
    			techebm:createParentTol($parentLineId)
    	    )
    	    else(),
			techebm:createClassificationCode($subject),
			techebm:createQualifiedElementFromString($techebm:techordNamespace, $techebm:techordPrefix, $techebm:FulfillmentSystemType, $ffsystem),
			techebm:createQualifiedElementFromString($techebm:techordNamespace, $techebm:techordPrefix, $techebm:SpecializedActionCode, $specialized),
			if (exists($target)) 
			then (techebm:createQualifiedElementFromString($techebm:techordNamespace, $techebm:techordPrefix, 'Target', $target)) 
			else(),
			if (exists($targetspec)) 
			then (techebm:createQualifiedElementFromString($techebm:techordNamespace, $techebm:techordPrefix, 'TargetSpec', $targetspec)) 
			else(),
			<techord:Subject>
        			 {
        			     if(exists($subject)) then (techebm:createQualifiedElementFromString($techebm:techordNamespace, $techebm:techordPrefix,  'Spec', $subject))else ()
        			 }
			 </techord:Subject>,
			local:buildTechnicalParameters($parameters/*)
       }            
};

(: This is the main function called. It will construct an xml fragment for the supplied list of parameters where some of them may be complex in nature. Complex parameters are 
identified by the __ between hierarchical elements :)
 declare function local:buildTechnicalParameters($parameterList) as element()* 
{   
    (: complex parameters are built differently so first need to separate out the complex from the simple params :)
    let $complexList    := local:getComplexParameters($parameterList)   
    let $simpleList     := local:removeComplexParameters($parameterList)
    let $xmlResult      := local:buildSimple($simpleList)
    return
        if(exists($complexList))
        then
            local:addComplex($xmlResult, $complexList)
        else $xmlResult
};

(: Get any complex paramters from the list. these are identified by __ between hierarchical element names :)
 declare function local:getComplexParameters( $params as node()*) as element()* 
{ 
    for $item in $params
    let $name := local-name($item)
    return
        if(fn:contains($name, "__"))
        then (
            $item
        )
        else()
};

(: Remove any complex paramters from the list. these are identified by __ between hierarchical element names :)
 declare function local:removeComplexParameters( $params as node()*) as element()* 
{ 
    for $item in $params
    let $name := local-name($item)
    return
        if(fn:contains($name, "__"))
        then (
        )
        else(
            $item
        )
};

(: This function builds a list of simple parameters. Currently it looks for the new subject parameters and strips them out of the list. these will be sent as first class parameters on 
the technical order so we don't want to include them here :)
 declare function local:buildSimple( $params as node()*) as element()* 
{   
    <techord:TechnicalParameters>
    {
        for $p in $params
        where ((local-name($p) != 'foreignSpec') and (local-name($p) != 'foreignContext') and (local-name($p) != 'foreignKey'))
        return
            if( count($p/*)>0 ) then
            (
                $p
            )
            else(
        
                local:createLeaf(local-name($p), $p/text())
            )
    }
    </techord:TechnicalParameters>
};

(: Adds a set of complex parameters to the xmlResult fragment :)
 declare function local:addComplex($xmlResult, $complex) as element()* 
{ 
    if(exists($complex))
    then
    (
        let $workingParam := fn:subsequence($complex, 1, 1)
        let $newParamList := remove($complex, 1)
        return
            if(exists($newParamList))
            then (
                let $newXmlResult := local:integrateComplex($xmlResult, $workingParam)
                return
                local:addComplex($newXmlResult, $newParamList)
            )
            else (
                local:integrateComplex($xmlResult, $workingParam)
            )
    )
    else
    (
        $xmlResult
    )
        
};

(: Creates an element with the specified name and value :)
declare function local:createLeaf(
    $tmpname as xs:string, 
    $value as xs:string) as element()* {

    let $elementname := $tmpname
    where (exists($value))
    return
    element {$elementname}  {
        data($value)
    }
};

(: Adds a complex parameter to the TechnicalParameters root. need to append the root node name to the xpath so it can be integrated properly :)
 declare function local:integrateComplex($xmlResult, $complex) as element()* 
{ 
    let $name := local-name($complex)
    let $xpath := fn:concat('TechnicalParameters__', $name)
    let $value := $complex/text()
    return       
        local:integrateXPath($xmlResult, fn:tokenize($xpath, "__"), $value)
};

(: Converts the specified $xpath into xml and incorporates it into the $xmlResult. this only works if there is a shared ancestor.
$xmlResult - the existing xml fragment that is being enhanced
$xpath - represents the new structure that should be created and inserted into the xmlResult. This should already be tokenized when passed in
$value - the value for the leaf node  :)
declare function local:integrateXPath(
    $xmlResult as element(),
    $xpath as item()*,
    $value as xs:string) as item()* 
{
    let $resultSet := local:iterate-establishCommonAncestor($xmlResult, (), $xpath)
    let $commonAncestor := fn:subsequence($resultSet, 1, 1)
    let $newXPath := remove($resultSet, 1)     

    return
        if(fn:count($newXPath)=1)
        then (
            (: Create a leaf and add it to the ancestor node :)
            let $updatedAncestor := local:nodeCopy-createLeaf($commonAncestor, fn:subsequence($newXPath,1,1), $value)
            
            return
                (: Is the commonAncestor also the root node? If so - we can throw away the original xml and just return the updatedAncestor :)
                if(node-name($updatedAncestor)=node-name($xmlResult))
                then (
                    $updatedAncestor
                )
                else (
                    (: return an updated xml node :)
                    let $tempResult := local:nodeCopy-removeChild($xmlResult, $commonAncestor)
                    return local:nodeCopy-addChild($tempResult, $updatedAncestor)
                )
        )
        else (       
            (: building new xml fragment from the remaining xpath :)
            let $newFrag := local:buildNewXMLFromTokens($newXPath, $value)
            (: Add the new fragment to the common ancestor :)
            let $updatedAncestor := local:nodeCopy-addChild($commonAncestor, $newFrag)
            return
                if(node-name($updatedAncestor)=node-name($xmlResult))
                then (
                    $updatedAncestor
                )
                else (
                    let $tempResult := local:nodeCopy-removeChild($xmlResult, $commonAncestor)
                    return local:nodeCopy-addChild($tempResult, $updatedAncestor)
                )                
        )
};

(: Iterative function that checks the token in an xpath and looks for a matching descendant in the $xmlResult :)
declare function local:iterate-establishCommonAncestor(
    $xmlResult as element(),
    $commonAncestor as element()*,
    $xpath as item()*) as item()* 
{
    (: This is the first segment of the xpath. :)
    let $token := fn:subsequence($xpath, 1, 1)  
    
    (: Reset context of the xpath :)
    let $newpath := remove($xpath, 1)   
   
    (: Does this segment already exist in the xml? :)
    let $existingToken := local:getDescendantWithName($xmlResult, $token)
           
    return
        if(exists($existingToken)) (: We just want to skip that segment of the xpath and go to the next :)
        then (
            (: Then continue looking down xpath for branch where we need to start creating nodes :)
            local:iterate-establishCommonAncestor($xmlResult, $existingToken, $newpath) 
        )
        else(
            $commonAncestor, $xpath
        )

};

(: Copies the $parent node's children (does this only one level deep) and adds to this, a new child with specified $nodename and $value :)
declare function local:nodeCopy-createLeaf(
    $parent as element()*,
    $nodename as item(),
    $value as xs:string) as element()*   
{
    let $name := fn:normalize-space($nodename)
    let $val := fn:normalize-space($value)
    return
    element {node-name($parent)} {
        $parent/node(),
        element {$name}{$val}
    }
};

(: Copies the $parent node with children, but removes the child specified by $child:)
declare function local:nodeCopy-removeChild(
    $parent as element()*, 
    $child as element()*) as element()* {

    element {node-name($parent)}  {
        for $node in $parent/node()
        return 
            if(node-name($node)=node-name($child))
            then(
            )
            else
            ($node)
    }
};

(: Copies the $parent node with children, and adds a new child specified by $child :)
declare function local:nodeCopy-addChild(
    $parent as element()*,
    $child as element()*) as element()*   
{
    element {node-name($parent)} {
        $parent/node(),
        $child
    }
};

(: This is different from above function because it doesn't try to tokenize the xpath. it assumes this is already tokenized :)
declare function local:buildNewXMLFromTokens(
    $xpath as item()*,
    $value as xs:string) as item()* 
{
    (: Reverse the sequence so that node creation can go from bottom up :)
    let $seq := fn:reverse($xpath) 
    let $leafname := fn:subsequence($seq, 1, 1)  

    (: Construct the initial leaf node with the value :)
    let $leaf := local:createLeaf($leafname, $value)
    let $remainder := remove($seq, 1)
    
    return 
        if(fn:count($remainder) > 0) (: Iterate through the rest of the xpath to create the parent nodes :)
        then (
            local:iterate-createAncestors($remainder, $leaf)
        )
        else ( (: The xpath might be only one item long :)
            $leaf
        )

};

(: If there is a descendant element with the specified name it, return it. :)
declare function local:getDescendantWithName(
    $xml as element(),
    $name as xs:string*) as element()*
{
    (: Check for matching child nodes :)
    let $node := $xml//*[local-name()=$name]
    return
        if(exists($node))
        then (
            $node
        )
        else (
            (: Check to see if the root itself has this name :)
            if(local-name($xml)=$name)
            then (
                $xml
            )
            else (
            ()
            )
        )
};

(: Iterates through a sequence of names and creates a parent node for each one recursively. The child node is appended to the new parent :)
declare function local:iterate-createAncestors(
    $seq as item()*,
    $child as element()*) as element()*
{
    let $name := fn:subsequence($seq, 1, 1)  
    let $parent := local:createParentAppendChild($name, $child)
    let $newseq := remove($seq, 1)
   
    return 
        if(exists($newseq)) then
        (   
            local:iterate-createAncestors($newseq, $parent)
         )    
        else
        (
            $parent
        )
};

(: Creates a parent element and appends child to it :)
declare function local:createParentAppendChild(
    $parent as item()*, 
    $child as element()) as element()* {
        
    element {$parent}  {
        $child
    }
};

(: A Check to determine if the complete message should be sent :)
declare function local:shouldCreateOrder(
    $eTaskData as element()) as xs:boolean  
{
    let $technicalActions := $eTaskData/oms:_root/oms:ControlData/oms:Functions/oms:DeliverOrderFunction/oms:orderItem/oms:orderItemRef/oms:TechnicalAction
    return
         (
            if (fn:exists($technicalActions))
            then fn:true()
            else fn:false()
         )
};

(: These are new functions to get the data from the Technical Action but these do not use the namespace as they should no longer have one. They are in an xml type
and when created it was done so without any namespaces :)

(: Returns the element that represents the actionCode. :)
declare function local:getTAActionCode(
            $ta as element()*) as element()* {

       let $code := $ta/*:ActionCode
       where (exists($code))
          return $code
      
};

(: Returns the element that represents the SpecializedActionCode. :)
declare function local:getTASpecializedActionCode(
            $ta as element()*) as element()* {

       let $code := $ta/*:SpecializedActionCode
       where (exists($code))
          return $code
      
};

(: Returns the element that represents the FulfillmentSystemType. :)
declare function local:getTAFulfillmentSystem(
            $ta as element()*) as element()* {

       let $type := $ta/*:FulfillmentSystemType
       where (exists($type))
          return $type
      
};

(: Returns the element that represents the Action Id. :)
declare function local:getTAActionId(
            $ta as element()*) as element()* {

       let $id := $ta/*:ActionId
       where (exists($id))
          return $id
      
};

(: Returns the element that represents the ParentAction Id. :)
declare function local:getTAParentId(
            $ta as element()*) as element()* {

       let $id := $ta/*:ParentId
       where (exists($id))
          return $id
      
};

(: Returns the element that represents the Service Id. :)
declare function local:getTAServiceId(
            $ta as element()*) as element()* {

       let $id := $ta/*:ServiceId
       where (exists($id))
          return $id
      
};

(: Returns the element that represents the Service Version. :)
declare function local:getTAServiceVersion(
            $ta as element()*) as element()* {

       let $id := $ta/*:ServiceVersion
       where (exists($id))
          return $id
      
};

(: Returns the element that represents the Subject. :)
declare function local:getTASubject(
            $ta as element()*) as element()* {

       let $subject := $ta/*:Subject
       where (exists($subject))
          return $subject
      
};

(: Returns the element that represents the Subject. :)
declare function local:getTATarget(
            $ta as element()*) as element()* {

       let $target := $ta/*:Target
       where (exists($target))
          return $target
      
};

(: Returns the element that represents the Subject. :)
declare function local:getTATargetSpec(
            $ta as element()*) as element()* {

       let $tSpec := $ta/*:TargetSpec
       where (exists($tSpec))
          return $tSpec
      
};
(: Returns the element that represents the Subject. :)
declare function local:getTAServiceAddress(
            $ta as element()*) as element()* {

       let $address := $ta/*:ServiceAddress
       return $address
      
};


(: END NEW FUNCTIONS :)


(:  ***************************
    Checks for the presence of technical actions. If there are no actions then this order doesn't need delivery.
    ***************************
:)
let $eTomRequest                          := local:createSoapMessage( $eTaskData )
let $sTaskData                            := saxon:serialize($eTaskData, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)
let $sTomRequest                          := saxon:serialize($eTomRequest, <xsl:output method='xml' omit-xml-declaration='yes' indent='yes' saxon:indent-spaces='4'/>)

return
    (
        log:info($log, fn:concat('TOM Order Request : ', $sTomRequest)),
        outboundMessage:setStringProperty( $outboundMessage, "URI", $osmURI),
        outboundMessage:setStringProperty( $outboundMessage, "_wls_mimehdrContent_Type", $mineContextType),
        $eTomRequest
    )