package org.klar.core.parser.ast;

public class ReturnStatementNode extends StatementNode{
    public final ExpressionNode value;
    
    public ReturnStatementNode(ExpressionNode value, int line, int column){
        super(line, column);

        this.value = value;
    }
}
