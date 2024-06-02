package com.jordanbunke.stipple_effect.tools;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.selection.SelectionUtils;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.math.Geometry;
import com.jordanbunke.stipple_effect.utility.settings.Settings;

import java.util.HashSet;
import java.util.Set;

public sealed abstract class MoverTool<T> extends Tool implements SnappableTool
        permits MoveSelection, PickUpSelection {
    public enum TransformType {
        NONE, MOVE, STRETCH, ROTATE
    }

    public enum Direction {
        R, BR, B, BL, L, TL, T, TR, NA;

        double angle() {
            if (this == NA)
                return Double.MAX_VALUE;

            return ordinal() * Constants._45_SNAP_INC;
        }
    }

    private boolean snap = false, snapToggled = false;

    private TransformType transformType, prospectiveType;
    private Direction direction;
    private Set<Coord2D> startSelection;
    private T transformation;
    private Coord2D startMousePosition, lastMousePosition,
            startTopLeft, startBottomRight, startTP, lastTP;
    private GameImage toolContentPreview;

    public MoverTool() {
        transformType = TransformType.NONE;
        prospectiveType = TransformType.NONE;
        direction = Direction.NA;

        startSelection = new HashSet<>();
        transformation = null;

        startMousePosition = new Coord2D();
        lastMousePosition = new Coord2D();
        startTP = Constants.NO_VALID_TARGET;
        lastTP = Constants.NO_VALID_TARGET;
        startTopLeft = Constants.NO_VALID_TARGET;
        startBottomRight = Constants.NO_VALID_TARGET;

        toolContentPreview = GameImage.dummy();
    }

    abstract boolean canBeMoved(final SEContext context);
    abstract T move(final SEContext context, final Coord2D displacement);
    abstract T stretch(
            final SEContext context, final Set<Coord2D> initial,
            final Coord2D change, final Direction direction
    );
    abstract T rotate(
            final SEContext context, final Set<Coord2D> initial,
            final double deltaR, final Coord2D pivot, final boolean[] offset
    );
    abstract void applyTransformation(
            final SEContext context, final T transformation, final boolean transform
    );
    abstract GameImage updateToolContentPreview(
            final SEContext context, final T transformation
    );

    public final void applyMove(final SEContext context, final Coord2D displacement) {
        applyTransformation(context, move(context, displacement), false);
    }

    public TransformType determineTransformType(
            final SEContext context
    ) {
        if (!context.getState().hasSelection())
            return TransformType.NONE;
        else {
            final Set<Coord2D> selection = context.getState().getSelection();

            startTopLeft = SelectionUtils.topLeft(selection);
            startBottomRight = SelectionUtils.bottomRight(selection);

            final Coord2D tp = context.getTargetPixel();

            if (tp.equals(Constants.NO_VALID_TARGET))
                return TransformType.MOVE;

            final int left = startTopLeft.x, right = startBottomRight.x,
                    top = startTopLeft.y, bottom = startBottomRight.y;

            final int leftProx = Math.abs(left - tp.x),
                    rightProx = Math.abs(right - tp.x),
                    topProx = Math.abs(top - tp.y),
                    bottomProx = Math.abs(bottom - tp.y);

            boolean atLeft = leftProx <= Constants.STRETCH_PX_THRESHOLD,
                    atRight = rightProx <= Constants.STRETCH_PX_THRESHOLD,
                    atTop = topProx <= Constants.STRETCH_PX_THRESHOLD,
                    atBottom = bottomProx <= Constants.STRETCH_PX_THRESHOLD;

            if (atLeft) {
                if (atTop) {
                    direction = Direction.TL;
                    return TransformType.STRETCH;
                } else if (atBottom) {
                    direction = Direction.BL;
                    return TransformType.STRETCH;
                } else if (tp.y > top && tp.y < bottom) {
                    direction = Direction.L;
                    return TransformType.STRETCH;
                }
            } else if (atRight) {
                if (atTop) {
                    direction = Direction.TR;
                    return TransformType.STRETCH;
                } else if (atBottom) {
                    direction = Direction.BR;
                    return TransformType.STRETCH;
                } else if (tp.y > top && tp.y < bottom) {
                    direction = Direction.R;
                    return TransformType.STRETCH;
                }
            } else if (atTop && tp.x > left && tp.x < right) {
                direction = Direction.T;
                return TransformType.STRETCH;
            } else if (atBottom && tp.x > left && tp.x < right) {
                direction = Direction.B;
                return TransformType.STRETCH;
            }

            atLeft = tp.x < left && leftProx <= Constants.ROTATE_PX_THRESHOLD;
            atRight = tp.x > right && rightProx <= Constants.ROTATE_PX_THRESHOLD;
            atTop = tp.y < top && topProx <= Constants.ROTATE_PX_THRESHOLD;
            atBottom = tp.y > bottom && bottomProx <= Constants.ROTATE_PX_THRESHOLD;

            final int middleX = left + ((right - left) / 2),
                    middleY = top + ((bottom - top) / 2);
            final boolean atMiddleX = Math.abs(tp.x - middleX) < Constants.ROTATE_PX_THRESHOLD,
                    atMiddleY = Math.abs(tp.y - middleY) < Constants.ROTATE_PX_THRESHOLD;

            if (atLeft || atRight) {
                if (atTop) {
                    direction = atLeft ? Direction.TL : Direction.TR;
                    return TransformType.ROTATE;
                } else if (atBottom) {
                    direction = atLeft ? Direction.BL : Direction.BR;
                    return TransformType.ROTATE;
                } else if (atMiddleY) {
                    direction = atLeft ? Direction.L : Direction.R;
                    return TransformType.ROTATE;
                }
            } else if (atTop && atMiddleX) {
                direction = Direction.T;
                return TransformType.ROTATE;
            } else if (atBottom && atMiddleX) {
                direction = Direction.B;
                return TransformType.ROTATE;
            }

            direction = Direction.NA;
            return TransformType.MOVE;
        }
    }

    @Override
    public String getCursorCode() {
        final TransformType relevantType = transformType == TransformType.NONE
                ? prospectiveType : transformType;

        final String suffix = switch (relevantType) {
            case ROTATE -> switch (direction) {
                case R, NA -> "_rotright";
                case L -> "_rotleft";
                case T -> "_rottop";
                case B -> "_rotbottom";
                case TL -> "_rottl";
                case TR -> "_rottr";
                case BL -> "_rotbl";
                case BR -> "_rotbr";
            };
            case STRETCH -> switch (direction) {
                case NA -> "";
                case B, T -> "_vert";
                case L, R -> "_horz";
                case TL, BR -> "_diag_tl";
                case BL, TR -> "_diag_bl";
            };
            default -> "";
        };

        return super.getCursorCode() + suffix;
    }

    @Override
    public void onMouseDown(final SEContext context, final GameMouseEvent me) {
        transformType = determineTransformType(context);
        prospectiveType = TransformType.NONE;

        if (canBeMoved(context)) {
            startSelection = new HashSet<>(context.getState().getSelection());
            transformation = move(context, new Coord2D());
            toolContentPreview = updateToolContentPreview(context, transformation);

            startMousePosition = me.mousePosition;
            lastMousePosition = me.mousePosition;
            startTP = context.getTargetPixel();
            lastTP = startTP;
        }
    }

    @Override
    public void update(final SEContext context, final Coord2D mousePosition) {
        final float zoomFactor = context.renderInfo.getZoomFactor();

        if (mousePosition.equals(lastMousePosition))
            return;

        if (isMoving() && !context.getState().hasSelection())
            transformType = TransformType.NONE;

        switch (transformType) {
            case NONE -> prospectiveType = determineTransformType(context);
            case MOVE -> {
                final Set<Coord2D> selection = context.getState().getSelection();
                final Coord2D topLeft = SelectionUtils.topLeft(selection);

                Coord2D displacement = new Coord2D(
                        -(int)((startMousePosition.x - mousePosition.x) / zoomFactor),
                        -(int)((startMousePosition.y - mousePosition.y) / zoomFactor)
                ).displace(startTopLeft.x - topLeft.x, startTopLeft.y - topLeft.y);

                if (isSnap()) {
                    if (!snapToggled) {
                        // displace in multiples of own bounds
                        final Bounds2D bounds = SelectionUtils.bounds(selection);

                        final int snappedX = bounds.width() * (int)Math.round(
                                displacement.x / (double) bounds.width()),
                                snappedY = bounds.height() * (int)Math.round(
                                        displacement.y / (double) bounds.height());
                        displacement = new Coord2D(snappedX, snappedY);
                    } else if (canSnapToGrid(context)) {
                        // snap to top left of pixel grid
                        final int px = Settings.getPixelGridXPixels(),
                                py = Settings.getPixelGridYPixels();

                        final Coord2D tp = context.getTargetPixel(),
                                gridPos = new Coord2D(
                                        ((tp.x / px) - (tp.x < 0 ? 1 : 0)) * px,
                                        ((tp.y / py) - (tp.y < 0 ? 1 : 0)) * py);
                        displacement = new Coord2D(
                                gridPos.x - topLeft.x, gridPos.y - topLeft.y);
                    }
                }

                transformation = move(context, displacement);
                toolContentPreview = updateToolContentPreview(context, transformation);
            }
            case STRETCH -> {
                final Coord2D delta = new Coord2D(
                        (int)((mousePosition.x - startMousePosition.x) / zoomFactor),
                        (int)((mousePosition.y - startMousePosition.y) / zoomFactor)
                );

                final Coord2D change = switch (direction) {
                    case NA -> new Coord2D();
                    case B, T -> new Coord2D(0, delta.y);
                    case L, R -> new Coord2D(delta.x, 0);
                    default -> new Coord2D(delta.x, delta.y);
                };

                transformation = stretch(context, startSelection, change, direction);
                toolContentPreview = updateToolContentPreview(context, transformation);
            }
            case ROTATE -> {
                final Coord2D tp = context.getTargetPixel();

                if (!(tp.equals(Constants.NO_VALID_TARGET) ||
                        tp.equals(startTP) || tp.equals(lastTP))) {
                    final Coord2D pivot = new Coord2D(
                            (startTopLeft.x + startBottomRight.x) / 2,
                            (startTopLeft.y + startBottomRight.y) / 2);
                    final boolean[] offset = new boolean[] {
                            (startTopLeft.x + startBottomRight.x) % 2 == 0,
                            (startTopLeft.y + startBottomRight.y) % 2 == 0
                    };
                    final double
                            initialAngle = Geometry.calculateAngleInRad(startTP, pivot),
                            angle = Geometry.calculateAngleInRad(tp, pivot),
                            candidate = ((initialAngle > angle
                                    ? Constants.CIRCLE : 0d) + angle) - initialAngle,
                            deltaR = isSnap() ? Geometry.snapAngle(
                                    candidate, Constants._45_SNAP_INC) :
                                    candidate;

                    direction = MathPlus.findBest(Direction.NA,
                            Double.MAX_VALUE, Direction::angle,
                            (a, b) -> Geometry.angleDiff(angle, a) <
                                    Geometry.angleDiff(angle, b),
                            Direction.values());

                    transformation = rotate(context,
                            startSelection, deltaR, pivot, offset);
                    toolContentPreview = updateToolContentPreview(context, transformation);

                    lastTP = tp;
                }
            }
        }

        lastMousePosition = mousePosition;
    }

    @Override
    public void onMouseUp(final SEContext context, final GameMouseEvent me) {
        if (isMoving() && transformation != null) {
            applyTransformation(context, transformation,
                    transformType != TransformType.MOVE);

            transformType = TransformType.NONE;
            me.markAsProcessed();
        }
    }

    public boolean isMoving() {
        return transformType != TransformType.NONE;
    }

    @Override
    public boolean hasToolContentPreview() {
        return isMoving();
    }

    @Override
    public GameImage getToolContentPreview() {
        return toolContentPreview;
    }

    private boolean canSnapToGrid(final SEContext context) {
        return context.renderInfo.isPixelGridOn() &&
                context.couldRenderPixelGrid();
    }

    public void setSnapToggled(final boolean snapToggled) {
        this.snapToggled = snapToggled;
    }

    @Override
    public void setSnap(final boolean snap) {
        this.snap = snap;
    }

    @Override
    public boolean isSnap() {
        return snap;
    }
}
