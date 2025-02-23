package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.light;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.MemberFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LightTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.Light;

import java.util.Arrays;

public final class LightGetterNode extends MemberFuncCallNode {
    public static final String IS_POINT = "is_point",
            GET_LUMINOSITY = "get_luminosity", GET_COLOR = "get_color",
            GET_DIRECTION = "get_direction", GET_POSITION = "get_position",
            GET_RADIUS = "get_radius", GET_Z = "get_z";

    private final String fName;

    private LightGetterNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final String fName
    ) {
        super(position, scope, LightTypeNode.get(), getReturnType(fName), args);

        this.fName = fName;
    }

    public static LightGetterNode point(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, IS_POINT);
    }

    public static LightGetterNode luminosity(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_LUMINOSITY);
    }

    public static LightGetterNode color(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_COLOR);
    }

    public static LightGetterNode direction(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_DIRECTION);
    }

    public static LightGetterNode position(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_POSITION);
    }

    public static LightGetterNode radius(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_RADIUS);
    }

    public static LightGetterNode z(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new LightGetterNode(position, scope, args, GET_Z);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final Light light = (Light) receiver.evaluate(symbolTable);
        final boolean point = light.point;

        return switch (fName) {
            case IS_POINT -> point;
            case GET_LUMINOSITY -> light.getLuminosity();
            case GET_COLOR -> light.getColor();
            case GET_RADIUS -> {
                if (point)
                    yield light.getRadius();

                typeOfLightError("radius", false);
                yield null;
            }
            case GET_Z -> {
                if (point)
                    yield light.getZ();

                typeOfLightError("z", false);
                yield null;
            }
            case GET_POSITION -> {
                if (point) {
                    final Coord2D pos = light.getPosition();
                    yield ScriptArray.of(pos.x, pos.y);
                }

                typeOfLightError("position", false);
                yield null;
            }
            case GET_DIRECTION -> {
                if (!point)
                    yield new ScriptArray(Arrays.stream(light.getDirection())
                            .mapToObj(c -> c));

                typeOfLightError("direction", true);
                yield null;
            }
            default -> null;
        };
    }

    private void typeOfLightError(final String property, final boolean point) {
        final String lt = point ? "point" : "directional",
                slt = point ? "directional" : "point";

        ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                getPosition(), "Attempted to get the \"" + property +
                        "\" of a " + lt + " light; \"" + property +
                        "\" is only a property of " + slt + " lights");
    }

    public static TypeNode getReturnType(final String fName) {
        return switch (fName) {
            case IS_POINT -> TypeNode.getBool();
            case GET_LUMINOSITY, GET_RADIUS, GET_Z -> TypeNode.getFloat();
            case GET_COLOR -> TypeNode.getColor();
            case GET_POSITION -> TypeNode.arrayOf(TypeNode.getInt());
            case GET_DIRECTION -> TypeNode.arrayOf(TypeNode.getFloat());
            default -> TypeNode.wildcard();
        };
    }

    @Override
    protected String funcName() {
        return fName;
    }
}
