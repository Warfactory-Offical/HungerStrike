package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hunger Strike restores pre-1.8 (pre-hunger) health mechanics: the hunger bar is pinned at a
 * configurable baseline and eating food heals the player directly instead of filling the bar.
 *
 * <p>Loader-agnostic entry point. The NeoForge and Fabric modules each build a
 * {@link HungerStrikePlatform} and hand it to {@link #init}; from there both builds run the same
 * code out of {@code common/}.
 */
public final class HungerStrike {
    public static final String MOD_ID = "hungerstrike";
    public static final Logger LOGGER = LoggerFactory.getLogger("Hunger Strike");

    private static volatile HungerStrikePlatform platform;

    private HungerStrike() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /** Installs the loader bridge and reads the config. Called once, from the loader entrypoint. */
    public static void init(HungerStrikePlatform platform) {
        HungerStrike.platform = platform;
        Config.load();
    }

    public static HungerStrikePlatform platform() {
        HungerStrikePlatform current = platform;
        if (current == null) {
            throw new IllegalStateException("Hunger Strike used before its loader entrypoint ran");
        }
        return current;
    }
}
