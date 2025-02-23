package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;
import com.jordanbunke.stipple_effect.utility.Constants;

public final class SetOpacityNode extends LayerStatementNode {
    public static final String NAME = "set_opacity";

    public SetOpacityNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getFloat());
    }

    @Override
    protected void operation(
            final LayerRep layer, final SymbolTable symbolTable
    ) {
        final double opacity =
                (double) arguments.get(0).evaluate(symbolTable);

        layer.project().changeLayerOpacity(
                MathPlus.bounded(0d, opacity, Constants.OPAQUE),
                layer.index(), true);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
