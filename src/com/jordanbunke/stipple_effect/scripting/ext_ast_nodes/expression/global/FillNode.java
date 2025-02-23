package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.selection.Selection;

import java.awt.*;

public final class FillNode extends SearchNode {
    public static final String NAME = "fill";

    public FillNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.getImage(), args, TypeNode.getImage(),
                TypeNode.getColor(), TypeNode.getInt(), TypeNode.getInt(),
                TypeNode.getFloat(), TypeNode.getBool(), TypeNode.getBool());
    }

    @Override
    public GameImage evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final GameImage img = (GameImage) vs[0];
        final Color color = (Color) vs[1];
        final int x = (int) vs[2], y = (int) vs[3];
        final double tol = (double) vs[4];
        final boolean global = (boolean) vs[5], diag = (boolean) vs[6];

        final Selection selection = search(img, x, y, tol, global, diag);
        final GameImage filled = new GameImage(img);
        final int w = filled.getWidth(), h = filled.getHeight();

        selection.pixelAlgorithm(w, h,
                (px, py) -> filled.setRGB(px, py, color.getRGB()));

        return filled;
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
