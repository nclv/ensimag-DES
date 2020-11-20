package game.events;

import java.util.Iterator;
import java.util.LinkedList;

import game.DonneesSimulation;
import game.robots.Robot;

/**
 * Classe abstraite utilisée pour l'ajoût d'évènements dans une stratégie.
 * 
 * @author Nicolas Vincent
 * @see DonneesSimulation
 * @see EventManager
 */
public abstract class EventAdder {
    
    /**
     * Les données de la simulation sont utilisées pour calculer la durée d'un event.
     */
    private DonneesSimulation donneesSimulation;
    
    /**
     * L'eventManager contient la méthode schedule qui permet d'ajouter un event.
     */
    private EventManager eventManager;

    public EventAdder(final DonneesSimulation donneesSimulation, final EventManager eventManager) {
        this.donneesSimulation = donneesSimulation;
        this.eventManager = eventManager;
    }

    /**
     * Ajout d'une suite d'events déplacements à la simulation
     * 
     * @param robot
     * @param path
     * @param date compteur externe ordonnant les déplacements
     * @see EventManager#schedule(long, Action)
     * @see ActionMove#ActionMove(DonneesSimulation, Robot, game.Direction)
     * @see Carte#getDirection(int, int)
     */
    public long scheduleActionsMove(final Robot robot, final LinkedList<Integer> path, long date) {
        final Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            final int nextPosition = iter.next();
            eventManager.schedule(date, new ActionMove(this.donneesSimulation, robot,
                    this.donneesSimulation.getCarte().getDirection(currentPosition, nextPosition)));
            date += donneesSimulation.getTimeToMove(robot, currentPosition, nextPosition);
            currentPosition = nextPosition;
        }
        return date;
    }

    /**
     * Ajoute une suite d'events représentant un chemin
     * 
     * @param robot
     * @param path
     */
    public abstract void addPath(Robot robot, LinkedList<Integer> path);

    /**
     * Ajoute un event exécutant l'action Empty
     * 
     * @param robot
     * @param firePosition
     */
    public abstract void addEmpty(Robot robot, int firePosition);

    /**
     * Ajoute un event exécutant l'action Fill
     * 
     * @param robot
     */
    public abstract void addFilling(Robot robot);

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }

    public void setDonneesSimulation(DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
