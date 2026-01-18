package org.klar.core.parser.ast;

public class WhileStatementNode extends StatementNode{
    public final ExpressionNode condition;
    public final BlockStatementNode body;
    
    public WhileStatementNode(ExpressionNode condition, BlockStatementNode body, int line, int column){
        super(line, column);

        this.condition = condition;
        this.body = body;
    }
}
