package org.klar.core.transpilers;


import java.nio.file.Path;
import java.util.stream.Collectors;

import org.klar.core.errors.SourceManager;
import org.klar.core.lexer.Token;
import org.klar.core.parser.ast.AssignmentStatementNode;
import org.klar.core.parser.ast.AstNode;
import org.klar.core.parser.ast.BinaryExpressionNode;
import org.klar.core.parser.ast.BlockStatementNode;
import org.klar.core.parser.ast.CallExpressionNode;
import org.klar.core.parser.ast.ConstantDeclarationNode;
import org.klar.core.parser.ast.DecisionStatementNode;
import org.klar.core.parser.ast.ExpressionNode;
import org.klar.core.parser.ast.ExpressionStatementNode;
import org.klar.core.parser.ast.FunctionDeclarationNode;
import org.klar.core.parser.ast.IndexExpressionNode;
import org.klar.core.parser.ast.LiteralExpressionNode;
import org.klar.core.parser.ast.NewArrayExpressionNode;
import org.klar.core.parser.ast.OtherwiseBranchNode;
import org.klar.core.parser.ast.ParameterNode;
import org.klar.core.parser.ast.ProgramNode;
import org.klar.core.parser.ast.ReturnStatementNode;
import org.klar.core.parser.ast.StatementNode;
import org.klar.core.parser.ast.TypeReferenceNode;
import org.klar.core.parser.ast.VariableDeclarationNode;
import org.klar.core.parser.ast.VariableExpressionNode;
import org.klar.core.parser.ast.WhileStatementNode;
import org.klar.core.semantics.TypeChecker;
import org.klar.core.semantics.TypeSymbol;
import org.klar.core.semantics.ArrayTypeSymbol;
import org.klar.core.semantics.PrimitiveTypeSymbol;
import org.klar.core.semantics.Type;

public class JavaTranspiler {
    private final SourceManager sm;
    private final Path path;
    private final JavaEmitter out = new JavaEmitter();
    private final JavaContext context = new JavaContext();
    private final TypeChecker t;;
    private final String fileName;

    public JavaTranspiler(String fileName, SourceManager sm, Path path){
        this.fileName = fileName;
        this.sm = sm;
        this.path = path;

        this.t = new TypeChecker(sm, path);
    }

    public String transpile(ProgramNode program){
        emitHeader();

        for (StatementNode stmt : program.statements) {
            transpileStatement(stmt);
        }

        out.closeBlock();
        return out.result();
    }

    public void emitHeader(){
        out.emit("public class " + fileName);
        out.openBlock();
    }

    private void transpileStatement(AstNode stmt){
        if (stmt instanceof FunctionDeclarationNode f) {
            out.newLine();
            transpileFunction(f);
            return;
        }

        if (stmt instanceof ConstantDeclarationNode c){
            transpileConstantDecl(c);
            return;
        }

        if (stmt instanceof VariableDeclarationNode v) {
            transpileVarDecl(v);
            return;
        }

        if (stmt instanceof DecisionStatementNode d){
            out.newLine();
            transpileDecision(d);
            return;
        }

        if (stmt instanceof WhileStatementNode w){
            transpileWhile(w);
            return;
        }

        if (stmt instanceof AssignmentStatementNode a) {
            transpileAssigment(a);            
            return;
        }

        if (stmt instanceof ExpressionStatementNode e) {
            out.emitLine(transpileExpression(e.expression) + out.semicollon());
            return;
        }

        if (stmt instanceof ReturnStatementNode r) {
            out.newLine();
            transpileReturn(r);
            return;
        }

        if (stmt instanceof NewArrayExpressionNode n){
            transpileArrayDecl(n);
            return;
        }

        throw new RuntimeException(
            "Unsupported top-level statement: " + stmt.getClass().getSimpleName()
        );
    }

    private void transpileArrayDecl(NewArrayExpressionNode n){
        String baseType = javaType(n.type);

        baseType = baseType.replace("[]", "");

        out.emitLine("new " + baseType + "[" + transpileExpression(n.size) + "]");
    }

    private void transpileConstantDecl(ConstantDeclarationNode c){
        out.emitLine(
            "final " + javaType(c.type) + " " + c.name.getValue() + " = " + transpileExpression(c.value) + out.semicollon());
    }

    private void transpileWhile(WhileStatementNode w){
        out.indent();
        out.emit("while (");
        out.emit(transpileExpression(w.condition));
        out.emit(")");

        out.openBlock();
        transpileBlock(w.body);
        out.closeBlock();

        return;
    }

    private void transpileDecision(DecisionStatementNode d){
        out.indent();
        out.emit("if (");
        out.emit(transpileExpression(d.condition));
        out.emit(")");
        out.openBlock();

        transpileBlock(d.ifBlock);
        out.closeBlock();

        for (OtherwiseBranchNode o : d.otherwiseBranches){
            out.indent();
            out.emit("else if (");
            out.emit(transpileExpression(o.condition));
            out.emit(")");

            if (o.reason != null){
                out.openBlockWith(" // " + o.reason + "\n");
            } else {
                out.openBlock();
            }

            transpileBlock(o.body);
            out.closeBlock();
        }

        if (d.afterallBlock != null){
            out.indent();

            out.emit("else ");
            out.openBlock();
            transpileBlock(d.afterallBlock);
            out.closeBlock();
        }
    }

