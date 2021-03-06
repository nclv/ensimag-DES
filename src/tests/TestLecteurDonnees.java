package tests;

import io.LecteurDonnees;

import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

import game.DonneesSimulation;

public class TestLecteurDonnees {

    public static void main(final String[] args) {
        if (args.length < 1) {
            System.out.println("Syntaxe: java TestLecteurDonnees <nomDeFichier>");
            System.exit(1);
        }

        try {
            final DonneesSimulation donneesSimulation = LecteurDonnees.lire(args[0]);
            System.out.println(donneesSimulation);
        } catch (final FileNotFoundException e) {
            System.out.println("fichier " + args[0] + " inconnu ou illisible");
        } catch (final DataFormatException e) {
            System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
        }
    }

}
