package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.HungerStrikeHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Drives {@link HungerStrikeHandler} from NeoForge's player tick events, parking the tick-start
 * food level on the player until {@code Post} reads it back.
 */
public final class HungerStrikeEvents {
    @SubscribeEvent
    public void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        int tickStartFoodLevel = HungerStrikeHandler.beginTick(player);
        if (tickStartFoodLevel != HungerStrikeHandler.NO_TICK_START) {
            player.setData(HungerStrikeAttachments.TICK_START_FOOD_LEVEL, tickStartFoodLevel);
        }
        // Nothing to write otherwise: every Post leaves the attachment at the sentinel, and players
        // the mod never touches should not accumulate one at all.
    }

    @SubscribeEvent
    public void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        // Read out and clear first, so a mode change between Pre and Post cannot strand a value.
        int tickStartFoodLevel = player.getData(HungerStrikeAttachments.TICK_START_FOOD_LEVEL);
        if (tickStartFoodLevel != HungerStrikeHandler.NO_TICK_START) {
            player.setData(HungerStrikeAttachments.TICK_START_FOOD_LEVEL, HungerStrikeHandler.NO_TICK_START);
        }

        HungerStrikeHandler.endTick(player, tickStartFoodLevel);
    }
}
