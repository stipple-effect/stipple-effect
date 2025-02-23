package com.jordanbunke.stipple_effect.utility;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stipple_effect.utility.action.SEAction;
import com.jordanbunke.stipple_effect.utility.settings.Settings;
import com.jordanbunke.stipple_effect.visual.GraphicsUtils;
import com.jordanbunke.stipple_effect.visual.theme.Theme;
import com.jordanbunke.stipple_effect.visual.theme.logic.ThemeLogic;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ParserUtils {
    public static GameImage generateStatusEffectText(final String message) {
        final Theme t = Settings.getTheme();
        final Color main = ThemeLogic.intuitTextColor(t.panelBackground, true),
                accent = ThemeLogic.intuitTextColor(t.panelBackground, false);

        final TextBuilder tb = GraphicsUtils.uiText(t.textLight);

        final String[] segments = extractHighlight(message,
                Constants.OPEN_COLOR, Constants.CLOSE_COLOR);

        for (int i = 0; i < segments.length; i++) {
            if (i % 2 == 0)
                tb.setColor(main);
            else {
                tb.setColor(accent);
                tb.addText("#");
                tb.setColor(ParserSerializer.deserializeColor(segments[i]));
            }

            tb.addText(segments[i]);
        }

        return tb.build().draw();
    }

    public static String[] extractHighlight(final String inputLine) {
        return extractHighlight(inputLine,
                Constants.OPEN_HIGHLIGHT, Constants.CLOSE_HIGHLIGHT);
    }

    private static String[] extractHighlight(
            final String inputLine, final String open, final String close
    ) {
        String leftToProcess = inputLine;
        final List<String> blocks = new ArrayList<>();

        while (leftToProcess.contains(open) && leftToProcess.contains(close) &&
                leftToProcess.indexOf(open) < leftToProcess.indexOf(close)) {
            final int openIndex = leftToProcess.indexOf(open),
                    closeIndex = leftToProcess.indexOf(close);

            blocks.add(leftToProcess.substring(0, openIndex));
            blocks.add(leftToProcess.substring(openIndex + open.length(), closeIndex));

            leftToProcess = leftToProcess.substring(closeIndex + close.length());
        }

        if (!leftToProcess.isEmpty())
            blocks.add(leftToProcess);

        return blocks.toArray(String[]::new);
    }

    public static String[] getBlurb(final String blurbCode) {
        final Path blurbFile = Constants.BLURB_FOLDER.resolve(blurbCode + ".txt");

        return FileIO.readResource(
                ResourceLoader.loadResource(blurbFile), "\"" + blurbCode + "\" blurb"
        ).split("\n");
    }

    public static String[] getToolTip(final String toolTipCode) {
        if (toolTipCode.startsWith(Constants.COLOR_TOOL_TIP_PREFIX))
            return toolTipCode.substring(Constants.COLOR_TOOL_TIP_PREFIX.length()).split("\n");

        final Path blurbFile = Constants.TOOL_TIP_FOLDER
                .resolve(toolTipCode + ".txt");

        return FileIO.readResource(ResourceLoader.loadResource(blurbFile),
                "\"" + toolTipCode + "\" tooltip").split("\n");
    }

    public static String getShortcut(final String code) {
        if (code.startsWith(Constants.SHORTCUT_PREFIX)) {
            final String actionString =
                    code.substring(Constants.SHORTCUT_PREFIX.length()).trim();

            try {
                final SEAction action = SEAction.valueOf(actionString);
                return action.shortcut == null
                        ? "" : action.shortcut.toString();
            } catch (Exception e) {
                return code;
            }
        }

        return code;
    }
}
