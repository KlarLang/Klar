package org.klang.core.semantics;

import java.util.HashMap;
import java.util.Map;

public class TypeContext {

    private final TypeContext parent;
    private final Map<String, Type> symbols = new HashMap<>();

    public TypeContext(TypeContext parent) {
        this.parent = parent;
    }

    public void declare(String name, Type type) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException("Em declare, symbols ja tem a chave " + name);
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
        throw new RuntimeException("Undefined variable '" + name + "'");
    }
}
