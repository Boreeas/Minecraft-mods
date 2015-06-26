package net.boreeas.lively.runeforge;

import net.boreeas.lively.Lively;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Malte Schütze
 */
public class RuneRegistry {
    private Map<String, Rune> runes = new HashMap<>();

    public void register(@NotNull Rune rune) {
        Lively.INSTANCE.logger.info("Registered rune " + rune.getName());
        runes.put(rune.getName().toLowerCase(), rune);
    }

    @NotNull
    public Optional<Rune> get(@NotNull String name) {
        return Optional.ofNullable(runes.get(name.toLowerCase()));
    }

    @NotNull
    public Collection<Rune> runes() {
        return runes.values();
    }

    public @NotNull Optional<Rune> match(boolean[][] values) {
        for (Rune rune: runes()) {
            if (match(rune, values)) {
                return Optional.of(rune);
            }
        }

        return Optional.empty();
    }

    private boolean match(@NotNull Rune rune, @NotNull boolean[][] values) {
        for (int z = 0; z < values.length; z++) {
            for (int x = 0; x < values[z].length; x++) {
                if (rune.isSet(x, z) != values[z][x]) return false;
            }
        }

        return true;
    }
}
