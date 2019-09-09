package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.bombe.type.signature.MethodSignature;

/**
 * Represents a unique, qualified path to a class.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public abstract class ClassReference extends QualifiedReference {

    protected static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    protected final ObjectType classType;

    /**
     * Constructs a new reference to a class.
     *
     * @param type The {@link Type} of reference (must be
     *     {@link Type#TOP_LEVEL_CLASS} or {@link Type#INNER_CLASS})
     * @param classType An {@link ObjectType} representing the type of the
     *     referenced class
     */
    public ClassReference(Type type, ObjectType classType) {
        super(type);
        this.classType = classType;
    }

    /**
     * Returns the type of class represented by this reference
     *
     * @return The type of class
     */
    public ObjectType getClassType() {
        return classType;
    }

    /**
     * Returns a reference to an inner class of this class.
     *
     * @param unqualifiedName The unqualified name of the inner class
     * @return A reference to the inner class
     */
    public InnerClassReference getInnerClass(String unqualifiedName) {
        return new InnerClassReference(
                this,
                new ObjectType(this.getClassType().getClassName() + INNER_CLASS_SEPARATOR_CHAR + unqualifiedName)
        );
    }

    /**
     * Returns a reference to a field contained by this class.
     *
     * @param signature The {@link FieldSignature signature} of the field
     * @return A reference to the field
     */
    public FieldReference getField(FieldSignature signature) {
        return new FieldReference(this, signature);
    }

    /**
     * Returns a reference to a method contained by this class.
     *
     * @param signature The {@link MethodSignature signature} of the method
     * @return A reference to the method
     */
    public MethodReference getMethod(MethodSignature signature) {
        return new MethodReference(this, signature);
    }

}
