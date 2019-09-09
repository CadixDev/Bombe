package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.signature.FieldSignature;

public class FieldReference extends MemberReference {

    public FieldReference(ClassReference owningClass, FieldSignature signature) {
        super(Type.FIELD, owningClass, signature);
    }

    @Override
    public FieldSignature getSignature() {
        return (FieldSignature) signature;
    }

}
