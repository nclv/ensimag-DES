package game.robots;

import java.util.EnumMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.NatureTerrain;

/**
 * Classe représentant un type de robot.
 * 
 * On initialise deux robots de type drone en position 0:
 * RobotType droneType = new RobotType(...)
 * Robot drone1 = droneType.newRobot(0);
 * Robot drone2 = droneType.newRobot(0);
 * 
 * @author Nicolas Vincent
 */
public class RobotType {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotType.class);
    private static long id = 0;  // tous les types de robots partage le même id de départ

    private MyRobotTypes.Type type;
    private Filling filling; // méthode de remplissage: ON, NEXT or NONE
    private Double vitesse;
    private Double vitesseMax;
    private Double capacity;
    private int maxEmptiedVolume; // volume maximal que le robot peut déverser
    private int maxtimeToEmpty; // temps mis pour déverser maxEmptiedVolume
    private int timeToFillUp;
    private EnumMap<NatureTerrain, Double> terrainVitesse; // renvoie la diminution de vitesse en fonction du terrain

    /**
     * @param type
     * @param filling
     * @param vitesse
     * @param vitesseMax
     * @param capacity
     * @param maxEmptiedVolume
     * @param maxtimeToEmpty
     * @param timeToFillUp
     * @param terrainVitesse
     */
    public RobotType(final MyRobotTypes.Type type, final Filling filling, final Double vitesse, final Double vitesseMax,
            final Double capacity, final int maxEmptiedVolume, final int maxtimeToEmpty, final int timeToFillUp,
            final EnumMap<NatureTerrain, Double> terrainVitesse) {
        LOGGER.info("Déclaration d'un robot de type {}", type);
        this.type = type;
        this.filling = filling;
        this.vitesse = vitesse;
        this.vitesseMax = vitesseMax;
        this.capacity = capacity;
        this.maxEmptiedVolume = maxEmptiedVolume;
        this.maxtimeToEmpty = maxtimeToEmpty;
        this.timeToFillUp = timeToFillUp;
        this.terrainVitesse = terrainVitesse;
    }

    /**
     * @param position
     * @return Robot
     * @see Robot#Robot(RobotType, long, int)
     */
    public Robot newRobot(final int position) {
        return new Robot(this, id++, position);
    }

    public MyRobotTypes.Type getType() {
        return type;
    }

    public void setType(final MyRobotTypes.Type type) {
        this.type = type;
    }

    public Double getVitesse() {
        return vitesse;
    }

    public void setVitesse(final Double vitesse) {
        this.vitesse = vitesse;
    }

    public Double getVitesseMax() {
        return vitesseMax;
    }

    public void setVitesseMax(final Double vitesseMax) {
        this.vitesseMax = vitesseMax;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(final Double capacity) {
        this.capacity = capacity;
    }

    public int getMaxEmptiedVolume() {
        return maxEmptiedVolume;
    }

    public void setMaxEmptiedVolume(final int maxEmptiedVolume) {
        this.maxEmptiedVolume = maxEmptiedVolume;
    }

    public int getMaxTimeToEmpty() {
        return maxtimeToEmpty;
    }

    public void setMaxTimeToEmpty(final int maxtimeToEmpty) {
        this.maxtimeToEmpty = maxtimeToEmpty;
    }

    public int getTimeToFillUp() {
        return timeToFillUp;
    }

    public void setTimeToFillUp(final int timeToFillUp) {
        this.timeToFillUp = timeToFillUp;
    }

    public EnumMap<NatureTerrain, Double> getTerrainVitesse() {
        return terrainVitesse;
    }

    public void setTerrainVitesse(final EnumMap<NatureTerrain, Double> terrainVitesse) {
        this.terrainVitesse = terrainVitesse;
    }

    public Filling getFilling() {
        return filling;
    }

    public void setFilling(final Filling filling) {
        this.filling = filling;
    }

    @Override
    public String toString() {
        return "RobotType [filling=" + filling + ", maxEmptiedVolume=" + maxEmptiedVolume + ", terrainVitesse="
                + terrainVitesse + ", timeToEmpty=" + maxtimeToEmpty + ", timeToFillUp=" + timeToFillUp + ", type=" + type
                + ", vitesse=" + vitesse + ", capacity=" + capacity + "]";
    }
}
