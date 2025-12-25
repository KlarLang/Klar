package org.klang.core.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.klang.core.diagnostic.DiagnosticCode;
import org.klang.core.error.LexicalException;
import org.klang.core.error.SourceLocation;
import org.klang.core.error.SourceManager;

public class Lexer {
    
    private int position = 0;
    private int line = 1;
    private int column = 0;
    
    private final String source;
    private final String filePath;
    
    private final char[] input;
    private final int length;

    private final List<Token> tokens;

    private final SourceManager sourceManager;
    private final StringBuilder stringBuilder = new StringBuilder(255);

    private final HashMap<String, TokenType> tokensTypeByString = new HashMap<>(65,1.0f);
    private final TokenType[] singleCharTokens = new TokenType[128];

    private final Map<String, String> symbolTable = new HashMap<>(512, 0.75f);

    public Lexer(String source, String filePath) {
        this.source = source;
        this.filePath = filePath;

        this.input = source.toCharArray();
        this.length = input.length;

        this.sourceManager = new SourceManager(source);
        int estimedTokens = Math.max(16, source.length() / 4);
        this.tokens = new ArrayList<>(estimedTokens);

        initialzerhashMapTokensTypes();
    }

    private String canonical(String s){
        String existing = symbolTable.get(s);

        if (existing != null){
            return existing;
        } 

        symbolTable.put(s, s);
        return s;

    }

    public List<Token> tokenizeSourceCode() {
        this.stringBuilder.setLength(0);
        while (!isAtEnd()) {

            char c = peek();

            if (Character.isWhitespace(c)) {
                advance();

                if (c == '\n') {
                    line++;
                    column = 0;
                }

                continue;
            }

            if (c == '"') {
                int startLine = this.line;
                int startColumn = this.position; 

                advance();

                String content = readString(startLine, startColumn);
                tokens.add(new Token(
                        TokenType.STRING_LITERAL,
                        content,
                        line,
                        position));

                continue;
            }

            if (c == '\'') {
                advance();

                String content = readCharacter();
                tokens.add(new Token(
                        TokenType.CHARACTER_LITERAL,
                        content,
                        line,
                        position));

                continue;
            }

            if (Character.isLetter(c) || c == '_' || c == '$') {

                if (c == '$' && !(Character.isLetter(peekNext()) || peekNext() == '_')) {
                    String example = "integer $variableName = 10;"; 

                    lexicalError(
                            DiagnosticCode.E001,
                            "The character '$' cannot start an identifier alone.",
                            "Identifiers starting with '$' must contain a letter or underscore.",
                            example, null, 1);
                }

                String ident = readIdentifier();
                ident = canonical(ident);
                TokenType tokenType = tokensTypeByString.getOrDefault(ident, TokenType.IDENTIFIER);

                if (tokenType == TokenType.IDENTIFIER) {
                    tokens.add(new Token(tokenType, ident, line, position));
                } else {
                    tokens.add(new Token(tokenType, line, position));
                }

                continue;
            }

            if (Character.isDigit(c)) {
                String num = readNumber();
                tokens.add(new Token(TokenType.NUMBER, num, line, position));
                continue;
            }

            if (peek() == '/' && peekNext() == '/') {
                advance();
                advance();

                while (!isAtEnd() && peek() != '\n') {
                    advance();
                }

                continue;
            }

            if (peek() == '/' && peekNext() == '*') {
                advance();
                advance();

                while (!isAtEnd()) {

                    if (peek() == '*' && peekNext() == '/') {
                        advance();
                        advance();
                        break;
                    }

                    if (peek() == '\n') {
                        line++;
                        column = 0;
                    }

                    advance();
                }

                continue;
            }

            TokenType tokenType = c < 128 ? singleCharTokens[c] : null;
            this.stringBuilder.setLength(0);
            switch (c) {
                case '@':
                    advance();
                    tokens.add(TokenFactory.simple(TokenType.AT, line, column));

                    continue;

                case '=':
                    advance();

                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.DOUBLEEQUAL, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.ASSIGNMENT, line, column));
                    }

