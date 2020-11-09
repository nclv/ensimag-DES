package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.pathfinding.AStar;
import game.Simulateur;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;
import strategie.Strategie;
import strategie.StrategieEvoluee;

public class TestStrategieEvoluee {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        Strategie strategie = new StrategieEvoluee(new AStar(donneesSimulation));
        EventManager eventManager = new EventManager(donneesSimulation, strategie);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );
        GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        new Simulateur(graphicsComponent, donneesSimulation, eventManager, strategie);
    }
}
