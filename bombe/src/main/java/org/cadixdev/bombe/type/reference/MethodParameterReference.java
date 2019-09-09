package org.cadixdev.bombe.type.reference;

public class MethodParameterReference extends QualifiedReference {

    private final MethodReference parentMethod;
    private final int index;

    public MethodParameterReference(MethodReference parentMethod, int index) {
        super(Type.METHOD_PARAMETER);
        this.parentMethod = parentMethod;
        this.index = index;
    }

    public MethodReference getParentMethod() {
        return parentMethod;
    }

    public int getParameterIndex() {
        return index;
    }

}
