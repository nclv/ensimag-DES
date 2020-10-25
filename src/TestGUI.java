import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import gui.GUISimulator;
import gui.ImageElement;
import gui.Simulable;
import io.LecteurDonnees;

public class TestGUI {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);

        int sizeFactor = 60;  // à adpater à son écran
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * sizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * sizeFactor, 
            Color.BLACK
        );

        Simulateur simulateur = new Simulateur(gui, sizeFactor, donneesSimulation);
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

// ImageObserver is an interface
class Simulateur implements Simulable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LecteurDonnees.class);

    private GUISimulator gui;
    private int sizeFactor;
    private DonneesSimulation donneesSimulation;


    static final EnumMap<NatureTerrain, String> ressourcesMap = new EnumMap<NatureTerrain, String>(Map.of(
        NatureTerrain.EAU, "eau.png", 
        NatureTerrain.FORET, "foret.png", 
        NatureTerrain.HABITAT, "habitat.png",
        NatureTerrain.ROCHE, "roche.png", 
        NatureTerrain.TERRAIN_LIBRE, "terrain_libre.png"
        )
    );

    /**
     * Crée un Simulateur et le dessine.
     * 
     * @param gui   l'interface graphique associée, dans laquelle se fera le dessin
     *              et qui enverra les messages via les méthodes héritées de
     *              Simulable.
     * @param color la couleur du simulateur
     */
    public Simulateur(GUISimulator gui, int sizeFactor, DonneesSimulation donneesSimulation) {
        this.gui = gui;
        LOGGER.info("GUI de dimensions {}x{}", gui.getPanelHeight(), gui.getPanelWidth());

        gui.setSimulable(this);

        this.sizeFactor = sizeFactor;
        this.donneesSimulation = donneesSimulation;

        drawMap();
    }

    private void drawMap() {
        gui.reset(); // clear the window

        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();
        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();

        for (Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            int x = tile.getKey() % nbLignes;
            int y = tile.getKey() / nbLignes;

            LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y, tile.getValue());

            gui.addGraphicalElement(new ImageElement(
                x * this.sizeFactor, 
                y * this.sizeFactor,
                "src\\ressources\\" + ressourcesMap.get(tile.getValue()), 
                this.sizeFactor, this.sizeFactor, 
                null)
            );
        }

    }

    @Override
    public void next() {
        // TODO Auto-generated method stub

    }

    @Override
    public void restart() {
        // TODO Auto-generated method stub

    }
}