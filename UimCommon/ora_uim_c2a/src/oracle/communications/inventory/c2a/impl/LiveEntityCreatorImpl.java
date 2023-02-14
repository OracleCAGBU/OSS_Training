package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.InventoryState;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.framework.security.impl.UserEnvironmentFactory;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.account.LogicalDeviceAccountManager;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.c2a.DesignManager;
import oracle.communications.inventory.c2a.LiveEntityCreator;
import oracle.communications.platform.persistence.PersistenceHelper;

public class LiveEntityCreatorImpl implements LiveEntityCreator{

	private static Log log = LogFactory.getLog(DesignManager.class);
	
	public LogicalDeviceAccount createLiveDeviceAccount(LogicalDeviceAccount account)
			throws ValidationException {

		boolean isBIContext = false;
		BusinessInteractionManager biMgr = PersistenceHelper
				.makeBusinessInteractionManager();
		BusinessInteraction currentBI = 
				(BusinessInteraction) UserEnvironmentFactory.getBusinessInteraction();
		try {

			if (account == null)
				return null;

			LogicalDeviceAccount e = null;
			BusinessInteraction newBI = null;

			if (currentBI != null) {
				isBIContext = true;
				// Switch to live
				biMgr.switchContext(newBI, null);
			}
			LogicalDeviceAccountManager manager = PersistenceHelper
					.makeLogicalDeviceAccountManager();
			List<LogicalDeviceAccount> list = new ArrayList<LogicalDeviceAccount>();
			list.add(account);
			list = manager.createLogicalDeviceAccounts(list);
			if (!Utils.isEmpty(list))
				e = list.get(0);
			return e;
		} finally {
			if (isBIContext)
				biMgr.switchContext(currentBI, null);
			RequestPolicyHelper.checkPolicy();
		}
	}

	public Service createLiveService(Service service)
			throws ValidationException {

		boolean isBIContext = false;
		BusinessInteractionManager biMgr = PersistenceHelper
				.makeBusinessInteractionManager();
		BusinessInteraction currentBI = (BusinessInteraction) UserEnvironmentFactory
				.getBusinessInteraction();
		try {

			if (service == null)
				return null;

			Service e = null;

			BusinessInteraction newBI = null;

			if (currentBI != null) {
				isBIContext = true;
				// Switch to live
				biMgr.switchContext(newBI, null);
			}
			List<Service> list = new ArrayList<Service>();
			list.add(service);
			list = PersistenceHelper.makeServiceManager().createService(list);
			if (!Utils.isEmpty(list))
				e = list.get(0);
			return e;
		} finally {
			if (isBIContext)
				biMgr.switchContext(currentBI, null);
			RequestPolicyHelper.checkPolicy();
		}
	}
	
	public LogicalDevice createLiveDevice(LogicalDevice device)
			throws ValidationException {

		boolean isBIContext = false;
		BusinessInteractionManager biMgr = PersistenceHelper
				.makeBusinessInteractionManager();
		BusinessInteraction currentBI = (BusinessInteraction) UserEnvironmentFactory
				.getBusinessInteraction();
		try {

			if (device == null)
				return null;

			LogicalDevice e = null;

			BusinessInteraction newBI = null;

			if (currentBI != null) {
				isBIContext = true;
				// Switch to live
				biMgr.switchContext(newBI, null);
			}
			LogicalDeviceManager manager = PersistenceHelper
					.makeLogicalDeviceManager();
			List<LogicalDevice> list = new ArrayList<LogicalDevice>();
			list.add(device);
			list = manager.createLogicalDevice(list);
			if (!Utils.isEmpty(list))
				e = list.get(0);
			return e;
		} finally {
			if (isBIContext)
				biMgr.switchContext(currentBI, null);
			RequestPolicyHelper.checkPolicy();
		}
	}
	
	public void setLogicalDeviceState(LogicalDevice resource, InventoryState state) 
			throws ValidationException {

		boolean isBIContext = false;
		BusinessInteraction newBI = null;
		BusinessInteractionManager biMgr = 
				PersistenceHelper.makeBusinessInteractionManager();
		BusinessInteraction currentBI = 
				(BusinessInteraction) UserEnvironmentFactory.getBusinessInteraction();
		try {
			if (currentBI != null) {
				isBIContext = true;				
				biMgr.switchContext(newBI, null);
			}
			resource.setAdminState(state);
				
			Collection<LogicalDevice> devs = new ArrayList<LogicalDevice>();
			devs.add(resource);
			PersistenceHelper.makeLogicalDeviceManager().updateLogicalDevice(devs);
		} catch (Exception e) {
			log.validationException("c2a.failedtoUpdate",
					new java.lang.IllegalArgumentException(),
					resource.getName());			
		} finally {
			if (isBIContext) {
				biMgr.switchContext(currentBI, null);
			}
			RequestPolicyHelper.checkPolicy();
		}	
	}
}
