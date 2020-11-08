package strategie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.Simulateur;
import game.events.EventEmpty;
import game.pathfinding.Pathfinding;
import game.robots.Robot;
import game.robots.Robot.State;

public class StrategieEvoluee extends Strategie {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrategieElementaire.class);

    public StrategieEvoluee(Pathfinding pathfinding) {
        super(pathfinding);
    }

    @Override
    public void execute(Simulateur simulateur) {
        ArrayList<Robot> robots = new ArrayList<Robot>();
        simulateur.getDonneesSimulation().getRobots().values().forEach(robots::addAll);

        for(Map.Entry<Integer, Integer> incendie: simulateur.getDonneesSimulation().getIncendies().entrySet()) {
            int intensite = incendie.getValue();
            if (intensite == 0) continue;
            
            int positionIncendie = incendie.getKey();

            long minDuration = Long.MAX_VALUE;
            Robot robotMinDuration = null;
            LinkedList<Integer> pathMinDuration = null;
            
            for (Robot robot : robots) {
                if (robot.getState() == State.BUSY) continue;
                LOGGER.info("Recherche d'un chemin pour le robot {}", robot.getId());
                
                LinkedList<Integer> path;
                try {
                    path = getPathfinding().shortestWay(robot, robot.getPosition(), positionIncendie);
                } catch (IllegalStateException e) {
                    LOGGER.info("Aucun chemin n'est praticable");
                    continue;
                }
                
                long duration = getPathDuration(robot, path, simulateur.getDonneesSimulation());
                duration += simulateur.getDonneesSimulation().getTimeToEmpty(robot, path.getLast());

                if (duration < minDuration) {
                    robotMinDuration = robot;
                    pathMinDuration = path;
                    minDuration = duration;
                }
            }
            if (pathMinDuration == null) {
                LOGGER.info("Tous les robots sont occupés");
            } else {
                // on envoie le robot le plus rapide
                LOGGER.info("Ajoût des events pour le robot {}", robotMinDuration.getId());
                simulateur.addEventsMove(robotMinDuration, pathMinDuration, getCount());
                setCount(this.count + (pathMinDuration.size() - 1) * Simulateur.INCREMENT);
                simulateur.addEvent(new EventEmpty(getCount(), simulateur.getDonneesSimulation(), robotMinDuration));
                setCount(this.count + Simulateur.INCREMENT);
            }
        }
    }

    @Override
    public Boolean canFree(Robot robot) {
        return (robot.getVolume() != 0.0);
    }

    private long getPathDuration(Robot robot, LinkedList<Integer> path, DonneesSimulation donneesSimulation) {
        long duration = 0;

        Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            duration += donneesSimulation.getTimeToMove(robot, currentPosition, nextPosition);
            currentPosition = nextPosition;
        }
        return duration;
    }
    
}
