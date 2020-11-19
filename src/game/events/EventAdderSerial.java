package game.events;

import java.util.LinkedList;

import game.DonneesSimulation;
import game.robots.Robot;
import strategie.Strategie;

/**
 * Implémentation concrète d'un EventAdder ajoutant des events en série.
 * 
 * @author Nicolas Vincent
 * @see EventAdder
 * @see Strategie
 * @see DonneesSimulation
 * @see EventManager
 */
public class EventAdderSerial extends EventAdder {
    private final Strategie strategie;

    public EventAdderSerial(final DonneesSimulation donneesSimulation, final Strategie strategie,
            final EventManager eventManager) {
        super(donneesSimulation, eventManager);
        this.strategie = strategie;
    }

    /**
     * @see Strategie#getDate()
     * @see Strategie#setDate(Long)
     * @see #scheduleActionsMove(Robot, LinkedList, long)
     */
    @Override
    public void addPath(final Robot robot, final LinkedList<Integer> path) {
        assert this.strategie != null;
        final long date = scheduleActionsMove(robot, path, this.strategie.getDate());
        this.strategie.setDate(date);
    }

    /**
     * @see Strategie#getDate()
     * @see Strategie#setDate(Long)
     * @see ActionEmpty(DonneesSimulation, Robot)
     * @see DonneesSimulation#getTimeToEmpty(Robot, int)
     */
    @Override
    public void addEmpty(final Robot robot, final int firePosition) {
        assert this.strategie != null;
        getEventManager().schedule(this.strategie.getDate(), new ActionEmpty(getDonneesSimulation(), robot));
        this.strategie.setDate(this.strategie.getDate() + getDonneesSimulation().getTimeToEmpty(robot, firePosition));
    }

    /**
     * @see Strategie#getDate()
     * @see Strategie#setDate(Long)
     * @see ActionFill(DonneesSimulation, Robot)
     * @see DonneesSimulation#getTimeToFillUp()
     */
    @Override
    public void addFilling(final Robot robot) {
        assert this.strategie != null;
        getEventManager().schedule(this.strategie.getDate(), new ActionFill(getDonneesSimulation(), robot));
        this.strategie.setDate(this.strategie.getDate() + robot.getTimeToFillUp());
    }
    
}
