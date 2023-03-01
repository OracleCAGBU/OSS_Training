package oracle.communications.inventory.techpack.ewo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.businessinteraction.BusinessInteractionSearchCriteria;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionChar;
import oracle.communications.inventory.api.entity.BusinessInteractionSpec;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.designnassign.jms.DesignNAssignJMSClient;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

public class EWODesigner {
    
	private static Log log = LogFactory.getLog(DesignNAssignJMSClient.class);
    private static final String SPEC_EWO = "Engineering Work Order";
    
    private BusinessInteractionManager biManager = PersistenceHelper.makeBusinessInteractionManager();
    
    /**
	 * Log the debug data values in the debug log if the log is enabled. 
	 * 
	 * @param objects
	 */
	
	private void debug(Object ...objects) {
		try {
			if (log.isDebugEnabled()) {
				StringBuilder sb=new StringBuilder();
				for(Object obj:objects) {
					sb.append(obj);
				}
				log.debug("",sb.toString());
			}
		} catch (Exception e) {
			log.debug("", "Error Message :"+e.getMessage());
		}
	}
	
    public EWODesigner() {

    }
    
    public BusinessInteraction createEWO(String name) throws ValidationException{
        debug(log,"#### createEWO START");

        Finder finder = PersistenceHelper.makeFinder();
        
        try{
            BusinessInteraction bi = biManager.makeBusinessInteraction();
            BusinessInteractionSpec biSpec = finder.findByName(BusinessInteractionSpec.class, SPEC_EWO).iterator().next();
        
            bi.setSpecification(biSpec);
            bi.setName(name);
            bi.setEffDate(new Date());
            
            //Set the JMS Correlation ID on the External Object Id for now. 
            //An alternative approach would be to create a new characteristic for managing the
            //correlationId
            String correlationId = DesignHelper.makeDesignManager().getJMSCorrelationId();
            bi.setExternalObjectId(correlationId);

            CharacteristicSpecification charSpec = PersistenceHelper.makeCharacteristicManager().getCharacteristicSpecification(biSpec, "desiredDueDate");
            BusinessInteractionChar charValue = bi.makeCharacteristicInstance();
            charValue.setCharacteristicSpecification((CharacteristicSpecification)charSpec.connect());
            charValue.setName(charSpec.getName());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            charValue.setValue(df.format(new Date()));
            charValue.setLabel(charSpec.getName());
            bi.getCharacteristics().add(charValue);
                                                                                
            charSpec = PersistenceHelper.makeCharacteristicManager().getCharacteristicSpecification(biSpec, "controlsConfigurationLifeCycle");
            BusinessInteractionChar charValuecclc = bi.makeCharacteristicInstance();
            charValuecclc.setCharacteristicSpecification((CharacteristicSpecification)charSpec.connect());
            charValuecclc.setName(charSpec.getName());
            charValuecclc.setValue("true");
            charValuecclc.setLabel(charSpec.getName());
            bi.getCharacteristics().add(charValuecclc);                                                                                    
                                                                                
            Collection biList = new ArrayList();
            biList.add(bi);
            List <BusinessInteraction> bis = biManager.createBusinessInteractions(biList);
            biManager.flushTransaction();
            
            bi = bis.get(0);

            return bi;

        }finally{
            debug(log,"#### createEWO END");
            finder.close();
        }

    }
    
