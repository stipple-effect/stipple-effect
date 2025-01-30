package com.jordanbunke.stipple_effect;

import com.jordanbunke.delta_time.OnStartup;
import com.jordanbunke.delta_time._core.GameManager;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.DebugChannel;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.DeltaTimeGlobal;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.stipple_effect.layer.OnionSkin;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.preview.EmbeddedPreview;
import com.jordanbunke.stipple_effect.preview.Preview;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.stip.ParserSerializer;
import com.jordanbunke.stipple_effect.tools.*;
import com.jordanbunke.stipple_effect.utility.*;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.action.SEAction;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;
import com.jordanbunke.stipple_effect.utility.math.StitchSplitMath;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.*;
import com.jordanbunke.stipple_effect.visual.theme.SEColors;
import com.jordanbunke.stipple_effect.visual.theme.Theme;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static com.jordanbunke.stipple_effect.utility.action.SEAction.*;

public class StippleEffect implements ProgramContext {
    public static String
            PROGRAM_NAME = "Stipple Effect",
            NATIVE_STANDARD = "1.0", PALETTE_STANDARD = "1.0";
    private static Version VERSION = new Version(1, 0, 0);
    private static boolean IS_DEVBUILD = false;

    public static final int PRIMARY = 0, SECONDARY = 1;

    private static final String STATUS_UPDATE_CHANNEL = "Status";

    private static final StippleEffect INSTANCE;

    public final Program program;
    public GameWindow window;
    private boolean windowed;

    // projects / contexts
    private final List<SEContext> contexts;
    private int contextIndex;
    private Menu projectsMenu;
    private final List<Path> toImport;

    // tools
    private Menu toolButtonMenu;
    private Tool tool;

    // colors
    private final Color[] colors;
    private int colorIndex;
    private final List<Palette> palettes;
    private int paletteIndex;
    private Menu colorsMenu;

    // flipbook
    private Menu flipbookMenu;

    // bottom bar
    private Menu bottomBarMenu;

    // dialog
    private Menu dialog;

    // cursor
    private Coord2D mousePos;

    // status update
    private int millisSinceStatusUpdate;
    private GameImage statusUpdate;

    // tool tip
    private int toolTipMillisCounter;
    private String toolTipCode, provisionalToolTipCode;
    private GameImage toolTip;

    // timer
    private int timerCounter, timerMillis;
    private boolean timerToggle;

    /**
     * This exists to ensure that the standard execution thread executes
     * the jobs that are scheduled, thus avoiding race conditions etc.
     * */
    private final Queue<Runnable> jobScheduler;

    static {
        OnStartup.run();
        Settings.read();
        readProgramFile();
        SEInterpreter.printErrorsToDialog();

        INSTANCE = new StippleEffect();

        DialogAssembly.setDialogToSplashScreen();

        // initially declared as empty method because
        // builders rely on calls to INSTANCE object
        INSTANCE.rebuildAllMenus();

        ToolWithBreadth.redrawToolOverlays();
    }

    private static void readProgramFile() {
        final String[] programFile = FileIO.readResource(ResourceLoader
                .loadResource(Constants.PROGRAM_FILE), "prg").split("\n");

        for (String line : programFile) {
            final String[] codeAndValue = ParserUtils.splitIntoCodeAndValue(line);

            if (codeAndValue.length != ParserUtils.DESIRED)
                continue;

            final String code = codeAndValue[ParserUtils.CODE],
                    value = codeAndValue[ParserUtils.VALUE];

            switch (code) {
                case Constants.NAME_CODE -> PROGRAM_NAME = value;
                case Constants.VERSION_CODE -> {
                    try {
                        final Integer[] components = Arrays
                                .stream(value.split("\\."))
                                .map(Integer::parseInt).toArray(Integer[]::new);

                        final int MAJOR = 0, MINOR = 1, PATCH = 2,
                                BUILD = 3, HAS_BUILD_LENGTH = 4;

                        if (components.length == HAS_BUILD_LENGTH)
                            VERSION = new Version(components[MAJOR],
                                    components[MINOR], components[PATCH],
                                    components[BUILD]);
                        else if (components.length > PATCH)
                            VERSION = new Version(components[MAJOR],
                                    components[MINOR], components[PATCH]);
                    } catch (NumberFormatException e) {
                        GameError.send("Could not read program version from data file.");
                    }
                }
                case Constants.IS_DEVBUILD_CODE ->
                        IS_DEVBUILD = Boolean.parseBoolean(value);
                case Constants.NATIVE_STANDARD_CODE ->
                        NATIVE_STANDARD = value;
                case Constants.PALETTE_STANDARD_CODE ->
                        PALETTE_STANDARD = value;
            }
        }

        if (IS_DEVBUILD) {
            VERSION.incrementBuild();

            final Path toSave = Path.of("res").resolve(Constants.PROGRAM_FILE);

            final String write = ParserUtils.encloseSetting(
                    Constants.NAME_CODE, PROGRAM_NAME) +
                    ParserUtils.encloseSetting(Constants.VERSION_CODE,
                            VERSION.toString()) +
                    ParserUtils.encloseSetting(Constants.IS_DEVBUILD_CODE,
                            String.valueOf(IS_DEVBUILD)) +
                    ParserUtils.encloseSetting(
                            Constants.NATIVE_STANDARD_CODE, NATIVE_STANDARD) +
                    ParserUtils.encloseSetting(
                            Constants.PALETTE_STANDARD_CODE, PALETTE_STANDARD);

            FileIO.writeFile(toSave, write);
        }
    }

