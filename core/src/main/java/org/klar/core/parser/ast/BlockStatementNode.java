package org.klar.core.parser.ast;

import java.util.List;

public class BlockStatementNode extends StatementNode {
    public final List<StatementNode> statements;
    
    public BlockStatementNode(List<StatementNode> statements, int line, int column){
        super(line, column);

        this.statements = statements;
    }
}
