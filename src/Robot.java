public abstract class Robot {
    private Case position;

    public Case getPosition() {
        return position;
    }

    public void setPosition(Case position) {
        this.position = position;
    }

    // TODO: revoir le type en sortie
    abstract float getVitesse(NatureTerrain natureTerrain);

    abstract void deverserEau(int volume);

    abstract void remplirReservoir();
}
