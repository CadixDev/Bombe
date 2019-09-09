package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.signature.FieldSignature;

/**
 * Represents a unique, qualified path to a field of a
 * {@link ClassReference class}.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public class FieldReference extends MemberReference<FieldSignature> {

    /**
     * Constructs a new reference to a class field.
     *
     * @param owningClass The class containing the field
     * @param signature The signature of the field
     */
    public FieldReference(ClassReference owningClass, FieldSignature signature) {
        super(Type.FIELD, owningClass, signature);
    }
}
