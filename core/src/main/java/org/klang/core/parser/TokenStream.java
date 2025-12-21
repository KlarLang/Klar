package org.klang.core.parser;

import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.lexer.TokenType;
import org.klang.core.parser.ast.AccessModifier;
import org.klang.core.parser.ast.AssignmentStatementNode;
import org.klang.core.parser.ast.BinaryExpressionNode;
import org.klang.core.parser.ast.BlockStatementNode;
import org.klang.core.parser.ast.CallExpressionNode;
import org.klang.core.parser.ast.ExpressionNode;
import org.klang.core.parser.ast.ExpressionStatementNode;
import org.klang.core.parser.ast.FunctionDeclarationNode;
import org.klang.core.parser.ast.LiteralExpressionNode;
import org.klang.core.parser.ast.ParameterNode;
import org.klang.core.parser.ast.ProgramNode;
import org.klang.core.parser.ast.ReturnStatementNode;
import org.klang.core.parser.ast.StatementNode;
import org.klang.core.parser.ast.VariableDeclarationNode;
import org.klang.core.parser.ast.VariableExpressionNode;
import org.klang.core.semantics.TypeContext;
import org.klang.core.Heddle;
import org.klang.core.errors.ParserException;

public class TokenStream {
    List<Token> tokens = new ArrayList<>();
    int position = 0;

    // Parser

    public VariableDeclarationNode parseValDecl(){
        Token type = current();
        
        if (!isType(type.getType())){
            error("Expected type in variable declaration");
        }
        
        type = consume();
        Token name = expect(TokenType.IDENTIFIER, "Expected variable name");

        expect(TokenType.ASSIGNMENT, "Expected '=' in variable declaration");
        ExpressionNode initializer = parseExpression();

        expect(TokenType.SEMICOLON, "Expected ';' after variable declaration");

        return new VariableDeclarationNode(type, name, initializer, type.getLine(), type.getColumn());
    }

    public ExpressionNode parseExpression(){
        return parseComparision();
    }
    
    // comparison → term ( (== | != | < | > | <= | >=) term )*
    public ExpressionNode parseComparision(){
        ExpressionNode left = parseTerm();

        while (isComparisionOperator(current().getType())){
            Token operator = consume();

            ExpressionNode right = parseTerm();
            
            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    // term → factor ( ('+' | '-') factor )*
    public ExpressionNode parseTerm(){
        ExpressionNode left = parseFactor();

        while (isTermOperador(current().getType())){
            Token operator = consume();

            ExpressionNode right = parseFactor();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    public ExpressionNode parseFactor(){
        ExpressionNode left = parsePrimary();

        while (isFactorOperator(current().getType())){
            Token operator = consume();

            ExpressionNode right = parsePrimary();

            left = new BinaryExpressionNode(left, operator, right, operator.getLine(), operator.getColumn());
        }

        return left;
    }

    public ExpressionNode parsePrimary(){
        if (check(TokenType.NUMBER)){
            Token number = consume();

            return new LiteralExpressionNode(number, number.getLine(), number.getColumn());
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
    
        error("Expected Expression");
        return null;
    }

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

    public StatementNode parseAssignmentStatement(){
        Token identifier = expect(TokenType.IDENTIFIER, "Expected variable name");

        expect(TokenType.ASSIGNMENT, "Expected '=' after variable '" + identifier.getValue() + "'");
        
        ExpressionNode value = parseExpression();
        
        expect(TokenType.SEMICOLON, "Expected ';' after assignment");

        return new AssignmentStatementNode(identifier, value, identifier.getLine(), identifier.getColumn());
    }

    public StatementNode parseExpressionStatement(){
        ExpressionNode expr = parseExpression();

        expect(TokenType.SEMICOLON, "Expected ';' after expression");

        return new ExpressionStatementNode(expr, expr.line, expr.column);
    }

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

    public BlockStatementNode parseBlockStatement(){
        Token openBrace = expect(TokenType.LBRACE, "Expected '{'");

        List<StatementNode> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE, "Expected '}' after block");

        return new BlockStatementNode(statements, openBrace.getLine(), openBrace.getColumn());
    }

    private StatementNode parseFunctionDeclaration(){
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

                parameters.add(new ParameterNode(paramType, parameterName));
            } while (match(TokenType.COMMA));
        }

        expect(TokenType.RPAREN, "Expected ')' after parameters");

        BlockStatementNode body = parseBlockStatement();

        return new FunctionDeclarationNode(access, returnType, name, parameters, body, returnType.getLine(), returnType.getColumn());
    }

    public StatementNode parseReturnStatement(){
        Token keyword = expect(TokenType.RETURN, "Expected 'return'");

        ExpressionNode value = null;

        if (!check(TokenType.SEMICOLON)){
            value = parseExpression();
        }

        expect(TokenType.SEMICOLON, "Expected ';' after return");
        
        return new ReturnStatementNode(value, keyword.getLine(), keyword.getColumn());
    }

    private StatementNode parseStatement() {
        if (isAtEnd()) return null;

        if (isType(current().getType()) &&
            peek(1).getType() == TokenType.IDENTIFIER &&
            peek(2).getType() == TokenType.LPAREN) {
            
            error("Function declaration requires an access modifier (public | protected | internal)");
        }

        if (looksLikeFunctionDeclaration()) {
            return parseFunctionDeclaration();
        }

        // Bloco {}
        if (check(TokenType.LBRACE)){
            return parseBlockStatement();
        }
        
        if (check(TokenType.RETURN)) {
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
    public TokenStream(List<Token> tokens){
        this.tokens = tokens;
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
        throw new ParserException(message);
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