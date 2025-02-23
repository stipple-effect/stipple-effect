package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.light;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.Light;

public final class LightFloatSetterNode extends LightStatementNode {
    public static final String SET_LUMINOSITY = "set_luminosity",
            SET_RADIUS = "set_radius", SET_Z = "set_z";

    private final String fName;

    private LightFloatSetterNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final String fName
    ) {
        super(position, scope, args, TypeNode.getFloat());

        this.fName = fName;
    }

    public static LightFloatSetterNode luminosity(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightFloatSetterNode(position, scope, args, SET_LUMINOSITY);
    }

    public static LightFloatSetterNode radius(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightFloatSetterNode(position, scope, args, SET_RADIUS);
    }

    public static LightFloatSetterNode z(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightFloatSetterNode(position, scope, args, SET_Z);
    }

    @Override
    void operation(final Light light, final SymbolTable symbolTable) {
        final double arg = (double) arguments.evaluate(symbolTable)[0];

        switch (fName) {
            case SET_LUMINOSITY -> light.setLuminosity(arg);
            case SET_RADIUS -> {
                if (light.point)
                    light.setRadius(arg);
            }
            case SET_Z -> {
                if (light.point)
                    light.setZ(arg);
            }
        }
    }

    @Override
    protected String funcName() {
        return fName;
    }
}
