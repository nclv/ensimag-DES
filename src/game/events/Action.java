package game.events;

import game.DonneesSimulation;
import game.Entity;
import game.Entity.State;

/**
 * Action exécutée lors d'un event.
 * 
 * @author Nicolas Vincent
 * @see DonneesSimulation
 * @see Entity
 */
public abstract class Action {

    /**
     * Une action utilise les données de la simulation pour s'exécuter
     */
    private DonneesSimulation donneesSimulation;
    
    /**
     * Une action est exécutée par une entité
     */
    private Entity entity;

    /**
     * Constructeur d'une action.
     * Marquage du robot comme étant occupé.
     * 
     * @param donneesSimulation
     * @param entity
     * @see State
     */
    public Action(final DonneesSimulation donneesSimulation, Entity entity) {
        this.donneesSimulation = donneesSimulation;
        this.entity = entity;
        // on marque l'entité comme occupée si elle ne l'est pas déjà
        if (this.entity.getState() == State.FREE) {
            this.entity.setState(State.BUSY);
        }
    }

    /**
     * Exécute l'action.
     */
    public abstract void execute();
    
    /**
     * @return durée de l'action
     * @throws IllegalArgumentException if outside the map or if the robot can't move on the position
     */
    public abstract long getDuration() throws IllegalArgumentException;
    
    /**
     * Utilisé pour copier un Event
     * 
     * @param donneesSimulation
     * @return copie de l'action
     */
    public abstract Action copy(DonneesSimulation donneesSimulation);

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }

    public Entity getEntity() {
        return entity;
    }
}
