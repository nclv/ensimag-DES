package strategie;

import java.util.Iterator;
import java.util.LinkedList;

import game.pathfinding.Pathfinding;
import game.Simulateur;
import game.events.EventMove;
import game.robots.Robot;

public abstract class Strategie {
    protected Pathfinding pathfinding;
    protected long count = 0;

    public Strategie(Pathfinding pathfinding) {
        this.pathfinding = pathfinding;
    }

    public abstract void execute(Simulateur simulateur);
    public abstract Boolean canFree(Robot robot);

    public void addEventsMove(Simulateur simulateur, Robot robot, LinkedList<Integer> path) {
        Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            simulateur.addEvent(new EventMove(this.count, simulateur.getDonneesSimulation(), robot, simulateur.getDonneesSimulation().getCarte().getDirection(currentPosition, nextPosition)));
            count += Simulateur.INCREMENT;
            currentPosition = nextPosition;
        }
    }

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
