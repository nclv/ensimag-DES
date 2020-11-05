package game;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.IntStream;

import game.robots.Robot;

import java.util.Comparator;
import java.util.Iterator;

public class Pathfinding {
    private final DonneesSimulation donneesSimulation;

    public Pathfinding(DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
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
        final Carte carte = this.donneesSimulation.getCarte();
        int yB = dest / carte.getNbLignes();
        int xB = dest % carte.getNbLignes();
        int yA = src / carte.getNbLignes();
        int xA = src % carte.getNbLignes();
        return Math.abs(xB - xA) + Math.abs(yB - yA);
    }

    /**
     * Initialise les valeurs de la HashMap pour la carte (par défaut à MAX_VALUE)
     */
    public HashMap<Integer, Integer> initializeGScores(int value) {
        int size = this.donneesSimulation.getCarte().getNbLignes() * this.donneesSimulation.getCarte().getNbColonnes();
        HashMap<Integer, Integer> map = IntStream.range(0, size).collect(HashMap::new, (m, position) -> m.put(position, value), Map::putAll);
        return map;
    }

    /**
     * Supprime le couple (neighbor,gscore) de la file de priorité
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
     * Calcule le plus court chemin, la solution est l'une des meilleures
     */
    public LinkedList<Integer> shortestWay(Robot robot, int src, int dest) {
        /*File de pairs (position,fScore)*/
        PriorityQueue<SimpleEntry<Integer, Integer>> open = new PriorityQueue<SimpleEntry<Integer, Integer>>(11, new SimplyEntryComparator());//initializeFScores(Integer.MAX_VALUE);
        /*HashMap des gScore*/
        HashMap<Integer, Integer> gScore = initializeGScores(Integer.MAX_VALUE);
        /*HashMap du path*/
        HashMap<Integer, Integer> close = new HashMap<Integer, Integer>();

        /* On start avec la position du robot */
        open.add(new SimpleEntry<Integer, Integer>(src, manhattanDistance(src, dest)));
        gScore.replace(src, 0);


        while (!open.isEmpty()) {
            int position = open.poll().getKey();
            /* Si on a atteint la destination, on reconstruit le chemin */
            if (position == dest) {
                return reconstructPath(close, position);
            }
            
            /* On explore les voisins */
            for (Integer neighbor : this.donneesSimulation.getCarte().getNeighbors(position)) {
                int tentativeGScore = Integer.MAX_VALUE;
                try {
                    tentativeGScore = gScore.get(position) + (int)donneesSimulation.getTimeToMove(robot, position, neighbor);
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                }
                if (tentativeGScore < gScore.get(neighbor)) {
                    close.put(neighbor, position);
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
     * Reconstruit le plus court chemin.
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
}