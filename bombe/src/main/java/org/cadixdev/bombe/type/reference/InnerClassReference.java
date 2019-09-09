package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;

/**
 * Represents a unique, qualified path to an inner {@link ClassReference class}.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public class InnerClassReference extends ClassReference {

    /**
     * Derives the parent of an inner class based on its identifier.
     *
     * @param classType The full type of the inner class
     * @return A reference to the parent class
     * @throws IllegalArgumentException If the given type is not an inner class
     */
    private static ClassReference deriveParentClass(ObjectType classType) throws IllegalArgumentException {
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

    /**
     * Constructs a new reference to an inner class.
     *
     * @param parentClass The parent class to the inner class
     * @param classType The full type of the inner class
     */
    InnerClassReference(ClassReference parentClass, ObjectType classType) {
        super(Type.INNER_CLASS, classType);

        assert(classType.getClassName().substring(0, classType.getClassName().lastIndexOf(INNER_CLASS_SEPARATOR_CHAR))
                        .equals(parentClass.classType.getClassName()));

        this.parentClass = parentClass;
    }

    /**
     * Constructs a new reference to an inner class.
     *
     * @param classType The full type of the inner class
     */
    public InnerClassReference(ObjectType classType) {
        this(deriveParentClass(classType), classType);
    }

    /**
     * Gets a reference to the parent class of this inner class.
     *
     * @return The parent class
     */
    public ClassReference getParentClass() {
        return parentClass;
    }

}
