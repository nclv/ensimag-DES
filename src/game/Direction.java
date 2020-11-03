package game;

import java.util.HashMap;

public enum Direction {
    EST(1, 0),
    NORD(0, -1),
    SUD(0, 1),
    OUEST(-1, 0);

    private final int dy;
    private final int dx;
    private static final int MULT = 2; // needs to be >= 1 
    private static final HashMap<Integer, Direction> BY_RELATIVE_POSITION = new HashMap<Integer, Direction>();

    static {
        for (Direction direction : values()) {
            BY_RELATIVE_POSITION.put(direction.getDy() * MULT + direction.getDx(), direction);
        }
    }

    private Direction(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public static Direction getDirection(int relativePosition) {
        return BY_RELATIVE_POSITION.get(relativePosition);
    }

    public int getDy() {
        return dy;
    }

    public int getDx() {
        return dx;
    }

    public static int getMult() {
        return MULT;
    }
}