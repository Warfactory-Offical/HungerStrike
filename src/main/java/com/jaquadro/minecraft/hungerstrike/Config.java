package com.jaquadro.minecraft.hungerstrike;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.Nullable;

/**
 * Common config. Mirrors the option set of the Forge builds so existing
 * {@code hungerstrike-common.toml} files carry over unchanged.
 */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<Mode> MODE = BUILDER
            .comment("Mode can be set to NONE, LIST, or ALL",
                    "- NONE: Hunger Strike is disabled for all players.",
                    "- LIST: Hunger Strike is enabled for players added",
                    "        in-game with /hungerstrike command.",
                    "- ALL:  Hunger Strike is enabled for all players.")
            .defineEnum("General.mode", Mode.ALL);

    public static final ModConfigSpec.DoubleValue FOOD_HEAL_FACTOR = BUILDER
            .comment("How to translate food points into heart points when consuming food.",
                    "At the default value of 0.5, food fills your heart bar at half the rate it would fill hunger.")
            .defineInRange("General.foodHealFactor", 0.5D, 0.0D, 100.0D);

    public static final ModConfigSpec.IntValue MAX_FOOD_STACK_SIZE = BUILDER
            .comment("Globally overrides the maximum stack size of food items.",
                    "This property affects all Vanilla and Mod items that carry a food component.",
                    "Set to -1 to retain the default stack size of each food item.  Note: This will affect the entire server, not just players on hunger strike.",
                    "This is applied once during mod loading; changing it requires a restart.",
                    "WARNING: Setting this property may result in unexpected behavior with other mods.")
            .defineInRange("General.maxFoodStackSize", -1, -1, 99);

    public static final ModConfigSpec.BooleanValue HIDE_HUNGER_BAR = BUILDER
            .comment("Controls whether or not the hunger bar is hidden for players on hunger strike.",
                    "If the hunger bar is left visible, it will remain filled at half capacity, except when certain potion effects are active like hunger and regeneration.")
            .define("General.hideHungerBar", true);

    public static final ModConfigSpec.IntValue HUNGER_BASELINE = BUILDER
            .comment("The default hunger level when no status effects are active.",
                    "Valid range is [1 - 20], with 20 being fully filled, and 10 being half-filled.  The default value is 10, which disables health regen but allows sprinting.")
            .defineInRange("General.hungerBaseline", 10, 1, 20);

    public static final ModConfigSpec SPEC = BUILDER.build();

    /** Mode from this instance's own config file. */
    private static volatile Mode localMode = Mode.ALL;

    /**
     * Mode pushed by the server we are connected to. Takes precedence over {@link #localMode} so a
     * client's config file can never disagree with the server about who is on strike. Cleared on
     * disconnect.
     */
    private static volatile @Nullable Mode serverMode;

    private Config() {}

    public static Mode mode() {
        Mode fromServer = serverMode;
        return fromServer != null ? fromServer : localMode;
    }

    /** Server-side mode change: updates the config file and the cached value. */
    public static void setMode(Mode mode) {
        MODE.set(mode);
        localMode = mode;
        serverMode = mode;
    }

    /** Client-side mode update, driven by the sync payload. Does not touch the config file. */
    public static void setModeFromServer(Mode mode) {
        serverMode = mode;
    }

    /** Drops the server override when the client leaves a world. */
    public static void clearServerMode() {
        serverMode = null;
    }

    /** Re-reads the cached mode from the loaded spec. Called on config load/reload. */
    public static void refreshFromSpec() {
        if (SPEC.isLoaded()) {
            localMode = MODE.get();
        }
    }

    public static double foodHealFactor() {
        return SPEC.isLoaded() ? FOOD_HEAL_FACTOR.get() : 0.5D;
    }

    public static int maxFoodStackSize() {
        return SPEC.isLoaded() ? MAX_FOOD_STACK_SIZE.get() : -1;
    }

    public static boolean hideHungerBar() {
        return !SPEC.isLoaded() || HIDE_HUNGER_BAR.get();
    }

    public static int hungerBaseline() {
        return SPEC.isLoaded() ? HUNGER_BASELINE.get() : 10;
    }

    public enum Mode {
        NONE,
        LIST,
        ALL
    }
}
