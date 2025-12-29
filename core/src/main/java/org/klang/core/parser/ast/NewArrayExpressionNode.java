package org.klang.core.parser.ast;

public class NewArrayExpressionNode extends ExpressionNode {
    public final TypeReferenceNode type;
    public final ExpressionNode size;

    public NewArrayExpressionNode(TypeReferenceNode type, ExpressionNode size, int line, int column){
        super(line, column);

        this.type = type;
        this.size = size;
    }
}
