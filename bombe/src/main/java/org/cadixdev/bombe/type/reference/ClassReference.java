package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.bombe.type.signature.MethodSignature;

public abstract class ClassReference extends QualifiedReference {

    protected static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    protected final ObjectType classType;

    public ClassReference(Type type, ObjectType classType) {
        super(type);
        this.classType = classType;
    }

    public ObjectType getClassType() {
        return classType;
    }

    public InnerClassReference getInnerClass(String unqualifiedName) {
        return new InnerClassReference(
                this,
                new ObjectType(this.getClassType().getClassName() + INNER_CLASS_SEPARATOR_CHAR + unqualifiedName)
        );
    }

    public FieldReference getField(FieldSignature signature) {
        return new FieldReference(this, signature);
    }

    public MethodReference getMethod(MethodSignature signature) {
        return new MethodReference(this, signature);
    }

}
