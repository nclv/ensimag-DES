package game;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class Carte {
    // private static final Logger LOGGER = LoggerFactory.getLogger(Carte.class);
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
        int ligne = position / nbLignes;
        int colonne = position % nbLignes;
        checkPosition(ligne + direction.getDy(), colonne + direction.getDx());
        int positionVoisin = position + (direction.getDy() * nbLignes + direction.getDx());
        return positionVoisin;
    }

    /**
     * Renvoie la direction selon la position voisine.
     * 
     * @param position
     * @param positionVoisin
     * @return Direction
     * @throws IllegalArgumentException
     */
    public Direction getDirection(int position, int positionVoisin) throws IllegalArgumentException {
        checkPosition(positionVoisin);
        int ligne = position / nbLignes;
        int colonne = position % nbLignes;
        int ligneVoisin = positionVoisin / nbLignes;
        int colonneVoisin = positionVoisin % nbLignes;
        return Direction.getDirection((ligneVoisin - ligne) * Direction.getMult() + (colonneVoisin - colonne));
    }

    /**
     * Renvoie la nature du terrain de la position
     * 
     * @param position
     * @return NatureTerrain
     * @throws IllegalArgumentException
     */
    public NatureTerrain getTerrain(int position) throws IllegalArgumentException {
        checkPosition(position);
        return map.get(position);
    }

    /**
     * Renvoie la nature du terrain de la position voisine
     * 
     * @param position
     * @param direction
     * @return NatureTerrain
     * @throws IllegalArgumentException
     */
    public NatureTerrain getTerrainVoisin(int position, Direction direction) throws IllegalArgumentException {
        return map.get(getVoisin(position, direction));
    }

    /**
     * Renvoie true si le type de terrain de la position match celui passé en argument
     * 
     * @param position
     * @param natureTerrain
     * @return Boolean
     * @throws IllegalArgumentException
     */
    public Boolean isTerrain(int position, NatureTerrain natureTerrain) throws IllegalArgumentException {
        return (getTerrain(position) == natureTerrain);
    }

    /**
     * Renvoie true s'il existe un terrain de type natureTerrain sur une position voisine de position
     * 
     * @param position
     * @param natureTerrain
     * @return
     * @throws IllegalArgumentException
     */
    public Boolean existTerrainVoisin(int position, NatureTerrain natureTerrain) throws IllegalArgumentException {
        for (Direction direction : Direction.values()) {
            if (getTerrainVoisin(position, direction) == natureTerrain) {
                return true;
            }
        }
        return false;
    }

    /**
     * Renvoie une exception si la position n'est pas sur la carte
     * 
     * @param position
     * @throws IllegalArgumentException
     */
    private void checkPosition(int position) throws IllegalArgumentException {
        int ligne = position / nbLignes;
        int colonne = position % nbLignes;
        checkPosition(ligne, colonne);
    }

    /**
     * Renvoie une exception si la position n'est pas sur la carte
     * 
     * @param ligne
     * @param colonne
     * @throws IllegalArgumentException
     */
    private void checkPosition(int ligne, int colonne) throws IllegalArgumentException {
        if (!isOnMap(ligne, colonne)) {
            throw new IllegalArgumentException("La position (" + ligne + ", " + colonne + ") n'est pas sur la carte.");
        }
    }

    /**
     * Renvoie true si la position est sur la carte.
     * 
     * @param ligne
     * @param colonne
     * @return
     */
    private Boolean isOnMap(int ligne, int colonne) {
        return (ligne >= 0 && colonne >= 0 && ligne < nbLignes && colonne < nbColonnes);
    }

    /**
     * Renvoie une liste des positions voisines de position.
     * 
     * @param position
     * @return ArrayList<Integer> de positions voisines de position
     */
    public ArrayList<Integer> getNeighbors(int position) {
        ArrayList<Integer> neighbors = new ArrayList<Integer>();
        for (Direction direction : Direction.values()) {
            try {
                // throw IllegalArgumentException if the voisin is not on the map
                int neighbor = getVoisin(position, direction);
                neighbors.add(neighbor);
            } catch (Exception IllegalArgumentException) {
            }
        }
        // LOGGER.info("voisins: {}", neighbors);
        return neighbors;
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
