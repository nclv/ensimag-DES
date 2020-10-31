package game.robots;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.IdentifiedEntity;
import game.NatureTerrain;

// on ne peut pas instancier une classe abstraite
public class Robot implements IdentifiedEntity<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);
    private final RobotType robotType;
    private final long robotId;
    private Double vitesse;
    private Double volume;
    private Integer position;

    public Robot(final RobotType robotType, final long robotId, final int position) {
        LOGGER.info("Instantiation d'un robot de type {}", robotType.getType());
        this.robotType = robotType;
        this.robotId = robotId;
        init(position);
    }

    public void init(final int position) {
        this.vitesse = robotType.getVitesse();
        this.volume = robotType.getCapacity(); // le robot est initialement plein
        this.position = position;
    }

    public Double deverserEau() {
        double emptiedVolume = this.robotType.getMaxEmptiedVolume();
        // we make sure there cannot be < 0 volume values
        if (this.volume < emptiedVolume) {
            LOGGER.info(
                    "La quantité d'eau à déverser ({} L) est supérieure à la quantité d'eau présente dans le robot ({} L)",
                    emptiedVolume, this.volume);
            emptiedVolume = this.volume;
        }
        this.volume -= emptiedVolume;
        return emptiedVolume;
    }

    public Double getVolume() {
        return volume;
    }

    public void remplirReservoir() {
        this.volume = this.robotType.getCapacity();
    }

    public MyRobotTypes.Type getType() {
        return this.robotType.getType();
    }

    public Filling getFilling() {
        return this.robotType.getFilling();
    }

    public int getTimeToEmpty() {
        return this.robotType.getTimeToEmpty();
    }

    public int getTimeToFillUp() {
        return this.robotType.getTimeToFillUp();
    }

    public int getMaxEmptiedVolume() {
        return this.robotType.getMaxEmptiedVolume();
    }

    public Double getVitesse(final NatureTerrain natureTerrain) throws IllegalArgumentException {
        // vitesse nulle si le robot ne peut pas se déplacer sur le terrain
        final double speedFactor = this.robotType.getTerrainVitesse().getOrDefault(natureTerrain, 0.0);
        if (speedFactor == 0.0) {
            throw new IllegalArgumentException(this + " ne peut pas se déplacer sur une case de type " + natureTerrain);
        }
        return this.vitesse * speedFactor;
    }

    public void setVitesse(final Double vitesse) {
        final Double vitesseMax = robotType.getVitesseMax();
        if (vitesse <= vitesseMax) {
            LOGGER.info("Set vitesse = {} km/h", vitesse);
            this.vitesse = vitesse;
        } else {
            LOGGER.info("{} km/h est plus élevée que la vitesse maximale de {} km/h", vitesse, vitesseMax);
        }
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    @Override
    public Long getId() {
        return this.robotId;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "Robot de type " + this.robotType.getType() + " avançant à " + vitesse + " km/h et contenant " + volume
                + " litres d'eau";
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.robotType, this.volume, this.vitesse);
    }

    @Override
    public boolean equals(final Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check and type check
        // instances of the type and its subtypes can never equal.
        if (obj == null || getClass() != obj.getClass())
            return false;
        final Robot other = (Robot) obj; // cast
        // le volume est variable pour un même robot, il ne sert donc pas à identifier un robot mais son état
        return Objects.equals(robotId, other.robotId);
    }
}
