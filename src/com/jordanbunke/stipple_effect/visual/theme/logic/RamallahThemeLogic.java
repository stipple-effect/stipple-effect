package com.jordanbunke.stipple_effect.visual.theme.logic;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.awt.*;
import java.nio.file.Path;

public final class RamallahThemeLogic extends ThemeLogic {
    private static final RamallahThemeLogic INSTANCE;

    private final Path FOLDER;
    private final GameImage KEFFIYEH;

    static {
        INSTANCE = new RamallahThemeLogic();
    }

    private RamallahThemeLogic() {
        FOLDER = THEMES_FOLDER.resolve("ramallah");
        KEFFIYEH = ResourceLoader.loadImageResource(FOLDER.resolve("keffiyeh.png"));
    }

    public static RamallahThemeLogic get() {
        return INSTANCE;
    }

    @Override
    public GameImage highlightedIconOverlay() {
        return overlayTransformation(super.highlightedIconOverlay());
    }

    @Override
    public GameImage selectedIconOverlay() {
        return overlayTransformation(super.selectedIconOverlay());
    }

    private GameImage overlayTransformation(final GameImage asset) {
        return pixelWiseTransformation(asset, c -> c.getAlpha() == 0
                ? c : new Color(0, 0, 0, c.getAlpha()));
    }

    @Override
    public GameImage drawScrollBoxBackground(final int w, final int h) {
        final GameImage scrollBox = new GameImage(w, h);

        final int vw = KEFFIYEH.getWidth(), vh = KEFFIYEH.getHeight(),
                timesX = w / vw, timesY = h / vh;

        for (int x = 0; x <= timesX; x++)
            for (int y = 0; y <= timesY; y++)
                scrollBox.draw(KEFFIYEH, x * vw, y * vh);

        return scrollBox.submit();
    }

    @Override
    public GameImage[] loadSplash() {
        final int SPLASH_FRAMES = 10, SPLASH_SCALE_UP = 3;
        final String SPLASH_FRAME_BASE = "splash_";

        final GameImage[] frames = new GameImage[SPLASH_FRAMES];

        for (int i = 0; i < SPLASH_FRAMES; i++) {
            final Path framePath = FOLDER.resolve(
                    SPLASH_FRAME_BASE + i + ".png");

            frames[i] = ImageProcessing.scale(
                    ResourceLoader.loadImageResource(framePath),
                    SPLASH_SCALE_UP);
        }

        return frames;
    }

    @Override
    public int ticksPerSplashFrame() {
        return 20;
    }
}
