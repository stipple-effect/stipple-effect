package com.jordanbunke.stipple_effect.project;

import com.jordanbunke.anim.data.AnimBuilder;
import com.jordanbunke.anim.writers.AnimWriter;
import com.jordanbunke.anim.writers.GIFWriter;
import com.jordanbunke.anim.writers.MP4Writer;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.selection.SelectionMode;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.stip.ParserSerializer;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.DialogAssembly;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BinaryOperator;

public final class SaveConfig {
    private static final int INVALID_BOUND = -1;

    private Path folder;
    private String name, indexPrefix, indexSuffix;
    private SaveType saveType;

    private boolean editedSinceLastSave, editedSinceLastPreview,
            saveRangeOfFrames;

    private int framesPerDim, fps, scaleUp, countFrom, lowerBound, upperBound;

    private DialogVals.SequenceOrder sequenceOrder;

    public enum SaveType {
        NATIVE, PNG_SHEET, PNG_SEPARATE, GIF, MP4;

        public String getFileSuffix() {
            return switch (this) {
                case PNG_SEPARATE, PNG_SHEET -> "png";
                case GIF -> "gif";
                case MP4 -> "mp4";
                case NATIVE -> Constants.NATIVE_FILE_SUFFIX;
            };
        }

        public String getButtonText(final SEContext context) {
            return switch (this) {
                case PNG_SHEET -> "Single PNG" +
                        (context.isAnimation() ? " (spritesheet)" : "");
                case PNG_SEPARATE -> "Separate PNGs per frame";
                case GIF -> "Animated GIF";
                case MP4 -> "MP4 Video";
                case NATIVE -> StippleEffect.PROGRAM_NAME +
                        " file (." + getFileSuffix() + ")";
            };
        }
    }

    public SaveConfig(final Path folder, final String name, final SaveType saveType) {
        this.folder = folder;
        this.name = name;
        this.saveType = saveType;

        editedSinceLastSave = false;
        editedSinceLastPreview = true;

        framesPerDim = 1;
        sequenceOrder = DialogVals.SequenceOrder.HORIZONTAL;
        fps = Constants.DEFAULT_PLAYBACK_FPS;
        scaleUp = Constants.DEFAULT_SAVE_SCALE_UP;

        countFrom = 1;
        indexPrefix = Settings.getDefaultIndexPrefix();
        indexSuffix = Settings.getDefaultIndexSuffix();

        saveRangeOfFrames = false;
        lowerBound = INVALID_BOUND;
        upperBound = INVALID_BOUND;
    }

    public static SaveConfig fromFilepath(final Path filepath) {
        final Path folder;
        final String name;

        if (filepath == null) {
            folder = null;
            name = "";
        } else {
            folder = filepath.getParent();
            final String filename = filepath.getFileName().toString();

            name = filename.contains(".") ? filename.substring(0,
                    filename.lastIndexOf(".")) : filename;
        }

        final SaveType saveType;

        if (filepath == null)
            saveType = SaveType.PNG_SHEET;
        else {
            final String fn = filepath.getFileName().toString();

            if (fn.endsWith(SaveType.NATIVE.getFileSuffix()))
                saveType = SaveType.NATIVE;
            else if (fn.endsWith(SaveType.GIF.getFileSuffix()))
                saveType = SaveType.GIF;
            else
                saveType = SaveType.PNG_SHEET;
        }

        return new SaveConfig(folder, name, saveType);
    }

