package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.light;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.Light;

import java.awt.*;

public final class SetLightColorNode extends LightStatementNode {
    public static final String NAME = "set_color";

    public SetLightColorNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getColor());
    }

    @Override
    void operation(final Light light, final SymbolTable symbolTable) {
        final Color color = (Color) arguments.evaluate(symbolTable)[0];

        light.setColor(color);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
