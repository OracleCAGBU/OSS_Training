import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.resource.MessageResource;

import org.apache.commons.lang.StringUtils;


Object param1=ruleParameters[0];
if(param1 instanceof List<GeographicAddress>)
{
for(GeographicAddress address:param1)
{	
// This is called from other address/address range rules

log.debug( "", "Create Address Characteristic Map: " + address.getName() );
Map<String, String> addrMap = new HashMap<String, String>();
addrMap = createMap(address);
return addrMap;
}
}

def Map createMap(GeographicAddress address) {
	//this list of characteristic names must correspond to the list of characteristic names used on the address specifications
	final String LANE = MessageResource.getMessage("addressRange.lane");
	final String SUBLANE = MessageResource.getMessage("addressRange.sublane");
	final String CITY_NUMBER = MessageResource.getMessage("addressRange.cityNumber");
	final String RURAL_NUMBER = MessageResource.getMessage("addressRange.ruralNumber");
	final String FLOOR = MessageResource.getMessage("addressRange.floor");
	final String ROOM = MessageResource.getMessage("addressRange.room");
	final String BUILDING = MessageResource.getMessage("addressRange.building");
	final String TOWN = MessageResource.getMessage("addressRange.town");
	final String TEAM = MessageResource.getMessage("addressRange.team");
	final String TO = MessageResource.getMessage("addressRange.to_prefix")+"_";
	final String FROM = MessageResource.getMessage("addressRange.from_prefix")+"_";
	Map<String, String> addrMap = new HashMap<String, String>();
	//set the input address component variables
	ArrayList<PlaceCharacteristic> pc = new ArrayList<PlaceCharacteristic>(address.getCharacteristics());
	if (pc != null) {
		for(int i=0;i < pc.size();i++) {
			String value = (pc.get(i)).getValue();
			String name = (pc.get(i)).getName();
			if (name.equals(LANE)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(LANE, value);
				}
			}
			else if (name.equals(SUBLANE)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(SUBLANE, value);
				}
			}
			else if (name.equals(CITY_NUMBER)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(CITY_NUMBER, value);
				}
			}
			else if (name.equals(FLOOR)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(FLOOR, value);
				}
			}
			else if (name.equals(ROOM)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(ROOM, value);
				}
			}
			else if (name.equals(BUILDING)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(BUILDING, value);
				}
			}
			if (name.equals(RURAL_NUMBER)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(RURAL_NUMBER, value);
				}
			}
			else if (name.equals(TEAM)) {
				if (StringUtils.isNotBlank(value)) {
					addrMap.put(TEAM, value);
				}
			}
		} // end of loop
	}	//end of if
	return addrMap;
} //end of function

