package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class PadNode extends ProjectStatementNode {
    public static final String NAME = "pad";

    public PadNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args,
                TypeNode.getInt(), TypeNode.getInt(),
                TypeNode.getInt(), TypeNode.getInt());
    }

    @Override
    public FuncControlFlow execute(SymbolTable symbolTable) {
        final Object[] dims = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int l = (int) dims[0], r = (int) dims[1],
                t = (int) dims[2], b = (int) dims[3];

        final int w = project.getState().getImageWidth(),
                h = project.getState().getImageHeight(),
                pw = l + w + r, ph = t + h + b;

        if (pw <= 0 || pw > Constants.MAX_CANVAS_W ||
                ph <= 0 || ph > Constants.MAX_CANVAS_H)
            StatusUpdates.scriptActionNotPermitted("pad the canvas dimensions",
                    "the prospective dimensions are invalid: width = " +
                            pw + ", height = " + ph, getPosition());
        else
            project.pad(l, r, t, b);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
