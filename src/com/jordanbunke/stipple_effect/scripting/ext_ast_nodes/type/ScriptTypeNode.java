package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type;

import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;

public final class ScriptTypeNode extends SEExtTypeNode {
    public static final String NAME = "script";
    private static final ScriptTypeNode INSTANCE;

    public ScriptTypeNode(final TextPosition position) {
        super(position);
    }

    private ScriptTypeNode() {
        this(TextPosition.N_A);
    }

    static {
        INSTANCE = new ScriptTypeNode();
    }

    public static ScriptTypeNode get() {
        return INSTANCE;
    }

    @Override
    public boolean complies(final Object o) {
        return o instanceof SEScript;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
