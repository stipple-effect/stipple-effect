package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.selection.Outliner;
import com.jordanbunke.stipple_effect.selection.Selection;
import com.jordanbunke.stipple_effect.utility.Constants;

import java.util.HashSet;
import java.util.Set;

public final class PresetOutlineNode extends GlobalExpressionNode {
    public static final String SINGLE = "single_outline",
            DOUBLE = "double_outline";

    private final boolean single;

    private PresetOutlineNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean single
    ) {
        super(position, TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())),
                args, TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())),
                TypeNode.getInt());

        this.single = single;
    }

    public static PresetOutlineNode sng(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new PresetOutlineNode(position, args, true);
    }

    public static PresetOutlineNode dbl(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new PresetOutlineNode(position, args, false);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final Selection selection =
                ScriptUtils.convertSelection((ScriptSet) vs[0]);

        final int side = (int) vs[1], MAX = Constants.MAX_OUTLINE_PX;

        if (side < -MAX || side > MAX) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(1).getPosition(),
                    "Side mask value " + side + " is out of bounds (" +
                            (-MAX) + " <= px <= " + MAX + ")");
            return null;
        }

        final int[] sideMask = single
                ? Outliner.getSingleOutlineMask(side)
                : Outliner.getDoubleOutlineMask(side);

        final Selection outlined = Outliner.outline(selection, sideMask);
        final Set<ScriptArray> pixels = new HashSet<>();

        outlined.unboundedPixelAlgorithm(
                (x, y) -> pixels.add(ScriptArray.of(x, y)));

        return new ScriptSet(pixels.stream().map(c -> c));
    }

    @Override
    protected String funcName() {
        return single ? SINGLE : DOUBLE;
    }
}
