package org.cadixdev.bombe.type.reference;

import java.util.Objects;

/**
 * Represents a unique, qualified path to a parameter of a
 * {@link MethodReference method}.
 *
 * @author Max Roncace
 * @since 0.3.1
 */
public class MethodParameterReference extends QualifiedReference {

    private final MethodReference parentMethod;
    private final int index;

    /**
     * Constructs a new reference to a method parameter.
     *
     * @param parentMethod The method specifying the parameter
     * @param index The index of the parameter (0-indexed)
     * @throws IllegalArgumentException If the parameter index of out-of-bounds
     */
    public MethodParameterReference(MethodReference parentMethod, int index) throws IllegalArgumentException {
        super(Type.METHOD_PARAMETER);

        if (index >= parentMethod.getSignature().getDescriptor().getParamTypes().size()) {
            throw new IllegalArgumentException("Cannot get out-of-bounds parameter index " + index);
        }

        this.parentMethod = parentMethod;
        this.index = index;
    }

    /**
     * Gets the method specifying this parameter.
     *
     * @return The method specifying this parameter
     */
    public MethodReference getParentMethod() {
        return parentMethod;
    }

    /**
     * Gets the index of this parameter (0-indexed).
     *
     * @return The index of this parameter
     */
    public int getParameterIndex() {
        return index;
    }

    @Override
    public String toJvmsIdentifier() {
        return getParentMethod().toJvmsIdentifier() + JVMS_COMPONENT_JOINER + index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), super.hashCode(), parentMethod, index);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MethodParameterReference
                && super.equals(o)
                && parentMethod.equals(((MethodParameterReference) o).parentMethod)
                && index == ((MethodParameterReference) o).index;
    }

}
