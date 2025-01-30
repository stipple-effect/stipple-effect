package com.jordanbunke.stipple_effect.utility.action;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.DropdownItem;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.preview.Preview;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.selection.Outliner;
import com.jordanbunke.stipple_effect.selection.SEClipboard;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.tools.*;
import com.jordanbunke.stipple_effect.utility.*;
import com.jordanbunke.stipple_effect.visual.DialogAssembly;
import com.jordanbunke.stipple_effect.visual.menu_elements.navigation.logic.GatewayActionItem;
import com.jordanbunke.stipple_effect.visual.menu_elements.navigation.logic.SimpleActionItem;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;

public enum SEAction {
    // placeholder
    DUMMY(ResourceCodes.NONE, c -> {}, null),

    // miscellaneous
    OPEN_FILE(ResourceCodes.OPEN_FILE, fromSE(StippleEffect::openProject),
            new KeyShortcut(true, false, O)),
    PREVIEW(ResourceCodes.PREVIEW, null, Preview::set,
            new KeyShortcut(false, true, SPACE),
            (c, eventLogger) -> eventLogger.unpressAllKeys(), true),
    ALL_UI(ResourceCodes.ALL_UI, c -> Layout.showAllPanels(),
            new KeyShortcut(true, true, A)),
    MINIMAL_UI(ResourceCodes.MINIMAL_UI, c -> Layout.minimalUI(),
            new KeyShortcut(true, true, A)),
    TOGGLE_PANELS(ResourceCodes.NONE, c -> Layout.togglePanels(),
            new KeyShortcut(true, true, A)),
    HIDE_PIXEL_GRID(ResourceCodes.PIXEL_GRID_ON,
            SEContext::couldRenderPixelGrid,
            c -> c.renderInfo.setPixelGrid(false),
            new KeyShortcut(true, false, G), null, false),
    SHOW_PIXEL_GRID(ResourceCodes.PIXEL_GRID_OFF,
            SEContext::couldRenderPixelGrid,
            c -> c.renderInfo.setPixelGrid(true),
            new KeyShortcut(true, false, G), null, false),
    TOGGLE_PIXEL_GRID(ResourceCodes.NONE, c -> {
        if (c.couldRenderPixelGrid()) {
            c.renderInfo.togglePixelGrid();

            if (!Layout.isToolbarShowing())
                StatusUpdates.setPixelGrid(c.renderInfo.isPixelGridOn());
        } else
            StatusUpdates.cannotSetPixelGrid();
    }, new KeyShortcut(true, false, G)),
    SET_PIXEL_GRID_SELECTION(ResourceCodes.SET_PIXEL_GRID_SELECTION,
            SEContext::setPixelGridAndCheckerboard,
            new KeyShortcut(true, false, B)),
    SET_PIXEL_GRID_CANVAS(ResourceCodes.SET_PIXEL_GRID_CANVAS,
            SEContext::setPixelGridAndCheckerboard,
            new KeyShortcut(true, false, B)),
    SNAP_TO_CENTER(ResourceCodes.NONE, SEContext::snapToCenterOfImage,
            KeyShortcut.single(ENTER)),
    SNAP_TO_TP(ResourceCodes.NONE, SEContext::snapToTargetPixel,
            new KeyShortcut(false, true, ENTER)),
    TOGGLE_ALIGNMENT(ResourceCodes.NONE,
            condFromSE(s -> s.getTool().equals(TextTool.get())),
            plain(TextTool.get()::toggleAlignment),
            new KeyShortcut(true, false, K)),
    SWAP_COLORS(ResourceCodes.SWAP_COLORS, fromSE(StippleEffect::swapColors),
            new KeyShortcut(false, true, C)),
    WINDOWED(ResourceCodes.WINDOWED,
            fromSE(StippleEffect::toggleFullscreen),
            KeyShortcut.single(ESCAPE)),
    FULLSCREEN(ResourceCodes.FULLSCREEN,
            fromSE(StippleEffect::toggleFullscreen),
            KeyShortcut.single(ESCAPE)),
    CLEAR_DIALOG(ResourceCodes.NONE, fromSE(StippleEffect::clearDialog),
            KeyShortcut.single(ESCAPE)),
    BUY(ResourceCodes.BUY, plain(WebUtils::buyPage), null),
    DOCUMENTATION(ResourceCodes.DOCUMENTATION, plain(WebUtils::documentation), null),
    SCRIPTING_API(ResourceCodes.SCRIPTING_API, plain(WebUtils::apiSpec), null),
    REPORT_BUG(ResourceCodes.REPORT_BUG, plain(WebUtils::reportBug), null),
    VS_CODE_EXT(ResourceCodes.VS_CODE_EXT, plain(WebUtils::vsCodeExt), null),

