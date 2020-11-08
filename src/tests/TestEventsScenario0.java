package tests;

import java.awt.Color;

import game.Direction;
import game.DonneesSimulation;
import game.Simulateur;
import game.events.ActionMove;
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

        Robot robot = donneesSimulation.getRobot(0);

        long date = 0;
        simulateur.schedule(date, new ActionMove(donneesSimulation, robot, Direction.NORD));
        date += Simulateur.INCREMENT;
        simulateur.schedule(date, new ActionMove(donneesSimulation, robot, Direction.NORD));
        date += Simulateur.INCREMENT;
        simulateur.schedule(date, new ActionMove(donneesSimulation, robot, Direction.NORD));
        date += Simulateur.INCREMENT;
        simulateur.schedule(date, new ActionMove(donneesSimulation, robot, Direction.NORD));
    }
}

