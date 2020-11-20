package game;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Carte représente le terrain.
 * 
 * @author Nicolas Vincent
 */
public class Carte {
    // private static final Logger LOGGER = LoggerFactory.getLogger(Carte.class);
    
    /**
     * On choisit de fixer la taille d'une carte.
     */
    private final int nbLignes, nbColonnes, tailleCases;

    /**
     * A chaque position correspond un type de terrain.
     */
    private final Map<Integer, NatureTerrain> map;

    /**
     * On stocke les positions des terrains de type EAU
     */
    private ArrayList<Integer> positionsWater = null;

    /**
     * On stocke les positions voisines des positions des terrains de type EAU
     */
    private ArrayList<Integer> positionsVoisinsWater = null;

    public Carte(final int nbLignes, final int nbColonnes, final int tailleCases,
            final Map<Integer, NatureTerrain> map) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.tailleCases = tailleCases;
        this.map = map;
    }

    /**
     * Renvoie la position voisine suivant la direction.
     * 
     * @param position
     * @param direction
     * @return -1 si on est en dehors de la carte, la position voisine sinon
     * @see #checkPosition(int, int)
     */
    public int getVoisin(final int position, final Direction direction) throws IllegalArgumentException {
        final int ligne = position / nbLignes;
        final int colonne = position % nbLignes;
        checkPosition(ligne + direction.getDy(), colonne + direction.getDx());
        final int positionVoisin = position + (direction.getDy() * nbLignes + direction.getDx());
        return positionVoisin;
    }

    /**
     * Renvoie la direction selon la position voisine.
     * 
     * @param position
     * @param positionVoisin
     * @return Direction
     * @throws IllegalArgumentException
     * @see #checkPosition(int)
     * @see Direction#getDirection(int)
     * @see Direction#getMult()
     */
    public Direction getDirection(final int position, final int positionVoisin) throws IllegalArgumentException {
        checkPosition(positionVoisin);
        final int ligne = position / nbLignes;
        final int colonne = position % nbLignes;
        final int ligneVoisin = positionVoisin / nbLignes;
        final int colonneVoisin = positionVoisin % nbLignes;
        final int relativePosition = (ligneVoisin - ligne) * Direction.getMult() + (colonneVoisin - colonne);
        return Direction.getDirection(relativePosition);
    }

    /**
     * Renvoie la nature du terrain de la position
     * 
     * @param position
     * @return NatureTerrain
     * @throws IllegalArgumentException
     * @see #checkPosition(int)
     */
    public NatureTerrain getTerrain(final int position) throws IllegalArgumentException {
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
     * @see #getVoisin(int, Direction)
     */
    public NatureTerrain getTerrainVoisin(final int position, final Direction direction)
            throws IllegalArgumentException {
        return map.get(getVoisin(position, direction));
    }

    /**
     * Renvoie true si le type de terrain de la position match celui passé en
     * argument
     * 
     * @param position
     * @param natureTerrain
     * @return Boolean
     * @throws IllegalArgumentException
     * @see #getTerrain(int)
     */
    public Boolean isTerrain(final int position, final NatureTerrain natureTerrain) throws IllegalArgumentException {
        return (getTerrain(position) == natureTerrain);
    }

    /**
     * Renvoie true s'il existe un terrain de type natureTerrain sur une position
     * voisine de position
     * 
     * @param position
     * @param natureTerrain
     * @return
     * @throws IllegalArgumentException
     * @see Direction
     * @see #getTerrainVoisin(int, Direction)
     */
    public Boolean existTerrainVoisin(final int position, final NatureTerrain natureTerrain)
            throws IllegalArgumentException {
        for (final Direction direction : Direction.values()) {
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
     * @see #checkPosition(int)
     */
    private void checkPosition(final int position) throws IllegalArgumentException {
        final int ligne = position / nbLignes;
        final int colonne = position % nbLignes;
        checkPosition(ligne, colonne);
    }

    /**
     * Renvoie une exception si la position n'est pas sur la carte
     * 
     * @param ligne
     * @param colonne
     * @throws IllegalArgumentException
     * @see #isOnMap(int, int)
     */
    private void checkPosition(final int ligne, final int colonne) throws IllegalArgumentException {
        if (!isOnMap(ligne, colonne)) {
            throw new IllegalArgumentException("La position (" + ligne + ", " + colonne + ") n'est pas sur la carte.");
        }
    }

    /**
     * @param ligne
     * @param colonne
     * @return true si la position est sur la carte.
     */
    private Boolean isOnMap(final int ligne, final int colonne) {
        return (ligne >= 0 && colonne >= 0 && ligne < nbLignes && colonne < nbColonnes);
    }

    /**
     * Renvoie une liste des positions voisines de position.
     * 
     * @param position
     * @return ArrayList<Integer> de positions voisines de position
     * @see Direction
     * @see #getVoisin(int, Direction)
     */
    public ArrayList<Integer> getNeighbors(final int position) {
        final ArrayList<Integer> neighbors = new ArrayList<Integer>();
        for (final Direction direction : Direction.values()) {
            try {
                // throw IllegalArgumentException if the voisin is not on the map
                final int neighbor = getVoisin(position, direction);
                neighbors.add(neighbor);
            } catch (final Exception IllegalArgumentException) {
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

    private void initPositionsWater() {
        this.positionsWater = map.entrySet().stream().filter(map -> (map.getValue() == NatureTerrain.EAU))
                .map(map -> map.getKey()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return liste des positions de type EAU
     */
    public ArrayList<Integer> getPositionsWater() {
        if (this.positionsWater == null) {
            initPositionsWater();
        }
        return this.positionsWater;
    }

    /**
     * @return liste des posiitons voisines des positions de type EAU
     */
    public ArrayList<Integer> getPositionsVoisinsWater() {
        if (this.positionsVoisinsWater == null) {
            initPositionsWater();
            this.positionsVoisinsWater = this.positionsWater.stream().map(positionWater -> getNeighbors(positionWater))
                    .flatMap(ArrayList::stream).filter(position -> (getTerrain(position) != NatureTerrain.EAU))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return this.positionsVoisinsWater;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "Carte de taille " + nbLignes + "x" + nbColonnes + "x" + tailleCases + "\n";
        for (final Map.Entry<Integer, NatureTerrain> tEntry : map.entrySet()) {
            res += tEntry.getKey() + ": " + tEntry.getValue().toString() + "\n";
        }
        return res;
    }
}
