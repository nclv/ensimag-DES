package game.events;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.robots.Robot;

public class ActionEmpty extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEmpty.class);

    public ActionEmpty(final DonneesSimulation donneesSimulation, final Robot robot) {
        super(donneesSimulation, robot);
    }

    public Action copy(DonneesSimulation donneesSimulation) {
        return new ActionEmpty(donneesSimulation, getRobot());
    }

    /**
     * Vide une partie de l'eau du robot sur sa position
     */
    @Override
    public void execute() {
        final Map<Integer, Integer> incendies = getDonneesSimulation().getIncendies();

        // save position
        final int position = getRobot().getPosition();
        LOGGER.info("{} en {} déverse de l'eau.", getRobot(), position);

        // extinction totale
        // on diminue l'intensité de l'incendie s'il y a un incendie à cette position
        Integer intensity = incendies.get(position);
        if (intensity != null) {
            // le robot déverse de l'eau sur sa position
            // empty all necessary water until there is no more water or the fire is extinguished
            // test != 0 because we make sure there cannot be < 0 values
            while (getRobot().getVolume() != 0.0 && intensity != 0) {
                double emptiedVolume = getRobot().deverserEau();
                if (intensity < emptiedVolume) {
                    LOGGER.info( "La quantité d'eau à déverser ({} L) est supérieure à l'intensité de l'incendie' ({})", emptiedVolume, intensity);
                    emptiedVolume = intensity;
                }
                intensity -= (int) emptiedVolume;
                incendies.put(position, intensity);
            }
        }
        LOGGER.info("Il contient maintenant {}L d'eau", getRobot().getVolume());
    }

     /**
     * @return durée mise pour vider le robot / éteindre l'incendie
     */
    @Override
    public long getDuration() {
        // le feu se trouve sur la position du robot
        final int firePosition = getRobot().getPosition();
        return getDonneesSimulation().getTimeToEmpty(getRobot(), firePosition);
    }
}
