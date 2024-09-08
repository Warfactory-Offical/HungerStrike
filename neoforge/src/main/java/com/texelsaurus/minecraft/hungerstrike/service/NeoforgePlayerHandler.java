package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.NeoforgeExtendedPlayer;
import net.minecraft.world.entity.player.Player;

public class NeoforgePlayerHandler implements CommonPlayerHandler
{
    @Override
    public NeoforgeExtendedPlayer getExtendedPlayer (Player player) {
        return NeoforgeExtendedPlayer.get(player);
    }
}