    // non-project dialogs
    SETTINGS(ResourceCodes.SETTINGS,
            plain(DialogAssembly::setDialogToProgramSettings),
            new KeyShortcut(true, false, E)),
    INFO(ResourceCodes.INFO, plain(DialogAssembly::setDialogToInfo),
            new KeyShortcut(true, false, H)),
    AUTOMATION_SCRIPT(ResourceCodes.AUTOMATION_SCRIPT,
            fromSE(StippleEffect::openAutomationScript),
            new KeyShortcut(true, false, W)),
    EXIT_PROGRAM(ResourceCodes.EXIT_PROGRAM,
            plain(DialogAssembly::setDialogToExitProgramAYS), null),
    PANEL_MANAGER(ResourceCodes.PANEL_MANAGER,
            plain(DialogAssembly::setDialogToPanelManager),
            new KeyShortcut(false, true, ESCAPE)),
    NEW_PROJECT(ResourceCodes.NEW_PROJECT,
            plain(DialogAssembly::setDialogToNewProject),
            new KeyShortcut(true, false, N)),
    NEW_FONT(ResourceCodes.NEW_FONT,
            condFromSE(s -> s.getTool().equals(TextTool.get())),
            plain(DialogAssembly::setDialogToNewFont),
            new KeyShortcut(false, true, T)),

    // project dialogs
    HISTORY(ResourceCodes.HISTORY, DialogAssembly::setDialogToHistory,
            new KeyShortcut(false, true, Y)),

    // sizing
    PAD(ResourceCodes.PAD, DialogAssembly::setDialogToPad,
            new KeyShortcut(true, true, R)),
    RESIZE(ResourceCodes.RESIZE, DialogAssembly::setDialogToResize,
            new KeyShortcut(true, false, R)),
    STITCH_SPLIT(ResourceCodes.STITCH_SPLIT_FRAMES, SEContext::stitchOrSplit,
            new KeyShortcut(true, true, G)),
    CROP_TO_SELECTION(ResourceCodes.CROP_TO_SELECTION,
            c -> c.getState().hasSelection(), SEContext::cropToSelection,
            new KeyShortcut(true, true, X)),

    // layers
    NEW_LAYER(ResourceCodes.NEW_LAYER, c -> c.getState().canAddLayer(),
            SEContext::addLayer, new KeyShortcut(true, false, L),
            null, false),
    DUPLICATE_LAYER(ResourceCodes.DUPLICATE_LAYER, c -> c.getState().canAddLayer(),
            SEContext::duplicateLayer, new KeyShortcut(true, true, L),
            null, false),
    REMOVE_LAYER(ResourceCodes.REMOVE_LAYER, c -> c.getState().canRemoveLayer(),
            SEContext::removeLayer, new KeyShortcut(true, true, BACKSPACE),
            null, false),
    MOVE_LAYER_UP(ResourceCodes.MOVE_LAYER_UP, c -> c.getState().canMoveLayerUp(),
            SEContext::moveLayerUp, new KeyShortcut(true, true, UP_ARROW),
            null, false),
    MOVE_LAYER_DOWN(ResourceCodes.MOVE_LAYER_DOWN,
            c -> c.getState().canMoveLayerDown(), SEContext::moveLayerDown,
            new KeyShortcut(true, true, DOWN_ARROW),
            null, false),
    MERGE_WITH_LAYER_BELOW(ResourceCodes.MERGE_WITH_LAYER_BELOW,
            c -> c.getState().canMoveLayerDown(),
            SEContext::mergeWithLayerBelow, new KeyShortcut(true, true, M),
            null, false),
    FLATTEN(ResourceCodes.FLATTEN, c -> c.getState().canRemoveLayer(),
            SEContext::flatten, new KeyShortcut(true, false, M)),

