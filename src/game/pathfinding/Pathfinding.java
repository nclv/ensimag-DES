package game.pathfinding;

import java.util.LinkedList;

import game.robots.Robot;

public abstract class Pathfinding {
    public abstract LinkedList<Integer> shortestWay(Robot robot, int src, int dest) throws IllegalStateException;
}
