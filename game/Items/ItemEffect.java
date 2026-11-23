package items;

import characters.Combatant;

@FunctionalInterface
public interface ItemEffect {
    void apply(Combatant target);
}
