package game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.robots.Robot;

public class DonneesSimulation {
    private static final Logger LOGGER = LoggerFactory.getLogger(DonneesSimulation.class);
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
    // a map hold references to its values
    Map<Integer, ArrayList<Robot>> robots;

    public DonneesSimulation() {}

    public DonneesSimulation(DonneesSimulation another) {
        this.carte = another.carte; // la carte ne change pas entre deux restarts
        this.incendies = new HashMap<Integer, Integer>(another.incendies);

        // on rappelle que les 2 HashMaps doivent être synchrones.
        this.robots = new HashMap<Integer, ArrayList<Robot>>();
        // LOGGER.info("Another robots: {}", another.robots);
        for (Map.Entry<Integer, ArrayList<Robot>> entry : another.robots.entrySet()) {
            for (Robot robot : entry.getValue()) {
                robot.init(entry.getKey()); // on ne cré pas de nouveaux objets robots, on les réinitialise
                this.robots.computeIfAbsent(entry.getKey(), k -> new ArrayList<Robot>()).add(robot);
            }
        }
        // LOGGER.info("My robots: {}", this.robots);
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

    public Map<Integer, ArrayList<Robot>> getRobots() {
        return robots;
    }

    public void setRobots(Map<Integer, ArrayList<Robot>> robots) {
        this.robots = robots;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "DonneesSimulation: \n";
        res += carte;
        res += "Incendies\n";
        for (Map.Entry<Integer, Integer> iEntry : incendies.entrySet()) {
            res += iEntry.getKey() + ": " + iEntry.getValue().toString() + "\n";
        }
        res += "Robots\n";
        for (Map.Entry<Integer, ArrayList<Robot>> rlEntry : robots.entrySet()) {
            for (Robot robot : rlEntry.getValue()) {
                res += rlEntry.getKey() + ": " + robot + "\n";
            }
        }
        return res;
    }
}
