package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSyncExtendedPlayer
{
    private boolean hungerStrikeEnabled;

    private PacketSyncExtendedPlayer(boolean hungerStrikeEnabled) {
        this.hungerStrikeEnabled = hungerStrikeEnabled;
    }

    public PacketSyncExtendedPlayer(Player player) {
        this(getHungerStrikeFromPlayer(player));
    }

    public PacketSyncExtendedPlayer(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(hungerStrikeEnabled);
    }

    private static boolean getHungerStrikeFromPlayer(Player player) {
        ExtendedPlayer ep = ExtendedPlayer.get(player);
        return (ep != null) && ep.isOnHungerStrike();
    }

    public void handle(NetworkEvent.Context ctx) {
        if (FMLEnvironment.dist.isClient())
            handle(this);

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle(PacketSyncExtendedPlayer message) {
        ExtendedPlayer ep = ExtendedPlayer.get(clientPlayer());
        if (ep != null)
            ep.loadState(message.hungerStrikeEnabled);
    }

    @OnlyIn(Dist.CLIENT)
    private static LocalPlayer clientPlayer() {
        return Minecraft.getInstance().player;
    }
}
