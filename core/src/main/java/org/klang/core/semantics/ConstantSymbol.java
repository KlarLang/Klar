package org.klang.core.semantics;

public final class ConstantSymbol implements TypeSymbol {
    public final TypeSymbol type;

    public ConstantSymbol(TypeSymbol type) {
        this.type = type;  // Fixed: was calling super() instead
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol other) {
        return type.isAssignableFrom(other);
    }

    @Override
    public boolean isString() {
        return type.isString();  // Delegate to wrapped type
    }

    @Override
    public boolean isNumeric() {
        if (type instanceof PrimitiveTypeSymbol t){
            return t.isNumeric();
        }

        if (type instanceof ConstantSymbol t){
            return t.isNumeric();
        }

        if (type instanceof ArrayTypeSymbol t){
            return t.isNumeric();
        }

        return false;
    }

    @Override
    public boolean isInteger() {
        if (type instanceof PrimitiveTypeSymbol t){
            return t.isInteger();
        }

        if (type instanceof ConstantSymbol t){
            return t.isInteger();
        }

        if (type instanceof ArrayTypeSymbol t){
            return t.isInteger();
        }

        return false;
    }

    @Override
    public boolean isDouble() {
        if (type instanceof PrimitiveTypeSymbol t){
            return t.isDouble();
        }

        if (type instanceof ConstantSymbol t){
            return t.isDouble();
        }

        if (type instanceof ArrayTypeSymbol t){
            return t.isDouble();
        }

        return false;
    }

    @Override
    public boolean isReference() {
        return type.isReference();
    }

    @Override
    public String toString() {
        return "constant " + type;
    }
}