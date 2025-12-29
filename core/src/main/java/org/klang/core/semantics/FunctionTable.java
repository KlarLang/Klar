package org.klang.core.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {
    private final Map<String, FunctionSymbol> functions = new HashMap<>(20);

    public FunctionTable(){
        declare(
            new FunctionSymbol(
                "println",
                Type.VOID,
                List.of(Type.UNKNOWN) // ou STRING por enquanto
            )
        );

        declare(
            new FunctionSymbol(
                "print",
                Type.VOID,
                List.of(Type.UNKNOWN) // ou STRING por enquanto
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
