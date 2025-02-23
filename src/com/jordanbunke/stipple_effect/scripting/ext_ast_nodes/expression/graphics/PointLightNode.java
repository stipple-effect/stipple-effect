package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.scripting.util.Light;

import java.awt.*;

public final class PointLightNode extends LightConstructorNode {
    public static final String NAME = "point_light";

    public PointLightNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, args, TypeNode.getFloat(), TypeNode.getColor(),
                TypeNode.getFloat(), TypeNode.getInt(), TypeNode.getInt(),
                TypeNode.getFloat());
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);

        final double luminosity = (double) vs[0],
                radius = (double) vs[2], z = (double) vs[5];
        final Color color = (Color) vs[1];
        final Coord2D position = new Coord2D((int) vs[3], (int) vs[4]);

        return Light.pointLight(luminosity, color, radius, position, z);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
