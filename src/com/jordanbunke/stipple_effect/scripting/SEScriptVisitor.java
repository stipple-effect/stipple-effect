package com.jordanbunke.stipple_effect.scripting;

import com.jordanbunke.delta_time.scripting.ScriptParser;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.*;
import com.jordanbunke.delta_time.scripting.ast.nodes.statement.*;
import com.jordanbunke.delta_time.scripting.util.ScriptVisitor;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeCompatibility;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.delegators.GraphicsNodeDelegator;
import com.jordanbunke.stipple_effect.scripting.delegators.MathNodeDelegator;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.SEExtTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;
import com.jordanbunke.stipple_effect.scripting.delegators.SENodeDelegator;
import com.jordanbunke.stipple_effect.scripting.util.Light;
import com.jordanbunke.stipple_effect.utility.Constants;

import java.util.Set;

public final class SEScriptVisitor extends ScriptVisitor {
    private static final String SCOPE_SEP = ".";

    static {
        final Set<Class<?>> extensionTypeObjects = Set.of(
                SEContext.class, LayerRep.class, SEScript.class,
                Palette.class, SaveConfig.class, Light.class);

        extensionTypeObjects.forEach(TypeCompatibility::addBaseType);
    }

    @Override
    public SEExtTypeNode visitExtensionType(
            final ScriptParser.ExtensionTypeContext ctx
    ) {
        return SENodeDelegator.type(
                TextPosition.fromToken(ctx.start), ctx.ident().getText());
    }

    @Override
    public ExpressionNode visitExtPropertyExpression(
            final ScriptParser.ExtPropertyExpressionContext ctx
    ) {
        final String namespace = ctx.namespace().ident().getText(),
                constID = ctx.namespace().subident().getText()
                        .substring(SCOPE_SEP.length());

        final TextPosition position = TextPosition.fromToken(ctx.start);

        return switch (namespace) {
            case Constants.SCRIPT_GLOBAL_NAMESPACE ->
                    SENodeDelegator.globalConstant(position, constID);
            case Constants.GRAPHICS_NAMESPACE ->
                    GraphicsNodeDelegator.constant(position, constID);
            case Constants.MATH_NAMESPACE ->
                    MathNodeDelegator.constant(position, constID);
            default -> new IllegalExpressionNode(position,
                    "\"" + namespace + "\" is an illegal namespace");
        };
    }

    @Override
    public ExpressionNode visitExtFuncCallExpression(
            final ScriptParser.ExtFuncCallExpressionContext ctx
    ) {
        final String namespace = ctx.namespace().ident().getText(),
                fID = ctx.namespace().subident().getText()
                        .substring(SCOPE_SEP.length());

        final TextPosition position = TextPosition.fromToken(ctx.start);

        final ExpressionNode[] args = unpackElements(ctx.args().elements());

        return switch (namespace) {
            case Constants.SCRIPT_GLOBAL_NAMESPACE ->
                SENodeDelegator.globalFunctionExpression(position, fID, args);
            case Constants.GRAPHICS_NAMESPACE ->
                GraphicsNodeDelegator.expression(position, fID, args);
            case Constants.MATH_NAMESPACE ->
                MathNodeDelegator.expression(position, fID, args);
            default -> new IllegalExpressionNode(position, "Namespace \"" +
                    namespace + "\" does not exist or defines no value-returning functions");
        };
    }

    @Override
    public StatementNode visitExtFuncCallStatement(
            final ScriptParser.ExtFuncCallStatementContext ctx
    ) {
        final String namespace = ctx.namespace().ident().getText(),
                fID = ctx.namespace().subident().getText()
                        .substring(SCOPE_SEP.length());

        final TextPosition position = TextPosition.fromToken(ctx.start);

        final ExpressionNode[] args = unpackElements(ctx.args().elements());

        if (namespace.equals(Constants.SCRIPT_GLOBAL_NAMESPACE))
            return SENodeDelegator.globalFunctionStatement(position, fID, args);

        // extend here if other namespaces implement void functions

        return new IllegalStatementNode(position, "Namespace \"" +
                namespace + "\" does not exist or defines no void functions");
    }

    @Override
    public ExpressionNode determineExtPropertyExpression(
            final TextPosition position, final ExpressionNode scope,
            final String propertyID
    ) {
        return SENodeDelegator.scopedProperty(position, scope, propertyID);
    }

    @Override
    public ExpressionNode determineExtScopedFunctionExpression(
            final TextPosition position, final ExpressionNode scope,
            final String functionID, final ExpressionNode... args
    ) {
        return SENodeDelegator.scopedFunctionExpression(
                position, scope, functionID, args);
    }

    @Override
    public StatementNode determineExtScopedFunctionStatement(
            final TextPosition position, final ExpressionNode scope,
            final String functionID, final ExpressionNode... args
    ) {
        return SENodeDelegator.scopedFunctionStatement(
                position, scope, functionID, args);
    }
}
