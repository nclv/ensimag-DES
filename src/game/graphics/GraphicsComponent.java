package game.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.MyRobotTypes;
import game.robots.Robot;
import gui.GUISimulator;
import gui.Simulable;

public class GraphicsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphicsComponent.class);

    private final GUISimulator gui;
    private final int guiSizeFactor;

    /* Utilitaire pour normaliser l'intensité des feux */
    private NormUtil normUtil;
    /* Buffer des images pour qu'on n'ait pas à les recharger à chaque affichage */
    private final ImagesBuffer imagesBuffer;

    private final ArrayList<TileImg> tileImgsArray = new ArrayList<TileImg>();

    private static final EnumMap<NatureTerrain, String> ressourcesMap = new EnumMap<NatureTerrain, String>(
            Map.of(NatureTerrain.EAU, "eau.png", NatureTerrain.FORET, "foret.png", NatureTerrain.HABITAT, "habitat.png",
                    NatureTerrain.ROCHE, "roche.png", NatureTerrain.TERRAIN_LIBRE, "terrain_libre.png"));

    private static final EnumMap<MyRobotTypes.Type, String> ressourcesRobots = new EnumMap<MyRobotTypes.Type, String>(
            Map.of(MyRobotTypes.Type.DRONE, "drone.png", MyRobotTypes.Type.ROUES, "roues.png",
                    MyRobotTypes.Type.CHENILLES, "chenilles.png", MyRobotTypes.Type.PATTES, "pattes.png"));

    private DonneesSimulation donneesSimulation;

    public GraphicsComponent(final GUISimulator gui, final int guiSizeFactor,
            final DonneesSimulation donneesSimulation) {
        this.imagesBuffer = new ImagesBuffer();

        this.donneesSimulation = donneesSimulation;

        this.gui = gui;
        LOGGER.info("GUI de dimensions {}x{}", gui.getPanelHeight(), gui.getPanelWidth());
        this.gui.reset(); // clear the window
        this.guiSizeFactor = guiSizeFactor;

        // initialisation de l'utilitaire de normalisation
        setNormUtil(donneesSimulation.getIncendies(), this.guiSizeFactor / 1.5, this.guiSizeFactor / 6);
    }

    /**
     * Set the gui simulable
     * 
     * @param simulable
     */
    public void setSimulable(final Simulable simulable) {
        this.gui.setSimulable(simulable);
    }

    /**
     * Set the normalisation utility parameters
     * 
     * @param incendies
     * @param normalizedHigh
     * @param normalizedLow
     */
    private void setNormUtil(final Map<Integer, Integer> incendies, final double normalizedHigh,
            final double normalizedLow) {
        // on va normaliser les intensités des incendies pour afficher des incendies de
        // taille différentes
        final Collection<Integer> intensites = incendies.values();
        final int minIntensity = Collections.min(intensites);
        final int maxIntensity = Collections.max(intensites);
        // on peut modifier les deux derniers arguments, attention à garder le feu
        // visible et ne remplissant pas toute la case
        this.normUtil = new NormUtil(maxIntensity, minIntensity, normalizedHigh, normalizedLow);
    }

    /**
     * Initialise l'interface graphique
     */
    public void init() {
        this.gui.reset(); // clear the window

        setTiles();
        updateAllTileImgs();

        // Ajout des composants à l'instance de JFrame
        for (final TileImg tileImg : this.tileImgsArray) {
            this.gui.addGraphicalElement(tileImg);
        }
    }

    /**
     * Dessine la carte et ses éléments
     * 
     * Drawing is automatic, no need to repaint
     */
    public void draw() {
        updateAllTileImgs();
    }

    /**
     * Retour à la carte initiale sans incendies et robots
     * 
     * On garde seulement les images de fond (qui représentent le type des cases)
     */
    public void reset() {
        final Map<Integer, NatureTerrain> map = donneesSimulation.getCarte().getMap();

        for (final Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            final TileImg tileImg = this.tileImgsArray.get(tile.getKey());
            tileImg.setFireNormalizedIntensity(0);
            tileImg.setTileForegroundImgsArray(null);
        }
    }

    /**
     * Ajoût des images des cases à tileImgsArray
     */
    private void setTiles() {
        final int nbLignes = donneesSimulation.getCarte().getNbLignes();
        final Map<Integer, NatureTerrain> map = donneesSimulation.getCarte().getMap();

        for (final Map.Entry<Integer, NatureTerrain> tile : map.entrySet()) {
            final int position = tile.getKey();
            final int x = position % nbLignes;
            final int y = position / nbLignes;

            // LOGGER.info("Affichage de la case ({}, {}) de type {}", x, y,
            // tile.getValue());
            tileImgsArray.add(new TileImg(x * this.guiSizeFactor, y * this.guiSizeFactor, this.guiSizeFactor,
                    this.imagesBuffer.getImg(ressourcesMap.get(tile.getValue()))));
        }
    }

    /**
     * Mise à jour des images des cases (incendies et robots)
     */
    private void updateAllTileImgs() {
        LOGGER.info("Mise à jour des entités:");
        if (!this.tileImgsArray.isEmpty()) {
            setIncendies();
            setRobots();
        }
    }

    /**
     * Mise à jour des incendies
     */
    private void setIncendies() {
        final Map<Integer, Integer> incendies = donneesSimulation.getIncendies();

        for (final Map.Entry<Integer, Integer> fire : incendies.entrySet()) {
            final int intensity = fire.getValue();
            int normalizedIntensity = (int) this.normUtil.normalize(intensity);
            // si le feu est éteint on ne l'affiche pas
            if (intensity == 0) {
                normalizedIntensity = 0;
            }
            LOGGER.info("Intensite de l'incendie: {}, {}", intensity, normalizedIntensity);

            tileImgsArray.get(fire.getKey()).setFireNormalizedIntensity(normalizedIntensity);
        }
    }

    /**
     * Mise à jour des robots
     */
    private void setRobots() {
        final Map<Integer, ArrayList<Robot>> robotsMap = donneesSimulation.getRobots();
        for (final Map.Entry<Integer, ArrayList<Robot>> robots : robotsMap.entrySet()) {
            // on veut dessiner un ou plusieurs robot(s) sur les cases,
            // on récupère la liste des images des robots en utilisant un stream (JDK8), on
            // pourrait aussi faire une boucle
            final ArrayList<BufferedImage> robotsImgsList = robots.getValue().stream()
                    .map((robot) -> this.imagesBuffer.getImg(ressourcesRobots.get(robot.getType())))
                    .collect(Collectors.toCollection(ArrayList::new));
            tileImgsArray.get(robots.getKey()).setTileForegroundImgsArray(robotsImgsList);
        }
    }

    public void setDonneesSimulation(final DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
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
        return ((x - dataLow) / (dataHigh - dataLow)) * (normalizedHigh - normalizedLow) + normalizedLow;
    }
}

class ImagesBuffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesBuffer.class);

    /* Stockage des images */
    private final HashMap<String, BufferedImage> picturesCache = new HashMap<String, BufferedImage>();

    /**
     * Chargement d'une image dans le buffer
     * 
     * @param imgFilename
     * @return image chargée BufferedImage
     */
    public BufferedImage loadImg(final String imgFilename) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("src/ressources/" + imgFilename));
            this.picturesCache.putIfAbsent(imgFilename, bufferedImage);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        LOGGER.info("Chargement de l'image {} dans le cache", imgFilename);
        return bufferedImage;
    }

    /**
     * Renvoie l'image présente dans le buffer
     * Chargement de l'image si elle n'est pas présente dans le buffer
     * 
     * @param imgFilename
     * @return BufferedImage présente dans le buffer
     */
    public BufferedImage getImg(final String imgFilename) {
        BufferedImage img = this.picturesCache.get(imgFilename);
        if (img == null) {
            img = loadImg(imgFilename);
        }

        return img;
    }
}