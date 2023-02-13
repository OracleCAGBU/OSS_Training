package oracle.communications.ordermanagement.orchestration.transformation.sequence;

import java.util.Collection;
import java.util.EnumSet;

import oracle.communications.ordermanagement.orchestration.transformation.OrderItemRef;
import oracle.communications.ordermanagement.orchestration.transformation.RelationshipHandler;
import oracle.communications.ordermanagement.orchestration.transformation.TransformationException;
import oracle.communications.ordermanagement.orchestration.transformation.rule.MappingRuleRef;
import oracle.communications.ordermanagement.orchestration.transformation.sequence.TransformationContext.Modifiers;

public class CreateNewTargetRelationshipHandler implements RelationshipHandler {

	@Override
	public EnumSet<Modifiers> getModifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<OrderItemRef> getTargetOrderItems(TransformationContext arg0, OrderItemRef arg1,
			MappingRuleRef arg2) throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<OrderItemRef> getTargetOrderItems(TransformationContext arg0, OrderItemRef arg1,
			MappingRuleRef arg2, OrderItemRef arg3) throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

}
