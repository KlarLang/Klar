package org.klar.core.parser.ast;

import java.util.List;

import org.klar.core.lexer.Token;

public class FunctionDeclarationNode extends StatementNode {
    public final UseAnnotationNode use;
    public final AccessModifier access;
    public final TypeReferenceNode returnType;
    public final Token name;
    public final List<ParameterNode> parameters;
    public final BlockStatementNode body;
    
    public FunctionDeclarationNode(AccessModifier access, TypeReferenceNode returnType, Token name, List<ParameterNode> parameters, BlockStatementNode body, UseAnnotationNode use, int line, int column){
        super(line, column);
        this.access = access;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.use = use;
    }
}
