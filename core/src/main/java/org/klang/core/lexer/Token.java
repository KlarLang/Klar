package org.klang.core.lexer;

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
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
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
