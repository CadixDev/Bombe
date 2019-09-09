package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;

public class InnerClassReference extends ClassReference {

    private static ClassReference deriveParentClass(ObjectType classType) {
        if (classType.getClassName().indexOf(INNER_CLASS_SEPARATOR_CHAR) < 0) {
            throw new IllegalArgumentException("Cannot derive parent class from non-inner class identifier");
        }

        ObjectType parentType = new ObjectType(
                classType.getClassName().substring(0, classType.getClassName().lastIndexOf('$'))
        );
        if (parentType.getClassName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0) {
            return new InnerClassReference(parentType);
        } else {
            return new TopLevelClassReference(parentType);
        }
    }

    private final ClassReference parentClass;

    public InnerClassReference(ClassReference parentClass, ObjectType classType) {
        super(Type.INNER_CLASS, classType);
        this.parentClass = parentClass;
    }

    public InnerClassReference(ObjectType classType) {
        this(deriveParentClass(classType), classType);
    }

    public ClassReference getParentClass() {
        return parentClass;
    }

}
