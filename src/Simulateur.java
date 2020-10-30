import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
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

    private final GUISimulator gui;
    private final int guiSizeFactor;

    private DonneesSimulation donneesSimulation;
    private final DonneesSimulation donneesSimulationSaved;

    private NormUtil normUtil;

    private long currentDate;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
    private final PriorityQueue<Event> eventQueueSaved = new PriorityQueue<Event>();

    private final ArrayList<TileImg> tileImgsArray = new ArrayList<TileImg>();

    private static final EnumMap<NatureTerrain, String> ressourcesMap = new EnumMap<NatureTerrain, String>(
            Map.of(NatureTerrain.EAU, "eau.png", NatureTerrain.FORET, "foret.png", NatureTerrain.HABITAT, "habitat.png",
                    NatureTerrain.ROCHE, "roche.png", NatureTerrain.TERRAIN_LIBRE, "terrain_libre.png"));

    private static final EnumMap<MyRobotTypes.Type, String> ressourcesRobots = new EnumMap<MyRobotTypes.Type, String>(
            Map.of(MyRobotTypes.Type.DRONE, "drone.png", MyRobotTypes.Type.ROUES, "roues.png",
                    MyRobotTypes.Type.CHENILLES, "chenilles.png", MyRobotTypes.Type.PATTES, "pattes.png"));

    private static HashMap<String, BufferedImage> picturesCache = new HashMap<String, BufferedImage>();

    /**
     * Crée un Simulateur et le dessine.
     * 
     * @param gui   l'interface graphique associée, dans laquelle se fera le dessin
     *              et qui enverra les messages via les méthodes héritées de
     *              Simulable.
     * @param color la couleur du simulateur
     */
    public Simulateur(final GUISimulator gui, final int guiSizeFactor, final DonneesSimulation donneesSimulation) {
        this.gui = gui;
        LOGGER.info("GUI de dimensions {}x{}", gui.getPanelHeight(), gui.getPanelWidth());

        this.gui.setSimulable(this);
        this.guiSizeFactor = guiSizeFactor;

        this.donneesSimulation = donneesSimulation;
        LOGGER.info("Copie des données initiales de la simulation");
        this.donneesSimulationSaved = new DonneesSimulation(donneesSimulation);

        setNormUtil();
        initMap();
    }

    public void setNormUtil() {
        final Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();
        // on va normaliser les intensités des incendies pour afficher des incendies de
        // taille différentes
        final Collection<Integer> intensites = incendies.values();
        final int minIntensity = Collections.min(intensites);
        final int maxIntensity = Collections.max(intensites);
        // on peut modifier les deux derniers arguments, attention à garder le feu
        // visible et ne remplissant pas toute la case
        this.normUtil = new NormUtil(maxIntensity, minIntensity, this.guiSizeFactor / 1.5, this.guiSizeFactor / 6);
    }

    private void initMap() {
        populateTileImgsArray();
        draw();
    }

    private void draw() {
        this.gui.reset(); // clear the window

        LOGGER.info("Mise à jour des entités:");
        updateAllTileImgs();
        // Ajout des composants à l'instance de JFrame
        for (final TileImg tileImg : this.tileImgsArray) {
            this.gui.addGraphicalElement(tileImg);
        }
    }

    private void populateTileImgsArray() {
        final int nbLignes = this.donneesSimulation.getCarte().getNbLignes();
        final Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();

        for (final Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            final int position = tile.getKey();
            final int x = position % nbLignes;
            final int y = position / nbLignes;

            // LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y,
            // tile.getValue());
            tileImgsArray.add(new TileImg(x * this.guiSizeFactor, y * this.guiSizeFactor, this.guiSizeFactor,
                    getImg(ressourcesMap.get(tile.getValue()))));
        }
    }

    private void resetTileImgs() {
        final Map<Integer, NatureTerrain> map = this.donneesSimulation.getCarte().getMap();

        for (final Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            final int position = tile.getKey();
            final TileImg tileImg = this.tileImgsArray.get(position);
            tileImg.setFireNormalizedIntensity(0);
            tileImg.setTileForegroundImgsArray(null);
        }
    }

    private void updateAllTileImgs() {
        if (!this.tileImgsArray.isEmpty()) {
            setIncendies();
            setRobots();
        }
    }

    private void setIncendies() {
        final Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();

        for (final Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            final int position = fire.getKey();

            final int intensity = fire.getValue();
            int normalizedIntensity = (int) this.normUtil.normalize(intensity);
            LOGGER.info("Intensite de l'incendie: {}, {}", intensity, normalizedIntensity);

            // si le feu est éteint on ne l'affiche pas
            if (intensity == 0) {
                normalizedIntensity = 0;
            }

            tileImgsArray.get(position).setFireNormalizedIntensity(normalizedIntensity);
        }
    }

    private void setRobots() {
        final Map<Integer, ArrayList<Robot>> robotsMap = this.donneesSimulation.getRobots();
        for (final Map.Entry<Integer, ArrayList<Robot>> robots : robotsMap.entrySet()) {
            final int position = robots.getKey();
            // on veut dessiner un ou plusieurs robot(s) sur les cases,
            // on récupère la liste des images des robots en utilisant un stream (JDK8), on
            // pourrait aussi faire une boucle
            final ArrayList<BufferedImage> robotsImgsList = robots.getValue().stream()
                    .map((robot) -> getImg(ressourcesRobots.get(robot.getType())))
                    .collect(Collectors.toCollection(ArrayList::new));
            tileImgsArray.get(position).setTileForegroundImgsArray(robotsImgsList);
        }
    }

    private static BufferedImage loadImg(final String imgFilename) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("src/ressources/" + imgFilename));
            picturesCache.putIfAbsent(imgFilename, bufferedImage);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        LOGGER.info("Chargement de l'image {} dans le cache", imgFilename);
        return bufferedImage;
    }

    private static BufferedImage getImg(final String imgFilename) {
        BufferedImage img = picturesCache.get(imgFilename);
        if (img == null) {
            img = loadImg(imgFilename);
        }

        return img;
    }

    public void addEvent(final Event event) {
        LOGGER.info("Date de l'évènement (ajoût): {}", event.getDate());

        eventQueue.add(event);
        // ajout à la queue de sauvegarde
        eventQueueSaved.add(event.copy(this.donneesSimulation));
    }

    private void executeNextEvents() {
        // peek/remove is faster than poll/add
        Event event = eventQueue.peek();
        while (event != null && event.getDate() <= this.currentDate) {
            LOGGER.info("Date de l'évènement (execution): {}", event.getDate());

            // on exécute l'action pour le robot si l'action est valide
            try {
                updateEventQueue(event);
                event.execute();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            }

            eventQueue.remove();
            event = eventQueue.peek();
        }
    }

    public void updateEventQueue(final Event event) throws IllegalArgumentException {
        // récupère la durée de l'event
        final long duration = event.getDuration();
        LOGGER.info("Fin d'exécution: {}", duration);
        // le robot est occupé pendant duration, on ne peut plus exécuter d'actions avec
        // ce robot
        // il faut incrémenter la date des évènements de ce robot de duration
        final ArrayList<Event> eventsToAdd = new ArrayList<Event>();
        final Iterator<Event> events = this.eventQueue.iterator();
        while (events.hasNext()) {
            final Event currentEvent = events.next();
            // problème d'égalité possible si l'égalité des volumes est vérifiée dans
            // equals()
            // on implémente un id propre à chaque robot
            if (currentEvent.getRobot().getId().equals(event.getRobot().getId())) {
                // on incrémente la date de l'event de la durée de l'event exécuté
                // l'event qui va être exécuté (donc supprimé de la queue) est aussi incrémenté
                currentEvent.updateDate(this.currentDate + duration);
                LOGGER.info("Nouvelle date de l'évènement: {}", currentEvent.getDate());
                events.remove();
                eventsToAdd.add(currentEvent);
            }
        }
        this.eventQueue.addAll(eventsToAdd);
    }

    public void updateCurrentDate() {
        this.currentDate += INCREMENT;
    }

    @Override
    public void next() {
        executeNextEvents();

        LOGGER.info("Ancienne date courante: {}", this.currentDate);
        updateCurrentDate();
        LOGGER.info("Nouvelle date courante: {}", this.currentDate);

        // Update de l'affichage
        draw();
    }

    @Override
    public void restart() {
        this.currentDate = 0;

        this.donneesSimulation = new DonneesSimulation(this.donneesSimulationSaved);
        this.eventQueue = new PriorityQueue<Event>();
        // donneesSimulation et pas donneesSimulationSaved pcq on modifie l'argument
        // lorsque l'on exécute l'event
        this.eventQueueSaved.stream().forEach((event) -> eventQueue.add(event.copy(this.donneesSimulation)));

        // Update de l'affichage
        resetTileImgs();
        draw();
    }
}

class NormUtil {
    private final double dataHigh;
    private final double dataLow;
    private final double normalizedHigh;
    private final double normalizedLow;

    /**
     * Construct the normalization utility, allow the normalization range to be
     * specified.
     * 
     * @param dataHigh The high value for the input data.
     * @param dataLow  The low value for the input data.
     * @param dataHigh The high value for the normalized data.
     * @param dataLow  The low value for the normalized data.
     */
    public NormUtil(final double dataHigh, final double dataLow, final double normalizedHigh,
            final double normalizedLow) {
        this.dataHigh = dataHigh;
        this.dataLow = dataLow;
        this.normalizedHigh = normalizedHigh;
        this.normalizedLow = normalizedLow;
    }

    /**
     * Normalize x.
     * 
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(final double x) {
        return ((x - dataLow) 
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
}