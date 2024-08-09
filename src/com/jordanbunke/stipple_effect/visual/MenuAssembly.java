package com.jordanbunke.stipple_effect.visual;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.DropdownItem;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.NestedItem;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.palette.PaletteLoader;
import com.jordanbunke.stipple_effect.project.PlaybackInfo;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.ZoomLevel;
import com.jordanbunke.stipple_effect.selection.SelectionMode;
import com.jordanbunke.stipple_effect.tools.Tool;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.EnumUtils;
import com.jordanbunke.stipple_effect.utility.SamplerMode;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.action.SEAction;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.menu_elements.*;
import com.jordanbunke.stipple_effect.visual.menu_elements.colors.*;
import com.jordanbunke.stipple_effect.visual.menu_elements.draggables.FrameButton;
import com.jordanbunke.stipple_effect.visual.menu_elements.draggables.LayerButton;
import com.jordanbunke.stipple_effect.visual.menu_elements.layout.VerticalPanelAdjuster;
import com.jordanbunke.stipple_effect.visual.menu_elements.navigation.Navbar;
import com.jordanbunke.stipple_effect.visual.menu_elements.navigation.logic.ThinkingActionItem;
import com.jordanbunke.stipple_effect.visual.menu_elements.scrollable.HorizontalScrollBox;
import com.jordanbunke.stipple_effect.visual.menu_elements.scrollable.VerticalScrollBox;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.Alignment;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.ButtonType;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.TextButton;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.jordanbunke.stipple_effect.utility.Layout.*;
import static com.jordanbunke.stipple_effect.utility.action.SEAction.*;

public class MenuAssembly {

    public static Menu stub() {
        return new Menu();
    }

