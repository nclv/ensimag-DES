import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import game.events.Event;
import game.robots.MyRobotTypes;
import game.robots.Robot;
import gui.GUISimulator;
import gui.Simulable;

public class Simulateur implements Simulable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulateur.class);

    public static final long INCREMENT = 1500000;

    private GUISimulator gui;
    private int guiSizeFactor;

    private DonneesSimulation donneesSimulation;
    private DonneesSimulation donneesSimulationSaved;

    private long currentDate;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
    private PriorityQueue<Event> eventQueueSaved = new PriorityQueue<Event>();

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
        this.donneesSimulationSaved = new DonneesSimulation(donneesSimulation);

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

            int intensity = fire.getValue();
            int normalizedIntensity = (int)normUtil.normalize(intensity);
            LOGGER.info("Intensite de l'incendie: {}, {}", intensity, normalizedIntensity);

            // si le feu est éteint on ne l'affiche pas
            if (intensity == 0) {
                normalizedIntensity = 0;
            }

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

    public void addEvent(Event event) {
        LOGGER.info("Date de l'évènement (ajoût): {}", event.getDate());

        eventQueue.add(event);
        eventQueueSaved.add(event);
    }

    private void executeNextEvents() {
        updateCurrentDate();
        LOGGER.info("Date courante: {}", this.currentDate);

        // peek/remove is faster than poll/add 
        Event event = eventQueue.peek();
        while (event != null && event.getDate() <= this.currentDate) {
            LOGGER.info("Date de l'évènement (execution): {}", event.getDate());
            event.execute();
            eventQueue.remove();
            event = eventQueue.peek();
        }
    }

    public void updateCurrentDate() {
        this.currentDate += INCREMENT;
    }

    @Override
    public void next() {
        executeNextEvents();

        // Update de l'affichage
        gui.reset();
        updateAllTileImgs();
        for (TileImg tileImg : this.tileImgsArray) {
            gui.addGraphicalElement(tileImg);
        }
    }

    @Override
    public void restart() {
        this.currentDate = 0;
        LOGGER.info("{}", this.donneesSimulation);
        this.donneesSimulation = this.donneesSimulationSaved;
        LOGGER.info("{}", this.donneesSimulation);
        LOGGER.info("{}", this.eventQueue);
        this.eventQueue = this.eventQueueSaved;
        LOGGER.info("{}", this.eventQueue);
        this.tileImgsArray.clear();

        // Update de l'affichage
        gui.reset();
        populateTileImgsArray();
        updateAllTileImgs();
        for (TileImg tileImg : this.tileImgsArray) {
            gui.addGraphicalElement(tileImg);
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