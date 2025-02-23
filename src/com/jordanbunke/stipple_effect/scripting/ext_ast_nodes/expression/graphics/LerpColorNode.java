package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;

import java.awt.*;

public final class LerpColorNode extends GraphicsExpressionNode {
    public static final String NAME = "lerp_color";

    public LerpColorNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.getColor(), args, TypeNode.getColor(),
                TypeNode.getColor(), TypeNode.getFloat());
    }

    @Override
    public Color evaluate(final SymbolTable symbolTable) {
        final Object[] args = arguments.evaluate(symbolTable);

        final Color a = (Color) args[0], b = (Color) args[1];
        final double t = (double) args[2];

        return ColorMath.lerp(a, b, t);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
