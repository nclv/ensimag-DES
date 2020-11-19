package game.events;

import java.util.LinkedList;

import game.DonneesSimulation;
import game.robots.Robot;

/**
 * Implémentation concrète d'un EventAdder ajoutant des events en parallèle
 * 
 * @author Nicolas Vincent
 * @see EventAdder
 * @see DonneesSimulation
 * @see EventManager
 */
public class EventAdderParallel extends EventAdder {

    public EventAdderParallel(final DonneesSimulation donneesSimulation, final EventManager eventManager) {
        super(donneesSimulation, eventManager);
    }

    /**
     * @see Robot#getDate()
     * @see Robot#setDate(Long)
     * @see #scheduleActionsMove(Robot, LinkedList, long)
     */
    @Override
    public void addPath(final Robot robot, final LinkedList<Integer> path) {
        final long date = scheduleActionsMove(robot, path, robot.getDate());
        robot.setDate(date);
    }

    /**
     * @see Robot#getDate()
     * @see Robot#setDate(Long)
     * @see ActionEmpty(DonneesSimulation, Robot)
     * @see DonneesSimulation#getTimeToEmpty(Robot, int)
     */
    @Override
    public void addEmpty(final Robot robot, final int firePosition) {
        getEventManager().schedule(robot.getDate(), new ActionEmpty(getDonneesSimulation(), robot));
        robot.setDate(robot.getDate() + getDonneesSimulation().getTimeToEmpty(robot, firePosition));
    }

    /**
     * @see Robot#getDate()
     * @see Robot#setDate(Long)
     * @see ActionFill(DonneesSimulation, Robot)
     * @see DonneesSimulation#getTimeToFillUp()
     */
    @Override
    public void addFilling(final Robot robot) {
        getEventManager().schedule(robot.getDate(), new ActionFill(getDonneesSimulation(), robot));
        robot.setDate(robot.getDate() + robot.getTimeToFillUp());
    }
}
