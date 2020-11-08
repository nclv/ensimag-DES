package game.events;

import game.DonneesSimulation;
import game.robots.Robot;
import game.robots.Robot.State;

public abstract class Action {
    private DonneesSimulation donneesSimulation;
    private Robot robot;

    public Action(final DonneesSimulation donneesSimulation, final Robot robot) {
        this.donneesSimulation = donneesSimulation;
        this.robot = robot;
        // on marque le robot comme occupé s'il ne l'est pas déjà
        if (this.robot.getState() == State.FREE) {
            this.robot.setState(State.BUSY);
        }
    }

    /**
     * Exécute l'action
     */
    public abstract void execute();
    
    /**
     * @return durée de l'action
     * @throws IllegalArgumentException if outside the map or if the robot can't move on the position
     */
    public abstract long getDuration() throws IllegalArgumentException;
    
    /**
     * Utilisé pour copier un Event
     * 
     * @return copie de l'action
     */
    public abstract Action copy(DonneesSimulation donneesSimulation);

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }

    public Robot getRobot() {
        return robot;
    }

    @Override
    public String toString() {
        return this.robot + "\n" + donneesSimulation.getRobots();
    }
}
