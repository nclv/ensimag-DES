package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.pathfinding.AStar;
import game.Simulateur;
import game.events.EventAdderParallel;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;
import strategie.Strategie;
import strategie.StrategieEvoluee;

public class TestStrategieEvoluee {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        final DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        final Strategie strategie = new StrategieEvoluee(new AStar(donneesSimulation));
        final EventManager eventManager = new EventManager(donneesSimulation, strategie);
        strategie.setEventAdder(new EventAdderParallel(donneesSimulation, eventManager));

        final int guiSizeFactor = 80; // à adapter à son écran, spiral: 20, others: 60
        final GUISimulator gui = new GUISimulator(donneesSimulation.getCarte().getNbLignes() * guiSizeFactor,
                donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, Color.BLACK);
        final GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        new Simulateur(graphicsComponent, donneesSimulation, eventManager, strategie);
    }
}
