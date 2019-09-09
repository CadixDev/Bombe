package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.signature.MemberSignature;

import java.util.Objects;

/**
 * Represents a unique, qualified path to a {@link ClassReference class} member.
 *
 * @param <S> The {@link MemberSignature} type used by this reference
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public abstract class MemberReference<S extends MemberSignature> extends QualifiedReference {

    protected final ClassReference owningClass;
    protected final S signature;

    /**
     * Constructs a new reference to a class member.
     *
     * @param type The type of reference (must be either {@link Type#FIELD} or
     * {@link Type#METHOD}.
     *
     * @param owningClass A reference to the class which owns the member
     * @param signature The signature of the member
     */
    public MemberReference(Type type, ClassReference owningClass, S signature) {
        super(type);
        this.owningClass = owningClass;
        this.signature = signature;
    }

    /**
     * Gets the class which owns this member.
     *
     * @return The class which owns this member
     */
    public ClassReference getOwningClass() {
        return owningClass;
    }

    /**
     * Gets the signature of this member.
     *
     * @return The signature of this member
     */
    public S getSignature() {
        return signature;
    }

    @Override
    public String toJvmsIdentifier() {
        return owningClass.toJvmsIdentifier() + JVMS_COMPONENT_JOINER + signature.toJvmsIdentifier();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owningClass, signature);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MemberReference
                && super.equals(o)
                && owningClass.equals(((MemberReference) o).owningClass)
                && signature.equals(((MemberReference) o).signature);
    }
}
