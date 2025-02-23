package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public final class IsSelectedNode extends ProjectExpressionNode {
    public static final String NAME = "is_selected";

    public IsSelectedNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.getBool(), args,
                TypeNode.getInt(), TypeNode.getInt());
    }

    @Override
    public Boolean evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int x = (int) vs[0], y = (int) vs[1];

        return project.getState().getSelection().selected(x, y);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
