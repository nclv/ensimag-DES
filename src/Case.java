public class Case {
    private int ligne, colonne;
    private NatureTerrain natureTerrain;

    public Case(int ligne, int colonne, NatureTerrain natureTerrain) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.natureTerrain = natureTerrain;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public NatureTerrain getNatureTerrain() {
        return natureTerrain;
    }

    public void setNatureTerrain(NatureTerrain natureTerrain) {
        this.natureTerrain = natureTerrain;
    }
}
