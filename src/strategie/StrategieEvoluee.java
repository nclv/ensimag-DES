package strategie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import game.DonneesSimulation;
import game.Simulateur;
import game.events.Event;
import game.events.EventEmpty;
import game.events.EventMove;
import game.pathfinding.Pathfinding;
import game.robots.Robot;
import game.robots.Robot.State;

public class StrategieEvoluee extends Strategie {

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
                
                LinkedList<Integer> path;
                try {
                    path = getPathfinding().shortestWay(robot, robot.getPosition(), positionIncendie);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    continue;
                }
                
                long duration = getPathDuration(robot, path, simulateur.getDonneesSimulation(), getCount());
                Event eventEmpty = new EventEmpty(getCount(), simulateur.getDonneesSimulation(), robot);
                duration += eventEmpty.getDuration();

                if (duration < minDuration) {
                    robotMinDuration = robot;
                    pathMinDuration = path;
                    minDuration = duration;
                }
            }

            // on envoie le robot le plus rapide
            simulateur.addEventsMove(robotMinDuration, pathMinDuration, getCount());
            setCount(this.count + pathMinDuration.size() * Simulateur.INCREMENT);
            simulateur.addEvent(new EventEmpty(getCount(), simulateur.getDonneesSimulation(), robotMinDuration));
            setCount(this.count + Simulateur.INCREMENT);
        }
    }

    @Override
    public Boolean canFree(Robot robot) {
        return true;
    }

    private long getPathDuration(Robot robot, LinkedList<Integer> path, DonneesSimulation donneesSimulation, long count) {
        long duration = 0;

        Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            Event event = new EventMove(count, donneesSimulation, robot, donneesSimulation.getCarte().getDirection(currentPosition, nextPosition));
            count += Simulateur.INCREMENT;

            duration += event.getDuration();

            currentPosition = nextPosition;
        }
        return duration;
    }
    
}
