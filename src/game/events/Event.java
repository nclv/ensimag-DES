package game.events;

import game.DonneesSimulation;

public class Event implements Comparable<Event> {
    protected long date;
    private Action action;

    public Event(final long date, final Action action) {
        this.date = date;
        this.action = action;
    }

    /**
     * Renvoie une copy de l'évènement avec des données de simulation différentes.
     * 
     * @param donneesSimulation
     * @return nouvel event
     */
    public Event copy(DonneesSimulation donneesSimulation) {
        return new Event(this.date, this.action.copy(donneesSimulation));
    }

    public long getDate() {
        return this.date;
    }

    public Action getAction() {
        return action;
    }

    /**
     * Change la date de l'event si elle arrive plus tôt que la nouvelle date
     * 
     * @param newdate
     */
    public void updateDate(final long newDate) {
        if (this.date < newDate)
            this.date = newDate;
    }

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
        return "Date: " + this.date + "\n" + this.action;
    }
}
