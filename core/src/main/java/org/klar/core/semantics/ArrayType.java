package org.klar.core.semantics;

public class ArrayType {
    public final Type elementType;

    public ArrayType(Type elementType){
        this.elementType = elementType;
    }
}
