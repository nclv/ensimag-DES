package game.events;

import game.DonneesSimulation;

public abstract class Event implements Comparable<Event> {
    protected long date;
    protected DonneesSimulation donneesSimulation;

    public Event(long date, DonneesSimulation donneesSimulation) {
        this.date = date;
        this.donneesSimulation = donneesSimulation;
    }

    public long getDate() {
        return this.date;
    }

    public void updateDate(long increment) {
        this.date += increment;
    }

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
}
