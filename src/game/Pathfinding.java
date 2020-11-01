package game;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Iterator;

public class Pathfinding {

    private static final int NB_LIGNES = 8;
    private static final int NB_COLONNES = 8;
    private static final int TAILLES_CASES = 64;

    private final Carte carte;
    private final int position;

    public Pathfinding(Carte carte, int position) {
        this.carte = carte;
        this.position = position;
    }
    
    /**
     * Comparateur pour la file de priorité
     * basé sur la valeur de l'heuristique.
     */
    public class SimplyEntryComparator implements Comparator<SimpleEntry<Integer, Integer>> {
        public int compare(SimpleEntry<Integer, Integer> first, SimpleEntry<Integer, Integer> second) {
            if (first.getValue() > second.getValue()) return 1;
            if (first.getValue() < second.getValue()) return -1;
            return 0;
        }
    }     

    /**
     * Calcule la distance de Manhattan entre src et dest
     */
    public int manhattanDistance(int src, int dest) {
        int yB = dest / carte.getNbLignes();
        int xB = dest % carte.getNbLignes();
        int yA = src / carte.getNbLignes();
        int xA = src % carte.getNbLignes();
        return Math.abs(xB - xA) + Math.abs(yB - yA);
    }

    /**
     * Initialise les valeurs de la HashMap pour la carte (par défaut à MAX_VALUE)
     */
    public HashMap<Integer, Integer> initializeGScores(Integer value) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < carte.getNbLignes() * carte.getNbColonnes(); i++) {
            map.put((Integer) i, value);
        }
        return map;
    }

    /**
        Supprime le couple (neighbor,gscore) de la file de priorité
     */
    public void removeNeighbor(PriorityQueue<SimpleEntry<Integer, Integer>> queue, Integer neighbor) {
        Iterator<SimpleEntry<Integer, Integer>> iter = queue.iterator();
        while(iter.hasNext()) {
            SimpleEntry<Integer, Integer> current = iter.next();
            if (current.getKey() == neighbor) {
                queue.remove(current);
                break;
            }
        }
    }

    /**
        Calcule le plus court chemin, la solution est l'une des meilleures
     */
    public LinkedList<Integer> shortestWay(int dest) {
        /*File de pairs (position,fScore)*/
        PriorityQueue<SimpleEntry<Integer, Integer>> open = new PriorityQueue<SimpleEntry<Integer, Integer>>(11, new SimplyEntryComparator());//initializeFScores(Integer.MAX_VALUE);
        /*HashMap des gScore*/
        HashMap<Integer, Integer> gScore = initializeGScores(Integer.MAX_VALUE);
        /*HashMap du path*/
        HashMap<Integer, Integer> close = new HashMap<Integer, Integer>();

        /*On start avec la position du robot*/
        open.add(new SimpleEntry<Integer, Integer>(position, manhattanDistance(position, dest)));
        gScore.replace(position, 0);


        while (!open.isEmpty()) {
            SimpleEntry<Integer, Integer> current = open.poll();
            /*Si on a atteint la destination, on reconstruit le chemin*/
            if (current.getKey() == dest) {
                return reconstructPath(close, current.getKey());
            }
            
            /*On explore les voisins*/
            for (Integer neighbor : carte.getNeighbors(current.getKey())) {
                int tentativeGScore = gScore.get(current.getKey()) + 1; //1 is the distance between current and his neighbor
                if (tentativeGScore < gScore.get(neighbor)) {
                    close.put(neighbor, current.getKey());
                    gScore.replace(neighbor, tentativeGScore);
                    removeNeighbor(open, neighbor);
                    SimpleEntry<Integer, Integer> neighborWithCost = new SimpleEntry<Integer, Integer>(neighbor, gScore.get(neighbor) + manhattanDistance(neighbor, dest));
                    open.add(neighborWithCost);
                }
            }
        }
        /* Pas de chemins, on renvoie null*/
        return null;
    }

    /**
        Reconstruit le plus court chemin.
     */
    public LinkedList<Integer> reconstructPath(HashMap<Integer, Integer> map, Integer dest) {
        LinkedList<Integer> path = new LinkedList<Integer>();
        path.add(dest);
        while (map.containsKey(dest)) {
            dest = map.get(dest);
            path.addFirst(dest);
        }
        return path;
    }

    /*Teste une carte*/
    public static Carte testCarte() {
        HashMap<Integer, NatureTerrain> map = new HashMap<Integer, NatureTerrain>();
        for (int i = 0; i < NB_LIGNES * NB_COLONNES; i++) {
            map.put(i, NatureTerrain.TERRAIN_LIBRE);
        }
        map.replace(1, NatureTerrain.ROCHE);
        map.replace(9, NatureTerrain.ROCHE);
        map.replace(17, NatureTerrain.ROCHE);
        map.replace(25, NatureTerrain.ROCHE);
        map.replace(63, NatureTerrain.ROCHE);

        return new Carte(NB_LIGNES, NB_COLONNES, TAILLES_CASES, map);
    }

    @Override
    public String toString() {
        return "Je suis un robot à la position " + String.valueOf(position);
    }

    public static void main(String[] args) {
        /*Génération du robot*/
        Pathfinding pathfinding = new Pathfinding(testCarte(), 0);
        System.out.println(pathfinding);

        /*Calcul du plus court chemin entre (pos_robot, dest)*/
        LinkedList<Integer> path = pathfinding.shortestWay(27);

        /*Affichage du path*/
        System.out.println("Affichage du path:");
        Iterator<Integer> iter = path.iterator();
        while (iter.hasNext()) {
            Integer element = iter.next();
            System.out.println("(" + String.valueOf(element / NB_LIGNES) + "," + String.valueOf(element % NB_LIGNES) + ")");
        }
    }

}