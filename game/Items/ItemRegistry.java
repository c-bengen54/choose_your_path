package Items;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds every item definition in the game, looked up by id.
 *
 * <p>Saves store item ids, and this registry turns an id back into an
 * {@link Item} when a save is loaded. Item definitions are registered once at
 * startup, either in code (see {@link #registerDefaults()}) or by loading them
 * from a data file.
 */
public final class ItemRegistry {
    private static final Map<String, Item> ITEMS = new HashMap<>();

    private ItemRegistry() {}

    /**
     * Adds an item definition.
     *
     * @param item the item to register
     * @throws IllegalArgumentException if an item with the same id is already registered
     */
    public static void register(Item item) {
        if (ITEMS.putIfAbsent(item.id(), item) != null) {
            throw new IllegalArgumentException("Duplicate item id: " + item.id());
        }
    }

    /**
     * Looks up an item by id.
     *
     * @param id the item id
     * @return the matching item
     * @throws IllegalArgumentException if no item has that id
     */
    public static Item get(String id) {
        Item item = ITEMS.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item id: " + id);
        }
        return item;
    }
}