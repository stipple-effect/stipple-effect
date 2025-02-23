package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ProjectTypeNode;
import com.jordanbunke.stipple_effect.utility.Constants;

public final class NewProjectNode extends GlobalExpressionNode {
    public static final String NAME = "new_project";

    private final boolean twoArg;

    private NewProjectNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean twoArg, final TypeNode... expectedTypes
    ) {
        super(position, ProjectTypeNode.get(), args, expectedTypes);

        this.twoArg = twoArg;
    }

    public static NewProjectNode twoArg(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new NewProjectNode(position, args, true,
                TypeNode.getInt(), TypeNode.getInt());
    }

    public static NewProjectNode threeArg(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new NewProjectNode(position, args, false,
                TypeNode.getInt(), TypeNode.getInt(), TypeNode.getBool());
    }

    @Override
    public SEContext evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final int w = (int) vs[0], h = (int) vs[1];
        final boolean openInSE = twoArg || (boolean) vs[2];

        if (w <= 0 || w > Constants.MAX_CANVAS_W) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    getPosition(), "Width argument " + w +
                            " is out of bounds: 0 < w <= " +
                            Constants.MAX_CANVAS_W);
            return null;
        } else if (h <= 0 || h > Constants.MAX_CANVAS_H) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    getPosition(), "Height argument " + h +
                            " is out of bounds: 0 < h <= " +
                            Constants.MAX_CANVAS_H);
            return null;
        } else {
            final SEContext c = new SEContext(w, h);

            if (openInSE)
                StippleEffect.get().addContext(c, true);

            return c;
        }
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
