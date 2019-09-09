package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.bombe.type.signature.MethodSignature;

public class MethodReference extends MemberReference {

    public MethodReference(ClassReference owningClass, MethodSignature signature) {
        super(Type.METHOD, owningClass, signature);
    }

    @Override
    public MethodSignature getSignature() {
        return (MethodSignature) signature;
    }

    public MethodParameterReference getParameter(int index) {
        if (index >= getSignature().getDescriptor().getParamTypes().size()) {
            throw new IllegalArgumentException("Cannot get out-of-bounds parameter index " + index);
        }

        return new MethodParameterReference(this, index);
    }

}
