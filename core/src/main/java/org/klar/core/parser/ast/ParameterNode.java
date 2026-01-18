package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class ParameterNode {
    public final TypeReferenceNode type;
    public final Token name;

    public ParameterNode(TypeReferenceNode type, Token name){
        this.type = type;
        this.name = name;
    }
}
