package game.pathfinding;

/**
 * Utilisé pour implémenter notre heuristique.
 * 
 * @author Nicolas Vincent
 */
public interface Heuristique {
    int heuristique(int src, int dest);
}
