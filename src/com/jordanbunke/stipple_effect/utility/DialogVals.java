package com.jordanbunke.stipple_effect.utility;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stipple_effect.palette.PaletteSorter;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.scripting.util.SEScript;
import com.jordanbunke.stipple_effect.selection.Outliner;
import com.jordanbunke.stipple_effect.tools.ScriptBrush;
import com.jordanbunke.stipple_effect.visual.SEFonts;

import java.nio.file.Path;

public class DialogVals {
    private static int
            newProjectWidth = Constants.DEFAULT_CANVAS_W,
            newProjectHeight = Constants.DEFAULT_CANVAS_H,
            importFrameWidth = Constants.DEFAULT_CANVAS_W,
            importFrameHeight = Constants.DEFAULT_CANVAS_H,
            importWidth = Constants.DEFAULT_CANVAS_W,
            importHeight = Constants.DEFAULT_CANVAS_H,
            importColumns = 1, importRows = 1,
            resizeWidth = Constants.DEFAULT_CANVAS_W,
            resizeHeight = Constants.DEFAULT_CANVAS_H,
            padLeft = 0, padRight = 0, padTop = 0, padBottom = 0, padAll = 0,
            newFontPixelSpacing = Constants.DEFAULT_FONT_PX_SPACING,
            framesPerDim = 1,
            frameWidth = Constants.DEFAULT_CANVAS_W,
            frameHeight = Constants.DEFAULT_CANVAS_H,
            splitColumns = 1, splitRows = 1,
            globalOutline = 0,
            hueShift = 0, satShift = 0, valueShift = 0;
    private static double
            layerOpacity = Constants.OPAQUE,
            frameDuration = Constants.DEFAULT_FRAME_DURATION,
            resizeScale = 1d,
            satScale = 1d, valueScale = 1d,
            resizeScaleX = resizeScale, resizeScaleY = resizeScale;
    private static boolean
            hasLatinEx = false,
            charSpecificSpacing = true,
            truncateSplitX = true,
            truncateSplitY = true,
            resizePreserveAspectRatio = false,
            sortPaletteBackwards = false,
            includeDisabledLayers = false,
            ignoreSelection = false,
            colorScriptValid = false,
            shiftingSat = false,
            shiftingValue = false;
    private static int[] outlineSideMask = Outliner.getSingleOutlineMask();
    private static String
            layerName = "",
            paletteName = "",
            fontName = "";
    private static Path paletteFolder = null;
    private static InfoScreen infoScreen = InfoScreen.ABOUT;
    private static SettingScreen settingScreen = SettingScreen.DEFAULTS;
    private static PaletteSorter paletteSorter = PaletteSorter.HUE;
    private static Scope scope = Scope.PROJECT;
    private static UploadStatus
            asciiStatus = UploadStatus.UNATTEMPTED,
            latinExStatus = UploadStatus.UNATTEMPTED;
    private static SequenceOrder sequenceOrder = SequenceOrder.HORIZONTAL;
    private static ResizeBy resizeBy = ResizeBy.PIXELS;
    private static GameImage
            asciiImage = null,
            latinExImage = null,
            fontPreviewImage = GameImage.dummy();

    private static SEScript colorScript = null;

    public enum ResizeBy {
        PIXELS, SCALE_FACTOR;

        @Override
        public String toString() {
            return this == SCALE_FACTOR
                    ? "Scale factor"
                    : EnumUtils.formattedName(this);
        }
    }

    public enum SequenceOrder {
        HORIZONTAL, VERTICAL;

        @Override
        public String toString() {
            return EnumUtils.formattedName(this);
        }

        public String dimName() {
            return switch (this) {
                case HORIZONTAL -> "row";
                case VERTICAL -> "column";
            };
        }

        public SequenceOrder complement() {
            return switch (this) {
                case VERTICAL -> HORIZONTAL;
                case HORIZONTAL -> VERTICAL;
            };
        }
    }

