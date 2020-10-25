import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

import game.DonneesSimulation;
import gui.GUISimulator;
import gui.Rectangle;
import gui.Simulable;
import io.LecteurDonnees;

public class TestGUI {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);
        GUISimulator gui = new GUISimulator(donneesSimulation.getCarte().getNbLignes(), donneesSimulation.getCarte().getNbColonnes(), Color.BLACK);
        Simulateur simulateur = new Simulateur(gui, Color.decode("#f2ff28"), donneesSimulation);
    }

    public static DonneesSimulation getDonneesSimulation(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntaxe: java TestLecteurDonnees <nomDeFichier>");
            System.exit(1);
        }
        DonneesSimulation donneesSimulation = null;
        try {
            donneesSimulation = LecteurDonnees.lire(args[0]);
            // System.out.println(donneesSimulation);
        } catch (FileNotFoundException e) {
            System.out.println("fichier " + args[0] + " inconnu ou illisible");
        } catch (DataFormatException e) {
            System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
        }
        return donneesSimulation;
    }
}

class Simulateur implements Simulable {
    private GUISimulator gui;
    private Color simulateurColor;
    private DonneesSimulation donneesSimulation;

    /**
     * Crée un Simulateur et le dessine.
     * 
     * @param gui   l'interface graphique associée, dans laquelle se fera le dessin
     *              et qui enverra les messages via les méthodes héritées de
     *              Simulable.
     * @param color la couleur du simulateur
     */
    public Simulateur(GUISimulator gui, Color simulateurColor, DonneesSimulation donneesSimulation) {
        this.gui = gui;
        gui.setSimulable(this);

        this.simulateurColor = simulateurColor;
        this.donneesSimulation = donneesSimulation;

        drawMap();
    }

    private void drawMap() {
        gui.reset(); // clear the window

        gui.addGraphicalElement(new Rectangle(x + 30, y, invaderColor, invaderColor, 10));
    }
}