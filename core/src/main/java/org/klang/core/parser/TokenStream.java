package org.klang.core.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.lexer.TokenType;
import org.klang.core.parser.ast.BinaryExpressionNode;
import org.klang.core.parser.ast.ExpressionNode;
import org.klang.core.parser.ast.LiteralExpressionNode;
import org.klang.core.parser.ast.VariableDeclarationNode;
import org.klang.core.parser.ast.VariableExpressionNode;
import org.klang.core.Heddle;
import org.klang.core.errors.ParserException;

public class TokenStream {
    List<Token> tokens = new ArrayList<>();
    int position = 0;

    public TokenStream(ArrayList<Token> tokens){
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

    public void error(String message){
        throw new ParserException(message);
    }

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

            left = new BinaryExpressionNode(left, operator, right, left.line, left.column);
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

    private boolean isType(TokenType type){
        return Heddle.TYPES.contains(type);
    }

    private boolean isComparisionOperator(TokenType type){
        return Heddle.COMPARISION_OPERATORS.contains(type);
    }

    private boolean isTermOperador(TokenType type){
        return Heddle.TERM_OPERATORS.contains(type);
    }

    private boolean isNumber(TokenType type){
        return Heddle.NUMBER_TYPE.contains(type);
    }

    private boolean isFactorOperator(TokenType type){
        return Heddle.FACTOR_OPERATORS.contains(type);
    }
}
