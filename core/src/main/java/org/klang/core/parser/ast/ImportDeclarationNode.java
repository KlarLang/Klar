package org.klang.core.parser.ast;

import java.util.List;

import org.klang.core.lexer.Token;

public class ImportDeclarationNode extends StatementNode {
    public final List<Token> path;

    public ImportDeclarationNode(List<Token> path, int line, int column){
        super(line, column);
        this.path = path;
    }
}
