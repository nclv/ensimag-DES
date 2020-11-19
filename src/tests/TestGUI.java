package tests;

import java.awt.Color;

import game.DonneesSimulation;
import game.Simulateur;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import gui.GUISimulator;

public class TestGUI implements InterfaceDonneesSimulation {
    public static void main(final String[] args) {
        final DonneesSimulation donneesSimulation = InterfaceDonneesSimulation.getDonneesSimulation(args);
        final EventManager eventManager = new EventManager(donneesSimulation);

        final int guiSizeFactor = 80; // à adapter à son écran, spiral: 20, others: 60
        final GUISimulator gui = new GUISimulator(donneesSimulation.getCarte().getNbLignes() * guiSizeFactor,
                donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, Color.BLACK);

        final GraphicsComponent graphicsComponent = new GraphicsComponent(gui, guiSizeFactor, donneesSimulation);

        new Simulateur(graphicsComponent, donneesSimulation, eventManager);
    }
}

