package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.sprite.UVMapping;

public final class GenLookupNode extends GraphicsExpressionNode {
    public static final String NAME = "gen_lookup";

    public GenLookupNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.getImage(), args,
                TypeNode.getImage(), TypeNode.getBool());
    }

    @Override
    public GameImage evaluate(final SymbolTable symbolTable) {
        final Object[] args = arguments.evaluate(symbolTable);

        final GameImage source = (GameImage) args[0];
        final boolean horizontal = (boolean) args[1];

        return UVMapping.generateNaiveLookup(source, horizontal);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
