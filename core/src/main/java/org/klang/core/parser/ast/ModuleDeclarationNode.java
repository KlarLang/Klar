package org.klang.core.parser.ast;

import org.klang.core.lexer.Token;

public class ModuleDeclarationNode extends StatementNode {
    
    private final Token name;

    public ModuleDeclarationNode(Token name, int line, int column){
        super(line, column);
        this.name = name;
    }   
}