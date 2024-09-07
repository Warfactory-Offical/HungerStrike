package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.ExtendedPlayer;
import net.minecraft.world.entity.player.Player;

public interface CommonPlayerHandler
{
    ExtendedPlayer getExtendedPlayer(Player player);
}
