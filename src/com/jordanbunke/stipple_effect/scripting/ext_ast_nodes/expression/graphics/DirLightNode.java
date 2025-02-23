package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.Light;

import java.awt.*;

public final class DirLightNode extends LightConstructorNode {
    public static final String NAME = "dir_light";

    public DirLightNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, args, TypeNode.getFloat(), TypeNode.getColor(),
                TypeNode.arrayOf(TypeNode.getFloat()));
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);

        final double luminosity = (double) vs[0];
        final Color color = (Color) vs[1];
        final ScriptArray scriptDir = (ScriptArray) vs[2];
        final double[] direction = scriptDir.stream()
                .mapToDouble(dim -> (double) dim).toArray();

        return Light.directionLight(luminosity, color, direction);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
