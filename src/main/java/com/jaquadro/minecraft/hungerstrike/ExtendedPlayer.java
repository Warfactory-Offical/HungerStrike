package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.NetworkHandler;
import com.jaquadro.minecraft.hungerstrike.network.PlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.neoforged.fml.LogicalSide;

public class ExtendedPlayer
{
    private final Player player;

    private int startHunger;

    public ExtendedPlayer(Player player) {
        this.player = player;
        this.player.getData(Attachments.HUNGER_STRIKE_ENABLED);
    }

    public static ExtendedPlayer get (Player player) {
        return new ExtendedPlayer(player);
    }

    public void enableHungerStrike (boolean enable) {
        if (this.player.getData(Attachments.HUNGER_STRIKE_ENABLED) != enable) {
            this.player.setData(Attachments.HUNGER_STRIKE_ENABLED, enable);

            if (player instanceof ServerPlayer playerMP)
                NetworkHandler.sendTo(playerMP, new PlayerData(this));
        }
    }

    public void loadState (boolean hungerStrikeEnabled) {
        this.player.setData(Attachments.HUNGER_STRIKE_ENABLED, hungerStrikeEnabled);
    }

    public boolean isOnHungerStrike () {
        return this.player.getData(Attachments.HUNGER_STRIKE_ENABLED);
    }

    private boolean shouldTick () {
        ModConfig.Mode mode = ModConfig.GENERAL.mode.get();
        if (mode == ModConfig.Mode.LIST)
            return isOnHungerStrike();
        else
            return mode == ModConfig.Mode.ALL;
    }

    public void tickStart () {
        if (!shouldTick())
            return;

        setFoodData(player.getFoodData(), calcBaselineHunger(), 1);
        startHunger = player.getFoodData().getFoodLevel();
    }

    public void tickEnd (LogicalSide side) {
        if (!shouldTick())
            return;

        if (side == LogicalSide.SERVER) {
            int foodDiff = player.getFoodData().getFoodLevel() - startHunger;
            if (foodDiff > 0)
                player.heal((float)(foodDiff * ModConfig.GENERAL.foodHealFactor.get()));
        }

        setFoodData(player.getFoodData(), calcBaselineHunger(), 1);
    }

    private void setFoodData (FoodData foodStats, int foodLevel, float saturationLevel) {
        foodStats.eat(1, (saturationLevel - foodStats.getSaturationLevel()) / 2);
        foodStats.eat(foodLevel - foodStats.getFoodLevel(), 0);
    }

    private int calcBaselineHunger () {
        if (player.hasEffect(MobEffects.HUNGER))
            return 5;
        else if (player.hasEffect(MobEffects.REGENERATION))
            return 20;
        else
            return ModConfig.GENERAL.hungerBaseline.get();
    }
}
