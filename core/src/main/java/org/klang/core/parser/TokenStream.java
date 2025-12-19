package org.klang.core.parser;

import java.util.ArrayList;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.lexer.TokenType;
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
            throw new ParserException(message);
        }
        
        return consume();
    }
}
