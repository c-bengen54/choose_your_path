package items;

import characters.Boss;

@FunctionalInterface
public interface ItemEffect {
    void apply(Boss target);
}