    public static Menu buildProjectsMenu() {
        final MenuBuilder mb = new MenuBuilder();
        final StippleEffect s = StippleEffect.get();
        final SEContext c = s.getContext();

        mb.add(new Navbar(
                new NestedItem(StippleEffect.PROGRAM_NAME,
                        SETTINGS.toItem(c),
                        INFO.toItem(c),
                        AUTOMATION_SCRIPT.toItem(c),
                        new NestedItem("Resources",
                                DOCUMENTATION.toItem(c),
                                SCRIPTING_API.toItem(c),
                                REPORT_BUG.toItem(c),
                                VS_CODE_EXT.toItem(c)),
                        EXIT_PROGRAM.toItem(c)),
                new NestedItem("File",
                        NEW_PROJECT.toItem(c),
                        OPEN_FILE.toItem(c)),
                new NestedItem("Edit",
                        new NestedItem("State control",
                                UNDO.toItem(c),
                                GRANULAR_UNDO.toItem(c),
                                REDO.toItem(c),
                                GRANULAR_REDO.toItem(c),
                                HISTORY.toItem(c)),
                        new NestedItem("Sizing",
                                RESIZE.toItem(c),
                                PAD.toItem(c),
                                STITCH_SPLIT.toItem(c),
                                CROP_TO_SELECTION.toItem(c)),
                        new NestedItem("Clipboard",
                                Arrays.stream(clipboardActions())
                                        .map(a -> a.toItem(c))
                                        .toArray(DropdownItem[]::new))),
                new NestedItem("View",
                        new ThinkingActionItem(c,
                                anon -> StippleEffect.get().isWindowed()
                                        ? FULLSCREEN : WINDOWED,
                                FULLSCREEN, WINDOWED),
                        PREVIEW.toItem(c),
                        new NestedItem("Grid and checkerboard",
                                new ThinkingActionItem(c,
                                        anon -> anon.renderInfo.isPixelGridOn()
                                                ? HIDE_PIXEL_GRID
                                                : SHOW_PIXEL_GRID,
                                        SHOW_PIXEL_GRID, HIDE_PIXEL_GRID),
                                new ThinkingActionItem(c,
                                        anon -> anon.getState().hasSelection()
                                                ? SET_PIXEL_GRID_SELECTION
                                                : SET_PIXEL_GRID_CANVAS,
                                        SET_PIXEL_GRID_SELECTION,
                                        SET_PIXEL_GRID_CANVAS)),
                        new NestedItem("Layout",
                                PANEL_MANAGER.toItem(c),
                                new ThinkingActionItem(c,
                                        na -> areAllPanelsShowing()
                                                ? MINIMAL_UI : ALL_UI,
                                        ALL_UI, MINIMAL_UI))),
                new NestedItem("Layer", NEW_LAYER.toItem(c),
                        DUPLICATE_LAYER.toItem(c), REMOVE_LAYER.toItem(c),
                        MOVE_LAYER_UP.toItem(c), MOVE_LAYER_DOWN.toItem(c),
                        MERGE_WITH_LAYER_BELOW.toItem(c), FLATTEN.toItem(c),
                        new NestedItem("Visibility status",
                                new ThinkingActionItem(c, anon ->
                                        anon.getState().getEditingLayer().isEnabled()
                                                ? DISABLE_LAYER : ENABLE_LAYER,
                                        DISABLE_LAYER, ENABLE_LAYER),
                                ISOLATE_LAYER.toItem(c),
                                ENABLE_ALL_LAYERS.toItem(c)),
                        LAYER_SETTINGS.toItem(c)),
                new NestedItem("Frame", Arrays.stream(frameActions())
                        .map(a -> a.toItem(c))
                        .toArray(DropdownItem[]::new)),
                new NestedItem("Selection",
                        new NestedItem("Modify selection",
                                Arrays.stream(selectionModificationActions())
                                        .map(a -> a.toItem(c))
                                        .toArray(DropdownItem[]::new)),
                        new NestedItem("Operate on selection",
                                FILL_PRIMARY.toItem(c),
                                FILL_SECONDARY.toItem(c),
                                DELETE_SELECTION_CONTENTS.toItem(c)),
                        new NestedItem("Reflection",
                                Arrays.stream(reflectionActions())
                                        .map(a -> a.toItem(c))
                                        .toArray(DropdownItem[]::new)),
                        new NestedItem("Outline",
                                CONFIGURE_OUTLINE.toItem(c),
                                LAST_OUTLINE.toItem(c),
                                SINGLE_OUTLINE.toItem(c),
                                DOUBLE_OUTLINE.toItem(c))),
                new NestedItem("Palette",
                        NEW_PALETTE.toItem(c),
                        IMPORT_PALETTE.toItem(c),
                        DELETE_PALETTE.toItem(c),
                        SAVE_PALETTE.toItem(c),
                        SORT_PALETTE.toItem(c),
                        PALETTE_SETTINGS.toItem(c),
                        new NestedItem("Load preset palette...",
                                Arrays.stream(PaletteLoader.Preset.values())
                                        .map(PaletteLoader.Preset::toItem)
                                        .toArray(DropdownItem[]::new))),
                new NestedItem("Actions", Arrays.stream(actionsMenuActions())
                        .map(a -> a.toItem(c))
                        .toArray(DropdownItem[]::new))));

        // panel expand / collapse
        final Coord2D panelIconPos = getProjectsPosition().displace(
                getProjectsWidth() - BUTTON_INC,
                ICON_BUTTON_OFFSET_Y);

        if (!isProjectsExpanded())
            mb.add(IconButton.make(ResourceCodes.EXPAND_PANEL, panelIconPos,
                    () -> adjustPanels(() -> setProjectsExpanded(true))));
        else
            mb.add(IconButton.make(ResourceCodes.COLLAPSE_PANEL, panelIconPos,
                    () -> adjustPanels(() -> setProjectsExpanded(false))));

        // early break if collapsed
        if (!isProjectsExpanded())
            return mb.build();

        // project previews

        final int amount = StippleEffect.get().getContexts().size(),
                selectedIndex = StippleEffect.get().getContextIndex();

        final MenuBuilder toScroll = new MenuBuilder();

        final Coord2D firstPos = getProjectsPosition()
                .displace(getPanelContentDisplacement());
        int realRightX = firstPos.x, cumulativeWidth = 0, initialOffsetX = 0;

        for (int i = 0; i < amount; i++) {
            int offsetX = 0;

            final Coord2D pos = firstPos.displace(cumulativeWidth, 0);
            final ProjectButton projectButton = ProjectButton.make(pos, i);
            toScroll.add(projectButton);

            offsetX += projectButton.getWidth() + BUTTON_OFFSET;

            // close project button
            final Coord2D cpPos = pos.displace(offsetX,
                    (STD_TEXT_BUTTON_H - BUTTON_DIM) / 2);

            offsetX += BUTTON_DIM + SPACE_BETWEEN_PROJECT_BUTTONS_X;

            final int index = i;
            final Runnable closeBehaviour = () -> {
                if (StippleEffect.get().getContexts().get(index)
                        .getSaveConfig().hasUnsavedChanges())
                    DialogAssembly.setDialogToCloseProjectAYS(index);
                else
                    StippleEffect.get().removeContext(index);
            };

            toScroll.add(IconButton.make(ResourceCodes.CLOSE_PROJECT, cpPos, closeBehaviour));

            cumulativeWidth += offsetX;
            realRightX = cpPos.x + BUTTON_DIM;

            if (i == selectedIndex - PROJECTS_BEFORE_TO_DISPLAY)
                initialOffsetX = pos.x - firstPos.x;
        }

        final int scrollBoxW = getProjectScrollWindowWidth();
        final boolean requiresScrolling = realRightX > firstPos.x + scrollBoxW;
        final int scrollBoxH = TOP_PANEL_SCROLL_WINDOW_H -
                (requiresScrolling ? 0 : SLIDER_BALL_DIM);
        setProjectsRequiresScrolling(requiresScrolling);

        mb.add(new HorizontalScrollBox(firstPos,
                new Bounds2D(scrollBoxW, scrollBoxH),
                Arrays.stream(toScroll.build().getMenuElements())
                        .map(Scrollable::new)
                        .toArray(Scrollable[]::new),
                realRightX, initialOffsetX));

        return mb.build();
    }

