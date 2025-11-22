package org.klang.core.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.klang.core.errors.Diagnostic;
import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticType;
import org.klang.core.errors.Note;
import org.klang.core.errors.Span;

public class Lexer {
    List<Token> tokens = new ArrayList<>();
    int position = 0, line = 1, column = 0;
    String source;
    String filePath;
    HashMap<String, TokenType> tokensTypeByString = new HashMap<>();
    HashMap<Character, TokenType> tokensTypeByChar = new HashMap<>();

    /**
     * Tokenizes the source code into a list of tokens.
     * 
     * @return List of tokens extracted from the source code
     */
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            char c = peek();

            // whitespace
            if (Character.isWhitespace(c)) {
                advance();
                if (c == '\n') {
                    line++;
                    column = 0;
                }
                continue;
            }

            // strings
            if (c == '"') {
                advance(); // consume opening quote
                String content = readString(); // now readString doesn't know about the quote
                tokens.add(new Token(TokenType.STRING_LITERAL, content));
                continue;
            }

            // char literal
            if (c == '\'') {
                advance(); // consume opening apostrophe
                String content = readCharacter();
                tokens.add(new Token(TokenType.CHARACTER_LITERAL, content));
                continue;
            }

            // identifiers / keywords
            if (Character.isLetter(c) || c == '_' || c == '$') {
                String ident = readIdentifier();

                TokenType type = tokensTypeByString.getOrDefault(ident, TokenType.IDENTIFIER);

                if (type == TokenType.IDENTIFIER)
                    tokens.add(new Token(type, ident));
                else
                    tokens.add(new Token(type));
                continue;
            }

            // numbers
            if (Character.isDigit(c)) {
                String num = readNumber();
                tokens.add(new Token(TokenType.NUMBER, num));
                continue;
            }

            if (peek() == '/' && peekNext() == '/') {
                // line comment
                advance();
                advance(); // consume '//'
                while (!isAtEnd() && peek() != '\n')
                    advance();
                continue;
            }

            if (peek() == '/' && peekNext() == '*') {
                advance();
                advance(); // consume '/*'
                while (!isAtEnd()) {
                    if (peek() == '*' && peekNext() == '/') {
                        advance();
                        advance(); // consume '*/'
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

            TokenType tokenType = tokensTypeByChar.getOrDefault(c, null);

            switch (c) {
                case '=' -> {
                    advance();

                    if (match('=')) {
                        tokens.add(new Token(TokenType.DOUBLEEQUAL));
                    } else {
                        tokens.add(new Token(tokenType));
                    }

                    continue;
                }

                case '+' -> {
                    advance();
                    if (match('+')) {
                        tokens.add(new Token(TokenType.INCREMENT));

                    } else {
                        tokens.add(new Token(tokenType));
                    }

                    continue;

                }

                case '-' -> {
                    advance();
                    if (match('-')) {
                        tokens.add(new Token(TokenType.DECREMENT));

                    } else if (match('>')) {
                        tokens.add(new Token(TokenType.ARROW));

                    } else {
                        tokens.add(new Token(tokenType));
                    }

                    continue;
                }

                case '*' -> {
                    advance();
                    if (match('*')) {
                        tokens.add(new Token(TokenType.POWER));

                    } else {
                        tokens.add(new Token(tokenType));
                    }

                    continue;
                }

                case '/' -> {
                    advance();
                    tokens.add(new Token(TokenType.DIVISION));
                    continue;
                }

                case '>' -> {
                    advance();

                    if (match('=')) {
                        tokens.add(new Token(TokenType.GTE));

                    } else {
                        tokens.add(new Token(tokenType));

                    }

                    continue;
                }

                case '<' -> {
                    advance();

                    if (match('=')) {
                        tokens.add(new Token(TokenType.LTE));

                    } else {
                        tokens.add(new Token(tokenType));

                    }

                    continue;
                }

                case '!' -> {
                    advance();

                    if (match('=')) {
                        tokens.add(new Token(TokenType.NOTEQUAL));

                    } else {
                        tokens.add(new Token(TokenType.BANG));

                    }

                    continue;
                }

                case '&' -> {
                    advance();

                    if (match('&')) {
                        tokens.add(new Token(TokenType.AND));

                        continue;
                    }

                    error("Unexpected character '" + c + "'.", "Did you mean '&&'?", DiagnosticType.LEXICAL);

                }

                case '|' -> {
                    advance(); // consume first '|'
                    if (match('|')) { // check/consume second '|'
                        tokens.add(new Token(TokenType.OR));
                    } else {
                        error("Unexpected character '|'", "Did you mean '||'?", DiagnosticType.LEXICAL);
                    }
                    continue;
                }

            }

            if (tokenType == null) {
                error("Unexpected character '" + c + "'", "Remove this.", DiagnosticType.LEXICAL);
            }

            tokens.add(new Token(tokenType));
            advance();
        }

        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }

    /**
     * Checks if the lexer has reached the end of the source code.
     * 
     * @return true if at end of source, false otherwise
     */
    private boolean isAtEnd() {
        return position >= source.length();
    }

    /**
     * Peeks at the current character without consuming it.
     * 
     * @return Current character or '\0' if at end of source
     */
    private char peek() {
        if (isAtEnd())
            return '\0';

        return source.charAt(position);
    }

    /**
     * Peeks at the next character without consuming it.
     * 
     * @return Next character or '\0' if beyond source bounds
     */
    private char peekNext() {
        if (position + 1 >= source.length())
            return '\0';
        return source.charAt(position + 1);
    }

    /**
     * Advances to the next character and updates position and column.
     * 
     * @return The character that was just consumed
     */
    private char advance() {
        char c = peek();
        position++;
        column++;
        return c;
    }

    /**
     * Checks if the current character matches the expected character and consumes
     * it if so.
     * 
     * @param expected The character to match
     * @return true if matched and consumed, false otherwise
     */
    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (peek() != expected)
            return false; // look at current character (after a previous advance())
        advance(); // consume this character
        return true;
    }

