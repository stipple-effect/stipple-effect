package com.jordanbunke.stipple_effect.preview;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.PlaybackInfo;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.MenuAssembly;
import com.jordanbunke.stipple_effect.visual.menu_elements.DynamicLabel;
import com.jordanbunke.stipple_effect.visual.menu_elements.IconButton;
import com.jordanbunke.stipple_effect.visual.menu_elements.IncrementalRangeElements;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.jordanbunke.stipple_effect.utility.Layout.*;

public abstract class Preview extends MenuElement implements PreviewPlayback {
    private static Preview INSTANCE;

    private static Path lastScriptPath;

    public final SEContext c;
    private HeadFuncNode script;

    // playback
    private final PlaybackInfo playbackInfo;
    private int frameIndex, frameCount;

    // project render
    private GameImage[] content;
    final PreviewContainer container;

    // element movement
    private final Coord2D initialPos;
    private final Map<MenuElement, Coord2D> componentsPositions;
    private final Menu menu;

    static {
        INSTANCE = null;
        lastScriptPath = null;
    }

    public static void set(final SEContext c) {
        final boolean windowed = StippleEffect.get().isWindowed(),
                separated = Settings.isSeparatedPreview();

        if (INSTANCE != null) {
            if (INSTANCE.c.equals(c) && INSTANCE instanceof EmbeddedPreview ep &&
                    !(windowed && separated)) {
                ep.refresh();
                return;
            } else
                INSTANCE.close();
        }

        INSTANCE = (windowed && separated)
                ? new WindowedPreview(c) : new EmbeddedPreview(c);
    }

    public static Preview get() {
        return INSTANCE;
    }

    static void kill() {
        INSTANCE = null;
    }

    protected Preview(
            final SEContext c, final Coord2D position, final Bounds2D dimensions
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);

        this.c = c;
        script = null;

        frameIndex = 0;
        updateContent();
        frameCount = content.length;

        initialPos = position;
        componentsPositions = new HashMap<>();

        playbackInfo = PlaybackInfo.forPreview();
        menu = makeMenu();