    public static Menu buildFlipbookMenu() {
        final MenuBuilder mb = new MenuBuilder();
        final SEContext c = StippleEffect.get().getContext();
        final Coord2D panelPos = getFlipbookPosition();

        final int panelHeight = getFlipbookHeight(),
                panelWidth = getFlipbookWidth(),
                lbLeftBuffer = CONTENT_BUFFER_PX,
                lbTopBuffer = 4 * STD_TEXT_BUTTON_INC,
                lbBoxWidth = LAYER_BUTTON_W + (5 * BUTTON_INC),
                fbOffsetFromLB = lbLeftBuffer + lbBoxWidth + CONTENT_BUFFER_PX;

        addHidePanelToMenuBuilder(mb,
                panelPos.displace(panelWidth, 0),
                () -> setFlipbookPanelShowing(false));

        final Coord2D firstFBPos = panelPos.displace(fbOffsetFromLB,
                PANEL_TITLE_CONTENT_OFFSET_Y),
                firstLBPos = panelPos.displace(lbLeftBuffer, lbTopBuffer);

        // aesthetic box
        final int abX = firstLBPos.x, abY = firstFBPos.y,
                abInPanelX = abX - panelPos.x, abInPanelY = abY - panelPos.y,
                abWidth = panelWidth - (abInPanelX + CONTENT_BUFFER_PX),
                abHeight = panelHeight - (abInPanelY + CONTENT_BUFFER_PX);
        mb.add(new VerticalScrollBox(
                new Coord2D(abX, abY), new Bounds2D(abWidth, abHeight),
                new Scrollable[0], abY + abHeight, 0));

        // playback
        final Coord2D labelPos = panelPos.displace(0,
                ICON_BUTTON_OFFSET_Y + BUTTON_INC);

        final TextLabel playbackLabel = TextLabel.make(labelPos, "");
        final IncrementalRangeElements<Integer> playback =
                IncrementalRangeElements.makeForInt(playbackLabel,
                        labelPos.y + ICON_BUTTON_OFFSET_Y,
                        labelPos.y + TEXT_Y_OFFSET, 1,
                        Constants.MIN_PLAYBACK_FPS, Constants.MAX_PLAYBACK_FPS,
                        c.playbackInfo::setFps, c.playbackInfo::getFps,
                        fps -> fps, fps -> fps, fps -> fps + " FPS",
                        "XXX FPS");
        mb.addAll(playbackLabel, playback.decButton, playback.incButton,
                playback.slider, playback.value);

        // layer controls
        populateButtonsIntoBuilder(mb, new SEAction[] {
                NEW_LAYER, DUPLICATE_LAYER, REMOVE_LAYER,
                MOVE_LAYER_UP, MOVE_LAYER_DOWN, MERGE_WITH_LAYER_BELOW
        }, c, panelPos.displace(CONTENT_BUFFER_PX,
                lbTopBuffer - STD_TEXT_BUTTON_INC));

        // playback controls
        populateButtonsIntoBuilder(mb, new SEAction[] {
                TO_FIRST_FRAME, PREVIOUS_FRAME, DUMMY,
                NEXT_FRAME, TO_LAST_FRAME
        }, c, panelPos.displace(CONTENT_BUFFER_PX, ICON_BUTTON_OFFSET_Y));

        final int PLAY_STOP_INDEX = 2,
                PLAYBACK_MODE_INDEX = 5;

        // play/stop as toggle
        final Coord2D disp = new Coord2D(BUTTON_INC, 0),
                playStopTogglePos = panelPos.displace(CONTENT_BUFFER_PX +
                                (PLAY_STOP_INDEX * BUTTON_INC),
                        ICON_BUTTON_OFFSET_Y);
        mb.add(generatePlayStopToggle(c.playbackInfo, playStopTogglePos));

        // playback mode toggle button
        final Coord2D playbackModeTogglePos = playStopTogglePos.displace(
                disp.scale(PLAYBACK_MODE_INDEX - PLAY_STOP_INDEX));
        mb.add(generatePlaybackModeToggle(c.playbackInfo, playbackModeTogglePos));

        // frame controls
        final Coord2D frameControlPos = panelPos.displace(fbOffsetFromLB, 0);
        populateButtonsIntoBuilder(mb, new SEAction[] {
                NEW_FRAME, DUPLICATE_FRAME, REMOVE_FRAME,
                MOVE_FRAME_BACK, MOVE_FRAME_FORWARD, DUMMY,
                FRAME_PROPERTIES
        }, c, frameControlPos.displace(0, ICON_BUTTON_OFFSET_Y));

        // layer buttons
        final List<SELayer> layers = c.getState().getLayers();
        final int layerCount = layers.size();
        final Scrollable[] layerButtons = new Scrollable[layerCount];

        final MenuBuilder layerElements = new MenuBuilder();

        int realBottomY = firstLBPos.y;

        LayerButton.logic.reset();

        for (int i = layerCount - 1; i >= 0; i--) {
            final Coord2D pos = firstLBPos.displace(0,
                    (layerCount - (i + 1)) * STD_TEXT_BUTTON_INC);

            layerButtons[i] = new Scrollable(
                    new LayerButton(pos, i, c, layers.get(i)));
            layerElements.add(layerButtons[i]);

            // visibility toggle
            final Coord2D vtPos = pos.displace(LAYER_BUTTON_W + BUTTON_OFFSET,
                    (STD_TEXT_BUTTON_H / 2)  - (BUTTON_DIM / 2));
            layerElements.add(new LayerVisibilityButton(vtPos, i));

            // frames linked toggle
            final Coord2D flPos = vtPos.displace(BUTTON_INC, 0);
            layerElements.add(generateFramesLinkedToggle(i, flPos));

            // onion skin toggle
            final Coord2D onionPos = vtPos.displace(BUTTON_INC * 2, 0);
            layerElements.add(generateOnionSkinToggle(i, onionPos));

            // layer settings
            final int index = i;
            final Coord2D lsPos = vtPos.displace(BUTTON_INC * 3, 0);
            layerElements.add(IconButton.make(ResourceCodes.LAYER_SETTINGS, lsPos,
                    () -> DialogAssembly.setDialogToLayerSettings(index)));

            realBottomY = pos.y + STD_TEXT_BUTTON_H;
        }

        final int boxInPanelY = firstLBPos.y - panelPos.y,
                lbBoxHeight = panelHeight - (boxInPanelY + CONTENT_BUFFER_PX);
        final VerticalScrollBox lbBox = new VerticalScrollBox(firstLBPos,
                new Bounds2D(lbBoxWidth, lbBoxHeight),
                Arrays.stream(layerElements.build().getMenuElements())
                        .map(m -> m instanceof Scrollable
                                ? (Scrollable) m
                                : new Scrollable(m))
                        .toArray(Scrollable[]::new), realBottomY,
                layerButtonYDisplacement(c, layerCount), GameImage::new);

        // frame buttons
        final int frameCount = c.getState().getFrameCount();
        final Scrollable[] frameButtons = new Scrollable[frameCount];

        Coord2D fbPos = firstFBPos;
        FrameButton.logic.reset();

        for (int i = 0; i < frameCount; i++) {
            frameButtons[i] = new Scrollable(new FrameButton(fbPos, i, c));

            fbPos = fbPos.displace(FRAME_BUTTON_W +
                    (i + 1 < frameCount ? BUTTON_OFFSET : 0), 0);
        }

        final int boxInPanelX = firstFBPos.x - panelPos.x,
                fbBoxWidth = panelWidth - (boxInPanelX + CONTENT_BUFFER_PX);
        final HorizontalScrollBox fbBox = new HorizontalScrollBox(firstFBPos,
                new Bounds2D(fbBoxWidth, TOP_PANEL_SCROLL_WINDOW_H + CONTENT_BUFFER_PX),
                frameButtons, fbPos.x, frameButtonXDisplacement(c),
                GameImage::new);
        mb.addAll(lbBox, fbBox);

        // cel buttons
        for (int l = 0; l < layerCount; l++)
            for (int f = 0; f < frameCount; f++)
                mb.add(new CelButton(c, l, f,
                        layerButtons[l], frameButtons[f], lbBox, fbBox));

        // panel adjuster
        mb.add(new VerticalPanelAdjuster(panelPos, panelWidth,
                getFlipbookUpLeeway(),
                getFlipbookDownLeeway(), dh -> {
            changeFlipbookHeight(dh);
            StippleEffect.get().rebuildAllMenus();
        }));

        return mb.build();
    }

