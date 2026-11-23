package Items;

import characters.Combatant;

/**
 * One thing an item does when used.
 *
 * <p>An effect is plain data (a type, a number, and optional text), so it can be
 * written to and read from a save file or item definition file. The behavior for
 * each type lives in {@link #apply(Combatant)}.
 *
 * @param type   what kind of effect this is
 * @param amount the strength (damage, heal, power), or the duration in turns for
 *               {@link Type#STATUS}; must not be negative
 * @param param  extra text, such as the status name for {@link Type#STATUS};
 *               {@code null} is stored as an empty string
 */
public record Effects(Type type, int amount, String param) {

    /** The kinds of effect an item can have. */
    public enum Type { DAMAGE, HEAL, RESTORE_POWER, STATUS }

    /**
     * Validates the fields. This also protects against bad data loaded from a file.
     *
     * @throws IllegalArgumentException if the type is null, the amount is negative,
     *                                  or a status effect has no name
     */
    public Effects {
        if (type == null) {
            throw new IllegalArgumentException("Effect type cannot be null.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Effect amount cannot be negative.");
        }
        if (param == null) {
            param = "";
        }
        if (type == Type.STATUS && param.isBlank()) {
            throw new IllegalArgumentException("A status effect needs a status name.");
        }
    }

    /** Creates an effect that deals damage. */
    public static Effects damage(int amount) {
        return new Effects(Type.DAMAGE, amount, "");
    }

    /** Creates an effect that restores health. */
    public static Effects heal(int amount) {
        return new Effects(Type.HEAL, amount, "");
    }

    /** Creates an effect that restores power (action points). */
    public static Effects restorePower(int amount) {
        return new Effects(Type.RESTORE_POWER, amount, "");
    }

    /** Creates an effect that applies a named status for a number of turns. */
    public static Effects status(String statusName, int turns) {
        return new Effects(Type.STATUS, turns, statusName);
    }

    /**
     * Applies this effect to a target.
     *
     * @param target who the effect is applied to
     */
    public void apply(Combatant target) {
        switch (type) {
            case DAMAGE        -> target.takeDamage(amount);
            case HEAL          -> target.heal(amount);
            case RESTORE_POWER -> target.restorePower(amount);
            case STATUS        -> target.addStatus(param, amount);
        }
    }
}
