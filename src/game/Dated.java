package game;

/**
 * Implémenté par un objet daté. Utilisé pour effectuer des actions en parallèles 
 * pour différentes entités.
 * 
 * @author Nicolas Vincent
 */
public interface Dated<L> {
    L getDate();
    void setDate(L date);
}
