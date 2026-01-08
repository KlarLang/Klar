package org.klang.core.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.lexer.TokenType;
import org.klang.core.parser.ast.AccessModifier;
import org.klang.core.parser.ast.AssignmentStatementNode;
import org.klang.core.parser.ast.BinaryExpressionNode;
import org.klang.core.parser.ast.BlockStatementNode;
import org.klang.core.parser.ast.CallExpressionNode;
import org.klang.core.parser.ast.DecisionStatementNode;
import org.klang.core.parser.ast.ExpressionNode;
import org.klang.core.parser.ast.ExpressionStatementNode;
import org.klang.core.parser.ast.FunctionDeclarationNode;
import org.klang.core.parser.ast.ImportDeclarationNode;
import org.klang.core.parser.ast.IndexExpressionNode;
import org.klang.core.parser.ast.LiteralExpressionNode;
import org.klang.core.parser.ast.ModuleDeclarationNode;
import org.klang.core.parser.ast.NewArrayExpressionNode;
import org.klang.core.parser.ast.OtherwiseBranchNode;
import org.klang.core.parser.ast.ParameterNode;
import org.klang.core.parser.ast.ProgramNode;
import org.klang.core.parser.ast.ReturnStatementNode;
import org.klang.core.parser.ast.StatementNode;
import org.klang.core.parser.ast.TypeReferenceNode;
import org.klang.core.parser.ast.UseAnnotationNode;
import org.klang.core.parser.ast.VariableDeclarationNode;
import org.klang.core.parser.ast.VariableExpressionNode;
import org.klang.core.parser.ast.WhileStatementNode;
import org.klang.core.Heddle;
import org.klang.core.diagnostics.DiagnosticCode;
import org.klang.core.errors.ParserException;
import org.klang.core.errors.SourceLocation;
import org.klang.core.errors.SourceManager;

