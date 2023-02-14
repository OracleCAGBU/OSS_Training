/*
REPLACE_COPYRIGHT_HERE
*/
package oracle.communications.inventory.c2a.impl;
import java.util.Set;

import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupCharacteristic;

public class ServiceAreaUtils {
	public static String getServiceAreaProperty(InventoryGroup area, String name) {
		Set<InventoryGroupCharacteristic> chars = area.getCharacteristics();
		for (InventoryGroupCharacteristic c : chars) {
			if (name.equalsIgnoreCase(c.getName())) {
				return c.getValue();
			}
		}
		return null;
	}
}
