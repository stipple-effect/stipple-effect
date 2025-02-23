package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ProjectTypeNode;

public final class GetProjectNoArgsNode extends GlobalExpressionNode {
    public static final String NAME = "get_project";

    public GetProjectNoArgsNode(
            final TextPosition position,
            final ExpressionNode[] args
    ) {
        super(position, ProjectTypeNode.get(), args);
    }

    @Override
    public SEContext evaluate(final SymbolTable symbolTable) {
        return StippleEffect.get().getContext();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
