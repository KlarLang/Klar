package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class BinaryExpressionNode extends ExpressionNode {
    
    public ExpressionNode left;
    public Token operator;
    public ExpressionNode right;

    public BinaryExpressionNode(ExpressionNode left, Token operator, ExpressionNode right, int line, int column){
        super(line, column);

        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}
