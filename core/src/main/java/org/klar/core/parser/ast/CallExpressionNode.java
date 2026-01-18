package org.klar.core.parser.ast;

import java.util.List;

import org.klar.core.lexer.Token;

public class CallExpressionNode extends ExpressionNode {
    public final Token callee;
    public final List<ExpressionNode> arguments;
    
    public CallExpressionNode(Token callee, List<ExpressionNode> argsuments, int line, int column){
        super(line, column);

        this.callee = callee;
        this.arguments = argsuments;
    }
    
}
