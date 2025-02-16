package com.jordanbunke.stipple_effect.visual.menu_elements.colors;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.utility.Layout;
import com.jordanbunke.stipple_effect.utility.Permissions;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;
import com.jordanbunke.stipple_effect.visual.GraphicsUtils;
import com.jordanbunke.stipple_effect.visual.SECursor;

import java.awt.*;
import java.util.List;

public abstract class TwoDimSampler extends MenuElement {
    protected static final int BORDER_PX = Layout.BUTTON_BORDER_PX,
            BUFFER_PX = BORDER_PX * 2;

    private Color lastC;
    private double lastHue, lastSat, lastValue;
    private boolean interacting;

    public TwoDimSampler(
            final Coord2D position, final Bounds2D dimensions
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);

        interacting = false;
    }

    abstract GameImage drawBackground();

    abstract void updateAssets(final Color c);

    abstract int getEffectiveWidth();

    abstract int getEffectiveHeight();

    abstract Color getPixelColor(final Color c, final Coord2D pixel);

    abstract Coord2D getNodePos(final Color c);

    protected void drawNode(final GameImage img, final int x, final int y) {
        final GameImage node = GraphicsUtils.COLOR_NODE;
        final int halfNode = node.getWidth() / 2;
        img.draw(node, x - halfNode, y - halfNode);
    }

    @Override
    public final void update(final double deltaTime) {
        final Color c = StippleEffect.get().getSelectedColor();
        final double hue = ColorMath.fetchHue(c),
                sat = ColorMath.fetchSat(c),
                value = ColorMath.fetchValue(c);

        if (!(c.equals(lastC) && lastHue == hue &&
                lastSat == sat && lastValue == value))
            updateAssets(c);

        lastC = c;
        lastHue = hue;
        lastSat = sat;
        lastValue = value;
    }

    @Override
    public final void process(final InputEventLogger eventLogger) {
        final Coord2D mp = eventLogger.getAdjustedMousePosition();
        final boolean mouseInBounds = mouseIsWithinBounds(mp),
                cursorFree = Permissions.isCursorFree();
        final Coord2D localMP = mp.displace(getRenderPosition().scale(-1));

        if (interacting) {
            final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
            for (GameEvent e : unprocessed) {
                if (e instanceof GameMouseEvent me &&
                        me.matchesAction(GameMouseEvent.Action.UP)) {
                    interacting = false;
                    me.markAsProcessed();
                    break;
                }
            }
        }

        if (mouseInBounds && cursorFree) {
            // check for mouse down and for click
            final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
            for (GameEvent e : unprocessed) {
                if (e instanceof GameMouseEvent me &&
                        !me.matchesAction(GameMouseEvent.Action.UP)) {
                    switch (me.action) {
                        case DOWN -> interacting = true;
                        case CLICK -> {
                            updateColor(localMP);
                            interacting = false;
                        }
                    }

                    me.markAsProcessed();
                    break;
                }
            }
        }

        // adjustment logic
        if (interacting)
            updateColor(localMP);

        // cursor
        if (mouseInBounds && cursorFree)
            SECursor.setCursorCode(interacting
                    ? SECursor.NONE : SECursor.RETICLE);
    }

    @Override
    public final void debugRender(
            final GameImage canvas, final GameDebugger debugger
    ) {

    }

    abstract void updateColor(final Coord2D localMP);
}
