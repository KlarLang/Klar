package org.klang.core.lexer;

import java.util.EnumMap;

public final class TokenFactory {

    private static final EnumMap<TokenType, Token> SIMPLE_TEMPLATES =
        new EnumMap<>(TokenType.class);

    static {
        for (TokenType type : TokenType.values()) {
            if (isSimple(type)) {
                SIMPLE_TEMPLATES.put(
                    type,
                    new Token(type, null, 0, 0)
                );
            }
        }
    }

    private TokenFactory() {}

    public static Token simple(TokenType type, int line, int column) {
        Token base = SIMPLE_TEMPLATES.get(type);

        if (base == null) {
            throw new IllegalArgumentException("TokenType is not simple: " + type);
        }

        return new Token(
            base.getType(),
            null,
            line,
            column
        );
    }


    private static boolean isSimple(TokenType type) {
        switch (type) {
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVISION:
            case REMAINDER:
            case ASSIGNMENT:
            case LT:
            case GT:
            case BANG:
            case LPAREN:
            case RPAREN:
            case LBRACE:
            case RBRACE:
            case LBRACKET:
            case RBRACKET:
            case COMMA:
            case SEMICOLON:
            case COLON:
            case DOT:
            case INCREMENT:
            case DECREMENT:
            case LTE:
            case GTE:
            case DOUBLEEQUAL:
            case NOTEQUAL:
            case AND:
            case OR:
            case ARROW:
            case AT:
            case EOF:
                return true;
            default:
                return false;
        }
    }
}
