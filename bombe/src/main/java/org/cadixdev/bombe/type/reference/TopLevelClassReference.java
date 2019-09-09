package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;

/**
 * Represents a unique, qualified path to a top-level
 * {@link ClassReference class}.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public class TopLevelClassReference extends ClassReference {

    /**
     * Constructs a new reference to a top-level class.
     *
     * @param classType The type of the class
     * @throws IllegalArgumentException If the given type represents an inner
     *     class
     */
    public TopLevelClassReference(ObjectType classType) {
        super(Type.TOP_LEVEL_CLASS, classType);

        if (classType.getClassName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0) {
            throw new IllegalArgumentException("Cannot create top-level class reference from inner class identifier");
        }
    }

}
