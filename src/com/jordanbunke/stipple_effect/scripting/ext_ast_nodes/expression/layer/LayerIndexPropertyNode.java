package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.PropertyNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

public final class LayerIndexPropertyNode extends PropertyNode {
    public static final String NAME = "index";

    public LayerIndexPropertyNode(
            final TextPosition position, final ExpressionNode scope
    ) {
        super(position, scope, LayerTypeNode.get(), TypeNode.getInt());
    }

    @Override
    public Integer evaluate(final SymbolTable symbolTable) {
        final LayerRep l = (LayerRep) receiver.evaluate(symbolTable);
        return l.index();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
