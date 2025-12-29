package org.klang.core.semantics.flow;

import java.util.List;

import org.klang.core.parser.ast.BlockStatementNode;
import org.klang.core.parser.ast.DecisionStatementNode;
import org.klang.core.parser.ast.FunctionDeclarationNode;
import org.klang.core.parser.ast.OtherwiseBranchNode;
import org.klang.core.parser.ast.ReturnStatementNode;
import org.klang.core.parser.ast.StatementNode;
import org.klang.core.parser.ast.WhileStatementNode;

public final class ReturnStructureAnalyzer {
    public ReturnStructureAnalyzer(){}

    public void analyze(FunctionDeclarationNode fn){
        List<StatementNode> stmts = fn.body.statements;

        if (stmts.isEmpty()){
            error(fn, "function must end with a return statement");
        }

        StatementNode last = stmts.get(stmts.size() - 1) ;

        if (!(last instanceof FunctionDeclarationNode)){
            error(fn, "function must end with a single return statement");
        }

        ensureNoOtherReturns(fn.body, last);;
    }

    private void ensureNoOtherReturns(BlockStatementNode block, StatementNode allowed){
        for (StatementNode stmt : block.statements){
            if (stmt == allowed){
                continue;
            }

            scan(stmt);
        }
    }

    private void scan(StatementNode stmt) {
        if (stmt instanceof ReturnStatementNode) {
            error(stmt, "return is only allowed as the final statement of a function");
        }

        if (stmt instanceof BlockStatementNode block) {
            for (StatementNode s : block.statements) {
                scan(s);
            }
        }

        if (stmt instanceof DecisionStatementNode d) {
            scan(d.ifBlock);

            for (OtherwiseBranchNode o : d.otherwiseBranches) {
                scan(o.body);
            }

            scan(d.afterallBlock);
        }

        if (stmt instanceof WhileStatementNode w) {
            scan(w.body);
        }
    }


    public void error (StatementNode fn, String str){
        return;
    }
}
