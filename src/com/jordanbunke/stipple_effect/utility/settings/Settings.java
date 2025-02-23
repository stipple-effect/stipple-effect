package com.jordanbunke.stipple_effect.utility.settings;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.SerialBlock;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.tools.ToolWithBreadth;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.Layout;
import com.jordanbunke.stipple_effect.utility.OSUtils;
import com.jordanbunke.stipple_effect.utility.settings.types.BooleanSettingType;
import com.jordanbunke.stipple_effect.utility.settings.types.EnumSettingType;
import com.jordanbunke.stipple_effect.utility.settings.types.IntSettingType;
import com.jordanbunke.stipple_effect.utility.settings.types.StringSettingType;
import com.jordanbunke.stipple_effect.visual.DialogAssembly;
import com.jordanbunke.stipple_effect.visual.GraphicsUtils;
import com.jordanbunke.stipple_effect.visual.SEFonts;
import com.jordanbunke.stipple_effect.visual.pixel_grid.PixelGrid;
import com.jordanbunke.stipple_effect.visual.theme.Theme;
import com.jordanbunke.stipple_effect.visual.theme.Themes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Settings {
    private static final Path SETTINGS_FILE;

    public enum Code {
        // boolean settings
        FULLSCREEN_ON_STARTUP(new Setting<>(BooleanSettingType.get(), false)),
        PIXEL_GRID_ON_BY_DEFAULT(new Setting<>(BooleanSettingType.get(), false)),
        SEPARATED_PREVIEW(new Setting<>(BooleanSettingType.get(), false)),
        PROPAGATE_BREADTH(new Setting<>(BooleanSettingType.get(), false,
                b -> {
            if (b) ToolWithBreadth.resetAll();
        })),
        INVERT_ZOOM_DIRECTION(new Setting<>(BooleanSettingType.get(), false)),
        INVERT_BREADTH_DIRECTION(new Setting<>(BooleanSettingType.get(), false)),
        INVERT_TOLERANCE_DIRECTION(new Setting<>(BooleanSettingType.get(), false)),
        INVERT_FONT_SIZE_DIRECTION(new Setting<>(BooleanSettingType.get(), false)),

        // int settings
        WINDOWED_W(new Setting<>(
                IntSettingType.get(), Layout.MAX_WINDOW_W,
                ww -> {
                    StippleEffect.get().remakeWindow();
                })),
        WINDOWED_H(new Setting<>(
                IntSettingType.get(), Layout.MAX_WINDOW_H,
                wh -> {
                    StippleEffect.get().remakeWindow();
                })),
        CHECKERBOARD_W_PX(new Setting<>(
                IntSettingType.get(), Layout.DEFAULT_CHECKERBOARD_DIM,
                cbw -> {
                    StippleEffect.get().getContexts()
                            .forEach(SEContext::redrawCheckerboard);
                })),
        CHECKERBOARD_H_PX(new Setting<>(
                IntSettingType.get(), Layout.DEFAULT_CHECKERBOARD_DIM,
                cbh -> {
                    StippleEffect.get().getContexts()
                            .forEach(SEContext::redrawCheckerboard);
                })),
        PIXEL_GRID_X_PX(new Setting<>(
                IntSettingType.get(), Layout.DEFAULT_PIXEL_GRID_DIM, pgx -> {
                    final SEContext c = StippleEffect.get().getContext();
                    c.renderInfo.setPixelGrid(true);
                    PixelGrid.redraw();
                })),
        PIXEL_GRID_Y_PX(new Setting<>(
                IntSettingType.get(), Layout.DEFAULT_PIXEL_GRID_DIM, pgy -> {
                    final SEContext c = StippleEffect.get().getContext();
                    c.renderInfo.setPixelGrid(true);
                    PixelGrid.redraw();
                })),
        DEFAULT_CANVAS_W_PX(new Setting<>(
                IntSettingType.get(), Constants.DEFAULT_CANVAS_W)),
        DEFAULT_CANVAS_H_PX(new Setting<>(
                IntSettingType.get(), Constants.DEFAULT_CANVAS_H)),
        DEFAULT_TOOL_BREADTH(new Setting<>(
                IntSettingType.get(), Constants.DEFAULT_BRUSH_BREADTH)),

        // string settings
        DEFAULT_INDEX_PREFIX(new Setting<>(
                StringSettingType.get(), "", prefix -> {
                    StippleEffect.get().getContexts().forEach(
                            c -> c.getSaveConfig().setIndexPrefix(prefix));
                })),
        DEFAULT_INDEX_SUFFIX(new Setting<>(
                StringSettingType.get(), "", suffix -> {
                    StippleEffect.get().getContexts().forEach(
                            c -> c.getSaveConfig().setIndexSuffix(suffix));
                })),

        // enum settings
        PROGRAM_FONT(new Setting<>(
                new EnumSettingType<>(SEFonts.Code.class),
                SEFonts.DEFAULT_FONT, code -> {
                    DialogAssembly.setDialogToProgramSettings();
                    StippleEffect.get().rebuildAllMenus();
                })),
        THEME(new Setting<>(
                new EnumSettingType<>(Themes.class),
                Themes.DEFAULT, theme -> {
                    GraphicsUtils.refreshAssets();

                    StippleEffect.get().getContexts().forEach(
                            SEContext::redrawCheckerboard);
                    StippleEffect.get().rebuildAllMenus();
                }));

        private final Setting<?> setting;

        <T> Code(final Setting<T> setting) {
            this.setting = setting;
        }
        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static Code fromString(final String code) {
            try {
                return Code.valueOf(code.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private <T> void set(final T value) {
            setting.trySet(value);
        }

        private void read(final String value) {
            switch (this) {
                case WINDOWED_W -> readWithMinimum(value, Layout.MIN_WINDOW_W);
                case WINDOWED_H -> readWithMinimum(value, Layout.MIN_WINDOW_H);
                case DEFAULT_CANVAS_W_PX, DEFAULT_CANVAS_H_PX,
                        CHECKERBOARD_W_PX, CHECKERBOARD_H_PX,
                        PIXEL_GRID_X_PX, PIXEL_GRID_Y_PX,
                        DEFAULT_TOOL_BREADTH -> readWithMinimum(value, 1);
                default -> setting.setFromRead(value);
            }
        }

        private void readWithMinimum(final String value, final int minimum) {
            try {
                final int dim = Integer.parseInt(value);

                if (dim >= minimum)
                    setting.setFromRead(value);
            } catch (NumberFormatException ignored) {

            }
        }
    }

    static {
        final Path internal = Path.of("data", ".settings"),
                medial = Path.of(StippleEffect.PROGRAM_NAME).resolve(internal);

        if (OSUtils.isWindows()) {
            final String appData = System.getenv("APPDATA");
            SETTINGS_FILE = Path.of(appData).resolve(medial);
        } else
            SETTINGS_FILE = internal;
    }

    public static void read() {
        final String file = FileIO.readFile(SETTINGS_FILE);

        if (file == null)
            return;

        final SerialBlock[] blocks = ParserSerializer
                .deserializeBlocksAtDepthLevel(file);

        for (SerialBlock block : blocks) {
            final Code matched = Code.fromString(block.tag());

            if (matched != null)
                matched.read(block.value());
        }
    }

    @SuppressWarnings("all")
    public static void write() {
        final Path settingsFolder = SETTINGS_FILE.getParent();

        if (!settingsFolder.toFile().exists())
            FileIO.safeMakeDirectory(settingsFolder);
        else if (!settingsFolder.toFile().isDirectory()) {
            try {
                Files.delete(settingsFolder);
                FileIO.safeMakeDirectory(settingsFolder);
            } catch (IOException ioe) {
                GameError.send("Couldn't delete file at " + settingsFolder +
                        " needed to clear space for the settings folder. " +
                        "Could not write " + StippleEffect.PROGRAM_NAME +
                        " settings.");
                return;
            }
        }

        final StringBuilder sb = new StringBuilder();

        ParserSerializer.serializeSimpleAttributes(sb, -1,
                Arrays.stream(Code.values()).map(c ->
                        new Pair<>(c.toString(), c.setting.get().toString()))
                        .toArray(Pair[]::new));

        FileIO.writeFile(SETTINGS_FILE, sb.toString());
    }

    public static void resetAssignments() {
        for (Code code : Code.values())
            code.setting.resetAssignment();
    }

    public static void apply() {
        for (Code code : Code.values())
            code.setting.apply();

        write();
    }

    // setters
    public static void setFullscreenOnStartup(final boolean fullscreenOnStartup) {
        Code.FULLSCREEN_ON_STARTUP.set(fullscreenOnStartup);
    }

    public static void setPixelGridOnByDefault(final boolean pixelGridOnByDefault) {
        Code.PIXEL_GRID_ON_BY_DEFAULT.set(pixelGridOnByDefault);
    }

    public static void setSeparatedPreview(final boolean separatedPreview) {
        Code.SEPARATED_PREVIEW.set(separatedPreview);
    }

    public static void setPropagateBreadth(final boolean propagateBreadth) {
        Code.PROPAGATE_BREADTH.set(propagateBreadth);
    }

    public static void setInvertZoomDirection(final boolean invertZoomDirection) {
        Code.INVERT_ZOOM_DIRECTION.set(invertZoomDirection);
    }

    public static void setInvertBreadthDirection(final boolean invertBreadthDirection) {
        Code.INVERT_BREADTH_DIRECTION.set(invertBreadthDirection);
    }

    public static void setInvertToleranceDirection(final boolean invertToleranceDirection) {
        Code.INVERT_TOLERANCE_DIRECTION.set(invertToleranceDirection);
    }

    public static void setInvertFontSizeDirection(final boolean invertFontSizeDirection) {
        Code.INVERT_FONT_SIZE_DIRECTION.set(invertFontSizeDirection);
    }

    public static void setWindowedWidth(
            final int windowedWidth
    ) {
        Code.WINDOWED_W.set(windowedWidth);
    }

    public static void setWindowedHeight(
            final int windowedHeight
    ) {
        Code.WINDOWED_H.set(windowedHeight);
    }

    public static void setCheckerboardWPixels(
            final int checkerboardWPixels
    ) {
        Code.CHECKERBOARD_W_PX.set(checkerboardWPixels);
    }

    public static void setCheckerboardHPixels(
            final int checkerboardHPixels
    ) {
        Code.CHECKERBOARD_H_PX.set(checkerboardHPixels);
    }

    public static void setPixelGridXPixels(
            final int pixelGridXPixels
    ) {
        Code.PIXEL_GRID_X_PX.set(pixelGridXPixels);
    }

    public static void setPixelGridYPixels(
            final int pixelGridYPixels
    ) {
        Code.PIXEL_GRID_Y_PX.set(pixelGridYPixels);
    }

    public static void setDefaultCanvasWPixels(
            final int defaultCanvasWPixels
    ) {
        Code.DEFAULT_CANVAS_W_PX.set(defaultCanvasWPixels);
    }

    public static void setDefaultCanvasHPixels(
            final int defaultCanvasHPixels
    ) {
        Code.DEFAULT_CANVAS_H_PX.set(defaultCanvasHPixels);
    }

    public static void setDefaultToolBreadth(
            final int defaultToolBreadth
    ) {
        Code.DEFAULT_TOOL_BREADTH.set(defaultToolBreadth);
    }

    public static void setDefaultIndexPrefix(
            final String defIndexPrefix
    ) {
        Code.DEFAULT_INDEX_PREFIX.set(defIndexPrefix);
    }

    public static void setDefaultIndexSuffix(
            final String defIndexSuffix
    ) {
        Code.DEFAULT_INDEX_SUFFIX.set(defIndexSuffix);
    }

    public static void setProgramFont(final SEFonts.Code fontCode) {
        Code.PROGRAM_FONT.set(fontCode);
    }

    public static void setTheme(final Themes theme) {
        Code.THEME.set(theme);
    }

    // checkers
    public static boolean checkIsFullscreenOnStartup() {
        return (boolean) Code.FULLSCREEN_ON_STARTUP.setting.check();
    }

    public static boolean checkIsPixelGridOnByDefault() {
        return (boolean) Code.PIXEL_GRID_ON_BY_DEFAULT.setting.check();
    }

    public static boolean checkIsSeparatedPreview() {
        return (boolean) Code.SEPARATED_PREVIEW.setting.check();
    }

    public static boolean checkIsPropagateBreadth() {
        return (boolean) Code.PROPAGATE_BREADTH.setting.check();
    }

    public static boolean checkIsInvertZoomDirection() {
        return (boolean) Code.INVERT_ZOOM_DIRECTION.setting.check();
     }

     public static boolean checkIsInvertBreadthDirection() {
        return (boolean) Code.INVERT_BREADTH_DIRECTION.setting.check();
     }

    public static boolean checkIsInvertToleranceDirection() {
        return (boolean) Code.INVERT_TOLERANCE_DIRECTION.setting.check();
    }

    public static boolean checkIsInvertFontSizeDirection() {
        return (boolean) Code.INVERT_FONT_SIZE_DIRECTION.setting.check();
    }

    public static int checkWindowedWidth() {
        return (int) Code.WINDOWED_W.setting.check();
    }

    public static int checkWindowedHeight() {
        return (int) Code.WINDOWED_H.setting.check();
    }

    public static int checkCheckerboardWPixels() {
        return (int) Code.CHECKERBOARD_W_PX.setting.check();
    }

    public static int checkCheckerboardHPixels() {
        return (int) Code.CHECKERBOARD_H_PX.setting.check();
    }

    public static int checkPixelGridXPixels() {
        return (int) Code.PIXEL_GRID_X_PX.setting.check();
    }

    public static int checkPixelGridYPixels() {
        return (int) Code.PIXEL_GRID_Y_PX.setting.check();
    }

    public static int checkDefaultCanvasWPixels() {
        return (int) Code.DEFAULT_CANVAS_W_PX.setting.check();
    }

    public static int checkDefaultCanvasHPixels() {
        return (int) Code.DEFAULT_CANVAS_H_PX.setting.check();
    }

    public static int checkDefaultToolBreadth() {
        return (int) Code.DEFAULT_TOOL_BREADTH.setting.check();
    }

    public static String checkDefaultIndexPrefix() {
        return (String) Code.DEFAULT_INDEX_PREFIX.setting.check();
    }

    public static String checkDefaultIndexSuffix() {
        return (String) Code.DEFAULT_INDEX_SUFFIX.setting.check();
    }

    public static SEFonts.Code checkProgramFont() {
        return (SEFonts.Code) Code.PROGRAM_FONT.setting.check();
    }

    public static Themes checkTheme() {
        return (Themes) Code.THEME.setting.check();
    }

    // getters
    public static boolean isFullscreenOnStartup() {
        return (boolean) Code.FULLSCREEN_ON_STARTUP.setting.get();
    }

    public static boolean isPixelGridOnByDefault() {
        return (boolean) Code.PIXEL_GRID_ON_BY_DEFAULT.setting.get();
    }

    public static boolean isSeparatedPreview() {
        return (boolean) Code.SEPARATED_PREVIEW.setting.get();
    }

    public static boolean isPropagateBreadth() {
        return (boolean) Code.PROPAGATE_BREADTH.setting.get();
    }

    public static int getWindowedWidth() {
        return (int) Code.WINDOWED_W.setting.get();
    }

    public static int getWindowedHeight() {
        return (int) Code.WINDOWED_H.setting.get();
    }

    public static int getCheckerboardWPixels() {
        return (int) Code.CHECKERBOARD_W_PX.setting.get();
    }

    public static int getCheckerboardHPixels() {
        return (int) Code.CHECKERBOARD_H_PX.setting.get();
    }

    public static int getPixelGridXPixels() {
        return (int) Code.PIXEL_GRID_X_PX.setting.get();
    }

    public static int getPixelGridYPixels() {
        return (int) Code.PIXEL_GRID_Y_PX.setting.get();
    }

    public static int getDefaultCanvasWPixels() {
        return (int) Code.DEFAULT_CANVAS_W_PX.setting.get();
    }

    public static int getDefaultCanvasHPixels() {
        return (int) Code.DEFAULT_CANVAS_H_PX.setting.get();
    }

    public static int getDefaultToolBreadth() {
        return (int) Code.DEFAULT_TOOL_BREADTH.setting.get();
    }

    public static String getDefaultIndexPrefix() {
        return (String) Code.DEFAULT_INDEX_PREFIX.setting.get();
    }

    public static String getDefaultIndexSuffix() {
        return (String) Code.DEFAULT_INDEX_SUFFIX.setting.get();
    }

    public static SEFonts.Code getProgramFont() {
        return (SEFonts.Code) Code.PROGRAM_FONT.setting.get();
    }

    public static Theme getTheme() {
        return ((Themes) Code.THEME.setting.get()).get();
    }

    public static int getScrollClicks(
            final int nativeClicksScrolled, final Code setting
    ) {
        try {
            final boolean inverted = (boolean) setting.setting.get();

            return nativeClicksScrolled * (inverted ? -1 : 1);
        } catch (ClassCastException cce) {
            return nativeClicksScrolled;
        }
    }
}
