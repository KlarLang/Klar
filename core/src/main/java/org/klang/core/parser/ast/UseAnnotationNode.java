package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class UseAnnotationNode {
    public final Token target;
    
    public UseAnnotationNode(Token target){
        this.target = target;
    }

}
