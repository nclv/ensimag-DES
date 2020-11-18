package game;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.robots.Robot;

/**
 * DonneesSimulation est "propriétaire" des instances de Incendie, Carte et
 * Robot qui la composent. Si les données de la simulation sont détruites, les
 * incendies, la carte et les robots le sont également.
 * 
 * Il peut n'y avoir aucun incendie. Il n'y a pas de limite au nombre
 * d'incendies.
 * Il y a au moins un robot. Il n'y a pas de limite au nombre de robots.
 * Il y a une carte.
 * 
 * Cette classe contient aussi les méthodes utilisant une combinaison de ses attributs.
 * 
 * @author Nicolas Vincent
 */
public class DonneesSimulation {
    // private static final Logger LOGGER = LoggerFactory.getLogger(DonneesSimulation.class);
    
    private Carte carte;
    
    /**
     * A une position correspond une intensité
     */
    private Map<Integer, Integer> incendies;
    
    /**
     * On peut trouver plusieurs robots sur une même position
     */
    private Map<Integer, ArrayList<Robot>> robots;

    public DonneesSimulation() {}

    /**
     * Copy constructor
     * Robots are reinitialized
     * 
     * @param another objet DonneesSimulation copié
     * @see Robot#init(int)
     */
    public DonneesSimulation(DonneesSimulation another) {
        this.carte = another.carte; // la carte ne change pas entre deux restarts
        this.incendies = new HashMap<Integer, Integer>(another.incendies);

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

    /**
     * @param robotId
     * @return the robot which id match
     * @see Robot#getId()
     */
    public Robot getRobot(long robotId) {
        ArrayList<Robot> robotsList = new ArrayList<Robot>();
        this.robots.values().forEach(robotsList::addAll);
        for (Robot robot : robotsList) {
            if (robot.getId().equals(robotId)) {
                return robot;
            }
        }
        return null;
    }

    /**
     * Le temps mis pour se rendre d’une case à l’autre est l'inverse de la moyenne de la
     * vitesse sur chacune des cases multipliée par la taille des cases.
     * 
     * @param position
     * @param newPosition
     * @return temps mis pour se déplacer de position à newPosition
     * @throws IllegalArgumentException if the robot can't move on newPosition
     * @see Robot#getVitesse(NatureTerrain)
     * @see Carte#getTerrain(int)
     * @see Carte#getTailleCases()
     */
    public long getTimeToMove(final Robot robot, final int position, final int newPosition) throws IllegalArgumentException {
        // LOGGER.info("{}km/h sur {}, {}km/h sur {}, pour des cases de taille {}",
        // robot.getVitesse(this.carte.getTerrain(position)),
        // this.carte.getTerrain(position),
        // robot.getVitesse(this.carte.getTerrain(newPosition)),
        // this.carte.getTerrain(newPosition),
        // this.carte.getTailleCases());

        // throws IllegalArgumentException if the robot can't move on the position
        return (long) (1 / ((robot.getVitesse(this.carte.getTerrain(position))
                + robot.getVitesse(this.carte.getTerrain(newPosition))) / 2) * carte.getTailleCases());
    }

    /**
     * @param robot
     * @param firePosition
     * @return temps mis par le robot pour se vider sur firePosition
     * @see Robot#getMaxTimeToEmpty()
     * @see Robot#getMaxEmptiedVolume()
     */
    public long getTimeToEmpty(final Robot robot, final int firePosition) {
        long timeToEmpty = 0;
        // calcul du temps mis pour éteindre complètement l'incendie
        final Integer intensity = this.incendies.get(firePosition);
        if (intensity != null) {
            // temps mis pour une extinction globale
            // getMaxTimeToEmpty renvoie le temps mis par le robot pour déverser la quantité maximale d'eau qu'il peut déverser
            timeToEmpty = robot.getMaxTimeToEmpty() * (intensity / robot.getMaxEmptiedVolume());
        }

        return timeToEmpty;
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
