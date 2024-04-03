package com.jordanbunke.stipple_effect.project;

import com.jordanbunke.anim.AnimWriter;
import com.jordanbunke.anim.GIFWriter;
import com.jordanbunke.anim.MP4Writer;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.MathPlus;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.stip.ParserSerializer;
import com.jordanbunke.stipple_effect.utility.math.StitchSplitMath;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.DialogAssembly;
import com.jordanbunke.stipple_effect.selection.SelectionMode;
import com.jordanbunke.stipple_effect.utility.*;

import java.nio.file.Path;
import java.util.function.BinaryOperator;

public class ProjectInfo {
    private static final int INVALID_BOUND = -1;

    private Path folder;
    private String name, indexPrefix, indexSuffix;
    private SaveType saveType;

    private boolean editedSinceLastSave, saveRangeOfFrames;

    private int framesPerDim, fps, scaleUp, countFrom, lowerBound, upperBound;

    public enum SaveType {
        NATIVE, PNG_STITCHED, PNG_SEPARATE, GIF, MP4;

        public String getFileSuffix() {
            return switch (this) {
                case PNG_SEPARATE, PNG_STITCHED -> "png";
                case GIF -> "gif";
                case MP4 -> "mp4";
                case NATIVE -> Constants.NATIVE_FILE_SUFFIX;
            };
        }

        public String getButtonText(final ProjectInfo projectInfo) {
            return switch (this) {
                case PNG_STITCHED -> "Single PNG" +
                        (projectInfo.isAnimation()
                                ? " (spritesheet)" : "");
                case PNG_SEPARATE -> "Separate PNGs per frame";
                case GIF -> "Animated GIF";
                case MP4 -> "MP4 Video";
                case NATIVE -> StippleEffect.PROGRAM_NAME +
                        " file (." + getFileSuffix() + ")";
            };
        }
    }

    public ProjectInfo() {
        this(null);
    }

    public ProjectInfo(final Path filepath) {
        if (filepath == null) {
            folder = null;
            name = "";
        } else {
            folder = filepath.getParent();
            final String filename = filepath.getFileName().toString();

            name = filename.contains(".") ? filename.substring(0,
                    filename.lastIndexOf(".")) : filename;
        }

        editedSinceLastSave = false;
        saveType = filepath != null && filepath.getFileName()
                .toString().endsWith(SaveType.NATIVE.getFileSuffix())
                ? SaveType.NATIVE : SaveType.PNG_STITCHED;
        framesPerDim = Constants.MAX_NUM_FRAMES;
        fps = Constants.DEFAULT_PLAYBACK_FPS;
        scaleUp = Constants.DEFAULT_SAVE_SCALE_UP;

        countFrom = 1;
        indexPrefix = Settings.getDefaultIndexPrefix();
        indexSuffix = Settings.getDefaultIndexSuffix();

        saveRangeOfFrames = false;
        lowerBound = INVALID_BOUND;
        upperBound = INVALID_BOUND;
    }

    public void save() {
        if (!hasSaveAssociation()) {
            DialogAssembly.setDialogToSave();
            return;
        }

        StatusUpdates.saving();

        final SEContext c = StippleEffect.get().getContext();

        if (c.getState().getSelectionMode() == SelectionMode.CONTENTS &&
                c.getState().hasSelection())
            c.dropContentsToLayer(true, false);

        final int w = c.getState().getImageWidth(),
                h = c.getState().getImageHeight(),
                framesToSave = calculateNumFrames(),
                f0 = saveRangeOfFrames ? lowerBound : 0;

        if (saveType == SaveType.NATIVE) {
            final Thread stipSaverThread = new Thread(() -> {
                final Path filepath = buildFilepath();

                ParserSerializer.save(c, filepath);
                StatusUpdates.saved(filepath);
            });

            stipSaverThread.start();
        } else {
            final int frameCount = c.getState().getFrameCount();

            if (f0 + framesToSave > frameCount) {
                StatusUpdates.saveFailed();
                return;
            }

            switch (saveType) {
                case GIF, MP4 -> {
                    final GameImage[] images = new GameImage[framesToSave];

                    for (int i = 0; i < framesToSave; i++) {
                        images[i] = c.getState().draw(
                                false, false, f0 + i);

                        if (scaleUp > 1)
                            images[i] = ImageProcessing.scale(images[i], scaleUp);
                    }

                    final AnimWriter writer = saveType == SaveType.GIF
                            ? GIFWriter.get() : MP4Writer.get();

                    final Thread animSaverThread = new Thread(() -> {
                        final Path filepath = buildFilepath();

                        writer.write(filepath, images,
                                Constants.MILLIS_IN_SECOND / fps);
                        StatusUpdates.saved(filepath);
                    });

                    animSaverThread.start();
                }
                case PNG_SEPARATE -> {
                    for (int i = 0; i < framesToSave; i++) {
                        final GameImage image = c.getState().draw(
                                false, false, f0 + i);
                        GameImageIO.writeImage(buildFilepath(countFrom + i),
                                scaleUp > 1
                                        ? ImageProcessing.scale(image, scaleUp)
                                        : image);
                    }

                    StatusUpdates.savedAllFrames(folder);
                }
                case PNG_STITCHED -> {
                    final int fpd = getFramesPerDim(),
                            fpcd = getFramesPerCompDim(),
                            sw = w * (StitchSplitMath.isHorizontal() ? fpd : fpcd),
                            sh = h * (StitchSplitMath.isHorizontal() ? fpcd : fpd);

                    final boolean isHorizontal = StitchSplitMath.isHorizontal();
                    final BinaryOperator<Integer>
                            horz = (a, b) -> a % b,
                            vert = (a, b) -> a / b,
                            xOp = isHorizontal ? horz : vert,
                            yOp = isHorizontal ? vert : horz;

                    final GameImage stitched = new GameImage(sw, sh);

                    for (int i = 0; i < framesToSave; i++) {
                        final int x = xOp.apply(i, fpd) * w,
                                y = yOp.apply(i, fpd) * h;

                        stitched.draw(c.getState().draw(
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

    public void markAsEdited() {
        editedSinceLastSave = true;
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

    public boolean isAnimation() {
        return StippleEffect.get().getContext().getState().getFrameCount() > 1;
    }

    public SaveType[] getSaveOptions() {
        if (isAnimation())
            return SaveType.values();

        return new SaveType[] { SaveType.NATIVE, SaveType.PNG_STITCHED };
    }

    public int calculateNumFrames() {
        if (!saveRangeOfFrames)
            return StippleEffect.get().getContext().getState().getFrameCount();

        return (upperBound - lowerBound) + 1;
    }

    public boolean hasSaveAssociation() {
        return folder != null && !name.equals("");
    }

    public boolean hasUnsavedChanges() {
        return editedSinceLastSave;
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

    public int getFramesPerCompDim() {
        return DialogVals.calculateFramesPerComplementaryDim(
                calculateNumFrames(), framesPerDim);
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

    public void setLowerBound(final int lowerBound) {
        this.lowerBound = clampFrameBounds(lowerBound);
    }

    public void setUpperBound(final int upperBound) {
        this.upperBound = clampFrameBounds(upperBound);
    }

    private int clampFrameBounds(final int candidate) {
        return MathPlus.bounded(0, candidate, StippleEffect.get()
                .getContext().getState().getFrameCount() - 1);
    }

    public void setCountFrom(final int countFrom) {
        this.countFrom = countFrom;
    }

    public void setFramesPerDim(final int framesPerDim) {
        this.framesPerDim = framesPerDim;
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