    public static IconToggleButton generatePlaybackModeToggle(
            final PlaybackInfo pbi, final Coord2D pos
    ) {
        final PlaybackInfo.Mode[] validModes = new PlaybackInfo.Mode[] {
                PlaybackInfo.Mode.FORWARDS,
                PlaybackInfo.Mode.BACKWARDS,
                PlaybackInfo.Mode.LOOP,
                PlaybackInfo.Mode.PONG_FORWARDS
        };

        final String[] codes = Arrays.stream(validModes).map(
                PlaybackInfo.Mode::getIconCode).toArray(String[]::new);

        return IconToggleButton.make(pos, codes,
                Arrays.stream(validModes).map(mode ->
                        (Runnable) () -> {}).toArray(Runnable[]::new),
                () -> pbi.getMode().buttonIndex(), pbi::cycleMode);
    }

    public static IconToggleButton generatePlayStopToggle(
            final PlaybackInfo pbi, final Coord2D pos
    ) {
        // 0: is playing, button click should STOP; 1: vice-versa
        final String[] codes = new String[]
                { ResourceCodes.STOP, ResourceCodes.PLAY };

        return IconToggleButton.make(pos, codes, new Runnable[] {
                pbi::stop, pbi::play
        }, () -> pbi.isPlaying() ? 0 : 1, () -> {});
    }

