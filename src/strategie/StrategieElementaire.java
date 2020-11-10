package strategie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.pathfinding.Pathfinding;
import game.Simulateur;
import game.robots.Robot;
import game.robots.Robot.State;

public class StrategieElementaire extends Strategie {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrategieElementaire.class);

    public StrategieElementaire(Pathfinding pathfinding) {
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
                
                LOGGER.info("Ajoût des events");
                
                // exécution en série
                // simulateur.addPathSerial(robot, path);
                // simulateur.addEmptySerial(robot);

                // exécution en parallèle
                simulateur.addPathParallel(robot, path);
                simulateur.addEmptyParallel(robot);
            }
        }
    }

    @Override
    public Boolean canFree(Robot robot) {
        return (robot.getVolume() != 0.0);
    }
}
