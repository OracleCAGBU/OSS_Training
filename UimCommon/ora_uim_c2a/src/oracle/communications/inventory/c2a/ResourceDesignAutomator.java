package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.List;

import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.xmlbeans.ParameterType;

/**
 * The Resource DesignAutomator interface is the contract that must be satisfied by an
 * implementation class that provides automated Design & Assign logic for a
 * resource. 
 */
public interface ResourceDesignAutomator {

	/* Service Action Codes (Standard) */
	public static final String RESOURCEACTION_ADD = "Add";
	public static final String RESOURCEACTION_CHANGE = "Change";
	public static final String RESOURCEACTION_SUSPEND = "Suspend";
	public static final String RESOURCEACTION_RESUME = "Resume";
	public static final String RESOURCEACTION_DISCONNECT = "Disconnect";

	/**
	 * Automate the design of a resource. 
	 * 
	 * @param parentConfigItem
	 *            the service configuration item under design
	 * @param parameters
	 *            resource parameters
	 * @param resourceAction
	 *            resource action	             
	 * @required parentConfigItem
	 * @required resourceAction
	 * @return The updated configuration item.
	 * @throws ValidationException
	 *             report an error that prevents the design from proceeding
	 */
	public ServiceConfigurationItem design(
			ServiceConfigurationItem parentConfigItem,
			List<ParameterType> parameters,
			String resourceAction) throws ValidationException;


	/**
	 * This method may be called to validate a resource 
	 * @param parentConfigItem
	 *            the service configuration item for the resource
	 * @throws ValidationException
	 *             report an error that prevents resource from being completed
	 */
	public void validate(ServiceConfigurationItem parentConfigItem)
			throws ValidationException;

}