package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionItemSearchCriteria;
import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.businessinteraction.BusinessInteractionSearchCriteria;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionItem;
import oracle.communications.inventory.api.entity.CharacteristicSpecUsage;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.xmlbeans.ConfigurationStateEnum;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.PersistenceHelper;

public class ConfigurationUtils {
	
	private static Log log = LogFactory.getLog( ConfigurationUtils.class );
	
	/**
	 * Given a biID and an external object id, find the business interaction for this id and return all configuration associated with this BI.
	 * @param biId
	 * @param extObjId
	 * @return
	 * @throws ValidationFaultType
	 */
	public static List<ServiceConfigurationVersion> getAssociatedVersions(String biId, String extObjId) throws ValidationException{
  	  List<ServiceConfigurationVersion> versions = new  ArrayList<ServiceConfigurationVersion>();
  	  BusinessInteraction businessInteraction = getBusinessInteraction(biId, extObjId);
  	  if( businessInteraction == null){
  		 log.validationError( "getAssociatedVersions error: Could not find BI" );
  		 return versions;
  	  }
  	  
  	  Collection<BusinessInteraction> allBis = new ArrayList<BusinessInteraction>();
  	  populateAllChildBI(businessInteraction, allBis);
  	  
		for (BusinessInteraction thisBi : allBis) {
			Collection<BusinessInteractionItem> biItems = getBusinessInteractionItems(thisBi);

			for (BusinessInteractionItem biItem : biItems) {
				Object referredEntity = biItem.getToEntity();
				if (referredEntity != null
						&& referredEntity instanceof ServiceConfigurationVersion) {
					ServiceConfigurationVersion config = (ServiceConfigurationVersion) referredEntity;
					versions.add(config);
				}
			}
		}
		
	 if( versions.isEmpty()){
  		 log.validationError( "getAssociatedVersions error: Could not find scv for BI" );
  		 
  	  }
		
  	  return versions;
    }
	
	/**
	 * Given a biID and an external object id, find the business interaction for this id and return the Service associated with this BI.
	 * This is for C2A D&A algorithms, where it is assumed to have one Service per BI
	 * @param biId
	 * @param extObjId
	 * @return
	 * @throws ValidationFaultType
	 */
	public static Service getAssociatedService(BusinessInteraction businessInteraction) throws ValidationException{
		Service service = null;
		ServiceConfigurationVersion version = null;
		if( businessInteraction == null){
			log.validationError( "getAssociatedVersions error: Could not find BI" );
			return service;
		}  	    	 
		
		Collection<BusinessInteractionItem> biItems = getBusinessInteractionItems(businessInteraction);
		for (BusinessInteractionItem biItem : biItems) {
			Object referredEntity = biItem.getToEntity();
			if (referredEntity != null && 
					referredEntity instanceof ServiceConfigurationVersion) {
				version = (ServiceConfigurationVersion) referredEntity;
			}
		}		
		
		if(version == null || version.equals(null)){
			log.validationError( "getAssociatedVersions error: Could not find a scv for BI" );  		 
		} else {
			service = version.getService();
		}
		return service;
    }
    
	/**
	 * Given a configuration, this method returns its previous non cancelled configuration.
	 * @param version
	 * @return
	 */
    public static ServiceConfigurationVersion getPreviousVersion(ServiceConfigurationVersion version){
    	ServiceConfigurationVersion prevVersion = version.getPreviousConfiguration();
    	if( version.getPreviousConfiguration() != null && version.getPreviousConfiguration().getAdminState().equals(ConfigurationStateEnum.CANCELLED)){
    		prevVersion = getPreviousVersion(prevVersion);
     	}
    	return prevVersion;
    }
    
    
    /**
     * Given a BusinessInteraction returns a Collection of all its items.
     * @param bi
     * @return
     */
    public static Collection<BusinessInteractionItem> getBusinessInteractionItems(BusinessInteraction bi ) 
        {
            Collection<BusinessInteractionItem> result = new ArrayList<BusinessInteractionItem>();
            BusinessInteractionManager bim = PersistenceHelper.makeBusinessInteractionManager();
            BusinessInteractionItemSearchCriteria criteria = bim.makeBusinessInteractionItemSearchCriteria();
            CriteriaItem cItem = criteria.makeCriteriaItem();
            cItem.setValue( bi );
            cItem.setOperator( CriteriaOperator.EQUALS );
            criteria.setBusinessInteraction( cItem );
            long i = 0;
            int pageSize = 100;
            Collection<BusinessInteractionItem> items = null;
            do
            {
                criteria.setRange( i * pageSize, ( ++i * pageSize ) - 1 );
                items = bim.findBusinessInteractionItem( criteria );
                result.addAll( items );
            } while( items.size() > 0 );
            return result;
        }


     
    
