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
import game.robots.MyRobotTypes;
import game.robots.Robot;
import gui.GUISimulator;
import gui.GraphicalElement;
import gui.ImageElement;
import gui.Simulable;
import io.LecteurDonnees;

public class TestGUI {
    public static void main(String[] args) {
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
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

    static final EnumMap<MyRobotTypes.Type, String> ressourcesRobots = new EnumMap<MyRobotTypes.Type, String>(Map.of(
        MyRobotTypes.Type.DRONE, "drone.png", 
        MyRobotTypes.Type.ROUES, "roues.png", 
        MyRobotTypes.Type.CHENILLES, "chenilles.png",
        MyRobotTypes.Type.PATTES, "pattes.png"
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

        initMap();
    }

    private void initMap() {
        gui.reset(); // clear the window

        drawTerrain();
        drawIncendies();
        drawRobots();
    }

    private void drawTerrain() {
        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();
        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();
        for (Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            int position = tile.getKey();
            int x = position % nbLignes;
            int y = position / nbLignes;

            // LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y, tile.getValue());

            gui.addGraphicalElement(new ImageElement(
                x * this.guiSizeFactor, 
                y * this.guiSizeFactor,
                "src\\ressources\\" + ressourcesMap.get(tile.getValue()), 
                this.guiSizeFactor, this.guiSizeFactor, 
                null)
            );
        }
    }

    private void drawIncendies() {
        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();
        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();

        Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();

        // on va normaliser les intensités des incendies pour afficher des incendies de taille différentes
        Collection<Integer> intensites = incendies.values();
        int minIntensity = Collections.min(intensites);
        int maxIntensity = Collections.max(intensites);
        // on peut modifier les deux derniers arguments, attention à garder le feu visible et ne remplissant pas toute la case
        NormUtil normUtil = new NormUtil(maxIntensity, minIntensity, this.guiSizeFactor / 1.5, this.guiSizeFactor / 6); 

        for (Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            int position = fire.getKey();
            int x = position % nbLignes;
            int y = position / nbLignes;

            // on veut dessiner un feu sur les cases
            LOGGER.info("Case de type {} ", ressourcesMap.get(map.get(position)));
            String tileFilename = ressourcesMap.get(map.get(position));

            int intensite = fire.getValue();
            int normalizedIntensity = (int)normUtil.normalize(intensite);
            LOGGER.info("Intensite de l'incendie: {}, {}", intensite, normalizedIntensity);

            gui.addGraphicalElement(new FireImg(x * this.guiSizeFactor, y * this.guiSizeFactor, normalizedIntensity, this.guiSizeFactor, "src\\ressources\\" + tileFilename));
        }
    }

    private void drawRobots() {
        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();
        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();

        Map<Integer, ArrayList<Robot>> robotsMap = this.donneesSimulation.getRobots();
        for (Map.Entry<Integer, ArrayList<Robot>> robots : robotsMap.entrySet()) {
            int position = robots.getKey();
            int x = position % nbLignes;
            int y = position / nbLignes;

            LOGGER.info("Case de type {} ", ressourcesMap.get(map.get(position)));
            String tileFilename = ressourcesMap.get(map.get(position));

            // on veut dessiner un ou plusieurs robot(s) sur les cases,
            // TODO: gérer l'affchage de plusieurs robots sur la même case
            ArrayList<Robot> robotsList = robots.getValue();
            for (Robot robot: robotsList) {
                LOGGER.info("Robot de type {} ", ressourcesRobots.get(robot.getType()));
                String robotFilename = ressourcesRobots.get(robot.getType());
                gui.addGraphicalElement(new RobotImg(
                    x * this.guiSizeFactor, 
                    y * this.guiSizeFactor, 
                    this.guiSizeFactor, 
                    "src\\ressources\\" + tileFilename, 
                    "src\\ressources\\" + robotFilename
                    )
                );
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
    
    private BufferedImage tileImg = null;

    private int x;
    private int y;
    private int normalizedIntensity;

    private int tileImgSize;
    
    // private static final int fireSizeFactor = 2; // on divise la taille du feu par deux par rapport à la taille de la case

    public FireImg(int x, int y, int normalizedIntensity, int tileImgSize, String imgFilename) {
        LOGGER.info("Création d'une image feu en ({}, {}) d'intensite {} sur une case de taille {}", x, y, normalizedIntensity, tileImgSize);
        try {
            this.tileImg = ImageIO.read(new File(imgFilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.x = x;
        this.y = y;
        this.normalizedIntensity = normalizedIntensity;
        this.tileImgSize = tileImgSize;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.tileImg != null) {
            g2d.drawImage(this.tileImg, this.x, this.y, this.tileImgSize, this.tileImgSize, null);
        }
        // on dessine le feu
        g2d.setColor(Color.RED);
        int padding = (this.tileImgSize - this.normalizedIntensity) / 2 ; // on veut un feu au milieu de la case
        g2d.fillOval(this.x + padding, this.y + padding, this.normalizedIntensity, this.normalizedIntensity);
    }
}

class RobotImg implements GraphicalElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotImg.class);
    
    private BufferedImage tileImg = null;
    private BufferedImage robotImg = null;

    private int x;
    private int y;

    private int tileImgSize;

    public RobotImg(int x, int y, int tileImgSize, String tileImgFilename, String robotImgFilename) {
        LOGGER.info("Création d'une image robot en ({}, {}) sur une case de taille {}", x, y, tileImgSize);
        try {
            this.tileImg = ImageIO.read(new File(tileImgFilename));
            this.robotImg = ImageIO.read(new File(robotImgFilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.x = x;
        this.y = y;
        this.tileImgSize = tileImgSize;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.tileImg != null) {
            g2d.drawImage(this.tileImg, this.x, this.y, this.tileImgSize, this.tileImgSize, null);
        }
        int padding = this.tileImgSize / 8 ;
        if (this.robotImg != null) {
            g2d.drawImage(this.robotImg, this.x + padding, this.y + padding, this.tileImgSize / 4, this.tileImgSize / 4, null);
        }
        // on dessine le robot
        // g2d.setColor(Color.YELLOW);
        // coin en haut à gauche: padding = this.tileImgSize / 8, width = height = this.tileImgSize / 4
        // au milieu: padding = this.tileImgSize / 4, width = height = this.tileImgSize / 2
        // int padding = this.tileImgSize / 8 ;
        // g2d.fillRect(this.x + padding, this.y + padding, this.tileImgSize / 4, this.tileImgSize / 4);
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

