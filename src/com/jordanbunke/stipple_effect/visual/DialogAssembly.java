package com.jordanbunke.stipple_effect.visual;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.fonts.FontConstants;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.PlaceholderMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.TimedMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.AnimationMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.ConcreteProperty;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.layer.OnionSkin;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.palette.PaletteSorter;
import com.jordanbunke.stipple_effect.project.PlaybackInfo;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.selection.*;
import com.jordanbunke.stipple_effect.state.Operation;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.stip.ParserSerializer;
import com.jordanbunke.stipple_effect.tools.TextTool;
import com.jordanbunke.stipple_effect.tools.Tool;
import com.jordanbunke.stipple_effect.utility.*;
import com.jordanbunke.stipple_effect.utility.action.ResourceCodes;
import com.jordanbunke.stipple_effect.utility.action.SEAction;
import com.jordanbunke.stipple_effect.utility.math.StitchSplitMath;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.menu_elements.Checkbox;
import com.jordanbunke.stipple_effect.visual.menu_elements.*;
import com.jordanbunke.stipple_effect.visual.menu_elements.colors.ColorComponent;
import com.jordanbunke.stipple_effect.visual.menu_elements.dialog.*;
import com.jordanbunke.stipple_effect.visual.menu_elements.scrollable.VerticalScrollBox;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.DynamicTextButton;
import com.jordanbunke.stipple_effect.visual.menu_elements.text_button.StaticTextButton;
import com.jordanbunke.stipple_effect.visual.theme.SEColors;
import com.jordanbunke.stipple_effect.visual.theme.Theme;
import com.jordanbunke.stipple_effect.visual.theme.Themes;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.stipple_effect.utility.Layout.*;

public class DialogAssembly {
    private static final int LINE_ABOVE = -2, AFTER_COMMON_COLOR_ACTION_ROW = 4;

    public static void setDialogToSave(final SEContext c) {
        final SaveConfig sc = c.getSaveConfig();

        // preprocessing logic
        // ensure frame bounds validity
        sc.setLowerBound(sc.isSaveRangeOfFrames()
                ? sc.getLowerBound() : 0, c);
        sc.setUpperBound(sc.isSaveRangeOfFrames()
                ? sc.getUpperBound() : c.getState().getFrameCount() - 1, c);

        final MenuBuilder mb = new MenuBuilder();

        // text labels
        final TextLabel
                folderLabel = makeDialogLeftLabel(0, "Folder"),
                nameLabel = TextLabel.make(textBelowPos(folderLabel),
                        "File name"),
                saveAsTypeLabel = TextLabel.make(textBelowPos(nameLabel),
                        "Save as"),
                scaleUpLabel = TextLabel.make(textBelowPos(saveAsTypeLabel),
                        "Scale factor"),
                frameBoundsLabel = TextLabel.make(textBelowPos(scaleUpLabel),
                        "Save limited range of frames?"),
                lowerBoundsLabel = TextLabel.make(textBelowPos(frameBoundsLabel),
                        "Lower bound"),
                upperBoundsLabel = makeDialogRightLabel(
                        lowerBoundsLabel, "Upper bound");
        mb.addAll(folderLabel, nameLabel, saveAsTypeLabel);

        final TextLabel indexPrefixLabel = TextLabel.make(
                        textBelowPos(lowerBoundsLabel, 1),
                        "Prefix"),
                indexSuffixLabel = makeDialogRightLabel(indexPrefixLabel, "Suffix"),
                countFromLabel = TextLabel.make(textBelowPos(indexPrefixLabel),
                        "Count from"),
                fpsLabel = TextLabel.make(
                        textBelowPos(lowerBoundsLabel, 1),
                        "Frame rate");

        // folder selection button
        final DynamicTextButton folderButton = makeFolderSelectionButton(
                folderLabel, sc::getFolder, sc::setFolder);
        mb.add(folderButton);

        // name text box
        final Textbox nameTextbox = makeDialogNameTextBox(nameLabel,
                sc.getName(), sc::setName);
        mb.add(nameTextbox);

        // save as type dropdown
        final SaveConfig.SaveType[] saveOptions = SaveConfig.getSaveOptions(c);
        final Dropdown saveAsTypeDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(saveAsTypeLabel),
                DIALOG_CONTENT_BIG_W_ALLOWANCE,
                Arrays.stream(saveOptions)
                        .map(st -> st.getButtonText(c))
                        .toArray(String[]::new),
                Arrays.stream(saveOptions)
                        .map(type -> (Runnable)
                                () -> sc.setSaveType(type))
                        .toArray(Runnable[]::new), () -> {
                    final SaveConfig.SaveType saveType = sc.getSaveType();

                    for (int i = 0; i < saveOptions.length; i++)
                        if (saveType == saveOptions[i])
                            return i;

                    sc.setSaveType(saveOptions[0]);
                    return 0;
                });
        mb.add(saveAsTypeDropdown);

        // scale up
        final IncrementalRangeElements<Integer> scaleUp =
                IncrementalRangeElements.makeForInt(
                        scaleUpLabel, scaleUpLabel.getY() +
                                DIALOG_CONTENT_COMP_OFFSET_Y,
                        scaleUpLabel.getY(), 1,
                        Constants.MIN_SCALE_UP, Constants.MAX_SCALE_UP,
                        sc::setScaleUp, sc::getScaleUp,
                        i -> i, i -> i, i -> i + "x", "XXx");
        final GatewayMenuElement scaleUpGateway = new GatewayMenuElement(
                new MenuElementGrouping(scaleUpLabel,
                        scaleUp.decButton, scaleUp.incButton,
                        scaleUp.slider, scaleUp.value),
                () -> !sc.getSaveType().equals(SaveConfig.SaveType.NATIVE));
        mb.add(scaleUpGateway);

        // frame bounds: (only save from frame A to B)
        final Supplier<Boolean> frameBoundsCondition =
                () -> c.isAnimation() &&
                        !sc.getSaveType().equals(SaveConfig.SaveType.NATIVE);
        final Checkbox frameBoundsCheckbox = new Checkbox(
                contentPositionAfterLabel(frameBoundsLabel),
                new ConcreteProperty<>(
                        sc::isSaveRangeOfFrames,
                        sc::setSaveRangeOfFrames));
        final String FRAME_NUM_PREFIX = "Frm. ";
        final DynamicTextbox lowerBoundTextbox =
                makeDialogNumericalDynamicTextbox(lowerBoundsLabel,
                        Layout::contentPositionAfterLabel,
                        FRAME_NUM_PREFIX, sc.getLowerBound() + 1,
                        "", tbv -> {
                    final int boundValue = tbv - 1;

                    final boolean inBounds = boundValue >= 0 &&
                            boundValue < c.getState().getFrameCount();
                    final boolean lowerThanUpper = boundValue <=
                            sc.getUpperBound();

                    return inBounds && lowerThanUpper;
                    }, tbv -> sc.setLowerBound(tbv - 1, c),
                        () -> sc.getLowerBound() + 1, 3),
                upperBoundTextbox = makeDialogNumericalDynamicTextbox(
                        upperBoundsLabel,
                        Layout::contentPositionAfterLabel,
                        FRAME_NUM_PREFIX, sc.getUpperBound() + 1,
                        "", tbv -> {
                            final int boundValue = tbv - 1;

                            final boolean inBounds = boundValue >= 0 &&
                                    boundValue < c.getState().getFrameCount();
                            final boolean higherThanLower = boundValue >=
                                    sc.getLowerBound();

                            return inBounds && higherThanLower;
                        }, tbv -> sc.setUpperBound(tbv - 1, c),
                        () -> sc.getUpperBound() + 1, 3);
        final GatewayMenuElement nestedBoundsGateway = new GatewayMenuElement(
                new MenuElementGrouping(
                        lowerBoundsLabel, lowerBoundTextbox,
                        upperBoundsLabel, upperBoundTextbox),
                () -> frameBoundsCondition.get() &&
                        sc.isSaveRangeOfFrames()),
                frameBoundsGateway = new GatewayMenuElement(
                        new MenuElementGrouping(
                                frameBoundsLabel, frameBoundsCheckbox,
                                nestedBoundsGateway),
                        frameBoundsCondition);
        mb.add(frameBoundsGateway);

        final MenuBuilder stitchMB = new MenuBuilder();

        makeStitchElementsForSaveSpriteSheet(stitchMB, c, lowerBoundsLabel);

        final MenuElementGrouping pngStitchedContents =
                new MenuElementGrouping(stitchMB.build().getMenuElements());

        // GIF playback speed iff saveType is GIF
        final IncrementalRangeElements<Integer> playbackSpeed =
                IncrementalRangeElements.makeForInt(fpsLabel,
                        fpsLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                        fpsLabel.getY(), 1, Constants.MIN_PLAYBACK_FPS,
                        Constants.MAX_PLAYBACK_FPS, sc::setFps, sc::getFps,
                        i -> i, sv -> sv, i -> i + " FPS", "XXX FPS");

        // GIF and MP4 contents
        final MenuElementGrouping gifContents = new MenuElementGrouping(
                fpsLabel, playbackSpeed.decButton, playbackSpeed.incButton,
                playbackSpeed.slider, playbackSpeed.value);

        // Extra file naming options IFF saveType is PNG_SEPARATE
        final DynamicTextbox indexPrefixTextbox =
                makeDialogAffixDynamicTextbox(indexPrefixLabel,
                        sc::setIndexPrefix, sc::getIndexPrefix),
                indexSuffixTextbox =
                        makeDialogAffixDynamicTextbox(indexSuffixLabel,
                                sc::setIndexSuffix, sc::getIndexSuffix);
        final Textbox countFromTextbox = makeDialogCustomTextBox(
                countFromLabel, DIALOG_CONTENT_SMALL_W_ALLOWANCE,
                Layout::contentPositionAfterLabel, sc::getIndexPrefix,
                String.valueOf(sc.getCountFrom()), sc::getIndexSuffix,
                Textbox.getIntTextValidator(i -> true),
                s -> sc.setCountFrom(Integer.parseInt(s)), 4);

        final MenuElementGrouping pngSeparateContents = new MenuElementGrouping(
                indexPrefixLabel, indexPrefixTextbox,
                indexSuffixLabel, indexSuffixTextbox,
                countFromLabel, countFromTextbox);

        // save type decision maker
        final ThinkingMenuElement basedOnSaveType = new ThinkingMenuElement(() ->
                switch (sc.getSaveType()) {
                    case PNG_SHEET -> c.isAnimation()
                            ? pngStitchedContents : new PlaceholderMenuElement();
                    case PNG_SEPARATE -> pngSeparateContents;
                    case GIF, MP4 -> gifContents;
                    default -> new PlaceholderMenuElement();
        });
        mb.add(basedOnSaveType);

        // dynamic label preview
        final DynamicLabel preview = makeDialogLeftDynamicLabelAtBottom(() -> {
            if (!sc.hasSaveAssociation())
                return "[ Select a folder and provide a name ]";

            final int PRINT_LETTERS = 64, ENDS = (PRINT_LETTERS / 2) - 1;

            final boolean multipleFiles = sc.getSaveType()
                    .equals(SaveConfig.SaveType.PNG_SEPARATE);

            final Path filepath = multipleFiles
                    ? sc.buildFilepath(sc.getCountFrom())
                    : sc.buildFilepath();

            final String composed = filepath +
                            (multipleFiles ? " + others" : "");

            return composed.length() <= PRINT_LETTERS
                    ? composed : composed.substring(0, ENDS) + "..." +
                    composed.substring(composed.length() - ENDS);
        });
        final TextLabel destinationLabel = TextLabel.make(textBelowPos(
                preview, LINE_ABOVE), "Destination:");
        mb.addAll(preview, destinationLabel);

