package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.network.SyncModePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * The only payload the mod still needs. Per-player strike state rides on the data attachment's
 * built-in sync, which replaced the request/response packet pair from the Forge builds.
 */
public final class NeoForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private NeoForgeNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                // Optional so a vanilla or server-only-mod client can still connect.
                .optional()
                .playToClient(SyncModePayload.TYPE, SyncModePayload.STREAM_CODEC,
                        (payload, context) -> Config.setModeFromServer(payload.mode()));
    }

    public static void sendModeTo(ServerPlayer player) {
        if (player.connection.hasChannel(SyncModePayload.TYPE)) {
            PacketDistributor.sendToPlayer(player, SyncModePayload.current());
        }
    }
}