        container = new PreviewContainer(
                initialPos.displace(PREV_TL_BUFFER, PREV_CONTAINER_Y), this);
        addMenuComponents(container);
    }

    private Menu makeMenu() {
        final MenuBuilder mb = new MenuBuilder();

        final Coord2D initial = initialPos.displace(
                PREV_TL_BUFFER,
                PREV_TL_BUFFER);
        final Supplier<Boolean> hasMultipleFrames = () -> frameCount > 1;

        // SCRIPT
        final String script = "Running script", noScript = "No script";

        final MenuElement removeButton = IconButton.make(
                ResourceCodes.REMOVE_SCRIPT, initial, this::removeScript),
                reloadButton = IconButton.make(
                        ResourceCodes.RELOAD_SCRIPT,
                        initial.displace(BUTTON_INC, 0),
                        () -> validatePreviewScript(lastScriptPath)),
                importButton = IconButton.make(
                        ResourceCodes.IMPORT_PREVIEW,
                        initial.displace(BUTTON_INC * 2, 0),
                        this::importPreview),
                perLayerButton = IconButton.make(
                        ResourceCodes.IMPORT_PER_LAYER,
                        initial.displace(BUTTON_INC * 3, 0),
                        this::importPerLayer),
                scriptText = labelAfterLastButton(
                        perLayerButton, () -> script, script),
                uploadButton = IconButton.make(
                        ResourceCodes.IMPORT_SCRIPT,
                        initial, this::openPreviewScript),
                noScriptText = labelAfterLastButton(
                        uploadButton, () -> noScript, noScript);
        addMenuComponents(uploadButton, removeButton, reloadButton,
                importButton, perLayerButton, scriptText, noScriptText);

        final MenuElement smartScriptElement = new ThinkingMenuElement(
                () -> this.script != null
                        ? new MenuElementGrouping(removeButton, reloadButton,
                        importButton, perLayerButton, scriptText)
                        : new MenuElementGrouping(uploadButton, noScriptText));
        mb.add(smartScriptElement);

        // PLAYBACK
        final MenuElement firstFrame =
                IconButton.make(ResourceCodes.TO_FIRST_FRAME,
                        initial.displace(0, BUTTON_INC), this::toFirstFrame),
                previousFrame = IconButton.make(ResourceCodes.PREVIOUS,
                        firstFrame.getRenderPosition().displace(BUTTON_INC, 0),
                        this::previousFrame),
                playStop = MenuAssembly.generatePlayStopToggle(playbackInfo,
                        previousFrame.getRenderPosition()
                                .displace(BUTTON_INC, 0)),
                nextFrame = IconButton.make(ResourceCodes.NEXT,
                        playStop.getRenderPosition().displace(BUTTON_INC, 0),
                        this::nextFrame),
                lastFrame = IconButton.make(ResourceCodes.TO_LAST_FRAME,
                        nextFrame.getRenderPosition().displace(BUTTON_INC, 0),
                        this::toLastFrame);

        final MenuElement playbackModeButton =
                MenuAssembly.generatePlaybackModeToggle(playbackInfo,
                        lastFrame.getRenderPosition().displace(BUTTON_INC, 0));
        final DynamicLabel frameTracker = labelAfterLastButton(
                playbackModeButton,
                () -> (frameIndex + 1) + "/" + frameCount, "XXX/XXX");

        addMenuComponents(firstFrame, previousFrame, playStop,
                nextFrame, lastFrame, playbackModeButton, frameTracker);

        // FPS
        final StaticMenuElement fpsReference = new StaticMenuElement(
                initial.displace(
                        -(BUTTON_DIM + CONTENT_BUFFER_PX),
                        BUTTON_INC), ICON_DIMS,
                MenuElement.Anchor.LEFT_TOP, GameImage.dummy());
        final int fpsButtonY = initial.y + BUTTON_INC * 2;
        final IncrementalRangeElements<Integer> fps =
                IncrementalRangeElements.makeForInt(fpsReference, fpsButtonY,
                        (fpsButtonY - BUTTON_OFFSET) + TEXT_Y_OFFSET,
                        1, Constants.MIN_PLAYBACK_FPS, Constants.MAX_PLAYBACK_FPS,
                        playbackInfo::setFps, playbackInfo::getFps,
                        i -> i, sv -> sv, sv -> sv + " FPS", "XXX FPS");

        addMenuComponents(fps.decButton, fps.incButton, fps.slider, fps.value);

        final MenuElement smartPlaybackElement = new GatewayMenuElement(
                new MenuElementGrouping(
                        firstFrame, previousFrame, playStop,
                        nextFrame, lastFrame, playbackModeButton, frameTracker,
                        fps.decButton, fps.incButton, fps.slider, fps.value
                ), hasMultipleFrames);
        mb.add(smartPlaybackElement);

        return mb.build();
    }

    private static DynamicLabel labelAfterLastButton(
            final MenuElement lastButton, final Supplier<String> getter,
            final String widestCase
    ) {
        return DynamicLabel.make(
                lastButton.getRenderPosition().displace(
                        BUTTON_DIM + CONTENT_BUFFER_PX,
                        -BUTTON_OFFSET + TEXT_Y_OFFSET),
                getter, DynamicLabel.getWidth(widestCase));
    }

    void addMenuComponents(final MenuElement... components) {
        for (MenuElement component : components)
            componentsPositions.put(component, component.getPosition());
    }

    @Override
    public void setPosition(final Coord2D position) {
        super.setPosition(position);

        propagatePositionChange();
    }

    private void propagatePositionChange() {
        final Coord2D deltaPos = getPosition().displace(initialPos.scale(-1));

        for (MenuElement comp : componentsPositions.keySet())
            comp.setPosition(componentsPositions.get(comp).displace(deltaPos));
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        menu.process(eventLogger);

        if (frameCount > 1)
            processKeys(eventLogger);

        container.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        if (!projectOpen()) {
            close();
            return;
        }

        animate(deltaTime);

        menu.update(deltaTime);
        container.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        final GameImage bg = new GameImage(getWidth(), getHeight());
        bg.fill(Settings.getTheme().panelBackground);
        draw(bg.submit(), canvas);

        menu.render(canvas);
        container.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public void close() {
        kill();
    }
    protected void executeOnFileDialogOpen() {}

    protected static Bounds2D minSize() {
        return new Bounds2D(PREV_MIN_W, PREV_MIN_H);
    }

    protected static Bounds2D maxSize() {
        return new Bounds2D(width() / 2,
                (int)(height() * 0.75));
    }

    public void copyState(final Preview source) {
        if (!c.equals(source.c))
            return;

        setScript(source.script);

        container.setFrame(source.container.getFrame());
        container.setRenderInfo(source.container.getRenderInfo());
    }

    private boolean projectOpen() {
        return StippleEffect.get().getContexts().contains(c);
    }

    private void animate(final double deltaTime) {
        final ProjectState s = c.getState();

        final double duration = script == null
                ? s.getFrameDurations().get(frameIndex % s.getFrameCount())
                : Constants.DEFAULT_FRAME_DURATION;

        if (playbackInfo.isPlaying()) {
            final boolean nextFrameDue =
                    playbackInfo.checkIfNextFrameDue(deltaTime, duration);

            if (nextFrameDue)
                frameIndex = playbackInfo.nextAnimationFrameForPreview(
                        frameIndex, frameCount);
        }

        if (c.getSaveConfig().hasChangesSincePreview())
            updateContent();
    }

    private void updateContent() {
        c.getSaveConfig().logPreview();

        content = IntStream.range(0, c.getState().getFrameCount())
                .mapToObj(i -> c.getState()
                        .draw(false, false, i))
                .toArray(GameImage[]::new);

        if (script != null)
            runScript();

        updateFrameData();
    }

    private void runScript() {
        content = ScriptUtils.runPreviewScript(content, script, lastScriptPath);

        if (content == null)
            close();
    }

    private void updateFrameData() {
        if (INSTANCE == null)
            return;

        frameCount = content.length;
        frameIndex %= frameCount;
    }

    private void openPreviewScript() {
        final Path filepath = StippleEffect.get().openScript();
        executeOnFileDialogOpen();

        if (filepath != null) {
            lastScriptPath = filepath;
            validatePreviewScript(filepath);
        }
    }

    private void validatePreviewScript(final Path filepath) {
        final HeadFuncNode script =
                SEInterpreter.get().build(FileIO.readFile(filepath));

        if (SEInterpreter.validatePreviewScript(script, c))
            setScript(script);
        else if (script != null)
            StatusUpdates.invalidPreviewScript(script.toString());
        else
            StatusUpdates.failedToCompileScript(filepath);
    }

    private void setScript(final HeadFuncNode script) {
        this.script = script;
        updateContent();
        animate(0d);
    }

    private void removeScript() {
        script = null;
        updateContent();
        animate(0d);
    }

    private void importPreview() {
        final SEContext project = ScriptUtils.projectFromScriptOutput(content);

        StippleEffect.get().scheduleJob(() ->
                StippleEffect.get().addContext(project, true));
    }

    private void importPerLayer() {
        final SEContext project = ScriptUtils
                .transformProjectPerLayer(c, script, lastScriptPath);

        StippleEffect.get().scheduleJob(() ->
                StippleEffect.get().addContext(project, true));
    }

    GameImage getFrame() {
        return content[frameIndex];
    }

    @Override
    public void toFirstFrame() {
        frameIndex = 0;
    }

    @Override
    public void toLastFrame() {
        frameIndex = frameCount - 1;
    }

    @Override
    public void previousFrame() {
        frameIndex = frameIndex == 0 ? frameCount - 1 : frameIndex - 1;
    }

    @Override
    public void nextFrame() {
        frameIndex = (frameIndex + 1) % frameCount;
    }

    @Override
    public PlaybackInfo getPlaybackInfo() {
        return playbackInfo;
    }

    @Override
    public String toString() {
        return "Preview: " + c.getSaveConfig().getFormattedName(false, false);
    }
}
