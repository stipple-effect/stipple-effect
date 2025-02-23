package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.selection.Selection;
import com.jordanbunke.stipple_effect.tools.ToolThatSearches;

public abstract class SearchNode extends GlobalExpressionNode {
    protected SearchNode(
            final TextPosition position, final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedTypes
    ) {
        super(position, returnType, args, expectedTypes);
    }

    protected final Selection search(
            final GameImage image, final int x, final int y,
            final double tol, final boolean global, final boolean diag
    ) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
            return Selection.EMPTY;

        final double tolBef = ToolThatSearches.getTolerance();
        final boolean globalBef = ToolThatSearches.isGlobal(),
                diagBef = ToolThatSearches.isSearchDiag();

        ToolThatSearches.setGlobal(global);
        ToolThatSearches.setSearchDiag(diag);
        ToolThatSearches.setTolerance(tol);

        final Selection res = ToolThatSearches.search(image, new Coord2D(x, y));

        ToolThatSearches.setGlobal(globalBef);
        ToolThatSearches.setSearchDiag(diagBef);
        ToolThatSearches.setTolerance(tolBef);

        return res;
    }
}
