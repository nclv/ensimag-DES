package game;

import java.util.Map;

import game.robots.Robot;

public class DonneesSimulation {
    /**
     * DonneesSimulation est "propriétaire" des instances de Incendie, Carte et
     * Robot qui la composent. Si les données de la simulation sont détruites, les
     * incendies, la carte et les robots le sont également.
     * 
     * Il peut n'y avoir aucun incendie. Il n'y a pas de limite au nombre
     * d'incendies.
     * 
     * Il y a au moins un robot. Il n'y a pas de limite au nombre de robots.
     * 
     * Il y a une carte.
     */
    private Carte carte;
    Map<Integer, Integer> incendies;
    Map<Integer, Robot> robots;

    public DonneesSimulation() {}

    @Override
    public String toString() {
        return "DonneesSimulation [carte=" + carte + ", incendies=" + incendies + ", robots=" + robots + "]";
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public Map<Integer, Integer> getIncendies() {
        return incendies;
    }

    public void setIncendies(Map<Integer, Integer> incendies) {
        this.incendies = incendies;
    }

    public Map<Integer, Robot> getRobots() {
        return robots;
    }

    public void setRobots(Map<Integer, Robot> robots) {
        this.robots = robots;
    }
}
