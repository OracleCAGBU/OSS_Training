/**
 * Maintenance Log:
 * When                | Who             | Description
 * -----------------   |-----------------|-------------------------------------------------------
 * 02/11/2020          | chansina        |  Created
 * -------------------------------------------------------------------------------------------
 */
package com.mobile.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.common.EntityUtils;
import oracle.communications.inventory.api.party.PartyManager;
import oracle.communications.inventory.api.party.PartySearchCriteria;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyCharacteristic;
import oracle.communications.inventory.api.entity.PartySpecification;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyServiceRel;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.common.Involvement;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
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
	 * This method finds the Party by Name & Spec.
	 * @param coSpecName
	 * @param name
	 * @param nameCriteriaOperator - optional parameter defaulted to EQUALS
	 * @param charMap
	 * @param charCriteriaOperator
	 * @param quantity
	 * @return List<Party>
	 * @throws ValidationException
	 */
	public static List<Party> findParty(String partySpecName, String name, CriteriaOperator nameCriteriaOperator, 
			         Map<String,String> charMap, CriteriaOperator charCriteriaOperator, Integer quantity) throws ValidationException {
		debug(log,"findParty - START");

		PartyManager mgr = PersistenceHelper.makePartyManager();
		List<Party> list = null;

		PartySearchCriteria criteria = mgr.makePartySearchCriteria();
		
		//Set Quantity Criteria in cases where only specific no. of DI are required.
		if(quantity!=null){
			long max = quantity.longValue()-1;
			criteria.setRange(0, max);
		}

		//Set Specification Criteria
		if(null!=partySpecName){
			PartySpecification spec= EntityUtils.findSpecification(PartySpecification.class, partySpecName);
			criteria.setPartySpecification(spec);
		}

		// Set Name Criteria
		if(name!=null){
			CriteriaItem nameCI = criteria.makeCriteriaItem();
			nameCI.setValue(name);
			if(nameCriteriaOperator==null){
				nameCI.setOperator(CriteriaOperator.EQUALS);
			} else {
				nameCI.setOperator(nameCriteriaOperator);
			}
			criteria.setName(nameCI);
		}


		//Set Characteristics Criteria
		Collection<CriteriaItem> criteriaItems = new ArrayList<>();
		if(null!=charMap){
			CriteriaItem charCriteriaItem = null;
			for (Map.Entry<String, String> entry : charMap.entrySet()) {
				charCriteriaItem = criteria.makeCriteriaItem();
				charCriteriaItem.setCriteriaClass(PartyCharacteristic.class);
				charCriteriaItem.setName(entry.getKey());
				charCriteriaItem.setValue(entry.getValue());
				if(charCriteriaOperator==null){
					charCriteriaItem.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
				} else {
					charCriteriaItem.setOperator(charCriteriaOperator);
				}
				criteriaItems.add(charCriteriaItem);
			}
			criteria.addCharacteristicData(criteriaItems);
		}

		criteria.setDisableOrdering(true);
		list = mgr.findParty(criteria);
		debug(log,"findParty - END, List Size=" + list.size());
		return list;
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
