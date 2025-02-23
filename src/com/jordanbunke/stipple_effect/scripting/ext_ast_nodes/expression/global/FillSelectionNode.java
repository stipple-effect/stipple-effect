package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.selection.Selection;

import java.awt.*;

public class FillSelectionNode extends GlobalExpressionNode {
    public static final String NAME = "fill_selection";

    private final boolean systemSelection;

    private FillSelectionNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean systemSelection, final TypeNode... expectedTypes
    ) {
        super(position, TypeNode.getImage(), args, expectedTypes);

        this.systemSelection = systemSelection;
    }

    public static FillSelectionNode system(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new FillSelectionNode(position, args, true,
                TypeNode.getImage(), TypeNode.getColor());
    }

    public static FillSelectionNode custom(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new FillSelectionNode(position, args, false,
                TypeNode.getImage(), TypeNode.getColor(),
                TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt())));
    }

    @Override
    public GameImage evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final GameImage img = (GameImage) vs[0],
                res = new GameImage(img.getWidth(), img.getHeight());
        final Color c = (Color) vs[1];
        final Selection selection = systemSelection
                ? StippleEffect.get().getContext().getState().getSelection()
                : ScriptUtils.convertSelection(
                        (ScriptSet) vs[2], img.getWidth(), img.getHeight());

        selection.unboundedPixelAlgorithm(
                (x, y) -> res.setRGB(x, y, c.getRGB()));

        return res;
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
