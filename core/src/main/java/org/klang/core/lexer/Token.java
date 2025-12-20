package org.klang.core.lexer;

public class Token {
    TokenType type;
    String value;
    int line, column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(TokenType type, char value) {
        this.type = type;
        this.value = String.valueOf(value);
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
