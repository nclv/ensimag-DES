package strategie;

import game.Simulateur;
import game.pathfinding.Pathfinding;
import game.robots.Robot;

public abstract class Strategie {
    // algorithme de plus court chemin
    protected Pathfinding pathfinding;
    // compteur interne permettant d'ordonner une suite d'events
    // utilisé pour effectuer les actions des robots en série
    protected long date = 0;

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
