import java.util.Objects;

public class Coordinate {
    private final int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public boolean equals(Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check and type check
        // instances of the type and its subtypes can never equal.
        if (obj == null || getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj; // cast
        return Objects.equals(x, other.x) && Objects.equals(y, other.y);
    }
}
