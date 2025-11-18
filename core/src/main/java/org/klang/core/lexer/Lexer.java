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

    public List<Token> tokenize() {
        StringBuilder s = new StringBuilder();

        while (!EOF()) {
            s.setLength(0); // Limpar builder
            char c = source.charAt(position);
            column++;

            if (Character.isWhitespace(c)) {
                position++;

                if (isNewLine(c)) {
                    line++;
                    column = 0;
                }

                continue;
            }

            if (c == '\"') {
                s.append(c);
                s.append(readString(c));
                s.append(c);
                position++;

                tokens.add(new Token(TokenType.STRING, s.toString()));

                continue;
            }

            if (c == '\'') {
                s.append(c);
                s.append(readCharacter(c));
                s.append(c);
                position++;

                tokens.add(new Token(TokenType.CHARACTER, s.toString()));

                continue;
            }

            if (Character.isLetter(c)) {
                s.append(c);

                while (hasNext()) {
                    position++;
                    char demaisChars = source.charAt(position);

                    s.append(demaisChars);
                }

                String value = s.toString();

                TokenType tokenType = tokensTypeByString.getOrDefault(value, TokenType.IDENTIFIER);

                if (tokenType == TokenType.IDENTIFIER) {
                    tokens.add(new Token(tokenType, value));

                } else {
                    tokens.add(new Token(tokenType));
                }

                position++;
                continue;
            }

            if (Character.isDigit(c)) {

                s.append(c);

                while (hasNext()) {
                    position++;
                    char demais = source.charAt(position);

                    s.append(demais);
                }

                String value = s.toString();

                tokens.add(new Token(TokenType.NUMBER, value));
                position++;

                continue;
            }

            TokenType tokenType = tokensTypeByChar.getOrDefault(c, null);

            if (tokenType == null) {
                error("Caractere inesperado '" + c + "'", "Remova esse caractere ou corrija o token!",
                        DiagnosticType.LEXICAL);
            }

            switch (c) {
                case '=' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.DOUBLEEQUAL));
                        position++;

                        continue;
                    }

                    tokens.add(new Token(tokenType));
                    continue;
                }

                case '+' -> {
                    if (getNext() == '+') {
                        tokens.add(new Token(TokenType.INCREMENT));
                        position++;

                        continue;
                    }

                    tokens.add(new Token(tokenType));
                    continue;
                }

                case '-' -> {
                    if (getNext() == '-') {
                        tokens.add(new Token(TokenType.DECREMENT));
                        position++;

                        continue;
                    }

                    if (getNext() == '>') {
                        tokens.add(new Token(TokenType.ARROW));
                        position++;

                        continue;

                    }

                    tokens.add(new Token(tokenType));
                    continue;
                }

                case '>' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.GTE));
                        position++;

                        continue;
                    }

                    tokens.add(new Token(tokenType));
                    continue;
                }

                case '<' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.LTE));
                        position++;

                        continue;
                    }

                    tokens.add(new Token(tokenType));
                    continue;
                }

            }

            tokens.add(new Token(tokenType));
            position++;
        }

        return tokens;

    }

    /**
     * @return Returns the string between "" from cAtual (cAtual is included and
     *         should be = \").
     * 
     * @param cCurrent Current character
     */
    private String readString(char cAtual) {
        StringBuilder s = new StringBuilder();

        position++;
        char c = source.charAt(position);

        while (c != '\"') {
            if (c == '\\') {
                position++;
                if (position >= source.length()) {
                    Span span = new Span(filePath, line, column, line, column + 1);
                    Diagnostic d = new Diagnostic(
                            DiagnosticType.LEXICAL,
                            "String não fechada: fim de arquivo inesperado",
                            span).addNote(new Note("Esperado \""));

                    throw new DiagnosticException(d);
                }
                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                Span span = new Span(filePath, line, column, line, column + 1);
                Diagnostic d = new Diagnostic(
                        DiagnosticType.LEXICAL,
                        "String não fechada: fim de arquivo inesperado",
                        span).addNote(new Note("Esperado \""));

                throw new DiagnosticException(d);
            }

            c = source.charAt(position);
        }

        return s.toString();
    }

    /**
     * @return Returns the char between '', it is returned as String as there is no
     *         use as char.
     * 
     * @param cCurrent Current character
     */
    private String readCharacter(char cAtual) {
        StringBuilder s = new StringBuilder();

        position++;
        int lengthCharacter = 0;
        char c = source.charAt(position);

        while (c != '\'') {
            if (lengthCharacter > 1) {
                error("caractere maior que o esperado",
                        "literais de caractere devem conter exatamente 1 caractere",
                        DiagnosticType.LEXICAL);
            }
            if (c == '\\') {
                position++;

                if (position >= source.length()) {
                    error("String não fechada: fim de arquivo inesperado", "Esperado \'", DiagnosticType.LEXICAL);
                }

                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                error("String não fechada: fim de arquivo inesperado", "Esperado \'", DiagnosticType.LEXICAL);
            }

            c = source.charAt(position);
            lengthCharacter++;
        }

        return s.toString();
    }

    /**
     * Issues a formatted diagnostic error and throws an exception.
     *
     * @param message   Error message that will be displayed.
     * @param note      Note or tip on what to do to solve the problem.
     * @param typeError Type of error to be issued (e.g., ERROR, WARNING).
     */
    private void error(String message, String note, DiagnosticType typeError) {
        Span span = new Span(filePath, line, column, line, column + 1);
        Diagnostic d = new Diagnostic(
                typeError,
                message,
                span).addNote(new Note(note));

        // Imprime o erro formatado antes de lançar

        throw new DiagnosticException(d);
    }

    /**
     * @return Returns whether the current character is a line break.
     * 
     * @param c Current character.
     */
    private boolean isNewLine(char c) {
        return c == '\n';

    }

    /**
     * @return Returns whether there is a next character (letter or number).
     */
    private boolean hasNext() {
        if (EOF()) {
            return false;
        }

        char c = source.charAt(position + 1);
        return Character.isLetter(c) || Character.isDigit(c);
    }

    /**
     * @return Returns the next character.
     */
    private char getNext() {
        if (EOF()) {
            return '\0';
        }

        position++;
        return source.charAt(position);
    }

    /**
     * @return Returns whether this is the end of the source code.
     */
    private boolean EOF() {
        return position >= source.length();
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
        tokensTypeByString.put("private", TokenType.PRIVATE);
        tokensTypeByString.put("protected", TokenType.PROTECTED);
        tokensTypeByString.put("static", TokenType.STATIC);
        tokensTypeByString.put("true", TokenType.TRUE);
        tokensTypeByString.put("false", TokenType.FALSE);
        tokensTypeByString.put("integer", TokenType.INTEGER);
        tokensTypeByString.put("double", TokenType.DOUBLE);
        tokensTypeByString.put("boolean", TokenType.BOOLEAN);
        tokensTypeByString.put("character", TokenType.CHARACTER);
        tokensTypeByString.put("String", TokenType.STRING);

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

        // Multi-Characters
        tokensTypeByString.put("++", TokenType.INCREMENT);
        tokensTypeByString.put("--", TokenType.DECREMENT);
        tokensTypeByString.put("**", TokenType.POWER);
        tokensTypeByString.put("<=", TokenType.LTE);
        tokensTypeByString.put(">=", TokenType.GTE);
        tokensTypeByString.put("==", TokenType.DOUBLEEQUAL);
        tokensTypeByString.put("!=", TokenType.NOTEQUAL);
        tokensTypeByString.put("&&", TokenType.AND);
        tokensTypeByString.put("||", TokenType.OR);
        tokensTypeByString.put("->", TokenType.ARROW);

        // Especials
        tokensTypeByChar.put('@', TokenType.AT);
        tokensTypeByChar.put('\0', TokenType.EOF);
    }

    public Lexer(String source, String filePath) {
        this.source = source;
        this.filePath = filePath;

        initialzerhashMapTokensTypes();
    }

    public void testTokenize() {
        this.source = "integer x = 10;";

        tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
