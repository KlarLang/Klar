package org.klar.core;

import java.util.EnumSet;

import org.klar.core.lexer.TokenType;
import org.klar.core.semantics.Type;

public final class Heddle {

    public static final EnumSet<TokenType> TYPES = EnumSet.of(
        TokenType.INTEGER_TYPE,
        TokenType.DOUBLE_TYPE,
        TokenType.BOOLEAN_TYPE,
        TokenType.CHARACTER_TYPE,
        TokenType.STRING_TYPE,
        TokenType.VOID
    );

    public static final EnumSet<TokenType> COMPARISION_OPERATORS = EnumSet.of(
        TokenType.LT,
        TokenType.GT,
        TokenType.DOUBLEEQUAL,
        TokenType.NOTEQUAL,
        TokenType.GTE,
        TokenType.LTE
    );


    public static final EnumSet<TokenType> TERM_OPERATORS = EnumSet.of(
        TokenType.PLUS,
        TokenType.MINUS
    );

    public static final EnumSet<TokenType> ARITHMETICS = EnumSet.of(
        TokenType.PLUS,
        TokenType.MINUS,
        TokenType.DIVISION,
        TokenType.MULTIPLY,
        TokenType.REMAINDER
    );

    public static final EnumSet<Type> NUMERICS = EnumSet.of(
        Type.DOUBLE,
        Type.INTEGER
    );

    public static final EnumSet<TokenType> NUMBER_TYPE = EnumSet.of(
        TokenType.NUMBER
    );

    public static final EnumSet<TokenType> FACTOR_OPERATORS = EnumSet.of(
        TokenType.MULTIPLY,
        TokenType.DIVISION,
        TokenType.REMAINDER
    );

    public static final EnumSet<TokenType> ACESS_MODIFIERS = EnumSet.of(
        TokenType.PUBLIC,
        TokenType.PROTECTED,
        TokenType.INTERNAL
    );

    private Heddle() {}
}
