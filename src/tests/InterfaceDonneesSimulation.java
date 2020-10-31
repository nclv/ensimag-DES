package tests;

import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

import game.DonneesSimulation;
import io.LecteurDonnees;

interface InterfaceDonneesSimulation {
    static DonneesSimulation getDonneesSimulation(String[] args) {
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
