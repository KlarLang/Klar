package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class VariableExpressionNode extends ExpressionNode {
    public final Token name;


    public VariableExpressionNode(Token name, int line, int column){
        super(line, column);
        this.name = name;
    }
    
}
