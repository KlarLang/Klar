package org.klang.core.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.klang.core.diagnostics.DiagnosticCode;
import org.klang.core.errors.LexicalException;
import org.klang.core.errors.SourceLocation;
import org.klang.core.errors.SourceManager;

/**
 * Lexical Analyzer (Lexer) for the Klang programming language.
 * <p>The Lexer is the first phase of the compiler pipeline. It takes the raw source code
 * as input and breaks it down into a sequence of atomic units called {@link Token}s.
 * This process is known as tokenization or lexical analysis.</p>
 * <p>Key responsibilities of this Lexer:</p>
 * <ul>
 * <li><strong>Tokenization:</strong> Converts character streams into meaningful tokens (Identifiers, Keywords, Literals, Operators).</li>
 * <li><strong>Whitespace Handling:</strong> Skips insignificant whitespace and tracks line/column numbers for error reporting.</li>
 * <li><strong>Comment Handling:</strong> Ignores both single-line ({@code //}) and multi-line ({@code /* ... *&#47;}) comments.</li>
 * <li><strong>Syntax Enforcement:</strong> Enforces Klang-specific lexical rules, such as prohibiting C-style increment/decrement operators ({@code ++}, {@code --}) and logical operators ({@code &&}, {@code ||}) in favor of Klang's idiomatic syntax.</li>
 * <li><strong>String Interning:</strong> Uses a symbol table to canonicalize identifiers, reducing memory usage.</li>
 * </ul>
 * @author Lucas Paulino Da Silva (~K')
 * @since 0.1
 */
public class Lexer {
    
    private int position = 0;
    private int line = 1;
    private int column = 0;
    
    private final String source;
    private final String filePath;
    
    private final char[] input;
    private final int length;

    private final ArrayList<Token> tokens;

    private final SourceManager sourceManager;
    private final StringBuilder stringBuilder = new StringBuilder(255);

    private final HashMap<String, TokenType> tokensTypeByString = new HashMap<>(65,1.0f);
    private final TokenType[] singleCharTokens = new TokenType[128];

    private final Map<String, String> symbolTable = new HashMap<>(512, 0.75f);

    /**
     * Constructs a new Lexer instance.
     *
     * @param source The raw source code string to be analyzed.
     * @param filePath The path to the file being processed (used for error reporting).
     * @param sourceManager The manager responsible for handling source context and diagnostics.
     */
    public Lexer(String source, String filePath, SourceManager sourceManager) {
        this.source = source;
        this.filePath = filePath;

        this.input = source.toCharArray();
        this.length = input.length;

        this.sourceManager = sourceManager;
        // Optimization: Estimate token count to avoid frequent array resizing
        int estimedTokens = Math.max(16, source.length() / 4);
        this.tokens = new ArrayList<>(estimedTokens);

        initialzerhashMapTokensTypes();
    }

    /**
     * Canonicalizes identifier strings using a symbol table (String Interning).
     * <p>This ensures that identical identifiers share the same String instance in memory,
     * which optimizes memory usage and speeds up equality checks in later stages of compilation.</p>
     * @param s The string to canonicalize.
     * @return The canonical instance of the string.
     */
    private String canonical(String s){
        String existing = symbolTable.get(s);

        if (existing != null){
            return existing;
        } 

        symbolTable.put(s, s);
        return s;
    }

