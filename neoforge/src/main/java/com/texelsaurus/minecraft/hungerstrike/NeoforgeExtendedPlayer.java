package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.network.PlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

public class NeoforgeExtendedPlayer extends ExtendedPlayer
{
    private int startHunger;

    public NeoforgeExtendedPlayer (Player player) {
        super(player);
        this.player.getData(Attachments.HUNGER_STRIKE_ENABLED);
    }

    public static NeoforgeExtendedPlayer get(Player player) {
        return new NeoforgeExtendedPlayer(player);
    }

    @Override
    public void enableHungerStrike (boolean enable) {
        if (this.player.getData(Attachments.HUNGER_STRIKE_ENABLED) != enable) {
            this.player.setData(Attachments.HUNGER_STRIKE_ENABLED, enable);

            if (player instanceof ServerPlayer playerMP)
                ModServices.NETWORK.sendToPlayer(new PlayerData(this), playerMP);
        }
    }

    @Override
    public void loadState (boolean hungerStrikeEnabled) {
        this.player.setData(Attachments.HUNGER_STRIKE_ENABLED, hungerStrikeEnabled);
    }

    @Override
    public boolean isOnHungerStrike () {
        return this.player.getData(Attachments.HUNGER_STRIKE_ENABLED);
    }
}