    private static int frameButtonXDisplacement(final SEContext c) {
        return (c.getState().getFrameIndex() -
                FRAMES_BEFORE_TO_DISPLAY) *
                (FRAME_BUTTON_W + BUTTON_OFFSET);
    }

    private static MenuElement generateOnionSkinToggle(
            final int index, final Coord2D pos
    ) {
        final SEContext c = StippleEffect.get().getContext();
        final String[] codes = new String[]
                { ResourceCodes.ONION_SKIN_OFF, ResourceCodes.ONION_SKIN_ON };

        return GraphicsUtils.generateIconToggleButton(pos, codes,
                new Runnable[] { () -> {}, () -> {} },
                () -> c.getState().getLayers().get(index).isOnionSkinOn() ? 1 : 0,
                () -> c.getState().getLayers().get(index).toggleOnionSkin(),
                () -> !c.getState().getLayers().get(index).areCelsLinked(),
                ResourceCodes.ONION_SKIN_OFF);
    }

    private static MenuElement generateFramesLinkedToggle(
            final int index, final Coord2D pos
    ) {
        // 0: is unlinked, button click should LINK; 1: vice-versa
        final String[] codes = new String[] {
                ResourceCodes.FRAMES_UNLINKED,
                ResourceCodes.FRAMES_LINKED
        };

        return IconToggleButton.make(pos, codes,
                new Runnable[] {
                        () -> StippleEffect.get().getContext().linkCelsInLayer(index),
                        () -> StippleEffect.get().getContext().unlinkCelsInLayer(index)
                },
                () -> StippleEffect.get().getContext().getState()
                        .getLayers().get(index).areCelsLinked() ? 1 : 0,
                () -> {});
    }

    private static int layerButtonYDisplacement(
            final SEContext c, final int amount
    ) {
        return (amount - ((c.getState().getLayerEditIndex() +
                LAYERS_ABOVE_TO_DISPLAY) + 1)) *
                STD_TEXT_BUTTON_INC;
    }

    private static void populateButtonsIntoBuilder(
            final MenuBuilder mb, final SEAction[] actions,
            final SEContext c, final Coord2D firstPos
    ) {
        Coord2D pos = firstPos;

        for (SEAction action : actions) {
            if (!action.code.equals(ResourceCodes.NONE))
                mb.add(new ActionButton(pos, action, c));

            pos = pos.displace(BUTTON_INC, 0);
        }
    }

