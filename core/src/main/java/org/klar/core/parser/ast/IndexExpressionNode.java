package org.klar.core.parser.ast;

public class IndexExpressionNode extends ExpressionNode {
    public final ExpressionNode target;
    public final ExpressionNode index;
    
    public IndexExpressionNode(ExpressionNode target, ExpressionNode index, int line, int column){
        super(line, column);

        this.target = target;
        this.index = index;
    }
}
