package game;

import java.util.HashMap;

/**
 * Représente les directions de déplacement possibles.
 * 
 * @author Nicolas Vincent
 */
public enum Direction {
    EST(1, 0),
    NORD(0, -1),
    SUD(0, 1),
    OUEST(-1, 0);

    private final int dy;
    private final int dx;

    /* on veut obtenir une Direction à partir d'une position et d'une position voisine */
    private static final int MULT = 2; // needs to be >= 1
    private static final HashMap<Integer, Direction> BY_RELATIVE_POSITION = new HashMap<Integer, Direction>();

    static {
        for (final Direction direction : values()) {
            BY_RELATIVE_POSITION.put(direction.getDy() * MULT + direction.getDx(), direction);
        }
    }

    private Direction(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * @param relativePosition obtenue à partir d'une position et d'une position
     *                         voisine: (ligneVoisin - ligne) * Direction.getMult()
     *                         + (colonneVoisin - colonne)
     * @return la direction correspondant à la position relative passée en argument
     */
    public static Direction getDirection(final int relativePosition) {
        return BY_RELATIVE_POSITION.get(relativePosition);
    }

    public int getDy() {
        return dy;
    }

    public int getDx() {
        return dx;
    }

    /**
     * @return multiplicative coefficient used to get corresponding direction from relative position
     */
    public static int getMult() {
        return MULT;
    }
}