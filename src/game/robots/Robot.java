package game.robots;

import game.Case;
import game.NatureTerrain;

public class Robot {
    private Case position;

    private final Type type;
    private int vitesse;

    public Robot(Type type, int vitesse) {
        this.type = type;
        this.vitesse = vitesse;
    }

    public Case getPosition() {
        return position;
    }

    public void setPosition(Case position) {
        this.position = position;
    }

    // TODO: revoir le type en sortie
    float getVitesse(NatureTerrain natureTerrain);

    void deverserEau(int volume);

    void remplirReservoir();
}
