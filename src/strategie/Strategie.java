package strategie;

import game.Simulateur;
import game.pathfinding.Pathfinding;
import game.robots.Robot;

public abstract class Strategie {
    // algorithme de plus court chemin
    protected Pathfinding pathfinding;
    // compteur interne permettant d'ordonner une suite d'events
    protected long count = 0;

    public Strategie(Pathfinding pathfinding) {
        this.pathfinding = pathfinding;
    }

    public abstract void execute(Simulateur simulateur);
    public abstract Boolean canFree(Robot robot);

    public Pathfinding getPathfinding() {
        return pathfinding;
    }

    public void setPathfinding(Pathfinding pathfinding) {
        this.pathfinding = pathfinding;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
