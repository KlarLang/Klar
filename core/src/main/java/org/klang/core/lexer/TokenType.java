package org.klang.core.lexer;

public enum TokenType {

    // @
    AT,

    IDENTIFIER, NUMBER, STRING, CHARACTER, ASSIGNMENT, RETURN, KEYWORD, TYPE, NATIVE_CLASS,

    // KEYWORDS
    IF, OTHERWISE, AFTERALL, FOR, WHILE, BREAK, CONTINUE, PUBLIC, PRIVATE, PROTECTED, STATIC,

    // Operações
    PLUS, INCREMENT, MINUS, DECREMENT, MULTIPLY, DIVISION, POWER, REMAINDER,

    // Delimitadores {} -> breaces [] -> brackets
    LPAREN, RPAREN, COMMA, LBRACE, RBRACE, LBRACKET, RBRACKET, SEMICOLON, COLON, DOT,

    BANG, LT, GT, LTE, GTE, DOUBLEEQUAL, NOTEQUAL, ARROW,
}

/*
 * 
 * tokensTypeByString.put("return", TokenType.KEYWORD);
 * tokensTypeByString.put("if", TokenType.KEYWORD);
 * tokensTypeByString.put("otherwise", TokenType.KEYWORD);
 * tokensTypeByString.put("afterall", TokenType.KEYWORD);
 * tokensTypeByString.put("for", TokenType.KEYWORD);
 * tokensTypeByString.put("while", TokenType.KEYWORD);
 * tokensTypeByString.put("break", TokenType.KEYWORD);
 * tokensTypeByString.put("continue", TokenType.KEYWORD);
 * tokensTypeByString.put("public", TokenType.KEYWORD);
 * tokensTypeByString.put("private", TokenType.KEYWORD);
 * tokensTypeByString.put("static", TokenType.KEYWORD);
 * tokensTypeByString.put("protected", TokenType.KEYWORD);
 */

/*
 * LPAREN (
 * RPAREN )
 * LBRACE {
 * RBRACE }
 * LBRACKET [
 * RBRACKET ]
 * COMMA ,
 * SEMICOLON ;
 * COLON :
 * DOT .
 * PLUS +
 * MINUS -
 * STAR *
 * SLASH /
 * PERCENT %
 * EQUAL =
 * BANG !
 * LT <
 * GT >
 * LTE <=
 * GTE >=
 * DOUBLEEQUAL ==
 * NOTEQUAL !=
 * ARROW ->
 * 
 */
