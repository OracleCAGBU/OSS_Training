import java.util.regex.*;

import oracle.communications.inventory.api.*;
import oracle.communications.inventory.api.characteristic.impl.*;
import oracle.communications.inventory.api.entity.*;
import oracle.communications.inventory.api.entity.common.*;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.platform.persistence.PersistenceHelper;


ArrayList<Object> ruleParams = new ArrayList<Object>();
ruleParams=this.binding.getVariable("ruleParameters");
for(Object obj:ruleParams)
{
if(obj instanceof TelephoneNumber)
{
TelephoneNumber telephoneNumber=(TelephoneNumber)obj;

setTheGrade (log, telephoneNumber);
setGradeCreateExtension (log, extensionPointRuleContext);
}
}
def void setTheGrade(Log log, TelephoneNumber telephoneNumber) {
	log.debug ("", "Set the grade before Set Grade Create Extension");
	if (telephoneNumber != null &&
	telephoneNumber.getSpecification() != null) {
		if (getUnluckyPattern().matcher(getTelephoneNumber(telephoneNumber)).find()) {
			setGrade(telephoneNumber, "UNLUCKY", log);
		} else if (getGoldenPattern().matcher(getTelephoneNumber(telephoneNumber)).find()) {
			setGrade(telephoneNumber, "GOLDEN", log);
		} else if (getLuckyPattern().matcher(getTelephoneNumber(telephoneNumber)).find()
		&& !getExcludeFromLuckyNumberPattern().matcher(getTelephoneNumber(telephoneNumber)).find()) {
			setGrade(telephoneNumber, "LUCKY", log);
		} else {
			setGrade(telephoneNumber, "NORMAL", log);
		}
	}
}
def void setGradeCreateExtension(Log log, ExtensionPointRuleContext context) {
	log.debug ("", "Set Grade Create Extension");
	List tns = null;
	boolean refresh = false;
	String signature = context.getSignature();
	if( signature.matches(".*updateTelephoneNumbers.*"))
	{
		Object[] args = context.getArguments();
		if(null != args)
		{
			// TN List Is First Parameter On Update EP
			tns = (List) args[0];
			refresh = true;
		}
	}
	else if( signature.matches(".*createTelephoneNumbers.*"))
	{
		// TN List Is Return Value On After Create EP
		tns = (List) context.getReturnValue();
	}
	if (tns != null && !tns.isEmpty())
	{
		Iterator tnItr = tns.iterator();
		while (tnItr.hasNext())
		{
			TelephoneNumber telephoneNumberLocal = (TelephoneNumber) tnItr.next();
			if( refresh )
			{
				// Need To Refresh On An After Update EP
				telephoneNumberLocal = telephoneNumberLocal.refresh();
			}
			if (getUnluckyPattern().matcher(getTelephoneNumber(telephoneNumberLocal)).find())
			{
				setGrade(telephoneNumberLocal, "UNLUCKY", log);
			}
			else if (getGoldenPattern().matcher(getTelephoneNumber(telephoneNumberLocal)).find())
			{
				setGrade(telephoneNumberLocal, "GOLDEN", log);
			}
			else if (getLuckyPattern().matcher(getTelephoneNumber(telephoneNumberLocal)).find()
			&& !getExcludeFromLuckyNumberPattern().matcher(getTelephoneNumber(telephoneNumberLocal)).find())
			{
				setGrade(telephoneNumberLocal, "LUCKY", log);
			}
			else
			{
				setGrade(telephoneNumberLocal, "NORMAL", log);
			}
		}
	}
}


// this function strips any formatting off of the phone number
def String getTelephoneNumber(oracle.communications.inventory.api.entity.TelephoneNumber telephoneNumber) {
	String id = telephoneNumber.getId();
	if (id != null) {
		return telephoneNumber.getId().replaceAll("\\D", "");
	} else {
		return "";
	}
}
def Pattern getLuckyPattern() {
	// Lucky Numbers
	String AAA = '(\\d)\\1\\1$' ;
	String luckyNumberPatterns = AAA;
	// compile the pattern
	return Pattern.compile(luckyNumberPatterns);
}
def Pattern getExcludeFromLuckyNumberPattern() {
	// Exclude from lucky Numbers
	String excludeFromLuckyNumberPatterns = '1818$|2828$|3838$|6868$';
	// compile the pattern
	return Pattern.compile(excludeFromLuckyNumberPatterns);
}
def Pattern getUnluckyPattern() {
	// Unlucky Numbers
	String unluckyNumberPatterns = '4$';
	// compile the pattern
	return Pattern.compile(unluckyNumberPatterns);
}
def Pattern getGoldenPattern() {
	// Golden Numbers
	String numberPatterns = '158$|168$|518$|888$|1818$|1688$|1890$|1898$|2828$|3388$|3838$|6688$|6868$|7788$|1234$|5678$|7891$|0000$|1111$|3333$|6666$|7777$|9999$';
	String AAAA = '(\\d)\\1\\1\\1$';
	String AABBCC = '(\\d)\\2(\\d)\\3(\\d)\\4$';
	String ABABAB = '(\\d\\d)\\5\\5$';
	String ABABABAB = '(\\d\\d)\\6\\6\\6$';
	String ABCDE = '01234$|12345$|23456$|34567$|45678$|56789$';
	String EDCBA = '98765$|87654$|76543$|65432$|54321$|43210$';
	StringBuilder sb = new StringBuilder();
	sb.append(numberPatterns);
	sb.append("|");
	sb.append(AAAA);
	sb.append("|");
	sb.append(AABBCC);
	sb.append("|");
	sb.append(ABABAB);
	sb.append("|");
	sb.append(ABABABAB);
	sb.append("|");
	sb.append(ABCDE);
	sb.append("|");
	sb.append(ABCDE);
	sb.append("|");
	sb.append(EDCBA);
	String goldenNumberPatterns = sb.toString();
	// compile the pattern
	return Pattern.compile(goldenNumberPatterns);
}
def void setGrade(TelephoneNumber telephoneNumber, String grade, Log log) {
	try {
		String characteristicName = "Grade";
		CharValue charValue = CharacteristicHelper.getCharacteristic(telephoneNumber, characteristicName);
		// if the grade has been set on the tn, then the user manually entered the grade.
		// In this case we don't want to set it.
		if (charValue == null) {
			Specification spec = telephoneNumber.getSpecification();
			CharacteristicSpecification characteristicSpecification =
					PersistenceHelper.makeCharacteristicManager().getCharacteristicSpecification(spec, characteristicName);
			if (characteristicSpecification != null) {
				charValue = telephoneNumber.makeCharacteristicInstance();
				charValue.setCharacteristicSpecification(characteristicSpecification);
				charValue.setName(characteristicSpecification.getName());
				charValue.setValue(grade);
				HashSet charValues = (HashSet)telephoneNumber.getCharacteristics();
				if (charValues == null) {
					charValues = new HashSet();
				}
				charValues.add(charValue);
				telephoneNumber.setCharacteristics(charValues);
			} else {
				log.warn("", "The spec " + spec.getName() + " does not have a characteristic named "
						+ characteristicName + ". The telephone numbers were not graded.");
			}
		}
	} catch (Exception e) {
		log.error("", e, "An exception occured while processing rule.");
	}
}
