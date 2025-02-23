package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;

public final class UnboundedNode extends SaveConfigStatementNode {
    public static final String NAME = "unbounded";

    public UnboundedNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        getSaveConfig(symbolTable).setSaveRangeOfFrames(false);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
