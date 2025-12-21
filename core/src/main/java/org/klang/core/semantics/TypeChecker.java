package org.klang.core.semantics;

import java.util.List;

import org.klang.core.errors.SemanticException;
import org.klang.core.lexer.Token;
import org.klang.core.parser.ast.*;

public class TypeChecker {

    public void check(ProgramNode program) {
        TypeContext global = new TypeContext(null);

        for (StatementNode stmt : program.statements) {
            checkStatement(stmt, global);
        }
    }

    // STATEMENTS

    private void checkStatement(StatementNode node, TypeContext ctx) {

        if (node instanceof VariableDeclarationNode v) {
            checkVariableDeclaration(v, ctx);
            return;
        }

        if (node instanceof AssignmentStatementNode a) {
            checkAssignment(a, ctx);
            return;
        }

        if (node instanceof ExpressionStatementNode e) {
            checkExpression(e.expression, ctx);
            return;
        }

        if (node instanceof BlockStatementNode b) {
            checkBlock(b, ctx);
            return;
        }

        if (node instanceof FunctionDeclarationNode f) {
            checkFunctionDeclaration(f, ctx);
            return;
        }

        error("Unsupported statement", node);
    }

    private void checkVariableDeclaration(VariableDeclarationNode node, TypeContext ctx) {
        Type declared = resolveType(node.type);
        Type value = checkExpression(node.value, ctx);

        if (declared != value) {
            error("Cannot assign " + value + " to " + declared, node);
        }

        ctx.declare(node.name.getValue(), declared);
    }

    private void checkAssignment(AssignmentStatementNode node, TypeContext ctx) {
        Type target = ctx.resolve(node.name.getValue());
        Type value = checkExpression(node.value, ctx);

        if (target != value) {
            error("Type mismatch in assignment: expected " + target + ", got " + value, node);
        }
    }

    private void checkBlock(BlockStatementNode node, TypeContext parent) {
        TypeContext local = new TypeContext(parent);

        for (StatementNode stmt : node.statements) {
            checkStatement(stmt, local);
        }
    }

    private void checkFunctionDeclaration(FunctionDeclarationNode node, TypeContext ctx) {
        Type returnType = resolveType(node.returnType);

        // Novo escopo para parâmetros + corpo
        TypeContext local = new TypeContext(ctx);

        for (ParameterNode param : node.parameters) {
            Type paramType = resolveType(param.type);
            local.declare(param.name.getValue(), paramType);
        }

        checkBlock(node.body, local);
    }

    /* =========================
       EXPRESSIONS
       ========================= */

    private Type checkExpression(ExpressionNode node, TypeContext ctx) {

        if (node instanceof LiteralExpressionNode l) {
            return resolveType(l.value);
        }

        if (node instanceof VariableExpressionNode v) {
            return ctx.resolve(v.name.getValue());
        }

        if (node instanceof BinaryExpressionNode b) {
            return checkBinaryExpression(b, ctx);
        }

        if (node instanceof CallExpressionNode c) {
            return checkCallExpression(c, ctx);
        }

        error("Unsupported expression", node);
        return Type.UNKNOWN;
    }

    private Type checkBinaryExpression(BinaryExpressionNode node, TypeContext ctx) {
        Type left = checkExpression(node.left, ctx);
        Type right = checkExpression(node.right, ctx);

        if (left != right) {
            error("Type mismatch in binary expression: " + left + " vs " + right, node);
        }

        return left;
    }

    private Type checkCallExpression(CallExpressionNode node, TypeContext ctx) {
        // Placeholder: ainda não há tabela de funções
        for (ExpressionNode arg : node.arguments) {
            checkExpression(arg, ctx);
        }
        return Type.UNKNOWN;
    }

    /* =========================
       UTIL
       ========================= */

    private Type resolveType(Token token) {
        return switch (token.getType()) {
            case INTEGER -> Type.INTEGER;
            case DOUBLE -> Type.DOUBLE;
            case BOOLEAN -> Type.BOOLEAN;
            case STRING_TYPE -> Type.STRING;
            case CHARACTER_TYPE -> Type.CHARACTER;
            case VOID -> Type.VOID;
            default -> Type.UNKNOWN;
        };
    }

    private void error(String message, AstNode node) {
        throw new SemanticException(
            message + " at line " + node.line + ", column " + node.column
        );
    }
}
