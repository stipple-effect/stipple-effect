package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

import java.util.Arrays;

public final class NewProjectStatementNode extends GlobalStatementNode {
    public static final String NAME = "new_project";

    public NewProjectStatementNode(
            final TextPosition position,
            final ExpressionNode[] args
    ) {
        super(position, args, TypeNode.getInt(), TypeNode.getInt());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Integer[] dims = Arrays.stream(arguments.args())
                .map(a -> (Integer) a.evaluate(symbolTable))
                .toArray(Integer[]::new);
        final int w = dims[0], h = dims[1];

        if (w <= 0 || w > Constants.MAX_CANVAS_W ||
                h <= 0 || h > Constants.MAX_CANVAS_H)
            StatusUpdates.scriptActionNotPermitted("create a new project",
                    "the supplied dimensions are invalid: width = " +
                            w + ", height = " + h, getPosition());
        else
            StippleEffect.get().addContext(new SEContext(w, h), true);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
