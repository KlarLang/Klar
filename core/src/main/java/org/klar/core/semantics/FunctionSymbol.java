package org.klar.core.semantics;

import java.util.List;

public class FunctionSymbol {

    public final String name;
    public final TypeSymbol returnType;
    public final List<TypeSymbol> parameters;

    public FunctionSymbol(String name, TypeSymbol returnType, List<TypeSymbol> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public FunctionSymbol(String name, PrimitiveTypeSymbol returnType, List<TypeSymbol> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public FunctionSymbol(String name, ArrayTypeSymbol returnType, List<TypeSymbol> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }
}
