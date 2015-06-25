package net.boreeas.lively.runeforge;

import net.boreeas.lively.Lively;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Malte Schütze
 */
public class RuneRegistry {
    private Map<String, Rune> runes = new HashMap<>();

    public void register(Rune rune) {
        Lively.INSTANCE.logger.info("Registered rune " + rune.getName());
        runes.put(rune.getName().toLowerCase(), rune);
    }

    public Rune get(String name) {
        return runes.get(name.toLowerCase());
    }

    public Collection<Rune> runes() {
        return runes.values();
    }

    public Rune match(boolean[][] values) {
        for (Rune rune: runes()) {
            if (match(rune, values)) {
                return rune;
            }
        }

        return null;
    }

    private boolean match(Rune rune, boolean[][] values) {
        for (int z = 0; z < values.length; z++) {
            for (int x = 0; x < values[z].length; x++) {
                if (rune.isSet(x, z) != values[z][x]) return false;
            }
        }

        return true;
    }
}
