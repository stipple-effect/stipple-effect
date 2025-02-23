package com.jordanbunke.stipple_effect.utility;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.project.ZoomLevel;

import java.nio.file.Path;

public class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild";

    public static final Path PROGRAM_FILE = Path.of("program"),
            ICON_FOLDER = Path.of("icons"),
            PALETTE_FOLDER = Path.of("palettes"),
            CURSOR_FOLDER = Path.of("cursors"),
            MISC_FOLDER = Path.of("misc"),
            TOOL_TIP_FOLDER = Path.of("tooltips"),
            BLURB_FOLDER = Path.of("blurbs");

    public static final String[] ACCEPTED_RASTER_IMAGE_SUFFIXES = new String[] {
            "jpg",
            SaveConfig.SaveType.PNG_SHEET.getFileSuffix()
    };

    public static final Coord2D NO_VALID_TARGET = new Coord2D(Integer.MIN_VALUE, Integer.MIN_VALUE);

    public static final double TICK_HZ = 100d, FPS = 60d;

    public static final double OPAQUE = 1d,
            CIRCLE = Math.PI * 2, _45_SNAP_INC = CIRCLE / 8d,
            _15_SNAP_INC = _45_SNAP_INC / 3d;

    public static final String
            BASE_LAYER_NAME = "Background", STD_LAYER_PREFIX = "Layer ",
            FLATTENED_LAYER_NAME = "Flattened", FROM_PREVIEW_LAYER_NAME = "From script",
            TIME_LAPSE = "Time lapse",
            UNTITLED_PROJECT_NAME = "[ Untitled ]", NO_FOLDER_SELECTED = "[ No folder selected ]",
            NATIVE_FILE_SUFFIX = "stip", PALETTE_FILE_SUFFIX = "stippal",
            SCRIPT_FILE_SUFFIX = "ses", SCRIPT_GLOBAL_NAMESPACE = "SE",
            MATH_NAMESPACE = "Math", GRAPHICS_NAMESPACE = "Graphics",
            ASCII_TEMPLATE = "ascii", LATIN_EXTENDED_TEMPLATE = "latin-extended",
            COLOR_TOOL_TIP_PREFIX = "color:", SHORTCUT_PREFIX = "sc:",
            OPEN_HIGHLIGHT = "{", CLOSE_HIGHLIGHT = "}",
            OPEN_COLOR = "<#", CLOSE_COLOR = ">",
            TYPING_CODE = "typing", CEL_SELECTION = "cel_selection",
            GENERIC_APPROVAL_TEXT = "Confirm", CLOSE_DIALOG_TEXT = "Close";

    public static final int DEFAULT_CANVAS_W = 32, DEFAULT_CANVAS_H = 32,
            MIN_CANVAS_W = 1, MIN_CANVAS_H = 1, MAX_CANVAS_W = 1920, MAX_CANVAS_H = 1080,
            OVERLAY_BORDER_PX = 6, SPLASH_TIMEOUT_SECS = 1800, NO_SELECTION = -1,
            MAX_PALETTE_SIZE = 300;

    public static final int DEFAULT_BRUSH_BREADTH = 1,
            MIN_BREADTH = 1, MAX_BREADTH = 100, BREADTH_INC = 5,
            MIN_FONT_SCALE = 1, MAX_FONT_SCALE = 10,
            DEFAULT_FONT_PX_SPACING = 2, MIN_FONT_PX_SPACING = -10, MAX_FONT_PX_SPACING = 50,
            MAX_NUM_LAYERS = 50, MAX_NUM_FRAMES = 300, MAX_OUTLINE_PX = 10,
            CHECKPOINTS_DUMP_THRESHOLD = 5,
            MILLIS_IN_SECOND = 1000, MIN_PLAYBACK_FPS = 1, MAX_PLAYBACK_FPS = 24,
            PLAYBACK_FPS_INC = 1, DEFAULT_PLAYBACK_FPS = 10,
            TIMER_MAX_OUT = 4, MILLIS_PER_TIMER_INC = 100,
            RGBA_SCALE = 255, COLOR_SET_RGBA_INC = 5,
            HUE_SCALE = 360, SAT_SCALE = RGBA_SCALE, VALUE_SCALE = RGBA_SCALE,
            STATUS_UPDATE_DURATION_MILLIS = 5000, TOOL_TIP_MILLIS_THRESHOLD = 250,
            MIN_SCALE_UP = 1, MAX_SCALE_UP = 20, DEFAULT_SAVE_SCALE_UP = MIN_SCALE_UP,
            MAX_NAME_LENGTH = 40, STRETCH_PX_THRESHOLD = 10, ROTATE_PX_THRESHOLD = 25,
            MAX_HUE_SHIFT = HUE_SCALE / 2, MIN_HUE_SHIFT = -MAX_HUE_SHIFT,
            MAX_SV_SHIFT = SAT_SCALE, MIN_SV_SHIFT = -MAX_SV_SHIFT;

    public static final float
            DEF_ZOOM = ZoomLevel.PLUS_6.z,
            ZOOM_FOR_OVERLAY = ZoomLevel.NONE.z,
            ZOOM_FOR_GRID = DEF_ZOOM;

    public static final double EXACT_COLOR_MATCH = 0d,
            DEFAULT_TOLERANCE = EXACT_COLOR_MATCH, MAX_TOLERANCE = 1d,
            SMALL_TOLERANCE_INC = 0.01, BIG_TOLERANCE_INC = SMALL_TOLERANCE_INC * 10,
            UNBIASED = 0.5d, MIN_BIAS = 0d, MAX_BIAS = 1d, BIAS_INC = 0.01,
            MIN_SV_SCALE = 0d, MAX_SV_SCALE = 50d,
            DEFAULT_FRAME_DURATION = 1d,
            MIN_FRAME_DURATION = 0.1, MAX_FRAME_DURATION = 20d;
}
