package org.klang.core.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {
    private final Map<String, FunctionSymbol> functions = new HashMap<>(20);

    public FunctionTable(){
        // Define 'void' e 'any' (UNKNOWN)
        TypeSymbol returnVoid = new PrimitiveTypeSymbol(Type.VOID, false);
        // UNKNOWN aqui funciona como um "Object" ou "Any", aceitando qualquer coisa
        List<TypeSymbol> argsAny = List.of(new PrimitiveTypeSymbol(Type.UNKNOWN, false));

        // Registra funções nativas
        internalDeclare(new FunctionSymbol("println", returnVoid, argsAny));
        internalDeclare(new FunctionSymbol("print", returnVoid, argsAny));
        internalDeclare(new FunctionSymbol("printf", returnVoid, argsAny));
    }

    /**
     * Declaração interna para o construtor (sem verificações)
     */
    private void internalDeclare(FunctionSymbol fn) {
        functions.put(fn.name, fn);
    }

    /**
     * Tenta declarar uma função.
     * @return true se declarou, false se já existia (colisão).
     */
    public boolean declare(FunctionSymbol fn){
        if (functions.containsKey(fn.name)){
            return false;
        }
        functions.put(fn.name, fn);
        return true;
    }

    /**
     * Busca uma função.
     * @return O símbolo da função ou null se não encontrar.
     */
    public FunctionSymbol resolve(String name){
        return functions.get(name);
    }

    public boolean contains(String name) {
        return functions.containsKey(name);
    }
}