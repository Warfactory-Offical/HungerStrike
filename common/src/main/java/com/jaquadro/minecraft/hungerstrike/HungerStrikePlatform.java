package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;

/**
 * Everything the shared code needs that the two mod loaders spell differently.
 *
 * <p>Kept deliberately small: only per-player attachment storage, the config directory and the
 * mode-sync send actually differ between NeoForge and Fabric. Everything else in {@code common/}
 * talks to vanilla classes, which carry the same official Mojang names on both loaders now that
 * Minecraft ships unobfuscated.
 *
 * <p>The implementation is installed once, from the loader's entrypoint, via
 * {@link HungerStrike#init(HungerStrikePlatform)}.
 */
public interface HungerStrikePlatform {
    /**
     * Directory holding {@code hungerstrike-common.toml}. Both loaders point at the instance's
     * {@code config/} folder, which is what keeps the file interchangeable between builds.
     */
    Path configDir();

    /** Reads the synced, persistent per-player strike flag. */
    boolean isOnStrike(Player player);

    /** Writes the per-player strike flag, syncing it to the owning client. */
    void setOnStrike(Player player, boolean onStrike);

    /**
     * Pushes the active mode to one player, if their client speaks our channel. A client without
     * the mod must stay connectable, so implementations check that before sending.
     */
    void sendModeTo(ServerPlayer player);

    /** Pushes the active mode to everyone. Identical on both loaders, so it lives here. */
    default void broadcastMode(MinecraftServer server) {
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendModeTo(player);
        }
    }
}
