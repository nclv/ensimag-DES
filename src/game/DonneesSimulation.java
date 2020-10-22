package game;

import java.util.ArrayList;
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
    // il peut y avoir plusieurs robots sur une même position
    Map<Integer, ArrayList<Robot>> robots;

    public DonneesSimulation() {}

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

    public Map<Integer, ArrayList<Robot>> getRobots() {
        return robots;
    }

    public void setRobots(Map<Integer, ArrayList<Robot>> robots) {
        this.robots = robots;
    }

    @Override
    public String toString() {
        return "DonneesSimulation [carte=" + carte + ", incendies=" + incendies + ", robots=" + robots + "]";
    }
}