    /**
     * Reads a string literal from the source code, handling escape sequences.
     * 
     * @return The string content without the surrounding quotes
     */
    private String readString() {
        StringBuilder s = new StringBuilder();

        while (!isAtEnd()) {
            char c = advance();

            // String closed
            if (c == '"') {
                return s.toString();
            }

            // Multiline not allowed
            if (c == '\n') {
                error("String cannot contain line break.",
                        "Close the string before the end of the line.",
                        DiagnosticType.LEXICAL);
            }

            // Escape
            if (c == '\\') {
                if (isAtEnd()) {
                    error("Unclosed string: unexpected end of file",
                            "Expected \"",
                            DiagnosticType.LEXICAL);
                }

                char escaped = advance();

                switch (escaped) {
                    case 'n' -> s.append("\\n");
                    case 't' -> s.append("\\t");
                    case '"' -> s.append("\\\"");
                    case '\\' -> s.append("\\\\");
                    default -> error(
                            "Invalid escape sequence: \\" + escaped,
                            "Use valid escapes: \\n, \\t, \\\", \\\\.",
                            DiagnosticType.LEXICAL);
                }

                continue;
            }

            // Normal character
            s.append(c);
        }

        error("Unclosed string: unexpected end of file",
                "Expected \"",
                DiagnosticType.LEXICAL);
        return null; // unreachable
    }

    /**
     * Reads a character literal from the source code, handling escape sequences.
     * 
     * @return The character content without the surrounding single quotes
     */
    private String readCharacter() {

        if (isAtEnd()) {
            error("Unclosed character.",
                    "Expected '",
                    DiagnosticType.LEXICAL);
        }

        char c = advance();

        // Literal cannot have line break
        if (c == '\n') {
            error("Character literal cannot contain line break.",
                    "Put only one valid character between ''.",
                    DiagnosticType.LEXICAL);
        }

        String value;

        if (c == '\\') {
            if (isAtEnd()) {
                error("Unclosed character.",
                        "Expected '",
                        DiagnosticType.LEXICAL);
            }

            char escaped = advance();

            switch (escaped) {
                case 'n' -> value = "\\n";
                case 't' -> value = "\\t";
                case '\'' -> value = "\\'";
                case '\\' -> value = "\\\\";
                default -> {
                    error("Invalid escape: \\" + escaped,
                            "Use valid escapes: \\n, \\t, \\', \\\\.",
                            DiagnosticType.LEXICAL);
                    return null; // unreachable
                }
            }
        } else {
            // Check if there are more characters without closing
            if (peek() != '\'') {
                error("Character literal with more than one character.",
                        "A literal should be like 'a' or '\\n'.",
                        DiagnosticType.LEXICAL);
            }
            value = String.valueOf(c);
        }

        // Expected closing '
        if (isAtEnd() || peek() != '\'') {
            error("Unclosed character.",
                    "Expected '",
                    DiagnosticType.LEXICAL);
        }

        advance(); // consume the '

        return value;
    }