    public void save(final SEContext context) {
        if (!hasSaveAssociation()) {
            DialogAssembly.setDialogToSave(context);
            return;
        }

        StatusUpdates.saving();

        // drop picked up selection down to layer so its contents can be saved
        if (context.getState().getSelectionMode() == SelectionMode.CONTENTS &&
                context.getState().hasSelection())
            context.dropContentsToLayer(true, false);

        final int w = context.getState().getImageWidth(),
                h = context.getState().getImageHeight(),
                framesToSave = calculateNumFrames(context),
                f0 = saveRangeOfFrames ? lowerBound : 0;

        setFramesPerDim(Math.min(framesToSave, framesPerDim));

        if (saveType == SaveType.NATIVE) {
            final Thread stipSaverThread = new Thread(() -> {
                final Path filepath = buildFilepath();

                ParserSerializer.save(context, filepath);
                StatusUpdates.saved(filepath);
            });

            stipSaverThread.start();
        } else {
            final int frameCount = context.getState().getFrameCount();

            if (f0 + framesToSave > frameCount || f0 < 0 || framesToSave <= 0) {
                StatusUpdates.saveFailed();
                return;
            }

            switch (saveType) {
                case GIF, MP4 -> {
                    final ProjectState state = context.getState();
                    final List<Double> frameDurations = state.getFrameDurations();

                    final int standardDurationMillis = Constants.MILLIS_IN_SECOND / fps;

                    final AnimBuilder ab = new AnimBuilder();

                    for (int i = 0; i < framesToSave; i++) {
                        final int frameIndex = f0 + i;

                        final int frameDurationMillis =
                                (int)(standardDurationMillis *
                                        frameDurations.get(frameIndex));

                        GameImage img = context.getState().draw(
                                false, false, frameIndex);

                        if (scaleUp > 1)
                            img = ImageProcessing.scale(img, scaleUp);

                        ab.addFrame(img, frameDurationMillis);
                    }

                    final AnimWriter writer = saveType == SaveType.GIF
                            ? GIFWriter.get() : MP4Writer.get();

                    final Thread animSaverThread = new Thread(() -> {
                        final Path filepath = buildFilepath();

                        writer.write(filepath, ab.build());
                        StatusUpdates.saved(filepath);
                    });

                    animSaverThread.start();
                }
                case PNG_SEPARATE -> {
                    for (int i = 0; i < framesToSave; i++) {
                        final GameImage image = context.getState().draw(
                                false, false, f0 + i);
                        GameImageIO.writeImage(buildFilepath(countFrom + i),
                                scaleUp > 1
                                        ? ImageProcessing.scale(image, scaleUp)
                                        : image);
                    }

                    StatusUpdates.savedAllFrames(folder);
                }
                case PNG_SHEET -> {
                    final boolean isHorizontal =
                            sequenceOrder == DialogVals.SequenceOrder.HORIZONTAL;
                    final int fpd = getFramesPerDim(),
                            fpcd = calcFramesPerCompDim(context),
                            sw = w * (isHorizontal ? fpd : fpcd),
                            sh = h * (isHorizontal ? fpcd : fpd);

                    final BinaryOperator<Integer>
                            horz = (a, b) -> a % b,
                            vert = (a, b) -> a / b,
                            xOp = isHorizontal ? horz : vert,
                            yOp = isHorizontal ? vert : horz;

                    final GameImage stitched = new GameImage(sw, sh);

                    for (int i = 0; i < framesToSave; i++) {
                        final int x = xOp.apply(i, fpd) * w,
                                y = yOp.apply(i, fpd) * h;

                        stitched.draw(context.getState().draw(
                                false, false, f0 + i), x, y);
                    }

                    GameImageIO.writeImage(buildFilepath(), scaleUp > 1
                            ? ImageProcessing.scale(stitched.submit(), scaleUp)
                            : stitched.submit());
                    StatusUpdates.saved(buildFilepath());
                }
            }
        }

        editedSinceLastSave = false;
        StippleEffect.get().rebuildProjectsMenu();
    }

    private int calcFramesPerCompDim(final SEContext context) {
        return DialogVals.calculateFramesPerComplementaryDim(
                calculateNumFrames(context), framesPerDim);
    }

    public void markAsEdited() {
        editedSinceLastSave = true;
        editedSinceLastPreview = true;
    }

    public void logPreview() {
        editedSinceLastPreview = false;
    }

