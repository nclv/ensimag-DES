package game.pathfinding;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

import game.Carte;
import game.DonneesSimulation;
import game.robots.Robot;

import java.util.Iterator;

public class AStar extends Pathfinding implements Heuristique {
    private final DonneesSimulation donneesSimulation;

    static class Node implements Comparable<Node> {
        private int position;
        // fScore = gScore + heuristic = the path cost of a node + the heuristic used to
        // estimate distance between a node and the goal
        private int fScore;

        public Node(int position, int fScore) {
            this.position = position;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(Node other) {
            if (this.fScore > other.fScore)
                return 1;
            if (this.fScore < other.fScore)
                return -1;
            return 0;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getfScore() {
            return fScore;
        }

        public void setfScore(int fScore) {
            this.fScore = fScore;
        }
    }

    public AStar(DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
    }

    /**
     * Implémentation de notre heuristique. On choisit la distance de Manhattan
     * 
     * @param src
     * @param dest
     * @return distance de Manhattan entre src et dst
     */
    @Override
    public int heuristique(int src, int dest) {
        final Carte carte = this.donneesSimulation.getCarte();
        int yB = dest / carte.getNbLignes();
        int xB = dest % carte.getNbLignes();
        int yA = src / carte.getNbLignes();
        int xA = src % carte.getNbLignes();
        return Math.abs(xB - xA) + Math.abs(yB - yA);
    }

    /**
     * Supprime le couple (neighbor,gscore) de la file de priorité
     */
    private void removeNeighbor(PriorityQueue<Node> queue, Integer neighbor) {
        Iterator<Node> iter = queue.iterator();
        while (iter.hasNext()) {
            Node current = iter.next();
            if (current.getPosition() == neighbor) {
                queue.remove(current);
                break;
            }
        }
    }

    /**
     * Calcule le plus court chemin, la solution est l'une des meilleures
     */
    @Override
    public LinkedList<Integer> shortestWay(Robot robot, int src, int dest) throws IllegalStateException {
        /* File de paires (position, fScore) */
        PriorityQueue<Node> openSet = new PriorityQueue<Node>();
        
        /* Initialisation des des gScore à Integer.MAX_VALUE */
        HashMap<Integer, Integer> gScore = IntStream
                .range(0,
                        this.donneesSimulation.getCarte().getNbLignes()
                                * this.donneesSimulation.getCarte().getNbColonnes())
                .collect(HashMap::new, (m, position) -> m.put(position, Integer.MAX_VALUE), Map::putAll);
        
        /* Chemin entre src et dst */
        HashMap<Integer, Integer> cameFrom = new HashMap<Integer, Integer>();

        /* On start avec la position du robot */
        openSet.add(new Node(src, heuristique(src, dest)));
        gScore.replace(src, 0);

        while (!openSet.isEmpty()) {
            int position = openSet.poll().getPosition();
            /* Si on a atteint la destination, on reconstruit le chemin */
            if (position == dest) {
                return reconstructPath(cameFrom, position);
            }

            /* On explore les voisins */
            for (Integer neighbor : this.donneesSimulation.getCarte().getNeighbors(position)) {
                // tentativeGScore is the distance from start to the neighbor through position
                int tentativeGScore = Integer.MAX_VALUE;
                try {
                    tentativeGScore = gScore.get(position)
                            + (int) donneesSimulation.getTimeToMove(robot, position, neighbor);
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                }
                if (tentativeGScore < gScore.get(neighbor)) {
                    // This path to neighbor is better than any previous one. Record it!
                    cameFrom.put(neighbor, position);
                    gScore.replace(neighbor, tentativeGScore);
                    removeNeighbor(openSet, neighbor);
                    Node neighborWithCost = new Node(neighbor, gScore.get(neighbor) + heuristique(neighbor, dest));
                    openSet.add(neighborWithCost);
                }
            }
        }
        /* Pas de chemins, on renvoie une exception */
        throw new IllegalStateException("No route found");
    }

    /**
     * Reconstruit le plus court chemin.
     */
    private LinkedList<Integer> reconstructPath(HashMap<Integer, Integer> map, Integer dest) {
        LinkedList<Integer> path = new LinkedList<Integer>();
        path.add(dest);
        while (map.containsKey(dest)) {
            dest = map.get(dest);
            path.addFirst(dest);
        }
        return path;
    }
}