package game.robots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.NatureTerrain;

// on ne peut pas instancier une classe abstraite
public class Robot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);
    RobotType robotType;
    private Double vitesse;
    private Double volume;

    public Robot(RobotType robotType) {
        LOGGER.info("Instantiation d'un robot de type {}", robotType.getType());
        this.robotType = robotType;
        this.vitesse = robotType.getVitesse();
        this.volume = robotType.getCapacity(); // le robot est initialement plein
    }

    void deverserEau(int volume) {
        double maxEmptiedVolume = this.robotType.getMaxEmptiedVolume();
        if (volume > maxEmptiedVolume) {
            throw new IllegalArgumentException("Le robot ne peut pas déverser plus de " + maxEmptiedVolume + " L à la fois");
        }
        if (this.volume < volume) {
            LOGGER.info("La quantité d'eau à déverser ({} L) est supérieure à la quantité d'eau présente dans le robot ({} L)", volume, this.volume);
        }
        this.volume -= volume;
    }

    void remplirReservoir() {
        this.volume = robotType.getCapacity();
    }

    public MyRobotTypes.Type getType() {
        return robotType.getType();
    }

    public Double getVitesse(NatureTerrain natureTerrain) {
        return this.vitesse * robotType.getTerrainVitesse().get(natureTerrain);
    }

    public void setVitesse(Double vitesse) {
        Double vitesseMax = robotType.getVitesseMax();
        if (vitesse <= vitesseMax) {
            LOGGER.info("Set vitesse = {} km/h", vitesse);
            this.vitesse = vitesse;
        } else {
            LOGGER.info("{} km/h est plus élevée que la vitesse maximale de {} km/h", vitesse, vitesseMax);
        }
    }

    @Override
    public String toString() {
        String res = new String();
        res += "Robot de type " + this.robotType.getType() + " avançant à " + vitesse + " km/h et contenant " + volume + " litres d'eau";
        return res;
    }
}
