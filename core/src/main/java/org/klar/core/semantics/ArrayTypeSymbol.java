package org.klar.core.semantics;

import org.klar.core.Heddle;

public final class ArrayTypeSymbol implements TypeSymbol {
    public final Type elementType;

    public ArrayTypeSymbol(Type elementType) {
        this.elementType = elementType;
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol other) {
        if (other instanceof ArrayTypeSymbol arr) {
            return arr.elementType == this.elementType;
        }
        return false;
    }

    @Override
    public boolean isReference() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInteger() {
        return elementType == Type.INTEGER;
    }

    @Override
    public boolean isDouble() {
        return elementType == Type.DOUBLE;
    }

    @Override
    public boolean isNumeric() {
        return Heddle.NUMERICS.contains(elementType);
    }

    @Override
    public boolean isString() {
        return elementType == Type.STRING;
    }

    @Override
    public String toString() {
        return elementType.name() + "[]";
    }
}
