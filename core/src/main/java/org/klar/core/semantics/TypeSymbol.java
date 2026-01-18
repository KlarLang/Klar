package org.klar.core.semantics;

public sealed interface TypeSymbol
    permits PrimitiveTypeSymbol, ArrayTypeSymbol, ConstantSymbol {

    boolean isAssignableFrom(TypeSymbol other);
    boolean isString();
    boolean isReference();
    boolean isNumeric();
    boolean isDouble();
    boolean isInteger();
}