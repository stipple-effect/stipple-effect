package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;

public class GetFrameDurationsNode extends ProjectExpressionNode {
    public static final String NAME = "get_frame_durations";

    public GetFrameDurationsNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.arrayOf(TypeNode.getFloat()), args);
    }

    @Override
    public ScriptArray evaluate(final SymbolTable symbolTable) {
        return new ScriptArray(getProject(symbolTable).getState()
                .getFrameDurations().stream().map(c -> c));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
