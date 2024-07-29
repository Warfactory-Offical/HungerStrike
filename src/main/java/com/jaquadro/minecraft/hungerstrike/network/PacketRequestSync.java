package com.jaquadro.minecraft.hungerstrike.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.PlayNetworkDirection;

public class PacketRequestSync
{
    public PacketRequestSync() { }

    public PacketRequestSync(FriendlyByteBuf buf) { }

    public void write(FriendlyByteBuf buf) { }

    public void handle(NetworkEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncExtendedPlayer(player), player.connection.connection, PlayNetworkDirection.PLAY_TO_CLIENT);
            PacketHandler.INSTANCE.sendTo(new PacketSyncConfig(), player.connection.connection, PlayNetworkDirection.PLAY_TO_CLIENT);
        }

        ctx.setPacketHandled(true);
    }
}
