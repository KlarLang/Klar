package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class AssignmentStatementNode extends StatementNode {
    public Token name;
    public ExpressionNode value;
    
    public AssignmentStatementNode(Token name, ExpressionNode value, int line, int column){
        super(line, column);

        this.name = name;
        this.value = value;
    }
}