    public static Menu buildColorsMenu() {
        final StippleEffect s = StippleEffect.get();
        final MenuBuilder mb = new MenuBuilder();
        final Coord2D panelPos = getColorsPosition();
        final int pw = getColorsWidth(),
                ph = getColorsHeight(),
                leftMargin = panelPos.x + CONTENT_BUFFER_PX,
                leftColumn = panelPos.x + (pw / 4),
                middle = panelPos.x + (pw / 2),
                rightColumn = panelPos.x + ((3 * pw) / 4);

        mb.add(TextLabel.make(panelPos.displace(
                CONTENT_BUFFER_PX, TEXT_Y_OFFSET),
                "Colors"));

        addHidePanelToMenuBuilder(mb, panelPos.displace(pw, 0),
                () -> setColorsPanelShowing(false));

        final int PRI = 0, SEC = 1;

        final int incY = PANEL_TITLE_CONTENT_OFFSET_Y,
                bigIncY = (int)(incY * 1.5), biggestIncY = incY * 2,
                colorLabelY = panelPos.y + incY,
                colorTextboxY = colorLabelY + incY;
        final Color labelColor = ThemeLogic.intuitTextColor(true);

        for (int i = 0; i <= SEC; i++) {
            final String text;
            final int x;

            if (i == PRI) {
                text = "Primary";
                x = leftColumn;
            } else {
                text = "Secondary";
                x = rightColumn;
            }

            // labels
            mb.add(TextLabel.make(new Coord2D(x, colorLabelY), text,
                    labelColor, 1d, MenuElement.Anchor.CENTRAL_TOP));

            // color textboxes
            final Coord2D textBoxPos = new Coord2D(x, colorTextboxY);
            final ColorTextbox colorTextBox = ColorTextbox.make(
                    textBoxPos, MenuElement.Anchor.CENTRAL_TOP, i);
            mb.add(colorTextBox);

            // selected color highlight icon
            final int index = i;
            final Bounds2D dims = new Bounds2D(colorTextBox.getWidth(),
                    colorTextBox.getHeight());
            final GatewayMenuElement highlight = new GatewayMenuElement(
                    new StaticMenuElement(textBoxPos, dims, MenuElement.Anchor.CENTRAL_TOP,
                            GraphicsUtils.drawSelectedTextbox(
                                    new GameImage(dims.width(), dims.height()))),
                    () -> s.getColorIndex() == index);
            mb.add(highlight);
        }

        // swap colors icon
        mb.add(new ActionButton(
                new Coord2D(middle, colorTextboxY + BUTTON_OFFSET),
                MenuElement.Anchor.CENTRAL_TOP, SWAP_COLORS, null));

        // sampler mode choice
        final Coord2D samplerLabelPos = new Coord2D(leftMargin,
                colorTextboxY + bigIncY);
        final TextLabel samplerLabel =
                TextLabel.make(samplerLabelPos, "Sampler");
        final Coord2D samplerDropdownPos = contentPositionAfterLabel(samplerLabel);
        final Dropdown samplerDropdown = new Dropdown(samplerDropdownPos,
                contentWidthAllowance(panelPos.x, pw, samplerDropdownPos.x),
                MenuElement.Anchor.LEFT_TOP,
                ph / 3, Dropdown.DEFAULT_RENDER_ORDER,
                EnumUtils.stream(SamplerMode.class)
                        .map(SamplerMode::toString)
                        .toArray(String[]::new),
                EnumUtils.stream(SamplerMode.class)
                        .map(sm -> (Runnable) () -> setSamplerMode(sm))
                        .toArray(Runnable[]::new),
                () -> getSamplerMode().ordinal());
        mb.addAll(samplerLabel, samplerDropdown);

        // sampler
        final Coord2D samplerStartingPos = new Coord2D(leftMargin,
                samplerLabelPos.y + bigIncY);
        final Map<SamplerMode, MenuElement> samplerContentMap = new HashMap<>();

        // basic RGB sampler content
        final SamplerMode rgb = SamplerMode.RGB_SLIDERS;
        final MenuBuilder rgbBuilder = new MenuBuilder();
        Coord2D compPos = samplerStartingPos;

        for (int i = 0; i < rgb.componentCount(); i++) {
            rgbBuilder.add(rgb.getForIndex(i, compPos));
            compPos = compPos.displace(0, biggestIncY);
        }

        // basic HSV sampler content
        final SamplerMode hsv = SamplerMode.HSV_SLIDERS;
        final MenuBuilder hsvBuilder = new MenuBuilder();
        compPos = samplerStartingPos;

        for (int i = 0; i < hsv.componentCount(); i++) {
            hsvBuilder.add(hsv.getForIndex(i, compPos));
            compPos = compPos.displace(0, biggestIncY);
        }

        // alpha slider - common to all samplers
        final ColorComponent alphaSlider = ColorComponent.alpha(compPos);
        rgbBuilder.add(alphaSlider);
        hsvBuilder.add(alphaSlider);

        samplerContentMap.put(rgb, new MenuElementGrouping(
                rgbBuilder.build().getMenuElements()));
        samplerContentMap.put(hsv, new MenuElementGrouping(
                hsvBuilder.build().getMenuElements()));

        // matrix sampler
        final int matrixWidth = contentWidthAllowance(
                panelPos.x, pw, samplerStartingPos.x);
        final SatValMatrix matrix = new SatValMatrix(samplerStartingPos,
                new Bounds2D(matrixWidth, biggestIncY * 3));

        final Coord2D mHuePos = samplerStartingPos.displace(
                0, matrix.getHeight()),
                mAlphaPos = mHuePos.displace(0, biggestIncY);
        final ColorComponent mHue = ColorComponent.hue(mHuePos),
                mAlpha = ColorComponent.alpha(mAlphaPos);

        samplerContentMap.put(SamplerMode.SV_MATRIX,
                new MenuElementGrouping(matrix, mHue, mAlpha));

        // color wheel
        final int wheelWidth = contentWidthAllowance(
                panelPos.x, pw, samplerStartingPos.x);
        final ColorWheel wheel = new ColorWheel(samplerStartingPos,
                new Bounds2D(wheelWidth, biggestIncY * 3));

        final Coord2D wValuePos = samplerStartingPos.displace(
                0, wheel.getHeight()),
                wAlphaPos = wValuePos.displace(0, biggestIncY);
        final ColorComponent wValue = ColorComponent.value(wValuePos),
                wAlpha = ColorComponent.alpha(wAlphaPos);

        samplerContentMap.put(SamplerMode.COLOR_WHEEL,
                new MenuElementGrouping(wheel, wValue, wAlpha));


        // sampler manager
        mb.add(new SamplerManager(samplerContentMap));

        // palette
        final Coord2D paletteStartingPos = wAlphaPos
                .displace(0, incY + bigIncY);
        final TextLabel paletteLabel =
                TextLabel.make(paletteStartingPos, "Palette");
        final Coord2D paletteDropdownPos =
                contentPositionAfterLabel(paletteLabel);
        final int paletteDropdownH = ph - (
                (paletteDropdownPos.y - panelPos.y) +
                        CONTENT_BUFFER_PX + STD_TEXT_BUTTON_H),
                paletteDropdownW = contentWidthAllowance(
                        panelPos.x, pw, paletteDropdownPos.x);
        final List<Palette> palettes = s.getPalettes();
        final boolean hasPaletteContents = s.hasPaletteContents();
        final MenuElement paletteDropdown = hasPaletteContents
                ? new Dropdown(paletteDropdownPos,
                paletteDropdownW, MenuElement.Anchor.LEFT_TOP,
                paletteDropdownH, Dropdown.DEFAULT_RENDER_ORDER,
                palettes.stream().map(Palette::getName).toArray(String[]::new),
                IntStream.range(0, palettes.size())
                        .mapToObj(i -> (Runnable) () -> s.setPaletteIndex(i))
                        .toArray(Runnable[]::new), s::getPaletteIndex)
                : new StaticMenuElement(paletteDropdownPos,
                new Bounds2D(paletteDropdownW, STD_TEXT_BUTTON_H),
                MenuElement.Anchor.LEFT_TOP,
                Settings.getTheme().logic.drawTextButton(
                        TextButton.of("No palettes", paletteDropdownW,
                                Alignment.CENTER, ButtonType.STUB)));

        mb.addAll(paletteLabel, paletteDropdown);

        // palette buttons
        final Coord2D paletteButtonPos = paletteStartingPos.displace(0, bigIncY);
        populateButtonsIntoBuilder(mb, paletteActions(), null, paletteButtonPos);

        // palette colors
        if (hasPaletteContents) {
            final Coord2D palettePos = paletteButtonPos.displace(0, incY);
            final int paletteW = contentWidthAllowance(
                    panelPos.x, pw, palettePos.x),
                    paletteH = ph - ((palettePos.y - panelPos.y) +
                            CONTENT_BUFFER_PX);

            mb.add(PaletteContainer.make(palettePos,
                    new Bounds2D(paletteW, paletteH), s.getSelectedPalette()));
        }

        return mb.build();
    }

