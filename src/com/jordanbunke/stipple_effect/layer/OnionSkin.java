package com.jordanbunke.stipple_effect.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stip_parser.rep.IROnionSkin;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.visual.theme.SEColors;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static com.jordanbunke.stipple_effect.utility.math.ColorMath.*;

public final class OnionSkin {
    public static final int DEF_LOOK = 1, MIN_LOOK = 0, MAX_LOOK = 6;
    public static final double RED = 0d, GREEN = 1 / 3d,
            DEF_FADE = 0.5, MIN_FADE = 0.1, MAX_FADE = 0.8;

    private static SkinType dSkinTypeBack, dSkinTypeForward;
    private static double dHueBack, dHueForward, dFadeFactorBack, dFadeFactorForward;
    private static int dLookBack, dLookForward;
    private static boolean dUnderBack, dUnderForward;

    public final SkinType skinTypeBack, skinTypeForward;
    public final double hueBack, hueForward, fadeFactorBack, fadeFactorForward;
    public final int lookBack, lookForward;
    public final boolean underBack, underForward;

    public enum SkinType {
        SIMPLE, OUTLINE, TINTED;

        public boolean hasHue() {
            return switch (this) {
                case OUTLINE, TINTED -> true;
                default -> false;
            };
        }
    }

    static {
        reset();
    }

    private OnionSkin() {
        skinTypeBack = dSkinTypeBack;
        skinTypeForward = dSkinTypeForward;

        hueBack = dHueBack;
        hueForward = dHueForward;

        fadeFactorBack = dFadeFactorBack;
        fadeFactorForward = dFadeFactorForward;

        lookBack = dLookBack;
        lookForward = dLookForward;

        underBack = dUnderBack;
        underForward = dUnderForward;
    }

    private OnionSkin(
            final SkinType skinTypeBack, final SkinType skinTypeForward,
            final double hueBack, final double hueForward,
            final double fadeFactorBack, final double fadeFactorForward,
            final int lookBack, final int lookForward,
            final boolean underBack, final boolean underForward
    ) {
        this.skinTypeBack = skinTypeBack;
        this.skinTypeForward = skinTypeForward;

        this.hueBack = hueBack;
        this.hueForward = hueForward;

        this.fadeFactorBack = fadeFactorBack;
        this.fadeFactorForward = fadeFactorForward;

        this.lookBack = lookBack;
        this.lookForward = lookForward;

        this.underBack = underBack;
        this.underForward = underForward;
    }

    public static void reset() {
        dSkinTypeBack = SkinType.SIMPLE;
        dSkinTypeForward = SkinType.SIMPLE;

        dHueBack = RED;
        dHueForward = GREEN;

        dFadeFactorBack = DEF_FADE;
        dFadeFactorForward = DEF_FADE;

        dLookBack = DEF_LOOK;
        dLookForward = DEF_LOOK;

        dUnderBack = true;
        dUnderForward = false;
    }

    public static OnionSkin trivial() {
        reset();
        return load();
    }

    public static OnionSkin load() {
        return new OnionSkin();
    }

    public void prep() {
        dSkinTypeBack = skinTypeBack;
        dSkinTypeForward = skinTypeForward;

        dHueBack = hueBack;
        dHueForward = hueForward;

        dFadeFactorBack = fadeFactorBack;
        dFadeFactorForward = fadeFactorForward;

        dLookBack = lookBack;
        dLookForward = lookForward;

        dUnderBack = underBack;
        dUnderForward = underForward;
    }

    static OnionSkin realize(final IROnionSkin rep) {
        return new OnionSkin(SkinType.valueOf(rep.skinTypeBack().name()),
                SkinType.valueOf(rep.skinTypeForward().name()),
                rep.hueBack(), rep.hueForward(), rep.fadeFactorBack(),
                rep.fadeFactorForward(), rep.lookBack(), rep.lookForward(),
                rep.underBack(), rep.underForward());
    }

    IROnionSkin convert() {
        return new IROnionSkin(
                IROnionSkin.SkinType.valueOf(skinTypeBack.name()),
                IROnionSkin.SkinType.valueOf(skinTypeForward.name()),
                hueBack, hueForward, fadeFactorBack, fadeFactorForward,
                lookBack, lookForward, underBack, underForward);
    }

    GameImage drawBase(final GameImage cel, final boolean back) {
        final double hue = back ? hueBack : hueForward;
        final SkinType skinType = back ? skinTypeBack : skinTypeForward;

        return switch (skinType) {
            case SIMPLE -> cel;
            case TINTED -> algo(c -> tint(c, hue), new HashMap<>(), cel, null);
            case OUTLINE -> outline(cel, hue);
        };
    }

    private static Color tint(final Color c, final double hue) {
        return fromHSV(hue, 1d, rgbToValue(c), c.getAlpha());
    }

