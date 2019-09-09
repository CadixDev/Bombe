package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.signature.MemberSignature;

public abstract class MemberReference extends QualifiedReference {

    protected final ClassReference owningClass;
    protected final MemberSignature signature;

    public MemberReference(Type type, ClassReference owningClass, MemberSignature signature) {
        super(type);
        this.owningClass = owningClass;
        this.signature = signature;
    }

    public ClassReference getOwningClass() {
        return owningClass;
    }

    public MemberSignature getSignature() {
        return signature;
    }

}
