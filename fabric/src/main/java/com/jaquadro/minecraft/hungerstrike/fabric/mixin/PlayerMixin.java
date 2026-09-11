package com.jaquadro.minecraft.hungerstrike.fabric.mixin;

import com.jaquadro.minecraft.hungerstrike.HungerStrikeHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stands in for NeoForge's {@code PlayerTickEvent.Pre}/{@code Post}, which the NeoForge module of
 * this repository uses and which vanilla fires from exactly these two points in {@code Player#tick}.
 * Eating resolves inside this call ({@code LivingEntity#tick} → {@code updatingUsingItem} →
 * {@code Item#finishUsingItem}), so bracketing it is what makes the food-level diff mean "nutrition
 * gained from food this tick".
 */
@Mixin(Player.class)
abstract class PlayerMixin {
    /**
     * Food level at the start of this tick, or {@link HungerStrikeHandler#NO_TICK_START} when the
     * mod is not active for this player. The NeoForge module parks the same value in a
     * non-serialized data attachment; either way it is per-player, so there is nothing to evict and
     * no cross-thread sharing on an integrated server.
     */
    @Unique
    private int hungerstrike$tickStartFoodLevel = HungerStrikeHandler.NO_TICK_START;

    @Inject(method = "tick", at = @At("HEAD"))
    private void hungerstrike$tickPre(CallbackInfo ci) {
        this.hungerstrike$tickStartFoodLevel = HungerStrikeHandler.beginTick((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void hungerstrike$tickPost(CallbackInfo ci) {
        // Read out first so a mid-tick mode change can never leave a stale value behind.
        int tickStartFoodLevel = this.hungerstrike$tickStartFoodLevel;
        this.hungerstrike$tickStartFoodLevel = HungerStrikeHandler.NO_TICK_START;
        HungerStrikeHandler.endTick((Player) (Object) this, tickStartFoodLevel);
    }
}