    // layer visibility
    TOGGLE_LAYER_VISIBILITY(ResourceCodes.NONE, c -> {
        final ProjectState state = c.getState();
        final int index = state.getLayerEditIndex();

        if (state.getEditingLayer().isEnabled())
            c.disableLayer(index);
        else
            c.enableLayer(index);
    }, new KeyShortcut(false, true, _1)),
    DISABLE_LAYER(ResourceCodes.DISABLE_LAYER, null,
            c -> c.disableLayer(c.getState().getLayerEditIndex()),
            new KeyShortcut(false, true, _1), null, false),
    ENABLE_LAYER(ResourceCodes.ENABLE_LAYER, null,
            c -> c.enableLayer(c.getState().getLayerEditIndex()),
            new KeyShortcut(false, true, _1), null, false),
    ISOLATE_LAYER(ResourceCodes.ISOLATE_LAYER,
            c -> c.isolateLayer(c.getState().getLayerEditIndex()),
            new KeyShortcut(false, true, _2)),
    ENABLE_ALL_LAYERS(ResourceCodes.ENABLE_ALL_LAYERS, SEContext::enableAllLayers,
            new KeyShortcut(false, true, _3)),

    // current layer
    LAYER_SETTINGS(ResourceCodes.LAYER_SETTINGS,
            c -> DialogAssembly.setDialogToLayerSettings(
                    c.getState().getLayerEditIndex()),
            new KeyShortcut(false, true, L)),
    TOGGLE_LAYER_LINKING(ResourceCodes.NONE, SEContext::toggleLayerLinking,
            new KeyShortcut(true, false, Q)),
    TOGGLE_ONION_SKIN(ResourceCodes.NONE,
            c -> c.getState().getEditingLayer().toggleOnionSkin(),
            new KeyShortcut(true, false, _1)),

    // frames
    NEW_FRAME(ResourceCodes.NEW_FRAME,
            c -> c.getState().canAddFrame(), SEContext::addFrame,
            new KeyShortcut(true, false, F), null, false),
    DUPLICATE_FRAME(ResourceCodes.DUPLICATE_FRAME,
            c -> c.getState().canAddFrame(), SEContext::duplicateFrame,
            new KeyShortcut(true, true, F), null, false),
    REMOVE_FRAME(ResourceCodes.REMOVE_FRAME,
            c -> c.getState().canRemoveFrame(), SEContext::removeFrame,
            new KeyShortcut(true, false, BACKSPACE), null, false),
    MOVE_FRAME_BACK(ResourceCodes.MOVE_FRAME_BACK,
            c -> c.getState().canMoveFrameBack(), SEContext::moveFrameBack,
            new KeyShortcut(true, true, LEFT_ARROW), null, false),
    MOVE_FRAME_FORWARD(ResourceCodes.MOVE_FRAME_FORWARD,
            c -> c.getState().canMoveFrameForward(),
            SEContext::moveFrameForward,
            new KeyShortcut(true, true, RIGHT_ARROW), null, false),
    FRAME_PROPERTIES(ResourceCodes.FRAME_PROPERTIES,
            c -> DialogAssembly.setDialogToFrameProperties(
                    c.getState().getFrameIndex()),
            new KeyShortcut(false, true, F)),

    // playback
    TO_FIRST_FRAME(ResourceCodes.TO_FIRST_FRAME, null,
            c -> c.getState().setFrameIndex(0),
            new KeyShortcut(true, false, LEFT_ARROW),
            (c, el) -> c.tryFrameNavigationStatusUpdate(), false),
    PREVIOUS_FRAME(ResourceCodes.PREVIOUS, null,
            c -> c.getState().previousFrame(),
            new KeyShortcut(true, true, SPACE),
            (c, el) -> c.tryFrameNavigationStatusUpdate(), false),
    NEXT_FRAME(ResourceCodes.NEXT, null,
            c -> c.getState().nextFrame(),
            new KeyShortcut(true, false, SPACE),
            (c, el) -> c.tryFrameNavigationStatusUpdate(), false),
    TO_LAST_FRAME(ResourceCodes.TO_LAST_FRAME, null,
            c -> c.getState().setFrameIndex(c.getState().getFrameCount() - 1),
            new KeyShortcut(true, false, RIGHT_ARROW),
            (c, el) -> c.tryFrameNavigationStatusUpdate(), false),
    TOGGLE_PLAYING(ResourceCodes.NONE, null,
            c -> c.playbackInfo.togglePlaying(), KeyShortcut.single(SPACE)),
    CYCLE_PLAYBACK_MODE(ResourceCodes.NONE, null,
            c -> c.playbackInfo.cycleMode(),
            new KeyShortcut(true, false, ENTER)),

