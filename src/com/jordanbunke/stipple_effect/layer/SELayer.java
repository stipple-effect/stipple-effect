package com.jordanbunke.stipple_effect.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.stip_parser.rep.IRLayer;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.selection.Selection;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.math.StitchSplitMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public final class SELayer {
    private final List<GameImage> cels, renders, backOnionSkins, forwardOnionSkins;
    private final GameImage linkedContent;
    private final double opacity;
    private final boolean enabled, celsLinked;
    private final String name;

    private final OnionSkin onionSkin;
    private boolean onionSkinOn;

    public static SELayer fromPreviewContent(
            final GameImage[] content
    ) {
        final List<GameImage> cels = new ArrayList<>(Arrays.asList(content));

        return new SELayer(cels, content[0], Constants.OPAQUE,
                true, false, false, OnionSkin.trivial(),
                Constants.FROM_PREVIEW_LAYER_NAME);
    }

    public static SELayer fromPreviewContent(
            final GameImage[] content, final SELayer source
    ) {
        final List<GameImage> cels = new ArrayList<>(Arrays.asList(content));

        return new SELayer(cels, content[0], source.opacity, source.enabled,
                content.length == 1 && source.celsLinked,
                source.onionSkinOn, source.onionSkin, source.name);
    }

    public static SELayer newLayer(
            final int w, final int h, final int frameCount
    ) {
        final List<GameImage> cels = new ArrayList<>();

        for (int f = 0; f < frameCount; f++) {
            cels.add(new GameImage(w, h));
        }

        return new SELayer(cels, new GameImage(w, h), Constants.OPAQUE,
                true, false, false, OnionSkin.trivial(), giveLayerDefaultName());
    }

    public SELayer(final int imageWidth, final int imageHeight) {
        this(new ArrayList<>(List.of(new GameImage(imageWidth, imageHeight))),
                new GameImage(imageWidth, imageHeight), Constants.OPAQUE,
                true, false, false, OnionSkin.trivial(),
                Constants.BASE_LAYER_NAME);
    }

    public SELayer(
            final List<GameImage> cels, final GameImage linkedContent,
            final double opacity,
            final boolean enabled, final boolean celsLinked,
            final boolean onionSkinOn, final OnionSkin onionSkin, final String name
    ) {
        this.cels = cels;
        this.linkedContent = linkedContent;
        this.opacity = opacity;
        this.enabled = enabled;
        this.celsLinked = celsLinked;
        this.name = name;

        this.onionSkin = onionSkin;
        this.onionSkinOn = onionSkinOn;

        renders = new ArrayList<>();
        backOnionSkins = new ArrayList<>();
        forwardOnionSkins = new ArrayList<>();

        generateRenders();
        generateOnionSkins();
    }

    public static SELayer realize(final IRLayer rep) {
        return new SELayer(Arrays.stream(rep.cels()).toList(),
                rep.linkedContent(), rep.opacity(), rep.enabled(),
                rep.celsLinked(), rep.onionSkinOn(),
                OnionSkin.realize(rep.onionSkin()), rep.name());
    }

    public IRLayer convert() {
        return new IRLayer(name, cels.toArray(GameImage[]::new),
                linkedContent, opacity, enabled, celsLinked,
                onionSkinOn, onionSkin.convert());
    }

    private void generateRenders() {
        if (celsLinked && opacity == Constants.OPAQUE)
            renders.add(cels.get(0));
        else if (celsLinked)
            renders.add(renderCel(0));
        else if (opacity == Constants.OPAQUE)
            renders.addAll(cels);
        else {
            for (int i = 0; i < cels.size(); i++)
                renders.add(renderCel(i));
        }
    }

    private void generateOnionSkins() {
        if (celsLinked || !onionSkinOn)
            return;

        final int fc = cels.size();

        final GameImage[] backBases = new GameImage[fc],
                forwardBases = new GameImage[fc];

        for (int i = 0; i < fc; i++) {
            final GameImage cel = getCel(i);

            backBases[i] = onionSkin.drawBase(cel, true);
            forwardBases[i] = onionSkin.drawBase(cel, false);
        }

        onionSkin.populateOnionSkins(fc,
                backOnionSkins, forwardOnionSkins,
                backBases, forwardBases);
    }

    private static String giveLayerDefaultName() {
        final int size = StippleEffect.get().getContext().getState().getLayers().size();
        return Constants.STD_LAYER_PREFIX + (size + 1);
    }

    public SELayer duplicate() {
        return new SELayer(new ArrayList<>(cels), linkedContent, opacity,
                enabled, celsLinked, onionSkinOn, onionSkin, name + " (copy)");
    }

    public SELayer returnCelReplaced(final GameImage edit, final int frameIndex) {
        final List<GameImage> cels = new ArrayList<>(this.cels);
        cels.set(frameIndex, edit);

        return new SELayer(cels, edit, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnStamped(
            final GameImage edit, final Selection selection, final int frameIndex
    ) {
        final List<GameImage> cels = new ArrayList<>(this.cels);
        final GameImage content = getCel(frameIndex);

        final GameImage composed = new GameImage(content);
        final int w = composed.getWidth(), h = composed.getHeight();

        selection.pixelAlgorithm(w, h, (x, y) -> {
            final Color c = edit.getColorAt(x, y);
            composed.setRGB(x, y, c.getRGB());
        });

        composed.free();
        cels.set(frameIndex, composed);

        return new SELayer(cels, composed, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnPaintedOver(final GameImage edit, final int frameIndex) {
        final List<GameImage> cels = new ArrayList<>(this.cels);
        final GameImage content = getCel(frameIndex);

        final GameImage composed = new GameImage(content);
        composed.draw(edit);
        composed.free();
        cels.set(frameIndex, composed);

        return new SELayer(cels, composed, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnErased(final boolean[][] eraserMask, final int frameIndex) {
        final List<GameImage> cels = new ArrayList<>(this.cels);
        final GameImage content = getCel(frameIndex);

        final GameImage after = new GameImage(content);

        for (int x = 0; x < after.getWidth(); x++) {
            for (int y = 0; y < after.getHeight(); y++) {
                if (eraserMask[x][y])
                    after.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
            }
        }

        after.free();
        cels.set(frameIndex, after);

        return new SELayer(cels, after, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnResized(final int w, final int h) {
        final List<GameImage> resizedCels = new ArrayList<>();

        for (int i = 0; i < cels.size(); i++)
            resizedCels.add(ImageProcessing.scale(
                    getCel(i), w, h));

        final GameImage resizedFLC = ImageProcessing.scale(
                linkedContent, w, h);

        return new SELayer(resizedCels, resizedFLC, opacity,
                enabled, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnPadded(
            final int left, final int top, final int w, final int h
    ) {
        final List<GameImage> paddedCels = new ArrayList<>();

        for (int i = 0; i < cels.size(); i++) {
            final GameImage cel = new GameImage(w, h);
            cel.draw(getCel(i), left, top);

            paddedCels.add(cel.submit());
        }

        final GameImage paddedFLC = new GameImage(w, h);
        paddedFLC.draw(linkedContent, left, top);

        return new SELayer(paddedCels, paddedFLC.submit(), opacity,
                enabled, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnStitched(
            final int fw, final int fh, final int fc
    ) {
        final int w = StitchSplitMath.stitchedWidth(fw, fc),
                h = StitchSplitMath.stitchedHeight(fh, fc),
                fpd = DialogVals.getFramesPerDim();

        final boolean isHorizontal = StitchSplitMath.isHorizontal();
        final BinaryOperator<Integer>
                horz = (a, b) -> a % b,
                vert = (a, b) -> a / b,
                xOp = isHorizontal ? horz : vert,
                yOp = isHorizontal ? vert : horz;

        final GameImage cel = new GameImage(w, h);

        for (int i = 0; i < fc; i++) {
            final int x = xOp.apply(i, fpd) * fw,
                    y = yOp.apply(i, fpd) * fh;

            cel.draw(getCel(i), x, y);
        }

        cel.free();

        return new SELayer(new ArrayList<>(List.of(cel)), cel,
                opacity, enabled, true, onionSkinOn, onionSkin, name);
    }

    public SELayer returnSplit(
            final int w, final int h, final int fc
    ) {
        final int fw = DialogVals.getFrameWidth(),
                fh = DialogVals.getFrameHeight(),
                fx = StitchSplitMath.splitColumns(w),
                fy = StitchSplitMath.splitRows(h);

        final boolean isHorizontal = StitchSplitMath.isHorizontal();
        final BinaryOperator<Integer>
                horz = (a, b) -> a % b,
                vert = (a, b) -> a / b,
                xOp = isHorizontal ? horz : vert,
                yOp = isHorizontal ? vert : horz;

        final int fpd = isHorizontal ? fx : fy;

        final List<GameImage> cels = new ArrayList<>();
        final GameImage canvas = getCel(0);

        for (int i = 0; i < fc; i++) {
            final GameImage cel = new GameImage(fw, fh);

            final int x = xOp.apply(i, fpd) * fw,
                    y = yOp.apply(i, fpd) * fh;

            cel.draw(canvas, -1 * x, -1 * y);
            cels.add(cel.submit());
        }

        return new SELayer(cels, cels.get(0),
                opacity, enabled, false, onionSkinOn, onionSkin, name);
    }

    public SELayer returnLinkedCels(final int frameIndex) {
        return new SELayer(new ArrayList<>(cels), getCel(frameIndex),
                opacity, enabled, true, false, onionSkin, name);
    }

    public SELayer returnUnlinkedCels() {
        final int frameCount = cels.size();

        final List<GameImage> clonedFromLinked = new ArrayList<>();

        for (int i = 0; i < frameCount; i++)
            clonedFromLinked.add(linkedContent);

        return new SELayer(clonedFromLinked, linkedContent,
                opacity, enabled, false, onionSkinOn, onionSkin, name);
    }

    public SELayer returnChangedOpacity(final double opacity) {
        return new SELayer(new ArrayList<>(cels), linkedContent,
                opacity, enabled, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnDisabled() {
        return new SELayer(new ArrayList<>(cels), linkedContent,
                opacity, false, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnEnabled() {
        return new SELayer(new ArrayList<>(cels), linkedContent,
                opacity, true, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnRenamed(final String name) {
        return new SELayer(new ArrayList<>(cels), linkedContent,
                opacity, enabled, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnChangedOnionSkin(final OnionSkin onionSkin) {
        return new SELayer(new ArrayList<>(cels), linkedContent,
                opacity, enabled, celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnFrameMoved(
            final int from, final int to
    ) {
        final List<GameImage> cels = new ArrayList<>(this.cels);

        final GameImage moved = cels.remove(from);
        cels.add(to, moved);

        return new SELayer(cels, linkedContent, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnFrameMovedBack(
            final int oldFrameIndex
    ) {
        return returnFrameMoved(oldFrameIndex, oldFrameIndex - 1);
    }

    public SELayer returnFrameMovedForward(
            final int oldFrameIndex
    ) {
        return returnFrameMoved(oldFrameIndex, oldFrameIndex + 1);
    }

    public SELayer returnAddedFrame(
            final int addIndex, final int w, final int h
    ) {
        final List<GameImage> cels = new ArrayList<>(this.cels);

        cels.add(addIndex, new GameImage(w, h));

        return new SELayer(cels, linkedContent, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnDuplicatedFrame(final int fromIndex) {
        final List<GameImage> cels = new ArrayList<>(this.cels);

        cels.add(fromIndex + 1, new GameImage(cels.get(fromIndex)));

        return new SELayer(cels, linkedContent, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public SELayer returnRemovedFrame(final int fromIndex) {
        final List<GameImage> cels = new ArrayList<>(this.cels);

        cels.remove(fromIndex);

        return new SELayer(cels, linkedContent, opacity, enabled,
                celsLinked, onionSkinOn, onionSkin, name);
    }

    public GameImage getCel(final int frameIndex) {
        return celsLinked ? linkedContent : cels.get(frameIndex);
    }

    public GameImage getRender(final int frameIndex) {
        if (opacity == Constants.OPAQUE)
            return getCel(frameIndex);

        return renders.get(celsLinked ? 0 : frameIndex);
    }

    public GameImage getOnionSkinCel(final int frameIndex, final boolean back) {
        final List<GameImage> onionSkins = back
                ? backOnionSkins : forwardOnionSkins;

        if (celsLinked || frameIndex >= onionSkins.size())
            return GameImage.dummy();

        return onionSkins.get(frameIndex);
    }

    private GameImage renderCel(final int frameIndex) {
        return renderCel(frameIndex, opacity);
    }

    private GameImage renderCel(final int frameIndex, final double opacity) {
        final GameImage cel = getCel(frameIndex);

        final GameImage render = new GameImage(cel.getWidth(), cel.getHeight());

        for (int x = 0; x < render.getWidth(); x++) {
            for (int y = 0; y < render.getHeight(); y++) {
                final Color c = cel.getColorAt(x, y);

                if (c.getAlpha() == 0)
                    continue;

                final Color cp = new Color(c.getRed(), c.getGreen(), c.getBlue(),
                        (int)(c.getAlpha() * opacity));
                render.setRGB(x, y, cp.getRGB());
            }
        }

        return render.submit();
    }

    public void toggleOnionSkin() {
        setOnionSkinOn(!onionSkinOn);
    }

    public void setOnionSkinOn(final boolean onionSkinOn) {
        this.onionSkinOn = onionSkinOn;

        if (onionSkinOn && backOnionSkins.isEmpty())
            generateOnionSkins();
    }

    public boolean isOnionSkinOn() {
        return onionSkinOn;
    }

    public OnionSkin getOnionSkin() {
        return onionSkin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean areCelsLinked() {
        return celsLinked;
    }

    public double getOpacity() {
        return opacity;
    }

    public String getName() {
        return name;
    }
}