    public enum UploadStatus {
        UNATTEMPTED, VALIDATED, INVALID;

        public String getMessage() {
            return "[ " + switch (this) {
                case INVALID -> "Invalid / upload failed";
                case VALIDATED -> "Validated image!";
                case UNATTEMPTED -> "Nothing uploaded";
            } + " ]";
        }

        public boolean isValid() {
            return this == VALIDATED;
        }
    }

    public enum InfoScreen {
        ABOUT, PROJECT, TOOLS, LAYERS, FRAMES, COLORS, MORE, SCRIPTS, CHANGES, ROADMAP;

        public String getTitle() {
            return switch (this) {
                case MORE -> "More Shortcuts & Advanced Info";
                case CHANGES -> "Changelog";
                case SCRIPTS -> "Scripting";
                default -> toString();
            };
        }

        @Override
        public String toString() {
            return EnumUtils.formattedName(this);
        }
    }

    public enum SettingScreen {
        DEFAULTS, CONTROLS, TOOLS, VISUAL;

        public String getTitle() {
            return (this == DEFAULTS ? this + " and startup" : this) + " settings";
        }

        @Override
        public String toString() {
            return EnumUtils.formattedName(this);
        }
    }

    public enum Scope {
        PROJECT, LAYER, FRAME, CEL;

        public boolean considersLayers() {
            return this == FRAME || this == PROJECT;
        }

        @Override
        public String toString() {
            return EnumUtils.formattedName(this);
        }
    }

    public static void setColorScript(final SEScript colorScript) {
        DialogVals.colorScript = colorScript;
        colorScriptValid = SEInterpreter.validateColorScript(colorScript.head());

        ScriptBrush.get().updateScript(colorScriptValid, colorScript);
    }

    public static void toggleShiftingSat() {
        shiftingSat = !shiftingSat;
    }

    public static void toggleShiftingValue() {
        shiftingValue = !shiftingValue;
    }

    public static void setPaletteFolder(final Path paletteFolder) {
        DialogVals.paletteFolder = paletteFolder;
    }

    public static void setOutlineSideMask(final int[] outlineSideMask) {
        DialogVals.outlineSideMask = outlineSideMask;
    }

    public static void setThisOutlineSide(final int index, final int value) {
        outlineSideMask[index] = value;
    }

    public static void setGlobalOutline(final int globalOutline) {
        DialogVals.globalOutline = globalOutline;

        for (int i = 0; i < Outliner.Direction.values().length; i++)
            outlineSideMask[i] = globalOutline;
    }

    public static void setInfoScreen(final InfoScreen infoScreen) {
        DialogVals.infoScreen = infoScreen;
    }

    public static void setSettingScreen(final SettingScreen settingScreen) {
        DialogVals.settingScreen = settingScreen;
    }

    public static void setPaletteSorter(final PaletteSorter paletteSorter) {
        DialogVals.paletteSorter = paletteSorter;
    }

    public static void setScope(final Scope scope) {
        DialogVals.scope = scope;
    }

    public static void setLayerName(final String layerName) {
        DialogVals.layerName = layerName;
    }

    public static void setPaletteName(final String paletteName) {
        DialogVals.paletteName = paletteName;
    }

    public static void setLayerOpacity(final double layerOpacity) {
        DialogVals.layerOpacity = layerOpacity;
    }

    public static void setFrameDuration(final double frameDuration) {
        DialogVals.frameDuration = frameDuration;
    }

    public static void setResizeScale(final double resizeScale) {
        DialogVals.resizeScale = resizeScale;
    }

    public static void setResizeScaleX(final double resizeScaleX) {
        DialogVals.resizeScaleX = resizeScaleX;
    }

    public static void setResizeScaleY(final double resizeScaleY) {
        DialogVals.resizeScaleY = resizeScaleY;
    }

    public static void setHueShift(final int hueShift) {
        DialogVals.hueShift = hueShift;
    }