    // layer navigation
    LAYER_ABOVE(ResourceCodes.NONE, null,
            c -> c.getState().editLayerAbove(),
            new KeyShortcut(true, false, UP_ARROW),
            (c, el) -> c.tryLayerNavigationStatusUpdate(), false),
    LAYER_BELOW(ResourceCodes.NONE, null,
            c -> c.getState().editLayerBelow(),
            new KeyShortcut(true, false, DOWN_ARROW),
            (c, el) -> c.tryLayerNavigationStatusUpdate(), false),

    // clipboard
    CUT(ResourceCodes.CUT, c -> c.getState().hasSelection(),
            SEContext::cut, new KeyShortcut(true, false, X)),
    COPY(ResourceCodes.COPY, c -> c.getState().hasSelection(),
            SEContext::copy, new KeyShortcut(true, false, C)),
    PASTE(ResourceCodes.PASTE, c -> SEClipboard.get().hasContent(),
            c -> c.paste(false), new KeyShortcut(true, false, V)),
    PASTE_NEW_LAYER(ResourceCodes.PASTE_NEW_LAYER,
            c -> SEClipboard.get().hasContent(),
            c -> c.paste(true), new KeyShortcut(true, true, V)),

    // palette
    NEW_PALETTE(ResourceCodes.NEW_PALETTE, fromSE(StippleEffect::newPalette),
            new KeyShortcut(false, true, N)),
    IMPORT_PALETTE(ResourceCodes.IMPORT_PALETTE,
            fromSE(StippleEffect::openPalette), null),
    DELETE_PALETTE(ResourceCodes.DELETE_PALETTE,
            condFromSE(StippleEffect::hasPaletteContents),
            fromSE(StippleEffect::deletePalette), null),
    SAVE_PALETTE(ResourceCodes.SAVE_PALETTE,
            condFromSE(StippleEffect::hasPaletteContents),
            fromSE(s -> DialogAssembly.setDialogToSavePalette(
                    s.getSelectedPalette())),
            new KeyShortcut(true, false, P)),
    SORT_PALETTE(ResourceCodes.SORT_PALETTE,
            condFromSE(StippleEffect::hasPaletteContents),
            fromSE(s -> DialogAssembly.setDialogToSortPalette(
                    s.getSelectedPalette())),
            new KeyShortcut(false, true, M)),
    PALETTE_SETTINGS(ResourceCodes.PALETTE_SETTINGS,
            condFromSE(StippleEffect::hasPaletteContents),
            fromSE(s -> DialogAssembly.setDialogToPaletteSettings(
                    s.getSelectedPalette())),
            new KeyShortcut(false, true, E)),

    // palette color
    ADD_TO_PALETTE(ResourceCodes.ADD_TO_PALETTE,
            condFromSE(StippleEffect::hasPaletteContents),
            fromSE(StippleEffect::addColorToPalette),
            new KeyShortcut(false, true, A)),
    MOVE_COLOR_LEFT(ResourceCodes.MOVE_LEFT_IN_PALETTE,
            fromSE(StippleEffect::moveColorLeftInPalette),
            new KeyShortcut(false, true, COMMA)),
    MOVE_COLOR_RIGHT(ResourceCodes.MOVE_RIGHT_IN_PALETTE,
            fromSE(StippleEffect::moveColorRightInPalette),
            new KeyShortcut(false, true, PERIOD)),
    SELECT_COLOR_LEFT(ResourceCodes.NONE,
            fromSE(StippleEffect::selectPaletteColorToTheLeft),
            new KeyShortcut(true, false, COMMA)),
    SELECT_COLOR_RIGHT(ResourceCodes.NONE,
            fromSE(StippleEffect::selectPaletteColorToTheRight),
            new KeyShortcut(true, false, PERIOD)),

