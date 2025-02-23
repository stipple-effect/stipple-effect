package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;

public class LayerGetNameNode extends LayerExpressionNode {
    public static final String NAME = "get_name";

    public LayerGetNameNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.getString(), args);
    }

    @Override
    public String evaluate(final SymbolTable symbolTable) {
        return layerRep(symbolTable).get().getName();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