/**
 * Recursive descent parser for the Klang programming language.
 * * <p>This parser transforms a sequence of tokens produced by the lexer into an
 * Abstract Syntax Tree (AST) that represents the structure of the source code.
 * It implements a top-down parsing strategy, processing tokens from left to right
 * and constructing the parse tree by recursive descent.</p>
 * * <p>The parser enforces Klang's syntax rules and generates detailed diagnostic
 * messages when syntax errors are encountered.</p>
 * * @author Lucas Paulino Da Silva (~K')
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
     * * <p>Grammar: {@code postfixExpr → primary ( '[' expression ']' )*}</p>
     * * @return an expression node representing the postfix expression
     */
    private ExpressionNode parsePostfixExpression(){
        ExpressionNode expr = parsePrimary();

        while (check(TokenType.LBRACKET)){
            Token braket = consume();

            ExpressionNode index = parseExpression();

            expect(TokenType.RBRACKET, "Expected ']' after index");

            expr = new IndexExpressionNode(expr, index, braket.getLine(), braket.getColumn());
        }

        return expr;
    }

    /**
     * Parses a type reference, including support for array types.
     * * <p>Grammar: {@code typeRef → TYPE ( '[]' )*}</p>
     * * @return a type reference node containing the base type and array depth
     */
    private TypeReferenceNode parseTypeReference(){
        Token base = current();

        if (!isType(base.getType())){
            parserException(
                DiagnosticCode.E201,
                "Expected type '" + base.getValue() + "'",
                "Remove this or replace a valid type",
                null, 
                "integer[] arr = new integer[10];",
                base.getLine(),
                base.getColumn(),
                null,
                base.getValue().length());
        }

        consume();

        int depth = 0;

        while (check(TokenType.LBRACKET) && peek(1).getType() == TokenType.RBRACKET){
            consume(); // '['
            consume(); // ']'
            depth++;
        }


        return new TypeReferenceNode(base, depth);
    }

    /**
     * Parses a while loop statement.
     * * <p>Grammar: {@code whileStmt → 'while' '(' expression ')' block}</p>
     * * @return a while statement node
     */
    private WhileStatementNode parseWhileStatement(){
        Token keyword = expect(TokenType.WHILE, "Expected 'while'");
        controlDepth++;

        expect(TokenType.LPAREN, "Expected '(' after while");
        ExpressionNode condition = parseExpression();
        expect(TokenType.RPAREN, "Expected ')' after condition");

        BlockStatementNode body = parseBlockStatement();
        controlDepth--;

        return new WhileStatementNode(condition, body, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Parses a decision statement (if/otherwise/afterall construct).
     * * <p>Grammar:</p>
     * <pre>{@code
     * decisionStmt → 'if' '(' expr ')' block
     * ( 'otherwise' '(' expr ')' ('because' STRING)? block )*
     * 'afterall' ( block | ';' )
     * }</pre>
     * * @return a decision statement node
     */
    private DecisionStatementNode parseDecisionStatement(){
        Token ifToken = expect(TokenType.IF, "Expected 'if'");
        controlDepth++;

        expect(TokenType.LPAREN, "Expected '(' after if");

        ExpressionNode condition = parseExpression();
        
        expect(TokenType.RPAREN, "Expected ')' after condition");

        BlockStatementNode ifBlock = parseBlockStatement();
        List<OtherwiseBranchNode> otherwiseBranches = new ArrayList<>(5);

        while (match(TokenType.OTHERWISE)){
            expect(TokenType.LPAREN, "Expected '(' after otherwise");
            ExpressionNode otherwiseCondition = parseExpression();
            expect(TokenType.RPAREN, "Expected ')' after condition");
        
            String reason = null;

            if (match(TokenType.BECAUSE)){
                Token str = expect(TokenType.STRING_LITERAL, "Expected String literl after because");
            
                reason = str.getValue();
            }

            BlockStatementNode body = parseBlockStatement();

            otherwiseBranches.add(new OtherwiseBranchNode(otherwiseCondition, reason, body));
        }

        expect(TokenType.AFTERALL, "Expected 'afterall' to close condition");

        BlockStatementNode afterallBlock = null;

        if (check(TokenType.LBRACE)){
            afterallBlock = parseBlockStatement();
        } else {
            expect(TokenType.SEMICOLON, "Expected ';' or block after afterall");
            afterallBlock = new BlockStatementNode(List.of(), ifToken.getLine(), ifToken.getColumn());
        }
        
        controlDepth--;
        return new DecisionStatementNode(condition, ifBlock, otherwiseBranches, afterallBlock, ifToken.getLine(), ifToken.getColumn());
    }

    /**
     * Parses a module declaration statement.
     * * <p>Grammar: {@code moduleDecl → 'module' IDENTIFIER ';'}</p>
     * * @return a module declaration node
     */
    private StatementNode parseModuleDeclaration(){
        Token keyword = expect(TokenType.MODULE, "Expected 'module'");

        Token name = expect(TokenType.IDENTIFIER, "Expected module name");
        expect(TokenType.SEMICOLON, "Expected ';' after module delcaration");

        return new ModuleDeclarationNode(name, keyword.getLine(), keyword.getColumn());
    
    }

    /**
     * Parses an import declaration statement.
     * * <p>Grammar: {@code importDecl → 'import' IDENTIFIER ( '.' IDENTIFIER )* ';'}</p>
     * * @return an import declaration node
     */
    private StatementNode parseImportDeclaration(){
        Token keyword = expect(TokenType.IMPORT, "Expected 'import'");

        List<Token> path = new ArrayList<>(20);

        path.add(expect(TokenType.IDENTIFIER, "Expected identifier in import"));
    
        while (match(TokenType.DOT)){
            path.add(expect(TokenType.IDENTIFIER, "Expected identifier after '.'"));
        }

        expect(TokenType.SEMICOLON, "Expected ';' after import");

        return new ImportDeclarationNode(path, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Parses a variable declaration statement.
     * * <p>Grammar: {@code varDecl → typeRef IDENTIFIER '=' expression ';'}</p>
     * * @return a variable declaration node
     */
    public VariableDeclarationNode parseValDecl(){
        TypeReferenceNode type = parseTypeReference();
        
        if (!isType(type.getBaseType().getType())){
            parserException(DiagnosticCode.E103, "Expected type in variable declaration", "Add the variable type in the declaration", null, "integer variableInt = 10;", type.getBaseType().getLine(), type.getBaseType().getColumn(), null,1);
        }
        
        Token name = expect(TokenType.IDENTIFIER, "Expected variable name");

        expect(TokenType.ASSIGNMENT, "Expected '=' in variable declaration");
        ExpressionNode initializer = parseExpression();

        expect(TokenType.SEMICOLON, "Expected ';' after variable declaration");

        return new VariableDeclarationNode(type, name, initializer, type.getBaseType().getLine(), type.getBaseType().getColumn());
    }

    /**
     * Parses any expression. Delegates to the lowest precedence operator (comparison).
     * * @return an expression node
     */
    public ExpressionNode parseExpression(){
        return parseComparision();
    }
    
    /**
     * Parses comparison expressions.
     * * <p>Grammar: {@code comparison → term ( (== | != | < | > | <= | >=) term )*}</p>
     * * @return an expression node
     */
    public ExpressionNode parseComparision(){
        ExpressionNode left = parseTerm();

        while (isComparisionOperator(current().getType())){
            Token operator = consume();

            ExpressionNode right = parseTerm();
            
            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    /**
     * Parses term expressions (addition and subtraction).
     * * <p>Grammar: {@code term → factor ( ('+' | '-') factor )*}</p>
     * * @return an expression node
     */
    public ExpressionNode parseTerm(){
        ExpressionNode left = parseFactor();

        while (isTermOperador(current().getType())){
            Token operator = consume();

            ExpressionNode right = parseFactor();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    /**
     * Parses factor expressions (multiplication, division, modulo).
     * * <p>Grammar: {@code factor → postfixExpr ( ('*' | '/' | '%') postfixExpr )*}</p>
     * * @return an expression node
     */
    public ExpressionNode parseFactor(){
        ExpressionNode left = parsePostfixExpression();

        while (isFactorOperator(current().getType())){
            Token operator = consume();

            ExpressionNode right = parsePostfixExpression();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    /**
     * Parses primary expressions (literals, identifiers, parentheses, 'new' expressions).
     * * @return an expression node
     */
    public ExpressionNode parsePrimary(){
        if (check(TokenType.DOUBLE_LITERAL) || check(TokenType.INTEGER_LITERAL)){
            Token number = consume();

            return new LiteralExpressionNode(number, number.getLine(), number.getColumn());
        }

        if (check(TokenType.STRING_LITERAL)){
            Token string = consume();

            return new LiteralExpressionNode(string, string.getLine(), string.getColumn());
        }

        if (check(TokenType.CHARACTER_LITERAL)){
            Token c = consume();

            return new LiteralExpressionNode(c, c.getLine(), c.getColumn());
        }

        if (check(TokenType.NULL)){
            Token nullToken = consume();

            return new LiteralExpressionNode(nullToken, nullToken.getLine(), nullToken.getColumn());
        }

        if (check(TokenType.TRUE) || check(TokenType.FALSE)){
            Token bool = consume();

            return new LiteralExpressionNode(bool, bool.getLine(), bool.getColumn());
        }

        if (check(TokenType.NEW)){
            Token keyword = consume();
            TypeReferenceNode type = parseTypeReference();

            // Verifica se tem '[' após o tipo
            if (!check(TokenType.LBRACKET)){
                Token errorToken = current(); // Token que está errado (esperava '[')
                parserException(
                    DiagnosticCode.E105, 
                    "The declaration of arrays with precision of the character '[' ", 
                    "use '" + type.getBaseType().getValue() + "[sizeVariableHere]'", 
                    "[",
                    "integer[] arr = new integer[sizeVariableHere];", 
                    errorToken.getLine(), 
                    errorToken.getColumn(), 
                    "Remember, no magic numbers, only variables for the size of the array are allowed.", 
                    errorToken.getValue().length()
                );
            }

            consume(); // Consome o '['

            /*
            // Verifica se já tem ']' (array vazio)
            if (check(TokenType.RBRACKET)){
                Token rbracket = current(); // O ']' que está errado
                parserException(
                    DiagnosticCode.E105, 
                    "The declaration of arrays with precision of the character ']' ", 
                    "use '" + type.getBaseType().getValue() + "[sizeVariableHere]'", 
                    "]",
                    "integer[] arr = new integer[sizeVariableHere];", 
                    rbracket.getLine(), 
                    rbracket.getColumn(), 
                    "Remember, no magic numbers, only variables for the size of the array are allowed.", 
                    rbracket.getValue().length()
                );
            }
            */
            
            ExpressionNode size = parseExpression();
            if (size == null){
                parserException(
                DiagnosticCode.E106,
                "Arrays precisam ter o seu tamanho declarados",
                "use '" + type.getBaseType().getValue() + "[sizeVariableHere]'",
                null,
                "integer[] arr = new integer[sizeVariableHere];",
                current().line,
                current().column,
                "Remember, no magic numbers, only variables for the size of the array are allowed.",
                1
                );
            }

            if (!check(TokenType.RBRACKET)){
                Token rbracket = current();
                parserException(
                    DiagnosticCode.E105, 
                    "The declaration of arrays with precision of the character ']' ", 
                    "use '" + type.getBaseType().getValue() + "[sizeVariableHere]'", 
                    "]",
                    "integer[] arr = new integer[sizeVariableHere];", 
                    rbracket.getLine(), 
                    rbracket.getColumn(), 
                    "Remember, no magic numbers, only variables for the size of the array are allowed.", 
                    rbracket.getValue().length()
                );
            }
            consume(); // ']'

            List<ExpressionNode> values = new ArrayList<>(current().value.length() * 2);
            
            if (current().type == TokenType.LBRACE){
                consume(); // {

                if (!check(TokenType.RBRACE)) {
                    do {
                        values.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }
                
                if (current().type != TokenType.RBRACE){
                    parserException(
                        DiagnosticCode.E107, 
                        "The declaration of array values ​​must end with '}'.", 
                        "use '" + type.getBaseType().getValue() + "[sizeVariableHere]{values, here}'", 
                        "}",
                        "integer sizeVariable = 6;\n  integer[] arr = new integer[sizeVariableHere]{0, 1, 2, 3, 4, 5};", 
                        peek(1).getLine(), 
                        peek(1).getColumn(), 
                        null, 
                        peek(1).getValue().length()
                    );
                }
            }

            return new NewArrayExpressionNode(type, size, values, keyword.getLine(), keyword.getColumn());
        }

        if (check(TokenType.IDENTIFIER)){
            Token identifier = consume();

            if (check(TokenType.LPAREN)){
                return parseCallExpression(identifier);
            }

            return new VariableExpressionNode(identifier, identifier.getLine(), identifier.getColumn());
        }

        if (check(TokenType.LPAREN)){
            consume();

            ExpressionNode expr = parseExpression();

            expect(TokenType.RPAREN, "Expected ')'");

            return expr;
        }
    
        error("Expected Expression " + current().getLine() + ":" + current().getColumn());
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

            if (stmt != null){
                statements.add(stmt);
            } 
        }


        return new ProgramNode(statements);
    }

    /**
     * Parses an assignment statement.
     * * <p>Grammar: {@code assignment → (IDENTIFIER | postfixExpr) '=' expression ';'}</p>
     * * @return an assignment statement node
     */
    public StatementNode parseAssignmentStatement(){
        ExpressionNode target;
        
        if (check(TokenType.IDENTIFIER) && peek(1).getType() == TokenType.LBRACKET){
            target = parsePostfixExpression();
        } else {
            Token identifier = expect(TokenType.IDENTIFIER, "Expected variable name");
            target = new VariableExpressionNode(identifier, identifier.getLine(), identifier.getColumn());
        }
        
        
        expect(TokenType.ASSIGNMENT, "Expected '=' after variable '");
        ExpressionNode value = parseExpression();
        expect(TokenType.SEMICOLON, "Expected ';' after assignment");

        return new AssignmentStatementNode(target, value, target.line, target.column);
    }

    /**
     * Parses an expression statement (expression followed by a semicolon).
     * * @return an expression statement node
     */
    public StatementNode parseExpressionStatement(){
        ExpressionNode expr = parseExpression();

        expect(TokenType.SEMICOLON, "Expected ';' after expression");

        return new ExpressionStatementNode(expr, expr.line, expr.column);
    }

    /**
     * Parses a function call expression.
     * * <p>Grammar: {@code call → IDENTIFIER '(' ( expression (',' expression)* )? ')'}</p>
     * * @param callee the token representing the function name
     * @return a call expression node with the callee and arguments
     */
    public ExpressionNode parseCallExpression(Token callee){
        expect(TokenType.LPAREN, "Expected '(' after function name");

        List<ExpressionNode> args = new ArrayList<>();

        if (!check(TokenType.RPAREN)){
            do {
                args.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        expect(TokenType.RPAREN, "Expected ')' after arguments");

        return new CallExpressionNode(callee, args, callee.getLine(), callee.getColumn());
    }

    /**
     * Parses a block of statements enclosed in braces.
     * * @return a block statement node containing the list of statements
     */
    public BlockStatementNode parseBlockStatement(){
        Token openBrace = expect(TokenType.LBRACE, "Expected '{'");

        List<StatementNode> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE, "Expected '}' after block");

        return new BlockStatementNode(statements, openBrace.getLine(), openBrace.getColumn());
    }

    /**
     * Parses a function declaration.
     * * <p>Grammar: {@code funcDecl → accessMod type IDENTIFIER '(' params? ')' block}</p>
     * * @param use the @Use annotation node associated with the function
     * @return a function declaration node
     */
    private StatementNode parseFunctionDeclaration(UseAnnotationNode use){
        AccessModifier access = AccessModifier.INTERNAL;

        if (match(TokenType.PUBLIC)){
            access = AccessModifier.PUBLIC;
        } else if (match(TokenType.PROTECTED)){
            access = AccessModifier.PROTECTED;
        } else if (match(TokenType.INTERNAL)){
            access = AccessModifier.INTERNAL;
        }

        Token returnType = current();

        if (!isType(returnType.getType())){
            error("Expected return type in function declaration");
        }

        returnType = consume();

        Token name = expect(TokenType.IDENTIFIER, "Expected function name");

        expect(TokenType.LPAREN, "Expected '(' after function name");

        List<ParameterNode> parameters = new ArrayList<>();

        if (!check(TokenType.RPAREN)){
            do {
                Token paramType = current();
                if (!isType(paramType.getType())){
                    error("Expected parameter type");
                }

                paramType = consume();

                Token parameterName = expect(TokenType.IDENTIFIER, "Expected parameter name");

                parameters.add(new ParameterNode(new TypeReferenceNode(paramType, 0), parameterName));
            } while (match(TokenType.COMMA));
        }

        expect(TokenType.RPAREN, "Expected ')' after parameters");

        functionDepth++;
        BlockStatementNode body = parseFunctionBody();
        functionDepth--;

        return new FunctionDeclarationNode(access, new TypeReferenceNode(returnType, 0), name, parameters, body, use, returnType.getLine(), returnType.getColumn());
    }

    /**
     * Parses the body of a function, ensuring valid return statement placement.
     * * @return a block statement node representing the function body
     */
    private BlockStatementNode parseFunctionBody(){
        Token openBrace = expect(TokenType.LBRACE, null);

        List<StatementNode> statementNodes = new ArrayList<>();
        boolean seenReturn = false;

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            if (check(TokenType.RETURN)){
                if (seenReturn){
                    error("Function can only have one return statement");
                }
                
                StatementNode ret = parseReturnStatement();
                statementNodes.add(ret);
                seenReturn = true;
                
                if (!check(TokenType.RBRACE)) {
                    error("Return must be the last statement of the function");
                }
                
                break;
            }
            
            StatementNode stmt = parseStatement();
            
            
            
            statementNodes.add(stmt);
        }

        expect(TokenType.RBRACE, "Expected '}' after function body");

        if (!seenReturn) {
            error("Function must end with a return statement");
        }

        return new BlockStatementNode(statementNodes, openBrace.getLine(), openBrace.getColumn());
    }

    /**
     * Parses a return statement.
     * * <p>Grammar: {@code returnStmt → 'return' expression? ';'}</p>
     * * @return a return statement node
     */
    public StatementNode parseReturnStatement(){
        Token keyword = expect(TokenType.RETURN, "Expected 'return'");

        ExpressionNode value = null;

        if (!check(TokenType.SEMICOLON)){
            value = parseExpression();
        }

        expect(TokenType.SEMICOLON, "Expected ';' after return");
        
        return new ReturnStatementNode(value, keyword.getLine(), keyword.getColumn());
    }

    /**
     * Dispatches parsing to the appropriate statement handler based on the current token.
     * * @return a statement node, or null if end of file
     */
    private StatementNode parseStatement() {
        if (isAtEnd()) return null;

        if (check(TokenType.AFTERALL)){
            error("'afterall' can only appear after if/otherwise decision");
        }

        if (check(TokenType.MODULE)){
            return parseModuleDeclaration();
        }

        if (check(TokenType.IMPORT)){
            return parseImportDeclaration();
        }

        if (isType(current().getType()) &&
            peek(1).getType() == TokenType.IDENTIFIER &&
            peek(2).getType() == TokenType.LPAREN) {
            
            error("Function declaration requires an access modifier (public | protected | internal)");
        }

        UseAnnotationNode pedingUse = null;

        if (check(TokenType.AT)){
            consume();

            Token name = expect(TokenType.IDENTIFIER, "Expected annotation name");
        
            if (!name.value.equals("Use")){
                error("Unknow annotation @" + name.getValue());
            }

            expect(TokenType.LPAREN, "Expected '(' after @Use");
            Token target = expect(TokenType.STRING_LITERAL, "Expected string literal after @Use");
            expect(TokenType.RPAREN, "Expected ')' after @Use");
            
            pedingUse = new UseAnnotationNode(target);
        }
        
        if (looksLikeFunctionDeclaration()) {
            if (pedingUse == null){
                error("Expected @Use before a function");
            }
            
            return parseFunctionDeclaration(pedingUse);
        }

        // while
        if (check(TokenType.WHILE)){

            return parseWhileStatement();
        }

        // if
        if (check(TokenType.IF)){
            return parseDecisionStatement();
        }

        // Bloco {}
        if (check(TokenType.LBRACE)){
            return parseBlockStatement();
        }
        
        if (check(TokenType.RETURN)) {
            if (functionDepth == 0) {
                error("Return outside function");
            }

            if (controlDepth > 0){
                error("Return is not allowed inside while or decision blocks");
            }

            return parseReturnStatement();
        }

        // declaração
        if (isType(current().getType())) {
            return parseValDecl();
        }

        // assignment (IDENTIFIER '=' ...)
        if (check(TokenType.IDENTIFIER)
            && peek(1).getType() == TokenType.ASSIGNMENT) {
            return parseAssignmentStatement();
        }

        // expression statement
        return parseExpressionStatement();
    }


    // Utility do Parser
    public Parser(List<Token> tokens, Path path, SourceManager sourceManager){
        this.tokens = tokens;
        this.filePath = path;
        this.sourceManager = sourceManager;
        this.fileName = filePath.getFileName().toString();
    }

    private boolean isAtEnd(){
        return current().getType() == TokenType.EOF;
    }
    
    public Token current(){
        return tokens.get(position);
    }

    public Token consume(){
        if (isAtEnd()){
            return tokens.get(position);
        }
        
        Token token = tokens.get(position); 
        position++;

        return token;
    }

    public Token peek(int offset){
        if (isAtEnd()){
            return tokens.get(position);
        }

        if (offset < 0){
            offset = 0;
        } 
        
        int index = offset + position;

        if (index > tokens.size() - 1){
            return tokens.get(tokens.size() - 1);
        }

        return tokens.get(index);
    }

    public boolean match(TokenType... types){
        for (TokenType tokenType : types) {
            if (check(tokenType)){
                consume();
                return true;
            }
        }

        return false;
    }

    public boolean check(TokenType type){
        if (isAtEnd()){
            return false;
        }

        return current().getType() == type;
    }

    public Token expect(TokenType type, String message) {
        if (!check(type)){
            error(message);
        }
        
        return consume();
    }

    public boolean looksLikeFunctionDeclaration(){
        int i = 0;

        if (!isAccessModifier(peek(i).getType())){
            return false;
        }
        i++;

        // type

        if (!isType(peek(i).getType())){
            return false;
        }
        i++;

        // function name
        if (peek(i).getType() != TokenType.IDENTIFIER){
            return false;
        }
        i++;

        // Must be '('
        return peek(i).getType() == TokenType.LPAREN;
    }

    public void error(String message){
        throw new RuntimeException(message);
    }

    private void parserException(DiagnosticCode code, String cause, String fix, String expected, String example, int line, int column, String note, int lenth){
        throw new ParserException(
            code, 
            new SourceLocation(filePath.toString(), line, Math.max(column - 1, 0)),
            sourceManager.getContextLines(line, 2),
            cause,
            fix,
            expected,
            example,
            note, current().getValue().length()
        );   
    }

    private boolean isType(TokenType type){
        return Heddle.TYPES.contains(type);
    }

    private boolean isComparisionOperator(TokenType type){
        return Heddle.COMPARISION_OPERATORS.contains(type);
    }

    private boolean isTermOperador(TokenType type){
        return Heddle.TERM_OPERATORS.contains(type);
    }

    private boolean isFactorOperator(TokenType type){
        return Heddle.FACTOR_OPERATORS.contains(type);
    }

    private boolean isAccessModifier(TokenType type){
        return Heddle.ACESS_MODIFIERS.contains(type);
    }

}