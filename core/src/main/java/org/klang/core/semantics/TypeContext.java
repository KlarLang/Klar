package org.klang.core.semantics;

import java.util.HashMap;
import java.util.Map;

import org.klang.core.errors.SemanticException;

public class TypeContext {

    private final TypeContext parent;
    private final Map<String, Type> symbols = new HashMap<>();

    public TypeContext(TypeContext parent) {
        this.parent = parent;
    }

    public void declare(String name, Type type) {
        if (symbols.containsKey(name)) {
            throw new SemanticException("Variable '" + name + "' already declared in this scope");
        }
        symbols.put(name, type);
    }

    public Type resolve(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        throw new SemanticException("Undefined variable '" + name + "'");
    }
}
