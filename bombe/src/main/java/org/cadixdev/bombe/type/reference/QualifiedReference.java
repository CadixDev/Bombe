package org.cadixdev.bombe.type.reference;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a unique, qualified path to a class, class member, or method
 * parameter.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public abstract class QualifiedReference {

    protected static final char JVMS_COMPONENT_JOINER = '.';

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

    /**
     * Returns a JVMS-like identifier string corresponding to this reference.
     *
     * The JVMS does not specify a qualified format for member and parameter
     * identifiers, so for these cases, a dot (".") is used to separate the
     * class, member signature, and parameter index components (as appropriate).
     *
     * @return A JVMS-like identifier string for this reference
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2"></a>
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3"></a>
     */
    public abstract String toJvmsIdentifier();

    protected StringJoiner buildToString() {
        return new StringJoiner("{type=" + type.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), type);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof QualifiedReference && type == ((QualifiedReference) o).type;
    }

    @Override
    public String toString() {
        return buildToString().add("}").toString();
    }

    public enum Type {
        TOP_LEVEL_CLASS,
        INNER_CLASS,
        FIELD,
        METHOD,
        METHOD_PARAMETER
    }

}
