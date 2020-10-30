package game.events;

import game.DonneesSimulation;
import game.robots.Robot;

public abstract class Event implements Comparable<Event> {
    protected long date;
    protected Robot robot;
    protected DonneesSimulation donneesSimulation;

    public Event(final long date, final DonneesSimulation donneesSimulation, final Robot robot) {
        this.date = date;
        this.donneesSimulation = donneesSimulation;
        this.robot = robot;
    }

    /**
     * Renvoie une copy de l'évènement avec des données de simulation différentes.
     * 
     * @param donneesSimulation
     * @return nouvel event
     */
    public abstract Event copy(DonneesSimulation donneesSimulation);

    public long getDate() {
        return this.date;
    }

    public void updateDate(final long newdate) {
        if (this.date < newdate)
            this.date = newdate;
    }

    public Robot getRobot() {
        return robot;
    }

    public DonneesSimulation getDonneesSimulation() {
        return donneesSimulation;
    }

    public abstract long getDuration();

    public abstract void execute();

    @Override
    public int compareTo(final Event o) {
		if (o == null)
			throw new NullPointerException();
		if (this.date < o.getDate())
			return -1;
		else if (this.date == o.getDate())
			return 0;
		else
			return 1;
    }

    @Override
    public String toString() {
        return this.robot + "\nDate: " + this.date + "\n" + donneesSimulation.getRobots();
    }
}
