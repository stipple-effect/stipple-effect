package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.SaveConfigTypeNode;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SaveAsNode extends ProjectStatementNode {
    public static final String NAME = "save_as";

    public SaveAsNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, SaveConfigTypeNode.get());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);
        final SaveConfig sc = (SaveConfig) arguments.evaluate(symbolTable)[0];

        project.setSaveConfig(sc);

        if (sc.hasSaveAssociation()) {
            project.setSaveConfig(sc);
            sc.save(project);
        } else
            StatusUpdates.scriptActionNotPermitted(
                    "save the project with this save configuration",
                    "the folder and name configuration is invalid or incomplete",
                    arguments.get(0).getPosition());

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