    /**
     * Reads an identifier or keyword from the source code.
     * 
     * @return The identifier string
     */
    private String readIdentifier() {
        StringBuilder s = new StringBuilder();
        s.append(advance()); // consumes first char (letter or '_')
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            s.append(advance());
        }
        return s.toString();
    }

    /**
     * Reads a number (integer or decimal) from the source code.
     * 
     * @return The number as a string
     */
    private String readNumber() {
        StringBuilder s = new StringBuilder();

        while (Character.isDigit(peek())) {
            s.append(advance());
        }

        // optional fraction
        if (peek() == '.') {
            if (Character.isDigit(peekNext())) {

                s.append(advance()); // consume '.'
                while (Character.isDigit(peek())) {
                    s.append(advance());
                }
            } else {
                error("Invalid decimal", "After '.' there must be a digit", DiagnosticType.LEXICAL);

            }
        }

        if (peek() == '_') {
            if (Character.isDigit(peekNext())) {

                s.append(advance()); // consume '_'
                while (Character.isDigit(peek())) {
                    s.append(advance());
                }
            } else {
                error("Invalid integer", "After '_' there must be a digit", DiagnosticType.LEXICAL);

            }
        }

        if (Character.isLetter(peek())) {
            error("Number followed by invalid identifier.",
                    "Identifiers cannot start with digits.",
                    DiagnosticType.LEXICAL);
        }

        return s.toString();
    }

    /**
     * Issues a formatted diagnostic error and throws an exception.
     *
     * @param message   Error message that will be displayed
     * @param note      Note or tip on what to do to solve the problem
     * @param typeError Type of error to be issued (e.g., ERROR, WARNING)
     */
    private void error(String message, String note, DiagnosticType typeError) {
        Span span = new Span(filePath, line, column, line, column + 1);
        Diagnostic d = new Diagnostic(
                typeError,
                message,
                span).addNote(new Note(note));

        // Print the formatted error before throwing

        throw new DiagnosticException(d);
    }

    /**
     * Initializes the hash maps that map strings and characters to their
     * corresponding token types.
     */
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
        tokensTypeByString.put("private", TokenType.PRIVATE);
        tokensTypeByString.put("protected", TokenType.PROTECTED);
        tokensTypeByString.put("static", TokenType.STATIC);
        tokensTypeByString.put("true", TokenType.TRUE);
        tokensTypeByString.put("false", TokenType.FALSE);
        tokensTypeByString.put("integer", TokenType.INTEGER);
        tokensTypeByString.put("double", TokenType.DOUBLE);
        tokensTypeByString.put("boolean", TokenType.BOOLEAN);
        tokensTypeByString.put("character", TokenType.CHARACTER_TYPE);
        tokensTypeByString.put("void", TokenType.VOID);
        tokensTypeByString.put("fresh", TokenType.FRESH);

        // References
        tokensTypeByString.put("String", TokenType.STRING_TYPE);

        // Single-Characters
        tokensTypeByChar.put('(', TokenType.LPAREN);
        tokensTypeByChar.put(')', TokenType.RPAREN);
        tokensTypeByChar.put('{', TokenType.LBRACE);
        tokensTypeByChar.put('}', TokenType.RBRACE);
        tokensTypeByChar.put('[', TokenType.LBRACKET);
        tokensTypeByChar.put(']', TokenType.RBRACKET);
        tokensTypeByChar.put(',', TokenType.COMMA);
        tokensTypeByChar.put(';', TokenType.SEMICOLON);
        tokensTypeByChar.put(':', TokenType.COLON);
        tokensTypeByChar.put('.', TokenType.DOT);
        tokensTypeByChar.put('+', TokenType.PLUS);
        tokensTypeByChar.put('-', TokenType.MINUS);
        tokensTypeByChar.put('*', TokenType.MULTIPLY);
        tokensTypeByChar.put('/', TokenType.DIVISION);
        tokensTypeByChar.put('%', TokenType.REMAINDER);
        tokensTypeByChar.put('=', TokenType.ASSIGNMENT);
        tokensTypeByChar.put('<', TokenType.LT);
        tokensTypeByChar.put('>', TokenType.GT);
        tokensTypeByChar.put('!', TokenType.BANG);

        // Specials
        tokensTypeByChar.put('@', TokenType.AT);
    }

    /**
     * Constructs a new Lexer with the given source code and file path.
     * 
     * @param source   The source code to tokenize
     * @param filePath The path of the source file (for error reporting)
     */
    public Lexer(String source, String filePath) {
        this.source = source;
        this.filePath = filePath;

        initialzerhashMapTokensTypes();
    }

    /**
     * Tests the tokenizer with a simple integer declaration.
     * Prints all generated tokens to standard output.
     */
    public void testTokenize() {
        this.source = "integer x = 10;";

        tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}