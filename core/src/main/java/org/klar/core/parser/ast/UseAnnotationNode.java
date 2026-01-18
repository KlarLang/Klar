package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class UseAnnotationNode {
    public final Token target;
    
    public UseAnnotationNode(Token target){
        this.target = target;
    }

}
