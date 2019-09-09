package org.cadixdev.bombe.type.reference;

/**
 * Represents a unique, qualified path to a class, class member, or method
 * parameter.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public abstract class QualifiedReference {

    protected final Type type;

    public QualifiedReference(Type type) {
        this.type = type;
    }

    /**
     * Returns the {@link Type} of this reference.
     *
     * @return The {@link Type} of this reference
     */
    public Type getType() {
        return type;
    }

    public enum Type {
        TOP_LEVEL_CLASS,
        INNER_CLASS,
        FIELD,
        METHOD,
        METHOD_PARAMETER
    }

}