    private Path buildFilepath(final String nameSuffix) {
        return folder.resolve(name + nameSuffix + "." + saveType.getFileSuffix());
    }

    public Path buildFilepath(final int frameIndex) {
        return buildFilepath(indexPrefix + frameIndex + indexSuffix);
    }

    public Path buildFilepath() {
        return buildFilepath("");
    }

    public static SaveType[] getSaveOptions(final SEContext context) {
        if (context.isAnimation())
            return SaveType.values();

        return new SaveType[] { SaveType.NATIVE, SaveType.PNG_SHEET};
    }

    public int calculateNumFrames(final SEContext context) {
        if (!saveRangeOfFrames)
            return context.getState().getFrameCount();

        return (upperBound - lowerBound) + 1;
    }

    public boolean hasSaveAssociation() {
        return folder != null && !name.equals("");
    }

    public boolean hasUnsavedChanges() {
        return editedSinceLastSave;
    }

    public boolean hasChangesSincePreview() {
        return editedSinceLastPreview;
    }

    public Path getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName(
            final boolean includeEditMark, final boolean abbreviate
    ) {
        final StringBuilder sb = new StringBuilder();

        if (includeEditMark && editedSinceLastSave)
            sb.append("* ");

        if (hasSaveAssociation()) {
            if (abbreviate && name.length() > Constants.MAX_NAME_LENGTH)
                sb.append(name, 0, Constants.MAX_NAME_LENGTH / 2)
                        .append("...").append(name.substring(name.length() - 4));
            else
                sb.append(name);
        } else
            sb.append(Constants.UNTITLED_PROJECT_NAME);

        return sb.toString();
    }

    public int getFps() {
        return fps;
    }

    public int getScaleUp() {
        return scaleUp;
    }

    public int getFramesPerDim() {
        return framesPerDim;
    }

    public DialogVals.SequenceOrder getSequenceOrder() {
        return sequenceOrder;
    }

    public SaveType getSaveType() {
        return saveType;
    }

    public String getIndexPrefix() {
        return indexPrefix;
    }

    public String getIndexSuffix() {
        return indexSuffix;
    }

    public int getCountFrom() {
        return countFrom;
    }

    public boolean isSaveRangeOfFrames() {
        return saveRangeOfFrames;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setSaveRangeOfFrames(final boolean saveRangeOfFrames) {
        this.saveRangeOfFrames = saveRangeOfFrames;
    }

    public void setLowerBound(final int lowerBound, final SEContext context) {
        this.lowerBound = clampFrameBounds(lowerBound, context);
    }

    public void setLowerBound(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(final int upperBound, final SEContext context) {
        this.upperBound = clampFrameBounds(upperBound, context);
    }

    public void setUpperBound(final int upperBound) {
        this.upperBound = upperBound;
    }

    private static int clampFrameBounds(
            final int candidate, final SEContext context
    ) {
        return MathPlus.bounded(0, candidate, context.getState().getFrameCount() - 1);
    }

    public void setCountFrom(final int countFrom) {
        this.countFrom = countFrom;
    }

    public void setFramesPerDim(final int framesPerDim) {
        this.framesPerDim = framesPerDim;
    }

    public void setSequenceOrder(final DialogVals.SequenceOrder sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public void setSaveType(final SaveType saveType) {
        this.saveType = saveType;
    }

    public void setFps(final int fps) {
        this.fps = MathPlus.bounded(Constants.MIN_PLAYBACK_FPS,
                fps, Constants.MAX_PLAYBACK_FPS);
    }

    public void setScaleUp(final int scaleUp) {
        this.scaleUp = MathPlus.bounded(Constants.MIN_SCALE_UP,
                scaleUp, Constants.MAX_SCALE_UP);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setIndexPrefix(final String indexPrefix) {
        this.indexPrefix = indexPrefix;
    }

    public void setIndexSuffix(final String indexSuffix) {
        this.indexSuffix = indexSuffix;
    }

    public void setFolder(final Path folder) {
        this.folder = folder;
    }
}
