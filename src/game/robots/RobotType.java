package game.robots;

import java.util.EnumMap;

import game.NatureTerrain;

public class RobotType {
    private Type type;
    private Filling filling; // méthode de remplissage: ON, NEXT or NONE
    private Double vitesse;
    private Double volume;
    private int maxEmptiedVolume; // volume maximal que le robot peut déverser
    private int timeToEmpty; // temps mis pour déverser maxEmptiedVolume
    private int timeToFillUp;
    EnumMap<NatureTerrain, Double> terrainVitesse; // renvoie la diminution de vitesse en fonction du terrain

    public RobotType(Type type, Filling filling, Double vitesse, Double volume, int maxEmptiedVolume, int timeToEmpty,
            int timeToFillUp, EnumMap<NatureTerrain, Double> terrainVitesse) {
        this.type = type;
        this.filling = filling;
        this.vitesse = vitesse;
        this.volume = volume;
        this.maxEmptiedVolume = maxEmptiedVolume;
        this.timeToEmpty = timeToEmpty;
        this.timeToFillUp = timeToFillUp;
        this.terrainVitesse = terrainVitesse;
    }

    public Robot newRobot() {
        return new Robot(this);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getVitesse() {
        return vitesse;
    }

    public void setVitesse(Double vitesse) {
        this.vitesse = vitesse;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
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
                + ", vitesse=" + vitesse + ", volume=" + volume + "]";
    }
}