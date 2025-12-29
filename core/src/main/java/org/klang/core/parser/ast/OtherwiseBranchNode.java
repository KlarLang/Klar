package org.klang.core.parser.ast;

public class OtherwiseBranchNode {
    public final ExpressionNode condition;
    public final String reason;
    public final BlockStatementNode body;

    public OtherwiseBranchNode(ExpressionNode condition, String reason, BlockStatementNode body){
        this.condition = condition;
        this.reason = reason;
        this.body = body;
    }
}
