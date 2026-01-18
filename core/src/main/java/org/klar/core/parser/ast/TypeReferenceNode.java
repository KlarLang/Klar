package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class TypeReferenceNode {
    public final Token baseType;
    public final int arrayDepth;

    public TypeReferenceNode(Token baseType, int arrayDepth){
        this.baseType = baseType;
        this.arrayDepth = arrayDepth;
    }

    public boolean isArray(){
        return arrayDepth != 0;
    }

    public Token getBaseType() {
        return baseType;
    }
}
