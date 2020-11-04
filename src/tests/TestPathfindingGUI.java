package tests;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import game.DonneesSimulation;
import game.Pathfinding;
import game.Simulateur;
import game.events.EventMove;
import game.graphics.GraphicsComponent;
import game.robots.Robot;
import gui.GUISimulator;

public class TestPathfindingGUI {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        Pathfinding pathfinding = new Pathfinding(donneesSimulation);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );
        GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        Simulateur simulateur = new Simulateur(graphicsComponent, donneesSimulation);

        /* Calcul du plus court chemin entre (src, dest) */
        Robot robot = donneesSimulation.getRobot(0);
        LinkedList<Integer> path = pathfinding.shortestWay(robot, robot.getPosition(), 7);

        /* Affichage du path */
        System.out.println("Affichage du chemin:");

        long count = 0;
        long increment = Simulateur.INCREMENT;

        Iterator<Integer> iter = path.iterator();
        
        int currentPosition = iter.next();
        System.out.println("(" + String.valueOf(currentPosition / donneesSimulation.getCarte().getNbColonnes()) + "," + String.valueOf(currentPosition % donneesSimulation.getCarte().getNbLignes()) + ")");
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            simulateur.addEvent(new EventMove(count, donneesSimulation, robot, donneesSimulation.getCarte().getDirection(currentPosition, nextPosition)));
            count += increment;
            System.out.println("(" + String.valueOf(nextPosition / donneesSimulation.getCarte().getNbColonnes()) + "," + String.valueOf(nextPosition % donneesSimulation.getCarte().getNbLignes()) + ")");
            currentPosition = nextPosition;
        }
    }
}
