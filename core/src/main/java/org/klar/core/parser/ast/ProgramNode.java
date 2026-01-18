package org.klar.core.parser.ast;

import java.util.List;

public class ProgramNode {
    public final List<StatementNode> statements;

    public ProgramNode(List<StatementNode> statements) {
        this.statements = statements;
    }
}
