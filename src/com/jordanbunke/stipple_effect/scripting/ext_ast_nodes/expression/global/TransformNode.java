package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ProjectTypeNode;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ScriptTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;

import java.util.stream.IntStream;

public final class TransformNode extends GlobalExpressionNode {
    public static final String NAME = "transform";

    private final boolean shortened;

    public TransformNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean shortened, final TypeNode... expectedTypes
    ) {
        super(position, ProjectTypeNode.get(), args, expectedTypes);

        this.shortened = shortened;
    }

    public static TransformNode shortened(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new TransformNode(position, args, true,
                ProjectTypeNode.get(), ScriptTypeNode.get());
    }

    public static TransformNode reg(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new TransformNode(position, args, false,
                ProjectTypeNode.get(), ScriptTypeNode.get(),
                TypeNode.getBool(), TypeNode.getBool());
    }

    @Override
    public SEContext evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);

        final SEContext c = (SEContext) vs[0];
        final SEScript script = (SEScript) vs[1];
        final boolean openInSE = shortened || (boolean) vs[2],
                runPerLayer = !shortened && (boolean) vs[3];

        if (script == null) {
            ScriptErrorLog.fireError(
                    ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(1).getPosition(),
                    "Could not compile the script to perform the transformation");
        } else if (SEInterpreter.validatePreviewScript(script.head(), c)) {
            final SEContext result = runPerLayer
                    ? ScriptUtils.transformProjectPerLayer(c, script.head(), script.path())
                    : runFlattened(c, script);

            if (openInSE && result != null)
                StippleEffect.get().addContext(result, true);

            return result;
        } else
            ScriptErrorLog.fireError(
                    ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(0).getPosition(),
                    "The script was not validated for this project");

        return null;
    }

    private SEContext runFlattened(
            final SEContext c, final SEScript script
    ) {
        final int srcFC = c.getState().getFrameCount();
        final GameImage[] input =IntStream.range(0, srcFC)
                .mapToObj(i -> c.getState().draw(false, false, i))
                .toArray(GameImage[]::new);

        return ScriptUtils.transform(input, script.head(), script.path());
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
