package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public abstract class CondProjectOpNode extends ProjectStatementNode {
    public CondProjectOpNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    public final FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        if (condition(project))
            operation(project);
        else
            StatusUpdates.scriptActionNotPermitted(attempt(),
                    failReason(project), receiver.receiver().getPosition());

        return FuncControlFlow.cont();
    }

    protected abstract boolean condition(final SEContext project);

    protected abstract String attempt();

    protected abstract String failReason(final SEContext project);

    protected abstract void operation(final SEContext project);
}
