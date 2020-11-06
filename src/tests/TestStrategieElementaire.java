package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.pathfinding.AStar;
import game.Simulateur;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;
import strategie.Strategie;
import strategie.StrategieElementaire;

public class TestStrategieElementaire {
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

        Strategie strategie = new StrategieElementaire(new AStar(donneesSimulation));

        new Simulateur(graphicsComponent, donneesSimulation, strategie);
    }
}
