import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicAddressRange;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.resource.MessageResource;
import oracle.communications.inventory.api.place.AddressRangeManager;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.inventory.extensibility.rules.*;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.impl.EntityField;

import org.apache.commons.lang.StringUtils;


Object param1=ruleParameters[0];

if(param1 instanceof List<GeographicAddress>)
{
for(GeographicAddress address:param1)
{
log.debug( "",  "Starting Find Address Range: " + address.getName() );
// execute for address
GeographicAddressRange range = queryAddressRange(address, log);
extensionPointRuleContext.setReturnValue(range);

}
}


def GeographicAddressRange queryAddressRange(GeographicAddress address, Log log) {
	GeographicAddressRange returnAddress = null;
	Finder finder = null;
	StringBuilder filterStr = new StringBuilder("");
	List<String> parms = new ArrayList<String>();
	List<Object> parmValues = new ArrayList<Object>();
	final String region = MessageResource.getMessage("addressRange.region");
	String regionValue = null;
	final String road = MessageResource.getMessage("addressRange.road");
	String roadValue = null;
	final String town = MessageResource.getMessage("addressRange.town");
	String townValue = null;

	//determine address format city or rural based on incoming object and get appropriate characteristic values
	try {
		ArrayList pc = new ArrayList(address.getCharacteristics());
		if (pc != null) {
			for(int i=0;i < pc.size();i++) {
				String value = ((PlaceCharacteristic)pc.get(i)).getValue();
				String name = ((PlaceCharacteristic)pc.get(i)).getName();
				if (name.equals(region)) {
					if (StringUtils.isBlank(value)) {
						log.error("place.fieldIsNull", name);
						return null;
					}
					regionValue = value;
				}
				else if (name.equals(road)) {
					if (StringUtils.isBlank(value)) {
						log.error("place.fieldIsNull", name);
						return null;
					}
					roadValue = value;
				}
				else if (name.equals(town)) {
					if (StringUtils.isBlank(value)) {
						log.error("place.fieldIsNull", name);
						return null;
					}
					townValue = value;
				}
			}
		}
		finder = PersistenceHelper.makeFinder();
		finder.setResultClass(GeographicAddressRange.class);

		// Add eager fetch classes.
		List<EntityField> eagerFetchEntityFields = new ArrayList<EntityField>();
		eagerFetchEntityFields.add(GeographicAddressRange._characteristics);
		eagerFetchEntityFields.add(GeographicAddressRange._specification);
		//finder.setEagerFetchFields(eagerFetchEntityFields);

		// Add parameters
		parms.add("region");
		parmValues.add(region);
		parms.add("regionValue");
		parmValues.add(regionValue);

		//set up filter string, region is common to both formats
		filterStr.append(" characteristics.contains(vCharacteristic1) ");
		filterStr.append(" && (vCharacteristic1.name == region && vCharacteristic1.value == regionValue)");
		Class[] classArray = [
			PlaceCharacteristic.class,
			PlaceCharacteristic.class
		];
		String[] stringArray = [
			"vCharacteristic1",
			"vCharacteristic2"
		];
		finder.declareVariables(classArray,stringArray);

		//if roadValue is populated, then we are searching for city formatted ranges
		if (roadValue != null) {
			filterStr.append(" && characteristics.contains(vCharacteristic2) ");
			filterStr.append(" && (vCharacteristic2.name == road && vCharacteristic2.value == roadValue)");
			parms.add("road");
			parmValues.add(road);
			parms.add("roadValue");
			parmValues.add(roadValue);
		}

		//if townValue is formatted, then we are searching for rural formatted ranges
		else if (townValue != null) {
			filterStr.append(" && characteristics.contains(vCharacteristic2) ");
			filterStr.append(" && (vCharacteristic2.name == town && vCharacteristic2.value == townValue)");
			parms.add("town");
			parmValues.add(town);
			parms.add("townValue");
			parmValues.add(townValue);
		}
		finder.setJPQLFilter(filterStr.toString());
		finder.setParameters(parms.toArray(new String[parms.size()]), parmValues.toArray(new Object[parmValues.size()]));
		Collection ranges = finder.findMatches();
		AddressRangeManager rangeManager = PersistenceHelper.makeAddressRangeManager();

		//create a characteristic map with just the populated characteristics
		Map<String, String> addrMap = new HashMap<String, String>();
		List resultList = new ArrayList();
		GroovyRulesExecutor groovyRulesExecutor = PersistenceHelper.makeGroovyRulesExecutor();
		resultList = groovyRulesExecutor.execute("CREATE_ADDRESS_CHARACTERISTIC_MAP", Map.class, address);
		addrMap = (HashMap) resultList.get(0);
		//find the range this address belongs to
		returnAddress = rangeManager.processRanges(ranges, addrMap);
	}
	catch(Exception e) {
		log.error("", e, address.getName());
	}
	finally {
		if (finder != null)
			finder.close();
	}
	return returnAddress;
}


