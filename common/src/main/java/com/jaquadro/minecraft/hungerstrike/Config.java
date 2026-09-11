package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.config.TomlConfigFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Common config. Mirrors the option set, defaults, ranges and file name of the Forge and NeoForge
 * builds, so an existing {@code config/hungerstrike-common.toml} carries over unchanged.
 *
 * <p>NeoForge's {@code ModConfigSpec} has no Fabric counterpart, so the file is read and rewritten
 * by {@link TomlConfigFile} instead. The public surface of this class is identical to the NeoForge
 * one; only the storage behind it differs.
 */
public final class Config {
    public static final String FILE_NAME = "hungerstrike-common.toml";

    private static final Mode DEFAULT_MODE = Mode.ALL;
    private static final double DEFAULT_FOOD_HEAL_FACTOR = 0.5D;
    private static final int DEFAULT_MAX_FOOD_STACK_SIZE = -1;
    private static final boolean DEFAULT_HIDE_HUNGER_BAR = true;
    private static final int DEFAULT_HUNGER_BASELINE = 10;

    /** Mode from this instance's own config file. */
    private static volatile Mode localMode = DEFAULT_MODE;

    /**
     * Mode pushed by the server we are connected to, or null when there is none. Takes precedence
     * over {@link #localMode} so a client's config file can never disagree with the server about who
     * is on strike. Cleared on disconnect.
     *
     * <p>Left unannotated on purpose: NeoForge ships JSpecify and Fabric ships the JetBrains
     * annotations, and shared code should not depend on either being present.
     */
    private static volatile Mode serverMode;

    private static volatile double foodHealFactor = DEFAULT_FOOD_HEAL_FACTOR;
    private static volatile int maxFoodStackSize = DEFAULT_MAX_FOOD_STACK_SIZE;
    private static volatile boolean hideHungerBar = DEFAULT_HIDE_HUNGER_BAR;
    private static volatile int hungerBaseline = DEFAULT_HUNGER_BASELINE;

    private Config() {}

    private static Path path() {
        return HungerStrike.platform().configDir().resolve(FILE_NAME);
    }

    /**
     * Reads the config file, creating it from the defaults if it is missing. Called once from
     * {@link HungerStrike#init}; the file is re-read only on restart.
     */
    public static void load() {
        try {
            TomlConfigFile file = TomlConfigFile.read(path());
            localMode = file.getEnum("General.mode", Mode.class, DEFAULT_MODE);
            foodHealFactor = file.getDouble("General.foodHealFactor", DEFAULT_FOOD_HEAL_FACTOR, 0.0D, 100.0D);
            maxFoodStackSize = file.getInt("General.maxFoodStackSize", DEFAULT_MAX_FOOD_STACK_SIZE, -1, 99);
            hideHungerBar = file.getBoolean("General.hideHungerBar", DEFAULT_HIDE_HUNGER_BAR);
            hungerBaseline = file.getInt("General.hungerBaseline", DEFAULT_HUNGER_BASELINE, 1, 20);
        } catch (IOException e) {
            HungerStrike.LOGGER.error("Could not read {}; falling back to defaults", FILE_NAME, e);
        }
        save();
    }

    /** Rewrites the file from the current values, adding any options a older file was missing. */
    public static void save() {
        try {
            TomlConfigFile.write(path(), entries());
        } catch (IOException e) {
            HungerStrike.LOGGER.error("Could not write {}", FILE_NAME, e);
        }
    }

    private static List<TomlConfigFile.Entry> entries() {
        return List.of(
                TomlConfigFile.Entry.of("General", "mode", TomlConfigFile.Entry.quote(localMode.name()),
                        "Mode can be set to NONE, LIST, or ALL",
                        "- NONE: Hunger Strike is disabled for all players.",
                        "- LIST: Hunger Strike is enabled for players added",
                        "        in-game with /hungerstrike command.",
                        "- ALL:  Hunger Strike is enabled for all players.",
                        "Allowed Values: NONE, LIST, ALL"),
                TomlConfigFile.Entry.of("General", "foodHealFactor", Double.toString(foodHealFactor),
                        "How to translate food points into heart points when consuming food.",
                        "At the default value of 0.5, food fills your heart bar at half the rate it would fill hunger.",
                        " Default: 0.5",
                        " Range: 0.0 ~ 100.0"),
                TomlConfigFile.Entry.of("General", "maxFoodStackSize", Integer.toString(maxFoodStackSize),
                        "Globally overrides the maximum stack size of food items.",
                        "This property affects all Vanilla and Mod items that carry a food component.",
                        "Set to -1 to retain the default stack size of each food item.  Note: This will affect the entire server, not just players on hunger strike.",
                        "This is applied once during mod loading; changing it requires a restart.",
                        "WARNING: Setting this property may result in unexpected behavior with other mods.",
                        " Default: -1",
                        " Range: -1 ~ 99"),
                TomlConfigFile.Entry.of("General", "hideHungerBar", Boolean.toString(hideHungerBar),
                        "Controls whether or not the hunger bar is hidden for players on hunger strike.",
                        "If the hunger bar is left visible, it will remain filled at half capacity, except when certain potion effects are active like hunger and regeneration."),
                TomlConfigFile.Entry.of("General", "hungerBaseline", Integer.toString(hungerBaseline),
                        "The default hunger level when no status effects are active.",
                        "Valid range is [1 - 20], with 20 being fully filled, and 10 being half-filled.  The default value is 10, which disables health regen but allows sprinting.",
                        " Default: 10",
                        " Range: 1 ~ 20"));
    }

    public static Mode mode() {
        Mode fromServer = serverMode;
        return fromServer != null ? fromServer : localMode;
    }

    /** Server-side mode change: updates the config file and the cached value. */
    public static void setMode(Mode mode) {
        localMode = mode;
        serverMode = mode;
        save();
    }

    /** Client-side mode update, driven by the sync payload. Does not touch the config file. */
    public static void setModeFromServer(Mode mode) {
        serverMode = mode;
    }

    /** Drops the server override when the client leaves a world. */
    public static void clearServerMode() {
        serverMode = null;
    }

    public static double foodHealFactor() {
        return foodHealFactor;
    }

    public static int maxFoodStackSize() {
        return maxFoodStackSize;
    }

    public static boolean hideHungerBar() {
        return hideHungerBar;
    }

    public static int hungerBaseline() {
        return hungerBaseline;
    }

    public enum Mode {
        NONE,
        LIST,
        ALL
    }
}
