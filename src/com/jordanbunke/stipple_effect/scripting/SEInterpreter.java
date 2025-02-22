package com.jordanbunke.stipple_effect.scripting;

import com.jordanbunke.delta_time.scripting.Interpreter;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;
import com.jordanbunke.stipple_effect.visual.DialogAssembly;

import java.nio.file.Path;

public final class SEInterpreter extends Interpreter {
    private static boolean printErrorsToDialog = false;

    static {
        SEInterpreter.overrideVisitor(new SEScriptVisitor());
    }

    public static SEInterpreter get() {
        return new SEInterpreter();
    }

    public static void printErrorsToDialog() {
        printErrorsToDialog = true;
    }

    public void runAutomationScript(final String content, final Path filepath) {
        final HeadFuncNode script = build(content);

        if (validateAutomationScript(script))
            run(script, filepath);
        else if (script != null)
            StatusUpdates.invalidAutomationScript(script.toString());
        else
            StatusUpdates.failedToCompileScript(filepath);
    }

    private boolean validateAutomationScript(
            final HeadFuncNode script) {
        if (script == null)
            return false;

        return script.paramsMatch() && script.getReturnType() == null;
    }

    public static boolean validateColorScript(final HeadFuncNode script) {
        if (script == null)
            return false;

        final TypeNode COL_TYPE = TypeNode.getColor();

        return script.paramsMatch(COL_TYPE) &&
                COL_TYPE.equals(script.getReturnType());
    }

    public static boolean validatePreviewScript(
            final HeadFuncNode script, final SEContext context
    ) {
        if (script == null)
            return false;

        final TypeNode IMG_TYPE = TypeNode.getImage(),
                IMG_ARRAY_TYPE = TypeNode.arrayOf(IMG_TYPE),
                returnType = script.getReturnType();

        final boolean imgReturn = IMG_TYPE.equals(returnType),
                arrayReturn = IMG_ARRAY_TYPE.equals(returnType),
                imgParam = script.paramsMatch(IMG_TYPE),
                arrayParam = script.paramsMatch(IMG_ARRAY_TYPE);

        if (!(imgReturn || arrayReturn))
            return false;

        return context.isAnimation()
                ? (imgParam ? imgReturn : arrayParam)
                : imgParam || arrayParam;
    }

    @Override
    protected void displayErrors() {
        if (printErrorsToDialog) {
            DialogAssembly.setDialogToScriptErrors();
            return;
        }

        final String[] errors = ScriptErrorLog.getErrors();

        for (String error : errors)
            System.out.println(error);
    }
}
