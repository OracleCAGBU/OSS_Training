package com.mobile.ci;

import java.util.Collection;

import com.mobile.utils.Constants;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceStatus;
import oracle.communications.inventory.api.entity.common.RootEntity;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

public class GetEntityAction {
	private static final Log log = LogFactory.getLog(GetEntityAction.class);
	
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
	/**
	 * This method will get the entity action for the provided action and BI object based on the Mobile Service specification type.
	 *  
	 * @param action
	 * @return
	 */
	public String getEntityAction(String action, RootEntity entity) throws ValidationException{
		debug("---------------  Entered into GetEntityAction.getEntityAction().");
		String convertedAction = null;
		Service serviceCfs = null;
		boolean isInserviceForChange = true;
		try{
			if(entity instanceof Service){
				serviceCfs = (Service) entity;
				String serviceId = serviceCfs.getId();
				debug("Before Service Id: " + serviceId + " Admin State: " + serviceCfs.getAdminState());
				if(!Utils.checkNull(serviceId)){
					Finder finder = PersistenceHelper.makeFinder();
					Collection<Service> serviceList = finder.findById(Service.class, serviceId);
					if(!Utils.isEmpty(serviceList)) {
						serviceCfs = serviceList.iterator().next();
						finder.close();
						isInserviceForChange = !Utils.checkNull(serviceCfs)? serviceCfs.getAdminState().equals(ServiceStatus.IN_SERVICE):true;
					}
				}
			}
			debug("isInserviceForChange : "+isInserviceForChange);
			debug("Received action : " + action);
			debug("Constants.SA_CHANGE : " + Constants.SA_CHANGECPE + " " +  Constants.SA_CHANGE + " " + Constants.SA_CHANGEUPLOADSPEED);
			if (action != null && action.length() > 0){
				debug("Action not null " + action);

				if ( action.equals(Constants.SA_CREATE) ) 
					convertedAction = Constants.SA_CREATE;
				else if ( action.equals(Constants.SA_CHANGE) || action.equals(Constants.SA_CHANGEMSISDN) || action.equals(Constants.SA_CHANGEPAYTYPE) )
					convertedAction = Constants.SA_CHANGE;
				else if ( action.equals(Constants.SA_RESUME) ) 
					convertedAction = Constants.SA_RESUMEWITHCONFIGURATION;
				else if( action.equals(Constants.SA_SUSPEND)) 
					convertedAction = Constants.SA_SUSPENDWITHCONFIGURATION;
				else if ( action.equals(Constants.SA_DISCONNECT) )
					convertedAction = Constants.SA_DISCONNECT;
				else
					convertedAction = "no_action";
				debug("Converted Action : " + convertedAction);
			}else{
				convertedAction = "no_action";
			}

			debug("---------------  Exited into GetEntityAction.getEntityAction().");
		}catch(Exception e){
			log.validationError(e.getMessage());
		}
		return convertedAction; 
	}
}

