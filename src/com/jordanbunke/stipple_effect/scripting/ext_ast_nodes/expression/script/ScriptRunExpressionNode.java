package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.script;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.MemberFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ScriptTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;

public final class ScriptRunExpressionNode extends MemberFuncCallNode {
    public static final String NAME = "run";

    public ScriptRunExpressionNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, ScriptTypeNode.get(),
                TypeNode.wildcard(), args);
    }

    @Override
    public void semanticErrorCheck(final SymbolTable symbolTable) {
        receiver.semanticErrorCheck(symbolTable);

        for (ExpressionNode arg : arguments.args())
            arg.semanticErrorCheck(symbolTable);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final SEScript script = (SEScript) receiver.evaluate(symbolTable);
        final Object[] args = arguments.evaluate(symbolTable);

        // execute before every internal script execution
        final SymbolTable scriptTable =
                SymbolTable.root(script.head(), script.path());
        script.head().semanticErrorCheck(scriptTable);

        return ScriptErrorLog.hasNoErrors()
                ? script.head().execute(scriptTable, args) : null;
    }

    @Override
    public TypeNode getType(final SymbolTable symbolTable) {
        final SEScript script = (SEScript) receiver.evaluate(symbolTable);

        return script.head().getReturnType();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
