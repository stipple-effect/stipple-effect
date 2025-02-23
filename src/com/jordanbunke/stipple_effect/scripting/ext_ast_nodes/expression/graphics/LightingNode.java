package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptList;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LightTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.Light;
import com.jordanbunke.stipple_effect.scripting.util.LightingUtils;

public final class LightingNode extends GraphicsExpressionNode {
    public static final String NAME = "lighting";

    public LightingNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.getImage(), args, TypeNode.getImage(),
                TypeNode.getImage(), TypeNode.listOf(LightTypeNode.get()));
    }

    @Override
    public GameImage evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);

        final GameImage texture = (GameImage) vs[0], normal = (GameImage) vs[1];
        final ScriptList scriptLights = (ScriptList) vs[2];
        final Light[] lights = scriptLights.stream()
                .map(l -> (Light) l).toArray(Light[]::new);

        return LightingUtils.processLighting(texture, normal, lights);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
