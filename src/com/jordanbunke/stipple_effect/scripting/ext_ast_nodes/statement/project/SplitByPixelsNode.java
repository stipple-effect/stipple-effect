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

public final class SplitByPixelsNode extends SplitNode {
    public static final String NAME = "split_px";

    public SplitByPixelsNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    public FuncControlFlow execute(SymbolTable symbolTable) {
        final Object[] dims = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int fw = (int) dims[0], fh = (int) dims[1];
        final boolean horizontal = (boolean) dims[2],
                truncateX = (boolean) dims[3],
                truncateY = (boolean) dims[4];

        final int w = project.getState().getImageWidth(),
                h = project.getState().getImageHeight(),
                colRem = w % fw, rowRem = h % fh,
                frames = ((w / fw) + (truncateX || colRem == 0 ? 0 : 1)) *
                        ((h / fh) + (truncateY || rowRem == 0 ? 0 : 1));

        if (fw <= 0 || fw > w)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "the single frame width (" + fw + " px) is invalid... " +
                            "should be 0 < fw <= " + w,
                    arguments.get(0).getPosition());
        else if (fh <= 0 || fh > h)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "the single frame height (" + fh + " px) is invalid... " +
                            "should be 0 < fh <= " + h,
                    arguments.get(1).getPosition());
        else if (frames > Constants.MAX_NUM_FRAMES)
            StatusUpdates.scriptActionNotPermitted(
                    "split the project into frames",
                    "it would produce " + frames + " frames; " +
                            Constants.MAX_NUM_FRAMES +
                            " is the maximum allowed", getPosition());
        else {
            DialogVals.setFrameWidth(fw, h);
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