    private static int contentWidthAllowance(
            final int panelX, final int panelWidth, final int x
    ) {
        final int xInPanel = x - panelX;

        return (panelWidth - CONTENT_BUFFER_PX) - xInPanel;
    }

    private static void addHidePanelToMenuBuilder(
            final MenuBuilder mb, final Coord2D topRight,
            final Runnable onClick
    ) {
        final Coord2D pos = topRight.displace(-BUTTON_INC,
                ICON_BUTTON_OFFSET_Y);
        mb.add(IconButton.make(ResourceCodes.HIDE_PANEL, pos,
                () -> adjustPanels(onClick)));
    }

    public static Menu buildToolButtonMenu() {
        final MenuBuilder mb = new MenuBuilder();
        final SEContext c = StippleEffect.get().getContext();

        final Tool[] all = Tool.getAll();
        for (int i = 0; i < all.length; i++) {
            mb.add(toolButtonFromTool(all[i], i));
        }

        addHidePanelToMenuBuilder(mb, getToolsPosition()
                        .displace(getToolsWidth(), 0),
                () -> setToolbarShowing(false));

        // outline button
        final Coord2D outlinePos = getToolsPosition()
                .displace(BUTTON_OFFSET,
                        getToolsHeight() - BUTTON_INC);
        mb.add(new ActionButton(outlinePos, CONFIGURE_OUTLINE, c));

        // reflection buttons
        final MenuElement verticalReflectionButton = GraphicsUtils.
                generateIconButton(ResourceCodes.VERTICAL_REFLECTION,
                        outlinePos.displace(0, -BUTTON_INC),
                        () -> c.getState().hasSelection(), () -> {
                            if (c.getState().getSelectionMode() == SelectionMode.BOUNDS)
                                VERT_BOUNDS_REFLECTION.behaviour.accept(c);
                            else
                                VERT_CONTENTS_REFLECTION.behaviour.accept(c);
                        }
                );
        mb.add(verticalReflectionButton);
        final MenuElement horizontalReflectionButton = GraphicsUtils.
                generateIconButton(ResourceCodes.HORIZONTAL_REFLECTION,
                        outlinePos.displace(0, -2 * BUTTON_INC),
                        () -> c.getState().hasSelection(), () -> {
                            if (c.getState().getSelectionMode() == SelectionMode.BOUNDS)
                                HORZ_BOUNDS_REFLECTION.behaviour.accept(c);
                            else
                                HORZ_CONTENTS_REFLECTION.behaviour.accept(c);
                        });
        mb.add(horizontalReflectionButton);

        // pixel grid
        final MenuElement pixelGridToggleButton = GraphicsUtils
                .generateIconToggleButton(
                        outlinePos.displace(0, -3 * BUTTON_INC),
                        new String[] {
                                ResourceCodes.PIXEL_GRID_OFF,
                                ResourceCodes.PIXEL_GRID_ON
                        },
                        new Runnable[] {
                                () -> c.renderInfo.setPixelGrid(true),
                                () -> c.renderInfo.setPixelGrid(false)
                        },
                        () -> c.renderInfo.isPixelGridOn() ? 1 : 0, () -> {},
                        c::couldRenderPixelGrid, ResourceCodes.PIXEL_GRID_OFF);
        mb.add(pixelGridToggleButton);

        final Tool tool = StippleEffect.get().getTool();

        if (tool.hasToolOptionsBar())
            mb.add(tool.buildToolOptionsBar());

        return mb.build();
    }

