import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicAddressRange;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.place.AddressRangeManager;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.inventory.extensibility.rules.*;
import oracle.communications.platform.persistence.PersistenceHelper;


ArrayList<Object> ruleParams = new ArrayList<Object>();
ruleParams=this.binding.getVariable("ruleParameters");
if(ruleParams.get(0) instanceof GeographicAddressRange && ruleParams.get(1) instanceof GeographicAddress)
{
GeographicAddressRange range=ruleParams.get(0);
GeographicAddress address=ruleParams.get(1);


log.debug( "", "Starting Validate Address Range. Range= "+range.getName()+" Address = " + address.getName() );
// execute for address
GeographicAddressRange rangeResult = validate(range, address);
extensionPointRuleContext.setReturnValue(rangeResult);
}

def GeographicAddressRange validate(GeographicAddressRange range, GeographicAddress address) {
	GeographicAddressRange returnAddress = null;
	AddressRangeManager rangeManager = PersistenceHelper.makeAddressRangeManager();
	//create a characteristic map with just the populated characteristics
	Map<String, String> addrMap = new HashMap<String, String>();
	List resultList = new ArrayList();
	GroovyRulesExecutor groovyRulesExecutor = PersistenceHelper.makeGroovyRulesExecutor();
	resultList = groovyRulesExecutor.execute("CREATE_ADDRESS_CHARACTERISTIC_MAP", Map.class, address);
	addrMap = (HashMap) resultList.get(0);
	Collection<GeographicAddressRange> ranges = new ArrayList<GeographicAddressRange>();
	ranges.add(range);
	//verify whether this range is valid for this address
	returnAddress = rangeManager.processRanges(ranges, addrMap);
	return returnAddress;
}



