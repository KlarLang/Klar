package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class VariableExpressionNode extends ExpressionNode {
    public final Token name;

    public VariableExpressionNode(Token name, int line, int column){
        super(line, column);
        this.name = name;
    }
    
}