    public static void setSatShift(final int satShift) {
        DialogVals.satShift = satShift;
    }

    public static void setValueShift(final int valueShift) {
        DialogVals.valueShift = valueShift;
    }

    public static void setSatScale(final double satScale) {
        DialogVals.satScale = satScale;
    }

    public static void setValueScale(final double valueScale) {
        DialogVals.valueScale = valueScale;
    }

    public static void setNewProjectHeight(final int newProjectHeight) {
        DialogVals.newProjectHeight = newProjectHeight;
    }

    public static void setNewProjectWidth(final int newProjectWidth) {
        DialogVals.newProjectWidth = newProjectWidth;
    }

    public static void setImportFrameHeight(
            final int importFrameHeight, final int canvasHeight
    ) {
        final int clampedIFH = MathPlus.bounded(Constants.MIN_CANVAS_H,
                importFrameHeight, canvasHeight);

        DialogVals.importFrameHeight = clampedIFH;
        setImportRows(canvasHeight / clampedIFH);
    }

    public static void setImportFrameWidth(
            final int importFrameWidth, final int canvasWidth
    ) {
        final int clampedIFW = MathPlus.bounded(Constants.MIN_CANVAS_W,
                importFrameWidth, canvasWidth);

        DialogVals.importFrameWidth = clampedIFW;
        setImportColumns(canvasWidth / clampedIFW);
    }

    public static void setImportHeight(
            final int importHeight, final int refWidth, final int refHeight,
            final boolean adjustComplement
    ) {
        DialogVals.importHeight = importHeight;
        setImportRows(importRows, importHeight);

        if (resizePreserveAspectRatio && adjustComplement) {
            final double ratio = refWidth / (double) refHeight;
            final int importWidth = Math.max(Constants.MIN_CANVAS_W,
                    (int) Math.round(ratio * importHeight));
            setImportWidth(importWidth, refWidth, refHeight, false);
        }
    }

    public static void setImportWidth(
            final int importWidth, final int refWidth, final int refHeight,
            final boolean adjustComplement
    ) {
        DialogVals.importWidth = importWidth;
        setImportColumns(importColumns, importWidth);

        if (resizePreserveAspectRatio && adjustComplement) {
            final double ratio = refHeight / (double) refWidth;
            final int importHeight = Math.max(Constants.MIN_CANVAS_H,
                    (int) Math.round(ratio * importWidth));
            setImportHeight(importHeight, refWidth, refHeight, false);
        }
    }

    public static void setPadAll(final int padAll) {
        DialogVals.padAll = padAll;

        setPadLeft(padAll);
        setPadRight(padAll);
        setPadTop(padAll);
        setPadBottom(padAll);
    }

    public static void setPadBottom(final int padBottom) {
        DialogVals.padBottom = padBottom;
    }

    public static void setPadLeft(final int padLeft) {
        DialogVals.padLeft = padLeft;
    }

    public static void setPadRight(final int padRight) {
        DialogVals.padRight = padRight;
    }

    public static void setPadTop(final int padTop) {
        DialogVals.padTop = padTop;
    }

    public static void setResizeHeight(
            final int resizeHeight, final int refWidth, final int refHeight,
            final boolean adjustComplement
    ) {
        DialogVals.resizeHeight = resizeHeight;

        if (resizePreserveAspectRatio && adjustComplement) {
            final double ratio = refWidth / (double) refHeight;
            final int resizeWidth = MathPlus.bounded(
                    Constants.MIN_CANVAS_W,
                    (int) Math.round(ratio * resizeHeight),
                    Constants.MAX_CANVAS_W);
            setResizeWidth(resizeWidth, refWidth, refHeight, false);
        }
    }

