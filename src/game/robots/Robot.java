package game.robots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Entity;
import game.NatureTerrain;

/**
 * Un robot est une entité. Il existe plusieurs types de robots.
 * 
 * @author Nicolas Vincent
 * @see Entity
 * @see RobotType
 */
public class Robot extends Entity {
    private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);
    private final RobotType robotType;
    private Double vitesse;
    private Double volume;

    /**
     * @param robotType
     * @param robotId
     * @param position
     * @see Entity#Entity(long)
     * @see #init(int)
     */
    public Robot(final RobotType robotType, final long robotId, final int position) {
        super(robotId);
        LOGGER.info("Instantiation d'un robot de type {}", robotType.getType());
        this.robotType = robotType;
        init(position);
    }

    /**
     * Initialisation du robot.
     * Utile pour en éviter la copie lors d'un restart.
     * 
     * @param position
     * @see Entity#setState(State)
     * @see Entity#setPosition(Integer)
     * @see Entity#setDate(Long)
     * @see RobotType#getVitesse()
     * @see RobotType#getCapacity()
     */
    @Override
    public void init(final int position) {
        setState(State.FREE);
        setPosition(position);
        setDate(0L);
        this.vitesse = robotType.getVitesse();
        this.volume = robotType.getCapacity(); // le robot est initialement plein
    }

    /**
     * Diminue le volume du robot de maxEmptiedVolume
     * On assure que le volume du robot reste >= 0
     * 
     * @return le volume déversé par le robot
     * @see RobotType#getMaxEmptiedVolume()
     */
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

    /**
     * @return true if volume == 0.0
     * @see #getVolume()
     */
    public Boolean isEmpty() {
        return (getVolume() == 0.0);
    }

    /**
     * On restore le volume du robot à sa capacité initiale.
     * 
     * @see RobotType#getCapacity()
     */
    public void remplirReservoir() {
        this.volume = this.robotType.getCapacity();
    }

    public MyRobotTypes.Type getType() {
        return this.robotType.getType();
    }

    public Filling getFilling() {
        return this.robotType.getFilling();
    }

    public int getMaxTimeToEmpty() {
        return this.robotType.getMaxTimeToEmpty();
    }

    public int getTimeToFillUp() {
        return this.robotType.getTimeToFillUp();
    }

    public int getMaxEmptiedVolume() {
        return this.robotType.getMaxEmptiedVolume();
    }

    /**
     * @param natureTerrain
     * @return la vitesse du robot sur le terrain en m/s
     * @throws IllegalArgumentException si le robot ne peut pas se déplacer sur le type de terrain
     * @see Entity#checkWalkable(NatureTerrain)
     * @see RobotType#getTerrainVitesse()
     */
    public Double getVitesse(final NatureTerrain natureTerrain) throws IllegalArgumentException {
        checkWalkable(natureTerrain);
        final double speedFactor = this.robotType.getTerrainVitesse().get(natureTerrain);
        return this.vitesse * 1000 / 3600 * speedFactor;
    }

    /**
     * @param natureTerrain
     * @return true if the robot can move on natureTerrain
     * @see RobotType#getTerrainVitesse()
     */
    @Override
    public Boolean isWalkable(final NatureTerrain natureTerrain) {
        return this.robotType.getTerrainVitesse().containsKey(natureTerrain);
    }

    /**
     * Set la vitesse si elle est inférieure à la vitesse maximale autorisée, ne fait rien sinon
     * 
     * @param vitesse
     * @see RobotType#getVitesseMax()
     */
    public void setVitesse(final Double vitesse) {
        final Double vitesseMax = robotType.getVitesseMax();
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
        res += "Le robot " + ((getState() == State.BUSY) ? "BUSY" : "FREE") + " de type " + this.robotType.getType() + " avançant à " + vitesse + " km/h et contenant " + volume
                + " litres d'eau";
        return res;
    }
}
