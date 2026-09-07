package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.world.entity.player.Player;

/** Convenience accessors for the per-player strike flag. */
public final class HungerStrikeState {
    private HungerStrikeState() {}

    public static boolean isOnStrike(Player player) {
        return player != null && player.getData(HungerStrikeAttachments.ON_STRIKE);
    }

    public static void setOnStrike(Player player, boolean onStrike) {
        if (player == null || isOnStrike(player) == onStrike) {
            return;
        }
        // setData syncs the new value to the owning player automatically.
        player.setData(HungerStrikeAttachments.ON_STRIKE, onStrike);
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