    public static String getVersion() {
        return "v" + VERSION + (IS_DEVBUILD ? " (devbuild)" : "");
    }

    public StippleEffect() {
        mousePos = new Coord2D();

        contexts = new ArrayList<>(List.of(new SEContext(
                Settings.getDefaultCanvasWPixels(),
                Settings.getDefaultCanvasHPixels())));
        contextIndex = 0;
        toImport = new ArrayList<>();

        // default tool is the hand
        tool = Hand.get();

        // colors
        colors = new Color[2];
        colors[PRIMARY] = SEColors.black();
        colors[SECONDARY] = SEColors.white();
        colorIndex = PRIMARY;

        palettes = new ArrayList<>();
        paletteIndex = Constants.NO_SELECTION;

        // menu stubs
        toolButtonMenu = MenuAssembly.stub();
        bottomBarMenu = MenuAssembly.stub();
        colorsMenu = MenuAssembly.stub();
        flipbookMenu = MenuAssembly.stub();
        projectsMenu = MenuAssembly.stub();

        dialog = null;

        windowed = !Settings.isFullscreenOnStartup();
        window = makeWindow();
        final GameManager manager = new GameManager(0, this);

        program = new Program(window, manager, Constants.TICK_HZ, Constants.FPS);
        program.setCanvasSize(Layout.width(), Layout.height());
        program.setScheduleUpdates(false);

        millisSinceStatusUpdate = 0;
        statusUpdate = GameImage.dummy();

        toolTipMillisCounter = 0;
        provisionalToolTipCode = ResourceCodes.NONE;
        toolTipCode = ResourceCodes.NONE;
        toolTip = GameImage.dummy();

        timerCounter = 0;
        timerToggle = false;
        timerMillis = 0;

        jobScheduler = new LinkedList<>();

        configureDebugger();
    }

    public static StippleEffect get() {
        return INSTANCE;
    }

    public static void main(final String[] args) {
        if (args.length > 0)
            get().launchWithFile(Path.of(Arrays.stream(args).reduce("",
                    (a, b) -> a.length() == 0 ? b : a + " " + b).trim()));
    }

    private void launchWithFile(final Path filepath) {
        jobScheduler.add(() -> verifyFilepath(filepath));
    }

    private void configureDebugger() {
        final GameDebugger debugger = program.getDebugger();

        debugger.muteChannel(GameDebugger.FRAME_RATE);
        debugger.addChannel(new DebugChannel(STATUS_UPDATE_CHANNEL,
                this::updateStatus, false, false));
    }

    public void sendStatusUpdate(final String message) {
        program.getDebugger().getChannel(STATUS_UPDATE_CHANNEL)
                .printMessage(message);
    }

    private void updateStatus(final String message) {
        millisSinceStatusUpdate = 0;

        final GameImage text = ParserUtils.generateStatusEffectText(message);
        final int w = text.getWidth() + (4 * Layout.BUTTON_BORDER_PX),
                h = text.getHeight() - (4 * Layout.BUTTON_BORDER_PX);

        final GameImage statusUpdate = new GameImage(w, h);
        statusUpdate.fillRectangle(
                Settings.getTheme().panelBackground, 0, 0, w, h);
        statusUpdate.draw(text, 2 * Layout.BUTTON_BORDER_PX,
                Layout.TEXT_Y_OFFSET);

        this.statusUpdate = statusUpdate.submit();
    }

    public void sendToolTipUpdate(final String code) {
        provisionalToolTipCode = code;
    }

    private GameWindow makeWindow() {
        final Bounds2D size = determineWindowSize();
        Layout.setSize(size.width(), size.height());
        final GameWindow window = new GameWindow(
                PROGRAM_NAME + " [Demo] " + getVersion(),
                size.width(), size.height(),
                GraphicsUtils.readIconAsset(ResourceCodes.PROGRAM),
                true, false, !windowed);
        window.hideCursor();
        return window;
    }

    private Bounds2D determineWindowSize() {
        final int screenW = Toolkit.getDefaultToolkit().getScreenSize().width,
                screenH = Toolkit.getDefaultToolkit().getScreenSize().height;

        return windowed ? new Bounds2D(
                Settings.getWindowedWidth(), Settings.getWindowedHeight())
                : new Bounds2D(screenW, screenH);
    }

    public void rebuildAllMenus() {
        rebuildStateDependentMenus();
        rebuildColorsMenu();
        rebuildToolButtonMenu();
    }

    public void rebuildStateDependentMenus() {
        rebuildFlipbookMenu();
        rebuildProjectsMenu();
        rebuildBottomBarMenu();
    }

    public void rebuildColorsMenu() {
        colorsMenu = Layout.isColorsPanelShowing()
                ? MenuAssembly.buildColorsMenu() : MenuAssembly.stub();
    }

