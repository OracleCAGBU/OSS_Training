import oracle.communications.inventory.api.entity.TelephoneNumberSpecification;
import oracle.communications.inventory.api.entity.TelephoneNumber;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;


ArrayList<Object> ruleParams = new ArrayList<Object>();
ruleParams=this.binding.getVariable("ruleParameters");
for(Object obj:ruleParams)
{
if(obj instanceof TelephoneNumber)
{
TelephoneNumberSpecification telephoneNumberSpecification=((TelephoneNumber)obj).getSpecification();

//-------------------------------------------------------------------------------------------------
// RULE
//-------------------------------------------------------------------------------------------------

log.debug ("", "Get TN Edit Mask");
String editMask = getEditMask(telephoneNumberSpecification);

extensionPointRuleContext.setReturnValue(editMask);


}
}

//-------------------------------------------------------------------------------------------------
// FUNCTIONS
//-------------------------------------------------------------------------------------------------

def String getEditMask(TelephoneNumberSpecification tnSpec) {
	// The character # is reserved and represents a required digit.
	// The default mask is eight required digits.
	String editMask = "##########";
	if ( tnSpec == null )
		return editMask;

	// Define the edit mask based on the spec name
	if(tnSpec.getName().equals("TNspec NPA-NXX"))
		editMask = "###-###-####";
	if(tnSpec.getName().equals("NANPA"))
		editMask = "+# (###) ###-####";

	return editMask;
}