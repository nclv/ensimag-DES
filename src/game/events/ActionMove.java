package game.events;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.Direction;
import game.DonneesSimulation;
import game.robots.Robot;

public class ActionMove extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMove.class);

    private Direction direction;

    public ActionMove(final DonneesSimulation donneesSimulation, final Robot robot, final Direction direction) {
        super(donneesSimulation, robot);
        this.direction = direction;
    }

    public Action copy(DonneesSimulation donneesSimulation) {
        return new ActionMove(donneesSimulation, getRobot(), this.direction);
    }

    /**
     * Déplace le robot (si possible)
     */
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

    /**
     * @return temps mis par le robot pour se déplacer
     * @throws IllegalArgumentException if outside the map or if the robot can't move on the position
     */
    @Override
    public long getDuration() throws IllegalArgumentException {
        // on a besoin des positions pour update la durée du mouvement
        final int position = getRobot().getPosition();
        // throws IllegalArgumentException if outside the map
        final int newPosition = getDonneesSimulation().getCarte().getVoisin(position, this.direction);
        // throws IllegalArgumentException if the robot can't move on the position
        final long timeToMove = getDonneesSimulation().getTimeToMove(getRobot(), position, newPosition);
        return timeToMove;
    }
}
