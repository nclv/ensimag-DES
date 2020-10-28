package game.events;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.robots.Robot;

public class EventEmpty extends Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventEmpty.class);
    private Robot robot;

    // le robot déverse de l'eau sur sa position
    public EventEmpty(long date, DonneesSimulation donneesSimulation, Robot robot) {
        super(date, donneesSimulation);
        this.robot = robot;

        LOGGER.info("Réception de l'ordre à {}", getDate());
        updateDate(this.robot.getTimeToEmpty());
        LOGGER.info("Fin d'exécution à {}", getDate());
    }

    @Override
    public void execute() {
        Map<Robot, Integer> robotsCoordinates = this.donneesSimulation.getRobotsCoordinates();
        Map<Integer, Integer> incendies = this.donneesSimulation.getIncendies();

        // save position
        int position = robotsCoordinates.get(this.robot);

        LOGGER.info("{} en {} déverse de l'eau.", this.robot, position);
        // remove old robot
        robotsCoordinates.remove(this.robot);
        double emptiedVolume = this.robot.deverserEau();
        LOGGER.info("Il contient maintenant {}L d'eau", this.robot.getVolume());
        // put same robot with updated volume field
        robotsCoordinates.put(this.robot, position);

        // on diminue l'intensité de l'incendie s'il y a un incendie à cette position
        Integer intensity = incendies.get(position);
        if (intensity != null) {
            incendies.put(position, intensity - (int)emptiedVolume);
        }
    }
}