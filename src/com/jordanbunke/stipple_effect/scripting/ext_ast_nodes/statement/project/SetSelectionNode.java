package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.selection.Selection;

public final class SetSelectionNode extends ProjectStatementNode {
    public static final String NAME = "set_selection";

    public SetSelectionNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args,
                TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())));
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int w = project.getState().getImageWidth(),
                h = project.getState().getImageHeight();
        final Selection selection = ScriptUtils
                .convertSelection((ScriptSet) vs[0], w, h);

        project.deselect(false);
        project.editSelection(selection, true);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
