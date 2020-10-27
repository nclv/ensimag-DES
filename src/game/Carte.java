package game;

import java.util.Map;

public class Carte {
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

    public NatureTerrain getTerrain(int ligne, int colonne) {
        return map.get(ligne * nbLignes + colonne);
    }

    /**
     * Renvoie la nature du terrain voisin suivant la direction.
     * @param ligne
     * @param colonne
     * @param direction
     * @return null si on est en dehors de la carte, la nature du terrain sinon
     */
    public NatureTerrain getTerrainVoisin(int ligne, int colonne, Direction direction) {
        return map.get((ligne + direction.getDx()) * nbLignes + (colonne + direction.getDy()));
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
