package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

import static com.jordanbunke.stipple_effect.utility.DialogVals.SequenceOrder.*;

public final class SplitByDimsNode extends SplitNode {
    public static final String NAME = "split";

    public SplitByDimsNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    public FuncControlFlow execute(SymbolTable symbolTable) {
        final Object[] dims = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int cols = (int) dims[0], rows = (int) dims[1];
        final boolean horizontal = (boolean) dims[2],
                truncateX = (boolean) dims[3],
                truncateY = (boolean) dims[4];

        final int w = project.getState().getImageWidth(),
                h = project.getState().getImageHeight(),
                fw = w / cols, fh = h / rows,
                colRem = w % cols, rowRem = h % rows,
                frames = (cols + (truncateX || colRem == 0 ? 0 : 1)) *
                        (rows + (truncateY || rowRem == 0 ? 0 : 1));

        if (cols <= 0 || cols > w)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "the number of columns (" + cols + ") is invalid... " +
                            "should be 0 < c <= " + w,
                    arguments.get(0).getPosition());
        else if (rows <= 0 || rows > h)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "the number of rows (" + rows + ") is invalid... " +
                            "should be 0 < r <= " + h,
                    arguments.get(1).getPosition());
        else if (frames > Constants.MAX_NUM_FRAMES)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "it would produce " + frames + " frames; " +
                            Constants.MAX_NUM_FRAMES +
                            " is the maximum allowed", getPosition());
        else {
            DialogVals.setFrameWidth(fw, w);
            DialogVals.setFrameHeight(fh, h);
            DialogVals.setSequenceOrder(horizontal ? HORIZONTAL : VERTICAL);
            DialogVals.setTruncateSplitX(truncateX);
            DialogVals.setTruncateSplitY(truncateY);

            project.split();
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
