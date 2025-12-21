package org.klang.core.parser.ast;

public class AstNode {
    public final int line;    
    public final int column;

    public AstNode(int line, int column){
        this.line = line;
        this.column = column;
    }
}
