package org.klang.core.semantics;

import java.util.ArrayList;
import java.util.List;

import org.klang.core.lexer.Token;
import org.klang.core.lexer.TokenType;
import org.klang.core.parser.ast.*;

public class TypeChecker {

    private TypeSymbol currentReturnType = null;
    private final FunctionTable functions = new FunctionTable();

    public void check(ProgramNode program) {
        for (StatementNode node : program.statements) {
            if (node instanceof FunctionDeclarationNode f) {
                collectFunction(f);
            }
        }

        TypeContext global = new TypeContext(null);
        for (StatementNode stmt : program.statements) {
            checkStatement(stmt, global);
        }
    }

    public void collectFunction(FunctionDeclarationNode fn) {
        if (fn.name.getValue().equals("println") || fn.name.getValue().equals("print")) {
            throw new RuntimeException("Cannot redefine builtin function '" + fn.name.getValue() + "'");
        }

        TypeSymbol returnType = resolveTypeSymbol(fn.returnType);

        List<TypeSymbol> params = new ArrayList<>();
        for (ParameterNode p : fn.parameters) {
            params.add(resolveTypeSymbol(p.type));
        }

        functions.declare(new FunctionSymbol(fn.name.getValue(), returnType, params));
    }

    public TypeSymbol checkCallExpression(CallExpressionNode node, TypeContext ctx) {
        FunctionSymbol fn = functions.resolve(node.callee.getValue());

        if (node.arguments.size() != fn.parameters.size()) {
            error("Function '" + fn.name + "' expects "
                    + fn.parameters.size() + " arguments, got "
                    + node.arguments.size(), node);
        }

        for (int i = 0; i < node.arguments.size(); i++) {
            TypeSymbol arg = checkExpression(node.arguments.get(i), ctx, ExpressionContext.ARGUMENT);
            TypeSymbol param = fn.parameters.get(i);

            if (!isAssignable(arg, param)) {
                error("Argument " + (i + 1)
                        + " of '" + fn.name
                        + "' expected " + param
                        + ", got " + arg, node);
            }
        }

        return fn.returnType;
    }

