package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.Simulateur;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;

public class TestGUI implements InterfaceDonneesSimulation {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );

        GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        Simulateur simulateur = new Simulateur(graphicsComponent, donneesSimulation);
    }
}

