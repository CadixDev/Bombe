package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;

/**
 * Represents a unique, qualified path to a class, class member, or method
 * parameter.
 *
 * @author Max Roncace
 * @since 0.3.0
 */
public abstract class QualifiedReference {

    protected final Type type;

    public QualifiedReference(Type type) {
        this.type = type;
    }

    public enum Type {
        TOP_LEVEL_CLASS,
        INNER_CLASS,
        FIELD,
        METHOD,
        METHOD_PARAMETER
    }

}
