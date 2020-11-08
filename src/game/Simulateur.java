package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.events.Event;
import game.events.EventEmpty;
import game.events.EventMove;
import game.graphics.GraphicsComponent;
import game.robots.Robot;
import game.robots.Robot.State;
import gui.Simulable;
import strategie.Strategie;

public class Simulateur implements Simulable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulateur.class);

    public static final long INCREMENT = 375000;

    private DonneesSimulation donneesSimulation;
    private final DonneesSimulation donneesSimulationSaved;

    private long currentDate;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
    private final PriorityQueue<Event> eventQueueSaved = new PriorityQueue<Event>();

    private GraphicsComponent graphicsComponent;
    private Strategie strategie = null;

    /**
     * Initialisation d'un Simulateur sans stratégie définie
     * 
     * @param graphicsComponent s'occupe de l'affichage
     * @param donneesSimulation
     */
    public Simulateur(final GraphicsComponent graphicsComponent, final DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
        LOGGER.info("Copie des données initiales de la simulation");
        this.donneesSimulationSaved = new DonneesSimulation(donneesSimulation);

        this.graphicsComponent = graphicsComponent;
        this.graphicsComponent.setSimulable(this);
        this.graphicsComponent.init();
    }

    /**
     * Initialisation d'un Simulateur avec stratégie
     * 
     * @param graphicsComponent
     * @param donneesSimulation
     * @param strategie
     */
    public Simulateur(final GraphicsComponent graphicsComponent, final DonneesSimulation donneesSimulation, Strategie strategie) {
        this(graphicsComponent, donneesSimulation);
        this.strategie = strategie;
    }    

    /**
     * Ajout d'un event à eventQueue et à sa sauvegarde
     * Marquage du robot comme étant occupé
     * 
     * @param event
     */
    public void addEvent(final Event event) {
        LOGGER.info("Date de l'évènement (ajoût): {}", event.getDate());

        this.eventQueue.add(event);
        // ajout à la queue de sauvegarde
        if (strategie == null) {
            this.eventQueueSaved.add(event.copy(this.donneesSimulation));
        }
        // on marque le robot comme occupé s'il ne l'est pas déjà
        if (event.getRobot().getState() == State.FREE) {
            event.getRobot().setState(State.BUSY);
        }
    }

    /**
     * Ajout d'une suite d'events move à la simulation
     * 
     * @param robot
     * @param path
     * @param count compteur externe ordonnant les déplacements
     */
    public void addEventsMove(Robot robot, LinkedList<Integer> path, long count) {
        Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            this.addEvent(new EventMove(count, this.getDonneesSimulation(), robot, this.getDonneesSimulation().getCarte().getDirection(currentPosition, nextPosition)));
            count += Simulateur.INCREMENT;
            currentPosition = nextPosition;
        }
    }

    public void addPathSerial(Robot robot, LinkedList<Integer> path) {
        addEventsMove(robot, path, this.strategie.getCount());
        this.strategie.setCount(this.strategie.getCount() + (path.size() - 1) * Simulateur.INCREMENT);
        addEvent(new EventEmpty(this.strategie.getCount(), this.donneesSimulation, robot));
        this.strategie.setCount(this.strategie.getCount() + Simulateur.INCREMENT);
    }

    public void addPathParallel(Robot robot, LinkedList<Integer> path) {
        addEventsMove(robot, path, robot.getCount());
        robot.setCount(robot.getCount() + (path.size() - 1) * Simulateur.INCREMENT);
        addEvent(new EventEmpty(robot.getCount(), this.donneesSimulation, robot));
        robot.setCount(robot.getCount() + Simulateur.INCREMENT);
    }

    /**
     * Execute les évènements présents entre deux dates.
     */
    private void executeNextEvents() {
        // peek/remove is faster than poll/add
        Event event;
        while ((event = eventQueue.peek()) != null && event.getDate() <= this.currentDate) {
            LOGGER.info("Date de l'évènement (execution): {}", event.getDate());

            // on récupère la durée de l'event si l'event est valide
            // sinon la durée de l'event est nulle
            // on exécute ensuite l'action pour le robot si l'action est valide
            long duration = 0;
            try {
                // throws IllegalArgumentException if outside the map or if the robot can't move on the position (EventMove)
                duration = event.getDuration();
                // throws IllegalArgumentException if outside the map or if the robot can't move on the position (EventMove)
                event.execute();
                // LOGGER.info("Il y a {} events concernant le robot {}", sameRobotEventsCount - 1, event.getRobot().getId());
            } catch (final IllegalArgumentException e) {
                LOGGER.warn(e.getMessage());
            }
            LOGGER.info("Fin d'exécution: {}", duration);

            // on récupère le nombre d'events concernant le même robot et on update leurs dates
            // si la durée est nulle la date reste inchangée
            // on a ainsi le bon nombre d'events concernant le même robot si on a une action non 
            // valide suivie d'actions valides
            int sameRobotEventsCount = updateEventQueue(event, duration);

            // s'il n'y a plus qu'un event concernant ce robot (cet event vient d'être exécuté) 
            // alors le robot est de nouveau libre
            if (sameRobotEventsCount == 1 && strategie.canFree(event.getRobot())) {
                assert event.getRobot().getState() == State.BUSY;
                event.getRobot().setState(State.FREE);
                LOGGER.info("Le robot {} est FREE", event.getRobot().getId());
            }
            eventQueue.remove(event);
        }
    }

    /**
     * Il faut réordonner la priority queue si le robot peut exécuter l'action.
     * Tous les events du robot exécutant l'action doivent être incrémentés de la durée de l'action.
     * On en profite pour compter le nombre d'events concernant le même robot
     * 
     * @param event
     * @param duration durée de l'event passé en argument
     * @return nombre d'events concernant le même robot dans eventQueue
     */
    private int updateEventQueue(final Event event, final long duration) throws IllegalArgumentException {
        // le robot est occupé pendant duration, on ne peut plus exécuter d'actions avec
        // ce robot. Il faut incrémenter la date des évènements de ce robot de duration
        int count = 0; // compteur des occurrences du robot dans eventQueue
        final ArrayList<Event> eventsToAdd = new ArrayList<Event>();
        final Iterator<Event> events = this.eventQueue.iterator();
        while (events.hasNext()) {
            final Event currentEvent = events.next();
            // problème d'égalité possible si l'égalité des volumes est vérifiée dans
            // equals(). On implémente un id propre à chaque robot qui vérifie l'égalité
            if (currentEvent.getRobot().equals(event.getRobot())) {
                // on incrémente la date de l'event de la durée de l'event exécuté
                // l'event qui va être exécuté (donc supprimé de la queue) est aussi incrémenté
                currentEvent.updateDate(this.currentDate + duration);
                LOGGER.info("Nouvelle date de l'évènement: {}", currentEvent.getDate());
                count++;
                events.remove();
                eventsToAdd.add(currentEvent);

            }
        }
        this.eventQueue.addAll(eventsToAdd);
        return count;
    }

    /**
     * Incrémente la date courante d'INCREMENT
     */
    public void updateCurrentDate() {
        this.currentDate += INCREMENT;
    }

    @Override
    public void next() {
        if (strategie != null) strategie.execute(this);
        // update donneesSimulation and eventQueue
        executeNextEvents();

        LOGGER.info("Ancienne date courante: {}", this.currentDate);
        updateCurrentDate();
        LOGGER.info("Nouvelle date courante: {}", this.currentDate);

        // Update de l'affichage
        this.graphicsComponent.draw();
    }

    @Override
    public void restart() {
        LOGGER.info("Restart");
        this.currentDate = 0;
        if (strategie != null) strategie.setCount(0);

        this.donneesSimulation = new DonneesSimulation(this.donneesSimulationSaved);
        this.eventQueue = new PriorityQueue<Event>();
        if (strategie == null) {
            // donneesSimulation et pas donneesSimulationSaved pcq on modifie l'argument lorsque l'on exécute l'event
            this.eventQueueSaved.stream().forEach((event) -> eventQueue.add(event.copy(this.donneesSimulation)));
        }
        
        // Update de l'affichage
        this.graphicsComponent.setDonneesSimulation(donneesSimulation);
        this.graphicsComponent.reset();
        this.graphicsComponent.draw();
    }

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }
}