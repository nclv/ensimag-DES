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
    private final Direction direction;

    public EventMove(final long date, final DonneesSimulation donneesSimulation, final Robot robot,
            final Direction direction) {
        super(date, donneesSimulation, robot);
        this.direction = direction;
    }

    public EventMove copy(final DonneesSimulation donneesSimulation) {
        return new EventMove(getDate(), donneesSimulation, getRobot(), this.direction);
    }

    /**
     * @return temps mis par le robot pour se déplacer
     * @throws IllegalArgumentException if outside the map or if the robot can't move on the position
     */
    public long getDuration() throws IllegalArgumentException {
        // on a besoin des positions pour update la durée du mouvement
        final int position = getRobot().getPosition();
        // throws IllegalArgumentException if outside the map
        final int newPosition = getDonneesSimulation().getCarte().getVoisin(position, this.direction);
        // throws IllegalArgumentException if the robot can't move on the position
        final long timeToMove = getTimeToMove(position, newPosition);
        return timeToMove;
    }

    /**
     * Le temps mis pour se rendre d’une case à l’autre est la moyenne de la
     * vitesse sur chacune des cases multipliée par la taille des cases.
     * @param position
     * @param newPosition
     * @return temps mis pour se déplacer de position à newPosition
     * @throws IllegalArgumentException if the robot can't move on newPosition
     */
    private long getTimeToMove(final int position, final int newPosition) throws IllegalArgumentException {
        final Carte carte = getDonneesSimulation().getCarte();
        // LOGGER.info("{}km/h sur {}, {}km/h sur {}, pour des cases de taille {}",
        // getRobot().getVitesse(carte.getTerrain(position)),
        // carte.getTerrain(position),
        // getRobot().getVitesse(carte.getTerrain(newPosition)),
        // carte.getTerrain(newPosition),
        // carte.getTailleCases());
        // throws IllegalArgumentException if the robot can't move on the position
        return (long) ((getRobot().getVitesse(carte.getTerrain(position))
                + getRobot().getVitesse(carte.getTerrain(newPosition))) / 2 * carte.getTailleCases());
    }

    @Override
    public void execute() {
        final Carte carte = getDonneesSimulation().getCarte();
        final Map<Integer, ArrayList<Robot>> robots = getDonneesSimulation().getRobots();

        final int position = getRobot().getPosition();

        try {
            // throws IllegalArgumentException if outside the map
            final int newPosition = carte.getVoisin(position, this.direction);
            // throws IllegalArgumentException if the robot can't move on the position
            getRobot().checkWalkable(carte.getTerrain(newPosition)); // protective, should already be catched in getDuration
            
            LOGGER.info("Déplacement: {} initialement en {} se déplace en {} ", getRobot(), position, newPosition);

            // update the robot position
            getRobot().setPosition(newPosition);
            // delete the robot
            robots.get(position).remove(getRobot());
            // add the robot
            robots.computeIfAbsent(newPosition, k -> new ArrayList<Robot>()).add(getRobot());
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
