package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.ExtendedPlayer;
import com.texelsaurus.minecraft.hungerstrike.ForgeExtendedPlayer;
import net.minecraft.world.entity.player.Player;

public class ForgePlayerHandler implements CommonPlayerHandler
{
    @Override
    public ExtendedPlayer getExtendedPlayer (Player player) {
        if (ForgeExtendedPlayer.EXTENDED_PLAYER_CAPABILITY == null || player == null)
            return null;

        return player.getCapability(ForgeExtendedPlayer.EXTENDED_PLAYER_CAPABILITY, null).orElse(null);
    }
}
