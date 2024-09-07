package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.config.ModConfig;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public abstract class ExtendedPlayer
{
    protected final Player player;

    private int startHunger;

    public ExtendedPlayer (Player player) {
        this.player = player;
        //this.player.getData(Attachments.HUNGER_STRIKE_ENABLED);
    }

    public abstract void enableHungerStrike (boolean enable);

    public abstract void loadState (boolean hungerStrikeEnabled);

    public abstract boolean isOnHungerStrike ();

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

    public void tickEnd (boolean serverSide) {
        if (!shouldTick())
            return;

        if (serverSide) {
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
