package com.broadband.ci;

import java.util.HashMap;
import java.util.List;

import com.broadband.utils.Constants;
import com.broadband.utils.UimHelper;

import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.impl.DesignHelper;
import oracle.communications.inventory.techpack.common.ServiceManager;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.ParameterType;

public class BroadbandCfsDesigner extends BaseDesigner {
	
	private static final Log log = LogFactory.getLog(BroadbandCfsDesigner.class);	
	
	@Override 
	protected ServiceConfigurationVersion designAdd(ServiceConfigurationVersion config, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designAdd - START");
		
		DesignManager designManager = DesignHelper.makeDesignManager();
		
		List<ParameterType> parameters = orderItem.getParameterList();
		HashMap<String, String> paramMap = UimHelper.getParamMap(parameters);
		
		debug(log,"config : " + config);
		debug(log,"config.getService() : " + config.getService());

		if(serviceAction.equals(Constants.SA_CREATE)) {

			debug(log,"partyName : " + paramMap.get(Constants.PARAM_PARTYNAME));
			/*PARAM_PARTY*/
			designManager.relateCustomerToService(paramMap.get(Constants.PARAM_PARTYNAME),config, Constants.PARAM_PARTY, Constants.PARAM_PARTYROLE );
			designManager.addUpdateServiceCharacteristic(config.getService(), Constants.PARAM_PAYTYPE, paramMap.get(Constants.PARAM_PAYTYPE).toString());
			
		}
		
		//Process Service Order
		super.designAdd(config, orderItem);

		debug(log,"designAdd - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return  serviceManager.updateServiceConfigurationVersion(config);
	}
	
	@Override 
	protected ServiceConfigurationVersion designChange(ServiceConfigurationVersion config, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designChange - START");

		//Process Service Order
		super.designChange(config, orderItem);
		
		debug(log,"designChange - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(config);
	}
	@Override 
	protected ServiceConfigurationVersion designDisconnect(ServiceConfigurationVersion config, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designDisconnect - START");
		
		//Process Service Order
		super.designDisconnect(config, orderItem);
		
		debug(log,"designDisconnect - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(config);
	}
	@Override 
	protected ServiceConfigurationVersion designSuspend(ServiceConfigurationVersion config, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designSuspend - START");
		
		//Process Service Order
		super.designSuspend(config, orderItem);
		
		debug(log,"designSuspend - END");		
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(config);
	}
	@Override 
	protected ServiceConfigurationVersion designResume(ServiceConfigurationVersion config, BusinessInteractionItemType orderItem) throws ValidationException { 
		debug(log,"designResume - START");
		
		//Process Service Order
		super.designResume(config, orderItem);
				
		debug(log,"designResume - END");
		ServiceManager serviceManager = CommonHelper.makeServiceManager();
		return serviceManager.updateServiceConfigurationVersion(config);
	}
	
}
