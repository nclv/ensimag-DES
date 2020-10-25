import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import gui.GUISimulator;
import gui.GraphicalElement;
import gui.ImageElement;
import gui.Simulable;
import io.LecteurDonnees;

public class TestGUI {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);

        int sizeFactor = 20;  // à adapter à son écran, spiral: 20, others: 60
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulateur.class);

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

        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();

        // le terrain
        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();
        for (Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            int x = tile.getKey() % nbLignes;
            int y = tile.getKey() / nbLignes;

            // LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y, tile.getValue());

            gui.addGraphicalElement(new ImageElement(
                x * this.sizeFactor, 
                y * this.sizeFactor,
                "src\\ressources\\" + ressourcesMap.get(tile.getValue()), 
                this.sizeFactor, this.sizeFactor, 
                null)
            );
        }

        // les incendies
        Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();
        for (Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            int x = fire.getKey() % nbLignes;
            int y = fire.getKey() / nbLignes;

            // on veut dessiner sur les cases
            LOGGER.info("Case de type {} ", ressourcesMap.get(map.get(fire.getKey())));
            String tileFilename = ressourcesMap.get(map.get(fire.getKey()));
            gui.addGraphicalElement(new FireImg(x * this.sizeFactor, y * this.sizeFactor, this.sizeFactor, "src\\ressources\\" + tileFilename));
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

class FireImg implements GraphicalElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(FireImg.class);
    private BufferedImage image;
    private int x;
    private int y;
    private int size;

    public FireImg(int x, int y, int size, String imgFilename) {
        LOGGER.info("Création d'une image feu en ({}, {}) de taille {} sur une case de type {}", x, y, size, imgFilename);
        try {
            this.image = ImageIO.read(new File(imgFilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.image != null) {
            g2d.drawImage(this.image, this.x, this.y, this.size, this.size, null);
        }
        // on dessine le feu
        // Oval oval = new Oval(this.x + this.size / 2, this.y + this.size / 2, Color.RED, Color.RED, this.size);
        // oval.paint(g2d);
        g2d.setColor(Color.RED);
        int relSizeFactor = 2; // on divise la taille du feu par deux par rapport à la taille de la case
        g2d.fillOval(this.x + (this.size / 2) / relSizeFactor, this.y + (this.size / 2) / relSizeFactor, this.size / relSizeFactor, this.size / relSizeFactor);
    }
}