        // content assembly
        final MenuElementGrouping contents = new MenuElementGrouping(
                mb.build().getMenuElements());
        setDialog(assembleDialog("Save project...", contents,
                sc::hasSaveAssociation, "Save", () -> sc.save(c), true));
    }

    public static void setDialogToResize(final SEContext c) {
        final int w = c.getState().getImageWidth(),
                h = c.getState().getImageHeight();

        DialogVals.setResizeScale(1d);
        DialogVals.setResizeScaleX(1d);
        DialogVals.setResizeScaleY(1d);

        DialogVals.setResizeWidth(w, w, h, false);
        DialogVals.setResizeHeight(h, w, h, false);

        final MenuBuilder mb = new MenuBuilder();

        // text labels
        final TextLabel context =
                makeDialogLeftLabel(0, "Current size: " + w + "x" + h),
                explanation = makeValidDimensionsBottomLabel();
        mb.addAll(context, explanation);

        final TextLabel preserveAspectRatioLabel =
                TextLabel.make(textBelowPos(context, 1),
                        "Preserve aspect ratio?");
        final Checkbox preserveAspectRatioCheckbox = new Checkbox(
                contentPositionAfterLabel(preserveAspectRatioLabel),
                new ConcreteProperty<>(
                        DialogVals::isResizePreserveAspectRatio,
                        DialogVals::setResizePreserveAspectRatio));
        mb.addAll(preserveAspectRatioLabel, preserveAspectRatioCheckbox);

        final TextLabel resizeTypeLabel =
                TextLabel.make(textBelowPos(preserveAspectRatioLabel),
                        "Resize by");
        final Dropdown resizeByDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(resizeTypeLabel),
                EnumUtils.stream(DialogVals.ResizeBy.class)
                        .map(DialogVals.ResizeBy::toString)
                        .toArray(String[]::new),
                EnumUtils.stream(DialogVals.ResizeBy.class)
                        .map(rb -> (Runnable) () -> DialogVals.setResizeBy(rb))
                        .toArray(Runnable[]::new),
                () -> DialogVals.getResizeBy().ordinal());
        mb.addAll(resizeTypeLabel, resizeByDropdown);

        // case 1: resize by scale and preserve aspect ratio
        final TextLabel universalScaleLabel = TextLabel.make(
                textBelowPos(resizeTypeLabel, 1),
                "Scale factor");
        final Textbox universalScaleTextbox = makeDialogCustomTextBox(
                universalScaleLabel, SMALL_TEXT_BOX_W,
                Layout::contentPositionAfterLabel,
                () -> "", String.valueOf(DialogVals.getResizeScale()), () -> "",
                Textbox.getPositiveFloatValidator(),
                s -> DialogVals.setResizeScale(Double.parseDouble(s)), 5);

        // case 2: resize by scale but aspect ratio is free
        final TextLabel scaleXLabel = TextLabel.make(
                textBelowPos(resizeTypeLabel, 1),
                "Scale factor (X)"),
                scaleYLabel = makeDialogRightLabel(
                        scaleXLabel, "Scale factor (Y)");
        final Textbox scaleXTextbox = makeDialogCustomTextBox(
                scaleXLabel, SMALL_TEXT_BOX_W,
                Layout::contentPositionAfterLabel,
                () -> "", String.valueOf(DialogVals.getResizeScaleX()),
                () -> "", Textbox.getPositiveFloatValidator(),
                s -> DialogVals.setResizeScaleX(Double.parseDouble(s)), 5),
                scaleYTextbox = makeDialogCustomTextBox(
                        scaleYLabel, SMALL_TEXT_BOX_W,
                        Layout::contentPositionAfterLabel,
                        () -> "", String.valueOf(DialogVals.getResizeScaleY()),
                        () -> "", Textbox.getPositiveFloatValidator(),
                        s -> DialogVals.setResizeScaleY(Double.parseDouble(s)), 5);

        // case 3: resize by pixels
        final TextLabel widthLabel = TextLabel.make(
                textBelowPos(resizeTypeLabel, 1),
                "Width"),
                heightLabel = makeDialogRightLabel(widthLabel, "Height");
        final DynamicTextbox widthTextbox =
                makeDialogPixelDynamicTextbox(widthLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_W, Constants.MAX_CANVAS_W,
                        rw -> DialogVals.setResizeWidth(rw, w, h,
                                DialogVals.isResizePreserveAspectRatio()),
                        DialogVals::getResizeWidth, 4),
                heightTextbox = makeDialogPixelDynamicTextbox(
                        heightLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_H, Constants.MAX_CANVAS_H,
                        rh -> DialogVals.setResizeHeight(rh, w, h,
                                DialogVals.isResizePreserveAspectRatio()),
                        DialogVals::getResizeHeight, 4);

        final ThinkingMenuElement resizeDecider = new ThinkingMenuElement(() -> {
            if (DialogVals.getResizeBy() == DialogVals.ResizeBy.PIXELS)
                return new MenuElementGrouping(widthLabel, heightLabel,
                        widthTextbox, heightTextbox);
            else if (DialogVals.isResizePreserveAspectRatio())
                return new MenuElementGrouping(
                        universalScaleLabel, universalScaleTextbox);

            return new MenuElementGrouping(
                    scaleXLabel, scaleXTextbox, scaleYLabel, scaleYTextbox);
        });
        mb.add(resizeDecider);

        // dynamic scale checker
        final String PREVIEW_PREFIX = "Preview size: ";
        final DynamicLabel preview = makeDynamicLabel(
                textBelowPos(resizeTypeLabel, 3), () -> {
                    final int widthCalc = DialogVals.calculcateResizeWidth(w),
                            heightCalc = DialogVals.calculateResizeHeight(h);
                    return PREVIEW_PREFIX + widthCalc + "x" + heightCalc;
                }, PREVIEW_PREFIX + "XXXXxXXXX");
        mb.add(preview);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Resize canvas...", contents, () -> {
                    final int widthCalc = DialogVals.calculcateResizeWidth(w),
                            heightCalc = DialogVals.calculateResizeHeight(h);

                    return widthCalc >= Constants.MIN_CANVAS_W &&
                            widthCalc <= Constants.MAX_CANVAS_W &&
                            heightCalc >= Constants.MIN_CANVAS_H &&
                            heightCalc <= Constants.MAX_CANVAS_H;
                }, Constants.GENERIC_APPROVAL_TEXT, c::resize, true));
    }

    public static void setDialogToPad(final SEContext c) {
        final int w = c.getState().getImageWidth(),
                h = c.getState().getImageHeight();

        DialogVals.setPadAll(0);

        final MenuBuilder mb = new MenuBuilder();

        // text labels
        final TextLabel
                context = makeDialogLeftLabel(0, "Current size: " + w + "x" + h),
                allLabel = TextLabel.make(textBelowPos(context, 1), "All"),
                leftLabel = TextLabel.make(textBelowPos(allLabel, 1), "Left"),
                rightLabel = TextLabel.make(textBelowPos(leftLabel), "Right"),
                topLabel = TextLabel.make(textBelowPos(rightLabel), "Top"),
                bottomLabel = TextLabel.make(textBelowPos(topLabel), "Bottom"),
                explanation = makeValidDimensionsBottomLabel();
        mb.addAll(context, explanation, allLabel,
                leftLabel, rightLabel, topLabel, bottomLabel);

        // pad textboxes
        final Textbox allTextbox = makeDialogPadTextBox(allLabel,
                i -> true, DialogVals::setPadAll, DialogVals::getPadAll);
        final Textbox leftTextbox = makeDialogPadTextBox(leftLabel, i -> {
            final int pw = i + DialogVals.getPadRight() + w;
            return pw <= Constants.MAX_CANVAS_W && pw > 0;
        }, DialogVals::setPadLeft, DialogVals::getPadLeft);
        final Textbox topTextbox = makeDialogPadTextBox(topLabel, i -> {
            final int ph = i + DialogVals.getPadBottom() + h;
            return ph <= Constants.MAX_CANVAS_H && ph > 0;
        }, DialogVals::setPadTop, DialogVals::getPadTop);
        final Textbox rightTextbox = makeDialogPadTextBox(rightLabel, i -> {
            final int pw = i + DialogVals.getPadLeft() + w;
            return pw <= Constants.MAX_CANVAS_W && pw > 0;
        }, DialogVals::setPadRight, DialogVals::getPadRight);
        final Textbox bottomTextbox = makeDialogPadTextBox(bottomLabel, i -> {
            final int ph = i + DialogVals.getPadTop() + h;
            return ph <= Constants.MAX_CANVAS_W && ph > 0;
        }, DialogVals::setPadBottom, DialogVals::getPadBottom);
        mb.addAll(allTextbox, leftTextbox, rightTextbox,
                topTextbox, bottomTextbox);

        // size preview
        final String PREVIEW_PREFIX = "Preview size: ";
        final DynamicLabel preview = makeDynamicLabel(
                textBelowPos(bottomLabel, 1), () -> {
                    final int pw = DialogVals.getPadLeft() +
                            DialogVals.getPadRight() + w,
                            ph = DialogVals.getPadTop() +
                                    DialogVals.getPadBottom() + h;

                    return PREVIEW_PREFIX + pw + "x" + ph;
                }, PREVIEW_PREFIX + "XXXXxXXXX");
        mb.add(preview);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Pad canvas...", contents,
                () -> leftTextbox.isValid() && rightTextbox.isValid() &&
                        topTextbox.isValid() && bottomTextbox.isValid(),
                Constants.GENERIC_APPROVAL_TEXT, c::pad, true));
    }

    public static void setDialogToStitchFramesTogether() {
        final SEContext c = StippleEffect.get().getContext();
        final MenuBuilder mb = new MenuBuilder();

        final int w = c.getState().getImageWidth(),
                h = c.getState().getImageHeight(),
                fc = c.getState().getFrameCount();

        DialogVals.setFramesPerDim(fc);

        // current frame size
        final TextLabel frameSize = makeDialogLeftLabel(0,
                "Frame size: " + w + "x" + h + " px");
        mb.add(frameSize);

        makeStitchElements(mb, () -> fc, frameSize);

        final Supplier<Boolean> fTooLarge =
                () -> StitchSplitMath.stitchedWidth(h, fc) > Constants.MAX_CANVAS_W ||
                        StitchSplitMath.stitchedHeight(h, fc) > Constants.MAX_CANVAS_H;

        // canvas size preview
        final String CSP_PREFIX = "Canvas size preview: ", CSP_INFIX = " px",
                CSP_OPT_SUFFIX = "... too large";
        final DynamicLabel canvasSizePreview = makeDynamicLabel(
                textBelowPos(frameSize, 5),
                () -> CSP_PREFIX + StitchSplitMath.stitchedWidth(w, fc) + "x" +
                        StitchSplitMath.stitchedHeight(h, fc) + CSP_INFIX +
                        (fTooLarge.get() ? CSP_OPT_SUFFIX : ""),
                CSP_PREFIX + "XXXXxXXXX" + CSP_INFIX + CSP_OPT_SUFFIX);
        mb.add(canvasSizePreview);

        final TextLabel maxBoundsLabel = makeValidDimensionsBottomLabel();
        mb.add(maxBoundsLabel);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Stitch frames together...", contents,
                () -> !fTooLarge.get(), Constants.GENERIC_APPROVAL_TEXT,
                c::stitch, true));
    }

    public static void setDialogToSplitCanvasIntoFrames() {
        final SEContext c = StippleEffect.get().getContext();
        final MenuBuilder mb = new MenuBuilder();

        final int w = c.getState().getImageWidth(),
                h = c.getState().getImageHeight();

        DialogVals.setFrameWidth(Settings.getCheckerboardWPixels(), w);
        DialogVals.setFrameHeight(Settings.getCheckerboardHPixels(), h);

        // current canvas size
        final TextLabel canvasSize = makeDialogLeftLabel(0,
                "Canvas size: " + w + "x" + h + " px");
        mb.add(canvasSize);

        makeSplitElements(mb, w, h, canvasSize);

        // pixel loss
        final String NO_LOSS = "Perfect split!",
                TRUNC = "truncating ", PAD = "padding ",
                RIGHT = "rightmost ", BOTTOM = "bottommost ",
                PIXELS = " pixels";
        final DynamicLabel pixelLoss = makeDialogLeftDynamicLabelAtBottom(
                () -> {
                    final boolean truncX = DialogVals.isTruncateSplitX(),
                            truncY = DialogVals.isTruncateSplitY();
                    final int fw = DialogVals.getFrameWidth(),
                            fh = DialogVals.getFrameHeight(),
                            remX = w % fw, remY = h % fh,
                            pxCountX = remX == 0 ? 0 :
                                    (truncX ? remX : (fw - remX)),
                            pxCountY = remY == 0 ? 0 :
                                    (truncY ? remY : (fh - remY));
                    final boolean perfectX = remX == 0,
                            perfectY = remY == 0,
                            noLoss = perfectX && perfectY;
                    final String xText = perfectX ? ""
                            : ((truncX ? TRUNC : PAD) +
                                    RIGHT + pxCountX + PIXELS),
                            yText = perfectY ? ""
                                    : ((truncY ? TRUNC : PAD) +
                                    BOTTOM + pxCountY + PIXELS);

                    final String pixelLossText = noLoss ? NO_LOSS : (xText +
                            (!(perfectX || perfectY) ? "; " : "") + yText);
                    return pixelLossText.substring(0, 1).toUpperCase() +
                            pixelLossText.substring(1);
                });
        mb.add(pixelLoss);

        // preview
        final String PRV_PREFIX = "Preview: ",
                PRV_INFIX = " frames of ", PRV_INFIX_SING = " frame of ",
                PRV_SUFFIX_SING = " px", PRV_SUFFIX = PRV_SUFFIX_SING + " each";
        final DynamicLabel dimsPreview = makeDynamicLabel(
                textBelowPos(pixelLoss, LINE_ABOVE), () -> {
                    final int fc = StitchSplitMath.splitFrameCount(w, h);

                    return PRV_PREFIX + fc +
                            (fc == 1 ? PRV_INFIX_SING : PRV_INFIX) +
                            DialogVals.getFrameWidth() + "x" +
                            DialogVals.getFrameHeight() +
                            (fc == 1 ? PRV_SUFFIX_SING : PRV_SUFFIX);
                },
                PRV_PREFIX + Constants.MAX_NUM_FRAMES + PRV_INFIX +
                        Constants.MAX_CANVAS_W + "x" + Constants.MAX_CANVAS_H + PRV_SUFFIX);
        mb.add(dimsPreview);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Split canvas into frames...", contents,
                () -> true, Constants.GENERIC_APPROVAL_TEXT, c::split, true));
    }

    public static void setDialogToOpenPNG(final GameImage image, final Path filepath) {
        final int w = image.getWidth(), h = image.getHeight();
        final boolean tooBig = w > Constants.MAX_CANVAS_W ||
                h > Constants.MAX_CANVAS_H;

        DialogVals.setImportWidth(w, w, h, false);
        DialogVals.setImportHeight(h, w, h, false);

        DialogVals.setImportColumns(1, w);
        DialogVals.setImportRows(1, h);

        final MenuBuilder mb = new MenuBuilder();

        // text labels
        final TextLabel context = makeDialogLeftLabel(0,
                "Current size: " + w + "x" + h + (tooBig
                        ? " ... must be split or downscaled" : ""));
        mb.add(context);

        final TextLabel resizeLabel = TextLabel.make(
                textBelowPos(context, 1), "Resize..."),
                preserveAspectRatioLabel = TextLabel.make(textBelowPos(
                        resizeLabel), "Preserve aspect ratio?"),
                widthLabel = TextLabel.make(textBelowPos(
                        preserveAspectRatioLabel), "Width"),
                heightLabel = makeDialogRightLabel(widthLabel, "Height");
        final Checkbox preserveAspectRatioCheckbox = new Checkbox(
                contentPositionAfterLabel(preserveAspectRatioLabel),
                new ConcreteProperty<>(
                        DialogVals::isResizePreserveAspectRatio,
                        DialogVals::setResizePreserveAspectRatio));
        final DynamicTextbox widthTextbox =
                makeDialogPixelDynamicTextbox(widthLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_W, Integer.MAX_VALUE,
                        iw -> DialogVals.setImportWidth(iw, w, h,
                                DialogVals.isResizePreserveAspectRatio()),
                        DialogVals::getImportWidth, 5),
                heightTextbox = makeDialogPixelDynamicTextbox(heightLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_H, Integer.MAX_VALUE,
                        ih -> DialogVals.setImportHeight(ih, w, h,
                                DialogVals.isResizePreserveAspectRatio()),
                        DialogVals::getImportHeight, 5);
        mb.addAll(resizeLabel,
                preserveAspectRatioLabel, preserveAspectRatioCheckbox,
                widthLabel, heightLabel,
                widthTextbox, heightTextbox);

        final TextLabel splitLabel = TextLabel.make(
                textBelowPos(widthLabel, 1), "Split..."),
                invisibleReference = TextLabel.make(textBelowPos(
                        splitLabel, LINE_ABOVE), "");
        mb.add(splitLabel);

        makeSplitElementsForOpenPNG(mb, invisibleReference);

        mb.add(makeValidDimensionsBottomLabel());

        final Supplier<Boolean> precondition = () -> {
            final int iw = DialogVals.getImportWidth(),
                    ih = DialogVals.getImportHeight(),
                    columns = StitchSplitMath.importColumns(iw),
                    rows = StitchSplitMath.importRows(ih),
                    fw = DialogVals.getImportFrameWidth(),
                    fh = DialogVals.getImportFrameHeight();

            return fw >= Constants.MIN_CANVAS_H &&
                    fh >= Constants.MIN_CANVAS_H &&
                    fw <= Constants.MAX_CANVAS_W &&
                    fh <= Constants.MAX_CANVAS_H &&
                    rows * columns <= Constants.MAX_NUM_FRAMES;
        };

        final MenuElementGrouping contents = new MenuElementGrouping(
                mb.build().getMenuElements());
        setDialog(assembleDialog("Import file " +
                        filepath.getFileName().toString(), contents,
                precondition, "Import", () -> StippleEffect.get()
                        .newProjectFromFile(image, filepath), false));
    }

    public static void setDialogToNewProject() {
        // initial canvas size suggestion determination
        final int NO_CLIPBOARD = 0;

        final int initialW, initialH, clipboardW, clipboardH;
        final Object contents = SEClipboard.get().getContent();

        if (contents instanceof SelectionContents sc) {
            final Selection clipboard = sc.getSelection();

            clipboardW = clipboard.bounds.width();
            clipboardH = clipboard.bounds.height();

            initialW = clipboardW;
            initialH = clipboardH;
        } else if (contents instanceof CelSelection cs) {
            clipboardW = cs.celWidth;
            clipboardH = cs.celHeight;

            initialW = clipboardW;
            initialH = clipboardH;
        } else {
            clipboardW = NO_CLIPBOARD;
            clipboardH = NO_CLIPBOARD;

            initialW = Settings.getDefaultCanvasWPixels();
            initialH = Settings.getDefaultCanvasHPixels();
        }

        DialogVals.setNewProjectWidth(initialW);
        DialogVals.setNewProjectHeight(initialH);

        // text labels
        final TextLabel
                presetLabel = makeDialogLeftLabel(0, "Size presets"),
                widthLabel = TextLabel.make(
                        textBelowPos(presetLabel, 1),
                        "Width"),
                heightLabel = makeDialogRightLabel(widthLabel, "Height"),
                explanation = makeValidDimensionsBottomLabel();

        // preset buttons
        final StaticTextButton defaultPreset =
                GraphicsUtils.makeBespokeTextButton("From default",
                        contentPositionAfterLabel(presetLabel),
                        () -> {
                    DialogVals.setNewProjectWidth(Settings.getDefaultCanvasWPixels());
                    DialogVals.setNewProjectHeight(Settings.getDefaultCanvasHPixels());
                });
        final GatewayMenuElement clipboardPreset = new GatewayMenuElement(
                GraphicsUtils.makeBespokeTextButton("Clipboard contents",
                        getDialogContentToRightOfContent(defaultPreset), () -> {
                            DialogVals.setNewProjectWidth(clipboardW);
                            DialogVals.setNewProjectHeight(clipboardH);
                }), () -> clipboardW != NO_CLIPBOARD);

        // dim textboxes
        final DynamicTextbox widthTextbox = makeDialogDynamicTextbox(
                widthLabel, Layout::contentPositionAfterLabel,
                Constants.MIN_CANVAS_W, initialW, Constants.MAX_CANVAS_W, "px",
                DialogVals::setNewProjectWidth,
                DialogVals::getNewProjectWidth, 4);
        final DynamicTextbox heightTextbox = makeDialogDynamicTextbox(
                heightLabel, Layout::contentPositionAfterLabel,
                Constants.MIN_CANVAS_H, initialW, Constants.MAX_CANVAS_H, "px",
                DialogVals::setNewProjectHeight,
                DialogVals::getNewProjectHeight, 4);

        final MenuElementGrouping menu = new MenuElementGrouping(
                presetLabel, defaultPreset, clipboardPreset,
                widthLabel, heightLabel, explanation,
                widthTextbox, heightTextbox);
        setDialog(assembleDialog("New project...", menu,
                () -> widthTextbox.isValid() && heightTextbox.isValid(),
                "Create", () -> StippleEffect.get().newProject(), true));
    }

    public static void setDialogToHistory(final SEContext c) {
        final MenuBuilder mb = new MenuBuilder();

        final Coord2D thirdDisp = new Coord2D(getDialogWidth() / 3, 0);

        final TextLabel stateHeader = makeDialogLeftLabel(0, "Project state stack:"),
                causeHeader = TextLabel.make(
                        stateHeader.getPosition().displace(thirdDisp),
                        "Preceding operation:");

        mb.addAll(stateHeader, causeHeader);

        AtomicInteger i = new AtomicInteger(-1);
        final List<Integer> checkpoints = c.stateManager.getCheckpoints();
        final int size = checkpoints.size();

        final MenuBuilder smb = new MenuBuilder();
        MenuElement bottomLabel = new PlaceholderMenuElement();

        for (int ci = 0; ci < size; ci++) {
            final int checkpoint = checkpoints.get((size - 1) - ci);

            final ProjectState state = c.stateManager.getState(checkpoint);

            final int relative = c.stateManager.relativePosition(checkpoint),
                    abs = Math.abs(relative);

            final Color col;
            final String text;

            if (relative < 0) {
                col = SEColors.red();
                text = abs + " state" + (abs > 1 ? "s" : "") + " behind";
            } else if (relative == 0) {
                col = ThemeLogic.intuitTextColor(
                        Settings.getTheme().panelBackground, true);
                text = "[ CURRENT ]";
            } else {
                col = SEColors.green();
                text = abs + " state" + (abs > 1 ? "s" : "") + " ahead";
            }

            final TextLabel stateLabel =
                    TextLabel.make(getDialogLeftContentPositionForRow(ci + 1),
                    text, col),
                    causeLabel = TextLabel.make(
                            stateLabel.getPosition().displace(thirdDisp),
                            state.getOperation().toString());
            smb.addAll(stateLabel, causeLabel);

            if (relative != 0) {
                final SelectStateButton selectButton = SelectStateButton.make(
                        getDialogLeftContentPositionForRow(ci + 1)
                                .displace(thirdDisp.x * 2, DIALOG_CONTENT_COMP_OFFSET_Y),
                        () -> i.set(checkpoint), () -> i.get() != checkpoint);
                smb.add(selectButton);
            }

            bottomLabel = stateLabel;
        }

        final int scrollerEndY = (getCanvasMiddle().y +
                getDialogHeight() / 2) - ((2 * CONTENT_BUFFER_PX) +
                STD_TEXT_BUTTON_H);

        final Coord2D scrollerPos = getDialogPosition().displace(0,
                (int)(3.5 * STD_TEXT_BUTTON_INC) +
                        TEXT_Y_OFFSET - BUTTON_DIM);
        final Bounds2D scrollerDims = new Bounds2D(getDialogWidth(),
                scrollerEndY - scrollerPos.y);

        final int realBottomY = bottomLabel.getRenderPosition().y +
                bottomLabel.getHeight() + STD_TEXT_BUTTON_H;

        mb.add(new VerticalScrollBox(scrollerPos, scrollerDims,
                Arrays.stream(smb.build().getMenuElements())
                        .map(Scrollable::new).toArray(Scrollable[]::new),
                realBottomY, 0));

        final Supplier<Boolean> precondition = () -> i.get() >= 0;

        final DynamicLabel selectedLabel =
                makeDialogLeftDynamicLabelAtBottom(() -> {
                    if (precondition.get()) {
                        final int relative = c.stateManager.relativePosition(i.get()),
                                abs = Math.abs(relative);

                        final String before = "Selected state ",
                                after = "the current state", middle;

                        if (relative < 0)
                            middle = abs + " state" +
                                    (abs > 1 ? "s" : "") + " behind ";
                        else if (relative == 0)
                            middle = "";
                        else
                            middle = abs + " state" +
                                    (abs > 1 ? "s" : "") + " ahead of ";

                        return before + middle + after;
                    } else
                        return "No selection";
                });
        mb.add(selectedLabel);

        setDialog(assembleDialog("History of \"" +
                        c.getSaveConfig().getFormattedName(false, false) + "\"",
                new MenuElementGrouping(mb.build().getMenuElements()),
                precondition, "Revert...",
                () -> setDialogToPreviewAction(
                        c.stateManager.getState(i.get()),
                        () -> c.stateManager.setState(i.get(), c),
                        () -> SEAction.HISTORY.behaviour.accept(c),
                        "project state reversion"), false));
    }

    public static void setDialogToNewFont() {
        DialogVals.setNewFontPixelSpacing(Constants.DEFAULT_FONT_PX_SPACING, false);
        DialogVals.setHasLatinEx(false, false);
        DialogVals.setCharSpecificSpacing(true, false);
        DialogVals.setFontName("");

        DialogVals.resetAscii();
        DialogVals.resetLatinEx();

        DialogVals.setFontPreviewImage(GameImage.dummy());

        final MenuBuilder mb = new MenuBuilder();

        final int LINE_BREAK = 1;
        int lines = 0;

        // specification
        mb.add(makeDialogLeftLabel(lines, "Font source files must be PNGs and " +
                FontConstants.FONT_SOURCE_BASE_WIDTH + "x" +
                FontConstants.FONT_SOURCE_BASE_HEIGHT + " pixels (or a multple thereof)."));
        lines++;

        // import font templates
        final TextLabel importTemplatesLabel = makeDialogLeftLabel(
                lines, "You can use these font templates as references:");
        final StaticTextButton importTemplatesButton =
                GraphicsUtils.makeStandardTextButton("Import",
                        contentPositionAfterLabel(importTemplatesLabel),
                        () -> StippleEffect.get().openFontTemplateProjects());
        mb.addAll(importTemplatesLabel, importTemplatesButton);
        lines += 1 + LINE_BREAK;

        // name
        final TextLabel nameLabel = makeDialogLeftLabel(lines, "Font name");
        final Textbox nameTextbox = makeDialogCustomTextBox(nameLabel,
                LONG_NAME_TEXTBOX_W,
                Layout::contentPositionAfterLabel,
                () -> "", DialogVals.getFontName(), () -> "",
                Textbox::validateAsFileName, DialogVals::setFontName,
                Constants.MAX_NAME_LENGTH);
        mb.addAll(nameLabel, nameTextbox);
        lines++;

        // pixel spacing
        final TextLabel spacingLabel = makeDialogLeftLabel(lines, "Spacing");
        final Textbox spacingTextbox = makeDialogPixelDynamicTextbox(
                spacingLabel, Layout::contentPositionAfterLabel,
                Constants.MIN_FONT_PX_SPACING, Constants.MAX_FONT_PX_SPACING,
                DialogVals::setNewFontPixelSpacing,
                DialogVals::getNewFontPixelSpacing, 3);
        mb.addAll(spacingLabel, spacingTextbox);
        // character-specific
        final TextLabel charSpecificLabel =
                makeDialogRightLabel(spacingLabel, "Character-specific spacing");
        final Checkbox charSpecificCheckbox = new Checkbox(
                contentPositionAfterLabel(charSpecificLabel),
                new ConcreteProperty<>(
                        DialogVals::isCharSpecificSpacing,
                        DialogVals::setCharSpecificSpacing));
        mb.addAll(charSpecificLabel, charSpecificCheckbox);
        lines += 1 + LINE_BREAK;

        // ASCII
        final TextLabel asciiLabel = makeDialogLeftLabel(
                lines, "ASCII source file");
        final StaticTextButton asciiButton =
                GraphicsUtils.makeStandardTextButton("Upload",
                        contentPositionAfterLabel(asciiLabel),
                        SEFonts::uploadASCIISourceFile);
        final DynamicLabel asciiConfirmation = DynamicLabel.make(
                getDialogRightContentPositionForRow(lines),
                () -> DialogVals.getAsciiStatus().getMessage(),
                getDialogWidth());
        mb.addAll(asciiLabel, asciiButton, asciiConfirmation);
        lines++;

        // latin extended
        final TextLabel latinExQuestion = makeDialogLeftLabel(lines,
                "Does this font support Latin Extended characters?");
        final Checkbox latinExCheckbox = new Checkbox(
                contentPositionAfterLabel(latinExQuestion),
                new ConcreteProperty<>(
                        DialogVals::hasLatinEx, DialogVals::setHasLatinEx));
        final TextLabel latinExLabel = makeDialogLeftLabel(
                lines + 1, "Latin Extended source file");
        final StaticTextButton latinExButton =
                GraphicsUtils.makeStandardTextButton("Upload",
                        contentPositionAfterLabel(latinExLabel),
                        SEFonts::uploadLatinExtendedSourceFile);
        final DynamicLabel latinExConfirmation = DynamicLabel.make(
                getDialogRightContentPositionForRow(lines + 1),
                () -> DialogVals.getLatinExStatus().getMessage(),
                getDialogWidth());
        final MenuElementGrouping latinExContent = new MenuElementGrouping(
                latinExLabel, latinExButton, latinExConfirmation);
        final GatewayMenuElement latinExGateway = new GatewayMenuElement(
                latinExContent, DialogVals::hasLatinEx);
        mb.addAll(latinExQuestion, latinExCheckbox, latinExGateway);
        lines += 2 + LINE_BREAK;

        // partial precondition
        final Supplier<Boolean> rawFontCondition =
                () -> spacingTextbox.isValid() &&
                        DialogVals.getAsciiStatus().isValid() &&
                        (!DialogVals.hasLatinEx() ||
                                DialogVals.getLatinExStatus().isValid());

        // preview text
        final int previewRow = lines;
        final ThinkingMenuElement previewText = new ThinkingMenuElement(() -> {
            if (rawFontCondition.get())
                return new StaticMenuElement(
                        getDialogLeftContentPositionForRow(previewRow),
                        MenuElement.Anchor.LEFT_TOP,
                        DialogVals.getFontPreviewImage());
            else
                return new PlaceholderMenuElement();
        });
        mb.add(previewText);

        final Supplier<Boolean> precondition =
                () -> nameTextbox.isValid() && rawFontCondition.get();
        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Define a new font...", contents,
                precondition, "Add",
                () -> TextTool.get().addFont(), true));
    }

    public static void setDialogToScriptErrors() {
        final  MenuBuilder mb = new MenuBuilder();
        final int MAX_TO_PRINT = 10;
        final String[] errors = ScriptErrorLog.getErrors();
        final String CONT = " ".repeat(6) + "...";

        final List<String> formattedErrors = new ArrayList<>();

        for (String error : errors) {
            String trimmedError = error;
            while (trimmedError.length() > MAX_ERROR_CHARS_PER_LINE) {
                final boolean first = error.equals(trimmedError);
                final String beginning = cutOffAtNextSpace(trimmedError,
                        CHARS_CUTOFF - (first ? 0 : CONT.length()));

                formattedErrors.add((first ? "" : CONT) + beginning);
                trimmedError = trimmedError.substring(beginning.length());
            }

            formattedErrors.add((error.equals(trimmedError) ? "" : CONT) +
                    trimmedError);
        }

        for (int i = 0; i < formattedErrors.size() && i < MAX_TO_PRINT; i++)
            mb.add(makeDialogLeftLabel(i, formattedErrors.get(i)));

        if (formattedErrors.size() > MAX_TO_PRINT) {
            final int more = (int) formattedErrors.stream()
                    .filter(s -> formattedErrors.indexOf(s) >= MAX_TO_PRINT)
                    .filter(s -> !s.startsWith(CONT)).count();

            mb.add(makeDialogLeftLabel(MAX_TO_PRINT, "... and " + more + " more"));
        }

        setDialog(assembleDialog(
                "Script encountered errors:",
                new MenuElementGrouping(mb.build().getMenuElements()),
                () -> false, Constants.CLOSE_DIALOG_TEXT, () -> {}, true));
    }

    public static void setDialogToExitProgramAYS() {
        setDialogToAYS("Quit " + StippleEffect.PROGRAM_NAME + "?",
                "All open projects will be closed without saving...",
                () -> StippleEffect.get().exitProgram());
    }

    public static void setDialogToCloseProjectAYS(final int index) {
        setDialogToAYS("Close the project \"" + StippleEffect.get()
                        .getContexts().get(index).getSaveConfig()
                        .getFormattedName(false, true) + "\"?",
                "All unsaved changes will be lost...",
                () -> StippleEffect.get().removeContext(index));
    }

    private static void setDialogToAYS(
            final String actionLabel, final String consequence,
            final Runnable onApprove
    ) {
        final GameImage warningText = GraphicsUtils.uiText(simpleTextColor())
                .addText(consequence).build().draw();
        final StaticMenuElement warning = new StaticMenuElement(
                getCanvasMiddle(), new Bounds2D(warningText.getWidth(),
                warningText.getHeight()), MenuElement.Anchor.CENTRAL, warningText);

        final MenuElementGrouping contents = new MenuElementGrouping(warning);
        setDialog(assembleDialog(actionLabel, contents, () -> true,
                Constants.GENERIC_APPROVAL_TEXT, onApprove, true));
    }

    public static void setDialogToOutline(final SEContext c) {
        final MenuBuilder mb = new MenuBuilder();

        // labels
        final TextLabel setAllLabel = makeDialogLeftLabel(0, "Set all edges"),
                presets = TextLabel.make(textBelowPos(setAllLabel), "Presets"),
                validity = TextLabel.make(textBelowPos(presets),
                        "Valid outline thickness can range from -" +
                                Constants.MAX_OUTLINE_PX + " to " +
                                Constants.MAX_OUTLINE_PX + ".");
        mb.addAll(setAllLabel, presets, validity);

        // no selection notification
        if (!c.getState().hasSelection())
            mb.add(makeDialogLeftLabelAtBottom("Cannot outline; nothing is selected"));

        // buttons for setting presets
        final StaticTextButton singlePreset =
                GraphicsUtils.makeStandardTextButton("Single",
                contentPositionAfterLabel(presets),
                        () -> DialogVals.setOutlineSideMask(
                                Outliner.getSingleOutlineMask())),
                doublePreset = GraphicsUtils.makeStandardTextButton("Double",
                        getDialogContentToRightOfContent(singlePreset),
                        () -> DialogVals.setOutlineSideMask(
                                Outliner.getDoubleOutlineMask()));

        // set all textbox
        final Textbox setAll = makeDialogPixelDynamicTextbox(setAllLabel,
                Layout::contentPositionAfterLabel,
                -Constants.MAX_OUTLINE_PX, Constants.MAX_OUTLINE_PX,
                DialogVals::setGlobalOutline, DialogVals::getGlobalOutline, 3);

        mb.addAll(setAll, singlePreset, doublePreset);

        // direction buttons
        final Coord2D buttonPos = getCanvasMiddle();

        mb.add(new StaticMenuElement(buttonPos, OUTLINE_BUTTON_DIMS,
                MenuElement.Anchor.CENTRAL,
                GraphicsUtils.loadIcon(ResourceCodes.SELECTION_REPRESENTATION)));

        Arrays.stream(Outliner.Direction.values()).forEach(direction -> {
            final Coord2D rc = direction.relativeCoordinate();

            final OutlineDirectionWatcher watcher =
                    new OutlineDirectionWatcher(buttonPos.displace(
                            rc.x * STD_TEXT_BUTTON_INC,
                            rc.y * STD_TEXT_BUTTON_INC), direction);
            final OutlineTextbox textbox =
                    OutlineTextbox.make(watcher, direction);

            mb.addAll(watcher, textbox);
        });

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Configure outline...", contents,
                () -> c.getState().hasSelection(), "Outline",
                () -> c.outlineSelection(DialogVals.getOutlineSideMask()),
                true));
    }

    public static void setDialogToPanelManager() {
        final MenuBuilder mb = new MenuBuilder();

        // presets: single & double
        final TextLabel presets = makeDialogLeftLabel(0, "Presets");
        mb.add(presets);

        // buttons for panel arrangement presets
        final StaticTextButton showAllPreset =
                GraphicsUtils.makeStandardTextButton("All",
                        contentPositionAfterLabel(presets),
                        Layout::showAllPanels),
                minimalUIPreset = GraphicsUtils.makeStandardTextButton("Minimal",
                        getDialogContentToRightOfContent(showAllPreset),
                        Layout::minimalUI);
        mb.addAll(showAllPreset, minimalUIPreset);

        // vars
        final String TOOLBAR = "Toolbar", FLIPBOOK = "Flipbook",
                COLORS = "Colors", PROJECTS = "Projects";
        final String[] labelTexts = new String[]
                { TOOLBAR, FLIPBOOK, COLORS, PROJECTS };
        final Map<String, Consumer<Boolean>> adjustmentFunctionMap =
                Map.ofEntries(
                        Map.entry(TOOLBAR, Layout::setToolbarShowing),
                        Map.entry(FLIPBOOK, Layout::setFlipbookPanelShowing),
                        Map.entry(COLORS, Layout::setColorsPanelShowing),
                        Map.entry(PROJECTS, Layout::setProjectsExpanded));
        final Map<String, Supplier<Boolean>> retrievalFunctionMap =
                Map.ofEntries(
                        Map.entry(TOOLBAR, Layout::isToolbarShowing),
                        Map.entry(FLIPBOOK, Layout::isFlipbookPanelShowing),
                        Map.entry(COLORS, Layout::isColorsPanelShowing),
                        Map.entry(PROJECTS, Layout::isProjectsExpanded));

        for (int i = 0; i < labelTexts.length; i++) {
            final boolean isProject = labelTexts[i].equals(PROJECTS);

            // panel label
            final TextLabel label = TextLabel.make(textBelowPos(presets, 1 + i),
                    labelTexts[i]);

            // panel toggle
            final Consumer<Boolean>
                    setter = adjustmentFunctionMap.get(labelTexts[i]),
                    adj = b -> adjustPanels(() -> setter.accept(b));
            final Supplier<Boolean> ret =
                    retrievalFunctionMap.get(labelTexts[i]);

            if (isProject) {
                final TextToggleButton toggle = TextToggleButton.make(
                        contentPositionAfterLabel(label),
                        new String[] { "Expanded", "Collapsed" },
                        new Runnable[] { () -> {}, () -> {} },
                        () -> ret.get() ? 0 : 1,
                        () -> adj.accept(!ret.get()));
                mb.addAll(label, toggle);
            } else {
                final Checkbox panelCheckbox = new Checkbox(
                        contentPositionAfterLabel(label),
                        new ConcreteProperty<>(ret, adj));
                mb.addAll(label, panelCheckbox);
            }
        }

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Panel Manager", contents, () -> false,
                Constants.CLOSE_DIALOG_TEXT, () -> {}, true));
    }

    public static void setDialogToHSVShift(final SEContext c) {
        final MenuBuilder mb = new MenuBuilder();

        makeCommonColorOperationElements(mb, c);

        final TextLabel hueLabel = makeDialogLeftLabel(
                AFTER_COMMON_COLOR_ACTION_ROW, "Shift hue");

        final String T_SHIFT = "Shift ", T_SCALE = "Scale ",
                T_SAT = "sat.", T_VAL = "value";

        final DynamicLabel satLabel = makeDynamicLabel(
                textBelowPos(hueLabel),
                () -> (DialogVals.isShiftingSat()
                        ? T_SHIFT : T_SCALE) + T_SAT,
                T_SCALE + T_SAT),
                valueLabel = makeDynamicLabel(
                        textBelowPos(satLabel),
                        () -> (DialogVals.isShiftingValue()
                                ? T_SHIFT : T_SCALE) + T_VAL,
                        T_SCALE + T_VAL);

        final IncrementalRangeElements<Integer> h =
                IncrementalRangeElements.makeForInt(hueLabel,
                        hueLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                        hueLabel.getY(),
                        1, Constants.MIN_HUE_SHIFT, Constants.MAX_HUE_SHIFT,
                        DialogVals::setHueShift, DialogVals::getHueShift,
                        i -> i, i -> i, String::valueOf, "-XXX");
        mb.addAll(hueLabel, h.decButton, h.incButton, h.slider, h.value);

        final double MIN = Constants.MIN_SV_SCALE, MAX = Constants.MAX_SV_SCALE;
        final int STEPS = 5;
        final double[] bounds = new double[] { MIN, 1d, 2d, 5d, 10d, MAX },
                increments = new double[] { 0.05, 0.1, 0.25, 0.5, 2.5 };

        final int[] valuesPerSlice = new int[STEPS], sums = new int[STEPS];

        for (int i = 0; i < STEPS; i++) {
            valuesPerSlice[i] =
                    sliceValues(bounds[i], bounds[i + 1], increments[i]);

            sums[i] = i == 0
                    ? valuesPerSlice[i]
                    : sums[i - 1] + valuesPerSlice[i];
        }

        final Runnable satDecrement = () -> {
            final double was = DialogVals.getSatScale();

            for (int i = 0; i < STEPS; i++)
                if (was <= bounds[i + 1]) {
                    DialogVals.setSatScale(Math.max(was - increments[i], MIN));
                    break;
                }
        }, satIncrement = () -> {
            final double was = DialogVals.getSatScale();

            for (int i = 0; i < STEPS; i++)
                if (was < bounds[i + 1]) {
                    DialogVals.setSatScale(Math.min(was + increments[i], MAX));
                    break;
                }
        }, valueDecrement = () -> {
            final double was = DialogVals.getValueScale();

            for (int i = 0; i < STEPS; i++)
                if (was <= bounds[i + 1]) {
                    DialogVals.setValueScale(
                            Math.max(was - increments[i], MIN));
                    break;
                }
        }, valueIncrement = () -> {
            final double was = DialogVals.getValueScale();

            for (int i = 0; i < STEPS; i++)
                if (was < bounds[i + 1]) {
                    DialogVals.setValueScale(
                            Math.min(was + increments[i], MAX));
                    break;
                }
        };

        final Function<Double, Integer> svfToSlider = d -> {
            for (int step = 0; step < STEPS; step++) {
                final double lb = bounds[step],
                        ub = bounds[step + 1],
                        inc = increments[step];

                if (d <= ub) {
                    final int prevSliceVs = step == 0 ? 0 : sums[step - 1],
                            inSlice = (int) ((d - lb) / inc);

                    return prevSliceVs + inSlice;
                }
            }

            // should never reach
            return 0;
        };

        final Function<Integer, Double> svfFromSlider = i -> {
            for (int step = 0; step < STEPS; step++) {
                final int sum = sums[step];

                if (i <= sum) {
                    final double lb = bounds[step],
                            inc = increments[step];

                    final int lowerSum = step == 0 ? 0 : sums[step - 1];

                    return lb + (inc * (i - lowerSum));
                }
            }

            // should never reach
            return 1d;
        };

        Function<Double, String> svfFormat = d -> {
            final int _20x = (int) Math.round(d * 20);

            return (_20x / 20d) + "x";
        };

        final MenuElementGrouping satScale = new MenuElementGrouping(
                IncrementalRangeElements.makeForDouble(satLabel,
                        satLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                        satLabel.getY(), satDecrement, satIncrement,
                        Constants.MIN_SV_SCALE, Constants.MAX_SV_SCALE,
                        DialogVals::setSatScale, DialogVals::getSatScale,
                        svfToSlider, svfFromSlider, svfFormat,
                        "x" + "X".repeat(20)).getAll()),
                satShift = new MenuElementGrouping(
                        IncrementalRangeElements.makeForInt(satLabel,
                                satLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                                satLabel.getY(),
                                1, Constants.MIN_SV_SHIFT, Constants.MAX_SV_SHIFT,
                                DialogVals::setSatShift, DialogVals::getSatShift,
                                i -> i, i -> i, String::valueOf, "XXXX").getAll());

        final ThinkingMenuElement satManager = new ThinkingMenuElement(
                () -> DialogVals.isShiftingSat() ? satShift : satScale);

        mb.addAll(satLabel, satManager);

        final MenuElementGrouping valueScale = new MenuElementGrouping(
                IncrementalRangeElements.makeForDouble(valueLabel,
                        valueLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                        valueLabel.getY(), valueDecrement, valueIncrement,
                        Constants.MIN_SV_SCALE, Constants.MAX_SV_SCALE,
                        DialogVals::setValueScale, DialogVals::getValueScale,
                        svfToSlider, svfFromSlider, svfFormat,
                        "x" + "X".repeat(20)).getAll()),
                valueShift = new MenuElementGrouping(
                        IncrementalRangeElements.makeForInt(valueLabel,
                                valueLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                                valueLabel.getY(),
                                1, Constants.MIN_SV_SHIFT, Constants.MAX_SV_SHIFT,
                                DialogVals::setValueShift, DialogVals::getValueShift,
                                i -> i, i -> i, String::valueOf, "XXXX").getAll());

        final ThinkingMenuElement valueManager = new ThinkingMenuElement(
                () -> DialogVals.isShiftingValue() ? valueShift : valueScale);

        mb.addAll(valueLabel, valueManager);

        final String RESET = "Reset";
        final Coord2D firstResetPos =
                getDialogRightContentPositionForRow(AFTER_COMMON_COLOR_ACTION_ROW)
                        .displace(0, DIALOG_CONTENT_COMP_OFFSET_Y);

        final StaticTextButton resetHue =
                GraphicsUtils.makeStandardTextButton(RESET, firstResetPos,
                        () -> DialogVals.setHueShift(0)),
                resetSat = GraphicsUtils.makeStandardTextButton(RESET,
                        textBelowPos(resetHue),
                        () -> {
                            DialogVals.setSatScale(1d);
                            DialogVals.setSatShift(0);
                        }),
                resetValue = GraphicsUtils.makeStandardTextButton(RESET,
                        textBelowPos(resetSat),
                        () -> {
                            DialogVals.setValueScale(1d);
                            DialogVals.setValueShift(0);
                        });

        final String[] toggleTexts = new String[] { "Scale", "Shift" };
        final Runnable[] toggleActions = new Runnable[] { () -> {}, () -> {} };
        final TextToggleButton
                toggleSat = TextToggleButton.make(
                        getDialogContentToRightOfContent(resetSat),
                        toggleTexts, toggleActions,
                        () -> DialogVals.isShiftingSat() ? 1 : 0,
                        DialogVals::toggleShiftingSat),
                toggleValue = TextToggleButton.make(
                        getDialogContentToRightOfContent(resetValue),
                        toggleTexts, toggleActions,
                        () -> DialogVals.isShiftingValue() ? 1 : 0,
                        DialogVals::toggleShiftingValue);

        mb.addAll(resetHue, resetSat, resetValue, toggleSat, toggleValue);

        setDialog(assembleDialog("Adjust HSV color levels...",
                new MenuElementGrouping(mb.build().getMenuElements()),
                () -> true, "Preview",
                () -> DialogAssembly.setDialogToPreviewAction(
                        c.prepHSVShift(),
                        () -> DialogAssembly.setDialogToHSVShift(c),
                        "shifted color levels"), false));
    }

    private static int sliceValues(
            final double lowerBound, final double upperBound,
            final double increment
    ) {
        return (int) ((upperBound - lowerBound) / increment);
    }

    public static void setDialogToColorScript(final SEContext c) {
        final MenuBuilder mb = new MenuBuilder();

        makeCommonColorOperationElements(mb, c);

        final TextLabel scriptLabel = makeDialogLeftLabel(
                AFTER_COMMON_COLOR_ACTION_ROW, "Script file");
        final StaticTextButton scriptButton =
                GraphicsUtils.makeStandardTextButton("Upload",
                        contentPositionAfterLabel(scriptLabel),
                        StippleEffect.get()::openColorScript);
        final DynamicLabel scriptConfirmation = makeDynamicLabel(
                getDialogRightContentPositionForRow(AFTER_COMMON_COLOR_ACTION_ROW),
                DialogVals::colorScriptMessage, "X".repeat(50));
        mb.addAll(scriptLabel, scriptButton, scriptConfirmation);

        setDialog(assembleDialog("Run a color script...",
                new MenuElementGrouping(mb.build().getMenuElements()),
                DialogVals::isColorScriptValid, "Preview",
                () -> DialogAssembly.setDialogToPreviewAction(
                        c.prepColorScript(DialogVals.getColorScript()),
                        () -> DialogAssembly.setDialogToColorScript(c),
                        "executed color script"), false));
    }

    private static void setDialogToPreviewAction(
            final ProjectState preview, final Runnable backButtonAction,
            final String previewAppend
    ) {

        final SEContext c = StippleEffect.get().getContext();

        setDialogToPreviewAction(preview, () -> {
                    preview.markAsCheckpoint(false);
                    c.stateManager.performAction(preview, Operation.EDIT_IMAGE);
                }, backButtonAction, "preview of " + previewAppend);
    }

    private static void setDialogToPreviewAction(
            final ProjectState preview, final Runnable onApproval,
            final Runnable backButtonAction, final String previewAppend
    ) {
        if (preview == null) {
            setDialogToScriptErrors();
            return;
        }

        final MenuBuilder mb = new MenuBuilder();
        final SEContext c = StippleEffect.get().getContext();

        final StaticTextButton backButton =
                GraphicsUtils.makeStandardTextButton(
                        "< Back",
                        getDialogContentInitial(), backButtonAction);
        mb.add(backButton);

        final int fc = preview.getFrameCount(),
                w = preview.getImageWidth(),
                h = preview.getImageHeight();

        final int pw, ph, MIN = 1,
                maxW = getDialogWidth() - (4 * CONTENT_BUFFER_PX),
                maxH = (int) (getDialogHeight() * (2/3d));

        if (w < maxW && h < maxH) {
            final double timesFits = Math.min(
                    maxW / (double) w, maxH / (double) h);

            pw = (int) (w * timesFits);
            ph = (int) (h * timesFits);
        } else if (w < maxW) {
            ph = maxH;
            pw = Math.max(MIN, (int) (w * (ph / (double) h)));
        } else if (h < maxH) {
            pw = maxW;
            ph = Math.max(MIN, (int) (h * (pw / (double) w)));
        } else {
            final double scaleDownW = maxW / (double) w,
                    scaleDownH = maxH / (double) h;

            if (scaleDownW > scaleDownH) {
                ph = Math.max(MIN, (int) (h * scaleDownH));
                pw = Math.max(MIN, (int) (w * scaleDownH));
            } else {
                ph = Math.max(MIN, (int) (h * scaleDownW));
                pw = Math.max(MIN, (int) (w * scaleDownW));
            }
        }

        final GameImage[] previewContent = new GameImage[fc];
        final GameImage checkerboard = c.getCheckerboard();

        for (int i = 0; i < fc; i++) {
            final GameImage frame = preview.draw(false, false, i),
                    composed = new GameImage(pw, ph);

            composed.draw(checkerboard, 0, 0, pw, ph);
            composed.draw(frame, 0, 0, pw, ph);

            previewContent[i] = composed.submit();
        }

        final ActionPreviewer previewer = new ActionPreviewer(
                new Coord2D(getCanvasMiddle().x,
                        textBelowPos(backButton, 1).y),
                new Bounds2D(pw, ph), previewContent,
                preview.getFrameDurations().stream()
                        .mapToDouble(d -> d).toArray());
        mb.add(previewer);

        final PlaybackInfo playbackInfo = previewer.getPlaybackInfo();

        if (fc > 1) {
            // frame and playback mode controls
            final MenuElement firstFrame =
                    IconButton.make(ResourceCodes.TO_FIRST_FRAME,
                            backButton.getPosition()
                                    .displace(0, DIALOG_CONTENT_INC_Y),
                            previewer::toFirstFrame),
                    previousFrame = IconButton.make(ResourceCodes.PREVIOUS,
                            firstFrame.getRenderPosition().displace(BUTTON_INC, 0),
                            previewer::previousFrame),
                    playStop = MenuAssembly.generatePlayStopToggle(playbackInfo,
                            previousFrame.getRenderPosition()
                                    .displace(BUTTON_INC, 0)),
                    nextFrame = IconButton.make(ResourceCodes.NEXT,
                            playStop.getRenderPosition().displace(BUTTON_INC, 0),
                            previewer::nextFrame),
                    lastFrame = IconButton.make(ResourceCodes.TO_LAST_FRAME,
                            nextFrame.getRenderPosition().displace(BUTTON_INC, 0),
                            previewer::toLastFrame);

            final MenuElement playbackModeButton =
                    MenuAssembly.generatePlaybackModeToggle(playbackInfo,
                            lastFrame.getRenderPosition().displace(BUTTON_INC, 0));
            final DynamicLabel frameTracker = makeDynamicLabel(
                    playbackModeButton.getRenderPosition().displace(
                            BUTTON_DIM + CONTENT_BUFFER_PX,
                            -BUTTON_OFFSET + TEXT_Y_OFFSET),
                    () -> (previewer.getFrameIndex() + 1) + "/" +
                            previewer.getFrameCount(), "XXX/XXX");

            mb.addAll(firstFrame, previousFrame, playStop, nextFrame,
                    lastFrame, playbackModeButton, frameTracker);

            // playback speed (FPS)
            final StaticMenuElement fpsReference = new StaticMenuElement(
                    getRightColumnFromLeftDisplacement(
                            firstFrame.getPosition())
                            .displace(-(BUTTON_DIM +
                                    CONTENT_BUFFER_PX), 0),
                    ICON_DIMS, MenuElement.Anchor.LEFT_TOP,
                    GameImage.dummy());
            final int fpsButtonY = firstFrame.getY();
            final IncrementalRangeElements<Integer> fps =
                    IncrementalRangeElements.makeForInt(fpsReference, fpsButtonY,
                            (fpsButtonY - BUTTON_OFFSET) + TEXT_Y_OFFSET,
                            1, Constants.MIN_PLAYBACK_FPS, Constants.MAX_PLAYBACK_FPS,
                            playbackInfo::setFps, playbackInfo::getFps,
                            i -> i, sv -> sv, sv -> sv + " FPS", "XXX FPS");
            mb.addAll(fps.decButton, fps.incButton, fps.slider, fps.value);
        }

        setDialog(assembleDialog("Preview of " + previewAppend,
                new MenuElementGrouping(mb.build().getMenuElements()),
                () -> true, "Apply", onApproval, true));
    }

    public static void setDialogToSavePalette(final Palette palette) {
        final MenuBuilder mb = new MenuBuilder();

        // labels
        final TextLabel
                folderLabel = makeDialogLeftLabel(0, "Folder"),
                nameLabel = TextLabel.make(textBelowPos(folderLabel),
                        "File name");
        mb.addAll(folderLabel, nameLabel);

        // folder button
        final DynamicTextButton folderButton =
                makeFolderSelectionButton(folderLabel,
                        DialogVals::getPaletteFolder,
                        DialogVals::setPaletteFolder);
        mb.add(folderButton);

        // name text box
        final String suggestedFilename = palette.getName().toLowerCase()
                .replaceAll(" ", "_");
        DialogVals.setPaletteName(suggestedFilename);
        final Textbox nameTextbox = makeDialogNameTextBox(nameLabel,
                suggestedFilename, DialogVals::setPaletteName);
        mb.add(nameTextbox);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog("Save palette...",
                contents, () -> nameTextbox.isValid() &&
                        DialogVals.getPaletteFolder() != null, "Save",
                () -> {
                    final Path filepath = DialogVals.getPaletteFolder()
                            .resolve(DialogVals.getPaletteName() +
                                    "." + Constants.PALETTE_FILE_SUFFIX);
                    ParserSerializer.savePalette(palette, filepath);
                }, true));
    }

    public static void setDialogToSortPalette(final Palette palette) {
        DialogVals.setSortPaletteBackwards(false);

        final MenuBuilder mb = new MenuBuilder();

        final TextLabel sortLabel = makeDialogLeftLabel(0, "Sort colors by"),
                backwardsLabel = TextLabel.make(textBelowPos(sortLabel),
                        "Backwards?");
        final Dropdown sortDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(sortLabel),
                DIALOG_CONTENT_SMALL_W_ALLOWANCE,
                EnumUtils.stream(PaletteSorter.class)
                        .map(PaletteSorter::toString)
                        .toArray(String[]::new),
                EnumUtils.stream(PaletteSorter.class)
                        .map(ps -> (Runnable)
                                () -> DialogVals.setPaletteSorter(ps))
                        .toArray(Runnable[]::new),
                () -> DialogVals.getPaletteSorter().ordinal());
        final Checkbox backwardsCheckbox = new Checkbox(
                contentPositionAfterLabel(backwardsLabel),
                new ConcreteProperty<>(
                        DialogVals::isSortPaletteBackwards,
                        DialogVals::setSortPaletteBackwards));
        mb.addAll(sortLabel, sortDropdown, backwardsLabel, backwardsCheckbox);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog(palette.getName() + " | Sort colors",
                contents, () -> true, "Sort", () -> {
                    palette.sort();
                    StippleEffect.get().rebuildColorsMenu();
                }, true));
    }

    public static void setDialogToAddContentsToPalette(
            final SEContext c, final Palette palette
    ) {
        final MenuBuilder mb = new MenuBuilder();

        makeCommonColorOperationElements(mb, c);

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog(palette.getName() + " | Add colors in project to palette",
                contents, () -> true, "Proceed", () -> {
                    c.contentsToPalette(palette);
                    StippleEffect.get().rebuildColorsMenu();
                }, true));
    }

    public static void setDialogToPalettize(
            final SEContext c, final Palette palette
    ) {
        final MenuBuilder mb = new MenuBuilder();

        makeCommonColorOperationElements(mb, c);

        if (palette.size() == 0)
            mb.add(makeDialogLeftLabelAtBottom(
                    "This palette is empty; palettization will be trivial."));

        final MenuElementGrouping contents =
                new MenuElementGrouping(mb.build().getMenuElements());
        setDialog(assembleDialog(palette.getName() + " | Palettize project contents",
                contents, () -> true, "Proceed",
                () -> c.palettize(palette), true));
    }

    public static void setDialogToPaletteSettings(final Palette palette) {
        DialogVals.setPaletteName(palette.getName());

        // text labels
        final TextLabel paletteNameLabel = makeDialogLeftLabel(1, "Name");

        // name textbox
        DialogVals.setPaletteName(palette.getName());
        final Textbox paletteNameTextbox = makeDialogNameTextBox(
                paletteNameLabel, palette.getName(), DialogVals::setPaletteName);

        final MenuElementGrouping contents = new MenuElementGrouping(
                paletteNameLabel, paletteNameTextbox);
        setDialog(assembleDialog(palette.getName() + "  |  Palette Settings",
                contents, paletteNameTextbox::isValid,
                Constants.GENERIC_APPROVAL_TEXT,
                () -> {
                    palette.setName(DialogVals.getPaletteName());

                    StippleEffect.get().rebuildColorsMenu();
                }, true));
    }

    public static void setDialogToInfo() {
        setDialog(assembleInfoDialog());
    }

    public static void setDialogToProgramSettings() {
        final MenuBuilder mb = new MenuBuilder();

        Settings.resetAssignments();

        Arrays.stream(DialogVals.SettingScreen.values()).forEach(ss -> {
            final Coord2D ssPos = getDialogPosition().displace(
                    CONTENT_BUFFER_PX + (ss.ordinal() *
                            (STD_TEXT_BUTTON_W + BUTTON_OFFSET)),
                    CONTENT_BUFFER_PX +
                            (int)(1.5 * STD_TEXT_BUTTON_INC));

            mb.add(SelectableListItemButton.make(ssPos,
                    STD_TEXT_BUTTON_W, ss.toString(), ss.ordinal(),
                    () -> DialogVals.getSettingScreen().ordinal(),
                    i -> DialogVals.setSettingScreen(
                            DialogVals.SettingScreen.values()[i])));
        });

        // decision logic
        final Map<DialogVals.SettingScreen, VerticalScrollBox>
                settingScreens = new HashMap<>();

        for (DialogVals.SettingScreen settingScreen : DialogVals.SettingScreen.values()) {
            settingScreens.put(settingScreen, assembleScroller(settingScreen));
        }

        final ThinkingMenuElement screenDecider = new ThinkingMenuElement(
                () -> settingScreens.get(DialogVals.getSettingScreen()));

        final MenuElementGrouping contents = new MenuElementGrouping(
                new MenuElementGrouping(mb.build().getMenuElements()),
                screenDecider);
        setDialog(assembleDialog("Program Settings", contents, () -> true,
                "Apply", Settings::apply, true));
    }

    public static void setDialogToLayerSettings(final int index) {
        final MenuBuilder mb = new MenuBuilder();

        final SEContext c = StippleEffect.get().getContext();
        final SELayer layer = c.getState().getLayers().get(index);

        DialogVals.setLayerOpacity(layer.getOpacity());
        DialogVals.setLayerName(layer.getName());

        layer.getOnionSkin().prep();

        // text labels
        final TextLabel layerNameLabel =
                makeDialogLeftLabel(0, "Name"),
                opacityLabel = TextLabel.make(textBelowPos(layerNameLabel),
                        "Opacity"),
                onionSkinAnnouncer = TextLabel.make(
                        textBelowPos(opacityLabel), "Onion skin settings"),
                precedingLabel = TextLabel.make(
                        textBelowPos(onionSkinAnnouncer), "Preceding cels"),
                followingLabel = makeDialogRightLabel(
                        precedingLabel, "Following cels"),
                skinTypeBackLabel = TextLabel.make(
                        textBelowPos(precedingLabel), "Type"),
                skinTypeForwardLabel = makeDialogRightLabel(
                        skinTypeBackLabel, "Type"),
                lookBackLabel = TextLabel.make(
                        textBelowPos(skinTypeBackLabel, 2), "# cels"),
                lookForwardLabel = makeDialogRightLabel(lookBackLabel, "# cels"),
                fadeFactorBackLabel = TextLabel.make(
                        textBelowPos(lookBackLabel), "Fade per frame"),
                fadeFactorForwardLabel = makeDialogRightLabel(
                        fadeFactorBackLabel, "Fade per frame"),
                underBackLabel = TextLabel.make(
                        textBelowPos(fadeFactorBackLabel), "Render under?"),
                underForwardLabel = makeDialogRightLabel(
                        underBackLabel, "Render under?");

        mb.addAll(layerNameLabel, opacityLabel, onionSkinAnnouncer,
                precedingLabel, followingLabel,
                skinTypeBackLabel, skinTypeForwardLabel,
                lookBackLabel, lookForwardLabel,
                fadeFactorBackLabel, fadeFactorForwardLabel,
                underBackLabel, underForwardLabel);

        // textboxes
        final Textbox layerNameTextbox = makeDialogNameTextBox(
                layerNameLabel, layer.getName(), DialogVals::setLayerName),
                lookBackTextbox = makeDialogNumericalDynamicTextbox(
                        lookBackLabel, Layout::contentPositionAfterLabel,
                        "", OnionSkin.getDLookBack(), "",
                        lb -> lb >= OnionSkin.MIN_LOOK && lb <= OnionSkin.MAX_LOOK,
                        OnionSkin::setDLookBack, OnionSkin::getDLookBack, 1),
                lookForwardTextbox = makeDialogNumericalDynamicTextbox(
                        lookForwardLabel, Layout::contentPositionAfterLabel,
                        "", OnionSkin.getDLookForward(), "",
                        lb -> lb >= OnionSkin.MIN_LOOK && lb <= OnionSkin.MAX_LOOK,
                        OnionSkin::setDLookForward, OnionSkin::getDLookForward, 1);

        mb.addAll(layerNameTextbox, lookBackTextbox, lookForwardTextbox);

        // fade factor
        final int PERCENT = 100;
        final double delta = 1 / (double) PERCENT;
        final Consumer<Boolean> tickFadeBack = up -> {
            final double was = OnionSkin.getDFadeFactorBack();
            OnionSkin.setDFadeFactorBack(was + (up ? delta : -delta));
        }, tickFadeForward = up -> {
            final double was = OnionSkin.getDFadeFactorForward();
            OnionSkin.setdFadeFactorForward(was + (up ? delta : -delta));
        };
        final IncrementalRangeElements<Double> fadeFactorBackUI =
                IncrementalRangeElements.makeForDouble(fadeFactorBackLabel,
                        fadeFactorBackLabel.getY() +
                                DIALOG_CONTENT_COMP_OFFSET_Y,
                        fadeFactorBackLabel.getY(),
                        () -> tickFadeBack.accept(false),
                        () -> tickFadeBack.accept(true),
                        OnionSkin.MIN_FADE, OnionSkin.MAX_FADE,
                        OnionSkin::setDFadeFactorBack,
                        OnionSkin::getDFadeFactorBack,
                        o -> (int)(o * PERCENT),
                        sv -> sv / (double) PERCENT,
                        o -> (int) (o * PERCENT) + "%", "XXX%"),
                fadeFactorForwardUI = IncrementalRangeElements.makeForDouble(
                        fadeFactorForwardLabel,
                        fadeFactorForwardLabel.getY() + DIALOG_CONTENT_COMP_OFFSET_Y,
                        fadeFactorBackLabel.getY(),
                        () -> tickFadeForward.accept(false),
                        () -> tickFadeForward.accept(true),
                        OnionSkin.MIN_FADE, OnionSkin.MAX_FADE,
                        OnionSkin::setdFadeFactorForward,
                        OnionSkin::getDFadeFactorForward,
                        o -> (int)(o * PERCENT),
                        sv -> sv / (double) PERCENT,
                        o -> (int) (o * PERCENT) + "%", "XXX%");
        mb.addAll(fadeFactorBackUI.decButton, fadeFactorBackUI.incButton,
                fadeFactorBackUI.slider, fadeFactorBackUI.value,
                fadeFactorForwardUI.decButton, fadeFactorForwardUI.incButton,
                fadeFactorForwardUI.slider, fadeFactorForwardUI.value);

        // hues
        final TextLabel backRef = TextLabel.make(
                textBelowPos(skinTypeBackLabel), ""),
                forwardRef = makeDialogRightLabel(backRef, "");
        mb.addAll(new GatewayMenuElement(new MenuElementGrouping(
                ColorComponent.onionSkinHue(backRef.getPosition(), true)
                ), () -> OnionSkin.getDSkinTypeBack().hasHue()),
                new GatewayMenuElement(new MenuElementGrouping(
                        ColorComponent.onionSkinHue(forwardRef.getRenderPosition(), false)
                ), () -> OnionSkin.getdSkinTypeForward().hasHue()));

        // opacity slider
        final int MAX_OPACITY = Constants.RGBA_SCALE;
        DialogVals.setLayerOpacity(layer.getOpacity());

        final Runnable fDecrement = () -> {
            final double was = DialogVals.getLayerOpacity();
            DialogVals.setLayerOpacity(Math.max(0d,
                    was - (1d / (double) MAX_OPACITY)));
        }, fIncrement = () -> {
            final double was = DialogVals.getLayerOpacity();
            DialogVals.setLayerOpacity(Math.min(Constants.OPAQUE,
                    was + (1d / (double) MAX_OPACITY)));
        };
        final IncrementalRangeElements<Double> opacityUI =
                IncrementalRangeElements.makeForDouble(opacityLabel,
                        opacityLabel.getY() +
                                DIALOG_CONTENT_COMP_OFFSET_Y,
                        opacityLabel.getY(), fDecrement, fIncrement,
                        0, Constants.OPAQUE, DialogVals::setLayerOpacity,
                        DialogVals::getLayerOpacity,
                        o -> (int)(o * MAX_OPACITY),
                        sv -> sv / (double) MAX_OPACITY,
                        o -> String.valueOf((int) (o * MAX_OPACITY)), "XXX");

        mb.addAll(opacityUI.decButton, opacityUI.incButton,
                opacityUI.slider, opacityUI.value);

        final Dropdown skinTypeBackDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(skinTypeBackLabel),
                EnumUtils.stream(OnionSkin.SkinType.class)
                        .map(EnumUtils::formattedName)
                        .toArray(String[]::new),
                EnumUtils.stream(OnionSkin.SkinType.class)
                        .map(os -> (Runnable) () -> OnionSkin.setDSkinTypeBack(os))
                        .toArray(Runnable[]::new),
                () -> OnionSkin.getDSkinTypeBack().ordinal()),
                skinTypeForwardDropdown = Dropdown.forDialog(
                        contentPositionAfterLabel(skinTypeForwardLabel),
                        EnumUtils.stream(OnionSkin.SkinType.class)
                                .map(EnumUtils::formattedName)
                                .toArray(String[]::new),
                        EnumUtils.stream(OnionSkin.SkinType.class)
                                .map(os -> (Runnable)
                                        () -> OnionSkin.setdSkinTypeForward(os))
                                .toArray(Runnable[]::new),
                        () -> OnionSkin.getdSkinTypeForward().ordinal());

        final Checkbox underBackCheckbox = new Checkbox(
                contentPositionAfterLabel(underBackLabel), new ConcreteProperty<>(
                        OnionSkin::isDUnderBack, OnionSkin::setDUnderBack)),
                underForwardCheckbox = new Checkbox(
                        contentPositionAfterLabel(underForwardLabel),
                        new ConcreteProperty<>(OnionSkin::isDUnderForward,
                                OnionSkin::setdUnderForward));

        mb.addAll(skinTypeBackDropdown, skinTypeForwardDropdown,
                underBackCheckbox, underForwardCheckbox);

        setDialog(assembleDialog(layer.getName() + "  |  Layer Settings",
                new MenuElementGrouping(mb.build().getMenuElements()),
                layerNameTextbox::isValid,
                Constants.GENERIC_APPROVAL_TEXT, () -> {
            final OnionSkin configured = OnionSkin.load();
            final String name = DialogVals.getLayerName();
            final double opacity = DialogVals.getLayerOpacity();

            if (!layer.getName().equals(name))
                c.changeLayerName(name, index);

            if (layer.getOpacity() != opacity)
                c.changeLayerOpacity(opacity, index, true);

            if (!layer.getOnionSkin().equals(configured))
                c.changeLayerOnionSkin(configured, index);
        }, true));
    }

    public static void setDialogToFrameProperties(final int index) {
        final SEContext c = StippleEffect.get().getContext();
        final MenuBuilder mb = new MenuBuilder();

        DialogVals.setFrameDuration(c.getState().getFrameDurations().get(index));

        final TextLabel durationLabel = makeDialogLeftLabel(0, "Frame duration");

        final double STEP = 0.1, DIV = 10d,
                MIN = Constants.MIN_FRAME_DURATION,
                MAX = Constants.MAX_FRAME_DURATION;
        final Runnable fDecrement = () -> {
            final double was = DialogVals.getFrameDuration(),
                    v = Math.max(MIN, was - STEP);

            DialogVals.setFrameDuration(Math.round(v * DIV) / DIV);
        }, fIncrement = () -> {
            final double was = DialogVals.getFrameDuration(),
                    v = Math.min(MAX, was + STEP);

            DialogVals.setFrameDuration(Math.round(v * DIV) / DIV);
        };
        final IncrementalRangeElements<Double> duration =
                IncrementalRangeElements.makeForDouble(durationLabel,
                        durationLabel.getY() +
                                DIALOG_CONTENT_COMP_OFFSET_Y,
                        durationLabel.getY(), fDecrement, fIncrement,
                        MIN, MAX, DialogVals::setFrameDuration,
                        DialogVals::getFrameDuration,
                        o -> (int)(o * DIV), sv -> sv / DIV,
                        o -> o + "x", "XXXXx");

        final StaticTextButton resetDuration =
                GraphicsUtils.makeStandardTextButton("Reset",
                        getDialogRightContentPositionForRow(0)
                                .displace(0, DIALOG_CONTENT_COMP_OFFSET_Y),
                        () -> DialogVals.setFrameDuration(
                                Constants.DEFAULT_FRAME_DURATION));

        mb.addAll(durationLabel, duration.decButton, duration.incButton,
                duration.slider, duration.value, resetDuration);

        setDialog(assembleDialog("Frame " + (index + 1) + " | Properties",
                new MenuElementGrouping(mb.build().getMenuElements()),
                () -> true, Constants.GENERIC_APPROVAL_TEXT,
                () -> c.changeFrameDuration(DialogVals.getFrameDuration(), index),
                true));
    }

    public static void setDialogToSplashScreen() {
        final MenuBuilder mb = new MenuBuilder();
        final Theme t = Settings.getTheme();

        // timer
        mb.add(new TimedMenuElement(
                (int)(Constants.SPLASH_TIMEOUT_SECS * Constants.TICK_HZ),
                () -> StippleEffect.get().clearDialog()));

        final int w = width(), h = height();

        // background
        final GameImage background = new GameImage(w, h);
        background.free();

        background.fillRectangle(t.splashBackground, 0, 0, w, h);
        mb.add(new SimpleMenuButton(new Coord2D(), new Bounds2D(w, h),
                MenuElement.Anchor.LEFT_TOP, true,
                DialogAssembly::setDialogToDemoLanding, background, background));

        // version
        final GameImage version = GraphicsUtils.uiText(t.splashText)
                .addText(StippleEffect.getVersion()).build().draw();

        mb.add(new StaticMenuElement(new Coord2D(w / 2, h),
                new Bounds2D(version.getWidth(), version.getHeight()),
                MenuElement.Anchor.CENTRAL_BOTTOM, version));

        // gateway
        final GameImage ctc = GraphicsUtils.uiText(t.splashFlashingText)
                .addText("Click anywhere to continue").build().draw();

        mb.add(new AnimationMenuElement(new Coord2D(w - CONTENT_BUFFER_PX, h),
                new Bounds2D(ctc.getWidth(), ctc.getHeight()),
                MenuElement.Anchor.RIGHT_BOTTOM, (int)(Constants.TICK_HZ / 2),
                ctc, GameImage.dummy()));

        // animation frames
        final GameImage[] frames = t.logic.loadSplash();
        mb.add(new AnimationMenuElement(getCanvasMiddle(),
                new Bounds2D(frames[0].getWidth(), frames[0].getHeight()),
                MenuElement.Anchor.CENTRAL, t.logic.ticksPerSplashFrame(),
                frames));

        // subtitle
        final GameImage subtitle = GraphicsUtils.uiText(t.splashText)
                .addText(t.subtitle).addLineBreak()
                .addText("Copyright (c) 2025 Jordan Bunke")
                .build().draw();

        mb.add(new StaticMenuElement(new Coord2D(w / 2, h - (version.getHeight() * 2)),
                new Bounds2D(subtitle.getWidth(), subtitle.getHeight()),
                MenuElement.Anchor.CENTRAL_BOTTOM, subtitle));

        setDialog(mb.build());
    }

    private static void setDialogToDemoLanding() {
        final MenuBuilder mb = new MenuBuilder();

        mb.add(makeDialogLeftLabel(0,
                "This demo is a limited version of " +
                        StippleEffect.PROGRAM_NAME + " " +
                        StippleEffect.getVersion() + "."));

        final String[] lines = ParserUtils.getBlurb(ResourceCodes.DEMO);
        final int BLURB_Y_INDEX = 2;

        for (int y = 0; y < lines.length; y++)
            mb.add(makeDialogLeftLabel(BLURB_Y_INDEX + y, lines[y]));

        final TextLabel storePageLabel =
                makeDialogLeftLabel(BLURB_Y_INDEX + lines.length + 1,
                        "You can buy " + StippleEffect.PROGRAM_NAME +
                                " to access all of the program's features:");
        final StaticTextButton storePageButton =
                GraphicsUtils.makeStandardTextButton("Go",
                        contentPositionAfterLabel(storePageLabel),
                        WebUtils::buyPage);

        mb.addAll(storePageLabel, storePageButton);

        setDialog(assembleDialog("Welcome!",
                new MenuElementGrouping(mb.build().getMenuElements()),
                () -> true, Constants.CLOSE_DIALOG_TEXT, () -> {}, true));
    }

    private static void setDialog(final Menu dialog) {
        StippleEffect.get().setDialog(dialog);
    }

    private static void makeCommonColorOperationElements(
            final MenuBuilder mb, final SEContext c
    ) {
        final DialogVals.Scope[] vs = DialogVals.Scope.values();

        final int initialIndex = DialogVals.getScope().ordinal();
        final boolean hasSelection = c.getState().hasSelection();

        final TextLabel scopeLabel = makeDialogLeftLabel(0, "Scope"),
                disabledLayersLabel = TextLabel.make(
                        textBelowPos(scopeLabel),
                        "Include disabled layers?"),
                ignoreSelectionLabel = TextLabel.make(
                        textBelowPos(disabledLayersLabel),
                        "Ignore selection?");
        final Dropdown dropdown = Dropdown.forDialog(
                contentPositionAfterLabel(scopeLabel),
                DIALOG_CONTENT_SMALL_W_ALLOWANCE,
                Arrays.stream(vs).map(DialogVals.Scope::toString)
                        .toArray(String[]::new),
                Arrays.stream(vs).map(s -> (Runnable) () -> DialogVals.setScope(s))
                        .toArray(Runnable[]::new),
                () -> initialIndex);
        final Checkbox disabledLayersCheckbox = new Checkbox(
                contentPositionAfterLabel(disabledLayersLabel),
                new ConcreteProperty<>(
                        DialogVals::isIncludeDisabledLayers,
                        DialogVals::setIncludeDisabledLayers)),
                ignoreSelectionCheckbox = new Checkbox(
                        contentPositionAfterLabel(
                                ignoreSelectionLabel),
                        new ConcreteProperty<>(
                                DialogVals::isIgnoreSelection,
                                DialogVals::setIgnoreSelection));
        final GatewayMenuElement disabledLayersGate =
                new GatewayMenuElement(
                        new MenuElementGrouping(
                                disabledLayersLabel, disabledLayersCheckbox),
                        () -> DialogVals.getScope().considersLayers()),
                ignoreSelectionGate = new GatewayMenuElement(
                        new MenuElementGrouping(
                                ignoreSelectionLabel, ignoreSelectionCheckbox),
                        () -> hasSelection);

        mb.addAll(scopeLabel, dropdown, disabledLayersGate, ignoreSelectionGate);
    }

    private static void makeStitchElementsForSaveSpriteSheet(
            final MenuBuilder mb, final SEContext c,
            final TextLabel referenceLabel
    ) {
        final SaveConfig sc = c.getSaveConfig();

        makeStitchElements(mb, sc::setFramesPerDim, sc::getFramesPerDim,
                sc::setSequenceOrder, sc::getSequenceOrder,
                () -> sc.calculateNumFrames(c), referenceLabel);
    }

    private static void makeStitchElements(
            final MenuBuilder mb, final Supplier<Integer> fcGetter,
            final TextLabel referenceLabel
    ) {
        makeStitchElements(mb, DialogVals::setFramesPerDim,
                DialogVals::getFramesPerDim,
                DialogVals::setSequenceOrder, DialogVals::getSequenceOrder,
                fcGetter, referenceLabel);
    }

    private static void makeStitchElements(
            final MenuBuilder mb,
            final Consumer<Integer> fpdSetter,
            final Supplier<Integer> fpdGetter,
            final Consumer<DialogVals.SequenceOrder> soSetter,
            final Supplier<DialogVals.SequenceOrder> soGetter,
            final Supplier<Integer> fcGetter,
            final TextLabel referenceLabel
    ) {
        // pre-processing
        fpdSetter.accept(fcGetter.get());

        // sequence order
        makeSequenceOrderElements(mb, soGetter, soSetter, referenceLabel);

        // frames per [dim]
        final String FPD_PREFIX = "Frames per ";
        final DynamicLabel framesPerDimLabel = makeDynamicLabel(
                textBelowPos(referenceLabel, 2), () -> FPD_PREFIX +
                        soGetter.get().dimName(), FPD_PREFIX + "column");
        final DynamicTextbox framesPerDimTextbox =
                makeDialogNumericalDynamicTextbox(
                        framesPerDimLabel,
                        Layout::contentPositionAfterLabel,
                        "", fcGetter.get(), "",
                        tbv -> tbv >= 1 && tbv <= Constants.MAX_NUM_FRAMES,
                        fpdSetter, fpdGetter,
                        String.valueOf(Constants.MAX_NUM_FRAMES).length());
        mb.addAll(framesPerDimLabel, framesPerDimTextbox);

        // frames per complementary dim
        final String FPCD_PREFIX = "... and ", FPCD_INFIX = " frames per ",
                FPCD_INFIX_SING = FPCD_INFIX.replace("s ", " ");
        final DynamicLabel framesPerCompDim = makeDynamicLabel(
                textBelowPos(framesPerDimLabel), () -> {
                    final int comp = DialogVals
                            .calculateFramesPerComplementaryDim(
                                    fcGetter.get(), fpdGetter.get());

                    return FPCD_PREFIX + comp + (
                            comp == 1 ? FPCD_INFIX_SING : FPCD_INFIX
                    ) + soGetter.get().complement().dimName();
                }, FPCD_PREFIX +
                        Constants.MAX_NUM_FRAMES + FPCD_INFIX + "column");
        mb.add(framesPerCompDim);
    }

    private static void makeSplitElements(
            final MenuBuilder mb,
            final int w, final int h,
            final TextLabel reference
    ) {
        makeSplitElements(mb, () -> w, () -> h,
                DialogVals::setSplitColumns, DialogVals::setSplitRows,
                DialogVals::setSplitColumns, DialogVals::setSplitRows,
                DialogVals::getSplitColumns, DialogVals::getSplitRows,
                DialogVals::setFrameWidth, DialogVals::setFrameHeight,
                DialogVals::getFrameWidth, DialogVals::getFrameHeight,
                reference);
    }

    private static void makeSplitElementsForOpenPNG(
            final MenuBuilder mb,
            final TextLabel reference
    ) {
        makeSplitElements(mb,
                DialogVals::getImportWidth, DialogVals::getImportHeight,
                DialogVals::setImportColumns, DialogVals::setImportRows,
                DialogVals::setImportColumns, DialogVals::setImportRows,
                DialogVals::getImportColumns, DialogVals::getImportRows,
                DialogVals::setImportFrameWidth, DialogVals::setImportFrameHeight,
                DialogVals::getImportFrameWidth, DialogVals::getImportFrameHeight,
                reference);
    }

    private static void makeSplitElements(
            final MenuBuilder mb,
            final Supplier<Integer> canvasWidthGetter,
            final Supplier<Integer> canvasHeightGetter,
            final Consumer<Integer> colSetter,
            final Consumer<Integer> rowSetter,
            final BiConsumer<Integer, Integer> biColSetter,
            final BiConsumer<Integer, Integer> biRowSetter,
            final Supplier<Integer> colGetter,
            final Supplier<Integer> rowGetter,
            final BiConsumer<Integer, Integer> widthSetter,
            final BiConsumer<Integer, Integer> heightSetter,
            final Supplier<Integer> widthGetter,
            final Supplier<Integer> heightGetter,
            final TextLabel referenceLabel
    ) {
        // pre-processing
        final int initialColumns = canvasWidthGetter.get() / widthGetter.get(),
                initialRows = canvasHeightGetter.get() / heightGetter.get();

        colSetter.accept(initialColumns);
        rowSetter.accept(initialRows);

        // sequence order
        makeSequenceOrderElements(mb, DialogVals::getSequenceOrder,
                DialogVals::setSequenceOrder, referenceLabel);

        // columns
        final TextLabel columnsLabel = TextLabel.make(
                textBelowPos(referenceLabel, 2), "Columns");
        final DynamicTextbox columnsTextbox = makeDialogDynamicTextbox(
                columnsLabel, Layout::contentPositionAfterLabel,
                1, initialColumns, Constants.MAX_NUM_FRAMES, "",
                x -> biColSetter.accept(x, canvasWidthGetter.get()), colGetter,
                String.valueOf(Constants.MAX_NUM_FRAMES).length());
        mb.addAll(columnsLabel, columnsTextbox);

        // rows
        final TextLabel yDivsLabel = makeDialogRightLabel(
                columnsLabel, "Rows");
        final DynamicTextbox yDivsTextbox = makeDialogDynamicTextbox(
                yDivsLabel, Layout::contentPositionAfterLabel,
                1, initialRows, Constants.MAX_NUM_FRAMES, "",
                y -> biRowSetter.accept(y, canvasHeightGetter.get()), rowGetter,
                String.valueOf(Constants.MAX_NUM_FRAMES).length());
        mb.addAll(yDivsLabel, yDivsTextbox);

        // frame width
        final TextLabel frameWidthLabel = TextLabel.make(
                textBelowPos(columnsLabel), "Frame width");
        final DynamicTextbox frameWidthTextbox =
                makeDialogPixelDynamicTextbox(frameWidthLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_W, Constants.MAX_CANVAS_W,
                        fw -> widthSetter.accept(fw, canvasWidthGetter.get()), widthGetter,
                        String.valueOf(Constants.MAX_CANVAS_W).length());
        mb.addAll(frameWidthLabel, frameWidthTextbox);

        // frame height
        final TextLabel frameHeightLabel = makeDialogRightLabel(
                frameWidthLabel, "Frame height");
        final DynamicTextbox frameHeightTextbox =
                makeDialogPixelDynamicTextbox(frameHeightLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_H, Constants.MAX_CANVAS_H,
                        fh -> heightSetter.accept(fh, canvasHeightGetter.get()), heightGetter,
                        String.valueOf(Constants.MAX_CANVAS_H).length());
        mb.addAll(frameHeightLabel, frameHeightTextbox);

        final String[] remainderLabels =
                new String[] { "Extra frame", "Truncate" };

        // X-axis remainder
        final TextLabel xRemainderLabel = TextLabel.make(
                textBelowPos(frameWidthLabel), "X-axis remainder");
        final Dropdown xRemainderDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(xRemainderLabel),
                remainderLabels, new Runnable[] {
                        () -> DialogVals.setTruncateSplitX(false),
                        () -> DialogVals.setTruncateSplitX(true)
                }, () -> DialogVals.isTruncateSplitX() ? 1 : 0);
        final GatewayMenuElement xRemainder = new GatewayMenuElement(
                new MenuElementGrouping(xRemainderLabel, xRemainderDropdown),
                () -> canvasWidthGetter.get() % widthGetter.get() != 0);
        mb.add(xRemainder);

        // Y-axis remainder
        final TextLabel yRemainderLabel = makeDialogRightLabel(
                xRemainderLabel, "Y-axis remainder");
        final Dropdown yRemainderDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(yRemainderLabel),
                remainderLabels, new Runnable[] {
                        () -> DialogVals.setTruncateSplitY(false),
                        () -> DialogVals.setTruncateSplitY(true)
                }, () -> DialogVals.isTruncateSplitY() ? 1 : 0);
        final GatewayMenuElement yRemainder = new GatewayMenuElement(
                new MenuElementGrouping(yRemainderLabel, yRemainderDropdown),
                () -> canvasHeightGetter.get() % heightGetter.get() != 0);
        mb.add(yRemainder);
    }

    private static void makeSequenceOrderElements(
            final MenuBuilder mb,
            final Supplier<DialogVals.SequenceOrder> soGetter,
            final Consumer<DialogVals.SequenceOrder> soSetter,
            final TextLabel referenceLabel
    ) {
        final TextLabel sequenceLabel = TextLabel.make(
                textBelowPos(referenceLabel, 1), "Sequence order");
        final Dropdown sequenceDropdown = Dropdown.forDialog(
                contentPositionAfterLabel(sequenceLabel),
                EnumUtils.stream(DialogVals.SequenceOrder.class)
                        .map(EnumUtils::formattedName).toArray(String[]::new),
                EnumUtils.stream(DialogVals.SequenceOrder.class)
                        .map(so -> (Runnable) () -> soSetter.accept(so))
                        .toArray(Runnable[]::new),
                () -> soGetter.get().ordinal());
        mb.addAll(sequenceLabel, sequenceDropdown);
    }

    private static DynamicTextButton makeFolderSelectionButton(
            final TextLabel label,
            final Supplier<Path> getter, final Consumer<Path> setter
    ) {
        return new DynamicTextButton(
                contentPositionAfterLabel(label),
                LONG_NAME_TEXTBOX_W,
                () -> {
                    FileIO.setDialogToFoldersOnly();
                    final Optional<File> opened = FileIO.openFileFromSystem();

                    if (opened.isEmpty())
                        return;

                    final Path folder = opened.get().toPath();
                    setter.accept(folder);
                },
                () -> {
                    final StringBuilder folderPathName = new StringBuilder();
                    final String ELLIPSE = "...";

                    Path folder = getter.get();
                    int placements = 0;

                    if (folder == null)
                        return Constants.NO_FOLDER_SELECTED;

                    do {
                        final Path filename = folder.getFileName();

                        final String level = filename != null
                                ? filename.toString()
                                : folder.getRoot().toString();

                        if (placements == 0)
                            folderPathName.insert(0, level);
                        else if (folderPathName.length() + File.separator.length() +
                                level.length() <= Constants.MAX_NAME_LENGTH) {
                            folderPathName.insert(0, File.separator);
                            folderPathName.insert(0, level);
                        } else {
                            folderPathName.insert(0, File.separator);
                            folderPathName.insert(0, ELLIPSE);
                            break;
                        }

                        placements++;
                        folder = folder.getParent();
                    } while (folder != null);

                    return folderPathName.toString();
                });
    }

    private static DynamicTextbox makeDialogPixelDynamicTextbox(
            final MenuElement label,
            final Function<MenuElement, Coord2D> offsetFunction,
            final int min, final int max,
            final Consumer<Integer> setter, final Supplier<Integer> getter,
            final int maxLength
    ) {
        return makeDialogDynamicTextbox(label, offsetFunction, min,
                getter.get(), max, "px", setter, getter, maxLength);
    }

    private static DynamicTextbox makeDialogNumericalDynamicTextbox(
            final MenuElement label,
            final Function<MenuElement, Coord2D> offsetFunction,
            final String prefix, final int initial, final String suffix,
            final Function<Integer, Boolean> numValidator,
            final Consumer<Integer> setter, final Supplier<Integer> getter,
            final int maxLength
    ) {
        return new DynamicTextbox(offsetFunction.apply(label),
                SMALL_TEXT_BOX_W, MenuElement.Anchor.LEFT_TOP,
                prefix, String.valueOf(initial), suffix,
                Textbox.getIntTextValidator(numValidator),
                s -> setter.accept(Integer.parseInt(s)),
                () -> String.valueOf(getter.get()), maxLength);
    }

    private static DynamicTextbox makeDialogDynamicTextbox(
            final MenuElement label,
            final Function<MenuElement, Coord2D> offsetFunction,
            final int min, final int initial, final int max,
            final String suffix,
            final Consumer<Integer> setter, final Supplier<Integer> getter,
            final int maxLength
    ) {
        return new DynamicTextbox(offsetFunction.apply(label),
                SMALL_TEXT_BOX_W, MenuElement.Anchor.LEFT_TOP,
                "", String.valueOf(initial), suffix,
                Textbox.getIntTextValidator(min, max),
                s -> setter.accept(Integer.parseInt(s)),
                () -> String.valueOf(getter.get()), maxLength);
    }

    private static DynamicTextbox makeDialogAffixDynamicTextbox(
            final MenuElement label, final Consumer<String> setter,
            final Supplier<String> getter
    ) {
        return new DynamicTextbox(
                contentPositionAfterLabel(label),
                SMALL_TEXT_BOX_W, MenuElement.Anchor.LEFT_TOP,
                "", getter.get(), "",
                Textbox::validateAsOptionallyEmptyFilename,
                setter, getter, 5);
    }

    private static DynamicTextbox makeDialogPadTextBox(
            final MenuElement label,
            final Function<Integer, Boolean> validatorLogic,
            final Consumer<Integer> setter, final Supplier<Integer> getter
    ) {
        return makeDialogNumericalDynamicTextbox(
                label, DialogAssembly::getDialogContentOffsetFromLabel,
                "", 0, "px", validatorLogic, setter, getter, 4);
    }

    private static Textbox makeDialogCustomTextBox(
            final MenuElement label, final int width,
            final Function<MenuElement, Coord2D> offsetFunction,
            final Supplier<String> prefixGetter, final String initial,
            final Supplier<String> suffixGetter,
            final Function<String, Boolean> textValidator,
            final Consumer<String> setter, final int length
    ) {
        return new Textbox(offsetFunction.apply(label), width,
                MenuElement.Anchor.LEFT_TOP, prefixGetter, initial, suffixGetter,
                textValidator, setter, length);
    }

    private static Textbox makeDialogNameTextBox(
            final MenuElement label, final String initial,
            final Consumer<String> setter
    ) {
        return new Textbox(contentPositionAfterLabel(label),
                LONG_NAME_TEXTBOX_W, MenuElement.Anchor.LEFT_TOP,
                initial, Textbox::validateAsFileName, setter,
                Constants.MAX_NAME_LENGTH);
    }

    private static DynamicLabel makeDynamicLabel(
            final Coord2D position, final Supplier<String> getter,
            final String widestTextCase
    ) {
        return DynamicLabel.make(position, getter,
                DynamicLabel.getWidth(widestTextCase));
    }

    private static TextLabel makeValidDimensionsBottomLabel() {
        return makeDialogLeftLabelAtBottom("(Canvas sizes can range from " +
                Constants.MIN_CANVAS_W + "x" + Constants.MIN_CANVAS_H + " to " +
                Constants.MAX_CANVAS_W + "x" + Constants.MAX_CANVAS_H + ")");
    }

    private static DynamicLabel makeDialogLeftDynamicLabelAtBottom(
            final Supplier<String> getter) {
        final int y = getCanvasMiddle()
                .displace(0, getDialogHeight() / 2)
                .displace(0, -(DIALOG_CONTENT_INC_Y +
                        CONTENT_BUFFER_PX)).y;

        return DynamicLabel.make(
                new Coord2D(getDialogContentInitial().x, y),
                getter, getDialogWidth());
    }

    private static TextLabel makeDialogLeftLabelAtBottom(final String text) {
        final int y = getCanvasMiddle()
                .displace(0, getDialogHeight() / 2)
                .displace(0, -(DIALOG_CONTENT_INC_Y +
                        CONTENT_BUFFER_PX)).y;

        return TextLabel.make(new Coord2D(getDialogContentInitial().x,
                y), text);
    }

    private static TextLabel makeDialogLeftLabel(final int index, final String text) {
        return TextLabel.make(getDialogLeftContentPositionForRow(index),
                text);
    }

    private static TextLabel makeDialogRightLabel(final TextLabel leftLabel, final String text) {
        return TextLabel.make(getRightColumnFromLeftDisplacement(
                leftLabel.getRenderPosition()), text);
    }

    private static Coord2D textBelowPos(final MenuElement reference) {
        return textBelowPos(reference, 0);
    }

    private static Coord2D textBelowPos(
            final MenuElement reference, final int lineBreaks
    ) {
        return reference.getRenderPosition().displace(0,
                DIALOG_CONTENT_INC_Y * (1 + lineBreaks));
    }

    private static Coord2D getDialogLeftContentPositionForRow(final int index) {
        return getDialogContentInitial()
                .displace(0, index * DIALOG_CONTENT_INC_Y);
    }

    private static Coord2D getDialogRightContentPositionForRow(final int index) {
        return getRightColumnFromLeftDisplacement(
                getDialogLeftContentPositionForRow(index));
    }

    private static Coord2D getRightColumnFromLeftDisplacement(final Coord2D left) {
        return left.displace(getDialogWidth() / 2, 0);
    }

    private static Coord2D getDialogContentOffsetFromLabel(final MenuElement label) {
        return label.getRenderPosition().displace(
                DIALOG_CONTENT_OFFSET_X,
                DIALOG_CONTENT_COMP_OFFSET_Y);
    }

    private static Coord2D getDialogContentToRightOfContent(
            final MenuElement preceding) {
        return preceding.getRenderPosition()
                .displace(preceding.getWidth() + CONTENT_BUFFER_PX, 0);
    }

    private static String cutOffAtNextSpace(String s, int i) {
        while (i < s.length()) {
            if (s.charAt(i) == ' ')
                return s.substring(0, i);
            i++;
        }

        return s;
    }

    private static Color simpleTextColor() {
        return ThemeLogic.intuitTextColor(
                Settings.getTheme().panelBackground, true);
    }

    private static VerticalScrollBox assembleScroller(
            final DialogVals.SettingScreen settingScreen
    ) {
        final MenuBuilder mb = new MenuBuilder();
        final Theme t = Settings.getTheme();

        // title
        final Coord2D titlePosition = getDialogPosition().displace(
                CONTENT_BUFFER_PX + BUTTON_BORDER_PX,
                (int)(3.5 * STD_TEXT_BUTTON_INC));
        mb.add(TextLabel.make(titlePosition, settingScreen.getTitle(),
                t.textMenuHeading, 2d));
        final int initialYIndex = 4;

        // initialize in every execution path
        final MenuElement bottomLabel = switch (settingScreen) {
            case DEFAULTS -> {
                // text labels
                final TextLabel fullscreenLabel = makeDialogLeftLabel(
                        initialYIndex, "Fullscreen on startup"),
                        pixelGridDefaultLabel = TextLabel.make(
                                textBelowPos(fullscreenLabel, 1),
                                "Pixel grid on by default"),
                        defaultNewProjectSizeLabel = TextLabel.make(
                                textBelowPos(pixelGridDefaultLabel, 1),
                                "Default canvas size for new projects"),
                        newProjectWidthLabel = TextLabel.make(
                                textBelowPos(defaultNewProjectSizeLabel),
                                "Width"),
                        newProjectHeightLabel = makeDialogRightLabel(
                                newProjectWidthLabel, "Height"),
                        defaultToolBreadthLabel = TextLabel.make(
                                textBelowPos(newProjectWidthLabel, 1),
                                "Default tool breadth"),
                        frameAffixLabel = TextLabel.make(
                                textBelowPos(defaultToolBreadthLabel, 1),
                                "Default separate PNGs frame affixes"),
                        prefixLabel = TextLabel.make(
                                textBelowPos(frameAffixLabel),
                                "Prefix"),
                        suffixLabel = makeDialogRightLabel(prefixLabel,
                                "Suffix");

                final Checkbox fullscreenCheckbox = new Checkbox(
                        contentPositionAfterLabel(fullscreenLabel),
                        new ConcreteProperty<>(
                                Settings::checkIsFullscreenOnStartup,
                                Settings::setFullscreenOnStartup)),
                        pixelGridCheckbox = new Checkbox(
                                contentPositionAfterLabel(
                                        pixelGridDefaultLabel),
                                new ConcreteProperty<>(
                                        Settings::checkIsPixelGridOnByDefault,
                                        Settings::setPixelGridOnByDefault));

                final DynamicTextbox widthTextbox = makeDialogPixelDynamicTextbox(
                        newProjectWidthLabel,
                        Layout::contentPositionAfterLabel,
                        Constants.MIN_CANVAS_W, Constants.MAX_CANVAS_W,
                        Settings::setDefaultCanvasWPixels,
                        Settings::checkDefaultCanvasWPixels, 4),
                        heightTextbox = makeDialogPixelDynamicTextbox(
                                newProjectHeightLabel,
                                Layout::contentPositionAfterLabel,
                                Constants.MIN_CANVAS_H, Constants.MAX_CANVAS_H,
                                Settings::setDefaultCanvasHPixels,
                                Settings::checkDefaultCanvasHPixels, 4),
                        toolBreadthTextbox = makeDialogPixelDynamicTextbox(
                                defaultToolBreadthLabel,
                                Layout::contentPositionAfterLabel,
                                Constants.MIN_BREADTH, Constants.MAX_BREADTH,
                                Settings::setDefaultToolBreadth,
                                Settings::checkDefaultToolBreadth, 3);

                final String FA_EXAMPLE = "Example: base_name", FA_MAX_VALUE = "WWWWW";
                final DynamicLabel frameAffixExample = makeDynamicLabel(
                        textBelowPos(prefixLabel), () -> FA_EXAMPLE +
                                Settings.checkDefaultIndexPrefix() + "0" +
                                Settings.checkDefaultIndexSuffix() + ".png",
                        FA_EXAMPLE + FA_MAX_VALUE + "0" + FA_MAX_VALUE + ".png");

                final DynamicTextbox prefixTextbox =
                        makeDialogAffixDynamicTextbox(prefixLabel,
                                Settings::setDefaultIndexPrefix,
                                Settings::checkDefaultIndexPrefix),
                        suffixTextbox = makeDialogAffixDynamicTextbox(
                                suffixLabel, Settings::setDefaultIndexSuffix,
                                Settings::checkDefaultIndexSuffix);

                mb.addAll(fullscreenLabel, fullscreenCheckbox,
                        pixelGridDefaultLabel, pixelGridCheckbox,
                        defaultNewProjectSizeLabel,
                        newProjectWidthLabel, widthTextbox,
                        newProjectHeightLabel, heightTextbox,
                        defaultToolBreadthLabel, toolBreadthTextbox,
                        frameAffixLabel, prefixLabel, suffixLabel,
                        frameAffixExample, prefixTextbox, suffixTextbox);

                // update as new settings are added to category
                yield frameAffixExample;
            }
            case CONTROLS -> {
                final TextLabel scrollDirectionLabel = makeDialogLeftLabel(
                        initialYIndex, "Direction inversion settings:"),
                        invertZoomLabel = TextLabel.make(
                                textBelowPos(scrollDirectionLabel),
                                "Invert zoom direction?"),
                        invertBreadthLabel = TextLabel.make(
                                textBelowPos(invertZoomLabel),
                                "Invert tool breadth incrementation direction?"),
                        invertToleranceLabel = TextLabel.make(
                                textBelowPos(invertBreadthLabel),
                                "Invert search tolerance incrementation direction?"),
                        invertFontSizeLabel = TextLabel.make(
                                textBelowPos(invertToleranceLabel),
                                "Invert font size incrementation direction?");

                final Checkbox invertZoomCheckbox = new Checkbox(
                        contentPositionAfterLabel(invertZoomLabel),
                        new ConcreteProperty<>(
                                Settings::checkIsInvertZoomDirection,
                                Settings::setInvertZoomDirection)),
                        invertBreadthCheckbox = new Checkbox(
                                contentPositionAfterLabel(invertBreadthLabel),
                                new ConcreteProperty<>(
                                        Settings::checkIsInvertBreadthDirection,
                                        Settings::setInvertBreadthDirection)),
                        invertToleranceCheckbox = new Checkbox(
                                contentPositionAfterLabel(invertToleranceLabel),
                                new ConcreteProperty<>(
                                        Settings::checkIsInvertToleranceDirection,
                                        Settings::setInvertToleranceDirection)),
                        invertFontSizeCheckbox = new Checkbox(
                                contentPositionAfterLabel(invertFontSizeLabel),
                                new ConcreteProperty<>(
                                        Settings::checkIsInvertFontSizeDirection,
                                        Settings::setInvertFontSizeDirection));

                mb.addAll(scrollDirectionLabel,
                        invertZoomLabel, invertZoomCheckbox,
                        invertBreadthLabel, invertBreadthCheckbox,
                        invertToleranceLabel, invertToleranceCheckbox,
                        invertFontSizeLabel, invertFontSizeCheckbox);

                yield invertFontSizeLabel;
            }
            case TOOLS -> {
                // text labels
                final TextLabel propagateBreadthLabel =
                        makeDialogLeftLabel(initialYIndex,
                        "Propagate breadth tool property changes to all tools?");

                final Checkbox propagateBreadthCheckbox = new Checkbox(
                        contentPositionAfterLabel(propagateBreadthLabel),
                        new ConcreteProperty<>(
                                Settings::checkIsPropagateBreadth,
                                Settings::setPropagateBreadth));

                mb.addAll(propagateBreadthLabel, propagateBreadthCheckbox);

                yield propagateBreadthLabel;
            }
            case VISUAL -> {
                // text labels
                final TextLabel fontLabel = makeDialogLeftLabel(
                        initialYIndex, "Program font"),
                        themeLabel = makeDialogRightLabel(fontLabel,
                                "Theme"),
                        windowedSizeLabel = TextLabel.make(
                                textBelowPos(fontLabel, 1),
                                "Windowed program size"),
                        windowedWidthLabel = TextLabel.make(
                                textBelowPos(windowedSizeLabel),
                                "Window width"),
                        windowedHeightLabel = makeDialogRightLabel(
                                windowedWidthLabel, "Window height"),
                        windowedContext = TextLabel.make(
                                textBelowPos(windowedWidthLabel),
                                "On this device, the windowed program can range from "),
                        windowedContext2 = TextLabel.make(
                                textBelowPos(windowedContext),
                                MIN_WINDOW_W + "x" + MIN_WINDOW_H + " to " +
                                        MAX_WINDOW_W + "x" + MAX_WINDOW_H + " pixels."),
                        checkerboardLabel = TextLabel.make(
                                textBelowPos(windowedContext2, 1),
                                "Checkerboard size"),
                        checkerboardWidthLabel = TextLabel.make(
                                textBelowPos(checkerboardLabel),
                                "Cell width"),
                        checkerboardHeightLabel = makeDialogRightLabel(
                                checkerboardWidthLabel, "Cell height"),
                        checkerboardContext = TextLabel.make(
                                textBelowPos(checkerboardWidthLabel),
                                "Valid checkerboard size values range from " +
                                        CHECKERBOARD_MIN + " to " +
                                        CHECKERBOARD_MAX + " pixels."),
                        pixelGridLabel = TextLabel.make(
                                textBelowPos(checkerboardContext, 1),
                                "Pixel grid"),
                        pixelGridXLabel = TextLabel.make(
                                textBelowPos(pixelGridLabel),
                                "X-axis increment"),
                        pixelGridYLabel = makeDialogRightLabel(
                                pixelGridXLabel, "Y-axis increment"),
                        pixelGridContext = TextLabel.make(
                                textBelowPos(pixelGridXLabel),
                                "Valid pixel grid size values range from " +
                                        PIXEL_GRID_MIN + " to " +
                                        PIXEL_GRID_MAX + " pixels."),
                        pixelGridLimits1 = TextLabel.make(
                                textBelowPos(pixelGridContext),
                                "There can be up to " + MAX_PIXEL_GRID_LINES +
                                        " pixel grid lines (X + Y) on"),
                        pixelGridLimits2 = TextLabel.make(
                                textBelowPos(pixelGridLimits1),
                                "the canvas due to performance constraints."),
                        separatedPreview = TextLabel.make(
                                textBelowPos(pixelGridLimits2, 1),
                                "Open preview panel in a separate window?");

                final Dropdown fontDropdown = Dropdown.forDialog(
                        contentPositionAfterLabel(fontLabel),
                        EnumUtils.stream(SEFonts.Code.class)
                                .map(SEFonts.Code::forButtonText)
                                .toArray(String[]::new),
                        EnumUtils.stream(SEFonts.Code.class)
                                .map(code -> (Runnable) () -> Settings
                                        .setProgramFont(code))
                                .toArray(Runnable[]::new),
                        () -> Settings.checkProgramFont().ordinal()),
                        themeDropdown = Dropdown.forDialog(
                                contentPositionAfterLabel(themeLabel),
                                EnumUtils.stream(Themes.class)
                                        .map(Themes::forButtonText)
                                        .toArray(String[]::new),
                                EnumUtils.stream(Themes.class)
                                        .map(theme -> (Runnable) () -> Settings
                                                .setTheme(theme))
                                        .toArray(Runnable[]::new),
                                () -> Settings.checkTheme().ordinal());

                // textboxes
                final DynamicTextbox windowedWidthTextbox =
                        makeDialogPixelDynamicTextbox(windowedWidthLabel,
                                Layout::contentPositionAfterLabel,
                                MIN_WINDOW_W, MAX_WINDOW_W,
                                Settings::setWindowedWidth,
                                Settings::checkWindowedWidth, 4);
                final DynamicTextbox windowedHeightTextbox =
                        makeDialogPixelDynamicTextbox(windowedHeightLabel,
                                Layout::contentPositionAfterLabel,
                                MIN_WINDOW_H, MAX_WINDOW_H,
                                Settings::setWindowedHeight,
                                Settings::checkWindowedHeight, 4);
                final DynamicTextbox checkerboardXTextbox =
                        makeDialogPixelDynamicTextbox(checkerboardWidthLabel,
                                Layout::contentPositionAfterLabel,
                                CHECKERBOARD_MIN, CHECKERBOARD_MAX,
                                Settings::setCheckerboardWPixels,
                                Settings::checkCheckerboardWPixels, 3);
                final DynamicTextbox checkerboardYTextbox =
                        makeDialogPixelDynamicTextbox(checkerboardHeightLabel,
                                Layout::contentPositionAfterLabel,
                                CHECKERBOARD_MIN, CHECKERBOARD_MAX,
                                Settings::setCheckerboardHPixels,
                                Settings::checkCheckerboardHPixels, 3);
                final DynamicTextbox pixelGridXTextbox =
                        makeDialogPixelDynamicTextbox(pixelGridXLabel,
                                Layout::contentPositionAfterLabel,
                                PIXEL_GRID_MIN, PIXEL_GRID_MAX,
                                Settings::setPixelGridXPixels,
                                Settings::checkPixelGridXPixels, 3);
                final DynamicTextbox pixelGridYTextbox =
                        makeDialogPixelDynamicTextbox(pixelGridYLabel,
                                Layout::contentPositionAfterLabel,
                                PIXEL_GRID_MIN, PIXEL_GRID_MAX,
                                Settings::setPixelGridYPixels,
                                Settings::checkPixelGridYPixels, 3);

                final Checkbox separatedPreviewCheckbox = new Checkbox(
                        contentPositionAfterLabel(separatedPreview),
                        new ConcreteProperty<>(
                                Settings::checkIsSeparatedPreview,
                                Settings::setSeparatedPreview));

                mb.addAll(fontLabel, fontDropdown, themeLabel, themeDropdown,
                        windowedSizeLabel,
                        windowedWidthLabel, windowedWidthTextbox,
                        windowedHeightLabel, windowedHeightTextbox,
                        windowedContext, windowedContext2,
                        checkerboardLabel,
                        checkerboardWidthLabel, checkerboardXTextbox,
                        checkerboardHeightLabel, checkerboardYTextbox,
                        checkerboardContext, pixelGridLabel,
                        pixelGridXLabel, pixelGridXTextbox,
                        pixelGridYLabel, pixelGridYTextbox,
                        pixelGridContext, pixelGridLimits1, pixelGridLimits2,
                        separatedPreview, separatedPreviewCheckbox);

                // update as new settings are added to category
                yield separatedPreview;
            }
        };

        // scrolling container
        final int scrollerEndY = (getCanvasMiddle().y +
                getDialogHeight() / 2) - ((2 * CONTENT_BUFFER_PX) +
                STD_TEXT_BUTTON_H);

        final Coord2D scrollerPos = getDialogPosition().displace(0,
                (4 * STD_TEXT_BUTTON_INC) +
                        TEXT_Y_OFFSET - BUTTON_DIM);
        final Bounds2D scrollerDims = new Bounds2D(getDialogWidth(),
                        scrollerEndY - scrollerPos.y);

        final int realBottomY = bottomLabel.getRenderPosition().y +
                bottomLabel.getHeight() + STD_TEXT_BUTTON_H;

        return new VerticalScrollBox(scrollerPos, scrollerDims,
                Arrays.stream(mb.build().getMenuElements())
                        .map(Scrollable::new).toArray(Scrollable[]::new),
                realBottomY, 0);
    }

    private static VerticalScrollBox assembleScroller(
            final DialogVals.InfoScreen infoScreen, final int scrollerEndY
    ) {
        final int dialogW = (int)(width() * 0.7);

        final Coord2D contentStart = new Coord2D(getCanvasMiddle().x -
                (dialogW / 2) + CONTENT_BUFFER_PX + BUTTON_BORDER_PX,
                4 * STD_TEXT_BUTTON_INC);
        final Set<MenuElement> contentAssembler = new HashSet<>();

        int initialbottomY = 0;

        final double titleSize = 2d;
        final TextLabel headingLabel = TextLabel.make(contentStart.displace(
                0, initialbottomY), infoScreen.getTitle(),
                Settings.getTheme().textMenuHeading, titleSize);
        initialbottomY += (int)(DIALOG_CONTENT_INC_Y * titleSize) + BUTTON_INC;

        contentAssembler.add(headingLabel);

        final int deltaBottomY = switch (infoScreen) {
            case ABOUT -> assembleAboutInfoScreen(contentAssembler,
                    contentStart, initialbottomY);
            case PROJECT -> assembleProjectInfoScreenContents(
                    contentAssembler, contentStart, initialbottomY);
            case FRAMES -> assembleFramesInfoScreen(contentAssembler,
                    contentStart, initialbottomY);
            case COLORS -> assembleColorsInfoScreen(contentAssembler,
                    contentStart, initialbottomY);
            case LAYERS -> assembleLayersInfoScreen(contentAssembler,
                    contentStart, initialbottomY);
            case TOOLS -> assembleToolsInfoScreenContents(contentAssembler,
                    contentStart, initialbottomY);
            case MORE -> assembleInfoScreenContents(
                    new String[] {
                            ResourceCodes.HORIZONTAL_REFLECTION,
                            ResourceCodes.VERTICAL_REFLECTION,
                            ResourceCodes.OUTLINE,
                            ResourceCodes.PIXEL_GRID_ON,
                            ResourceCodes.GENERAL,
                            ResourceCodes.CLIPBOARD_SHORTCUTS,
                            ResourceCodes.SELECTION_SHORTCUTS,
                            ResourceCodes.COLOR_SHORTCUTS
                    },
                    new String[] {
                            "Horizontal reflection",
                            "Vertical reflection",
                            "Outline",
                            "Pixel grid",
                            "General",
                            "Clipboard shortcuts",
                            "Selection shortcuts",
                            "Advanced color shortcuts"
                    },
                    contentAssembler, contentStart, initialbottomY);
            case SCRIPTS -> assembleScriptingInfoScreen(contentAssembler,
                    contentStart, initialbottomY);
            case CHANGES -> assembleInfoScreenContents(
                    new String[] { ResourceCodes.CHANGELOG },
                    new String[] { "" },
                    contentAssembler, contentStart, initialbottomY);
            case ROADMAP -> assembleInfoScreenContents(
                    new String[] { ResourceCodes.ROADMAP },
                    new String[] { "" },
                    contentAssembler, contentStart, initialbottomY);
        };

        final Scrollable[] scrollingElements =
                contentAssembler.stream()
                        .map(Scrollable::new).toArray(Scrollable[]::new);

        final Bounds2D wrapperDims = new Bounds2D(
                dialogW - (2 * BUTTON_BORDER_PX),
                scrollerEndY - (contentStart.y + TEXT_Y_OFFSET));

        // assemble contents into scrolling element
        return new VerticalScrollBox(contentStart.displace(
                -CONTENT_BUFFER_PX, TEXT_Y_OFFSET), wrapperDims,
                scrollingElements, initialbottomY + deltaBottomY +
                contentStart.y, 0);
    }

    private static int assembleInfoScreenContents(
            final String[] iconAndBlurbCodes, final String[] headings,
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        final Theme t = Settings.getTheme();

        if (iconAndBlurbCodes.length != headings.length) {
            GameError.send("Length of file codes and headings arrays did not match...");
            return 0;
        }

        final int indent = (2 * BUTTON_INC),
                incY = DIALOG_CONTENT_INC_Y;

        int bottomY = initialBottomY;

        for (int i = 0; i < iconAndBlurbCodes.length; i++) {
            final String code = iconAndBlurbCodes[i];
            final boolean hasIcon = ResourceCodes.hasIcon(code);

            if (hasIcon) {
                final StaticMenuElement icon = new StaticMenuElement(
                        contentStart.displace(0, bottomY),
                        MenuElement.Anchor.LEFT_TOP, GraphicsUtils.loadIcon(code));
                contentAssembler.add(icon);
            }

            final TextLabel name = TextLabel.make(contentStart.displace(
                            (hasIcon ? BUTTON_INC : 0) + CONTENT_BUFFER_PX,
                            bottomY + TEXT_Y_OFFSET - BUTTON_BORDER_PX),
                    headings[i], hasIcon ? t.textShortcut
                            : ThemeLogic.intuitTextColor(t.panelBackground, false));
            contentAssembler.add(name);

            bottomY += incY;

            final String[] blurb = ParserUtils.getBlurb(code);

            final Color defC = ThemeLogic.intuitTextColor(
                    t.panelBackground, true);

            for (String line : blurb) {
                final String[] lineSegments = ParserUtils.extractHighlight(line);

                int offsetX = 0;

                for (int j = 0; j < lineSegments.length; j++) {
                    final String text;
                    final Color c;

                    if (j % 2 == 0) {
                        text = lineSegments[j];
                        c = defC;
                    } else {
                        text = ParserUtils.getShortcut(lineSegments[j]);
                        c = t.textShortcut;
                    }

                    final TextLabel segmentText = TextLabel.make(
                            contentStart.displace(indent + offsetX,
                                    bottomY + TEXT_Y_OFFSET),
                            text, c);

                    contentAssembler.add(segmentText);
                    offsetX += segmentText.getWidth() + BUTTON_BORDER_PX;
                }

                bottomY += incY;
            }

            bottomY += incY;
        }

        return bottomY - initialBottomY;
    }

    private static int assembleAboutInfoScreen(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        final int indent = (2 * BUTTON_INC);

        int bottomY = initialBottomY + assembleInfoScreenContents(
                new String[] { ResourceCodes.ABOUT }, new String[] { "" },
                contentAssembler, contentStart, initialBottomY);

        final TextLabel storePageLabel = TextLabel.make(
                contentStart.displace(indent, bottomY + TEXT_Y_OFFSET),
                "Buy the program to access all of its features and save your work:"),
                sourceLabel = TextLabel.make(textBelowPos(storePageLabel),
                "Check out the source code:");
        final StaticTextButton storePageButton =
                GraphicsUtils.makeStandardTextButton("Go",
                        contentPositionAfterLabel(storePageLabel),
                        WebUtils::buyPage),
                sourceButton = GraphicsUtils.makeStandardTextButton("Go",
                        contentPositionAfterLabel(sourceLabel),
                        WebUtils::sourceRepo);
        contentAssembler.addAll(Set.of(storePageLabel, sourceLabel,
                storePageButton, sourceButton));

        bottomY += DIALOG_CONTENT_INC_Y * 3;

        return bottomY - initialBottomY;
    }

    private static int assembleScriptingInfoScreen(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        final int indent = (2 * BUTTON_INC);

        int bottomY = initialBottomY + assembleInfoScreenContents(
                new String[] { ResourceCodes.SCRIPTING }, new String[] { "" },
                contentAssembler, contentStart, initialBottomY);

        final TextLabel scriptLabel = TextLabel.make(
                contentStart.displace(indent, bottomY + TEXT_Y_OFFSET),
                "Read the scripting API specification: "),
                vsCodeLabel = TextLabel.make(
                        textBelowPos(scriptLabel),
                        "VS Code extension for Stipple Effect script syntax highlighting: ");
        final StaticTextButton scriptButton =
                GraphicsUtils.makeStandardTextButton("Go",
                        contentPositionAfterLabel(scriptLabel),
                        WebUtils::apiSpec),
                vsCodeButton = GraphicsUtils.makeStandardTextButton("Go",
                        contentPositionAfterLabel(vsCodeLabel),
                        WebUtils::vsCodeExt);
        contentAssembler.addAll(
                Set.of(scriptLabel, scriptButton, vsCodeLabel, vsCodeButton));

        bottomY += DIALOG_CONTENT_INC_Y * 3;

        return bottomY - initialBottomY;
    }

    private static int assembleColorsInfoScreen(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        return assembleInfoScreenContents(
                new String[] {
                        ResourceCodes.SWAP_COLORS,
                        ResourceCodes.HSV_SHIFT,
                        ResourceCodes.COLOR_SCRIPT,
                        ResourceCodes.NEW_PALETTE,
                        ResourceCodes.IMPORT_PALETTE,
                        ResourceCodes.CONTENTS_TO_PALETTE,
                        ResourceCodes.DELETE_PALETTE,
                        ResourceCodes.SAVE_PALETTE,
                        ResourceCodes.SORT_PALETTE,
                        ResourceCodes.PALETTIZE,
                        ResourceCodes.PALETTE_SETTINGS,
                        ResourceCodes.ADD_TO_PALETTE
                },
                new String[] {
                        "Swap primary and secondary color",
                        "Adjust HSV color levels",
                        "Run a color script",
                        "Create a new palette",
                        "Import a " + StippleEffect.PROGRAM_NAME + " palette file (." +
                                Constants.PALETTE_FILE_SUFFIX + ")",
                        "Add colors from project contents to palette",
                        "Delete the selected palette",
                        "Save palette to file",
                        "Sort colors in palette",
                        "Palettize project contents",
                        "Open the settings dialog for the selected palette",
                        "Add selected color to palette"
                }, contentAssembler, contentStart, initialBottomY
        );
    }

    private static int assembleProjectInfoScreenContents(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        return assembleInfoScreenContents(
                new String[] {
                        ResourceCodes.INFO,
                        ResourceCodes.PANEL_MANAGER,
                        ResourceCodes.SETTINGS,
                        ResourceCodes.NEW_PROJECT,
                        ResourceCodes.OPEN_FILE,
                        ResourceCodes.RESIZE,
                        ResourceCodes.PAD,
                        ResourceCodes.STITCH_SPLIT_FRAMES,
                        ResourceCodes.PREVIEW,
                        ResourceCodes.AUTOMATION_SCRIPT,
                        ResourceCodes.UNDO,
                        ResourceCodes.GRANULAR_UNDO,
                        ResourceCodes.GRANULAR_REDO,
                        ResourceCodes.REDO,
                        ResourceCodes.HISTORY
                },
                new String[] {
                        "Info", "Open panel manager", "Program Settings",
                        "New Project", "Import",
                        "Resize", "Pad", "Stitch or split frames", "Preview",
                        "Automation script",
                        "Undo", "Granular Undo", "Granular Redo", "Redo",
                        "History"
                }, contentAssembler, contentStart, initialBottomY
        );
    }

    private static int assembleLayersInfoScreen(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        return assembleInfoScreenContents(
                new String[] {
                        ResourceCodes.NEW_LAYER,
                        ResourceCodes.DUPLICATE_LAYER,
                        ResourceCodes.REMOVE_LAYER,
                        ResourceCodes.MOVE_LAYER_UP,
                        ResourceCodes.MOVE_LAYER_DOWN,
                        ResourceCodes.MERGE_WITH_LAYER_BELOW,
                        ResourceCodes.FLATTEN,
                        ResourceCodes.LAYER_VISIBILITY,
                        ResourceCodes.LAYER_ENABLED,
                        ResourceCodes.LAYER_DISABLED,
                        ResourceCodes.ONION_SKIN,
                        ResourceCodes.FRAME_LOCKING,
                        ResourceCodes.FRAMES_LINKED,
                        ResourceCodes.FRAMES_UNLINKED,
                        ResourceCodes.LAYER_SETTINGS
                },
                new String[] {
                        "New layer",
                        "Duplicate layer",
                        "Remove layer",
                        "Move layer up",
                        "Move layer down",
                        "Merge with layer below",
                        "Flatten project",
                        "Layer visibility controls",
                        "Layer is visible/enabled",
                        "Layer is invisible/disabled",
                        "Onion skin",
                        "Cel linking",
                        "This layer's cels are linked",
                        "This layer's cels are not linked",
                        "Layer settings"
                }, contentAssembler, contentStart, initialBottomY
        );
    }

    private static int assembleFramesInfoScreen(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        return assembleInfoScreenContents(
                new String[] {
                        ResourceCodes.NEW_FRAME,
                        ResourceCodes.DUPLICATE_FRAME,
                        ResourceCodes.REMOVE_FRAME,
                        ResourceCodes.MOVE_FRAME_FORWARD,
                        ResourceCodes.MOVE_FRAME_BACK,
                        ResourceCodes.FRAME_PROPERTIES,
                        ResourceCodes.TO_FIRST_FRAME,
                        ResourceCodes.PREVIOUS,
                        ResourceCodes.NEXT,
                        ResourceCodes.TO_LAST_FRAME,
                        ResourceCodes.PLAY,
                        ResourceCodes.PLAYBACK_MODES,
                        ResourceCodes.FORWARDS,
                        ResourceCodes.BACKWARDS,
                        ResourceCodes.LOOP,
                        ResourceCodes.PONG
                },
                new String[] {
                        "New frame",
                        "Duplicate frame",
                        "Remove frame",
                        "Move frame forward",
                        "Move frame back",
                        "Frame properties",
                        "Navigate to first frame",
                        "To previous frame",
                        "To next frame",
                        "Navigate to last frame",
                        "Play animation / Stop animation playback",
                        "Playback modes",
                        "Forwards",
                        "Backwards",
                        "Looping",
                        "Oscillating"
                }, contentAssembler, contentStart, initialBottomY
        );
    }

    private static int assembleToolsInfoScreenContents(
            final Set<MenuElement> contentAssembler, final Coord2D contentStart,
            final int initialBottomY
    ) {
        final Tool[] all = Tool.getAll();
        return assembleInfoScreenContents(
                Arrays.stream(all).map(Tool::convertNameToFilename)
                        .toArray(String[]::new),
                Arrays.stream(all).map(Tool::getName).toArray(String[]::new),
                contentAssembler, contentStart, initialBottomY);
    }

    private static Menu assembleInfoDialog() {
        final int dialogW = (int)(width() * 0.7);

        final MenuBuilder mb = new MenuBuilder();

        // background
        final GameImage backgroundImage = new GameImage(dialogW,
                height() - (2 * BUTTON_DIM));
        backgroundImage.fillRectangle(Settings.getTheme().panelBackground,
                0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
        backgroundImage.drawRectangle(Settings.getTheme().buttonOutline,
                2f * BUTTON_BORDER_PX, 0, 0,
                backgroundImage.getWidth(), backgroundImage.getHeight());

        final StaticMenuElement background =
                new StaticMenuElement(getCanvasMiddle(),
                        MenuElement.Anchor.CENTRAL, backgroundImage.submit());
        mb.add(background);

        // title
        mb.add(TextLabel.make(background.getRenderPosition().displace(
                        CONTENT_BUFFER_PX + BUTTON_BORDER_PX,
                        TEXT_Y_OFFSET + BUTTON_BORDER_PX),
                StippleEffect.PROGRAM_NAME + " [Demo] " + StippleEffect.getVersion() +
                        "  |  Help & Information"));

        // close button
        final Coord2D cancelPos = background.getRenderPosition()
                .displace(background.getWidth(), background.getHeight())
                .displace(-(CONTENT_BUFFER_PX + STD_TEXT_BUTTON_W),
                        -(CONTENT_BUFFER_PX + STD_TEXT_BUTTON_H));

        mb.add(GraphicsUtils.makeStandardTextButton("Close",
                cancelPos, StippleEffect.get()::clearDialog));

        // contents
        Arrays.stream(DialogVals.InfoScreen.values()).forEach(is -> {
                    final Coord2D isPos = background.getRenderPosition().displace(
                            CONTENT_BUFFER_PX + (is.ordinal() *
                                    (STD_TEXT_BUTTON_W + BUTTON_OFFSET)),
                            CONTENT_BUFFER_PX +
                                            (int)(1.5 * STD_TEXT_BUTTON_INC));

                    mb.add(SelectableListItemButton.make(
                            isPos, STD_TEXT_BUTTON_W,
                            is.toString(), is.ordinal(),
                            () -> DialogVals.getInfoScreen().ordinal(),
                            i -> DialogVals.setInfoScreen(
                                    DialogVals.InfoScreen.values()[i])));
                });

        final int scrollerEndY = (background.getRenderPosition().y +
                background.getHeight()) - ((2 * CONTENT_BUFFER_PX) +
                STD_TEXT_BUTTON_H);

        final Map<DialogVals.InfoScreen, VerticalScrollBox>
                infoScreens = new HashMap<>();

        for (DialogVals.InfoScreen infoScreen : DialogVals.InfoScreen.values()) {
            infoScreens.put(infoScreen, assembleScroller(infoScreen, scrollerEndY));
        }

        final ThinkingMenuElement screenDecider = new ThinkingMenuElement(
                () -> infoScreens.get(DialogVals.getInfoScreen()));

        mb.add(screenDecider);
        return mb.build();
    }

    private static Menu assembleDialog(
            final String title, final MenuElementGrouping contents,
            final Supplier<Boolean> precondition, final String approveText,
            final Runnable onApproval, final boolean clearDialog
    ) {
        final MenuBuilder mb = new MenuBuilder();

        // background
        final GameImage backgroundImage = new GameImage(
                getDialogWidth(), getDialogHeight());
        backgroundImage.fillRectangle(Settings.getTheme().panelBackground,
                0, 0, getDialogWidth(), getDialogHeight());

        final StaticMenuElement background =
                new StaticMenuElement(getCanvasMiddle(), new Bounds2D(
                        getDialogWidth(), getDialogHeight()),
                        MenuElement.Anchor.CENTRAL, backgroundImage.submit());
        mb.add(background);

        // title
        mb.add(TextLabel.make(background.getRenderPosition().displace(
                        CONTENT_BUFFER_PX + BUTTON_BORDER_PX,
                        TEXT_Y_OFFSET + BUTTON_BORDER_PX),
                title));

        // cancel button
        final Coord2D cancelPos = background.getRenderPosition()
                .displace(background.getWidth(), background.getHeight())
                .displace(-(CONTENT_BUFFER_PX + STD_TEXT_BUTTON_W),
                        -(CONTENT_BUFFER_PX + STD_TEXT_BUTTON_H));

        mb.add(GraphicsUtils.makeStandardTextButton(
                approveText.equals(
                        Constants.CLOSE_DIALOG_TEXT)
                        ? Constants.CLOSE_DIALOG_TEXT : "Cancel",
                cancelPos, StippleEffect.get()::clearDialog));

        // approve button
        if (!approveText.equals(Constants.CLOSE_DIALOG_TEXT)) {
            final Coord2D approvePos = cancelPos.displace(
                    -(STD_TEXT_BUTTON_W +
                    BUTTON_OFFSET), 0);

            mb.add(new ApproveDialogButton(approvePos,
                    onApproval, clearDialog, precondition, approveText));
        }

        // contents come before border to ensure proper rendering
        mb.add(contents);

        // border
        final GameImage borderImage = new GameImage(
                getDialogWidth(), getDialogHeight());
        borderImage.drawRectangle(Settings.getTheme().buttonOutline,
                2f * BUTTON_BORDER_PX, 0, 0,
                getDialogWidth(), getDialogHeight());

        final StaticMenuElement border =
                new StaticMenuElement(getCanvasMiddle(), new Bounds2D(
                        getDialogWidth(), getDialogHeight()),
                        MenuElement.Anchor.CENTRAL, borderImage.submit());
        mb.add(border);

        return mb.build();
    }
}
