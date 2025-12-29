package org.klang.core.parser.ast;

import java.util.List;

public class DecisionStatementNode extends StatementNode {
    public final ExpressionNode condition;
    public final BlockStatementNode ifBlock;

    public final List<OtherwiseBranchNode> otherwiseBranches;

    public final BlockStatementNode afterallBlock;

    public DecisionStatementNode(ExpressionNode condition, BlockStatementNode ifBlock, List<OtherwiseBranchNode> otherwiseBranches, BlockStatementNode afterallBlock, int line, int column){
        super(line, column);

        this.condition = condition;
        this.ifBlock = ifBlock;
        this.otherwiseBranches = otherwiseBranches;
        this.afterallBlock = afterallBlock;
    }
}