    // state control
    UNDO(ResourceCodes.UNDO, c -> c.stateManager.canUndo(),
            c -> c.stateManager.undoToCheckpoint(),
            new KeyShortcut(true, false, Z), null, false),
    REDO(ResourceCodes.REDO, c -> c.stateManager.canRedo(),
            c -> c.stateManager.redoToCheckpoint(),
            new KeyShortcut(true, false, Y), null, false),
    GRANULAR_UNDO(ResourceCodes.GRANULAR_UNDO, c -> c.stateManager.canUndo(),
            c -> c.stateManager.undo(true),
            new KeyShortcut(true, true, Z), null, false),
    GRANULAR_REDO(ResourceCodes.GRANULAR_REDO, c -> c.stateManager.canRedo(),
            c -> c.stateManager.redo(true),
            new KeyShortcut(true, true, Y), null, false),

    // selection
    DESELECT(ResourceCodes.DESELECT, c -> c.getState().hasSelection(),
            c -> c.deselect(true), new KeyShortcut(true, false, D)),
    SELECT_ALL(ResourceCodes.SELECT_ALL, SEContext::selectAll,
            new KeyShortcut(true, false, A)),
    INVERT_SELECTION(ResourceCodes.INVERT_SELECTION, SEContext::invertSelection,
            new KeyShortcut(true, false, I)),
    FILL_PRIMARY(ResourceCodes.FILL_PRIMARY,
            c -> c.getState().hasSelection(),
            c -> c.fillSelection(false), KeyShortcut.single(BACKSPACE)),
    FILL_SECONDARY(ResourceCodes.FILL_SECONDARY,
            c -> c.getState().hasSelection(),
            c -> c.fillSelection(true),
            new KeyShortcut(false, true, BACKSPACE)),
    DELETE_SELECTION_CONTENTS(ResourceCodes.DELETE_SELECTION_CONTENTS,
            c -> c.getState().hasSelection(),
            c -> c.deleteSelectionContents(true),
            KeyShortcut.single(DELETE)),
    DELETE_SELECTION_CONTENTS_NO_DESELECT(ResourceCodes.NONE,
            c -> c.getState().hasSelection(),
            c -> c.deleteSelectionContents(false),
            new KeyShortcut(false, true, DELETE)),
    HORZ_BOUNDS_REFLECTION(ResourceCodes.HORZ_BOUNDS_REFLECTION,
            c -> c.getState().hasSelection(), c -> c.reflectSelection(true),
            new KeyShortcut(true, false, _4), null, false),
    VERT_BOUNDS_REFLECTION(ResourceCodes.VERT_BOUNDS_REFLECTION,
            c -> c.getState().hasSelection(), c -> c.reflectSelection(false),
            new KeyShortcut(true, false, _5), null, false),
    HORZ_CONTENTS_REFLECTION(ResourceCodes.HORZ_CONTENTS_REFLECTION,
            c -> c.getState().hasSelection(),
            c -> c.reflectSelectionContents(true),
            new KeyShortcut(true, true, _4), null, false),
    VERT_CONTENTS_REFLECTION(ResourceCodes.VERT_CONTENTS_REFLECTION,
            c -> c.getState().hasSelection(),
            c -> c.reflectSelectionContents(false),
            new KeyShortcut(true, true, _5), null, false),
    CONFIGURE_OUTLINE(ResourceCodes.OUTLINE,
            DialogAssembly::setDialogToOutline,
            new KeyShortcut(false, true, O)),
    LAST_OUTLINE(ResourceCodes.LAST_OUTLINE,
            c -> c.getState().hasSelection(),
            c -> c.outlineSelection(DialogVals.getOutlineSideMask()),
            new KeyShortcut(true, false, _9), null, false),
    SINGLE_OUTLINE(ResourceCodes.SINGLE_OUTLINE,
            c -> c.getState().hasSelection(),
            c -> c.outlineSelection(Outliner.getSingleOutlineMask()),
            new KeyShortcut(false, true, _9), null, false),
    DOUBLE_OUTLINE(ResourceCodes.DOUBLE_OUTLINE,
            c -> c.getState().hasSelection(),
            c -> c.outlineSelection(Outliner.getDoubleOutlineMask()),
            new KeyShortcut(true, true, _9), null, false),