                    continue;

                case '+':
                    advance();

                    if (match('+')) {
                        tokens.add(TokenFactory.simple(TokenType.INCREMENT, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.PLUS, line, column));
                    }

                    continue;

                case '.':
                    advance();

                    tokens.add(TokenFactory.simple(TokenType.DOT, line, column));

                    continue;

                case '-':
                    advance();

                    if (match('-')) {
                        tokens.add(TokenFactory.simple(TokenType.DECREMENT, line, column));
                    } else if (match('>')) {
                        tokens.add(TokenFactory.simple(TokenType.ARROW, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.MINUS, line, column));
                    }

                    continue;

                case '*':
                    advance();
                    this.stringBuilder.append('*');
                    
                    if (peek() == '*') {
                        while (peek() == '*') {
                            this.stringBuilder.append(advance());
                        }
                        
                        lexicalError(
                            DiagnosticCode.E001,
                            "This '"+  this.stringBuilder.toString() + "' is not valid.",
                            "Use Mathematics.power() for logical AND.",
                            "double powerResult = Mathematics.power(variableAPotentize);",
                        null ,this.stringBuilder.length());
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.MULTIPLY, line, column));
                    }

                    continue;

                case '>':
                    advance();

                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.GTE, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.GT, line, column));
                    }

                    continue;

                case '<':
                    advance();

                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.LTE, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.LT, line, column));
                    }

                    continue;

                case '!':
                    advance();

                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.NOTEQUAL, line, column));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.BANG, line, column));
                    }

                    continue;

                case '&':
                    advance();
                    this.stringBuilder.append("&");

                    while (peek() == '&') {
                        this.stringBuilder.append(advance());
                    }

                    lexicalError(
                            DiagnosticCode.E001,
                            "This '"+  this.stringBuilder.toString() + "' is not valid.",
                            "Use 'and' for logical AND.",
                            "if (firstCondition and secondCondition) {\n\tprintln(\"The first and second conditions are in agreement.\")\n  }",
                        null ,this.stringBuilder.length());
                    break;

                case '|':
                    advance();
                    this.stringBuilder.append("|");

                    while (peek() == '|') {
                        this.stringBuilder.append(advance());
                    }

                    lexicalError(
                            DiagnosticCode.E001,
                            "This '"+  this.stringBuilder.toString() + "' is not valid.",
                            "Use 'or' for logical OR.",
                            "if (firstCondition or secondCondition) {\n\tprintln(\"The first and second conditions are in agreement.\")\n  }",
                        null, this.stringBuilder.length());
                    break;
            }

            if (tokenType == null) {
                lexicalError(
                        DiagnosticCode.E001,
                        "Character '" + c + "' is not valid in Klang.",
                        "Remove or replace it.",
                        null, null,
                        1
                    );
            }

            tokens.add(TokenFactory.simple(tokenType, line, position));
            advance();
        }

        tokens.add(TokenFactory.simple(TokenType.EOF, line, column));
        return tokens;
    }

    private String readString(int startLine, int startColumn) {
        this.stringBuilder.setLength(0);
        String example = "\"" + this.stringBuilder.toString().strip() + "\"";

        while (!isAtEnd()) {
            char c = advance();
            if (c == '"') {
                return this.stringBuilder.toString();
            }

            if (c == '\n') {
                int errorLength = this.stringBuilder.length();

                lexicalError(
                        DiagnosticCode.E002,
                        "String literal cannot span multiple lines.",
                        "Close the string before the line break.",
                        example, null, errorLength

                );
            }

            if (c == '\\') {

                if (isAtEnd()) {
                    int errorLength = this.stringBuilder.length();

                    lexicalError(
                            DiagnosticCode.E002,
                            "Unclosed string literal.",
                            "Add closing quote.",
                            example, null, errorLength);
                }

                char escaped = advance();

                if (escaped == 'n') {
                    this.stringBuilder.append('\n');
                } else if (escaped == 't') {
                    this.stringBuilder.append('\t');
                } else if (escaped == '"') {
                    this.stringBuilder.append('"');
                } else if (escaped == '\\') {
                    this.stringBuilder.append('\\');
                } else {
                    int errorLength = this.stringBuilder.length();

                    lexicalError(
                            DiagnosticCode.E004,
                            "Invalid escape sequence: \\" + escaped,
                            "Use valid escapes like \\n, \\t, \\\".", example, null, errorLength

                    );
                }

                continue;
            }

            this.stringBuilder.append(c);
        }

        int errorLength = this.stringBuilder.length();
        lexicalError(
                DiagnosticCode.E002,
                "Unclosed string literal.",
                "Add closing quote.",
                example, null, errorLength

        );
        return null;
    }

    private String readCharacter() {
        this.stringBuilder.setLength(0);
        int errorLength = 1;
        
        if (isAtEnd()) {
            String example = "\'x\'";
            
            advance();
            lexicalError(
                DiagnosticCode.E004,
                "Unclosed character literal.",
                "Add closing '.",
                example,  
                null, 
                errorLength);
        }

        char c = advance();
        String value;

        if (c == '\\') {
            if (isAtEnd()) {
                String example = "\'\\n\'";

                advance();
                lexicalError(
                    DiagnosticCode.E004,
                    "Unclosed character literal.",
                    "Add closing '.",
                    example, 
                    null, 
                    errorLength+2);
            }

            char escaped = advance();

            if (escaped == 'n') {
                value = "\\n";
            } else if (escaped == 't') {
                value = "\\t";
            } else if (escaped == '\'') {
                value = "'";
            } else if (escaped == '\\') {
                value = "\\\\";
            } else {
                String example = "'\\n'";

                lexicalError(
                    DiagnosticCode.E004,
                    "Invalid escape in character literal: \\" + escaped,
                    "Use valid escapes.",
                    example, 
                    null,  
                    errorLength);
                return null;
            }

        } else {
            value = String.valueOf(c);
        }

        if (isAtEnd()) {
            String example = "character charVariable = '" + value + "';";

            advance();
            lexicalError(
                DiagnosticCode.E004,
                "Unclosed character literal.",
                "Add closing '.",
                example,
                null, 
                1);
            return null;
        }

        if (peek() != '\'') {
            this.stringBuilder.append(value);

            while (!isAtEnd() && peek() != '\'') {
                this.stringBuilder.append(advance());
            }

            String allChars = this.stringBuilder.toString();
            errorLength = allChars.length();

            String example = "character charVariable = '" + value + "';\n" +
                            "// or\n" +
                            "  String stringVariable = \"" + allChars + "\";";

            lexicalError(
                DiagnosticCode.E103,
                "Character literal can only contain one character.",
                "Remove extra characters or use a string literal.",
                example,
                "Furthermore, character types can only have one character",
                errorLength);
        }

        advance();
        return value;
    }   

    private String readIdentifier() {
        int start = position;
        advance();
        
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            advance();
        }

        int len = position - start;
        String ident = new String(input, start, len);

        return canonical(ident);
    }

    private String readNumber() {
        this.stringBuilder.setLength(0);

        while (Character.isDigit(peek())) {
            this.stringBuilder.append(advance());
        }

        if (peek() == '.' && Character.isDigit(peekNext())) {
            this.stringBuilder.append(advance());

            while (Character.isDigit(peek())) {
                this.stringBuilder.append(advance());
            }
        }

        if (Character.isLetter(peek())) {
            String example = "integer numberVariable = " + this.stringBuilder.toString() + ";";

            this.stringBuilder.setLength(0);
            while (!isAtEnd() && Character.isLetter(peek())){
                this.stringBuilder.append(advance());
            }

            int errorLenth = this.stringBuilder.length();

            lexicalError(
                    DiagnosticCode.E101,
                    "Invalid numeric literal.",
                    "Numbers cannot be followed by letters.",
                    example, null, (errorLenth));
        }

        return this.stringBuilder.toString();
    }

    private boolean isAtEnd() {
        return position >= this.length;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';

        }

        return this.input[position];
    }

    private char peekNext() {
        int next = position + 1;

        if (next >= this.length) {
            return '\0';

        }

        return this.input[next];
    }

    private char advance() {
        char c = peek();
        position++;
        column++;
        return c;
    }

    private boolean match(char expected) {

        if (isAtEnd()) {
            return false;
        }

        if (peek() != expected) {
            return false;
        }

        advance();
        return true;
    }

    private void lexicalError(
            DiagnosticCode code,
            String cause,
            String fix,
            String example,
            String note,
            int lenth) {

        throw new LexicalException(
                code,
                new SourceLocation(filePath, line, Math.max(column - 1, 0)),
                sourceManager.getContextLines(line, 2),
                cause,
                fix,
                example,
                note,
            lenth);
    }

    private void initialzerhashMapTokensTypes() {
        // Keywords
        tokensTypeByString.put("return", TokenType.RETURN);
        tokensTypeByString.put("if", TokenType.IF);
        tokensTypeByString.put("otherwise", TokenType.OTHERWISE);
        tokensTypeByString.put("afterall", TokenType.AFTERALL);
        tokensTypeByString.put("for", TokenType.FOR);
        tokensTypeByString.put("while", TokenType.WHILE);
        tokensTypeByString.put("break", TokenType.BREAK);
        tokensTypeByString.put("continue", TokenType.CONTINUE);
        tokensTypeByString.put("public", TokenType.PUBLIC);
        tokensTypeByString.put("internal", TokenType.INTERNAL);
        tokensTypeByString.put("protected", TokenType.PROTECTED);
        tokensTypeByString.put("static", TokenType.STATIC);
        tokensTypeByString.put("true", TokenType.TRUE);
        tokensTypeByString.put("false", TokenType.FALSE);
        tokensTypeByString.put("integer", TokenType.INTEGER);
        tokensTypeByString.put("try", TokenType.TRY);
        tokensTypeByString.put("catch", TokenType.CATCH);
        tokensTypeByString.put("double", TokenType.DOUBLE);
        tokensTypeByString.put("boolean", TokenType.BOOLEAN);
        tokensTypeByString.put("character", TokenType.CHARACTER_TYPE);
        tokensTypeByString.put("void", TokenType.VOID);
        tokensTypeByString.put("null", TokenType.NULL);
        tokensTypeByString.put("new", TokenType.NEW);
        tokensTypeByString.put("Use", TokenType.USE);
        tokensTypeByString.put("or", TokenType.OR);
        tokensTypeByString.put("and", TokenType.AND);
        tokensTypeByString.put("because", TokenType.BECAUSE);

        // References
        tokensTypeByString.put("String", TokenType.STRING_TYPE);

        // Single-Characters
        singleCharTokens['('] = TokenType.LPAREN;
        singleCharTokens[')'] = TokenType.RPAREN;
        singleCharTokens['{'] = TokenType.LBRACE;
        singleCharTokens['}'] = TokenType.RBRACE;
        singleCharTokens['['] = TokenType.LBRACKET;
        singleCharTokens[']'] = TokenType.RBRACKET;
        singleCharTokens[','] = TokenType.COMMA;
        singleCharTokens[';'] = TokenType.SEMICOLON;
        singleCharTokens[':'] = TokenType.COLON;
        singleCharTokens['.'] = TokenType.DOT;
        singleCharTokens['+'] = TokenType.PLUS;
        singleCharTokens['-'] = TokenType.MINUS;
        singleCharTokens['*'] = TokenType.MULTIPLY;
        singleCharTokens['/'] = TokenType.DIVISION;
        singleCharTokens['%'] = TokenType.REMAINDER;
        singleCharTokens['='] = TokenType.ASSIGNMENT;
        singleCharTokens['<'] = TokenType.LT;
        singleCharTokens['>'] = TokenType.GT;
        singleCharTokens['!'] = TokenType.BANG;
        // Specials
        singleCharTokens['@'] = TokenType.AT;
    }
}
