package com.jordanbunke.stipple_effect.utility;

import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.selection.CelSelection;

import java.awt.*;
import java.nio.file.Path;

public class StatusUpdates {
    private static void send(final String update) {
        StippleEffect.get().sendStatusUpdate(update);
    }

    public static void failedToCompileScript(final Path filepath) {
        send("Script at \"" + filepath + "\" could not be compiled");
    }

    // not permitted
    public static void invalidLink(final String link) {
        actionNotPermitted("visit the link \"" + link + "\"",
                "it is not a valid URL");
    }

    public static void invalidPreviewScript(final String signature) {
        actionNotPermitted("upload script",
                "a preview script with the type signature \"" + signature +
                        "\" cannot be applied to this project");
    }

    public static void invalidAutomationScript(final String signature) {
        actionNotPermitted("upload script",
                "this script has the type signature \"" + signature +
                        "\"; automation scripts must be \"()\"");
    }

    public static void cannotSetCheckAndGridToBounds(
            final boolean fromSelection
    ) {
        actionNotPermitted(
                "set the checkerboard and pixel grid cell dimensions",
                "the width and/or height of the " +
                        (fromSelection ? "selection" : "canvas") +
                        " is out of bounds (" + Layout.PIXEL_GRID_MIN +
                        "<= px <= " + Layout.PIXEL_GRID_MAX + ")");
    }

    public static void cannotSetPixelGrid() {
        actionNotPermitted("turn pixel grid on or off",
                "the pixel grid cannot be rendered for this project's current dimensions and/or zoom level");
    }

    public static void cannotCropToSelection() {
        actionNotPermitted("crop the canvas to the selection",
                "nothing is selected");
    }

    public static void cannotFlatten() {
        actionNotPermitted("flatten the project",
                "it only has a single layer");
    }

    public static void cannotMergeWithLayerBelow(final String layerName) {
        actionNotPermitted("merge \"" + layerName + "\" with the layer below",
                "it is the bottommost layer");
    }

    public static void cannotMoveLayer(
            final String layerName, final boolean triedForward
    ) {
        cannotMove(true, "\"" + layerName + "\"", triedForward);
    }

    public static void cannotMoveFrame(
            final int index, final boolean triedForward
    ) {
        cannotMove(false, String.valueOf(index + 1), triedForward);
    }

    private static void cannotMove(
            final boolean isLayer, final String name, final boolean triedForward
    ) {
        final String thing = isLayer ? "layer" : "frame",
                direction = isLayer
                        ? (triedForward ? "up" : "down")
                        : (triedForward ? "forward" : "back"),
                extreme = isLayer
                        ? (triedForward ? "highest" : "lowest")
                        : (triedForward ? "last" : "first");

        actionNotPermitted("move " + thing + " " + name + " " + direction,
                "it is already the " + extreme + " " + thing + " in the project");
    }

    public static void cannotAddLayer() {
        cannotAdd("layer", Constants.MAX_NUM_LAYERS);
    }

    public static void cannotAddFrame() {
        cannotAdd("frame", Constants.MAX_NUM_FRAMES);
    }

    private static void cannotAdd(final String thing, final int maximum) {
        actionNotPermitted("add a " + thing + " to the project",
                "the maximum (" + maximum + ") has already been reached");
    }

    public static void cannotRemoveFrame() {
        cannotRemove("This", "frame");
    }

    public static void cannotRemoveLayer(final String layerName) {
        cannotRemove("\"" + layerName + "\"", "layer");
    }

    private static void cannotRemove(final String name, final String thing) {
        send(name + " is the only " + thing +
                " in the project; it cannot be removed");
    }

    public static void noPalette() {
        send("There is no palette selected for this action to be performed");
    }

    public static void cannotShiftColorPalette(
            final Palette p, final Color c, final boolean isLeft
    ) {
        final boolean colorInPalette = p.containsColor(c);

        final String dir = isLeft ? "left" : "right";

        actionNotPermitted("shift the selected color " + processColor(c) +
                        " to the " + dir + " in \"" + p.getName() + "\"",
                colorInPalette ? ("it is already the " + dir + "most color")
                        : "it is not in the palette");
    }

