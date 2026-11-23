package items;

import characters.Combatant;
import java.util.List;

/**
 * The definition of an item: what it is and what it does when used.
 *
 * <p>An item is shared, unchanging data. How many of it a player owns is tracked
 * by the inventory, and saves store only the {@link #id()}.
 *
 * @param id          the unique, stable identifier used by saves and doors
 *                    (for example {@code "health_potion"})
 * @param name        the display name
 * @param description the text shown to the player
 * @param consumable  whether using the item removes one from the inventory
 * @param effects     what happens when the item is used; empty for items such
 *                    as keys
 */
public record Item(String id, String name, String description, boolean consumable, List<Effects> effects) {
    /**
     * Validates the fields and makes an immutable copy of the effects.
     *
     * @throws IllegalArgumentException if the id or name is null or blank
     */
    public Item {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id cannot be blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be blank.");
        }
        if (description == null) {
            description = "";
        }
        effects = (effects == null) ? List.of() : List.copyOf(effects);
    }

    /**
     * Creates a non-consumable item with no effects, such as a key. Doors
     * and similar checks look for it by id.
     */
    public static Item key(String id, String name, String description) {
        return new Item(id, name, description, false, List.of());
    }

    /**
     * Applies every effect of this item to the target, in order. This does not
     * remove the item from any inventory.
     *
     * @param target who the effects are applied to
     */
    public void use(Combatant target) {
        for (Effects effect : effects) {
            effect.apply(target);
        }
    }
}