    public void rebuildToolButtonMenu() {
        toolButtonMenu = Layout.isToolbarShowing()
                ? MenuAssembly.buildToolButtonMenu() : MenuAssembly.stub();
    }

    public void rebuildBottomBarMenu() {
        bottomBarMenu = MenuAssembly.buildBottomBarMenu();
    }

    public void rebuildFlipbookMenu() {
        flipbookMenu = Layout.isFlipbookPanelShowing()
                ? MenuAssembly.buildFlipbookMenu() : MenuAssembly.stub();
    }

    public void rebuildProjectsMenu() {
        projectsMenu = MenuAssembly.buildProjectsMenu();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        mousePos = eventLogger.getAdjustedMousePosition();
        DeltaTimeGlobal.setStatus(DeltaTimeGlobal.SC_CURSOR_CAPTURED, null);
        SECursor.resetCursorCode();

        if (dialog == null) {
            if (!Permissions.isTyping())
                processNonStateKeyPresses(eventLogger);

            final boolean doBehindPreview;

            if (Preview.get() instanceof EmbeddedPreview ep) {
                if (ep.inBounds(mousePos)) {
                    ep.process(eventLogger);
                    doBehindPreview = false;
                } else {
                    ep.processExternal(eventLogger);
                    doBehindPreview = true;
                }
            } else
                doBehindPreview = true;

            if (doBehindPreview) {
                // projects
                projectsMenu.process(eventLogger);
                // colors
                colorsMenu.process(eventLogger);
                // tools
                toolButtonMenu.process(eventLogger);
                // flipbook
                flipbookMenu.process(eventLogger);
                // bottom bar
                bottomBarMenu.process(eventLogger);

                // workspace
                getContext().process(eventLogger);
            }
        } else {
            dialog.process(eventLogger);
            CLEAR_DIALOG.doForMatchingKeyStroke(eventLogger, null);
        }
    }

    private void processNonStateKeyPresses(final InputEventLogger eventLogger) {
        SETTINGS.doForMatchingKeyStroke(eventLogger, null);
        INFO.doForMatchingKeyStroke(eventLogger, null);
        AUTOMATION_SCRIPT.doForMatchingKeyStroke(eventLogger, null);

        NEW_PROJECT.doForMatchingKeyStroke(eventLogger, null);
        OPEN_FILE.doForMatchingKeyStroke(eventLogger, null);

        NEW_PALETTE.doForMatchingKeyStroke(eventLogger, null);
        SAVE_PALETTE.tryForMatchingKeyStroke(eventLogger, null);
        SORT_PALETTE.tryForMatchingKeyStroke(eventLogger, null);
        PALETTE_SETTINGS.tryForMatchingKeyStroke(eventLogger, null);

        ADD_TO_PALETTE.tryForMatchingKeyStroke(eventLogger, null);
        MOVE_COLOR_LEFT.doForMatchingKeyStroke(eventLogger, null);
        MOVE_COLOR_RIGHT.doForMatchingKeyStroke(eventLogger, null);
        SELECT_COLOR_LEFT.doForMatchingKeyStroke(eventLogger, null);
        SELECT_COLOR_RIGHT.doForMatchingKeyStroke(eventLogger, null);

        PANEL_MANAGER.doForMatchingKeyStroke(eventLogger, null);
        TOGGLE_PANELS.doForMatchingKeyStroke(eventLogger, null);

        NEW_FONT.tryForMatchingKeyStroke(eventLogger, null);
        TOGGLE_ALIGNMENT.tryForMatchingKeyStroke(eventLogger, null);
        SWAP_COLORS.doForMatchingKeyStroke(eventLogger, null);

        // represents both fullscreen and windowed as calls are identical
        FULLSCREEN.doForMatchingKeyStroke(eventLogger, null);

        // quick context select
        for (SEAction qs : quickSelectActions())
            qs.doForMatchingKeyStroke(eventLogger, null);

        // set tools
        for (SEAction setTool : setToolActions())
            setTool.doForMatchingKeyStroke(eventLogger, null);
    }

    @Override
    public void update(final double deltaTime) {
        provisionalToolTipCode = ResourceCodes.NONE;

        while (!jobScheduler.isEmpty())
            jobScheduler.poll().run();

        if (dialog == null) {
            getContext().animate(deltaTime);

            final int millisThisTick = (int)
                    (Constants.MILLIS_IN_SECOND / Constants.TICK_HZ);

            // system timer
            timerMillis += millisThisTick;

            if (timerMillis >= Constants.MILLIS_PER_TIMER_INC) {
                timerMillis -= Constants.MILLIS_PER_TIMER_INC;

                timerCounter++;

                if (timerCounter >= Constants.TIMER_MAX_OUT) {
                    timerCounter = 0;
                    timerToggle = !timerToggle;
                }
            }

            // status update
            millisSinceStatusUpdate += millisThisTick;

            // bottom bar
            bottomBarMenu.update(deltaTime);
            // tools
            toolButtonMenu.update(deltaTime);
            // colors
            colorsMenu.update(deltaTime);
            // projects
            projectsMenu.update(deltaTime);
            // flipbook
            flipbookMenu.update(deltaTime);

            // preview
            if (Preview.get() instanceof EmbeddedPreview ep)
                ep.update(deltaTime);
        } else {
            dialog.update(deltaTime);
        }

        // tool tip update
        updateToolTip();
    }