    public static void cannotColorPalette(
            final boolean add, final Palette p, final Color c
    ) {
        actionNotPermitted((add ? "add" : "remove") + " the selected color " +
                        processColor(c) + (add ? " to " : " from ") +
                        "\"" + p.getName() + "\"",
                processColor(c) + (add ? " is already" : " is not") +
                        " in the palette");
    }

    public static void cannotSelectColorPalette(
            final Palette p, final Color c, final boolean isLeft
    ) {
        final String dir = isLeft ? "left" : "right";

        actionNotPermitted("select the next included color to the " + dir +
                        " in the palette \"" + p.getName() + "\"",
                "the current selected color " + processColor(c) +
                        " is not included in the palette");
    }

    public static void scriptActionNotPermitted(
            final String attempt, final String reason,
            final TextPosition position
    ) {
        final String update = "Script failed to " +
                attempt + " because " + reason;
        send(update);
        ScriptErrorLog.fireError(
                ScriptErrorLog.Message.CUSTOM_RT, position, update);
    }

    private static void actionNotPermitted(
            final String attempt, final String reason
    ) {
        send("Cannot " + attempt + " because " + reason);
    }

    // toolbar actions
    public static void setPixelGrid(final boolean on) {
        final String value = on ? "ON" : "OFF";
        send("Turned pixel grid " + value);
    }

    // color actions
    public static void colorSliderAdjustment(
            final String slider, final int sliderVal, final Color result
    ) {
        send(slider + " slider set to " + sliderVal +
                "; selected color is " + processColor(result));
    }

    public static void swapColors() {
        final Color primary = StippleEffect.get().getPrimary(),
                secondary = StippleEffect.get().getSecondary();

        send("Primary: " + processColor(primary) +
                " | Secondary: " + processColor(secondary));
    }

    public static void addColorToPalette(final Palette p, final Color c) {
        send("Added " + processColor(c) + " to \"" + p.getName() + "\"");
    }

    public static void removeColorFromPalette(final Palette p, final Color c) {
        send("Removed " + processColor(c) + " from \"" + p.getName() + "\"");
    }

    public static void moveLeftInPalette(final Palette p, final Color c) {
        send("Shifted " + processColor(c) + " to the left in \"" +
                p.getName() + "\"");
    }

    public static void moveRightInPalette(final Palette p, final Color c) {
        send("Shifted " + processColor(c) + " to the right in \"" +
                p.getName() + "\"");
    }

    public static void selectNextPaletteColor(
            final Palette p, final Color c, final boolean isLeft
    ) {
        final Color next = isLeft ? p.nextLeft(c) : p.nextRight(c);

        send("Selected next included color to the " +
                (isLeft ? "left" : "right") + " of " + processColor(c) +
                " in \"" + p.getName() + "\": " + processColor(next));
    }

    public static String processColor(final Color c) {
        return Constants.OPEN_COLOR +
                ParserSerializer.serializeColor(c, true) +
                Constants.CLOSE_COLOR;
    }

    // frame actions
    public static void movedFrame(
            final int fromIndex, final int toIndex, final int frameCount
    ) {
        send("Moved selected frame from position (" + (fromIndex + 1) +
                "/" + frameCount + ") to (" + (toIndex + 1) +
                "/" + frameCount + ")");
    }

    public static void addedFrame(
            final boolean duplicated, final int wasIndex,
            final int index, final int frameCount
    ) {
        send((duplicated ? "Duplicated frame from (" + (wasIndex + 1) + " to "
                : "Added frame (") + (index + 1) + "/" + frameCount + ")");
    }

    public static void removedFrame(
            final int wasIndex, final int index, final int frameCount
    ) {
        send("Removed frame " + (wasIndex + 1) + "; active frame: (" +
                (index + 1) + "/" + frameCount + ")");
    }

    public static void frameNavigation(
            final int index, final int frameCount
    ) {
        send("Active frame: (" + (index + 1) + "/" + frameCount + ")");
    }

