module namespace soaplib = "http://www.training.com/comms/ordermanagement/common/soap/library";

(: This class represents helper functions for creating SOAP security headers :)

declare namespace wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

declare variable $soaplib:soapPrefix := "soapenv:";
declare variable $soaplib:soapNamespace := "http://schemas.xmlsoap.org/soap/envelope/";

(: String Constants :)
declare variable $soaplib:Body := "Body";
declare variable $soaplib:Header := "Header";

(: Creates the soapenv:Header element using credentials that were created using the specified map/key :)
declare function soaplib:createHeaderForCustomCredential(
            $sUsername as xs:string,
            $sPassword as xs:string) as element()* { 
    
    (: <soapenv:Header> :)    
    let $sElementname := fn:QName($soaplib:soapNamespace, concat($soaplib:soapPrefix, $soaplib:Header))
    return
        element {$sElementname } {
            <wsse:Security>
                <wsse:UsernameToken>
                    <wsse:Username>{$sUsername}</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">
                    {
                        $sPassword
                    }
                    </wsse:Password>
                </wsse:UsernameToken>
           </wsse:Security>
        }
};