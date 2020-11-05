package game.robots;

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
    private State state = State.FREE;

    public static enum State {
        FREE,
        BUSY
    }

    public Robot(final RobotType robotType, final long robotId, final int position) {
        LOGGER.info("Instantiation d'un robot de type {}", robotType.getType());
        this.robotType = robotType;
        this.robotId = robotId;
        init(position);
    }

    /**
     * Initialisation du robot.
     * Utile pour en éviter la copie lors d'un restart.
     * @param position
     */
    public void init(final int position) {
        this.vitesse = robotType.getVitesse();
        this.volume = robotType.getCapacity(); // le robot est initialement plein
        this.position = position;
    }

    /**
     * Diminue le volume du robot de maxEmptiedVolume
     * On assure que le volume du robot reste >= 0
     * @return le volume déversé par le robot
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
     */
    public Double getVitesse(final NatureTerrain natureTerrain) throws IllegalArgumentException {
        checkWalkable(natureTerrain);
        final double speedFactor = this.robotType.getTerrainVitesse().get(natureTerrain);
        return this.vitesse * 1000 / 3600 * speedFactor;
    }

    /**
     * Vérifie que le robot peut se déplacer sur le type de terrain.
     * @param natureTerrain
     * @throws IllegalArgumentException
     */
    public void checkWalkable(final NatureTerrain natureTerrain) throws IllegalArgumentException {
        if (!isWalkable(natureTerrain)) {
            throw new IllegalArgumentException(this + " ne peut pas se déplacer sur une case de type " + natureTerrain);
        }
    }

    /**
     * @param natureTerrain
     * @return true if the robot can move on natureTerrain
     */
    private Boolean isWalkable(final NatureTerrain natureTerrain) {
        return this.robotType.getTerrainVitesse().containsKey(natureTerrain);
    }

    /**
     * Set la vitesse si elle est inférieure à la vitesse maximale autorisée, ne fait rien sinon
     * @param vitesse
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public Long getId() {
        return this.robotId;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "Le robot " + ((state == State.BUSY) ? "BUSY" : "FREE") + " de type " + this.robotType.getType() + " avançant à " + vitesse + " km/h et contenant " + volume
                + " litres d'eau";
        return res;
    }
}
