package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class NetworkHandler
{
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(HungerStrike.MOD_ID);

        registrar.play(SyncRequest.ID, SyncRequest::new, handler -> handler
            .server(SyncRequest::handleServer)
        );

        registrar.play(PlayerData.ID, PlayerData::new, handler -> handler
            .client(PlayerData::handleClient)
        );

        registrar.play(ConfigData.ID, ConfigData::new, handler -> handler
            .client(ConfigData::handleClient)
        );
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer))
            PacketDistributor.PLAYER.with(player).send(message);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }
}
