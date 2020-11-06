package strategie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import game.pathfinding.Pathfinding;
import game.Simulateur;
import game.events.EventEmpty;
import game.robots.Robot;
import game.robots.Robot.State;

public class StrategieElementaire extends Strategie {

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
                
                LinkedList<Integer> path;
                try {
                    path = getPathfinding().shortestWay(robot, robot.getPosition(), positionIncendie);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    continue;
                }
                
                addEventsMove(simulateur, robot, path);
                simulateur.addEvent(new EventEmpty(getCount(), simulateur.getDonneesSimulation(), robot));
                setCount(count + Simulateur.INCREMENT);
            }
        }
    }

    @Override
    public Boolean canFree(Robot robot) {
        return (robot.getVolume() != 0.0);
    }
}
