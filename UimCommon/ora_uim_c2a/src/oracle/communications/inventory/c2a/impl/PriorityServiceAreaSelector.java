package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Collection;
import java.util.Set;

import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupCharacteristic;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.c2a.ServiceAreaBestFitSelector;

public class PriorityServiceAreaSelector implements ServiceAreaBestFitSelector {

	@Override
	public InventoryGroup selectBestServiceArea(
	        ServiceConfigurationVersion svcConVers,
	        Collection<InventoryGroup> areas) {
		InventoryGroup best = null;
		int bestPriority = 0;
		for (InventoryGroup a : areas) {
			int aPriority = getPriority(a);
			if (best == null || aPriority > bestPriority) {
				best = a;
				bestPriority = aPriority;
			}
		}
		return best;
	}

	private int getPriority(InventoryGroup area) {
		int priority = 0;
		Set<InventoryGroupCharacteristic> chars = area.getCharacteristics();
		for (InventoryGroupCharacteristic c : chars) {
			if ("Priority".equalsIgnoreCase(c.getName())) {
				String value = c.getValue();
				try {
					priority = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					// ignore
				}
				break;
			}
		}
		return priority;
	}
}
