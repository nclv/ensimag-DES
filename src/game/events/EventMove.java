package game.events;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.Direction;
import game.DonneesSimulation;
import game.robots.Robot;

public class EventMove extends Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventMove.class);
    private Direction direction;

    public EventMove(long date, DonneesSimulation donneesSimulation, Robot robot, Direction direction) {
        super(date, donneesSimulation, robot);
        this.direction = direction;
    }

    public long getDuration() {
        long timeToMove = 0;
        // on a besoin des positions pour update la durée du mouvement
        int position = this.donneesSimulation.getRobotsCoordinates().get(getRobot());
        try {
            // throws IllegalArgumentException if outside the map
            int newPosition = this.donneesSimulation.getCarte().getVoisin(position, this.direction);
            timeToMove = getTimeToMove(position, newPosition);
            LOGGER.info("Réception de l'ordre à {}", getDate());
            // updateDate(timeToMove);
            // LOGGER.info("Fin d'exécution à {}", getDate());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return timeToMove;
    }

    // Le temps nécessaire pour se rendre d’une case à l’autre sera la moyenne de la
    // vitesse sur chacune des cases multipliée par la taille des cases.
    public long getTimeToMove(int position, int newPosition) {
        Carte carte = this.donneesSimulation.getCarte();
        // LOGGER.info("{}km/h sur {}, {}km/h sur {}, pour des cases de taille {}",
        //         getRobot().getVitesse(carte.getTerrain(position)), carte.getTerrain(position),
        //         getRobot().getVitesse(carte.getTerrain(newPosition)), carte.getTerrain(newPosition),
        //         carte.getTailleCases());
        return (long) ((getRobot().getVitesse(carte.getTerrain(position))
                + getRobot().getVitesse(carte.getTerrain(newPosition))) / 2 * carte.getTailleCases());
    }

    @Override
    public void execute() {
        Map<Robot, Integer> robotsCoordinates = this.donneesSimulation.getRobotsCoordinates();
        Map<Integer, ArrayList<Robot>> robots = this.donneesSimulation.getRobots();

        int position = robotsCoordinates.get(getRobot());

        try {
            // throws IllegalArgumentException if outside the map
            int newPosition = this.donneesSimulation.getCarte().getVoisin(position, this.direction);
            LOGGER.info("Déplacement: {} initialement en {} se déplace en {} ", getRobot(), position, newPosition);

            // update the robot position
            robotsCoordinates.put(getRobot(), newPosition);
            robots.get(position).remove(getRobot());
            robots.computeIfAbsent(newPosition, k -> new ArrayList<Robot>()).add(getRobot());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
