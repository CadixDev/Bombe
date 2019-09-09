package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.signature.MethodSignature;

/**
 * Represents a unique, qualified path to a method of a
 * {@link ClassReference class}.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public class MethodReference extends MemberReference<MethodSignature> {

    /**
     * Constructs a new reference to a class method.
     *
     * @param owningClass The class containing the method
     * @param signature The signature of the method
     */
    public MethodReference(ClassReference owningClass, MethodSignature signature) {
        super(Type.METHOD, owningClass, signature);
    }

    /**
     * Returns a reference to the parameter of this method with the given index.
     *
     * @param index The index of the parameter
     * @return A refernce to the parameter
     * @throws IllegalArgumentException If the parameter index is out-of-bounds
     */
    public MethodParameterReference getParameter(int index) {
        return new MethodParameterReference(this, index);
    }

}