    public static void setResizeWidth(
            final int resizeWidth, final int refWidth, final int refHeight,
            final boolean adjustComplement
    ) {
        DialogVals.resizeWidth = resizeWidth;

        if (resizePreserveAspectRatio && adjustComplement) {
            final double ratio = refHeight / (double) refWidth;
            final int resizeHeight = MathPlus.bounded(
                    Constants.MIN_CANVAS_H,
                    (int) Math.round(ratio * resizeWidth),
                    Constants.MAX_CANVAS_H);
            setResizeHeight(resizeHeight, refWidth, refHeight, false);
        }
    }

    public static void setImportColumns(final int importColumns) {
        DialogVals.importColumns = MathPlus.bounded(1, importColumns,
                Constants.MAX_NUM_FRAMES);
    }

    public static void setImportColumns(
            final int importColumns, final int canvasWidth
    ) {
        setImportFrameWidth(canvasWidth / importColumns, canvasWidth);
    }

    public static void setImportRows(final int importRows) {
        DialogVals.importRows = MathPlus.bounded(1, importRows,
                Constants.MAX_NUM_FRAMES);
    }

    public static void setImportRows(
            final int importRows, final int canvasHeight
    ) {
        setImportFrameHeight(canvasHeight / importRows, canvasHeight);
    }

    public static void setNewFontPixelSpacing(final int newFontPixelSpacing) {
        setNewFontPixelSpacing(newFontPixelSpacing, true);
    }

    public static void setFramesPerDim(final int framesPerDim) {
        DialogVals.framesPerDim = framesPerDim;
    }

    public static void setFrameWidth(
            final int frameWidth, final int canvasWidth
    ) {
        final int clampedFW = MathPlus.bounded(Constants.MIN_CANVAS_W,
                frameWidth, canvasWidth);

        DialogVals.frameWidth = clampedFW;
        setSplitColumns(canvasWidth / clampedFW);
    }

    public static void setFrameHeight(
            final int frameHeight, final int canvasHeight
    ) {
        final int clampedFH = MathPlus.bounded(Constants.MIN_CANVAS_H,
                frameHeight, canvasHeight);

        DialogVals.frameHeight = clampedFH;
        setSplitRows(canvasHeight / clampedFH);
    }

    public static void setSplitColumns(final int splitColumns) {
        DialogVals.splitColumns = splitColumns;
    }

    public static void setSplitColumns(final int splitColumns, final int canvasWidth) {
        setFrameWidth(canvasWidth / splitColumns, canvasWidth);
    }

    public static void setSplitRows(final int splitRows) {
        DialogVals.splitRows = splitRows;
    }

    public static void setSplitRows(final int splitRows, final int canvasHeight) {
        setFrameHeight(canvasHeight / splitRows, canvasHeight);
    }

    public static void setIncludeDisabledLayers(final boolean includeDisabledLayers) {
        DialogVals.includeDisabledLayers = includeDisabledLayers;
    }

    public static void setIgnoreSelection(final boolean ignoreSelection) {
        DialogVals.ignoreSelection = ignoreSelection;
    }

    public static void setResizePreserveAspectRatio(
            final boolean resizePreserveAspectRatio
    ) {
        DialogVals.resizePreserveAspectRatio = resizePreserveAspectRatio;
    }

    public static void setTruncateSplitX(final boolean truncateSplitX) {
        DialogVals.truncateSplitX = truncateSplitX;
    }

    public static void setTruncateSplitY(final boolean truncateSplitY) {
        DialogVals.truncateSplitY = truncateSplitY;
    }

    public static void setNewFontPixelSpacing(
            final int newFontPixelSpacing, final boolean attemptPreviewUpdate
    ) {
        DialogVals.newFontPixelSpacing = newFontPixelSpacing;

        if (attemptPreviewUpdate)
            SEFonts.attemptPreviewUpdate();
    }

    public static void setHasLatinEx(final boolean hasLatinEx) {
        setHasLatinEx(hasLatinEx, true);
    }

