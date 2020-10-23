package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Carte;
import game.DonneesSimulation;
import game.NatureTerrain;
import game.robots.MyRobotTypes;
import game.robots.Robot;

/**
 * Lecteur de cartes au format spectifié dans le sujet. Les données sur les
 * cases, robots puis incendies sont lues dans le fichier, puis simplement
 * affichées. A noter: pas de vérification sémantique sur les valeurs numériques
 * lues.
 *
 * IMPORTANT:
 *
 * Cette classe ne fait que LIRE les infos et les afficher. A vous de modifier
 * ou d'ajouter des méthodes, inspirées de celles présentes (ou non), qui CREENT
 * les objets au moment adéquat pour construire une instance de la classe
 * DonneesSimulation à partir d'un fichier.
 *
 * Vous pouvez par exemple ajouter une méthode qui crée et retourne un objet
 * contenant toutes les données lues: public static DonneesSimulation
 * creeDonnees(String fichierDonnees); Et faire des méthode creeCase(),
 * creeRobot(), ... qui lisent les données, créent les objets adéquats et les
 * ajoutent ds l'instance de DonneesSimulation.
 */
public class LecteurDonnees {
    private static final Logger LOGGER = LoggerFactory.getLogger(LecteurDonnees.class);
    // private Carte carte;
    // private final int nbLignes = 0;
    // Map<Integer, NatureTerrain> map;
    // Map<Integer, Integer> incendies;
    // Map<Integer, Robot> robots;

    private static DonneesSimulation donneesSimulation;

    /**
     * Lit et affiche le contenu d'un fichier de donnees (cases, robots et
     * incendies). Ceci est méthode de classe; utilisation:
     * LecteurDonnees.lire(fichierDonnees)
     * 
     * @param fichierDonnees nom du fichier à lire
     */
    public static DonneesSimulation lire(String fichierDonnees) throws FileNotFoundException, DataFormatException {
        LOGGER.info("Lecture du fichier {}", fichierDonnees);
        LecteurDonnees lecteur = new LecteurDonnees(fichierDonnees);
        donneesSimulation = new DonneesSimulation();

        lecteur.lireCarte();
        lecteur.lireIncendies();
        lecteur.lireRobots();
        scanner.close();

        LOGGER.info("Lecture terminee");

        return donneesSimulation;
    }

    // Tout le reste de la classe est prive!

    private static Scanner scanner;

    /**
     * Constructeur prive; impossible d'instancier la classe depuis l'exterieur
     * 
     * @param fichierDonnees nom du fichier a lire
     */
    private LecteurDonnees(String fichierDonnees) throws FileNotFoundException {
        scanner = new Scanner(new File(fichierDonnees));
        scanner.useLocale(Locale.US);
    }

    /**
     * Lit et affiche les donnees de la carte.
     * 
     * @throws ExceptionFormatDonnees
     */
    private void lireCarte() throws DataFormatException {
        ignorerCommentaires();
        try {
            int nbLignes = scanner.nextInt();
            int nbColonnes = scanner.nextInt();
            int tailleCases = scanner.nextInt(); // en m

            // Création de la carte
            donneesSimulation.setCarte(new Carte(nbLignes, nbColonnes, tailleCases,
                    new HashMap<Integer, NatureTerrain>(nbLignes * nbColonnes)));

            LOGGER.info("Carte {}x{}", nbLignes, nbColonnes);
            LOGGER.info("Cases de taille {}", tailleCases);

            for (int lig = 0; lig < nbLignes; lig++) {
                for (int col = 0; col < nbColonnes; col++) {
                    lireCase(lig, col);
                }
            }

        } catch (NoSuchElementException e) {
            throw new DataFormatException("Format invalide. " + "Attendu: nbLignes nbColonnes tailleCases");
        }
        // une ExceptionFormat levee depuis lireCase est remontee telle quelle
    }

    /**
     * Lit et affiche les donnees d'une case.
     */
    private void lireCase(int lig, int col) throws DataFormatException {
        ignorerCommentaires();
        String chaineNature = new String();
        // NatureTerrain nature;

        try {
            chaineNature = scanner.next();
            // si NatureTerrain est un Enum, vous pouvez recuperer la valeur
            // de l'enum a partir d'une String avec:
            // NatureTerrain nature = NatureTerrain.valueOf(chaineNature);

            // on stocke le type de terrain
            Carte carte = donneesSimulation.getCarte();
            carte.getMap().put(lig * carte.getNbLignes() + col, NatureTerrain.valueOf(chaineNature));

            verifieLigneTerminee();

            LOGGER.info("Case ({}, {}): {}", lig, col, chaineNature);

        } catch (NoSuchElementException e) {
            throw new DataFormatException("format de case invalide. " + "Attendu: nature altitude [valeur_specifique]");
        }
    }

