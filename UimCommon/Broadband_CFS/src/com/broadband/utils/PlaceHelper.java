package com.broadband.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.broadband.utils.Constants;
import com.broadband.utils.EntityHelper;
import com.broadband.utils.PlaceHelper;
import com.broadband.utils.UimHelper;

import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.entity.GeographicAddress;
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.PartyServiceRel;
import oracle.communications.inventory.api.entity.PlaceServiceRel;
import oracle.communications.inventory.api.entity.PlaceSpecification;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.common.Involvement;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.place.PlaceManager;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.xmlbeans.ConfigurationItemPropertyType;
import oracle.communications.inventory.xmlbeans.ConfigurationItemType;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.PersistenceHelper;

public class PlaceHelper {

	private static final Log log = LogFactory.getLog(PlaceHelper.class);

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
	 * Set the Place name using new Party's Account Id prefix.
	 * 
	 * @param cfs
	 * @param accountId
	 * @throws ValidationException
	 */
	public static void updatePlaceName(Service cfs, String accountId) throws ValidationException {
		debug(log, "updatePlaceName - START");
		if (cfs != null) {
			// Fetch Place and set Name attribute.
			Set<PlaceServiceRel> placeSet = cfs.getPlace();
			Iterator<PlaceServiceRel> placeIter = placeSet.iterator();
			while (placeIter.hasNext()) {
				PlaceServiceRel partyServiceRel = placeIter.next();
				GeographicPlace place = partyServiceRel.getGeographicPlace();
				String placeName = place.getName();
				debug(log, "Place Name before ", placeName);
				if (!placeName.contains(accountId)) {
					String oldPartyId = placeName.split(Constants.COMMA)[0];
					placeName = placeName.replace(oldPartyId, accountId);
					place.setName(placeName);
					List<GeographicPlace> places = new ArrayList<>();
					places.add(place);
					PersistenceHelper.makePlaceManager().updateGeographicPlace(places);
					debug(log, "Place Name set as ", placeName);
				} else {
					debug(log, "Place Name not set as old Place Name does not contains AccountId.");
				}
			}
		}
		debug(log, "updatePlaceName - END");
	}

	/**
	 *  Process the update of the place during the relocation internal.
	 * @param config
	 * @param configItemType
	 * @param configSpec
	 * @return
	 * @throws ValidationException
	 */
	public static GeographicPlace processPlaceUpdate(ServiceConfigurationVersion config, ConfigurationItemType configItemType, String...configSpec) throws ValidationException {
		debug(log,"processUpdatePlace - START");
		Service cfs = null;
		
		if(configSpec != null && configSpec.length > 0) {
			cfs = UimHelper.getCfsService(config, configSpec[0]);
		}else {
			cfs = UimHelper.getCfsService(config);
		}
		Set<PlaceServiceRel> placeServiceRelSet = cfs.getPlace();
		GeographicPlace geographicPlace = null;
		if(!Utils.isEmpty(placeServiceRelSet)) {
			
			Iterator<PlaceServiceRel> iterPlaceServiceRelIter = placeServiceRelSet.iterator();
			
			while(iterPlaceServiceRelIter.hasNext())	{
				PlaceServiceRel placeServiceRel = iterPlaceServiceRelIter.next();
				geographicPlace = placeServiceRel.getGeographicPlace();
				break;
			}
		}
		
		if(geographicPlace != null) {
			List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
			
			String latitude = null; 
			String longitude = null; 
			
			HashMap<String,String> charMap = new HashMap<>();
			for(ConfigurationItemPropertyType prop: propList){
				String propertyName = prop.getName();
				if(propertyName.equals(Constants.PARAM_LATITUDE)) {
					latitude = prop.getValue();
				}else if(propertyName.equals(Constants.PARAM_LONGITUDE)) {
					longitude = prop.getValue();
				}/*else if(propertyName.equals(Constants.PARAM_DIVISION)) {
					charMap.put(Constants.PARAM_FDIVISION, prop.getValue());
				}*/else {
					charMap.put(propertyName, prop.getValue());
				}	
			}	
			geographicPlace.setLatitude(latitude);
			geographicPlace.setLongitude(longitude);
			// UimHelper.setEntityChars(geographicPlace, charMap);
			EntityHelper entityHelper = new EntityHelper();
			entityHelper.addCharacteristics(geographicPlace.getSpecification(), geographicPlace, charMap);
		}else {
			log.validationError("", "Place not found for the Service. ");
		}
		
		UimHelper.setConfigItemCharValue(config, Constants.PARAM_SERVICE_ADDRESS_CI, Constants.PARAM_RELOCTAIONINTERNALDATE, (new Date()).toString());
		debug(log,"processUpdatePlace - END");		
		return geographicPlace;
	}
	
