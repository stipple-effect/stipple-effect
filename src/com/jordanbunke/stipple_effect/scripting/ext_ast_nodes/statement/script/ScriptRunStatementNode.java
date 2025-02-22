package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.script;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.ScopedStatementNode;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ScriptTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;

public final class ScriptRunStatementNode extends ScopedStatementNode {
    public static final String NAME = "run";

    public ScriptRunStatementNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, ScriptTypeNode.get(), args);
    }

    @Override
    public void semanticErrorCheck(final SymbolTable symbolTable) {
        scope.semanticErrorCheck(symbolTable, getPosition());

        for (ExpressionNode arg : arguments.args())
            arg.semanticErrorCheck(symbolTable);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEScript script = (SEScript) scope.evaluate(symbolTable);
        final Object[] args = arguments.getValues(symbolTable);

        // execute before every internal script execution
        final SymbolTable scriptTable =
                SymbolTable.root(script.head(), script.path());
        script.head().semanticErrorCheck(scriptTable);

        if (ScriptErrorLog.hasNoErrors())
            script.head().execute(scriptTable, args);

        return FuncControlFlow.cont();
    }

    @Override
    protected String callName() {
        return NAME;
    }
}
