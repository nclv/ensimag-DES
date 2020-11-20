package strategie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.Simulateur;
import game.pathfinding.Pathfinding;
import game.robots.Filling;
import game.robots.Robot;
import game.Entity.State;

/**
 * @author Nicolas Vincent
 * @see Strategie
 */
public class StrategieEvoluee extends Strategie {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrategieElementaire.class);

    /**
     * @param pathfinding
     * @see Strategie#Strategie(Pathfinding)
     */
    public StrategieEvoluee(final Pathfinding pathfinding) {
        super(pathfinding);
    }

    /**
     * Stratégie évoluée. Chaque robot se déplace sur un incendie différent. On
     * choisit le robot disponible le plus rapide. Un robot vide va se remplir à un
     * point d'eau
     * 
     * @param simulateur
     * @see #getPathDuration(Robot, LinkedList, DonneesSimulation)
     * @see DonneesSimulation#getRobots()
     * @see DonneesSimulation#getIncendies()
     * @see Robot#getState()
     * @see Robot#isEmpty()
     * @see Robot#getTimeToFillUp()
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

            long minDuration = Long.MAX_VALUE;
            Robot robotMinDuration = null;
            LinkedList<Integer> pathMinDuration = null;

            for (final Robot robot : robots) {
                if (robot.getState() == State.BUSY)
                    continue;
                if (robot.isEmpty()) {
                    long fillingMinDuration = Long.MAX_VALUE;
                    LinkedList<Integer> fillingPathMinDuration = null;

                    // Le robot roues se remplit à côté d'un point d'eau
                    // Le drone se remplit sur un point d'eau
                    ArrayList<Integer> fillingPositions = null;
                    final Filling robotFilling = robot.getFilling();
                    if (robotFilling == Filling.ON) {
                        fillingPositions = simulateur.getDonneesSimulation().getCarte().getPositionsWater();
                    } else if (robotFilling == Filling.NEXT) {
                        fillingPositions = simulateur.getDonneesSimulation().getCarte().getPositionsVoisinsWater();
                    } else if (robotFilling == Filling.NONE) {
                        continue;
                    }
                    if (fillingPositions == null)
                        continue;

                    for (final int positionWater : fillingPositions) {
                        LOGGER.info(
                                "Recherche d'un chemin pour aller remplir le robot {} au point d'eau le plus proche",
                                robot.getId());

                        LinkedList<Integer> path;
                        try {
                            path = getPathfinding().shortestWay(robot, robot.getPosition(), positionWater);
                        } catch (final IllegalStateException e) {
                            LOGGER.info("Aucun chemin n'est praticable");
                            continue;
                        }

                        long duration = getPathDuration(robot, path, simulateur.getDonneesSimulation());
                        duration += robot.getTimeToFillUp();

                        if (duration < fillingMinDuration) {
                            fillingPathMinDuration = path;
                            fillingMinDuration = duration;
                        }
                    }
                    if (fillingPathMinDuration == null) {
                        LOGGER.info("Il n'existe pas de chemin praticable jusqu'à un point d'eau");
                    } else {
                        // on envoie le robot le plus rapide
                        LOGGER.info("Ajoût des events pour le robot {}", robot.getId());

                        getEventAdder().addPath(robot, fillingPathMinDuration);
                        getEventAdder().addFilling(robot);
                    }
                    continue;
                }
                LOGGER.info("Recherche d'un chemin pour le robot {}", robot.getId());

                LinkedList<Integer> path;
                try {
                    path = getPathfinding().shortestWay(robot, robot.getPosition(), positionIncendie);
                } catch (final IllegalStateException e) {
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

                getEventAdder().addPath(robotMinDuration, pathMinDuration);
                getEventAdder().addEmpty(robotMinDuration, pathMinDuration.getLast());
            }
        }
    }

    /**
     * @param robot
     * @param path
     * @param donneesSimulation
     * @return durée mise par le robot pour se déplacer en suivant path
     * @see DonneesSimulation#getTimeToMove(Robot, int, int)
     */
    private long getPathDuration(final Robot robot, final LinkedList<Integer> path,
            final DonneesSimulation donneesSimulation) {
        long duration = 0;

        final Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            final int nextPosition = iter.next();
            duration += donneesSimulation.getTimeToMove(robot, currentPosition, nextPosition);
            currentPosition = nextPosition;
        }
        return duration;
    }
    
}