	/**
	 * Create Geographic Place/Address for the Service Address and assign to the service and service configuration. 
	 * @param config
	 * @param configItemType
	 * @param configSpec
	 * @return
	 * @throws ValidationException
	 */
	public static GeographicPlace processPlace(ServiceConfigurationVersion config, HashMap<String,String> paramPlace, String...configSpec) throws ValidationException {
		debug(log,"processPlace - START");
      
		debug(log," CFS in processPlace : ", config.getService());

		Service cfs = UimHelper.getCfsFromRfs(config.getService());
		if(cfs==null) {
			if(configSpec != null && configSpec.length > 0) {
				cfs = UimHelper.getCfsService(config, configSpec[0]);
			}else {
				cfs = UimHelper.getCfsService(config);
			}
		}
		Party customer = null;
		String accountId = null;
		if(cfs != null) {
	        // Fetch Party and set customer attributes
			Set<PartyServiceRel> partySet = cfs.getParty();
			Iterator<PartyServiceRel> partyIter = partySet.iterator();
			while(partyIter.hasNext()) {
				PartyServiceRel partyServiceRel = partyIter.next();
				customer = partyServiceRel.getParty();
				accountId = customer.getId();
				break;
			}
        }
		
		List<GeographicPlace> geographicPlaceList = null;
		GeographicPlace geographicPlace = null;
		//List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();
		
		String placeName = "";
		String latitude = paramPlace.get(Constants.PARAM_LATITUDE); 
		String longitude = paramPlace.get(Constants.PARAM_LONGITUDE); 
		String streetVillageName = paramPlace.get(Constants.PARAM_VILLAGE_NAME);
		String streetHouseNumber = paramPlace.get(Constants.PARAM_STREET_HOUSE_NUMBER);
		String buildingName = paramPlace.get(Constants.PARAM_BUILDING_NAME);
		
		HashMap<String,String> charMap = new HashMap<>();
		/*for(ConfigurationItemPropertyType prop: propList){
			String propertyName = prop.getName();
			if(propertyName.equals(Constants.PARAM_LATITUDE)) {
				latitude = prop.getValue();
			}else if(propertyName.equals(Constants.PARAM_LONGITUDE)) {
				longitude = prop.getValue();
			}else {
				charMap.put(propertyName, prop.getValue());
			}	
			
			if(propertyName.equals(Constants.PARAM_VILLAGE_NAME)) {
				streetVillageName = prop.getValue();
			}
			if(propertyName.equals(Constants.PARAM_STREET_HOUSE_NUMBER)) {
				streetHouseNumber = prop.getValue();
			}
			if(propertyName.equals(Constants.PARAM_BUILDING_NAME)) {
				buildingName = prop.getValue();
			}
		}*/	
		placeName =/* accountId + Constants.DELIMITER_PIPE_SEPARATOR +*/ streetVillageName + Constants.DELIMITER_PIPE_SEPARATOR + streetHouseNumber + Constants.DELIMITER_PIPE_SEPARATOR + buildingName;
		geographicPlaceList = UimHelper.findGeographicPlace(Constants.SPEC_ADDRESS, placeName, CriteriaOperator.BEGINS_WITH, null, CriteriaOperator.EQUALS);
		debug(log,"geographicPlaceList count " + geographicPlaceList.size());
		debug(log,"Before try");
		try {
			debug(log,"Inside try");

			if(!Utils.isEmpty(geographicPlaceList)) {
				debug(log,"Not null");

				geographicPlace = geographicPlaceList.get(0);
				//propList = configItemType.getPropertyList();
				
				
				charMap = new HashMap<>();
				charMap.put(Constants.PARAM_LATITUDE, paramPlace.get(Constants.PARAM_LATITUDE));
				charMap.put(Constants.PARAM_LONGITUDE, paramPlace.get(Constants.PARAM_LONGITUDE));
				charMap.put(Constants.PARAM_VILLAGE_NAME, paramPlace.get(Constants.PARAM_VILLAGE_NAME));
				charMap.put(Constants.PARAM_STREET_HOUSE_NUMBER, paramPlace.get(Constants.PARAM_STREET_HOUSE_NUMBER));
				charMap.put(Constants.PARAM_BUILDING_NAME, paramPlace.get(Constants.PARAM_BUILDING_NAME));

				/*for(ConfigurationItemPropertyType prop: propList){
					String propertyName = prop.getName();
					if(propertyName.equals(Constants.PARAM_LATITUDE)) {
						latitude = prop.getValue();
						geographicPlace.setLatitude(latitude);
					}else if(propertyName.equals(Constants.PARAM_LONGITUDE)) {
						longitude = prop.getValue();
						geographicPlace.setLongitude(longitude);
					}else {
						charMap.put(propertyName, prop.getValue());
					}	
				}*/	
				// UimHelper.setEntityChars(geographicPlace, charMap);
				EntityHelper entityHelper = new EntityHelper();
				entityHelper.addCharacteristics(geographicPlace.getSpecification(), geographicPlace, charMap);
				
				// Associate place with service.
				Set<PlaceServiceRel> placeServiceRelSet = cfs.getPlace();
				if(Utils.isEmpty(placeServiceRelSet)) {
					if(geographicPlace != null) {
						debug(log,"Associate place with service. ");
						Involvement servicePlaceInvolvement = null;
						servicePlaceInvolvement = PersistenceHelper.makeAttachmentManager().makeRel(PlaceServiceRel.class);
				
						servicePlaceInvolvement.setToEntity(cfs);
						servicePlaceInvolvement.setFromEntity(geographicPlace);
				
						AttachmentManager attachmentManager = PersistenceHelper.makeAttachmentManager();
						attachmentManager.createRel(servicePlaceInvolvement);
					}
				}
			}else {
				// Remove any address association if exist
				debug(log,"Before finding PlaceServiceRel");
				Set<PlaceServiceRel> placeServiceRelSet = cfs.getPlace();
				debug(log,"placeServiceRelSet "+placeServiceRelSet);
				if(!Utils.isEmpty(placeServiceRelSet)) {
					Iterator<PlaceServiceRel> iterPlaceServiceRelIter = placeServiceRelSet.iterator();
					List<Involvement> placeList = new ArrayList<> ();
					
					while(iterPlaceServiceRelIter.hasNext())	{
						PlaceServiceRel placeServiceRel = iterPlaceServiceRelIter.next();
						placeList.add(placeServiceRel);
					}
					if(!Utils.isEmpty(placeList)) {
						AttachmentManager attachmentManager;
						attachmentManager = PersistenceHelper.makeAttachmentManager();
						attachmentManager.deleteRel(placeList);
					}
				}
				
				PlaceSpecification placeSpec = (PlaceSpecification) UimHelper.getSpecification(Constants.SPEC_ADDRESS);
				
				debug(log,"Start of try Block of processPlace ");
				PlaceManager placeManager = PersistenceHelper.makePlaceManager();
				geographicPlace = placeManager.makeGeographicPlace(GeographicAddress.class);
				debug(log,"After makeGeographicPlace()");
							
				geographicPlace.setSpecification(placeSpec);
				geographicPlace.setName(placeName);
				geographicPlace.setLatitude(latitude);
				geographicPlace.setLongitude(longitude);
				
				UimHelper.setEntityChars(geographicPlace, charMap);
				
				debug(log,"Aftrer calling UimHelper.setEntityChars");
		
				List places = new ArrayList();
				places.add(geographicPlace);
		
				places = placeManager.createGeographicPlace(places);
				debug(log,"Aftrer calling placeManager.createGeographicPlace()");
				geographicPlace = (GeographicPlace) places.get(0);
				
				// Associate place with service.
				if(geographicPlace != null) {
					debug(log,"Associate place with service. ");
					Involvement servicePlaceInvolvement = null;
					servicePlaceInvolvement = PersistenceHelper.makeAttachmentManager().makeRel(PlaceServiceRel.class);
			
					servicePlaceInvolvement.setToEntity(cfs);
					servicePlaceInvolvement.setFromEntity(geographicPlace);
			
					AttachmentManager attachmentManager = PersistenceHelper.makeAttachmentManager();
					attachmentManager.createRel(servicePlaceInvolvement);
				}
			}
			
		}catch(Exception e) {
			log.validationException("Unexpected error while processing Service Address.", new IllegalArgumentException(), "");	
		}
		
		debug(log,"processPlace - END");		
		return geographicPlace;
	}
	
