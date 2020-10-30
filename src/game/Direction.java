package game;

public enum Direction {
    EST(1, 0),
    NORD(0, -1),
    SUD(0, 1),
    OUEST(-1, 0);

    private final int dy;
    private final int dx;

    Direction (final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDy() {
        return dy;
    }

    public int getDx() {
        return dx;
    }

    public Direction next() {
        return values()[(ordinal() + 1) % values().length];
    }
}