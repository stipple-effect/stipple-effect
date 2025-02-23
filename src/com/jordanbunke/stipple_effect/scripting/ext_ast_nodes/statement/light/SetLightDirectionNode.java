package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.light;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.Light;

public final class SetLightDirectionNode extends LightStatementNode {
    public static final String NAME = "set_direction";

    public SetLightDirectionNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.arrayOf(TypeNode.getFloat()));
    }

    @Override
    void operation(final Light light, final SymbolTable symbolTable) {
        final ScriptArray scriptDir =
                (ScriptArray) arguments.evaluate(symbolTable)[0];
        final double[] direction = scriptDir.stream()
                .mapToDouble(dim -> (double) dim).toArray();

        if (!light.point)
            light.setDirection(direction);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
