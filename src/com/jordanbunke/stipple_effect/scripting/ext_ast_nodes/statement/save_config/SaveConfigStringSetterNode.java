package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;
import com.jordanbunke.stipple_effect.visual.menu_elements.dialog.Textbox;

import java.util.function.Consumer;

public final class SaveConfigStringSetterNode extends SaveConfigStatementNode {
    public static final String NAME = "set_filename",
            PREFIX = "set_prefix", SUFFIX = "set_suffix";

    private final String property;

    private SaveConfigStringSetterNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final String property
    ) {
        super(position, scope, args, TypeNode.getString());

        this.property = property;
    }

    public static SaveConfigStringSetterNode name(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigStringSetterNode(position, scope, args, NAME);
    }

    public static SaveConfigStringSetterNode prefix(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigStringSetterNode(position, scope, args, PREFIX);
    }

    public static SaveConfigStringSetterNode suffix(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigStringSetterNode(position, scope, args, SUFFIX);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SaveConfig sc = getSaveConfig(symbolTable);
        final String toSet = (String) arguments.evaluate(symbolTable)[0];

        final boolean valid = property.equals(NAME)
                ? validateName(toSet) : validateAffix(toSet);

        if (valid) {
            final Consumer<String> f = switch (property) {
                case PREFIX -> sc::setIndexPrefix;
                case SUFFIX -> sc::setIndexSuffix;
                default -> sc::setName;
            };

            f.accept(toSet);
        } else
            StatusUpdates.scriptActionNotPermitted(
                    property.replace("_", " the ") +
                            " of the save configuration",
                    "\"" + toSet + "\" is not a valid " +
                            property.replace("set_", ""),
                    arguments.get(0).getPosition());

        return FuncControlFlow.cont();
    }

    private static boolean validateAffix(final String affix) {
        return Textbox.validateAsOptionallyEmptyFilename(affix) &&
                affix.length() <= 5;
    }

    private static boolean validateName(final String name) {
        return Textbox.validateAsFileName(name);
    }

    @Override
    protected String funcName() {
        return property;
    }
}
