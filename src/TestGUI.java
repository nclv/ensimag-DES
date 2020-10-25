import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.Robot;
import gui.GUISimulator;
import gui.GraphicalElement;
import gui.ImageElement;
import gui.Simulable;
import io.LecteurDonnees;

public class TestGUI {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);

        int guiSizeFactor = 60;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );

        Simulateur simulateur = new Simulateur(gui, guiSizeFactor, donneesSimulation);
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
    private int guiSizeFactor;
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
    public Simulateur(GUISimulator gui, int guiSizeFactor, DonneesSimulation donneesSimulation) {
        this.gui = gui;
        LOGGER.info("GUI de dimensions {}x{}", gui.getPanelHeight(), gui.getPanelWidth());

        gui.setSimulable(this);

        this.guiSizeFactor = guiSizeFactor;
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
                x * this.guiSizeFactor, 
                y * this.guiSizeFactor,
                "src\\ressources\\" + ressourcesMap.get(tile.getValue()), 
                this.guiSizeFactor, this.guiSizeFactor, 
                null)
            );
        }

        // les incendies
        Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();

        // on va normaliser les intensités des incendies pour afficher des incendies de taille différentes
        Collection<Integer> intensites = incendies.values();
        int minIntensity = Collections.min(intensites);
        int maxIntensity = Collections.max(intensites);
        NormUtil normUtil = new NormUtil(maxIntensity, minIntensity, this.guiSizeFactor / 1.5, this.guiSizeFactor / 6);

        for (Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            int x = fire.getKey() % nbLignes;
            int y = fire.getKey() / nbLignes;

            // on veut dessiner un feu sur les cases
            LOGGER.info("Case de type {} ", ressourcesMap.get(map.get(fire.getKey())));
            String tileFilename = ressourcesMap.get(map.get(fire.getKey()));

            int intensite = fire.getValue();
            int normalizedIntensity = (int)normUtil.normalize(intensite);
            LOGGER.info("Intensite de l'incendie: {}, {}", intensite, normalizedIntensity);

            gui.addGraphicalElement(new FireImg(x * this.guiSizeFactor, y * this.guiSizeFactor, normalizedIntensity, this.guiSizeFactor, "src\\ressources\\" + tileFilename));
        }

        // les robots
        Map<Integer, ArrayList<Robot>> robotsMap = this.donneesSimulation.getRobots();
        for (Map.Entry<Integer, ArrayList<Robot>> robots : robotsMap.entrySet()) {
            int x = robots.getKey() % nbLignes;
            int y = robots.getKey() / nbLignes;

            // on veut dessiner un ou plusieurs robot(s) sur les cases, on va faire un quadrillage
            ArrayList<Robot> robotsList = robots.getValue();
            int robotsCount = robotsList.size();
            for (Robot robot: robotsList) {

            }
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
    private int normalizedIntensity;

    private int imgSize;
    private int fireSize;
    
    private static final int fireSizeFactor = 2; // on divise la taille du feu par deux par rapport à la taille de la case

    public FireImg(int x, int y, int normalizedIntensity, int imgSize, String imgFilename) {
        LOGGER.info("Création d'une image feu en ({}, {}) d'intensite {} sur une case de type {} de taille {}", x, y, normalizedIntensity, imgFilename, imgSize);
        try {
            this.image = ImageIO.read(new File(imgFilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.x = x;
        this.y = y;
        this.normalizedIntensity = normalizedIntensity;
        this.imgSize = imgSize;
        this.fireSize = imgSize / fireSizeFactor;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.image != null) {
            g2d.drawImage(this.image, this.x, this.y, this.imgSize, this.imgSize, null);
        }
        // on dessine le feu
        g2d.setColor(Color.RED);
        int padding = (this.imgSize - this.normalizedIntensity) / 2 ; // on veut un feu au milieu de la case
        g2d.fillOval(this.x + padding, this.y + padding, this.normalizedIntensity, this.normalizedIntensity);
    }
}

class NormUtil {
    private double dataHigh;
    private double dataLow;
    private double normalizedHigh;
    private double normalizedLow;
    
    /**
     * Construct the normalization utility, allow the normalization range to be specified.
     * @param dataHigh The high value for the input data.
     * @param dataLow The low value for the input data.
     * @param dataHigh The high value for the normalized data.
     * @param dataLow The low value for the normalized data. 
     */
    public NormUtil(double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        this.dataHigh = dataHigh;
        this.dataLow = dataLow;
        this.normalizedHigh = normalizedHigh;
        this.normalizedLow = normalizedLow;
    }

    /**
     * Normalize x.
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(double x) {
        return ((x - dataLow) 
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
}