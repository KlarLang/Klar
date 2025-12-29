package org.klang.core.transpilers;


import org.klang.core.lexer.Token;
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
import org.klang.core.parser.ast.TypeReferenceNode;
import org.klang.core.parser.ast.VariableDeclarationNode;
import org.klang.core.parser.ast.VariableExpressionNode;
import org.klang.core.semantics.TypeChecker;
import org.klang.core.semantics.Type;

public class JavaTranspiler {
    private final JavaEmitter out = new JavaEmitter();
    private final JavaContext context = new JavaContext();
    private final TypeChecker t = new TypeChecker();
    private final StringBuilder sb = new StringBuilder();


    public String transpile(ProgramNode program){
        emitHeader();

        for (StatementNode stmt : program.statements) {
            transpileStatement(stmt);
        }

        out.closeBlock();
        return out.result();
    }

    public void emitHeader(){
        out.emit("public class Main");
        out.openBlock();
    }

    private void transpileStatement(StatementNode stmt){
        if (stmt instanceof FunctionDeclarationNode f) {
            transpileFunction(f);
            return;
        }

        if (stmt instanceof VariableDeclarationNode v) {
            transpileVarDecl(v);
            return;
        }

        if (stmt instanceof AssignmentStatementNode a) {
            transpileAssigment(a);
            return;
        }

        if (stmt instanceof ExpressionStatementNode e) {
            out.emitLine(transpileExpression(e.expression) + ";");
            return;
        }

        if (stmt instanceof ReturnStatementNode r) {
            transpileReturn(r);
            return;
        }

        throw new RuntimeException(
            "Unsupported top-level statement: " + stmt.getClass().getSimpleName()
        );
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
                "return " + transpileExpression(r.value) + ";"
            );
        }
    }

    private void transpileAssigment(AssignmentStatementNode a){
        out.emitLine(
            transpileExpression(a.name) + " = " + transpileExpression(a.value) + ";"
        );
    }

    private void transpileVarDecl(VariableDeclarationNode v){
        out.emitLine(
            javaType(v.type) + " " + v.name.getValue() + " = " + transpileExpression(v.value) + ";");
    }

    private String transpileExpression(ExpressionNode e){
        if (e instanceof LiteralExpressionNode l){
            return l.value.getValue();
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
            
            sb.setLength(0);

            sb.append(ce.callee.getValue()).append("(");

            for (int i = 0; i < ce.arguments.size(); i++) {
                if (i > 0){
                    sb.append(", ");
                }
                sb.append(transpileExpression(ce.arguments.get(i)));
            }
            
            sb.append(")");
            return sb.toString();
        }

        throw new RuntimeException("Unsupported expression in transpiler");
    }

    private String javaType(Token type){
        return switch (type.getType()) {
            case INTEGER -> "int";
            case DOUBLE -> "double";
            case BOOLEAN -> "boolean";
            case STRING_TYPE -> "String";
            case CHARACTER_TYPE -> "char";
            case VOID -> "void";

            default -> throw new RuntimeException("Unsupproted type '" + type.getType() + "'");
        };
    }

    private String javaType(TypeReferenceNode type){
        return switch (type.getBaseType().getType()) {
            case INTEGER -> "int";
            case DOUBLE -> "double";
            case BOOLEAN -> "boolean";
            case STRING_TYPE -> "String";
            case CHARACTER_TYPE -> "char";
            case VOID -> "void";

            default -> throw new RuntimeException("Unsupproted type '" + type.getBaseType().getType() + "'");
        };
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

    private Type mapType(Token tk){
        return t.resolveType(tk);
    }
}
