package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public final class MoveFrameNode extends CondProjectOpNode {
    public static final String BACK = "move_frame_back",
            FORWARD = "move_frame_forward";

    private final boolean back;

    private MoveFrameNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean back
    ) {
        super(position, scope, args);

        this.back = back;
    }

    public static MoveFrameNode back(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new MoveFrameNode(position, scope, args, true);
    }

    public static MoveFrameNode forward(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new MoveFrameNode(position, scope, args, false);
    }

    @Override
    protected boolean condition(final SEContext project) {
        return back ? project.getState().canMoveFrameBack()
                : project.getState().canMoveFrameForward();
    }

    @Override
    protected String attempt() {
        return "move the selected frame " + (back ? "back" : "forward");
    }

    @Override
    protected String failReason(final SEContext project) {
        return "the selected frame is already the " +
                (back ? "first" : "last") + " frame in the project";
    }

    @Override
    protected void operation(final SEContext project) {
        if (back)
            project.moveFrameBack();
        else
            project.moveFrameForward();
    }

    @Override
    protected String funcName() {
        return back ? BACK : FORWARD;
    }
}