    private void transpileFunction(FunctionDeclarationNode fn){
        boolean isMain = fn.name.getValue().equals("main");

        context.currentReturnType = mapType(fn.returnType);
        out.emit("    public static ");

        if (isMain){
            out.emit("void main(String[] args");
        } else {
            out.emit(javaType(fn.returnType));
            out.emit(" ");
            out.emit(fn.name.getValue());
            out.emit("(");
            
            for (int i = 0; i < fn.parameters.size(); i++){
                ParameterNode p = fn.parameters.get(i);
                if (i > 0){
                    out.emit(", ");
                }
                
                out.emit(javaType(p.type) + " " + p.name.getValue());
            }
        }
            
        out.emit(")");
        out.openBlock();
        transpileBlock(fn.body);
        out.closeBlock();
    }

    private void transpileBlock(BlockStatementNode block){
        for (StatementNode stmt : block.statements){
            transpileStatement(stmt);
        }
    }

    private void transpileReturn(ReturnStatementNode r){
        if (context.currentReturnType == Type.VOID){
            out.emitLine("return;");
        } else {
            out.emitLine(
                "return " + transpileExpression(r.value) + out.semicollon()
            );
        }
    }

    private void transpileAssigment(AssignmentStatementNode a){
        out.emitLine(
            transpileExpression(a.name) + " = " + transpileExpression(a.value) + out.semicollon()
        );
    }

    private void transpileVarDecl(VariableDeclarationNode v){
        out.emitLine(
            javaType(v.type) + " " + v.name.getValue() + " = " + transpileExpression(v.value) + out.semicollon());
    }

    private String transpileExpression(ExpressionNode e){
        if (e instanceof LiteralExpressionNode l) {
            return switch (l.value.getType()) {
                case TRUE -> "true";
                case FALSE -> "false";
                case STRING_LITERAL -> l.value.getValue();
                case INTEGER_LITERAL, DOUBLE_LITERAL -> l.value.getValue();
                case NULL -> "null";
                default -> throw new RuntimeException(
                    "Unsupported literal: " + l.value.getType()
                );
            };
        }

        if (e instanceof IndexExpressionNode i) {
            return transpileExpression(i.target) + "[" + transpileExpression(i.index) + "]";
        }

        if (e instanceof VariableExpressionNode v ){
            return v.name.getValue();
        }

        if (e instanceof BinaryExpressionNode b ){
            return transpileExpression(b.left) + " " + javaOperator(b.operator) + " " + transpileExpression(b.right);
        }

        if (e instanceof CallExpressionNode ce ){
            if (ce.callee.getValue().equals("println")) {
                return "System.out.println(" + transpileExpression(ce.arguments.get(0)) + ")";
            }
            
            if (ce.callee.getValue().equals("print")) {
                return "System.out.print(" + transpileExpression(ce.arguments.get(0)) + ")";
            }

            if (ce.callee.getValue().equals("printf")) {
                return "System.out.printf(" + transpileExpression(ce.arguments.get(0)) + ")";
            }

            String args = ce.arguments.stream()
            .map(this::transpileExpression)
            .collect(Collectors.joining(", "));

            return ce.callee.getValue() + "(" + args + ")";
        }

        throw new RuntimeException("Unsupported expression in transpiler");
    }

    private String javaType(TypeReferenceNode type){
        String base = switch (type.getBaseType().getType()) {
            case INTEGER_TYPE -> "int";
            case DOUBLE_TYPE -> "double";
            case BOOLEAN_TYPE -> "boolean";
            case STRING_TYPE -> "String";
            case CHARACTER_TYPE -> "char";
            case VOID -> "void";

            default -> throw new RuntimeException("Unsupproted type '" + type.getBaseType().getType() + "'");
        };

        if (type.isArray()){
            return base + "[]";
        }

        return base;
    }

    private String javaOperator(Token op) {
        return switch (op.getType()) {
            case PLUS -> "+";
            case MINUS -> "-";
            case MULTIPLY -> "*";
            case DIVISION -> "/";
            case REMAINDER -> "%";

            case DOUBLEEQUAL -> "==";
            case NOTEQUAL -> "!=";
            case LT -> "<";
            case GT -> ">";
            case LTE -> "<=";
            case GTE -> ">=";

            default -> throw new RuntimeException(
                "Unsupported operator: " + op.getType()
            );
        };
    }

    private Type mapType(TypeReferenceNode tk){
        TypeSymbol type = t.resolveTypeSymbol(tk);

        if (type instanceof PrimitiveTypeSymbol p){
            return p.type;
        }

        if (type instanceof ArrayTypeSymbol p){
            return p.elementType;
        }

        return Type.UNKNOWN;
    }
}
