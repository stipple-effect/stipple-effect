package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.PropertyNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;

import java.awt.*;

public final class ColorPropertyGetterNode extends PropertyNode {
    public static final String HUE = "hue", SAT = "sat", VAL = "val";

    private final String property;

    private ColorPropertyGetterNode(
            final TextPosition position, final ExpressionNode scope,
            final String property
    ) {
        super(position, scope, TypeNode.getColor(), TypeNode.getFloat());

        this.property = property;
    }

    public static ColorPropertyGetterNode hue(
            final TextPosition position, final ExpressionNode scope
    ) {
        return new ColorPropertyGetterNode(position, scope, HUE);
    }

    public static ColorPropertyGetterNode sat(
            final TextPosition position, final ExpressionNode scope
    ) {
        return new ColorPropertyGetterNode(position, scope, SAT);
    }

    public static ColorPropertyGetterNode val(
            final TextPosition position, final ExpressionNode scope
    ) {
        return new ColorPropertyGetterNode(position, scope, VAL);
    }

    @Override
    public Double evaluate(final SymbolTable symbolTable) {
        final Color c = (Color) receiver.evaluate(symbolTable);

        return switch (property) {
            case HUE -> ColorMath.rgbToHue(c);
            case SAT -> ColorMath.rgbToSat(c);
            default -> ColorMath.rgbToValue(c);
        };
    }

    @Override
    protected String funcName() {
        return property;
    }
}
