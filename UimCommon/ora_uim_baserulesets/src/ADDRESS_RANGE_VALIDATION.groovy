/**
 * This ruleset validates the content of an input GeographicAddressRange object. If a validation error is encountered, the ruleset logs
 an error. If a validation error is not encountered, processing continues. You can customize the base ruleset to perform custom validations 
 as required by your business needs, and configure the ruleset to run when an address is associated with an address  range in UIM.
 */
import oracle.communications.inventory.api.entity.GeographicAddressRange;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.resource.MessageResource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

Object param1=ruleParameters[0];

if(param1 instanceof List<GeographicAddressRange>)
{
for(GeographicAddressRange range:param1)
{	
log.debug( "", "entering AddressRangeValidationRules: " + range.getName() );
// validate address
boolean isValid = validateAddressRange(range, log);
if (isValid) {
	log.info("place.addressrange.validationSuccessful");
}
log.info("place.addressrange.validationSuccessful");
}
}
def boolean validateMixedRange(String from, String to, Log log) {
	String fromAlpha = null;
	String fromNumeric = null;
	String toAlpha = null;
	String toNumeric = null;
	boolean alphaIsRanged = false;
	boolean alphaSameLength = false;
	boolean numbersSame = false;
	for (int i=0; i< from.length(); i++) {
		String fromTemp = StringUtils.substring(from, i,i+1);
		if (StringUtils.isAlpha(fromTemp)) {
			if (StringUtils.isNotBlank(fromAlpha)) {
				fromAlpha += fromTemp;
			}
			else {
				fromAlpha = fromTemp;
			}
		}
		else if(StringUtils.isNumeric(fromTemp)){
			if (StringUtils.isNotBlank(fromNumeric)) {
				fromNumeric += fromTemp;
			}
			else {
				fromNumeric = fromTemp;
			}
		}
		else {
			log.error("place.addressRange.invalidCharacter");
			return false;
		}
	}
	for (int i=0; i< to.length(); i++) {
		String toTemp = StringUtils.substring(to, i,i+1);
		if (StringUtils.isAlpha(toTemp)) {
			if (StringUtils.isNotBlank(toAlpha)) {
				toAlpha += toTemp;
			}
			else {
				toAlpha = toTemp;
			}
		}
		else if (StringUtils.isNumeric(toTemp)){
			if (StringUtils.isNotBlank(toNumeric)) {
				toNumeric += toTemp;
			}
			else {
				toNumeric = toTemp;
			}
		}
		else {
			log.error("place.addressRange.invalidCharacter");
			return false;
		}
	}
	if (fromNumeric.equals(toNumeric)) {
		numbersSame = true;
	}
	if (fromAlpha == null || toAlpha == null) {
		log.error("place.addressRange.alphaNotEqual");
		return false;
	}
	if (fromAlpha.equals(toAlpha)) {
		alphaSameLength = true;
	}
	else if (fromAlpha.length() == toAlpha.length()) {
		alphaSameLength = true;
		alphaIsRanged = true;
	}
	if (alphaSameLength) {
		if (alphaIsRanged && !numbersSame) {
			log.error("place.addressRange.bothNotEqual");
			return false;
		}
	}
	else {
		log.error("place.addressRange.alphaNotEqual");
		return false;
	}
	return true;
}

def boolean validateAddressRange(GeographicAddressRange range, Log log) {
	String from = null;
	String to = null;
	String type = null;
	//refer to addressRange.properties file for localization of these words
	final String from_prefix = MessageResource.getMessage("addressRange.from_prefix")+"_";
	final String to_prefix = MessageResource.getMessage("addressRange.to_prefix")+"_";
	final String road = MessageResource.getMessage("addressRange.road");
	final String region = MessageResource.getMessage("addressRange.region");

	ArrayList pc = new ArrayList(range.getCharacteristics());
	if (pc != null) {
		for(int i=0;i < pc.size();i++) {
			String value = ((PlaceCharacteristic)pc.get(i)).getValue();
			String name = ((PlaceCharacteristic)pc.get(i)).getName();
			if (name.equals(road)|| name.equals(region)) {
				if (StringUtils.isBlank(value)) {
					log.error("place.fieldIsNull", name);
					return false;
				}
			}
			else if (name.startsWith(from_prefix)) {
				if (StringUtils.isNotBlank(value) && StringUtils.isBlank(from)) {
					from = value;
				}
				else {
					log.error("place.addressRange.tooManyRanges");
					return false;
				}
			}
			else if (name.startsWith(to_prefix)) {
				if (StringUtils.isNotBlank(value) && StringUtils.isBlank(to)) {
					to = value;
				}
				else {
					log.error("place.addressRange.tooManyRanges");
					return false;
				}
			}
		}
	}
	//analyze range values
	if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
		if (StringUtils.isAlpha(from) && StringUtils.isAlpha(to)) {
			//each must have same number of characters
			if (from.length() != to.length()) {
				log.error("place.addressRange.stringsNotEqual");
				return false;
			}
		}
		else if (StringUtils.isNumeric(from) && StringUtils.isNumeric(to)) {
			int intFrom = NumberUtils.toInt(from);
			int intTo = NumberUtils.toInt(to);
			if (intFrom > intTo) {
				log.error("place.addressRange.fromGreaterThanTo");
				return false;
			}
		}
		else {
			//must be mixed alphanumeric
			return ValidateMixedRange.validateMixedRange(from, to, log);
		}
	}
	else {
		log.error("place.addressRange.notFullyDefined");
		return false;
	}
	return true;
}



