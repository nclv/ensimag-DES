import java.util.HashMap;
import java.util.Map;

public class Carte {
    /**
     * Pour le moment on choisit de fixer la taille d'une carte, on ne peut pas
     * redimmensionner la carte. Donc pas de setters pour les attributs nbLignes et
     * nbColonnes.
     */
    private final int nbLignes, nbColonnes;

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
    Map<Coordinate, NatureTerrain> map = new HashMap<Coordinate, NatureTerrain>();

    public Carte(int nbLignes, int nbColonnes) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
    }

    // TODO: code the thing
    public int getTailleCases() {
        return 0; // bullshit return
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
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }
}
