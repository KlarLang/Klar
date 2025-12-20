package org.klang.core;

import java.util.EnumSet;

import org.klang.core.lexer.TokenType;

public final class Heddle {

    public static final EnumSet<TokenType> TYPES = EnumSet.of(
        TokenType.INTEGER,
        TokenType.DOUBLE,
        TokenType.BOOLEAN,
        TokenType.CHARACTER_TYPE,
        TokenType.STRING_TYPE
    );

    public static final EnumSet<TokenType> COMPARISION_OPERATORS = EnumSet.of(
        TokenType.LT,
        TokenType.GT
    );


    public static final EnumSet<TokenType> TERM_OPERATORS = EnumSet.of(
        TokenType.PLUS,
        TokenType.MINUS
    );

    public static final EnumSet<TokenType> NUMBER_TYPE = EnumSet.of(
        TokenType.NUMBER
    );

    public static final EnumSet<TokenType> FACTOR_OPERATORS = EnumSet.of(
        TokenType.POWER,
        TokenType.MULTIPLY,
        TokenType.DIVISION,
        TokenType.REMAINDER
    );

    private Heddle() {}
}