    public static void setHasLatinEx(
            final boolean hasLatinEx, final boolean attemptPreviewUpdate
    ) {
        DialogVals.hasLatinEx = hasLatinEx;

        if (attemptPreviewUpdate)
            SEFonts.attemptPreviewUpdate();
    }

    public static void setCharSpecificSpacing(final boolean charSpecificSpacing) {
        setCharSpecificSpacing(charSpecificSpacing, true);
    }

    public static void setCharSpecificSpacing(
            final boolean charSpecificSpacing, final boolean attemptPreviewUpdate
    ) {
        DialogVals.charSpecificSpacing = charSpecificSpacing;

        if (attemptPreviewUpdate)
            SEFonts.attemptPreviewUpdate();
    }

    public static void setSortPaletteBackwards(final boolean sortPaletteBackwards) {
        DialogVals.sortPaletteBackwards = sortPaletteBackwards;
    }

    public static void setFontName(final String fontName) {
        DialogVals.fontName = fontName;
    }

    public static void resetAscii() {
        asciiStatus = UploadStatus.UNATTEMPTED;
        asciiImage = null;
    }

    public static void asciiFailedValidation() {
        asciiStatus = UploadStatus.INVALID;
        asciiImage = null;
    }

    public static void asciiPassedValidation(
            final GameImage asciiImage
    ) {
        asciiStatus = UploadStatus.VALIDATED;
        DialogVals.asciiImage = asciiImage;

        SEFonts.attemptPreviewUpdate();
    }

    public static void resetLatinEx() {
        latinExStatus = UploadStatus.UNATTEMPTED;
        latinExImage = null;
    }

    public static void latinExFailedValidation() {
        latinExStatus = UploadStatus.INVALID;
        latinExImage = null;
    }

    public static void latinExPassedValidation(
            final GameImage latinExImage
    ) {
        latinExStatus = UploadStatus.VALIDATED;
        DialogVals.latinExImage = latinExImage;

        SEFonts.attemptPreviewUpdate();
    }

    public static void setFontPreviewImage(
            final GameImage fontPreviewImage
    ) {
        DialogVals.fontPreviewImage = fontPreviewImage;
    }

    public static void setSequenceOrder(final SequenceOrder sequenceOrder) {
        DialogVals.sequenceOrder = sequenceOrder;
    }

    public static void setResizeBy(final ResizeBy resizeBy) {
        DialogVals.resizeBy = resizeBy;
    }

    public static String colorScriptMessage() {
        if (isColorScriptValid())
            return "Validated";
        else if (colorScript == null)
            return "No script / upload failed";
        else
            return "Invalid";
    }

    public static boolean isColorScriptValid() {
        return colorScriptValid;
    }

    public static SEScript getColorScript() {
        return colorScript;
    }

    public static Path getPaletteFolder() {
        return paletteFolder;
    }

    public static InfoScreen getInfoScreen() {
        return infoScreen;
    }

    public static SettingScreen getSettingScreen() {
        return settingScreen;
    }

    public static PaletteSorter getPaletteSorter() {
        return paletteSorter;
    }

    public static Scope getScope() {
        return scope;
    }

    public static boolean isShiftingSat() {
        return shiftingSat;
    }

    public static boolean isShiftingValue() {
        return shiftingValue;
    }

    public static int getHueShift() {
        return hueShift;
    }

    public static int getSatShift() {
        return satShift;
    }

    public static int getValueShift() {
        return valueShift;
    }

    public static double getSatScale() {
        return satScale;
    }

    public static double getValueScale() {
        return valueScale;
    }

    public static int getImportFrameHeight() {
        return importFrameHeight;
    }

    public static int getImportFrameWidth() {
        return importFrameWidth;
    }

    public static int getImportHeight() {
        return importHeight;
    }

    public static int getImportWidth() {
        return importWidth;
    }

    public static int getNewProjectHeight() {
        return newProjectHeight;
    }

    public static int getNewProjectWidth() {
        return newProjectWidth;
    }

    public static int getPadAll() {
        return padAll;
    }

