package com.jordanbunke.stipple_effect.palette;

import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stip_parser.rep.IRPalette;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.DialogVals;
import com.jordanbunke.stipple_effect.utility.math.ColorMath;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public class Palette {
    private String name;
    private final List<Color> colorSequence;
    private final Map<Color, Boolean> inclusionMap;

    public Palette(
            final String name, final Color[] loaded
    ) {
        this.name = name;
        this.colorSequence = new ArrayList<>();
        this.inclusionMap = new HashMap<>();

        for (Color c : loaded)
            if (!colorSequence.contains(c)) {
                colorSequence.add(c);
                inclusionMap.put(c, true);
            }
    }

    public static Palette realize(final IRPalette rep) {
        return new Palette(rep.name(), rep.colors());
    }

    public IRPalette convert() {
        return new IRPalette(name, getColors());
    }

    public void addColor(final Color c) {
        if (!canAdd(c))
            return;

        colorSequence.add(c);
        inclusionMap.put(c, true);
    }

    public void removeColor(final Color c) {
        if (!canRemove(c))
            return;

        colorSequence.remove(c);
        inclusionMap.remove(c);
    }

    public void moveLeft(final Color c) {
        shift(c, i -> i - 1, this::canMoveLeft);
    }

    public void moveRight(final Color c) {
        shift(c, i -> i + 1, this::canMoveRight);
    }

    private void shift(
            final Color c,
            final Function<Integer, Integer> indexUpdate,
            final Function<Color, Boolean> precondition
    ) {
        if (precondition.apply(c)) {
            final int i = colorSequence.indexOf(c);
            colorSequence.remove(c);
            colorSequence.add(indexUpdate.apply(i), c);
        }
    }

    public void moveToIndex(final Color c, final int i) {
        final int index = MathPlus.bounded(0, i, colorSequence.size());

        colorSequence.remove(c);
        colorSequence.add(index, c);
    }

    public Color nextLeft(final Color c) {
        return adjacentIncludedColor(c, -1, i -> i > 0);
    }

    public Color nextRight(final Color c) {
        return adjacentIncludedColor(c, 1, i -> i < colorSequence.size() - 1);
    }

    private Color adjacentIncludedColor(
            final Color c, final int increment,
            final Function<Integer, Boolean> boundCondition
    ) {
        if (!isIncluded(c))
            return c;

        int i = colorSequence.indexOf(c);

        while (boundCondition.apply(i)) {
            i += increment;
            final Color next = colorSequence.get(i);
            if (isIncluded(next))
                return next;
        }

        return c;
    }

    public void toggleInclusion(final Color c) {
        if (!colorSequence.contains(c))
            return;

        inclusionMap.put(c, !inclusionMap.get(c));
    }

    public void sort() {
        colorSequence.sort(DialogVals.getPaletteSorter().getComparator());

        if (DialogVals.isSortPaletteBackwards())
            Collections.reverse(colorSequence);
    }

    public Color palettize(
            final Color source
    ) {
        if (colorSequence.isEmpty())
            return source;

        final int max = Constants.RGBA_SCALE;
        final Color worst = new Color(
                (source.getRed() + (max / 2)) % max,
                (source.getGreen() + (max / 2)) % max,
                (source.getBlue() + (max / 2)) % max,
                (source.getAlpha() + (max / 2)) % max);

        return MathPlus.findBest(source, worst, c -> c,
                (c1, c2) -> ColorMath.diff(c1, source) <
                        ColorMath.diff(c2, source),
                colorSequence.stream().filter(this::isIncluded)
                        .toArray(Color[]::new));
    }

    public boolean canAdd(final Color candidate) {
        return !colorSequence.contains(candidate);
    }

    public boolean canRemove(final Color candidate) {
        return colorSequence.contains(candidate);
    }

    public boolean canMoveLeft(final Color candidate) {
        return colorSequence.indexOf(candidate) > 0;
    }

    public boolean canMoveRight(final Color candidate) {
        return colorSequence.contains(candidate) &&
                colorSequence.indexOf(candidate) < colorSequence.size() - 1;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int size() {
        return colorSequence.size();
    }

    public Color[] getColors() {
        return colorSequence.toArray(Color[]::new);
    }

    public String getName() {
        return name;
    }

    public boolean isIncluded(final Color c) {
        return inclusionMap.getOrDefault(c, false);
    }

    public boolean containsColor(final Color c) {
        return colorSequence.contains(c);
    }
}
