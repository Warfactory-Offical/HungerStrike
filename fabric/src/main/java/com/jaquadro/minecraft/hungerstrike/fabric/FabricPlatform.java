package com.jaquadro.minecraft.hungerstrike.fabric;

import com.jaquadro.minecraft.hungerstrike.HungerStrikePlatform;
import com.jaquadro.minecraft.hungerstrike.network.SyncModePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;

/** Fabric side of {@link HungerStrikePlatform}. */
public final class FabricPlatform implements HungerStrikePlatform {
    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isOnStrike(Player player) {
        return player.getAttachedOrElse(HungerStrikeAttachments.ON_STRIKE, Boolean.FALSE);
    }

    @Override
    public void setOnStrike(Player player, boolean onStrike) {
        // setAttached syncs the new value to the owning player automatically.
        player.setAttached(HungerStrikeAttachments.ON_STRIKE, onStrike);
    }

    /**
     * Fabric has no "optional channel" flag: an unregistered channel is simply not negotiated, and
     * {@link ServerPlayNetworking#canSend} reports that, so a vanilla or server-only-mod client can
     * still connect.
     */
    @Override
    public void sendModeTo(ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, SyncModePayload.TYPE)) {
            ServerPlayNetworking.send(player, SyncModePayload.current());
        }
    }
}
