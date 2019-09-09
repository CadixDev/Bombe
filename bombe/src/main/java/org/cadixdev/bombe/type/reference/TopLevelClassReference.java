package org.cadixdev.bombe.type.reference;

import org.cadixdev.bombe.type.ObjectType;

public class TopLevelClassReference extends ClassReference {

    public TopLevelClassReference(ObjectType classType) {
        super(Type.TOP_LEVEL_CLASS, classType);
    }

}
