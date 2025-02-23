package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;

import java.awt.*;

public final class HSVNode extends GraphicsExpressionNode {
    public static final String NAME = "hsv";

    private HSVNode(
            final TextPosition position, final ExpressionNode[] args,
            final TypeNode... expectedTypes
    ) {
        super(position, TypeNode.getColor(), args, expectedTypes);
    }

    public static HSVNode newHSV(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new HSVNode(position, args, TypeNode.getFloat(),
                TypeNode.getFloat(), TypeNode.getFloat());
    }

    public static HSVNode withAlpha(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new HSVNode(position, args, TypeNode.getFloat(),
                TypeNode.getFloat(), TypeNode.getFloat(), TypeNode.getInt());
    }

    @Override
    public Color evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final double h = (double) vs[0],
                s = (double) vs[1], v = (double) vs[2];
        final int a = vs.length == 4 ? (int) vs[3] : Constants.RGBA_SCALE;

        if (h < 0d || h > 1d)
            fireError("hue", 0, String.valueOf(h));
        if (s < 0d || s > 1d)
            fireError("saturation", 1, String.valueOf(s));
        if (v < 0d || v > 1d)
            fireError("value", 2, String.valueOf(v));
        if (a < 0 || a > Constants.RGBA_SCALE)
            fireError("alpha (opacity)", 3, String.valueOf(a));

        if (ScriptErrorLog.hasNoErrors())
            return ColorMath.fromHSV(h, s, v, a);

        return null;
    }

    private void fireError(
            final String component, final int argIndex, String value
    ) {
        ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                arguments.get(argIndex).getPosition(),
                "The value for the color's \"" + component +
                        "\" was out of bounds (" + value + ")");
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
