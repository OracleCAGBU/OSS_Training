import oracle.communications.inventory.api.entity.*;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.GeographicLocation;
import oracle.communications.inventory.api.entity.GeographicAddress;

Object param1=ruleParameters[0] ;
if(param1 instanceof List<GeographicPlace>)
{
for(GeographicPlace place:param1){
	if (place instanceof GeographicLocation) {
		// execute for location
		StringBuilder formattedIdentifier = new StringBuilder();
		List pc = new ArrayList(place.getCharacteristics());
		if (pc != null) {
			for(int i=0;i<pc.size();i++) {
				String value = ((PlaceCharacteristic)pc.get(i)).getValue();
				if (value != null) formattedIdentifier.append(value).append(" ");
			}
		}
		place.setFormattedIdentifier(formattedIdentifier.toString().trim());
	}
	if (place instanceof GeographicAddress) {
	
	
		// execute for address
		StringBuilder formattedIdentifier = new StringBuilder();
		List pc = new ArrayList(place.getCharacteristics());
		if (pc != null) {
			for(int i=0;i<pc.size();i++) {
				String value = ((PlaceCharacteristic)pc.get(i)).getValue();
				if (value != null) formattedIdentifier.append(value).append(" ");
			}
		}
		place.setFormattedIdentifier(formattedIdentifier.toString().trim());
	}
}
println("executed successfully");
}