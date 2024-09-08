package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.ModConstants;
import com.texelsaurus.minecraft.hungerstrike.network.CommonPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;

public class NeoforgeNetworking implements CommonNetworking
{
    private static PayloadRegistrar registrar = null;

    public static void init (IEventBus modBus) {
        modBus.addListener((Consumer<RegisterPayloadHandlersEvent>) event -> {
            registrar = event.registrar(ModConstants.MOD_ID);
            CommonNetworking.init();
            registrar = null;
        });
    }

    @Override
    public <B extends FriendlyByteBuf, P extends CommonPacket> void registerPacketInternal (CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        IPayloadHandler<P> handler = (packet, context) -> {
            packet.handleMessage(context.player(), context::enqueueWork);
        };

        if (clientBound)
            registrar.playToClient(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
        else
            registrar.playToServer(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
    }

    @Override
    public void sendToPlayer (CommonPacket packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public void sendToServer (CommonPacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}
