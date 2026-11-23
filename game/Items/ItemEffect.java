package Items;

import characters.Boss;

@FunctionalInterface
public interface ItemEffect {
    void apply(Boss target);
}
