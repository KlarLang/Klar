package org.klar.core.semantics;

public enum Type {
    INTEGER,
    DOUBLE,
    BOOLEAN,
    STRING,
    CHARACTER,
    VOID,
    NULL,
    UNKNOWN;

    public boolean isReference(){
        return this == STRING;
    }
}
