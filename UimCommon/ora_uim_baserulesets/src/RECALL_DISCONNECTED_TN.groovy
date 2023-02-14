import javax.transaction.UserTransaction;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.TNAssignment;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.util.*;
import oracle.communications.inventory.api.ObjectState;
import oracle.communications.inventory.extensibility.extension.util.*;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.PersistenceManager;

log.debug("", "Recall Disconnected Telephone Number");

log.debug("", "timerExpired extension point has been invoked");

Finder finder = null;
TNAssignment tnAssignment = null;
PersistenceManager pm = null;
UserTransaction ut = null;
try {

	pm = PersistenceHelper.makePersistenceManager();

	// Define the expiry for changing TRANSITIONAL to UNASSIGNED state
	GregorianCalendar stateExpiry = new GregorianCalendar();
	int durationInDays = 30;
	stateExpiry.add(GregorianCalendar.DAY_OF_MONTH, durationInDays);

	// For testing only; to set the TRANSITIONAL duration to 1 minute
	//int durationInMins = 1;
	//stateExpiry.add(GregorianCalendar.MINUTE,durationInMins);

	boolean finished = false;
	// there is no do/while loop in groovy so we will substitute it with this
	// for(;;){ // infinite for
	//  ...
	// 	   if (conditionBreak){ //condition to break
	//			break
	//	   }
	// }

	for (;;) // do
	{
		ut = pm.getTransaction();
		ut.begin();

		finder = PersistenceHelper.makeFinder();
		String filter = "o.stateExpiry < :pCurrentDate AND " +
				"(o.adminState = :pDisconnectedState OR o.adminState = :pTransitionalState)  AND o.objectState = :pObjectState";
		finder.setResultClass(TNAssignment.class);
		finder.setJPQLFilter(filter);
		String[] stringArray = [
			"pCurrentDate",
			"pDisconnectedState",
			"pTransitionalState",
			"pObjectState"
		];
		Object[] objectArray = [
			new Date(),
			AssignmentState.DISCONNECTED,
			AssignmentState.TRANSITIONAL,
			ObjectState.ACTIVE
		];
		finder.setParameters(stringArray, objectArray);
		finder.setRange( 0, 500 );

		Collection candidates = finder.findMatches();

		if ( candidates.isEmpty() ) finished = true;

		Iterator itr = candidates.iterator();
		while (itr.hasNext()) {
			tnAssignment = (TNAssignment)itr.next();
			AssignmentState currentState = tnAssignment.getAdminState();
			// Change DISCONNECTED to TRANSITIONAL state
			if (currentState.getValueAsString().equals(AssignmentState.DISCONNECTED.getValueAsString())) {
				tnAssignment.setAdminState(AssignmentState.TRANSITIONAL);
				tnAssignment.setStateExpiry(stateExpiry.getTime());
			}
			// Change TRANSITIONAL to UNASSIGNED state
			else if (currentState.getValueAsString().equals(AssignmentState.TRANSITIONAL.getValueAsString())) {
				tnAssignment.setAdminState(AssignmentState.UNASSIGNED);
				tnAssignment.getResource().setCurrentAssignment(null);
				tnAssignment.setStateExpiry(null);
			}
		}

		ut.commit();
		if (finished) {
			break;
		}
	}
	// while ( ! finished );
}
catch (Throwable t) {
	if ( ut != null )
	{
		try
		{
			ut.rollback();
		}
		catch ( Exception notUsed ) {}
	}

	if (tnAssignment != null)
		log.error("consumer.failedToExpiredDisconnectedTN", tnAssignment.getTelephoneNumber().getName() + t.getLocalizedMessage());
	else
		log.error("consumer.failedToFindTNAssignment", t.getLocalizedMessage());
}
finally {
	if (pm != null)
		pm.close();

	if (finder != null)
		finder.close();
}