    private void updateToolTip() {
        if (!provisionalToolTipCode.equals(ResourceCodes.NONE) &&
                provisionalToolTipCode.equals(toolTipCode)) {
            toolTipMillisCounter +=
                    (int) (Constants.MILLIS_IN_SECOND / Constants.TICK_HZ);

            if (toolTipMillisCounter == Constants.TOOL_TIP_MILLIS_THRESHOLD)
                toolTip = drawToolTip();
        } else {
            toolTipMillisCounter = 0;
            toolTipCode = provisionalToolTipCode;
        }
    }

    private GameImage drawToolTip() {
        final Theme t = Settings.getTheme();
        final Color cText = ThemeLogic.intuitTextColor(t.panelBackground, true);

        final String[] lines = ParserUtils.getToolTip(toolTipCode);
        final TextBuilder tb = GraphicsUtils.uiText(cText);

        for (int l = 0; l < lines.length; l++) {
            final String[] segments = ParserUtils.extractHighlight(lines[l]);

            for (int i = 0; i < segments.length; i++) {
                final String text;

                if (i % 2 == 0) {
                    tb.setColor(cText);
                    text = segments[i];
                } else {
                    tb.setColor(t.textShortcut);
                    text = ParserUtils.getShortcut(segments[i]);
                }

                tb.addText(text);
            }

            if (l + 1 < lines.length)
                tb.addLineBreak();
        }

        final GameImage text = tb.build().draw();
        final int w = text.getWidth() + (6 * Layout.BUTTON_BORDER_PX),
                h = text.getHeight() - (6 * Layout.BUTTON_BORDER_PX);

        final GameImage tt = new GameImage(w, h);
        tt.fillRectangle(t.panelBackground, 0, 0, w, h);
        tt.drawRectangle(t.panelDivisions, 2f, 1, 1, w - 2, h - 2);
        tt.draw(text, 3 * Layout.BUTTON_BORDER_PX, Layout.TEXT_Y_OFFSET);

        return tt.submit();
    }

