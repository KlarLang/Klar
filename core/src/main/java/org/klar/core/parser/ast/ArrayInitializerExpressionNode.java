package org.klar.core.parser.ast;

import java.util.List;

public class ArrayInitializerExpressionNode {
    public final List<ExpressionNode> values;

    public ArrayInitializerExpressionNode(List<ExpressionNode> values){
        this.values = values;
    }
}
