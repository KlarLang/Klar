package org.klang.core.semantics;

import java.util.ArrayList;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.parser.ast.*;

public class TypeChecker {

    private Type currentReturnType = null;
    private final FunctionTable functions = new FunctionTable();

    public void check(ProgramNode program) {
        for (StatementNode node : program.statements){
            if (node instanceof FunctionDeclarationNode f){
                collectFunction(f);
            }
        }

        TypeContext global = new TypeContext(null);
        for (StatementNode stmt : program.statements) {
            checkStatement(stmt, global);
        }
    }

    public Type checkCallExpression(CallExpressionNode node, TypeContext ctx){
        FunctionSymbol fn = functions.resolve(node.callee.getValue());

        if (node.arguments.size() != fn.parameters.size()){
            error(
                "Function '" + fn.name + "' expects "
                + fn.parameters.size()
                + " arguments, got "
                + node.arguments.size(),
                node
            );
        }

        for (int i = 0; i < node.arguments.size(); i++){
            Type arg = checkExpression(node.arguments.get(i), ctx);
            Type param = fn.parameters.get(i);

            if (!isAssignable(arg, param)){
                error(
                    "Argument " + (i + 1)
                    + " of '" + fn.name
                    + "' expected " + param
                    + ", got " + arg,
                    node
                );
            }
        }

        return fn.returnType;
    }

    public void collectFunction(FunctionDeclarationNode fn){
        if (fn.name.getValue().equals("println")) {
            throw new RuntimeException("Cannot redefine builtin function 'println'");
        }

        if (fn.name.getValue().equals("print")) {
            throw new RuntimeException("Cannot redefine builtin function 'print'");
        }

        Type returnType = resolveType(fn.returnType);

        List<Type> args = new ArrayList<>(10);
        for (ParameterNode p : fn.parameters){
            args.add(resolveType(p.type));
        }

        functions.declare(new FunctionSymbol(fn.name.getValue(), returnType, args));
    }

    public void checkStatement(StatementNode node, TypeContext ctx) {

        if (node instanceof VariableDeclarationNode v) {
            Type declared = resolveType(v.type.getBaseType());
            Type value = checkExpression(v.value, ctx);

            if (!isAssignable(value, declared)) {
                error("Cannot assign " + value + " to " + declared, node);
            }

            ctx.declare(v.name.getValue(), declared);
            return;
        }

        if (node instanceof AssignmentStatementNode a) {
            Type target = resolveTarget(a.name, ctx);
            Type value  = checkExpression(a.value, ctx);


            if (!isAssignable(value, target)) {
                error("Type mismatch: expected " + target + ", got " + value, node);
            }
            return;
        }

        if (node instanceof ExpressionStatementNode e) {
            checkExpression(e.expression, ctx);
            return;
        }

        if (node instanceof BlockStatementNode b) {
            TypeContext local = new TypeContext(ctx);
            for (StatementNode stmt : b.statements) {
                checkStatement(stmt, local);
            }
            return;
        }

        if (node instanceof FunctionDeclarationNode f) {
            checkFunctionDeclaration(f, ctx);
            return;
        }

        if (node instanceof ReturnStatementNode r) {
            checkReturn(r, ctx);
            return;
        }

        error("Unsupported statement", node);
    }

    public void checkFunctionDeclaration(FunctionDeclarationNode node, TypeContext ctx) {
        if (node.name.getValue().equals("main")) {
            if (!node.parameters.isEmpty()) {
                error("main function cannot have parameters", node);
            }
            if (resolveType(node.returnType) != Type.VOID) {
                error("main function must return void", node);
            }
        }

        Type returnType = resolveType(node.returnType);
        currentReturnType = returnType;

        TypeContext local = new TypeContext(ctx);

        for (ParameterNode param : node.parameters) {
            Type paramType = resolveType(param.type);
            local.declare(param.name.getValue(), paramType);
        }

        checkStatement(node.body, local);
        currentReturnType = null;
    }

    public void checkReturn(ReturnStatementNode node, TypeContext ctx) {

        if (currentReturnType == Type.VOID) {
            if (node.value != null) {
                error("Void function cannot return a value", node);
            }
            return;
        }

        if (node.value == null) {
            error("Non-void function must return a value", node);
        }

        Type value = checkExpression(node.value, ctx);

        if (!isAssignable(value, currentReturnType)) {
            error("Return type mismatch: expected " + currentReturnType + ", got " + value, node);
        }
    }

    public Type checkExpression(ExpressionNode node, TypeContext ctx) {

        if (node instanceof LiteralExpressionNode l) {
            return resolveLiteral(l.value);
        }

        if (node instanceof VariableExpressionNode v) {
            return ctx.resolve(v.name.getValue());
        }

        if (node instanceof BinaryExpressionNode b) {
            return checkBinary(b, ctx);
        }

        if (node instanceof CallExpressionNode c) {            
            return checkCallExpression(c, ctx);
        }

        error("Unsupported expression", node);
        return Type.UNKNOWN;
    }

    public Type checkBinary(BinaryExpressionNode node, TypeContext ctx) {
        Type left = checkExpression(node.left, ctx);
        Type right = checkExpression(node.right, ctx);

        return switch (node.operator.getType()) {

            case DOUBLEEQUAL, NOTEQUAL -> Type.BOOLEAN;

            case PLUS -> {
                if (left == Type.STRING || right == Type.STRING) {
                    yield Type.STRING;
                }
                if (left == right) {
                    yield left;
                }
                error("Invalid operands for '+'", node);
                yield Type.UNKNOWN;
            }

            default -> {
                if (left != right) {
                    error("Type mismatch in binary expression", node);
                }
                yield left;
            }
        };
    }

    public Type resolveLiteral(Token token) {
        return switch (token.getType()) {
            case NUMBER -> Type.INTEGER;
            case TRUE, FALSE -> Type.BOOLEAN;
            case STRING_LITERAL -> Type.STRING;
            case NULL -> Type.NULL;
            default -> Type.UNKNOWN;
        };
    }

    public Type resolveType(Token token) {
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


    public void error(String msg, AstNode node) {
        throw new RuntimeException(msg + " at " + node.line + ":" + node.column);
    }

    public boolean isAssignable(Type from, Type to) {
        if (to == Type.UNKNOWN) return true;

        if (from == to) return true;

        if (from == Type.NULL && to.isReference()) return true;

        return false;
    }

    private Type resolveTarget(ExpressionNode node, TypeContext ctx) {
        if (node instanceof VariableExpressionNode v) {
            return ctx.resolve(v.name.getValue());
        }
        error("Invalid assignment target", node);
        return Type.UNKNOWN;
    }

}
