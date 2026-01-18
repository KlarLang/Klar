package org.klar.core.semantics;

import org.klar.core.Heddle;

public final class PrimitiveTypeSymbol implements TypeSymbol {
    public final Type type;
    public boolean isLiteral;

    public PrimitiveTypeSymbol(Type type, boolean isLiteral) {
        this.type = type;
        this.isLiteral = isLiteral;
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol other) {
        if (other instanceof PrimitiveTypeSymbol p) {
            return p.type == this.type;
        }

        if (other instanceof PrimitiveTypeSymbol p
            && p.type == Type.NULL
            && type == Type.STRING) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDouble() {
        return type == Type.DOUBLE;
    }

    @Override
    public boolean isInteger() {
        return type == Type.INTEGER;
    }
    
    @Override
    public boolean isNumeric() {
        return Heddle.NUMERICS.contains(type);
    }

    @Override
    public boolean isReference() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isString() {
        return type == Type.STRING;
    }


    @Override
    public String toString() {
        return type.name().toLowerCase();
    }
}
