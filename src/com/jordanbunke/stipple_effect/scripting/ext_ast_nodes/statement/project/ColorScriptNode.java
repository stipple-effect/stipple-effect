package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ScriptTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;
import com.jordanbunke.stipple_effect.state.Operation;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class ColorScriptNode extends ProjectStatementNode {
    public static final String NAME = "color_script";

    public ColorScriptNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getInt(),
                TypeNode.getBool(), TypeNode.getBool(), ScriptTypeNode.get());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        final Object[] vs = arguments.evaluate(symbolTable);
        final int scopeIndex = (int) vs[0];
        final boolean includeDisabled = (boolean) vs[1],
                ignoreSelection = (boolean) vs[2];
        final SEScript colorScript = (SEScript) vs[3];
        final String attempt = "run the color script";

        if (scopeIndex < 0 || scopeIndex >= DialogVals.Scope.values().length)
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the scope (" + scopeIndex +
                            ") is not a valid index for this enumeration",
                    arguments.get(0).getPosition());
        else if (!SEInterpreter.validateColorScript(colorScript.head()))
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the script is not a valid color script",
                    arguments.get(3).getPosition());
        else {
            final DialogVals.Scope scope =
                    DialogVals.Scope.values()[scopeIndex];

            final DialogVals.Scope scopeWas = DialogVals.getScope();
            final boolean includeWas = DialogVals.isIncludeDisabledLayers(),
                    ignoreWas = DialogVals.isIgnoreSelection();

            DialogVals.setScope(scope);
            DialogVals.setIncludeDisabledLayers(includeDisabled);
            DialogVals.setIgnoreSelection(ignoreSelection);

            final ProjectState res = project.prepColorScript(colorScript);
            project.stateManager.performAction(res, Operation.EDIT_IMAGE);

            // reset to dialog values
            DialogVals.setScope(scopeWas);
            DialogVals.setIncludeDisabledLayers(includeWas);
            DialogVals.setIgnoreSelection(ignoreWas);
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
