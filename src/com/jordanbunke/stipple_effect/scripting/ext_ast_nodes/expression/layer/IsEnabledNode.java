package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;

public final class IsEnabledNode extends LayerExpressionNode {
    public static final String NAME = "is_enabled";

    public IsEnabledNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.getBool(), args);
    }

    @Override
    public Boolean evaluate(final SymbolTable symbolTable) {
        return layerRep(symbolTable).get().isEnabled();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
