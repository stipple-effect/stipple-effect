package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.NumTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.ShiftOrScale;
import com.jordanbunke.stipple_effect.state.Operation;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class HSVShiftNode extends ProjectStatementNode {
    public static final String NAME = "hsv_shift";

    public HSVShiftNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getInt(),
                TypeNode.getBool(), TypeNode.getBool(),
                TypeNode.getInt(), NumTypeNode.get(), NumTypeNode.get());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        final Object[] vs = arguments.evaluate(symbolTable);
        final int scopeIndex = (int) vs[0], hShift = (int) vs[3];
        final boolean includeDisabled = (boolean) vs[1],
                ignoreSelection = (boolean) vs[2];
        final ShiftOrScale sShift = new ShiftOrScale(vs[4]),
                vShift = new ShiftOrScale(vs[5]);

        final String attempt = "apply an HSV level shift";

        if (scopeIndex < 0 || scopeIndex >= DialogVals.Scope.values().length)
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the scope (" + scopeIndex +
                            ") is not a valid index for this enumeration",
                    arguments.get(0).getPosition());
        else {
            if (hShift < Constants.MIN_HUE_SHIFT ||
                    hShift > Constants.MAX_HUE_SHIFT)
                StatusUpdates.scriptActionNotPermitted(attempt,
                        "the hue shift (" +
                                hShift + ") is out of bounds (" +
                                Constants.MIN_HUE_SHIFT + "<= h_shift <= " +
                                Constants.MAX_HUE_SHIFT + ")",
                        arguments.get(3).getPosition());
            else if (sShift.outOfBounds())
                sShift.oobNotification("saturation", attempt,
                        arguments.get(4).getPosition());
            else if (vShift.outOfBounds())
                vShift.oobNotification("value", attempt,
                        arguments.get(5).getPosition());
            else {
                final DialogVals.Scope scope =
                        DialogVals.Scope.values()[scopeIndex];

                final DialogVals.Scope scopeWas = DialogVals.getScope();
                final boolean includeWas = DialogVals.isIncludeDisabledLayers(),
                        ignoreWas = DialogVals.isIgnoreSelection();

                DialogVals.setScope(scope);
                DialogVals.setIncludeDisabledLayers(includeDisabled);
                DialogVals.setIgnoreSelection(ignoreSelection);

                DialogVals.setHueShift(hShift);

                if (sShift.isShifting != DialogVals.isShiftingSat())
                    DialogVals.toggleShiftingSat();

                if (sShift.isShifting)
                    DialogVals.setSatShift(sShift.shift);
                else
                    DialogVals.setSatScale(sShift.scale);

                if (vShift.isShifting != DialogVals.isShiftingValue())
                    DialogVals.toggleShiftingValue();

                if (vShift.isShifting)
                    DialogVals.setValueShift(vShift.shift);
                else
                    DialogVals.setValueScale(vShift.scale);

                final ProjectState res = project.prepHSVShift();
                project.stateManager.performAction(res, Operation.EDIT_IMAGE);

                // reset to dialog values
                DialogVals.setScope(scopeWas);
                DialogVals.setIncludeDisabledLayers(includeWas);
                DialogVals.setIgnoreSelection(ignoreWas);
            }
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
