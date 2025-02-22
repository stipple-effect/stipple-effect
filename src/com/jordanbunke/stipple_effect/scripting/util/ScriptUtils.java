package com.jordanbunke.stipple_effect.scripting.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.SEInterpreter;
import com.jordanbunke.stipple_effect.selection.Outliner;
import com.jordanbunke.stipple_effect.selection.Selection;
import com.jordanbunke.stipple_effect.state.ProjectState;
import com.jordanbunke.stipple_effect.utility.Constants;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class ScriptUtils {
    public static SEContext transformProjectPerLayer(
            final SEContext c, final HeadFuncNode script, final Path path
    ) {
        int w = 1, h = 1, fc = 1;

        final int srcFC = c.getState().getFrameCount();

        final List<SELayer> layers = new ArrayList<>(),
                srcLayers = new ArrayList<>(c.getState().getLayers());

        for (final SELayer layer : srcLayers) {
            final GameImage[] layerContent = IntStream.range(0, srcFC)
                    .mapToObj(layer::getCel).toArray(GameImage[]::new);

            final GameImage[] output =
                    ScriptUtils.runPreviewScript(layerContent, script, path);

            if (output == null) {
                ScriptErrorLog.fireError(
                        ScriptErrorLog.Message.CUSTOM_RT, TextPosition.N_A,
                        "Script failed to produce a result for layer \"" +
                                layer.getName() + "\"");
                return null;
            }

            fc = Math.max(fc, output.length);
            w = Math.max(w, Arrays.stream(output).map(GameImage::getWidth)
                    .reduce(0, Math::max));
            h = Math.max(h, Arrays.stream(output).map(GameImage::getHeight)
                    .reduce(0, Math::max));

            layers.add(SELayer.fromPreviewContent(output, layer));
        }

        final ProjectState state = ProjectState.makeFromNativeFile(
                w, h, layers, fc, ProjectState.defaultFrameDurations(fc));

        return new SEContext(null, state, w, h);
    }

    public static SEContext transform(
            final GameImage[] input, final HeadFuncNode script, final Path path
    ) {
        return projectFromScriptOutput(runPreviewScript(input, script, path));
    }

    public static SEContext projectFromScriptOutput(final GameImage[] output) {
        if (output == null) {
            ScriptErrorLog.fireError(
                    ScriptErrorLog.Message.CUSTOM_RT, TextPosition.N_A,
                    "Script failed to produce a result");
            return null;
        }

        final int fc = output.length;
        final int w = Arrays.stream(output).map(GameImage::getWidth)
                .reduce(0, Math::max),
                h = Arrays.stream(output).map(GameImage::getHeight)
                        .reduce(0, Math::max);

        final SELayer layer = SELayer.fromPreviewContent(output);
        final ProjectState state =
                ProjectState.makeFromRasterFile(w, h, layer, fc);

        return new SEContext(null, state, w, h);
    }

    public static GameImage[] runPreviewScript(
            final GameImage[] input, final HeadFuncNode script, final Path path
    ) {
        final boolean animScript = script.paramsMatch(
                TypeNode.arrayOf(TypeNode.getImage())),
                imgReturn = script.getReturnType().equals(TypeNode.getImage());

        if (animScript) {
            // image[] -> image[] or image[] -> image
            final Object result = SEInterpreter.get().run(script, path, (Object) input);

            if (result instanceof GameImage image)
                return new GameImage[] { image };
            else if (result instanceof ScriptArray arr)
                return convertScriptImageArray(arr);

            return null;
        } else if (imgReturn) {
            // image -> image
            final GameImage[] output = new GameImage[input.length];

            for (int i = 0; i < output.length; i++) {
                final Object result = SEInterpreter.get().run(script, path, input[i]);

                if (result instanceof GameImage image)
                    output[i] = image;
                else
                    return null;
            }

            return output;
        } else if (input.length == 1) {
            // image -> image[]
            final Object result = SEInterpreter.get().run(script, path, input[0]);

            return (result instanceof ScriptArray arr)
                    ? convertScriptImageArray(arr) : null;
        }

        return null;
    }

    private static GameImage[] convertScriptImageArray(final ScriptArray arr) {
        final GameImage[] images = new GameImage[arr.size()];

        for (int i = 0; i < images.length; i++)
            images[i] = (GameImage) arr.get(i);

        return images;
    }

    public static Path scriptFolderToPath(final ScriptArray folder) {
        final String first = (String) folder.get(0);
        final String[] rest = new String[folder.size() - 1];

        for (int i = 0; i < rest.length; i++)
            rest[i] = (String) folder.get(i + 1);

        return Path.of(first, rest);
    }

    public static Selection convertSelection(
            final ScriptSet input, final int w, final int h
    ) {
        return convertSelection(input, (x, y) ->
                x >= 0 && x < w && y >= 0 && y < h);
    }

    public static Selection convertSelection(
            final ScriptSet input
    ) {
        return convertSelection(input, (x, y) -> true);
    }

    private static Selection convertSelection(
            final ScriptSet input,
            final BiPredicate<Integer, Integer> coordCondition
    ) {
        final Set<Coord2D> pixels = new HashSet<>();

        input.stream().map(px -> (ScriptArray) px).forEach(a -> {
            if (a.size() != 2)
                return;

            final int x = (int) a.get(0), y = (int) a.get(1);

            if (coordCondition.test(x, y))
                pixels.add(new Coord2D(x, y));
        });

        return Selection.fromPixels(pixels);
    }

    public static boolean invalidSideMask(
            final int[] sideMask, final ExpressionNode culprit
    ) {
        final int expected = Outliner.Direction.values().length,
                MAX = Constants.MAX_OUTLINE_PX;

        if (sideMask.length != expected) {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    culprit.getPosition(),
                    "Side mask array argument for outline operation has " +
                            sideMask.length + " elements instead of " + expected);
            return true;
        }

        for (int side : sideMask) {
            if (side < -MAX || side > MAX) {
                ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                        culprit.getPosition(),
                        "Side mask value " + side + " is out of bounds (" +
                                (-MAX) + " <= px <= " + MAX + ")");
                return true;
            }
        }

        return false;
    }
}
