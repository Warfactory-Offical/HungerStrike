package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.HungerStrikePlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

/** NeoForge side of {@link HungerStrikePlatform}. */
public final class NeoForgePlatform implements HungerStrikePlatform {
    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isOnStrike(Player player) {
        return player.getData(HungerStrikeAttachments.ON_STRIKE);
    }

    @Override
    public void setOnStrike(Player player, boolean onStrike) {
        // setData syncs the new value to the owning player automatically.
        player.setData(HungerStrikeAttachments.ON_STRIKE, onStrike);
    }

    @Override
    public void sendModeTo(ServerPlayer player) {
        NeoForgeNetwork.sendModeTo(player);
    }
}
