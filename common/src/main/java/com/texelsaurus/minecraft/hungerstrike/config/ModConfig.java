package com.texelsaurus.minecraft.hungerstrike.config;

import com.texelsaurus.minecraft.hungerstrike.ModServices;
import com.texelsaurus.minecraft.hungerstrike.service.CommonConfig;

public final class ModConfig
{
    public static General GENERAL;

    public static void init() {
        GENERAL = new General();
    }

    public static class General {
        public CommonConfig.ConfigEntry<Mode> mode;
        public CommonConfig.ConfigEntry<Double> foodHealFactor;
        public CommonConfig.ConfigEntry<Boolean> hideHungerBar;
        public CommonConfig.ConfigEntry<Integer> hungerBaseline;

        public General() {
            ModServices.CONFIG.pushGroup("General");

            mode = ModServices.CONFIG.defineEnum("mode", Mode.ALL)
                .comment("Mode can be set to NONE, LIST, or ALL",
                    "- NONE: Hunger Strike is disabled for all players.",
                    "- LIST: Hunger Strike is enabled for players added",
                    "        in-game with /hungerstrike command.",
                    "- ALL:  Hunger Strike is enabled for all players.")
                .build();

            foodHealFactor = ModServices.CONFIG.define("foodHealFactor", 0.5)
                .comment("How to translate food points into heart points when consuming food.",
                    "At the default value of 0.5, food fills your heart bar at half the rate it would fill hunger.")
                .build();

            hideHungerBar = ModServices.CONFIG.define("hideHungerBar", true)
                .comment("Controls whether or not the hunger bar is hidden for players on hunger strike.",
                    "If the hunger bar is left visible, it will remain filled at half capacity, except when certain potion effects are active like hunger and regeneration.")
                .build();

            hungerBaseline = ModServices.CONFIG.defineInRange("hungerBaseline", 10, 1, 20)
                .comment("The default hunger level when no status effects are active.",
                    "Valid range is [1 - 20], with 20 being fully filled, and 10 being half-filled.  The default value is 10, which disables health regen but allows sprinting.")
                .build();

            ModServices.CONFIG.popGroup();
        }
    }

    public enum Mode {
        NONE,
        LIST,
        ALL;

        public static Mode fromValueIgnoreCase (String value) {
            if (value.compareToIgnoreCase("NONE") == 0)
                return Mode.NONE;
            else if (value.compareToIgnoreCase("LIST") == 0)
                return Mode.LIST;
            else if (value.compareToIgnoreCase("ALL") == 0)
                return Mode.ALL;

            return LIST;
        }
    }
}
