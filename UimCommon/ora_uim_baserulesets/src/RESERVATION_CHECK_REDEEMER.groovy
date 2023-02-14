

import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.entity.common.EntityConsumer;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;


if(ruleParameters[0] instanceof EntityConsumer && ruleParameters[0] instanceof ConsumableResource)
{
EntityConsumer entityconsumer = ruleParameters[0];
ConsumableResource consumableresource=ruleParameters[1];


log.debug( "", "Rule Reservation Check Redeemer");
boolean returnvalue = checkRedeemer(entityconsumer, consumableresource);
extensionPointRuleContext.setReturnValue(returnvalue);
}

def boolean checkRedeemer(EntityConsumer ec, ConsumableResource cr){
	return true;
}

