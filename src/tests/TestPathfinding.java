package tests;

import java.util.Iterator;
import java.util.LinkedList;

import game.DonneesSimulation;
import game.Pathfinding;
import game.robots.Robot;

public class TestPathfinding {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        Pathfinding pathfinding = new Pathfinding(donneesSimulation);

        /* Calcul du plus court chemin (robot, src, dest) */
        Robot robot = donneesSimulation.getRobot(0);
        LinkedList<Integer> path = pathfinding.shortestWay(robot, robot.getPosition(), 3);

        /* Affichage du path */
        System.out.println("Affichage du chemin:");
        Iterator<Integer> iter = path.iterator();
        while (iter.hasNext()) {
            Integer element = iter.next();
            System.out.println("(" + String.valueOf(element / donneesSimulation.getCarte().getNbColonnes()) + "," + String.valueOf(element % donneesSimulation.getCarte().getNbLignes()) + ")");
        }
    }
}
