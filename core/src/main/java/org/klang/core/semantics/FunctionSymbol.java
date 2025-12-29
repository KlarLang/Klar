package org.klang.core.semantics;

import java.util.List;

public class FunctionSymbol {

    public final String name;
    public final Type returnType;
    public final List<Type> parameters;

    public FunctionSymbol(String name, Type returnType, List<Type> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }
}
