package game.events;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.Direction;
import game.DonneesSimulation;
import game.robots.Robot;

/**
 * @author Nicolas Vincent
 * @see Robot
 * @see Direction
 */
public class ActionMove extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMove.class);

    private Robot robot;
    private Direction direction;

    /**
     * @param donneesSimulation
     * @param robot
     * @param direction
     * @see Action#Action(DonneesSimulation, game.Entity)
     */
    public ActionMove(final DonneesSimulation donneesSimulation, final Robot robot, final Direction direction) {
        super(donneesSimulation, robot);
        this.robot = robot;
        this.direction = direction;
    }

    /**
     * @param donneesSimulation
     * @return nouvelle instance de la classe
     * @see ActionMove#ActionMove(DonneesSimulation, Robot, Direction)
     */
    @Override
    public Action copy(DonneesSimulation donneesSimulation) {
        return new ActionMove(donneesSimulation, this.robot, this.direction);
    }

    /**
     * Déplace le robot (si possible)
     * 
     * @see DonneesSimulation#getCarte()
     * @see DonneesSimulation#getRobots()
     * @see Robot#getPosition()
     * @see Robot#setPosition(Integer)
     * @see Robot#checkWalkable(game.NatureTerrain)
     * @see Carte#getVoisin(int, Direction)
     * @see Carte#getTerrain(int)
     */
    @Override
    public void execute() {
        final Carte carte = getDonneesSimulation().getCarte();
        final Map<Integer, ArrayList<Robot>> robots = getDonneesSimulation().getRobots();

        final int position = this.robot.getPosition();

        try {
            // throws IllegalArgumentException if outside the map
            final int newPosition = carte.getVoisin(position, this.direction);
            // throws IllegalArgumentException if the robot can't move on the position
            this.robot.checkWalkable(carte.getTerrain(newPosition)); // protective, should already be catched in getDuration
            
            LOGGER.info("Déplacement: {} initialement en {} se déplace en {} ", this.robot, position, newPosition);

            // update the robot position
            this.robot.setPosition(newPosition);
            // delete the robot
            robots.get(position).remove(this.robot);
            // add the robot
            robots.computeIfAbsent(newPosition, k -> new ArrayList<Robot>()).add(this.robot);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * @return temps mis par le robot pour se déplacer
     * @throws IllegalArgumentException if outside the map or if the robot can't move on the position
     * @see Robot#getPosition() 
     * @see DonneesSimulation#getCarte()
     * @see DonneesSimulation#getTimeToMove(Robot, int, int)
     * @see Carte#getVoisin(int, Direction)
     */
    @Override
    public long getDuration() throws IllegalArgumentException {
        // on a besoin des positions pour update la durée du mouvement
        final int position = this.robot.getPosition();
        // throws IllegalArgumentException if outside the map
        final int newPosition = getDonneesSimulation().getCarte().getVoisin(position, this.direction);
        // throws IllegalArgumentException if the robot can't move on the position
        final long timeToMove = getDonneesSimulation().getTimeToMove(this.robot, position, newPosition);
        return timeToMove;
    }
}
