import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

import game.Direction;
import game.DonneesSimulation;
import game.events.EventEmpty;
import game.events.EventFill;
import game.events.EventMove;
import game.robots.Robot;
import gui.GUISimulator;
import io.LecteurDonnees;

public class TestEventsScenario1 {
    public static void main(String[] args) {
        args = new String[]{"cartes/carteSujet.map"};
        DonneesSimulation donneesSimulation = getDonneesSimulation(args);

        int guiSizeFactor = 80;  // à adapter à son écran, spiral: 20, others: 60
        GUISimulator gui = new GUISimulator(
            donneesSimulation.getCarte().getNbLignes() * guiSizeFactor, 
            donneesSimulation.getCarte().getNbColonnes() * guiSizeFactor, 
            Color.BLACK
        );

        Simulateur simulateur = new Simulateur(gui, guiSizeFactor, donneesSimulation);

        Robot robot = donneesSimulation.getRobots().get(6 * donneesSimulation.getCarte().getNbLignes() + 5).get(0);

        long count = 0;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.NORD));
        count += Simulateur.INCREMENT;

        simulateur.addEvent(new EventFill(count, donneesSimulation, robot));
        count += Simulateur.INCREMENT;
        
        simulateur.addEvent(new EventEmpty(count, donneesSimulation, robot));
        count += Simulateur.INCREMENT;

        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.OUEST));
        count += Simulateur.INCREMENT;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.OUEST));
        count += Simulateur.INCREMENT;

        simulateur.addEvent(new EventFill(count, donneesSimulation, robot));
        count += Simulateur.INCREMENT;

        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.EST));
        count += Simulateur.INCREMENT;
        simulateur.addEvent(new EventMove(count, donneesSimulation, robot, Direction.EST));
        count += Simulateur.INCREMENT;

        simulateur.addEvent(new EventEmpty(count, donneesSimulation, robot));
        count += Simulateur.INCREMENT;
    }

    public static DonneesSimulation getDonneesSimulation(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntaxe: java TestLecteurDonnees <nomDeFichier>");
            System.exit(1);
        }
        DonneesSimulation donneesSimulation = null;
        try {
            donneesSimulation = LecteurDonnees.lire(args[0]);
            // System.out.println(donneesSimulation);
        } catch (FileNotFoundException e) {
            System.out.println("fichier " + args[0] + " inconnu ou illisible");
        } catch (DataFormatException e) {
            System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
        }
        return donneesSimulation;
    }
}
