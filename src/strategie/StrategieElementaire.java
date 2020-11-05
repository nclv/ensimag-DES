package strategie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import game.Pathfinding;
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
        LinkedList<Integer> path;

        for(Map.Entry<Integer, Integer> incendie: simulateur.getDonneesSimulation().getIncendies().entrySet()) {
            int intensite = incendie.getValue();
            if (intensite == 0) continue;
            
            int positionIncendie = incendie.getKey();
            for (Robot robot : robots) {
                if (robot.getState() == State.BUSY) continue;
                
                path = pathfinding.shortestWay(robot, robot.getPosition(), positionIncendie);
                if (path == null) continue;
                
                addEventsMove(simulateur, robot, path);
                simulateur.addEvent(new EventEmpty(getCount(), simulateur.getDonneesSimulation(), robot));
                setCount(count + Simulateur.INCREMENT);

                if (robot.getVolume() == 0.0) robot.setState(State.BUSY);
            }
        }
    }
}
