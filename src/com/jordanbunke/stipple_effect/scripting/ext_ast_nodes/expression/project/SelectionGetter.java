package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

import java.util.HashSet;
import java.util.Set;

public final class SelectionGetter extends ProjectExpressionNode {
    public static final String GET = "get_selection", HAS = "has_selection";

    private final boolean get;

    private SelectionGetter(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean get
    ) {
        super(position, scope, getReturnType(get), args);

        this.get = get;
    }

    public static SelectionGetter newGet(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new SelectionGetter(position, scope, args, true);
    }

    public static SelectionGetter newHas(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new SelectionGetter(position, scope, args, false);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        final Set<ScriptArray> pixels = new HashSet<>();
        project.getState().getSelection().unboundedPixelAlgorithm(
                (x, y) -> pixels.add(ScriptArray.of(x, y)));

        return get ? new ScriptSet(pixels.stream().map(c -> c))
                : project.getState().hasSelection();
    }

    public static TypeNode getReturnType(final boolean get) {
        return get ? TypeNode.setOf(TypeNode.arrayOf(TypeNode.getInt()))
                : TypeNode.getBool();
    }

    @Override
    protected String funcName() {
        return get ? GET : HAS;
    }
}
