package tests;

import java.awt.Color;

import game.Direction;
import game.DonneesSimulation;
import game.Simulateur;
import game.events.ActionEmpty;
import game.events.ActionFill;
import game.events.ActionMove;
import game.events.Event;
import game.graphics.GraphicsComponent;
import game.robots.Robot;
import gui.GUISimulator;

public class TestEventsScenario01 {
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

        Robot drone = donneesSimulation.getRobot(0);
        Robot roues = donneesSimulation.getRobot(1);

        long count = 0;
        long increment = Simulateur.INCREMENT;
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, roues, Direction.NORD)));
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, drone, Direction.NORD)));
        count += increment;
        
        simulateur.addEvent(new Event(count, new ActionEmpty(donneesSimulation, roues)));
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, drone, Direction.NORD)));
        count += increment;

        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, roues, Direction.OUEST)));
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, drone, Direction.NORD)));
        count += increment;
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, roues, Direction.OUEST)));
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, drone, Direction.NORD)));
        count += increment;

        simulateur.addEvent(new Event(count, new ActionFill(donneesSimulation, roues)));
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, drone, Direction.SUD)));
        count += increment;

        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, roues, Direction.EST)));
        count += increment;
        simulateur.addEvent(new Event(count, new ActionMove(donneesSimulation, roues, Direction.EST)));
        count += increment;

        simulateur.addEvent(new Event(count, new ActionEmpty(donneesSimulation, roues)));
        count += increment;
    }
}
