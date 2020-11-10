package game.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.DonneesSimulation;
import game.robots.Robot;
import game.robots.Robot.State;
import strategie.Strategie;

public class EventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
    
    private long currentDate;
    private PriorityQueue<Event> eventSet = new PriorityQueue<Event>();
    private PriorityQueue<Event> eventSetSaved = null;

    private DonneesSimulation donneesSimulation;
    private Strategie strategie = null;

    public EventManager(DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
        eventSetSaved = new PriorityQueue<Event>();
    }

    public EventManager(DonneesSimulation donneesSimulation, Strategie strategie) {
        this.donneesSimulation = donneesSimulation;
        this.strategie = strategie;
        if (strategie == null) {
            eventSetSaved = new PriorityQueue<Event>();
        }
    }

    /**
     * Ajout d'un event à eventSet et à sa sauvegarde Marquage du robot comme étant
     * occupé
     * 
     * @param event
     */
    public void schedule(final long date, final Action action) {
        LOGGER.info("Date de l'évènement (ajoût): {}", date);
        Event event = new Event(date, action);

        this.eventSet.add(event);
        // ajout à la queue de sauvegarde
        if (strategie == null) {
            this.eventSetSaved.add(event.copy(this.donneesSimulation));
        }
    }

    /**
     * Ajout d'une suite d'events move à la simulation
     * 
     * @param robot
     * @param path
     * @param date compteur externe ordonnant les déplacements
     */
    private void scheduleActionsMove(Robot robot, LinkedList<Integer> path, long date, long increment) {
        Iterator<Integer> iter = path.iterator();
        int currentPosition = iter.next();
        while (iter.hasNext()) {
            int nextPosition = iter.next();
            schedule(date, new ActionMove(this.donneesSimulation, robot,
                    this.donneesSimulation.getCarte().getDirection(currentPosition, nextPosition)));
            date += increment;
            currentPosition = nextPosition;
        }
    }

    public void addPathSerial(Robot robot, LinkedList<Integer> path, long increment) {
        assert this.strategie != null;
        scheduleActionsMove(robot, path, this.strategie.getDate(), increment);
        this.strategie.setDate(this.strategie.getDate() + (path.size() - 1) * increment);
    }

    public void addEmptySerial(Robot robot, long increment) {
        assert this.strategie != null;
        schedule(this.strategie.getDate(), new ActionEmpty(this.donneesSimulation, robot));
        this.strategie.setDate(this.strategie.getDate() + increment);
    }

    public void addFillingSerial(Robot robot, long increment) {
        assert this.strategie != null;
        schedule(this.strategie.getDate(), new ActionFill(this.donneesSimulation, robot));
        this.strategie.setDate(this.strategie.getDate() + increment);
    }

    public void addPathParallel(Robot robot, LinkedList<Integer> path, long increment) {
        scheduleActionsMove(robot, path, robot.getDate(), increment);
        robot.setDate(robot.getDate() + (path.size() - 1) * increment);
    }

    public void addEmptyParallel(Robot robot, long increment) {
        schedule(robot.getDate(), new ActionEmpty(this.donneesSimulation, robot));
        robot.setDate(robot.getDate() + increment);
    }

    public void addFillingParallel(Robot robot, long increment) {
        schedule(robot.getDate(), new ActionFill(this.donneesSimulation, robot));
        robot.setDate(robot.getDate() + increment);
    }

    /**
     * Execute les évènements présents entre deux dates.
     */
    public void executeNextEvents() {
        // peek/remove is faster than poll/add
        Event event;
        while ((event = eventSet.peek()) != null && event.getDate() <= this.currentDate) {
            LOGGER.info("Date de l'évènement (execution): {}", event.getDate());

            // on récupère la durée de l'event si l'event est valide
            // sinon la durée de l'event est nulle
            // on exécute ensuite l'action pour le robot si l'action est valide
            long duration = 0;
            try {
                Action eventAction = event.getAction();
                // throws IllegalArgumentException if outside the map or if the robot can't move
                // on the position (EventMove)
                duration = eventAction.getDuration();
                // throws IllegalArgumentException if outside the map or if the robot can't move
                // on the position (EventMove)
                eventAction.execute();
                // LOGGER.info("Il y a {} events concernant le robot {}", sameRobotEventsCount -
                // 1, event.getRobot().getId());
            } catch (final IllegalArgumentException e) {
                LOGGER.warn(e.getMessage());
            }
            LOGGER.info("Fin d'exécution: {}", duration);

            // on récupère le nombre d'events concernant le même robot et on update leurs
            // dates
            // si la durée est nulle la date reste inchangée
            // on a ainsi le bon nombre d'events concernant le même robot si on a une action
            // non
            // valide suivie d'actions valides
            int sameRobotEventsCount = rescheduleEvents(event, duration);

            // s'il n'y a plus qu'un event concernant ce robot (cet event vient d'être
            // exécuté)
            // alors le robot est de nouveau libre
            Robot eventRobot = event.getAction().getRobot();
            if (sameRobotEventsCount == 1 && strategie != null && strategie.canFree(eventRobot)) {
                assert eventRobot.getState() == State.BUSY;
                eventRobot.setState(State.FREE);
                LOGGER.info("Le robot {} est FREE", eventRobot.getId());
            }
            eventSet.remove(event);
        }
    }

    /**
     * Il faut réordonner la priority queue si le robot peut exécuter l'action. Tous
     * les events du robot exécutant l'action doivent être incrémentés de la durée
     * de l'action. On en profite pour compter le nombre d'events concernant le même
     * robot
     * 
     * @param event
     * @param duration durée de l'event passé en argument
     * @return nombre d'events concernant le même robot dans eventSet
     */
    private int rescheduleEvents(final Event event, final long duration) throws IllegalArgumentException {
        // le robot est occupé pendant duration, on ne peut plus exécuter d'actions avec
        // ce robot. Il faut incrémenter la date des évènements de ce robot de duration
        int count = 0; // compteur des occurrences du robot dans eventSet
        final ArrayList<Event> eventsToAdd = new ArrayList<Event>();
        final Iterator<Event> events = this.eventSet.iterator();
        while (events.hasNext()) {
            final Event currentEvent = events.next();
            // problème d'égalité possible si l'égalité des volumes est vérifiée dans
            // equals(). On implémente un id propre à chaque robot qui vérifie l'égalité
            if (currentEvent.getAction().getRobot().equals(event.getAction().getRobot())) {
                // on incrémente la date de l'event de la durée de l'event exécuté
                // l'event qui va être exécuté (donc supprimé de la queue) est aussi incrémenté
                currentEvent.updateDate(this.currentDate + duration);
                LOGGER.info("Nouvelle date de l'évènement: {}", currentEvent.getDate());
                count++;
                events.remove();
                eventsToAdd.add(currentEvent);

            }
        }
        this.eventSet.addAll(eventsToAdd);
        return count;
    }

    public void reset() {
        this.eventSet = new PriorityQueue<Event>();
        if (strategie == null) {
            // donneesSimulation et pas donneesSimulationSaved pcq on modifie l'argument
            // lorsque l'on exécute l'event
            this.eventSetSaved.stream().forEach((event) -> eventSet.add(event.copy(this.donneesSimulation)));
        }
    }

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public void setDonneesSimulation(DonneesSimulation donneesSimulation) {
        this.donneesSimulation = donneesSimulation;
    }
}
