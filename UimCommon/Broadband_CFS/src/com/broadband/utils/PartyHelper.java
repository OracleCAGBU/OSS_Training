/**
 * Maintenance Log:
 * When                | Who             | Description
 * -----------------   |-----------------|-------------------------------------------------------
 * 02/11/2020          | chansina        |  Created
 * -------------------------------------------------------------------------------------------
 */
package com.broadband.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyServiceRel;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.common.Involvement;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.platform.persistence.PersistenceHelper;

/**
 * This class will have all the place related functionalities.
 * 
 * @author Ramasamy M
 *
 */
public class PartyHelper {

	private static final Log log = LogFactory.getLog(PartyHelper.class);

	/**
	 * To Generate the log.
	 * 
	 * @param logger
	 * @param objects
	 */
	public static void debug(Log logger, Object... objects) {
		try {
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				for (Object obj : objects) {
					sb.append(obj);
				}
				logger.debug("", sb.toString());
			}
		} catch (Exception e) {
			logger.error("", "Error generating log message : " + e.getMessage(), e);
		}
	}

	/**
	 * Get the CFS Service object from the current BI. 
	 * 
	 * @param config
	 * @return
	 * @throws ValidationException
	 */
	public static void changeOwnership(Service cfs, String accountId) throws ValidationException {
		debug(log,"changeOwnership - START*");
		PartyServiceRel oldPartyServiceRel = null;
		List<Involvement> partyList = new ArrayList<> ();
		boolean isPlaceNameUpdateRequired =false;
        if(cfs != null) {
	        // Fetch Party and set customer attributes
        	Set<PartyServiceRel> partySet = cfs.getParty();
        	Iterator<PartyServiceRel> partyIter = partySet.iterator();
			while(partyIter.hasNext()) {
				PartyServiceRel partyServiceRel = partyIter.next();
				Party party = partyServiceRel.getParty();
				if(!accountId.equals(party.getId())) {
					isPlaceNameUpdateRequired=true;
					oldPartyServiceRel = partyServiceRel;
					partyList.add(oldPartyServiceRel);
				}
			}
        }
        if(isPlaceNameUpdateRequired) {
        	PlaceHelper.updatePlaceName(cfs, accountId);
        }
		if(!Utils.isEmpty(partyList)) {
			AttachmentManager attachmentManager;
			attachmentManager = PersistenceHelper.makeAttachmentManager();
			attachmentManager.deleteRel(partyList);
		}
		debug(log,"changeOwnership - END*");
	}

}
