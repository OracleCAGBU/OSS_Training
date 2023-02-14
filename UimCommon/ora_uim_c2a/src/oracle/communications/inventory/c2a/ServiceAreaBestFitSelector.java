package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Collection;

import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;

public interface ServiceAreaBestFitSelector {
	/**
	 * Select the best service area to supply resources for designing a service
	 * configuration.
	 * 
	 * @param svcConVers
	 *            the service configuration under design
	 * @param areas
	 *            candidate service areas
	 * @return the best service area selected
	 */
	public InventoryGroup selectBestServiceArea(
	        ServiceConfigurationVersion svcConVers,
	        Collection<InventoryGroup> areas);
}
