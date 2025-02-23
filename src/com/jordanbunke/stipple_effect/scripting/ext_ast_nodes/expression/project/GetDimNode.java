package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.state.ProjectState;

public final class GetDimNode extends ProjectExpressionNode {
    public static final String WIDTH = "get_width", HEIGHT = "get_height";

    private final boolean width;

    public GetDimNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean width
    ) {
        super(position, scope, TypeNode.getInt(), args);

        this.width = width;
    }

    public static GetDimNode newWidth(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new GetDimNode(position, scope, args, true);
    }

    public static GetDimNode newHeight(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new GetDimNode(position, scope, args, false);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);
        final ProjectState s = project.getState();

        return width ? s.getImageWidth() : s.getImageHeight();
    }

    @Override
    protected String funcName() {
        return width ? WIDTH : HEIGHT;
    }
}
