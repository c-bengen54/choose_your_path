package characters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import items.Item;
import items.ItemRegistry;

/**
 * The player character: health, power, money, location, inventory, party,
 * learned moves, and active statuses.
 *
 * <p>Items, party members, and moves are stored by id (a {@code String}), not as
 * objects, so the whole player can be turned into plain data with
 * {@link #toSaveData()} and rebuilt with {@link #fromSaveData(SaveData)}.
 *
 * <p>Invalid arguments (negative amounts, null ids) throw
 * {@link IllegalArgumentException}. Ordinary game outcomes, such as not having
 * enough money, return {@code false} instead.
 */
public class bear5 implements Combatant {
    private int health;
    private final int maxHealth;
    private int power;
    private final int maxPower;
    private int money;
    private String location;

    /** Item id to how many the player owns. */
    private final Map<String, Integer> inventory = new LinkedHashMap<>();

    /** Ids of party members. */
    private final List<String> party = new ArrayList<>();

    /** Ids of learned special moves. */
    private final Set<String> knownMoves = new LinkedHashSet<>();

    /** Status name to turns remaining. */
    private final Map<String, Integer> statuses = new LinkedHashMap<>();

    /**
     * Creates a player at full health and full power with no money.
     *
     * @param maxHealth the maximum (and starting) health; must be positive
     * @param maxPower  the maximum (and starting) power; must not be negative
     * @param location  the starting location; must not be null
     * @throws IllegalArgumentException if any argument is invalid
     */
    public bear5(int maxHealth, int maxPower, String location) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive.");
        }
        requireNonNegative(maxPower, "max power");
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxPower = maxPower;
        this.power = maxPower;
        this.location = requireId(location);
    }

    /**
     * Adds copies of an item to the inventory.
     *
     * @param item  the item to add
     * @param count how many to add; must be positive
     * @throws IllegalArgumentException if the item is null or the count is not positive
     */
    public void addToInventory(Item item, int count) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null to inventory.");
        }
        requirePositive(count, "count");
        inventory.merge(item.id(), count, Integer::sum);
    }

    /** Adds one copy of an item to the inventory. */
    public void addToInventory(Item item) {
        addToInventory(item, 1);
    }

    /**
     * Removes copies of an item from the inventory.
     *
     * @param item  the item to remove
     * @param count how many to remove; must be positive
     * @throws IllegalArgumentException if the item is null, the count is not
     *                                  positive, or the player has too few
     */
    public void removeFromInventory(Item item, int count) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot remove null from inventory.");
        }
        requirePositive(count, "count");
        int have = countOf(item.id());
        if (have < count) {
            throw new IllegalArgumentException("Not enough " + item.name() + " in inventory.");
        }
        if (have == count) {
            inventory.remove(item.id());
        } else {
            inventory.put(item.id(), have - count);
        }
    }

    /** Returns how many of the item with this id the player owns (0 if none). */
    public int countOf(String itemId) {
        return inventory.getOrDefault(itemId, 0);
    }

    /** Returns whether the player owns at least one of the item with this id. */
    public boolean hasItem(String itemId) {
        return countOf(itemId) > 0;
    }

    /**
     * Uses an item on the player.
     *
     * @param item the item to use
     * @throws IllegalArgumentException if the player does not own the item
     * @see #useItem(Item, Combatant)
     */
    public void useItem(Item item) {
        useItem(item, this);
    }

    /**
     * Uses an item on a target (for example an enemy). The effects are applied
     * first, then one copy is removed if the item is consumable.
     *
     * @param item   the item to use
     * @param target who the item's effects apply to
     * @throws IllegalArgumentException if the player does not own the item
     */
    public void useItem(Item item, Combatant target) {
        if (item == null || !hasItem(item.id())) {
            throw new IllegalArgumentException("Item not found in inventory.");
        }
        item.use(target);
        if (item.consumable()) {
            removeFromInventory(item, 1);
        }
    }

    @Override
    public void takeDamage(int amount) {
        requireNonNegative(amount, "damage");
        health = Math.max(0, health - amount);
    }

    /**
     * Takes damage scaled by a multiplier, rounded to the nearest whole number.
     *
     * @param amount     the base damage; must not be negative
     * @param multiplier the scale factor; must not be negative
     * @throws IllegalArgumentException if either argument is negative
     */
    public void takeDamage(int amount, double multiplier) {
        requireNonNegative(amount, "damage");
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative.");
        }
        takeDamage((int) Math.min(Integer.MAX_VALUE, Math.round(amount * multiplier)));
    }

    @Override
    public void heal(int amount) {
        requireNonNegative(amount, "heal amount");
        health = (int) Math.min(maxHealth, (long) health + amount);
    }

    /** Returns whether the player has any health left. */
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public void restorePower(int amount) {
        requireNonNegative(amount, "power amount");
        power = (int) Math.min(maxPower, (long) power + amount);
    }

    /**
     * Spends power on a move or spell.
     *
     * @param cost the power to spend; must not be negative
     * @return {@code true} if the power was spent, {@code false} if the player
     *         does not have enough (nothing is spent)
     */
    public boolean spendPower(int cost) {
        requireNonNegative(cost, "cost");
        if (cost > power) {
            return false;
        }
        power -= cost;
        return true;
    }

    /**
     * Adds money.
     *
     * @param amount the money to add; must not be negative
     */
    public void addMoney(int amount) {
        requireNonNegative(amount, "money amount");
        money = Math.addExact(money, amount);
    }

    /**
     * Spends money.
     *
     * @param cost the price; must not be negative
     * @return {@code true} if the money was spent, {@code false} if the player
     *         cannot afford it (nothing is spent)
     */
    public boolean spendMoney(int cost) {
        requireNonNegative(cost, "cost");
        if (cost > money) {
            return false;
        }
        money -= cost;
        return true;
    }

    /**
     * Teaches the player a special move.
     *
     * @param moveId the move's id
     * @return {@code true} if it was newly learned, {@code false} if already known
     */
    public boolean learnMove(String moveId) {
        return knownMoves.add(requireId(moveId));
    }

    /** Returns whether the player has learned the move with this id. */
    public boolean knowsMove(String moveId) {
        return knownMoves.contains(moveId);
    }

    /**
     * Adds a party member.
     *
     * @param memberId the member's id
     * @return {@code true} if added, {@code false} if already in the party
     */
    public boolean addPartyMember(String memberId) {
        requireId(memberId);
        if (party.contains(memberId)) {
            return false;
        }
        party.add(memberId);
        return true;
    }

    /**
     * Removes a party member.
     *
     * @param memberId the member's id
     * @return {@code true} if they were in the party
     */
    public boolean removePartyMember(String memberId) {
        return party.remove(memberId);
    }

    /**
     * Applies a status. If the status is already active, the longer duration is kept.
     */
    @Override
    public void addStatus(String status, int turns) {
        requireId(status);
        requirePositive(turns, "turns");
        statuses.merge(status, turns, Math::max);
    }

    public int getHealth()     { return health; }
    public int getMaxHealth()  { return maxHealth; }
    public int getPower()      { return power; }
    public int getMaxPower()   { return maxPower; }
    public int getMoney()      { return money; }
    public String getLocation() { return location; }

    /**
     * Moves the player to a new location.
     *
     * @param location the location id; must not be null or blank
     * @throws IllegalArgumentException if the location is null or blank
     */
    public void setLocation(String location) {
        this.location = requireId(location);
    }

    /** Returns a read-only view of the inventory (item id to count). */
    public Map<String, Integer> getInventory() {
        return Collections.unmodifiableMap(inventory);
    }

    /** Returns a read-only view of the party member ids. */
    public List<String> getParty() {
        return Collections.unmodifiableList(party);
    }

    /** Returns a read-only view of the learned move ids. */
    public Set<String> getKnownMoves() {
        return Collections.unmodifiableSet(knownMoves);
    }

    /** Returns a read-only view of active statuses (name to turns remaining). */
    public Map<String, Integer> getStatuses() {
        return Collections.unmodifiableMap(statuses);
    }

    /**
     * A plain-data snapshot of a player, suitable for writing to a save file.
     * Collections are copied, so the snapshot never changes after creation.
     */
    public record SaveData(
        int health, int maxHealth, int power, int maxPower, int money,
        String location, Map<String, Integer> inventory,
        List<String> party, Set<String> knownMoves,
        Map<String, Integer> statuses
    ) {
        public SaveData {
            inventory = Map.copyOf(inventory);
            party = List.copyOf(party);
            knownMoves = Set.copyOf(knownMoves);
            statuses = Map.copyOf(statuses);
        }
    }

    /** Captures the player's current state as plain data. */
    public SaveData toSaveData() {
        return new SaveData(
            health, maxHealth, power, maxPower, money, location,
            inventory, party, knownMoves, statuses
        );
    }

    /**
     * Rebuilds a player from saved data, checking it as it goes.
     *
     * @param data the saved state
     * @return the restored player
     * @throws IllegalArgumentException if the data is invalid, such as health
     *                                  outside 0 to max health, a non-positive
     *                                  item count, or an item id that is not in
     *                                  the {@link ItemRegistry}
     */
    public static bear5 fromSaveData(SaveData data) {
        Objects.requireNonNull(data, "data");
        bear5 p = new bear5(data.maxHealth(), data.maxPower(), data.location());

        if (data.health() < 0 || data.health() > data.maxHealth()) {
            throw new IllegalArgumentException("Saved health is out of range.");
        }
        if (data.power() < 0 || data.power() > data.maxPower()) {
            throw new IllegalArgumentException("Saved power is out of range.");
        }
        requireNonNegative(data.money(), "money");

        p.health = data.health();
        p.power = data.power();
        p.money = data.money();

        for (Map.Entry<String, Integer> entry : data.inventory().entrySet()) {
            ItemRegistry.get(entry.getKey());   // fails early on unknown item ids
            requirePositive(entry.getValue(), "item count");
            p.inventory.put(entry.getKey(), entry.getValue());
        }
        for (String member : data.party()) {
            p.addPartyMember(member);
        }
        for (String move : data.knownMoves()) {
            p.learnMove(move);
        }
        for (Map.Entry<String, Integer> entry : data.statuses().entrySet()) {
            p.addStatus(entry.getKey(), entry.getValue());
        }
        return p;
    }

    private static void requireNonNegative(int value, String what) {
        if (value < 0) {
            throw new IllegalArgumentException("The " + what + " cannot be negative.");
        }
    }

    private static void requirePositive(int value, String what) {
        if (value <= 0) {
            throw new IllegalArgumentException("The " + what + " must be positive.");
        }
    }

    private static String requireId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id cannot be null or blank.");
        }
        return id;
    }
}