    /**
     * Performs the main tokenization loop.
     * <p>Scans the input character by character until the End Of File (EOF) is reached.
     * It delegates specific patterns (strings, numbers, identifiers) to specialized methods
     * and handles single-character tokens and operators directly.</p>
     * <p>This method explicitly validates and rejects C-style operators that are not supported
     * in Klang (e.g., {@code ++}, {@code --}, {@code &&}, {@code ||}), providing helpful
     * diagnostic messages suggesting the correct Klang alternatives.</p>
     * @return A list of tokens representing the source code.
     * @throws LexicalException if an invalid character or malformed literal is encountered.
     */
    public List<Token> tokenizeSourceCode() {
        this.stringBuilder.setLength(0);
        while (!isAtEnd()) {

            char c = peek();

            // Handle Whitespace
            if (Character.isWhitespace(c)) {
                advance();
                if (c == '\n') {
                    line++;
                    column = 0;
                }
                continue;
            }

            // Handle String Literals
            if (c == '"') {
                int startLine = this.line;
                int startColumn = this.column; // Captura coluna inicial

                advance();

                String content = readString(startLine, startColumn);
                tokens.add(new Token(
                        TokenType.STRING_LITERAL,
                        content,
                        line,
                        startColumn)); // CORREÇÃO: Usa startColumn, não position

                continue;
            }

            // Handle Character Literals
            if (c == '\'') {
                int startColumn = this.column; // Captura coluna inicial
                advance();

                String content = readCharacter();
                tokens.add(new Token(
                        TokenType.CHARACTER_LITERAL,
                        content,
                        line,
                        startColumn)); // CORREÇÃO: Usa startColumn, não position

                continue;
            }

            // Handle Identifiers and Keywords
            if (Character.isLetter(c) || c == '_' || c == '$') {
                int startColumn = this.column; // Captura coluna inicial
                
                if (c == '$' && !(Character.isLetter(peekNext()) || peekNext() == '_')) {
                    String example = "integer $validName = 10; // ok\n integer $ = 10; // invalid"; 

                    lexicalError(
                            DiagnosticCode.E001,
                            "Invalid identifier format.",
                            "Identifiers starting with '$' must be followed by a letter or underscore.",
                            example, 
                            null, 
                            1);
                }

                String ident = readIdentifier();
                ident = canonical(ident);
                TokenType tokenType = tokensTypeByString.getOrDefault(ident, TokenType.IDENTIFIER);

                if (tokenType == TokenType.IDENTIFIER) {
                    tokens.add(new Token(tokenType, ident, line, startColumn));
                } else {
                    tokens.add(new Token(tokenType, line, startColumn));
                }

                continue;
            }

            // Handle Numbers
            if (Character.isDigit(c)) {
                int startColumn = this.column; // Captura coluna inicial
                String[] data = readNumber();
                String num = data[0];

                if (data[1].equals("true")){
                    tokens.add(new Token(TokenType.DOUBLE_LITERAL, num, line, startColumn));
                } else {
                    tokens.add(new Token(TokenType.INTEGER_LITERAL, num, line, startColumn));
                }

                continue;
            }

            // Handle Comments (Single-line)
            if (peek() == '/' && peekNext() == '/') {
                advance();
                advance();

                while (!isAtEnd() && peek() != '\n') {
                    advance();
                }

                continue;
            }

            // Handle Comments (Multi-line)
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

            // Handle Operators and Symbols
            // CRUCIAL: Captura a coluna ANTES de avançar no switch
            int tokenStart = this.column;
            TokenType tokenType = c < 128 ? singleCharTokens[c] : null;
            this.stringBuilder.setLength(0);
            
            switch (c) {
                case '@':
                    advance();
                    tokens.add(TokenFactory.simple(TokenType.AT, line, tokenStart));
                    continue;

                case '=':
                    advance();
                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.DOUBLEEQUAL, line, tokenStart));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.ASSIGNMENT, line, tokenStart));
                    }
                    continue;

                case '+':
                    advance();
                    this.stringBuilder.append('+');
                    if (peek() == '+') {
                        while (peek() == '+') {
                            this.stringBuilder.append(advance());
                        }
                        
                        lexicalError(
                            DiagnosticCode.E001,
                            "Unsupported operator '" + this.stringBuilder.toString() + "'.",
                            "The increment operator '++' is not supported in K. Use explicit assignment.",
                            "x = x + 1;",
                            "K favors explicit modification over implicit increment effects.", 
                            this.stringBuilder.length());

                    } else {
                        tokens.add(TokenFactory.simple(TokenType.PLUS, line, tokenStart));
                    }
                    continue;

                case '.':
                    advance();
                    tokens.add(TokenFactory.simple(TokenType.DOT, line, tokenStart));
                    continue;

                case '-':
                    advance();
                    if (peek() == '-') {
                        this.stringBuilder.append('-');
                        while (peek() == '-') {
                            this.stringBuilder.append(advance());
                        }
                        
                        lexicalError(
                            DiagnosticCode.E001,
                            "Unsupported operator '" + this.stringBuilder.toString() + "'.",
                            "The decrement operator '--' is not supported in K. Use explicit assignment.",
                            "x = x - 1;",
                            "K favors explicit modification over implicit decrement effects.",
                            this.stringBuilder.length());
                            
                    } else if (peek() == '>') {
                        this.stringBuilder.append('>');
                        while (peek() == '-' || peek() == '>') {
                            this.stringBuilder.append(advance());
                        }
                        
                        lexicalError(
                            DiagnosticCode.E001,
                            "Invalid operator sequence '" + this.stringBuilder.toString() + "'.",
                            "This sequence is not recognized as a valid operator.",
                            null,
                            null, 
                            this.stringBuilder.length());
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.MINUS, line, tokenStart));
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
                            "Unsupported operator '" + this.stringBuilder.toString() + "'.",
                            "The power operator '**' is not supported.",
                            "double res = Mathematics.power(base, exponent);",
                            "Use the standard Mathematics library for exponentiation.",
                            this.stringBuilder.length());
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.MULTIPLY, line, tokenStart));
                    }
                    continue;

                case '>':
                    advance();
                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.GTE, line, tokenStart));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.GT, line, tokenStart));
                    }
                    continue;

                case '<':
                    advance();
                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.LTE, line, tokenStart));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.LT, line, tokenStart));
                    }
                    continue;

                case '!':
                    advance();
                    if (match('=')) {
                        tokens.add(TokenFactory.simple(TokenType.NOTEQUAL, line, tokenStart));
                    } else {
                        tokens.add(TokenFactory.simple(TokenType.BANG, line, tokenStart));
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
                            "Unsupported operator '" + this.stringBuilder.toString() + "'.",
                            "Use the keyword 'and' for logical conjunction.",
                            "if (isValid and isReady) { ... }",
                            "K uses readable keywords ('and', 'or') instead of C-style symbols.", 
                            this.stringBuilder.length());
                    break;

                case '|':
                    advance();
                    this.stringBuilder.append("|");

                    while (peek() == '|') {
                        this.stringBuilder.append(advance());
                    }

                    lexicalError(
                            DiagnosticCode.E001,
                            "Unsupported operator '" + this.stringBuilder.toString() + "'.",
                            "Use the keyword 'or' for logical disjunction.",
                            "if (isA or isB) { ... }",
                            "K uses readable keywords ('and', 'or') instead of C-style symbols.", 
                            this.stringBuilder.length());
                    break;
            }

            if (tokenType == null) {
                lexicalError(
                        DiagnosticCode.E001,
                        "Unexpected character '" + c + "'.",
                        "Remove or replace this character.",
                        null, 
                        "This character is not valid in the source code context.",
                        1
                    );
            }

            // CORREÇÃO: Usa tokenStart aqui também, pois é um singleCharToken
            tokens.add(TokenFactory.simple(tokenType, line, tokenStart));
            advance();
        }

        tokens.add(TokenFactory.simple(TokenType.EOF, line, column));
        tokens.trimToSize();
        return tokens;
    }

    /**
     * Reads a string literal from the input.
     * <p>Handles escape sequences (e.g., {@code \n}, {@code \t}, {@code \"}) and checks for
     * unclosed strings or line breaks within the string (which are not allowed in Klang).</p>
     * @param startLine The line number where the string started.
     * @param startColumn The column number where the string started.
     * @return The content of the string literal.
     * @throws LexicalException if the string is unclosed or contains invalid escapes.
     */
    private String readString(int startLine, int startColumn) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("\"");
        String example = "\"" + this.stringBuilder.toString().strip() + "\"";

        while (!isAtEnd()) {
            char c = advance();
            if (c == '"') {
                this.stringBuilder.append("\"");
                return this.stringBuilder.toString();
            }

            if (c == '\n') {
                int errorLength = this.stringBuilder.length();

                lexicalError(
                        DiagnosticCode.E002,
                        "Unterminated string literal.",
                        "String literals cannot span multiple lines. Close the string before the line break.",
                        example, 
                        null, 
                        errorLength
                );
            }

            if (c == '\\') {
                if (isAtEnd()) {
                    int errorLength = this.stringBuilder.length();
                    lexicalError(
                            DiagnosticCode.E002,
                            "Unterminated string literal at EOF.",
                            "Add a closing quote '\"' to the string.",
                            example, 
                            null, 
                            errorLength);
                }

                char escaped = advance();

                if (escaped == 'n') {
                    this.stringBuilder.append("\\n");
                } else if (escaped == 't') {
                    this.stringBuilder.append("\\t");
                } else if (escaped == '"') {
                    this.stringBuilder.append('"');
                } else if (escaped == '\\') {
                    this.stringBuilder.append("\\");
                } else {
                    int errorLength = this.stringBuilder.length();

                    lexicalError(
                            DiagnosticCode.E001,
                            "Invalid escape sequence '\\" + escaped + "'.",
                            "Use valid escape sequences like \\n, \\t, \\\", or \\\\.", 
                            example, 
                            null, 
                            errorLength
                    );
                }
                continue;
            }
            this.stringBuilder.append(c);
        }

        int errorLength = this.stringBuilder.length();
        lexicalError(
                DiagnosticCode.E002,
                "Unterminated string literal.",
                "Add a closing quote '\"' before the end of the file.",
                example, 
                null, 
                errorLength
        );
        return null;
    }

    /**
     * Reads a character literal from the input.
     * <p>Ensures the literal contains exactly one character. Handles escape sequences
     * within character literals (e.g., {@code '\n'}).</p>
     * @return The string representation of the character literal.
     * @throws LexicalException if the literal is empty, contains multiple characters, or is unclosed.
     */
    private String readCharacter() {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("\'");

        int errorLength = 1;
        
        if (isAtEnd()) {
            String example = "\'c\'";
            advance();
            lexicalError(
                DiagnosticCode.E104,
                "Unclosed character literal at EOF.",
                "Add a closing single quote \"'\" .",
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
                    DiagnosticCode.E104,
                    "Unclosed character literal escape.",
                    "Complete the escape sequence and close the quote.",
                    example, 
                    null, 
                    errorLength + 2);
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
                    DiagnosticCode.E001,
                    "Invalid escape sequence in character literal: \\" + escaped,
                    "Use valid escapes like \\n, \\t, \\', or \\\\.",
                    example, 
                    null,  
                    errorLength);
                return null;
            }

        } else {
            value = String.valueOf(c);
        }

        if (isAtEnd()) {
            String example = "character c = '" + value + "';";
            advance();
            lexicalError(
                DiagnosticCode.E104,
                "Unclosed character literal.",
                "Add a closing single quote \"'\".",
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

            String example = "String s = \"" + allChars + "\"; // Use double quotes for strings";

            lexicalError(
                DiagnosticCode.E104,
                "Invalid character literal length.",
                "Character literals must contain exactly one character. Use double quotes for strings.",
                example,
                "Type 'character' holds a single Unicode code point.",
                errorLength);
        }

        advance(); // Consume closing '
        c += '\'';
        value = String.valueOf(c);
        
        return value;
    }   

    /**
     * Reads an identifier or keyword from the input.
     * <p>Scans alphanumeric characters and underscores. The resulting string is
     * canonicalized to optimize storage.</p>
     * @return The scanned identifier string.
     */
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

    /**
     * Reads a numeric literal (integer or floating point) from the input.
     * <p>Validates that the number is not immediately followed by letters, which would
     * indicate a malformed identifier or invalid syntax.</p>
     * @return The string representation of the number.
     * @throws LexicalException if the number format is invalid.
     */
    private String[] readNumber() {
        this.stringBuilder.setLength(0);
        String isDouble = "false";

        this.stringBuilder.append(advance());

        while (Character.isDigit(peek())) {
            this.stringBuilder.append(advance());
        }

        if (peek() == '.' && Character.isDigit(peekNext())) {
            isDouble = "true";
            this.stringBuilder.append(advance());

            while (Character.isDigit(peek())) {
                this.stringBuilder.append(advance());
            }
        }

        if (Character.isLetter(peek())) {
            String example = "integer n = " + this.stringBuilder.toString() + ";";

            this.stringBuilder.setLength(0);
            while (!isAtEnd() && Character.isLetter(peek())){
                this.stringBuilder.append(advance());
            }

            int errorLenth = this.stringBuilder.length();

            lexicalError(
                    DiagnosticCode.E101,
                    "Malformed numeric literal.",
                    "Numbers cannot be immediately followed by letters.",
                    example, 
                    "Separate the number and the identifier with whitespace or an operator.", 
                    (errorLenth));
        }

        return new String[]{this.stringBuilder.toString(), isDouble};
    }

    // Utility methods

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

    /**
     * Reports a lexical error using the SourceManager.
     * @param code The diagnostic error code.
     * @param cause The cause of the error.
     * @param fix A suggested fix for the user.
     * @param example An example of correct usage.
     * @param note Additional notes or context.
     * @param lenth The length of the erroneous segment for highlighting.
     */
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

    /**
     * Initializes the lookup tables for keywords and single-character tokens.
     */
    private void initialzerhashMapTokensTypes() {
        // Keywords
        tokensTypeByString.put("return", TokenType.RETURN);
        tokensTypeByString.put("if", TokenType.IF);
        tokensTypeByString.put("otherwise", TokenType.OTHERWISE);
        tokensTypeByString.put("afterall", TokenType.AFTERALL);
        tokensTypeByString.put("while", TokenType.WHILE);
        tokensTypeByString.put("public", TokenType.PUBLIC);
        tokensTypeByString.put("internal", TokenType.INTERNAL);
        tokensTypeByString.put("protected", TokenType.PROTECTED);
        tokensTypeByString.put("true", TokenType.TRUE);
        tokensTypeByString.put("false", TokenType.FALSE);
        tokensTypeByString.put("integer", TokenType.INTEGER_TYPE);
        tokensTypeByString.put("try", TokenType.TRY);
        tokensTypeByString.put("catch", TokenType.CATCH);
        tokensTypeByString.put("double", TokenType.DOUBLE_TYPE);
        tokensTypeByString.put("boolean", TokenType.BOOLEAN_TYPE);
        tokensTypeByString.put("character", TokenType.CHARACTER_TYPE);
        tokensTypeByString.put("void", TokenType.VOID);
        tokensTypeByString.put("null", TokenType.NULL);
        tokensTypeByString.put("new", TokenType.NEW);
        tokensTypeByString.put("Use", TokenType.IDENTIFIER);
        tokensTypeByString.put("or", TokenType.OR);
        tokensTypeByString.put("and", TokenType.AND);
        tokensTypeByString.put("module", TokenType.MODULE);
        tokensTypeByString.put("import", TokenType.IMPORT);
        tokensTypeByString.put("because", TokenType.BECAUSE);
        tokensTypeByString.put("constant", TokenType.CONSTANT);

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