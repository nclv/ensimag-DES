package game.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.Filling;
import game.robots.Robot;

/**
 * @author Nicolas Vincent
 * @see Robot
 */
public class ActionFill extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionFill.class);

    private Robot robot;

    /**
     * @param donneesSimulation
     * @param robot
     * @see Action#Action(DonneesSimulation, game.Entity)
     */
    public ActionFill(final DonneesSimulation donneesSimulation, final Robot robot) {
        super(donneesSimulation, robot);
        this.robot = robot;
    }

    /**
     * @param donneesSimulation
     * @return nouvelle instance de la classe
     * @see ActionFill#ActionFill(DonneesSimulation, Robot)
     */
    public Action copy(DonneesSimulation donneesSimulation) {
        return new ActionFill(donneesSimulation, this.robot);
    }

    /**
     * @return true if you can fill the robot from his position
     * @see DonneesSimulation#getCarte()
     * @see Robot#getPosition()
     * @see Filling
     * @see Robot#getFilling()
     * @see Carte#isTerrain(int, NatureTerrain)
     * @see Carte#existTerrainVoisin(int, NatureTerrain)
     */
    private Boolean canFill() {
        // on sépare la logique de la carte (position) de celle des robots (filling)
        final Carte carte = getDonneesSimulation().getCarte();
        final int position = this.robot.getPosition();
        // le robot se remplit sur sa position, à côté d'une case EAU ou ne se remplit
        // pas
        Boolean canFill = false;
        final Filling filling = this.robot.getFilling();
        if (filling == Filling.ON) {
            canFill = carte.isTerrain(position, NatureTerrain.EAU);
        } else if (filling == Filling.NEXT) {
            canFill = carte.existTerrainVoisin(position, NatureTerrain.EAU);
        } else if (filling == Filling.NONE) {
            canFill = true;
        }
        return canFill;
    }

    /**
     * Remplit le robot (si possible)
     * 
     * @see #canFill()
     * @see Robot#remplirReservoir()
     */
    @Override
    public void execute() throws IllegalArgumentException {
        if (!canFill()) {
            throw new IllegalArgumentException("On ne peut pas remplir le robot sur cette position.");
        }
        // on remplit si canFill == true
        LOGGER.info("{} en {} se remplit.", this.robot, this.robot.getPosition());
        this.robot.remplirReservoir();
        LOGGER.info("Il contient maintenant {}L d'eau", this.robot.getVolume());
    }
    
    /**
     * @return temps mis pour remplir le robot
     * @see Robot#getTimeToFillUp()
     */
    @Override
    public long getDuration() {
        return this.robot.getTimeToFillUp();
    }
}
