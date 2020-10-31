package tests;

import java.awt.Color;

import game.Direction;
import game.DonneesSimulation;
import game.Simulateur;
import game.events.EventMove;
import game.graphics.GraphicsComponent;
import game.robots.Robot;
import gui.GUISimulator;

public class TestEventsScenario0 implements InterfaceDonneesSimulation {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );

        GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        Simulateur simulateur = new Simulateur(graphicsComponent, donneesSimulation);

        Robot robot = donneesSimulation.getRobots().get(3 * donneesSimulation.getCarte().getNbLignes() + 3).get(0);

        long count = 0;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.NORD));
        count += Simulateur.INCREMENT;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.NORD));
        count += Simulateur.INCREMENT;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.NORD));
        count += Simulateur.INCREMENT;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.NORD));
    }
}
