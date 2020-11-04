package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.graphics.GraphicsComponent;
import game.events.Event;
import gui.Simulable;

public class Simulateur implements Simulable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulateur.class);

    public static final long INCREMENT = 375000;

    private DonneesSimulation donneesSimulation;
    private final DonneesSimulation donneesSimulationSaved;

    private long currentDate;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
    private final PriorityQueue<Event> eventQueueSaved = new PriorityQueue<Event>();

    GraphicsComponent graphicsComponent;

    /**
     * Initialisation d'un Simulateur
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

    public void addEvent(final Event event) {
        LOGGER.info("Date de l'évènement (ajoût): {}", event.getDate());

        eventQueue.add(event);
        // ajout à la queue de sauvegarde
        eventQueueSaved.add(event.copy(this.donneesSimulation));
    }

    private void executeNextEvents() {
        // peek/remove is faster than poll/add
        Event event;
        while ((event = eventQueue.peek()) != null && event.getDate() <= this.currentDate) {
            LOGGER.info("Date de l'évènement (execution): {}\n{}", event.getDate(), event.getRobot());

            // on exécute l'action pour le robot si l'action est valide
            try {
                updateEventQueue(event);
                event.execute();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            }

            eventQueue.remove(event);
            // LOGGER.info("Next event: {}", event);
        }
    }

    private void updateEventQueue(final Event event) throws IllegalArgumentException {
        // récupère la durée de l'event
        final long duration = event.getDuration();
        LOGGER.info("Fin d'exécution: {}", duration);
        // le robot est occupé pendant duration, on ne peut plus exécuter d'actions avec
        // ce robot. Il faut incrémenter la date des évènements de ce robot de duration
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
                events.remove();
                eventsToAdd.add(currentEvent);
            }
        }
        this.eventQueue.addAll(eventsToAdd);
    }

    public void updateCurrentDate() {
        this.currentDate += INCREMENT;
    }

    @Override
    public void next() {
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
        this.currentDate = 0;

        this.donneesSimulation = new DonneesSimulation(this.donneesSimulationSaved);
        this.eventQueue = new PriorityQueue<Event>();
        // donneesSimulation et pas donneesSimulationSaved pcq on modifie l'argument lorsque l'on exécute l'event
        this.eventQueueSaved.stream().forEach((event) -> eventQueue.add(event.copy(this.donneesSimulation)));

        // Update de l'affichage
        this.graphicsComponent.setDonneesSimulation(donneesSimulation);
        this.graphicsComponent.reset();
        this.graphicsComponent.draw();
    }
}