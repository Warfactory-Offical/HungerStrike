package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import java.util.ArrayList;
import java.util.List;

/**
 * Pins the hunger bar at the configured baseline and converts anything eaten on top of it into
 * health.
 *
 * <p>The bar is reset at the start of the tick and read again at the end: whatever nutrition was
 * added in between came from food the player ate, and is paid out as hearts.
 *
 * <p>The two halves are driven by whichever hook the loader offers: NeoForge fires
 * {@code PlayerTickEvent.Pre}/{@code Post}, and the Fabric module injects at {@code HEAD} and
 * {@code RETURN} of {@code Player#tick} — the very points NeoForge fires those events from, so both
 * bracket exactly the same work.
 *
 * <p>Neither half keeps state here. {@link #beginTick} hands the tick-start food level back to the
 * caller, which parks it per player (a data attachment on NeoForge, a mixin field on Fabric), so
 * nothing has to be evicted and the client and server player objects of an integrated server never
 * share mutable state.
 */
public final class HungerStrikeHandler {
    /** Saturation is held slightly above zero so vanilla exhaustion never drains the pinned bar. */
    private static final float BASELINE_SATURATION = 1.0F;

    private static final int HUNGER_EFFECT_LEVEL = 5;
    private static final int REGENERATION_EFFECT_LEVEL = 20;

    /** Sentinel for "this player was not on strike when the tick began". */
    public static final int NO_TICK_START = -1;

    private HungerStrikeHandler() {}

    /**
     * Start of {@code Player#tick}. Returns the food level to compare against at the end of the
     * tick, or {@link #NO_TICK_START} if the mod is not active for this player.
     */
    public static int beginTick(Player player) {
        if (!HungerStrikeState.isActiveFor(player)) {
            return NO_TICK_START;
        }

        pinFoodData(player);
        return player.getFoodData().getFoodLevel();
    }

    /** End of {@code Player#tick}, given whatever {@link #beginTick} returned. */
    public static void endTick(Player player, int tickStartFoodLevel) {
        if (!HungerStrikeState.isActiveFor(player)) {
            return;
        }

        if (tickStartFoodLevel != NO_TICK_START && player instanceof ServerPlayer) {
            int gained = player.getFoodData().getFoodLevel() - tickStartFoodLevel;
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
