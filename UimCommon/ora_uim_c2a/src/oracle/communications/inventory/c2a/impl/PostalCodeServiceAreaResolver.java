package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicLocation;
import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.InventoryGroupCharacteristic;
import oracle.communications.inventory.api.entity.PlaceCharacteristic;
import oracle.communications.inventory.api.entity.PlaceInventoryGroupRel;
import oracle.communications.inventory.api.entity.common.Involvement;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.place.PlaceManager;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.ServiceAreaResolver;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

public class PostalCodeServiceAreaResolver implements ServiceAreaResolver {
	public List<InventoryGroup> findServingAreaForServiceAddress(
	        GeographicAddress serviceAddress, String cfsSpecName,
	        String[] candidateAreaSpecs) throws ValidationException {
		if (serviceAddress == null || Utils.isEmpty(candidateAreaSpecs)) {
			return null;
		}
		try {

			Collection<GeographicLocation> places = findServiceLocations(serviceAddress);

			List<InventoryGroup> groups = null;
			if (!Utils.isEmpty(places)) {
				GeographicLocation place = places.iterator().next();
				Set<String> candidateSpecNames = new HashSet<String>();
				for (int i = 0; i < candidateAreaSpecs.length; i++) {
					candidateSpecNames.add(candidateAreaSpecs[i]);
				}
				groups = this.findInvGroups(place, cfsSpecName,
				        candidateSpecNames);
			}

			return groups;
		} finally {
			RequestPolicyHelper.checkPolicy();
		}

	}

	/**
	 * Find service locations using the matching criteria given a service
	 * address
	 * 
	 * @param serviceAddress
	 * @return
	 * @throws ValidationException
	 */
	private Collection<GeographicLocation> findServiceLocations(
	        GeographicAddress serviceAddress) throws ValidationException {
		String charName = "PostalCode";
		Finder f = PersistenceHelper.makeFinder();
		try {
			String postalCode = null;

			Set<PlaceCharacteristic> values = serviceAddress
			        .getCharacteristics();
			for (PlaceCharacteristic value : values) {
				String name = value.getName();
				if (name.equals(charName)) {
					postalCode = value.getValue();
					break;
				}
			}

			Collection<GeographicLocation> places = f.findByName(
			        GeographicLocation.class, postalCode);
			return places;
		} finally {
			if (f != null)
				f.close();
			RequestPolicyHelper.checkPolicy();
		}
	}

	/**
	 * Find the Inventory Groups that are associated with a service location
	 * constrained by CFS specification name and a set of candidate Inventory
	 * Group Specification names
	 * 
	 * @param location
	 *            service location constraining the associated service areas
	 * @param cfsSpecName
	 *            name of the CFS specification constraining the associated
	 *            service areas
	 * @param candidateAreaSpecs
	 *            set of service area specification names constraining the
	 *            associated service areas
	 * @return collection of matching service areas
	 * @throws ValidationException
	 */
	private List<InventoryGroup> findInvGroups(GeographicLocation location,
	        String cfsSpecName, Set<String> candidateAreaSpecs)
	        throws ValidationException {
		Finder f = PersistenceHelper.makeFinder();
		try {
			if (location == null)
				return null;

			List<InventoryGroup> invGroupsReturn = new ArrayList<InventoryGroup>();

			PlaceManager placeMgr = PersistenceHelper.makePlaceManager();
			List<Involvement> involvments = placeMgr.getInvolvementsForPlace(
			        InventoryGroup.class, location);
			for (Involvement involvement : involvments) {
				if (involvement instanceof PlaceInventoryGroupRel) {
					InventoryGroup invGroup = ((PlaceInventoryGroupRel) involvement)
					        .getInventoryGroup();
					if (invGroup != null) {
						invGroupsReturn.add(invGroup);
					}
				}
			}

			List<InventoryGroup> returnList = new ArrayList<InventoryGroup>();
			for (InventoryGroup group : invGroupsReturn) {
				Set<InventoryGroupCharacteristic> chars = group
				        .getCharacteristics();
				boolean add = false;
				if (group.getSpecification() != null
				        && candidateAreaSpecs.contains(group.getSpecification()
				                .getName())) {
					for (InventoryGroupCharacteristic c : chars) {
						if (cfsSpecName == null
						        || ("CFS".equals(c.getName()) && cfsSpecName
						                .equals(c.getValue()))) {
							add = true;
							break;
						}
					}
				}
				if (add)
					returnList.add(group);
			}
			return returnList;
		} finally {
			if (f != null)
				f.close();
			RequestPolicyHelper.checkPolicy();
		}
	}
}
