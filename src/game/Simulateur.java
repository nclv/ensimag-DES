package game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.events.Action;
import game.events.EventManager;
import game.graphics.GraphicsComponent;
import gui.Simulable;
import strategie.Strategie;

/**
 * Simulateur d'évènements discrets
 * 
 * @see Simulable
 * @see DonneesSimulation
 * @see EventManager
 * @see GraphicsComponent
 * @see Strategie
 * @author Nicolas Vincent
 */
public class Simulateur implements Simulable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulateur.class);

    public static final long INCREMENT = 360;

    private DonneesSimulation donneesSimulation;
    private final DonneesSimulation donneesSimulationSaved;

    private final EventManager eventManager;

    private final GraphicsComponent graphicsComponent;
    private Strategie strategie = null;

    /**
     * Initialisation d'un Simulateur sans stratégie définie
     * 
     * @param graphicsComponent s'occupe de l'affichage
     * @param donneesSimulation
     * @param eventManager s'occupe des events
     * @see DonneesSimulation#DonneesSimulation(DonneesSimulation)
     * @see GraphicsComponent#setSimulable(Simulable)
     * @see GraphicsComponent#init()
     */
    public Simulateur(final GraphicsComponent graphicsComponent, final DonneesSimulation donneesSimulation, final EventManager eventManager) {
        this.donneesSimulation = donneesSimulation;
        LOGGER.info("Copie des données initiales de la simulation");
        this.donneesSimulationSaved = new DonneesSimulation(donneesSimulation);

        this.eventManager = eventManager;

        this.graphicsComponent = graphicsComponent;
        this.graphicsComponent.setSimulable(this);
        this.graphicsComponent.init();
    }

    /**
     * Initialisation d'un Simulateur avec stratégie
     * 
     * @param graphicsComponent s'occupe de l'affichage
     * @param donneesSimulation
     * @param eventManager s'occupe des events
     * @param strategie
     */
    public Simulateur(final GraphicsComponent graphicsComponent, final DonneesSimulation donneesSimulation, final EventManager eventManager,
            final Strategie strategie) {
        this(graphicsComponent, donneesSimulation, eventManager);
        this.strategie = strategie;
    }

    public void schedule(final long date, final Action action) {
        this.eventManager.schedule(date, action);
    }

    /**
     * @see Simulable#next()
     * @see Strategie#execute(Simulateur)
     * @see EventManager#executeNextEvents()
     * @see EventManager#getCurrentDate()
     * @see EventManager#setCurrentDate(long)
     * @see GraphicsComponent#draw()
     */
    @Override
    public void next() {
        if (strategie != null)
            strategie.execute(this);
        // update donneesSimulation and eventSet
        this.eventManager.executeNextEvents();

        LOGGER.info("Ancienne date courante: {}", eventManager.getCurrentDate());
        eventManager.setCurrentDate(eventManager.getCurrentDate() + INCREMENT);
        LOGGER.info("Nouvelle date courante: {}", eventManager.getCurrentDate());

        // Update de l'affichage
        this.graphicsComponent.draw();
    }

    /**
     * @see DonneesSimulation#DonneesSimulation(DonneesSimulation)
     * @see Strategie#setDate(Long)
     * @see EventManager#setCurrentDate(long)
     * @see EventManager#setDonneesSimulation(DonneesSimulation)
     * @see EventManager#reset()
     * @see GraphicsComponent#setDonneesSimulation(DonneesSimulation)
     * @see GraphicsComponent#reset()
     * @see GraphicsComponent#draw()
     */
    @Override
    public void restart() {
        LOGGER.info("Restart");

        this.donneesSimulation = new DonneesSimulation(this.donneesSimulationSaved);
        
        // Update de l'event manager
        eventManager.setCurrentDate(0);
        eventManager.setDonneesSimulation(donneesSimulation);
        eventManager.reset();

        // Update de la stratégie
        if (strategie != null)
            strategie.setDate(0L);
            strategie.getEventAdder().setDonneesSimulation(donneesSimulation);
            strategie.getEventAdder().setEventManager(eventManager);


        // Update de l'affichage
        this.graphicsComponent.setDonneesSimulation(donneesSimulation);
        this.graphicsComponent.reset();
        this.graphicsComponent.draw();
    }

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }
}