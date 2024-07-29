package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(HungerStrike.MOD_ID, "main_channel"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void init() {
        INSTANCE.messageBuilder(PacketRequestSync.class, id(), PlayNetworkDirection.PLAY_TO_SERVER)
            .decoder(PacketRequestSync::new)
            .encoder(PacketRequestSync::write)
            .consumerMainThread(PacketRequestSync::handle).add();

        INSTANCE.messageBuilder(PacketSyncExtendedPlayer.class, id(), PlayNetworkDirection.PLAY_TO_CLIENT)
            .decoder(PacketSyncExtendedPlayer::new)
            .encoder(PacketSyncExtendedPlayer::write)
            .consumerMainThread(PacketSyncExtendedPlayer::handle).add();

        INSTANCE.messageBuilder(PacketSyncConfig.class, id(), PlayNetworkDirection.PLAY_TO_CLIENT)
            .decoder(PacketSyncConfig::new)
            .encoder(PacketSyncConfig::write)
            .consumerMainThread(PacketSyncConfig::handle).add();
    }
}
