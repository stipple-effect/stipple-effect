package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

import static com.jordanbunke.stipple_effect.utility.DialogVals.SequenceOrder.*;

public final class StitchNode extends ProjectStatementNode {
    public static final String NAME = "stitch";

    public StitchNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args,
                TypeNode.getInt(), TypeNode.getBool());
    }

    @Override
    public FuncControlFlow execute(SymbolTable symbolTable) {
        final Object[] dims = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int fpd = (int) dims[0];
        final boolean horizontal = (boolean) dims[1];

        if (fpd <= 0)
            StatusUpdates.scriptActionNotPermitted(
                    "stitch the project's frames together",
                    "the number of frames per " +
                            (horizontal ? "row" : "column") + " was negative",
                    arguments.get(0).getPosition());
        else {
            final int fc = project.getState().getFrameCount(),
                    w = project.getState().getImageWidth(),
                    h = project.getState().getImageHeight(),
                    sw = w * (horizontal ? fpd : (fc / fpd)),
                    sh = h * (horizontal ? (fc / fpd) : fpd);

            if (sw <= 0 || w > Constants.MAX_CANVAS_W ||
                    sh <= 0 || h > Constants.MAX_CANVAS_H)
                StatusUpdates.scriptActionNotPermitted(
                        "stitch the project's frames together",
                        "the prospective dimensions are invalid: width = " +
                                sw + ", height = " + sh, getPosition());
            else {
                DialogVals.setSequenceOrder(horizontal ? HORIZONTAL : VERTICAL);
                DialogVals.setFramesPerDim(fpd);

                project.stitch();
            }
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
