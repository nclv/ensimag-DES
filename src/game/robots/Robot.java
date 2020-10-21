package game.robots;

import game.NatureTerrain;

// on ne peut pas instancier une classe abstraite
public class Robot {
    private final Type type;
    private int vitesse;

    public Robot(Type type, int vitesse) {
        this.type = type;
        this.vitesse = vitesse;
    }

    // TODO: revoir le type en sortie
    float getVitesse(NatureTerrain natureTerrain) {
        return 0.0f;
    }

    void deverserEau(int volume) {}

    void remplirReservoir() {}

    public Type getType() {
        return type;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }
}
