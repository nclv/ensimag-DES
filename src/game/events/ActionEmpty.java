package game.events;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.robots.Robot;

/**
 * @author Nicolas Vincent
 * @see Robot
 */
public class ActionEmpty extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEmpty.class);

    private final Robot robot;

    /**
     * @param donneesSimulation
     * @param robot
     * @see Action#Action(DonneesSimulation, game.Entity)
     */
    public ActionEmpty(final DonneesSimulation donneesSimulation, final Robot robot) {
        super(donneesSimulation, robot);
        this.robot = robot;
    }

    /**
     * @param donneesSimulation
     * @return nouvelle instance de la classe
     * @see ActionEmpty#ActionEmpty(DonneesSimulation, Robot)
     */
    @Override
    public Action copy(final DonneesSimulation donneesSimulation) {
        return new ActionEmpty(donneesSimulation, this.robot);
    }

    /**
     * Vide une partie de l'eau du robot sur sa position
     * 
     * @see DonneesSimulation#getIncendies()
     * @see Robot#getPosition()
     * @see Robot#getVolume()
     * @see Robot#deverserEau()
     */
    @Override
    public void execute() {
        final Map<Integer, Integer> incendies = getDonneesSimulation().getIncendies();

        // save position
        final int position = this.robot.getPosition();
        LOGGER.info("{} en {} déverse de l'eau.", this.robot, position);

        // extinction totale
        // on diminue l'intensité de l'incendie s'il y a un incendie à cette position
        Integer intensity = incendies.get(position);
        if (intensity != null) {
            // le robot déverse de l'eau sur sa position
            // empty all necessary water until there is no more water or the fire is extinguished
            // test != 0 because we make sure there cannot be < 0 values
            while (robot.getVolume() != 0.0 && intensity != 0) {
                double emptiedVolume = robot.deverserEau();
                if (intensity < emptiedVolume) {
                    LOGGER.info( "La quantité d'eau à déverser ({} L) est supérieure à l'intensité de l'incendie' ({})", emptiedVolume, intensity);
                    emptiedVolume = intensity;
                }
                intensity -= (int) emptiedVolume;
                incendies.put(position, intensity);
            }
        }
        LOGGER.info("Il contient maintenant {}L d'eau", this.robot.getVolume());
    }

    /**
     * @return durée mise pour vider le robot / éteindre l'incendie
     * @see Robot#getPosition()
     * @see DonneesSimulation#getTimeToEmpty(Robot, int)
     */
    @Override
    public long getDuration() {
        // le feu se trouve sur la position du robot
        final int firePosition = this.robot.getPosition();
        return getDonneesSimulation().getTimeToEmpty(this.robot, firePosition);
    }
}