    public boolean isPendingEWOExist(String name){
		debug(log,"isPendingEWOExist - START");

    	boolean isExist = false;
    	Finder finder = PersistenceHelper.makeFinder();
    	BusinessInteractionSpec biSpec = finder.findByName(BusinessInteractionSpec.class, SPEC_EWO).iterator().next();
    	BusinessInteractionSearchCriteria searchCriteria = biManager.makeBusinessInteractionSearchCriteria();
    	CriteriaItem biName = searchCriteria.makeCriteriaItem();
    	biName.setOperator(CriteriaOperator.CONTAINS);
    	biName.setValue(name);
    	searchCriteria.setName(biName);
    	searchCriteria.setBusinessInteractionSpecification(biSpec);
    	CriteriaItem adminState = searchCriteria.makeCriteriaItem();
    	adminState.setOperator(CriteriaOperator.EQUALS_IGNORE_CASE);
    	adminState.setValue("IN_PROGRESS");
    	searchCriteria.setAdminState(adminState);
    	List<BusinessInteraction> bis = biManager.findBusinessInteraction(searchCriteria);
    	if(!bis.isEmpty())
    		isExist = true;
		debug(log,"isPendingEWOExist - END");
    	return isExist;
    }
    
    public BusinessInteraction getEWOByName(String name){
		debug(log,"getEWOByName - START");

    	BusinessInteraction ewo = null;
    	Finder finder = PersistenceHelper.makeFinder();
    	BusinessInteractionSpec biSpec = finder.findByName(BusinessInteractionSpec.class, SPEC_EWO).iterator().next();
    	BusinessInteractionSearchCriteria searchCriteria = biManager.makeBusinessInteractionSearchCriteria();
    	CriteriaItem biName = searchCriteria.makeCriteriaItem();
    	biName.setOperator(CriteriaOperator.BEGINS_WITH);
    	biName.setValue(name);
    	searchCriteria.setName(biName);
    	searchCriteria.setBusinessInteractionSpecification(biSpec);
    	List<BusinessInteraction> bis = biManager.findBusinessInteraction(searchCriteria);
    	if(!bis.isEmpty()){
    		//TODO Assuming there will only be one BI with this name.
    		ewo = bis.get(0);
    	}
		debug(log,"getEWOByName - END");
    	return ewo;
    }
    
	public void setManualDesignComponent(String componentName) {
		debug(log,"setManualDesignComponent - START");

		if(componentName!=null){
			if(DesignNAssignJMSClient.getManualDesignComponent()!=null)
				DesignNAssignJMSClient.setManualDesignComponent(componentName+"#"+DesignNAssignJMSClient.getManualDesignComponent());
			else
				DesignNAssignJMSClient.setManualDesignComponent(componentName);
		}
		else
			DesignNAssignJMSClient.setManualDesignComponent(componentName);
		debug(log,"setManualDesignComponent - END");

	}

	public String getManualDesignComponent(String designerClassName) throws ValidationException {
		debug(log,"getManualDesignComponent - START");

		String componentName = null;
		String resumeComponentString = DesignNAssignJMSClient.getManualDesignComponent();
		if (resumeComponentString != null) {
			String[] components = resumeComponentString.split("#");
			for (String component : components) {
				if (component.contains(designerClassName)) {
					StringTokenizer tkns = new StringTokenizer(component);
					while (tkns.hasMoreTokens())
						componentName = tkns.nextToken(".");
				}
			}
		}
		debug(log,"getManualDesignComponent - END");

		return componentName;
	}
	
	public void startManualDesign(BusinessInteraction currentBi, String ewoName) throws ValidationException {
		debug(log,"startManualDesign - START");

		BusinessInteractionManager biManager = PersistenceHelper.makeBusinessInteractionManager();
		DesignManager designManager = DesignHelper.makeDesignManager();
		BusinessInteraction ewo = this.getEWOByName(ewoName);
		if (ewo == null) {
			try {
				ewo = this.createEWO(ewoName);
				// Associating this EWO as child of service order BI
				BusinessInteraction parentBi = designManager.getTopLevelParentBusinessInteraction(currentBi);
				biManager.addChildBusinessInteraction(parentBi, ewo);

				// Reset manual design component on creation of any new manual
				// BI
				this.setManualDesignComponent(null);
			} catch (ValidationException e) {
				throw e;
			}
		}
		biManager.switchContext(ewo, null);
		debug(log,"startManualDesign - END");
	}
	
}
