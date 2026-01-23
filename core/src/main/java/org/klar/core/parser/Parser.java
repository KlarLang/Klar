package org.klar.core.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.klar.core.lexer.Token;
import org.klar.core.lexer.TokenType;
import org.klar.core.parser.ast.AccessModifier;
import org.klar.core.parser.ast.AssignmentStatementNode;
import org.klar.core.parser.ast.BinaryExpressionNode;
import org.klar.core.parser.ast.BlockStatementNode;
import org.klar.core.parser.ast.CallExpressionNode;
import org.klar.core.parser.ast.DecisionStatementNode;
import org.klar.core.parser.ast.ExpressionNode;
import org.klar.core.parser.ast.ExpressionStatementNode;
import org.klar.core.parser.ast.FunctionDeclarationNode;
import org.klar.core.parser.ast.ImportDeclarationNode;
import org.klar.core.parser.ast.IndexExpressionNode;
import org.klar.core.parser.ast.LiteralExpressionNode;
import org.klar.core.parser.ast.ModuleDeclarationNode;
import org.klar.core.parser.ast.NewArrayExpressionNode;
import org.klar.core.parser.ast.OtherwiseBranchNode;
import org.klar.core.parser.ast.ParameterNode;
import org.klar.core.parser.ast.ProgramNode;
import org.klar.core.parser.ast.ReturnStatementNode;
import org.klar.core.parser.ast.StatementNode;
import org.klar.core.parser.ast.TypeReferenceNode;
import org.klar.core.parser.ast.UseAnnotationNode;
import org.klar.core.parser.ast.VariableDeclarationNode;
import org.klar.core.parser.ast.VariableExpressionNode;
import org.klar.core.parser.ast.WhileStatementNode;
import org.klar.core.Heddle;
import org.klar.core.diagnostics.DiagnosticCode;
import org.klar.core.errors.BackendException;
import org.klar.core.errors.ParserException;
import org.klar.core.errors.SourceLocation;
import org.klar.core.errors.SourceManager;

/**
 * Recursive descent parser for the Klar programming language.
 *
 * Abstract Syntax Tree (AST) that represents the structure of the source code.
 * It implements a top-down parsing strategy.
 *
 * @author Lucas Paulino Da Silva (~K')
 * @since 0.2
 */
public class Parser {
    private final List<Token> tokens;
    private final Path filePath;
    private final SourceManager sourceManager;
    private final String fileName;
    private int position = 0;
    private int functionDepth = 0;
    private int controlDepth = 0;

    // Parser

    /**
     * Parses postfix expressions, specifically array indexing operations.
     * * @return an expression node representing the postfix expression
     */
    private ExpressionNode parsePostfixExpression() {
        ExpressionNode expr = parsePrimary();
        while (check(TokenType.LBRACKET)) {
            Token braket = consume();
            ExpressionNode index = parseExpression();

            expect(TokenType.RBRACKET,
                    DiagnosticCode.E105,
                    "Missing closing bracket ']' in array index",
                    "Array index expressions must be enclosed in '[' and ']'",
                    "Add ']'",
                    "]",
                    "integer[] arr = integer[sizeVariable];",
                    null);

            expr = new IndexExpressionNode(expr, index, braket.getLine(), braket.getColumn());
        }
        return expr;
    }

    /**
     * Parses a type reference, including support for array types.
     * * @return a type reference node containing the base type and array depth
     */
    private TypeReferenceNode parseTypeReference() {
        Token base = current();
        if (!isType(base.getType())) {
            parserException(
                    DiagnosticCode.E201,
                    "Unknown type identifier '" + base.getValue() + "'",
                    "Ensure the type is valid or imported correctly",
                    "Type Identifier",
                    "integer[] arr = new integer[10];",
                    base.getLine(),
                    base.getColumn(),
                    null,
                    base.getValue().length());
        }
        consume();
        int depth = 0;
        while (check(TokenType.LBRACKET) && peek(1).getType() == TokenType.RBRACKET) {
            consume(); // '['
            consume(); // ']'
            depth++;
        }
        return new TypeReferenceNode(base, depth);
    }