    public static int getPadBottom() {
        return padBottom;
    }

    public static int getPadLeft() {
        return padLeft;
    }

    public static int getPadRight() {
        return padRight;
    }

    public static int getPadTop() {
        return padTop;
    }

    public static int getResizeHeight() {
        return resizeHeight;
    }

    public static int getResizeWidth() {
        return resizeWidth;
    }

    public static int calculcateResizeWidth(final int refWidth) {
        return resizeBy == ResizeBy.PIXELS ? resizeWidth
                : (int) Math.round(refWidth * (
                        resizePreserveAspectRatio
                                ? resizeScale : resizeScaleX));
    }

    public static int calculateResizeHeight(final int refHeight) {
        return resizeBy == ResizeBy.PIXELS ? resizeHeight
                : (int) Math.round(refHeight * (
                resizePreserveAspectRatio
                        ? resizeScale : resizeScaleY));
    }

    public static double getLayerOpacity() {
        return layerOpacity;
    }

    public static double getFrameDuration() {
        return frameDuration;
    }

    public static double getResizeScale() {
        return resizeScale;
    }

    public static double getResizeScaleX() {
        return resizeScaleX;
    }

    public static double getResizeScaleY() {
        return resizeScaleY;
    }

    public static String getLayerName() {
        return layerName;
    }

    public static String getPaletteName() {
        return paletteName;
    }

    public static int getImportColumns() {
        return importColumns;
    }

    public static int getImportRows() {
        return importRows;
    }

    public static int getNewFontPixelSpacing() {
        return newFontPixelSpacing;
    }

    public static boolean hasLatinEx() {
        return hasLatinEx;
    }

    public static boolean isCharSpecificSpacing() {
        return charSpecificSpacing;
    }

    public static boolean isIncludeDisabledLayers() {
        return includeDisabledLayers;
    }

    public static boolean isIgnoreSelection() {
        return ignoreSelection;
    }

    public static String getFontName() {
        return fontName;
    }

    public static UploadStatus getAsciiStatus() {
        return asciiStatus;
    }

    public static UploadStatus getLatinExStatus() {
        return latinExStatus;
    }

    public static GameImage getAsciiImage() {
        return asciiImage;
    }

    public static GameImage getLatinExImage() {
        return latinExImage;
    }

    public static GameImage getFontPreviewImage() {
        return fontPreviewImage;
    }

    public static SequenceOrder getSequenceOrder() {
        return sequenceOrder;
    }

    public static ResizeBy getResizeBy() {
        return resizeBy;
    }

    public static int getFramesPerDim() {
        return framesPerDim;
    }

    public static int calculateFramesPerComplementaryDim(
            final int frameCount) {
        return calculateFramesPerComplementaryDim(
                frameCount, getFramesPerDim());
    }

    public static int calculateFramesPerComplementaryDim(
            final int frameCount, final int fpd
    ) {
        final int compTruncated = frameCount / fpd,
                remainder = frameCount % fpd;

        return compTruncated + (remainder == 0 ? 0 : 1);
    }

    public static int getFrameWidth() {
        return frameWidth;
    }

    public static int getFrameHeight() {
        return frameHeight;
    }

    public static int getSplitColumns() {
        return splitColumns;
    }

    public static int getSplitRows() {
        return splitRows;
    }

    public static boolean isTruncateSplitX() {
        return truncateSplitX;
    }

    public static boolean isTruncateSplitY() {
        return truncateSplitY;
    }

    public static boolean isResizePreserveAspectRatio() {
        return resizePreserveAspectRatio;
    }

    public static boolean isSortPaletteBackwards() {
        return sortPaletteBackwards;
    }

    public static int getThisOutlineSide(final int index) {
        return outlineSideMask[index];
    }

    public static int getGlobalOutline() {
        return globalOutline;
    }

    public static int[] getOutlineSideMask() {
        return outlineSideMask;
    }
}
