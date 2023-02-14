import oracle.communications.inventory.api.characteristic.container.CharacteristicData;
import oracle.communications.inventory.api.entity.*;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.group.InventoryGroupManager;
import oracle.communications.inventory.api.group.InventoryGroupSearchCriteria;
import oracle.communications.inventory.api.location.*;
import oracle.communications.inventory.api.number.container.TelephoneNumberSelectionCriteria;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

Object param1=ruleParameters[0];
if(param1 instanceof TelephoneNumberSelectionCriteria)
{
TelephoneNumberSelectionCriteria selectionCriteria=param1;

log.debug("" ,  "Select Telephone Numbers" );
selectTelephoneNumbers (log, selectionCriteria, extensionPointRuleContext);
}

def void selectTelephoneNumbers (Log log,TelephoneNumberSelectionCriteria selectionCriteria, ExtensionPointRuleContext context) {

	ServiceSpecification serviceSpec = getServiceSpecification(selectionCriteria.getServiceSpecName(), log);
	List allTelephoneNumbers = selectTn(serviceSpec, selectionCriteria, log);
	for (int i = 0; allTelephoneNumbers != null && i < allTelephoneNumbers.size(); i++) {
		log.debug("" , "TN:"+i+" "+allTelephoneNumbers.get(i) );

	}

	context.setReturnValue( allTelephoneNumbers );
}

def ServiceSpecification getServiceSpecification(String specName, Log log) {
	Finder finder = null;
	ServiceSpecification serviceSpec = null;
	if (Utils.isEmpty(specName)) return serviceSpec;

	try {
		finder = PersistenceHelper.makeFinder();
		Collection<ServiceSpecification> serviceSpecs = finder.findByName(ServiceSpecification.class, specName);
		if (serviceSpecs != null && serviceSpecs.size()>0) {
			serviceSpec = serviceSpecs.iterator().next();
		}
		else {
			log.exception("number.cantFindServiceSpecByName", new java.lang.IllegalArgumentException(), specName);
			return null;
		}
	} catch (Exception e) {
		log.exception("number.cantFindServiceSpecByName", e, specName);
	}
	finally {
		if (finder != null)
			finder.close();
	}
	return serviceSpec;
}

def List getPreSelectedTelephoneNumbers(TelephoneNumberSelectionCriteria criteria, Log log) {
	String[] selectedTNs = criteria.getSelectedTN();
	List<TelephoneNumber> tnCandidates = null;

	if (!Utils.isEmpty(selectedTNs)) {
		// The user specified the TN, find the TNs.
		tnCandidates = PersistenceHelper.makeTelephoneNumberManager().findTelephoneNumbers(selectedTNs, false);
		if (tnCandidates.size() == 0) {
			log.exception("number.selectedTNNotFound", new java.lang.IllegalArgumentException());
		}
		if (tnCandidates.size() < selectedTNs.length) {
			log.warn("number.notAllSelectedTNWereFound", selectedTNs.length, tnCandidates.size());
		}
	}

	return tnCandidates;
}

def List getInvGroupsForServiceSpec(ServiceSpecification serviceSpec, Log log) {
	InventoryGroupManager inventoryGroupManager = PersistenceHelper.makeInventoryGroupManager();
	List groups = inventoryGroupManager.getInventoryGroupsForResource(serviceSpec);
	if (groups == null || groups.isEmpty())
		log.exception("", new java.lang.IllegalArgumentException(), "The chosen service specification does not have any groups." );
	return groups;
}

def InventoryGroup validateGroup(ServiceSpecification serviceSpec, TelephoneNumberSelectionCriteria selectionCriteria, Log log) {
	// If the group name is specified, make sure it is part of the ServiceSpec groups.
	String groupName = selectionCriteria.getGroup();
	InventoryGroupManager mgr = PersistenceHelper.makeInventoryGroupManager();
	List groups = GetInvGroupsForServiceSpec.getInvGroupsForServiceSpec(serviceSpec, log);
	if (serviceSpec != null && !Utils.isEmpty(groupName) && groups != null) {
		boolean foundGroup = false;
		Iterator groupIterator = groups.iterator();
		while (groupIterator.hasNext()) {
			InventoryGroup inventoryGroup = (InventoryGroup) groupIterator.next();
			String name = inventoryGroup.getName();
			if (groupName.equalsIgnoreCase(name)) {
				foundGroup = true;
				return inventoryGroup;
			}
		}
		if (!foundGroup)
			log.exception("", new java.lang.IllegalArgumentException(), "The chosen inventory group is not part of the chosen service specification group." );
	}
	else if (serviceSpec == null && !Utils.isEmpty(groupName)) {
		InventoryGroupSearchCriteria criteria = mgr.makeInventoryGroupSearchCriteria();
		CriteriaItem name = criteria.makeCriteriaItem();
		name.setOperator( CriteriaOperator.EQUALS );
		name.setValue( groupName );
		criteria.setName( name );
		groups = mgr.findInventoryGroup( criteria );
		if (!Utils.isEmpty(groups))
			return (InventoryGroup) groups.iterator().next();
	}
	return null;
}

