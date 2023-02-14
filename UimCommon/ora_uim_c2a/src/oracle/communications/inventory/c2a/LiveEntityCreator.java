package oracle.communications.inventory.c2a;
import oracle.communications.inventory.api.entity.InventoryState;
/*
REPLACE_COPYRIGHT_HERE
*/
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceAccount;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.exception.ValidationException;

public interface LiveEntityCreator {

	LogicalDeviceAccount createLiveDeviceAccount(LogicalDeviceAccount account)
			throws ValidationException;
	
	LogicalDevice createLiveDevice(LogicalDevice device)
			throws ValidationException;
	
	Service createLiveService(Service service)
			throws ValidationException;
	
	void setLogicalDeviceState(LogicalDevice resource, InventoryState state) 
			throws ValidationException;
}
