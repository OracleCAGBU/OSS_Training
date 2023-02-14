package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.List;

import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.exception.ValidationException;

public interface ServiceAreaResolver {
	/**
	 * Find Service Areas that apply to a Service Address constrained by CFS
	 * specification name and a set of candidate Inventory Group Specification
	 * names.
	 * 
	 * @param serviceAddress
	 *            the service address
	 * @param cfsSpecName
	 *            CFS Specification name
	 * @param candidateAreaSpecs
	 *            Set of candidate Inventory Group Specification names
	 * @return a list of service areas
	 * @throws ValidationException
	 */
	public List<InventoryGroup> findServingAreaForServiceAddress(
	        GeographicAddress serviceAddress, String cfsSpecName,
	        String[] candidateAreaSpecs) throws ValidationException;
}
