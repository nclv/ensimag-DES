package game.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.Filling;
import game.robots.Robot;

public class EventFill extends Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventFill.class);

    public EventFill(final long date, final DonneesSimulation donneesSimulation, final Robot robot) {
        super(date, donneesSimulation, robot);
    }

    public EventFill copy(final DonneesSimulation donneesSimulation) {
        return new EventFill(getDate(), donneesSimulation, getRobot());
    }

    public long getDuration() {
        return getRobot().getTimeToFillUp();
    }

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
            canFill = carte.doesTerrainVoisinExist(position, NatureTerrain.EAU);
        } else if (filling == Filling.NONE) {
            canFill = true;
        }
        return canFill;
    }

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
}
