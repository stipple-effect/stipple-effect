package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.SaveConfigTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.visual.menu_elements.dialog.Textbox;

import java.nio.file.Path;

import static com.jordanbunke.stipple_effect.project.SaveConfig.SaveType;

public final class NewSaveConfigNode extends GlobalExpressionNode {
    public static final String NAME = "new_save_config";

    public NewSaveConfigNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, SaveConfigTypeNode.get(), args,
                TypeNode.arrayOf(TypeNode.getString()),
                TypeNode.getString(), TypeNode.getInt());
    }

    @Override
    public SaveConfig evaluate(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);

        final ScriptArray folder = (ScriptArray) vs[0];
        final String name = (String) vs[1];
        final int saveTypeIndex = (int) vs[2];
        final Path fp = ScriptUtils.scriptFolderToPath(folder);

        if (folder.size() <= 0) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(0).getPosition(),
                    "the folder path supplied is empty");
        } else if (!fp.toFile().isDirectory()) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(0).getPosition(),
                    "\"" + fp + "\" is not a directory");
        } else if (!Textbox.validateAsFileName(name)) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(1).getPosition(),
                    "\"" + name + "\" is not a valid filename");
        } else if (saveTypeIndex < 0 || saveTypeIndex >= SaveType.values().length) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(2).getPosition(),
                    saveTypeIndex + " is not a valid save type index (0 - " +
                            (SaveType.values().length - 1) + ")");
        } else
            return new SaveConfig(fp, name, SaveType.values()[saveTypeIndex]);

        return null;
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
