package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.Pathfinding;
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

        /* Initialisation de l'algorithme de pathfinding */
        Pathfinding pathfinding = new Pathfinding(donneesSimulation);
        Strategie strategie = new StrategieElementaire(pathfinding);

        Simulateur simulateur = new Simulateur(graphicsComponent, donneesSimulation, strategie);
    }
}
