package com.jordanbunke.stipple_effect.state;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.stip_parser.rep.IRLayer;
import com.jordanbunke.stip_parser.rep.IRState;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.layer.OnionSkin;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.selection.Selection;
import com.jordanbunke.stipple_effect.selection.SelectionContents;
import com.jordanbunke.stipple_effect.selection.SelectionMode;
import com.jordanbunke.stipple_effect.tools.PickUpSelection;
import com.jordanbunke.stipple_effect.tools.Tool;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.visual.theme.SEColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectState {
    private boolean checkpoint;
    private Operation operation;

    private final int imageWidth, imageHeight;

    // FRAME
    private final int frameCount;
    private final List<Double> frameDurations;
    private int frameIndex;

    // LAYER
    private final List<SELayer> layers;
    private int layerEditIndex;

    // SELECTION
    private final SelectionMode selectionMode;
    private final Selection selection;
    private final SelectionContents selectionContents;

    // POPULATED
    private final boolean[][] populated;

    public static ProjectState makeNew(
            final int imageWidth, final int imageHeight
    ) {
        return new ProjectState(
                imageWidth, imageHeight, new ArrayList<>(List.of(
                        new SELayer(imageWidth, imageHeight))), 1);
    }

    public static ProjectState makeFromRasterFile(
            final int imageWidth, final int imageHeight,
            final SELayer firstLayer, final int frameCount
    ) {
        return new ProjectState(imageWidth, imageHeight,
                new ArrayList<>(List.of(firstLayer)), frameCount);
    }

    public static ProjectState makeFromNativeFile(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers,
            final int frameCount, final List<Double> frameDurations
    ) {
        return new ProjectState(imageWidth, imageHeight, layers, 0,
                frameCount, frameDurations, 0,
                SelectionMode.BOUNDS, Selection.EMPTY, null, true);
    }

    private ProjectState(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers, final int frameCount
    ) {
        this(imageWidth, imageHeight, layers, 0,
                frameCount, defaultFrameDurations(frameCount), 0,
                SelectionMode.BOUNDS, Selection.EMPTY, null, true);
    }

    private ProjectState(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers, final int layerEditIndex,
            final int frameCount, final List<Double> frameDurations,
            final int frameIndex,
            final SelectionMode selectionMode,
            final Selection selection,
            final SelectionContents selectionContents
    ) {
        this(imageWidth, imageHeight, layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents, true);
    }

    private ProjectState(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers, final int layerEditIndex,
            final int frameCount, final List<Double> frameDurations,
            final int frameIndex,
            final SelectionMode selectionMode,
            final Selection selection,
            final SelectionContents selectionContents,
            final boolean checkpoint
    ) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        this.layers = layers;
        this.layerEditIndex = layerEditIndex;

        this.frameCount = frameCount;
        this.frameDurations = frameDurations.size() == frameCount
                ? frameDurations : defaultFrameDurations(frameCount);
        this.frameIndex = Math.max(0, Math.min(frameIndex, frameCount - 1));

        this.selectionMode = selectionMode;
        this.selection = selection;
        this.selectionContents = selectionContents;

        this.checkpoint = checkpoint;
        this.operation = Operation.NONE;

        this.populated = determinePopulatedCels();
    }

    public static List<Double> defaultFrameDurations(final int frameCount) {
        final List<Double> frameDurations = new ArrayList<>();

        while (frameDurations.size() < frameCount)
            frameDurations.add(Constants.DEFAULT_FRAME_DURATION);

        return frameDurations;
    }

    public static ProjectState realize(final IRState rep) {
        return makeFromNativeFile(rep.width(), rep.height(),
                Arrays.stream(rep.layers()).map(SELayer::realize).toList(),
                rep.frameCount(),
                Arrays.stream(rep.frameDurations()).boxed().toList());
    }

    public IRState convert() {
        return new IRState(imageWidth, imageHeight, frameCount,
                frameDurations.stream().mapToDouble(d -> d).toArray(),
                layers.stream().map(SELayer::convert).toArray(IRLayer[]::new));
    }

    public ProjectState changeIsCheckpoint(
            final boolean checkpoint
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents,
                checkpoint);
    }

    public ProjectState changeSelectionContents(
            final SelectionContents selectionContents
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                SelectionMode.CONTENTS, Selection.EMPTY, selectionContents);
    }

    public ProjectState changeSelectionBounds(
            final Selection selection
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                SelectionMode.BOUNDS, selection, null);
    }

    public ProjectState changeLayers(
            final List<SELayer> layers
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents);
    }

    public ProjectState changeLayers(
            final List<SELayer> layers, final int layerEditIndex
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents);
    }

    public ProjectState changeFrames(
            final List<SELayer> layers, final int frameIndex,
            final int frameCount, final List<Double> frameDurations
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents);
    }

    public ProjectState changeFrameDurations(
            final List<Double> frameDurations
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents);
    }

    public ProjectState resize(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, frameDurations, frameIndex,
                selectionMode, selection, selectionContents);
    }

    public ProjectState stitch(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                1, defaultFrameDurations(1), 0,
                selectionMode, selection, selectionContents);
    }

    public ProjectState split(
            final int imageWidth, final int imageHeight,
            final List<SELayer> layers, final int frameCount
    ) {
        return new ProjectState(imageWidth, imageHeight,
                layers, layerEditIndex,
                frameCount, defaultFrameDurations(frameCount), 0,
                selectionMode, selection, selectionContents);
    }

    public GameImage draw(
            final boolean includeOnionSkins, final boolean inProjectRender,
            final int frameIndex
    ) {
        final GameImage image = new GameImage(imageWidth, imageHeight);
        final Tool tool = StippleEffect.get().getTool();

        for (SELayer layer : layers) {
            if (layer.isEnabled()) {
                final boolean osOn = layer.isOnionSkinOn();
                final OnionSkin os = layer.getOnionSkin();
                final GameImage onionSkinBack, onionSkinForward;

                if (includeOnionSkins && osOn) {
                    onionSkinBack = layer.getOnionSkinCel(frameIndex, true);
                    onionSkinForward = layer.getOnionSkinCel(frameIndex, false);
                } else {
                    onionSkinBack = GameImage.dummy();
                    onionSkinForward = GameImage.dummy();
                }

                // render onion skin under layer
                if (includeOnionSkins && osOn) {
                    if (os.underBack)
                        image.draw(onionSkinBack);

                    if (os.underForward)
                        image.draw(onionSkinForward);
                }

                // this layer
                GameImage layerImage = new GameImage(layer.getRender(frameIndex));

                // edge case where pixels on editing layer behind transparent
                // pixel that is part of selection must be unrendered when selection
                // is being moved
                final boolean previewCondition = inProjectRender &&
                        layer.equals(getEditingLayer()) && hasSelection() &&
                        selectionMode == SelectionMode.CONTENTS &&
                        !(tool.equals(PickUpSelection.get()) &&
                                PickUpSelection.get().isMoving());

                if (previewCondition) {
                    final Selection selection = selectionContents.getSelection();
                    final int t = SEColors.transparent().getRGB();

                    selection.pixelAlgorithm(imageWidth, imageHeight,
                            (x, y) -> layerImage.setRGB(x, y, t));
                }

                image.draw(layerImage.submit());

                // render onion skin above layer
                if (includeOnionSkins && osOn) {
                    if (!os.underBack)
                        image.draw(onionSkinBack);

                    if (!os.underForward)
                        image.draw(onionSkinForward);
                }

                if (previewCondition) {
                    final GameImage preview = selectionContents
                            .getContentForCanvas(getImageWidth(), getImageHeight());
                    image.draw(preview);
                }

                final boolean toolPreviewCondition = inProjectRender &&
                        layer.equals(getEditingLayer()) &&
                        tool.hasToolContentPreview() &&
                        !tool.previewScopeIsGlobal();

                if (toolPreviewCondition) {
                    final GameImage toolContentPreview =
                            tool.getToolContentPreview();
                    image.draw(toolContentPreview);
                }
            }
        }

        final boolean toolPreviewCondition = inProjectRender &&
                tool.hasToolContentPreview() &&
                tool.previewScopeIsGlobal();

        if (toolPreviewCondition) {
            final GameImage toolContentPreview = tool.getToolContentPreview();
            image.draw(toolContentPreview);
        }

        return image.submit();
    }

    public void setLayerEditIndex(final int layerEditIndex) {
        if (this.layerEditIndex != layerEditIndex &&
                layerEditIndex >= 0 && layerEditIndex < layers.size())
            this.layerEditIndex = layerEditIndex;
    }

    public void editLayerBelow() {
        setLayerEditIndex(layerEditIndex - 1);
    }

    public void editLayerAbove() {
        setLayerEditIndex(layerEditIndex + 1);
    }

    public void markAsCheckpoint(
            final boolean processConsequence
    ) {
        this.checkpoint = true;

        if (processConsequence)
            operation.getActionType().consequence();
    }

    public void tag(final Operation operation) {
        this.operation = operation;
    }

    // PRECONDITIONS

    // layer preconditions
    public boolean canAddLayer() {
        return layers.size() < Constants.MAX_NUM_LAYERS;
    }

    public boolean canRemoveLayer() {
        return layers.size() > 1;
    }

    public boolean canMoveLayerDown() {
        return layerEditIndex > 0;
    }

    public boolean canMoveLayerUp() {
        return layerEditIndex + 1 < layers.size();
    }

    // frame preconditions
    public boolean canAddFrame() {
        return frameCount < Constants.MAX_NUM_FRAMES;
    }

    public boolean canRemoveFrame() {
        return frameCount > 1;
    }

    public boolean canMoveFrameBack() {
        return frameIndex > 0;
    }

    public boolean canMoveFrameForward() {
        return frameIndex + 1 < frameCount;
    }

    // getters
    public boolean isCheckpoint() {
        return checkpoint;
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean hasSelection() {
        return switch (selectionMode) {
            case BOUNDS -> selection.hasSelection();
            case CONTENTS -> hasSelectionContents();
        };
    }

    public Selection getSelection() {
        return switch (selectionMode) {
            case BOUNDS -> selection;
            case CONTENTS -> selectionContents.getSelection(); // TODO: investigate crash - NullPointerException
        };
    }

    public boolean hasSelectionContents() {
        return selectionContents != null;
    }

    public SelectionContents getSelectionContents() {
        return selectionContents;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public SELayer getEditingLayer() {
        return layers.get(layerEditIndex);
    }

    public GameImage getActiveCel() {
        return getEditingLayer().getCel(frameIndex);
    }

    public int getLayerEditIndex() {
        return layerEditIndex;
    }

    public List<SELayer> getLayers() {
        return layers;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public List<Double> getFrameDurations() {
        return frameDurations;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }


    // frame index manipulation
    public void setFrameIndex(final int frameIndex) {
        if (this.frameIndex != frameIndex &&
                frameIndex >= 0 && frameIndex < frameCount) {
            this.frameIndex = frameIndex;
        }
    }

    public void nextFrame() {
        frameIndex++;

        if (frameIndex >= frameCount)
            frameIndex = 0;
    }

    public void previousFrame() {
        frameIndex--;

        if (frameIndex < 0)
            frameIndex = frameCount - 1;
    }

    private boolean[][] determinePopulatedCels() {
        final int layerCount = layers.size();
        final boolean[][] populated = new boolean[layerCount][frameCount];

        for (int l = 0; l < layerCount; l++)
            for (int f = 0; f < frameCount; f++) {
                final GameImage cel = layers.get(l).getCel(f);

                pixels:
                for (int x = 0; x < cel.getWidth(); x++)
                    for (int y = 0; y < cel.getHeight(); y++)
                        if (cel.getColorAt(x, y).getAlpha() > 0) {
                            populated[l][f] = true;
                            break pixels;
                        }
            }

        return populated;
    }

    public boolean isCelPopulated(final int layerIndex, final int frameIndex) {
        if (layerIndex < 0 || layerIndex >= populated.length ||
                frameIndex < 0 || frameIndex >= populated[layerIndex].length)
            return false;

        return populated[layerIndex][frameIndex];
    }
}