    // layer actions
    public static void changedLayerLinkedStatus(
            final boolean linked, final String layerName,
            final int index, final int layerCount
    ) {
        send((linked ? "L" : "Unl") +
                "inked frames in layer \"" + layerName + "\" (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void changedLayerVisibilityStatus(
            final boolean visible, final String layerName,
            final int index, final int layerCount
    ) {
        send((visible ? "En" : "Dis") +
                "abled layer \"" + layerName + "\" (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void addedLayer(
            final String layerName, final int index, final int layerCount
    ) {
        send("Added layer \"" + layerName + "\" (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void duplicatedLayer(
            final String oldLayerName, final String newLayerName,
            final int index, final int layerCount
    ) {
        send("Duplicated layer \"" +
                oldLayerName + "\" as \"" + newLayerName + "\" (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void removedLayer(
            final String layerName, final int index, final int layerCount
    ) {
        send("Removed layer \"" + layerName + "\"; active layer: (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void layerNavigation(
            final String layerName, final int index, final int layerCount
    ) {
        send("Active layer: \"" + layerName + "\" (" +
                (index + 1) + "/" + layerCount + ")");
    }

    public static void mergedWithLayerBelow(
            final String aboveName, final String belowName,
            final int index, final int layerCount
    ) {
        send("Merged layer \"" + aboveName + "\" onto \"" + belowName +
                "\" (" + (index + 1) + "/" + layerCount + ")");
    }

    public static void flattened() {
        send("Flattened the project down to a single layer");
    }

    public static void movedLayer(
            final String name, final int fromIndex, final int toIndex,
            final int layerCount
    ) {
        send("Moved layer \"" + name + "\" " +
                (fromIndex > toIndex ? "down" : "up") +
                " from (" + (fromIndex + 1) + "/" + layerCount +
                ") to (" + (toIndex + 1) + "/" + layerCount + ")");
    }

    public static void stateChangeFailed(final boolean triedUndo) {
        send("Cannot " + (triedUndo ? "undo" : "redo") + "; reached " +
                (triedUndo ? "beginning" : "end") + " of project state stack");
    }

    public static void setCheckAndGridToBounds(
            final int width, final int height, final boolean fromSelection
    ) {
        send("Set the dimensions of the checkerboard and pixel grid" +
                " cells to the bounds of the " +
                (fromSelection ? "selection" : "project canvas") +
                ": " + width + "x" + height);
    }

    public static void sentCelsToClipboard(
            final boolean copied, final CelSelection cels
    ) {
        final int celCount = cels.frameRange * cels.layersRange;

        send((copied ? "Copied" : "Cut") + " " + celCount + " cels of " +
                cels.celWidth + "x" + cels.celHeight + "px to the clipboard");
    }

    public static void sentSelectionToClipboard(
            final boolean copied, final int pixelCount
    ) {
        send((copied ? "Copied" : "Cut") + " " + pixelCount + " pixels " +
                (copied ? "" : "from the canvas ") + "to the clipboard");
    }

    public static void clipboardSendFailed(
            final boolean triedCopy
    ) {
        actionNotPermitted(triedCopy ? "copy" : "cut",
                "there is nothing selected");
    }

    public static void pasteFailed() {
        actionNotPermitted("paste",
                "the clipboard is empty or does not content content of a valid data type");
    }

    public static void pasteCelsFailed() {
        actionNotPermitted("paste the selected cels",
                "the clipboard contents are incompatible with this project");
    }

    public static void deleteCelsFailed() {
        actionNotPermitted("delete the selected cels",
                "the selected range is incompatible with this project");
    }

    public static void saving() {
        send("Saving... do not close " + StippleEffect.PROGRAM_NAME +
                " until the project has been saved");
    }

    public static void saveFailed() {
        send("Failed to save file(s); the project's save settings are invalid.");
    }

    public static void saved(final Path filepath) {
        send("Saved project to \"" + filepath + "\"");
    }

    public static void savedAllFrames(final Path folder) {
        send("Saved all frames in \"" + folder + "\"");
    }

    public static void savedPalette(final Path filepath) {
        send("Saved palette to \"" + filepath + "\"");
    }

    public static void openFailed(final Path filepath) {
        send("Couldn't open file \"" + filepath + "\"");
    }
}
