package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.selection.Outliner;
import com.jordanbunke.stipple_effect.selection.Selection;

import java.util.HashSet;
import java.util.Set;

public final class OutlineNode extends GlobalExpressionNode {
    public static final String NAME = "outline";

    public OutlineNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())),
                args, TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())),
                TypeNode.arrayOf(TypeNode.getInt()));
    }

    @Override
    public ScriptSet evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final Selection selection =
                ScriptUtils.convertSelection((ScriptSet) vs[0]);
        final int[] sideMask = ((ScriptArray) vs[1]).stream()
                .mapToInt(s -> (int) s).toArray();

        if (ScriptUtils.invalidSideMask(sideMask, arguments.get(1)))
            return null;

        final Selection outlined = Outliner.outline(selection, sideMask);
        final Set<ScriptArray> pixels = new HashSet<>();
        outlined.unboundedPixelAlgorithm(
                (x, y) -> pixels.add(ScriptArray.of(x, y)));

        return new ScriptSet(pixels.stream().map(c -> c));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
