package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.selection.Selection;

import java.util.HashSet;
import java.util.Set;

public final class WandNode extends SearchNode {
    public static final String NAME = "wand";

    public WandNode(
            final TextPosition position, ExpressionNode[] args
    ) {
        super(position, TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())),
                args, TypeNode.getImage(), TypeNode.getInt(),
                TypeNode.getInt(), TypeNode.getFloat(),
                TypeNode.getBool(), TypeNode.getBool());
    }

    @Override
    public ScriptSet evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final GameImage img = (GameImage) vs[0];
        final int x = (int) vs[1], y = (int) vs[2];
        final double tol = (double) vs[3];
        final boolean global = (boolean) vs[4], diag = (boolean) vs[5];

        final Selection selection = search(img, x, y, tol, global, diag);

        final Set<ScriptArray> pixels = new HashSet<>();
        selection.unboundedPixelAlgorithm(
                (px, py) -> pixels.add(ScriptArray.of(px, py)));

        return new ScriptSet(pixels.stream().map(c -> c));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
