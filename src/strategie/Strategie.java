package strategie;

import game.Dated;
import game.Simulateur;
import game.pathfinding.Pathfinding;

/**
 * Un objet stratégie va calculer les déplacements des robots avec l'algorithme de
 * plus court chemin choisi.
 * La méthode execute() est appelée à chaque itération.
 * 
 * @author Nicolas Vincent
 * @see Dated
 */
public abstract class Strategie implements Dated<Long> {
    /** 
     * Algorithme de plus court chemin
     */
    private Pathfinding pathfinding;
    
    /**
     * Compteur interne permettant d'ordonner une suite d'events.
     * Utilisé pour effectuer les actions des robots en série.
     */
    private long date = 0L;

    /**
     * @param pathfinding
     */
    public Strategie(Pathfinding pathfinding) {
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

    public void setPathfinding(Pathfinding pathfinding) {
        this.pathfinding = pathfinding;
    }

    @Override
    public Long getDate() {
        return date;
    }

    @Override
    public void setDate(Long date) {
        this.date = date;
    }
}
