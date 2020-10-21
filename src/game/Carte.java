package game;

import java.util.HashMap;
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
    Map<Integer, NatureTerrain> map;

    public Carte(int nbLignes, int nbColonnes, int tailleCases, Map<Integer, NatureTerrain> map) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.tailleCases = tailleCases;
        this.map = map;
    }

    // TODO: code the thing
    public Case getCase(int ligne, int colonne) {
        return new Case(3, 4, NatureTerrain.EAU); // bullshit return
    }

    // TODO: code the thing
    public boolean voisinExiste(Case src, Direction direction) {
        return false; // bullshit return
    }

    // TODO: code the thing
    public Case getVoisin(Case src, Direction direction) {
        return new Case(3, 4, NatureTerrain.EAU); // bullshit return
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

    public void setMap(Map<Integer, NatureTerrain> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "Carte [map=" + map + ", nbColonnes=" + nbColonnes + ", nbLignes=" + nbLignes + ", tailleCases="
                + tailleCases + "]";
    }
}