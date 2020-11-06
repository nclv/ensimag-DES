package game.pathfinding;

import java.util.LinkedList;

import game.robots.Robot;

public abstract class Pathfinding {
    /**
     * Calcule le plus court chemin pour le robot entre src et dest
     * 
     * @param robot
     * @param src
     * @param dest
     * @return suite de positions
     * @throws IllegalStateException if there is no path
     */
    public abstract LinkedList<Integer> shortestWay(Robot robot, int src, int dest) throws IllegalStateException;
}
