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
    public EventEmpty(long date, DonneesSimulation donneesSimulation, Robot robot, int firePosition, int intensity) {
        super(date, donneesSimulation);
        this.robot = robot;

        // calcul du temps mis pour éteindre complètement l'incendie
        LOGGER.info("Réception de l'ordre à {}", getDate());
        // Integer intensity = this.donneesSimulation.getIncendies().get(firePosition);
        // if (intensity != null) {
        // temps mis pour une extinction globale
        long timeToEmpty = this.robot.getTimeToEmpty() * (intensity / this.robot.getMaxEmptiedVolume());
        LOGGER.info("Intensité: {}, Temps: {}", intensity, timeToEmpty);
        updateDate(timeToEmpty);
        // }
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

        // extinction totale
        // on diminue l'intensité de l'incendie s'il y a un incendie à cette position
        Integer intensity = incendies.get(position);
        if (intensity != null) {
            // empty all necessary water until there is no more water or the fire is extinguished
            // test != 0 because we make sure there cannot be < 0 values
            while (this.robot.getVolume() != 0.0 && intensity != 0) {
                double emptiedVolume = this.robot.deverserEau();
                if (intensity < emptiedVolume) {
                    LOGGER.info( "La quantité d'eau à déverser ({} L) est supérieure à l'intensité de l'incendie' ({})", emptiedVolume, intensity);
                    emptiedVolume = intensity;
                }
                intensity -= (int) emptiedVolume;
                incendies.put(position, intensity);
            }
        }
        LOGGER.info("Il contient maintenant {}L d'eau", this.robot.getVolume());
        
        // put same robot with updated volume field
        robotsCoordinates.put(this.robot, position);
    }
}