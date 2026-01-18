package org.klar.core.semantics;

import java.util.HashMap;
import java.util.Map;

public class TypeContext {

    private final TypeContext parent;
    private final Map<String, TypeSymbol> symbols = new HashMap<>();

    public TypeContext(TypeContext parent) {
        this.parent = parent;
    }

    /**
     * Tenta declarar uma variável. Retorna false se já existir (para o TypeChecker
     * tratar o erro).
     */
    public boolean declare(String name, TypeSymbol type) {
        if (symbols.containsKey(name)) {
            return false;
        }
        symbols.put(name, type);
        return true;
    }

    /**
     * Busca uma variável. Retorna NULL se não achar (não lança exceção).
     */
    public TypeSymbol resolve(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        return null;
    }
}