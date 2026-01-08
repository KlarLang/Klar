package org.klang.core.lexer;

import org.klang.core.Heddle;

public class Token {
    public final TokenType type;
    public final String value;
    public final int line;
    public final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Token(TokenType type, int line, int column) {
        this.type = type;
        this.value = null;
        this.line = line;
        this.column = column;
    }

    public Token(TokenType type, char value, int line, int column) {
        this.type = type;
        this.value = String.valueOf(value);
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue(){
        if (value == null){
            return "";
        }
        
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean isVoid(){
        return this == null;
    }

    public boolean isArithmetic(){
        return Heddle.TERM_OPERATORS.contains(type);
    }

    @Override
    public String toString() {
        String saida = "" + type;

        if (value != null) {
            saida += "(" + value + ")";
        }

        return saida;
    }
}
