package characters;

/**
 * Anything an item {@link items.Effect} can be applied to: the player,
 * party members, enemies.
 */
public interface Combatant {
    /**
     * Reduces health by the given amount
     * 
     * @param amount amount of damgage to be taken; must not be negative.
     */
    void takeDamage(int amount);
    
    /**
     * Increases health by the given amount
     * 
     * @param amount amount of health to be added back; must not be negative.
     */
    void heal(int amount);

    /**
     * Restores power (action points), up to the maximum.
     * 
     * @param amount amount of power to be restored; must not be negative.
     */
    void restorePower(int amount);

    /**
     * Applies a status effect for a number of turns
     * @param status name of the status effect, such as {@code poison}.
     * @param turns the number of turns the status effect is applied for.
     */
    void addStatus(String status, int turns);
}
