package com.jordanbunke.stipple_effect.visual.menu_elements;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.SelectableMenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.Layout;
import com.jordanbunke.stipple_effect.utility.Permissions;
import com.jordanbunke.stipple_effect.visual.GraphicsUtils;

import java.util.List;

public class LayerVisibilityButton extends SelectableMenuElement {
    private final int index;

    private boolean highlighted;
    private GameImage nh, hi;

    private boolean enabled;

    public LayerVisibilityButton(
            final Coord2D position,
            final int index
    ) {
        super(position, Layout.ICON_DIMS, Anchor.LEFT_TOP, true);

        this.index = index;
        highlighted = false;
        updateEnabled();
        updateAssets();
    }

    @Override
    public void choose() {
        toggleEnabled();
    }

    private void toggleEnabled() {
        if (enabled)
            StippleEffect.get().getContext().disableLayer(index);
        else
            StippleEffect.get().getContext().enableLayer(index);
    }

    private void isolateLayer() {
        StippleEffect.get().getContext().isolateLayer(index);
    }

    private void enableAllLayers() {
        StippleEffect.get().getContext().enableAllLayers();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final boolean mouseInBounds = mouseIsWithinBounds(
                eventLogger.getAdjustedMousePosition());

        highlighted = isSelected() || mouseInBounds;

        if (!mouseInBounds)
            return;

        final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
        for (GameEvent e : unprocessed) {
            if (e instanceof GameMouseEvent mouseEvent &&
                    mouseEvent.matchesAction(GameMouseEvent.Action.DOWN)) {
                mouseEvent.markAsProcessed();

                if (eventLogger.isPressed(Key.SHIFT) && !eventLogger.isPressed(Key.CTRL))
                    isolateLayer();
                else if (eventLogger.isPressed(Key.CTRL) && !eventLogger.isPressed(Key.SHIFT))
                    enableAllLayers();
                else
                    toggleEnabled();

                return;
            }
        }
    }

    @Override
    public void update(final double deltaTime) {
        final boolean was = enabled;
        updateEnabled();

        if (was != enabled)
            updateAssets();

        if (highlighted && Permissions.isCursorFree())
            StippleEffect.get().sendToolTipUpdate(getCode());
    }

    private void updateEnabled() {
        enabled = StippleEffect.get().getContext().getState()
                .getLayers().get(index).isEnabled();
    }

    private void updateAssets() {
        nh = GraphicsUtils.loadIcon(getCode());
        hi = GraphicsUtils.highlightIconButton(nh);
    }

    private String getCode() {
        return enabled
                ? ResourceCodes.LAYER_ENABLED
                : ResourceCodes.LAYER_DISABLED;
    }

    @Override
    public void render(final GameImage canvas) {
        draw(highlighted ? hi : nh, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {

    }
}
