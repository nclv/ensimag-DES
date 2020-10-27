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
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.MyRobotTypes;
import game.robots.Robot;
import game.Event;
import gui.GUISimulator;
import gui.GraphicalElement;
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

    private long currentDate;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

    private ArrayList<TileImg> tileImgsArray = new ArrayList<TileImg>();

    private static final EnumMap<NatureTerrain, String> ressourcesMap = new EnumMap<NatureTerrain, String>(Map.of(
        NatureTerrain.EAU, "eau.png", 
        NatureTerrain.FORET, "foret.png", 
        NatureTerrain.HABITAT, "habitat.png",
        NatureTerrain.ROCHE, "roche.png", 
        NatureTerrain.TERRAIN_LIBRE, "terrain_libre.png"
        )
    );

    private static final EnumMap<MyRobotTypes.Type, String> ressourcesRobots = new EnumMap<MyRobotTypes.Type, String>(Map.of(
        MyRobotTypes.Type.DRONE, "drone.png", 
        MyRobotTypes.Type.ROUES, "roues.png", 
        MyRobotTypes.Type.CHENILLES, "chenilles.png",
        MyRobotTypes.Type.PATTES, "pattes.png"
        )
    );

    private static HashMap<String, BufferedImage> picturesCache = new HashMap<String, BufferedImage>();

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

        // l'ordre a de l'importance, il faut remplir avant d'update
        populateTileImgsArray();
        updateAllTileImgs();

        // Ajout des composants à l'instance de JFrame
        for (TileImg tileImg : this.tileImgsArray) {
            gui.addGraphicalElement(tileImg);
        }
    }

    private void populateTileImgsArray() {
        int nbLignes = this.donneesSimulation.getCarte().getNbLignes();
        Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();

        for (Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            int position = tile.getKey();
            int x = position % nbLignes;
            int y = position / nbLignes;

            // LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y, tile.getValue());
            tileImgsArray.add(new TileImg(
                x * this.guiSizeFactor, 
                y * this.guiSizeFactor,
                this.guiSizeFactor,
                getImg(ressourcesMap.get(tile.getValue())))
            );
        }
    }

    private void updateAllTileImgs() {
        if (!this.tileImgsArray.isEmpty()) {
            setIncendies();
            setRobots();
        }
    }

    private void setIncendies() {
        Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();

        // on va normaliser les intensités des incendies pour afficher des incendies de taille différentes
        Collection<Integer> intensites = incendies.values();
        int minIntensity = Collections.min(intensites);
        int maxIntensity = Collections.max(intensites);
        // on peut modifier les deux derniers arguments, attention à garder le feu visible et ne remplissant pas toute la case
        NormUtil normUtil = new NormUtil(maxIntensity, minIntensity, this.guiSizeFactor / 1.5, this.guiSizeFactor / 6); 

        for (Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            int position = fire.getKey();

            int intensite = fire.getValue();
            int normalizedIntensity = (int)normUtil.normalize(intensite);
            LOGGER.info("Intensite de l'incendie: {}, {}", intensite, normalizedIntensity);

            tileImgsArray.get(position).setFireNormalizedIntensity(normalizedIntensity);
        }
    }

    private void setRobots() {
        Map<Integer, ArrayList<Robot>> robotsMap = this.donneesSimulation.getRobots();
        for (Map.Entry<Integer, ArrayList<Robot>> robots : robotsMap.entrySet()) {
            int position = robots.getKey();

            // on veut dessiner un ou plusieurs robot(s) sur les cases,
            // on récupère la liste des images des robots en utilisant un stream (JDK8), on pourrait aussi faire une boucle
            ArrayList<BufferedImage> robotsImgsList = robots.getValue().stream().map((robot) -> getImg(ressourcesRobots.get(robot.getType()))).collect(Collectors.toCollection(ArrayList::new));
            tileImgsArray.get(position).setTileForegroundImgsArray(robotsImgsList);
        }
    }

    public void addEvent(Event event) {
        eventQueue.add(event);
    }

    public void executeNextEvents() {
        this.currentDate++;

        // peek/remove is faster than poll/add 
        Event event = eventQueue.peek();
        if (event != null && event.getDate() <= this.currentDate) {
            event.execute();
            eventQueue.remove();
        }
    }

    public Boolean isSimulationEnded() {
        return eventQueue.isEmpty();
    }

    private static BufferedImage loadImg(String imgFilename) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("src/ressources/" + imgFilename));
            picturesCache.putIfAbsent(imgFilename, bufferedImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOGGER.info("Chargement de l'image {} dans le cache", imgFilename);
        return bufferedImage;
    }

    private static BufferedImage getImg(String imgFilename) {
        BufferedImage img = picturesCache.get(imgFilename);
        if (img == null) {
            img = loadImg(imgFilename);
        }
  
        return img;
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

class TileImg implements GraphicalElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TileImg.class);
    
    private BufferedImage tileBackgroundImg = null;
    private ArrayList<BufferedImage> tileForegroundImgsArray = null;

    private int tileBackgroundImgSize;

    private int x;
    private int y;

    private int fireNormalizedIntensity = 0;

    public TileImg(int x, int y, int tileBackgroundImgSize, BufferedImage tileBackgroundImg) {
        LOGGER.info("Création d'une image représentant une case de taille {} en ({}, {})", tileBackgroundImgSize, x, y);
        this.tileBackgroundImgSize = tileBackgroundImgSize;
        this.tileBackgroundImg = tileBackgroundImg;

        this.x = x;
        this.y = y;
    }

    public void setTileForegroundImgsArray(ArrayList<BufferedImage> tileForegroundImgsArray) {
        LOGGER.info("Assignation d'une liste d'images (de robots) en ({}, {})", this.x, this.y);
        this.tileForegroundImgsArray = tileForegroundImgsArray;
    }

    public void setFireNormalizedIntensity(int fireNormalizedIntensity) {
        LOGGER.info("Assignation d'un dessin de feu en ({}, {}) d'intensite {}", this.x, this.y, fireNormalizedIntensity);
        this.fireNormalizedIntensity = fireNormalizedIntensity;
    }

    @Override
    public void paint(Graphics2D g2d) {
        // L'ordre est IMPORTANT. On superpose des images et dessins.
        // Cette méthode est appelée à chaque interaction avec la fenêtre (update)

        // Affichage de l'image de fond (nature du terrain)
        if (this.tileBackgroundImg != null) {
            g2d.drawImage(this.tileBackgroundImg, this.x, this.y, this.tileBackgroundImgSize, this.tileBackgroundImgSize, null);
        }

        // Affichage de l'incendie
        if (this.fireNormalizedIntensity != 0) {
            g2d.setColor(Color.RED);
            int firePadding = (this.tileBackgroundImgSize - this.fireNormalizedIntensity) / 2 ; // on veut un feu au milieu de la case
            g2d.fillOval(this.x + firePadding, this.y + firePadding, this.fireNormalizedIntensity, this.fireNormalizedIntensity);
        }

        // Affichage du ou des robot(s)
        // TODO: gérer l'affichage de plusieurs robots sur la même case
        // coin en haut à gauche: imgsPadding = this.tileImgSize / 8, width = height = this.tileImgSize / 4
        // au milieu: imgsPadding = this.tileImgSize / 4, width = height = this.tileImgSize / 2
        if (this.tileForegroundImgsArray != null && !this.tileForegroundImgsArray.isEmpty()) {
            int imgsPadding = this.tileBackgroundImgSize / 4 ;
            for (int index = 0; index < this.tileForegroundImgsArray.size(); index++) {
                BufferedImage foregroundImg = this.tileForegroundImgsArray.get(index);
                g2d.drawImage(foregroundImg, this.x + index * imgsPadding, this.y, imgsPadding, imgsPadding, null);
            }
        }
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

