package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.palette;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

import java.awt.*;

public final class PaletteColorOpNode extends PaletteStatementNode {
    public static final String
            ADD = "add_color", REMOVE = "remove_color",
            MOVE_LEFT = "move_color_left", MOVE_RIGHT = "move_color_right";

    private final Op operation;

    private enum Op {
        ADD, REMOVE, MOVE_LEFT, MOVE_RIGHT;

        boolean condition(final Palette palette, final Color color) {
            return switch (this) {
                case ADD -> palette.canAdd(color);
                case REMOVE -> palette.canRemove(color);
                case MOVE_LEFT -> palette.canMoveLeft(color);
                case MOVE_RIGHT -> palette.canMoveRight(color);
            };
        }

        String attempt(final Palette palette, final Color color) {
            final String beginning = switch (this) {
                case ADD -> "add";
                case REMOVE -> "remove";
                default -> "move";
            };

            final String middle = switch (this) {
                case MOVE_LEFT -> " to the left in ";
                case MOVE_RIGHT -> " to the right in ";
                default -> " to ";
            };

            return beginning + " the color " +
                    StatusUpdates.processColor(color) + middle +
                    "the palette \"" + palette.getName() + "\"";
        }

        String failReason(final Palette palette, final Color color) {
            final String palText = "\"" + palette.getName() + "\"",
                    colText = StatusUpdates.processColor(color);

            return switch (this) {
                case ADD -> palText + " already contains " + colText;
                case REMOVE -> palText + " does not contain " + colText;
                case MOVE_LEFT -> colText + " is already leftmost in " + palText;
                case MOVE_RIGHT -> colText + " is already rightmost in " + palText;
            };
        }

        void apply(final Palette palette, final Color color) {
            switch (this) {
                case ADD -> palette.addColor(color);
                case REMOVE -> palette.removeColor(color);
                case MOVE_LEFT -> palette.moveLeft(color);
                case MOVE_RIGHT -> palette.moveRight(color);
            }
        }

        @Override
        public String toString() {
            return switch (this) {
                case ADD -> PaletteColorOpNode.ADD;
                case REMOVE -> PaletteColorOpNode.REMOVE;
                case MOVE_LEFT -> PaletteColorOpNode.MOVE_LEFT;
                case MOVE_RIGHT -> PaletteColorOpNode.MOVE_RIGHT;
            };
        }
    }

    private PaletteColorOpNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final Op operation
    ) {
        super(position, scope, args, TypeNode.getColor());

        this.operation = operation;
    }

    public static PaletteColorOpNode add(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorOpNode(position, scope, args, Op.ADD);
    }

    public static PaletteColorOpNode remove(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorOpNode(position, scope, args, Op.REMOVE);
    }

    public static PaletteColorOpNode moveLeft(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorOpNode(position, scope, args, Op.MOVE_LEFT);
    }

    public static PaletteColorOpNode moveRight(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorOpNode(position, scope, args, Op.MOVE_RIGHT);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Palette palette = getPalette(symbolTable);
        final Color color = (Color) arguments.evaluate(symbolTable)[0];

        if (!operation.condition(palette, color))
            StatusUpdates.scriptActionNotPermitted(
                    operation.attempt(palette, color),
                    operation.failReason(palette, color),
                    receiver.receiver().getPosition());
        else
            operation.apply(palette, color);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return operation.toString();
    }
}
