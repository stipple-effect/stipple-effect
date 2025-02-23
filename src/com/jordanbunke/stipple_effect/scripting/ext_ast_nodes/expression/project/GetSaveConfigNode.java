package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.SaveConfigTypeNode;

public final class GetSaveConfigNode extends ProjectExpressionNode {
    public static final String NAME = "get_save_config";

    public GetSaveConfigNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, SaveConfigTypeNode.get(), args);
    }

    @Override
    public SaveConfig evaluate(final SymbolTable symbolTable) {
        return getProject(symbolTable).getSaveConfig();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