    /**
     * Find the Business interaction given an id or an external object id. This method will first look for a Business interaction with that id,
     * if no such BI exists,then it will use the external Object id as the criteria. 
     * @param biId
     * @param extObjId
     * @return
     * @throws ValidationFaultType
     */
    public static BusinessInteraction getBusinessInteraction( String biId , String extObjId)
            throws ValidationException
        {
            BusinessInteractionManager biMgr = PersistenceHelper.makeBusinessInteractionManager();
            BusinessInteraction bi = null;
            
            if ( Utils.isEmpty( biId ) && Utils.isEmpty( extObjId ) )
            {
                log.fatal("businessInteraction.noBIidOrExtBiId");
            }
            
            if ( !Utils.isEmpty( biId ) )
            {
                BusinessInteractionSearchCriteria biSearch = biMgr.makeBusinessInteractionSearchCriteria();
                CriteriaItem crit = biSearch.makeCriteriaItem();
                crit.setOperator( CriteriaOperator.EQUALS );
                crit.setValue( biId );
                biSearch.setId( crit );
                List<BusinessInteraction> results = biMgr.findBusinessInteraction( biSearch );
                
                if( Utils.isEmpty(results) )
                {
                    log.fatal( "businessInteraction.biNotFound", biId );
                }
                else
                {
                    bi = results.iterator().next();
                }
            }
            else
            {
                if ( !Utils.isEmpty( extObjId ) )                   
                {
                    BusinessInteractionSearchCriteria biSearch = biMgr.makeBusinessInteractionSearchCriteria();
                    CriteriaItem crit = biSearch.makeCriteriaItem();
                    crit.setOperator( CriteriaOperator.EQUALS );
                    crit.setValue( extObjId );
                    biSearch.setExternalObjectId( crit );
                    List<BusinessInteraction> results = biMgr.findBusinessInteraction( biSearch );                    
                    if( Utils.isEmpty(results) )
                    {
                        log.fatal( "businessInteraction.noBIidOrExtBiId", extObjId );
                    }
                    else
                    {
                        if ( results.size() > 1 )
                        {
                            StringBuffer sb = new StringBuffer();
                            for ( BusinessInteraction b : results) {
                                sb.append(b.getId()).append(", ");
                            }
                            sb.delete( sb.length()-1, sb.length() );
                            log.fatal( "businessInteraction.biFoundFoundMultiple", sb.toString(), extObjId );
                        }
                        bi = results.iterator().next();
                    }
                }
            }            
            return bi;
        }
    
    
    private static void  populateAllChildBI(BusinessInteraction bi, Collection<BusinessInteraction> bis){
    	
    	bis.add(bi);
    	if( bi.getChildBusinessInteractions() == null || bi.getChildBusinessInteractions().isEmpty())
    		return ;    	
    	else{
    		for(BusinessInteraction childBI : bi.getChildBusinessInteractions()) {
    			populateAllChildBI(childBI, bis);
    		}
    	}    	
    }

	public static ServiceConfigurationItem getConfigurationItem(ServiceConfigurationVersion svcConVers, String name) {
		List<ServiceConfigurationItem> items = svcConVers.getConfigItems();
		for (ServiceConfigurationItem item : items) {
			if (name.equalsIgnoreCase(item.getName())) {
				return item;
			}
		}
		return null;
	}

	public static String getConfigurationProperty(InventoryConfigurationItem item, String name) {
		Set<? extends CharValue> chars = item.getCharacteristics();
		for (CharValue c : chars) {
			if (name.equalsIgnoreCase(c.getName())) {
				return c.getValue();
			}
		}
		return null;
	}

	public static boolean isValidConfigItemCharacteristic(ServiceConfigurationItem item, String paramName) {
		Specification configItemSpec = item.getSpecification();
		Set<CharacteristicSpecUsage> propertySpecs = configItemSpec.getCharacteristicSpecUsages();
		for (CharacteristicSpecUsage c : propertySpecs) {
			CharacteristicSpecification charSpec = c.getCharacteristicSpecification();
			if (paramName.equals(charSpec.getName()))
				return true;
		}
		return false;
	}
}
