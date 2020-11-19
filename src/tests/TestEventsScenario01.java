package tests;

import java.awt.Color;

import game.Direction;
import game.DonneesSimulation;
import game.Simulateur;
import game.events.ActionEmpty;
import game.events.ActionFill;
import game.events.ActionMove;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import game.robots.Robot;
import gui.GUISimulator;

public class TestEventsScenario01 {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        final DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        final EventManager eventManager = new EventManager(donneesSimulation);

        final int guiSizeFactor = 80; // à adapter à son écran, spiral: 20, others: 60
        final GUISimulator gui = new GUISimulator(donneesSimulation.getCarte().getNbLignes() * guiSizeFactor,
                donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, Color.BLACK);
        final GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        final Simulateur simulateur = new Simulateur(graphicsComponent, donneesSimulation, eventManager);

        final Robot drone = donneesSimulation.getRobot(0);
        final Robot roues = donneesSimulation.getRobot(1);

        long date = 0;
        final long increment = Simulateur.INCREMENT;
        simulateur.schedule(date, new ActionMove(donneesSimulation, roues, Direction.NORD));
        simulateur.schedule(date, new ActionMove(donneesSimulation, drone, Direction.NORD));
        date += increment;
        
        simulateur.schedule(date, new ActionEmpty(donneesSimulation, roues));
        simulateur.schedule(date, new ActionMove(donneesSimulation, drone, Direction.NORD));
        date += increment;

        simulateur.schedule(date, new ActionMove(donneesSimulation, roues, Direction.OUEST));
        simulateur.schedule(date, new ActionMove(donneesSimulation, drone, Direction.NORD));
        date += increment;
        simulateur.schedule(date, new ActionMove(donneesSimulation, roues, Direction.OUEST));
        simulateur.schedule(date, new ActionMove(donneesSimulation, drone, Direction.NORD));
        date += increment;

        simulateur.schedule(date, new ActionFill(donneesSimulation, roues));
        simulateur.schedule(date, new ActionMove(donneesSimulation, drone, Direction.SUD));
        date += increment;

        simulateur.schedule(date, new ActionMove(donneesSimulation, roues, Direction.EST));
        date += increment;
        simulateur.schedule(date, new ActionMove(donneesSimulation, roues, Direction.EST));
        date += increment;

        simulateur.schedule(date, new ActionEmpty(donneesSimulation, roues));
        date += increment;
    }
}
