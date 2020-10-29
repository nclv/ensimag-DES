package game;

import java.util.ArrayList;
import java.util.HashMap;
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
    // 2 way hashmap
    // il peut y avoir plusieurs robots sur une même position
    // a map hold references to its values
    Map<Integer, ArrayList<Robot>> robots;
    Map<Robot, Integer> robotsCoordinates;

    public DonneesSimulation() {}

    public DonneesSimulation(DonneesSimulation another) {
        this.carte = new Carte(another.carte);
        this.incendies = new HashMap<Integer, Integer>(another.incendies);
        this.robots = new HashMap<Integer, ArrayList<Robot>>();
        for (Map.Entry<Integer, ArrayList<Robot>> entry : another.robots.entrySet()) {
            ArrayList<Robot> robotsList = new ArrayList<Robot>();
            for (Robot robot : entry.getValue()) {
                robotsList.add(new Robot(robot));
            }
            this.robots.put(entry.getKey(), robotsList);
        }
        this.robotsCoordinates = new HashMap<Robot, Integer>();
        for (Map.Entry<Robot, Integer> entry : another.robotsCoordinates.entrySet()) {
            this.robotsCoordinates.put(entry.getKey(), entry.getValue());
        };
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

    public Robot getRobot(Robot robot) {
        for (Robot currentRobot : this.robotsCoordinates.keySet()) {
            if (currentRobot.equals(robot)) {
                return currentRobot;
            }
        }
        return null;
    }

    public Map<Robot, Integer> getRobotsCoordinates() {
        return robotsCoordinates;
    }

    public void setRobotsCoordinates(Map<Robot, Integer> robotsCoordinates) {
        this.robotsCoordinates = robotsCoordinates;
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