    public void checkStatement(StatementNode node, TypeContext ctx) {

        if (node instanceof ConstantDeclarationNode c) {
            TypeSymbol declared = resolveTypeSymbol(c.type);
            TypeSymbol value = checkExpression(c.value, ctx, ExpressionContext.ASSIGNMENT);

            if (!isAssignable(value, declared)) {
                error("Cannot assign " + value + " to constant " + declared, node);
            }
        
            // constante só aceita literal ou expressão constante
            if (!isCompileTimeConstant(c.value)) {
                error("Constant value must be compile-time evaluable", node);
            }
        
            ctx.declare(
                c.name.getValue(),
                new ConstantSymbol(declared)
            );
            return;
        }

        if (node instanceof VariableDeclarationNode v) {
            TypeSymbol declared = resolveTypeSymbol(v.type);
            TypeSymbol value = checkExpression(v.value, ctx, ExpressionContext.ASSIGNMENT);

            if (!isAssignable(value, declared)) {
                error("Cannot assign " + value + " to " + declared, node);
            }

            ctx.declare(v.name.getValue(), declared);
            return;
        }

        if (node instanceof AssignmentStatementNode a) {
            TypeSymbol target = resolveTarget(a.name, ctx);
            
            if (target instanceof ConstantSymbol) {
                error("Cannot assign to constant", node);
            }

            TypeSymbol value = checkExpression(a.value, ctx, ExpressionContext.ASSIGNMENT);

            if (!isAssignable(value, target)) {
                error("Type mismatch: expected " + target + ", got " + value, node);
            }

            return;
        }

        if (node instanceof ExpressionStatementNode e) {
            checkExpression(e.expression, ctx, ExpressionContext.GENERAL);
            return;
        }

        if (node instanceof BlockStatementNode b) {
            TypeContext local = new TypeContext(ctx);
            for (StatementNode stmt : b.statements) {
                checkStatement(stmt, local);
            }
            return;
        }

        if (node instanceof DecisionStatementNode d) {
            checkDecision(d, ctx);
            return;
        }

        if (node instanceof WhileStatementNode w) {
            TypeSymbol cond = checkExpression(w.condition, ctx, ExpressionContext.CONDITION);

            if (!(cond instanceof PrimitiveTypeSymbol p) || p.type != Type.BOOLEAN) {
                error("while condition must be boolean", node);
            }

            checkStatement(w.body, new TypeContext(ctx));
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

    public void checkDecision(DecisionStatementNode d, TypeContext ctx) {
        TypeSymbol cond = checkExpression(d.condition, ctx, ExpressionContext.CONDITION);

        if (!(cond instanceof PrimitiveTypeSymbol p) || p.type != Type.BOOLEAN) {
            error("if condition must be boolean", d);
        }

        checkStatement(d.ifBlock, new TypeContext(ctx));

        for (OtherwiseBranchNode o : d.otherwiseBranches) {
            TypeSymbol oCond = checkExpression(o.condition, ctx, ExpressionContext.CONDITION);

            if (!(oCond instanceof PrimitiveTypeSymbol p2) || p2.type != Type.BOOLEAN) {
                error("otherwise condition must be boolean", d);
            }

            checkStatement(o.body, new TypeContext(ctx));
        }

        if (d.afterallBlock != null) {
            checkStatement(d.afterallBlock, new TypeContext(ctx));
        }
    }

    public void checkFunctionDeclaration(FunctionDeclarationNode node, TypeContext ctx) {
        if (node.name.getValue().equals("main")) {
            if (!node.parameters.isEmpty()) {
                error("main function cannot have parameters", node);
            }
            if (!(resolveTypeSymbol(node.returnType) instanceof PrimitiveTypeSymbol p)
                    || p.type != Type.VOID) {
                error("main function must return void", node);
            }
        }

        currentReturnType = resolveTypeSymbol(node.returnType);

        TypeContext local = new TypeContext(ctx);
        for (ParameterNode param : node.parameters) {
            local.declare(param.name.getValue(), resolveTypeSymbol(param.type));
        }

        checkStatement(node.body, local);
        currentReturnType = null;
    }

    public void checkReturn(ReturnStatementNode node, TypeContext ctx) {
        if (currentReturnType instanceof PrimitiveTypeSymbol p && p.type == Type.VOID) {
            if (node.value != null) {
                error("Void function cannot return a value", node);
            }
            return;
        }

        if (node.value == null) {
            error("Non-void function must return a value", node);
        }

        TypeSymbol value = checkExpression(node.value, ctx, ExpressionContext.RETURN);

        if (!isAssignable(value, currentReturnType)) {
            error("Return type mismatch: expected "
                    + currentReturnType + ", got " + value, node);
        }
    }

    public TypeSymbol checkExpression(ExpressionNode node, TypeContext ctx, ExpressionContext context) {
        if (node instanceof LiteralExpressionNode l) {
            if (l.value.type == TokenType.INTEGER_LITERAL){
                String v = l.value.getValue();
                
                boolean isAllowed = context == ExpressionContext.ASSIGNMENT || context == ExpressionContext.GENERAL;
                if (!(v.equals("0") || v.equals("1")) && !isAllowed){
                    error(
                        "Magic Number '" + v + "' is not allowed on this context", node
                    );
                }
                
                
                return new PrimitiveTypeSymbol(Type.INTEGER, true);
            }

            // DOUBLE
            if (l.value.type == TokenType.DOUBLE_LITERAL) {

                if (context != ExpressionContext.ASSIGNMENT && context != ExpressionContext.RETURN) {
                    error(
                        "Decimal literal must be assigned to a variable before use",
                        node
                    );
                }
            
                return new PrimitiveTypeSymbol(Type.DOUBLE, true);
            }
            
            
            return resolveLiteral(l.value);
        }

        if (node instanceof NewArrayExpressionNode n) {
            // Resolver tipo base
            TypeSymbol base = resolveTypeSymbol(n.type);
            ArrayTypeSymbol arr = null;

            if (!(base instanceof ArrayTypeSymbol b)) {
                error("Invalid array type", node);
            } else {
                arr = b;
            }
        
            // Verificar size (se existir)
            Integer declaredSize = null;
            if (n.size != null) {
                TypeSymbol sizeType = checkExpression(n.size, ctx, ExpressionContext.ARRAY_SIZE);
            
                if (!(sizeType instanceof PrimitiveTypeSymbol p)
                    || p.type != Type.INTEGER) {
                    error("Array size must be integer", node);
                }
            
                // se size for literal, você pode extrair depois
                declaredSize = -1; // placeholder
            }
        
            // Verificar initializer
            if (n.initializer != null) {
            
                if (n.initializer.isEmpty() && declaredSize == null) {
                    error("Array initializer cannot be empty without explicit size", node);
                }
            
                for (ExpressionNode expr : n.initializer) {
                    TypeSymbol value = checkExpression(expr, ctx, ExpressionContext.Array_INIT);
                
                    if (!arr.isAssignableFrom(value)) {
                        error(
                            "Array initializer expects " + arr.elementType +
                            ", got " + value,
                            expr
                        );
                    }
                }
            
                if (declaredSize != null
                    && n.initializer.size() > declaredSize) {
                    error(
                        "Array initializer exceeds declared size",
                        node
                    );
                }
            }
        
            if (n.size == null && n.initializer == null) {
                error("Array must be explicitly initialized", node);
            }
        
            return base;
        }

        if (node instanceof IndexExpressionNode i) {
            TypeSymbol target = checkExpression(i.target, ctx, ExpressionContext.INDEX);
            TypeSymbol index  = checkExpression(i.index, ctx, ExpressionContext.INDEX);
            ArrayTypeSymbol arr = null;

            if (!(target instanceof ArrayTypeSymbol a)) {
                error("Cannot index non-array type", node);
            } else {
                arr = a;
            }
        
            if (!(index instanceof PrimitiveTypeSymbol p)
                || p.type != Type.INTEGER) {
                error("Array index must be integer", node);
            }
        
            return new PrimitiveTypeSymbol(arr.elementType, false);
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
        return new PrimitiveTypeSymbol(Type.UNKNOWN, false);
    }

    public TypeSymbol checkBinary(BinaryExpressionNode node, TypeContext ctx) {
        TypeSymbol left = checkExpression(node.left, ctx, ExpressionContext.GENERAL);
        TypeSymbol right = checkExpression(node.right, ctx, ExpressionContext.GENERAL);

        switch (node.operator.getType()) {
            case PLUS -> {
                if (left.isString() || right.isString()) {
                    return new PrimitiveTypeSymbol(Type.STRING, true);
                }

                if (left.isNumeric() && right.isNumeric()) {
                    if (left.isDouble() || right.isDouble()) {
                        return new PrimitiveTypeSymbol(Type.DOUBLE, true);
                    }
                    return new PrimitiveTypeSymbol(Type.INTEGER, true);
                }

                error("Invalid operands for '+'", node);
            }

            case MINUS, MULTIPLY, DIVISION, REMAINDER -> {
                if (left.isNumeric() && right.isNumeric()) {
                    if (left.isDouble() || right.isDouble()) {
                        return new PrimitiveTypeSymbol(Type.DOUBLE, true);
                    }
                    return new PrimitiveTypeSymbol(Type.INTEGER, true);
                }
                error("Invalid operands for arithmetic operation", node);
            }

            case DOUBLEEQUAL, NOTEQUAL -> {
                return new PrimitiveTypeSymbol(Type.BOOLEAN, true);
            }

            default -> {
                if (!left.equals(right)) {
                    error("Type mismatch in binary expression", node);
                }
                return left;
            }
        }

        throw new IllegalStateException("Unreachable binary expression");
    }


    public TypeSymbol resolveLiteral(Token token) {
        return switch (token.type) {
            case NUMBER -> new PrimitiveTypeSymbol(Type.INTEGER, true);
            case DOUBLE_LITERAL -> new PrimitiveTypeSymbol(Type.DOUBLE, true);
            case TRUE, FALSE -> new PrimitiveTypeSymbol(Type.BOOLEAN, true);
            case STRING_LITERAL -> new PrimitiveTypeSymbol(Type.STRING, true);
            case CHARACTER_LITERAL -> new PrimitiveTypeSymbol(Type.CHARACTER, true);
            case NULL -> new PrimitiveTypeSymbol(Type.NULL, false);
            default -> new PrimitiveTypeSymbol(Type.UNKNOWN, false);
        };
    }

    public TypeSymbol resolveTypeSymbol(TypeReferenceNode node) {
        Type base = switch (node.getBaseType().getType()) {
            case INTEGER_TYPE -> Type.INTEGER;
            case DOUBLE_TYPE -> Type.DOUBLE;
            case BOOLEAN_TYPE -> Type.BOOLEAN;
            case STRING_TYPE -> Type.STRING;
            case CHARACTER_TYPE -> Type.CHARACTER;
            case VOID -> Type.VOID;
            default -> Type.UNKNOWN;
        };

        if (node.isArray()) {
            return new ArrayTypeSymbol(base);
        }

        return new PrimitiveTypeSymbol(base, true);
    }

    public boolean isAssignable(TypeSymbol from, TypeSymbol to) {
        // UNKNOWN accepts any type (wildcard)
        if (to instanceof PrimitiveTypeSymbol p && p.type == Type.UNKNOWN) {
            return true;
        }

        if (from.isInteger() && to.isDouble()){
            return true;
        }

        return to.isAssignableFrom(from);
    }

    private boolean isCompileTimeConstant(ExpressionNode node) {

        if (node instanceof LiteralExpressionNode) return true;
        if (node instanceof BinaryExpressionNode b) {
            return isCompileTimeConstant(b.left)
                && isCompileTimeConstant(b.right);
        }

        return false;
    }

    private TypeSymbol resolveTarget(ExpressionNode node, TypeContext ctx) {
        
        if (node instanceof VariableExpressionNode v) {
            return ctx.resolve(v.name.getValue());
        }
        error("Invalid assignment target", node);
        return new PrimitiveTypeSymbol(Type.UNKNOWN, false);
    }

    public void error(String msg, AstNode node) {
        throw new RuntimeException(msg + " at " + node.line + ":" + node.column);
    }
}
