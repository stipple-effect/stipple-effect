package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

public final class DisableLayerNode extends LayerStatementNode {
    public static final String NAME = "disable";

    public DisableLayerNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    protected void operation(
            final LayerRep layer, final SymbolTable symbolTable
    ) {
        layer.project().disableLayer(layer.index());
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
