package org.klang.core.parser.ast;

public class AssignmentStatementNode extends StatementNode {
    public ExpressionNode name;
    public ExpressionNode value;
    
    public AssignmentStatementNode(ExpressionNode name, ExpressionNode value, int line, int column){
        super(line, column);

        this.name = name;
        this.value = value;
    }
} 