    // actions
    HSV_SHIFT(ResourceCodes.HSV_SHIFT, DialogAssembly::setDialogToHSVShift,
            new KeyShortcut(true, true, H)),
    COLOR_SCRIPT(ResourceCodes.COLOR_SCRIPT,
            DialogAssembly::setDialogToColorScript,
            new KeyShortcut(true, true, W)),
    CONTENTS_TO_PALETTE(ResourceCodes.CONTENTS_TO_PALETTE,
            condFromSE(StippleEffect::hasPaletteContents),
            c -> DialogAssembly.setDialogToAddContentsToPalette(c,
                    StippleEffect.get().getSelectedPalette()),
            new KeyShortcut(false, true, D)),
    PALETTIZE(ResourceCodes.PALETTIZE,
            condFromSE(StippleEffect::hasPaletteContents),
            c -> DialogAssembly.setDialogToPalettize(c,
                    StippleEffect.get().getSelectedPalette()),
            new KeyShortcut(false, true, P)),
    GENERATE_TIME_LAPSE(ResourceCodes.GENERATE_TIME_LAPSE,
            SEContext::generateTimeLapse, null),

    // set tools
    SET_TOOL_HAND(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Hand.get())),
            KeyShortcut.single(H)),
    SET_TOOL_ZOOM(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Zoom.get())),
            KeyShortcut.single(Z)),
    SET_TOOL_STIPPLE_PENCIL(ResourceCodes.NONE,
            fromSE(s -> s.setTool(StipplePencil.get())),
            KeyShortcut.single(O)),
    SET_TOOL_PENCIL(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Pencil.get())),
            KeyShortcut.single(P)),
    SET_TOOL_BRUSH(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Brush.get())),
            KeyShortcut.single(B)),
    SET_TOOL_SHADE_BRUSH(ResourceCodes.NONE,
            fromSE(s -> s.setTool(ShadeBrush.get())),
            KeyShortcut.single(D)),
    SET_TOOL_SCRIPT_BRUSH(ResourceCodes.NONE,
            fromSE(s -> s.setTool(ScriptBrush.get())),
            KeyShortcut.single(Q)),
    SET_TOOL_ERASER(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Eraser.get())),
            KeyShortcut.single(E)),
    SET_TOOL_GRADIENT(ResourceCodes.NONE,
            fromSE(s -> s.setTool(GradientTool.get())),
            KeyShortcut.single(G)),
    SET_TOOL_LINE(ResourceCodes.NONE,
            fromSE(s -> s.setTool(LineTool.get())),
            KeyShortcut.single(L)),
    SET_TOOL_SHAPE(ResourceCodes.NONE,
            fromSE(s -> s.setTool(ShapeTool.get())),
            KeyShortcut.single(R)),
    SET_TOOL_TEXT(ResourceCodes.NONE,
            fromSE(s -> s.setTool(TextTool.get())),
            KeyShortcut.single(T)),
    SET_TOOL_FILL(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Fill.get())),
            KeyShortcut.single(F)),
    SET_TOOL_COLOR_PICKER(ResourceCodes.NONE,
            fromSE(s -> s.setTool(ColorPicker.get())),
            KeyShortcut.single(C)),
    SET_TOOL_WAND(ResourceCodes.NONE,
            fromSE(s -> s.setTool(Wand.get())),
            KeyShortcut.single(W)),
    SET_TOOL_BOX_SELECT(ResourceCodes.NONE,
            fromSE(s -> s.setTool(BoxSelect.get())),
            KeyShortcut.single(X)),
    SET_TOOL_POLYGON_SELECT(ResourceCodes.NONE,
            fromSE(s -> s.setTool(PolygonSelect.get())),
            KeyShortcut.single(Y)),
    SET_TOOL_BRUSH_SELECT(ResourceCodes.NONE,
            fromSE(s -> s.setTool(BrushSelect.get())),
            KeyShortcut.single(V)),
    SET_TOOL_MOVE_SELECTION(ResourceCodes.NONE,
            fromSE(s -> s.setTool(MoveSelection.get())),
            KeyShortcut.single(M)),
    SET_TOOL_PICK_UP_SELECTION(ResourceCodes.NONE,
            fromSE(s -> s.setTool(PickUpSelection.get())),
            KeyShortcut.single(U)),

    // quick selects
    PROJECT_1(ResourceCodes.NUMKEY_PREFIX + 1,
            fromSE(s -> s.setContextIndex(0)),
            KeyShortcut.single(_1)),
    PROJECT_2(ResourceCodes.NUMKEY_PREFIX + 2,
            fromSE(s -> s.setContextIndex(1)),
            KeyShortcut.single(_2)),
    PROJECT_3(ResourceCodes.NUMKEY_PREFIX + 3,
            fromSE(s -> s.setContextIndex(2)),
            KeyShortcut.single(_3)),
    PROJECT_4(ResourceCodes.NUMKEY_PREFIX + 4,
            fromSE(s -> s.setContextIndex(3)),
            KeyShortcut.single(_4)),
    PROJECT_5(ResourceCodes.NUMKEY_PREFIX + 5,
            fromSE(s -> s.setContextIndex(4)),
            KeyShortcut.single(_5)),
    PROJECT_6(ResourceCodes.NUMKEY_PREFIX + 6,
            fromSE(s -> s.setContextIndex(5)),
            KeyShortcut.single(_6)),
    PROJECT_7(ResourceCodes.NUMKEY_PREFIX + 7,
            fromSE(s -> s.setContextIndex(6)),
            KeyShortcut.single(_7)),
    PROJECT_8(ResourceCodes.NUMKEY_PREFIX + 8,
            fromSE(s -> s.setContextIndex(7)),
            KeyShortcut.single(_8)),
    PROJECT_9(ResourceCodes.NUMKEY_PREFIX + 9,
            fromSE(s -> s.setContextIndex(8)),
            KeyShortcut.single(_9));

    public final String code;
    public final Consumer<SEContext> behaviour;
    private final Predicate<SEContext> precondition;

    // key press
    public final BiConsumer<SEContext, InputEventLogger> postKeyPressBehaviour;
    public final KeyShortcut shortcut;

    // dropdown menu
    private final boolean closeDropdown;

    SEAction(
            final String code, final Consumer<SEContext> behaviour,
            final KeyShortcut shortcut
    ) {
        this(code, null, behaviour, shortcut);
    }

    SEAction(
            final String code,
            final Predicate<SEContext> precondition,
            final Consumer<SEContext> behaviour, final KeyShortcut shortcut
    ) {
        this(code, precondition, behaviour, shortcut, null, true);
    }

    SEAction(
            final String code,
            final Predicate<SEContext> precondition,
            final Consumer<SEContext> behaviour, final KeyShortcut shortcut,
            final BiConsumer<SEContext, InputEventLogger> postKeyPressBehaviour,
            final boolean closeDropdown
    ) {
        this.code = code;
        this.precondition = precondition;
        this.behaviour = behaviour;
        this.shortcut = shortcut;
        this.postKeyPressBehaviour = postKeyPressBehaviour;
        this.closeDropdown = closeDropdown;
    }

    public void tryForMatchingKeyStroke(
            final InputEventLogger eventLogger, final SEContext c
    ) {
        if (shortcut != null)
            shortcut.checkIfPressed(eventLogger, () -> {
                final boolean executed = tryExecute(c);

                if (executed && postKeyPressBehaviour != null)
                    postKeyPressBehaviour.accept(c, eventLogger);
            });
    }

    public void doForMatchingKeyStroke(
            final InputEventLogger eventLogger, final SEContext c
    ) {
        if (shortcut != null)
            shortcut.checkIfPressed(eventLogger, () -> {
                behaviour.accept(c);

                if (postKeyPressBehaviour != null)
                    postKeyPressBehaviour.accept(c, eventLogger);
            });
    }

    public boolean tryExecute(final SEContext c) {
        if (precondition == null || precondition.test(c)) {
            behaviour.accept(c);
            return true;
        }

        return false;
    }

    public Predicate<SEContext> getPrecondition() {
        return precondition == null ? c -> true : precondition;
    }

    public DropdownItem toItem(final SEContext c) {
        if (precondition != null)
            return new GatewayActionItem(code, () -> precondition.test(c),
                    () -> behaviour.accept(c), closeDropdown);
        else
            return new SimpleActionItem(code,
                    () -> behaviour.accept(c), closeDropdown);
    }

    private static Consumer<SEContext> fromSE(
            final Consumer<StippleEffect> behaviour
    ) {
        return c -> behaviour.accept(StippleEffect.get());
    }

    private static Consumer<SEContext> plain(final Runnable behaviour) {
        return c -> behaviour.run();
    }

    private static Predicate<SEContext> condFromSE(
            final Predicate<StippleEffect> precondition
    ) {
        return c -> precondition.test(StippleEffect.get());
    }

    public static SEAction[] quickSelectActions() {
        return new SEAction[] {
                PROJECT_1, PROJECT_2, PROJECT_3,
                PROJECT_4, PROJECT_5, PROJECT_6,
                PROJECT_7, PROJECT_8, PROJECT_9
        };
    }

    public static SEAction[] setToolActions() {
        return new SEAction[] {
                SET_TOOL_HAND, SET_TOOL_ZOOM,
                SET_TOOL_STIPPLE_PENCIL, SET_TOOL_PENCIL,
                SET_TOOL_BRUSH, SET_TOOL_SHADE_BRUSH, SET_TOOL_SCRIPT_BRUSH, SET_TOOL_ERASER,
                SET_TOOL_GRADIENT, SET_TOOL_LINE, SET_TOOL_SHAPE, SET_TOOL_TEXT,
                SET_TOOL_FILL, SET_TOOL_COLOR_PICKER,
                SET_TOOL_WAND, SET_TOOL_BOX_SELECT, SET_TOOL_POLYGON_SELECT, SET_TOOL_BRUSH_SELECT,
                SET_TOOL_MOVE_SELECTION, SET_TOOL_PICK_UP_SELECTION
        };
    }

    public static SEAction[] stateControlActions() {
        return new SEAction[] {
                UNDO, REDO, GRANULAR_UNDO, GRANULAR_REDO
        };
    }

    public static SEAction[] frameActions() {
        return new SEAction[] {
                NEW_FRAME, DUPLICATE_FRAME, REMOVE_FRAME,
                MOVE_FRAME_BACK, MOVE_FRAME_FORWARD, FRAME_PROPERTIES
        };
    }

    public static SEAction[] playbackActions() {
        return new SEAction[] {
                TO_FIRST_FRAME, PREVIOUS_FRAME, NEXT_FRAME, TO_LAST_FRAME,
                TOGGLE_PLAYING, CYCLE_PLAYBACK_MODE
        };
    }

    public static SEAction[] layerActions() {
        return new SEAction[] {
                NEW_LAYER, DUPLICATE_LAYER, REMOVE_LAYER,
                MOVE_LAYER_UP, MOVE_LAYER_DOWN,
                MERGE_WITH_LAYER_BELOW, FLATTEN
        };
    }

    public static SEAction[] clipboardActions() {
        return new SEAction[] {
                CUT, COPY, PASTE, PASTE_NEW_LAYER
        };
    }

    public static SEAction[] actionsMenuActions() {
        return new SEAction[] {
                HSV_SHIFT, COLOR_SCRIPT, CONTENTS_TO_PALETTE, PALETTIZE,
                GENERATE_TIME_LAPSE
        };
    }

    public static SEAction[] reflectionActions() {
        return new SEAction[] {
                HORZ_BOUNDS_REFLECTION, VERT_BOUNDS_REFLECTION,
                HORZ_CONTENTS_REFLECTION, VERT_CONTENTS_REFLECTION
        };
    }

    public static SEAction[] selectionModificationActions() {
        return new SEAction[] {
                DESELECT, SELECT_ALL, INVERT_SELECTION
        };
    }

    public static SEAction[] selectionOperationActions() {
        return new SEAction[] {
                FILL_PRIMARY, FILL_SECONDARY, DELETE_SELECTION_CONTENTS,
                DELETE_SELECTION_CONTENTS_NO_DESELECT
        };
    }

    public static SEAction[] paletteActions() {
        return new SEAction[] {
                NEW_PALETTE, IMPORT_PALETTE, DELETE_PALETTE,
                SAVE_PALETTE, SORT_PALETTE, PALETTE_SETTINGS
        };
    }
}
