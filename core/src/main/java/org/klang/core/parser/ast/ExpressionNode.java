package org.klang.core.parser.ast;

public abstract class ExpressionNode {
    public final int line;
    public final int column;

    public ExpressionNode(int line, int column){
        this.line = line;
        this.column = column;
    }
}
