package oracle.communications.ordermanagement.orchestration.transformation.sequence;

import java.util.EnumSet;

import oracle.communications.ordermanagement.orchestration.transformation.OrderItemRef;
import oracle.communications.ordermanagement.orchestration.transformation.RelationshipHandler;
import oracle.communications.ordermanagement.orchestration.transformation.SpecialPropertyMapping;
import oracle.communications.ordermanagement.orchestration.transformation.TransformationException;
import oracle.communications.ordermanagement.orchestration.transformation.sequence.TransformationContext.Modifiers;

public class PrimaryRelationshipHandler extends
		CreateNewTargetRelationshipHandler implements RelationshipHandler, SpecialPropertyMapping {
	
	 private static final EnumSet<Modifiers> MODIFIERS = EnumSet.of(Modifiers.MAP_ORDER_ITEM_ID, Modifiers.MAP_ORDER_ITEM_NAME,
             Modifiers.MAP_FULFILLMENT_PATTERN_MAPPING);

     @Override
     public EnumSet<Modifiers> getModifiers() {
         return MODIFIERS;
     }

     @Override
     public String mapSpecialProperty(TransformationContext transformationContext, Modifiers modifier, String sourceValue,
             OrderItemRef source, OrderItemRef target) throws TransformationException {
         String result = sourceValue;
         switch (modifier) {
         case MAP_FULFILLMENT_PATTERN_MAPPING:
             // if no namespace in value, use namespace of source order item
             if (sourceValue != null && !sourceValue.contains("{")) {
                 final String sourceName = sourceValue.substring(sourceValue.indexOf('}') + 1);
                 final String sourceNamespace = source.getOrderItemSpecRef().substring(0, source.getOrderItemSpecRef().indexOf('}') + 1);
                 result = sourceNamespace + sourceName;
             }
             break;

         case MAP_ORDER_ITEM_ID:
             result = "CSO_" + sourceValue;
             break;
         case MAP_ORDER_ITEM_NAME:
         	 if(sourceValue.contains("Broadband"))
         		 result="Broadband_CFS";
         	 break;
         }
         return result;
     }

}
