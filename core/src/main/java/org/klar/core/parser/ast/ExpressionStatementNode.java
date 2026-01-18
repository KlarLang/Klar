package org.klar.core.parser.ast;

public class ExpressionStatementNode extends StatementNode{
    public final ExpressionNode expression;
    
    public ExpressionStatementNode(ExpressionNode expression, int line, int column){
        super(line, column);

        this.expression = expression;
    }
}
