package game.robots;

import game.NatureTerrain;

// on ne peut pas instancier une classe abstraite
public class Robot {
    RobotType robotType;
    private Double vitesse;
    private Double volume;

    public Robot(RobotType robotType) {
        this.robotType = robotType;
        this.vitesse = robotType.getVitesse();
        this.volume = robotType.getVolume();
    }

    void deverserEau(int volume) {
    }

    void remplirReservoir() {
    }

    public Type getType() {
        return robotType.getType();
    }

    public Double getVitesse(NatureTerrain natureTerrain) {
        return this.vitesse / robotType.getTerrainVitesse().get(natureTerrain);
    }

    public void setVitesse(Double vitesse) {
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "Robot [robotType=" + robotType + ", vitesse=" + vitesse + ", volume=" + volume + "]";
    }
}