def List selectTn(ServiceSpecification serviceSpec, TelephoneNumberSelectionCriteria criteria, Log log) {
	// Define the number of telephone numbers can be auto-selected.
	int autoSelectCounts = 1;
	// bug 7613315; commented out ValidateGroup
	InventoryGroup group = null;//ValidateGroup.validateGroup(serviceSpec, criteria, log);
	log.debug("" , "group: " + group );
	Finder finder = null;
	try {
		List groups = null;

		// If service spec was specified, use it to narrow down the inventory groups.
		log.debug("" , "serviceSpec: "+serviceSpec );
		if (serviceSpec != null) {
			groups = GetInvGroupsForServiceSpec.getInvGroupsForServiceSpec(serviceSpec, log);
			if (Utils.isEmpty(groups))
				return null;
		}
		log.debug("" ,  "groups.isEmpty: "+Utils.isEmpty(groups) );

		// If group was specified, add it to the inventory groups.
		if (group != null) {
			if (groups == null)
				groups = new ArrayList();
			groups.add(group);
		}
		log.debug("" ,  "groups.isEmpty: "+Utils.isEmpty(groups) );

		// If pre-selected TNs was specified, use it to narrow down the TN candidates.
		List tnCandidates = GetPreSelectedTelephoneNumbers.getPreSelectedTelephoneNumbers(criteria, log);
		log.debug("" ,  "tnCandidates.isEmpty: " + Utils.isEmpty(tnCandidates) );

		// After narrowing down the TN candidates, need to execute the find
		// using are criteria based on core assignment logic (i.e. only
		// return available and assignable TNs)
		finder = PersistenceHelper.makeFinder();
		finder.setResultClass(oracle.communications.inventory.api.entity.TelephoneNumber.class);
		finder.setCandidates(tnCandidates);

		List parameters = new ArrayList();
		List values = new ArrayList();
		List variableTypes = new ArrayList();
		List variables = new ArrayList();

		// Only INSTALLED TNs.
		String filter = "adminState == pState";
		parameters.add( "pState" );
		values.add( InventoryState.INSTALLED );

		// TNs with matching characteristics.
		CharacteristicData [] characteristicData = criteria.getCharacteristics();
		for ( int i = 0; characteristicData != null && i < characteristicData.length; ++i ) {
			CharacteristicData characteristic = characteristicData[i];
			String charName = characteristic.getName();
			// multi-valued is not yet supported -- so just use the first one.
			String charValue = characteristic.getValue()[0];
			filter += " && ( characteristics.contains(vCharacteristic" + i + ") && ";
			filter += "      ( vCharacteristic" + i + ".name == pCharName" + i + " && ";
			filter += "        vCharacteristic" + i + ".value == pCharValue" + i + " ) )";
			parameters.add( "pCharName" + i );
			values.add( charName );
			parameters.add( "pCharValue" + i );
			values.add( charValue );
			variableTypes.add( TNCharacteristic.class );
			variables.add( "vCharacteristic" + i );
		}

		// TNs with matching patterns.
		if ( !Utils.isEmpty( criteria.getPattern() ) ) {
			if ( criteria.getEndedWithPattern() ) {
				filter += " && "
				+ CriteriaOperator.translateStringMatch( "name", "pName",
						CriteriaOperator.ENDS_WITH );
			}
			else {
				filter += " && "
				+ CriteriaOperator.translateStringMatch( "name", "pName",
						CriteriaOperator.CONTAINS );
			}
			parameters.add( "pName" );
			values.add( criteria.getPattern() );
		}

		if ( !Utils.isEmpty( groups ) ) {
			String f = "this.groups.contains(vInvGroupRef) && pGroups.contains(vInvGroupRef.invGroup)";
			parameters.add( "pGroups" );
			values.add( groups );

			variables.add( "vInvGroupRef" );
			variableTypes.add( InvGroupRef.class );

			filter += " && " + f;
		}

		// Handle the consumers - no consumer OR (no blocking, no reservations, and no assignments)
		filter += " && (consumers.isEmpty() ||"
		+ " ("
		+ "     !(consumers.contains(vCondition) && vCondition.type == pBlockedType && vCondition.endDate > pCurrentDate) &&"
		+ "     !(consumers.contains(vReservation) && vReservation.reservationType != null && vReservation.endDate > pCurrentDate) &&"
		+ "     !(consumers.contains(vAssignment) && vAssignment.serviceConsumer != null && (vAssignment.endDate > pCurrentDate || vAssignment.adminState != pUnassignedState))";
		filter += " ))";

		parameters.add( "pCurrentDate" );
		values.add( Utils.getCurrentDate() );
		parameters.add( "pBlockedType" );
		values.add( ConditionType.BLOCKED );
		parameters.add( "pUnassignedState" );
		values.add( AssignmentState.UNASSIGNED );
		variableTypes.add( TNCondition.class );
		variables.add( "vCondition" );
		variableTypes.add( TNAssignment.class );
		variables.add( "vAssignment" );
		variableTypes.add( TNReservation.class );
		variables.add( "vReservation" );

		finder.setFilter(filter);

		finder.setParameters((String[]) parameters.toArray(new String[parameters.size()]),
				(Object[]) values.toArray(new Object[values.size()]));
		finder.declareVariables((Class[]) variableTypes.toArray(new Class[variableTypes.size()]),
				(String[]) variables.toArray(new String[variables.size()]));

		int count = criteria.getMaxResults();
		// If NOT manualSelect, then the user wants the system to automatically
		// selects the TN.  Otherwise, if manualSelect, then return the maxResults
		// of TN for use to select.
		if (!criteria.getManualSelect()) {
			count = 1;
		}
		finder.setRange(0, count);

		finder.setOrdering( "name ascending" );

		// convert the collection to a list
		Collection tnCollection = finder.findMatches();
		List tnList = new ArrayList();
		tnList.addAll(tnCollection);
		return tnList;
	} finally {
		if (finder != null) {
			finder.close();
		}
	}
}