	/**
	 * Create Geographic Place/Address for the Foreign Address.  
	 * 
	 * @param config
	 * @param configItemType
	 * @return
	 * @throws ValidationException
	 */
	@SuppressWarnings("unchecked")
	public static GeographicPlace processForeignPlace(ServiceConfigurationVersion config, ConfigurationItemType configItemType) throws ValidationException {
		debug(log,"processForeignPlace - START");
      
		Service cfs = UimHelper.getCfsService(config);
		String accountId = UimHelper.getEntityCharValue(cfs, Constants.ACCOUNT_ID);
		
		String placeName = null;
		String latitude = null; 
		String longitude = null; 
		String streetVillageName = null; 
		String streetHouseNumber = null; 
		String buildingName = null;
		HashMap<String,String> charMap = new HashMap<>();
		List<ConfigurationItemPropertyType> propList = configItemType.getPropertyList();

		for(ConfigurationItemPropertyType prop: propList){
			String propertyName = prop.getName();
			if(propertyName.equals(Constants.PARAM_LATITUDE)) {
				latitude = prop.getValue();
			} else if(propertyName.equals(Constants.PARAM_LONGITUDE)) {
				longitude = prop.getValue();
			} else if(propertyName.equals(Constants.PARAM_DIVISION)) {
				charMap.put(Constants.PARAM_FDIVISION, prop.getValue());
			} else {
				charMap.put(propertyName, prop.getValue());
			}	
			
			if(propertyName.equals(Constants.PARAM_VILLAGE_NAME)) {
				streetVillageName = prop.getValue();
			}
			if(propertyName.equals(Constants.PARAM_STREET_HOUSE_NUMBER)) {
				streetHouseNumber = prop.getValue();
			}
			if(propertyName.equals(Constants.PARAM_BUILDING_NAME)) {
				buildingName = prop.getValue();
			}
		}	
		
		placeName = accountId + Constants.DELIMITER_PIPE_SEPARATOR + streetVillageName + Constants.DELIMITER_PIPE_SEPARATOR + streetHouseNumber + Constants.DELIMITER_PIPE_SEPARATOR + buildingName;
		
		GeographicPlace geographicPlace = null;
		PlaceSpecification placeSpec = (PlaceSpecification) UimHelper.getSpecification(Constants.SPEC_SERVICEADDRESS);
		try {
			PlaceManager placeManager = PersistenceHelper.makePlaceManager();
			geographicPlace = placeManager.makeGeographicPlace(GeographicAddress.class);
						
			geographicPlace.setSpecification(placeSpec);
			geographicPlace.setName(placeName);
			geographicPlace.setLatitude(latitude);
			geographicPlace.setLongitude(longitude);
			
			UimHelper.setEntityChars(geographicPlace, charMap);
	
			List<GeographicPlace> places = new ArrayList<>();
			places.add(geographicPlace);
	
			places = (ArrayList) placeManager.createGeographicPlace(places);
			geographicPlace = places.get(0);
			
		} catch (Exception e) {
			log.validationException(Constants.ERR_INTERNAL_ERROR, e);
		}
		debug(log,"processForeignPlace - END");		
		return geographicPlace;
	}
}
