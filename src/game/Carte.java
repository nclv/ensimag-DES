package game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Carte {
    private static final Logger LOGGER = LoggerFactory.getLogger(Carte.class);
    /**
     * Pour le moment on choisit de fixer la taille d'une carte, on ne peut pas
     * redimmensionner la carte. Donc pas de setters pour les attributs nbLignes et
     * nbColonnes.
     */
    private final int nbLignes, nbColonnes, tailleCases;

    /**
     * Comment stocker une matrice d'objects ?
     * 
     * use bidimentional arrays such as int[][] but the size must be known
     * 
     * use HashMap<Coordinate, Data>, Coordinate may be a Point, It does not
     * guarantee any order of the elements stored internally in the map.
     */
    // on peut préciser la capacité initiale en paramètre: nbLignes * nbColonnes
    // See https://www.javatpoint.com/java-hashmap
    // Map<Coordinate, NatureTerrain> map = new HashMap<Coordinate,
    // NatureTerrain>();
    // Map<Integer, Map<Integer, Tile>>
    private final Map<Integer, NatureTerrain> map;

    public Carte(int nbLignes, int nbColonnes, int tailleCases, Map<Integer, NatureTerrain> map) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.tailleCases = tailleCases;
        this.map = map;
    }

    /**
     * Renvoie la position voisine suivant la direction.
     * 
     * @param ligne
     * @param colonne
     * @param direction
     * @return -1 si on est en dehors de la carte, la position voisine sinon
     */
    public int getVoisin(int position, Direction direction) throws IllegalArgumentException {
        int positionVoisin = position + (direction.getDy() * nbLignes + direction.getDx());
        checkPosition(positionVoisin);
        return positionVoisin;
    }

    public NatureTerrain getTerrain(int position) throws IllegalArgumentException {
        checkPosition(position);
        return map.get(position);
    }

    public NatureTerrain getTerrainVoisin(int position, Direction direction) throws IllegalArgumentException {
        return map.get(getVoisin(position, direction));
    }

    public Boolean isTerrain(int position, NatureTerrain natureTerrain) throws IllegalArgumentException {
        return (getTerrain(position) == natureTerrain);
    }

    public Boolean doesTerrainVoisinExist(int position, NatureTerrain natureTerrain) throws IllegalArgumentException {
        for (Direction direction : Direction.values()) {
            if (getTerrainVoisin(position, direction) == natureTerrain) {
                return true;
            }
        }
        return false;
    }

    private void checkPosition(int position) throws IllegalArgumentException {
        int ligne = position / nbLignes;
        int colonne = position % nbLignes;
        if (!isOnmap(ligne, colonne)) {
            throw new IllegalArgumentException("La position (" + ligne + ", " + colonne + ") n'est pas sur la carte.");
        }
    }

    public LinkedList<Integer> getNeighbors(int position) {
        LinkedList<Integer> neighbors = new LinkedList<Integer>();
        for (Direction direction : Direction.values()) {
            try {
                int neighbor = getVoisin(position, direction);
                neighbors.add(neighbor);
            } catch (Exception IllegalArgumentException) {
            }
        }
        return neighbors;
    }

    private Boolean isOnmap(int ligne, int colonne) {
        return (ligne >= 0 && colonne >= 0 && ligne < nbLignes && colonne < nbColonnes);
    }

    public int getNbLignes() {
        return this.nbLignes;
    }

    public int getNbColonnes() {
        return this.nbColonnes;
    }

    public int getTailleCases() {
        return this.tailleCases;
    }

    public Map<Integer, NatureTerrain> getMap() {
        return map;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "Carte de taille " + nbLignes + "x" + nbColonnes + "x" + tailleCases + "\n";
        for (Map.Entry<Integer, NatureTerrain> tEntry : map.entrySet()) {
            res += tEntry.getKey() + ": " + tEntry.getValue().toString() + "\n";
        }
        return res;
    }
}