    private static SimpleMenuButton toolButtonFromTool(
            final Tool tool, final int index
    ) {
        final Coord2D position = getToolsPosition().displace(
                BUTTON_OFFSET, BUTTON_OFFSET +
                        (BUTTON_INC * (index + 1)));

        final boolean selected = StippleEffect.get().getTool().equals(tool);
        final GameImage icon = selected
                ? tool.getSelectedIcon() : tool.getIcon();

        return new IconButton(position, MenuElement.Anchor.LEFT_TOP,
                () -> StippleEffect.get().setTool(tool),
                tool.convertNameToFilename(), icon, tool.getHighlightedIcon());
    }

    public static Menu buildBottomBarMenu() {
        final MenuBuilder mb = new MenuBuilder();
        final SEContext c = StippleEffect.get().getContext();

        final int bottomBarTextY = getBottomBarPosition().y +
                TEXT_Y_OFFSET,
                bottomBarButtonY = getBottomBarPosition().y +
                        BUTTON_OFFSET;

        // active tool
        final Indicator toolIndicator = new Indicator(new Coord2D(
                BUTTON_OFFSET, bottomBarButtonY),
                ResourceCodes.IND_TOOL);
        final DynamicLabel toolLabel = DynamicLabel.make(new Coord2D(
                optionsBarNextElementX(toolIndicator, false),
                bottomBarTextY),
                () -> StippleEffect.get().getTool().getBottomBarText(),
                getBottomBarToolWidth());
        mb.addAll(toolIndicator, toolLabel);

        // target pixel
        final Indicator targetIndicator = new Indicator(new Coord2D(
                getBottomBarTargetPixelX(), bottomBarButtonY),
                ResourceCodes.IND_TARGET);
        final DynamicLabel targetLabel = DynamicLabel.make(new Coord2D(
                optionsBarNextElementX(targetIndicator, false),
                bottomBarTextY),
                c::getTargetPixelText, getBottomBarTargetPixelWidth());
        mb.addAll(targetIndicator, targetLabel);

        // canvas size
        final Indicator boundsIndicator = new Indicator(new Coord2D(
                getBottomBarCanvasSizeX(), bottomBarButtonY),
                ResourceCodes.IND_BOUNDS);
        final DynamicLabel boundsLabel = DynamicLabel.make(new Coord2D(
                optionsBarNextElementX(boundsIndicator, false),
                bottomBarTextY),
                c::getImageSizeText, getBottomBarCanvasSizeWidth());
        mb.addAll(boundsIndicator, boundsLabel);

        // zoom
        final Indicator zoomIndicator = new Indicator(
                new Coord2D(getBottomBarZoomPercentageX(),
                        bottomBarButtonY), ResourceCodes.IND_ZOOM);
        final IncrementalRangeElements<Float> zoom =
                IncrementalRangeElements.makeForFloat(zoomIndicator,
                        getBottomBarPosition().y + BUTTON_OFFSET,
                        bottomBarTextY,
                        () -> c.renderInfo.zoomOut(Constants.NO_VALID_TARGET),
                        () -> c.renderInfo.zoomIn(Constants.NO_VALID_TARGET),
                        ZoomLevel.MIN.z, ZoomLevel.MAX.z,
                        z -> c.renderInfo.setZoomLevel(ZoomLevel.fromZ(z)),
                        c.renderInfo::getZoomFactor,
                        z -> ZoomLevel.fromZ(z).ordinal(),
                        sv -> ZoomLevel.values()[sv].z,
                        f -> c.renderInfo.getZoomText(), "XXX.XX%");
        mb.addAll(zoomIndicator, zoom.decButton, zoom.incButton,
                zoom.slider, zoom.value);

        // selection
        mb.add(DynamicLabel.make(new Coord2D(width() -
                CONTENT_BUFFER_PX, bottomBarTextY),
                MenuElement.Anchor.RIGHT_TOP, c::getSelectionText,
                width() -
                (getBottomBarZoomSliderX() + getUISliderWidth())));

        return mb.build();
    }
}
