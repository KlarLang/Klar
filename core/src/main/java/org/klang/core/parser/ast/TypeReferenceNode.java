package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class TypeReferenceNode {
    public final Token baseType;
    public final int arrayDepth;

    public TypeReferenceNode(Token baseType, int arrayDepth){
        this.baseType = baseType;
        this.arrayDepth = arrayDepth;
    }

    public Token getBaseType() {
        return baseType;
    }
}
