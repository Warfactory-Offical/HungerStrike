package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler
{
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(HungerStrike.MOD_ID);

        registrar.playToServer(SyncRequest.TYPE, SyncRequest.STREAM_CODEC, SyncRequest::handleServer);

        registrar.playToClient(PlayerData.TYPE, PlayerData.STREAM_CODEC, PlayerData::handleClient);

        registrar.playToClient(ConfigData.TYPE, ConfigData.STREAM_CODEC, ConfigData::handleClient);
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer))
            PacketDistributor.sendToPlayer(player, message);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }
}