    private static GameImage outline(final GameImage source, final double hue) {
        final int w = source.getWidth(), h = source.getHeight();
        final Color found = fromHSV(hue, 1d, 1d, Constants.RGBA_SCALE),
                notFound = SEColors.transparent();

        return ThemeLogic.contextualTransformation(source, (asset, x, y) -> {
            final Color orig = asset.getColorAt(x, y);
            final int NEIGHBOURS = 4;

            if (orig.getAlpha() == 0)
                return orig;
            else {
                boolean transparentNeighbour = false;

                for (int i = 0; i < NEIGHBOURS; i++) {
                    final int sx = x + switch (i) {
                        case 1 -> -1;
                        case 2 -> 1;
                        default -> 0;
                    }, sy = y + switch (i) {
                        case 0 -> -1;
                        case 3 -> 1;
                        default -> 0;
                    };

                    if (sx < 0 || sx >= w || sy < 0 || sy >= h)
                        continue;

                    if (asset.getColorAt(sx, sy).getAlpha() == 0) {
                        transparentNeighbour = true;
                        break;
                    }
                }

                return transparentNeighbour ? found : notFound;
            }
        });
    }

    void populateOnionSkins(final int fc,
            final List<GameImage> backHost, final List<GameImage> forwardHost,
            final GameImage[] backBases, final GameImage[] forwardBases
    ) {
        final GameImage ref = backBases[0];
        final int w = ref.getWidth(), h = ref.getHeight(),
                range = Math.max(lookBack, lookForward);

        for (int f = 0; f < fc; f++) {
            final GameImage backSkin = new GameImage(w, h),
                    forwardSkin = new GameImage(w, h);

            for (int prox = range; prox > 0; prox--) {
                final boolean doBack = f - prox >= 0 && prox <= lookBack,
                        doForward = f + prox < fc && prox <= lookForward;
                final int fBack = f - prox, fForward = f + prox;
                final double oBack = Math.pow(fadeFactorBack, prox),
                        oForward = Math.pow(fadeFactorForward, prox);

                if (doBack)
                    backSkin.draw(algo(c -> fade(c, oBack),
                            new HashMap<>(), backBases[fBack], null));

                if (doForward)
                    forwardSkin.draw(algo(c -> fade(c, oForward),
                            new HashMap<>(), forwardBases[fForward], null));
            }

            backHost.add(backSkin.submit());
            forwardHost.add(forwardSkin.submit());
        }
    }

    private static Color fade(final Color c, final double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(),
                (int) (c.getAlpha() * opacity));
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof OnionSkin os &&
                skinTypeBack == os.skinTypeBack &&
                skinTypeForward == os.skinTypeForward &&
                hueBack == os.hueBack &&
                hueForward == os.hueForward &&
                fadeFactorBack == os.fadeFactorBack &&
                fadeFactorForward == os.fadeFactorForward &&
                underBack == os.underBack &&
                underForward == os.underForward &&
                lookBack == os.lookBack &&
                lookForward == os.lookForward;
    }

    @Override
    public int hashCode() {
        return ((MAX_LOOK + 1) * lookBack) + lookForward;
    }

    public static SkinType getDSkinTypeBack() {
        return dSkinTypeBack;
    }

    public static void setDSkinTypeBack(final SkinType skinType) {
        dSkinTypeBack = skinType;
    }

    public static SkinType getdSkinTypeForward() {
        return dSkinTypeForward;
    }

    public static void setdSkinTypeForward(final SkinType skinType) {
        dSkinTypeForward = skinType;
    }

    public static double getDHueBack() {
        return dHueBack;
    }

    public static void setDHueBack(final double hue) {
        dHueBack = hue;
    }

    public static double getDHueForward() {
        return dHueForward;
    }

    public static void setDHueForward(final double hue) {
        dHueForward = hue;
    }

    public static double getDFadeFactorBack() {
        return dFadeFactorBack;
    }

    public static void setDFadeFactorBack(final double fadeFactor) {
        dFadeFactorBack = MathPlus.bounded(MIN_FADE, fadeFactor, MAX_FADE);
    }

    public static double getDFadeFactorForward() {
        return dFadeFactorForward;
    }

    public static void setdFadeFactorForward(final double fadeFactor) {
        dFadeFactorForward = MathPlus.bounded(MIN_FADE, fadeFactor, MAX_FADE);
    }

    public static int getDLookBack() {
        return dLookBack;
    }

    public static void setDLookBack(final int look) {
        dLookBack = look;
    }

    public static int getDLookForward() {
        return dLookForward;
    }

    public static void setDLookForward(final int look) {
        dLookForward = look;
    }

    public static boolean isDUnderBack() {
        return dUnderBack;
    }

    public static void setDUnderBack(final boolean under) {
        dUnderBack = under;
    }

    public static boolean isDUnderForward() {
        return dUnderForward;
    }

    public static void setdUnderForward(final boolean under) {
        dUnderForward = under;
    }
}
