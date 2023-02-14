/*
 * # Name:         VALIDATE_RELATE_PLACES
 * # Description:  Performs validation of the Relate Places
 */
import oracle.communications.inventory.api.entity.GeographicPlace;
import oracle.communications.inventory.api.entity.PlaceSpecification;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.specification.SpecManager;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.platform.persistence.PersistenceHelper;


log.debug ("", "Validate Relate Places");
Binding binding=this.getBinding();
Map<String,Object> map= binding.getVariables();

if(map.get("extensionPointRuleContext")!=null)
{
ExtensionPointRuleContext extensionPointRuleContext=binding.getVariable("extensionPointRuleContext");
GeographicPlace child = (GeographicPlace)extensionPointRuleContext.getArguments()[0];
GeographicPlace parent = (GeographicPlace)extensionPointRuleContext.getArguments()[1];
String methodName = extensionPointRuleContext.getMethodName();
String targetName = extensionPointRuleContext.getDeclaringTargetType().getSimpleName();

if(parent!= null&&child!= null)
{
	PlaceSpecification parentSpecification = parent.getSpecification();
	PlaceSpecification childSpecification = child.getSpecification();

	SpecManager specManager = PersistenceHelper.makeSpecManager();
	if(specManager != null)
	{
		List<Specification> specifications = specManager.getRelatedSpecs(parentSpecification, null, parentSpecification.getClass(), false);
		if(specifications != null)
		{
			if(specifications.contains(childSpecification))
			{
				log.info("Vlaidation sucessfull");
			}
			else
			{
				log.error("place.ValidateRelatePlaces"+child.getId(),parent.getId(),childSpecification.getName(),parentSpecification.getName());
			}
		}
	}
}
}



