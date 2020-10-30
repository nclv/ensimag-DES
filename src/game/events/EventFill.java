package game.events;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.Filling;
import game.robots.Robot;

public class EventFill extends Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventFill.class);

    public EventFill(long date, DonneesSimulation donneesSimulation, Robot robot) {
        super(date, donneesSimulation, robot);
    }

    public EventFill copy(DonneesSimulation donneesSimulation){
        return new EventFill(getDate(), donneesSimulation, getRobot());
    }

    public long getDuration() {
        long timeToFillUp = getRobot().getTimeToFillUp();
        
        LOGGER.info("Réception de l'ordre à {}", getDate());
        
        return timeToFillUp;
    }

    @Override
    public void execute() throws IllegalArgumentException {
        Map<Robot, Integer> robotsCoordinates = this.donneesSimulation.getRobotsCoordinates();
        Carte carte = this.donneesSimulation.getCarte();

        // save position
        int position = robotsCoordinates.get(getRobot());

        // on sépare la logique de la carte (position) de celle des robots (filling)
        // le robot se remplit sur sa position, à côté d'une case EAU ou ne se remplit
        // pas
        Boolean canFill = false;
        Filling filling = getRobot().getFilling();
        if (filling == Filling.ON) {
            canFill = carte.isTerrain(position, NatureTerrain.EAU);
        } else if (filling == Filling.NEXT) {
            canFill = carte.doesTerrainVoisinExist(position, NatureTerrain.EAU);
        } else if (filling == Filling.NONE) {
            canFill = true;
        }

        if (!canFill) {
            throw new IllegalArgumentException("On ne peut pas remplir le robot sur cette position.");
        }

        // on remplit si canFill == true
        LOGGER.info("{} en {} se remplit.", getRobot(), position);
        // remove old robot
        robotsCoordinates.remove(getRobot());
        getRobot().remplirReservoir();
        LOGGER.info("Il contient maintenant {}L d'eau", getRobot().getVolume());
        // put same robot with updated volume field
        robotsCoordinates.put(getRobot(), position);
    }
}
