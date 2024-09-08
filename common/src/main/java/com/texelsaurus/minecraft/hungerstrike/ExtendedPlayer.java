package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.config.ModConfig;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtendedPlayer
{
    private static Map<Player, Integer> startHungerMap = new HashMap<>();

    protected final Player player;

    public ExtendedPlayer (Player player) {
        this.player = player;
        if (!startHungerMap.containsKey(player))
            startHungerMap.put(player, 0);
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
        startHungerMap.put(player, player.getFoodData().getFoodLevel());
    }

    public void tickEnd (boolean serverSide) {
        if (!shouldTick())
            return;

        if (serverSide) {
            int foodLevel = player.getFoodData().getFoodLevel();
            int startHunger = startHungerMap.getOrDefault(player, foodLevel);

            int foodDiff = foodLevel - startHunger;
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
