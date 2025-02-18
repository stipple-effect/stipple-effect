package com.jordanbunke.stipple_effect.preview;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.menu_elements.IconButton;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.Alignment;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.ButtonType;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.TextButton;
import com.jordanbunke.stipple_effect.visual.theme.Theme;

import static com.jordanbunke.stipple_effect.utility.Layout.*;

public final class EmbeddedTopBar extends MenuElement {
    private final EmbeddedPreview preview;

    private boolean moving, highlighted;
    private Coord2D lastMousePos;

    private final IconButton close;

    private GameImage standardImg, highlightImg, movingImg;

    public EmbeddedTopBar(
            final EmbeddedPreview caller
    ) {
        super(new Coord2D(caller.getX(), caller.getY() - EMBED_PREV_TOP_BAR_Y),
                new Bounds2D(caller.getWidth(), EMBED_PREV_TOP_BAR_Y),
                Anchor.LEFT_TOP, true);

        preview = caller;

        close = IconButton.make(ResourceCodes.HIDE_PANEL,
                closePosition(), caller::close);

        moving = false;
        highlighted = false;

        lastMousePos = new Coord2D();

        updateAssets();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        close.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        final boolean inBounds = mouseIsWithinBounds(mousePos);

        if (moving) {
            final Coord2D delta = mousePos.displace(lastMousePos.scale(-1));

            // movement update logic
            if (!delta.equals(new Coord2D())) {
                final Coord2D movedTo = getPosition().displace(delta);

                setPosition(movedTo);
                lastMousePos = mousePos;
            }

            // release logic
            for (GameEvent e : eventLogger.getUnprocessedEvents())
                if (e instanceof GameMouseEvent me &&
                        me.matchesAction(GameMouseEvent.Action.UP)) {
                    me.markAsProcessed();
                    moving = false;
                    break;
                }
        }

        if (inBounds) {
            for (GameEvent e : eventLogger.getUnprocessedEvents())
                if (e instanceof GameMouseEvent me &&
                        me.matchesAction(GameMouseEvent.Action.DOWN)) {
                    me.markAsProcessed();

                    moving = true;
                    lastMousePos = mousePos;

                    break;
                }
        }

        highlighted = !moving && inBounds;
    }

    @Override
    public void update(final double deltaTime) {
        close.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        draw(moving ? movingImg : (highlighted
                ? highlightImg : standardImg), canvas);

        // border
        canvas.drawRectangle(Settings.getTheme().panelDivisions,
                2f, getX(), getY(), getWidth(), getHeight());

        close.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public void setPosition(final Coord2D position) {
        super.setPosition(position);
        boundingCheck();

        positionUpdated();
    }

    @Override
    public void setX(final int x) {
        super.setX(x);
        boundingCheck();

        positionUpdated();
    }

    @Override
    public void setY(final int y) {
        super.setY(y);
        boundingCheck();

        positionUpdated();
    }

    @Override
    public void incrementX(int deltaX) {
        super.setX(getPosition().x + deltaX);
    }

    private void boundingCheck() {
        final int ww = width(), wh = height(), w = getWidth(),
                h = getHeight() + preview.getHeight(), x = getX(), y = getY();

        if (w >= ww || h >= wh)
            return;

        if (x < 0)
            super.setX(0);
        if (x + w > ww)
            super.setX(ww - w);
        // navbar is off-limits
        if (y < COLLAPSED_PROJECTS_H)
            super.setY(COLLAPSED_PROJECTS_H);
        // bottom bar is off-limits
        if (y + h > wh - BOTTOM_BAR_H)
            super.setY((wh - BOTTOM_BAR_H) - h);
    }

    @Override
    public void setWidth(final int width) {
        super.setWidth(width);
        boundingCheck();

        widthUpdated();
    }

    @Override
    public void setDimensions(final Bounds2D dimensions) {
        super.setDimensions(dimensions);
        boundingCheck();

        widthUpdated();
    }

    private void positionUpdated() {
        preview.setPosition(previewPosition());
        close.setPosition(closePosition());
    }

    private void widthUpdated() {
        close.setPosition(closePosition());
        updateAssets();
    }

    private Coord2D closePosition() {
        return getPosition().displace(
                getWidth() - BUTTON_INC, ICON_BUTTON_OFFSET_Y);
    }

    private Coord2D previewPosition() {
        return getPosition().displace(0, EMBED_PREV_TOP_BAR_Y);
    }

    private void updateAssets() {
        final Theme t = Settings.getTheme();
        final TextButton base = TextButton.of(preview.toString(),
                getWidth(), Alignment.LEFT, ButtonType.STANDARD);

        standardImg = t.logic.drawTopBar(base);
        highlightImg = t.logic.drawTopBar(base.sim(false, true));
        movingImg = t.logic.drawTopBar(base.sim(true, false));
    }
}
