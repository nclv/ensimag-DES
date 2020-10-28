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
    private Robot robot;

    public EventFill(long date, DonneesSimulation donneesSimulation, Robot robot) {
        super(date, donneesSimulation);
        this.robot = robot;

        LOGGER.info("Réception de l'ordre à {}", getDate());
        updateDate(this.robot.getTimeToFillUp());
        LOGGER.info("Fin d'exécution à {}", getDate());
    }

    @Override
    public void execute() {
        Map<Robot, Integer> robotsCoordinates = this.donneesSimulation.getRobotsCoordinates();
        Carte carte = this.donneesSimulation.getCarte();

        // save position
        int position = robotsCoordinates.get(this.robot);

        // on sépare la logique de la carte (position) de celle des robots (filling)
        // le robot se remplit sur sa position, à côté d'une case EAU ou ne se remplit
        // pas
        Boolean canFill = false;
        Filling filling = this.robot.getFilling();
        if (filling == Filling.ON) {
            canFill = carte.isTerrain(position, NatureTerrain.EAU);
        } else if (filling == Filling.NEXT) {
            canFill = carte.doesTerrainVoisinExist(position, NatureTerrain.EAU);
        } else if (filling == Filling.NONE) {
            canFill = true;
        }

        try {
            if (!canFill) {
                throw new IllegalArgumentException("On ne peut pas remplir le robot sur cette position.");
            }

            LOGGER.info("{} en {} se remplit.", this.robot, position);
            // remove old robot
            robotsCoordinates.remove(this.robot);
            this.robot.remplirReservoir();
            LOGGER.info("Il contient maintenant {}L d'eau", this.robot.getVolume());
            // put same robot with updated volume field
            robotsCoordinates.put(this.robot, position);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
