package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SaveNode extends ProjectStatementNode {
    public static final String NAME = "save";

    public SaveNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);
        final SaveConfig sc = project.getSaveConfig();

        if (sc.hasSaveAssociation())
            sc.save(project);
        else
            StatusUpdates.scriptActionNotPermitted("save the project",
                    "the project does not have a valid save association",
                    receiver.receiver().getPosition());

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
