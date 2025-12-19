package org.klang.core.parser.ast;

abstract public class StatementNode {
    public final int line;    
    public final int column;    

    public StatementNode(int line, int column){
        this.line = line;
        this.column = column;
    }        
}
