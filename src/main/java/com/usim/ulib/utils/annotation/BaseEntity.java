package com.usim.ulib.utils.annotation;

public @interface BaseEntity {
    enum EntityType {
        METHOD,
        VARIABLE,
        CLASS,
        INTERFACE,
        ANNOTATION,
        CONSTRUCTOR,
        NONE
    }

    EntityType type();
}