    /**
     * Parses a while loop statement.
     * * @return a while statement node
     */
    private WhileStatementNode parseWhileStatement() {
        Token keyword = expect(TokenType.WHILE,
                DiagnosticCode.E000,
                "Expected 'while' keyword",
                "Loop structure must start with 'while'",
                "Add 'while'",
                "while",
                "while (condition) { ... }",
                null);

        controlDepth++;

        expect(TokenType.LPAREN,
                DiagnosticCode.E000,
                "Expected '(' after 'while'",
                "Loop condition must be enclosed in parentheses",
                "Add '('",
                "(",
                "while (i < 10) { ... }",
                null);

        ExpressionNode condition = parseExpression();

        expect(TokenType.RPAREN,
                DiagnosticCode.E000,
                "Expected ')' after condition",
                "Close the condition parentheses",
                "Add ')'",
                ")",
                "while (i < 10) { ... }",
                null);

        BlockStatementNode body = parseBlockStatement();
        controlDepth--;
        return new WhileStatementNode(condition, body, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Parses a decision statement (if/otherwise/afterall construct).
     * * @return a decision statement node
     */
    private DecisionStatementNode parseDecisionStatement() {
        Token ifToken = expect(TokenType.IF,
                DiagnosticCode.E000,
                "Expected 'if' keyword",
                "Decision structure must start with 'if'",
                "Add 'if'",
                "if",
                "if (condition) { ... }",
                null);

        controlDepth++;

        expect(TokenType.LPAREN,
                DiagnosticCode.E000,
                "Expected '(' after 'if'",
                "Condition must be enclosed in parentheses",
                "Add '('",
                "(",
                "if (isValid) { ... }",
                null);

        ExpressionNode condition = parseExpression();

        expect(TokenType.RPAREN,
                DiagnosticCode.E000,
                "Expected ')' after condition",
                "Close the condition parentheses",
                "Add ')'",
                ")",
                "if (isValid) { ... }",
                null);

        BlockStatementNode ifBlock = parseBlockStatement();
        List<OtherwiseBranchNode> otherwiseBranches = new ArrayList<>(5);

        while (match(TokenType.OTHERWISE)) {
            expect(TokenType.LPAREN,
                    DiagnosticCode.E000,
                    "Expected '(' after 'otherwise'",
                    "Otherwise condition must be enclosed in parentheses",
                    "Add '('",
                    "(",
                    "otherwise (x > 0) { ... }",
                    null);

            ExpressionNode otherwiseCondition = parseExpression();

            expect(TokenType.RPAREN,
                    DiagnosticCode.E000,
                    "Expected ')' after condition",
                    "Close the condition parentheses",
                    "Add ')'",
                    ")",
                    "otherwise (x > 0) { ... }",
                    null);

            String reason = null;

            if (match(TokenType.BECAUSE)) {
                Token str = expect(TokenType.STRING_LITERAL,
                        DiagnosticCode.E000,
                        "Expected string literal after 'because'",
                        "The reason must be explicitly stated as a string",
                        "Add a string literal",
                        "\"Reason\"",
                        "because \"Verification failed\" { ... }",
                        null);
                reason = str.getValue();
            }

            BlockStatementNode body = parseBlockStatement();

            otherwiseBranches.add(new OtherwiseBranchNode(otherwiseCondition, reason, body));
        }

        expect(TokenType.AFTERALL,
                DiagnosticCode.E000,
                "Expected 'afterall' to close decision block",
                "All decision structures in Klar must end with 'afterall'",
                "Add 'afterall'",
                "afterall",
                "if (...) { ... } afterall;",
                null);

        BlockStatementNode afterallBlock = null;

        if (check(TokenType.LBRACE)) {
            afterallBlock = parseBlockStatement();
        } else {
            expect(TokenType.SEMICOLON,
                    DiagnosticCode.E102,
                    "Expected ';' or block after 'afterall'",
                    "Declarative 'afterall' must be terminated with a semicolon",
                    "Add ';'",
                    ";",
                    "afterall;",
                    null);
            afterallBlock = new BlockStatementNode(List.of(), ifToken.getLine(), ifToken.getColumn());
        }
        controlDepth--;
        return new DecisionStatementNode(condition, ifBlock, otherwiseBranches, afterallBlock, ifToken.getLine(),
                ifToken.getColumn());
    }

    /**
     * Parses a module declaration statement.
     * * @return a module declaration node
     */
    private StatementNode parseModuleDeclaration() {
        Token keyword = expect(TokenType.MODULE,
                DiagnosticCode.E000,
                "Expected 'module' keyword",
                "File must define a module",
                "Add 'module'",
                "module",
                "module MyModule;",
                null);

        Token name = expect(TokenType.IDENTIFIER,
                DiagnosticCode.E000,
                "Expected module name",
                "Module declaration requires an identifier",
                "Add a name",
                "Identifier",
                "module MySystem;",
                null);

        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after module declaration",
                "Statements must be terminated",
                "Add ';'",
                ";",
                "module MySystem;",
                null);

        return new ModuleDeclarationNode(name, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Parses an import declaration statement.
     * * @return an import declaration node
     */
    private StatementNode parseImportDeclaration() {
        Token keyword = expect(TokenType.IMPORT,
                DiagnosticCode.E000,
                "Expected 'import' keyword",
                "Import statement must start with 'import'",
                "Add 'import'",
                "import",
                "import System.IO;",
                null);

        List<Token> path = new ArrayList<>(20);
        path.add(expect(TokenType.IDENTIFIER,
                DiagnosticCode.E000,
                "Expected identifier in import",
                "Import path must start with a module/package name",
                "Add identifier",
                "Identifier",
                "import System;",
                null));

        while (match(TokenType.DOT)) {
            path.add(expect(TokenType.IDENTIFIER,
                    DiagnosticCode.E000,
                    "Expected identifier after '.'",
                    "Import path must continue after dot",
                    "Add identifier",
                    "Identifier",
                    "import System.IO;",
                    null));
        }

        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after import",
                "Import statement must be terminated",
                "Add ';'",
                ";",
                "import System.IO;",
                null);

        return new ImportDeclarationNode(path, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Parses a variable declaration statement.
     * * @return a variable declaration node
     */
    public VariableDeclarationNode parseValDecl() {
        TypeReferenceNode type = parseTypeReference();
        if (!isType(type.getBaseType().getType())) {
            parserException(
                    DiagnosticCode.E103,
                    "Expected valid type in variable declaration",
                    "Specify the variable type explicitly",
                    "Type",
                    "integer variableInt = 10;",
                    type.getBaseType().getLine(),
                    type.getBaseType().getColumn(),
                    null,
                    1);
        }

        Token name = expect(TokenType.IDENTIFIER,
                DiagnosticCode.E000,
                "Expected variable name",
                "Variable declaration requires an identifier",
                "Add a name",
                "Identifier",
                "integer myVar = 10;",
                null);

        expect(TokenType.ASSIGNMENT,
                DiagnosticCode.E000,
                "Expected '=' in variable declaration",
                "Variables must be initialized explicitly",
                "Add '='",
                "=",
                "integer x = 0;",
                "Klar does not support uninitialized variables.");

        ExpressionNode initializer = parseExpression();

        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after variable declaration",
                "Declaration must be terminated",
                "Add ';'",
                ";",
                "integer x = 0;",
                null);

        return new VariableDeclarationNode(type, name, initializer, type.getBaseType().getLine(),
                type.getBaseType().getColumn());
    }

    /**
     * Parses any expression. Delegates to the lowest precedence operator.
     * * @return an expression node
     */
    public ExpressionNode parseExpression() {
        return parseComparision();
    }

    /**
     * Parses comparison expressions.
     * * @return an expression node
     */
    public ExpressionNode parseComparision() {
        ExpressionNode left = parseTerm();
        while (isComparisionOperator(current().getType())) {
            Token operator = consume();
            ExpressionNode right = parseTerm();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    /**
     * Parses term expressions (addition and subtraction).
     * * @return an expression node
     */
    public ExpressionNode parseTerm() {
        ExpressionNode left = parseFactor();
        while (isTermOperador(current().getType())) {
            Token operator = consume();
            ExpressionNode right = parseFactor();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    /**
     * Parses factor expressions (multiplication, division, modulo).
     * * @return an expression node
     */
    public ExpressionNode parseFactor() {
        ExpressionNode left = parsePostfixExpression();
        while (isFactorOperator(current().getType())) {
            Token operator = consume();
            ExpressionNode right = parsePostfixExpression();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    /**
     * Parses primary expressions (literals, identifiers, parentheses, 'new'
     * expressions).
     * * @return an expression node
     */
    public ExpressionNode parsePrimary() {
        if (check(TokenType.DOUBLE_LITERAL) || check(TokenType.INTEGER_LITERAL)) {
            Token number = consume();
            return new LiteralExpressionNode(number, number.getLine(), number.getColumn());
        }
        if (check(TokenType.STRING_LITERAL)) {
            Token string = consume();
            return new LiteralExpressionNode(string, string.getLine(), string.getColumn());
        }
        if (check(TokenType.CHARACTER_LITERAL)) {
            Token c = consume();
            return new LiteralExpressionNode(c, c.getLine(), c.getColumn());
        }
        if (check(TokenType.NULL)) {
            Token nullToken = consume();
            return new LiteralExpressionNode(nullToken, nullToken.getLine(), nullToken.getColumn());
        }
        if (check(TokenType.TRUE) || check(TokenType.FALSE)) {
            Token bool = consume();
            return new LiteralExpressionNode(bool, bool.getLine(), bool.getColumn());
        }

        if (check(TokenType.NEW)) {
            Token keyword = consume();
            TypeReferenceNode type = parseTypeReference();

            // Check for '[' after type
            if (!check(TokenType.LBRACKET)) {
                Token errorToken = current();
                parserException(
                        DiagnosticCode.E105,
                        "Expected '[' after type in array declaration",
                        "Use brackets to define array size: '" + type.getBaseType().getValue() + "[size]'",
                        "[",
                        "integer[] arr = new integer[mySize];",
                        errorToken.getLine(),
                        errorToken.getColumn(),
                        "Array size definition requires explicit brackets.",
                        errorToken.getValue().length());
            }

            consume(); // Consome o '['

            ExpressionNode size = parseExpression();
            if (size == null) {
                parserException(
                        DiagnosticCode.E106,
                        "Array size expression is missing",
                        "Provide a variable or expression for the array size",
                        "Expression",
                        "integer[] arr = new integer[sizeVariable];",
                        current().line,
                        current().column,
                        "Avoid magic numbers; prefer variables for array sizing.",
                        1);
            }

            if (!check(TokenType.RBRACKET)) {
                Token rbracket = current();
                parserException(
                        DiagnosticCode.E105,
                        "Expected ']' after array size expression",
                        "Close the array declaration with ']' found",
                        "]",
                        "integer[] arr = new integer[sizeVariable];",
                        rbracket.getLine(),
                        rbracket.getColumn(),
                        null,
                        rbracket.getValue().length());
            }
            consume(); // ']'

            List<ExpressionNode> values = new ArrayList<>(current().value.length() * 2);

            if (current().type == TokenType.LBRACE) {
                consume(); // {

                if (!check(TokenType.RBRACE)) {
                    do {
                        values.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }

                if (current().type != TokenType.RBRACE) {
                    parserException(
                            DiagnosticCode.E107,
                            "Array initializer block must end with '}'",
                            "Ensure the initializer list is closed",
                            "}",
                            "integer[] arr = new integer[size]{0, 1, 2, 3};",
                            peek(1).getLine(),
                            peek(1).getColumn(),
                            null,
                            peek(1).getValue().length());
                }
            }

            return new NewArrayExpressionNode(type, size, values, keyword.getLine(), keyword.getColumn());
        }

        if (check(TokenType.IDENTIFIER)) {
            Token identifier = consume();
            if (check(TokenType.LPAREN)) {
                return parseCallExpression(identifier);
            }

            return new VariableExpressionNode(identifier, identifier.getLine(), identifier.getColumn());
        }

        if (check(TokenType.LPAREN)) {
            consume();
            ExpressionNode expr = parseExpression();
            expect(TokenType.RPAREN,
                    DiagnosticCode.E000,
                    "Expected ')' after expression",
                    "Parentheses must be balanced",
                    "Add ')'",
                    ")",
                    "(1 + 2)",
                    null);
            return expr;
        }

        parserException(
                DiagnosticCode.E108,
                "Expected expression",
                "Insert a valid expression (literal, identifier, or operation)",
                "Expression",
                "integer x = 10 + 5;",
                current().getLine(),
                current().getColumn(),
                null,
                current().getValue().length());
        return null;
    }

    /**
     * Parses the entire program.
     * * @return the root ProgramNode containing all parsed statements
     */
    public ProgramNode parseProgram() {
        List<StatementNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            StatementNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        return new ProgramNode(statements);
    }

    /**
     * Parses an assignment statement.
     * * @return an assignment statement node
     */
    public StatementNode parseAssignmentStatement() {
        ExpressionNode target;
        if (check(TokenType.IDENTIFIER) && peek(1).getType() == TokenType.LBRACKET) {
            target = parsePostfixExpression();
        } else {
            Token identifier = expect(TokenType.IDENTIFIER,
                    DiagnosticCode.E000,
                    "Expected variable name",
                    "Assignment requires a target identifier",
                    "Add identifier",
                    "Identifier",
                    "x = 10;",
                    null);
            target = new VariableExpressionNode(identifier, identifier.getLine(), identifier.getColumn());
        }

        expect(TokenType.ASSIGNMENT,
                DiagnosticCode.E000,
                "Expected '=' after variable",
                "Assignment syntax requires '='",
                "Add '='",
                "=",
                "x = 10;",
                null);

        ExpressionNode value = parseExpression();

        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after assignment",
                "Statement must be terminated",
                "Add ';'",
                ";",
                "x = 10;",
                null);

        return new AssignmentStatementNode(target, value, target.line, target.column);
    }

    /**
     * Parses an expression statement (expression followed by a semicolon).
     * * @return an expression statement node
     */
    public StatementNode parseExpressionStatement() {
        ExpressionNode expr = parseExpression();
        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after expression",
                "Statement must be terminated",
                "Add ';'",
                ";",
                "func();",
                null);
        return new ExpressionStatementNode(expr, expr.line, expr.column);
    }

    /**
     * Parses a function call expression.
     * * @param callee the token representing the function name
     * 
     * @return a call expression node with the callee and arguments
     */
    public ExpressionNode parseCallExpression(Token callee) {
        expect(TokenType.LPAREN,
                DiagnosticCode.E000,
                "Expected '(' after function name",
                "Function call requires parentheses for arguments",
                "Add '('",
                "(",
                "func(a, b)",
                null);

        List<ExpressionNode> args = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                args.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        expect(TokenType.RPAREN,
                DiagnosticCode.E000,
                "Expected ')' after arguments",
                "Close the function call parentheses",
                "Add ')'",
                ")",
                "func(a, b)",
                null);

        return new CallExpressionNode(callee, args, callee.getLine(), callee.getColumn());
    }

    /**
     * Parses a block of statements enclosed in braces.
     * * @return a block statement node containing the list of statements
     */
    public BlockStatementNode parseBlockStatement() {
        Token openBrace = expect(TokenType.LBRACE,
                DiagnosticCode.E000,
                "Expected '{' to start block",
                "Block must start with '{'",
                "Add '{'",
                "{",
                "{ ... }",
                null);

        List<StatementNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE,
                DiagnosticCode.E000,
                "Expected '}' to close block",
                "Block must end with '}'",
                "Add '}'",
                "}",
                "{ ... }",
                null);

        return new BlockStatementNode(statements, openBrace.getLine(), openBrace.getColumn());
    }

    /**
     * Parses a function declaration.
     * * @param use the @Use annotation node associated with the function
     * 
     * @return a function declaration node
     */
    private StatementNode parseFunctionDeclaration(UseAnnotationNode use) {
        AccessModifier access = AccessModifier.INTERNAL;
        if (match(TokenType.PUBLIC)) {
            access = AccessModifier.PUBLIC;
        } else if (match(TokenType.PROTECTED)) {
            access = AccessModifier.PROTECTED;
        } else if (match(TokenType.INTERNAL)) {
            access = AccessModifier.INTERNAL;
        }

        Token returnType = current();
        if (!isType(returnType.getType())) {
            parserException(
                    DiagnosticCode.E109,
                    "Missing return type in function declaration",
                    "Explicitly declare the return type (e.g., void, integer)",
                    "Type",
                    "@Use(\"" + use.target.getValue() + "\")\n " + access.toString().toLowerCase()
                            + " void myFunction() { ... }",
                    returnType.getLine(),
                    returnType.getColumn(),
                    "Implicit typing is not allowed for function returns.",
                    returnType.getValue().length());
        }
        returnType = consume();

        Token name = expect(TokenType.IDENTIFIER,
                DiagnosticCode.E000,
                "Expected function name",
                "Function declaration requires a name",
                "Add a name",
                "Identifier",
                "void myFunction()",
                null);

        expect(TokenType.LPAREN,
                DiagnosticCode.E000,
                "Expected '(' after function name",
                "Parameters must be enclosed in parentheses",
                "Add '('",
                "(",
                "void myFunction()",
                null);

        List<ParameterNode> parameters = new ArrayList<>();

        if (!check(TokenType.RPAREN)) {
            do {
                Token paramType = current();
                if (!isType(paramType.getType())) {
                    parserException(
                            DiagnosticCode.E110,
                            "Missing parameter type",
                            "Declare the parameter type explicitly",
                            "Type",
                            "void func(integer param) { ... }",
                            paramType.getLine(),
                            paramType.getColumn(),
                            "Implicit typing is not allowed for parameters.",
                            paramType.getValue().length());
                }
                paramType = consume();

                Token parameterName = expect(TokenType.IDENTIFIER,
                        DiagnosticCode.E000,
                        "Expected parameter name",
                        "Parameter requires an identifier",
                        "Add a name",
                        "Identifier",
                        "void func(integer x)",
                        null);

                parameters.add(new ParameterNode(new TypeReferenceNode(paramType, 0), parameterName));
            } while (match(TokenType.COMMA));
        }

        expect(TokenType.RPAREN,
                DiagnosticCode.E000,
                "Expected ')' after parameters",
                "Close the parameter list",
                "Add ')'",
                ")",
                "void myFunction()",
                null);

        functionDepth++;
        BlockStatementNode body = parseFunctionBody();
        functionDepth--;

        return new FunctionDeclarationNode(access, new TypeReferenceNode(returnType, 0), name, parameters, body, use,
                returnType.getLine(), returnType.getColumn());
    }

    /**
     * Parses the body of a function, ensuring valid return statement placement.
     * * @return a block statement node representing the function body
     */
    private BlockStatementNode parseFunctionBody() {
        Token openBrace = expect(TokenType.LBRACE,
                DiagnosticCode.E000,
                "Expected '{' to start function body",
                "Function body must be a block",
                "Add '{'",
                "{",
                "void func() { ... }",
                null);

        List<StatementNode> statementNodes = new ArrayList<>();
        boolean seenReturn = false;

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            if (check(TokenType.RETURN)) {
                if (seenReturn) {
                    Token current = current();
                    String example = "return result; // Only one return allowed at the end";

                    parserException(
                            DiagnosticCode.E202,
                            "Multiple return statements found",
                            "Keep only one return statement at the end of the function scope",
                            null,
                            example,
                            current.getLine(),
                            current.getColumn(),
                            "To prevent divergent return types, K allows only one return per function.",
                            current.getValue().length());
                }

                StatementNode ret = parseReturnStatement();
                statementNodes.add(ret);
                seenReturn = true;

                if (!check(TokenType.RBRACE)) {
                    Token current = current();
                    String example = "...\n    return result; // Must be the last line\n}";

                    parserException(
                            DiagnosticCode.E203,
                            "Return statement is not the last statement",
                            "Move the return statement to the end of the function body",
                            null,
                            example,
                            current.getLine(),
                            current.getColumn(),
                            "Code after the return statement is unreachable and invalid in this context.",
                            current.getValue().length());
                }
                break;
            }

            StatementNode stmt = parseStatement();
            statementNodes.add(stmt);
        }

        expect(TokenType.RBRACE,
                DiagnosticCode.E000,
                "Expected '}' after function body",
                "Function body must be closed",
                "Add '}'",
                "}",
                "void func() { ... }",
                null);

        if (!seenReturn) {
            Token current = current();
            String example = "...\n    return result; // Explicit return required\n}";

            parserException(
                    DiagnosticCode.E205,
                    "Function is missing a return statement",
                    "Add a return statement at the end of the function",
                    "return",
                    example,
                    current.getLine(),
                    current.getColumn(),
                    "Even 'void' functions must return explicitly.",
                    current.getValue().length());
        }
        return new BlockStatementNode(statementNodes, openBrace.getLine(), openBrace.getColumn());
    }

    /**
     * Parses a return statement.
     * * @return a return statement node
     */
    public StatementNode parseReturnStatement() {
        Token keyword = expect(TokenType.RETURN,
                DiagnosticCode.E000,
                "Expected 'return' keyword",
                "Return statement starts with 'return'",
                "Add 'return'",
                "return",
                "return x;",
                null);

        ExpressionNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }

        expect(TokenType.SEMICOLON,
                DiagnosticCode.E102,
                "Expected ';' after return value",
                "Return statement must be terminated",
                "Add ';'",
                ";",
                "return x;",
                null);

        return new ReturnStatementNode(value, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Dispatches parsing to the appropriate statement handler based on the current
     * token.
     * * @return a statement node, or null if end of file
     */
    private StatementNode parseStatement() {
        if (isAtEnd())
            return null;

        if (check(TokenType.AFTERALL)) {
            Token current = current();
            String example = "if (...) { ... } afterall { ... }";

            parserException(
                    DiagnosticCode.E111,
                    "Unexpected 'afterall' without preceding if/otherwise",
                    "Ensure 'afterall' follows a decision structure",
                    "if",
                    example,
                    current.getLine(),
                    current.getColumn(),
                    null,
                    current.getValue().length());
        }

        if (check(TokenType.MODULE)) {
            return parseModuleDeclaration();
        }
        if (check(TokenType.IMPORT)) {
            return parseImportDeclaration();
        }

        // Function declaration check (Type -> Identifier -> '(')
        if (isType(current().getType()) &&
                peek(1).getType() == TokenType.IDENTIFIER &&
                peek(2).getType() == TokenType.LPAREN) {

            Token current = current();
            String example = "public integer myFunction(...) { ... }";

            parserException(
                    DiagnosticCode.E112,
                    "Missing access modifier in function declaration",
                    "Add an access modifier (public, protected, or internal)",
                    "Access Modifier",
                    example,
                    current.getLine(),
                    current.getColumn(),
                    "K requires explicit access modifiers.",
                    current.getValue().length());
        }

        UseAnnotationNode pendingUse = null;
        if (check(TokenType.AT)) {
            consume();
            Token name = expect(TokenType.IDENTIFIER,
                    DiagnosticCode.E204,
                    "Expected annotation name",
                    "Annotation requires an identifier",
                    "Add name",
                    "Identifier",
                    "@Use",
                    null);

            if (!name.value.equals("Use")) {
                Token current = current();
                parserException(
                        DiagnosticCode.E204,
                        "Unknown annotation '@" + name.getValue() + "'",
                        "Use a supported annotation (e.g., @Use)",
                        "Use",
                        "@Use(\"context\")",
                        current.getLine(),
                        current.getColumn(),
                        null,
                        current.getValue().length());
            }

            expect(TokenType.LPAREN,
                    DiagnosticCode.E000,
                    "Expected '(' after @Use",
                    "Annotation parameters must be in parentheses",
                    "Add '('",
                    "(",
                    "@Use(\"java\")",
                    null);

            Token target = expect(TokenType.STRING_LITERAL,
                    DiagnosticCode.E000,
                    "Expected string literal after @Use",
                    "Annotation target must be a string",
                    "Add string",
                    "\"Target\"",
                    "@Use(\"java\")",
                    null);

            String _target = target.getValue().substring(1, target.getValue().length() - 1);

            if (!_target.equals("java")) {
                throw new BackendException(
                        DiagnosticCode.E400,
                        new SourceLocation(filePath.toString(), target.getLine(), Math.max(target.getColumn() - 1, 0)),
                        sourceManager.getContextLines(target.getLine(), 2),
                        "Unsupported backend target '" + _target + "'",
                        "Use a supported backend (currently only 'java' is supported)",
                        "@Use(\"java\")\npublic void myFunction() { ... }",
                        "K currently supports only Java as a compilation target.",
                        target.getValue().length());
            }

            expect(TokenType.RPAREN,
                    DiagnosticCode.E000,
                    "Expected ')' after @Use",
                    "Close annotation parentheses",
                    "Add ')'",
                    ")",
                    "@Use(\"java\")",
                    null);

            pendingUse = new UseAnnotationNode(target);
        }

        if (looksLikeFunctionDeclaration()) {
            if (pendingUse == null) {
                Token current = current();
                String example = "@Use(\"java\")\npublic void myFunction() { ... }";

                parserException(
                        DiagnosticCode.E113,
                        "Missing @Use annotation on function",
                        "Annotate the function with @Use to specify the context",
                        "@Use",
                        example,
                        current.getLine(),
                        current.getColumn(),
                        null,
                        current.getValue().length());
            }
            return parseFunctionDeclaration(pendingUse);
        }

        // while
        if (check(TokenType.WHILE)) {
            return parseWhileStatement();
        }
        // if
        if (check(TokenType.IF)) {
            return parseDecisionStatement();
        }
        // Block {}
        if (check(TokenType.LBRACE)) {
            return parseBlockStatement();
        }

        if (check(TokenType.RETURN)) {
            // Check: Return outside function
            if (functionDepth == 0) {
                Token current = current();
                parserException(
                        DiagnosticCode.E203,
                        "Return statement found outside function body",
                        "Move return statement inside a function",
                        null,
                        null,
                        current.getLine(),
                        current.getColumn(),
                        null,
                        current.getValue().length());
            }

            // Check: Return not at end (semantic)
            if (controlDepth > 0) {
                Token current = current();
                String example = "afterall {\n    result = x / y;\n}\nreturn result;";

                parserException(
                        DiagnosticCode.E203,
                        "Return must be in the main scope of the function",
                        "Move return out of control blocks (if/while) to the end of the function",
                        null,
                        example,
                        current.getLine(),
                        current.getColumn(),
                        "Returns inside nested blocks can cause unreachable code or ambiguous return paths.",
                        current.getValue().length());
            }

            return parseReturnStatement();
        }

        // Declaration
        if (isType(current().getType())) {
            return parseValDecl();
        }

        // Assignment (IDENTIFIER '=' ...)
        if (check(TokenType.IDENTIFIER) && peek(1).getType() == TokenType.ASSIGNMENT) {
            return parseAssignmentStatement();
        }

        // Expression statement
        return parseExpressionStatement();
    }

    // Utility do Parser

    public Parser(List<Token> tokens, Path path, SourceManager sourceManager) {
        this.tokens = tokens;
        this.filePath = path;
        this.sourceManager = sourceManager;
        this.fileName = filePath.getFileName().toString();
    }

    private boolean isAtEnd() {
        return current().getType() == TokenType.EOF;
    }

    public Token current() {
        return tokens.get(position);
    }

    public Token consume() {
        if (isAtEnd()) {
            return tokens.get(position);
        }
        Token token = tokens.get(position);
        position++;
        return token;
    }

    public Token peek(int offset) {
        if (isAtEnd()) {
            return tokens.get(position);
        }

        if (offset < 0) {
            if (offset != -1) {
                offset = 0;
            }
        }

        int index = offset + position;
        if (index > tokens.size() - 1) {
            return tokens.get(tokens.size() - 1);
        }

        return tokens.get(index);
    }

    public boolean match(TokenType... types) {
        for (TokenType tokenType : types) {
            if (check(tokenType)) {
                consume();
                return true;
            }
        }
        return false;
    }

    public boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return current().getType() == type;
    }

    public Token expect(TokenType type, DiagnosticCode code, String message, String cause, String fix, String expected,
            String example, String note) {
        if (!check(type)) {
            Token blame = current();

            // Lógica Especial para Ponto e Vírgula Faltante (Lookbehind)
            if (type == TokenType.SEMICOLON && position > 0) {
                Token prev = peek(-1);

                // Se mudou de linha (o erro está na linha anterior)
                if (prev.getLine() < blame.getLine()) {
                    int errorLine = prev.getLine();
                    int errorCol = prev.getColumn() + prev.getValue().length();

                    System.out.println(prev.getColumn());

                    throw new ParserException(
                            code,
                            new SourceLocation(filePath.toString(), errorLine, errorCol),
                            sourceManager.getContextLines(errorLine, 2),
                            cause,
                            fix,
                            expected,
                            example,
                            note,
                            1 // O tamanho do erro é 1 (o tamanho do ';' invisível)
                    );
                }
            }

            syntaxException(code, message, cause, fix, expected, example, note, blame);
        }
        return consume();
    }

    public boolean looksLikeFunctionDeclaration() {
        int i = 0;
        if (!isAccessModifier(peek(i).getType())) {
            return false;
        }
        i++;
        // type
        if (!isType(peek(i).getType())) {
            return false;
        }
        i++;
        // function name
        if (peek(i).getType() != TokenType.IDENTIFIER) {
            return false;
        }
        i++;
        // Must be '('
        return peek(i).getType() == TokenType.LPAREN;
    }

    private void parserException(DiagnosticCode code, String cause, String fix, String expected, String example,
            int line, int column, String note, int lenth) {
        throw new ParserException(
                code,
                new SourceLocation(filePath.toString(), line, Math.max(column - 1, 0)),
                sourceManager.getContextLines(line, 2),
                cause,
                fix,
                expected,
                example,
                note,
                current().getValue().length());
    }

    private void syntaxException(DiagnosticCode code, String message, String cause, String fix, String expected,
            String example, String note, Token tokenToBlame) {
        Token currentToken = current();

        throw new ParserException(
                code,
                new SourceLocation(filePath.toString(), tokenToBlame.getLine(),
                        Math.max(tokenToBlame.getColumn() - 1, 0)),
                sourceManager.getContextLines(tokenToBlame.getLine(), 2),
                cause,
                fix,
                expected,
                example,
                note,
                tokenToBlame.getValue().length());
    }

    private boolean isType(TokenType type) {
        return Heddle.TYPES.contains(type);
    }

    private boolean isComparisionOperator(TokenType type) {
        return Heddle.COMPARISION_OPERATORS.contains(type);
    }

    private boolean isTermOperador(TokenType type) {
        return Heddle.TERM_OPERATORS.contains(type);
    }

    private boolean isFactorOperator(TokenType type) {
        return Heddle.FACTOR_OPERATORS.contains(type);
    }

    private boolean isAccessModifier(TokenType type) {
        return Heddle.ACESS_MODIFIERS.contains(type);
    }
}