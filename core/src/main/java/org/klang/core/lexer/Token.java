package org.klang.core.lexer;

public class Token {
    TokenType type;
    String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
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

    @Override
    public String toString() {
        String saida = "" + type;

        if (value != null) {
            saida += "(" + value + ")";
        }

        return saida;
    }
}