    /**
     * Lit et affiche les donnees des incendies.
     */
    private void lireIncendies() throws DataFormatException {
        ignorerCommentaires();
        try {
            int nbIncendies = scanner.nextInt();
            LOGGER.info("Il y a {} incendies", nbIncendies);

            // initialisation du stockage des incendies
            donneesSimulation.setIncendies(new HashMap<Integer, Integer>(nbIncendies));

            for (int i = 0; i < nbIncendies; i++) {
                lireIncendie(i);
            }

        } catch (NoSuchElementException e) {
            throw new DataFormatException("Format invalide. " + "Attendu: nbIncendies");
        }
    }

    /**
     * Lit et affiche les donnees du i-eme incendie.
     * 
     * @param i
     */
    private void lireIncendie(int i) throws DataFormatException {
        ignorerCommentaires();

        try {
            int lig = scanner.nextInt();
            int col = scanner.nextInt();
            int intensite = scanner.nextInt();
            if (intensite <= 0) {
                throw new DataFormatException("incendie " + i + "nb litres pour eteindre doit etre > 0");
            }

            // on stocke l'incendie
            donneesSimulation.getIncendies().put(lig * donneesSimulation.getCarte().getNbLignes() + col, intensite);

            verifieLigneTerminee();

            LOGGER.info("Incendie {} d'intensité {} en position ({}, {})", i, intensite, lig, col);

        } catch (NoSuchElementException e) {
            throw new DataFormatException("format d'incendie invalide. " + "Attendu: ligne colonne intensite");
        }
    }

    /**
     * Lit et affiche les donnees des robots.
     */
    private void lireRobots() throws DataFormatException {
        ignorerCommentaires();
        try {
            int nbRobots = scanner.nextInt();
            LOGGER.info("Il y a {} robots", nbRobots);

            if (nbRobots == 0) {
                throw new DataFormatException("il n'y a pas de robots");
            }

            // initialisation du stockage des robots
            donneesSimulation.setRobots(new HashMap<Integer, ArrayList<Robot>>());

            for (int i = 0; i < nbRobots; i++) {
                lireRobot(i);
            }

        } catch (NoSuchElementException e) {
            throw new DataFormatException("Format invalide. " + "Attendu: nbRobots");
        }
    }

    /**
     * Lit et affiche les donnees du i-eme robot.
     * 
     * @param i
     */
    private void lireRobot(int i) throws DataFormatException {
        ignorerCommentaires();

        try {
            int lig = scanner.nextInt();
            int col = scanner.nextInt();
            String type = scanner.next();

            LOGGER.info("Robot {} de type {} en position ({}, {})", i, type, lig, col);
            // on stocke le robot
            Robot robot = MyRobotTypes.getType(MyRobotTypes.Type.valueOf(type)).newRobot();

            // lecture eventuelle d'une vitesse du robot (entier)
            String s = scanner.findInLine("(\\d+)"); // 1 or more digit(s) ?
            // pour lire un flottant: ("(\\d+(\\.\\d+)?)");

            if (s == null) {
                LOGGER.info("Déplacement à la vitesse par défaut");
            } else {
                int vitesse = Integer.parseInt(s);
                LOGGER.info("Déplacement à {} km/h", vitesse);
                // on modifie la vitesse par défaut
                robot.setVitesse((double)vitesse);
            }
    
            // création de la liste s'il n'y a aucun robot à cette position
            donneesSimulation.getRobots().computeIfAbsent(
                lig * donneesSimulation.getCarte().getNbLignes() + col, 
                k -> new ArrayList<Robot>())
                .add(robot);

            verifieLigneTerminee();

        } catch (NoSuchElementException e) {
            throw new DataFormatException(
                    "format de robot invalide. " + "Attendu: ligne colonne type [valeur_specifique]");
        }
    }

    /** Ignore toute (fin de) ligne commencant par '#' */
    private void ignorerCommentaires() {
        while (scanner.hasNext("#.*")) {
            scanner.nextLine();
        }
    }

    /**
     * Verifie qu'il n'y a plus rien a lire sur cette ligne (int ou float).
     * 
     * @throws ExceptionFormatDonnees
     */
    private void verifieLigneTerminee() throws DataFormatException {
        if (scanner.findInLine("(\\d+)") != null) {
            throw new DataFormatException("format invalide, donnees en trop.");
        }
    }
}
