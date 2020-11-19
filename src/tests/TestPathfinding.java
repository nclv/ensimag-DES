package tests;

import java.util.Iterator;
import java.util.LinkedList;

import game.DonneesSimulation;
import game.pathfinding.AStar;
import game.pathfinding.Pathfinding;
import game.robots.Robot;

public class TestPathfinding {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        final DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        final Pathfinding pathfinding = new AStar(donneesSimulation);

        /* Calcul du plus court chemin (robot, src, dest) */
        final Robot robot = donneesSimulation.getRobot(0);
        final LinkedList<Integer> path = pathfinding.shortestWay(robot, robot.getPosition(), 3);

        /* Affichage du path */
        System.out.println("Affichage du chemin:");
        final Iterator<Integer> iter = path.iterator();
        while (iter.hasNext()) {
            final Integer element = iter.next();
            System.out.println("(" + String.valueOf(element / donneesSimulation.getCarte().getNbColonnes()) + "," + String.valueOf(element % donneesSimulation.getCarte().getNbLignes()) + ")");
        }
    }
}
