package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class LiteralExpressionNode extends ExpressionNode {
    public final Token value;
    
    public LiteralExpressionNode(Token value, int line, int column){
        super(line, column);

        this.value = value;
    }
}
