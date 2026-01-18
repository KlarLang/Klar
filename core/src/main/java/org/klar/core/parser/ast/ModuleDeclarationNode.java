package org.klar.core.parser.ast;

import org.klar.core.lexer.Token;

public class ModuleDeclarationNode extends StatementNode {
    
    private final Token name;

    public ModuleDeclarationNode(Token name, int line, int column){
        super(line, column);
        this.name = name;
    }   
}