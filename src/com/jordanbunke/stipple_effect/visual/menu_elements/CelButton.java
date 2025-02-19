package com.jordanbunke.stipple_effect.visual.menu_elements;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.DeltaTimeGlobal;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.Layout;
import com.jordanbunke.stipple_effect.utility.Permissions;
import com.jordanbunke.stipple_effect.utility.action.KeyShortcut;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.menu_elements.scrollable.HorizontalScrollBox;
import com.jordanbunke.stipple_effect.visual.menu_elements.scrollable.VerticalScrollBox;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.util.List;

public final class CelButton extends MenuElement {
    private static int framesFrom, layersFrom, framesTo, layersTo;

    private final int layerIndex, frameIndex;
    private final Scrollable frameButton, layerButton;
    private final VerticalScrollBox layers;
    private final HorizontalScrollBox frames;

    private final SEContext c;
    private GameImage base, highlightImg, selectedImg;

    // status
    private Status status;
    private boolean enabled, highlighted, selected, partOfSelection;

    static {
        framesFrom = 0;
        layersFrom = 0;
        framesTo = 0;
        layersTo = 0;
    }

    public enum Status {
        LINKED, EMPTY, POPULATED
    }

    public static int getFramesFrom() {
        return framesFrom;
    }

    public static int getFramesTo() {
        return framesTo;
    }

    public static int getLayersFrom() {
        return layersFrom;
    }

    public static int getLayersTo() {
        return layersTo;
    }

    public CelButton(
            final SEContext c, final int layerIndex, final int frameIndex,
            final Scrollable layerButton, final Scrollable frameButton,
            final VerticalScrollBox layers, final HorizontalScrollBox frames
    ) {
        super(new Coord2D(frameButton.getX(), layerButton.getY()),
                new Bounds2D(Layout.FRAME_BUTTON_W, Layout.STD_TEXT_BUTTON_H),
                Anchor.LEFT_TOP, true);

        this.frameButton = frameButton;
        this.layerButton = layerButton;
        this.frames = frames;
        this.layers = layers;

        this.c = c;
        this.frameIndex = frameIndex;
        this.layerIndex = layerIndex;

        status = Status.EMPTY;

        updateAssets();
        update(0d);
    }

    private void updateAssets() {
        final ThemeLogic tl = Settings.getTheme().logic;
        base = tl.drawCelButton(false, false, status, enabled, partOfSelection);
        highlightImg = tl.drawCelButton(false, true, status, enabled, partOfSelection);
        selectedImg = tl.drawCelButton(true, false, status, enabled, partOfSelection);
    }

    @Override
    public void update(final double deltaTime) {
        // use the guaranteed cel button to update global once per cycle
        if (frameIndex == 0 && layerIndex == 0)
            updateGlobal(c.getState());

        if (highlighted)
            StippleEffect.get().sendToolTipUpdate(ResourceCodes.CEL_BUTTON);

        setX(frameButton.getX());
        setY(layerButton.getY());

        final ProjectState s = c.getState();

        selected = s.getLayerEditIndex() == layerIndex &&
                s.getFrameIndex() == frameIndex;

        final Status oldStatus = status;
        final boolean wasEnabled = enabled, wasPartOfSelection = partOfSelection;

        final SELayer layer = s.getLayers().get(layerIndex);
        final boolean populated = s.isCelPopulated(layerIndex, frameIndex),
                linked = layer.areCelsLinked();

        status = populated
                ? (linked ? Status.LINKED : Status.POPULATED)
                : Status.EMPTY;
        enabled = layer.isEnabled();
        partOfSelection = determinePartOfSelection();

        if (oldStatus != status || wasEnabled != enabled ||
                wasPartOfSelection != partOfSelection)
            updateAssets();
    }

    private static void updateGlobal(final ProjectState s) {
        if (Permissions.selectionIsCels()) {
            final int fc = s.getFrameCount(), lc = s.getLayers().size();

            if (Math.max(framesFrom, framesTo) >= fc ||
                    Math.max(layersFrom, layersTo) >= lc)
                DeltaTimeGlobal.setStatus(Constants.CEL_SELECTION, false);
        }
    }

    private boolean determinePartOfSelection() {
        if (!Permissions.selectionIsCels())
            return false;

        final boolean validFrame = (framesFrom <= frameIndex &&
                frameIndex <= framesTo) ||
                (framesTo <= frameIndex &&
                        frameIndex <= framesFrom),
                validLayer = (layersFrom <= layerIndex &&
                        layerIndex <= layersTo) ||
                        (layersTo <= layerIndex &&
                                layerIndex <= layersFrom);

        return validFrame && validLayer;
    }

    private boolean inRenderArea() {
        return layers.renderingChild(layerButton) &&
                frames.renderingChild(frameButton);
    }

    @Override
    public void render(final GameImage canvas) {
        if (inRenderArea())
            draw(selected ? selectedImg
                    : (highlighted ? highlightImg : base), canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {

    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (!inRenderArea())
            return;

        final boolean mouseInBounds = mouseIsWithinBounds(eventLogger.getAdjustedMousePosition());

        highlighted = mouseInBounds && Permissions.isCursorFree();

        if (!mouseInBounds)
            return;

        final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
        for (GameEvent e : unprocessed) {
            if (e instanceof GameMouseEvent me &&
                    me.matchesAction(GameMouseEvent.Action.DOWN)) {
                if (KeyShortcut.areModKeysPressed(false, true, eventLogger)) {
                    me.markAsProcessed();

                    final ProjectState s = c.getState();

                    framesFrom = s.getFrameIndex();
                    layersFrom = s.getLayerEditIndex();
                    framesTo = frameIndex;
                    layersTo = layerIndex;

                    DeltaTimeGlobal.setStatus(Constants.CEL_SELECTION, true);
                } else if (KeyShortcut.areModKeysPressed(false, false, eventLogger)) {
                    me.markAsProcessed();

                    c.getState().setFrameIndex(frameIndex);
                    c.getState().setLayerEditIndex(layerIndex);

                    framesFrom = frameIndex;
                    framesTo = frameIndex;
                    layersFrom = layerIndex;
                    layersTo = layerIndex;

                    DeltaTimeGlobal.setStatus(Constants.CEL_SELECTION, false);
                }
            }
        }
    }
}
