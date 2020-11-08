package game.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.Filling;
import game.robots.Robot;

public class ActionFill extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionFill.class);

    public ActionFill(final DonneesSimulation donneesSimulation, final Robot robot) {
        super(donneesSimulation, robot);
    }

    public Action copy(DonneesSimulation donneesSimulation) {
        return new ActionFill(donneesSimulation, getRobot());
    }

    /**
     * Renvoie true si on peut remplir le robot
     * 
     * @return true if you can fill the robot from his position
     */
    private Boolean canFill() {
        // on sépare la logique de la carte (position) de celle des robots (filling)
        final Carte carte = getDonneesSimulation().getCarte();
        final int position = getRobot().getPosition();
        // le robot se remplit sur sa position, à côté d'une case EAU ou ne se remplit
        // pas
        Boolean canFill = false;
        final Filling filling = getRobot().getFilling();
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
     */
    @Override
    public void execute() throws IllegalArgumentException {
        if (!canFill()) {
            throw new IllegalArgumentException("On ne peut pas remplir le robot sur cette position.");
        }
        // on remplit si canFill == true
        LOGGER.info("{} en {} se remplit.", getRobot(), getRobot().getPosition());
        getRobot().remplirReservoir();
        LOGGER.info("Il contient maintenant {}L d'eau", getRobot().getVolume());
    }
    
    /**
     * @return temps mis pour remplir le robot 
     */
    @Override
    public long getDuration() {
        return getRobot().getTimeToFillUp();
    }
}