package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.Simulateur;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;

public class TestGUI implements InterfaceDonneesSimulation {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        EventManager eventManager = new EventManager(donneesSimulation);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );

        GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        new Simulateur(graphicsComponent, donneesSimulation, eventManager);
    }
}

