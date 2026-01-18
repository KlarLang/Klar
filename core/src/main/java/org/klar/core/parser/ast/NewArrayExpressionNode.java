package org.klar.core.parser.ast;

import java.util.List;

public class NewArrayExpressionNode extends ExpressionNode {
    public final TypeReferenceNode type;
    public final ExpressionNode size;
    public final List<ExpressionNode> initializer;

    public NewArrayExpressionNode(TypeReferenceNode type, ExpressionNode size, List<ExpressionNode> initializer, int line, int column){
        super(line, column);

        this.type = type;
        this.size = size;
        this.initializer = initializer;

    }
}