package oracle.communications.inventory.c2a;
/*
REPLACE_COPYRIGHT_HERE
*/
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;

/**
 * The DesignAutomator interface is the contract that must be satisfied by an
 * implementation class that provides automated Design & Assign logic for a
 * service. The methods on this interface represent event handlers corresponding
 * to the events that are relevant to the Design & Assign function within the
 * life cycle of a service order.
 */
public interface DesignAutomator {

	/* Service Action Codes (Standard) */
	public static final String SERVICEACTION_ADD = "Add";
	public static final String SERVICEACTION_CHANGE = "Change";
	public static final String SERVICEACTION_SUSPEND = "Suspend";
	public static final String SERVICEACTION_RESUME = "Resume";
	public static final String SERVICEACTION_DISCONNECT = "Disconnect";
	public static final String SERVICEACTION_MOVE = "Move";

	/* Service Action Codes (Non-standard synonyms) */
	public static final String SERVICEACTION_CREATE = "Create";
	public static final String SERVICEACTION_MODIFY = "Modify";
	public static final String SERVICEACTION_REMOVE = "Remove";

	/**
	 * Automate the design of a service configuration. This method should be
	 * called from a ruleset on the service configuration specification instead
	 * of BaseConfigurationManager_automateConfiguration.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @param orderItem
	 *            the order item that carries the action being requested. If
	 *            orderItem is null, it means that this method is called from
	 *            the user interface.
	 * @required config
	 * @required orderItem
	 * @return The updated service configuration.
	 * @throws ValidationException
	 *             report an error that prevents the design from proceeding
	 */
	public ServiceConfigurationVersion design(
			ServiceConfigurationVersion config,
			BusinessInteractionItemType orderItem) throws ValidationException;

	/**
	 * This method will be called before the service configuration is canceled.
	 * This method should implement any logic for cleaning up resources and
	 * resource-facing services that are bound to the life cycle of the service,
	 * if the service itself is being canceled. This method should be called
	 * from a ruleset on the service configuration specification after
	 * BaseConfigurationManager_cancelConfigurationVersion.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @throws ValidationException
	 *             report an error that prevents the configuration from
	 *             transitioning to canceled
	 */
	public void cancel(ServiceConfigurationVersion config)
			throws ValidationException;

	/**
	 * This method will be called before the service configuration is issued.
	 * This method should implement any logic for validating the configuration,
	 * to ensure that all the design constraints are satisfied, before work is
	 * performed to implement the configuration in the network. This method
	 * should be called from a ruleset on the service configuration
	 * specification before BaseConfigurationManager_issueConfigurationVersion.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @throws ValidationException
	 *             report an error that prevents the configuration from
	 *             transitioning to issued
	 */
	public void issue(ServiceConfigurationVersion config)
			throws ValidationException;

	/**
	 * This method will be called before the service configuration is completed.
	 * This method should implement any logic for validating the configuration,
	 * to ensure that all the output parameters have been updated after the work
	 * has been performed to implement the configuration in the network. This
	 * method should be called from a ruleset on the service configuration
	 * specification before
	 * BaseConfigurationManager_completeConfigurationVersion.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @throws ValidationException
	 *             report an error that prevents the configuration from
	 *             transitioning to completed
	 */
	public void beforeComplete(ServiceConfigurationVersion config)
			throws ValidationException;

	/**
	 * This method will be called after the service configuration is completed.
	 * This method should implement any logic for cleaning up resources and
	 * resource-facing services that are bound to the life cycle of the service,
	 * if the service itself has been disconnected. This method should be called
	 * from a ruleset on the service configuration specification after
	 * BaseConfigurationManager_completeConfigurationVersion.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @throws ValidationException
	 *             report an error that prevents the configuration from
	 *             transitioning to completed
	 */
	public void complete(ServiceConfigurationVersion config)
			throws ValidationException;

	/**
	 * This method will be called to implement the suspend action. When the
	 * desired behavior is to suspend without a configuration, no "instead"
	 * ruleset extension point should be used, and this method will not be
	 * called. The standard behavior is to suspend with a configuration, and
	 * this method should be invoked by a ruleset to create a service
	 * configuration, if one is not already under design (in progress). This
	 * method should be called from a ruleset on the service configuration
	 * specification instead of ServiceManager_suspendService.
	 * 
	 * @param service
	 *            the service being suspended
	 * @throws ValidationException
	 *             report an error that prevents the design from proceeding
	 */
	public void suspend(Service service) throws ValidationException;

	/**
	 * /** This method will be called to implement the resume action. When the
	 * desired behavior is to resume without a configuration, no "instead"
	 * ruleset extension point should be used, and this method will not be
	 * called. The standard behavior is to resume with a configuration, and this
	 * method should be invoked by a ruleset to create a service configuration,
	 * if one is not already under design (in progress). This method should be
	 * called from a ruleset on the service configuration specification instead
	 * of ServiceManager_resumeService.
	 * 
	 * @param service
	 *            the service being resumed
	 * @throws ValidationException
	 *             report an error that prevents the design from proceeding
	 */
	public void resume(Service service) throws ValidationException;

	/**
	 * This method may be called to validate a service configuration before
	 * various state transitions of the service configuration (approve, issue,
	 * complete). This method should implement any logic for checking design
	 * constraints. This method may be called directly from a ruleset on the
	 * service configuration specification before
	 * BaseConfigurationManager_approveConfigurationVersion.
	 * 
	 * @param config
	 *            the service configuration under design
	 * @throws ValidationException
	 *             report an error that prevents the state transition from
	 *             proceeding
	 */
	public void validate(ServiceConfigurationVersion config)
			throws ValidationException;

}
