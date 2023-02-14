package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.List;

import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.xmlbeans.ParameterType;

/**
 * The EntityDesignAutomator interface is the contract that must be satisfied by an
 * implementation class that provides automated Design & Assign logic for an
 * entity. 
 */
public interface EntityDesignAutomator {

	/* Service Action Codes (Standard) */
	public static final String ACTION_ADD = "Add";
	public static final String ACTION_CHANGE = "Change";
	public static final String ACTION_SUSPEND = "Suspend";
	public static final String ACTION_RESUME = "Resume";
	public static final String ACTION_DISCONNECT = "Disconnect";

	/**
	 * Automate the design of an entity. 
	 * 
	 * @param parentConfigItem
	 *            the service configuration item under design
	 * @param parameters
	 *            entity parameters
	 * @param action
	 *            action on the entity	             
	 * @required parentConfigItem
	 * @required action
	 * @return The updated configuration item.
	 * @throws ValidationException
	 *             report an error that prevents the design from proceeding
	 */
	public InventoryConfigurationItem design(
			InventoryConfigurationItem parentConfigItem,
			List<ParameterType> parameters,
			String action) throws ValidationException;


	/**
	 * This method may be called to validate an entity 
	 * @param parentConfigItem
	 *            the service configuration item for the entity
	 * @throws ValidationException
	 *             report an error that prevents entity from being completed
	 */
	public void validate(InventoryConfigurationItem parentConfigItem)
			throws ValidationException;

}