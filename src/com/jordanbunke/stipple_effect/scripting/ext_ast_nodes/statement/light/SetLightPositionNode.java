package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.light;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.scripting.util.Light;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SetLightPositionNode extends LightStatementNode {
    public static final String NAME = "set_position";

    public SetLightPositionNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.arrayOf(TypeNode.getInt()));
    }

    @Override
    void operation(final Light light, final SymbolTable symbolTable) {
        final ScriptArray scriptPos =
                (ScriptArray) arguments.evaluate(symbolTable)[0];

        if (scriptPos.size() != 2) {
            StatusUpdates.scriptActionNotPermitted(
                    "set the position of the point light",
                    "position array contains " + scriptPos.size() +
                            " elements instead of 2",
                    getPosition());
            return;
        }

        final int x = (int) scriptPos.get(0), y = (int) scriptPos.get(1);

        if (light.point)
            light.setPosition(new Coord2D(x, y));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
