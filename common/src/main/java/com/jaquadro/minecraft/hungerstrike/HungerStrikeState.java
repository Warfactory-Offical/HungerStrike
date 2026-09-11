package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.world.entity.player.Player;

/**
 * Convenience accessors for the per-player strike flag, which each loader stores in its own data
 * attachment behind {@link HungerStrikePlatform}.
 */
public final class HungerStrikeState {
    private HungerStrikeState() {}

    public static boolean isOnStrike(Player player) {
        return player != null && HungerStrike.platform().isOnStrike(player);
    }

    public static void setOnStrike(Player player, boolean onStrike) {
        if (player == null || isOnStrike(player) == onStrike) {
            return;
        }
        // Both platforms sync the new value to the owning player automatically.
        HungerStrike.platform().setOnStrike(player, onStrike);
    }

    /**
     * Whether hunger-strike mechanics currently apply to this player, taking the active mode into
     * account.
     */
    public static boolean isActiveFor(Player player) {
        return switch (Config.mode()) {
            case NONE -> false;
            case LIST -> isOnStrike(player);
            case ALL -> true;
        };
    }
}
