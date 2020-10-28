package game.robots;

import java.util.EnumMap;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.NatureTerrain;

public class RobotType {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotType.class);
    private MyRobotTypes.Type type;
    private Filling filling; // méthode de remplissage: ON, NEXT or NONE
    private Double vitesse;
    private Double vitesseMax;
    private Double capacity; // au sens de capacité
    private int maxEmptiedVolume; // volume maximal que le robot peut déverser
    private int timeToEmpty; // temps mis pour déverser maxEmptiedVolume
    private int timeToFillUp;
    EnumMap<NatureTerrain, Double> terrainVitesse; // renvoie la diminution de vitesse en fonction du terrain

    public RobotType(MyRobotTypes.Type type, Filling filling, Double vitesse, Double vitesseMax, Double capacity,
            int maxEmptiedVolume, int timeToEmpty, int timeToFillUp, EnumMap<NatureTerrain, Double> terrainVitesse) {
        LOGGER.info("Déclaration d'un robot de type {}", type);
        this.type = type;
        this.filling = filling;
        this.vitesse = vitesse;
        this.vitesseMax = vitesseMax;
        this.capacity = capacity;
        this.maxEmptiedVolume = maxEmptiedVolume;
        this.timeToEmpty = timeToEmpty;
        this.timeToFillUp = timeToFillUp;
        this.terrainVitesse = terrainVitesse;
    }

    public Robot newRobot() {
        return new Robot(this);
    }

    public MyRobotTypes.Type getType() {
        return type;
    }

    public void setType(MyRobotTypes.Type type) {
        this.type = type;
    }

    public Double getVitesse() {
        return vitesse;
    }

    public void setVitesse(Double vitesse) {
        this.vitesse = vitesse;
    }

    public Double getVitesseMax() {
        return vitesseMax;
    }

    public void setVitesseMax(Double vitesseMax) {
        this.vitesseMax = vitesseMax;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public int getMaxEmptiedVolume() {
        return maxEmptiedVolume;
    }

    public void setMaxEmptiedVolume(int maxEmptiedVolume) {
        this.maxEmptiedVolume = maxEmptiedVolume;
    }

    public int getTimeToEmpty() {
        return timeToEmpty;
    }

    public void setTimeToEmpty(int timeToEmpty) {
        this.timeToEmpty = timeToEmpty;
    }

    public int getTimeToFillUp() {
        return timeToFillUp;
    }

    public void setTimeToFillUp(int timeToFillUp) {
        this.timeToFillUp = timeToFillUp;
    }

    public EnumMap<NatureTerrain, Double> getTerrainVitesse() {
        return terrainVitesse;
    }

    public void setTerrainVitesse(EnumMap<NatureTerrain, Double> terrainVitesse) {
        this.terrainVitesse = terrainVitesse;
    }

    public Filling getFilling() {
        return filling;
    }

    public void setFilling(Filling filling) {
        this.filling = filling;
    }

    @Override
    public String toString() {
        return "RobotType [filling=" + filling + ", maxEmptiedVolume=" + maxEmptiedVolume + ", terrainVitesse="
                + terrainVitesse + ", timeToEmpty=" + timeToEmpty + ", timeToFillUp=" + timeToFillUp + ", type=" + type
                + ", vitesse=" + vitesse + ", capacity=" + capacity + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.filling, this.vitesse, this.vitesseMax, this.capacity,
                this.maxEmptiedVolume, this.timeToEmpty, this.timeToFillUp, this.terrainVitesse);
    }

    @Override
    public boolean equals(Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check and type check
        // instances of the type and its subtypes can never equal.
        if (obj == null || getClass() != obj.getClass())
            return false;
        RobotType other = (RobotType) obj; // cast
        return Objects.equals(type, other.type) && Objects.equals(filling, other.filling)
                && Objects.equals(vitesse, other.vitesse) && Objects.equals(vitesseMax, other.vitesseMax)
                && Objects.equals(capacity, other.capacity) && Objects.equals(maxEmptiedVolume, other.maxEmptiedVolume)
                && Objects.equals(timeToEmpty, other.timeToEmpty) && Objects.equals(timeToFillUp, other.timeToFillUp)
                && Objects.equals(terrainVitesse, other.terrainVitesse);
    }
}
