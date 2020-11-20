package strategie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.pathfinding.Pathfinding;
import game.Simulateur;
import game.robots.Robot;
import game.Entity.State;

/**
 * @author Nicolas Vincent
 * @see Strategie
 */
public class StrategieElementaire extends Strategie {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrategieElementaire.class);

    /**
     * @param pathfinding
     * @see Strategie#Strategie(Pathfinding)
     */
    public StrategieElementaire(final Pathfinding pathfinding) {
        super(pathfinding);
    }

    /**
     * Stratégie élémentaire. Chaque robot se déplace sur le même incendie. Un robot
     * vide ne se déplace plus.
     * 
     * @param simulateur
     * @see Robot#getState()
     * @see Robot#setState(State)
     * @see Robot#isEmpty()
     * @see State
     * @see Pathfinding#shortestWay(Robot, int, int)
     * @see EventAdder#addPath(Robot, LinkedList)
     * @see EventAdder#addEmpty(Robot)
     */
    @Override
    public void execute(final Simulateur simulateur) {
        final ArrayList<Robot> robots = new ArrayList<Robot>();
        simulateur.getDonneesSimulation().getRobots().values().forEach(robots::addAll);

        for (final Map.Entry<Integer, Integer> incendie : simulateur.getDonneesSimulation().getIncendies().entrySet()) {
            final int intensite = incendie.getValue();
            if (intensite == 0)
                continue;

            final int positionIncendie = incendie.getKey();
            for (final Robot robot : robots) {
                if (robot.isEmpty())
                    robot.setState(State.BUSY);
                if (robot.getState() == State.BUSY)
                    continue;
                LOGGER.info("Recherche d'un chemin pour le robot {}", robot.getId());

                LinkedList<Integer> path;
                try {
                    path = getPathfinding().shortestWay(robot, robot.getPosition(), positionIncendie);
                } catch (final IllegalStateException e) {
                    LOGGER.info("Aucun chemin n'est praticable");
                    continue;
                }
                
                LOGGER.info("Ajoût des events");
                
                getEventAdder().addPath(robot, path);
                getEventAdder().addEmpty(robot, path.getLast());
            }
        }
    }
}
