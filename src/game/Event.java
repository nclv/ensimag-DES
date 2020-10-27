package game;

public abstract class Event implements Comparable<Event> {
    protected long date;

    public Event(long date) {
        this.date = date;
    }

    public long getDate() {
        return this.date;
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