    @Override
    public void render(final GameImage canvas) {
        final Coord2D wp = Layout.getWorkspacePosition(),
                tp = Layout.getToolsPosition(),
                tobp = Layout.getToolOptionsBarPosition(),
                cp = Layout.getColorsPosition(),
                pp = Layout.getProjectsPosition(),
                fbp = Layout.getFlipbookPosition(),
                bbp = Layout.getBottomBarPosition();

        // panel backgrounds

        canvas.draw(drawBottomBar(), bbp.x, bbp.y);

        if (Layout.isToolbarShowing()) {
            canvas.draw(drawTools(), tp.x, tp.y);

            if (tool.hasToolOptionsBar())
                canvas.draw(drawToolOptionsBar(), tobp.x, tobp.y);
        }

        if (Layout.isColorsPanelShowing())
            canvas.draw(drawColorsSegment(), cp.x, cp.y);

        canvas.draw(drawProjects(), pp.x, pp.y);

        if (Layout.isFlipbookPanelShowing())
            canvas.draw(drawFlipbook(), fbp.x, fbp.y);

        // workspace
        final GameImage workspace = getContext().drawWorkspace();
        canvas.draw(workspace, wp.x, wp.y);

        // borders
        final float strokeWidth = 2f;

        canvas.setColor(Settings.getTheme().panelDivisions);
        canvas.drawLine(strokeWidth, pp.x, tobp.y, Layout.width(), tobp.y); // top segments and middle separation
        canvas.drawLine(strokeWidth, tp.x, wp.y, cp.x, wp.y); // tool options bar and tools/workspace separation
        canvas.drawLine(strokeWidth, bbp.x, bbp.y, Layout.width(), bbp.y); // middle segments and bottom bar separation
        canvas.drawLine(strokeWidth, wp.x, wp.y, wp.x, bbp.y); // tools and workspace separation
        canvas.drawLine(strokeWidth, wp.x, fbp.y, cp.x, fbp.y); // workspace and flipbook separation
        canvas.drawLine(strokeWidth, cp.x, cp.y, cp.x, bbp.y); // workspace/option bar and right segments separation

        if (millisSinceStatusUpdate < Constants.STATUS_UPDATE_DURATION_MILLIS)
            canvas.draw(statusUpdate, wp.x, wp.y);

        // bottom bar - zoom, animation
        bottomBarMenu.render(canvas);

        // flipbook
        flipbookMenu.render(canvas);

        // tools
        toolButtonMenu.render(canvas);

        // colors
        colorsMenu.render(canvas);

        // projects / contexts
        projectsMenu.render(canvas);

        // preview
        if (Preview.get() instanceof EmbeddedPreview ep)
            ep.render(canvas);

        if (dialog != null) {
            canvas.fillRectangle(Settings.getTheme().dialogVeil,
                    0, 0, Layout.width(), Layout.height());
            dialog.render(canvas);
        }

        // tool tip
        if (toolTipMillisCounter >= Constants.TOOL_TIP_MILLIS_THRESHOLD) {
            final boolean leftSide = mousePos.x <= Layout.getCanvasMiddle().x,
                    atBottom = mousePos.y > Layout.height() - (2 * toolTip.getHeight());
            final int x = mousePos.x + (leftSide
                    ? Layout.TOOL_TIP_OFFSET : -toolTip.getWidth()),
                    y = mousePos.y + (atBottom
                            ? -toolTip.getHeight() : Layout.TOOL_TIP_OFFSET);

            canvas.draw(toolTip, x, y);
        }

        // cursor
        final String cursorCode = SECursor.systemCursorCode();
        final GameImage cursor = SECursor.fetchCursor(
                getContext().isInWorkspaceBounds() && dialog == null &&
                        Permissions.isCursorFree() &&
                        cursorCode.equals(SECursor.MAIN_CURSOR)
                        ? tool.getCursorCode() : cursorCode);
        canvas.draw(cursor, mousePos.x - (Layout.CURSOR_DIM / 2),
                mousePos.y - (Layout.CURSOR_DIM / 2));
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    private GameImage drawColorsSegment() {
        return drawPanel(Layout.getColorsWidth(),
                Layout.getColorsHeight());
    }

    private GameImage drawTools() {
        return drawPanel(Layout.getToolsWidth(),
                Layout.getToolsHeight());
    }

    private GameImage drawToolOptionsBar() {
        return drawPanel(Layout.getToolOptionsBarWidth(),
                Layout.TOOL_OPTIONS_BAR_H);
    }

    private GameImage drawProjects() {
        return drawPanel(Layout.getProjectsWidth(),
                Layout.getTopPanelHeight());
    }

    private GameImage drawFlipbook() {
        return drawPanel(Layout.getFlipbookWidth(),
                Layout.getFlipbookHeight());
    }

    private GameImage drawBottomBar() {
        return drawPanel(Layout.width(), Layout.BOTTOM_BAR_H);
    }

    private GameImage drawPanel(final int w, final int h) {
        final GameImage panel = new GameImage(w, h);
        panel.fill(Settings.getTheme().panelBackground);

        return panel.submit();
    }

    public SEContext getContext() {
        return contexts.get(contextIndex);
    }

    public List<SEContext> getContexts() {
        return contexts;
    }

    public int getContextIndex() {
        return contextIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public Color getSelectedColor() {
        return colors[colorIndex];
    }

    public Color getPrimary() {
        return colors[PRIMARY];
    }

    public Color getSecondary() {
        return colors[SECONDARY];
    }

    public Color getColorAtIndex(final int index) {
        return colors[index];
    }

    public int getPaletteIndex() {
        return paletteIndex;
    }

    public List<Palette> getPalettes() {
        return palettes;
    }

    public Tool getTool() {
        return tool;
    }

    public int getTimerCounter() {
        return timerCounter;
    }

    public boolean isTimerToggle() {
        return timerToggle;
    }

    public boolean isWindowed() {
        return windowed;
    }

    public void newProject() {
        addContext(new SEContext(DialogVals.getNewProjectWidth(),
                DialogVals.getNewProjectHeight()), true);
    }

    public void newPalette() {
        addPalette(new Palette("Palette " +
                (palettes.size() + 1), new Color[] {}), true);
    }

    public void openProject() {
        FileIO.setDialogToFilesOnly();
        final String[] acceptedFileTypes = Stream.concat(
                Stream.of(SaveConfig.SaveType.NATIVE.getFileSuffix()),
                Arrays.stream(Constants.ACCEPTED_RASTER_IMAGE_SUFFIXES)
        ).toArray(String[]::new);

        final Optional<File[]> opened = FileIO.openFilesFromSystem(
                new String[] { "Accepted file types" },
                new String[][] { acceptedFileTypes });
        window.getEventLogger().unpressAllKeys();

        if (opened.isEmpty())
            return;

        final Path[] filepaths = Arrays.stream(opened.get())
                .map(File::toPath).toArray(Path[]::new);

        if (filepaths.length > 0) {
            toImport.clear();
            toImport.addAll(Arrays.asList(filepaths).subList(1, filepaths.length));

            verifyFilepath(filepaths[0]);
        }
    }

    public void openFontTemplateProjects() {
        final String suffix = "." + Constants.NATIVE_FILE_SUFFIX;
        final Path folder = SEFonts.FONT_FOLDER.resolve("templates"),
                asciiPath = folder.resolve(Constants.ASCII_TEMPLATE + suffix),
                latinExtendedPath = folder.resolve(Constants.LATIN_EXTENDED_TEMPLATE + suffix);

        final String ascii = FileIO.readResource(
                ResourceLoader.loadResource(asciiPath), "ASCII Template"),
                latinExtended = FileIO.readResource(
                        ResourceLoader.loadResource(latinExtendedPath),
                        "Latin Extended Template");

        openNativeProject(ascii, asciiPath);
        openNativeProject(latinExtended, latinExtendedPath);

        clearDialog();
    }

    public void openPalette() {
        FileIO.setDialogToFilesOnly();
        final Optional<File> opened = FileIO.openFileFromSystem(
                new String[] {
                        PROGRAM_NAME + " palettes (." + Constants.PALETTE_FILE_SUFFIX + ")"
                },
                new String[][] {
                        new String[] { Constants.PALETTE_FILE_SUFFIX }
                });
        window.getEventLogger().unpressAllKeys();

        if (opened.isEmpty())
            return;

        verifyFilepath(opened.get().toPath());
    }

    public void openAutomationScript() {
        final Path filepath = openScript();

        if (filepath == null)
            return;

        final String content = FileIO.readFile(filepath);
        SEInterpreter.get().runAutomationScript(content, filepath);
    }

    public void openColorScript() {
        final Path filepath = openScript();

        if (filepath == null)
            return;

        final HeadFuncNode script =
                SEInterpreter.get().build(FileIO.readFile(filepath));
        DialogVals.setColorScript(script);
    }

    public Path openScript() {
        FileIO.setDialogToFilesOnly();
        final Optional<File> opened = FileIO.openFileFromSystem(
                new String[] {
                        StippleEffect.PROGRAM_NAME + " scripts (." +
                                Constants.SCRIPT_FILE_SUFFIX + ")"
                },
                new String[][] {
                        new String[] { Constants.SCRIPT_FILE_SUFFIX }
                });
        window.getEventLogger().unpressAllKeys();

        if (opened.isEmpty())
            return null;

        return opened.get().toPath();
    }

    private void verifyFilepath(final Path filepath) {
        final String fileName = filepath.getFileName().toString();

        if (fileName.endsWith(SaveConfig.SaveType.NATIVE.getFileSuffix())) {
            final String contents = FileIO.readFile(filepath);
            openNativeProject(contents, filepath);

            processNextImport();
        } else if (isAcceptedRasterFormat(fileName)) {
            final GameImage image = GameImageIO.readImage(filepath);

            DialogAssembly.setDialogToOpenPNG(image, filepath);
        } else if (fileName.endsWith(Constants.PALETTE_FILE_SUFFIX)) {
            final String file = FileIO.readFile(filepath);

            if (file != null)
                addPalette(ParserSerializer.loadPalette(file), true);
            else
                StatusUpdates.openFailed(filepath);
        }
    }

    public void openNativeProject(final String contents, final Path filepath) {
        if (contents != null) {
            final SEContext project = ParserSerializer.load(contents, filepath);
            addContext(project, true);
        } else
            StatusUpdates.openFailed(filepath);
    }

    private static boolean isAcceptedRasterFormat(final String toCheck) {
        for (String suffix : Constants.ACCEPTED_RASTER_IMAGE_SUFFIXES)
            if (toCheck.endsWith(suffix))
                return true;

        return false;
    }

    public void newProjectFromFile(
            final GameImage image, final Path filepath
    ) {
        final int iw = DialogVals.getImportWidth(),
                ih = DialogVals.getImportHeight(),
                fw = DialogVals.getImportFrameWidth(),
                fh = DialogVals.getImportFrameHeight(),
                columns = StitchSplitMath.importColumns(iw),
                rows = StitchSplitMath.importRows(ih),
                frameCount = columns * rows;

        final GameImage resized = ImageProcessing.scale(image, iw, ih);
        final List<GameImage> frames = new ArrayList<>();

        final boolean isHorizontal = StitchSplitMath.isHorizontal();
        final BinaryOperator<Integer>
                horz = (a, b) -> a % b,
                vert = (a, b) -> a / b,
                xOp = isHorizontal ? horz : vert,
                yOp = isHorizontal ? vert : horz;
        final int fpd = isHorizontal ? columns : rows;

        for (int i = 0; i < frameCount; i++) {
            final GameImage frame = new GameImage(fw, fh);

            final int x = xOp.apply(i, fpd) * fw,
                    y = yOp.apply(i, fpd) * fh;

            frame.draw(resized, -1 * x, -1 * y);
            frames.add(frame.submit());
        }

        final SELayer firstLayer = new SELayer(frames,
                new GameImage(fw, fh), Constants.OPAQUE, true, false,
                false, OnionSkin.trivial(), Constants.BASE_LAYER_NAME);
        final ProjectState initialState = ProjectState.makeFromRasterFile(
                fw, fh, firstLayer, frameCount);

        final SEContext project = new SEContext(filepath, initialState, fw, fh);
        addContext(project, true);

        processNextImport();
    }

    public void processNextImport() {
        if (toImport.isEmpty())
            clearDialog();
        else
            verifyFilepath(toImport.remove(0));
    }

    public void addContext(final SEContext context, final boolean setActive) {
        // close unmodified untitled project
        if (contexts.size() == 1 && !contexts.get(0).getSaveConfig()
                .hasUnsavedChanges() &&
                !contexts.get(0).getSaveConfig().hasSaveAssociation()) {
            contexts.remove(0);
        }

        contexts.add(context);

        if (setActive)
            setContextIndex(contexts.size() - 1);
        else
            rebuildProjectsMenu();
    }

    public void removeContext(final int index) {
        if (index >= 0 && index < contexts.size()) {
            contexts.remove(index);

            if (contextIndex >= contexts.size())
                setContextIndex(contexts.size() - 1);
            else {
                DeltaTimeGlobal.setStatus(Constants.CEL_SELECTION, false);
                rebuildAllMenus();
            }

            if (contexts.size() == 0)
                exitProgram();
        }
    }

    public void exitProgram() {
        Settings.write();
        System.exit(0);
    }

    public void addPalette(final Palette palette, final boolean setActive) {
        palettes.add(palette);

        if (setActive)
            setPaletteIndex(palettes.size() - 1);

        rebuildColorsMenu();
    }

    public void deletePalette() {
        if (hasPaletteContents()) {
            palettes.remove(paletteIndex);

            if (paletteIndex >= palettes.size())
                setPaletteIndex(palettes.size() - 1);

            rebuildColorsMenu();
        }
    }

    public boolean hasPaletteContents() {
        return paletteIndex != Constants.NO_SELECTION &&
                palettes != null && paletteIndex < palettes.size();
    }

    public Palette getSelectedPalette() {
        return hasPaletteContents() ? palettes.get(paletteIndex) : null;
    }

    public void addColorToPalette() {
        paletteSelectedColorAction(Palette::addColor,
                Palette::canAdd, StatusUpdates::addColorToPalette,
                (p, c) -> StatusUpdates.cannotColorPalette(true, p, c));
    }

    public void removeColorFromPalette(final Color c) {
        paletteColorAction(c, Palette::removeColor, Palette::canRemove,
                StatusUpdates::removeColorFromPalette,
                (p, col) -> StatusUpdates.cannotColorPalette(false, p, col));
    }

    public void moveColorLeftInPalette() {
        paletteSelectedColorAction(Palette::moveLeft,
                Palette::canMoveLeft, StatusUpdates::moveLeftInPalette,
                (p, c) -> StatusUpdates.cannotShiftColorPalette(p, c, true));
    }

    public void moveColorRightInPalette() {
        paletteSelectedColorAction(Palette::moveRight,
                Palette::canMoveRight, StatusUpdates::moveRightInPalette,
                (p, c) -> StatusUpdates.cannotShiftColorPalette(p, c, false));
    }

    public void selectPaletteColorToTheLeft() {
        paletteSelectedColorAction(
                (p, c) -> setSelectedColor(p.nextLeft(c),
                        ColorMath.LastHSVEdit.NONE), Palette::isIncluded,
                (p, c) -> StatusUpdates.selectNextPaletteColor(p, c, true),
                (p, c) -> StatusUpdates.cannotSelectColorPalette(p, c, true));
    }

    public void selectPaletteColorToTheRight() {
        paletteSelectedColorAction(
                (p, c) -> setSelectedColor(p.nextRight(c),
                        ColorMath.LastHSVEdit.NONE), Palette::isIncluded,
                (p, c) -> StatusUpdates.selectNextPaletteColor(p, c, false),
                (p, c) -> StatusUpdates.cannotSelectColorPalette(p, c, false));
    }

    private void paletteSelectedColorAction(
            final BiConsumer<Palette, Color> f,
            final BiFunction<Palette, Color, Boolean> precondition,
            final BiConsumer<Palette, Color> statusUpdate,
            final BiConsumer<Palette, Color> notPermitted
    ) {
        paletteColorAction(getSelectedColor(), f,
                precondition, statusUpdate, notPermitted);
    }

    private void paletteColorAction(
            final Color c, final BiConsumer<Palette, Color> f,
            final BiFunction<Palette, Color, Boolean> precondition,
            final BiConsumer<Palette, Color> statusUpdate,
            final BiConsumer<Palette, Color> notPermitted
    ) {
        final Palette p = getSelectedPalette();

        if (hasPaletteContents() && precondition.apply(p, c)) {
            f.accept(p, c);

            if (Layout.isColorsPanelShowing())
                rebuildColorsMenu();
            else
                statusUpdate.accept(p, c);
        } else if (hasPaletteContents()) {
            // precondition failed
            notPermitted.accept(p, c);
        } else {
            StatusUpdates.noPalette();
        }
    }

    public void setContextIndex(final int contextIndex) {
        if (contextIndex >= 0 && contextIndex < contexts.size()) {
            // assign
            this.contextIndex = contextIndex;

            // auxiliaries
            DeltaTimeGlobal.setStatus(Constants.CEL_SELECTION, false);
            rebuildAllMenus();
            ToolWithBreadth.redrawToolOverlays();
            getContext().redrawCheckerboard();
        }
    }

    public void incrementSelectedColorHue(
            final int deltaH
    ) {
        final Color c = getSelectedColor();
        int hue = ColorMath.hueGetter(c) + deltaH;

        while (hue > Constants.HUE_SCALE)
            hue -= Constants.HUE_SCALE;

        while (hue < 0)
            hue += Constants.HUE_SCALE;

        final Color newC = ColorMath.hueAdjustedColor(hue, c);

        setSelectedColor(newC, ColorMath.LastHSVEdit.HUE);

        StatusUpdates.colorSliderAdjustment("Hue", hue, newC);
    }

    public void incrementSelectedColorSaturation(
            final int deltaS
    ) {
        final Color c = getSelectedColor();
        final int sat = MathPlus.bounded(0,
                ColorMath.satGetter(c) + deltaS, Constants.SAT_SCALE);

        final Color newC = ColorMath.satAdjustedColor(sat, c);

        setSelectedColor(newC, ColorMath.LastHSVEdit.SAT);

        StatusUpdates.colorSliderAdjustment("Saturation", sat, newC);
    }

    public void incrementSelectedColorValue(
            final int deltaV
    ) {
        final Color c = getSelectedColor();
        final int value = MathPlus.bounded(0,
                ColorMath.valueGetter(c) + deltaV, Constants.VALUE_SCALE);

        final Color newC = ColorMath.valueAdjustedColor(value, c);

        setSelectedColor(newC, ColorMath.LastHSVEdit.VAL);

        StatusUpdates.colorSliderAdjustment("Value", value, newC);
    }

    public void incrementSelectedColorRGBA(
            final int deltaR, final int deltaG,
            final int deltaB, final int deltaAlpha
    ) {
        final Color c = getSelectedColor(), newC = new Color(
                MathPlus.bounded(0, c.getRed() + deltaR, Constants.RGBA_SCALE),
                MathPlus.bounded(0, c.getGreen() + deltaG, Constants.RGBA_SCALE),
                MathPlus.bounded(0, c.getBlue() + deltaB, Constants.RGBA_SCALE),
                MathPlus.bounded(0, c.getAlpha() + deltaAlpha, Constants.RGBA_SCALE)
        );

        setSelectedColor(newC, ColorMath.LastHSVEdit.NONE);

        final String slider;
        final int sliderVal;

        if (deltaR != 0) {
            slider = "Red";
            sliderVal = newC.getRed();
        } else if (deltaG != 0) {
            slider = "Green";
            sliderVal = newC.getGreen();
        } else if (deltaB != 0) {
            slider = "Blue";
            sliderVal = newC.getBlue();
        } else if (deltaAlpha != 0) {
            slider = "Alpha";
            sliderVal = newC.getAlpha();
        } else {
            slider = "???";
            sliderVal = 0;
        }

        StatusUpdates.colorSliderAdjustment(slider, sliderVal, newC);
    }

    public void setSelectedColor(final Color color, final ColorMath.LastHSVEdit lastHSVEdit) {
        colors[colorIndex] = color;
        ColorMath.setLastHSVEdit(lastHSVEdit, color);
    }

    public void setColorIndex(final int colorIndex) {
        this.colorIndex = colorIndex;

        ColorMath.setLastHSVEdit(ColorMath.LastHSVEdit.NONE, getSelectedColor());
    }

    public void setColorIndexAndColor(final int colorIndex, final Color color) {
        setColorIndex(colorIndex);
        setSelectedColor(color, ColorMath.LastHSVEdit.NONE);
    }

    public void swapColors() {
        final Color temp = colors[PRIMARY];
        colors[PRIMARY] = colors[SECONDARY];
        colors[SECONDARY] = temp;

        ColorMath.setLastHSVEdit(ColorMath.LastHSVEdit.NONE, getSelectedColor());

        if (!Layout.isColorsPanelShowing())
            StatusUpdates.swapColors();
    }

    public void setPaletteIndex(final int paletteIndex) {
        this.paletteIndex = paletteIndex;
        rebuildColorsMenu();
    }

    public void toggleFullscreen() {
        windowed = !windowed;

        remakeWindow();
    }

    public void remakeWindow() {
        window = makeWindow();
        program.setCanvasSize(Layout.width(), Layout.height());
        program.replaceWindow(window);

        // redraw everything
        rebuildAllMenus();

        final Preview p = Preview.get();

        if (p instanceof EmbeddedPreview ep && !Settings.isSeparatedPreview())
            ep.refresh();
        else if (p != null) {
            Preview.set(p.c);
            Preview.get().copyState(p);
        }

        window.focus();
    }

    public void autoAssignPickUpSelection() {
        this.tool = PickUpSelection.get();
        rebuildToolButtonMenu();
    }

    public void setTool(final Tool tool) {
        final Tool was = this.tool;

        if (was.equals(PickUpSelection.get()) &&
                !tool.equals(PickUpSelection.get()))
            PickUpSelection.get().disengage(getContext());
        else if (was.equals(TextTool.get()) &&
                !tool.equals(TextTool.get()) && TextTool.get().isTyping())
            TextTool.get().setTyping(false);

        this.tool = tool;

        if (!was.equals(PickUpSelection.get()) &&
                tool.equals(PickUpSelection.get()))
            PickUpSelection.get().engage(getContext());

        rebuildToolButtonMenu();
    }

    public void clearDialog() {
        dialog = null;
        rebuildStateDependentMenus();
    }

    public void setDialog(final Menu dialog) {
        this.dialog = dialog;
    }

    /**
     * Ensure thread safety by passing tasks from other threads
     * into the job scheduler
     * */
    public void scheduleJob(final Runnable job) {
        jobScheduler.add(job);
    }
}
