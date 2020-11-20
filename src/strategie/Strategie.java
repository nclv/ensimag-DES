package strategie;

import game.Dated;
import game.Simulateur;
import game.events.EventAdder;
import game.pathfinding.Pathfinding;

/**
 * Un objet stratégie va calculer les déplacements des robots avec l'algorithme de
 * plus court chemin choisi.
 * La méthode execute() est appelée à chaque itération.
 * 
 * @author Nicolas Vincent
 * @see Dated
 * @see Pathfinding
 * @see EventAdder
 */
public abstract class Strategie implements Dated<Long> {
    /** 
     * Algorithme de plus court chemin
     */
    private final Pathfinding pathfinding;

    /**
     * Compteur interne permettant d'ordonner une suite d'events. Utilisé pour
     * effectuer les actions des robots en série.
     */
    private long date = 0L;

    private EventAdder eventAdder = null;

    /**
     * @param pathfinding
     */
    public Strategie(final Pathfinding pathfinding) {
        this.pathfinding = pathfinding;
    }

    /**
     * Exécute la stratégie.
     * 
     * @param simulateur
     */
    public abstract void execute(Simulateur simulateur);

    public Pathfinding getPathfinding() {
        return pathfinding;
    }

    public EventAdder getEventAdder() {
        return eventAdder;
    }

    public void setEventAdder(final EventAdder eventAdder) {
        this.eventAdder = eventAdder;
    }

    @Override
    public Long getDate() {
        return date;
    }

    @Override
    public void setDate(final Long date) {
        this.date = date;
    }
}
