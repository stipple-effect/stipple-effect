package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.PaletteTypeNode;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class PaletteActionNode extends ProjectStatementNode {
    public static final String PALETTIZE = "palettize",
            EXTRACT = "extract_to_pal";

    private final boolean palettize;

    private PaletteActionNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean palettize
    ) {
        super(position, scope, args, PaletteTypeNode.get(),
                TypeNode.getInt(), TypeNode.getBool(), TypeNode.getBool());

        this.palettize = palettize;
    }

    public static PaletteActionNode palettize(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteActionNode(position, scope, args, true);
    }

    public static PaletteActionNode extract(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteActionNode(position, scope, args, false);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        final Object[] vs = arguments.evaluate(symbolTable);
        final Palette palette = (Palette) vs[0];
        final int scopeIndex = (int) vs[1];
        final boolean includeDisabled = (boolean) vs[2],
                ignoreSelection = (boolean) vs[3];

        if (scopeIndex < 0 || scopeIndex >= DialogVals.Scope.values().length)
            StatusUpdates.scriptActionNotPermitted(
                    "extract project contents to the palette \"" +
                            palette.getName() + "\"",
                    "the scope (" + scopeIndex +
                            ") is not a valid index for this enumeration",
                    arguments.get(1).getPosition());
        else {
            final DialogVals.Scope scope =
                    DialogVals.Scope.values()[scopeIndex];

            final DialogVals.Scope scopeWas = DialogVals.getScope();
            final boolean includeWas = DialogVals.isIncludeDisabledLayers(),
                    ignoreWas = DialogVals.isIgnoreSelection();

            DialogVals.setScope(scope);
            DialogVals.setIncludeDisabledLayers(includeDisabled);
            DialogVals.setIgnoreSelection(ignoreSelection);

            if (palettize)
                project.palettize(palette);
            else
                project.contentsToPalette(palette);

            // reset to dialog values
            DialogVals.setScope(scopeWas);
            DialogVals.setIncludeDisabledLayers(includeWas);
            DialogVals.setIgnoreSelection(ignoreWas);
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return palettize ? PALETTIZE : EXTRACT;
    }
}
