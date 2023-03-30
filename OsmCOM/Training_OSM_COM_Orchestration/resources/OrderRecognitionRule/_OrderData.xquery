(:  Identify contents of the incoming order :)

(: Declare OSM name space :)
declare namespace osm="http://xmlns.oracle.com/communications/ordermanagement/model";

(: Declare incoming order name space:)
declare namespace fulfillord    =   "http://www.training.com/inputMessage";
declare namespace corecom       =   "http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2";

(: Returns all contents of incoming order. :)
(: TO DO: Modify the XQuery to only select subset of incoming order data.:)
(: This XQuery selects all the contents of the incoming order. :)
let $eOrderData      := fn:root(.)//fulfillord:DataArea
let $sOrderNumber    := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:Identification/corecom:BusinessComponentID/text()
let $sVersion        := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:Identification/corecom:Revision/corecom:Number/text()
let $sTypeCode       := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/fulfillord:TypeCode/text()
let $sCustomerName   := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountName/text()
let $sFirstName      := fn:substring-before($sCustomerName, ',')
let $sLastName       := fn:substring-after($sCustomerName, ',')
let $sAccountType    := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountTypeCode/text()
let $sAccountID      := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountIdentification
                        /corecom:BusinessComponentID[@schemeID='CUSTOMERPARTY_ACCOUNTID']/text()
let $sContactID      := $eOrderData/fulfillord:ProcessSalesOrderFulfillment/corecom:CustomerPartyReference/corecom:CustomerPartyAccountContactIdentification
                        /corecom:BusinessComponentID[@schemeID='CUSTOMERPARTY_CONTACTID']/text()
return
    <_root>
        <Order>
            <OrderNumber>{$sOrderNumber}</OrderNumber>
            <Version>{$sVersion}</Version>
            <TypeCode>{$sTypeCode}</TypeCode>
            <CustomerPartyReference>
                <FirstName>{$sFirstName}</FirstName>
                <LastName>{$sLastName}</LastName>
                <AccountType>{$sAccountType}</AccountType>
                <AccountID>{$sAccountID}</AccountID>
                <ContactID>{$sContactID}</ContactID>
            </CustomerPartyReference>
        </Order>
    </_root>
