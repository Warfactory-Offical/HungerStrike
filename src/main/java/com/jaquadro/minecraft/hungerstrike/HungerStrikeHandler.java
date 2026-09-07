package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pins the hunger bar at the configured baseline and converts anything eaten on top of it into
 * health.
 *
 * <p>The bar is reset at the start of the tick and read again at the end: whatever nutrition was
 * added in between came from food the player ate, and is paid out as hearts.
 */
public class HungerStrikeHandler {
    /** Saturation is held slightly above zero so vanilla exhaustion never drains the pinned bar. */
    private static final float BASELINE_SATURATION = 1.0F;

    private static final int HUNGER_EFFECT_LEVEL = 5;
    private static final int REGENERATION_EFFECT_LEVEL = 20;

    /**
     * Food level recorded at the start of the current tick. Entries never outlive a single tick:
     * every {@link PlayerTickEvent.Pre} that adds one is followed by the matching
     * {@link PlayerTickEvent.Post} that takes it back out.
     */
    private final Map<Player, Integer> tickStartFoodLevel = new IdentityHashMap<>();

    @SubscribeEvent
    public void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (!HungerStrikeState.isActiveFor(player)) {
            return;
        }

        pinFoodData(player);
        this.tickStartFoodLevel.put(player, player.getFoodData().getFoodLevel());
    }

    @SubscribeEvent
    public void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        // Taken out first so a mid-tick mode change can never strand an entry.
        Integer startFoodLevel = this.tickStartFoodLevel.remove(player);
        if (!HungerStrikeState.isActiveFor(player)) {
            return;
        }

        if (startFoodLevel != null && player instanceof ServerPlayer) {
            int gained = player.getFoodData().getFoodLevel() - startFoodLevel;
            if (gained > 0) {
                player.heal((float) (gained * Config.foodHealFactor()));
            }
        }

        pinFoodData(player);
    }

    private static void pinFoodData(Player player) {
        FoodData food = player.getFoodData();
        food.setFoodLevel(baselineFor(player));
        food.setSaturation(BASELINE_SATURATION);
    }

    /**
     * Hunger and Regeneration are expressed through the bar rather than the effect's own behaviour:
     * Hunger drops it below the sprint threshold, Regeneration fills it so vanilla regen kicks in.
     */
    private static int baselineFor(Player player) {
        if (player.hasEffect(MobEffects.HUNGER)) {
            return HUNGER_EFFECT_LEVEL;
        }
        if (player.hasEffect(MobEffects.REGENERATION)) {
            return REGENERATION_EFFECT_LEVEL;
        }
        return Config.hungerBaseline();
    }

    public static List<ServerPlayer> getStrikingPlayers(MinecraftServer server) {
        return collect(server, true);
    }

    public static List<ServerPlayer> getNonStrikingPlayers(MinecraftServer server) {
        return collect(server, false);
    }

    private static List<ServerPlayer> collect(MinecraftServer server, boolean onStrike) {
        List<ServerPlayer> result = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (HungerStrikeState.isOnStrike(player) == onStrike) {
                result.add(player);
            }
        }
        return result;
    }
}
