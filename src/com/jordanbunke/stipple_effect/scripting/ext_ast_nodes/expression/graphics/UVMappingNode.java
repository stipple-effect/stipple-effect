package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.sprite.UVMapping;

import java.util.Arrays;

public final class UVMappingNode extends GraphicsExpressionNode {
    public static final String NAME = "uv_mapping";

    public UVMappingNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.getImage(), args, TypeNode.getImage(),
                TypeNode.getImage(), TypeNode.getImage());
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final GameImage[] args = Arrays.stream(arguments.evaluate(symbolTable))
                .map(img -> (GameImage) img).toArray(GameImage[]::new);
        final GameImage texture = args[0], map = args[1], animation = args[2];

        if (texture.getWidth() != map.getWidth() ||
                texture.getHeight() != map.getHeight())
            return texture;

        return UVMapping.replace(texture, map, animation);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
