package game.events;

import game.DonneesSimulation;
import game.robots.Robot;

public abstract class Event implements Comparable<Event> {
    protected long date;
    private Robot robot;
    protected DonneesSimulation donneesSimulation;

    public Event(long date, DonneesSimulation donneesSimulation, Robot robot) {
        this.date = date;
        this.donneesSimulation = donneesSimulation;
        this.robot = robot;
    }

    public abstract Event copy(DonneesSimulation donneesSimulationSaved, Robot robotSaved);

    public long getDate() {
        return this.date;
    }

    public void updateDate(long newdate) {
        if (this.date < newdate)
            this.date = newdate;
    }

    public Robot getRobot() {
        return robot;
    }

    public abstract long getDuration();
    public abstract void execute();

    @Override
    public int compareTo(Event o) {
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
