package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class VariableDeclarationNode extends StatementNode{
    public final Token type;
    public final Token name;
    public final ExpressionNode value;

    public VariableDeclarationNode(Token type, Token name, ExpressionNode value, int line, int column){
        super(line, column);
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
