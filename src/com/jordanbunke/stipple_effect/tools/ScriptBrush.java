package com.jordanbunke.stipple_effect.tools;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.Layout;
import com.jordanbunke.stipple_effect.visual.GraphicsUtils;
import com.jordanbunke.stipple_effect.visual.SECursor;
import com.jordanbunke.stipple_effect.visual.menu_elements.DynamicLabel;
import com.jordanbunke.stipple_effect.visual.menu_elements.TextLabel;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.StaticTextButton;

import java.awt.*;
import java.util.function.Function;

public final class ScriptBrush extends AbstractBrush
        implements SnappableTool, ToggleModeTool {
    private static final ScriptBrush INSTANCE;

    private Function<Color, Color> c;
    private boolean valid, ignoreTransparent, fromSystem, secondary;

    static {
        INSTANCE = new ScriptBrush();
    }

    private ScriptBrush() {
        c = null;
        valid = false;
        ignoreTransparent = false;
        fromSystem = false;
        secondary = false;
    }

    public static ScriptBrush get() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "Script Brush";
    }

    @Override
    public String getCursorCode() {
        return valid ? super.getCursorCode() : SECursor.NO_SCRIPT;
    }

    @Override
    public void onMouseDown(final SEContext context, final GameMouseEvent me) {
        if (valid)
            super.onMouseDown(context, me);
    }

    public void updateScript(final boolean valid, final SEScript script) {
        c = valid ? c -> (Color) SEInterpreter.get()
                .run(script.head(), script.path(), c) : null;
        this.valid = valid;
    }

    @Override
    void setColorGetter(final SEContext context, final GameMouseEvent me) {
        secondary = me.button == GameMouseEvent.Button.RIGHT;
    }

    @Override
    boolean paintCondition(final Color existing, final int x, final int y) {
        return !ignoreTransparent || existing.getAlpha() > 0;
    }

    @Override
    Color getColor(final Color existing, final int x, final int y) {
        return c.apply(fromSystem ? (secondary
                ? StippleEffect.get().getSecondary()
                : StippleEffect.get().getPrimary())
                : existing);
    }

    @Override
    public MenuElementGrouping buildToolOptionsBar() {
        final MenuElementGrouping inherited = super.buildToolOptionsBar();

        // script label
        final TextLabel scriptLabel = TextLabel.make(
                new Coord2D(getAfterBreadthTextX(), Layout.optionsBarTextY()),
                "Color script");

        // upload script button
        final StaticTextButton scriptButton =
                GraphicsUtils.makeStandardTextButton("Upload",
                        new Coord2D(Layout.optionsBarNextElementX(
                                scriptLabel, false),
                                Layout.getToolOptionsBarPosition().y +
                                        ((Layout.TOOL_OPTIONS_BAR_H -
                                                Layout.STD_TEXT_BUTTON_H) / 2)),
                        StippleEffect.get()::openColorScript);

        // script feedback label
        final DynamicLabel scriptFeedback = DynamicLabel.make(new Coord2D(
                Layout.optionsBarNextElementX(scriptButton, false),
                Layout.optionsBarTextY()), DialogVals::colorScriptMessage,
                Layout.getToolOptionsBarWidth());

        return new MenuElementGrouping(inherited,
                scriptLabel, scriptButton, scriptFeedback);
    }

    @Override
    public void setSnap(final boolean is) {
        fromSystem = is;
    }

    @Override
    public boolean isSnap() {
        return fromSystem;
    }

    @Override
    public void setMode(final boolean is) {
        ignoreTransparent = is;
    }
}
