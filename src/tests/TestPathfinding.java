package tests;

import java.util.Iterator;
import java.util.LinkedList;

import game.DonneesSimulation;
import game.Pathfinding;

public class TestPathfinding {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);

        /* Génération du robot */
        Pathfinding pathfinding = new Pathfinding(donneesSimulation.getCarte());

        /* Calcul du plus court chemin entre (src, dest) */
        LinkedList<Integer> path = pathfinding.shortestWay(0, 27);

        /* Affichage du path */
        System.out.println("Affichage du chemin:");
        Iterator<Integer> iter = path.iterator();
        while (iter.hasNext()) {
            Integer element = iter.next();
            System.out.println("(" + String.valueOf(element / donneesSimulation.getCarte().getNbColonnes()) + "," + String.valueOf(element % donneesSimulation.getCarte().getNbLignes()) + ")");
        }
    }
}
