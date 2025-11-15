package org.klang.core.lexer;

public enum TokenType {

    // @Use()
    USELANGUAGE,

    TYPE, IDENTIFIER, NUMBER, ASSIGNMENT, SEMICOLON, RETURN,

    // Operações
    PLUS, MINUS, MULTIPLY, DIVISION, POWER, REMAINDER,

    // Delimitadores {} -> breaces [] -> brackets
    LPAREN, RPAREN, SEPARATOR, LBRACE, RBRACE, LBRACKET, RBRACKET,

    // Métodos
    MODIFIER, RETURNTYPE, ARGUMENT, METHOD
}
