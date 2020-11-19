package game;

/**
 * Représente une entité datée, identifiée par un id unique
 * 
 * @see IdentifiedEntity
 * @see Dated
 * @author Nicolas Vincent
 */
public abstract class Entity implements IdentifiedEntity<Long>, Dated<Long> {

    public static enum State {
        FREE,
        BUSY
    }
    
    private final long entityId;
    
    /** 
     * Compteur interne permettant d'ordonner une suite d'events.
     * Utilisé pour effectuer les actions des robots en parallèles
     */ 
    private long date = 0L;

    private State state = State.FREE;
    private Integer position;

    public Entity(final long entityId) {
        this.entityId = entityId;
    }

    /**
     * Réinitialise l'entité en position
     * 
     * @param position
     */
    public abstract void init(final int position);

    /**
     * @param natureTerrain
     * @return true if the entity can move on the terrain
     */
    public abstract Boolean isWalkable(final NatureTerrain natureTerrain);

    /**
     * Vérifie que le robot peut se déplacer sur le type de terrain.
     * @param natureTerrain
     * @throws IllegalArgumentException
     * @see #isWalkable(NatureTerrain)
     */
    public void checkWalkable(final NatureTerrain natureTerrain) throws IllegalArgumentException {
        if (!isWalkable(natureTerrain)) {
            throw new IllegalArgumentException(this + " ne peut pas se déplacer sur une case de type " + natureTerrain);
        }
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Override
    public Long getId() {
        return this.entityId;
    }

    @Override
    public Long getDate() {
        return date;
    }

    @Override
    public void setDate(final Long date) {
        this.date = date;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        return (this.entityId == other.entityId);
    }
}
