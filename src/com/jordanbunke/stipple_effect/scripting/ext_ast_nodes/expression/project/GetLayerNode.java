package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

public abstract class GetLayerNode extends ProjectExpressionNode {
    public static final String NAME = "get_layer";

    public GetLayerNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args,
            final TypeNode... expectedTypes
    ) {
        super(position, scope, LayerTypeNode.get(), args, expectedTypes);
    }

    @Override
    public abstract LayerRep evaluate(SymbolTable symbolTable);

    @Override
    protected final String funcName() {
        return NAME;
    }
}
