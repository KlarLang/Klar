package org.klang.core.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {
    private final Map<String, FunctionSymbol> functions = new HashMap<>(20);

    public FunctionTable(){
        TypeSymbol returnT = new PrimitiveTypeSymbol(Type.VOID, false);
        List<TypeSymbol> argsT = List.of(new PrimitiveTypeSymbol(Type.UNKNOWN, false));

        declare(
            new FunctionSymbol(
                "println",
                returnT,
                argsT
            )
        );

        declare(new FunctionSymbol("printf", returnT, argsT));

        declare(
            new FunctionSymbol(
                "print",
                returnT,
                argsT
            )
        );
    }

    public void declare(FunctionSymbol fn){
        if (functions.containsKey(fn.name)){
            throw new RuntimeException("Function " + fn.name + " already declared.");
        }

        functions.put(fn.name, fn);
    }

    public FunctionSymbol resolve(String fn){
        FunctionSymbol function = functions.get(fn);

        if (function == null){
            throw new RuntimeException("Function " + fn + " not declared.");
        }

        return function;
    }
}
