/*
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 */
package oracle.communications.ordermanagement.orchestration.transformation.sequence;

import java.util.EnumSet;

import oracle.communications.ordermanagement.orchestration.transformation.RelationshipTypeRef;
import oracle.communications.ordermanagement.orchestration.transformation.TransformationException;
import oracle.communications.ordermanagement.orchestration.transformation.sequence.TransformationContext;
import oracle.communications.ordermanagement.orchestration.transformation.sequence.TransformationContext.Modifiers;
import oracle.communications.ordermanagement.orchestration.transformation.sequence.UseExistingTargetRelationshipHandler;

// TODO move to src/test/java hierarchy after Aux handler is delivered in base cartridge

/**
 * An auxiliary relationship handler used in tests.
 */
public class AuxiliaryRelationshipHandler extends UseExistingTargetRelationshipHandler
{
	@Override
    protected RelationshipTypeRef getExistingRelationshipType(final TransformationContext context) throws TransformationException {
        return context.getRelationshipType("Primary");
    }

    @Override
    public EnumSet<Modifiers> getModifiers() {
        return EnumSet.noneOf(TransformationContext.Modifiers.class);
    }
};