/* # Name:         TRAIL_PIPE_TOPOLOGY_EDGE
 * # Description:  Provides a mechanism for a trail pipe to become a topology edge.
 */
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;


log.debug ("", "Trail Pipe Topology Edge");

Binding binding=this.getBinding();
Map<String,Object> map= binding.getVariables();

if(map.get("extensionPointRuleContext")!=null)
{
ExtensionPointRuleContext extensionPointRuleContext=binding.getVariable("extensionPointRuleContext");
extensionPointRuleContext.setReturnValue(true);
}

